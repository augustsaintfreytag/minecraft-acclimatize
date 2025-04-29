package net.saint.acclimatize.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.biome.Biome;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.server.ServerState;

public final class WindTemperatureUtil {

	// Library

	public static class WindTemperatureTuple {
		public double temperature;
		public double windChillFactor;

		public WindTemperatureTuple(double windTemperature, double windChillFactor) {
			this.temperature = windTemperature;
			this.windChillFactor = windChillFactor;
		}

		public static WindTemperatureTuple zero() {
			return new WindTemperatureTuple(0.0, 0.0);
		}
	}

	// Main

	public static WindTemperatureTuple windTemperatureForEnvironment(ServerState serverState, ServerPlayerEntity player,
			boolean isInInterior) {
		var world = player.getWorld();
		var dimension = world.getDimension();

		if (!Mod.CONFIG.enableWind || isInInterior || (!Mod.CONFIG.multidimensionalWind && !dimension.natural())) {
			return WindTemperatureTuple.zero();
		}

		// Wind Base Temperature

		var windTemperature = serverState.windTemperature;

		var heightWindTemperatureDelta = heightTemperatureDeltaForPlayer(player);
		windTemperature += heightWindTemperatureDelta;

		var precipitationWindModifier = precipitationTemperatureDeltaForPlayer(serverState, player);
		windTemperature += precipitationWindModifier;

		// Wind Ray Calculation

		var numberOfUnblockedRays = checkUnblockedWindRaysForPlayer(serverState, player);
		var windChillTemperatureFactor = ((double) numberOfUnblockedRays / Mod.CONFIG.windRayCount)
				* Mod.CONFIG.windChillFactor;

		return new WindTemperatureTuple(windTemperature, windChillTemperatureFactor);
	}

	private static double precipitationTemperatureDeltaForPlayer(ServerState serverState, ServerPlayerEntity player) {
		var precipitationWindModifier = serverState.precipitationWindModifier;
		var world = player.getWorld();
		var position = player.getBlockPos();
		var biome = world.getBiome(position).value();
		var precipitation = biome.getPrecipitation(position);

		if (precipitation == Biome.Precipitation.RAIN) {
			if (world.isThundering()) {
				return precipitationWindModifier * 1.1;
			} else {
				return precipitationWindModifier;
			}
		} else if (precipitation == Biome.Precipitation.SNOW) {
			if (world.isRaining()) {
				return precipitationWindModifier * 1.3;
			}
		}

		return 0.0;
	}

	private static double heightTemperatureDeltaForPlayer(ServerPlayerEntity player) {
		var coefficient = -0.02;
		var growthFactor = 1.5;
		var softeningFactor = 15.0;

		var height = player.getPos().y;
		var heightValue = height - 62.0;

		var lowerBound = -20.0;
		var upperBound = 15.0;

		var delta = coefficient * Math.signum(heightValue) * Math.pow(Math.abs(heightValue), growthFactor)
				- coefficient * Math.pow(softeningFactor, growthFactor);

		return MathUtil.clamp(delta, lowerBound, upperBound);
	}

	private static int checkUnblockedWindRaysForPlayer(ServerState serverState, ServerPlayerEntity player) {
		var windBaseTurbulence = 23.0;
		var windTurbulence = windBaseTurbulence * Math.PI / 180d;
		var windYaw = serverState.windYaw;
		var windPitch = serverState.windPitch;

		var world = player.getWorld();
		var random = world.getRandom();

		var numberOfUnblockedRays = 0;

		// Profile Start Time
		var profile = Mod.PROFILER.begin("wind");

		for (int i = 0; i < Mod.CONFIG.windRayCount; i++) {
			var directionVector = new Vec3d(
					(MathUtil.approximateCos(windPitch + random.nextTriangular(0, windTurbulence))
							* MathUtil.approximateCos(windYaw + random.nextTriangular(0, windTurbulence))),
					(MathUtil.approximateSin(windPitch + random.nextTriangular(0, windTurbulence))
							* MathUtil.approximateCos(windYaw + random.nextTriangular(0, windTurbulence))),
					MathUtil.approximateSin(windYaw + random.nextTriangular(0, windTurbulence)));

			var startVector = new Vec3d(player.getPos().x, player.getPos().y + 1, player.getPos().z);
			var endVector = startVector.add(directionVector.multiply(Mod.CONFIG.windRayLength));

			var hitResult = world.raycast(new RaycastContext(startVector, endVector, RaycastContext.ShapeType.COLLIDER,
					RaycastContext.FluidHandling.NONE, player));

			if (hitResult.getType() == HitResult.Type.MISS) {
				numberOfUnblockedRays += 1;
			}
		}

		profile.end();
		Mod.LOGGER.info("Wind raycast duration: " + profile.getDescription());

		return numberOfUnblockedRays;
	}

}
