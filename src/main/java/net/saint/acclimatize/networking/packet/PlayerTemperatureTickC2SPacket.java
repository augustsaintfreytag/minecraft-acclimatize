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
import net.saint.acclimatize.util.PlayerEffectsUtil;
import net.saint.acclimatize.util.PlayerTemperatureUtil;
import net.saint.acclimatize.util.ServerStateUtil;

public class PlayerTemperatureTickC2SPacket {

	public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
			PacketByteBuf buf, PacketSender responseSender) {

		ServerState serverState = ServerStateUtil.getServerState(server);
		PlayerState playerState = ServerStateUtil.getPlayerState(player);

		// Temperature

		PlayerTemperatureUtil.tickPlayerTemperature(player, serverState, playerState);

		// Damage

		PlayerEffectsUtil.handlePlayerEffects(player, playerState);

		// Finalization

		PacketByteBuf sendingdata = PacketByteBufs.create();

		sendingdata.writeDouble(serverState.windDirection);
		sendingdata.writeDouble(serverState.windIntensity);
		sendingdata.writeDouble(playerState.bodyTemperature);
		sendingdata.writeDouble(playerState.ambientTemperature);
		sendingdata.writeDouble(playerState.windTemperature);

		ServerPlayNetworking.send(player, TemperaturePackets.SEND_TEMPERATURE_PLAYERSTATE_S2C_PACKET_ID,
				sendingdata);

		serverState.markDirty();
	}

}
