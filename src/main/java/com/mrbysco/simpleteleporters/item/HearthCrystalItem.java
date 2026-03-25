package com.mrbysco.simpleteleporters.item;

import com.mrbysco.simpleteleporters.registry.SimpleTeleportersAttachments;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersAttachments.HearthData;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HearthCrystalItem extends Item {
    private static final int TELEPORT_DELAY_TICKS = 60; // 3 seconds
    public static final int MAX_CHARGES = 20;

    public HearthCrystalItem(Properties properties) {
        super(properties);
    }

    public static int getCharges(ItemStack stack) {
        return stack.getOrDefault(SimpleTeleportersComponents.CHARGES, MAX_CHARGES);
    }

    public static void setCharges(ItemStack stack, int charges) {
        stack.set(SimpleTeleportersComponents.CHARGES, Mth.clamp(charges, 0, MAX_CHARGES));
    }

    public static void consumeCharge(ItemStack stack) {
        setCharges(stack, getCharges(stack) - 1);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getCharges(stack) < MAX_CHARGES;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getCharges(stack) / MAX_CHARGES);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float ratio = (float) getCharges(stack) / MAX_CHARGES;
        return Mth.hsvToRgb(ratio / 3.0F, 1.0F, 1.0F);
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (context.getHand() != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos clickedPos = context.getClickedPos();
        BlockPos targetPos;
        if (level.getBlockState(clickedPos).getCollisionShape(level, clickedPos).isEmpty()) {
            targetPos = clickedPos;
        } else if (level.getBlockState(clickedPos).is(SimpleTeleportersBlocks.TELEPORTER.get())) {
            targetPos = clickedPos.above();
        } else {
            targetPos = clickedPos.relative(context.getClickedFace());
        }

        GlobalPos globalPos = GlobalPos.of(level.dimension(), targetPos);
        HearthData currentData = player.getData(SimpleTeleportersAttachments.HEARTH_DATA);
        player.setData(SimpleTeleportersAttachments.HEARTH_DATA, currentData.withBoundLocation(globalPos));

        String dimensionName = level.dimension().identifier().toString();
        player.sendOverlayMessage(Component.translatable("text.simpleteleporters.hearth_info",
                targetPos.getX(), targetPos.getY(), targetPos.getZ(), dimensionName));
        player.playSound(SimpleTeleportersSoundEvents.ENDER_SHARD_LINK.get(), 0.5F,
                0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        HearthData hearthData = player.getData(SimpleTeleportersAttachments.HEARTH_DATA);

        if (hearthData.isTeleporting()) {
            player.sendOverlayMessage(Component.translatable("text.simpleteleporters.hearth_already_teleporting")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }
        if (!hearthData.hasBoundLocation()) {
            player.sendOverlayMessage(Component.translatable("text.simpleteleporters.invalid_hearth_target")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }
        if (getCharges(stack) <= 0) {
            player.sendOverlayMessage(Component.translatable("text.simpleteleporters.hearth_no_charges")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        player.setData(SimpleTeleportersAttachments.HEARTH_DATA, hearthData.withTeleportTimer(TELEPORT_DELAY_TICKS));
        player.sendOverlayMessage(Component.translatable("text.simpleteleporters.hearth_teleporting")
                .withStyle(ChatFormatting.GOLD));
        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        HearthData hearthData = player.getData(SimpleTeleportersAttachments.HEARTH_DATA);
        if (!hearthData.isTeleporting()) {
            return;
        }

        ItemStack mainHandItem = player.getMainHandItem();
        if (!(mainHandItem.getItem() instanceof HearthCrystalItem)) {
            player.setData(SimpleTeleportersAttachments.HEARTH_DATA, hearthData.withTeleportTimer(0));
            player.sendOverlayMessage(Component.translatable("text.simpleteleporters.hearth_cancelled")
                    .withStyle(ChatFormatting.RED));
            return;
        }

        int timer = hearthData.teleportTimer();
        if (timer > 1) {
            player.setData(SimpleTeleportersAttachments.HEARTH_DATA, hearthData.withTeleportTimer(timer - 1));
            return;
        }

        player.setData(SimpleTeleportersAttachments.HEARTH_DATA, hearthData.withTeleportTimer(0));
        if (!hearthData.hasBoundLocation()) {
            return;
        }

        GlobalPos globalPos = hearthData.boundLocation().get();
        BlockPos targetPos = globalPos.pos();
        ResourceKey<Level> targetDimension = globalPos.dimension();

        double destX = targetPos.getX() + 0.5D;
        double destY = targetPos.getY();
        double destZ = targetPos.getZ() + 0.5D;

        MinecraftServer server = level.getServer();
        if (server == null) return;

        if (!level.dimension().equals(targetDimension)) {
            ServerLevel destinationLevel = server.getLevel(targetDimension);
            if (destinationLevel != null) {
                player.teleportTo(destinationLevel, destX, destY, destZ, java.util.Set.of(), player.getYRot(), player.getXRot(), true);
            }
        } else {
            player.teleportTo(destX, destY, destZ);
        }
        player.fallDistance = 0;
        player.playSound(SimpleTeleportersSoundEvents.TELEPORTER_TELEPORT.get(), 1.0F, 1.0F);
        consumeCharge(mainHandItem);
    }

    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int charges = getCharges(stack);
        ChatFormatting chargeColor = charges > 5 ? ChatFormatting.GREEN : (charges > 0 ? ChatFormatting.YELLOW : ChatFormatting.RED);
        MutableComponent chargesInfo = Component.translatable("text.simpleteleporters.hearth_charges", charges, MAX_CHARGES);
        chargesInfo.setStyle(Style.EMPTY.withColor(chargeColor));
        tooltip.add(chargesInfo);

        Component sneakKey = Component.literal("Sneak");
        Component useKey = Component.literal("Right Click");

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            sneakKey = Component.keybind(Minecraft.getInstance().options.keyShift.getName());
            useKey = Component.keybind(Minecraft.getInstance().options.keyUse.getName());
        }

        MutableComponent bindInfo = Component.translatable("text.simpleteleporters.hearth_bind_hint", sneakKey, useKey);
        bindInfo.setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE));
        tooltip.add(bindInfo);

        MutableComponent useInfo = Component.translatable("text.simpleteleporters.hearth_use_hint", useKey);
        useInfo.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
        tooltip.add(useInfo);

        MutableComponent repairInfo = Component.translatable("text.simpleteleporters.hearth_repair_hint");
        repairInfo.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
        tooltip.add(repairInfo);
    }
}
