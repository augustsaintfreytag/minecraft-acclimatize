package net.saint.acclimatize.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.biome.Biome;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.server.ServerState;

public final class WindTemperatureUtil {

	// Configuration

	private static final double WIND_RAY_TURBULENCE = Math.toRadians(25);

	// State

	private static int numberOfRaysFired = 0;

	private static final Map<UUID, boolean[]> playerWindBuffers = new HashMap<>();
	private static final Map<UUID, Integer> playerBufferIndices = new HashMap<>();

	// Wind Effects

	public static double windTemperatureForEnvironment(ServerState serverState, ServerPlayerEntity player, boolean isInInterior) {
		var world = player.getWorld();
		var dimension = world.getDimension();

		if (!Mod.CONFIG.enableWind || isInInterior || player.isSubmergedInWater() || !dimension.natural()) {
			cleanUpPlayerData(player);
			return 0.0;
		}

		// Base Wind Temperature

		var windTemperature = serverState.windIntensity * Mod.CONFIG.windChillFactor;

		// Precipitation Wind Chill

		var precipitationWindFactor = precipitationTemperatureFactorForPlayer(serverState, player);
		windTemperature *= precipitationWindFactor;

		// Wind Raycast Hit Factor

		var numberOfUnblockedRays = getUnblockedWindRaysForPlayer(serverState, player);
		var windHitTemperatureFactor = ((double) numberOfUnblockedRays / (double) numberOfRaysFired);

		windTemperature *= windHitTemperatureFactor;

		return windTemperature;
	}

	private static double precipitationTemperatureFactorForPlayer(ServerState serverState, ServerPlayerEntity player) {
		var world = player.getWorld();
		var position = player.getBlockPos();
		var biome = world.getBiome(position).value();
		var precipitation = biome.getPrecipitation(position);

		if (precipitation == Biome.Precipitation.RAIN) {
			if (world.isThundering()) {
				return 1.3;
			} else {
				return 1.1;
			}
		} else if (precipitation == Biome.Precipitation.SNOW) {
			if (world.isRaining()) {
				return 1.2;
			}
		}

		return 1.0;
	}

	private static int getUnblockedWindRaysForPlayer(ServerState serverState, ServerPlayerEntity player) {
		var playerId = player.getUuid();

		// Initialize buffer for this player if needed
		if (!playerWindBuffers.containsKey(playerId)) {
			playerWindBuffers.put(playerId, new boolean[Mod.CONFIG.windRayCount]);
			playerBufferIndices.put(playerId, 0);
		}

		var buffer = playerWindBuffers.get(playerId);
		var currentIndex = playerBufferIndices.get(playerId);

		// Profile Start Time
		var profile = Mod.PROFILER.begin("wind");

		// Perform only a single raycast and store the result
		buffer[currentIndex] = performSingleWindRaycast(serverState, player);
		numberOfRaysFired = Math.min(numberOfRaysFired + 1, Mod.CONFIG.windRayCount);

		// Update index for next call
		currentIndex = (currentIndex + 1) % Mod.CONFIG.windRayCount;
		playerBufferIndices.put(playerId, currentIndex);

		// Count unblocked rays in the buffer
		var numberOfUnblockedRays = 0;

		for (var isUnblocked : buffer) {
			if (isUnblocked) {
				numberOfUnblockedRays++;
			}
		}

		profile.end();

		if (Mod.CONFIG.enableLogging) {
			Mod.LOGGER.info("Wind raycast, " + numberOfUnblockedRays + " unblocked ray(s), duration: " + profile.getDescription());
		}

		return numberOfUnblockedRays;
	}

	private static boolean performSingleWindRaycast(ServerState serverState, ServerPlayerEntity player) {
		var world = player.getWorld();
		var random = world.getRandom();

		var windDirection = serverState.windDirection;
		var turbulentAngle = windDirection + Math.PI + random.nextTriangular(0, WIND_RAY_TURBULENCE);
		var directionVector = new Vec3d(MathUtil.sin(turbulentAngle), 0, MathUtil.cos(turbulentAngle));

		var startVector = new Vec3d(player.getPos().x, player.getPos().y + 1, player.getPos().z);
		var endVector = startVector.add(directionVector.multiply(Mod.CONFIG.windRayLength));

		var hitResult = world.raycast(
				new RaycastContext(startVector, endVector, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));

		// Return true if ray is unblocked (missed all blocks)
		return hitResult.getType() == HitResult.Type.MISS;
	}

	// Buffer

	public static void cleanUpPlayerData(ServerPlayerEntity player) {
		var playerId = player.getUuid();
		playerWindBuffers.remove(playerId);
		playerBufferIndices.remove(playerId);
	}

}
