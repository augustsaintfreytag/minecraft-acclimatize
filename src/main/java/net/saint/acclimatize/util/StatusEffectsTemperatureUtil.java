package net.saint.acclimatize.util;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.ModStatusEffects;

public final class StatusEffectsTemperatureUtil {

	public static double temperatureDeltaForItemsAndStatusEffects(ServerPlayerEntity player) {
		var temperatureDeltaFromItems = temperatureDeltaFromItems(player);
		var temperatureDeltaFromStatusEffects = temperatureDeltaFromStatusEffects(player);

		return temperatureDeltaFromItems + temperatureDeltaFromStatusEffects;
	}

	private static double temperatureDeltaFromItems(ServerPlayerEntity player) {
		var fireProtectionLevels = 0;

		for (var item : player.getArmorItems()) {
			var enchantments = item.getEnchantments();

			for (var enchantmentIndex = 0; enchantmentIndex < enchantments.size(); enchantmentIndex++) {
				var compound = enchantments.getCompound(enchantmentIndex);
				var name = compound.getString("id");
				var level = compound.getInt("lvl");

				if (name == StatusEffects.FIRE_RESISTANCE.getTranslationKey()) {
					fireProtectionLevels += level;
				}
			}
		}

		return fireProtectionLevels * Mod.CONFIG.fireProtectionCoolingFactor;
	}

	private static double temperatureDeltaFromStatusEffects(ServerPlayerEntity player) {
		var fireProtectionLevels = 0;
		var coldProtectionLevels = 0;

		for (var statusEffect : player.getStatusEffects()) {
			if (statusEffect.getTranslationKey() == StatusEffects.FIRE_RESISTANCE.getTranslationKey()) {
				fireProtectionLevels += statusEffect.getAmplifier();
			}

			if (statusEffect.getTranslationKey() == ModStatusEffects.COLD_RESISTANCE.getTranslationKey()) {
				coldProtectionLevels += statusEffect.getAmplifier();
			}
		}

		var fireProtectionTemperatureDelta = fireProtectionLevels * Mod.CONFIG.fireProtectionCoolingFactor;
		var coldProtectionTemperatureDelta = coldProtectionLevels * Mod.CONFIG.coldProtectionCoolingFactor;

		return fireProtectionTemperatureDelta + coldProtectionTemperatureDelta;
	}

}
