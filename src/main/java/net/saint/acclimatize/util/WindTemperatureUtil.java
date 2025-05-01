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

	private static final double windBaseTurbulence = 23.0;
	private static final double windTurbulence = windBaseTurbulence * Math.PI / 180d;

	// State

	private static final Map<UUID, boolean[]> playerWindBuffers = new HashMap<>();
	private static final Map<UUID, Integer> playerBufferIndices = new HashMap<>();

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

	// Main

	public static WindTemperatureTuple windTemperatureForEnvironment(ServerState serverState, ServerPlayerEntity player,
			boolean isInInterior) {
		var world = player.getWorld();
		var dimension = world.getDimension();

		if (!Mod.CONFIG.enableWind || isInInterior || (!Mod.CONFIG.multidimensionalWind && !dimension.natural())) {
			cleanUpPlayerData(player);
			return WindTemperatureTuple.zero();
		}

		// Wind Base Temperature

		var windTemperature = serverState.windTemperature;

		var heightWindTemperatureDelta = heightTemperatureDeltaForPlayer(player);
		windTemperature += heightWindTemperatureDelta;

		var precipitationWindModifier = precipitationTemperatureDeltaForPlayer(serverState, player);
		windTemperature += precipitationWindModifier;

		// Wind Ray Calculation

		var numberOfUnblockedRays = getUnblockedWindRaysForPlayer(serverState, player);
		var biomeWindChillFactor = biomeWindChillFactorForPlayer(player);
		var windChillTemperatureFactor = ((double) numberOfUnblockedRays / Mod.CONFIG.windRayCount)
				* Mod.CONFIG.windChillFactor * biomeWindChillFactor;

		return new WindTemperatureTuple(windTemperature, windChillTemperatureFactor);
	}

	private static double biomeWindChillFactorForPlayer(ServerPlayerEntity player) {
		var world = player.getWorld();
		var biome = world.getBiome(player.getBlockPos()).value();
		var internalBiomeTemperature = biome.getTemperature();
		var climateKind = BiomeTemperatureUtil.climateKindForTemperature(internalBiomeTemperature);

		switch (climateKind) {
			case ARID:
				return 1.15;
			case HOT:
				return 0.9;
			default:
				return 1.0;
		}
	}

	private static double precipitationTemperatureDeltaForPlayer(ServerState serverState, ServerPlayerEntity player) {
		var precipitationWindModifier = serverState.precipitationWindModifier;
		var world = player.getWorld();
		var position = player.getBlockPos();
		var biome = world.getBiome(position).value();
		var precipitation = biome.getPrecipitation(position);

		if (precipitation == Biome.Precipitation.RAIN) {
			if (world.isThundering()) {
				return precipitationWindModifier * 1.1;
			} else {
				return precipitationWindModifier;
			}
		} else if (precipitation == Biome.Precipitation.SNOW) {
			if (world.isRaining()) {
				return precipitationWindModifier * 1.3;
			}
		}

		return 0.0;
	}

	private static double heightTemperatureDeltaForPlayer(ServerPlayerEntity player) {
		var coefficient = -0.02;
		var growthFactor = 1.5;
		var softeningFactor = 15.0;

		var height = player.getPos().y;
		var heightValue = height - 62.0;

		var lowerBound = -20.0;
		var upperBound = 15.0;

		var delta = coefficient * Math.signum(heightValue) * Math.pow(Math.abs(heightValue), growthFactor)
				- coefficient * Math.pow(softeningFactor, growthFactor);

		return MathUtil.clamp(delta, lowerBound, upperBound);
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

		// Update index for next call
		currentIndex = (currentIndex + 1) % Mod.CONFIG.windRayCount;
		playerBufferIndices.put(playerId, currentIndex);

		// Count unblocked rays in the buffer
		var numberOfUnblockedRays = 0;

		for (boolean isUnblocked : buffer) {
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
		var windYaw = serverState.windYaw;
		var windPitch = serverState.windPitch;
		var world = player.getWorld();
		var random = world.getRandom();

		var directionVector = new Vec3d(
				(MathUtil.approximateCos(windPitch + random.nextTriangular(0, windTurbulence))
						* MathUtil.approximateCos(windYaw + random.nextTriangular(0, windTurbulence))),
				(MathUtil.approximateSin(windPitch + random.nextTriangular(0, windTurbulence))
						* MathUtil.approximateCos(windYaw + random.nextTriangular(0, windTurbulence))),
				MathUtil.approximateSin(windYaw + random.nextTriangular(0, windTurbulence)));

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
