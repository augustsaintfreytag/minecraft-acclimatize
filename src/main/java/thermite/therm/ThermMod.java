package thermite.therm;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.lortseam.completeconfig.data.ConfigOptions;
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
import thermite.therm.block.ThermBlocks;
import thermite.therm.effect.ThermStatusEffects;
import thermite.therm.item.GoldSweetBerriesItem;
import thermite.therm.item.IceWaterItem;
import thermite.therm.item.TesterItem;
import thermite.therm.item.ThermometerItem;
import thermite.therm.item.WoolClothItem;
import thermite.therm.networking.ThermNetworkingPackets;
import thermite.therm.player.PlayerState;
import thermite.therm.recipe.LeatherArmorWoolRecipe;
import thermite.therm.server.ServerState;

public class ThermMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("therm");
	public static final String modid = "therm";
	public static final String modVersion = "6.0.0";

	// Items

	public static final GoldSweetBerriesItem GOLDEN_SWEET_BERRIES_ITEM = new GoldSweetBerriesItem(
			new FabricItemSettings().maxCount(64));
	public static final IceWaterItem ICE_WATER_ITEM = new IceWaterItem(new FabricItemSettings().maxCount(16));
	public static final ThermometerItem THERMOMETER_ITEM = new ThermometerItem(new FabricItemSettings().maxCount(1));
	public static final WoolClothItem WOOL_CLOTH_ITEM = new WoolClothItem(new FabricItemSettings().maxCount(64));
	public static final TesterItem TESTER_ITEM = new TesterItem(new FabricItemSettings().maxCount(1));

	// Block Items

	public static final BlockItem ICE_BOX_EMPTY_ITEM = new BlockItem(ThermBlocks.ICE_BOX_EMPTY_BLOCK,
			new FabricItemSettings());
	public static final BlockItem ICE_BOX_FREEZING_ITEM = new BlockItem(ThermBlocks.ICE_BOX_FREEZING_BLOCK,
			new FabricItemSettings());
	public static final BlockItem ICE_BOX_FROZEN_ITEM = new BlockItem(ThermBlocks.ICE_BOX_FROZEN_BLOCK,
			new FabricItemSettings());

	// Special Recipes

	public static final RecipeSerializer<LeatherArmorWoolRecipe> LEATHER_ARMOR_WOOL_RECIPE_SERIALIZER = RecipeSerializer
			.register("crafting_special_leather_armor_wool",
					new SpecialRecipeSerializer<LeatherArmorWoolRecipe>(LeatherArmorWoolRecipe::new));

	// Config

	public static final ThermConfig config = new ThermConfig();

	// Init

	@Override
	public void onInitialize() {

		config.load();
		ConfigOptions.mod(modid).branch(new String[] { "branch", "config" });

		// Status Effects

		Registry.register(Registries.STATUS_EFFECT, new Identifier(modid, "cooling"),
				ThermStatusEffects.COLD_RESISTANCE);

		// Items

		Registry.register(Registries.ITEM, new Identifier(modid, "golden_sweet_berries"), GOLDEN_SWEET_BERRIES_ITEM);
		Registry.register(Registries.ITEM, new Identifier(modid, "ice_water"), ICE_WATER_ITEM);
		Registry.register(Registries.ITEM, new Identifier(modid, "thermometer"), THERMOMETER_ITEM);
		Registry.register(Registries.ITEM, new Identifier(modid, "wool_cloth"), WOOL_CLOTH_ITEM);
		Registry.register(Registries.ITEM, new Identifier(modid, "tester_item"), TESTER_ITEM);

		// Blocks

		Registry.register(Registries.BLOCK, new Identifier(modid, "ice_box_empty"), ThermBlocks.ICE_BOX_EMPTY_BLOCK);
		Registry.register(Registries.BLOCK, new Identifier(modid, "ice_box_freezing"),
				ThermBlocks.ICE_BOX_FREEZING_BLOCK);
		Registry.register(Registries.BLOCK, new Identifier(modid, "ice_box_frozen"), ThermBlocks.ICE_BOX_FROZEN_BLOCK);
		Registry.register(Registries.BLOCK, new Identifier(modid, "smoke"), ThermBlocks.SMOKE_BLOCK);

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

		ThermNetworkingPackets.registerC2SPackets();

		// Events

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {

			ServerState serverState = ServerState.getServerState(handler.player.getWorld().getServer());

			if (!Objects.equals(serverState.worldVersion, modVersion)) {
				serverState.windRandomizeTick = 24000;
				serverState.windTemperatureModifierRange = 8;
				serverState.worldVersion = modVersion;

				serverState.players.forEach((uuid, state) -> {
					state.windTurbulence = 23;
				});

				serverState.markDirty();
			}

		});

		ServerTickEvents.END_SERVER_TICK.register((server) -> {
			ServerState serverState = ServerState.getServerState(server);

			if (serverState.windRandomizeTick >= 24000) {
				serverState.windRandomizeTick = 0;

				var random = server.getOverworld().getRandom();

				serverState.windPitch = 360 * Math.PI / 180;
				serverState.windYaw = random.nextDouble() * 360 * Math.PI / 180;
				serverState.windTemperatureModifier = -serverState.windTemperatureModifierRange
						+ random.nextDouble() * serverState.windTemperatureModifierRange * 2;
				serverState.precipitationWindModifier = -serverState.windTemperatureModifierRange
						+ random.nextDouble() * -serverState.windTemperatureModifierRange;

				serverState.markDirty();
			}

			serverState.windRandomizeTick += 1;

		});

		// Commands

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
				.register(literal("thermite_resetPlayerState").requires(source -> source.hasPermissionLevel(4))
						.then(argument("player", EntityArgumentType.player())
								.executes(context -> {

									ServerState serverState = ServerState.getServerState(
											EntityArgumentType.getPlayer(context, "player").getWorld().getServer());
									PlayerState playerState = ServerState
											.getPlayerState(EntityArgumentType.getPlayer(context, "player"));

									playerState.bodyTemperature = 50;
									playerState.temperatureRate = 0.0625;
									playerState.ambientTemperature = 404;
									playerState.ambientMinTemperature = -400;
									playerState.ambientMaxTemperature = 400;
									playerState.damageType = "";
									playerState.damageTick = 0;
									playerState.maxDamageTick = 10;
									playerState.searchFireplaceTick = 4;
									serverState.markDirty();

									context.getSource().sendMessage(Text.literal("Reset "
											+ EntityArgumentType.getPlayer(context, "player").getName().getString()
											+ "'s playerState."));

									return 1;
								}))));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
				.register(literal("windRandomize").requires(source -> source.hasPermissionLevel(4))
						.then(argument("player", EntityArgumentType.player())
								.executes(context -> {

									var player = EntityArgumentType.getPlayer(context, "player");
									var world = player.getWorld();
									var server = world.getServer();

									var serverState = ServerState.getServerState(server);
									var random = server.getOverworld().getRandom();

									serverState.windPitch = 360 * Math.PI / 180;
									serverState.windYaw = random.nextDouble() * 360 * Math.PI / 180;
									serverState.windTemperatureModifier = -serverState.windTemperatureModifierRange
											+ random.nextDouble() * serverState.windTemperatureModifierRange * 2;
									serverState.precipitationWindModifier = -serverState.windTemperatureModifierRange
											+ random.nextDouble() * -serverState.windTemperatureModifierRange;

									serverState.markDirty();

									context.getSource().sendMessage(Text.literal("Wind Randomized."));
									context.getSource().sendMessage(
											Text.literal("Wind Yaw: " + serverState.windYaw * 180 / Math.PI));
									context.getSource().sendMessage(
											Text.literal("Wind Temperature Modifier: "
													+ serverState.windTemperatureModifier));
									context.getSource().sendMessage(Text.literal(
											"Precipitation Modifier: " + serverState.precipitationWindModifier));

									return 1;
								}))));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
				.register(literal("showWind").requires(source -> source.hasPermissionLevel(4))
						.executes(context -> {

							ServerState serverState = ServerState.getServerState(context.getSource().getServer());
							PlayerEntity player = context.getSource().getPlayer();

							Vec3d dir = new Vec3d((Math.cos(serverState.windPitch) * Math.cos(serverState.windYaw)),
									(Math.sin(serverState.windPitch) * Math.cos(serverState.windYaw)),
									Math.sin(serverState.windYaw));

							player.getWorld().addParticle(ParticleTypes.CLOUD, player.getX(), player.getY() + 1,
									player.getZ(), dir.x * 4, dir.y * 4, dir.z * 4);

							return 1;
						})));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
				.register(literal("windInfo").requires(source -> source.hasPermissionLevel(4))
						.executes(context -> {

							ServerState serverState = ServerState.getServerState(context.getSource().getServer());

							context.getSource().sendMessage(Text.literal("§e=====Wind Info====="));
							context.getSource()
									.sendMessage(Text.literal("§eWind Yaw: §6" + serverState.windYaw * 180 / Math.PI));
							context.getSource().sendMessage(
									Text.literal(
											"§eWind Temperature Modifier: §6" + serverState.windTemperatureModifier));
							context.getSource().sendMessage(Text
									.literal("§ePrecipitation Modifier: §6" + serverState.precipitationWindModifier));
							context.getSource().sendMessage(
									Text.literal("§eNext Randomize: §a" + serverState.windRandomizeTick + "§7/24000"));

							return 1;
						})));

	}
}