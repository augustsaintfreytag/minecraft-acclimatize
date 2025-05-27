package net.saint.acclimatize;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = Mod.modId) @Config.Gui.Background("minecraft:textures/block/ice.png")
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
	@Comment("The opacity value used when rendering the temperature vignette. Can be used to fade or intensify the effect. (Default: 0.7)")
	public double temperatureVignetteAlpha = 0.7;

	@ConfigEntry.Category("hud")
	@Comment("The duration of the transition to show and hide the vignette overlay (in milliseconds). (Default: 1500)")
	public int temperatureVignetteTransitionDuration = 1500;

	@ConfigEntry.Category("hud")
	@Comment("When enabled, particles will spawn showing the direction that the wind is flowing. More wind = more particles. (Default: true)")
	public boolean enableWindParticles = false;

	@ConfigEntry.Category("hud")
	@Comment("When enabled, holding a thermometer will show the exact measured ambient temperature. (Default: false)")
	public boolean enableThermometerTemperatureDisplay = false;

	// Player

	@ConfigEntry.Category("player")
	@Comment("The rate by which body temperature adapts to ambient temperatures. (Default: 0.01)")
	public double acclimatizationRate = 0.01;

	@ConfigEntry.Category("player")
	@Comment("The lowest possible value for player acclimatization after all factors have been applied. (Default: 0.001)")
	public double acclimatizationRateMinimum = 0.001;

	@ConfigEntry.Category("player")
	@Comment("The factor by which player acclimatization rate is boosted when wet. (Default: 2.5)")
	public double wetAcclimatizationRateBoostFactor = 2.5;

	@ConfigEntry.Category("player")
	@Comment("First threshold for hypothermia, being below this you will start to freeze (Default: 35.0)")
	public double hypothermiaThresholdMinor = 35.0;

	@ConfigEntry.Category("player")
	@Comment("Second threshold for hypothermia, being below this you will freeze faster. (Default: 15.0)")
	public double hypothermiaThresholdMajor = 15.0;

	@ConfigEntry.Category("player")
	@Comment("First threshold for hyperthermia, being above this you will start to burn (Default: 65.0)")
	public double hyperthermiaThresholdMinor = 65.0;

	@ConfigEntry.Category("player")
	@Comment("Second threshold for hyperthermia, being above this you will burn faster (Default: 85.0)")
	public double hyperthermiaThresholdMajor = 85.0;

	// World

	@ConfigEntry.Category("world")
	@Comment("The number of ticks between temperature updates. (Default: 20)")
	public int temperatureTickInterval = 20;

	@ConfigEntry.Category("world")
	@Comment("Anchor value for zeroing the temperature value calculation from a biome's native temperature attribute. Increase causes global warming, decrease causes cooling. (Default: 1.0)")
	public double biomeTemperatureZeroingAnchor = 1.0;

	@ConfigEntry.Category("world")
	@Comment("The presumed sea level for the world, used to calculate altitude temperature deltas. Should be set to the median y coordinate players spend time at. (Default: 62)")
	public int altitudeZeroingAnchor = 62;

	@ConfigEntry.Category("world")
	@Comment("The final applied coefficient for altitude-based temperature deltas. At 1.0, y64 -> y0 would be 5 units warmer, y64 -> y128 would be 5 units colder. (Default: 1.0)")
	public double altitudeTemperatureFactor = 1.0;

	@ConfigEntry.Category("world")
	@Comment("Enables a raycast to check if the player is exposed to the sun or in shade. (Default: true)")
	public boolean enableSunShadeCheck = true;

	@ConfigEntry.Category("world")
	@Comment("The temperature delta applied when the player is in shade from the sun. (Default: -6.0)")
	public double sunShadeTemperatureDelta = -6.0;

	@ConfigEntry.Category("world")
	@Comment("The factor of biome base temperature applied to the sun shade temperature delta. 0.0 means all biomes are equal. 0.0-1.0+ means hotter biomes provide stronger shade. (Default: 0.75)")
	public double sunShadeBiomeTemperatureFactor = 0.75;

	@ConfigEntry.Category("world")
	@Comment("The temperature delta applied to biome ambient temperature when it is raining. (Default: -4.0)")
	public double rainTemperatureDelta = -4.0;

	@ConfigEntry.Category("world")
	@Comment("The temperature delta applied to biome ambient temperature when it is snowing. (Default: -6.0)")
	public double snowTemperatureDelta = -6.0;

	@ConfigEntry.Category("world")
	@Comment("The temperature delta gradually applied between sunset and sunrise. (Default: -10.0)")
	public double nightTemperatureDelta = -10.0;

	@ConfigEntry.Category("world")
	@Comment("The raw biome temperature value of all nether biomes. (Between -2.0 and +2.0) (Default: 1.8)")
	public double netherBiomeTemperature = 1.8;

	@ConfigEntry.Category("world")
	@Comment("The raw biome temperature value of all end biomes. (Between -2.0 and +2.0) (Default: 0.5)")
	public double endBiomeTemperature = -0.7;

	@ConfigEntry.Category("world")
	@Comment("The number of rays cast in a cone shape around the player to check for an interior space. Higher number means more precision but higher cost. (Default: 12)")
	public int spaceNumberOfRays = 12;

	@ConfigEntry.Category("world")
	@Comment("The length of the rays cast around the player to check for an interior space. Higher means more coverage but higher cost. (Default: 32)")
	public int spaceRayLength = 32;

	@ConfigEntry.Category("world")
	@Comment("The number of ticks in a single day that the sun is out (dawn to dusk). Default is vanilla. (Default: 12000)")
	public int daylightTicks = 12000;

	@ConfigEntry.Category("world")
	@Comment("The number of ticks in a single day that the moon is out (dusk to dawn). Default is vanilla. (Default: 12000)")
	public int nighttimeTicks = 12000;

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
	@Comment("The value added to distance under heat intensity used in the fall-off calculation. (Default: 0.75)")
	public double blockTemperatureFalloffConstant = 0.75;

	@ConfigEntry.Category("blocks")
	@Comment("The factor multiplied by the distance between player and heat source. Lower means less falloff. (Default: 0.075)")
	public double blockTemperatureDistanceFalloffFactor = 0.075;

	@ConfigEntry.Category("blocks")
	@Comment("The factor applied to base acclimatization rate when close to a block heat source. (Default: 5.0)")
	public double blockAcclimatizationBoostFactor = 5.0;

	@ConfigEntry.Category("blocks")
	@Comment("The minimum temperature at which the acclimatization boost is applied. (Default: 10.0)")
	public double blockTemperatureAcclimatizationBoostThreshold = 10.0;

	@ConfigEntry.Category("blocks")
	@Comment("Temperature value applied when the player is submerged in water. (Default: -15.0)")
	public double waterBlockTemperature = -15.0;

	@ConfigEntry.Category("blocks")
	@Comment("Blocks that will heat you up when near.")
	public String heatingBlocks = String.join(", ", "minecraft:fire = 5.0", "minecraft:lava = 8.0", "minecraft:campfire = 18.0",
			"minecraft:torch = 4.0", "minecraft:wall_torch = 4.0", "minecraft:redstone_torch = 1.0", "minecraft:redstone_wall_torch = 1.0",
			"minecraft:soul_torch = 4.0", "minecraft:soul_wall_torch = 4.0", "minecraft:soul_campfire = 18.0",
			"minecraft:lava_cauldron = 5.0", "minecraft:furnace = 10.0", "minecraft:blast_furnace = 10.0", "minecraft:smoker = 10.0",
			"minecraft:redstone_lamp = 2.0", "minecraft:ochre_froglight = 3.0", "minecraft:pearlescent_froglight = 3.0",
			"minecraft:verdant_froglight = 3.0", "minecraft:end_rod = 2.0", "hardcore_torches:lit_torch = 4.0",
			"hardcore_torches:lit_wall_torch = 4.0", "hardcore_torches:lit_lantern = 2.0", "farmersdelight:stove = 18.0",
			"refurbished_furniture:light_ceiling_light = 10.0", "refurbished_furniture:dark_ceiling_light = 10.0",
			"refurbished_furniture:black_lamp = 10.0", "refurbished_furniture:blue_lamp = 10.0", "refurbished_furniture:brown_lamp = 10.0",
			"refurbished_furniture:cyan_lamp = 10.0", "refurbished_furniture:gray_lamp = 10.0", "refurbished_furniture:green_lamp = 10.0",
			"refurbished_furniture:light_blue_lamp = 10.0", "refurbished_furniture:light_gray_lamp = 10.0",
			"refurbished_furniture:lime_lamp = 10.0", "refurbished_furniture:magenta_lamp = 10.0",
			"refurbished_furniture:orange_lamp = 10.0", "refurbished_furniture:pink_lamp = 10.0", "refurbished_furniture:red_lamp = 10.0",
			"refurbished_furniture:white_lamp = 10.0", "refurbished_furniture:yellow_lamp = 10.0");

	@ConfigEntry.Category("blocks")
	@Comment("Blocks that will cool you down when near.")
	public String coolingBlocks = String.join(", ", "minecraft:ice = -0.1", "minecraft:packed_ice = -0.5", "minecraft:blue_ice = -0.75",
			"minecraft:snow_block = -0.01");

	@ConfigEntry.Category("blocks")
	@Comment("Blocks that wind can pass through.")
	public String windPermeableBlocks = String.join(", ", "minecraft:oak_leaves", "minecraft:spruce_leaves", "minecraft:birch_leaves",
			"minecraft:jungle_leaves", "minecraft:acacia_leaves", "minecraft:dark_oak_leaves", "minecraft:mangrove_leaves",
			"minecraft:cherry_leaves", "minecraft:azalea_leaves", "minecraft:flowering_azalea_leaves", "minecraft:vine",
			"minecraft:waterlily", "minecraft:tall_grass", "minecraft:dead_bush", "minecraft:fern", "minecraft:dandelion",
			"minecraft:poppy", "minecraft:blue_orchid", "minecraft:allium", "minecraft:azure_bluet", "minecraft:red_tulip",
			"minecraft:orange_tulip", "minecraft:white_tulip", "minecraft:pink_tulip", "minecraft:oxeye_daisy", "minecraft:cornflower",
			"minecraft:lily_of_the_valley", "minecraft:wither_rose", "minecraft:sunflower", "minecraft:lilac", "minecraft:rose_bush",
			"minecraft:peony");

	// Items

	@ConfigEntry.Category("items")
	@Comment("Multiplier for how much each level of fire protection cools you (Default: -5.0)")
	public double fireProtectionCoolingFactor = -5.0;

	@ConfigEntry.Category("items")
	@Comment("Multiplier for how much each level of cold protection warms you (Default: 5.0)")
	public double coldProtectionCoolingFactor = 5.0;

	@ConfigEntry.Category("items")
	@Comment("Item temperature values that add to body temperature calculation when actively held.")
	public String heldTemperatureItems = String.join(", ", "minecraft:torch = 2.0", "minecraft:lava_bucket = 1.0",
			"hardcore_torches:lit_torch = 2.0", "hardcore_torches:lit_lantern = 1.0");

	@ConfigEntry.Category("items")
	@Comment("Item temperature values that add to body temperature calculation when worn.")
	public String wornTemperatureItems = String.join(", ", "");

	@ConfigEntry.Category("items")
	@Comment("Material temperature values used to auto-assign values to wearable items.")
	public String materialAutoTemperature = String.join(", ", "leather = 3.0", "iron = 2.0", "gold = 1.5", "diamond = 2.5",
			"netherite = 3.0", "chainmail = 1.0", "aeternium = 2.0", "brass = 1.0", "bronze = 1.0", "cincinnasite = 2.0", "copper = 1.0",
			"crystalite = 3.0", "flaming_ruby = 5.0", "nether_ruby = 4.0", "resonarium = 3.0", "rose_quartz = 2.0", "steel = 2.0",
			"sturdy = 3.0", "terminite = 3.0", "thallasium = 2.0", "warden = 3.0", "turtle = 1.0");

	@ConfigEntry.Category("items")
	@Comment("Temperature factor for helmets with auto-assigned material-based values. (Default: 1.0)")
	public double helmetAutoTemperatureFactor = 1.0;

	@ConfigEntry.Category("items")
	@Comment("Temperature factor for chestplates with auto-assigned material-based values. (Default: 2.0)")
	public double leggingsAutoTemperatureFactor = 2.0;

	@ConfigEntry.Category("items")
	@Comment("Temperature factor for leggings with auto-assigned material-based values. (Default: 3.0)")
	public double chestplateAutoTemperatureFactor = 3.0;

	@ConfigEntry.Category("items")
	@Comment("Temperature factor for boots with auto-assigned material-based values. (Default: 1.0)")
	public double bootsAutoTemperatureFactor = 1.0;

	@ConfigEntry.Category("items")
	@Comment("The factor for how much the raw temperature value of worn items adds to player temperature. (Default: 0.35)")
	public double itemTemperatureFactor = 0.35;

	@ConfigEntry.Category("items")
	@Comment("Factor for how much player acclimatization is affected by the temperature value of worn items. (Default: -0.1)")
	public double itemAcclimatizationRateFactor = -0.15;

	@ConfigEntry.Category("items")
	@Comment("Duration of the cooling effect of ice water drinks in ticks. (Default: 6000)")
	public int iceWaterEffectDuration = 6000;

	// Wind

	@ConfigEntry.Category("wind")
	@Comment("Toggles wind simulation, including effects on effective temperatures and particles. (Default: true)")
	public boolean enableWind = true;

	@ConfigEntry.Category("wind")
	@Comment("The base number of ticks until wind direction is updated, plus randomization. (Default: 6000)")
	public int windDirectionUpdateInterval = 6000;

	@ConfigEntry.Category("wind")
	@Comment("The base number of ticks until wind intensity is updated, plus randomization. (Default: 400)")
	public int windIntensityUpdateInterval = 400;

	@ConfigEntry.Category("wind")
	@Comment("The number of ticks to transition from one wind intensity or direction to a new value. (Default: 200)")
	public int windTransitionInterval = 200;

	@ConfigEntry.Category("wind")
	@Comment("The minimum intensity wind can be transitioned to in random tick assignment. (Default: 0.25)")
	public double windIntensityMin = 0.25;

	@ConfigEntry.Category("wind")
	@Comment("The maximum intensity wind can be transitioned to in random tick assignment. (Default: 3.5)")
	public double windIntensityMax = 3.5;

	@ConfigEntry.Category("wind")
	@Comment("The factor for how much wind exposure affects ambient temperature. (Default: 1.25)")
	public double windChillFactor = -1.25;

	@ConfigEntry.Category("wind")
	@Comment("Number of rays used in wind calculation. Increase for more precise wind simulation. (Default: 6)")
	public int windRayCount = 6;

	@ConfigEntry.Category("wind")
	@Comment("How many blocks long wind rays are. Increase for larger spaces. (Default: 8)")
	public int windRayLength = 8;

	// Particles

	@ConfigEntry.Category("particles")
	@Comment("Toggles particles being affected by wind intensity and direction. (Default: true)")
	public boolean enableParticleWindEffects = true;

	@ConfigEntry.Category("particles")
	@Comment("The intensity by which all particles are affected by wind if enabled. (Default: 1.0)")
	public double particleWindEffectFactor = 1.0;

	@ConfigEntry.Category("particles")
	@Comment("The intensity by which weather particles are titled towards wind direction. (Default: 0.1)")
	public double particleWeatherEffectFactor = 0.1;

	@ConfigEntry.Category("particles")
	@Comment("The maximum angle a rain particle can get skewed by wind intensity and angle, in degrees. (Default: 30)")
	public double particleRainMaxAngle = 30.0;

	@ConfigEntry.Category("particles")
	@Comment("Toggles particles being affected by heat source blocks (only applies to smoke). (Performance impact: low) (Default: true)")
	public boolean enableParticleHeatEffects = true;

	@ConfigEntry.Category("particles")
	@Comment("Toggles blocks being able to affect particle velocity if wind path is obstructed. (Performance impact: medium) (Default: true)")
	public boolean enableParticleBlockChecks = true;

	@ConfigEntry.Category("particles")
	@Comment("Toggles particles being affected by near-by fluid blocks. (Performance impact: medium) (Default: true)")
	public boolean enableParticleFluidBlockChecks = true;

	@ConfigEntry.Category("particles")
	@Comment("Toggles complex wind simulation, including being pushed alongside and around obstacles. (Performance impact: high) (Default: false)")
	public boolean enableParticleComplexInteractions = false;

	@ConfigEntry.Category("particles")
	@Comment("Toggles complex wind simulation to allow particles to be funelled through gaps in obstacles. (Performance impact: high) (Default: false)")
	public boolean enableParticleFunneling = false;

	// Developer

	@ConfigEntry.Category("developer")
	@Comment("Enables debug logging and performance profiling to the console. (Default: false)")
	public boolean enableLogging = false;

	@ConfigEntry.Category("developer")
	@Comment("Enables temperature ticking and HUD display for players in creative mode. Useful for debugging. (Default: false)")
	public boolean enableCreativeModeTemperature = false;

	@ConfigEntry.Category("developer")
	@Comment("Enables debug visualization of sun vectors showing the raycast direction from player to sun. (Default: false)")
	public boolean enableSunVectorDebug = false;

}