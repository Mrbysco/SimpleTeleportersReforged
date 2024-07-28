package com.mrbysco.simpleteleporters.item;

import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersComponents;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.List;

public class TeleportCrystalItem extends Item {
	public TeleportCrystalItem(Properties settings) {
		super(settings);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		if (ctx.isSecondaryUseActive()) {
			Player player = ctx.getPlayer();

			ItemStack stack = ctx.getItemInHand().split(1);

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
			stack.set(SimpleTeleportersComponents.GLOBAL_POS, GlobalPos.of(player.level().dimension(), offsetPos));
			String dimensionName = player.level().dimension().location().toString();

			if (!player.addItem(stack)) {
				player.drop(stack, false);
			}

			MutableComponent msg = Component.translatable("text.simpleteleporters.crystal_info",
					offsetPos.getX(), offsetPos.getY(), offsetPos.getZ(), dimensionName);
			msg.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));

			player.displayClientMessage(msg, true);

			player.playSound(SimpleTeleportersSoundEvents.ENDER_SHARD_LINK.get(), 0.5F,
					0.4F / (ctx.getLevel().getRandom().nextFloat() * 0.4F + 0.8F));

			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
		if (!stack.has(SimpleTeleportersComponents.GLOBAL_POS)) {
			MutableComponent unlinked = Component.translatable("text.simpleteleporters.unlinked");
			unlinked.setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
			tooltip.add(unlinked);

			Component sneakKey = Component.literal("Sneak");
			Component useKey = Component.literal("Right Click");

			if (FMLEnvironment.dist.isClient()) {
				sneakKey = Component.keybind(Minecraft.getInstance().options.keyShift.getName());
				useKey = Component.keybind(Minecraft.getInstance().options.keyUse.getName());
			}

			MutableComponent info = Component.translatable("text.simpleteleporters.how_to_link", sneakKey, useKey);
			info.setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE));
			tooltip.add(info);
		} else {
			GlobalPos globalPos = stack.get(SimpleTeleportersComponents.GLOBAL_POS);
			BlockPos pos = globalPos.pos();
			ResourceKey<Level> dimension = globalPos.dimension();
			MutableComponent component = Component.translatable("text.simpleteleporters.linked",
					pos.getX(), pos.getY(), pos.getZ(), dimension.location());
			component.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));

			tooltip.add(component);
		}
	}
}
