package net.saint.acclimatize.util;

public final class MathUtil {

	// Trigonometric Approximations

	public static double sin(double x) {
		// Range reduction to [-π, π]
		x = x % (2 * Math.PI);

		if (x > Math.PI) {
			x -= 2 * Math.PI;
		}

		if (x < -Math.PI) {
			x += 2 * Math.PI;
		}

		// Bhaskara I's approximation
		return 1.27323954 * x - 0.405284735 * x * Math.abs(x);
	}

	public static double cos(double x) {
		// cos(x)=sin(x+π/2)
		return sin(x + Math.PI / 2);
	}

	// Clamp

	public static int clamp(int value, int min, int max) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		} else {
			return value;
		}
	}

	public static float clamp(float value, float min, float max) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		} else {
			return value;
		}
	}

	public static double clamp(double value, double min, double max) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		} else {
			return value;
		}
	}

	// Linear Interpolation

	public static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}

}
