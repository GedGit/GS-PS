package org.rs2server.rs2.content.api;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * A game event which is created when an in-game NPC is clicked by the player.
 *
 * @author tommo
 */
@Immutable
public class GameNpcActionEvent {

	public enum ActionType {
		OPTION_1, OPTION_2, OPTION_3, OPTION_4, OPTION_EXAMINE, OPTION_TRADE, ITEM_ON_NPC
	}

	private final Player player;

	private final ActionType actionType;

	private final NPC npc;

	/**
	 * Only available if the action type is ITEM_ON_NPC.
	 */
	@Nullable
	private final Item item;

	public GameNpcActionEvent(final Player player, final ActionType actionType, final NPC npc) {
		this(player, actionType, npc, null);
	}

	public GameNpcActionEvent(final Player player, final ActionType actionType, final NPC npc, final Item item) {
		this.player = player;
		this.actionType = actionType;
		this.npc = npc;
		this.item = item;
	}

	public Player getPlayer() {
		return player;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public NPC getNpc() {
		return npc;
	}

	@Nullable
	public Item getItem() {
		return item;
	}
}
