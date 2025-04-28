package net.saint.acclimatize.util;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.ModStatusEffects;
import net.saint.acclimatize.player.PlayerState;

public final class PlayerEffectsUtil {

	private static final int EFFECT_DURATION = 420; // 20+1 seconds
	private static final int EFFECT_TICK_INTERVAL = 10; // 10 seconds

	private static int effectTick = -1;

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
		effectTick++;

		if (effectTick != 0 && effectTick < EFFECT_TICK_INTERVAL) {
			return;
		}

		var temperatureDamageTuple = temperatureDamageTupleForPlayerState(playerState);

		if (temperatureDamageTuple == null) {
			effectTick = -1;
			return;
		}

		if (temperatureDamageTuple.kind == TemperatureDamageKind.COLD) {
			if (temperatureDamageTuple.intensity == TemperatureIntensityKind.MINOR) {
				var hypothermiaStatusEffect = new StatusEffectInstance(
						ModStatusEffects.HYPOTHERMIA, EFFECT_DURATION, 0);
				player.addStatusEffect(hypothermiaStatusEffect);
			} else {
				var hypothermiaStatusEffect = new StatusEffectInstance(
						ModStatusEffects.HYPOTHERMIA, EFFECT_DURATION, 1);
				player.addStatusEffect(hypothermiaStatusEffect);
			}
		} else if (temperatureDamageTuple.kind == TemperatureDamageKind.HEAT) {
			if (temperatureDamageTuple.intensity == TemperatureIntensityKind.MINOR) {
				var hyperthermiaStatusEffect = new StatusEffectInstance(
						ModStatusEffects.HYPERTHERMIA, EFFECT_DURATION, 0);
				player.addStatusEffect(hyperthermiaStatusEffect);
			} else {
				var hyperthermiaStatusEffect = new StatusEffectInstance(
						ModStatusEffects.HYPERTHERMIA, EFFECT_DURATION, 1);
				player.addStatusEffect(hyperthermiaStatusEffect);
			}
		}

		if (player.getHealth() <= 0.0) {
			playerState.bodyTemperature = 50;
			playerState.damageTick = 0;
		}

		effectTick = 0;
	}

	private static TemperatureDamageTuple temperatureDamageTupleForPlayerState(PlayerState playerState) {
		var bodyTemperature = playerState.bodyTemperature;

		if (bodyTemperature <= Mod.CONFIG.hypothermiaThresholdMinor
				&& bodyTemperature > Mod.CONFIG.hypothermiaThresholdMajor) {
			return new TemperatureDamageTuple(TemperatureDamageKind.COLD, TemperatureIntensityKind.MINOR);
		}

		if (bodyTemperature <= Mod.CONFIG.hypothermiaThresholdMajor) {
			return new TemperatureDamageTuple(TemperatureDamageKind.COLD, TemperatureIntensityKind.MAJOR);
		}

		if (bodyTemperature >= Mod.CONFIG.hyperthermiaThresholdMinor
				&& bodyTemperature < Mod.CONFIG.hyperthermiaThresholdMajor) {
			return new TemperatureDamageTuple(TemperatureDamageKind.HEAT, TemperatureIntensityKind.MINOR);
		}

		if (bodyTemperature >= Mod.CONFIG.hyperthermiaThresholdMajor) {
			return new TemperatureDamageTuple(TemperatureDamageKind.HEAT, TemperatureIntensityKind.MAJOR);
		}

		return null;
	}

}
