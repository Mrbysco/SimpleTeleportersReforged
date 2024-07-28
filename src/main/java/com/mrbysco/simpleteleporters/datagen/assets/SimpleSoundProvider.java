package com.mrbysco.simpleteleporters.datagen.assets;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersSoundEvents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class SimpleSoundProvider extends SoundDefinitionsProvider {

	public SimpleSoundProvider(PackOutput packOutput, ExistingFileHelper helper) {
		super(packOutput, SimpleTeleporters.MOD_ID, helper);
	}

	@Override
	public void registerSounds() {
		this.add(SimpleTeleportersSoundEvents.TELEPORTER_TELEPORT, definition()
				.subtitle(modSubtitle(SimpleTeleportersSoundEvents.TELEPORTER_TELEPORT.getId()))
				.with(
						sound(ResourceLocation.withDefaultNamespace("mob/endermen/portal")),
						sound(ResourceLocation.withDefaultNamespace("mob/endermen/portal2"))
				));

		this.add(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_INSERTED, definition()
				.subtitle(modSubtitle(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_INSERTED.getId()))
				.with(
						sound(ResourceLocation.withDefaultNamespace("random/pop"))
				));
		this.add(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_REMOVED, definition()
				.subtitle(modSubtitle(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_INSERTED.getId()))
				.with(
						sound(ResourceLocation.withDefaultNamespace("random/pop"))
				));
		this.add(SimpleTeleportersSoundEvents.ENDER_SHARD_LINK, definition()
				.subtitle(modSubtitle(SimpleTeleportersSoundEvents.ENDER_SHARD_LINK.getId()))
				.with(
						sound(ResourceLocation.withDefaultNamespace("mob/endermen/portal")),
						sound(ResourceLocation.withDefaultNamespace("mob/endermen/portal2"))
				));
	}

	public String modSubtitle(ResourceLocation id) {
		return SimpleTeleporters.MOD_ID + ".subtitle." + id.getPath();
	}
}
