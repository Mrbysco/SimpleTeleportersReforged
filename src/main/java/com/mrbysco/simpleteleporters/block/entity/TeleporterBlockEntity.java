package com.mrbysco.simpleteleporters.block.entity;

import com.mrbysco.simpleteleporters.item.TeleportCrystalItem;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

		CompoundTag nbt = getCrystal().getTag();
		return TeleportCrystalItem.getDimensionKey(nbt).equals(entity.level().dimension());
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

		CompoundTag nbt = this.getCrystalNbt();
		return TeleportCrystalItem.getPosition(nbt);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);

		if (nbt.contains("crystal")) {
			this.setCrystal(ItemStack.of(nbt.getCompound("crystal")));
		} else {
			this.setCrystal(ItemStack.EMPTY);
		}
		if (nbt.contains("cooldown")) {
			this.setCooldown(nbt.getInt("cooldown"));
		} else {
			this.setCooldown(0);
		}
	}


	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);

		if (!crystal.isEmpty()) {
			nbt.put("crystal", this.crystal.save(new CompoundTag()));
		}
		nbt.putInt("cooldown", cooldown);
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

	public CompoundTag getCrystalNbt() {
		if (!hasCrystal())
			return null;

		return getCrystal().getTag();
	}
}
