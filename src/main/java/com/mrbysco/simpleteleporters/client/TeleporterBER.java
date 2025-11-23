package com.mrbysco.simpleteleporters.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.simpleteleporters.block.entity.TeleporterBlockEntity;
import com.mrbysco.simpleteleporters.config.SimpleTeleportersConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

public class TeleporterBER implements BlockEntityRenderer<TeleporterBlockEntity> {
	private final Font font;
	private final EntityRenderDispatcher entityRenderDispatcher;

	public TeleporterBER(BlockEntityRendererProvider.Context context) {
		this.font = context.getFont();
		this.entityRenderDispatcher = context.getEntityRenderer();
	}

	@Override
	public void render(TeleporterBlockEntity blockEntity, float partialTick, PoseStack poseStack,
	                   MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (!SimpleTeleportersConfig.CLIENT.disableNameplate.getAsBoolean() && blockEntity.hasCrystal()) {
			ItemStack crystalStack = blockEntity.getCrystal();
			if (crystalStack.has(DataComponents.CUSTOM_NAME)) {
				// Render the nameplate above the teleporter
				Component displayName = crystalStack.getHoverName();
				poseStack.pushPose();
				poseStack.translate(0.5F, 2.0F, 0.5F);
				poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
				poseStack.scale(0.025F, -0.025F, 0.025F);
				Matrix4f matrix4f = poseStack.last().pose();
				float f1 = (float) (-font.width(displayName) / 2);
				float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
				int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;
				font.drawInBatch(
						displayName, f1, (float) 0, FastColor.ARGB32.opaque(-1), false, matrix4f, bufferSource, Font.DisplayMode.SEE_THROUGH, backgroundColor, packedLight
				);
				poseStack.popPose();
			}
		}
	}

	@Override
	public AABB getRenderBoundingBox(TeleporterBlockEntity blockEntity) {
		return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
	}
}
