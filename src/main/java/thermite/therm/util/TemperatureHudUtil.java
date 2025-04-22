package thermite.therm.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import thermite.therm.ThermClient;
import thermite.therm.ThermMod;

public final class TemperatureHudUtil {

	private static final Identifier THERMOMETER_FRAME = new Identifier(ThermMod.modid,
			"textures/thermometer/thermometer_frame.png");
	private static final Identifier THERMOMETER_GAUGE = new Identifier(ThermMod.modid,
			"textures/thermometer/thermometer_gauge_fix_1.png");
	private static final Identifier THERMOMETER_HAND = new Identifier(ThermMod.modid,
			"textures/thermometer/thermometer_hand.png");
	private static final Identifier THERMOMETER_SNOWFLAKE = new Identifier(ThermMod.modid,
			"textures/thermometer/snowflake_icon_8x8.png");
	private static final Identifier THERMOMETER_FLAME = new Identifier(ThermMod.modid,
			"textures/thermometer/flame_icon_8x8.png");
	private static final Identifier THERMOMETER_STILL = new Identifier(ThermMod.modid,
			"textures/thermometer/temperate_icon.png");

	private static final Identifier THERMOMETER_DISPLAY = new Identifier(ThermMod.modid,
			"textures/thermometer/thermometer_display.png");

	// glass thermometer
	private static final Identifier TEMPERATE_GLASS = new Identifier(ThermMod.modid,
			"textures/glass_thermometer/temperate_glass.png");
	private static final Identifier COLD_GLASS = new Identifier(ThermMod.modid,
			"textures/glass_thermometer/cold_glass.png");
	private static final Identifier FROZEN_GLASS = new Identifier(ThermMod.modid,
			"textures/glass_thermometer/frozen_glass.png");
	private static final Identifier HOT_GLASS = new Identifier(ThermMod.modid,
			"textures/glass_thermometer/hot_glass.png");
	private static final Identifier BLAZING_GLASS = new Identifier(ThermMod.modid,
			"textures/glass_thermometer/blazing_glass.png");

	private static final Identifier COOLING_OUTLINE = new Identifier(ThermMod.modid,
			"textures/glass_thermometer/cooling_outline.png");
	private static final Identifier COOLING_OUTLINE_SMALL = new Identifier(ThermMod.modid,
			"textures/glass_thermometer/cooling_small_outline.png");
	private static final Identifier HEATING_OUTLINE = new Identifier(ThermMod.modid,
			"textures/glass_thermometer/heating_outline.png");
	private static final Identifier HEATING_OUTLINE_SMALL = new Identifier(ThermMod.modid,
			"textures/glass_thermometer/heating_small_outline.png");

	public static void renderGaugeThermometerHud(DrawContext context) {
		int x = 0;
		int y = 0;

		ItemStack offHand = ItemStack.EMPTY;
		MinecraftClient client = MinecraftClient.getInstance();

		if (client != null) {
			x = (client.getWindow().getScaledWidth() / 2) + ThermMod.CONFIG.temperatureXOffset;
			y = client.getWindow().getScaledHeight() + ThermMod.CONFIG.temperatureYOffset;
			assert client.player != null;
			offHand = client.player.getOffHandStack();
		}

		float pixelMultiplier = 1.5f;
		float tempFract = ((ThermClient.clientStoredTemperature / 100f) * Math.round(40 * pixelMultiplier));

		if (((ThermClient.clientStoredTemperature / 100f) * Math.round(40 * pixelMultiplier)) > 59.0f) {
			tempFract = ((97 / 100f) * Math.round(40 * pixelMultiplier));
		} else if ((ThermClient.clientStoredTemperature / 100f) < 0) {
			tempFract = 0f;
		}

		context.drawTexture(THERMOMETER_GAUGE, x - ((44 + 149) - Math.round(2 * pixelMultiplier)),
				y - (Math.round(8 * pixelMultiplier) + Math.round(3 * pixelMultiplier) + 1), 0, 0,
				Math.round(40 * pixelMultiplier), Math.round(9 * pixelMultiplier),
				Math.round(40 * pixelMultiplier), Math.round(9 * pixelMultiplier));

		context.drawTexture(THERMOMETER_HAND,
				x - (int) (((44 + 149) - Math.round(2 * pixelMultiplier)) - tempFract),
				y - (Math.round(8 * pixelMultiplier) + Math.round(3 * pixelMultiplier) + 1), 0, 0,
				Math.round(1), Math.round(9 * pixelMultiplier), Math.round(1), Math.round(9 * pixelMultiplier));

		int frameY = y - (Math.round(13 * pixelMultiplier) + 1);

		context.drawTexture(THERMOMETER_FRAME, x - (44 + 149), frameY, 0, 0,
				Math.round(44 * pixelMultiplier), Math.round(13 * pixelMultiplier),
				Math.round(44 * pixelMultiplier), Math.round(13 * pixelMultiplier));

		if (ThermClient.clientStoredTemperatureDifference > 0) {
			context.drawTexture(THERMOMETER_FLAME, x - (17 + 149), y - (Math.round(22 * pixelMultiplier)),
					0, 0, Math.round(8 * pixelMultiplier), Math.round(8 * pixelMultiplier),
					Math.round(8 * pixelMultiplier), Math.round(8 * pixelMultiplier));
		} else if (ThermClient.clientStoredTemperatureDifference < 0) {
			context.drawTexture(THERMOMETER_SNOWFLAKE, x - (17 + 149),
					y - (Math.round(22 * pixelMultiplier)), 0, 0, Math.round(8 * pixelMultiplier),
					Math.round(8 * pixelMultiplier), Math.round(8 * pixelMultiplier),
					Math.round(8 * pixelMultiplier));
		} else {
			context.drawTexture(THERMOMETER_STILL, x - (17 + 149), y - (Math.round(22 * pixelMultiplier)),
					0, 0, Math.round(8 * pixelMultiplier), Math.round(8 * pixelMultiplier),
					Math.round(8 * pixelMultiplier), Math.round(8 * pixelMultiplier));
		}

		if (offHand.getItem() == ThermMod.THERMOMETER_ITEM) {
			context.drawTexture(THERMOMETER_DISPLAY, (x - (x - 16)) + ThermMod.CONFIG.thermometerXOffset,
					frameY + ThermMod.CONFIG.thermometerYOffset, 0, 0, Math.round(16 * pixelMultiplier),
					Math.round(13 * pixelMultiplier), Math.round(16 * pixelMultiplier),
					Math.round(13 * pixelMultiplier));
			assert client != null;
			context.drawText(client.textRenderer, "§7" + ThermClient.clientStoredTemperature,
					((x - (x - 16)) + 6) + ThermMod.CONFIG.thermometerXOffset,
					(frameY + 7) + ThermMod.CONFIG.thermometerYOffset, 16777215, true);
		}
	}

	public static void renderGlassThermometerHud(DrawContext context) {
		int tx = 0;
		int ty = 0;

		float pixelMultiplier = 1.5f;
		ItemStack offHand = ItemStack.EMPTY;
		MinecraftClient client = MinecraftClient.getInstance();

		int x = 0;
		int y = 0;

		if (client != null) {
			tx = (client.getWindow().getScaledWidth() / 2) + ThermMod.CONFIG.temperatureXOffset;
			ty = client.getWindow().getScaledHeight() + ThermMod.CONFIG.temperatureYOffset;

			x = (client.getWindow().getScaledWidth() / 2) + ThermMod.CONFIG.temperatureXOffset;
			y = (client.getWindow().getScaledHeight() - 48) + ThermMod.CONFIG.temperatureYOffset;

			assert client.player != null;
			offHand = client.player.getOffHandStack();
		}

		int tFrameY = ty - (Math.round(13 * pixelMultiplier) + 1);
		int temp = (int) ThermClient.clientStoredTemperature;

		assert client != null;

		if (!client.player.isSpectator() && !client.player.isCreative()) {
			if (temp < ThermMod.CONFIG.freezeThresholdMinor + 1
					&& temp > ThermMod.CONFIG.freezeThresholdMajor) {
				ThermClient.glassShakeTickMax = 4;
				ThermClient.glassShakeAxis = true;
			} else if (temp < ThermMod.CONFIG.freezeThresholdMajor + 1) {
				ThermClient.glassShakeTickMax = 3;
				ThermClient.glassShakeAxis = true;
			} else if (temp > ThermMod.CONFIG.burnThresholdMinor - 1
					&& temp < ThermMod.CONFIG.burnThresholdMajor) {
				ThermClient.glassShakeTickMax = 4;
				ThermClient.glassShakeAxis = false;
			} else if (temp > ThermMod.CONFIG.burnThresholdMajor - 1) {
				ThermClient.glassShakeTickMax = 3;
				ThermClient.glassShakeAxis = false;
			} else {
				ThermClient.glassShakeTickMax = 0;
			}

			if (ThermClient.glassShakeTickMax != 0) {
				ThermClient.glassShakeTick += 1;
				if (ThermClient.glassShakeTick >= ThermClient.glassShakeTickMax) {
					ThermClient.glassShakeTick = 0;
					if (ThermClient.glassShakePM == 1) {
						ThermClient.glassShakePM = -1;
					} else if (ThermClient.glassShakePM == -1) {
						ThermClient.glassShakePM = 1;
					}
				}
				if (ThermClient.glassShakeAxis) {
					x += ThermClient.glassShakePM;
				} else {
					y += ThermClient.glassShakePM;
				}
			}

			if (temp < ThermMod.CONFIG.burnThresholdMinor - 10
					&& temp > ThermMod.CONFIG.freezeThresholdMinor + 10) {
				context.drawTexture(TEMPERATE_GLASS, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			} else if (temp < ThermMod.CONFIG.freezeThresholdMinor + 11
					&& temp > ThermMod.CONFIG.freezeThresholdMinor + 5) {
				context.drawTexture(COLD_GLASS, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			} else if (temp < ThermMod.CONFIG.freezeThresholdMinor + 6) {
				context.drawTexture(FROZEN_GLASS, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			} else if (temp > ThermMod.CONFIG.burnThresholdMinor - 11
					&& temp < ThermMod.CONFIG.burnThresholdMinor - 5) {
				context.drawTexture(HOT_GLASS, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			} else if (temp > ThermMod.CONFIG.burnThresholdMinor - 6) {
				context.drawTexture(BLAZING_GLASS, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			}

			if (ThermClient.clientStoredTemperatureDifference < 0
					&& ThermClient.clientStoredTemperatureDifference > -10) {
				context.drawTexture(COOLING_OUTLINE_SMALL, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			} else if (ThermClient.clientStoredTemperatureDifference < -9) {
				context.drawTexture(COOLING_OUTLINE, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			} else if (ThermClient.clientStoredTemperatureDifference > 0
					&& ThermClient.clientStoredTemperatureDifference < 10) {
				context.drawTexture(HEATING_OUTLINE_SMALL, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			} else if (ThermClient.clientStoredTemperatureDifference > 9) {
				context.drawTexture(HEATING_OUTLINE, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			}
		}

		if (offHand.isOf(ThermMod.THERMOMETER_ITEM)) {
			context.drawTexture(THERMOMETER_DISPLAY, (tx - (tx - 16)) + ThermMod.CONFIG.thermometerXOffset,
					tFrameY + ThermMod.CONFIG.thermometerYOffset, 0, 0, Math.round(16 * pixelMultiplier),
					Math.round(13 * pixelMultiplier), Math.round(16 * pixelMultiplier),
					Math.round(13 * pixelMultiplier));
			assert client != null;
			context.drawText(client.textRenderer, "§7" + ThermClient.clientStoredTemperature,
					((tx - (tx - 16)) + 6) + ThermMod.CONFIG.thermometerXOffset,
					(tFrameY + 7) + ThermMod.CONFIG.thermometerYOffset, 16777215, true);
		}
	}

}
