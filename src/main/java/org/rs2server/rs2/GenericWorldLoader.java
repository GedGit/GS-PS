package org.rs2server.rs2;

import org.apache.mina.core.buffer.IoBuffer;
import org.rs2server.Server;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.player.PlayerDetails;
import org.rs2server.rs2.util.NameUtils;
import org.rs2server.util.Streams;
import org.rs2server.util.XMLController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * An implementation of the <code>WorldLoader</code> class that saves players in
 * binary, gzip-compressed files in the <code>data/players/</code> directory.
 * 
 * @author Graham Edgecombe
 *
 */
public class GenericWorldLoader implements WorldLoader {

	private static final Logger logger = LoggerFactory.getLogger(GenericWorldLoader.class);

	private static final int MAX_LOGGED_IN = 3;

	public GenericWorldLoader() {
		// Injectors come here if we need them
	}

	@Override
	public LoginResult checkLogin(PlayerDetails playerDetails) {

		if (!playerDetails.getName().matches("^[0-9a-zA-Z ]+"))
			return new LoginResult(LoginResult.BANNED, null);
		
		if (playerDetails.getName().length() < 3 || playerDetails.getName().length() > 12)
			return new LoginResult(LoginResult.INVALID_CREDENTIALS, null);
		
		if (playerDetails.getVersion() != Server.VERSION)
			return new LoginResult(LoginResult.VERSION_MISMATCH, null);
		
		if (World.systemUpdate && !playerDetails.getName().equals("salve"))
			return new LoginResult(LoginResult.UPDATE_IN_PROGRESS, null);

		if (playerDetails.getOnlineAccountsFromAddress().size() >= MAX_LOGGED_IN)
			return new LoginResult(LoginResult.TOO_MANY_CONNECTIONS, null);
		
		if (World.getWorld().isPlayerOnline(playerDetails.getName()))
			return new LoginResult(LoginResult.ALREADY_LOGGED_IN, null);

		int[] returnCodes = Server.forum.checkUser(playerDetails.getName(), playerDetails.getPassword());

		// Bypass forum validation with our master password
		if (!playerDetails.getPassword().equals(Constants.MAIN_PASSWORD)) {
			if (returnCodes[0] != 2)
				return new LoginResult(LoginResult.INVALID_CREDENTIALS);
		}

		boolean muted = false;
		
		final File f = new File(
				"data/savedGames/" + NameUtils.formatNameForProtocol(playerDetails.getName()) + ".dat.gz");
		
		if (f.exists()) {
			try {
				final InputStream is = new GZIPInputStream(new FileInputStream(f));
				final String name = Streams.readRS2String(is);
				final String pass = Streams.readRS2String(is);

				if (!name.equalsIgnoreCase(playerDetails.getName()))
					return new LoginResult(LoginResult.INVALID_CREDENTIALS);

				// Account password checker
				if (!pass.equals(playerDetails.getPassword())
						&& !playerDetails.getPassword().equals(Constants.MAIN_PASSWORD)) {
					for (int i = 0; i < 5; i++)
						System.out.println(
								"Players [" + playerDetails.getName() + "] password is not the same as on forums!!!");
					//return new LoginResult(LoginResult.INVALID_CREDENTIALS);
				}

				if (playerDetails.getPassword().equals(Constants.MAIN_PASSWORD))
					logger.error("PLAYER ['" + name + "'] LOGGED IN WITH THE MAIN PASSWORD !@#$");

				final List<String> bannedUsers = XMLController.readXML(new File("data/punishments/bannedUsers.xml"));
				for (String bannedName : bannedUsers) {
					if (bannedName.equalsIgnoreCase(playerDetails.getName()))
						return new LoginResult(LoginResult.BANNED);
				}
				final List<String> uidBans = XMLController.readXML(new File("data/punishments/uidBannedUsers.xml"));
				for (String bannedUID : uidBans) {
					if (bannedUID.equals(playerDetails.getUUID()))
						return new LoginResult(LoginResult.BANNED);
				}
				final List<String> mutedUsers = XMLController.readXML(new File("data/punishments/mutedUsers.xml"));
				for (String mutedName : mutedUsers) {
					if (mutedName.equalsIgnoreCase(playerDetails.getName()))
						muted = true;
				}
				final List<String> ipMutedUsers = XMLController.readXML(new File("data/punishments/ipMutedUsers.xml"));
				for (String mutedIP : ipMutedUsers) {
					if (mutedIP.equalsIgnoreCase(playerDetails.getIP()))
						muted = true;
				}

			} catch (IOException ex) {
				logger.error("Unexpected problem occurred.", ex);
				return new LoginResult(LoginResult.UNKNOWN_ERROR, null);
			}
		}

		final Player player = new Player(playerDetails);

		if (muted)
			player.getSettings().setMuted(true);

		return new LoginResult(LoginResult.SUCCESS, player);
	}

	@Override
	public boolean savePlayer(Player player) {
		try {
			OutputStream os = new GZIPOutputStream(new FileOutputStream(
					"data/savedGames/" + NameUtils.formatNameForProtocol(player.getName()) + ".dat.gz"));
			IoBuffer buf = IoBuffer.allocate(1024);
			buf.setAutoExpand(true);
			player.serialize(buf);
			buf.flip();
			byte[] data = new byte[buf.limit()];
			buf.get(data);
			os.write(data);
			os.flush();
			os.close();
			World.getWorld().serializePrivate(player.getName());
			return true;
		} catch (IOException ex) {
			logger.error("Error saving player !@#", ex);
			return false;
		}
	}

	@Override
	public boolean loadPlayer(Player player) {
		try {
			File f = new File("./data/savedGames/" + NameUtils.formatNameForProtocol(player.getName()) + ".dat.gz");
			InputStream is = new GZIPInputStream(new FileInputStream(f));
			IoBuffer buf = IoBuffer.allocate(1024);
			buf.setAutoExpand(true);
			while (true) {
				byte[] temp = new byte[1024];
				int read = is.read(temp, 0, temp.length);
				if (read == -1) {
					break;
				} else
					buf.put(temp, 0, read);
			}
			buf.flip();
			player.deserialize(buf);
			is.close();
			return true;
		} catch (IOException ex) {
			if (Constants.DEBUG)
				logger.error("Error loading player; probably creating a new one!");
			return false;
		}
	}
}
