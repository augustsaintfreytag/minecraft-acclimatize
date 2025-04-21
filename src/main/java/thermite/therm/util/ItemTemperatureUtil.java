package thermite.therm.util;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import thermite.therm.ThermMod;

public final class ItemTemperatureUtil {

	private static HashMap<String, Double> armorItems = allArmorItemsFromConfig();

	public static double temperatureDeltaForAllArmorItems(ServerPlayerEntity player) {
		var temperature = 0f;

		for (var itemStack : player.getArmorItems()) {
			var temperatureValue = temperatureValueForItem(itemStack);
			temperature += temperatureValue;
		}

		return temperature;
	}

	public static double temperatureValueForItem(ItemStack itemStack) {
		var itemId = itemStack.getTranslationKey();

		if (!armorItems.containsKey(itemId)) {
			return 0.0;
		}

		var itemTemperature = armorItems.get(itemId);
		var itemWoolTemperature = woolValueForItem(itemStack);

		return itemTemperature + itemWoolTemperature;
	}

	private static double woolValueForItem(ItemStack item) {
		return (double) item.getNbt().getInt("wool");
	}

	private static HashMap<String, Double> allArmorItemsFromConfig() {
		var allArmorItems = new HashMap<String, Double>();

		allArmorItems.putAll(ThermMod.CONFIG.bootTemperatureItems);
		allArmorItems.putAll(ThermMod.CONFIG.helmetTemperatureItems);
		allArmorItems.putAll(ThermMod.CONFIG.chestplateTemperatureItems);
		allArmorItems.putAll(ThermMod.CONFIG.leggingTemperatureItems);

		return allArmorItems;
	}

}
