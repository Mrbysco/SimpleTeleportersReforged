package com.mrbysco.simpleteleporters.block.entity;

import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlockEntities;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersComponents;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TeleporterBlockEntity extends BlockEntity {
	private ItemStack crystal = ItemStack.EMPTY;
	private int cooldown = 0;
	private DyeColor color = DyeColor.WHITE;
	private boolean powered = false;
	// Track old values for client-side re-render detection
	private DyeColor oldColor = DyeColor.WHITE;
	private ItemStack oldCrystal = ItemStack.EMPTY;

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

		// Enhanced shards can cross dimensions
		if (hasEnhancedCrystal()) {
			return true;
		}

		GlobalPos globalPos = getCrystal().get(SimpleTeleportersComponents.GLOBAL_POS);
		ResourceKey<Level> dimensionKey = globalPos != null ? globalPos.dimension() : Level.OVERWORLD;
		return dimensionKey.equals(entity.level().dimension());
	}

	public boolean hasEnhancedCrystal() {
		return !getCrystal().isEmpty() && getCrystal().is(SimpleTeleportersItems.ENHANCED_ENDER_SHARD.get());
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
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);

		// Store old values for comparison
		this.oldColor = this.color;
		this.oldCrystal = this.crystal.copy();

		Optional<ItemStack> optionalCrystal = input.read("crystal", ItemStack.OPTIONAL_CODEC);
		this.crystal = optionalCrystal.orElse(ItemStack.EMPTY);

		this.cooldown = input.getIntOr("cooldown", 0);
		this.color = DyeColor.byId(input.getIntOr("color", DyeColor.WHITE.getId()));
		this.powered = input.getBooleanOr("powered", false);

		// Trigger re-render if color or crystal changed on client
		if (level != null && level.isClientSide() && (oldColor != this.color || !ItemStack.matches(oldCrystal, this.crystal))) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
		}
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		if (!crystal.isEmpty()) {
			output.store("crystal", ItemStack.CODEC, this.crystal);
		}
		output.putInt("cooldown", cooldown);
		output.putInt("color", color.getId());
		output.putBoolean("powered", powered);
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

	public DyeColor getColor() {
		return color;
	}

	public void setColor(DyeColor color) {
		this.color = color;
		setChanged();
		if (getLevel() != null) {
			BlockState state = getLevel().getBlockState(getBlockPos());
			getLevel().sendBlockUpdated(getBlockPos(), state, state, 3);
		}
	}

	public boolean wasPowered() {
		return powered;
	}

	public void setPowered(boolean powered) {
		this.powered = powered;
		setChanged();
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		return this.saveWithoutMetadata(provider);
	}
}
