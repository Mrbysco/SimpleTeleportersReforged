package com.mrbysco.simpleteleporters.registry;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mojang.serialization.Codec;
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

	// Add this new component for the teleport timer with a custom-created codec
	public static final Supplier<DataComponentType<Integer>> TELEPORT_TIMER = DATA_COMPONENT_TYPES.register("teleport_timer", () ->
			DataComponentType.<Integer>builder()
					.persistent(Codec.INT)
					// Create a stream codec similar to how GlobalPos does it
					.networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.VAR_INT)
					.build());

	// Charges component for Hearth Crystal
	public static final Supplier<DataComponentType<Integer>> CHARGES = DATA_COMPONENT_TYPES.register("charges", () ->
			DataComponentType.<Integer>builder()
					.persistent(Codec.INT)
					.networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.VAR_INT)
					.build());
}