package thermite.therm.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.biome.Biome;
import thermite.therm.ThermMod;
import thermite.therm.ThermUtil;
import thermite.therm.library.ClimateKind;

public final class AmbientTemperatureUtil {

	public static class TemperatureRange {
		public double minTemperature;
		public double medTemperature;
		public double maxTemperature;

		public TemperatureRange(double minTemperature, double medTemperature, double maxTemperature) {
			this.minTemperature = minTemperature;
			this.medTemperature = medTemperature;
			this.maxTemperature = maxTemperature;
		}
	}

	public static TemperatureRange ambientBaseTemperatureRangeForClimate(ClimateKind climateKind) {
		switch (climateKind) {
			case FRIGID:
				return new TemperatureRange(0.0, ThermMod.config.frigidClimateTemperature, 80.0);
			case COLD:
				return new TemperatureRange(0.0, ThermMod.config.coldClimateTemperature, 100.0);
			case TEMPERATE:
				return new TemperatureRange(0.0, ThermMod.config.temperateClimateTemperature, 100.0);
			case HOT:
				return new TemperatureRange(40.0, ThermMod.config.hotClimateTemperature, 120.0);
			case ARID:
				return new TemperatureRange(40.0, ThermMod.config.aridClimateTemperature, 120.0);
			default:
				return new TemperatureRange(40.0, ThermMod.config.aridClimateTemperature, 120.0);
		}
	}

	public static double ambientNighttimeTemperatureDeltaForClimate(ClimateKind climateKind) {
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

	public static TemperatureRange ambientTemperatureForPlayer(ServerPlayerEntity player) {
		var world = player.getWorld();
		var dimension = world.getDimension();
		var playerPos = player.getBlockPos();

		var biome = world.getBiome(playerPos).value();
		var precipitation = biome.getPrecipitation(player.getBlockPos());

		var ambientTemperature = biome.getTemperature();
		var climateKind = ThermUtil.climateKindForTemperature(ambientTemperature);

		var temperatureRange = ambientBaseTemperatureRangeForClimate(climateKind);

		// Nighttime

		if (dimension.natural() && !world.isDay()) {
			temperatureRange.medTemperature += ambientNighttimeTemperatureDeltaForClimate(climateKind);
		}

		// Precipitation

		if (precipitation == Biome.Precipitation.RAIN && world.isRaining() && player.isWet()
				&& !player.isTouchingWater()) {
			temperatureRange.medTemperature -= 8;
		} else if (precipitation == Biome.Precipitation.SNOW && world.isRaining()) {
			temperatureRange.medTemperature -= 12;
		}

		return temperatureRange;
	}

}
