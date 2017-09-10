package org.rs2server.rs2.content.api;


import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.concurrent.Immutable;

/**
 * @author Clank1337
 */
@Immutable
public class GamePlayerTradeEvent {

	private final Player player;
	private final Player partner;
	private final Container playerContainer;
	private final Container partnerContainer;

	public GamePlayerTradeEvent(Player player, Player partner, Container playerContainer, Container partnerContainer) {
		this.player = player;
		this.partner = partner;
		this.playerContainer = playerContainer;
		this.partnerContainer = partnerContainer;
	}

	public Player getPlayer() {
		return player;
	}

	public Player getPartner() {
		return partner;
	}

	public Container getPlayerContainer() {
		return playerContainer;
	}

	public Container getPartnerContainer() {
		return partnerContainer;
	}
}
