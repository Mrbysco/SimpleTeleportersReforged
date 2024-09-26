package com.mrbysco.simpleteleporters.registry;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.item.HearthCrystalItem;
import com.mrbysco.simpleteleporters.item.TeleportCrystalItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SimpleTeleportersItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SimpleTeleporters.MOD_ID);

	public static final DeferredItem<Item> ENDER_SHARD = ITEMS.register("ender_shard", () -> new TeleportCrystalItem(new Item.Properties().stacksTo(16)));
	public static final DeferredItem<Item> HEARTH_CRYSTAL = ITEMS.register("hearth_crystal", () -> new HearthCrystalItem(new Item.Properties().stacksTo(1)));
}
