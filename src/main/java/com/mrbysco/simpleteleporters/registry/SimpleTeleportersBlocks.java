package com.mrbysco.simpleteleporters.registry;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.block.TeleporterBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SimpleTeleportersBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SimpleTeleporters.MOD_ID);

	public static final DeferredBlock<TeleporterBlock> TELEPORTER = registerBlock("teleporter",
			() -> new TeleporterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()
					.destroyTime(1).explosionResistance(1).lightLevel((state) -> 15)));
	//Put this item in TAB_TRANSPORTATION

	public static <B extends Block> DeferredBlock<B> registerBlock(String name, Supplier<? extends B> supplier) {
		DeferredBlock<B> block = SimpleTeleportersBlocks.BLOCKS.register(name, supplier);
		SimpleTeleportersItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
		return block;
	}
}
