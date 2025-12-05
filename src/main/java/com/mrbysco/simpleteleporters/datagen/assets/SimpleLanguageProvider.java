package com.mrbysco.simpleteleporters.datagen.assets;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersBlocks;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersSoundEvents;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class SimpleLanguageProvider extends LanguageProvider {
	public SimpleLanguageProvider(PackOutput packOutput) {
		super(packOutput, SimpleTeleporters.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		addItem(SimpleTeleportersItems.ENDER_SHARD, "Ender Shard");
		addItem(SimpleTeleportersItems.HEARTH_CRYSTAL, "Hearth Crystal");
		addBlock(SimpleTeleportersBlocks.TELEPORTER, "Teleporter");

		addSubtitle(SimpleTeleportersSoundEvents.TELEPORTER_TELEPORT, "Teleporter teleports");
		addSubtitle(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_INSERTED, "Ender Shard inserted");
		addSubtitle(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_REMOVED, "Ender Shard removed");
		addSubtitle(SimpleTeleportersSoundEvents.ENDER_SHARD_LINK, "Ender Shard link established");

		add("text.simpleteleporters.unlinked", "Unlinked");
		add("text.simpleteleporters.how_to_link_hearth", "%1$s + %2$s on a block to link a position");
		add("text.simpleteleporters.invalid_hearth_target", "Invalid Hearth Crystal target!");
		add("text.simpleteleporters.linked_hearth", "Linked Hearth Crystal to %1$s, %2$s, %3$s in %4$s");
		add("text.simpleteleporters.linked", "Linked, %1$s, %2$s, %3$s in %4$s");
		add("text.simpleteleporters.how_to_link", "%1$s + %2$s on a block to link a position");
		add("text.simpleteleporters.hearth_info", "Linked Hearth Crystal to %1$s, %2$s, %3$s in %4$s");
		add("text.simpleteleporters.hearth_teleporting", "Teleporting in 3 seconds...");
		add("text.simpleteleporters.hearth_countdown", "Teleporting in %1$s seconds...");
		add("text.simpleteleporters.crystal_info", "Linked Ender Shard to %1$s, %2$s, %3$s in %4$s");
		add("text.simpleteleporters.error.no_crystal", "This teleporter doesn't have an Ender Shard!");
		add("text.simpleteleporters.error.unlinked_teleporter", "This teleporter's Ender Shard is unlinked!");
		add("text.simpleteleporters.error.unlinked_shard", "This Ender Shard is unlinked!");
		add("text.simpleteleporters.error.invalid_position", "Teleport position is invalid! Perhaps there's a block in the way?");
		add("text.simpleteleporters.error.wrong_dimension", "This teleporter's Ender Shard isn't powerful enough to cross dimensions!");

		addConfig("client", "Client", "Client Settings");
		addConfig("disableParticles", "Disable Particles", "Disable the particles shown above an active teleporter");
		addConfig("disableNameplate", "Disable Nameplate", "Disable the nameplate shown above a teleporter with a named crystal");
	}

	public void addSubtitle(DeferredHolder<SoundEvent, SoundEvent> sound, String name) {
		String path = SimpleTeleporters.MOD_ID + sound.getId().getPath() + ".subtitle.";
		this.add(path, name);
	}

	/**
	 * Add the translation for a config entry
	 *
	 * @param path        The path of the config entry
	 * @param name        The name of the config entry
	 * @param description The description of the config entry (optional in case of targeting "title" or similar entries that have no tooltip)
	 */
	private void addConfig(String path, String name, @Nullable String description) {
		this.add(SimpleTeleporters.MOD_ID + ".configuration." + path, name);
		if (description != null && !description.isEmpty())
			this.add(SimpleTeleporters.MOD_ID + ".configuration." + path + ".tooltip", description);
	}
}