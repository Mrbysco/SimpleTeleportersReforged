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

	public static class Server {
		public final ModConfigSpec.BooleanValue enableSableIntegration;

		Server(ModConfigSpec.Builder builder) {
			builder.comment("Server settings")
					.push("server");

			enableSableIntegration = builder
					.comment("Allow Ender Shards to bind to Sable SubLevels (Create: Aeronautics airships). Has no effect when Sable is not installed. [Default: true]")
					.define("enableSableIntegration", true);

			builder.pop();
		}
	}

	public static final ModConfigSpec clientSpec;
	public static final Client CLIENT;

	public static final ModConfigSpec serverSpec;
	public static final Server SERVER;

	static {
		final Pair<Client, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(Client::new);
		clientSpec = clientPair.getRight();
		CLIENT = clientPair.getLeft();

		final Pair<Server, ModConfigSpec> serverPair = new ModConfigSpec.Builder().configure(Server::new);
		serverSpec = serverPair.getRight();
		SERVER = serverPair.getLeft();
	}
}
