package thermite.therm;

import java.util.HashMap;
import java.util.Map;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.ConfigOptions;

public class ThermConfig extends Config {

	// Seasons

	@ConfigEntry(comment = "(Experimental) A small built in season system that affects your temperature depending on the season. You can configure the length of each season in half seconds, (one minecraft day = 2400 half seconds).")
	public boolean enableSeasonSystem = false;

	@ConfigEntry(comment = "Length of spring (Default: 48000 half seconds = 20 days).")
	public long springSeasonLength = 48000;

	@ConfigEntry(comment = "Length of summer (Default 48000 half seconds = 20 days).")
	public long summerSeasonLength = 48000;

	@ConfigEntry(comment = "Length of fall (Default 48000 half seconds = 20 days).")
	public long fallSeasonLength = 48000;

	@ConfigEntry(comment = "Length of winter (Default 48000 half seconds = 20 days).")
	public long winterSeasonLength = 48000;

	@ConfigEntry(comment = "Not implemented.")
	public String startingSeason = "spring";

	@ConfigEntry(comment = "Multiplier for how much seasons affect your temperature.")
	public float seasonTemperatureExtremenessMultiplier = 1.0f;

	// Weather

	@ConfigEntry(comment = "(Experimental) Makes weather reflect the current season. If you enable this make sure to run (/gamerule doWeatherCycle false) to disable the vanilla weather cycle.")
	public boolean seasonalWeather = false;

	// HUD

	@ConfigEntry(comment = "X coordinate of temperature UI relative to its default position. (Default: 0)")
	public int temperatureXPos = 0;

	@ConfigEntry(comment = "Y coordinate of temperature UI relative to its default position. (Default: 0)")
	public int temperatureYPos = 0;

	@ConfigEntry(comment = "X coordinate of thermometer UI relative to its default position. (Default: 0)")
	public int thermometerXPos = 0;

	@ConfigEntry(comment = "Y coordinate of thermometer UI relative to its default position. (Default: 0)")
	public int thermometerYPos = 0;

	@ConfigEntry(comment = "Different styles for the temperature display. (options: gauge, glass_thermometer)")
	public String temperatureDisplayType = "glass_thermometer";

	@ConfigEntry(comment = "Whether or not temperature damage decreases your saturation. Beware disabling this makes it really easy to bypass temperature damage just by eating. (Default: true)")
	public boolean temperatureDamageDecreasesSaturation = true;

	@ConfigEntry(comment = "When enabled, being cold enough causes a blue outline effect. And being hot enough causes an orange one. (Default: true)")
	public boolean enableTemperatureVignette = true;

	@ConfigEntry(comment = "When enabled, particles will spawn showing the direction that the wind is flowing. More wind = more particles. (Default: true)")
	public boolean enableWindParticles = true;

	// Player

	@ConfigEntry(comment = "How quick your body temperature absorbs or loses heat to the environment. (Default: 1.0)")
	public double acclimatizationRate = 0.25;

	// Player Damage

	@ConfigEntry(comment = "Hyperthermia damage per 5 seconds. (Default: 1.0)")
	public double hyperthermiaDamage = 1.5;

	@ConfigEntry(comment = "Hypothermia damage per 5 seconds. (Default: 1.0)")
	public double hypothermiaDamage = 1.5;

	@ConfigEntry(comment = "Damage interval for hypothermia and hyperthermia in seconds (Default: 3)")
	public int temperatureDamageInterval = 3;

	@ConfigEntry(comment = "Damage interval for extreme hypothermia and hyperthermia in seconds (Default: 2)")
	public int extremeTemperatureDamageInterval = 2;

	// Item Effects

	@ConfigEntry(comment = "Multiplier for how much each level of fire protection cools you (Default: -1.0)")
	public double fireProtectionCoolingMultiplier = -1.0;

	@ConfigEntry(comment = "Multiplier for how much each level of cold protection warms you (Default: 1.0)")
	public double coldProtectionCoolingMultiplier = 1.0;

	@ConfigEntry(comment = "Duration of the cooling effect of ice water in ticks. (Default: 6000)")
	public int iceWaterEffectDuration = 6000;

	// Wearables Effects

	@ConfigEntry(comment = "Helmets that will change your temperature.")
	public Map<String, Double> helmetTemperatureItems = new HashMap<>() {
		{
			put("minecraft:leather_helmet", 1.0);
		}
	};

	@ConfigEntry(comment = "Chestplates that will change your temperature.")
	public Map<String, Double> chestplateTemperatureItems = new HashMap<>() {
		{
			put("minecraft:leather_chestplate", 3.0);
		}
	};

	@ConfigEntry(comment = "Leggings that will change your temperature.")
	public Map<String, Double> leggingTemperatureItems = new HashMap<>() {
		{
			put("minecraft:leather_leggings", 2.0);
		}
	};

	@ConfigEntry(comment = "Boots that will change your temperature.")
	public Map<String, Double> bootTemperatureItems = new HashMap<>() {
		{
			put("minecraft:leather_boots", 1.0);
		}
	};

	@ConfigEntry(comment = "Items that when held will change your temperature.")
	public Map<String, Double> heldTemperatureItems = new HashMap<>() {
		{
			put("minecraft:torch", 1.0);
			put("minecraft:lava_bucket", 1.0);
		}
	};

	@ConfigEntry(comment = "Blocks that will heat you up when near.")
	public Map<String, Double> heatingBlocks = new HashMap<>() {
		{
			put("minecraft:fire", 3.0);
			put("minecraft:lava", 2.0);
			put("minecraft:campfire", 15.0);
			put("minecraft:torch", 1.0);
			put("minecraft:wall_torch", 1.0);
			put("minecraft:soul_torch", 1.0);
			put("minecraft:soul_wall_torch", 3.0);
			put("minecraft:soul_campfire", 15.0);
			put("minecraft:lava_cauldron", 2.0);
			put("minecraft:furnace", 15.0);
			put("minecraft:blast_furnace", 15.0);
			put("minecraft:smoker", 15.0);
		}
	};

	@ConfigEntry(comment = "Blocks that will cool you down when near.")
	public Map<String, Double> coolingBlocks = new HashMap<>() {
		{
			put("minecraft:ice", -1.0);
			put("minecraft:packed_ice", -3.0);
			put("minecraft:blue_ice", -6.0);
			put("minecraft:powder_snow", -1.0);
		}
	};

	// Ambient Temperatures

	@ConfigEntry(comment = "Base temperature for frigid climates. (Default: 25.0)")
	public double frigidClimateTemperature = 25;

	@ConfigEntry(comment = "Base temperature for cold climates. (Default: 30.0)")
	public double coldClimateTemperature = 30;

	@ConfigEntry(comment = "Base temperature for temperate climates. (Default: 50.0)")
	public double temperateClimateTemperature = 50;

	@ConfigEntry(comment = "Base temperature for hot climates. (Default: 55.0)")
	public double hotClimateTemperature = 55;

	@ConfigEntry(comment = "Base temperature for arid climates. (Default: 70.0)")
	public double aridClimateTemperature = 70;

	// Damage Thresholds

	@ConfigEntry(comment = "First threshold for hypothermia, being below this you will start to freeze (Default: 35)")
	public double freezeThresholdMinor = 35;

	@ConfigEntry(comment = "Second threshold for hypothermia, being below this you will freeze faster. (Default: 25)")
	public double freezeThresholdMajor = 25;

	@ConfigEntry(comment = "First threshold for hyperthermia, being above this you will start to burn (Default: 65)")
	public double burnThresholdMinor = 65;

	@ConfigEntry(comment = "Second threshold for hyperthermia, being above this you will burn faster (Default: 75)")
	public double burnThresholdMajor = 75;

	// Wind

	@ConfigEntry(comment = "Disables or enables wind. (Default: true)")
	public boolean enableWind = true;

	@ConfigEntry(comment = "If disabled, wind will only be applied in the overworld. (Default: false)")
	public boolean multidimensionalWind = false;

	@ConfigEntry(comment = "Number of rays used in wind calculation. (Default: 10)")
	public int windRayCount = 32;

	@ConfigEntry(comment = "How many blocks long wind rays are. (Default: 32)")
	public int windRayLength = 32;

	// Init

	public ThermConfig() {
		super(ConfigOptions.mod(ThermMod.modid));
	}

}