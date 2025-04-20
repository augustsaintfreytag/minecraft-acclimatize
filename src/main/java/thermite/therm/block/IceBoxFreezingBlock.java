package thermite.therm.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import thermite.therm.ThermUtil;
import thermite.therm.library.ClimateKind;

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
		var climateKind = ThermUtil.climateKindForTemperature(biomeTemperature);

		if (climateKind == ClimateKind.COLD || climateKind == ClimateKind.FRIGID) {
			if (random.nextInt(5) == 0) {
				world.setBlockState(position, ThermBlocks.ICE_BOX_FROZEN_BLOCK.getDefaultState());
			}
		}
	}
}