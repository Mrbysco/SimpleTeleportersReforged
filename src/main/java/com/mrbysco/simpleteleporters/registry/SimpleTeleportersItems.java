package com.mrbysco.simpleteleporters.registry;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.item.EnhancedTeleportCrystalItem;
import com.mrbysco.simpleteleporters.item.HearthCrystalItem;
import com.mrbysco.simpleteleporters.item.TeleportCrystalItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SimpleTeleportersItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SimpleTeleporters.MOD_ID);

	public static final DeferredItem<Item> ENDER_SHARD = ITEMS.registerItem("ender_shard", TeleportCrystalItem::new, new Item.Properties().stacksTo(16));
	public static final DeferredItem<Item> ENHANCED_ENDER_SHARD = ITEMS.registerItem("enhanced_ender_shard", EnhancedTeleportCrystalItem::new, new Item.Properties().stacksTo(16));
	public static final DeferredItem<Item> HEARTH_CRYSTAL = ITEMS.registerItem("hearth_crystal", HearthCrystalItem::new, new Item.Properties().stacksTo(1));
}
