package net.saint.acclimatize.data.item;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.saint.acclimatize.Mod;
import net.saint.acclimatize.config.MapConfigCodingUtil;
import net.saint.acclimatize.library.WearableKind;

public final class ItemTemperatureUtil {

	// Properties

	private static HashMap<String, Double> armorItems;
	private static HashMap<String, Double> materials;

	private static HashMap<String, Double> cachedItemTemperatures = new HashMap<>();

	// Lifecycle

	public static void reloadItems() {
		armorItems = allArmorItemsFromConfig();
		materials = allMaterialsFromConfig();
		cachedItemTemperatures = new HashMap<>();
	}

	// Temperature

	public static double temperatureDeltaForAllArmorItems(ServerPlayerEntity player) {
		var temperature = 0f;

		for (var itemStack : player.getArmorItems()) {
			var temperatureValue = temperatureValueForItem(itemStack);
			temperature += temperatureValue * Mod.CONFIG.itemTemperatureFactor;
		}

		return temperature;
	}

	public static double temperatureValueForItem(ItemStack itemStack) {
		var itemId = itemStack.getTranslationKey();

		if (cachedItemTemperatures.containsKey(itemId)) {
			return cachedItemTemperatures.get(itemId);
		}

		var configTemperatureValue = temperatureValueForItemFromConfig(itemStack);

		if (configTemperatureValue != 0.0) {
			cachedItemTemperatures.put(itemId, configTemperatureValue);
			return configTemperatureValue;
		}

		var autoTemperatureValue = temperatureValueForItemFromAutoAssignment(itemStack);
		cachedItemTemperatures.put(itemId, autoTemperatureValue);

		return autoTemperatureValue;
	}

	// Acclimatization Rate

	public static double acclimatizationRateDeltaForItemTemperature(double temperatureDelta) {
		return ((Math.abs(temperatureDelta) / 10.0) * Mod.CONFIG.itemAcclimatizationRateFactor);
	}

	// Temperature (Auto-Assignment)

	private static double temperatureValueForItemFromAutoAssignment(ItemStack itemStack) {
		var itemKind = wearableKindForItem(itemStack);

		if (itemKind == null) {
			return 0.0;
		}

		var itemKindTemperatureFactor = temperatureFactorForItemKind(itemKind);
		var materialTemperature = temperatureValueForMaterial(itemStack);

		return Math.round(materialTemperature * itemKindTemperatureFactor * 10.0) / 10.0;
	}

	private static double temperatureFactorForItemKind(WearableKind itemKind) {
		switch (itemKind) {
		case BOOTS:
			return Mod.CONFIG.bootsAutoTemperatureFactor;
		case LEGGINGS:
			return Mod.CONFIG.leggingsAutoTemperatureFactor;
		case CHESTPLATE:
			return Mod.CONFIG.chestplateAutoTemperatureFactor;
		case HELMET:
			return Mod.CONFIG.helmetAutoTemperatureFactor;
		default:
			return 1.0;
		}
	}

	private static double temperatureValueForMaterial(ItemStack itemStack) {
		var itemId = itemStack.getTranslationKey();

		for (var materialId : materials.keySet()) {
			if (!itemId.contains(materialId)) {
				continue;
			}

			var materialTemperature = materials.get(materialId);
			return materialTemperature;
		}

		return 0.0;
	}

	// Temperature (Config)

	private static double temperatureValueForItemFromConfig(ItemStack itemStack) {
		var itemId = itemStack.getTranslationKey();

		if (!armorItems.containsKey(itemId)) {
			return 0.0;
		}

		var itemTemperature = armorItems.get(itemId);
		var itemWoolTemperature = woolValueForItem(itemStack);

		return itemTemperature + itemWoolTemperature;
	}

	// Item Analysis

	private static WearableKind wearableKindForItem(ItemStack itemStack) {
		var itemId = itemStack.getTranslationKey();

		if (itemId.contains("_boots") || itemId.contains("_feet")) {
			return WearableKind.BOOTS;
		} else if (itemId.contains("_leggings") || itemId.contains("_legs") || itemId.contains("_pants")) {
			return WearableKind.LEGGINGS;
		} else if (itemId.contains("_chestplate") || itemId.contains("_chest") || itemId.contains("_tunic")) {
			return WearableKind.CHESTPLATE;
		} else if (itemId.contains("_helmet") || itemId.contains("_head") || itemId.contains("_hood") || itemId.contains("_mask")) {
			return WearableKind.HELMET;
		}

		return null;
	}

	// NBT Access

	private static double woolValueForItem(ItemStack item) {
		return (double) item.getNbt().getInt("wool");
	}

	// Config Access

	private static HashMap<String, Double> allArmorItemsFromConfig() {
		return MapConfigCodingUtil.decodeDoubleValueMapFromRaw(Mod.CONFIG.wornTemperatureItems);
	}

	private static HashMap<String, Double> allMaterialsFromConfig() {
		return MapConfigCodingUtil.decodeDoubleValueMapFromRaw(Mod.CONFIG.materialAutoTemperature);
	}

}
