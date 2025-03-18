package com.mrbysco.simpleteleporters.item;

import com.mrbysco.simpleteleporters.registry.SimpleTeleportersComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
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

import javax.annotation.Nullable;
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
        InteractionHand hand = context.getHand();

        // Check if the player is sneaking (shift-clicking)
        if (player != null && player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                // Set the position component on THIS specific item
                GlobalPos globalPos = GlobalPos.of(level.dimension(), blockPos);
                itemStack.set(SimpleTeleportersComponents.GLOBAL_POS.get(), globalPos);

                // Display confirmation message
                player.displayClientMessage(Component.translatable("message.simpleteleporters.set_crystal")
                        .append(String.format(" X: %d, Y: %d, Z: %d", blockPos.getX(), blockPos.getY(), blockPos.getZ())), true);
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
        if (itemstack.has(SimpleTeleportersComponents.GLOBAL_POS.get())) {
            // Set teleport timer on THIS specific item
            itemstack.set(SimpleTeleportersComponents.TELEPORT_TIMER.get(), 60); // 3 seconds (60 ticks)

            // You might want to play a sound or show a particle effect here
            playerIn.displayClientMessage(Component.translatable("message.simpleteleporters.teleporting"), true);

            return InteractionResultHolder.consume(itemstack);
        }

        // If we couldn't teleport, let the player know
        playerIn.displayClientMessage(Component.translatable("message.simpleteleporters.unset_crystal"), true);
        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entityIn, int itemSlot, boolean isSelected) {
        if (level.isClientSide || !(entityIn instanceof Player)) {
            return;
        }

        Player player = (Player) entityIn;

        // Check if this specific item has a teleport timer
        if (stack.has(SimpleTeleportersComponents.TELEPORT_TIMER.get())) {
            int timer = stack.get(SimpleTeleportersComponents.TELEPORT_TIMER.get());

            if (timer > 0) {
                timer--;
                stack.set(SimpleTeleportersComponents.TELEPORT_TIMER.get(), timer);

                // Optional: Add particles or sounds every tick while waiting
            } else {
                // Timer has expired, remove the timer
                stack.remove(SimpleTeleportersComponents.TELEPORT_TIMER.get());

                // Check if this specific item has position data
                if (stack.has(SimpleTeleportersComponents.GLOBAL_POS.get())) {
                    GlobalPos pos = stack.get(SimpleTeleportersComponents.GLOBAL_POS.get());

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
                                serverPlayer.teleportTo(destinationLevel, destinationX + 0.5D, destinationY + 0.5D, destinationZ + 0.5D, serverPlayer.getYRot(), serverPlayer.getXRot());
                                serverPlayer.fallDistance = 0;
                            }
                        }
                    } else {
                        player.teleportTo(destinationX + 0.5D, destinationY + 0.5D, destinationZ + 0.5D);
                        player.fallDistance = 0;
                    }
                }
            }
        }
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        if (stack.has(SimpleTeleportersComponents.GLOBAL_POS.get())) {
            GlobalPos pos = stack.get(SimpleTeleportersComponents.GLOBAL_POS.get());

            tooltip.add(Component.translatable("tooltip.simpleteleporters.x").append(": " + pos.pos().getX()));
            tooltip.add(Component.translatable("tooltip.simpleteleporters.y").append(": " + pos.pos().getY()));
            tooltip.add(Component.translatable("tooltip.simpleteleporters.z").append(": " + pos.pos().getZ()));
            tooltip.add(Component.translatable("tooltip.simpleteleporters.dim").append(": " + pos.dimension().location()));

            // Optionally show teleport timer if active
            if (stack.has(SimpleTeleportersComponents.TELEPORT_TIMER.get())) {
                int timer = stack.get(SimpleTeleportersComponents.TELEPORT_TIMER.get());
                tooltip.add(Component.translatable("tooltip.simpleteleporters.teleporting_in").append(": " + (timer / 20) + "s"));
            }
        }
    }
}