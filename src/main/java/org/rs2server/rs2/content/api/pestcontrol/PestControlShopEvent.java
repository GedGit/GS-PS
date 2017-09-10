package org.rs2server.rs2.content.api.pestcontrol;

import lombok.Value;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.concurrent.Immutable;

/**
 * @author Clank1337
 */
@Immutable
public final @Value
class PestControlShopEvent {
	
	private final Player player;
	private final int button;
	
	public Player getPlayer() {
		return player;
	}
	public int getButton() {
		return button;
	}
	
	public PestControlShopEvent (final Player player, int button) {
		this.player = player;
		this.button = button;
	}
}