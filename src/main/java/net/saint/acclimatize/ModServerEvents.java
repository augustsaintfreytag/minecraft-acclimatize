package net.saint.acclimatize;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.saint.acclimatize.player.PlayerState;
import net.saint.acclimatize.server.ServerState;
import net.saint.acclimatize.util.PlayerEffectsUtil;
import net.saint.acclimatize.util.PlayerTemperatureUtil;
import net.saint.acclimatize.util.ServerStateUtil;
import net.saint.acclimatize.util.SpaceUtil;
import net.saint.acclimatize.util.WindTemperatureUtil;
import net.saint.acclimatize.util.WindUtil;

public final class ModServerEvents {

	public static void registerServerEvents() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			var serverState = ServerStateUtil.getServerState(server);

			if (!Mod.modVersion.equals(serverState.worldVersion)) {
				serverState.worldVersion = Mod.modVersion;
				serverState.markDirty();
			}

			var player = handler.player;
			var playerState = ServerStateUtil.getPlayerState(player);

			if (playerState.bodyTemperature == 0.0) {
				playerState.bodyTemperature = 50.0;
				playerState.markDirty();
			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			var player = handler.player;

			SpaceUtil.cleanUpPlayerData(player);
			WindTemperatureUtil.cleanUpPlayerData(player);
		});

		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			var serverState = ServerStateUtil.getServerState(server);
			var serverWorld = server.getOverworld();

			Mod.LOGGER.info("Randomizing new wind direction and intensity at server start.");
			WindUtil.tickWindInSchedule(serverWorld, serverState);
		});

		ServerTickEvents.END_SERVER_TICK.register((server) -> {
			tickServerInSchedule(server);
			tickAllPlayersInSchedule(server);
		});
	}

	private static void tickServerInSchedule(MinecraftServer server) {
		var serverState = ServerStateUtil.getServerState(server);
		var serverWorld = server.getOverworld();

		WindUtil.tickWindInSchedule(serverWorld, serverState);
	}

	private static void tickAllPlayersInSchedule(MinecraftServer server) {
		var serverState = ServerStateUtil.getServerState(server);

		for (var player : server.getPlayerManager().getPlayerList()) {
			if (!(player instanceof ServerPlayerEntity)) {
				continue;
			}

			var playerState = ServerStateUtil.getPlayerState(player);
			tickPlayerInSchedule(serverState, playerState, (ServerPlayerEntity) player);
		}
	}

	private static void tickPlayerInSchedule(ServerState serverState, PlayerState playerState, ServerPlayerEntity player) {
		if (player.isCreative() || player.isSpectator()) {
			return;
		}

		// Temperature
		PlayerTemperatureUtil.tickPlayerTemperatureInSchedule(player, serverState, playerState);

		// Damage
		PlayerEffectsUtil.tickPlayerEffectsInSchedule(player, playerState);
	}

}
