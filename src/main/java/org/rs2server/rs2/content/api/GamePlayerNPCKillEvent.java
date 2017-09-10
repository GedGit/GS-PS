package org.rs2server.rs2.content.api;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

/**
 * @author Clank1337
 */
public class GamePlayerNPCKillEvent {

	private final Player player;
	private final String npcName;
	private final Item item;

	public GamePlayerNPCKillEvent(Player player, String npcName, Item item) {
		this.player = player;
		this.npcName = npcName;
		this.item = item;
	}

	public Player getPlayer() {
		return player;
	}

	public String getNpcName() {
		return npcName;
	}

	public Item getItem() {
		return item;
	}
}
