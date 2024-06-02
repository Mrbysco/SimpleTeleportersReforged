package com.mrbysco.simpleteleporters.client;

import com.mrbysco.simpleteleporters.item.TeleportCrystalItem;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientHandler {
	@SubscribeEvent
	public static void onRender(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
			final Minecraft minecraft = Minecraft.getInstance();
			LocalPlayer player = minecraft.player;
			if (player != null) {
				Level level = player.level();
				if (level != null) {
					RandomSource random = level.getRandom();
					for (InteractionHand hand : InteractionHand.values()) {
						ItemStack stack = player.getItemInHand(hand);
						if (!stack.isEmpty() && stack.is(SimpleTeleportersItems.ENDER_SHARD.get())) {
							CompoundTag nbt = stack.getTag();
							if (TeleportCrystalItem.hasPosition(nbt)) {
								ResourceKey<Level> dimension = TeleportCrystalItem.getDimensionKey(nbt);
								if (level.dimension().equals(dimension)) {
									BlockPos telePos = TeleportCrystalItem.getPosition(nbt);
									if (telePos != null) {
										BlockPos downPos = telePos.below();
										if (level.getBlockState(downPos).is(SimpleTeleportersBlocks.TELEPORTER.get())) {
											telePos = downPos;
										}
										if (player.blockPosition().distManhattan(telePos) < 15) {
											level.addParticle(ParticleTypes.MYCELIUM,
													random.triangle(telePos.getX() + 0.5, 0.2),
													random.triangle(telePos.getY() + 0.5, 0.2),
													random.triangle(telePos.getZ() + 0.5, 0.2),
													0, 0, 0);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
