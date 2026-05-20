package com.mrbysco.simpleteleporters;

import com.mojang.logging.LogUtils;
import com.mrbysco.simpleteleporters.client.ClientHandler;
import com.mrbysco.simpleteleporters.config.SimpleTeleportersConfig;
import com.mrbysco.simpleteleporters.integration.GuideMEIntegration;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersAttachments;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlockEntities;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersComponents;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersCreativeTabs;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

@Mod(SimpleTeleporters.MOD_ID)
public class SimpleTeleporters {
	public static final String MOD_ID = "simpleteleporters";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

	public static boolean isGuideMELoaded() {
		return ModList.get().isLoaded("guideme");
	}

	public static boolean isSableLoaded() {
		return ModList.get().isLoaded("sable");
	}

	public static boolean isSableIntegrationActive() {
		return isSableLoaded() && SimpleTeleportersConfig.SERVER.enableSableIntegration.getAsBoolean();
	}

	public SimpleTeleporters(IEventBus eventBus, ModContainer container, Dist dist) {
		SimpleTeleportersBlocks.BLOCKS.register(eventBus);
		SimpleTeleportersBlockEntities.BLOCK_ENTITY_TYPES.register(eventBus);
		SimpleTeleportersComponents.DATA_COMPONENT_TYPES.register(eventBus);
		SimpleTeleportersItems.ITEMS.register(eventBus);
		SimpleTeleportersSoundEvents.SOUND_EVENTS.register(eventBus);
		SimpleTeleportersAttachments.ATTACHMENT_TYPES.register(eventBus);
		SimpleTeleportersCreativeTabs.CREATIVE_MODE_TABS.register(eventBus);

		// Register the GuideME guide if GuideME is loaded
		if (isGuideMELoaded()) {
			GuideMEIntegration.init();
		}

		container.registerConfig(ModConfig.Type.SERVER, SimpleTeleportersConfig.serverSpec);

		if (dist.isClient()) {
			container.registerConfig(ModConfig.Type.CLIENT, SimpleTeleportersConfig.clientSpec);
			container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

			eventBus.addListener(ClientHandler::registerEntityRenders);
			eventBus.addListener(ClientHandler::registerBlockColors);
			eventBus.addListener(ClientHandler::onClientSetup);
		}
	}
}
