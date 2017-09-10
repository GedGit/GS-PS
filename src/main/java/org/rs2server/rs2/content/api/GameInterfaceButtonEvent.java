package org.rs2server.rs2.content.api;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.concurrent.Immutable;

/**
 * An event which is fired when a button on an interface is clicked.
 *
 * @author tommo
 */
@Immutable
public class GameInterfaceButtonEvent {

	private Player player;
	private int interfaceId;
	private int button;
	private int childButton;
	private int childButton2;
	private int menuIndex;

	public GameInterfaceButtonEvent(Player player, int interfaceId, int button, int childButton, int childButton2, int menuIndex) {
		this.player = player;
		this.interfaceId = interfaceId;
		this.button = button;
		this.childButton = childButton;
		this.childButton2 = childButton2;
		this.menuIndex = menuIndex;
	}

	public Player getPlayer() {
		return player;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public int getButton() {
		return button;
	}

	public int getChildButton() {
		return childButton;
	}

	public int getChildButton2() {
		return childButton2;
	}

	public int getMenuIndex() {
		return menuIndex;
	}
}
