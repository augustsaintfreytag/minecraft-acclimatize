package net.saint.acclimatize.util;

public final class MathUtil {

	public static double approximateSin(double x) {
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

	public static double approximateCos(double x) {
		// cos(x)=sin(x+π/2)
		return approximateSin(x + Math.PI / 2);
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

	public static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}

}
