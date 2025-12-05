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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.List;

public class HearthCrystalItem extends Item {
    public HearthCrystalItem(Properties properties) {
        super(properties);
    }

    // Handle right-clicking a block (for setting the position)
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        ItemStack itemStack = context.getItemInHand();

        // Check if the player is sneaking (shift-clicking)
        if (player != null && player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                // Set the position component on THIS specific item
                BlockPos targetPos;
                if (level.getBlockState(blockPos).getCollisionShape(level, blockPos).isEmpty()) {
                    targetPos = blockPos;
                } else if (level.getBlockState(blockPos).is(SimpleTeleportersBlocks.TELEPORTER.get())) {
                    targetPos = blockPos.above();
                } else {
                    targetPos = blockPos.relative(context.getClickedFace());
                }
                GlobalPos globalPos = GlobalPos.of(level.dimension(), targetPos);
                itemStack.set(SimpleTeleportersComponents.GLOBAL_POS, globalPos);

                // Display confirmation message
                player.displayClientMessage(Component.translatable("text.simpleteleporters.hearth_info", targetPos.getX(), targetPos.getY(), targetPos.getZ(), level.dimension().location().toString())
                        , true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // If not sneaking, defer to normal use behavior
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);  // Get the specific item that was clicked

        if (level.isClientSide) {
            return InteractionResultHolder.success(itemstack);
        }

        // Check if this specific item has position data
        if (itemstack.has(SimpleTeleportersComponents.GLOBAL_POS)) {
            // Set teleport timer on THIS specific item
            itemstack.set(SimpleTeleportersComponents.TELEPORT_TIMER, 60); // 3 seconds (60 ticks)

            // You might want to play a sound or show a particle effect here
            playerIn.displayClientMessage(Component.translatable("text.simpleteleporters.hearth_teleporting"), true);

            return InteractionResultHolder.consume(itemstack);
        }

        // If we couldn't teleport, let the player know
        playerIn.displayClientMessage(Component.translatable("text.simpleteleporters.invalid_hearth_target"), true);
        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entityIn, int itemSlot, boolean isSelected) {
        if (level.isClientSide || !(entityIn instanceof Player)) {
            return;
        }

        Player player = (Player) entityIn;

        // Check if this specific item has a teleport timer
        if (stack.has(SimpleTeleportersComponents.TELEPORT_TIMER)) {
            int timer = stack.get(SimpleTeleportersComponents.TELEPORT_TIMER);

            if (timer > 0) {
                timer--;
                stack.set(SimpleTeleportersComponents.TELEPORT_TIMER, timer);

                // Optional: Add particles or sounds every tick while waiting
            } else {
                // Timer has expired, remove the timer
                stack.remove(SimpleTeleportersComponents.TELEPORT_TIMER);

                // Check if this specific item has position data
                if (stack.has(SimpleTeleportersComponents.GLOBAL_POS)) {
                    GlobalPos pos = stack.get(SimpleTeleportersComponents.GLOBAL_POS);

                    // Get destination information from THIS specific item
                    double destinationX = pos.pos().getX();
                    double destinationY = pos.pos().getY();
                    double destinationZ = pos.pos().getZ();
                    ResourceKey<Level> dimension = pos.dimension();

                    if (!level.dimension().equals(dimension)) {
                        ServerPlayer serverPlayer = (ServerPlayer) player;
                        MinecraftServer server = level.getServer();
                        if (server != null) {
                            ServerLevel destinationLevel = server.getLevel(dimension);
                            if (destinationLevel != null) {
                                serverPlayer.teleportTo(destinationLevel, destinationX + 0.5D, destinationY, destinationZ + 0.5D, serverPlayer.getYRot(), serverPlayer.getXRot());
                                serverPlayer.fallDistance = 0;
                            }
                        }
                    } else {
                        player.teleportTo(destinationX + 0.5D, destinationY, destinationZ + 0.5D);
                        player.fallDistance = 0;
                    }
                }
            }
        }
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
            Component dimensionName = Component.translatable(dimension.location().toLanguageKey("dimension"));

            MutableComponent linked = Component.translatable("text.simpleteleporters.linked_hearth",
                    pos.getX(), pos.getY(), pos.getZ(), dimensionName);
            linked.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
            tooltip.add(linked);

            // Show teleport countdown if active
            if (stack.has(SimpleTeleportersComponents.TELEPORT_TIMER)) {
                int timer = stack.getOrDefault(SimpleTeleportersComponents.TELEPORT_TIMER, 0);
                MutableComponent countdown = Component.translatable("text.simpleteleporters.hearth_countdown", (timer / 20) + 1);
                countdown.setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
                tooltip.add(countdown);
            }
        }
    }
}