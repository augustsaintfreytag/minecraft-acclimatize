package net.saint.acclimatize.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.player.PlayerState;
import net.saint.acclimatize.server.ServerState;

public class PlayerTemperatureUtil {

	public static void tickPlayerTemperature(ServerPlayerEntity player, ServerState serverState,
			PlayerState playerState) {
		// Biome Temperature

		var biomeTemperature = BiomeTemperatureUtil.biomeTemperatureForPlayer(player);
		var effectiveTemperature = biomeTemperature.median;

		// Item Temperature (Wearables)

		var itemTemperatureDelta = ItemTemperatureUtil.temperatureDeltaForAllArmorItems(player);
		effectiveTemperature += itemTemperatureDelta;

		// Block Temperature (Heating & Cooling)

		var blockTemperatureDelta = BlockTemperatureUtil.temperatureDeltaForBlocksInVicinity(player);
		effectiveTemperature += blockTemperatureDelta;

		// Wind

		var windTemperatureTuple = WindTemperatureUtil.windTemperatureForEnvironment(player, playerState, serverState);
		var windTemperatureDelta = windTemperatureTuple.temperature * windTemperatureTuple.windChillFactor;

		effectiveTemperature += windTemperatureDelta;

		// Effects

		var effectsTemperatureDelta = StatusEffectsTemperatureUtil.temperatureDeltaForItemsAndStatusEffects(player);
		effectiveTemperature += effectsTemperatureDelta;

		// State Changes

		var bodyTemperature = playerState.bodyTemperature;
		var acclimatizationRate = Mod.CONFIG.acclimatizationRate;

		if (blockTemperatureDelta > Mod.CONFIG.blockTemperatureAcclimatizationBoostThreshold) {
			// Boost acclimatization when heating by block.
			acclimatizationRate *= Mod.CONFIG.blockAcclimatizationBoostFactor;
		}

		// Newton’s Law (discretized)

		bodyTemperature += (effectiveTemperature - bodyTemperature) * acclimatizationRate;

		// State

		playerState.temperatureRate = acclimatizationRate;
		playerState.bodyTemperature = roundedTemperatureValue(bodyTemperature);
		playerState.ambientTemperature = roundedTemperatureValue(effectiveTemperature);

		playerState.biomeTemperature = roundedTemperatureValue(biomeTemperature.median);
		playerState.blockTemperature = roundedTemperatureValue(blockTemperatureDelta);
		playerState.itemTemperature = roundedTemperatureValue(itemTemperatureDelta);
		playerState.windTemperature = roundedTemperatureValue(windTemperatureDelta);
	}

	private static double roundedTemperatureValue(double value) {
		return Math.round(value * 100.0) / 100.0;
	}

}
