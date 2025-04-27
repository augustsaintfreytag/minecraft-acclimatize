# Minecraft Acclimatize

A comprehensive and highly-configurable body temperature system for Minecraft. This mod adds realistic temperature mechanics to the game, challenging players to manage their body heat in different environments. Originally forked from [*Thermite*](https://github.com/Sparkierkan7/thermite_mod) by [Sparkierkan7](https://github.com/Sparkierkan7), *Acclimatize* expands on its foundation with extended configurability, rewritten block and item handling, and drastically increased compatibility with other mods.

## Features

### Core Temperature System
- **Body Temperature Mechanics**: Experience a realistic temperature system that requires balancing your heat level
- **Variable Acclimatization**: The rate of temperature gain around a heat source can be higher than walking out in the open for improved player enjoyment
- **Environmental Effects**: Temperature changes based on biomes, time of day, and weather, even including indoor/outdoor detection
- **Temperature-Based Effects**: Suffer from hypothermia in cold environments or hyperthermia in hot ones through status effects (not just basic player damage)
- **Status Effects**: Cold and heat resistance effects to temporarily protect against temperature extremes

### Temperature Sources
- **Biome Temperature**: Each biome has a base temperature affecting the player
- **Block Temperature**: Heating blocks (fireplaces, lava, campfires) and cooling blocks (ice, snow) affect nearby players
- **Temperature Falloff**: Heat sources have configurable falloff based on distance
- **Thermal Clothing**: Wear different materials to insulate against cold or heat
- **Customizable Wool Lining**: Add wool to leather armor for additional insulation (ported from *Thermite*)

### Items and Blocks
- **Thermometer**: Check your current temperature and environmental factors
- **Ice Water**: Drinkable item that provides temporary cold resistance
- **Golden Sweet Berries**: Food with temperature-related effects
- **Wool Cloth**: Crafting material to improve armor insulation
- **Brick Fireplace**: Decorative heating block to warm your home
- **Ice Box**: A cooling block with three stages (empty, freezing, frozen)

### Weather and Seasons
- **Wind System**: Configurable wind that affects temperature and can be visualized with particles
- **Optional Seasons**: Seasonal temperature variations with configurable duration
- **Weather Integration**: Option for season-dependent weather patterns

### HUD and Visual Feedback
- **Temperature Display**: Configurable HUD element showing current body temperature
- **Temperature Vignette**: Visual effects indicating when you're too hot or cold
- **Wind Particles**: Optional particles showing wind direction and intensity
- **Glass Thermometer**: Visual representation of your current temperature

## Configuration

The mod offers extensive configuration options through a user-friendly in-game interface (requires Cloth Config and Mod Menu) or via `/<root>/configs/acclimatize.json5` in the game directory.

- **Temperature Thresholds**: Set your own thresholds for hypothermia and hyperthermia
- **Damage Settings**: Configure how much damage temperature extremes cause
- **Block Temperature**: Add or modify blocks that affect temperature
- **Item Temperature**: Configure how armor and held items affect temperature
- **Material Properties**: Set temperature values for different armor materials
- **HUD Customization**: Adjust the position and style of temperature display
- **Environmental Factors**: Configure wind, seasons, and biome temperatures

## Mod Compatibility

*Acclimatize* is designed to be compatible with many other mods:

- **Block Auto-Detection**: The mod can automatically handle modded blocks with heat or cold properties
- **Material Auto-Assignment**: Temperature values can be automatically assigned to armor from other mods
- **Configurable Compatibility**: Add specific modded items and blocks to the temperature system via configs
- **Built-in Support**: Default configuration includes support for:
  - Hardcore Torches
  - Farmer's Delight
  - Refurbished Furniture
  - Various armor material mods

## Requirements

- Minecraft 1.20.1
- Fabric API 0.91.0+
- Cloth Config 11.1.136+
- Fabric Loader 0.16.10+
- Java 17+

## License and Credits

This project is available under the MIT License, which permits free use, modification, and distribution under the condition that the original copyright notice and permission notice are included in all copies or substantial portions of the software.

*Acclimatize* is built upon the foundation of *Thermite*, originally created by Sparkierkan7. This fork is developed and maintained by Saint, with extensions and re-interpretations to the original concept including configurability, block and item handling, and player acclimatization.

Copyright © 2025 Saint