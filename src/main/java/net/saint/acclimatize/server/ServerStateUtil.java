package net.saint.acclimatize.server;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.player.PlayerState;

public class ServerStateUtil {

	// State Access

	public static ServerState getServerState(MinecraftServer server) {
		PersistentStateManager manager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
		return manager.getOrCreate(ServerState::createFromNbt, ServerState::new, Mod.MOD_ID);
	}

	public static PlayerState getPlayerState(LivingEntity player) {
		ServerState serverState = getServerState(player.getWorld().getServer());
		return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerState());
	}

}
