package org.rs2server.rs2.model.container;

import org.rs2server.Server;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.domain.service.api.PermissionService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.bit.component.Access;
import org.rs2server.rs2.model.bit.component.AccessBits;
import org.rs2server.rs2.model.bit.component.NumberRange;
import org.rs2server.rs2.model.container.impl.InterfaceContainerListener;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.player.RequestManager.RequestState;

public class Trade {

	/**
	 * The trade size.
	 */
	public static final int SIZE = 28;

	/**
	 * The player inventory interface.
	 */
	public static final int PLAYER_INVENTORY_INTERFACE = 336;

	/**
	 * The trade inventory interface.
	 */
	public static final int TRADE_INVENTORY_INTERFACE = 335;

	/**
	 * The trade inventory interface.
	 */
	public static final int SECOND_TRADE_SCREEN = 334;

	private static final String CONFIRM_TRADE_MESSAGE = "<col=00FFFF>Are you sure you want to make this trade?";

	private static final Access TRADE_ACCESS_SELF = Access.of(335, 25, NumberRange.of(0, 28), AccessBits.optionBit(10));
	private static final Access TRADE_ACCESS_OTHER = Access.of(335, 28, NumberRange.of(0, 28), AccessBits.optionBit(10));
	
	/**
	 * Opens the trade for the specified player.
	 * @param player The player to open the trade for.
	 */
	public static void open(Player player, Player partner) {
		Object[] TRADE_PARAMETERS_2 = new Object[] { "", "", "", "", "Remove-X", "Remove-All", "Remove-10", "Remove-5", "Remove", -1, 0, 7, 4, 81, 335 << 16 | 24};
		Object[] TRADE_PARAMETERS_1 = new Object[] {-2, 0, 7, 4, 80, 335 << 16 | 27};



		player.sendAccess(TRADE_ACCESS_SELF);
		player.sendAccess(TRADE_ACCESS_OTHER);

		partner.sendAccess(TRADE_ACCESS_SELF);
		partner.sendAccess(TRADE_ACCESS_OTHER);
		player.getActionSender()
		.sendCS2Script(150, TRADE_PARAMETERS_2, Constants.TRADE_TYPE_STRING)
		.sendCS2Script(150, Constants.OFFER_PARAMETERS, Constants.TRADE_TYPE_STRING)
		.sendCS2Script(150, TRADE_PARAMETERS_1, "iiiiii")
//		.sendAccessMask(1278, 335, 24, 0, 28)
//		.sendAccessMask(1026, 335, 27, 0, 28)
		.sendAccessMask(1278, 336, 0, 0, 28)
		.sendInterface(TRADE_INVENTORY_INTERFACE, false)
		.sendString(335, 31, "Trading with: " + partner.getName())
		.sendString(335, 9, partner.getName() + " has " + partner.getInventory().freeSlots() + "<br> free inventory slots.")
		.sendInterfaceInventory(336);
		
		player.getInterfaceState().addListener(player.getTrade(), new InterfaceContainerListener(player, -1, 2, 90));
		player.getInterfaceState().addListener(partner.getTrade(), new InterfaceContainerListener(player, -2, 60981, 90));
		player.getInterfaceState().addListener(partner.getTrade(), new InterfaceContainerListener(player, -1, 50, 80));
		player.getInterfaceState().addListener(player.getTrade(), new InterfaceContainerListener(player, -1, 1, 81));

		player.getInterfaceState().addListener(player.getInventory(), new InterfaceContainerListener(player, 149, 0, 93));
		player.getInterfaceState().addListener(player.getInventory(), new InterfaceContainerListener(player, -1, 1, 82));
		
		partner.getActionSender()
		.sendCS2Script(150, TRADE_PARAMETERS_2, Constants.TRADE_TYPE_STRING)
		.sendCS2Script(150, Constants.OFFER_PARAMETERS, Constants.TRADE_TYPE_STRING)
		.sendCS2Script(150, TRADE_PARAMETERS_1, "iiiiii")
//		.sendAccessMask(1278, 335, 24, 0, 28)
//		.sendAccessMask(1026, 335, 27, 0, 28)
		.sendAccessMask(1278, 336, 0, 0, 28)
		.sendInterface(TRADE_INVENTORY_INTERFACE, false)
		.sendString(335, 31, "Trading with: " + player.getName())
		.sendString(335, 9, player.getName() + " has " + player.getInventory().freeSlots() + "<br> free inventory slots.")
		.sendInterfaceInventory(336);
		
		partner.getInterfaceState().addListener(player.getTrade(), new InterfaceContainerListener(partner, -1, 50, 80));
		partner.getInterfaceState().addListener(partner.getTrade(), new InterfaceContainerListener(partner, -1, 1, 81));
		partner.getInterfaceState().addListener(partner.getTrade(), new InterfaceContainerListener(partner, -1, 2, 90));
		partner.getInterfaceState().addListener(player.getTrade(), new InterfaceContainerListener(partner, -2, 60981, 90));
		
		partner.getInterfaceState().addListener(partner.getInventory(), new InterfaceContainerListener(partner, 149, 0, 93));
		partner.getInterfaceState().addListener(partner.getInventory(), new InterfaceContainerListener(partner, -1, 1, 82));
	}

	/**
	 * Offers an item.
	 * @param player The player.
	 * @param slot The slot in the player's inventory.
	 * @param id The item id.
	 * @param amount The amount of the item to offer.
	 */
	public static void offerItem(Player player, int slot, int id, int amount) {
		final PermissionService permissionService = Server.getInjector().getInstance(PermissionService.class);
		if(player.getInterfaceState().getCurrentInterface() != TRADE_INVENTORY_INTERFACE) {
			return;
		}
		player.getActionSender().removeChatboxInterface();
		Item item = player.getInventory().get(slot);
		if(item == null) {
			return; // invalid packet, or client out of sync
		}
		if(item.getId() != id) {
			return; // invalid packet, or client out of sync
		}
		Player partner = player.getRequestManager().getAcquaintance();
		if(partner == null) {
			return;
		}
		if(!item.getDefinition().isTradable() && permissionService.isNot(player, PermissionService.PlayerPermissions.DEV) && permissionService.isNot(partner, PermissionService.PlayerPermissions.DEV)) {
			player.getActionSender().sendMessage("You cannot trade this item.");
			return;
		}
		if (item.getId() == 11283 && player.dfsCharges > 0) {
			player.getActionSender().sendMessage("You can't trade this item until you empty it.");
			return;
		}
		//player.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
		//partner.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
		player.getRequestManager().setState(RequestState.PARTICIPATING);
		partner.getRequestManager().setState(RequestState.PARTICIPATING);
		boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
		player.getInventory().setFiringEvents(false);
		try {
			int transferAmount = player.getInventory().getCount(id);
			if(transferAmount >= amount) {
				transferAmount = amount;
			} else if(transferAmount == 0) {
				return; // invalid packet, or client out of sync
			}

			if(player.getTrade().add(new Item(item.getId(), transferAmount), -1)) {
				player.getInventory().remove(new Item(item.getId(), transferAmount));
			}
			player.getInventory().fireItemsChanged();
		} finally {
			player.getInventory().setFiringEvents(inventoryFiringEvents);
			updateFirstScreen(player);
			player.getActionSender().removeChatboxInterface();
		}
	}

	/**
	 * Removes an offered an item.
	 * @param player The player.
	 * @param slot The slot in the player's inventory.
	 * @param id The item id.
	 * @param amount The amount of the item to offer.
	 */
	public static void removeItem(Player player, int slot, int id, int amount) {
		if(player.getInterfaceState().getCurrentInterface() != TRADE_INVENTORY_INTERFACE) {
			return;
		}
		player.getActionSender().removeChatboxInterface();
		Player partner = player.getRequestManager().getAcquaintance();
		if(partner == null) {
			return;
		}
		//player.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
		//partner.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
		player.getRequestManager().setState(RequestState.PARTICIPATING);
		partner.getRequestManager().setState(RequestState.PARTICIPATING);
		boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
		player.getInventory().setFiringEvents(false);
		try {			
			Item item = player.getTrade().get(slot);
			if(item == null || item.getId() != id) {
				return; // invalid packet, or client out of sync
			}
			int transferAmount = player.getTrade().getCount(id);
			if(transferAmount >= amount) {
				transferAmount = amount;
			} else if(transferAmount == 0) {
				return; // invalid packet, or client out of sync
			}

			if(player.getInventory().add(new Item(item.getId(), transferAmount), -1)) {
				player.getTrade().remove(new Item(item.getId(), transferAmount));
			}
			player.getInventory().fireItemsChanged();
		} finally {
			player.getInventory().setFiringEvents(inventoryFiringEvents);
			updateFirstScreen(player);
			player.getActionSender().removeChatboxInterface();
		}
	}

	public static void secondScreen(Player player) {
		Player partner = player.getRequestManager().getAcquaintance();
		if(partner == null) {
			return;
		}
		player.getActionSender().removeChatboxInterface();
		partner.getActionSender().removeChatboxInterface();

		partner.getActionSender().sendString(334, 4, CONFIRM_TRADE_MESSAGE);
		player.getActionSender().sendString(334, 4, CONFIRM_TRADE_MESSAGE);
		player.getActionSender().sendInterface(SECOND_TRADE_SCREEN, false)
		.sendString(334, 30, "<col=00FFFF>Trading with:<br><col=00FFFF>" + partner.getName());
		partner.getActionSender().sendInterface(SECOND_TRADE_SCREEN, false)
		.sendString(334, 30, "<col=00FFFF>Trading with:<br><col=00FFFF>" + player.getName());
	}

	public static void acceptTrade(Player player, int screenStage) {
		Player partner = player.getRequestManager().getAcquaintance();
		if(partner == null) {
			return;
		}
		switch(screenStage) {
		case 1:
			if(player.getInventory().freeSlots() < partner.getTrade().size()) {
				player.getActionSender().sendMessage("You do not have enough free inventory slots.");
				////player.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
				////partner.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
				player.getRequestManager().setState(RequestState.PARTICIPATING);
				partner.getRequestManager().setState(RequestState.PARTICIPATING);
				return;
			}
			for(Item item : partner.getTrade().toArray()) {
				if(item != null && item.getDefinition().isStackable() && player.getInventory().getCount(item.getId()) > 0) {
					long partnerCount = player.getInventory().getCount(item.getId());
					long myCount = item.getCount();
					long totalCount = (partnerCount + myCount);
					if(totalCount > Integer.MAX_VALUE) {
						player.getActionSender().sendMessage("You cannot accept this amount of " + item.getDefinition().getName() + (item.getDefinition().getName().endsWith("s") ? "" : "s") + ".");
						////player.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
						////partner.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
						player.getRequestManager().setState(RequestState.PARTICIPATING);
						partner.getRequestManager().setState(RequestState.PARTICIPATING);
						return;						
					}
				}
			}
			if(partner.getInventory().freeSlots() < player.getTrade().size()) {
				player.getActionSender().sendMessage("The other player does not have enough free inventory slots.");
				//player.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
				//partner.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
				player.getRequestManager().setState(RequestState.PARTICIPATING);
				partner.getRequestManager().setState(RequestState.PARTICIPATING);
				return;
			}
			for(Item item : player.getTrade().toArray()) {
				if(item != null && item.getDefinition().isStackable() && partner.getInventory().getCount(item.getId()) > 0) {
					long partnerCount = partner.getInventory().getCount(item.getId());
					long myCount = item.getCount();
					long totalCount = (partnerCount + myCount);
					if(totalCount > Integer.MAX_VALUE) {
						player.getActionSender().sendMessage("The other player cannot accept this amount of " + item.getDefinition().getName() + (item.getDefinition().getName().endsWith("s") ? "" : "s") + ".");
						//player.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
						//partner.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
						player.getRequestManager().setState(RequestState.PARTICIPATING);
						partner.getRequestManager().setState(RequestState.PARTICIPATING);
						return;						
					}
				}
			}
			if(partner.getRequestManager().getState() == RequestState.CONFIRM_1) {
				secondScreen(player);
				return;
			}
			player.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 30, "Waiting for other player...");
			partner.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 30, "Other player has accepted");
			player.getRequestManager().setState(RequestState.CONFIRM_1);
			break;
		case 2:
			if(partner.getRequestManager().getState() == RequestState.CONFIRM_2) {
				player.getRequestManager().finishRequest();
			} else {
				player.getRequestManager().setState(RequestState.CONFIRM_2);
				player.getActionSender().sendString(SECOND_TRADE_SCREEN, 4, "Waiting for other player...");
				partner.getActionSender().sendString(SECOND_TRADE_SCREEN, 4, "Other player has accepted");
			}
			break;
		}
		player.getActionSender().removeChatboxInterface();
	}

	/**
	 * Updates the first trade screen for a player and the acquaintance.
	 * @param player The player.
	 */
	public static void updateFirstScreen(Player player) {
		if(player.getInterfaceState().getCurrentInterface() != TRADE_INVENTORY_INTERFACE) {
			return;
		}
		Player partner = player.getRequestManager().getAcquaintance();
		if(partner == null) {
			return;
		}
		player.getActionSender().sendString(335, 9, partner.getName() + " has " + partner.getInventory().freeSlots() + " free inventory slots.");
		partner.getActionSender().sendString(335, 9, player.getName() + " has " + player.getInventory().freeSlots() + " free inventory slots.");

	}

}
