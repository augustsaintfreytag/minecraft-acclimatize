package net.saint.acclimatize;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.saint.acclimatize.util.ServerStateUtil;
import net.saint.acclimatize.util.SpaceUtil;
import net.saint.acclimatize.util.WindTemperatureUtil;

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

			if (FabricLoader.getInstance().isModLoaded("immersivewinds")) {
				Mod.LOGGER.info("Assigning deferred wind direction and intensity from loaded Immersive Winds.");
				return;
			}

			Mod.LOGGER.info("Randomizing new wind direction and intensity at server start.");
			WindTemperatureUtil.tickWind(serverWorld, serverState);
		});

		ServerTickEvents.END_SERVER_TICK.register((server) -> {
			var serverState = ServerStateUtil.getServerState(server);
			var serverWorld = server.getOverworld();

			WindTemperatureUtil.tickWindIfNeeded(serverWorld, serverState);
		});
	}

}
