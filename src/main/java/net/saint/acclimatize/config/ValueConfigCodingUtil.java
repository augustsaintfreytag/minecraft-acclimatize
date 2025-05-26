package net.saint.acclimatize.config;

public final class ValueConfigCodingUtil {

	// Library

	@FunctionalInterface
	public interface ValueDecoder<T> {
		T decode(String rawValue);
	}

	@FunctionalInterface
	public interface ValueEncoder<T> {
		String encode(T value);
	}

	// Numeric/Double Value Coding

	public static Double decodeDoubleValue(String rawValue) {
		return Double.parseDouble(rawValue);
	}

	public static String encodeDoubleValue(Double value) {
		return value.toString();
	}

	// String Value Coding

	public static String decodeStringValue(String rawValue) {
		return rawValue;
	}

	public static String encodeStringValue(String value) {
		return value;
	}

}
