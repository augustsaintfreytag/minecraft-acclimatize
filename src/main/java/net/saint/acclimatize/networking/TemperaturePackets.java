package net.saint.acclimatize.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.ModClient;
import net.saint.acclimatize.networking.packet.PlayerTemperatureTickC2SPacket;

public class TemperaturePackets {

	public static class TemperaturePacketTuple {
		public double acclimatizationRate;
		public double bodyTemperature;
		public double ambientTemperature;
		public double windTemperature;
		public double windDirection;
		public double windIntensity;

		public TemperaturePacketTuple() {
			this.acclimatizationRate = 0;
			this.bodyTemperature = 0;
			this.ambientTemperature = 0;
			this.windTemperature = 0;
			this.windDirection = 0;
			this.windIntensity = 0;
		}

		public void encodeValuesToBuffer(PacketByteBuf buffer) {
			buffer.writeDouble(acclimatizationRate);
			buffer.writeDouble(bodyTemperature);
			buffer.writeDouble(ambientTemperature);
			buffer.writeDouble(windTemperature);
			buffer.writeDouble(windDirection);
			buffer.writeDouble(windIntensity);
		}

		public static TemperaturePacketTuple valuesFromBuffer(PacketByteBuf buffer) {
			var tuple = new TemperaturePacketTuple();

			tuple.acclimatizationRate = buffer.readDouble();
			tuple.bodyTemperature = buffer.readDouble();
			tuple.ambientTemperature = buffer.readDouble();
			tuple.windTemperature = buffer.readDouble();
			tuple.windDirection = buffer.readDouble();
			tuple.windIntensity = buffer.readDouble();

			return tuple;
		}
	}

	// Client to Server

	public static final Identifier PLAYER_TEMPERATURE_TICK_C2S_PACKET_ID = new Identifier(Mod.modId, "player_temperature_tick_c2s_packet");

	// Server to Client

	public static final Identifier SEND_TEMPERATURE_PLAYERSTATE_S2C_PACKET_ID = new Identifier(Mod.modId, "send_temperature_playerstate_s2c_packet");

	public static void registerC2SPackets() {
		ServerPlayNetworking.registerGlobalReceiver(PLAYER_TEMPERATURE_TICK_C2S_PACKET_ID, PlayerTemperatureTickC2SPacket::receive);
	}

	// Registration

	public static void registerS2CPackets() {
		ClientPlayNetworking.registerGlobalReceiver(SEND_TEMPERATURE_PLAYERSTATE_S2C_PACKET_ID, (client, handler, buf, responseSender) -> {
			double acclimatizationRate = buf.readDouble();
			double bodyTemperature = buf.readDouble();
			double ambientTemperature = buf.readDouble();
			double windTemperature = buf.readDouble();
			double windDirection = buf.readDouble();
			double windIntensity = buf.readDouble();

			client.execute(() -> {
				ModClient.cachedAcclimatizationRate = acclimatizationRate;
				ModClient.cachedBodyTemperature = bodyTemperature;
				ModClient.cachedAmbientTemperature = ambientTemperature;
				ModClient.cachedTemperatureDifference = ambientTemperature - bodyTemperature;
				ModClient.cachedWindTemperature = windTemperature;
				ModClient.cachedWindDirection = windDirection;
				ModClient.cachedWindIntensity = windIntensity;
			});
		});
	}
}