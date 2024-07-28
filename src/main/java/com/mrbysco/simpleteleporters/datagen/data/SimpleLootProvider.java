package com.mrbysco.simpleteleporters.datagen.data;

import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SimpleLootProvider extends LootTableProvider {
	public SimpleLootProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, Set.of(), List.of(
				new SubProviderEntry(SimpleBlockLootSubProvider::new, LootContextParamSets.BLOCK)
		), lookupProvider);
	}

	private static class SimpleBlockLootSubProvider extends BlockLootSubProvider {

		protected SimpleBlockLootSubProvider(HolderLookup.Provider provider) {
			super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
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

	@Override
	protected void validate(WritableRegistry<LootTable> writableregistry, ValidationContext validationcontext, ProblemReporter.Collector problemreporter$collector) {
		super.validate(writableregistry, validationcontext, problemreporter$collector);
	}
}
