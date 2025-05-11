package net.saint.acclimatize.util;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Precipitation;
import net.saint.acclimatize.Mod;

public final class BiomeTemperatureUtil {

	// Biome

	public static double biomeTemperatureForPlayer(ServerPlayerEntity player, boolean isInInterior) {
		var world = player.getWorld();
		var position = player.getBlockPos();

		var dimension = world.getDimension();
		var biomeEntry = world.getBiome(position);
		var biomeTemperature = baseTemperatureForBiome(biomeEntry);

		// Nether & End

		if (!dimension.natural()) {
			return biomeTemperature;
		}

		// Height

		var height = position.getY();
		var heightTemperatureDelta = temperatureDeltaForAltitude(height);

		biomeTemperature += heightTemperatureDelta;

		// Daylight/Nighttime

		var dayTick = world.getTimeOfDay();
		var dayNightTemperatureDelta = temperatureDeltaForDayNightTime(dayTick);

		biomeTemperature += dayNightTemperatureDelta;

		// Precipitation

		var precipitation = biomeEntry.value().getPrecipitation(position);
		var precipitationTemperatureDelta = temperatureDeltaForPrecipitation(precipitation);

		biomeTemperature += precipitationTemperatureDelta;

		return biomeTemperature;
	}

	// Biome

	public static double baseTemperatureForBiome(RegistryEntry<Biome> biomeEntry) {
		var rawBiomeTemperature = (double) biomeEntry.value().getTemperature();
		var baseTemperature = ((rawBiomeTemperature + Mod.CONFIG.biomeTemperatureZeroingAnchor) / 3) * 100;

		return baseTemperature;
	}

	// Precipitation

	public static double temperatureDeltaForPrecipitation(Precipitation precipitation) {
		var temperatureDelta = 0.0;

		if (precipitation == Biome.Precipitation.RAIN) {
			temperatureDelta = Mod.CONFIG.rainTemperatureDelta;
		} else if (precipitation == Biome.Precipitation.SNOW) {
			temperatureDelta = Mod.CONFIG.snowTemperatureDelta;
		}

		return temperatureDelta;
	}

	// Altitude

	private static double temperatureDeltaForAltitude(double altitude) {
		var coefficient = -0.02;
		var growthFactor = 1.5;
		var softeningFactor = 15.0;
		var lowerBound = -20.0;
		var upperBound = 15.0;

		var normalizedAltitude = altitude - 62.0;

		var delta = coefficient * Math.signum(normalizedAltitude) * Math.pow(Math.abs(normalizedAltitude), growthFactor)
				- coefficient * Math.pow(softeningFactor, growthFactor);

		return MathUtil.clamp(delta, lowerBound, upperBound);
	}

	// Day/Night

	public static double temperatureDeltaForDayNightTime(long dayTick) {
		var phaseValue = phaseValueForAsymmetricTime(dayTick); // Phase shift phi: φ
		var plateau = 2; // Plateau: p
		var offset = 1.65 * Math.PI; // Offset delta: δ

		// Formula: Tdf(x) = ((1 + cos(φ - δ)) / 2) ^ p
		var dropFactor = Math.pow(((1 + MathUtil.approximateCos(phaseValue - offset)) / 2), plateau);
		var temperatureDelta = Mod.CONFIG.nightTemperatureDelta * dropFactor;

		return temperatureDelta;
	}

	private static double phaseValueForAsymmetricTime(double time) {
		// Wrap around day/night cycle
		var dayLength = Mod.CONFIG.daylightTicks;
		var nightLength = Mod.CONFIG.nighttimeTicks;
		var cycleLength = dayLength + nightLength;

		// Get "tick within this cycle" in [0 … cycleLength)
		var cycleTick = time % cycleLength;
		if (cycleTick < 0)
			cycleTick += cycleLength; // just in case time < 0

		// 2) Normalize ticks into [0 … 1)
		var normalizedTime = cycleTick / (double) cycleLength;

		// 3) Compute day/night fractions
		var nightFraction = nightLength / (double) cycleLength;
		var dayFraction = dayLength / (double) cycleLength;

		// 4) Piecewise φ(x)
		if (normalizedTime < nightFraction) {
			// Night: φ ∈ [0, π)
			return Math.PI * (normalizedTime / nightFraction);
		} else {
			// Day: φ ∈ [π, 2π)
			return Math.PI + Math.PI * ((normalizedTime - nightFraction) / dayFraction);
		}
	}

}
