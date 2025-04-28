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
		var dimension = world.getDimension();
		var position = player.getBlockPos();

		var biome = world.getBiome(position).value();
		var precipitation = biome.getPrecipitation(player.getBlockPos());

		var ambientTemperature = biome.getTemperature();
		var climateKind = climateKindForTemperature(ambientTemperature);

		var temperatureRange = baseTemperatureRangeForClimate(climateKind);

		// Nighttime

		if (dimension.natural() && !world.isDay()) {
			temperatureRange.median += nighttimeTemperatureDeltaForClimate(climateKind);
		}

		// Precipitation

		if (!isInInterior) {
			if (precipitation == Biome.Precipitation.RAIN && world.isRaining()) {
				temperatureRange.median += Mod.CONFIG.rainTemperatureDelta;
			} else if (precipitation == Biome.Precipitation.SNOW && world.isRaining()) {
				temperatureRange.median += Mod.CONFIG.snowTemperatureDelta;
			}
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
