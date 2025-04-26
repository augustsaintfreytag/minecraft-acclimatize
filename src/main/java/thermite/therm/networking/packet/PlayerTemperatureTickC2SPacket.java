package thermite.therm.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import thermite.therm.networking.ThermNetworkingPackets;
import thermite.therm.player.PlayerState;
import thermite.therm.server.ServerState;
import thermite.therm.util.PlayerEffectsUtil;
import thermite.therm.util.PlayerTemperatureUtil;
import thermite.therm.util.ServerStateUtil;

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

		var temperatureDifference = (short) (playerState.ambientTemperature - playerState.bodyTemperature);

		PacketByteBuf sendingdata = PacketByteBufs.create();

		sendingdata.writeDouble(playerState.bodyTemperature);
		sendingdata.writeShort(temperatureDifference);
		sendingdata.writeDouble(serverState.windPitch);
		sendingdata.writeDouble(serverState.windYaw);
		sendingdata.writeDouble(playerState.windTemperature);

		ServerPlayNetworking.send(player, ThermNetworkingPackets.SEND_TEMPERATURE_PLAYERSTATE_S2C_PACKET_ID,
				sendingdata);

		serverState.markDirty();
	}

}
