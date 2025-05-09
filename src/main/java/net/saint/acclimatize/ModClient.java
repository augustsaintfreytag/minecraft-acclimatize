package net.saint.acclimatize;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.saint.acclimatize.networking.TemperaturePackets;
import net.saint.acclimatize.util.ItemTemperatureUtil;

public class ModClient implements ClientModInitializer {

	public static double cachedAcclimatizationRate = 0;
	public static double cachedBodyTemperature = 0;
	public static double cachedTemperatureDifference = 0;

	public static double cachedAmbientTemperature = 0;
	public static double cachedWindTemperature = 0;
	public static double cachedWindDirection = 0;
	public static double cachedWindIntensity = 0;

	public static int temperatureUpdateTick = 0;

	public static boolean enableHUD = true;

	private static KeyBinding enableHUDKeyBinding;

	public static int glassShakeTick = 0;
	public static int glassShakeTickMax = 0;
	public static int glassShakePM = -1;
	public static boolean glassShakeAxis = false;

	@Override
	public void onInitializeClient() {
		setUpKeybindings();
		setUpNetworkingPacketRegistration();
		setUpClientTickEventHandler();
		setUpItemTooltipCallback();
	}

	private static void setUpKeybindings() {
		enableHUDKeyBinding = KeyBindingHelper
				.registerKeyBinding(new KeyBinding("Toggle Temperature GUI", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "Acclimatize"));

		if (enableHUDKeyBinding.wasPressed()) {
			enableHUD = !enableHUD;
		}
	}

	private static void setUpNetworkingPacketRegistration() {
		TemperaturePackets.registerS2CPackets();
	}

	private static void setUpClientTickEventHandler() {
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			var world = client.world;

			if (world == null || !world.isClient()) {
				return;
			}

			var isPaused = client.isInSingleplayer() && client.isPaused();

			// Temperature Tick

			if (++temperatureUpdateTick >= Mod.CONFIG.temperatureTickInterval) {
				if (!isPaused && !client.player.isCreative() && !client.player.isSpectator()) {
					ClientPlayNetworking.send(TemperaturePackets.PLAYER_TEMPERATURE_TICK_C2S_PACKET_ID, PacketByteBufs.create());
				}

				temperatureUpdateTick = 0;
			}

			// Wind Particles

			if (Mod.CONFIG.enableWindParticles && !isPaused) {
				renderWindParticles(client);
			}
		});
	}

	private static void setUpItemTooltipCallback() {
		ItemTooltipCallback.EVENT.register((stack, context, tooltip) -> {
			if (!stack.hasNbt()) {
				return;
			}

			var temperature = Math.round(ItemTemperatureUtil.temperatureValueForItem(stack));

			if (temperature == 0) {
				return;
			}

			tooltip.add(Text.literal("§9+" + temperature + " Temperature"));
		});
	}

	private static void renderWindParticles(MinecraftClient client) {
		var player = client.player;
		var world = client.world;
		var random = world.getRandom();

		var bound = Math.max(1, 16 + (int) cachedWindTemperature);

		if (random.nextInt(bound) == 0) {
			double windDirectionRadians = Math.toRadians(cachedWindDirection);

			Vec3d direction = new Vec3d(-Math.sin(windDirectionRadians), 0, Math.cos(windDirectionRadians));

			double x = player.getX() + random.nextTriangular(0, 10) - direction.x * 7;
			double y = player.getY() + random.nextTriangular(5, 7);
			double z = player.getZ() + random.nextTriangular(0, 10) - direction.z * 7;

			world.addParticle(ParticleTypes.CLOUD, x, y, z, direction.x, direction.y, direction.z);
		}
	}

}
