package com.mrbysco.simpleteleporters.item;

import com.mrbysco.simpleteleporters.block.entity.TeleporterBlockEntity;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersComponents;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class HearthCrystalItem extends Item {
    boolean isTeleporting = false;
    int charge = 0;

    public HearthCrystalItem(Properties properties) {
        super(new Properties()
                .fireResistant()
                .durability(200)
        );
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int itemSlot, boolean isSelected) {

        if (!this.isTeleporting) {
            return;
        }
        //TODO: cancel tp if player takes damage or moves and config is set to do so
        if (this.isTeleporting && entity instanceof Player player) {
            if (this.charge < 100) {
                this.charge++;
                if (this.charge == 1) {
                    level.playSound(player, player.blockPosition(), SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.5F, 0.5F);
                }
            } else {
                this.isTeleporting = false;
                this.charge = 0;
                itemStack.setDamageValue(itemStack.getDamageValue() + 50);
                GlobalPos globalPos = itemStack.get(SimpleTeleportersComponents.GLOBAL_POS);
                BlockPos pos = globalPos.pos();
                if (!level.isClientSide()) {
                    System.out.println("pos = " + pos);
                    System.out.println("clientside = true ");
                    level.playSound(player, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 0.5F);
                    BlockEntity teleporterEntity = level.getBlockEntity(pos.below());
                    if (teleporterEntity instanceof TeleporterBlockEntity teleporter) {
                        System.out.println("entity is instance of teleporter");
                        ServerPlayer serverPlayer = (ServerPlayer) player;
                        serverPlayer.hurtMarked = true;

                        Vec3 playerPos = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                        serverPlayer.connection.teleport(playerPos.x(), playerPos.y(), playerPos.z(), serverPlayer.getYRot(), serverPlayer.getXRot());
                        System.out.println("called server player connection teleport");
                        serverPlayer.setDeltaMovement(0, 0, 0);
                        serverPlayer.hasImpulse = true;

                        level.playSound(player, player, SimpleTeleportersSoundEvents.TELEPORTER_TELEPORT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        teleporter.setCooldown(10);

                        BlockEntity down = level.getBlockEntity(pos.below());
                        if (down instanceof TeleporterBlockEntity tpDown) {
                            tpDown.setCooldown(10);
                        }
                    }

                }


            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            if (!stack.has(SimpleTeleportersComponents.GLOBAL_POS)) {
                if (player.isCrouching()) {
                    return InteractionResultHolder.fail(player.getItemInHand(hand));
                }
                player.displayClientMessage(Component.literal("Not Yet Bound").withStyle(ChatFormatting.RED), true);
            } else {
                player.displayClientMessage(Component.literal("Initiating Teleport...").withStyle(ChatFormatting.GREEN), true);
                this.isTeleporting = true;
            }

        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.isSecondaryUseActive()) {
            Player player = ctx.getPlayer();

            ItemStack stack = ctx.getItemInHand();

            Level level = ctx.getLevel();
            BlockPos pos = ctx.getClickedPos();
            BlockPos offsetPos;
            String dimensionName = player.level().dimension().location().toString();

            if (level.getBlockState(pos).is(SimpleTeleportersBlocks.TELEPORTER.get())) {
                offsetPos = pos.above();
            } else {
                MutableComponent Errormsg = Component.translatable("text.simpleteleporters.invalid_hearth_target");
                Errormsg.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
                player.displayClientMessage(Errormsg, true);
                return InteractionResult.FAIL;
            }

            stack.set(SimpleTeleportersComponents.GLOBAL_POS, GlobalPos.of(player.level().dimension(), offsetPos));

            MutableComponent msg = Component.translatable("text.simpleteleporters.hearth_info",
                    offsetPos.getX(), offsetPos.getY(), offsetPos.getZ(), dimensionName);
            msg.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
            player.displayClientMessage(msg, true);

            player.playSound(SimpleTeleportersSoundEvents.ENDER_SHARD_LINK.get(), 0.5F,
                    0.4F / (ctx.getLevel().getRandom().nextFloat() * 0.4F + 0.8F));

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
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
            ResourceKey<Level> dimension = globalPos.dimension();
            String dimensionName = dimension.location().toString();
            MutableComponent linked = Component.translatable("text.simpleteleporters.linked_hearth",
                    pos.getX(), pos.getY(), pos.getZ(), dimensionName);
            linked.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));

            tooltip.add(linked);
        }
    }
}
