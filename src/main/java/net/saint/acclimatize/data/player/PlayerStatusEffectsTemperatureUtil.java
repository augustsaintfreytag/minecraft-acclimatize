package net.saint.acclimatize.data.player;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.ModStatusEffects;

public final class PlayerStatusEffectsTemperatureUtil {

	public static double temperatureDeltaForItemsAndStatusEffects(ServerPlayerEntity player, double ambientTemperature) {
		var temperatureDeltaFromItems = temperatureDeltaFromItems(player);
		var temperatureDeltaFromStatusEffects = temperatureDeltaFromStatusEffects(player);
		var temperatureDelta = temperatureDeltaFromItems + temperatureDeltaFromStatusEffects;
		var appliedTemperatureDelta = dynamicCooling(ambientTemperature, temperatureDelta);

		return appliedTemperatureDelta;
	}

	private static double dynamicCooling(double ambientTemperature, double applicableDelta) {
		var start = Mod.CONFIG.hyperthermiaThresholdMinor - Mod.CONFIG.hypothermiaThresholdMajor;
		var end = Mod.CONFIG.hyperthermiaThresholdMinor;

		var t = (ambientTemperature - start) / (end - start);
		t = MathHelper.clamp(t, 0.0, 1.0);

		return applicableDelta * t;
	}

	private static double temperatureDeltaFromItems(ServerPlayerEntity player) {
		var fireProtectionLevels = 0;

		for (var item : player.getArmorItems()) {
			var enchantments = item.getEnchantments();

			for (var enchantmentIndex = 0; enchantmentIndex < enchantments.size(); enchantmentIndex++) {
				var compound = enchantments.getCompound(enchantmentIndex);
				var name = compound.getString("id");
				var level = compound.getInt("lvl");

				if (name.equals(StatusEffects.FIRE_RESISTANCE.getTranslationKey())) {
					fireProtectionLevels += level;
				}
			}
		}

		return fireProtectionLevels * Mod.CONFIG.fireProtectionCoolingFactor;
	}

	private static double temperatureDeltaFromStatusEffects(ServerPlayerEntity player) {
		var fireProtectionLevels = 0;
		var heatDissipationLevels = 0;

		for (var statusEffect : player.getStatusEffects()) {
			var statusEffectId = statusEffect.getTranslationKey();

			if (statusEffectId.equals(StatusEffects.FIRE_RESISTANCE.getTranslationKey())) {
				fireProtectionLevels += statusEffect.getAmplifier() + 1;
			}

			if (statusEffectId.equals(ModStatusEffects.HEAT_DISSIPATION.getTranslationKey())) {
				heatDissipationLevels += statusEffect.getAmplifier() + 1;
			}
		}

		var fireProtectionTemperatureDelta = fireProtectionLevels * Mod.CONFIG.fireProtectionCoolingFactor;
		var heatDissipationTemperatureDelta = heatDissipationLevels * Mod.CONFIG.iceWaterCoolingFactor;

		return fireProtectionTemperatureDelta + heatDissipationTemperatureDelta;
	}

}
