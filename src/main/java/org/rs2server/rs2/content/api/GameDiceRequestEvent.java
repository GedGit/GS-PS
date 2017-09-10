package org.rs2server.rs2.content.api;

import lombok.Value;

import java.util.Optional;

import org.rs2server.rs2.domain.service.impl.content.gamble.DiceGameContainer;
import org.rs2server.rs2.model.player.Player;

/**
 * @author Clank1337
 */
public @Value
final class GameDiceRequestEvent {

	public Player getPlayer() {
		// TODO Auto-generated method stub
		return player;
	}
	
	public GameDiceRequestEvent (final Player player, final Player partner) {
		this.player = player;
		this.partner = partner;
	}

	private final Player player;
	private final Player partner;
	public Player getPartner() {
		// TODO Auto-generated method stub
		return partner;
	}
	public Optional<DiceGameContainer> stream() {
		// TODO Auto-generated method stub
		return null;
	}
}
