package org.rs2server.rs2.model;

import org.rs2server.rs2.domain.service.api.content.gamble.IndexedDiceItem;
import org.rs2server.rs2.domain.service.api.content.trade.IndexedTradeItem;
import org.rs2server.rs2.domain.service.impl.content.gamble.DiceGameServiceImpl;
import org.rs2server.rs2.domain.service.impl.content.trade.TradeServiceImpl;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.ContainerListener;
import org.rs2server.rs2.model.container.Trade;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.Cooking;
import org.rs2server.rs2.model.skills.Cooking.CookingItem;
import org.rs2server.rs2.model.skills.Cooking.CookingMethod;
import org.rs2server.rs2.model.skills.FletchingAction;
import org.rs2server.rs2.model.skills.FletchingAction.FletchingItem;
import org.rs2server.rs2.model.skills.herblore.Herblore;
import org.rs2server.rs2.model.skills.herblore.Herblore.HerbloreType;
import org.rs2server.rs2.model.skills.herblore.Herblore.PrimaryIngredient;
import org.rs2server.rs2.model.skills.herblore.Herblore.SecondaryIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains information about the state of interfaces open in the client.
 * 
 * @author Graham Edgecombe
 *
 */
public class InterfaceState {

	/**
	 * The current open interface.
	 */
	private int currentInterface = -1;

	/**
	 * The current open interface walkable flag.
	 */
	private boolean walkableInterface = false;

	private boolean blackoutMap = false;

	/**
	 * The active enter amount interface.
	 */
	private int enterAmountInterfaceId = -1;

	/**
	 * The active enter amount id.
	 */
	private int enterAmountId;

	/**
	 * The active enter amount slot.
	 */
	private int enterAmountSlot;

	/**
	 * The open shop.
	 */
	private int openShop = -1;

	/**
	 * The main or player stock.
	 */
	private int openStockType;

	/**
	 * The open dialogue id.
	 */
	private int openDialogueId;

	/**
	 * The open autocast type. -1 = none 0 = normal 1 = defensive
	 */
	private int openAutocastType = -1;

	/**
	 * The last used autocast config.
	 */
	private int lastUsedAutocast;

	/**
	 * The public chat config. 0 = On 1 = Friends 2 = Off 3 = Hide
	 */
	private int publicChat = 0;

	/**
	 * The private chat config. 0 = On 1 = Friends 2 = Off 3 = Hide
	 */
	private int privateChat = 0;

	/**
	 * The trade offer config. 0 = On 1 = Friends 2 = Off 3 = Hide
	 */
	private int trade = 0;

	/**
	 * The clan this player is in.
	 */
	private String clan = "";

	/**
	 * The next dialogue id.
	 */
	private int[] nextDialogueId = new int[] { -1, -1, -1, -1, -1 };

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * A list of container listeners used on interfaces that have containers.
	 */
	private List<ContainerListener> containerListeners = new ArrayList<ContainerListener>();

	private int sideTabInterface;

	private int chatboxInterface = 0;

	/**
	 * The item id which is to be destroyed when the player is in the 'Are you
	 * sure you want to destroy this item' interface.
	 */
	private int destroyItemId = -1;
	private int currentInventoryInterface = -1;

	/**
	 * Creates the interface state.
	 */
	public InterfaceState(Player player) {
		this.player = player;
	}

	/**
	 * Checks if the specified interface is open.
	 * 
	 * @param id
	 *            The interface id.
	 * @return <code>true</code> if the interface is open, <code>false</code> if
	 *         not.
	 */
	public boolean isInterfaceOpen(int id) {
		return currentInterface == id;
	}

	public void setSidetabInterface(int id) {
		this.sideTabInterface = id;
	}

	public boolean hasSideTabInterfaceOpen() {
		return sideTabInterface > -1;
	}

	public void setChatboxInterface(int chatboxInterface) {
		this.chatboxInterface = chatboxInterface;
	}

	public int getChatboxInterface() {
		return chatboxInterface;
	}

	public boolean hasChatboxInterfaceOpen() {
		return chatboxInterface > 0;
	}

	/**
	 * Gets the current open interface.
	 * 
	 * @return The current open interface.
	 */
	public int getCurrentInterface() {
		return currentInterface;
	}

	/**
	 * Called when an interface is opened.
	 * 
	 * @param id
	 *            The interface.
	 */
	public void interfaceOpened(int id, boolean walkable) {
		if (currentInterface != -1 && currentInterface != id) {
			// interfaceClosed();
		}
		walkableInterface = walkable;
		currentInterface = id;
	}

	/**
	 * Called when an interface is closed.
	 */
	public void interfaceClosed() {
		currentInterface = -1;
		enterAmountInterfaceId = -1;
		openShop = -1;
		openStockType = -1;
		walkableInterface = false;
		if (openDialogueId == 12) {
			// player.getCombatState().setSpellbookSwap(false);
		}
		openDialogueId = -1;
		nextDialogueId = new int[] { -1, -1, -1, -1, -1 };
		removeListeners();
		// player.getActionQueue().clearRemovableActions();
		player.removeAllInterfaceAttributes();
	}

	/**
	 * Removes all listeners.
	 */
	public void removeListeners() {
		for (ContainerListener l : containerListeners) {
			player.getInventory().removeListener(l);
			player.getEquipment().removeListener(l);
			player.getBank().removeListener(l);
		}
	}

	/**
	 * Adds a listener to an interface that is closed when the inventory is
	 * closed.
	 * 
	 * @param container
	 *            The container.
	 * @param containerListener
	 *            The listener.
	 */
	public void addListener(Container container, ContainerListener containerListener) {
		container.addListener(containerListener);
		containerListeners.add(containerListener);
	}

	/**
	 * Called to open the enter amount interface.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 * @param slot
	 *            The slot.
	 * @param id
	 *            The id.
	 */
	public void openEnterAmountInterface(int interfaceId, int slot, int id) {
		enterAmountInterfaceId = interfaceId;
		enterAmountSlot = slot;
		enterAmountId = id;
		player.getActionSender().sendEnterAmountInterface();
	}

	/**
	 * Called to open the enter amount interface.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 * @param slot
	 *            The slot.
	 * @param id
	 *            The id.
	 */
	public void openEnterTextInterface(int interfaceId, String question) {
		enterAmountInterfaceId = interfaceId;
		player.getActionSender().sendEnterTextInterface(question);
	}

	/**
	 * Checks if the enter amount interface is open.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isEnterAmountInterfaceOpen() {
		return enterAmountInterfaceId != -1;
	}

	/**
	 * Called when the enter amount interface is closed.
	 * 
	 * @param amount
	 *            The amount that was entered.
	 */
	public void closeEnterAmountInterface(int amount) {
		try {
			switch (enterAmountInterfaceId) {
			case Bank.PLAYER_INVENTORY_INTERFACE:
				Bank.depositInventory(player, enterAmountSlot, enterAmountId, amount);
				player.getSettings().setLastWithdrawnValue(amount);
				player.getActionSender().sendConfig(304, (player.getSettings().getLastWithdrawnValue() * 2)
						| (player.getSettings().isSwapping() ? 0 : 1));
				break;
			case Bank.BANK_INVENTORY_INTERFACE:
				Bank.withdraw(player, enterAmountSlot, enterAmountId, amount);
				player.getSettings().setLastWithdrawnValue(amount);
				player.getActionSender().sendConfig(304, (player.getSettings().getLastWithdrawnValue() * 2)
						| (player.getSettings().isSwapping() ? 0 : 1));
				break;
			case Trade.TRADE_INVENTORY_INTERFACE:
				if (player.getTransaction() != null && isInterfaceOpen(TradeServiceImpl.TRADE_WIDGET)) {
					player.getTransaction().remove(player.getTransaction().get(player).get(),
							new IndexedTradeItem(null, amount));
				} else if (player.getDiceGameTransaction() != null
						&& isInterfaceOpen(DiceGameServiceImpl.TRADE_WIDGET)) {
					player.getDiceGameTransaction().remove(player.getDiceGameTransaction().get(player).get(),
							new IndexedDiceItem(null, amount));
				}
				// Trade.removeItem(player, enterAmountSlot, enterAmountId,
				// amount);
				break;
			case Trade.PLAYER_INVENTORY_INTERFACE:
				if (player.getTransaction() != null && isInterfaceOpen(TradeServiceImpl.TRADE_WIDGET)) {
					player.getTransaction().add(player.getTransaction().get(player).get(),
							new Item(enterAmountId, amount));
				} else if (player.getDiceGameTransaction() != null
						&& isInterfaceOpen(DiceGameServiceImpl.TRADE_WIDGET)) {
					player.getDiceGameTransaction().add(player.getDiceGameTransaction().get(player).get(),
							new Item(enterAmountId, amount));
				}
				// Trade.offerItem(player, enterAmountSlot, enterAmountId,
				// amount);
				break;
			case Shop.SHOP_INVENTORY_INTERFACE:
				Shop.buyItem(player, enterAmountSlot, enterAmountId, amount);
				break;
			case Shop.PLAYER_INVENTORY_INTERFACE:
				Shop.sellItem(player, enterAmountSlot, enterAmountId, amount);
				break;
			case 303:
			case 304:
			case 305:
			case 306:
				if (player.getInterfaceAttribute("fletch_item") != null) {
					player.getActionQueue().addAction(new FletchingAction(player, amount,
							(FletchingItem) player.getInterfaceAttribute("fletch_item")));
					player.removeInterfaceAttribute("fletch_item");
				}
				break;
			case 309:
				if (player.getInterfaceAttribute("cookItem") != null
						&& player.getInterfaceAttribute("cookMethod") != null) {
					if (amount > 0) {
						player.getActionQueue()
								.addAction(new Cooking(player, 2, amount,
										(CookingItem) player.getInterfaceAttribute("cookItem"),
										(CookingMethod) player.getInterfaceAttribute("cookMethod")));
						player.getActionSender().removeChatboxInterface();
						player.removeInterfaceAttribute("cookItem");
						player.removeInterfaceAttribute("cookMethod");
					}
				} else if (player.getInterfaceAttribute("herblore_index") != null
						&& player.getInterfaceAttribute("herblore_type") != null) {
					switch ((HerbloreType) player.getInterfaceAttribute("herblore_type")) {
					case PRIMARY_INGREDIENT:
						player.getActionQueue()
								.addAction(new Herblore(player, amount,
										PrimaryIngredient
												.forId((Integer) player.getInterfaceAttribute("herblore_index")),
										null, HerbloreType.PRIMARY_INGREDIENT));
						break;
					case SECONDARY_INGREDIENT:
						player.getActionQueue()
								.addAction(new Herblore(player, amount, null,
										SecondaryIngredient
												.forId((Integer) player.getInterfaceAttribute("herblore_index")),
										HerbloreType.SECONDARY_INGREDIENT));
						break;
					}
					player.removeInterfaceAttribute("herblore_index");
					player.removeInterfaceAttribute("herblore_type");
				}
				break;
			/*
			 * case 320://Skill tab if (amount > 99) { player.getActionSender().
			 * sendMessage("You can only set a stat level 1 through 99.");
			 * return; } int skill_id =
			 * player.getInterfaceAttribute("skillLevelChange"); String
			 * skill_name = ""; double exp =
			 * player.getSkills().getExperienceForLevel(amount); if (skill_id ==
			 * 0) { skill_name = "Attack"; } else if (skill_id == 1) {
			 * skill_name = "Defence"; } else if (skill_id == 2) { skill_name =
			 * "Strength"; } else if (skill_id == 3) { skill_name = "Hitpoints";
			 * } else if (skill_id == 4) { skill_name = "Range"; } else if
			 * (skill_id == 5) { skill_name = "Prayer"; } else if (skill_id ==
			 * 6) { skill_name = "Magic"; } if(skill_id == 1) { int[] equipment
			 * = new int[] { Equipment.SLOT_BOOTS, Equipment.SLOT_BOTTOMS,
			 * Equipment.SLOT_CHEST, Equipment.SLOT_CAPE, Equipment.SLOT_GLOVES,
			 * Equipment.SLOT_HELM, Equipment.SLOT_SHIELD }; for(int i = 0; i <
			 * equipment.length; i++) {
			 * if(player.getEquipment().get(equipment[i]) != null) {
			 * player.getActionSender().
			 * sendMessage("You can't change your Defence level whilst wearing equipment."
			 * ); return; } } } if(skill_id == 0 || skill_id == 2 || skill_id ==
			 * 4 || skill_id == 6) {
			 * if(player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
			 * player.getActionSender().sendMessage("You can't change your " +
			 * skill_name + " level whilst wielding any equipment."); return; }
			 * } if (skill_id == 5) { player.getSkills().setPrayerPoints(amount,
			 * true); player.getSkills().setExperience(skill_id, exp); } else {
			 * player.getSkills().setLevel(skill_id, amount);
			 * player.getSkills().setExperience(skill_id, exp); }
			 * player.getActionSender().sendMessage("Your "+ skill_name
			 * +" level has been set to "+ amount +".");
			 * player.getInterfaceAttributes().remove("skillLevelChange");
			 * break;
			 */
			}
		} finally {
			enterAmountInterfaceId = -1;
			player.getActionSender().removeChatboxInterface();
		}
	}

	/**
	 * Called when the enter amount interface is closed.
	 * 
	 * @param fletchAmount
	 *            The amount that was entered.
	 */
	public void closeEnterTextInterface(String text) {
		try {
			switch (enterAmountInterfaceId) {
			case 590:
				if (text.length() < 1) {
					return;
				}
				player.getPrivateChat().setChannelName(text);
				if (player.getPrivateChat().getMembers().size() > 0) {
					player.getPrivateChat().updateClanMembers();
				}
				player.getActionSender().sendString(590, 29, text);
				break;
			}
		} finally {
			enterAmountInterfaceId = -1;
		}
	}

	/**
	 * @return the openShop
	 */
	public int getOpenShop() {
		return openShop;
	}

	/**
	 * @param openShop
	 *            the openShop to set
	 */
	public void setOpenShop(int openShop) {
		this.openShop = openShop;
	}

	/**
	 * @return the openStockType
	 */
	public int getOpenStockType() {
		return openStockType;
	}

	/**
	 * @param openStockType
	 *            the openStockType to set
	 */
	public void setOpenStockType(int openStockType) {
		this.openStockType = openStockType;
	}

	/**
	 * @return the openDialogueId
	 */
	public int getOpenDialogueId() {
		return openDialogueId;
	}

	/**
	 * @param openDialogueId
	 *            the openDialogueId to set
	 */
	public void setOpenDialogueId(int openDialogueId) {
		this.openDialogueId = openDialogueId;
	}

	/**
	 * @return the nextDialogueId
	 */
	public int getNextDialogueId(int index) {
		return nextDialogueId[index]; 
	}

	/**
	 * @param nextDialogueId
	 *            the nextDialogueId to set
	 */
	public void setNextDialogueId(int index, int nextDialogueId) {
		this.nextDialogueId[index] = nextDialogueId;
	}

	/**
	 * @return the openAutocastType
	 */
	public int getOpenAutocastType() {
		return openAutocastType;
	}

	/**
	 * @param openAutocastType
	 *            the openAutocastType to set
	 */
	public void setOpenAutocastType(int openAutocastType) {
		this.openAutocastType = openAutocastType;
	}

	/**
	 * @return the lastUsedAutocast
	 */
	public int getLastUsedAutocast() {
		return lastUsedAutocast;
	}

	/**
	 * @param lastUsedAutocast
	 *            the lastUsedAutocast to set
	 */
	public void setLastUsedAutocast(int lastUsedAutocast) {
		this.lastUsedAutocast = lastUsedAutocast;
	}

	/**
	 * @return the publicChat
	 */
	public int getPublicChat() {
		return publicChat;
	}

	/**
	 * @param publicChat
	 *            the publicChat to set
	 */
	public void setPublicChat(int publicChat) {
		this.publicChat = publicChat;
	}

	/**
	 * @return the privateChat
	 */
	public int getPrivateChat() {
		return privateChat;
	}

	/**
	 * @param privateChat
	 *            the privateChat to set
	 */
	public void setPrivateChat(int privateChat) {
		this.privateChat = privateChat;
	}

	/**
	 * @return the trade
	 */
	public int getTrade() {
		return trade;
	}

	/**
	 * @param trade
	 *            the trade to set
	 */
	public void setTrade(int trade) {
		this.trade = trade;
	}

	/**
	 * @return the clan
	 */
	public String getClan() {
		return clan;
	}

	/**
	 * @param clan
	 *            the clan to set
	 */
	public void setClan(String clan) {
		this.clan = clan;
	}

	/**
	 * @return The walkableInterface.
	 */
	public boolean isWalkableInterface() {
		return walkableInterface;
	}

	/**
	 * @param walkableInterface
	 *            The walkableInterface to set.
	 */
	public void setWalkableInterface(boolean walkableInterface) {
		this.walkableInterface = walkableInterface;
	}

	public void setBlackout(boolean b) {
		this.blackoutMap = b;
	}

	public boolean getBlackout() {
		return blackoutMap;
	}

	private int shopItemId = -1;
	private int shopSlot = -1;

	public void setShopItem(int itemId, int slot) {
		this.shopItemId = itemId;
		this.shopSlot = slot;
	}

	public int getShopItem() {
		return shopItemId;
	}

	public int getShopSlot() {
		return shopSlot;
	}

	private boolean fletchingInterface;

	public boolean isFletchingInterface() {
		return fletchingInterface;
	}

	public void setFletchingInterface(boolean fletchingInterface) {
		this.fletchingInterface = fletchingInterface;
	}

	public int getDestroyItemId() {
		return destroyItemId;
	}

	public void setDestroyItemId(int destroyItemId) {
		this.destroyItemId = destroyItemId;
	}

	public void inventoryInterfaceOpened(int inventoryInterfaceId) {
		if (currentInventoryInterface != -1) {
			// interfaceClosed();
		}
		currentInventoryInterface = inventoryInterfaceId;
	}

	public void inventoryInterfaceClosed() {
		currentInventoryInterface = -1;
		player.removeAllInterfaceAttributes();
	}

	public int getCurrentInventoryInterface() {
		return currentInventoryInterface;
	}

	// public boolean wornItems = false;
	//
	// public boolean isEnablingWornItems() {
	// return wornItems;
	// }
	//
	// public void setEnablingWornItems(boolean wornItems) {
	// this.wornItems = wornItems;
	// }

}
