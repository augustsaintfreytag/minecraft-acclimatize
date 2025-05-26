package net.saint.acclimatize.config;

import java.util.HashMap;
import java.util.Map;

public final class MapConfigCodingUtil {

	// Typed Coding

	public static HashMap<String, Double> decodeDoubleValueMapFromRaw(String rawString) {
		return decodeValueMapFromRaw(rawString, ValueConfigCodingUtil::decodeDoubleValue);
	}

	public static String encodeDoubleValueMapToRaw(Map<String, Double> map) {
		return encodeValueMapToRaw(map, ValueConfigCodingUtil::encodeDoubleValue);
	}

	// Map Coding

	public static <T> HashMap<String, T> decodeValueMapFromRaw(String rawString, ValueConfigCodingUtil.ValueDecoder<T> valueDecoder) {
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

	public static <T> String encodeValueMapToRaw(Map<String, T> map, ValueConfigCodingUtil.ValueEncoder<T> valueEncoder) {
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

}
