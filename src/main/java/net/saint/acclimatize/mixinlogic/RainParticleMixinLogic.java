package net.saint.acclimatize.mixinlogic;

import net.saint.acclimatize.Mod;
import net.saint.acclimatize.ModClient;
import net.saint.acclimatize.util.MathUtil;
import pigcart.particlerain.particle.RainParticle;

public interface RainParticleMixinLogic {

	public static class ValueTuple {
		public double velocityX;
		public double velocityY;
		public double angle;

		public ValueTuple(double velocityX, double velocityY, double angle) {
			this.velocityX = velocityX;
			this.velocityY = velocityY;
			this.angle = angle;
		}
	}

	public default ValueTuple windAffectedVelocityForParticle(RainParticle particle) {
		// Fetch our mod's wind parameters
		var windDirection = ModClient.getWindDirection(); // radians, 0 = north
		var windIntensity = ModClient.getWindIntensity(); // e.g. 0..6 scale
		var effectFactor = Mod.CONFIG.particleWeatherEffectFactor;

		// Compute horizontal velocities based on wind direction and intensity
		var vx = effectFactor * windIntensity * MathUtil.cos(windDirection);
		var vz = effectFactor * windIntensity * MathUtil.sin(windDirection);

		// Recompute angle so the particle quad faces into the wind
		var angle = windDirection + Math.PI / 2;

		return new ValueTuple(vx, vz, angle);
	}

}
