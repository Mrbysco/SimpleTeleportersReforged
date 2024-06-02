package com.mrbysco.simpleteleporters.registry;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.block.entity.TeleporterBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SimpleTeleportersBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SimpleTeleporters.MOD_ID);

	public static final Supplier<BlockEntityType<TeleporterBlockEntity>> TELEPORTER = BLOCK_ENTITY_TYPES.register("teleporter", () -> BlockEntityType.Builder.of(
			TeleporterBlockEntity::new, SimpleTeleportersBlocks.TELEPORTER.get()).build(null));
}
