package com.mrbysco.simpleteleporters.registry;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SimpleTeleportersSoundEvents {
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SimpleTeleporters.MOD_ID);

	public static final RegistryObject<SoundEvent> TELEPORTER_TELEPORT = register("block.teleporter.teleport");
	public static final RegistryObject<SoundEvent> TELEPORTER_CRYSTAL_INSERTED = register("block.teleporter.crystal_inserted");
	public static final RegistryObject<SoundEvent> TELEPORTER_CRYSTAL_REMOVED = register("block.teleporter.crystal_removed");
	public static final RegistryObject<SoundEvent> ENDER_SHARD_LINK = register("item.ender_shard.link");

	public static RegistryObject<SoundEvent> register(String path) {
		ResourceLocation id = SimpleTeleporters.id(path);
		return SOUND_EVENTS.register(path, () -> SoundEvent.createVariableRangeEvent(id));
	}
}
