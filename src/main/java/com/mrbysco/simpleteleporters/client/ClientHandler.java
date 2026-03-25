package com.mrbysco.simpleteleporters.client;

import com.mrbysco.simpleteleporters.block.entity.TeleporterBlockEntity;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlockEntities;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersComponents;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;

@EventBusSubscriber(Dist.CLIENT)
public class ClientHandler {
	@SubscribeEvent
	public static void onRender(RenderLevelStageEvent.AfterTranslucentBlocks event) {
		final Minecraft minecraft = Minecraft.getInstance();
		final LocalPlayer player = minecraft.player;
		final Level level = minecraft.level;
		if (player != null && level != null) {
			final RandomSource random = level.getRandom();
			for (InteractionHand hand : InteractionHand.values()) {
				ItemStack stack = player.getItemInHand(hand);
				if (!stack.isEmpty() && (stack.is(SimpleTeleportersItems.ENDER_SHARD.get()) || stack.is(SimpleTeleportersItems.ENHANCED_ENDER_SHARD.get()))) {
					if (stack.has(SimpleTeleportersComponents.GLOBAL_POS)) {
						GlobalPos globalPos = stack.get(SimpleTeleportersComponents.GLOBAL_POS);
						assert globalPos != null;
						ResourceKey<Level> dimension = globalPos.dimension();
						if (level.dimension().equals(dimension)) {
							BlockPos telePos = globalPos.pos();
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
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(SimpleTeleportersBlockEntities.TELEPORTER.get(), TeleporterBER::new);
	}

	public static void registerBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
		event.register(List.of(teleporter()), SimpleTeleportersBlocks.TELEPORTER.get());
	}

	public static BlockTintSource teleporter() {
		return new BlockTintSource() {
			public int color(BlockState state) {
				return DyeColor.WHITE.getTextureDiffuseColor();
			}

			public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
				if (level.getBlockEntity(pos) instanceof TeleporterBlockEntity teleporter) {
					return teleporter.getColor().getTextureDiffuseColor();
				}
				return DyeColor.WHITE.getTextureDiffuseColor();
			}
		};
	}
}
