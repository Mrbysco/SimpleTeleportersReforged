package com.mrbysco.simpleteleporters.integration;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.data.SubLevelBinding;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

// Callers must guard with SimpleTeleporters.isSableLoaded() so Sable types aren't class-loaded
// when Sable is absent.
public final class SableIntegration {
	private SableIntegration() {
	}

	public static Optional<SubLevelBinding> bindClick(Level level, BlockPos clickedPos) {
		SubLevel sub = Sable.HELPER.getContaining(level, clickedPos);
		if (sub == null || sub.isRemoved()) {
			return Optional.empty();
		}
		Vec3 plotPos = new Vec3(clickedPos.getX() + 0.5, clickedPos.getY() + 1.0, clickedPos.getZ() + 0.5);
		return Optional.of(new SubLevelBinding(sub.getUniqueId(), plotPos));
	}

	public static Optional<SubLevelLookup> findSub(MinecraftServer server, java.util.UUID uuid) {
		for (ServerLevel level : server.getAllLevels()) {
			SubLevelContainer container = SubLevelContainer.getContainer(level);
			if (container == null) continue;
			SubLevel sub = container.getSubLevel(uuid);
			if (sub != null && !sub.isRemoved()) {
				return Optional.of(new SubLevelLookup(level, sub));
			}
		}
		return Optional.empty();
	}

	public static Vec3 projectToWorld(SubLevelLookup lookup, Vec3 plotPos) {
		return Sable.HELPER.projectOutOfSubLevel(lookup.level(), plotPos);
	}

	public record SubLevelLookup(ServerLevel level, SubLevel sub) {
		public ResourceKey<Level> dimension() {
			return level.dimension();
		}
	}
}
