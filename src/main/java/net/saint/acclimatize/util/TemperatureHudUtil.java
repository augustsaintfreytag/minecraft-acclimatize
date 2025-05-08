package net.saint.acclimatize.util;

import java.awt.Point;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.ModClient;

public final class TemperatureHudUtil {

	// Textures

	private static final Identifier THERMOMETER_FRAME_TEXTURE = new Identifier(Mod.modId,
			"textures/thermometer/thermometer_frame.png");
	private static final Identifier THERMOMETER_GAUGE_TEXTURE = new Identifier(Mod.modId,
			"textures/thermometer/thermometer_gauge_fix_1.png");
	private static final Identifier THERMOMETER_HAND_TEXTURE = new Identifier(Mod.modId,
			"textures/thermometer/thermometer_hand.png");
	private static final Identifier THERMOMETER_SNOWFLAKE_TEXTURE = new Identifier(Mod.modId,
			"textures/thermometer/snowflake_icon_8x8.png");
	private static final Identifier THERMOMETER_FLAME_TEXTURE = new Identifier(Mod.modId,
			"textures/thermometer/flame_icon_8x8.png");
	private static final Identifier THERMOMETER_STILL_TEXTURE = new Identifier(Mod.modId,
			"textures/thermometer/temperate_icon.png");

	private static final Identifier TEMPERATE_GLASS_TEXTURE = new Identifier(Mod.modId,
			"textures/glass_thermometer/temperate_glass.png");
	private static final Identifier COLD_GLASS_TEXTURE = new Identifier(Mod.modId,
			"textures/glass_thermometer/cold_glass.png");
	private static final Identifier FROZEN_GLASS_TEXTURE = new Identifier(Mod.modId,
			"textures/glass_thermometer/frozen_glass.png");
	private static final Identifier HOT_GLASS_TEXTURE = new Identifier(Mod.modId,
			"textures/glass_thermometer/hot_glass.png");
	private static final Identifier BLAZING_GLASS_TEXTURE = new Identifier(Mod.modId,
			"textures/glass_thermometer/blazing_glass.png");

	private static final Identifier COOLING_OUTLINE_TEXTURE = new Identifier(Mod.modId,
			"textures/glass_thermometer/cooling_outline.png");
	private static final Identifier COOLING_OUTLINE_SMALL_TEXTURE = new Identifier(Mod.modId,
			"textures/glass_thermometer/cooling_small_outline.png");
	private static final Identifier HEATING_OUTLINE_TEXTURE = new Identifier(Mod.modId,
			"textures/glass_thermometer/heating_outline.png");
	private static final Identifier HEATING_OUTLINE_SMALL_TEXTURE = new Identifier(Mod.modId,
			"textures/glass_thermometer/heating_small_outline.png");

	// Rendering (Glass Thermometer)

	public static void renderGlassThermometerHud(DrawContext context) {
		var client = MinecraftClient.getInstance();
		var window = client.getWindow();

		var xOffset = Mod.CONFIG.temperatureXOffset;
		var yOffset = Mod.CONFIG.temperatureYOffset;

		var x = window.getScaledWidth() / 2 + xOffset;
		var y = window.getScaledHeight() - 48 + yOffset;

		var temperature = ModClient.cachedBodyTemperature;
		var temperatureDifference = ModClient.cachedTemperatureDifference;

		if (temperature == 0.0) {
			return;
		}

		if (!client.player.isSpectator() && !client.player.isCreative()) {
			var offset = applyGlassShakeForRender(temperature);
			var glassTexture = selectGlassTexture(temperature);
			var outlineTexture = selectOutlineTexture(temperatureDifference);

			var positionX = x + offset.x;
			var positionY = y + offset.y;

			if (glassTexture != null) {
				context.drawTexture(glassTexture, positionX - 8, positionY - 10, 0, 0, 16, 21, 16, 21);
			}

			if (outlineTexture != null) {
				context.drawTexture(outlineTexture, positionX - 8, positionY - 10, 0, 0, 16, 21, 16, 21);
			}
		}
	}

	private static Point applyGlassShakeForRender(double temperature) {
		var burnThresholdMinor = Mod.CONFIG.hyperthermiaThresholdMinor;
		var burnThresholdMajor = Mod.CONFIG.hyperthermiaThresholdMajor;
		var freezeThresholdMinor = Mod.CONFIG.hypothermiaThresholdMinor;
		var freezeThresholdMajor = Mod.CONFIG.hypothermiaThresholdMajor;

		if (temperature < freezeThresholdMinor + 1 && temperature > freezeThresholdMajor) {
			ModClient.glassShakeTickMax = 4;
			ModClient.glassShakeAxis = true;
		} else if (temperature < freezeThresholdMajor + 1) {
			ModClient.glassShakeTickMax = 3;
			ModClient.glassShakeAxis = true;
		} else if (temperature > burnThresholdMinor - 1 && temperature < burnThresholdMajor) {
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
				ModClient.glassShakePM = -ModClient.glassShakePM;
			}

			if (ModClient.glassShakeAxis) {
				return new Point(ModClient.glassShakePM, 0);
			} else {
				return new Point(0, ModClient.glassShakePM);
			}
		}
		return new Point(0, 0);
	}

	private static Identifier selectGlassTexture(double temperature) {
		var burnThresholdMinor = Mod.CONFIG.hyperthermiaThresholdMinor;
		var freezeThresholdMinor = Mod.CONFIG.hypothermiaThresholdMinor;

		if (temperature < burnThresholdMinor - 10 && temperature > freezeThresholdMinor + 10) {
			return TEMPERATE_GLASS_TEXTURE;
		}

		if (temperature < freezeThresholdMinor + 11 && temperature > freezeThresholdMinor + 5) {
			return COLD_GLASS_TEXTURE;
		}

		if (temperature < freezeThresholdMinor + 6) {
			return FROZEN_GLASS_TEXTURE;
		}

		if (temperature > burnThresholdMinor - 11 && temperature < burnThresholdMinor - 5) {
			return HOT_GLASS_TEXTURE;
		}

		if (temperature > burnThresholdMinor - 6) {
			return BLAZING_GLASS_TEXTURE;
		}

		return null;
	}

	private static Identifier selectOutlineTexture(double temperatureDifference) {
		if (temperatureDifference < 0 && temperatureDifference > -10) {
			return COOLING_OUTLINE_SMALL_TEXTURE;
		}

		if (temperatureDifference < -9) {
			return COOLING_OUTLINE_TEXTURE;
		}

		if (temperatureDifference > 0 && temperatureDifference < 10) {
			return HEATING_OUTLINE_SMALL_TEXTURE;
		}

		if (temperatureDifference > 9) {
			return HEATING_OUTLINE_TEXTURE;
		}

		return null;
	}

	// Rendering (Gauge Thermometer)

	public static void renderGaugeThermometerHud(DrawContext context) {
		var client = MinecraftClient.getInstance();
		var window = client.getWindow();

		var temperature = ModClient.cachedBodyTemperature;
		var temperatureDifference = ModClient.cachedTemperatureDifference;

		if (temperature == 0.0) {
			return;
		}

		var xOffset = Mod.CONFIG.temperatureXOffset;
		var yOffset = Mod.CONFIG.temperatureYOffset;

		var x = window.getScaledWidth() / 2 + xOffset;
		var y = window.getScaledHeight() + yOffset;

		var spacingFactor = 1.5f;
		var temperatureFraction = ((temperature / 100f) * Math.round(40 * spacingFactor));

		if (((temperature / 100f) * Math.round(40 * spacingFactor)) > 59.0f) {
			temperatureFraction = ((97 / 100f) * Math.round(40 * spacingFactor));
		} else if ((temperature / 100f) < 0) {
			temperatureFraction = 0f;
		}

		context.drawTexture(THERMOMETER_GAUGE_TEXTURE, x - ((44 + 149) - Math.round(2 * spacingFactor)),
				y - (Math.round(8 * spacingFactor) + Math.round(3 * spacingFactor) + 1), 0, 0,
				Math.round(40 * spacingFactor), Math.round(9 * spacingFactor),
				Math.round(40 * spacingFactor), Math.round(9 * spacingFactor));

		context.drawTexture(THERMOMETER_HAND_TEXTURE,
				x - (int) (((44 + 149) - Math.round(2 * spacingFactor)) - temperatureFraction),
				y - (Math.round(8 * spacingFactor) + Math.round(3 * spacingFactor) + 1), 0, 0,
				Math.round(1), Math.round(9 * spacingFactor), Math.round(1), Math.round(9 * spacingFactor));

		var frameY = y - (Math.round(13 * spacingFactor) + 1);

		context.drawTexture(THERMOMETER_FRAME_TEXTURE, x - (44 + 149), frameY, 0, 0,
				Math.round(44 * spacingFactor), Math.round(13 * spacingFactor),
				Math.round(44 * spacingFactor), Math.round(13 * spacingFactor));

		if (temperatureDifference > 0) {
			context.drawTexture(THERMOMETER_FLAME_TEXTURE, x - (17 + 149), y - (Math.round(22 * spacingFactor)),
					0, 0, Math.round(8 * spacingFactor), Math.round(8 * spacingFactor),
					Math.round(8 * spacingFactor), Math.round(8 * spacingFactor));
		} else if (temperatureDifference < 0) {
			context.drawTexture(THERMOMETER_SNOWFLAKE_TEXTURE, x - (17 + 149),
					y - (Math.round(22 * spacingFactor)), 0, 0, Math.round(8 * spacingFactor),
					Math.round(8 * spacingFactor), Math.round(8 * spacingFactor),
					Math.round(8 * spacingFactor));
		} else {
			context.drawTexture(THERMOMETER_STILL_TEXTURE, x - (17 + 149), y - (Math.round(22 * spacingFactor)),
					0, 0, Math.round(8 * spacingFactor), Math.round(8 * spacingFactor),
					Math.round(8 * spacingFactor), Math.round(8 * spacingFactor));
		}
	}

}
