package com.mrbysco.simpleteleporters.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

// plotPos is in the SubLevel's storage/plot coordinates; project through Sable at teleport time
// to get current world-space position so it tracks the ship as it moves.
public record SubLevelBinding(UUID subUuid, Vec3 plotPos) {
	public static final Codec<SubLevelBinding> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					UUIDUtil.CODEC.fieldOf("sub_uuid").forGetter(SubLevelBinding::subUuid),
					Vec3.CODEC.fieldOf("plot_pos").forGetter(SubLevelBinding::plotPos)
			).apply(instance, SubLevelBinding::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, SubLevelBinding> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, SubLevelBinding::subUuid,
			ByteBufCodecs.fromCodec(Vec3.CODEC), SubLevelBinding::plotPos,
			SubLevelBinding::new
	);
}
