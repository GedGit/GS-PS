package org.rs2server.rs2.content.api;

import lombok.Value;
import org.rs2server.rs2.model.player.Player;

/**
 * @author twelve
 */
public @Value
final class GameTradeRequestEvent {

	public Player getPlayer() {
		return player;
	}
	
	public GameTradeRequestEvent (final Player player, final Player partner) {
		this.player = player;
		this.partner = partner;
	}

	private final Player player;
	private final Player partner;
	
	public Player getPartner() {
		return partner;
	}
}