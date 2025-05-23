package net.saint.acclimatize.compat;

public class FallingLeafParticleCompat {

	public static final Class<?> FALLING_LEAF_PARTICLE = initFallingLeafParticleClass();

	private static Class<?> initFallingLeafParticleClass() {
		try {
			return Class.forName("randommcsomethin.fallingleaves.particle.FallingLeafParticle");
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static boolean isLeafParticle(Object obj) {
		return FALLING_LEAF_PARTICLE != null && FALLING_LEAF_PARTICLE.isInstance(obj);
	}

}
