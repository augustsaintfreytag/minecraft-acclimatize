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

	public static final Identifier HIGH_TEMPERATURE_OVERLAY = new Identifier(Mod.modid,
			"textures/overlay/high_temperature_overlay.png");

	public static final Identifier EXTREME_TEMPERATURE_OVERLAY = new Identifier(Mod.modid,
			"textures/overlay/extreme_temperature_overlay.png");

	public static void renderVignetteHudOverlay(DrawContext context, Entity entity) {
		if (!Mod.CONFIG.enableTemperatureVignette) {
			return;
		}

		if (!entity.isPlayer()) {
			return;
		}

		var player = (ClientPlayerEntity) entity;

		if (!player.isCreative() && !player.isSpectator()) {
			float r = 0;
			float g = 0;
			float b = 0;
			float a = 0;

			boolean isExtreme = false;

			if (ModClient.clientStoredTemperature < 41 && ModClient.clientStoredTemperature > 35) {
				r = 0.25f;
				g = 0.5f;
				b = 0.8f;
				a = 0.5f;
			} else if (ModClient.clientStoredTemperature < 36 && ModClient.clientStoredTemperature > 25) {
				r = 0.25f;
				g = 0.5f;
				b = 0.8f;
				a = 1f;
			} else if (ModClient.clientStoredTemperature < 26) {
				r = 0.60f;
				g = 0.75f;
				b = 1.0f;
				a = 2.5f;
				// extreme = true;
			} else if (ModClient.clientStoredTemperature > 59 && ModClient.clientStoredTemperature < 65) {
				r = 0.8f;
				g = 0.3f;
				b = 0.15f;
				a = 0.5f;
			} else if (ModClient.clientStoredTemperature > 64 && ModClient.clientStoredTemperature < 75) {
				r = 0.8f;
				g = 0.3f;
				b = 0.15f;
				a = 1f;
			} else if (ModClient.clientStoredTemperature > 74) {
				r = 0.9f;
				g = 0.4f;
				b = 0.15f;
				a = 02.5f;
			}

			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(false);
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA,
					GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE,
					GlStateManager.DstFactor.ZERO);
			context.setShaderColor(r, g, b, a);

			if (isExtreme) {
				context.drawTexture(EXTREME_TEMPERATURE_OVERLAY, 0, 0, -90, 0.0f, 0.0f,
						context.getScaledWindowWidth(), context.getScaledWindowHeight(),
						context.getScaledWindowWidth(), context.getScaledWindowHeight());
			} else {
				context.drawTexture(HIGH_TEMPERATURE_OVERLAY, 0, 0, -90, 0.0f, 0.0f,
						context.getScaledWindowWidth(), context.getScaledWindowHeight(),
						context.getScaledWindowWidth(), context.getScaledWindowHeight());
			}

			RenderSystem.depthMask(true);
			RenderSystem.enableDepthTest();
			context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
			RenderSystem.defaultBlendFunc();
		}
	}

}
