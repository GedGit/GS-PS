package org.rs2server.rs2.model.player;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.container.impl.BankContainerListener;
import org.rs2server.rs2.model.container.impl.InterfaceContainerListener;

/**
 * 
 * @author Nine
 *
 */
public class Banking {

	public static int TAB_SIZE = 12;

	private final int[] tabStartSlot = new int[TAB_SIZE];
	private int currentTab;

	private Player player;

	public Banking(Player player) {
		this.player = player;
	}

	public void increaseTabStartSlots(int startId) {
		for (int i = startId + 1; i < tabStartSlot.length; i++) {
			tabStartSlot[i]++;
		}
	}

	public void decreaseTabStartSlots(int startId) {
		if (startId == 11)
			return;
		for (int i = startId + 1; i < tabStartSlot.length; i++) {
			tabStartSlot[i]--;
		}
		if (getItemsInTab(startId) == 0) {
			collapseTab(startId);
		}
	}

	public void insert(int fromId, int toId) {
		Item temp = player.getBank().toArray()[fromId];
		if (toId > fromId) {
			for (int i = fromId; i < toId; i++) {
				player.getBank().set(i, player.getBank().get(i + 1));
			}
		} else if (fromId > toId) {
			for (int i = fromId; i > toId; i--) {
				player.getBank().set(i, player.getBank().get(i - 1));
			}
		}
		player.getBank().set(toId, temp);
	}

	public int getItemsInTab(int tabId) {
		return tabStartSlot[tabId + 1] - tabStartSlot[tabId];
	}

	public int getTabByItemSlot(int itemSlot) {
		int tabId = 0;
		for (int i = 0; i < tabStartSlot.length; i++) {
			if (itemSlot >= tabStartSlot[i]) {
				tabId = i;
			}
		}
		return tabId;
	}

	public void collapseTab(int tabId) {
		int size = getItemsInTab(tabId);
		Item[] tempTabItems = new Item[size];

		if (currentTab == tabId) {
			currentTab = 0;
		}

		player.getBank().setFiringEvents(false);
		try {
			for (int i = 0; i < size; i++) {
				tempTabItems[i] = player.getBank().get(tabStartSlot[tabId] + i);
				player.getBank().set(tabStartSlot[tabId] + i, null);
			}
			player.getBank().shift();
			for (int i = tabId; i < tabStartSlot.length - 1; i++) {
				tabStartSlot[i] = tabStartSlot[i + 1] - size;
			}
			tabStartSlot[11] = tabStartSlot[11] - size;
			for (int i = 0; i < size; i++) {
				int slot = player.getBank().freeSlot();
				player.getBank().set(slot, tempTabItems[i]);
			}
		} finally {
			player.getBank().setFiringEvents(true);
			player.getBank().fireItemsChanged();
		}
	}

	public void sendTabConfig() {
		int config = 0;
		config += getItemsInTab(2);
		config += getItemsInTab(3) << 10;
		config += getItemsInTab(4) << 20;

		player.getActionSender().sendConfig(867, config);
		// System.out.println("Test - " + config);

		config = 0;
		config += getItemsInTab(5);
		config += getItemsInTab(6) << 10;
		config += getItemsInTab(7) << 20;
		player.getActionSender().sendConfig(1052, config);

		// System.out.println("Test1 - " + config);
		config = 0;
		config += getItemsInTab(8);
		config += getItemsInTab(9) << 10;
		config += getItemsInTab(10) << 20;
		player.getActionSender().sendConfig(1053, config);
		// System.out.println("Test2 - " + config);

		player.getActionSender().updateBankConfig();
		// player.getActionSender().sendConfig(115,
		// (player.getSettings().isWithdrawingAsNotes() ? 1 : 0currentTab * 4);
	}

	public int[] getTab() {
		return tabStartSlot;
	}// ctrl + n ??

	public void setCurrentTab(int tab) {
		this.currentTab = tab;
	}

	public int getCurrentTab() {
		return currentTab;
	}

	public int getActualTab() {
		return currentTab == 0 ? 11 : currentTab + 1;
	}

	public void openPlayerBank(Player ban) {
		ban.getBank().shift();
		player.getActionSender().sendInterface(12, false);
		player.getActionSender().sendInterfaceInventory(15);
		player.getActionSender().sendString(12, 4, "The Bank of " + Constants.SERVER_NAME);
		player.getInterfaceState().addListener(ban.getBank(), new InterfaceContainerListener(player, -1, 64207, 95));
		player.getInterfaceState().addListener(ban.getBank(), new BankContainerListener(player));
		player.getInterfaceState().addListener(player.getInventory(),
				new InterfaceContainerListener(player, -1, 64209, 93));
		player.getInterfaceState().addListener(player.getInventory(),
				new InterfaceContainerListener(player, 149, 0, 93));

		player.getActionSender().updateBankConfig();

		// player.getActionSender().sendAccessMask(0, 799, 1311998, 12 << 16 |
		// 12);
		// player.getActionSender().sendAccessMask(809, 817, 2, 12 << 16 | 12);
		// player.getActionSender().sendAccessMask(818, 827, 1048576,
		// 12 << 16 | 12);
		// player.getActionSender().sendAccessMask(10, 10, 1048578, 12 << 16 |
		// 10);
		// player.getActionSender().sendAccessMask(11, 19, 1179714, 12 << 16 |
		// 10);
		// player.getActionSender().sendAccessMask(0, 27, 1181438, 15 << 16 |
		// 3);
		// player.getActionSender().sendAccessMask(0, 27, 1054, 15 << 16 | 12);
		// player.getActionSender().sendAccessMask(0, 3, 2, 12 << 16 | 32);

		player.getActionSender().sendString(12, 7, "800");
	}

	public int[] getTabStartSlot() {
		return tabStartSlot;
	}

	public int getFreeSlot(int currentTab) {
		return player.getBanking().getTab()[currentTab] + player.getBanking().getItemsInTab(currentTab);
	}
}
