package net.saint.acclimatize;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.saint.acclimatize.block.IceBoxEmptyBlock;
import net.saint.acclimatize.block.IceBoxFreezingBlock;
import net.saint.acclimatize.block.IceBoxFrozenBlock;
import net.saint.acclimatize.block.SmokeBlock;

public class ModBlocks {

	public static final IceBoxEmptyBlock ICE_BOX_EMPTY_BLOCK = new IceBoxEmptyBlock(FabricBlockSettings.create().strength(1.0f));
	public static final IceBoxFreezingBlock ICE_BOX_FREEZING_BLOCK = new IceBoxFreezingBlock(
			FabricBlockSettings.create().strength(1.0f).ticksRandomly());
	public static final IceBoxFrozenBlock ICE_BOX_FROZEN_BLOCK = new IceBoxFrozenBlock(
			FabricBlockSettings.create().strength(2.0f).ticksRandomly());
	public static final SmokeBlock SMOKE_BLOCK = new SmokeBlock(
			FabricBlockSettings.create().replaceable().noCollision().dropsNothing().air());

}
