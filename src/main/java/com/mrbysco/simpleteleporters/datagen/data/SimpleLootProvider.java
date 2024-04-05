package com.mrbysco.simpleteleporters.datagen.data;

import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleLootProvider extends LootTableProvider {
	public SimpleLootProvider(PackOutput packOutput) {
		super(packOutput, Set.of(), List.of(
				new SubProviderEntry(SimpleBlockLootSubProvider::new, LootContextParamSets.BLOCK)
		));
	}

	private static class SimpleBlockLootSubProvider extends BlockLootSubProvider {

		protected SimpleBlockLootSubProvider() {
			super(Set.of(), FeatureFlags.REGISTRY.allFlags());
		}

		@Override
		protected void generate() {
			dropSelf(SimpleTeleportersBlocks.TELEPORTER.get());
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return (Iterable<Block>) SimpleTeleportersBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
		}
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, @Nonnull ValidationContext validationContext) {
		map.forEach((location, lootTable) -> lootTable.validate(validationContext));
	}
}
