package com.mrbysco.simpleteleporters.datagen.assets;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

public class SimpleModelProvider extends ModelProvider {
	public SimpleModelProvider(PackOutput output) {
		super(output, SimpleTeleporters.MOD_ID);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		MultiVariant variant = BlockModelGenerators.plainVariant(SimpleTeleportersBlocks.TELEPORTER.getId().withPrefix("block/"));
		blockModels.blockStateOutput.accept(
				MultiVariantGenerator.dispatch(SimpleTeleportersBlocks.TELEPORTER.get(), variant)
		);

		itemModels.generateFlatItem(SimpleTeleportersItems.ENDER_SHARD.get(), ModelTemplates.FLAT_ITEM);
	}
}
