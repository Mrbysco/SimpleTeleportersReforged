package com.mrbysco.simpleteleporters.registry;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.block.TeleporterBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SimpleTeleportersBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SimpleTeleporters.MOD_ID);

	public static final RegistryObject<TeleporterBlock> TELEPORTER = registerBlock("teleporter",
			() -> new TeleporterBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion()
					.destroyTime(1).explosionResistance(1).lightLevel((state) -> 15)));
	//Put this item in TAB_TRANSPORTATION

	public static <B extends Block> RegistryObject<B> registerBlock(String name, Supplier<? extends B> supplier) {
		RegistryObject<B> block = SimpleTeleportersBlocks.BLOCKS.register(name, supplier);
		SimpleTeleportersItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
		return block;
	}
}
