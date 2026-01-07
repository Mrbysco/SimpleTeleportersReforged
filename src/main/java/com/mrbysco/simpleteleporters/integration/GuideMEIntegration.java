package com.mrbysco.simpleteleporters.integration;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import guideme.Guide;
import guideme.Guides;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * GuideME integration - only loaded when GuideME is present.
 * This class is isolated to prevent ClassNotFoundException when GuideME is not installed.
 */
public class GuideMEIntegration {
	private static final Identifier GUIDE_ID = SimpleTeleporters.id("guide");
	private static Guide guide;

	public static void init() {
		guide = Guide.builder(GUIDE_ID)
				.defaultNamespace(SimpleTeleporters.MOD_ID)
				.build();
	}

	public static Guide getGuide() {
		return guide;
	}

	public static ItemStack getGuideItem() {
		return Guides.createGuideItem(GUIDE_ID);
	}
}
