package thermite.therm.effect;

import java.util.UUID;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class HypothermiaStatusEffect extends StatusEffect {

	// Properties

	// #4e64ca
	private static final int color = -11639606;

	private static final UUID movementSpeedEffectId = UUID.fromString("63C5A824-8303-4DB8-B7C9-A0ED0BF22A9D");
	private static final UUID attackWeaknessEffectId = UUID.fromString("1B4C06C7-F1F2-466C-8323-929F9C0FF3AD");

	private static final double movementSpeedIntensity = -0.15;
	private static final double attackWeakIntensity = -1.0;

	// Init

	public HypothermiaStatusEffect() {
		super(StatusEffectCategory.HARMFUL, color);

		this.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, movementSpeedEffectId.toString(),
				movementSpeedIntensity,
				Operation.MULTIPLY_TOTAL);

		this.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackWeaknessEffectId.toString(),
				attackWeakIntensity,
				Operation.ADDITION);
	}

	// Effects

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		int interval = 40 >> amplifier;
		return interval > 0 ? duration % interval == 0 : true;
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
	}

	private static void applyFreezeDamage(LivingEntity entity, int amplifier) {
		var world = entity.getWorld();
		var damageSources = world.getDamageSources();

		entity.damage(damageSources.freeze(), 1.0F + amplifier * 0.5F);
	}

}
