package net.saint.acclimatize.util;

import net.minecraft.server.world.ServerWorld;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.server.ServerState;

public final class WindUtil {

	// Configuration

	private static final double WIND_INTERVAL_JITTER_FACTOR = 0.15;

	// Wind Override

	public static void overrideWind(ServerState serverState, double windDirection, double windIntensity) {
		serverState.windDirection = windDirection;
		serverState.windIntensity = windIntensity;
	}

	// Wind Tick

	public static void tickWindInSchedule(ServerWorld world, ServerState serverState) {
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
		var randomWindDirection = Math.toRadians(random.nextDouble() * 360);

		serverState.windDirection = randomWindDirection;
		serverState.markDirty();
	}

	private static void tickWindIntensity(ServerWorld world, ServerState serverState) {
		var random = world.getRandom();
		var windIntensity = random.nextDouble() * (Mod.CONFIG.windIntensityMax + Mod.CONFIG.windIntensityMin)
				- Mod.CONFIG.windIntensityMin;

		if (world.isThundering()) {
			windIntensity *= 2.0;
		} else if (world.isRaining()) {
			windIntensity *= 1.65;
		}

		serverState.windIntensity = windIntensity;
		serverState.markDirty();
	}
}
