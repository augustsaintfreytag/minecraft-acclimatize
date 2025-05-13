package net.saint.acclimatize.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.saint.acclimatize.mixinlogic.ParticleMixinLogic;
import randommcsomethin.fallingleaves.particle.FallingLeafParticle;

// Code originally ported from *Immersive Winds* by Vibzz.

@Mixin(Particle.class)
public abstract class ParticleMixin implements ParticleMixinLogic {

	// Shadowed Properties

	@Shadow
	protected double x;

	@Shadow
	protected double y;

	@Shadow
	protected double z;

	@Shadow
	protected double velocityX;

	@Shadow
	protected double velocityY;

	@Shadow
	protected double velocityZ;

	@Final
	@Shadow
	protected ClientWorld world;

	// Injected Properties

	@Unique
	Vec3d lastPositionDelta = Vec3d.ZERO;

	@Unique
	private double heatValue = 0.0;

	// Logic

	@Shadow
	public Box getBoundingBox() {
		return null;
	}

	@Unique
	public Vec3d getPosition() {
		return new Vec3d(x, y, z);
	}

	@Unique
	public void setPosition(Vec3d position) {
		this.x = position.getX();
		this.y = position.getY();
		this.z = position.getZ();
	}

	@Unique
	public Vec3d getLastPositionDelta() {
		return lastPositionDelta;
	}

	@Unique
	public void setLastPositionDelta(Vec3d delta) {
		this.lastPositionDelta = delta;
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
	private double mixinMoveDx(double dx) {
		return this.calculateDeltaX(dx);
	}

	@ModifyVariable(method = "move(DDD)V", at = @At("HEAD"), ordinal = 1, argsOnly = true)
	private double mixinMoveDy(double dy) {
		return dy;
	}

	@ModifyVariable(method = "move(DDD)V", at = @At("HEAD"), ordinal = 2, argsOnly = true)
	private double mixinMoveDz(double dz) {
		return this.calculateDeltaZ(dz);
	}

	@ModifyVariable(method = "setBoundingBox", at = @At("HEAD"), argsOnly = true)
	private Box mixinSetBoundingBox(Box box) {
		if ((Object) this instanceof FallingLeafParticle) {
			this.setBoundingBoxAndCachePosition(box);
		}

		return box;
	}

	@Inject(method = "repositionFromBoundingBox", at = @At("HEAD"), cancellable = true)
	private void mixinRepositionFromBoundingBox(CallbackInfo callbackInfo) {
		if ((Object) this instanceof FallingLeafParticle) {
			setPositionFromLastPositionDelta();
		}
	}
}