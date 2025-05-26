package net.saint.acclimatize.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.saint.acclimatize.data.statuseffect.StatusEffectsUtil;

@Mixin(LivingEntity.class)
public abstract class PlayerStatusEffectsMixin extends Entity {

	// Init

	private PlayerStatusEffectsMixin() {
		super(null, null);
	}

	// Injection

	@Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
	private void setSprintingMixin(boolean sprinting, CallbackInfo callbackInfo) {
		withPlayerEntity(player -> {
			// Check for extreme body temperature status effects and disable sprinting.
			if (!StatusEffectsUtil.entityHasAnyTemperatureStatusEffects(player)) {
				return;
			}

			super.setSprinting(false);
			callbackInfo.cancel();
		});
	}

	@Shadow
	protected abstract void clearPotionSwirls();

	/**
	 * Right after vanilla resets potion‑swirl visibility, if the entity has Hypothermia we zero out
	 * the colour so tickStatusEffects() never spawns any particles.
	 */
	@Inject(method = "updatePotionVisibility", at = @At("TAIL"))
	private void mixinUpdatePotionVisibility(CallbackInfo ci) {
		withPlayerEntity(player -> {
			if (StatusEffectsUtil.entityHasOnlyBlacklistedStatusEffects(player)) {
				clearPotionSwirls();
			}
		});
	}

	// Effects Analysis

	// Utility

	private void withPlayerEntity(Consumer<PlayerEntity> block) {
		var entity = (LivingEntity) (Object) this;

		if (!(entity instanceof PlayerEntity)) {
			return;
		}

		block.accept((PlayerEntity) entity);
	}

}
