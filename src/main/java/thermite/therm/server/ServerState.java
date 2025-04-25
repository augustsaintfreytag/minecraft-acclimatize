package thermite.therm.server;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import thermite.therm.ThermMod;
import thermite.therm.player.PlayerState;
import thermite.therm.player.PlayerStateNBTKeys;

public class ServerState extends PersistentState {

	public String worldVersion = "4.1.0.8";

	public int season = 0;
	public int seasonTick = 0;
	public long currentSeasonTick = 0;
	public int seasonalWeatherTick = 0;

	public double windPitch = 360 * Math.PI / 180;
	public double windYaw = 0;
	public int windRandomizeTick = 0;

	public double windTemperatureModifierRange = 8;
	public double windTemperatureModifier = 0;
	public double precipitationWindModifier = 0;

	public HashMap<UUID, PlayerState> players = new HashMap<>();

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {

		NbtCompound playersNbtCompound = new NbtCompound();
		players.forEach((UUID, playerSate) -> {
			NbtCompound playerStateNbt = new NbtCompound();

			playerStateNbt.putDouble(PlayerStateNBTKeys.temperature, playerSate.bodyTemperature);
			playerStateNbt.putDouble(PlayerStateNBTKeys.temperatureRate, playerSate.temperatureRate);
			playerStateNbt.putDouble(PlayerStateNBTKeys.restingTemperature, playerSate.ambientTemperature);
			playerStateNbt.putDouble(PlayerStateNBTKeys.minTemperature, playerSate.ambientMinTemperature);
			playerStateNbt.putDouble(PlayerStateNBTKeys.maxTemperature, playerSate.ambientMaxTemperature);
			playerStateNbt.putString(PlayerStateNBTKeys.damageType, playerSate.damageType);
			playerStateNbt.putInt(PlayerStateNBTKeys.damageTick, playerSate.damageTick);
			playerStateNbt.putInt(PlayerStateNBTKeys.maxDamageTick, playerSate.maxDamageTick);
			playerStateNbt.putDouble(PlayerStateNBTKeys.baseWindTemperature, playerSate.baseWindTemperature);
			playerStateNbt.putDouble(PlayerStateNBTKeys.windTemperature, playerSate.windTemperature);

			playersNbtCompound.put(String.valueOf(UUID), playerStateNbt);
		});

		nbt.put(ServerStateNBTKeys.players, playersNbtCompound);
		nbt.putString(ServerStateNBTKeys.worldVersion, worldVersion);
		nbt.putInt(ServerStateNBTKeys.season, season);
		nbt.putInt(ServerStateNBTKeys.seasonTick, seasonTick);
		nbt.putLong(ServerStateNBTKeys.currentSeasonTick, currentSeasonTick);
		nbt.putInt(ServerStateNBTKeys.seasonalWeatherTick, seasonalWeatherTick);
		nbt.putDouble(ServerStateNBTKeys.windPitch, windPitch);
		nbt.putDouble(ServerStateNBTKeys.windYaw, windYaw);
		nbt.putInt(ServerStateNBTKeys.windRandomizeTick, windRandomizeTick);
		nbt.putDouble(ServerStateNBTKeys.windTemperatureModifierRange, windTemperatureModifierRange);
		nbt.putDouble(ServerStateNBTKeys.windTemperatureModifier, windTemperatureModifier);
		nbt.putDouble(ServerStateNBTKeys.precipitationWindModifier, precipitationWindModifier);

		return nbt;
	}

	public static ServerState createFromNbt(NbtCompound tag) {
		ServerState serverState = new ServerState();
		NbtCompound playersTag = tag.getCompound("players");

		playersTag.getKeys().forEach(key -> {
			NbtCompound playerTag = playersTag.getCompound(key);
			PlayerState playerState = new PlayerState();

			playerState.bodyTemperature = playerTag.getDouble(PlayerStateNBTKeys.temperature);
			playerState.temperatureRate = playerTag.getDouble(PlayerStateNBTKeys.temperatureRate);
			playerState.ambientTemperature = playerTag.getDouble(PlayerStateNBTKeys.restingTemperature);
			playerState.ambientMinTemperature = playerTag.getDouble(PlayerStateNBTKeys.minTemperature);
			playerState.ambientMaxTemperature = playerTag.getDouble(PlayerStateNBTKeys.maxTemperature);
			playerState.damageType = playerTag.getString(PlayerStateNBTKeys.damageType);
			playerState.damageTick = playerTag.getInt(PlayerStateNBTKeys.damageTick);
			playerState.maxDamageTick = playerTag.getInt(PlayerStateNBTKeys.maxDamageTick);
			playerState.baseWindTemperature = playerTag.getDouble(PlayerStateNBTKeys.baseWindTemperature);
			playerState.windTemperature = playerTag.getDouble(PlayerStateNBTKeys.windTemperature);

			UUID uuid = UUID.fromString(key);
			serverState.players.put(uuid, playerState);
		});

		serverState.worldVersion = tag.getString(ServerStateNBTKeys.worldVersion);
		serverState.season = tag.getInt(ServerStateNBTKeys.season);
		serverState.seasonTick = tag.getInt(ServerStateNBTKeys.seasonTick);
		serverState.currentSeasonTick = tag.getLong(ServerStateNBTKeys.currentSeasonTick);
		serverState.seasonalWeatherTick = tag.getInt(ServerStateNBTKeys.seasonalWeatherTick);
		serverState.windPitch = tag.getDouble(ServerStateNBTKeys.windPitch);
		serverState.windYaw = tag.getDouble(ServerStateNBTKeys.windYaw);
		serverState.windRandomizeTick = tag.getInt(ServerStateNBTKeys.windRandomizeTick);
		serverState.windTemperatureModifierRange = tag.getDouble(ServerStateNBTKeys.windTemperatureModifierRange);
		serverState.windTemperatureModifier = tag.getDouble(ServerStateNBTKeys.windTemperatureModifier);
		serverState.precipitationWindModifier = tag.getDouble(ServerStateNBTKeys.precipitationWindModifier);

		return serverState;
	}

	public static ServerState getServerState(MinecraftServer server) {
		PersistentStateManager persistentStateManager = server
				.getWorld(World.OVERWORLD).getPersistentStateManager();

		ServerState serverState = persistentStateManager.getOrCreate(
				ServerState::createFromNbt,
				ServerState::new,
				ThermMod.modid);

		return serverState;
	}

	public static PlayerState getPlayerState(LivingEntity player) {
		ServerState serverState = getServerState(player.getWorld().getServer());

		PlayerState playerState = serverState.players.computeIfAbsent(player.getUuid(),
				uuid -> new PlayerState());

		return playerState;
	}
}