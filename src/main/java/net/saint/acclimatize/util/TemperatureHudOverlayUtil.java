package net.saint.acclimatize.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.ModClient;
import net.saint.acclimatize.library.RGBAColor;

public final class TemperatureHudOverlayUtil {

	// Configuration (Rendering)

	public static final Identifier TEMPERATURE_OVERLAY = new Identifier(Mod.modId, "textures/overlay/temperature_overlay.png");

	private static final RGBAColor HYPOTHERMIA_COLOR = new RGBAColor(0.6f, 0.75f, 1.0f, 1.0f);
	private static final RGBAColor HYPERTHERMIA_COLOR = new RGBAColor(0.8f, 0.3f, 0.15f, 1.0f);

	// Configuration (Animation)

	private static final long ANIMATION_DURATION = 1500;

	private static long animationStartTime = 0;
	private static boolean isDisplayingOverlay = false;
	private static float lastTargetAlpha = 0;
	private static RGBAColor lastOverlayColor = RGBAColor.white().transparent();

	// Rendering

	public static void renderVignetteHudOverlayIfNeeded(DrawContext context, Entity entity) {
		if (!Mod.CONFIG.enableTemperatureVignette) {
			return;
		}

		if (!entity.isPlayer()) {
			return;
		}

		var player = (ClientPlayerEntity) entity;

		if (player.isCreative() || player.isSpectator()) {
			return;
		}

		renderVignetteHudOverlay(context, ModClient.cachedBodyTemperature);
	}

	private static void renderVignetteHudOverlay(DrawContext context, double temperature) {
		var staticColor = overlayColorForTemperature(temperature);
		var shouldDisplayOverlay = staticColor != null;

		if (staticColor != null) {
			lastOverlayColor = staticColor.copy();
			lastTargetAlpha = lastOverlayColor.a;
		}

		if (shouldDisplayOverlay != isDisplayingOverlay) {
			animationStartTime = System.currentTimeMillis();
			isDisplayingOverlay = shouldDisplayOverlay;
		}

		var maxAlpha = MathUtil.clamp((float) Mod.CONFIG.temperatureVignetteAlpha, 0, 1);
		var progress = Math.min(maxAlpha, (System.currentTimeMillis() - animationStartTime) / (float) ANIMATION_DURATION);
		var alpha = shouldDisplayOverlay ? lastTargetAlpha * progress : lastTargetAlpha * (maxAlpha - progress);

		// Bail if not supposed to draw and fade out has already completed.
		if (!shouldDisplayOverlay && progress >= 1f) {
			return;
		}

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);

		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

		// Apply tint and alpha for drawing.
		context.setShaderColor(lastOverlayColor.r, lastOverlayColor.g, lastOverlayColor.b, alpha);

		int width = context.getScaledWindowWidth();
		int height = context.getScaledWindowHeight();

		context.drawTexture(TEMPERATURE_OVERLAY, 0, 0, -90, 0.0f, 0.0f, width, height, width, height);

		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();

		context.setShaderColor(1f, 1f, 1f, 1f);

		RenderSystem.defaultBlendFunc();
	}

	// Overlay Color

	private static RGBAColor overlayColorForTemperature(double temperature) {
		if (temperature <= Mod.CONFIG.hypothermiaThresholdMinor && temperature > Mod.CONFIG.hypothermiaThresholdMajor) {
			return HYPOTHERMIA_COLOR;
		}

		if (temperature <= Mod.CONFIG.hypothermiaThresholdMajor) {
			return HYPOTHERMIA_COLOR;
		}

		if (temperature >= Mod.CONFIG.hyperthermiaThresholdMinor && temperature < Mod.CONFIG.hyperthermiaThresholdMajor) {
			return HYPERTHERMIA_COLOR;
		}

		if (temperature >= Mod.CONFIG.hyperthermiaThresholdMajor) {
			return HYPERTHERMIA_COLOR;
		}

		return null;
	}
}
