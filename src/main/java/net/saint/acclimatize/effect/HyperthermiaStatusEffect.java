package net.saint.acclimatize.effect;

import java.util.UUID;

import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class HyperthermiaStatusEffect extends StatusEffect {

	// Properties

	private static final int color = -613060;

	private static final UUID movementSpeedEffectId = UUID.fromString("63C5A824-8303-4DB8-B7C9-A0ED0BF22A9D");
	private static final UUID attackWeaknessEffectId = UUID.fromString("1B4C06C7-F1F2-466C-8323-929F9C0FF3AD");

	private static final double movementSpeedIntensity = -0.15;
	private static final double attackWeakIntensity = -1.0;

	// Init

	public HyperthermiaStatusEffect() {
		super(StatusEffectCategory.HARMFUL, color);

		this.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, movementSpeedEffectId.toString(),
				movementSpeedIntensity,
				Operation.MULTIPLY_TOTAL);

		this.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackWeaknessEffectId.toString(),
				attackWeakIntensity,
				Operation.ADDITION);
	}

}
