package org.rs2server.rs2.content.api;

import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.player.Player;

/**
 * @author Clank1337
 */
public class GamePlayerKillEvent {

	private final Player player;

	private final Player killer;

	private final Container items;

	public GamePlayerKillEvent(Player player, Player killer, Container items) {
		this.player = player;
		this.killer = killer;
		this.items = items;
	}

	public Player getPlayer() {
		return player;
	}

	public Player getKiller() {
		return killer;
	}

	public Container getItems() {
		return items;
	}
}
