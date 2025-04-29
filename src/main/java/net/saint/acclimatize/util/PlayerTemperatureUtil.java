package net.saint.acclimatize.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.player.PlayerState;
import net.saint.acclimatize.server.ServerState;

public class PlayerTemperatureUtil {

	public static void tickPlayerTemperature(ServerPlayerEntity player, ServerState serverState,
			PlayerState playerState) {
		// Prerequisites

		var isInInterior = SpaceUtil.checkPlayerIsInInterior(player);

		// Biome Temperature

		var biomeTemperature = BiomeTemperatureUtil.biomeTemperatureForPlayer(player, isInInterior);
		var effectiveTemperature = biomeTemperature.median;

		// Item Temperature (Wearables)

		var itemTemperatureDelta = ItemTemperatureUtil.temperatureDeltaForAllArmorItems(player);
		effectiveTemperature += itemTemperatureDelta;

		// Block Temperature (Heating & Cooling)

		var blockTemperatureDelta = BlockTemperatureUtil.temperatureDeltaForBlocksInVicinity(player);
		effectiveTemperature += blockTemperatureDelta;

		// Wind

		var windTemperatureTuple = WindTemperatureUtil.windTemperatureForEnvironment(serverState, player, isInInterior);
		var windTemperatureDelta = windTemperatureTuple.temperature * windTemperatureTuple.windChillFactor;

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

		acclimatizationRate = MathUtil.clamp(acclimatizationRate, Mod.CONFIG.itemAcclimatizationRateMinimum, 1.0);

		// Player Temperature

		var bodyTemperature = playerState.bodyTemperature;
		bodyTemperature += (effectiveTemperature - bodyTemperature) * acclimatizationRate;

		// State

		playerState.isInInterior = isInInterior;
		playerState.acclimatizationRate = acclimatizationRate;

		if (playerState.bodyTemperature == 0.0) {
			// First tick, set body temperature to effective temperature.
			bodyTemperature = effectiveTemperature;
		} else {
			playerState.bodyTemperature = bodyTemperature;
		}

		playerState.ambientTemperature = effectiveTemperature;
		playerState.biomeTemperature = biomeTemperature.median;
		playerState.blockTemperature = blockTemperatureDelta;
		playerState.itemTemperature = itemTemperatureDelta;
		playerState.windTemperature = windTemperatureDelta;
	}

}
