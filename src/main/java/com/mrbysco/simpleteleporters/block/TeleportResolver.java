package com.mrbysco.simpleteleporters.block;

import com.mrbysco.simpleteleporters.SimpleTeleporters;
import com.mrbysco.simpleteleporters.data.SubLevelBinding;
import com.mrbysco.simpleteleporters.integration.SableIntegration;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersComponents;
import com.mrbysco.simpleteleporters.registry.SimpleTeleportersItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class TeleportResolver {
	private TeleportResolver() {
	}

	public enum Error {
		NONE,
		UNLINKED,
		WRONG_DIMENSION,
		INVALID_POSITION,
		AIRSHIP_NOT_FOUND
	}

	public record Result(ServerLevel targetLevel, Vec3 worldPos, BlockPos blockPos, Error error) {
		public boolean ok() {
			return error == Error.NONE;
		}

		public static Result error(Error e) {
			return new Result(null, null, null, e);
		}
	}

	public static Result resolve(MinecraftServer server, ItemStack crystal, Entity entity) {
		if (crystal.isEmpty()) {
			return Result.error(Error.UNLINKED);
		}

		SubLevelBinding sub = crystal.get(SimpleTeleportersComponents.SUB_LEVEL_BINDING.get());
		if (sub != null) {
			if (!SimpleTeleporters.isSableIntegrationActive()) {
				return Result.error(Error.AIRSHIP_NOT_FOUND);
			}
			Optional<SableIntegration.SubLevelLookup> lookup = SableIntegration.findSub(server, sub.subUuid());
			if (lookup.isEmpty()) {
				return Result.error(Error.AIRSHIP_NOT_FOUND);
			}
			SableIntegration.SubLevelLookup l = lookup.get();
			boolean enhanced = crystal.is(SimpleTeleportersItems.ENHANCED_ENDER_SHARD.get());
			if (!enhanced && !l.dimension().equals(entity.level().dimension())) {
				return Result.error(Error.WRONG_DIMENSION);
			}
			Vec3 worldPos = SableIntegration.projectToWorld(l, sub.plotPos());
			return new Result(l.level(), worldPos, BlockPos.containing(worldPos), Error.NONE);
		}

		GlobalPos globalPos = crystal.get(SimpleTeleportersComponents.GLOBAL_POS);
		if (globalPos == null) {
			return Result.error(Error.UNLINKED);
		}

		ResourceKey<Level> targetDim = globalPos.dimension();
		boolean enhanced = crystal.is(SimpleTeleportersItems.ENHANCED_ENDER_SHARD.get());
		if (!enhanced && !targetDim.equals(entity.level().dimension())) {
			return Result.error(Error.WRONG_DIMENSION);
		}

		ServerLevel targetLevel = server.getLevel(targetDim);
		if (targetLevel == null) {
			return Result.error(Error.INVALID_POSITION);
		}

		BlockPos blockPos = globalPos.pos();
		if (targetLevel.getBlockState(blockPos).isSuffocating(targetLevel, blockPos)) {
			return Result.error(Error.INVALID_POSITION);
		}

		Vec3 worldPos = new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
		return new Result(targetLevel, worldPos, blockPos, Error.NONE);
	}
}
