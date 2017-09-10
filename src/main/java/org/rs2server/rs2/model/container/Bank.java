package org.rs2server.rs2.model.container;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.rs2server.Server;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.domain.model.player.PlayerBankEntity;
import org.rs2server.rs2.domain.model.player.PlayerSettingsEntity;
import org.rs2server.rs2.domain.service.api.BankPinService;
import org.rs2server.rs2.domain.service.api.PermissionService;
import org.rs2server.rs2.domain.service.impl.BankPinServiceImpl;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.ItemDefinition;
import org.rs2server.rs2.model.container.impl.BankContainerListener;
import org.rs2server.rs2.model.container.impl.InterfaceContainerListener;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;

/**
 * Banking utility class.
 *
 * @author Graham Edgecombe
 * @author Twelve
 */
public class Bank {

	/**
	 * The bank size.
	 */
	public static final int SIZE = 520;

	/**
	 * The player inventory interface.
	 */
	public static final int PLAYER_INVENTORY_INTERFACE = 15;

	/**
	 * The bank inventory interface.
	 */
	public static final int BANK_INVENTORY_INTERFACE = 12;

	/**
	 * Opens the bank for the specified player.
	 *
	 * @param player
	 *            The player to open the bank for.
	 */
	public static void open(Player player) {

		PlayerBankEntity bankEntity = player.getDatabaseEntity().getBank();
		PlayerSettingsEntity settings = player.getDatabaseEntity().getPlayerSettings();
		PermissionService permissionService = Server.getInjector().getInstance(PermissionService.class);
		BankPinService bankPinService = Server.getInjector().getInstance(BankPinService.class);
		int[] requestedPin = bankEntity.getRequestedPin();

		DateTime requestTime = bankEntity.getPinRequestTime();
		DateTime now = DateTime.now(DateTimeZone.UTC);
		if (permissionService.is(player, PermissionService.PlayerPermissions.ULTIMATE_IRON_MAN) && !player.isAdministrator()) {
			player.getActionSender().sendMessage("You are an ultimate ironman and cannot use a bank.");
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 12813, null,
					"You are an ultimate ironman and cannot use a bank.");
			return;
		}
		if (requestTime != null && requestedPin != null
				&& requestTime.plus(Duration.standardDays(bankEntity.getRecoveryDelay())).isBefore(now)) {

			settings.setBankPinDigit1(requestedPin[0]);
			settings.setBankPinDigit2(requestedPin[1]);
			settings.setBankPinDigit3(requestedPin[2]);
			settings.setBankPinDigit4(requestedPin[3]);
			settings.setBankSecured(true);
			bankEntity.setPinRequestTime(null);
			bankEntity.setRequestedPin(null);
		}

		boolean secured = player.getDatabaseEntity().getPlayerSettings().isBankSecured();
		if (secured && !player.isEnteredPinOnce()) {
			bankPinService.openPinInterface(player, BankPinServiceImpl.PinType.EXISTING);
			return;
		}

		player.getBank().shift();
		player.getActionSender().sendInterface(BANK_INVENTORY_INTERFACE, false);
		player.getActionSender().sendInterfaceInventory(PLAYER_INVENTORY_INTERFACE);
		player.getActionSender().sendString(BANK_INVENTORY_INTERFACE, 4, "The Bank of " + Constants.SERVER_NAME);
		player.getInterfaceState().addListener(player.getBank(), new InterfaceContainerListener(player, -1, 64207, 95));
		player.getInterfaceState().addListener(player.getBank(), new BankContainerListener(player));
		player.getInterfaceState().addListener(player.getInventory(),
				new InterfaceContainerListener(player, -1, 64209, 93));
		player.getInterfaceState().addListener(player.getInventory(),
				new InterfaceContainerListener(player, 149, 0, 93));
		player.getInterfaceState().addListener(player.getLootingBag(),
				new InterfaceContainerListener(player, -1, 63786, 516));

		player.getActionSender().updateBankConfig();

		player.getActionSender().sendAccessMask(0, 799, 1311998, 12 << 16 | 12);
		player.getActionSender().sendAccessMask(809, 817, 2, 12 << 16 | 12);
		player.getActionSender().sendAccessMask(818, 827, 1048576, 12 << 16 | 12);
		player.getActionSender().sendAccessMask(10, 10, 1048578, 12 << 16 | 10);
		player.getActionSender().sendAccessMask(11, 19, 1179714, 12 << 16 | 10);
		player.getActionSender().sendAccessMask(0, 27, 1181438, 15 << 16 | 3);
		player.getActionSender().sendAccessMask(0, 27, 1054, 15 << 16 | 12);
		player.getActionSender().sendAccessMask(0, 3, 2, 12 << 16 | 32);
		player.getActionSender().sendAccessMask(1054, 15, 10, 0, 27);

		player.getActionSender().sendString(12, 7, "512");
	}

	/**
	 * Withdraws an item.
	 *
	 * @param player
	 *            The player.
	 * @param slot
	 *            The slot in the player's inventory.
	 * @param id
	 *            The item id.
	 * @param amount
	 *            The amount of the item to deposit.
	 */
	public static void withdraw(Player player, int slot, int id, int amount) {
		Item item = player.getBank().get(slot);

		if (item == null)
			return; // invalid packet, or client out of sync
		if (item.getId() != id)
			return; // invalid packet, or client out of sync
		if (player.getInterfaceState().isEnterAmountInterfaceOpen())
			player.getActionSender().removeEnterAmountInterface();
		int transferAmount = item.getCount();
		if (transferAmount >= amount)
			transferAmount = amount;
		else if (transferAmount == 0)
			return; // invalid packet, or client out of sync
		int tabId = player.getBanking().getTabByItemSlot(slot);
		int newId = item.getId(); // TODO deal with withdraw as notes!
		player.getBank().setFiringEvents(false);
		if (player.getSettings().isWithdrawingAsNotes()) {
			if (item.getDefinition().isNoteable())
				newId = item.getDefinition2().getNoted();
			else
				player.getActionSender().sendMessage("This item cannot be withdrawn as a note.");
		}
		ItemDefinition def = ItemDefinition.forId(newId);
		if (def == null)
			return;
		if (def.isStackable()) {
			if (player.getInventory().freeSlots() <= 0 && player.getInventory().getById(newId) == null) {
			}
		} else {
			int free = player.getInventory().freeSlots();
			if (transferAmount > free)
				transferAmount = free;
		}
		// now add it to inv
		if (newId != -1 && transferAmount > 0 && player.getInventory().add(new Item(newId, transferAmount), -1)) {
			int newAmount = item.getCount() - transferAmount;
			if (newAmount <= 0) {
				if (player.getDatabaseEntity().getPlayerSettings().isPlaceHolderEnabled())
					player.getBank().set(slot, new Item(item.getId(), 0));
				else
					player.getBank().set(slot, null);
			} else {
				player.getBank().set(slot, new Item(item.getId(), newAmount));
			}
		} else {
			player.getActionSender().sendMessage("You don't have enough inventory space to withdraw that many.");
		}
		if (player.getBank().get(slot) == null) {
			player.getBanking().decreaseTabStartSlots(tabId);
		}

		player.getBank().shift();
		player.getBank().setFiringEvents(true);
		player.getBank().fireItemsChanged();
		player.getActionSender().removeChatboxInterface();
	}

	/**
	 * Deposits an item.
	 *
	 * @param player
	 *            The player.
	 * @param slot
	 *            The slot in the player's inventory.
	 * @param id
	 *            The item id.
	 * @param amount
	 *            The amount of the item to deposit.
	 */
	public static void deposit(Player player, Container container, int slot, int id, int amount,
			boolean fireItemsChanged) {
		boolean inventoryFiringEvents = container.isFiringEvents();
		container.setFiringEvents(false);
		try {
			Item item = container.get(slot);
			if (item == null)
				return; // invalid packet, or client out of sync
			if (item.getId() != id)
				return; // invalid packet, or client out of sync
			if (player.getInterfaceState().isEnterAmountInterfaceOpen())
				player.getActionSender().removeEnterAmountInterface();
			if (player.getBank().size() >= Bank.SIZE) {
				player.getActionSender().sendMessage("You don't have enough space in your bank account.");
				return;
			}
			if (item.getId() == 12791 && player.getRunePouch().size() > 0) {
				player.getActionSender().sendMessage("Please empty the pouch before banking it.");
				return;
			}
			int transferAmount = container.getCount(id);
			if (transferAmount >= amount)
				transferAmount = amount;
			else if (transferAmount == 0)
				return; // invalid packet, or client out of sync
			boolean noted = item.getDefinition2().isNoted();
			if (item.getDefinition().isStackable() || noted) {
				int bankedId = noted ? item.getDefinition2().getNoted() : item.getId();
				if (player.getBank().freeSlots() < 1 && player.getBank().getById(bankedId) == null)
					player.getActionSender().sendMessage("You don't have enough space in your bank account.");
				int newInventoryAmount = item.getCount() - transferAmount;
				Item newItem;
				if (newInventoryAmount <= 0)
					newItem = null;
				else
					newItem = new Item(item.getId(), newInventoryAmount);
				if (!player.getBank().add(new Item(bankedId, transferAmount), -1))
					player.getActionSender().sendMessage("You don't have enough space in your bank account.");
				else {
					container.set(slot, newItem);
					container.fireItemsChanged();
					player.getBank().fireItemsChanged();
				}
			} else {
				if (player.getBank().freeSlots() < transferAmount)
					player.getActionSender().sendMessage("You don't have enough space in your bank account.");
				boolean exists = player.getBank().contains(item.getId());
				if (exists)
					player.getBank().add(new Item(item.getId(), transferAmount));
				else {
					int currentTab = player.getBanking().getActualTab();
					int freeSlot = player.getBank().freeSlot();
					int availableSlot = currentTab == 11 ? freeSlot : player.getBanking().getFreeSlot(currentTab);
					if (freeSlot == -1)
						player.getActionSender().sendMessage("You don't have enough space in your bank account.");
					else {
						if (currentTab != 11) {
							player.getBanking().insert(freeSlot, availableSlot);
							player.getBanking().increaseTabStartSlots(currentTab);
						}
						player.getBank().set(availableSlot, new Item(item.getId(), transferAmount));
					}
				}
				for (int i = 0; i < transferAmount; i++) {
					if (i == 0) {
						container.set(slot, null);
					} else {
						container.set(container.getSlotById(item.getId()), null);
					}
				}
				if (fireItemsChanged)
					container.fireItemsChanged();
			}
		} finally {
			container.setFiringEvents(inventoryFiringEvents);
		}
		player.getActionSender().removeChatboxInterface();
	}

	/**
	 * Deposits an item.
	 *
	 * @param player
	 *            The player.
	 * @param slot
	 *            The slot in the player's inventory.
	 * @param id
	 *            The item id.
	 * @param amount
	 *            The amount of the item to deposit.
	 */
	public static void depositInventory(Player player, int slot, int id, int amount) {
		if (player.getBank().size() >= Bank.SIZE) {
			player.getActionSender().sendMessage("You don't have enough space in your bank account.");
			return;
		}
		deposit(player, player.getInventory(), slot, id, amount, true);
	}

	public static void depositInventory(Player player, int slot, int id, int amount, boolean firingEvents) {
		if (player.getBank().size() >= Bank.SIZE) {
			player.getActionSender().sendMessage("You don't have enough space in your bank account.");
			return;
		}
		deposit(player, player.getInventory(), slot, id, amount, firingEvents);
	}

	/**
	 * Deposits an item.
	 *
	 * @param player
	 *            The player.
	 * @param slot
	 *            The slot in the player's equipment.
	 * @param id
	 *            The item id.
	 * @param amount
	 *            The amount of the item to deposit.
	 */
	public static void depositEquipment(Player player, int slot, int id, int amount) {
		deposit(player, player.getEquipment(), slot, id, amount, true);
	}

	public boolean wornItems = false;

	public boolean isEnablingWornItems() {
		return wornItems;
	}

	public void setEnablingWornItems(boolean wornItems) {
		this.wornItems = wornItems;
	}

}