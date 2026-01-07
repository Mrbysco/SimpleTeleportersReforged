package com.mrbysco.simpleteleporters.registry;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.block.TeleporterBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("removal")
public class SimpleTeleportersBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SimpleTeleporters.MOD_ID);

	public static final DeferredBlock<TeleporterBlock> TELEPORTER = BLOCKS.registerBlock("teleporter",
			TeleporterBlock::new,
			BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()
					.destroyTime(1).explosionResistance(1).lightLevel((state) -> 15));
}
