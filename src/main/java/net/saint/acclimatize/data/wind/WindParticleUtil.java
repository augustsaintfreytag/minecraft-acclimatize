package net.saint.acclimatize.data.wind;

import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.saint.acclimatize.ModClient;

public class WindParticleUtil {

	public static void renderWindParticles(MinecraftClient client) {
		var player = client.player;
		var world = client.world;
		var random = world.getRandom();
		var bound = Math.max(1, 16 + (int) ModClient.getWindTemperature());

		if (random.nextInt(bound) == 0) {
			var windDirectionRadians = Math.toRadians(ModClient.getWindDirection());
			var direction = new Vec3d(-Math.sin(windDirectionRadians), 0, Math.cos(windDirectionRadians));

			var x = player.getX() + random.nextTriangular(0, 10) - direction.x * 7;
			var y = player.getY() + random.nextTriangular(5, 7);
			var z = player.getZ() + random.nextTriangular(0, 10) - direction.z * 7;

			world.addParticle(ParticleTypes.POOF, x, y, z, direction.x, direction.y, direction.z);
		}
	}

}
