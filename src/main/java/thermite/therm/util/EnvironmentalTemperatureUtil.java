package thermite.therm.util;

import java.util.HashMap;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thermite.therm.ThermMod;

public final class EnvironmentalTemperatureUtil {

	private static final HashMap<String, Double> blockTemperatureById = allBlockTemperatureById();

	public static double temperatureDeltaForEnvironment(ServerPlayerEntity player, int radius) {
		var world = player.getWorld();
		var centerPosition = player.getBlockPos();
		var aggregateTemperatureDelta = 0.0;

		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					var blockPosition = centerPosition.add(x, y, z);
					var state = world.getBlockState(blockPosition);
					var temperatureDelta = temperatureDeltaForBlock(world, blockPosition, state);

					aggregateTemperatureDelta += temperatureDelta;
				}
			}
		}

		aggregateTemperatureDelta += waterSubmersionDeltaForPlayer(player);

		return aggregateTemperatureDelta;
	}

	private static double waterSubmersionDeltaForPlayer(ServerPlayerEntity player) {
		if (!player.isTouchingWater()) {
			return 0.0;
		}

		return -10.0;
	}

	private static double temperatureDeltaForBlock(World world, BlockPos position, BlockState blockState,
			String blockId) {
		var blockTemperature = rawBlockTemperatureForId(blockId);

		if (blockTemperature == 0) {
			return 0.0;
		}

		// Block-specific exceptions

		if ((blockState.isOf(Blocks.CAMPFIRE) || blockState.isOf(Blocks.SOUL_CAMPFIRE))
				&& !blockState.get(CampfireBlock.LIT)) {
			return 0.0;
		}

		if ((blockState.isOf(Blocks.FURNACE) || blockState.isOf(Blocks.BLAST_FURNACE) || blockState.isOf(Blocks.SMOKER))
				&& !blockState.get(FurnaceBlock.LIT)) {
			return 0.0;
		}

		if ((blockId.contains("lamp") || blockId.contains("light"))
				&& (!blockState.contains(Properties.POWERED) || blockState.getLuminance() == 0)) {
			return 0.0;
		}

		return blockTemperature;
	}

	private static double rawBlockTemperatureForId(String blockId) {
		if (blockTemperatureById.containsKey(blockId)) {
			return blockTemperatureById.get(blockId);
		}

		return 0;
	}

	private static HashMap<String, Double> allBlockTemperatureById() {
		var allBlocks = new HashMap<String, Double>();

		allBlocks.putAll(ConfigCodingUtil.decodeTemperatureMapFromRaw(ThermMod.CONFIG.heatingBlocks));
		allBlocks.putAll(ConfigCodingUtil.decodeTemperatureMapFromRaw(ThermMod.CONFIG.coolingBlocks));

		return allBlocks;
	}

}
