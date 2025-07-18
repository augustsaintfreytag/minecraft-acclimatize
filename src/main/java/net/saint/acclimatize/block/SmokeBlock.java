package net.saint.acclimatize.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class SmokeBlock extends Block {

	public SmokeBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {

		world.addParticle(ParticleTypes.HAPPY_VILLAGER, (double) pos.getX() + 0.5, (double) pos.getY(), (double) pos.getZ() + 0.5, 0.0, 0.0,
				0.0);

		super.randomDisplayTick(state, world, pos, random);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		entity.setAir(entity.getAir() - 20);
	}
}