package com.mrbysco.simpleteleporters.registry;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.integration.GuideMEIntegration;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SimpleTeleportersCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SimpleTeleporters.MOD_ID);

	public static final Supplier<CreativeModeTab> SIMPLE_TELEPORTERS_TAB = CREATIVE_MODE_TABS.register("simple_teleporters", () ->
			CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.simpleteleporters"))
					.icon(() -> new ItemStack(SimpleTeleportersBlocks.TELEPORTER.get()))
					.displayItems((parameters, output) -> {
						// Add all registered items
						SimpleTeleportersItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
						// Add the guide book if GuideME is loaded
						if (SimpleTeleporters.isGuideMELoaded()) {
							output.accept(GuideMEIntegration.getGuideItem());
						}
					})
					.build()
	);
}
