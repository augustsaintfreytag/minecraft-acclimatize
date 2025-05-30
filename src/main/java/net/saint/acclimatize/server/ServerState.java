package net.saint.acclimatize.server;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.player.PlayerState;

public class ServerState extends PersistentState {

	// World Metadata

	public String worldVersion = Mod.MOD_VERSION;

	// Wind

	public double windDirection = 0.0;
	public double windIntensity = 0.0;

	public long nextWindDirectionTick = 0;
	public long nextWindIntensityTick = 0;

	// Player State

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
		nbt.putDouble(ServerStateNBTKeys.windDirection, windDirection);
		nbt.putDouble(ServerStateNBTKeys.windIntensity, windIntensity);
		nbt.putLong(ServerStateNBTKeys.nextWindDirectionTick, nextWindDirectionTick);
		nbt.putLong(ServerStateNBTKeys.nextWindIntensityTick, nextWindIntensityTick);

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
		serverState.windDirection = tag.getDouble(ServerStateNBTKeys.windDirection);
		serverState.windIntensity = tag.getDouble(ServerStateNBTKeys.windIntensity);
		serverState.nextWindDirectionTick = tag.getLong(ServerStateNBTKeys.nextWindDirectionTick);
		serverState.nextWindIntensityTick = tag.getLong(ServerStateNBTKeys.nextWindIntensityTick);

		return serverState;
	}
}