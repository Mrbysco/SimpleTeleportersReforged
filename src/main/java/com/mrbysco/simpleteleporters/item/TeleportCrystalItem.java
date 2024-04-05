package com.mrbysco.simpleteleporters.item;

import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class TeleportCrystalItem extends Item {
	public TeleportCrystalItem(Properties settings) {
		super(settings);
	}

	public static boolean hasPosition(CompoundTag nbt) {
		return nbt != null && nbt.contains("pos");
	}

	public static int getX(CompoundTag nbt) {
		if (hasPosition(nbt)) return getPosition(nbt).getX();
		else return BlockPos.ZERO.getX();
	}

	public static int getY(CompoundTag nbt) {
		if (hasPosition(nbt)) return getPosition(nbt).getY();
		else return BlockPos.ZERO.getY();
	}

	public static int getZ(CompoundTag nbt) {
		if (hasPosition(nbt)) return getPosition(nbt).getZ();
		else return BlockPos.ZERO.getZ();
	}

	public static BlockPos getPosition(CompoundTag nbt) {
		if (!hasPosition(nbt)) return null;
		return BlockPos.of(nbt.getLong("pos"));
	}

	public static String getDimensionName(CompoundTag nbt) {
		if (nbt != null && nbt.contains("dimension")) return nbt.getString("dimension");
		else return Level.OVERWORLD.location().toString();
	}

	public static ResourceKey<Level> getDimensionKey(CompoundTag nbt) {
		return ResourceKey.create(Registries.DIMENSION, new ResourceLocation(getDimensionName(nbt)));
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		if (ctx.isSecondaryUseActive()) {
			Player player = ctx.getPlayer();

			ItemStack stack = ctx.getItemInHand().split(1);
			CompoundTag nbt = stack.getTag();
			if (nbt == null) {
				stack.setTag(new CompoundTag());
				nbt = stack.getTag();
			}

			Level level = ctx.getLevel();
			BlockPos pos = ctx.getClickedPos();
			BlockPos offsetPos;
			if (level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
				offsetPos = pos;
			} else if (level.getBlockState(pos).is(SimpleTeleportersBlocks.TELEPORTER.get())) {
				offsetPos = pos.above();
			} else {
				offsetPos = pos.relative(ctx.getClickedFace());
			}
			nbt.putLong("pos", offsetPos.asLong());
			String dimensionName = player.level().dimension().location().toString();
			nbt.putString("dimension", dimensionName);

			if (!player.addItem(stack)) {
				player.drop(stack, false);
			}

			MutableComponent msg = Component.translatable("text.simpleteleporters.crystal_info", offsetPos.getX(), offsetPos.getY(), offsetPos.getZ(), dimensionName);
			msg.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));

			player.displayClientMessage(msg, true);

			player.playSound(SimpleTeleportersSoundEvents.ENDER_SHARD_LINK.get(), 0.5F, 0.4F / (ctx.getLevel().getRandom().nextFloat() * 0.4F + 0.8F));

			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
		CompoundTag nbt = stack.getTag();
		if (!hasPosition(nbt)) {
			MutableComponent unlinked = Component.translatable("text.simpleteleporters.unlinked");
			unlinked.setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
			tooltip.add(unlinked);

			Component sneakKey = Component.literal("Sneak");
			Component useKey = Component.literal("Right Click");

			if (level != null && level.isClientSide()) {
				sneakKey = Component.keybind(Minecraft.getInstance().options.keyShift.getName());
				useKey = Component.keybind(Minecraft.getInstance().options.keyUse.getName());
			}

			MutableComponent info = Component.translatable("text.simpleteleporters.how_to_link", sneakKey, useKey);
			info.setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE));
			tooltip.add(info);
		} else {
			MutableComponent pos = Component.translatable("text.simpleteleporters.linked", getX(nbt), getY(nbt), getZ(nbt), getDimensionName(nbt));
			pos.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));

			tooltip.add(pos);
		}
	}
}
