package com.mrbysco.simpleteleporters.block.entity;

import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlockEntities;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersComponents;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
import com.mrbysco.simpleteleporters.data.SubLevelBinding;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TeleporterBlockEntity extends BlockEntity {
	private ItemStack crystal = ItemStack.EMPTY;
	private int cooldown = 0;
	private DyeColor color = DyeColor.WHITE;
	private boolean powered = false;

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

		// Ship can move dims; defer to resolver so the user sees the airship-specific error.
		if (getCrystal().has(SimpleTeleportersComponents.SUB_LEVEL_BINDING.get())) {
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
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);

		if (tag.contains("crystal")) {
			this.crystal = ItemStack.parseOptional(registries, tag.getCompound("crystal"));
		} else {
			this.crystal = ItemStack.EMPTY;
		}
		if (tag.contains("cooldown")) {
			this.cooldown = tag.getInt("cooldown");
		} else {
			this.cooldown = 0;
		}
		if (tag.contains("color")) {
			this.color = DyeColor.byId(tag.getInt("color"));
		} else {
			this.color = DyeColor.WHITE;
		}
		this.powered = tag.getBoolean("powered");
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (!crystal.isEmpty()) {
			tag.put("crystal", this.crystal.save(registries, new CompoundTag()));
		}
		tag.putInt("cooldown", cooldown);
		tag.putInt("color", color.getId());
		tag.putBoolean("powered", powered);
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
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
		DyeColor oldColor = this.color;
		ItemStack oldCrystal = this.crystal;
		super.onDataPacket(net, pkt, registries);
		// Trigger re-render if color or crystal changed
		if (level != null && level.isClientSide() && (oldColor != this.color || !ItemStack.matches(oldCrystal, this.crystal))) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
		}
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag, registries);
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
		DyeColor oldColor = this.color;
		ItemStack oldCrystal = this.crystal;
		loadAdditional(tag, registries);
		// Trigger re-render if color or crystal changed on client
		if (level != null && level.isClientSide() && (oldColor != this.color || !ItemStack.matches(oldCrystal, this.crystal))) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
		}
	}
}
