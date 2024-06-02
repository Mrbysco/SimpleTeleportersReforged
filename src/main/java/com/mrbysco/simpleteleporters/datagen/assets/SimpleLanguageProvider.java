package com.mrbysco.simpleteleporters.datagen.assets;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersSoundEvents;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SimpleLanguageProvider extends LanguageProvider {
	public SimpleLanguageProvider(PackOutput packOutput) {
		super(packOutput, SimpleTeleporters.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		addItem(SimpleTeleportersItems.ENDER_SHARD, "Ender Shard");
		addBlock(SimpleTeleportersBlocks.TELEPORTER, "Teleporter");

		addSubtitle(SimpleTeleportersSoundEvents.TELEPORTER_TELEPORT, "Teleporter teleports");
		addSubtitle(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_INSERTED, "Ender Shard inserted");
		addSubtitle(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_REMOVED, "Ender Shard removed");
		addSubtitle(SimpleTeleportersSoundEvents.ENDER_SHARD_LINK, "Ender Shard link established");

		add("text.simpleteleporters.unlinked", "Unlinked");
		add("text.simpleteleporters.linked", "Linked, %1$s, %2$s, %3$s in %4$s");
		add("text.simpleteleporters.how_to_link", "%1$s + %2$s on a block to link a position");
		add("text.simpleteleporters.crystal_info", "Linked Ender Shard to %1$s, %2$s, %3$s in %4$s");
		add("text.simpleteleporters.error.no_crystal", "This teleporter doesn't have an Ender Shard!");
		add("text.simpleteleporters.error.unlinked_teleporter", "This teleporter's Ender Shard is unlinked!");
		add("text.simpleteleporters.error.unlinked_shard", "This Ender Shard is unlinked!");
		add("text.simpleteleporters.error.invalid_position", "Teleport position is invalid! Perhaps there's a block in the way?");
		add("text.simpleteleporters.error.wrong_dimension", "This teleporter's Ender Shard isn't powerful enough to cross dimensions!");
	}

	public void addSubtitle(DeferredHolder<SoundEvent, SoundEvent> sound, String name) {
		String path = SimpleTeleporters.MOD_ID + sound.getId().getPath() + ".subtitle.";
		this.add(path, name);
	}
}