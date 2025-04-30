package net.saint.acclimatize.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.ModClient;

public final class TemperatureHudUtil {

	private static final Identifier THERMOMETER_FRAME = new Identifier(Mod.modId,
			"textures/thermometer/thermometer_frame.png");
	private static final Identifier THERMOMETER_GAUGE = new Identifier(Mod.modId,
			"textures/thermometer/thermometer_gauge_fix_1.png");
	private static final Identifier THERMOMETER_HAND = new Identifier(Mod.modId,
			"textures/thermometer/thermometer_hand.png");
	private static final Identifier THERMOMETER_SNOWFLAKE = new Identifier(Mod.modId,
			"textures/thermometer/snowflake_icon_8x8.png");
	private static final Identifier THERMOMETER_FLAME = new Identifier(Mod.modId,
			"textures/thermometer/flame_icon_8x8.png");
	private static final Identifier THERMOMETER_STILL = new Identifier(Mod.modId,
			"textures/thermometer/temperate_icon.png");

	private static final Identifier THERMOMETER_DISPLAY = new Identifier(Mod.modId,
			"textures/thermometer/thermometer_display.png");

	// glass thermometer
	private static final Identifier TEMPERATE_GLASS = new Identifier(Mod.modId,
			"textures/glass_thermometer/temperate_glass.png");
	private static final Identifier COLD_GLASS = new Identifier(Mod.modId,
			"textures/glass_thermometer/cold_glass.png");
	private static final Identifier FROZEN_GLASS = new Identifier(Mod.modId,
			"textures/glass_thermometer/frozen_glass.png");
	private static final Identifier HOT_GLASS = new Identifier(Mod.modId,
			"textures/glass_thermometer/hot_glass.png");
	private static final Identifier BLAZING_GLASS = new Identifier(Mod.modId,
			"textures/glass_thermometer/blazing_glass.png");

	private static final Identifier COOLING_OUTLINE = new Identifier(Mod.modId,
			"textures/glass_thermometer/cooling_outline.png");
	private static final Identifier COOLING_OUTLINE_SMALL = new Identifier(Mod.modId,
			"textures/glass_thermometer/cooling_small_outline.png");
	private static final Identifier HEATING_OUTLINE = new Identifier(Mod.modId,
			"textures/glass_thermometer/heating_outline.png");
	private static final Identifier HEATING_OUTLINE_SMALL = new Identifier(Mod.modId,
			"textures/glass_thermometer/heating_small_outline.png");

	public static void renderGaugeThermometerHud(DrawContext context) {
		MinecraftClient client = MinecraftClient.getInstance();

		var temperature = ModClient.cachedBodyTemperature;
		var temperatureDifference = ModClient.cachedTemperatureDifference;

		if (temperature == 0.0) {
			return;
		}

		var xOffset = Mod.CONFIG.temperatureXOffset;
		var yOffset = Mod.CONFIG.temperatureYOffset;

		var x = (client.getWindow().getScaledWidth() / 2) + xOffset;
		var y = client.getWindow().getScaledHeight() + yOffset;

		var spacingFactor = 1.5f;
		var temperatureFraction = ((temperature / 100f) * Math.round(40 * spacingFactor));

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

		var frameY = y - (Math.round(13 * spacingFactor) + 1);

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

		if (Mod.CONFIG.enableThermometerTemperatureDisplay && (mainHand.isOf(Mod.THERMOMETER_ITEM) ||
				offHand.isOf(Mod.THERMOMETER_ITEM))) {
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

		var xOffset = Mod.CONFIG.temperatureXOffset;
		var yOffset = Mod.CONFIG.temperatureYOffset;

		var tx = (client.getWindow().getScaledWidth() / 2) + xOffset;
		var ty = client.getWindow().getScaledHeight() + yOffset;

		var x = (client.getWindow().getScaledWidth() / 2) + xOffset;
		var y = (client.getWindow().getScaledHeight() - 48) + yOffset;

		var temperature = ModClient.cachedBodyTemperature;
		var temperatureDifference = ModClient.cachedTemperatureDifference;

		if (temperature == 0.0) {
			return;
		}

		var burnThresholdMinor = Mod.CONFIG.hyperthermiaThresholdMinor;
		var burnThresholdMajor = Mod.CONFIG.hyperthermiaThresholdMajor;
		var freezeThresholdMinor = Mod.CONFIG.hypothermiaThresholdMinor;
		var freezeThresholdMajor = Mod.CONFIG.hypothermiaThresholdMajor;

		if (!client.player.isSpectator() && !client.player.isCreative()) {
			if (temperature < freezeThresholdMinor + 1
					&& temperature > freezeThresholdMajor) {
				ModClient.glassShakeTickMax = 4;
				ModClient.glassShakeAxis = true;
			} else if (temperature < freezeThresholdMajor + 1) {
				ModClient.glassShakeTickMax = 3;
				ModClient.glassShakeAxis = true;
			} else if (temperature > burnThresholdMinor - 1
					&& temperature < burnThresholdMajor) {
				ModClient.glassShakeTickMax = 4;
				ModClient.glassShakeAxis = false;
			} else if (temperature > burnThresholdMajor - 1) {
				ModClient.glassShakeTickMax = 3;
				ModClient.glassShakeAxis = false;
			} else {
				ModClient.glassShakeTickMax = 0;
			}

			if (ModClient.glassShakeTickMax != 0) {
				ModClient.glassShakeTick += 1;
				if (ModClient.glassShakeTick >= ModClient.glassShakeTickMax) {
					ModClient.glassShakeTick = 0;
					if (ModClient.glassShakePM == 1) {
						ModClient.glassShakePM = -1;
					} else if (ModClient.glassShakePM == -1) {
						ModClient.glassShakePM = 1;
					}
				}
				if (ModClient.glassShakeAxis) {
					x += ModClient.glassShakePM;
				} else {
					y += ModClient.glassShakePM;
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

		if (Mod.CONFIG.enableThermometerTemperatureDisplay && (mainHand.isOf(Mod.THERMOMETER_ITEM) ||
				offHand.isOf(Mod.THERMOMETER_ITEM))) {
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
