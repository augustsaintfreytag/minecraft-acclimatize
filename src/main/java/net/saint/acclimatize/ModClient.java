package net.saint.acclimatize;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.saint.acclimatize.networking.TemperaturePackets;
import net.saint.acclimatize.util.ItemTemperatureUtil;
import net.saint.acclimatize.util.WindParticleUtil;

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


	// Init

	@Override
	public void onInitializeClient() {
		setUpKeybindings();
		setUpNetworkingPacketRegistration();
		setUpClientTickEventHandler();
		setUpItemTooltipCallback();
	}

	// Set-Up

	private static void setUpKeybindings() {
		enableHUDKeyBinding = KeyBindingHelper
				.registerKeyBinding(new KeyBinding("Toggle Temperature GUI", InputUtil.Type.KEYSYM,
						GLFW.GLFW_KEY_UNKNOWN, "Acclimatize"));

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
				if (!isPaused) {
					ClientPlayNetworking.send(
							TemperaturePackets.PLAYER_TEMPERATURE_TICK_C2S_PACKET_ID,
							PacketByteBufs.create());
				}

				temperatureUpdateTick = 0;
			}

			// Wind Particles

			if (Mod.CONFIG.enableWindParticles && !isPaused) {
				WindParticleUtil.renderWindParticles(client);
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

}
