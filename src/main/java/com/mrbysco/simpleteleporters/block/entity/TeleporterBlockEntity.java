package com.mrbysco.simpleteleporters.block.entity;

import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlockEntities;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TeleporterBlockEntity extends BlockEntity {
	private ItemStack crystal = ItemStack.EMPTY;
	private int cooldown = 0;

	public TeleporterBlockEntity(BlockPos pos, BlockState state) {
		super(SimpleTeleportersBlockEntities.TELEPORTER.get(), pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TeleporterBlockEntity teleporter) {
		if (teleporter.isCoolingDown()) {
			teleporter.incrementCooldown();
		}
	}

	public boolean hasCrystal() {
		return !getCrystal().isEmpty();
	}

	public boolean isInDimension(Entity entity) {
		if (getCrystal().isEmpty())
			return false;

		GlobalPos globalPos = getCrystal().get(SimpleTeleportersComponents.GLOBAL_POS);
		ResourceKey<Level> dimensionKey = globalPos != null ? globalPos.dimension() : Level.OVERWORLD;
		return dimensionKey.equals(entity.level().dimension());
	}

	public ItemStack getCrystal() {
		return crystal;
	}

	public void setCrystal(ItemStack crystal) {
		this.crystal = crystal;
		setChanged();
		if (getLevel() != null) {
			BlockState state = getLevel().getBlockState(getBlockPos());
			getLevel().sendBlockUpdated(getBlockPos(), state, state, 3);
		}
	}

	public BlockPos getTeleportPos() {
		if (!hasCrystal())
			return null;

		GlobalPos globalPos = getCrystal().get(SimpleTeleportersComponents.GLOBAL_POS);
		return globalPos != null ? globalPos.pos() : null;
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);

		if (tag.contains("crystal")) {
			this.setCrystal(ItemStack.parseOptional(registries, tag.getCompound("crystal")));
		} else {
			this.setCrystal(ItemStack.EMPTY);
		}
		if (tag.contains("cooldown")) {
			this.setCooldown(tag.getInt("cooldown"));
		} else {
			this.setCooldown(0);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (!crystal.isEmpty()) {
			tag.put("crystal", this.crystal.save(registries, new CompoundTag()));
		}
		tag.putInt("cooldown", cooldown);
	}

	public boolean isCoolingDown() {
		return getCooldown() > 0;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public void incrementCooldown() {
		this.setCooldown(this.getCooldown() - 1);
	}
}
