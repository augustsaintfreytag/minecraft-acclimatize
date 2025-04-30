package net.saint.acclimatize.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.player.PlayerState;
import net.saint.acclimatize.server.ServerState;

public class ServerStateUtil {

	// State Access

	public static ServerState getServerState(MinecraftServer server) {
		PersistentStateManager manager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
		return manager.getOrCreate(
				ServerState::createFromNbt,
				ServerState::new,
				Mod.modId);
	}

	public static PlayerState getPlayerState(LivingEntity player) {
		ServerState serverState = getServerState(player.getWorld().getServer());
		return serverState.players.computeIfAbsent(
				player.getUuid(),
				uuid -> new PlayerState());
	}

}
