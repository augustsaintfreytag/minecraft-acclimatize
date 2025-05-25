package net.saint.acclimatize.util;

import java.util.HashMap;
import java.util.Map;

public final class ConfigCodingUtil {

	// Library

	@FunctionalInterface
	public interface ValueDecoder<T> {
		T decode(String rawValue);
	}

	@FunctionalInterface
	public interface ValueEncoder<T> {
		String encode(T value);
	}

	// Typed Coding

	public static HashMap<String, Double> decodeDoubleValueMapFromRaw(String rawString) {
		return decodeValueMapFromRaw(rawString, ConfigCodingUtil::decodeDoubleValue);
	}

	public static String encodeDoubleValueMapToRaw(Map<String, Double> map) {
		return encodeValueMapToRaw(map, ConfigCodingUtil::encodeDoubleValue);
	}

	// Map Coding

	public static <T> HashMap<String, T> decodeValueMapFromRaw(String rawString, ValueDecoder<T> valueDecoder) {
		// Encoded form: minecraft:fire = 3.0, minecraft:lava = 2.0

		var map = new HashMap<String, T>();
		var entries = rawString.split(", ");

		for (var entry : entries) {
			var parts = entry.split(" = ");

			if (parts.length == 2) {
				var key = parts[0].trim();
				var value = valueDecoder.decode(parts[1].trim());
				map.put(key, value);
			}
		}

		return map;
	}

	public static <T> String encodeValueMapToRaw(Map<String, T> map, ValueEncoder<T> valueEncoder) {
		var entries = new StringBuilder();

		for (var entry : map.entrySet()) {
			entries.append(entry.getKey()).append(" = ").append(valueEncoder.encode(entry.getValue())).append(", ");
		}

		if (entries.length() > 0) {
			// Remove the last comma and space
			entries.setLength(entries.length() - 2);
		}

		return entries.toString();
	}

	// Numeric/Double Value Coding

	public static Double decodeDoubleValue(String rawValue) {
		return Double.parseDouble(rawValue);
	}

	public static String encodeDoubleValue(Double value) {
		return value.toString();
	}

}
