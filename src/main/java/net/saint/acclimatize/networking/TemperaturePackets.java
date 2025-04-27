package net.saint.acclimatize.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.ModClient;
import net.saint.acclimatize.networking.packet.PlayerTemperatureTickC2SPacket;

public class TemperaturePackets {

	// Client to Server

	public static final Identifier PLAYER_TEMPERATURE_TICK_C2S_PACKET_ID = new Identifier(Mod.modid,
			"player_temperature_tick_c2s_packet");

	// Server to Client

	public static final Identifier SEND_TEMPERATURE_PLAYERSTATE_S2C_PACKET_ID = new Identifier(Mod.modid,
			"send_temperature_playerstate_s2c_packet");

	public static void registerC2SPackets() {
		ServerPlayNetworking.registerGlobalReceiver(PLAYER_TEMPERATURE_TICK_C2S_PACKET_ID,
				PlayerTemperatureTickC2SPacket::receive);

	}

	// Registration

	public static void registerS2CPackets() {
		ClientPlayNetworking.registerGlobalReceiver(SEND_TEMPERATURE_PLAYERSTATE_S2C_PACKET_ID,
				(client, handler, buf, responseSender) -> {
					double temperature = buf.readDouble();
					short temperatureDifference = buf.readShort();
					double windPitch = buf.readDouble();
					double windYaw = buf.readDouble();
					double windTemperature = buf.readDouble();

					client.execute(() -> {
						ModClient.clientStoredTemperature = Math.round(temperature);
						ModClient.clientStoredTemperatureDifference = temperatureDifference;
						ModClient.clientStoredWindPitch = windPitch;
						ModClient.clientStoredWindYaw = windYaw;
						ModClient.clientStoredWindTemperature = windTemperature;
					});
				});
	}
}