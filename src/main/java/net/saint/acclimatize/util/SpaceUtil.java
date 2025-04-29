package net.saint.acclimatize.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.saint.acclimatize.Mod;

public final class SpaceUtil {

	private static final double CONE_ANGLE = Math.toRadians(45);

	public static boolean checkPlayerIsInInterior(ServerPlayerEntity player) {
		var profile = Mod.PROFILER.begin("space_check");

		var world = player.getWorld();
		var origin = player.getPos();

		// Pre-check by raycasting once straight up from player position.
		if (!preCheckRaycastForPositionInInterior(world, player, origin)) {
			// Ray hit a block, assume indoors.
			profile.end();
			Mod.LOGGER.info("Space check raycast (hit sky, pre-check only): " + profile.getDescription());
			return false;
		}

		if (!checkRaycastForPositionInInterior(world, player, origin)) {
			// Ray sequence hit a block, assume indoors.
			profile.end();
			Mod.LOGGER.info(
					"Space check raycast (hit sky, extended check), duration: " + profile.getDescription());
			return false;
		}

		profile.end();
		Mod.LOGGER.info(
				"Space check raycast (hit block, extended check), duration: " + profile.getDescription());
		return true;
	}

	private static boolean preCheckRaycastForPositionInInterior(World world, ServerPlayerEntity player,
			Vec3d origin) {
		final var rayLength = Mod.CONFIG.spaceRayLength;
		final var direction = new Vec3d(0, 1, 0);
		final var target = origin.add(direction.multiply(rayLength));

		final var preCheckHitResult = world.raycast(new RaycastContext(
				origin, target,
				RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.NONE,
				player));

		if (preCheckHitResult.getType() == HitResult.Type.MISS) {
			// Straight up ray hit the sky, assume outdoors.
			return false;
		}

		return true;
	}

	private static boolean checkRaycastForPositionInInterior(World world, ServerPlayerEntity player,
			Vec3d origin) {
		final var baseCosAngle = MathUtil.approximateCos(CONE_ANGLE);
		final var baseSinAngle = MathUtil.approximateSin(CONE_ANGLE);
		final var numberOfRays = Mod.CONFIG.spaceNumberOfRays;
		final var rayLength = Mod.CONFIG.spaceRayLength;

		for (int i = 0; i < numberOfRays; i++) {
			var theta = 2 * Math.PI * i / numberOfRays;
			var direction = new Vec3d(
					baseSinAngle * MathUtil.approximateCos(theta),
					baseCosAngle,
					baseSinAngle * MathUtil.approximateSin(theta));
			var target = origin.add(direction.multiply(rayLength));

			var hitResult = world.raycast(new RaycastContext(
					origin, target,
					RaycastContext.ShapeType.COLLIDER,
					RaycastContext.FluidHandling.NONE,
					player));

			if (hitResult.getType() == HitResult.Type.MISS) {
				// Ray hit the sky, assume outdoors.
				return false;
			}
		}

		return true;
	}

}
