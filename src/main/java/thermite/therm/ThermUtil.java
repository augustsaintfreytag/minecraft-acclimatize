package thermite.therm;

import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thermite.therm.library.BlockStatePositionTuple;
import thermite.therm.library.ClimateKind;

public class ThermUtil {

	public static ClimateKind climateKindForTemperature(float temperature) {
		if (temperature < 0.0) {
			return ClimateKind.FRIGID;
		} else if (temperature < 0.31 && temperature >= 0.0) {
			return ClimateKind.COLD;
		} else if (temperature < 0.9 && temperature >= 0.31) {
			return ClimateKind.TEMPERATE;
		} else if (temperature < 2.0 && temperature > 0.8) {
			return ClimateKind.HOT;
		} else if (temperature >= 2.0) {
			return ClimateKind.ARID;
		}

		return ClimateKind.ARID;
	}

	public static ArrayList<BlockStatePositionTuple> getBlockBox(World world, int x1, int y1, int z1, int x2, int y2,
			int z2) {

		ArrayList<BlockStatePositionTuple> arr = new ArrayList<>();

		int y = 0;
		int z = 0;

		int width = Math.abs((x2 - x1));
		int height = Math.abs((y2 - y1));
		int depth = Math.abs((z2 - z1));

		for (int x = 0; x < width;) {
			for (y = 0; y < height;) {
				for (z = 0; z < depth;) {
					arr.add(new BlockStatePositionTuple(world.getBlockState(new BlockPos(x1 + x, y1 + y, z1 + z)),
							new BlockPos(x1 + x, y1 + y, z1 + z)));
					z++;
				}

				y++;
			}

			x++;
		}

		return arr;
	}

	public static int randInt(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}

}
