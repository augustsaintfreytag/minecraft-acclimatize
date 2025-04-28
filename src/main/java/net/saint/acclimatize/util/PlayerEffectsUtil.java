package net.saint.acclimatize.util;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.ModStatusEffects;
import net.saint.acclimatize.player.PlayerState;

public final class PlayerEffectsUtil {

	// Library

	public static enum TemperatureDamageKind {
		HEAT, COLD
	}

	public static enum TemperatureIntensityKind {
		MINOR, MAJOR
	}

	public static class TemperatureDamageTuple {
		public TemperatureDamageKind kind;
		public TemperatureIntensityKind intensity;

		public TemperatureDamageTuple(TemperatureDamageKind kind, TemperatureIntensityKind intensity) {
			this.kind = kind;
			this.intensity = intensity;
		}
	}

	// Handling

	public static void handlePlayerEffects(ServerPlayerEntity player, PlayerState playerState) {
		// TODO: Check for resistance status effects or armor items.

			return;
		}

		var temperatureDamageTuple = temperatureDamageTupleForPlayerState(playerState);

		if (temperatureDamageTuple == null) {
			return;
		}

		if (temperatureDamageTuple.kind == TemperatureDamageKind.COLD) {
			if (temperatureDamageTuple.intensity == TemperatureIntensityKind.MINOR) {
				var hypothermiaStatusEffect = new StatusEffectInstance(ModStatusEffects.HYPOTHERMIA, 600, 0);
				player.addStatusEffect(hypothermiaStatusEffect);
			} else {
				var hypothermiaStatusEffect = new StatusEffectInstance(ModStatusEffects.HYPOTHERMIA, 600, 1);
				player.addStatusEffect(hypothermiaStatusEffect);
			}
		} else if (temperatureDamageTuple.kind == TemperatureDamageKind.HEAT) {
			if (temperatureDamageTuple.intensity == TemperatureIntensityKind.MINOR) {
				var hyperthermiaStatusEffect = new StatusEffectInstance(ModStatusEffects.HYPERTHERMIA, 600, 0);
				player.addStatusEffect(hyperthermiaStatusEffect);
			} else {
				var hyperthermiaStatusEffect = new StatusEffectInstance(ModStatusEffects.HYPERTHERMIA, 600, 1);
				player.addStatusEffect(hyperthermiaStatusEffect);
			}
		}

		if (player.getHealth() <= 0.0) {
			playerState.bodyTemperature = 50;
			playerState.damageTick = 0;
		}
	}

	private static TemperatureDamageTuple temperatureDamageTupleForPlayerState(PlayerState playerState) {
		var bodyTemperature = playerState.bodyTemperature;

		if (bodyTemperature <= Mod.CONFIG.freezeThresholdMinor
				&& bodyTemperature > Mod.CONFIG.freezeThresholdMajor) {
			return new TemperatureDamageTuple(TemperatureDamageKind.COLD, TemperatureIntensityKind.MINOR);
		}

		if (bodyTemperature <= Mod.CONFIG.freezeThresholdMajor) {
			return new TemperatureDamageTuple(TemperatureDamageKind.COLD, TemperatureIntensityKind.MAJOR);
		}

		if (bodyTemperature >= Mod.CONFIG.burnThresholdMinor
				&& bodyTemperature < Mod.CONFIG.burnThresholdMajor) {
			return new TemperatureDamageTuple(TemperatureDamageKind.HEAT, TemperatureIntensityKind.MINOR);
		}

		if (bodyTemperature >= Mod.CONFIG.burnThresholdMajor) {
			return new TemperatureDamageTuple(TemperatureDamageKind.HEAT, TemperatureIntensityKind.MAJOR);
		}

		return null;
	}

}
