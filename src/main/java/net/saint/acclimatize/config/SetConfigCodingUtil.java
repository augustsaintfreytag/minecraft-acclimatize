package net.saint.acclimatize.config;

import java.util.HashSet;

public final class SetConfigCodingUtil {

	// Typed Coding

	public static HashSet<String> decodeStringValueSetFromRaw(String rawString) {
		return decodeValueSetFromRaw(rawString, ValueConfigCodingUtil::decodeStringValue);
	}

	public static String encodeStringValueSetToRaw(HashSet<String> set) {
		return encodeValueSetToRaw(set, ValueConfigCodingUtil::encodeStringValue);
	}

	// Set Coding

	public static <T> HashSet<T> decodeValueSetFromRaw(String rawString, ValueConfigCodingUtil.ValueDecoder<T> valueDecoder) {
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

	public static <T> String encodeValueSetToRaw(HashSet<T> set, ValueConfigCodingUtil.ValueEncoder<T> valueEncoder) {
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

}
