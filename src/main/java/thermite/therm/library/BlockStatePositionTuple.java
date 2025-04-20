package thermite.therm.library;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockStatePositionTuple {
	public BlockState blockState;
	public BlockPos position;

	public BlockStatePositionTuple(BlockState b, BlockPos p) {
		blockState = b;
		position = p;
	}

}
