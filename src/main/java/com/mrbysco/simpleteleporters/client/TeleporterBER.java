package com.mrbysco.simpleteleporters.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.simpleteleporters.block.entity.TeleporterBlockEntity;
import com.mrbysco.simpleteleporters.config.SimpleTeleportersConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TeleporterBER implements BlockEntityRenderer<TeleporterBlockEntity, TeleporterRenderState> {
	private final Font font;
	private final EntityRenderDispatcher entityRenderDispatcher;

	public TeleporterBER(BlockEntityRendererProvider.Context context) {
		this.font = context.font();
		this.entityRenderDispatcher = context.entityRenderer();
	}

	@Override
	public TeleporterRenderState createRenderState() {
		return new TeleporterRenderState();
	}

	@Override
	public void extractRenderState(TeleporterBlockEntity blockEntity, TeleporterRenderState renderState,
	                               float partialTick, Vec3 cameraPosition,
	                               @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
		if (blockEntity.hasCrystal() && blockEntity.getCrystal().has(DataComponents.CUSTOM_NAME)) {
			renderState.name = blockEntity.getCrystal().getHoverName();
		} else {
			renderState.name = null;
		}
	}

	@Override
	public void submit(TeleporterRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
		if (!SimpleTeleportersConfig.CLIENT.disableNameplate.getAsBoolean() && renderState.name != null) {
			// Render the nameplate above the teleporter
			Component displayName = renderState.name;
			poseStack.pushPose();
			poseStack.translate(0.5F, 2.0F, 0.5F);
			poseStack.mulPose(this.entityRenderDispatcher.camera.rotation());
			poseStack.scale(0.025F, -0.025F, 0.025F);
			float f1 = (float) (-font.width(displayName) / 2);
			float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
			int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;
			nodeCollector.submitText(
					poseStack, f1, 0, displayName.getVisualOrderText(), false,
					Font.DisplayMode.POLYGON_OFFSET, renderState.lightCoords, ARGB.opaque(-1), backgroundColor, 0
			);
			poseStack.popPose();
		}
	}

	@Override
	public AABB getRenderBoundingBox(TeleporterBlockEntity blockEntity) {
		return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
	}
}
