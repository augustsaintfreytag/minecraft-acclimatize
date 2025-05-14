package net.saint.acclimatize.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import randommcsomethin.fallingleaves.particle.FallingLeafParticle;

@Mixin(FallingLeafParticle.class)
public abstract class FallingLeafParticleMixin extends SpriteBillboardParticle {

	// Properties

	@Shadow
	protected float windCoefficient = 0;

	// Init

	private FallingLeafParticleMixin(ClientWorld world, double x, double y, double z) {
		super(world, x, y, z);
		this.windCoefficient = 0;
	}

	// Logic

	@Inject(method = "move", at = @At("TAIL"))
	public void move(double dx, double dy, double dz, CallbackInfo callbackInfo) {
		super.move(this.velocityX, this.velocityY, this.velocityZ);
	}

}
