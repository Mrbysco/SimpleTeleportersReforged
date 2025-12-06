package com.mrbysco.simpleteleporters.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.simpleteleporters.SimpleTeleporters;
import net.minecraft.core.GlobalPos;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

public class SimpleTeleportersAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SimpleTeleporters.MOD_ID);

    public record HearthData(Optional<GlobalPos> boundLocation, int teleportTimer) {
        public static final HearthData DEFAULT = new HearthData(Optional.empty(), 0);

        public static final Codec<HearthData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        GlobalPos.CODEC.optionalFieldOf("bound_location").forGetter(HearthData::boundLocation),
                        Codec.INT.fieldOf("teleport_timer").forGetter(HearthData::teleportTimer)
                ).apply(instance, HearthData::new)
        );

        public HearthData withBoundLocation(GlobalPos pos) {
            return new HearthData(Optional.ofNullable(pos), this.teleportTimer);
        }

        public HearthData withTeleportTimer(int timer) {
            return new HearthData(this.boundLocation, timer);
        }

        public boolean hasBoundLocation() {
            return boundLocation.isPresent();
        }

        public boolean isTeleporting() {
            return teleportTimer > 0;
        }
    }

    public static final Supplier<AttachmentType<HearthData>> HEARTH_DATA = ATTACHMENT_TYPES.register(
            "hearth_data",
            () -> AttachmentType.builder(() -> HearthData.DEFAULT)
                    .serialize(HearthData.CODEC.fieldOf("hearth_data"))
                    .copyOnDeath()
                    .build()
    );
}
