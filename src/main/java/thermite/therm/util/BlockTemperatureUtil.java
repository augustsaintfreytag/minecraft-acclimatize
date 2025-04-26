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

public final class BlockTemperatureUtil {

	private static HashMap<String, Double> blockTemperatureById;

	public static void reloadBlocks() {
		blockTemperatureById = allBlockTemperatureById();
	}

	public static double temperatureDeltaForBlocksInVicinity(ServerPlayerEntity player) {
		var radius = ThermMod.CONFIG.blockTemperatureRadius;
		var falloffConstant = ThermMod.CONFIG.blockTemperatureFalloffConstant;
		var distanceFalloffFactor = ThermMod.CONFIG.blockTemperatureDistanceFalloffFactor;

		var world = player.getWorld();
		var playerPosition = player.getBlockPos();

		var aggregateTemperatureDelta = 0.0;

		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius / 2; y <= radius / 2; y++) {
				for (int z = -radius; z <= radius; z++) {
					var blockPosition = playerPosition.add(x, y, z);
					var blockState = world.getBlockState(blockPosition);
					var blockId = Registries.BLOCK.getId(blockState.getBlock()).toString();
					var blockTemperature = temperatureDeltaForBlock(world, blockPosition, blockState, blockId);

					if (blockTemperature == 0.0) {
						continue;
					}

					var blockDistance = blockPosition.getSquaredDistance(player.getPos());
					var fallOffTemperature = blockTemperature
							/ Math.max(0.001, ((blockDistance * distanceFalloffFactor) + falloffConstant));

					aggregateTemperatureDelta += Math.min(blockTemperature, fallOffTemperature);
				}
			}
		}

		aggregateTemperatureDelta = Math.max(ThermMod.CONFIG.blockTemperatureAbsoluteMinimum,
				aggregateTemperatureDelta);
		aggregateTemperatureDelta = Math.min(ThermMod.CONFIG.blockTemperatureAbsoluteMaximum,
				aggregateTemperatureDelta);

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
