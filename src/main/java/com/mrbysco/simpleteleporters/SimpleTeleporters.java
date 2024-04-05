package com.mrbysco.simpleteleporters;

import com.mojang.logging.LogUtils;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlockEntities;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.List;

@Mod(SimpleTeleporters.MOD_ID)
public class SimpleTeleporters {
	public static final String MOD_ID = "simpleteleporters";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public SimpleTeleporters() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

		SimpleTeleportersBlocks.BLOCKS.register(eventBus);
		SimpleTeleportersBlockEntities.BLOCK_ENTITY_TYPES.register(eventBus);
		SimpleTeleportersItems.ITEMS.register(eventBus);
		SimpleTeleportersSoundEvents.SOUND_EVENTS.register(eventBus);

		eventBus.addListener(this::buildCreativeContents);
	}

	public void buildCreativeContents(final BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
			List<ItemStack> stacks = SimpleTeleportersItems.ITEMS.getEntries().stream().map(reg -> new ItemStack(reg.get())).toList();
			event.acceptAll(stacks);
		}
	}
}
