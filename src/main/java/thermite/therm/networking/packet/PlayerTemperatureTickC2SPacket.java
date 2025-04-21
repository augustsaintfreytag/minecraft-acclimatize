package thermite.therm.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import thermite.therm.ThermMod;
import thermite.therm.networking.ThermNetworkingPackets;
import thermite.therm.player.PlayerState;
import thermite.therm.server.ServerState;
import thermite.therm.util.AmbientTemperatureUtil;
import thermite.therm.util.EnvironmentalTemperatureUtil;
import thermite.therm.util.ItemTemperatureUtil;
import thermite.therm.util.PlayerEffectsUtil;
import thermite.therm.util.StatusEffectsTemperatureUtil;
import thermite.therm.util.WindTemperatureUtil;

public class PlayerTemperatureTickC2SPacket {

	public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
			PacketByteBuf buf, PacketSender responseSender) {

		ServerState serverState = ServerState.getServerState(server);
		PlayerState playerState = ServerState.getPlayerState(player);

		// Ambient Temperature

		var ambientTemperature = AmbientTemperatureUtil.ambientTemperatureForPlayer(player);
		var effectiveTemperature = ambientTemperature.medTemperature;

		// Wearable Item Temperature

		var wearableTemperatureDelta = ItemTemperatureUtil.temperatureDeltaForAllArmorItems(player);
		effectiveTemperature += wearableTemperatureDelta;

		// Heat Source Temperature

		var environmentalTemperatureDelta = EnvironmentalTemperatureUtil.temperatureDeltaForEnvironment(player, 8);
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
		var timeDelta = 1.0 / 20.0;

		// Newton’s Law (discretized)

		bodyTemperature += (effectiveTemperature - bodyTemperature) * acclimatizationRate * timeDelta;

		playerState.bodyTemperature = Math.round(bodyTemperature * 100.0) / 100.0;
		playerState.ambientTemperature = Math.round(effectiveTemperature * 100.0) / 100.0;
		playerState.ambientMinTemperature = ambientTemperature.minTemperature;
		playerState.ambientMaxTemperature = ambientTemperature.maxTemperature;

		// Damage

		PlayerEffectsUtil.handlePlayerEffects(player, playerState);

		// Finalization

		var temperatureDifference = (short) (playerState.ambientTemperature - playerState.bodyTemperature);

		PacketByteBuf sendingdata = PacketByteBufs.create();

		sendingdata.writeDouble(playerState.bodyTemperature);
		sendingdata.writeShort(temperatureDifference);
		sendingdata.writeDouble(serverState.windPitch);
		sendingdata.writeDouble(serverState.windYaw);
		sendingdata.writeDouble(playerState.windTemperature);

		ServerPlayNetworking.send(player, ThermNetworkingPackets.SEND_THERMPLAYERSTATE_S2C_PACKET_ID, sendingdata);

		serverState.markDirty();
	}

}
