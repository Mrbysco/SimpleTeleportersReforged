package com.mrbysco.simpleteleporters.datagen.assets;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersSoundEvents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class SimpleSoundProvider extends SoundDefinitionsProvider {

	public SimpleSoundProvider(PackOutput packOutput) {
		super(packOutput, SimpleTeleporters.MOD_ID);
	}

	@Override
	public void registerSounds() {
		this.add(SimpleTeleportersSoundEvents.TELEPORTER_TELEPORT, definition()
				.subtitle(modSubtitle(SimpleTeleportersSoundEvents.TELEPORTER_TELEPORT.getId()))
				.with(
						sound(mcLoc("mob/endermen/portal")),
						sound(mcLoc("mob/endermen/portal2"))
				));

		this.add(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_INSERTED, definition()
				.subtitle(modSubtitle(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_INSERTED.getId()))
				.with(
						sound(mcLoc("random/pop"))
				));
		this.add(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_REMOVED, definition()
				.subtitle(modSubtitle(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_INSERTED.getId()))
				.with(
						sound(mcLoc("random/pop"))
				));
		this.add(SimpleTeleportersSoundEvents.ENDER_SHARD_LINK, definition()
				.subtitle(modSubtitle(SimpleTeleportersSoundEvents.ENDER_SHARD_LINK.getId()))
				.with(
						sound(mcLoc("mob/endermen/portal")),
						sound(mcLoc("mob/endermen/portal2"))
				));
	}

	private Identifier mcLoc(String path) {
		return Identifier.withDefaultNamespace(path);
	}

	private String modSubtitle(Identifier id) {
		return SimpleTeleporters.MOD_ID + ".subtitle." + id.getPath();
	}
}
