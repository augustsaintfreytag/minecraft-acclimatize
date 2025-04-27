package net.saint.acclimatize.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.saint.acclimatize.ModBlocks;
import net.saint.acclimatize.ModUtil;
import net.saint.acclimatize.library.ClimateKind;

public class IceBoxFreezingBlock extends Block {

	public IceBoxFreezingBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos position, Random random) {
		if (world.isClient()) {
			return;
		}

		var biome = world.getBiome(position).value();
		var biomeTemperature = biome.getTemperature();
		var climateKind = ModUtil.climateKindForTemperature(biomeTemperature);

		if (climateKind == ClimateKind.COLD || climateKind == ClimateKind.FRIGID) {
			if (random.nextInt(5) == 0) {
				world.setBlockState(position, ModBlocks.ICE_BOX_FROZEN_BLOCK.getDefaultState());
			}
		}
	}
}