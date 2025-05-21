package net.saint.acclimatize.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.saint.acclimatize.networking.TemperaturePackets;
import net.saint.acclimatize.player.PlayerState;
import net.saint.acclimatize.server.ServerState;
import net.saint.acclimatize.util.ServerStateUtil;

public class PlayerTemperatureTickC2SPacket {

	public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf,
			PacketSender responseSender) {

		ServerState serverState = ServerStateUtil.getServerState(server);
		PlayerState playerState = ServerStateUtil.getPlayerState(player);

		// Finalization

		var tuple = new TemperaturePackets.TemperaturePacketTuple();

		tuple.acclimatizationRate = playerState.acclimatizationRate;
		tuple.bodyTemperature = playerState.bodyTemperature;
		tuple.ambientTemperature = playerState.ambientTemperature;
		tuple.windTemperature = playerState.windTemperature;
		tuple.windDirection = serverState.windDirection;
		tuple.windIntensity = serverState.windIntensity;

		var outgoingBuffer = PacketByteBufs.create();
		tuple.encodeValuesToBuffer(outgoingBuffer);

		ServerPlayNetworking.send(player, TemperaturePackets.PLAYER_S2C_PACKET_ID, outgoingBuffer);
	}

}
