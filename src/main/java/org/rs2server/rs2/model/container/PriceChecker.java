package org.rs2server.rs2.model.container;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.container.impl.InterfaceContainerListener;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.Misc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PriceChecker {

	/**
	 * The pc size.
	 */
	public static final int SIZE = 20;

	/**
	 * The player inventory interface.
	 */
	public static final int PLAYER_INVENTORY_INTERFACE = 238;

	/**
	 * The pc inventory interface.
	 */
	public static final int PRICE_INVENTORY_INTERFACE = 464;

	/**
	 * Opens the bank for the specified player.
	 * 
	 * @param player
	 *            The player to open the bank for.
	 */
	public static void open(Player player) {
		player.getPriceChecker().shift();
		player.getActionSender().sendInterface(PRICE_INVENTORY_INTERFACE,
				false);
		
		player.getActionSender().sendInterfaceInventory(
				PLAYER_INVENTORY_INTERFACE);
		player.getActionSender()
				.sendCS2Script(600, new Object[] { 30408716, 15, 1, 1 }, "iiiI")
				.sendCS2Script(149, Constants.PRICE_PARAMETERS, "IviiiIsssss")
				.sendAccessMask(1086, 464, 2, 0, 27)
				.sendAccessMask(1086, 238, 0, 0, 27);

		player.getInterfaceState().addListener(player.getInventory(), new InterfaceContainerListener(player, -1, 64209, 93));
		player.getInterfaceState().addListener(player.getPriceChecker(), new InterfaceContainerListener(player, -1, 65212, 90));
		updatePrice(player, 0);
	}

	/**
	 * Offers an item.
	 * 
	 * @param player
	 *            The player.
	 * @param slot
	 *            The slot in the player's inventory.
	 * @param id
	 *            The item id.
	 * @param amount
	 *            The amount of the item to offer.
	 */
	public static void deposit(Player player, int slot, int id, int amount) {
		if (player.getInterfaceState().getCurrentInterface() != PRICE_INVENTORY_INTERFACE) {
			return;
		}
		player.getActionSender().removeChatboxInterface();
		Item item = player.getInventory().get(slot);
		if (item == null) {
			return; // invalid packet, or client out of sync
		}
		if (item.getId() == 995 || item.getId() == 996) {
			player.getActionSender().sendMessage("You can't deposit coins.");
			return;
		}
		if (item.getId() != id) {
			return; // invalid packet, or client out of sync
		}
		if (!item.getDefinition().isTradable()) {
			player.getActionSender().sendMessage("You cannot deposit this item.");
			return;
		}
		boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
		player.getInventory().setFiringEvents(false);
		try {
		int transferAmount = player.getInventory().getCount(id);
		if (transferAmount >= amount) {
			transferAmount = amount;
		} else if (transferAmount == 0) {
			return; // invalid packet, or client out of sync
		}

		if (player.getPriceChecker().add(
				new Item(item.getId(), transferAmount), -1)) {
			player.getInventory()
					.remove(new Item(item.getId(), transferAmount), slot);
			player.totalPrice += item.getPrice() * transferAmount;
			updatePrice(player, player.totalPrice);
			
			Object[] params = new Object[28];//sorry had to afk
			for (int i = 0; i < params.length; i++) {
				Item it = player.getPriceChecker().get(i);
				params[i] = it != null ? it.getPrice() : 0;
			}
			
			
			List<Object> list = Arrays.asList(params);
			Collections.reverse(list);
			
			player.getActionSender().sendCS2Script(785, list.toArray(), "iiiiiiiiiiiiiiiiiiiiiiiiiiii");
		}
		
		player.getInventory().fireItemsChanged();
		} finally {
			player.getInventory().setFiringEvents(inventoryFiringEvents);
		}
	}
	

	/**
	 * Removes an offered an item.
	 * 
	 * @param player
	 *            The player.
	 * @param slot
	 *            The slot in the player's inventory.
	 * @param id
	 *            The item id.
	 * @param amount
	 *            The amount of the item to offer.
	 */
	public static void withdraw(Player player, int slot, int id, int amount) {
		if (player.getInterfaceState().getCurrentInterface() != PRICE_INVENTORY_INTERFACE) {
			return;
		}
		player.getActionSender().removeChatboxInterface();
		// player.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56,
		// "");
		// partner.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56,
		// "");
		boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
		player.getInventory().setFiringEvents(false);
		try {
			Item item = player.getPriceChecker().get(slot);
			if (item == null || item.getId() != id) {
				return; // invalid packet, or client out of sync
			}
			int transferAmount = player.getPriceChecker().getCount(id);
			if (transferAmount >= amount) {
				transferAmount = amount;
			} else if (transferAmount == 0) {
				return; // invalid packet, or client out of sync
			}
			if (player.getInventory().add(new Item(item.getId(), transferAmount), -1)) {
				player.getPriceChecker().remove(new Item(item.getId(), transferAmount));
				player.totalPrice -= item.getPrice() * transferAmount;
				
				updatePrice(player, player.totalPrice);

				Object[] params = new Object[28];
				for (int i = 0; i < params.length; i++) {// try
					Item it = player.getPriceChecker().get(i);
					params[i] = it != null ? it.getPrice() : 0;
				}


				List<Object> list = Arrays.asList(params);
				Collections.reverse(list);

				player.getActionSender().sendCS2Script(785, list.toArray(), "iiiiiiiiiiiiiiiiiiiiiiiiiiii");
			}
			player.getInventory().fireItemsChanged();
		} finally {
			player.getInventory().setFiringEvents(inventoryFiringEvents);
		}
	}
	
	public static void updatePrice(Player player, int amount) {
		player.getActionSender().sendString(464, 12, "Total guide price:<br><col=ffffff>" + Misc.formatCurrency(amount) + "</col>");
	}
	
	public static void returnItems(Player player) {
		boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
		player.getInventory().setFiringEvents(false);
		try {
			if (player.getPriceChecker().size() > 0) {
				for (Item item : player.getPriceChecker().toArray()) {
					if (item != null) {
						player.getInventory().add(item);
					}
				}
				player.getPriceChecker().clear();
				player.getInventory().fireItemsChanged();
				player.totalPrice = 0;
			}
		} finally {
			player.getInventory().setFiringEvents(inventoryFiringEvents);
		}
	}

}
