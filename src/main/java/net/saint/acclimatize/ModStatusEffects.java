package net.saint.acclimatize;

import net.minecraft.entity.effect.StatusEffect;
import net.saint.acclimatize.effect.ColdResistanceStatusEffect;
import net.saint.acclimatize.effect.HyperthermiaStatusEffect;
import net.saint.acclimatize.effect.HypothermiaStatusEffect;

public class ModStatusEffects {

	public static final StatusEffect COLD_RESISTANCE = new ColdResistanceStatusEffect();
	public static final StatusEffect HYPOTHERMIA = new HypothermiaStatusEffect();
	public static final StatusEffect HYPERTHERMIA = new HyperthermiaStatusEffect();

}
