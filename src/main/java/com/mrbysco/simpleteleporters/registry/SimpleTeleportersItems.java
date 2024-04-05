package com.mrbysco.simpleteleporters.registry;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.item.TeleportCrystalItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SimpleTeleportersItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SimpleTeleporters.MOD_ID);

	public static final RegistryObject<Item> ENDER_SHARD = ITEMS.register("ender_shard", () -> new TeleportCrystalItem(new Item.Properties().stacksTo(16)));
	//Put this item in TAB_TRANSPORTATION
}
