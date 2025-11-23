package com.mrbysco.simpleteleporters.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class SimpleTeleportersConfig {
	public static class Client {
		public final ModConfigSpec.BooleanValue disableParticles;
		public final ModConfigSpec.BooleanValue disableNameplate;

		Client(ModConfigSpec.Builder builder) {
			builder.comment("Client settings")
					.push("client");

			disableParticles = builder
					.comment("Disable the particles shown above an active teleporter [Default: false]")
					.define("disableParticles", false);

			disableNameplate = builder
					.comment("Disable the nameplate shown above a teleporter with a named crystal [Default: false]")
					.define("disableNameplate", false);

			builder.pop();
		}
	}

	public static final ModConfigSpec clientSpec;
	public static final Client CLIENT;

	static {
		final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
		clientSpec = specPair.getRight();
		CLIENT = specPair.getLeft();
	}
}
