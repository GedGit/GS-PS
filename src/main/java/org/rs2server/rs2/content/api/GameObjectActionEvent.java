package org.rs2server.rs2.content.api;

import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * A game event which is created when an in-game object is clicked by the player.
 *
 * @author tommo
 */
@Immutable
public class GameObjectActionEvent {

	public enum ActionType {
		OPTION_1, OPTION_2, OPTION_3, ITEM_ON_OBJECT, OPTION_EXAMINE
	}

	private final Player player;

	private final ActionType actionType;

	private final GameObject gameObject;

	/**
	 * Only available if the action type is ITEM_ON_OBJECT.
	 */
	@Nullable
	private Item item;

	public GameObjectActionEvent(final Player player, final ActionType actionType, final GameObject gameObject) {
		this(player, actionType, gameObject, null);
	}

	public GameObjectActionEvent(final Player player, final ActionType actionType, final GameObject gameObject, final Item item) {
		this.player = player;
		this.actionType = actionType;
		this.gameObject = gameObject;
		this.item = item;
	}

	public Player getPlayer() {
		return player;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public GameObject getGameObject() {
		return gameObject;
	}

	@Nullable
	public Item getItem() {
		return item;
	}
}
