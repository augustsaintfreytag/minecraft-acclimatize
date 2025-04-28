package net.saint.acclimatize;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = Mod.modid)
@Config.Gui.Background("minecraft:textures/block/ice.png")
public class ModConfig implements ConfigData {

	// HUD

	@ConfigEntry.Category("hud")
	@Comment("Horizontal offset of the temperature HUD element. (Default: 0)")
	public int temperatureXOffset = 0;

	@ConfigEntry.Category("hud")
	@Comment("Vertical offset of the temperature HUD element. (Default: 0)")
	public int temperatureYOffset = 0;

	@ConfigEntry.Category("hud")
	@Comment("Horizontal offset of the thermometer HUD element. (Default: 0)")
	public int thermometerXOffset = 0;

	@ConfigEntry.Category("hud")
	@Comment("Vertical offset of the thermometer HUD element. (Default: 0)")
	public int thermometerYOffset = 0;

	@ConfigEntry.Category("hud")
	@Comment("Different styles for the temperature display. (options: gauge, glass_thermometer)")
	public String temperatureDisplayType = "glass_thermometer";

	@ConfigEntry.Category("hud")
	@Comment("Whether or not temperature damage decreases your saturation. Beware disabling this makes it really easy to bypass temperature damage just by eating. (Default: true)")
	public boolean temperatureDamageDecreasesSaturation = true;

	@ConfigEntry.Category("hud")
	@Comment("When enabled, being cold enough causes a blue outline effect. And being hot enough causes an orange one. (Default: true)")
	public boolean enableTemperatureVignette = true;

	@ConfigEntry.Category("hud")
	@Comment("When enabled, particles will spawn showing the direction that the wind is flowing. More wind = more particles. (Default: true)")
	public boolean enableWindParticles = true;

	@ConfigEntry.Category("hud")
	@Comment("When enabled, holding a thermometer will show the exact measured ambient temperature. (Default: false)")
	public boolean enableThermometerTemperatureDisplay = false;

	// Player

	@ConfigEntry.Category("player")
	@Comment("First threshold for hypothermia, being below this you will start to freeze (Default: 35.0)")
	public double freezeThresholdMinor = 35.0;

	@ConfigEntry.Category("player")
	@Comment("Second threshold for hypothermia, being below this you will freeze faster. (Default: 20.0)")
	public double freezeThresholdMajor = 20.0;

	@ConfigEntry.Category("player")
	@Comment("First threshold for hyperthermia, being above this you will start to burn (Default: 65.0)")
	public double burnThresholdMinor = 65.0;

	@ConfigEntry.Category("player")
	@Comment("Second threshold for hyperthermia, being above this you will burn faster (Default: 75.0)")
	public double burnThresholdMajor = 75.0;

	@ConfigEntry.Category("player")
	@Comment("How quick your body temperature absorbs or loses heat from/to the environment. (Default: 0.05)")
	public double acclimatizationRate = 0.05;

	@ConfigEntry.Category("player")
	@Comment("Hyperthermia damage per 5 seconds. (Default: 1.0)")
	public double hyperthermiaDamage = 1.0;

	@ConfigEntry.Category("player")
	@Comment("Hypothermia damage per 5 seconds. (Default: 1.0)")
	public double hypothermiaDamage = 1.0;

	@ConfigEntry.Category("player")
	@Comment("Damage interval for hypothermia and hyperthermia in network ticks (Default: 10)")
	public int temperatureDamageInterval = 10;

	@ConfigEntry.Category("player")
	@Comment("Damage interval for extreme hypothermia and hyperthermia in network ticks (Default: 10)")
	public int extremeTemperatureDamageInterval = 10;
	// World

	@ConfigEntry.Category("world")
	@Comment("Base temperature for frigid climates. (Default: 15.0)")
	public double frigidClimateTemperature = 15.0;

	@ConfigEntry.Category("world")
	@Comment("Base temperature for cold climates. (Default: 30.0)")
	public double coldClimateTemperature = 30.0;

	@ConfigEntry.Category("world")
	@Comment("Base temperature for temperate climates. (Default: 50.0)")
	public double temperateClimateTemperature = 50.0;

	@ConfigEntry.Category("world")
	@Comment("Base temperature for hot climates. (Default: 55.0)")
	public double hotClimateTemperature = 55.0;

	@ConfigEntry.Category("world")
	@Comment("Base temperature for arid climates. (Default: 70.0)")
	public double aridClimateTemperature = 70.0;

	@ConfigEntry.Category("world")
	@Comment("The temperature delta applied when the player is exposed to rainfall. (Default: -5.0)")
	public double rainTemperatureDelta = -5.0;

	@ConfigEntry.Category("world")
	@Comment("The temperature delta applied when the player is exposed to snowfall. (Default: -10.0)")
	public double snowTemperatureDelta = -10.0;

	// Blocks

	@ConfigEntry.Category("blocks")
	@Comment("The radius around the player in which blocks are checked for temperature effects. (Default: 8)")
	public int blockTemperatureRadius = 8;

	@ConfigEntry.Category("blocks")
	@Comment("The lowest possible value to be added to ambient temperatures based on blocks in the environment. (Default: -20.0")
	public double blockTemperatureAbsoluteMinimum = -20.0;

	@ConfigEntry.Category("blocks")
	@Comment("The highest possible value to be added to ambient temperatures based on blocks in the environment. (Default: 40.0)")
	public double blockTemperatureAbsoluteMaximum = 40.0;

	@ConfigEntry.Category("blocks")
	@Comment("The value added to distance under heat intensity used in the fall-off calculation. (Default: 0.1)")
	public double blockTemperatureFalloffConstant = 0.75;

	@ConfigEntry.Category("blocks")
	@Comment("The factor multiplied by the distance between player and heat source. Lower means less falloff. (Default: 0.5)")
	public double blockTemperatureDistanceFalloffFactor = 0.075;

	@ConfigEntry.Category("blocks")
	@Comment("The factor applied to base acclimatization rate when close to a block heat source. (Default: 0.5)")
	public double blockAcclimatizationBoostFactor = 2.5;

	@ConfigEntry.Category("blocks")
	@Comment("The minimum temperature at which the acclimatization boost is applied. (Default: 10.0)")
	public double blockTemperatureAcclimatizationBoostThreshold = 10.0;

	@ConfigEntry.Category("blocks")
	@Comment("Blocks that will heat you up when near.")
	public String heatingBlocks = String.join(", ",
			"minecraft:fire = 5.0",
			"minecraft:lava = 8.0",
			"minecraft:campfire = 20.0",
			"minecraft:torch = 2.0",
			"minecraft:wall_torch = 2.0",
			"minecraft:soul_torch = 2.0",
			"minecraft:soul_wall_torch = 2.0",
			"minecraft:soul_campfire = 20.0",
			"minecraft:lava_cauldron = 5.0",
			"minecraft:furnace = 15.0",
			"minecraft:blast_furnace = 15.0",
			"minecraft:smoker = 15.0",
			"minecraft:redstone_lamp = 2.0",
			"hardcore_torches:lit_torch = 2.0",
			"hardcore_torches:lit_wall_torch = 2.0",
			"hardcore_torches:lit_lantern = 1.0",
			"farmersdelight:stove = 15.0",
			"refurbished_furniture:light_ceiling_light = 10.0",
			"refurbished_furniture:dark_ceiling_light = 10.0",
			"refurbished_furniture:black_lamp = 10.0",
			"refurbished_furniture:blue_lamp = 10.0",
			"refurbished_furniture:brown_lamp = 10.0",
			"refurbished_furniture:cyan_lamp = 10.0",
			"refurbished_furniture:gray_lamp = 10.0",
			"refurbished_furniture:green_lamp = 10.0",
			"refurbished_furniture:light_blue_lamp = 10.0",
			"refurbished_furniture:light_gray_lamp = 10.0",
			"refurbished_furniture:lime_lamp = 10.0",
			"refurbished_furniture:magenta_lamp = 10.0",
			"refurbished_furniture:orange_lamp = 10.0",
			"refurbished_furniture:pink_lamp = 10.0",
			"refurbished_furniture:red_lamp = 10.0",
			"refurbished_furniture:white_lamp = 10.0",
			"refurbished_furniture:yellow_lamp = 10.0");

	@ConfigEntry.Category("blocks")
	@Comment("Blocks that will cool you down when near.")
	public String coolingBlocks = String.join(", ",
			"minecraft:ice = -0.5",
			"minecraft:packed_ice = -1.0",
			"minecraft:blue_ice = -2.0",
			"minecraft:snow_block = -0.5");

	// Items

	@ConfigEntry.Category("items")
	@Comment("Multiplier for how much each level of fire protection cools you (Default: -1.0)")
	public double fireProtectionCoolingFactor = -1.0;

	@ConfigEntry.Category("items")
	@Comment("Multiplier for how much each level of cold protection warms you (Default: 1.0)")
	public double coldProtectionCoolingFactor = 1.0;

	@ConfigEntry.Category("items")
	@Comment("Item temperature values that add to body temperature calculation when actively held.")
	public String heldTemperatureItems = String.join(", ",
			"minecraft:torch = 2.0",
			"minecraft:lava_bucket = 1.0",
			"hardcore_torches:lit_torch = 2.0",
			"hardcore_torches:lit_lantern = 1.0");

	@ConfigEntry.Category("items")
	@Comment("Item temperature values that add to body temperature calculation when worn.")
	public String wornTemperatureItems = String.join(", ",
			"");

	@ConfigEntry.Category("items")
	@Comment("Material temperature values used to auto-assign values to wearable items.")
	public String materialAutoTemperature = String.join(", ",
			"leather = 3.0",
			"iron = 2.0",
			"gold = 2.0",
			"diamond = 1.0",
			"netherite = 3.0",
			"chainmail = 1.0",
			"aeternium = 2.0",
			"brass = 1.0",
			"bronze = 1.0",
			"cincinnasite = 2.0",
			"copper = 1.0",
			"crystalite = 3.0",
			"flaming_ruby = 5.0",
			"nether_ruby = 4.0",
			"resonarium = 3.0",
			"rose_quartz = 2.0",
			"steel = 2.0",
			"sturdy = 3.0",
			"terminite = 3.0",
			"thallasium = 2.0",
			"warden = 3.0",
			"turtle = 1.0");

	@ConfigEntry.Category("items")
	@Comment("Temperature factor for helmets with auto-assigned material-based values. (Default: 0.5)")
	public double helmetAutoTemperatureFactor = 1.0;

	@ConfigEntry.Category("items")
	@Comment("Temperature factor for chestplates with auto-assigned material-based values. (Default: 1.0)")
	public double leggingsAutoTemperatureFactor = 2.0;

	@ConfigEntry.Category("items")
	@Comment("Temperature factor for leggings with auto-assigned material-based values. (Default: 1.25)")
	public double chestplateAutoTemperatureFactor = 3.0;

	@ConfigEntry.Category("items")
	@Comment("Temperature factor for boots with auto-assigned material-based values. (Default: 1.0)")
	public double bootsAutoTemperatureFactor = 1;

	@ConfigEntry.Category("items")
	@Comment("The factor for how much the raw temperature value of worn items adds to player temperature. (Default: 0.35)")
	public double itemTemperatureFactor = 0.35;

	@ConfigEntry.Category("items")
	@Comment("Factor for how much player acclimatization is affected by the temperature value of worn items. (Default: -0.005)")
	public double itemAcclimatizationRateFactor = -0.005;

	@ConfigEntry.Category("items")
	@Comment("The lowest possible value player acclimatization rate can be lowered to with worn items. (Default: 0.001)")
	public double itemAcclimatizationRateMinimum = 0.001;

	@ConfigEntry.Category("items")
	@Comment("Duration of the cooling effect of ice water drinks in ticks. (Default: 6000)")
	public int iceWaterEffectDuration = 6000;

	// Wind

	@ConfigEntry.Category("wind")
	@Comment("Disables or enables wind. (Default: true)")
	public boolean enableWind = true;

	@ConfigEntry.Category("wind")
	@Comment("If disabled, wind will only be applied in the overworld. (Default: false)")
	public boolean multidimensionalWind = false;

	@ConfigEntry.Category("wind")
	@Comment("The factor for how much wind exposure affects ambient temperature. (Default: 1.0)")
	public double windChillFactor = 1.0;

	@ConfigEntry.Category("wind")
	@Comment("Number of rays used in wind calculation. (Default: 16)")
	public int windRayCount = 16;

	@ConfigEntry.Category("wind")
	@Comment("How many blocks long wind rays are. (Default: 32)")
	public int windRayLength = 32;

	// Seasons

	@ConfigEntry.Category("seasons")
	@Comment("(Experimental) A small built in season system that affects your temperature depending on the season. You can configure the length of each season in half seconds, (one minecraft day = 2400 half seconds).")
	public boolean enableSeasonSystem = false;

	@ConfigEntry.Category("seasons")
	@Comment("Length of spring (Default: 48000 half seconds = 20 days).")
	public long springSeasonLength = 48000;

	@ConfigEntry.Category("seasons")
	@Comment("Length of summer (Default 48000 half seconds = 20 days).")
	public long summerSeasonLength = 48000;

	@ConfigEntry.Category("seasons")
	@Comment("Length of fall (Default 48000 half seconds = 20 days).")
	public long fallSeasonLength = 48000;

	@ConfigEntry.Category("seasons")
	@Comment("Length of winter (Default 48000 half seconds = 20 days).")
	public long winterSeasonLength = 48000;

	@ConfigEntry.Category("seasons")
	@Comment("Multiplier for how much seasons affect your temperature.")
	public float seasonTemperatureExtremenessFactor = 1.0f;

	@ConfigEntry.Category("seasons")
	@Comment("(Experimental) Makes weather reflect the current season. If you enable this make sure to run (/gamerule doWeatherCycle false) to disable the vanilla weather cycle.")
	public boolean seasonalWeather = false;

}