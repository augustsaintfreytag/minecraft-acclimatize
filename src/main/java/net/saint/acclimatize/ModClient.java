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
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.saint.acclimatize.networking.TemperaturePackets;
import net.saint.acclimatize.networking.TemperaturePackets.TemperaturePacketTuple;
import net.saint.acclimatize.util.ItemTemperatureUtil;
import net.saint.acclimatize.util.MathUtil;
import net.saint.acclimatize.util.WindParticleUtil;

public class ModClient implements ClientModInitializer {

	// State

	private static TemperaturePacketTuple cachedTemperatureValues = new TemperaturePacketTuple();

	private static long lastWindUpdateTick = 0;

	private static double lastWindIntensity = 0;
	private static double lastWindDirection = 0;

	// References

	private static KeyBinding enableHUDKeyBinding;

	// Properties

	public static boolean enableHUD = true;

	private static ClientWorld getWorld() {
		return MinecraftClient.getInstance().world;
	}

	// Init

	@Override
	public void onInitializeClient() {
		setUpKeybindings();
		setUpNetworkingPacketRegistration();
		setUpClientTickEventHandler();
		setUpItemTooltipCallback();
	}

	// Access

	public static void updateTemperatureValues(TemperaturePacketTuple values) {
		var world = MinecraftClient.getInstance().world;
		var serverTick = world.getTime();
		var previousValues = cachedTemperatureValues;
		cachedTemperatureValues = values;

		if (previousValues.windDirection != values.windDirection) {
			lastWindDirection = previousValues.windDirection;
			lastWindUpdateTick = serverTick;
		}

		if (previousValues.windIntensity != values.windIntensity) {
			lastWindIntensity = previousValues.windIntensity;
			lastWindUpdateTick = serverTick;
		}
	}

	public static double getAcclimatizationRate() {
		return cachedTemperatureValues.acclimatizationRate;
	}

	public static double getBodyTemperature() {
		return cachedTemperatureValues.bodyTemperature;
	}

	public static double getAmbientTemperature() {
		return cachedTemperatureValues.ambientTemperature;
	}

	public static double getWindTemperature() {
		return cachedTemperatureValues.windTemperature;
	}

	public static double getWindDirection() {
		return MathUtil.lerp(lastWindDirection, cachedTemperatureValues.windDirection, windInterpolationValue());
	}

	public static double getWindIntensity() {
		return MathUtil.lerp(lastWindIntensity, cachedTemperatureValues.windIntensity, windInterpolationValue())
				* windPrecipitationFactor();
	}

	private static double windInterpolationValue() {
		var serverTick = getWorld().getTime();
		var deltaTime = serverTick - lastWindUpdateTick;
		var transitionFactor = (double) deltaTime / (double) Mod.CONFIG.windTransitionInterval;

		return transitionFactor;
	}

	private static double windPrecipitationFactor() {
		var world = getWorld();

		if (world.isThundering()) {
			return 5.0;
		} else if (world.isRaining()) {
			return 3.0;
		}

		return 1.0;
	}

	// Set-Up

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

			if (world.getTime() % Mod.CONFIG.temperatureTickInterval == 0) {
				if (!isPaused) {
					ClientPlayNetworking.send(TemperaturePackets.PLAYER_C2S_PACKET_ID, PacketByteBufs.create());
				}
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
