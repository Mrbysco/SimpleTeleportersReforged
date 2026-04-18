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
		public final ModConfigSpec.IntValue teleportCooldown;
		public final ModConfigSpec.BooleanValue redstonePreventsCooldown;

		Server(ModConfigSpec.Builder builder) {
			builder.comment("Server settings")
					.push("server");

			teleportCooldown = builder
					.comment("The cooldown (in seconds) applied to a teleporter after use, preventing immediate re-teleport. Set to 0 to disable. [Default: 3, Min: 0, Max: 300]")
					.defineInRange("teleportCooldown", 3, 0, 300);

			redstonePreventsCooldown = builder
					.comment("If true, a redstone pulse always triggers the teleporter regardless of any pending cooldown. [Default: true]")
					.define("redstonePreventsCooldown", true);

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
