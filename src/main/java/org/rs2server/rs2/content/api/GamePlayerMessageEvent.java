package org.rs2server.rs2.content.api;

import org.rs2server.rs2.model.player.Player;

/**
 * @author Clank1337
 */
public class GamePlayerMessageEvent {

	private final Player player;
	private final Player receiver;
	private final String message;

	public GamePlayerMessageEvent(Player player, Player receiver, String message) {
		this.player = player;
		this.receiver = receiver;
		this.message = message;
	}

	public Player getPlayer() {
		return player;
	}

	public Player getReceiver() {
		return receiver;
	}

	public String getMessage() {
		return message;
	}
}
