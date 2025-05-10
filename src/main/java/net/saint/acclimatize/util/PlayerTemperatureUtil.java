package net.saint.acclimatize.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.player.PlayerState;
import net.saint.acclimatize.server.ServerState;

public class PlayerTemperatureUtil {

	public static void tickPlayerTemperature(ServerPlayerEntity player, ServerState serverState, PlayerState playerState) {
		// Prerequisites

		var isInInterior = SpaceUtil.checkPlayerIsInInterior(player);

		// Biome Temperature

		var biomeTemperature = BiomeTemperatureUtil.biomeTemperatureForPlayer(player, isInInterior);
		var effectiveTemperature = biomeTemperature;

		// Item Temperature (Wearables)

		var itemTemperatureDelta = ItemTemperatureUtil.temperatureDeltaForAllArmorItems(player);
		effectiveTemperature += itemTemperatureDelta;

		// Block Temperature (Heating & Cooling)

		var blockTemperatureDelta = BlockTemperatureUtil.temperatureDeltaForBlocksInVicinity(player);
		effectiveTemperature += blockTemperatureDelta;

		// Wind

		var windTemperatureDelta = WindTemperatureUtil.windTemperatureForEnvironment(serverState, player, isInInterior);
		effectiveTemperature += windTemperatureDelta;

		// Effects

		var effectsTemperatureDelta = StatusEffectsTemperatureUtil.temperatureDeltaForItemsAndStatusEffects(player);
		effectiveTemperature += effectsTemperatureDelta;

		// Acclimatization Rate

		var acclimatizationRate = Mod.CONFIG.acclimatizationRate;

		if (blockTemperatureDelta > Mod.CONFIG.blockTemperatureAcclimatizationBoostThreshold
				|| blockTemperatureDelta < -Mod.CONFIG.blockTemperatureAcclimatizationBoostThreshold) {
			// Boost acclimatization when excessively heated or cooled by blocks.
			acclimatizationRate *= Mod.CONFIG.blockAcclimatizationBoostFactor;
		} else {
			// Reduce acclimatization when wearing items.
			acclimatizationRate += ItemTemperatureUtil.acclimatizationRateDeltaForItemTemperature(itemTemperatureDelta);
		}

		if (player.isWet()) {
			// Increase acclimatization rate when wet.
			acclimatizationRate *= Mod.CONFIG.wetAcclimatizationRateBoostFactor;
		}

		// Player Temperature

		acclimatizationRate = applicableAcclimatizationRate(acclimatizationRate);

		var bodyTemperature = playerState.bodyTemperature;
		bodyTemperature += (effectiveTemperature - bodyTemperature) * acclimatizationRate;

		// State

		playerState.isInInterior = isInInterior;
		playerState.acclimatizationRate = acclimatizationRate;
		playerState.bodyTemperature = bodyTemperature;
		playerState.ambientTemperature = effectiveTemperature;
		playerState.biomeTemperature = biomeTemperature;
		playerState.blockTemperature = blockTemperatureDelta;
		playerState.itemTemperature = itemTemperatureDelta;
		playerState.windTemperature = windTemperatureDelta;
	}

	public static double applicableAcclimatizationRate(double acclimatizationRate) {
		return MathUtil.clamp(acclimatizationRate / 10.0, Mod.CONFIG.acclimatizationRateMinimum, 1.0);
	}

}
