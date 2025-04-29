package net.saint.acclimatize.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.saint.acclimatize.Mod;

public final class SpaceUtil {

	private static final double CONE_ANGLE = Math.toRadians(45);
	private static final double BASE_COS_ANGLE = MathUtil.approximateCos(CONE_ANGLE);
	private static final double BASE_SIN_ANGLE = MathUtil.approximateSin(CONE_ANGLE);

	// Checks

	public static boolean checkPlayerIsInInterior(ServerPlayerEntity player) {
		var profile = Mod.PROFILER.begin("space_check");

		var world = player.getWorld();

		// Pre-check by raycasting once straight up from player position.
		if (!preCheckRaycastForPositionInInterior(world, player)) {
			// Ray hit a block, assume indoors.
			profile.end();
			Mod.LOGGER.info("Space check raycast (hit sky, pre-check only): " + profile.getDescription());
			return false;
		}

		if (!getRaycastResultForPositionInInterior(world, player)) {
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

	private static boolean preCheckRaycastForPositionInInterior(World world, ServerPlayerEntity player) {
		var origin = player.getPos();
		var rayLength = Mod.CONFIG.spaceRayLength;
		var direction = new Vec3d(0, 1, 0);
		var target = origin.add(direction.multiply(rayLength));

		var preCheckHitResult = world.raycast(new RaycastContext(
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

	private static boolean getRaycastResultForPositionInInterior(World world, ServerPlayerEntity player) {
		var numberOfRays = Mod.CONFIG.spaceNumberOfRays;

		for (int i = 0; i < numberOfRays; i++) {
			if (performSingleSpaceRaycast(world, player, i)) {
				// Ray hit a block, assume indoors.
				return false;
			}
		}

		return true;
	}

	private static boolean performSingleSpaceRaycast(World world, ServerPlayerEntity player, int offset) {
		var origin = player.getPos();
		var theta = 2 * Math.PI * offset / Mod.CONFIG.spaceNumberOfRays;
		var direction = new Vec3d(
				BASE_SIN_ANGLE * MathUtil.approximateCos(theta),
				BASE_COS_ANGLE,
				BASE_SIN_ANGLE * MathUtil.approximateSin(theta));
		var target = origin.add(direction.multiply(Mod.CONFIG.spaceRayLength));

		var hitResult = world.raycast(new RaycastContext(
				origin, target,
				RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.NONE,
				player));

		return hitResult.getType() == HitResult.Type.MISS;
	}

}
