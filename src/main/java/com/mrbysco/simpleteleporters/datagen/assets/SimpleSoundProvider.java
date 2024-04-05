package com.mrbysco.simpleteleporters.datagen.assets;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersSoundEvents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

public class SimpleSoundProvider extends SoundDefinitionsProvider {

	public SimpleSoundProvider(PackOutput packOutput, ExistingFileHelper helper) {
		super(packOutput, SimpleTeleporters.MOD_ID, helper);
	}

	@Override
	public void registerSounds() {
		this.add(SimpleTeleportersSoundEvents.TELEPORTER_TELEPORT, definition()
				.subtitle(modSubtitle(SimpleTeleportersSoundEvents.TELEPORTER_TELEPORT.getId()))
				.with(
						sound(new ResourceLocation("mob/endermen/portal")),
						sound(new ResourceLocation("mob/endermen/portal2"))
				));

		this.add(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_INSERTED, definition()
				.subtitle(modSubtitle(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_INSERTED.getId()))
				.with(
						sound(new ResourceLocation("random/pop"))
				));
		this.add(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_REMOVED, definition()
				.subtitle(modSubtitle(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_INSERTED.getId()))
				.with(
						sound(new ResourceLocation("random/pop"))
				));
		this.add(SimpleTeleportersSoundEvents.ENDER_SHARD_LINK, definition()
				.subtitle(modSubtitle(SimpleTeleportersSoundEvents.ENDER_SHARD_LINK.getId()))
				.with(
						sound(new ResourceLocation("mob/endermen/portal")),
						sound(new ResourceLocation("mob/endermen/portal2"))
				));
	}

	public String modSubtitle(ResourceLocation id) {
		return SimpleTeleporters.MOD_ID + ".subtitle." + id.getPath();
	}
}
