package net.saint.acclimatize.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.biome.Biome;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.library.ClimateKind;

public final class BiomeTemperatureUtil {

	// Library

	public static class TemperatureRange {
		public double minimum;
		public double median;
		public double maximum;

		public TemperatureRange(double minTemperature, double medTemperature, double maxTemperature) {
			this.minimum = minTemperature;
			this.median = medTemperature;
			this.maximum = maxTemperature;
		}
	}

	// Biome

	public static double biomeTemperatureForPlayer(ServerPlayerEntity player, boolean isInInterior) {
		var world = player.getWorld();
		var position = player.getBlockPos();

		var dimension = world.getDimension();
		var biome = world.getBiome(position).value();
		var precipitation = biome.getPrecipitation(player.getBlockPos());

		var internalTemperatureValue = biome.getTemperature();
		var climateKind = climateKindForTemperature(internalTemperatureValue);

		var biomeTemperature = baseTemperatureForClimate(climateKind);

		// Daylight/Nighttime

		if (dimension.natural()) {
			var dayTick = world.getTimeOfDay();
			var dayNightTemperatureDelta = dayNightTemperatureDeltaForTime(climateKind, dayTick);

			biomeTemperature += dayNightTemperatureDelta;
		}

		// Precipitation

		if (precipitation == Biome.Precipitation.RAIN && world.isRaining()) {
			biomeTemperature += Mod.CONFIG.rainTemperatureDelta;
		} else if (precipitation == Biome.Precipitation.SNOW && world.isRaining()) {
			biomeTemperature += Mod.CONFIG.snowTemperatureDelta;
		}

		return biomeTemperature;
	}

	// Climate

	public static ClimateKind climateKindForTemperature(float temperature) {
		if (temperature < 0.0) {
			return ClimateKind.FRIGID;
		} else if (temperature < 0.31 && temperature >= 0.0) {
			return ClimateKind.COLD;
		} else if (temperature < 0.9 && temperature >= 0.31) {
			return ClimateKind.TEMPERATE;
		} else if (temperature < 2.0 && temperature > 0.8) {
			return ClimateKind.HOT;
		} else if (temperature >= 2.0) {
			return ClimateKind.ARID;
		}

		return ClimateKind.ARID;
	}

	private static double baseTemperatureForClimate(ClimateKind climateKind) {
		switch (climateKind) {
			case FRIGID:
				return Mod.CONFIG.frigidClimateTemperature;
			case COLD:
				return Mod.CONFIG.coldClimateTemperature;
			case TEMPERATE:
				return Mod.CONFIG.temperateClimateTemperature;
			case HOT:
				return Mod.CONFIG.hotClimateTemperature;
			case ARID:
				return Mod.CONFIG.aridClimateTemperature;
			default:
				return Mod.CONFIG.aridClimateTemperature;
		}
	}

	public static double dayNightTemperatureDeltaForTime(ClimateKind climateKind, long dayTick) {
		var phaseValue = phaseValueForAsymmetricTime(dayTick); // Phase shift phi: φ
		var plateau = 3; // Plateau: p
		var offset = 0.1; // Offset delta: δ

		// Formula: Tdf(x) = ((1 + cos(φ - δ)) / 2) ^ p
		var dropFactor = Math.pow(((1 + MathUtil.approximateCos(phaseValue - offset)) / 2), plateau);
		var temperatureDelta = -Mod.CONFIG.nightTemperatureDelta * dropFactor;

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
