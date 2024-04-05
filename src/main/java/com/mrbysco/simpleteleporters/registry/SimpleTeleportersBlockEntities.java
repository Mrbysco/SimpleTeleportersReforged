package com.mrbysco.simpleteleporters.registry;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.block.entity.TeleporterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SimpleTeleportersBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SimpleTeleporters.MOD_ID);

	public static final RegistryObject<BlockEntityType<TeleporterBlockEntity>> TELEPORTER = BLOCK_ENTITY_TYPES.register("teleporter", () -> BlockEntityType.Builder.of(
			TeleporterBlockEntity::new, SimpleTeleportersBlocks.TELEPORTER.get()).build(null));
}
