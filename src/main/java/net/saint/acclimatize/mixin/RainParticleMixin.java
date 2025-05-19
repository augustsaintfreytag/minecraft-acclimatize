package net.saint.acclimatize.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.saint.acclimatize.mixinlogic.RainParticleMixinLogic;
import pigcart.particlerain.particle.RainParticle;
import pigcart.particlerain.particle.WeatherParticle;

@Environment(EnvType.CLIENT)
@Mixin(RainParticle.class)
public abstract class RainParticleMixin extends WeatherParticle implements RainParticleMixinLogic {

	// Init

	private RainParticleMixin(ClientWorld world, double x, double y, double z) {
		super(world, x, y, z);
	}

	// Injections

	@Inject(method = "<init>", at = @At("TAIL"))
	private void mixinInit(ClientWorld world, double x, double y, double z,
			CallbackInfo callbackInfo) {
		var values = windAffectedVelocityForParticle((RainParticle) (Object) this);

		this.velocityX = values.velocityX;
		this.velocityY = values.velocityY;
		this.angle = (float) values.angle;
	}

}