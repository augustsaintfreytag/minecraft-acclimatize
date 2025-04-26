package thermite.therm;

import java.util.Random;

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
import thermite.therm.networking.ThermNetworkingPackets;
import thermite.therm.util.ItemTemperatureUtil;

public class ThermClient implements ClientModInitializer {

	public static long clientStoredTemperature = 0;
	public static short clientStoredTemperatureDifference = 0;
	public static double clientStoredWindPitch = 0;
	public static double clientStoredWindYaw = 0;
	public static double clientStoredWindTemperature = 0;

	public static int temperatureUpdateTick = 0;
	public static int temperatureUpdateTickInterval = 20;

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
		enableHUDKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Toggle Temperature GUI",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				"Thermite"));

		if (enableHUDKeyBinding.wasPressed()) {
			enableHUD = !enableHUD;
		}
	}

	private static void setUpNetworkingPacketRegistration() {
		ThermNetworkingPackets.registerS2CPackets();
	}

	private static void setUpClientTickEventHandler() {
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			var world = client.world;

			if (world == null || !world.isClient()) {
				return;
			}

			var isPaused = client.isInSingleplayer() && client.isPaused();

			// Temperature Tick

			if (++temperatureUpdateTick >= temperatureUpdateTickInterval) {
				if (!isPaused && !client.player.isCreative() && !client.player.isSpectator()) {
					ClientPlayNetworking.send(
							ThermNetworkingPackets.PLAYER_TEMPERATURE_TICK_C2S_PACKET_ID,
							PacketByteBufs.create());
				}

				temperatureUpdateTick = 0;
			}

			// Wind Particles

			if (ThermMod.CONFIG.enableWindParticles && !isPaused) {
				renderWindParticles(client);
			}
		});
	}

	private static void setUpItemTooltipCallback() {
		ItemTooltipCallback.EVENT.register((stack, context, tooltip) -> {
			if (!stack.hasNbt()) {
				return;
			}

			var temperature = ItemTemperatureUtil.temperatureValueForItem(stack);

			if (temperature == 0) {
				return;
			}

			tooltip.add(Text.literal("§9+" + temperature + " Temperature"));
		});
	}

	private static void renderWindParticles(MinecraftClient client) {
		if (clientStoredWindTemperature >= -3) {
			return;
		}

		var player = client.player;
		var world = client.world;

		var random = new Random();
		var bound = Math.max(1, 16 + (int) clientStoredWindTemperature);

		if (random.nextInt(bound) == 0) {
			Vec3d dir = new Vec3d(
					Math.cos(clientStoredWindPitch) * Math.cos(clientStoredWindYaw),
					Math.sin(clientStoredWindPitch) * Math.cos(clientStoredWindYaw),
					Math.sin(clientStoredWindYaw)).negate();

			double x = player.getX() + random.nextDouble(-10, 10) - dir.x * 7;
			double y = player.getY() + random.nextDouble(-5, 10);
			double z = player.getZ() + random.nextDouble(-10, 10) - dir.z * 7;

			world.addParticle(ParticleTypes.CLOUD, x, y, z, dir.x, dir.y, dir.z);
		}
	}

}
