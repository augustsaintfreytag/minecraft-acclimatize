package net.saint.acclimatize.util;

import java.util.HashMap;
import java.util.HashSet;
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

	public static HashSet<String> decodeStringValueSetFromRaw(String rawString) {
		return decodeValueSetFromRaw(rawString, ConfigCodingUtil::decodeStringValue);
	}

	public static String encodeStringValueSetToRaw(HashSet<String> set) {
		return encodeValueSetToRaw(set, ConfigCodingUtil::encodeStringValue);
	}

	// Map Coding

	public static <T> HashMap<String, T> decodeValueMapFromRaw(String rawString, ValueDecoder<T> valueDecoder) {
		// Encoded form: minecraft:fire = 3.0, minecraft:lava = 2.0

		var map = new HashMap<String, T>();
		var entries = rawString.split(",");

		for (var entry : entries) {
			var parts = entry.split("=");

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

	// Set Coding

	public static <T> HashSet<T> decodeValueSetFromRaw(String rawString, ValueDecoder<T> valueDecoder) {
		// Encoded form: minecraft:stone, minecraft:oak_leaves

		var set = new HashSet<T>();
		var entries = rawString.split(",");

		for (var entry : entries) {
			var trimmedEntry = entry.trim();
			if (!trimmedEntry.isEmpty()) {
				var value = valueDecoder.decode(trimmedEntry);
				set.add(value);
			}
		}

		return set;
	}

	public static <T> String encodeValueSetToRaw(HashSet<T> set, ValueEncoder<T> valueEncoder) {
		var entries = new StringBuilder();

		for (var value : set) {
			entries.append(valueEncoder.encode(value)).append(", ");
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

	// String Value Coding

	public static String decodeStringValue(String rawValue) {
		return rawValue;
	}

	public static String encodeStringValue(String value) {
		return value;
	}

}
