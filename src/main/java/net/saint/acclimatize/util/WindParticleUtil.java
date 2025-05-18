package net.saint.acclimatize.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.saint.acclimatize.ModClient;

public class WindParticleUtil {

	public static void renderWindParticles(MinecraftClient client) {
		var player = client.player;
		var world = client.world;
		var random = world.getRandom();

		var bound = Math.max(1, 16 + (int) ModClient.cachedWindTemperature);

		if (random.nextInt(bound) == 0) {
			double windDirectionRadians = Math.toRadians(ModClient.cachedWindDirection);

			Vec3d direction = new Vec3d(-Math.sin(windDirectionRadians), 0, Math.cos(windDirectionRadians));

			double x = player.getX() + random.nextTriangular(0, 10) - direction.x * 7;
			double y = player.getY() + random.nextTriangular(5, 7);
			double z = player.getZ() + random.nextTriangular(0, 10) - direction.z * 7;

			world.addParticle(ParticleTypes.CLOUD, x, y, z, direction.x, direction.y, direction.z);
		}
	}

}
