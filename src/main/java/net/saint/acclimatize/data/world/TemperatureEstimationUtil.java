package net.saint.acclimatize.data.world;

import net.saint.acclimatize.Mod;
import net.saint.acclimatize.player.PlayerState;

public class TemperatureEstimationUtil {

	// Configuration

	private static final double TIME_ESTIMATION_TOLERANCE = 1.0;

	// Library

	public static class EffectTargetEstimation {
		public static enum Target {
			HYPOTHERMIA, HYPERTHERMIA, AMBIENT
		}

		public final int ticks;
		public final Target target;

		public EffectTargetEstimation(int ticks, Target target) {
			this.ticks = ticks;
			this.target = target;
		}

		public String description() {
			var timeDescription = descriptionForTime(ticks);
			var targetDescription = descriptionForTarget(target);

			return timeDescription + " to " + targetDescription;
		}

		private static String descriptionForTime(int ticks) {
			if (ticks == Integer.MAX_VALUE) {
				return "infinite";
			}

			var seconds = (ticks * Mod.CONFIG.temperatureTickInterval) / 20;

			if (seconds < 60) {
				return seconds + "s";
			}

			var minutes = seconds / 60;
			return minutes + "m";
		}

		private static String descriptionForTarget(Target target) {
			switch (target) {
			case HYPOTHERMIA:
				return "hypothermia";
			case HYPERTHERMIA:
				return "hyperthermia";
			case AMBIENT:
				return "ambient";
			default:
				return "unknown";
			}
		}
	}

	// Estimation (General)

	public static int estimateTicksToTargetTemperature(double currentTemperature, double ambientTemperature, double acclimatizationRate) {
		var delta = Math.abs(ambientTemperature - currentTemperature);

		if (delta <= TIME_ESTIMATION_TOLERANCE) {
			// For small differences, assume temperature has already been reached.
			return 0;
		}

		if (acclimatizationRate <= 0) {
			// For zero or negative acclimatization rate, changes take infinite time.
			return Integer.MAX_VALUE;
		}

		if (acclimatizationRate >= 1) {
			// For maximum acclimatization rate, changes are immediate.
			return 1;
		}

		var complementAcclimatizationRate = 1 - acclimatizationRate;

		// n = ln(tolerance/delta) / ln(1-r)
		var ticks = (int) Math.ceil(Math.log(TIME_ESTIMATION_TOLERANCE / delta) / Math.log(complementAcclimatizationRate));
		ticks = Math.max(ticks, 1);

		return ticks;
	}

	// Estimation (Player)

	public static EffectTargetEstimation estimateTicksToExtremeTemperatureForPlayer(PlayerState player) {
		var bodyTemperature = player.bodyTemperature;
		var ambientTemperature = player.ambientTemperature;
		var acclimatizationRate = player.acclimatizationRate;

		if (ambientTemperature < Mod.CONFIG.hypothermiaThresholdMinor) {
			// Estimate time to hypothermia.
			var ticks = estimateTicksToTargetTemperature(bodyTemperature, Mod.CONFIG.hypothermiaThresholdMinor, acclimatizationRate);
			return new EffectTargetEstimation(ticks, EffectTargetEstimation.Target.HYPOTHERMIA);
		}

		if (ambientTemperature > Mod.CONFIG.hyperthermiaThresholdMinor) {
			// Estimate time to hyperthermia.
			var ticks = estimateTicksToTargetTemperature(bodyTemperature, Mod.CONFIG.hyperthermiaThresholdMinor, acclimatizationRate);
			return new EffectTargetEstimation(ticks, EffectTargetEstimation.Target.HYPERTHERMIA);
		}

		var ticks = estimateTicksToTargetTemperature(bodyTemperature, ambientTemperature, acclimatizationRate);
		return new EffectTargetEstimation(ticks, EffectTargetEstimation.Target.AMBIENT);
	}

}
