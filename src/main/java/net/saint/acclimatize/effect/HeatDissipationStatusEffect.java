package net.saint.acclimatize.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class HeatDissipationStatusEffect extends StatusEffect {
	public HeatDissipationStatusEffect() {
		super(StatusEffectCategory.NEUTRAL, 0x6bb0ff);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
	}

}