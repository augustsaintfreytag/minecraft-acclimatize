package net.saint.acclimatize.data.space;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.util.MathUtil;

public final class SpaceUtil {

	// Configuration

	private static final double CONE_ANGLE = Math.toRadians(45);
	private static final double BASE_COS_ANGLE = MathUtil.cos(CONE_ANGLE);
	private static final double BASE_SIN_ANGLE = MathUtil.sin(CONE_ANGLE);

	// State

	private static final Map<UUID, boolean[]> playerSpaceBuffers = new HashMap<>();
	private static final Map<UUID, Boolean> playerLastSpacePreCheck = new HashMap<>();
	private static final Map<UUID, Integer> playerSpaceIndices = new HashMap<>();

	// Checks

	public static boolean checkPlayerIsInInterior(ServerPlayerEntity player) {
		var profile = Mod.PROFILER.begin("space_check");
		var playerId = player.getUuid();
		var world = player.getWorld();

		// Pre-check by raycasting once straight up from player position.
		var lastPreCheckResult = playerLastSpacePreCheck.computeIfAbsent(playerId, k -> false).booleanValue();
		var preCheckResult = performStandaloneRaycastForPositionInInterior(world, player);
		playerLastSpacePreCheck.put(playerId, preCheckResult);

		if (!preCheckResult) {
			// Pre-check raycast hit did not hit blocks, assume exterior.
			// Having a single block above your head does not make an interior
			// but having no block above your head definitively makes an exterior.
			cleanUpPlayerData(player);
			profile.end();

			if (Mod.CONFIG.enableLogging) {
				Mod.LOGGER.info("Space check raycast (hit sky, pre-check), duration: " + profile.getDescription());
			}

			return false;
		}

		if (lastPreCheckResult) {
			cleanUpPlayerData(player);
		}

		if (!performAccumulativeRaycastForPositionInInterior(world, player)) {
			// Rays in ring buffer did not hit blocks, assume exterior.
			profile.end();

			if (Mod.CONFIG.enableLogging) {
				Mod.LOGGER.info("Space check raycast (hit sky, extended check), duration: " + profile.getDescription());
			}
			return false;
		}

		profile.end();

		if (Mod.CONFIG.enableLogging) {
			Mod.LOGGER.info("Space check raycast (hit block, extended check), duration: " + profile.getDescription());
		}

		return true;
	}

	private static boolean performStandaloneRaycastForPositionInInterior(World world, ServerPlayerEntity player) {
		var origin = player.getPos();
		var rayLength = Mod.CONFIG.spaceRayLength;
		var direction = new Vec3d(0, 1, 0);
		var target = origin.add(direction.multiply(rayLength));

		var hitResult = world
				.raycast(new RaycastContext(origin, target, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));

		return !raycastResultHitVoid(world, hitResult);
	}

	private static boolean performAccumulativeRaycastForPositionInInterior(World world, ServerPlayerEntity player) {
		var playerId = player.getUuid();

		// Initialize buffer for this player if needed
		if (!playerSpaceBuffers.containsKey(playerId)) {
			playerSpaceBuffers.put(playerId, new boolean[Mod.CONFIG.spaceNumberOfRays]);
			playerSpaceIndices.put(playerId, 0);
		}

		var buffer = playerSpaceBuffers.get(playerId);
		var currentIndex = playerSpaceIndices.get(playerId);

		// Calculate ray offset for this check
		var rayOffset = currentIndex % Mod.CONFIG.spaceNumberOfRays;

		// Perform single raycast and store result (true = ray hit sky)
		buffer[currentIndex] = performSingleSpaceRaycast(world, player, rayOffset);

		// Update index for next call
		currentIndex = (currentIndex + 1) % Mod.CONFIG.spaceNumberOfRays;
		playerSpaceIndices.put(playerId, currentIndex);

		// Count rays that hit sky - any hit means we're outside
		for (boolean hitSky : buffer) {
			if (hitSky) {
				// Found a ray that hit sky, player is outdoors
				return false;
			}
		}

		// All rays hit blocks, player is indoors
		return true;
	}

	private static boolean performSingleSpaceRaycast(World world, ServerPlayerEntity player, int offset) {
		var origin = player.getPos();
		var theta = 2 * Math.PI * offset / Mod.CONFIG.spaceNumberOfRays;
		var direction = new Vec3d(BASE_SIN_ANGLE * MathUtil.cos(theta), BASE_COS_ANGLE, BASE_SIN_ANGLE * MathUtil.sin(theta));
		var target = origin.add(direction.multiply(Mod.CONFIG.spaceRayLength));

		var hitResult = world
				.raycast(new RaycastContext(origin, target, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));

		return raycastResultHitVoid(world, hitResult);
	}

	private static boolean raycastResultHitVoid(World world, HitResult hitResult) {
		if (hitResult.getType() == HitResult.Type.MISS) {
			return true;
		}

		var hitPosition = BlockPos.ofFloored(hitResult.getPos());
		var hitBlock = world.getBlockState(hitPosition).getBlock();
		var hitBlockId = Registries.BLOCK.getId(hitBlock).toString();

		// Check if hit block is leaves or other outdoors block.
		if (hitBlockId.contains("leaves") || hitBlockId.contains("grass") || hitBlockId.contains("crop") || hitBlockId.contains("sugar")) {
			// Hit a block that is outdoors, return true (presume hit sky)
			return true;
		}

		return false;
	}

	// Buffer

	public static void cleanUpPlayerData(ServerPlayerEntity player) {
		var playerId = player.getUuid();

		playerSpaceBuffers.remove(playerId);
		playerSpaceIndices.remove(playerId);
		playerLastSpacePreCheck.remove(playerId);
	}

}
