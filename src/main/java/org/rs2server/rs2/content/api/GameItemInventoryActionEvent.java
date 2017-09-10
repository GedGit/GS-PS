package org.rs2server.rs2.content.api;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.concurrent.Immutable;

/**
 * A game event which is created when an item option in the inventory is clicked by the player.
 *
 * @author tommo
 */
@Immutable
public class GameItemInventoryActionEvent {

	public enum ClickType {
		OPTION_1, OPTION_2, OPTION_3, OPTION_4, DROP, ITEM_ON_ITEM, WIELD_OPTION,
		/**
		 * Note that if the event is of type DESTROY, the event is fired /after/ the destroy item interface has been shown and confirmed.
		 */
		DESTROY
	}

	private final Player player;

	private final ClickType clickType;

	private final Item item;

	private final int slot;

	public GameItemInventoryActionEvent(final Player player, final ClickType clickType, final Item item, final int slot) {
		this.player = player;
		this.clickType = clickType;
		this.item = item;
		this.slot = slot;
	}

	public Player getPlayer() {
		return player;
	}

	public ClickType getClickType() {
		return clickType;
	}

	public Item getItem() {
		return item;
	}

	public int getSlot() {
		return slot;
	}

}
