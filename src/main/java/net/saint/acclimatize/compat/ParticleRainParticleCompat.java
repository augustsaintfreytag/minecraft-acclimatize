package net.saint.acclimatize.compat;

public class ParticleRainParticleCompat {

	public static final Class<?> RAIN_PARTICLE = safeInitClass("pigcart.particlerain.particle.RainParticle");
	public static final Class<?> DUST_PARTICLE = safeInitClass("pigcart.particlerain.particle.DustParticle");
	public static final Class<?> FOG_PARTICLE = safeInitClass("pigcart.particlerain.particle.FogParticle");

	// Init

	private static Class<?> safeInitClass(String identifier) {
		try {
			return Class.forName(identifier);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	// Convenience

	public static boolean isRainParticle(Object obj) {
		return RAIN_PARTICLE != null && RAIN_PARTICLE.isInstance(obj);
	}

	public static boolean isDustParticle(Object obj) {
		return DUST_PARTICLE != null && DUST_PARTICLE.isInstance(obj);
	}

	public static boolean isFogParticle(Object obj) {
		return FOG_PARTICLE != null && FOG_PARTICLE.isInstance(obj);
	}

}
