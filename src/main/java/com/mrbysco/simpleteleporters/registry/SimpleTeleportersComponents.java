package com.mrbysco.simpleteleporters.registry;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SimpleTeleportersComponents {
	public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, SimpleTeleporters.MOD_ID);

	public static final Supplier<DataComponentType<GlobalPos>> GLOBAL_POS = DATA_COMPONENT_TYPES.register("position", () ->
			DataComponentType.<GlobalPos>builder()
					.persistent(GlobalPos.CODEC)
					.networkSynchronized(GlobalPos.STREAM_CODEC)
					.build());
}
