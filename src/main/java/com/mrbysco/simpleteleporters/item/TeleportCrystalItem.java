package com.mrbysco.simpleteleporters.item;

import com.mojang.serialization.Codec;
import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.data.SubLevelBinding;
import com.mrbysco.simpleteleporters.integration.SableIntegration;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersComponents;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

			if (SimpleTeleporters.isSableIntegrationActive()) {
				Optional<SubLevelBinding> binding = SableIntegration.bindClick(level, pos);
				if (binding.isPresent()) {
					SubLevelBinding b = binding.get();
					stack.set(SimpleTeleportersComponents.SUB_LEVEL_BINDING.get(), b);
					stack.remove(SimpleTeleportersComponents.GLOBAL_POS.get());

					if (!player.addItem(stack)) {
						player.drop(stack, false);
					}

					MutableComponent msg = Component.translatable("text.simpleteleporters.airship_link",
							shortUuid(b.subUuid()));
					msg.setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA));
					player.displayClientMessage(msg, true);

					player.playSound(SimpleTeleportersSoundEvents.ENDER_SHARD_LINK.get(), 0.5F,
							0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

					return InteractionResult.SUCCESS;
				}
			}

			BlockPos offsetPos;
			if (level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
				offsetPos = pos;
			} else if (level.getBlockState(pos).is(SimpleTeleportersBlocks.TELEPORTER.get())) {
				offsetPos = pos.above();
			} else {
				offsetPos = pos.relative(ctx.getClickedFace());
			}
			stack.set(SimpleTeleportersComponents.GLOBAL_POS, GlobalPos.of(player.level().dimension(), offsetPos));
			stack.remove(SimpleTeleportersComponents.SUB_LEVEL_BINDING.get());
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
		SubLevelBinding sub = stack.get(SimpleTeleportersComponents.SUB_LEVEL_BINDING.get());
		if (sub != null) {
			MutableComponent component = Component.translatable("text.simpleteleporters.linked_airship",
					shortUuid(sub.subUuid()));
			component.setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA));
			tooltip.add(component);
			return;
		}

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
			Component dimensionName = Component.translatable(dimension.location().toLanguageKey("dimension"));
			MutableComponent component = Component.translatable("text.simpleteleporters.linked",
					pos.getX(), pos.getY(), pos.getZ(), dimensionName);
			component.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));

			tooltip.add(component);
		}
	}

	public static String shortUuid(UUID uuid) {
		String s = uuid.toString();
		int dash = s.indexOf('-');
		return dash > 0 ? s.substring(0, dash) : s;
	}
}
