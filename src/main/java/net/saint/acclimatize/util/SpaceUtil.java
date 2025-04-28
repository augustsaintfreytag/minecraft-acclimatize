package net.saint.acclimatize.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public final class SpaceUtil {

	private static final int NUMBER_OF_RAYS = 8;
	private static final double CONE_ANGLE = Math.toRadians(45);
	private static final double RAY_RANGE = 32.0;

	public static boolean checkPlayerIsInInterior(ServerPlayerEntity player) {
		var world = player.getWorld();
		var position = player.getBlockPos();

		// Pre-check if player is clearly exposed to sky.
		if (world.isSkyVisible(position)) {
			return false;
		}

		var origin = player.getPos();
		var baseCosAngle = MathUtil.approximateCos(CONE_ANGLE);
		var baseSinAngle = MathUtil.approximateSin(CONE_ANGLE);

		for (int i = 0; i < NUMBER_OF_RAYS; i++) {
			var theta = 2 * Math.PI * i / NUMBER_OF_RAYS;
			var direction = new Vec3d(
					baseSinAngle * MathUtil.approximateCos(theta),
					baseCosAngle,
					baseSinAngle * MathUtil.approximateSin(theta));
			var end = origin.add(direction.multiply(RAY_RANGE));

			var hitResult = world.raycast(new RaycastContext(
					origin, end,
					RaycastContext.ShapeType.COLLIDER,
					RaycastContext.FluidHandling.NONE,
					player));

			if (hitResult.getType() == HitResult.Type.MISS) {
				// Ray hit the sky, assume outdoors.
				return false;
			}
		}

		// All rays cast were blocked.
		return true;
	}

}
