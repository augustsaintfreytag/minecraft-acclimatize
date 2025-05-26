package net.saint.acclimatize;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.text.Text;
import net.saint.acclimatize.data.wind.WindUtil;
import net.saint.acclimatize.server.ServerState;
import net.saint.acclimatize.server.ServerStateUtil;

public final class ModCommands {

	public static void registerCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
				.register(literal("acclimatize:reset_temperature").requires(source -> source.hasPermissionLevel(4))
						.then(argument("player", EntityArgumentType.player()).executes(context -> {
							var player = EntityArgumentType.getPlayer(context, "player");
							var playerState = ServerStateUtil.getPlayerState(player);

							playerState.bodyTemperature = 50.0;
							playerState.markDirty();

							return 1;
						}))));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
				.register(literal("acclimatize:randomize_wind").requires(source -> source.hasPermissionLevel(4)).executes(context -> {
					var server = context.getSource().getServer();
					var serverWorld = server.getOverworld();

					var serverState = ServerStateUtil.getServerState(server);
					WindUtil.tickWindDirectionAndIntensity(serverWorld, serverState);

					context.getSource().sendMessage(Text.literal("Wind randomized."));
					context.getSource().sendMessage(Text.literal("Wind Direction: " + Math.toDegrees(serverState.windDirection) + "°"));
					context.getSource().sendMessage(Text.literal("Wind Intensity: " + serverState.windIntensity));

					return 1;
				})));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
				.register(literal("acclimatize:force_north_wind").requires(source -> source.hasPermissionLevel(4)).executes(context -> {
					var server = context.getSource().getServer();
					var serverState = ServerStateUtil.getServerState(server);

					WindUtil.overrideWind(serverState, Math.toRadians(0.5), 5.0);

					context.getSource().sendMessage(Text.literal("Wind set to straight north."));
					context.getSource().sendMessage(Text.literal("Wind Direction: " + Math.toDegrees(serverState.windDirection) + "°"));
					context.getSource().sendMessage(Text.literal("Wind Intensity: " + serverState.windIntensity));

					return 1;
				})));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
				.register(literal("acclimatize:log_wind_info").requires(source -> source.hasPermissionLevel(4)).executes(context -> {

					ServerState serverState = ServerStateUtil.getServerState(context.getSource().getServer());

					context.getSource().sendMessage(Text.literal("§e=====Wind Info====="));
					context.getSource().sendMessage(Text.literal("§eWind Direction: §6" + Math.toDegrees(serverState.windDirection) + "°"));
					context.getSource().sendMessage(Text.literal("§eWind Temperature Modifier: §6" + serverState.windIntensity));

					return 1;
				})));
	}

}
