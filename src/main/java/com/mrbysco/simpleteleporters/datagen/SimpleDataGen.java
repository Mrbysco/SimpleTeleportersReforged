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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class SimpleDataGen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new SimpleLootProvider(packOutput, lookupProvider));
		generator.addProvider(true, new SimpleRecipeProvider.Runner(packOutput, lookupProvider));
		generator.addProvider(true, new SimpleBlockTagsProvider(packOutput, lookupProvider));

		generator.addProvider(true, new SimpleLanguageProvider(packOutput));
		generator.addProvider(true, new SimpleSoundProvider(packOutput));
		generator.addProvider(true, new SimpleModelProvider(packOutput));

	}
}