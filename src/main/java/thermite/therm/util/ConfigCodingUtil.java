package thermite.therm.util;

import java.util.HashMap;
import java.util.Map;

public final class ConfigCodingUtil {

	public static Map<String, Double> decodeTemperatureMapFromRaw(String rawString) {
		// Encoded form: minecraft:fire = 3.0, minecraft:lava = 2.0

		var map = new HashMap<String, Double>();
		var entries = rawString.split(", ");

		for (var entry : entries) {
			var parts = entry.split(" = ");

			if (parts.length == 2) {
				var key = parts[0].trim();
				var value = Double.parseDouble(parts[1].trim());
				map.put(key, value);
			}
		}

		return map;
	}

	public static String encodeTemperatureMapToRaw(Map<String, Double> map) {
		var entries = new StringBuilder();

		for (var entry : map.entrySet()) {
			entries.append(entry.getKey()).append(" = ").append(entry.getValue()).append(", ");
		}

		if (entries.length() > 0) {
			entries.setLength(entries.length() - 2); // Remove the last comma and space
		}

		return entries.toString();
	}

}
