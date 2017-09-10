package org.rs2server.rs2.content.api;

import org.rs2server.rs2.domain.service.api.GroundItemService;
import org.rs2server.rs2.model.player.Player;

/**
 * @author Clank1337
 */
public class GamePlayerItemDropEvent {

	private final Player player;
	private final GroundItemService.GroundItem groundItem;

	public GamePlayerItemDropEvent(Player player, GroundItemService.GroundItem groundItem) {
		this.player = player;
		this.groundItem = groundItem;
	}

	public Player getPlayer() {
		return player;
	}

	public GroundItemService.GroundItem getGroundItem() {
		return groundItem;
	}
}
