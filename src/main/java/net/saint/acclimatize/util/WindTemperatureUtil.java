package net.saint.acclimatize.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.biome.Biome;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.player.PlayerState;
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

	public static WindTemperatureTuple windTemperatureForEnvironment(ServerPlayerEntity player,
			PlayerState playerState,
			ServerState serverState) {
		var world = player.getWorld();
		var dimension = world.getDimension();

		if (!Mod.CONFIG.enableWind || (!Mod.CONFIG.multidimensionalWind && !dimension.natural())) {
			return WindTemperatureTuple.zero();
		}

		// Wind Base Temperature

		var playerPosition = player.getBlockPos();
		var precipitation = world.getBiome(playerPosition).value().getPrecipitation(playerPosition);

		var precipitationWindModifier = serverState.precipitationWindModifier;
		var windTemperature = serverState.windTemperatureModifier;

		if (player.getPos().y > 62) {
			double heightAddition = (player.getPos().y - 62);

			if (player.getPos().y <= 150) {
				heightAddition = heightAddition / 7;
			} else {
				heightAddition = heightAddition / 8;
			}

			windTemperature -= heightAddition;
		}

		if (precipitation == Biome.Precipitation.RAIN) {
			if (player.getWorld().isThundering()) {
				windTemperature += precipitationWindModifier * 1.1;
			} else {
				windTemperature += precipitationWindModifier;
			}
		} else if (precipitation == Biome.Precipitation.SNOW) {
			if (player.getWorld().isRaining()) {
				windTemperature += precipitationWindModifier * 1.3;
			}
		}

		// Wind Ray Calculation

		var windTurbulence = 23.0;
		var unblockedRays = Mod.CONFIG.windRayCount;

		var random = player.getRandom();

		for (int i = 0; i < Mod.CONFIG.windRayCount; i++) {

			var turbulence = windTurbulence * Math.PI / 180d;

			var directionVector = new Vec3d(
					(Math.cos(serverState.windPitch + random.nextTriangular(0, turbulence))
							* Math.cos(serverState.windYaw + random.nextTriangular(0, turbulence))),
					(Math.sin(serverState.windPitch + random.nextTriangular(0, turbulence))
							* Math.cos(serverState.windYaw + random.nextTriangular(0, turbulence))),
					Math.sin(serverState.windYaw + random.nextTriangular(0, turbulence)));

			var startPosition = new Vec3d(player.getPos().x, player.getPos().y + 1, player.getPos().z);

			var hitResult = player.getWorld()
					.raycast(new RaycastContext(startPosition,
							startPosition.add(directionVector.multiply(Mod.CONFIG.windRayLength)),
							RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.WATER, player));

			if (!player.getWorld().getBlockState(hitResult.getBlockPos()).isAir()) {
				unblockedRays -= 1;
			}
		}

		var windChillTemperatureFactor = ((double) unblockedRays / Mod.CONFIG.windRayCount);

		return new WindTemperatureTuple(windTemperature, windChillTemperatureFactor);
	}

}
