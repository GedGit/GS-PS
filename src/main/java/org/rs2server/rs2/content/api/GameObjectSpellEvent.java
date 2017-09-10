package org.rs2server.rs2.content.api;

import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.concurrent.Immutable;

/**
 * @author Clank1337
 */
@Immutable
public class GameObjectSpellEvent {

	private final Player player;

	private final GameObject object;

	private final int spellId;

	public GameObjectSpellEvent(Player player, GameObject object, int spellId) {
		this.player = player;
		this.object = object;
		this.spellId = spellId;
	}

	public Player getPlayer() {
		return player;
	}

	public GameObject getObject() {
		return object;
	}

	public int getSpellId() {
		return spellId;
	}
}
