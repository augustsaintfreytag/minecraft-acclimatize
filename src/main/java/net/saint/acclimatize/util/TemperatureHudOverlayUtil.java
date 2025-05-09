package net.saint.acclimatize.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.ModClient;

public final class TemperatureHudOverlayUtil {

	// Configuration

	public static final Identifier HIGH_TEMPERATURE_OVERLAY = new Identifier(Mod.modId, "textures/overlay/high_temperature_overlay.png");
	public static final Identifier EXTREME_TEMPERATURE_OVERLAY = new Identifier(Mod.modId,
			"textures/overlay/extreme_temperature_overlay.png");

	public static final double ALPHA_EFFECT_MINOR = 0.4;
	public static final double ALPHA_EFFECT_MAJOR = 0.7;

	private static final RGBAColor hypothermiaColor = new RGBAColor(0.6f, 0.75f, 1.0f, 1.0f);
	private static final RGBAColor hyperthermiaColor = new RGBAColor(0.8f, 0.3f, 0.15f, 1.0f);

	// Library

	private static final class RGBAColor {
		public final float r;
		public final float g;
		public final float b;
		public final float a;

		private RGBAColor(float r, float g, float b, float a) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}

		public RGBAColor withAlpha(float alpha) {
			return new RGBAColor(this.r, this.g, this.b, alpha);
		}
	}

	// Rendering

	public static void renderVignetteHudOverlay(DrawContext context, Entity entity) {
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

		var overlayColor = overlayColorForTemperature(ModClient.cachedBodyTemperature);

		if (overlayColor == null) {
			return;
		}

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR,
				GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		context.setShaderColor(overlayColor.r, overlayColor.g, overlayColor.b, overlayColor.a);

		var texture = overlayColor.a > 1f ? EXTREME_TEMPERATURE_OVERLAY : HIGH_TEMPERATURE_OVERLAY;

		context.drawTexture(texture, 0, 0, -90, 0f, 0f, context.getScaledWindowWidth(), context.getScaledWindowHeight(),
				context.getScaledWindowWidth(), context.getScaledWindowHeight());

		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();

		context.setShaderColor(1f, 1f, 1f, 1f);

		RenderSystem.defaultBlendFunc();
	}

	// Overlay Color

	private static RGBAColor overlayColorForTemperature(double temperature) {
		if (temperature <= Mod.CONFIG.hypothermiaThresholdMinor && temperature > Mod.CONFIG.hypothermiaThresholdMajor) {
			return hypothermiaColor.withAlpha((float) (Mod.CONFIG.temperatureVignetteAlpha * 0.8));
		}

		if (temperature <= Mod.CONFIG.hypothermiaThresholdMajor) {
			return hypothermiaColor.withAlpha((float) Mod.CONFIG.temperatureVignetteAlpha);
		}

		if (temperature >= Mod.CONFIG.hyperthermiaThresholdMinor && temperature < Mod.CONFIG.hyperthermiaThresholdMajor) {
			return hyperthermiaColor.withAlpha((float) (Mod.CONFIG.temperatureVignetteAlpha * 0.8));
		}

		if (temperature >= Mod.CONFIG.hyperthermiaThresholdMajor) {
			return hyperthermiaColor.withAlpha((float) Mod.CONFIG.temperatureVignetteAlpha);
		}

		return null;
	}
}
