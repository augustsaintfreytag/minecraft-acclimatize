package net.saint.acclimatize.util;

public final class MathUtil {

	public static double approximateSin(double x) {
		// range‐reduced to [‑π,π], then:
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

}
