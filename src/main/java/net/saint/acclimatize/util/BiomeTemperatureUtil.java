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

	public static TemperatureRange biomeTemperatureForPlayer(ServerPlayerEntity player, boolean isInInterior) {
		var world = player.getWorld();
		var position = player.getBlockPos();

		var dimension = world.getDimension();
		var biome = world.getBiome(position).value();
		var precipitation = biome.getPrecipitation(player.getBlockPos());

		var ambientTemperature = biome.getTemperature();
		var climateKind = climateKindForTemperature(ambientTemperature);

		var temperatureRange = baseTemperatureRangeForClimate(climateKind);

		// Daylight/Nighttime

		if (dimension.natural()) {
			var dayTick = world.getTimeOfDay();
			var dayNightTemperatureDelta = dayNightTemperatureDeltaForTime(climateKind, dayTick);

			temperatureRange.median += dayNightTemperatureDelta;
		}

		// Precipitation

		if (precipitation == Biome.Precipitation.RAIN && world.isRaining()) {
			temperatureRange.median += Mod.CONFIG.rainTemperatureDelta;
		} else if (precipitation == Biome.Precipitation.SNOW && world.isRaining()) {
			temperatureRange.median += Mod.CONFIG.snowTemperatureDelta;
		}

		return temperatureRange;
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

	private static TemperatureRange baseTemperatureRangeForClimate(ClimateKind climateKind) {
		switch (climateKind) {
			case FRIGID:
				return new TemperatureRange(0.0, Mod.CONFIG.frigidClimateTemperature, 80.0);
			case COLD:
				return new TemperatureRange(0.0, Mod.CONFIG.coldClimateTemperature, 100.0);
			case TEMPERATE:
				return new TemperatureRange(0.0, Mod.CONFIG.temperateClimateTemperature, 100.0);
			case HOT:
				return new TemperatureRange(40.0, Mod.CONFIG.hotClimateTemperature, 120.0);
			case ARID:
				return new TemperatureRange(40.0, Mod.CONFIG.aridClimateTemperature, 120.0);
			default:
				return new TemperatureRange(40.0, Mod.CONFIG.aridClimateTemperature, 120.0);
		}
	}

	public static double dayNightTemperatureDeltaForTime(ClimateKind climateKind, long dayTick) {
		// Get cycle lengths and calc transition periods
		final long dayLength = Mod.CONFIG.daylightTicks;
		final long nightLength = Mod.CONFIG.nighttimeTicks;
		final long totalLength = dayLength + nightLength;

		final long dayTickInCycle = dayTick % totalLength;

		// Calculate transition period (15% of shorter period)
		final long transitionLength = (long) (Math.min(dayLength, nightLength) * 0.2);

		if (dayTickInCycle < transitionLength) {
			// Night -> Day transition (dawn)
			double t = (double) dayTickInCycle / transitionLength;
			return MathUtil.lerp(nighttimeTemperatureDeltaForClimate(climateKind), 0, t);
		} else if (dayTickInCycle >= dayLength && dayTickInCycle < dayLength + transitionLength) {
			// Day -> Night transition (dusk)
			double t = (double) (dayTickInCycle - dayLength) / transitionLength;
			return MathUtil.lerp(0, nighttimeTemperatureDeltaForClimate(climateKind), t);
		} else if (dayTickInCycle >= dayLength) {
			// Full night
			return nighttimeTemperatureDeltaForClimate(climateKind);
		} else {
			// Full day
			return 0.0;
		}
	}

	private static double nighttimeTemperatureDeltaForClimate(ClimateKind climateKind) {
		switch (climateKind) {
			case FRIGID:
				return -10;
			case COLD:
				return -10;
			case TEMPERATE:
				return -10;
			case HOT:
				return -8;
			case ARID:
				return -15;
			default:
				return 0;
		}
	}

}
