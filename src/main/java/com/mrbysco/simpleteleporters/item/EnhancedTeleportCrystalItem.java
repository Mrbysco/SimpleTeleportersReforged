package com.mrbysco.simpleteleporters.item;

import com.mrbysco.simpleteleporters.data.SubLevelBinding;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.List;

public class EnhancedTeleportCrystalItem extends TeleportCrystalItem {

	public EnhancedTeleportCrystalItem(Properties settings) {
		super(settings);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
		// Add enhanced indicator
		MutableComponent enhanced = Component.translatable("text.simpleteleporters.enhanced");
		enhanced.setStyle(Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE));
		tooltip.add(enhanced);

		SubLevelBinding sub = stack.get(SimpleTeleportersComponents.SUB_LEVEL_BINDING.get());
		if (sub != null) {
			MutableComponent component = Component.translatable("text.simpleteleporters.linked_airship",
					TeleportCrystalItem.shortUuid(sub.subUuid()));
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

	@Override
	public boolean isFoil(ItemStack stack) {
		// Give it an enchantment glint to show it's special
		return true;
	}
}
