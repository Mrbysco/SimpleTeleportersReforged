package com.mrbysco.simpleteleporters.datagen;

import com.mrbysco.simpleteleporters.datagen.assets.SimpleLanguageProvider;
import com.mrbysco.simpleteleporters.datagen.assets.SimpleModelProvider;
import com.mrbysco.simpleteleporters.datagen.assets.SimpleSoundProvider;
import com.mrbysco.simpleteleporters.datagen.data.SimpleBlockTagsProvider;
import com.mrbysco.simpleteleporters.datagen.data.SimpleLootProvider;
import com.mrbysco.simpleteleporters.datagen.data.SimpleRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class SimpleDataGen {
	@SubscribeEvent
	static void onGatherRegistries(GatherDataRegistryEntriesEvent event) {
		event.recipe(SimpleRecipeProvider::new);
		event.lootTable(new LootTableProvider.SubProviderEntry(SimpleLootProvider.SimpleBlockLootSubProvider::new, LootContextParamSets.BLOCK));
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		event.createProvider(SimpleBlockTagsProvider::new);

		event.createProvider(SimpleLanguageProvider::new);
		event.createProvider(SimpleSoundProvider::new);
		event.createProvider(SimpleModelProvider::new);

	}
}