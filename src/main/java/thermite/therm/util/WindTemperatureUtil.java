package thermite.therm.util;

import java.util.Random;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.biome.Biome;
import thermite.therm.ThermMod;
import thermite.therm.player.PlayerState;
import thermite.therm.server.ServerState;

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

		if (!ThermMod.CONFIG.enableWind || (!ThermMod.CONFIG.multidimensionalWind && !dimension.natural())) {
			return WindTemperatureTuple.zero();
		}

		// Wind base temperature calculation

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

		Random rand = new Random();
		int unblockedRays = ThermMod.CONFIG.windRayCount;
		for (int i = 0; i < ThermMod.CONFIG.windRayCount; i++) {

			double turbulence = playerState.windTurbulence * Math.PI / 180;

			Vec3d dir = new Vec3d(
					(Math.cos(serverState.windPitch + rand.nextDouble(-turbulence, turbulence))
							* Math.cos(serverState.windYaw + rand.nextDouble(-turbulence, turbulence))),
					(Math.sin(serverState.windPitch + rand.nextDouble(-turbulence, turbulence))
							* Math.cos(serverState.windYaw + rand.nextDouble(-turbulence, turbulence))),
					Math.sin(serverState.windYaw + rand.nextDouble(-turbulence, turbulence)));

			Vec3d startPos = new Vec3d(player.getPos().x, player.getPos().y + 1, player.getPos().z);

			BlockHitResult r = player.getWorld()
					.raycast(new RaycastContext(startPos,
							startPos.add(dir.multiply(ThermMod.CONFIG.windRayLength)),
							RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.WATER, player));
			if (!player.getWorld().getBlockState(r.getBlockPos()).isAir()) {
				unblockedRays -= 1;
			}
		}

		var windChillTemperatureFactor = ((double) unblockedRays / ThermMod.CONFIG.windRayCount);

		return new WindTemperatureTuple(windTemperature, windChillTemperatureFactor);
	}

}
