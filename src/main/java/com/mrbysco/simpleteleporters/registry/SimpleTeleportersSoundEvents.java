package com.mrbysco.simpleteleporters.registry;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SimpleTeleportersSoundEvents {
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SimpleTeleporters.MOD_ID);

	public static final DeferredHolder<SoundEvent, SoundEvent> TELEPORTER_TELEPORT = register("block.teleporter.teleport");
	public static final DeferredHolder<SoundEvent, SoundEvent> TELEPORTER_CRYSTAL_INSERTED = register("block.teleporter.crystal_inserted");
	public static final DeferredHolder<SoundEvent, SoundEvent> TELEPORTER_CRYSTAL_REMOVED = register("block.teleporter.crystal_removed");
	public static final DeferredHolder<SoundEvent, SoundEvent> ENDER_SHARD_LINK = register("item.ender_shard.link");

	public static DeferredHolder<SoundEvent, SoundEvent> register(String path) {
		ResourceLocation id = SimpleTeleporters.id(path);
		return SOUND_EVENTS.register(path, () -> SoundEvent.createVariableRangeEvent(id));
	}
}
