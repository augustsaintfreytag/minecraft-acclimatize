package thermite.therm;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = ThermMod.modid)
@Config.Gui.Background("minecraft:textures/block/ice.png")
public class ThermConfig implements ConfigData {

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

	// Player

	@ConfigEntry.Category("player")
	@Comment("First threshold for hypothermia, being below this you will start to freeze (Default: 35)")
	public double freezeThresholdMinor = 35.0;

	@ConfigEntry.Category("player")
	@Comment("Second threshold for hypothermia, being below this you will freeze faster. (Default: 25)")
	public double freezeThresholdMajor = 25.0;

	@ConfigEntry.Category("player")
	@Comment("First threshold for hyperthermia, being above this you will start to burn (Default: 65)")
	public double burnThresholdMinor = 65.0;

	@ConfigEntry.Category("player")
	@Comment("Second threshold for hyperthermia, being above this you will burn faster (Default: 75)")
	public double burnThresholdMajor = 75.0;

	@ConfigEntry.Category("player")
	@Comment("How quick your body temperature absorbs or loses heat from/to the environment. (Default: 1.0)")
	public double acclimatizationRate = 1.0;

	@ConfigEntry.Category("player")
	@Comment("Hyperthermia damage per 5 seconds. (Default: 1.0)")
	public double hyperthermiaDamage = 1.0;

	@ConfigEntry.Category("player")
	@Comment("Hypothermia damage per 5 seconds. (Default: 1.0)")
	public double hypothermiaDamage = 1.0;

	@ConfigEntry.Category("player")
	@Comment("Damage interval for hypothermia and hyperthermia in seconds (Default: 3)")
	public int temperatureDamageInterval = 3;

	@ConfigEntry.Category("player")
	@Comment("Damage interval for extreme hypothermia and hyperthermia in seconds (Default: 2)")
	public int extremeTemperatureDamageInterval = 2;

	// Items

	@ConfigEntry.Category("items")
	@Comment("Multiplier for how much each level of fire protection cools you (Default: -1.0)")
	public double fireProtectionCoolingFactor = -1.0;

	@ConfigEntry.Category("items")
	@Comment("Multiplier for how much each level of cold protection warms you (Default: 1.0)")
	public double coldProtectionCoolingFactor = 1.0;

	@ConfigEntry.Category("items")
	@Comment("Duration of the cooling effect of ice water drinks in ticks. (Default: 6000)")
	public int iceWaterEffectDuration = 6000;

	@ConfigEntry.Category("items")
	@Comment("Helmets that will change your temperature.")
	public String helmetTemperatureItems = String.join(", ",
			"minecraft:leather_helmet = 1.0");

	@ConfigEntry.Category("items")
	@Comment("Chestplates that will change your temperature.")
	public String chestplateTemperatureItems = String.join(", ",
			"minecraft:leather_chestplate = 3.0");

	@ConfigEntry.Category("items")
	@Comment("Leggings that will change your temperature.")
	public String leggingTemperatureItems = String.join(", ",
			"minecraft:leather_leggings = 2.0");

	@ConfigEntry.Category("items")
	@Comment("Boots that will change your temperature.")
	public String bootTemperatureItems = String.join(", ",
			"minecraft:leather_boots = 1.0");

	// Blocks

	@ConfigEntry.Category("blocks")
	@Comment("Items that when held will change your temperature.")
	public String heldTemperatureItems = String.join(", ",
			"minecraft:torch = 1.0",
			"minecraft:lava_bucket = 1.0");

	@ConfigEntry.Category("blocks")
	@Comment("Blocks that will heat you up when near.")
	public String heatingBlocks = String.join(", ",
			"minecraft:fire = 3.0",
			"minecraft:lava = 2.0",
			"minecraft:campfire = 15.0",
			"minecraft:torch = 1.0",
			"minecraft:wall_torch = 1.0",
			"minecraft:soul_torch = 1.0",
			"minecraft:soul_wall_torch = 3.0",
			"minecraft:soul_campfire = 15.0",
			"minecraft:lava_cauldron = 2.0",
			"minecraft:furnace = 15.0",
			"minecraft:blast_furnace = 15.0",
			"minecraft:smoker = 15.0");

	@ConfigEntry.Category("blocks")
	@Comment("Blocks that will cool you down when near.")
	public String coolingBlocks = String.join(", ",
			"minecraft:ice = -1.0",
			"minecraft:packed_ice = -3.0",
			"minecraft:blue_ice = -6.0",
			"minecraft:powder_snow = -1.0");

	// Ambient Temperatures

	@ConfigEntry.Category("environment")
	@Comment("Base temperature for frigid climates. (Default: 25.0)")
	public double frigidClimateTemperature = 25.0;

	@ConfigEntry.Category("environment")
	@Comment("Base temperature for cold climates. (Default: 30.0)")
	public double coldClimateTemperature = 30.0;

	@ConfigEntry.Category("environment")
	@Comment("Base temperature for temperate climates. (Default: 50.0)")
	public double temperateClimateTemperature = 50.0;

	@ConfigEntry.Category("environment")
	@Comment("Base temperature for hot climates. (Default: 55.0)")
	public double hotClimateTemperature = 55.0;

	@ConfigEntry.Category("environment")
	@Comment("Base temperature for arid climates. (Default: 70.0)")
	public double aridClimateTemperature = 70.0;

	// Wind

	@ConfigEntry.Category("wind")
	@Comment("Disables or enables wind. (Default: true)")
	public boolean enableWind = true;

	@ConfigEntry.Category("wind")
	@Comment("If disabled, wind will only be applied in the overworld. (Default: false)")
	public boolean multidimensionalWind = false;

	@ConfigEntry.Category("wind")
	@Comment("Number of rays used in wind calculation. (Default: 10)")
	public int windRayCount = 32;

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