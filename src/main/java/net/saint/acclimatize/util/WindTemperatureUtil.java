package net.saint.acclimatize.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.biome.Biome;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.server.ServerState;

public final class WindTemperatureUtil {

	// Configuration

	private static final double WIND_BASE_TURBULENCE = 10.0;
	private static final double WIND_TURBULENCE = WIND_BASE_TURBULENCE * Math.PI / 180d;

	private static final double WIND_INTERVAL_JITTER_FACTOR = 0.15;

	// State

	private static int numberOfRaysFired = 0;

	private static final Map<UUID, boolean[]> playerWindBuffers = new HashMap<>();
	private static final Map<UUID, Integer> playerBufferIndices = new HashMap<>();

	private static boolean didLogWindPropertiesSource = false;

	// Library

	public static class WindTemperatureTuple {
		public final double temperature;
		public final double windChillFactor;

		public WindTemperatureTuple(double windTemperature, double windChillFactor) {
			this.temperature = windTemperature;
			this.windChillFactor = windChillFactor;
		}

		public static WindTemperatureTuple zero() {
			return new WindTemperatureTuple(0.0, 0.0);
		}
	}

	// Wind Tick

	public static void tickWindInSchedule(ServerWorld world, ServerState serverState) {
		if (FabricLoader.getInstance().isModLoaded("immersivewinds")) {
			if (Mod.CONFIG.enableLogging && !didLogWindPropertiesSource) {
				didLogWindPropertiesSource = true;
				Mod.LOGGER.info("Assigning deferred wind direction and intensity from loaded Immersive Winds.");
			}

			return;
		}

		var random = world.getRandom();
		var serverTick = world.getTime();
		var dayTimeLength = Mod.CONFIG.daylightTicks + Mod.CONFIG.nighttimeTicks;

		if (dayTimeLength > serverState.nextWindIntensityTick) {
			tickWindIntensity(world, serverState);

			var intervalJitter = (int) (WIND_INTERVAL_JITTER_FACTOR * Mod.CONFIG.windIntensityUpdateInterval);
			serverState.nextWindIntensityTick = serverTick + Mod.CONFIG.windIntensityUpdateInterval
					+ random.nextBetween(-intervalJitter, intervalJitter);

			if (Mod.CONFIG.enableLogging) {
				Mod.LOGGER.info("Randomizing new wind intensity via tick at " + serverTick + ", next scheduled for "
						+ serverState.nextWindIntensityTick + ".");
			}

		}

		if (dayTimeLength > serverState.nextWindDirectionTick) {
			tickWindDirection(world, serverState);

			var intervalJitter = (int) (WIND_INTERVAL_JITTER_FACTOR * Mod.CONFIG.windDirectionUpdateInterval);
			serverState.nextWindDirectionTick = serverTick + Mod.CONFIG.windDirectionUpdateInterval
					+ random.nextBetween(-intervalJitter, intervalJitter);

			if (Mod.CONFIG.enableLogging) {
				Mod.LOGGER.info("Randomizing new wind direction via tick at " + serverTick + ", next scheduled for "
						+ serverState.nextWindDirectionTick + ".");
			}
		}
	}

	public static void tickWindDirectionAndIntensity(ServerWorld world, ServerState serverState) {
		tickWindDirection(world, serverState);
		tickWindIntensity(world, serverState);
	}

	private static void tickWindDirection(ServerWorld world, ServerState serverState) {
		var random = world.getRandom();

		serverState.windDirection = random.nextDouble() * 2 * Math.PI;
		serverState.markDirty();
	}

	private static void tickWindIntensity(ServerWorld world, ServerState serverState) {
		var random = world.getRandom();

		serverState.windIntensity = random.nextTriangular(4.0, 8.0);
		serverState.markDirty();
	}

	// Wind Override

	public static void overrideWind(ServerState serverState, double windDirection, double windIntensity) {
		serverState.windDirection = windDirection;
		serverState.windIntensity = windIntensity;

		serverState.setDirty(true);
	}

	// Wind Effects

	public static double windTemperatureForEnvironment(ServerState serverState, ServerPlayerEntity player,
			boolean isInInterior) {
		var world = player.getWorld();
		var dimension = world.getDimension();

		if (!Mod.CONFIG.enableWind || isInInterior || player.isSubmergedInWater()
				|| (!Mod.CONFIG.multidimensionalWind && !dimension.natural())) {
			cleanUpPlayerData(player);
			return 0.0;
		}

		// Base Wind Temperature

		var windTemperature = serverState.windIntensity * -1.0;

		// Precipitation Wind Chill

		var precipitationWindFactor = precipitationTemperatureFactorForPlayer(serverState, player);
		windTemperature *= precipitationWindFactor;

		// Configurable Wind Chill

		var flatWindChillFactor = Mod.CONFIG.windChillFactor;
		windTemperature *= flatWindChillFactor;

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

		return 0.0;
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
			Mod.LOGGER.info("Wind raycast, " + numberOfUnblockedRays + " unblocked ray(s), duration: "
					+ profile.getDescription());
		}

		return numberOfUnblockedRays;
	}

	private static boolean performSingleWindRaycast(ServerState serverState, ServerPlayerEntity player) {
		var world = player.getWorld();
		var random = world.getRandom();

		var windDirection = serverState.windDirection;
		var turbulentAngle = windDirection + Math.PI + random.nextTriangular(0, WIND_TURBULENCE);
		var directionVector = new Vec3d(MathUtil.sin(turbulentAngle), 0,
				MathUtil.cos(turbulentAngle));

		var startVector = new Vec3d(player.getPos().x, player.getPos().y + 1, player.getPos().z);
		var endVector = startVector.add(directionVector.multiply(Mod.CONFIG.windRayLength));

		var hitResult = world.raycast(new RaycastContext(startVector, endVector,
				RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.NONE,
				player));

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
