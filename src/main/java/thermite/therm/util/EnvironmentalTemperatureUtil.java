package thermite.therm.util;

import java.util.HashMap;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
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
					var temperatureDelta = temperatureDeltaForBlock(blockPosition, state);

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

	private static double temperatureDeltaForBlock(BlockPos position, BlockState state) {
		var blockId = state.getBlock().toString();
		var rawBlockTemperature = rawBlockTemperatureForId(blockId);

		// Block-specific exceptions

		if ((state.isOf(Blocks.CAMPFIRE) || state.isOf(Blocks.SOUL_CAMPFIRE)) && !state.get(CampfireBlock.LIT)) {
			rawBlockTemperature = 0.0;
		}

		if ((state.isOf(Blocks.FURNACE) || state.isOf(Blocks.BLAST_FURNACE) || state.isOf(Blocks.SMOKER))
				&& !state.get(FurnaceBlock.LIT)) {
			rawBlockTemperature = 0.0;
		}

		return rawBlockTemperature;
	}

	private static double rawBlockTemperatureForId(String blockId) {
		if (blockTemperatureById.containsKey(blockId)) {
			return blockTemperatureById.get(blockId);
		}

		return 0;
	}

	private static HashMap<String, Double> allBlockTemperatureById() {
		var allBlocks = new HashMap<String, Double>();

		allBlocks.putAll(ThermMod.config.heatingBlocks);
		allBlocks.putAll(ThermMod.config.coolingBlocks);

		return allBlocks;
	}

}
