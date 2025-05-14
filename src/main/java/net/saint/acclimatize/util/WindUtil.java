package net.saint.acclimatize.util;

import net.minecraft.server.world.ServerWorld;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.server.ServerState;

public final class WindUtil {

	// Configuration

	private static final double WIND_INTERVAL_JITTER_FACTOR = 0.15;

	// State

	private static double lastWindDirection = 0;
	private static double targetWindDirection = 0;
	private static double lastWindIntensity = 0;
	private static double targetWindIntensity = 0;

	private static long lastWindUpdateTick = 0;

	// Wind Override

	public static void overrideWind(double windDirection, double windIntensity) {
		targetWindDirection = windDirection;
		targetWindIntensity = windIntensity;
	}

	// Wind Tick

	public static void tickWindInSchedule(ServerWorld world, ServerState serverState) {
		var random = world.getRandom();
		var serverTick = world.getTime();
		var dayTimeLength = Mod.CONFIG.daylightTicks + Mod.CONFIG.nighttimeTicks;

		if (dayTimeLength > serverState.nextWindIntensityTick) {
			tickWindIntensity(world);

			var intervalJitter = (int) (WIND_INTERVAL_JITTER_FACTOR * Mod.CONFIG.windIntensityUpdateInterval);
			serverState.nextWindIntensityTick = serverTick + Mod.CONFIG.windIntensityUpdateInterval
					+ random.nextBetween(-intervalJitter, intervalJitter);

			if (Mod.CONFIG.enableLogging) {
				Mod.LOGGER.info("Randomizing new wind intensity via tick at " + serverTick + ", next scheduled for "
						+ serverState.nextWindIntensityTick + ".");
			}

		}

		if (dayTimeLength > serverState.nextWindDirectionTick) {
			tickWindDirection(world);

			var intervalJitter = (int) (WIND_INTERVAL_JITTER_FACTOR * Mod.CONFIG.windDirectionUpdateInterval);
			serverState.nextWindDirectionTick = serverTick + Mod.CONFIG.windDirectionUpdateInterval
					+ random.nextBetween(-intervalJitter, intervalJitter);

			if (Mod.CONFIG.enableLogging) {
				Mod.LOGGER.info("Randomizing new wind direction via tick at " + serverTick + ", next scheduled for "
						+ serverState.nextWindDirectionTick + ".");
			}
		}

		tickWindTransition(world, serverState);
	}

	public static void tickWindTransition(ServerWorld world, ServerState serverState) {
		var serverTick = world.getTime();
		var deltaTime = serverTick - lastWindUpdateTick;

		if (deltaTime > Mod.CONFIG.windTransitionInterval || !needsWindTransitionUpdate(serverState)) {
			lastWindUpdateTick = serverTick;
			lastWindDirection = targetWindDirection;
			lastWindIntensity = targetWindIntensity;

			targetWindDirection = serverState.windDirection;
			targetWindIntensity = serverState.windIntensity;
		}

		var transitionFactor = (double) deltaTime / (double) Mod.CONFIG.windTransitionInterval;

		serverState.windDirection = MathUtil.lerp(lastWindDirection, targetWindDirection, transitionFactor);
		serverState.windIntensity = MathUtil.lerp(lastWindIntensity, targetWindIntensity, transitionFactor);
		serverState.setDirty(true);
	}

	private static boolean needsWindTransitionUpdate(ServerState serverState) {
		return serverState.windDirection != lastWindDirection || serverState.windIntensity != lastWindIntensity;
	}

	public static void tickWindDirectionAndIntensity(ServerWorld world, ServerState serverState) {
		tickWindDirection(world);
		tickWindIntensity(world);
	}

	private static void tickWindDirection(ServerWorld world) {
		var random = world.getRandom();
		var randomWindDirection = random.nextDouble() * 2 * Math.PI;

		targetWindDirection = randomWindDirection;
	}

	private static void tickWindIntensity(ServerWorld world) {
		var random = world.getRandom();
		var windIntensity = random.nextDouble() * (Mod.CONFIG.windIntensityMax + Mod.CONFIG.windIntensityMin)
				- Mod.CONFIG.windIntensityMin;

		if (world.isThundering()) {
			windIntensity *= 1.85;
		} else if (world.isRaining()) {
			windIntensity *= 1.4;
		}

		targetWindIntensity = windIntensity;
	}
}
