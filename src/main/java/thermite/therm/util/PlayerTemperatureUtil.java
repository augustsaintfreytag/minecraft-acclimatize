package thermite.therm.util;

import net.minecraft.server.network.ServerPlayerEntity;
import thermite.therm.ThermMod;
import thermite.therm.player.PlayerState;
import thermite.therm.server.ServerState;

public class PlayerTemperatureUtil {

	public static void tickPlayerTemperature(ServerPlayerEntity player, ServerState serverState,
			PlayerState playerState) {
		// Ambient Temperature

		var ambientTemperature = AmbientTemperatureUtil.ambientTemperatureForPlayer(player);
		var effectiveTemperature = ambientTemperature.medTemperature;

		// Wearable Item Temperature

		var wearableTemperatureDelta = ItemTemperatureUtil.temperatureDeltaForAllArmorItems(player);
		effectiveTemperature += wearableTemperatureDelta;

		// Heat Source Temperature

		var environmentalTemperatureDelta = EnvironmentalTemperatureUtil.temperatureDeltaForEnvironment(player);
		effectiveTemperature += environmentalTemperatureDelta;

		// Wind

		var windTemperatureTuple = WindTemperatureUtil.windTemperatureForEnvironment(player, playerState, serverState);
		var windTemperatureDelta = windTemperatureTuple.temperature * windTemperatureTuple.windChillFactor;

		effectiveTemperature += windTemperatureDelta;

		// Effects

		var effectsTemperatureDelta = StatusEffectsTemperatureUtil.temperatureDeltaForItemsAndStatusEffects(player);
		effectiveTemperature += effectsTemperatureDelta;

		// State Changes

		var bodyTemperature = playerState.bodyTemperature;
		var acclimatizationRate = ThermMod.CONFIG.acclimatizationRate;

		// Newton’s Law (discretized)

		bodyTemperature += (effectiveTemperature - bodyTemperature) * acclimatizationRate;

		// State

		playerState.bodyTemperature = Math.round(bodyTemperature * 100.0) / 100.0;

		playerState.ambientTemperature = Math.round(effectiveTemperature * 100.0) / 100.0;
		playerState.ambientMinTemperature = ambientTemperature.minTemperature;
		playerState.ambientMaxTemperature = ambientTemperature.maxTemperature;

		playerState.windTemperature = Math.round(windTemperatureTuple.temperature * 100.0) / 100.0;
	}

}
