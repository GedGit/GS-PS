package org.rs2server;

import com.google.inject.*;

import plugin.discord.Hookscord;
import plugin.discord.utils.Message;

import org.rs2server.http.HTTPServer;
import org.rs2server.ls.LoginServer;
import org.rs2server.rs2.*;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.mysql.impl.ForumValidation;
import org.slf4j.*;

import java.io.IOException;

/**
 * The main class for those who cannot read :P.
 * 
 * @author Vichy
 */
public class Server {

	/**
	 * Constructs the login server.
	 */
	private static LoginServer loginServer = new LoginServer();

	/**
	 * The protocol version.
	 */
	public static final int VERSION = 83;

	/**
	 * Constructs the applet logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Server.class);

	/**
	 * Defines the Injector instance.
	 */
	private static Injector injector;

	/**
	 * Defines the Discords webhook.
	 */
	public static Hookscord hk = null;

	/**
	 * Forum integrations and crap.
	 */
	public static ForumValidation forum = null;

	/**
	 * The entry point of the application.
	 *
	 * @param args
	 *            The command line arguments.
	 * @throws IOException
	 */
	public static void main(String[] args) {

		if (args.length < 2) {
			logger.info("USE: debug(boolean) port(integer) worldID(integer)");
			System.exit(1);
			return;
		}

		/** Run Arguments **/
		Constants.DEBUG = Boolean.parseBoolean(args[0]);
		Constants.PORT = Integer.parseInt(args[1]);
		Constants.WORLD_ID = Integer.parseInt(args[2]);

		if (Constants.WORLD_ID == 1)
			HTTPServer.start();

		logger.info("Starting " + Constants.SERVER_NAME + " - (World " + Constants.WORLD_ID + " - Port: "
				+ Constants.PORT + ")...");

		injector = Guice.createInjector(new WorldModule());

		try {
			new RS2Server().bind(Constants.PORT).start();

			/**
			 * Discord integration
			 */
			hk = new Hookscord("https://canary.discordapp.com/api/webhooks/" + Constants.DISCORD_WEBHOOK);
			sendDiscordMessage("[SERVER] " + Constants.SERVER_NAME + "-PS is now back online. Latest patch notes: # "
					+ Constants.LATEST_PATCH_NOTES_INT + " !");

			/**
			 * Forum integrations.
			 */
			forum = new ForumValidation(Constants.DEBUG ? "144.217.242.115" : "localhost", "admin_forums",
					"salve-ps_admin", "bCs6m_17");

		} catch (Exception ex) {
			logger.error("World " + Constants.WORLD_ID + " - Port: " + Constants.PORT + "!", ex);
			System.exit(1);
		}
		World.getWorld();
	}

	public static Injector getInjector() {
		return injector;
	}

	public static LoginServer getLoginServer() {
		return loginServer;
	}

	public static Hookscord getDiscord() {
		return hk;
	}

	public static void sendDiscordMessage(String message) {
		if (Constants.DEBUG)
			return;
		if (getDiscord() == null) {
			System.out.println("[Server] Discord Webhook hasn't been initiated; can't send message!");
			return;
		}
		try {
			getDiscord().sendMessage(new Message("Salve-PS", "", message));
		} catch (IOException e) {
			System.err.println("[Server] Critical Error; Discord Webhook couldn't send message!");
		}
	}
}