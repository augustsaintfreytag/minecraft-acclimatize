package thermite.therm.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
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
		MinecraftClient client = MinecraftClient.getInstance();

		float temperature = ThermClient.clientStoredTemperature;
		float temperatureDifference = ThermClient.clientStoredTemperatureDifference;

		if (temperature == 0.0) {
			return;
		}

		int xOffset = ThermMod.CONFIG.temperatureXOffset;
		int yOffset = ThermMod.CONFIG.temperatureYOffset;

		int x = (client.getWindow().getScaledWidth() / 2) + xOffset;
		int y = client.getWindow().getScaledHeight() + yOffset;

		float spacingFactor = 1.5f;
		float temperatureFraction = ((temperature / 100f) * Math.round(40 * spacingFactor));

		if (((temperature / 100f) * Math.round(40 * spacingFactor)) > 59.0f) {
			temperatureFraction = ((97 / 100f) * Math.round(40 * spacingFactor));
		} else if ((temperature / 100f) < 0) {
			temperatureFraction = 0f;
		}

		context.drawTexture(THERMOMETER_GAUGE, x - ((44 + 149) - Math.round(2 * spacingFactor)),
				y - (Math.round(8 * spacingFactor) + Math.round(3 * spacingFactor) + 1), 0, 0,
				Math.round(40 * spacingFactor), Math.round(9 * spacingFactor),
				Math.round(40 * spacingFactor), Math.round(9 * spacingFactor));

		context.drawTexture(THERMOMETER_HAND,
				x - (int) (((44 + 149) - Math.round(2 * spacingFactor)) - temperatureFraction),
				y - (Math.round(8 * spacingFactor) + Math.round(3 * spacingFactor) + 1), 0, 0,
				Math.round(1), Math.round(9 * spacingFactor), Math.round(1), Math.round(9 * spacingFactor));

		int frameY = y - (Math.round(13 * spacingFactor) + 1);

		context.drawTexture(THERMOMETER_FRAME, x - (44 + 149), frameY, 0, 0,
				Math.round(44 * spacingFactor), Math.round(13 * spacingFactor),
				Math.round(44 * spacingFactor), Math.round(13 * spacingFactor));

		if (temperatureDifference > 0) {
			context.drawTexture(THERMOMETER_FLAME, x - (17 + 149), y - (Math.round(22 * spacingFactor)),
					0, 0, Math.round(8 * spacingFactor), Math.round(8 * spacingFactor),
					Math.round(8 * spacingFactor), Math.round(8 * spacingFactor));
		} else if (temperatureDifference < 0) {
			context.drawTexture(THERMOMETER_SNOWFLAKE, x - (17 + 149),
					y - (Math.round(22 * spacingFactor)), 0, 0, Math.round(8 * spacingFactor),
					Math.round(8 * spacingFactor), Math.round(8 * spacingFactor),
					Math.round(8 * spacingFactor));
		} else {
			context.drawTexture(THERMOMETER_STILL, x - (17 + 149), y - (Math.round(22 * spacingFactor)),
					0, 0, Math.round(8 * spacingFactor), Math.round(8 * spacingFactor),
					Math.round(8 * spacingFactor), Math.round(8 * spacingFactor));
		}

		// Thermometer Item

		var player = client.player;
		var mainHand = player.getMainHandStack();
		var offHand = player.getOffHandStack();

		if (ThermMod.CONFIG.enableThermometerTemperatureDisplay && (mainHand.isOf(ThermMod.THERMOMETER_ITEM) ||
				offHand.isOf(ThermMod.THERMOMETER_ITEM))) {
			var text = "§7" + (Math.round(temperature * 10.0) / 10.0);
			var textWidth = client.textRenderer.getWidth(text);
			var textHeight = client.textRenderer.fontHeight;

			context.drawTexture(THERMOMETER_DISPLAY, (x - (x - textWidth)) + xOffset,
					frameY + yOffset, 0, 0, Math.round(textWidth * spacingFactor),
					Math.round(textHeight * spacingFactor), Math.round(textWidth *
							spacingFactor),
					Math.round(textHeight * spacingFactor));

			context.drawText(client.textRenderer, "§7" + temperature,
					((x - (x - textWidth)) + 6) + xOffset,
					(frameY + 7) + yOffset, 16777215, true);
		}
	}

	public static void renderGlassThermometerHud(DrawContext context) {
		MinecraftClient client = MinecraftClient.getInstance();

		int xOffset = ThermMod.CONFIG.temperatureXOffset;
		int yOffset = ThermMod.CONFIG.temperatureYOffset;

		int tx = (client.getWindow().getScaledWidth() / 2) + xOffset;
		int ty = client.getWindow().getScaledHeight() + yOffset;

		int x = (client.getWindow().getScaledWidth() / 2) + xOffset;
		int y = (client.getWindow().getScaledHeight() - 48) + yOffset;

		double temperature = ThermClient.clientStoredTemperature;
		double temperatureDifference = ThermClient.clientStoredTemperatureDifference;

		if (temperature == 0.0) {
			return;
		}

		double burnThresholdMinor = ThermMod.CONFIG.burnThresholdMinor;
		double burnThresholdMajor = ThermMod.CONFIG.burnThresholdMajor;
		double freezeThresholdMinor = ThermMod.CONFIG.freezeThresholdMinor;
		double freezeThresholdMajor = ThermMod.CONFIG.freezeThresholdMajor;

		if (!client.player.isSpectator() && !client.player.isCreative()) {
			if (temperature < freezeThresholdMinor + 1
					&& temperature > freezeThresholdMajor) {
				ThermClient.glassShakeTickMax = 4;
				ThermClient.glassShakeAxis = true;
			} else if (temperature < freezeThresholdMajor + 1) {
				ThermClient.glassShakeTickMax = 3;
				ThermClient.glassShakeAxis = true;
			} else if (temperature > burnThresholdMinor - 1
					&& temperature < burnThresholdMajor) {
				ThermClient.glassShakeTickMax = 4;
				ThermClient.glassShakeAxis = false;
			} else if (temperature > burnThresholdMajor - 1) {
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

			if (temperature < burnThresholdMinor - 10
					&& temperature > freezeThresholdMinor + 10) {
				context.drawTexture(TEMPERATE_GLASS, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			} else if (temperature < freezeThresholdMinor + 11
					&& temperature > freezeThresholdMinor + 5) {
				context.drawTexture(COLD_GLASS, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			} else if (temperature < freezeThresholdMinor + 6) {
				context.drawTexture(FROZEN_GLASS, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			} else if (temperature > burnThresholdMinor - 11
					&& temperature < burnThresholdMinor - 5) {
				context.drawTexture(HOT_GLASS, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			} else if (temperature > burnThresholdMinor - 6) {
				context.drawTexture(BLAZING_GLASS, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			}

			if (temperatureDifference < 0
					&& temperatureDifference > -10) {
				context.drawTexture(COOLING_OUTLINE_SMALL, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			} else if (temperatureDifference < -9) {
				context.drawTexture(COOLING_OUTLINE, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			} else if (temperatureDifference > 0
					&& temperatureDifference < 10) {
				context.drawTexture(HEATING_OUTLINE_SMALL, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			} else if (temperatureDifference > 9) {
				context.drawTexture(HEATING_OUTLINE, x - (8), y - (10), 0, 0, 16, 21, 16, 21);
			}
		}

		var player = client.player;
		var mainHand = player.getMainHandStack();
		var offHand = player.getOffHandStack();

		if (ThermMod.CONFIG.enableThermometerTemperatureDisplay && (mainHand.isOf(ThermMod.THERMOMETER_ITEM) ||
				offHand.isOf(ThermMod.THERMOMETER_ITEM))) {
			var text = "§7" + (Math.round(temperature * 10.0) / 10.0);
			var textWidth = client.textRenderer.getWidth(text);
			var textHeight = client.textRenderer.fontHeight;

			float spacingFactor = 1.75f;
			int tFrameY = ty - (Math.round(textHeight * spacingFactor) + 1);

			context.drawTexture(THERMOMETER_DISPLAY, (tx - (tx - textWidth)) + xOffset,
					tFrameY + yOffset, 0, 0, Math.round(textWidth * spacingFactor),
					Math.round(textHeight * spacingFactor), Math.round(textWidth *
							spacingFactor),
					Math.round(textHeight * spacingFactor));

			context.drawText(client.textRenderer, text,
					((tx - (tx - textWidth)) + 6) + xOffset,
					(tFrameY + 7) + yOffset, 16777215, true);
		}
	}

}
