package com.mrbysco.simpleteleporters.datagen;

import com.mrbysco.simpleteleporters.datagen.assets.SimpleLanguageProvider;
import com.mrbysco.simpleteleporters.datagen.assets.SimpleSoundProvider;
import com.mrbysco.simpleteleporters.datagen.data.SimpleBlockTagsProvider;
import com.mrbysco.simpleteleporters.datagen.data.SimpleLootProvider;
import com.mrbysco.simpleteleporters.datagen.data.SimpleRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SimpleDataGen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(true, new SimpleLootProvider(packOutput));
			generator.addProvider(true, new SimpleRecipeProvider(packOutput));
			generator.addProvider(true, new SimpleBlockTagsProvider(packOutput, lookupProvider, existingFileHelper));
		}
		if (event.includeClient()) {
			generator.addProvider(true, new SimpleLanguageProvider(packOutput));
			generator.addProvider(true, new SimpleSoundProvider(packOutput, existingFileHelper));
		}
	}
}