package thermite.therm.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import thermite.therm.ThermUtil;
import thermite.therm.library.ClimateKind;

public class IceBoxFrozenBlock extends Block {

	public IceBoxFrozenBlock(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit) {

		if (!world.isClient) {
			world.setBlockState(pos, ThermBlocks.ICE_BOX_EMPTY_BLOCK.getDefaultState());
			IceBoxFrozenBlock.dropStack(world, pos, new ItemStack(Items.ICE, 3));
		}

		return ActionResult.SUCCESS;
	}

	public void randomTick(BlockState state, ServerWorld world, BlockPos position, Random random) {
		if (world.isClient()) {
			return;
		}

		var biome = world.getBiome(position).value();
		var biomeTemperature = biome.getTemperature();
		var climateKind = ThermUtil.climateKindForTemperature(biomeTemperature);

		if (climateKind == ClimateKind.COLD || climateKind == ClimateKind.FRIGID) {
			if (random.nextInt(7) == 0) {
				world.setBlockState(position, ThermBlocks.ICE_BOX_FREEZING_BLOCK.getDefaultState());
			}

			return;
		}

		if (climateKind == ClimateKind.ARID) {
			if (random.nextInt(5) == 0) {
				world.setBlockState(position, ThermBlocks.ICE_BOX_EMPTY_BLOCK.getDefaultState());
			}

			return;
		}
	}

}