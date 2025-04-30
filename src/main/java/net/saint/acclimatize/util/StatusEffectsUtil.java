package net.saint.acclimatize.util;

import java.util.HashSet;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.saint.acclimatize.ModStatusEffects;

public final class StatusEffectsUtil {

	// Configuration

	private static final HashSet<Identifier> blacklistedPotionEffects = new HashSet<>();

	static {
		blacklistedPotionEffects.add(Registries.STATUS_EFFECT.getId(ModStatusEffects.HYPOTHERMIA));
		blacklistedPotionEffects.add(Registries.STATUS_EFFECT.getId(ModStatusEffects.HYPERTHERMIA));
		blacklistedPotionEffects.add(Registries.STATUS_EFFECT.getId(ModStatusEffects.COLD_RESISTANCE));
		blacklistedPotionEffects.add(new Identifier("dehydration", "thirst_effect"));
	}

	// Analysis

	public static boolean entityHasOnlyBlacklistedStatusEffects(PlayerEntity player) {
		for (var statusEffect : player.getStatusEffects()) {
			var statusEffectIdentifier = Registries.STATUS_EFFECT.getId(statusEffect.getEffectType());

			if (!blacklistedPotionEffects.contains(statusEffectIdentifier)) {
				return false;
			}
		}

		return true;
	}

	public static boolean entityHasAnyTemperatureStatusEffects(PlayerEntity player) {
		if (player.hasStatusEffect(ModStatusEffects.HYPOTHERMIA)
				|| player.hasStatusEffect(ModStatusEffects.HYPERTHERMIA)) {
			return true;
		}

		return false;
	}

}
