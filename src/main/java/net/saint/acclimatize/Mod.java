package net.saint.acclimatize;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.saint.acclimatize.item.GoldSweetBerriesItem;
import net.saint.acclimatize.item.IceWaterItem;
import net.saint.acclimatize.item.ThermometerItem;
import net.saint.acclimatize.item.WoolClothItem;
import net.saint.acclimatize.networking.TemperaturePackets;
import net.saint.acclimatize.profiler.Profiler;
import net.saint.acclimatize.recipe.LeatherArmorWoolRecipe;
import net.saint.acclimatize.server.ServerState;
import net.saint.acclimatize.util.BlockTemperatureUtil;
import net.saint.acclimatize.util.ItemTemperatureUtil;
import net.saint.acclimatize.util.ServerStateUtil;
import net.saint.acclimatize.util.SpaceUtil;
import net.saint.acclimatize.util.WindTemperatureUtil;

public class Mod implements ModInitializer {
	public static final String modid = "acclimatize";
	public static final String modVersion = "6.0.0";

	public static final Logger LOGGER = LoggerFactory.getLogger("acclimatize");
	public static final Profiler PROFILER = Profiler.getProfiler("acclimatize");

	// Items

	public static final GoldSweetBerriesItem GOLDEN_SWEET_BERRIES_ITEM = new GoldSweetBerriesItem(
			new FabricItemSettings().maxCount(64));
	public static final IceWaterItem ICE_WATER_ITEM = new IceWaterItem(new FabricItemSettings().maxCount(16));
	public static final ThermometerItem THERMOMETER_ITEM = new ThermometerItem(new FabricItemSettings().maxCount(1));
	public static final WoolClothItem WOOL_CLOTH_ITEM = new WoolClothItem(new FabricItemSettings().maxCount(64));

	// Block Items

	public static final BlockItem ICE_BOX_EMPTY_ITEM = new BlockItem(ModBlocks.ICE_BOX_EMPTY_BLOCK,
			new FabricItemSettings());
	public static final BlockItem ICE_BOX_FREEZING_ITEM = new BlockItem(ModBlocks.ICE_BOX_FREEZING_BLOCK,
			new FabricItemSettings());
	public static final BlockItem ICE_BOX_FROZEN_ITEM = new BlockItem(ModBlocks.ICE_BOX_FROZEN_BLOCK,
			new FabricItemSettings());

	// Special Recipes

	public static final RecipeSerializer<LeatherArmorWoolRecipe> LEATHER_ARMOR_WOOL_RECIPE_SERIALIZER = RecipeSerializer
			.register("crafting_special_leather_armor_wool",
					new SpecialRecipeSerializer<LeatherArmorWoolRecipe>(LeatherArmorWoolRecipe::new));

	// Config

	public static ModConfig CONFIG;

	// Init

	@Override
	public void onInitialize() {
		// Config

		AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

		AutoConfig.getConfigHolder(ModConfig.class).registerSaveListener(
				(config, data) -> {
					ItemTemperatureUtil.reloadItems();
					BlockTemperatureUtil.reloadBlocks();
					return null;
				});

		// Reload

		ItemTemperatureUtil.reloadItems();
		BlockTemperatureUtil.reloadBlocks();

		// Status Effects

		Registry.register(Registries.STATUS_EFFECT, new Identifier(modid, "cold_resistance"),
				ModStatusEffects.COLD_RESISTANCE);
		Registry.register(Registries.STATUS_EFFECT, new Identifier(modid, "hypothermia"),
				ModStatusEffects.HYPOTHERMIA);
		Registry.register(Registries.STATUS_EFFECT, new Identifier(modid, "hyperthermia"),
				ModStatusEffects.HYPERTHERMIA);

		// Items

		Registry.register(Registries.ITEM, new Identifier(modid, "thermometer"), THERMOMETER_ITEM);
		Registry.register(Registries.ITEM, new Identifier(modid, "golden_sweet_berries"), GOLDEN_SWEET_BERRIES_ITEM);
		Registry.register(Registries.ITEM, new Identifier(modid, "ice_water"), ICE_WATER_ITEM);
		Registry.register(Registries.ITEM, new Identifier(modid, "wool_cloth"), WOOL_CLOTH_ITEM);

		// Blocks

		Registry.register(Registries.BLOCK, new Identifier(modid, "ice_box_empty"), ModBlocks.ICE_BOX_EMPTY_BLOCK);
		Registry.register(Registries.BLOCK, new Identifier(modid, "ice_box_freezing"),
				ModBlocks.ICE_BOX_FREEZING_BLOCK);
		Registry.register(Registries.BLOCK, new Identifier(modid, "ice_box_frozen"), ModBlocks.ICE_BOX_FROZEN_BLOCK);
		Registry.register(Registries.BLOCK, new Identifier(modid, "smoke"), ModBlocks.SMOKE_BLOCK);

		// Block Item Registry

		Registry.register(Registries.ITEM, new Identifier(modid, "ice_box_empty_item"), ICE_BOX_EMPTY_ITEM);
		Registry.register(Registries.ITEM, new Identifier(modid, "ice_box_freezing_item"), ICE_BOX_FREEZING_ITEM);
		Registry.register(Registries.ITEM, new Identifier(modid, "ice_box_frozen_item"), ICE_BOX_FROZEN_ITEM);

		// Item Groups

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> {
			content.add(GOLDEN_SWEET_BERRIES_ITEM);
			content.add(ICE_WATER_ITEM);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.add(THERMOMETER_ITEM);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
			content.add(ICE_BOX_EMPTY_ITEM);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
			content.add(WOOL_CLOTH_ITEM);
		});

		TemperaturePackets.registerC2SPackets();

		// Events

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			var serverState = ServerStateUtil.getServerState(server);

			if (!modVersion.equals(serverState.worldVersion)) {
				serverState.windRandomizeTick = 24000;
				serverState.windTemperatureModifierRange = 8;
				serverState.worldVersion = modVersion;

				serverState.markDirty();
			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			var player = handler.player;
			SpaceUtil.cleanUpPlayerData(player);
			WindTemperatureUtil.cleanUpPlayerData(player);
		});

		ServerTickEvents.END_SERVER_TICK.register((server) -> {
			ServerState serverState = ServerStateUtil.getServerState(server);

			if (serverState.windRandomizeTick >= 24000) {
				serverState.windRandomizeTick = 0;

				var random = server.getOverworld().getRandom();

				serverState.windPitch = 360 * Math.PI / 180;
				serverState.windYaw = random.nextDouble() * 360 * Math.PI / 180;
				serverState.windTemperature = -serverState.windTemperatureModifierRange
						+ random.nextDouble() * serverState.windTemperatureModifierRange * 2;
				serverState.precipitationWindModifier = -serverState.windTemperatureModifierRange
						+ random.nextDouble() * -serverState.windTemperatureModifierRange;

				serverState.markDirty();
			}

			serverState.windRandomizeTick += 1;

		});

		// Commands

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
				.register(literal("acclimatize:reset_temperature").requires(source -> source.hasPermissionLevel(4))
						.then(argument("player", EntityArgumentType.player())
								.executes(context -> {

									var player = EntityArgumentType.getPlayer(context, "player");
									var playerState = ServerStateUtil.getPlayerState(player);

									playerState.bodyTemperature = 50.0;
									playerState.markDirty();

									return 1;
								}))));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
				.register(literal("acclimatize:randomize_wind").requires(source -> source.hasPermissionLevel(4))
						.then(argument("player", EntityArgumentType.player())
								.executes(context -> {

									var player = EntityArgumentType.getPlayer(context, "player");
									var world = player.getWorld();
									var server = world.getServer();

									var serverState = ServerStateUtil.getServerState(server);
									var random = server.getOverworld().getRandom();

									serverState.windPitch = 360 * Math.PI / 180;
									serverState.windYaw = random.nextDouble() * 360 * Math.PI / 180;
									serverState.windTemperature = -serverState.windTemperatureModifierRange
											+ random.nextDouble() * serverState.windTemperatureModifierRange * 2;
									serverState.precipitationWindModifier = -serverState.windTemperatureModifierRange
											+ random.nextDouble() * -serverState.windTemperatureModifierRange;

									serverState.markDirty();

									context.getSource().sendMessage(Text.literal("Wind Randomized."));
									context.getSource().sendMessage(
											Text.literal("Wind Yaw: " + serverState.windYaw * 180 / Math.PI));
									context.getSource().sendMessage(
											Text.literal("Wind Temperature Modifier: "
													+ serverState.windTemperature));
									context.getSource().sendMessage(Text.literal(
											"Precipitation Modifier: " + serverState.precipitationWindModifier));

									return 1;
								}))));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
				.register(literal("acclimatize:show_wind").requires(source -> source.hasPermissionLevel(4))
						.executes(context -> {

							ServerState serverState = ServerStateUtil.getServerState(context.getSource().getServer());
							PlayerEntity player = context.getSource().getPlayer();

							Vec3d dir = new Vec3d((Math.cos(serverState.windPitch) * Math.cos(serverState.windYaw)),
									(Math.sin(serverState.windPitch) * Math.cos(serverState.windYaw)),
									Math.sin(serverState.windYaw));

							player.getWorld().addParticle(ParticleTypes.CLOUD, player.getX(), player.getY() + 1,
									player.getZ(), dir.x * 4, dir.y * 4, dir.z * 4);

							return 1;
						})));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
				.register(literal("acclimatize:log_wind_info").requires(source -> source.hasPermissionLevel(4))
						.executes(context -> {

							ServerState serverState = ServerStateUtil.getServerState(context.getSource().getServer());

							context.getSource().sendMessage(Text.literal("§e=====Wind Info====="));
							context.getSource()
									.sendMessage(Text.literal("§eWind Yaw: §6" + serverState.windYaw * 180 / Math.PI));
							context.getSource().sendMessage(
									Text.literal(
											"§eWind Temperature Modifier: §6" + serverState.windTemperature));
							context.getSource().sendMessage(Text
									.literal("§ePrecipitation Modifier: §6" + serverState.precipitationWindModifier));
							context.getSource().sendMessage(
									Text.literal("§eNext Randomize: §a" + serverState.windRandomizeTick + "§7/24000"));

							return 1;
						})));

	}
}