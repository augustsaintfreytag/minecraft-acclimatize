package thermite.therm.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import thermite.therm.effect.ThermStatusEffects;

@Mixin(LivingEntity.class)
public abstract class PlayerStatusEffectsMixin extends Entity {

	private PlayerStatusEffectsMixin() {
		super(null, null);
	}

	@Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
	private void setSprintingMixin(boolean sprinting, CallbackInfo callbackInfo) {
		withPlayerEntity(player -> {
			// Check for extreme body temperature status effects and disable sprinting.
			if (!entityHasTemperatureStatusEffects(player)) {
				return;
			}

			super.setSprinting(false);
			callbackInfo.cancel();
		});
	}

	@Shadow
	protected abstract void clearPotionSwirls();

	/**
	 * Right after vanilla resets potion‑swirl visibility, if the
	 * entity has Hypothermia we zero out the colour so tickStatusEffects()
	 * never spawns any particles.
	 */
	@Inject(method = "updatePotionVisibility", at = @At("TAIL"))
	private void disableSwirlsForHypothermia(CallbackInfo ci) {
		withPlayerEntity(player -> {
			if (entityHasOnlyTemperatureStatusEffects(player)) {
				clearPotionSwirls();
			}
		});
	}

	// Utility

	private void withPlayerEntity(Consumer<PlayerEntity> block) {
		var entity = (LivingEntity) (Object) this;

		if (!(entity instanceof PlayerEntity)) {
			return;
		}

		block.accept((PlayerEntity) entity);
	}

	private static boolean entityHasTemperatureStatusEffects(PlayerEntity player) {
		if (player.hasStatusEffect(ThermStatusEffects.HYPOTHERMIA)
				|| player.hasStatusEffect(ThermStatusEffects.HYPERTHERMIA)) {
			return true;
		}

		return false;
	}

	private static boolean entityHasOnlyTemperatureStatusEffects(PlayerEntity player) {
		var numberOfStatusEffects = player.getStatusEffects().size();

		if (entityHasTemperatureStatusEffects(player) && numberOfStatusEffects == 1) {
			return true;
		}

		return false;
	}

}
