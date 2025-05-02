package net.saint.acclimatize.server;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;
import net.saint.acclimatize.player.PlayerState;

public class ServerState extends PersistentState {

	// Server metadata
	public String worldVersion = "4.1.0.8";

	// Seasonal state
	public int season = 0;
	public int seasonTick = 0;
	public long currentSeasonTick = 0;
	public int seasonalWeatherTick = 0;

	// Wind parameters
	public double windDirection = 0.0;
	public double windTemperatureModifierRange = 8;
	public double windTemperature = 0;
	public double precipitationWindModifier = 0;

	public HashMap<UUID, PlayerState> players = new HashMap<>();

	// NBT

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		// Player State Properties

		NbtCompound playersNbt = new NbtCompound();

		for (var entry : players.entrySet()) {
			UUID id = entry.getKey();
			PlayerState playerState = entry.getValue();

			playersNbt.put(id.toString(), playerState.writeNbt(new NbtCompound()));
		}

		nbt.put(ServerStateNBTKeys.players, playersNbt);

		// Server State Properties

		nbt.putString(ServerStateNBTKeys.worldVersion, worldVersion);
		nbt.putInt(ServerStateNBTKeys.season, season);
		nbt.putInt(ServerStateNBTKeys.seasonTick, seasonTick);
		nbt.putLong(ServerStateNBTKeys.currentSeasonTick, currentSeasonTick);
		nbt.putInt(ServerStateNBTKeys.seasonalWeatherTick, seasonalWeatherTick);
		nbt.putDouble(ServerStateNBTKeys.windDirection, windDirection);
		nbt.putDouble(ServerStateNBTKeys.windTemperatureModifierRange, windTemperatureModifierRange);
		nbt.putDouble(ServerStateNBTKeys.windTemperatureModifier, windTemperature);
		nbt.putDouble(ServerStateNBTKeys.precipitationWindModifier, precipitationWindModifier);

		return nbt;
	}

	public static ServerState createFromNbt(NbtCompound tag) {
		ServerState serverState = new ServerState();

		// Player State Properties

		NbtCompound playersTag = tag.getCompound(ServerStateNBTKeys.players);

		for (String key : playersTag.getKeys()) {
			NbtCompound ptag = playersTag.getCompound(key);
			PlayerState ps = PlayerState.fromNbt(ptag);
			serverState.players.put(UUID.fromString(key), ps);
		}

		// Server State Properties

		serverState.worldVersion = tag.getString(ServerStateNBTKeys.worldVersion);
		serverState.season = tag.getInt(ServerStateNBTKeys.season);
		serverState.seasonTick = tag.getInt(ServerStateNBTKeys.seasonTick);
		serverState.currentSeasonTick = tag.getLong(ServerStateNBTKeys.currentSeasonTick);
		serverState.seasonalWeatherTick = tag.getInt(ServerStateNBTKeys.seasonalWeatherTick);
		serverState.windDirection = tag.getDouble(ServerStateNBTKeys.windDirection);
		serverState.windTemperatureModifierRange = tag.getDouble(ServerStateNBTKeys.windTemperatureModifierRange);
		serverState.windTemperature = tag.getDouble(ServerStateNBTKeys.windTemperatureModifier);
		serverState.precipitationWindModifier = tag.getDouble(ServerStateNBTKeys.precipitationWindModifier);

		return serverState;
	}
}