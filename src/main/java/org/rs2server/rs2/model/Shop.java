package org.rs2server.rs2.model;

import org.rs2server.Server;
import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.domain.service.api.PermissionService;
import org.rs2server.rs2.domain.service.api.PermissionService.PlayerPermissions;
import org.rs2server.rs2.model.container.*;
import org.rs2server.rs2.model.container.Container.Type;
import org.rs2server.rs2.model.container.impl.*;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.tickable.impl.*;
import org.rs2server.rs2.util.Misc;
import org.rs2server.util.ShopUtils;
import org.rs2server.util.XMLController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Handles everything shop-related.
 * 
 * @author Vichy.
 */
public class Shop {

	/**
	 * The number formatting instance.
	 */
	private static NumberFormat format = NumberFormat.getNumberInstance(Locale.ENGLISH);

	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(Shop.class.getName());

	/**
	 * The list of registered shops.
	 */
	private static List<Shop> shops;

	/**
	 * Returns a shop by its ID.
	 *
	 * @param id
	 *            The shop ID.
	 * @return The shop.
	 */
	public static Shop forId(int id) {
		return shops.get(id);
	}

	/**
	 * The id of the main stock.
	 */
	private int mainStockId;

	/**
	 * The shops name, as displayed on the interface.
	 */
	private String name = "Shop";

	/**
	 * The shop's type.
	 */
	private ShopType shopType;

	/**
	 * The shop's currency.
	 */
	private int currency = 995;

	/**
	 * The starting stock of this shop.
	 */
	private Item[] mainItems;

	/**
	 * The starting stock of this shop.
	 */
	private Item[] playerItems;

	/**
	 * The default stock of the shop.
	 */
	private Container defaultStock;

	/**
	 * The default stock of the shop.
	 */
	private boolean canIronmanBuy;

	/**
	 * The stock of the shop.
	 */
	private Container mainStock;

	/**
	 * The stock of the shop.
	 */
	private Container playerStock;

	/**
	 * @return the mainStockId
	 */
	public int getMainStockId() {
		return mainStockId;
	}

	/**
	 * @return the mainItems
	 */
	public Item[] getMainItems() {
		return mainItems;
	}

	/**
	 * @return the playerItems
	 */
	public Item[] getPlayerItems() {
		return playerItems;
	}

	/**
	 * @param defaultStock
	 *            the defaultStock to set
	 */
	public void setDefaultStock(Container defaultStock) {
		this.defaultStock = defaultStock;
	}

	/**
	 * @param mainStock
	 *            the mainStock to set
	 */
	public void setMainStock(Container mainStock) {
		this.mainStock = mainStock;
	}

	/**
	 * @param playerStock
	 *            the playerStock to set
	 */
	public void setPlayerStock(Container playerStock) {
		this.playerStock = playerStock;
	}

	/**
	 * Gets the shop's main stock.
	 *
	 * @return The shop's main stock.
	 */
	public Container getMainStock() {
		return mainStock;
	}

	/**
	 * Gets the shop's player stock.
	 *
	 * @return The shop's player stock.
	 */
	public Container getPlayerStock() {
		return playerStock;
	}

	/**
	 * Gets the shop's default stock.
	 *
	 * @return The shop's default stock.
	 */
	public Container getDefaultStock() {
		return defaultStock;
	}

	/**
	 * Gets the shop's name.
	 *
	 * @return The shop's name.
	 */
	public String getShopName() {
		return name;
	}

	/**
	 * Gets the shop's type.
	 *
	 * @return The shop's type.
	 */
	public ShopType getShopType() {
		return shopType;
	}

	/**
	 * Gets shop's currency.
	 *
	 * @return The shop's currency.
	 */
	public int getCurrency() {
		return currency;
	}

	/**
	 * Checks whether ironman can buy from the shop.
	 * 
	 * @return if ironman can buy items.
	 */
	public boolean canIronmanBuy() {
		return canIronmanBuy;
	}

	/**
	 * Creates a new shop instance.
	 *
	 * @param name
	 *            The shop's name.
	 * @param shopType
	 *            The shop's type.
	 * @param currency
	 *            The shop's currency.
	 * @param mainItems
	 *            The shop's main stock items.
	 * @param playerItems
	 *            The shop's main stock player items.
	 */
	public Shop(String name, int mainStockId, ShopType shopType, int currency, Item[] mainItems, Item[] playerItems,
			boolean canIronmanBuy) {
		this.name = name;
		this.mainStockId = mainStockId;
		this.shopType = shopType;
		this.currency = currency;
		this.mainItems = mainItems;
		this.playerItems = playerItems;
		this.canIronmanBuy = canIronmanBuy;
	}

	/**
	 * An enum containing all possible shop types.
	 * 
	 * @author Vichy.
	 *
	 */
	public static enum ShopType {

		/**
		 * A general store, which takes all items, buys items for 0.4 of their price and
		 * sells them for 0.8 of their price. Default stock will go to 0, and
		 * non-default will be removed if the stock is < 1.
		 */
		GENERAL_STORE,

		/**
		 * A specialist store that does buy items that are in its stock (none others
		 * though!). It will buy items for 0.6 of their price, and sell them for 1.0
		 */
		SPECIALIST_STORE_BUY,

		/**
		 * A specialist store that will not buy any stock. It will sell items for 1.0 of
		 * their price.
		 */
		SPECIALIST_STORE_NO_BUY;
	}

	/**
	 * Initialise all shops.
	 * 
	 * @throws IOException
	 */
	public static void init() throws IOException {
		if (shops != null)
			throw new IllegalStateException("Shops already loaded.");
		logger.info("Loading Shops definitions...");
		File file = new File("data/items/shops.xml");
		shops = new ArrayList<Shop>();
		if (file.exists()) {
			shops = XMLController.readXML(file);
			logger.info("Loaded " + shops.size() + " shops.");
		} else
			logger.info("Shops not found.");
		for (Shop shop : shops) {
			Container defaultStock = new Container(Type.ALWAYS_STACK, SIZE);
			Container playerStock = new Container(Type.ALWAYS_STACK, SIZE);
			Container mainStock = new Container(Type.ALWAYS_STACK, SIZE);
			if (shop.getMainItems() != null) {
				for (Item item : shop.getMainItems()) {
					if (item != null) {
						mainStock.add(item);
						defaultStock.add(item);
					}
				}
			}
			if (shop.getPlayerItems() != null) {
				for (Item item : shop.getPlayerItems()) {
					if (item != null) {
						playerStock.add(item);
						defaultStock.add(item);
					}
				}
			}
			shop.setDefaultStock(defaultStock);
			shop.setPlayerStock(playerStock);
			shop.setMainStock(mainStock);
		}
	}

	/**
	 * The shop size.
	 */
	public static final int SIZE = 40, TOURNAMENT_SIZE = 293;

	/**
	 * The player inventory interface.
	 */
	public static final int PLAYER_INVENTORY_INTERFACE = 301;

	/**
	 * The shop inventory interface.
	 */
	public static final int SHOP_INVENTORY_INTERFACE = 300, TOURNAMENT_INVENTORY_INTERFACE = 100;

	/**
	 * The shop's main stock.
	 */
	public static final int SHOP_MAIN_STOCK = 23;

	/**
	 * The shop's player stock.
	 */
	public static final int SHOP_PLAYER_STOCK = 24;

	/**
	 * Int array containing recipe for disaster glove ID's.
	 */
	public static final int[] RFD_GLOVES = { 7458, 7459, 7460, 7461, 7462 };

	/**
	 * Opens the shop for the specified player.
	 *
	 * @param player
	 *            The player to open the shop for.
	 */
	public static void open(Player player, int shopId, int stockType) {
		if (shopId == 29) { // ironman only shop
			if (!player.getPermissionService().isAny(player, PlayerPermissions.IRON_MAN,
					PlayerPermissions.ULTIMATE_IRON_MAN, PlayerPermissions.HARDCORE_IRON_MAN)) {
				player.sendMessage("Sorry, but this is an ironman only shop.");
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 12810, null,
						"Sorry, but this is an ironman only shop.");
				return;
			}
		}
		player.getInterfaceState().setOpenShop(shopId);
		Shop shop = shops.get(player.getInterfaceState().getOpenShop());
		if (player.getPermissionService().isAny(player, PermissionService.PlayerPermissions.IRON_MAN,
				PermissionService.PlayerPermissions.ULTIMATE_IRON_MAN,
				PermissionService.PlayerPermissions.HARDCORE_IRON_MAN) && !shop.canIronmanBuy()) {
			player.getActionSender().sendMessage("You may not purchase from this shop as an ironman.");
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 12810, null,
					"You may not purchase from this shop as an ironman.");
			return;
		}
		player.getActionSender().sendConfig(118, 17);
		player.getActionSender().sendInterface(SHOP_INVENTORY_INTERFACE, false);
		player.getActionSender().sendInterfaceInventory(PLAYER_INVENTORY_INTERFACE);
		player.getActionSender().sendCS2Script(917, new Object[] { -1, -1 }, "ii")
				.sendCS2Script(1074, new Object[] { shop.getShopName(), 51 }, "vs").sendAccessMask(1278, 300, 2, 0, 40)
				.sendCS2Script(149, Constants.SELL_PARAMETERS, "IviiiIsssss").sendAccessMask(1086, 301, 0, 0, 27);
		player.getInterfaceState().addListener(shop.getMainStock(),
				new ShopContainerListener(player, shopId, -1, 64251, 51));
		player.getInterfaceState().addListener(player.getInventory(),
				new InterfaceContainerListener(player, -1, 64209, 93));
		player.getInterfaceState().setOpenStockType(2);

		ItemDefinition.loadExchangePrices();
	}

	/**
	 * Used for setting custom item prices for custom shops.
	 * 
	 * @param item
	 *            the item.
	 * @return the price as integer.
	 */
	public static int getCustomPrice(Item item) {
		switch (item.getId()) {
		case 1038:
			return 15000;
		}
		return -1;
	}

	/**
	 * Sells an item.
	 *
	 * @param player
	 *            The player.
	 * @param slot
	 *            The slot in the player's inventory.
	 * @param id
	 *            The item id.
	 * @param amount
	 *            The amount of the item to sell.
	 */
	public static void sellItem(Player player, int slot, int id, int amount) {
		player.getActionSender().removeChatboxInterface();
		Shop shop = shops.get(player.getInterfaceState().getOpenShop());
		Item item = player.getInventory().get(slot);
		if (item == null)
			return; // invalid packet, or client out of sync
		if (item.getId() != id)
			return; // invalid packet, or client out of sync
		if (!item.getDefinition().isTradable()) {
			player.getActionSender().sendMessage("You cannot sell this item.");
			return;
		}
		int transferAmount = player.getInventory().getCount(id);
		if (amount >= transferAmount)
			amount = transferAmount;
		else if (transferAmount == 0)
			return; // invalid packet, or client out of sync
		boolean canSell = false;
		if (shop.getShopType() == ShopType.SPECIALIST_STORE_BUY) {
			if (shop.getMainStock().contains(item.getId()) || shop.getDefaultStock().contains(item.getId()))
				canSell = true;
		}
		if (shop.getShopType() == ShopType.GENERAL_STORE)
			canSell = true;
		if (item.getId() == shop.getCurrency())
			canSell = false;
		if (canSell) {
			Shop.open(player, player.getInterfaceState().getOpenShop(), 2);
			int shopSlot = shop.getMainStock().contains(item.getId()) ? shop.getMainStock().getSlotById(item.getId())
					: shop.getMainStock().freeSlot();
			if (shopSlot == -1)
				player.getActionSender().sendMessage("This shop is currently full.");
			else {
				if (shop.getMainStock().get(shopSlot) != null) {
					if (shop.getMainStock().get(shopSlot).getCount() + amount < 1
							|| shop.getMainStock().get(shopSlot).getCount() + amount > Constants.MAX_ITEMS) {
						player.getActionSender().sendMessage("This shop is currently full.");
						return;
					}
				}
				long totalAmount = amount;
				long totalValue = shop.getSellValue(player, item);

				long totalPrice = (totalAmount * totalValue);

				if (totalPrice > Integer.MAX_VALUE)
					amount = (Integer.MAX_VALUE / shop.getSellValue(player, item)) - 1;

				Item reward = new Item(shop.getCurrency(), amount * shop.getSellValue(player, item));

				// Increased sell price while wearing karamja gloves
				if (player.getEquipment().containsOneItem(11136, 11138, 11140, 13103) && shop.getCurrency() == 6529)
					reward.setCount((int) (reward.getCount() * 1.1));

				// Prevent people from abusing selling of runes for tokkul
				if (shop.getCurrency() == 6529)
					reward.setCount((int) (reward.getCount() / 4));

				Container temporaryInventory = new Container(Type.STANDARD, Inventory.SIZE);
				for (Item invItem : player.getInventory().toArray())
					temporaryInventory.add(invItem);
				temporaryInventory.remove(new Item(item.getId(), amount));
				if (!temporaryInventory.add(reward))
					return;
				player.getInventory().remove(new Item(item.getId(), amount));
				if (CacheItemDefinition.get(item.getId()).isNoted())
					item.setId(item.getId() - 1);
				shop.getMainStock().add(new Item(item.getId(), amount));
				player.getInventory().add(reward);
				World.getWorld().submit(new ShopItemRemoveTick(item, shop));

				handleLog(player, item, amount, false);
			}
		} else
			player.getActionSender().sendMessage("This shop will not buy that item.");
	}

	/**
	 * Buys an item.
	 *
	 * @param player
	 *            The player.
	 * @param slot
	 *            The slot in the player's inventory.
	 * @param id
	 *            The item id.
	 * @param amount
	 *            The amount of the item to buy.
	 */
	public static void buyItem(Player player, int slot, int id, int amount) {

		if (player.getInterfaceState().getOpenShop() == -1)
			return;

		final PermissionService permissionService = Server.getInjector().getInstance(PermissionService.class);
		Shop shop = shops.get(player.getInterfaceState().getOpenShop());
		Item item = shop.getMainStock().get(slot - 1);

		if (item == null || item.getId() != id || shop.getCostValue(player, item) < 0)
			return; // invalid packet, or client out of sync

		if (shop.getCostValue(player, item) < 1) {
			player.sendMessage("This item cannot be bought; report it to an administrator+!");
			return;
		}

		int shopId = player.getInterfaceState().getOpenShop();

		// Prevent ironmen from buying items sold by other players
		if (!shop.getDefaultStock().contains(item.getId())) {
			if (permissionService.isAny(player, PermissionService.PlayerPermissions.IRON_MAN,
					PermissionService.PlayerPermissions.ULTIMATE_IRON_MAN,
					PermissionService.PlayerPermissions.HARDCORE_IRON_MAN)) {
				player.getActionSender().sendMessage("You may not purchase this item as an ironman.");
				return;
			}
		}

		if (shopId == 65) { // vote point store
			int transferAmount = player.getInventory().freeSlots();
			// Set the amount to how many inventory spaces we have.
			if (amount >= transferAmount && (!ItemDefinition.forId(item.getId()).isStackable()))
				amount = transferAmount;
			// If we have 0 inventory space don't continue.
			else if (transferAmount == 0)
				return;
			if (shop.getMainStock().get(slot - 1).getCount() > 0) {

				// If buy amount greater than shop currently has then lower it down
				if (amount >= shop.getMainStock().get(slot - 1).getCount())
					amount = shop.getMainStock().get(slot - 1).getCount();

				// Can't afford to buy even one.
				if (player.getDatabaseEntity().getVotePoints() < shop.getCostValue(player,
						new Item(item.getId(), amount))) {
					player.sendMessage("You don't have enough vote points to buy this item.");
					return;
				}
				// If trying to buy more than can afford, set the amount that's affordable.
				if (!shop.hasCurrency(player, item, amount))
					amount = player.getDatabaseEntity().getVotePoints()
							/ shop.getCostValue(player, new Item(item.getId(), amount));

				// Constructing the item we're buying.
				Item reward = new Item(item.getId(), amount);
				player.getDatabaseEntity().setVotePoints(player.getDatabaseEntity().getVotePoints()
						- (amount * shop.getCostValue(player, new Item(item.getId(), amount))));
				player.getInventory().add(reward);
				shop.getMainStock().remove(reward);
				player.sendMessage("You've purchased <col=ff0000>" + Misc.withPrefix(item.getDefinition2().getName())
						+ "</col>; vote points left: <col=ff0000>"
						+ Misc.formatCurrency(player.getDatabaseEntity().getVotePoints()) + "</col>.");
				for (Item i : shop.getMainItems()) {
					if (reward.getId() == i.getId() && shop.getMainStock().get(slot - 1) == null)
						shop.getMainStock().add(new Item(reward.getId(), 0));
				}
				World.getWorld().submit(new ShopItemRestoreTick(item, shop, slot - 1));

				handleLog(player, item, amount, true);
			}
			return;
		}
		if (shopId == 2) { // loyalty point store
			int transferAmount = player.getInventory().freeSlots();
			if (amount >= transferAmount && (!ItemDefinition.forId(item.getId()).isStackable()))
				amount = transferAmount;
			else if (transferAmount == 0)
				return; // invalid packet, or client out of sync

			String currencyName = "loyalty points";
			if (shop.getMainStock().get(slot - 1).getCount() > 0) {
				if (amount >= shop.getMainStock().get(slot - 1).getCount())
					amount = shop.getMainStock().get(slot - 1).getCount();
				if (!shop.hasCurrency(player, item, amount)) {
					player.getActionSender()
							.sendMessage("You don't have enough " + currencyName + " to buy this item.");
				} else {
					if (!ShopUtils.canBuy(player, item))
						return;
					Item reward = new Item(item.getId(), amount);
					if (player.getInventory().freeSlots() < amount) {
						player.sendMessage("Not enough inventory space to purchase this item.");
						return;
					}
					player.getDatabaseEntity().setLoyaltyPoints(player.getDatabaseEntity().getLoyaltyPoints()
							- (amount * shop.getCostValue(player, new Item(item.getId(), amount))));
					player.getInventory().add(reward);
					shop.getMainStock().remove(reward);
					player.sendMessage(
							"You've purchased <col=ff0000>" + Misc.withPrefix(item.getDefinition2().getName())
									+ "</col>; loyalty points left: <col=ff0000>"
									+ Misc.formatCurrency(player.getDatabaseEntity().getLoyaltyPoints()) + "</col>.");
					for (Item i : shop.getMainItems()) {
						if (reward.getId() == i.getId() && shop.getMainStock().get(slot - 1) == null)
							shop.getMainStock().add(new Item(reward.getId(), 0));
					}
					World.getWorld().submit(new ShopItemRestoreTick(item, shop, slot - 1));
					handleLog(player, item, amount, true);
				}
			}
			return;
		}

		if (shopId == 13) { // rfd item chest
			int minigameStage = player.getSettings().getRFDState();
			if (minigameStage < 2 && (item.getId() == 7457 || item.getId() == 7458)) {
				player.sendMessage("You must complete the second wave in Recipe for disaster to buy these gloves!");
				return;
			}
			if (minigameStage < 3 && (item.getId() == 7459 || item.getId() == 7460)) {
				player.sendMessage("You must complete the third wave in Recipe for disaster to buy these gloves!");
				return;
			}
			if (minigameStage < 4 && (item.getId() == 7461 || item.getId() == 7462)) {
				player.sendMessage("You must have completed Recipe for Disaster to buy these gloves!");
				return;
			}
		}

		int transferAmount = player.getInventory().freeSlots();
		if (amount >= transferAmount && (!ItemDefinition.forId(item.getId()).isStackable()))
			amount = transferAmount;
		else if (transferAmount == 0)
			return; // invalid packet, or client out of sync
		if (transferAmount > 1000) {
			transferAmount = 1000;
			player.getActionSender().sendMessage("You cannot buy more than 1000 items at a time.");
		}
		if (shop.getMainStock().get(slot - 1).getCount() > 0) {
			if (amount >= shop.getMainStock().get(slot - 1).getCount())
				amount = shop.getMainStock().get(slot - 1).getCount();
			// Can't afford to buy even one.
			if (player.getInventory().getCount(shop.getCurrency()) < shop.getCostValue(player,
					new Item(item.getId(), amount))) {
				player.sendMessage("You don't have enough "
						+ CacheItemDefinition.get(shop.getCurrency()).getName().toLowerCase() + " to buy this item.");
				return;
			}
			// If trying to buy more than can afford, set the amount that's affordable.
			if (!shop.hasCurrency(player, item, amount))
				amount = player.getInventory().getCount(shop.getCurrency())
						/ shop.getCostValue(player, new Item(item.getId(), amount));

			Item reward = new Item(item.getId(), amount);
			Container temporaryInventory = new Container(Type.STANDARD, Inventory.SIZE);
			for (Item invItem : player.getInventory().toArray())
				temporaryInventory.add(invItem);
			temporaryInventory.remove(
					new Item(shop.getCurrency(), amount * shop.getCostValue(player, new Item(item.getId(), amount))));
			if (!temporaryInventory.add(reward))
				return;
			Item currency = new Item(shop.getCurrency(),
					amount * shop.getCostValue(player, new Item(item.getId(), amount)));

			// Decreased buy price while wearing karamja gloves
			if (player.getEquipment().containsOneItem(11136, 11138, 11140, 13103) && shop.getCurrency() == 6529)
				currency.setCount((int) (currency.getCount() * 0.9));

			player.getInventory().remove(currency);
			player.getInventory().add(reward);
			shop.getMainStock().remove(reward);
			for (Item i : shop.getMainItems()) {
				if (reward.getId() == i.getId() && shop.getMainStock().get(slot - 1) == null)
					shop.getMainStock().add(new Item(reward.getId(), 0));
			}
			World.getWorld().submit(new ShopItemRestoreTick(item, shop, slot - 1));
			handleLog(player, item, amount, true);
		}
	}

	/**
	 * Checks if the player has enough of given currency to purchase an item.
	 * 
	 * @param player
	 *            the player purchasing.
	 * @param item
	 *            the item to purchase.
	 * @param amt
	 *            the amount to purchase.
	 * @return if can purchase.
	 */
	public boolean hasCurrency(Player player, Item item, int amt) {
		Shop shop = shops.get(player.getInterfaceState().getOpenShop());
		int finalAmt = getCostValue(player, item);
		if (finalAmt == -1) {
			player.getActionSender().sendMessage("Currency Error.");
			return false;
		}
		finalAmt *= amt;
		if (player.getInterfaceState().getOpenShop() == 2) { // loyalty point shop
			if (player.getDatabaseEntity().getLoyaltyPoints() >= finalAmt)
				return true;
		}
		if (player.getInterfaceState().getOpenShop() == 65) { // vote point shop
			if (player.getDatabaseEntity().getVotePoints() >= finalAmt)
				return true;
		}
		return player.getInventory().getCount(shop.getCurrency()) >= finalAmt;
	}

	/**
	 * Used to reset all shops back to default.
	 */
	public static void resetAllShops() {
		try {
			Shop.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets items cost value.
	 * 
	 * @param player
	 *            the player
	 * @param item
	 *            the item
	 * @return the item cost
	 */
	public int getCostValue(Player player, Item item) {

		if (player.getInterfaceState().getOpenShop() == 11) // blood money store
			return ShopUtils.getBloodMoneyPrice(item);
		if (player.getInterfaceState().getOpenShop() == 2) // loyalty point store
			return ShopUtils.getLoyaltyPrice(item);
		if (player.getInterfaceState().getOpenShop() == 65) // vote point store
			return ShopUtils.getVotePrice(item);

		switch (player.getInterfaceState().getOpenStockType()) {
		case 1: // Main stock
			switch (getShopType()) {
			case GENERAL_STORE:
				return (int) (ItemDefinition.forId(item.getId()).getStorePrice() * 0.9 < 1 ? 1
						: ItemDefinition.forId(item.getId()).getStorePrice() * 0.9);
			case SPECIALIST_STORE_BUY:
			case SPECIALIST_STORE_NO_BUY:
				return (int) ItemDefinition.forId(item.getId()).getStorePrice();
			}
			break;
		case 2: // Player stock
			switch (getShopType()) {
			case GENERAL_STORE:
				return (int) (ItemDefinition.forId(item.getId()).getStorePrice() * 0.9 < 1 ? 1
						: ItemDefinition.forId(item.getId()).getStorePrice() * 0.9);
			case SPECIALIST_STORE_BUY:
			case SPECIALIST_STORE_NO_BUY:
				return ItemDefinition.forId(item.getId()).getStorePrice();
			}
			break;
		}
		return 1;
	}

	public int getSellValue(Player player, Item item) {
		switch (getShopType()) {
		case GENERAL_STORE:
		case SPECIALIST_STORE_BUY:
		case SPECIALIST_STORE_NO_BUY:
			return (int) (ItemDefinition.forId(item.getId()).getStorePrice() * 0.6 < 1 ? 1
					: ItemDefinition.forId(item.getId()).getStorePrice() * 0.6);
		}
		return 1;
	}

	public static void costItem(Player player, int slot, int id) {
		if (player.getInterfaceState().getOpenShop() == -1) {
			return;
		}

		Shop shop = shops.get(player.getInterfaceState().getOpenShop());
		Item item = shop.getMainStock().get(slot - 1);

		String itemName = item.getDefinition2().getName();

		// Blood money exchange
		if (player.getInterfaceState().getOpenShop() == 11) {
			player.getActionSender().sendMessage(itemName + ": currently costs "
					+ format.format(ShopUtils.getBloodMoneyPrice(item)) + " " + "Blood money.");
			return;
		}

		String currencyName = CacheItemDefinition.get(shop.getCurrency()).getName().toLowerCase();
		int costValue = shop.getCostValue(player, item);

		// Decreased buy price while wearing karamja gloves
		if (player.getEquipment().containsOneItem(11136, 11138, 11140, 13103) && shop.getCurrency() == 6529)
			costValue *= 0.9;

		if (currencyName.contains("survival token"))
			currencyName = "wintertodt reward tokens";

		// Used for custom point shops.
		else if (currencyName.contains("dwarf remains")) {
			if (player.getInterfaceState().getOpenShop() == 2) // loyalty point store
				currencyName = "loyalty points";
			if (player.getInterfaceState().getOpenShop() == 65) // vote point store
				currencyName = "vote points";
		}

		player.getActionSender()
				.sendMessage(itemName + ": currently costs " + format.format(costValue) + " " + currencyName + ".");
	}

	public static void valueItem(Player player, int slot, int id) {
		Shop shop = shops.get(player.getInterfaceState().getOpenShop());
		Item item = player.getInventory().get(slot);

		if (item == null || item.getId() != id) {
			return;
		}

		if (!item.getDefinition().isTradable()) {
			player.getActionSender().sendMessage("You cannot sell this item.");
			return;
		}

		boolean message = false;

		if (shop.getShopType() == ShopType.GENERAL_STORE)
			message = true;

		if (shop.getShopType() == ShopType.SPECIALIST_STORE_BUY) {
			if (shop.getMainStock().contains(item.getId()) || shop.getDefaultStock().contains(item.getId()))
				message = true;
		}

		String currencyName = CacheItemDefinition.get(shop.getCurrency()).getName().toLowerCase();

		int finalValue = shop.getSellValue(player, item);

		// To prevent people from abusing of rune selling for tokkul
		if (currencyName.contains("tokkul"))
			finalValue /= 4;

		// Increased sell price while wearing karamja gloves
		if (player.getEquipment().containsOneItem(11136, 11138, 11140, 13103) && currencyName.contains("tokkul"))
			finalValue *= 1.1;

		String shopAdd = "";

		if (finalValue >= 1000 && finalValue < 1000000) {
			shopAdd = "(" + (finalValue / 1000) + "K).";
		} else if (finalValue >= 1000000) {
			shopAdd = "(" + (finalValue / 1000000) + " million).";
		}

		String itemName = item.getDefinition2().getName();

		// Quick-fix for getting noted item names XXX
		if (itemName == null)
			itemName = item.getDefinition2().getNotedName();

		player.getActionSender().sendMessage(
				message ? itemName + ": shop will buy for " + finalValue + " " + currencyName + " " + shopAdd
						: "This shop owner has no interest in " + Misc.withPrefix(itemName) + ".");
	}

	public static void reloadShops() {
		shops = null;
		try {
			Shop.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles logging of shop items bought and sold.
	 * @param player
	 * @param item
	 * @param amount
	 * @param buy
	 */
	private static void handleLog(Player player, Item item, int amount, boolean buy) {
		try {
			BufferedWriter bf = new BufferedWriter(new FileWriter(
					"data/logs/shops/shop-" + player.getInterfaceState().getOpenShop() + ".txt", true));
			bf.write("[Player: " + player.getName() + ", on "
					+ DateFormat.getDateTimeInstance().format(new Date()) + "]: "+(buy ? "BOUGHT" : "SOLD")+" :  " + amount + " x "
					+ item.getId() + ".");
			bf.newLine();
			bf.flush();
			bf.close();
		} catch (IOException ignored) {
			System.out.println("Failed writing SHOP logs..");
		}
	}
}