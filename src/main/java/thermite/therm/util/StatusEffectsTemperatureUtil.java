package thermite.therm.util;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import thermite.therm.ThermMod;
import thermite.therm.effect.ThermStatusEffects;

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

		return fireProtectionLevels * ThermMod.CONFIG.fireProtectionCoolingFactor;
	}

	private static double temperatureDeltaFromStatusEffects(ServerPlayerEntity player) {
		var fireProtectionLevels = 0;
		var coldProtectionLevels = 0;

		for (var statusEffect : player.getStatusEffects()) {
			if (statusEffect.getTranslationKey() == StatusEffects.FIRE_RESISTANCE.getTranslationKey()) {
				fireProtectionLevels += statusEffect.getAmplifier();
			}

			if (statusEffect.getTranslationKey() == ThermStatusEffects.COLD_RESISTANCE.getTranslationKey()) {
				coldProtectionLevels += statusEffect.getAmplifier();
			}
		}

		var fireProtectionTemperatureDelta = fireProtectionLevels * ThermMod.CONFIG.fireProtectionCoolingFactor;
		var coldProtectionTemperatureDelta = coldProtectionLevels * ThermMod.CONFIG.coldProtectionCoolingFactor;

		return fireProtectionTemperatureDelta + coldProtectionTemperatureDelta;
	}

}
