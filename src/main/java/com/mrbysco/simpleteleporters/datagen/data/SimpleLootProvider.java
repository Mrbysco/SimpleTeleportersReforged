package com.mrbysco.simpleteleporters.datagen.data;

import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class SimpleLootProvider {

	public static class SimpleBlockLootSubProvider extends BlockLootSubProvider {

		public SimpleBlockLootSubProvider(Context context) {
			super(Set.of(), FeatureFlags.REGISTRY.allFlags(), context);
		}

		@Override
		protected void generate() {
			dropSelf(SimpleTeleportersBlocks.TELEPORTER.get());
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return (Iterable<Block>) SimpleTeleportersBlocks.BLOCKS.getEntries().stream().map(holder -> (Block) holder.value())::iterator;
		}
	}
}
