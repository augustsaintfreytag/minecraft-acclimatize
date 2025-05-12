package net.saint.acclimatize.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.saint.acclimatize.mixinlogic.ParticleMixinLogic;

// Code originally ported from *Immersive Winds* by Vibzz.

@Mixin(Particle.class)
public abstract class ParticleMixin implements ParticleMixinLogic {

	// Properties

	@Shadow
	protected double x;

	@Shadow
	protected double y;

	@Shadow
	protected double z;

	@Final
	@Shadow
	protected ClientWorld world;

	@Unique
	private double heatValue = 0.0;

	// Logic

	@Override
	public double getX() {
		return this.x;
	}

	@Override
	public double getY() {
		return this.y;
	}

	@Override
	public double getZ() {
		return this.z;
	}

	@Override
	public void addY(double motionY) {
		this.y += motionY;
	}

	@Override
	public ClientWorld getWorld() {
		return this.world;
	}

	@Override
	public double getHeatValue() {
		return this.heatValue;
	}

	@Override
	public void setHeatValue(double value) {
		this.heatValue = value;
	}

	@ModifyVariable(method = "move(DDD)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private double modifyDx(double dx) {
		return this.calculateDeltaX(dx);
	}

	@ModifyVariable(method = "move(DDD)V", at = @At("HEAD"), ordinal = 1, argsOnly = true)
	private double modifyDy(double dy) {
		return dy;
	}

	@ModifyVariable(method = "move(DDD)V", at = @At("HEAD"), ordinal = 2, argsOnly = true)
	private double modifyDz(double dz) {
		return this.calculateDeltaZ(dz);
	}
}