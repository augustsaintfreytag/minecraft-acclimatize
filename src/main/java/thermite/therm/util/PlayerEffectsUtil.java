package thermite.therm.util;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import thermite.therm.ThermMod;
import thermite.therm.effect.ThermStatusEffects;
import thermite.therm.player.PlayerState;

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
		public int duration;

		public TemperatureDamageTuple(TemperatureDamageKind kind, TemperatureIntensityKind intensity, int duration) {
			this.kind = kind;
			this.intensity = intensity;
			this.duration = duration;
		}
	}

	// Handling

	public static void handlePlayerEffects(ServerPlayerEntity player, PlayerState playerState) {
		// TODO: Check for resistance status effects or armor items.

		if (playerState.damageTick < playerState.damageTickDuration) {
			playerState.damageTick++;
			return;
		}

		if (playerState.damageTick >= playerState.damageTickDuration) {
			playerState.damageTick = 0;
			// May apply damage, can apply and set ticks for re-arm below.
		}

		var temperatureDamageTuple = temperatureDamageTupleForPlayerState(playerState);

		if (temperatureDamageTuple == null) {
			return;
		}

		if (temperatureDamageTuple.kind == TemperatureDamageKind.COLD) {
			if (temperatureDamageTuple.intensity == TemperatureIntensityKind.MINOR) {
				var hypothermiaStatusEffect = new StatusEffectInstance(ThermStatusEffects.HYPOTHERMIA, 600, 0);
				player.addStatusEffect(hypothermiaStatusEffect);
			} else {
				var hypothermiaStatusEffect = new StatusEffectInstance(ThermStatusEffects.HYPOTHERMIA, 600, 1);
				player.addStatusEffect(hypothermiaStatusEffect);
			}

			playerState.damageTick = 0;
			playerState.damageTickDuration = ThermMod.CONFIG.temperatureDamageInterval;

		} else if (temperatureDamageTuple.kind == TemperatureDamageKind.HEAT) {
			if (temperatureDamageTuple.intensity == TemperatureIntensityKind.MINOR) {
				var hyperthermiaStatusEffect = new StatusEffectInstance(ThermStatusEffects.HYPERTHERMIA, 600, 0);
				player.addStatusEffect(hyperthermiaStatusEffect);
			} else {
				var hyperthermiaStatusEffect = new StatusEffectInstance(ThermStatusEffects.HYPERTHERMIA, 600, 1);
				player.addStatusEffect(hyperthermiaStatusEffect);
			}

			playerState.damageTick = 0;
			playerState.damageTickDuration = ThermMod.CONFIG.extremeTemperatureDamageInterval;
		}

		if (player.getHealth() <= 0.0) {
			playerState.bodyTemperature = 50;
			playerState.damageTick = 0;
		}
	}

	private static TemperatureDamageTuple temperatureDamageTupleForPlayerState(PlayerState playerState) {
		var bodyTemperature = playerState.bodyTemperature;

		if (bodyTemperature <= ThermMod.CONFIG.freezeThresholdMinor
				&& bodyTemperature > ThermMod.CONFIG.freezeThresholdMajor) {
			return new TemperatureDamageTuple(TemperatureDamageKind.COLD, TemperatureIntensityKind.MINOR,
					ThermMod.CONFIG.temperatureDamageInterval);
		}

		if (bodyTemperature <= ThermMod.CONFIG.freezeThresholdMajor) {
			return new TemperatureDamageTuple(TemperatureDamageKind.COLD, TemperatureIntensityKind.MAJOR,
					ThermMod.CONFIG.extremeTemperatureDamageInterval);
		}

		if (bodyTemperature >= ThermMod.CONFIG.burnThresholdMinor
				&& bodyTemperature < ThermMod.CONFIG.burnThresholdMajor) {
			return new TemperatureDamageTuple(TemperatureDamageKind.HEAT, TemperatureIntensityKind.MINOR,
					ThermMod.CONFIG.temperatureDamageInterval);
		}

		if (bodyTemperature >= ThermMod.CONFIG.burnThresholdMajor) {
			return new TemperatureDamageTuple(TemperatureDamageKind.HEAT, TemperatureIntensityKind.MAJOR,
					ThermMod.CONFIG.extremeTemperatureDamageInterval);
		}

		return null;
	}

}
