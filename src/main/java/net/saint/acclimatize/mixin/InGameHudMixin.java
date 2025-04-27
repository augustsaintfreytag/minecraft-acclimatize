package net.saint.acclimatize.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.ModClient;
import net.saint.acclimatize.util.TemperatureHudOverlayUtil;
import net.saint.acclimatize.util.TemperatureHudUtil;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

	@Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 1))
	private void mixinRenderStatusBars(DrawContext context, CallbackInfo callbackInfo) {
		if (!ModClient.enableHUD) {
			return;
		}

		if (Mod.CONFIG.temperatureDisplayType.equals("gauge")) {
			TemperatureHudUtil.renderGaugeThermometerHud(context);
			return;
		}

		if (Mod.CONFIG.temperatureDisplayType.equals("glass_thermometer")) {
			TemperatureHudUtil.renderGlassThermometerHud(context);
			return;
		}
	}

	@Inject(method = "renderVignetteOverlay", at = @At(value = "HEAD"))
	private void mixinRenderVignetteOverlay(DrawContext context, Entity entity, CallbackInfo callbackInfo) {
		TemperatureHudOverlayUtil.renderVignetteHudOverlay(context, entity);
	}
}