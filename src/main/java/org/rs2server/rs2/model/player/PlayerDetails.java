package org.rs2server.rs2.model.player;

import com.google.common.collect.ImmutableList;
import org.apache.mina.core.session.IoSession;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.net.ISAACCipher;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;


/**
 * Contains details about a player (but not the actual <code>Player</code>
 * object itself) that has not logged in yet.
 * @author Graham Edgecombe
 *
 */
public class PlayerDetails {
	
	/**
	 * The session.
	 */
	private IoSession session;
	
	/**
	 * The player name.
	 */
	private String name;
	
	private int userId;
	private int forumRights;
	
	/**
	 * The player password.
	 */
	private String pass;
	
	private int version;
	
	/**
	 * The incoming ISAAC cipher.
	 */
	private ISAACCipher inCipher;
	
	/**
	 * The outgoing ISAAC cipher.
	 */
	private ISAACCipher outCipher;

	private String UUID;

	/**
	 * Creates the player details class.
	 * @param session The session.
	 * @param name The name.
	 * @param pass The password.
	 * @param inCipher The incoming cipher.
	 * @param outCipher The outgoing cipher.
	 */
	public PlayerDetails(IoSession session, String name, String pass, int version, ISAACCipher inCipher, ISAACCipher outCipher, String UUID) {
		this.session = session;
		this.name = name;
		this.pass = pass;
		this.version = version;
		this.inCipher = inCipher;
		this.outCipher = outCipher;
		this.UUID = UUID;
	}

	public final List<Player> getOnlineAccountsFromAddress() {
		if (session == null || session.getRemoteAddress() == null) {
			return ImmutableList.of();
		}
		return World.getWorld().getPlayers().stream()
				.filter(Objects::nonNull)
				.filter(p -> p.getSession() != null
						&& p.getSession().getRemoteAddress() != null
						&& p.getSession().getRemoteAddress().toString().split(":")[0].replaceFirst("/", "")
						.equals(session.getRemoteAddress().toString().split(":")[0].replaceFirst("/", ""))).collect(toList());
	}
	
	/**
	 * Gets the <code>IoSession</code>.
	 * @return The <code>IoSession</code>.
	 */
	public IoSession getSession() {
		return session;
	}
	
	/**
	 * Gets the name. 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the password.
	 * @return The password.
	 */
	public String getPassword() {
		return pass;
	}
	
	/**
	 * Gets the incoming ISAAC cipher.
	 * @return The incoming ISAAC cipher.
	 */
	public ISAACCipher getInCipher() {
		return inCipher;
	}
	
	/**
	 * Gets the outgoing ISAAC cipher.
	 * @return The outgoing ISAAC cipher.
	 */
	public ISAACCipher getOutCipher() {
		return outCipher;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUserId() {
		return userId;
	}

	public void setForumRights(int forumRights) {
		this.forumRights = forumRights;
	}

	public int getForumRights() {
		return forumRights;
	}

	public int getVersion() {
		return version;
	}

	public String getUUID() { return UUID;}

	public String getIP() {
		String ip = session.getRemoteAddress().toString();
		return ip.substring(0, ip.indexOf(":")).replaceAll("/", "");
	}

}
