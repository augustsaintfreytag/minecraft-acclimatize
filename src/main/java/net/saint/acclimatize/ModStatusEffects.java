package net.saint.acclimatize;

import net.minecraft.entity.effect.StatusEffect;
import net.saint.acclimatize.effect.HeatDissipationStatusEffect;
import net.saint.acclimatize.effect.HyperthermiaStatusEffect;
import net.saint.acclimatize.effect.HypothermiaStatusEffect;

public class ModStatusEffects {

	// Major Effects

	public static final StatusEffect HYPOTHERMIA = new HypothermiaStatusEffect();
	public static final StatusEffect HYPERTHERMIA = new HyperthermiaStatusEffect();

	// Item Effects

	public static final StatusEffect HEAT_DISSIPATION = new HeatDissipationStatusEffect();

}
