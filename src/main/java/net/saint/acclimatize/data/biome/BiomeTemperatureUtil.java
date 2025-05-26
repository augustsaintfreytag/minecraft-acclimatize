package net.saint.acclimatize.data.biome;

import java.util.HashMap;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.util.MathUtil;

public final class BiomeTemperatureUtil {

	// Configuration

	private static final HashMap<String, Double> biomeRawTemperatureOverrides = new HashMap<String, Double>() {
		{
			// Cold Biomes
			put("minecraft:deep_frozen_ocean", -0.4);
			put("minecraft:deep_cold_ocean", -0.2);
			put("minecraft:cold_ocean", -0.1);
			put("minecraft:deep_ocean", 0.0);
			put("minecraft:ocean", 0.1);
			put("minecraft:deep_lukewarm_ocean", 0.15);
			put("minecraft:lukewarm_ocean", 0.2);
			put("minecraft:warm_ocean", 0.3);

			// Humid Biomes
			put("minecraft:swamp", 0.85);
			put("minecraft:mangrove_swamp", 0.8);

			// Hot Biomes
			put("minecraft:savanna", 1.2);
			put("minecraft:savanna_plateau", 1.18);
			put("minecraft:windswept_savanna", 1.15);
			put("minecraft:desert", 1.35);
			put("minecraft:badlands", 1.45);
			put("minecraft:wooded_badlands", 1.42);
			put("minecraft:eroded_badlands", 1.55);
		}
	};

	// Aggregate Temperature

	public static double biomeTemperatureForPlayer(ServerPlayerEntity player, boolean isInInterior) {
		var world = player.getWorld();
		var position = player.getBlockPos();
		var dimension = world.getDimension();

		var biomeTemperature = baseTemperatureForBiomeAtPosition(world, position);

		// Nether & End

		if (!dimension.natural()) {
			return biomeTemperature;
		}

		// Altitude

		var height = position.getY();
		var heightTemperatureDelta = temperatureDeltaForAltitude(height);

		biomeTemperature += heightTemperatureDelta;

		// Daylight/Nighttime

		var dayTick = world.getTimeOfDay();
		var dayNightTemperatureDelta = temperatureDeltaForDayNightTime(dayTick);

		biomeTemperature += dayNightTemperatureDelta;

		// Precipitation

		var precipitationTemperatureDelta = temperatureDeltaForPrecipitation(world, position);
		biomeTemperature += precipitationTemperatureDelta;

		// Return

		return biomeTemperature;
	}

	// Base Temperature

	public static double baseTemperatureForBiomeAtPosition(World world, BlockPos position) {
		var dimensionKey = world.getRegistryKey();

		if (dimensionKey == World.NETHER) {
			return Mod.CONFIG.netherBiomeTemperature;
		}

		if (dimensionKey == World.END) {
			return Mod.CONFIG.endBiomeTemperature;
		}

		var biomeEntry = world.getBiome(position);
		var biomeTemperature = baseTemperatureForBiome(biomeEntry);

		return biomeTemperature;
	}

	private static double baseTemperatureForBiome(RegistryEntry<Biome> biomeEntry) {
		var rawBiomeTemperature = rawTemperatureForBiome(biomeEntry);
		var baseTemperature = ((rawBiomeTemperature + Mod.CONFIG.biomeTemperatureZeroingAnchor) / 3) * 100;

		return baseTemperature;
	}

	private static double rawTemperatureForBiome(RegistryEntry<Biome> biomeEntry) {
		var biome = biomeEntry.value();
		var rawBiomeTemperature = (double) biome.getTemperature();

		rawBiomeTemperature = rawTemperatureOverrideForBiome(biomeEntry, rawBiomeTemperature);

		return rawBiomeTemperature;
	}

	private static double rawTemperatureOverrideForBiome(RegistryEntry<Biome> biomeEntry, double baseTemperature) {
		var biomeId = biomeEntry.getKey().get().toString();

		if (!biomeRawTemperatureOverrides.containsKey(biomeId)) {
			return baseTemperature;
		}

		return biomeRawTemperatureOverrides.get(biomeId);
	}

	// Precipitation

	private static double temperatureDeltaForPrecipitation(World world, BlockPos position) {
		var biomeEntry = world.getBiome(position);
		var precipitation = biomeEntry.value().getPrecipitation(position);
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
		var scalingFactor = -0.015; // Scaling factor: α
		var growthFactor = 1.5; // Growth factor: γ
		var softeningFactor = 15.0; // Softening factor: σ

		var lowerBound = -15.0; // Lower bound: L
		var upperBound = 15.0; // Upper bound: U

		var altitudeAnchor = (double) Mod.CONFIG.altitudeZeroingAnchor; // Altitude anchor: h_n

		// Formula: ΔT_alt = α * (sgn(h - h_n) * |h - h_n|^γ - α * σ^γ) - ae
		var delta = scalingFactor * Math.signum(altitude - altitudeAnchor) * Math.pow(Math.abs(altitude - altitudeAnchor), growthFactor)
				- scalingFactor * Math.pow(softeningFactor, growthFactor) - 0.65;

		return MathUtil.clamp(delta, lowerBound, upperBound) * Mod.CONFIG.altitudeTemperatureFactor;
	}

	// Day/Night

	private static double temperatureDeltaForDayNightTime(long tick) {
		// phaseValue now holds the calculated phiAngle from phaseValueForAsymmetricTime
		var phaseValue = phaseValueForAsymmetricTime(tick);

		var plateau = 1.8; // Plateau: p
		var offset = 1.65 * Math.PI; // Offset: ε

		// Formula: ΔT_{dn} = ((1 + cos(φ - ε)) / 2) ^ p
		// Use the phaseValue (which is phiAngle) directly
		var dropFactor = Math.pow(((1 + MathUtil.cos(phaseValue - offset)) / 2), plateau);
		var temperatureDelta = Mod.CONFIG.nightTemperatureDelta * dropFactor;

		return temperatureDelta;
	}

	private static double phaseValueForAsymmetricTime(long tick) {
		// Wrap around day/night cycle
		var dayLength = Mod.CONFIG.daylightTicks;
		var nightLength = Mod.CONFIG.nighttimeTicks;
		var cycleLength = dayLength + nightLength;

		// Get "tick within this cycle" in [0 … cycleLength)
		var cycleTick = tick % cycleLength;
		if (cycleTick < 0)
			cycleTick += cycleLength; // just in case time < 0

		// Normalize ticks into [0 … 1)
		// If cycleLength is 0, this would be division by zero. Assume cycleLength > 0.
		var normalizedTime = (cycleLength == 0) ? 0 : (cycleTick / (double) cycleLength);

		// Calculate phiAngle based on normalizedTime and day/night fractions
		// Assuming cycleLength > 0, ensured by Mod.CONFIG validation ideally
		var nightFraction = (cycleLength == 0) ? 0 : (nightLength / (double) cycleLength);
		var dayFraction = (cycleLength == 0) ? 0 : (dayLength / (double) cycleLength);

		double phiAngle;
		if (normalizedTime < nightFraction) {
			// Night: φ ∈ [0, π)
			// Avoid division by zero if nightFraction is 0 (i.e., nightLength is 0)
			phiAngle = (nightFraction == 0) ? 0 : (Math.PI * (normalizedTime / nightFraction));
		} else {
			// Day: φ ∈ [π, 2π)
			// Avoid division by zero if dayFraction is 0 (i.e., dayLength is 0)
			phiAngle = (dayFraction == 0) ? Math.PI : (Math.PI + Math.PI * ((normalizedTime - nightFraction) / dayFraction));
		}

		// Return the calculated angle phiAngle
		return phiAngle;
	}

}
