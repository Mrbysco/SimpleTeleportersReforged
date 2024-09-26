package com.mrbysco.simpleteleporters.item;

import com.mrbysco.simpleteleporters.block.entity.TeleporterBlockEntity;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.List;

import static java.util.Objects.isNull;

public class HearthCrystalItem extends Item {


    public HearthCrystalItem(Properties properties) {
        super(new Properties()
                .fireResistant()
                .durability(200));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            if (!stack.has(SimpleTeleportersComponents.GLOBAL_POS)) {
                player.displayClientMessage(Component.literal("Not Yet Bound").withStyle(ChatFormatting.GREEN), true);
            } else {
                level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.8F, 0.5F);
                player.displayClientMessage(Component.literal("Initiating Teleport!").withStyle(ChatFormatting.RED), true);
                BlockPos pos = stack.get(SimpleTeleportersComponents.GLOBAL_POS).pos();
                BlockEntity teleporterEntity = level.getBlockEntity(pos.below());
                if (teleporterEntity instanceof TeleporterBlockEntity teleporter) {
                    serverPlayer.hurtMarked = true;

                    Vec3 playerPos = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    serverPlayer.connection.teleport(playerPos.x(), playerPos.y(), playerPos.z(), serverPlayer.getYRot(), serverPlayer.getXRot());

                    serverPlayer.setDeltaMovement(0, 0, 0);
                    serverPlayer.hasImpulse = true;

                    level.playSound(null, player, SimpleTeleportersSoundEvents.TELEPORTER_TELEPORT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    teleporter.setCooldown(10);

                    BlockEntity down = level.getBlockEntity(pos.below());
                    if (down instanceof TeleporterBlockEntity tpDown) {
                        tpDown.setCooldown(10);
                    }
                }
                else{
                    player.displayClientMessage(Component.literal("Target block is not a teleporter!").withStyle(ChatFormatting.RED), true);
                }
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.isSecondaryUseActive()) {
            Player player = ctx.getPlayer();

            ItemStack stack = ctx.getItemInHand().split(1);

            Level level = ctx.getLevel();
            BlockPos pos = ctx.getClickedPos();
            BlockPos offsetPos;
            String dimensionName = player.level().dimension().location().toString();

            if (level.getBlockState(pos).is(SimpleTeleportersBlocks.TELEPORTER.get())) {
                offsetPos = pos.above();
            } else {
                offsetPos = pos.relative(ctx.getClickedFace());
                MutableComponent Errormsg = Component.translatable("text.simpleteleporters.invalid_hearth_target");
                Errormsg.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
                player.displayClientMessage(Errormsg, true);
                return InteractionResult.PASS;
            }
            stack.set(SimpleTeleportersComponents.GLOBAL_POS, GlobalPos.of(player.level().dimension(), offsetPos));


            if (!player.addItem(stack)) {
                player.drop(stack, false);
            }

            MutableComponent msg = Component.translatable("text.simpleteleporters.hearth_info",
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

            MutableComponent info = Component.translatable("text.simpleteleporters.how_to_link_hearth", sneakKey, useKey);
            info.setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE));
            tooltip.add(info);
        } else {
            GlobalPos globalPos = stack.get(SimpleTeleportersComponents.GLOBAL_POS);
            BlockPos pos = globalPos.pos();
            System.out.println("pos = " + pos);
            ResourceKey<Level> dimension = globalPos.dimension();
            MutableComponent linked = Component.translatable("text.simpleteleporters.linked_hearth",
                    pos.getX(), pos.getY(), pos.getZ(), dimension.location());
            linked.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));

            tooltip.add(linked);
        }
    }
}
