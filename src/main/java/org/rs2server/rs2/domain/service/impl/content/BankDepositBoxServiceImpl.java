package org.rs2server.rs2.domain.service.impl.content;

import org.rs2server.rs2.domain.service.api.content.BankDepositBoxService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * Created by Tim on 11/18/2015.
 */
public class BankDepositBoxServiceImpl implements BankDepositBoxService {

	public static final int DEPOSIT_INTERFACE = 192;

	@Override
	public void openDepositBox(@Nonnull Player player) {
		@SuppressWarnings("unused")
		int inventoryChild = (int) player.getAttribute("tabmode") == 161 ? 63
				: (int) player.getAttribute("tabmode") == 164 ? 61 : 65;
		@SuppressWarnings("unused")
		int equipmentChild = (int) player.getAttribute("tabmode") == 161 ? 64
				: (int) player.getAttribute("tabmode") == 164 ? 62 : 66;
		player.getActionSender().sendConfig(867, 37843994)
				// .removeInterface(equipmentChild)
				// .removeInterface(inventoryChild)
				.sendAccessMask(548, 47, -1, -1).sendAccessMask(548, 48, -1, -1)
				.sendCS2Script(915, new Object[] { 3 }, "i").sendInterface(192, false)
				.sendAccessMask(1180734, 192, 2, 0, 27);
	}

	@Override
	public void handleInterfaceActions(@Nonnull Player player, int button, int childButton, int childButton2,
			int menuIndex) {
		Item item = player.getInventory().get(childButton);
		if (item == null)
			return;
		switch (button) {
		/**
		 * Send an item to bank.
		 */
		case 2:
			switch (menuIndex) {
			/**
			 * Deposit a single item
			 */
			case 0:
				Bank.depositInventory(player, childButton, childButton2, 1);
				break;
			/**
			 * Deposit 5 of an item
			 */
			case 1:
				Bank.depositInventory(player, childButton, childButton2, 5);
				break;
			/**
			 * Deposit 10 of an item
			 */
			case 2:
				Bank.depositInventory(player, childButton, childButton2, 10);
				break;
			/**
			 * Deposit all of an item
			 */
			case 3:
				Bank.depositInventory(player, childButton, childButton2, player.getInventory().getCount(childButton2));
				break;
			/**
			 * Examine an item
			 */
			case 9:
				break;
			/**
			 * Deposit x amount of an item
			 */
			case 10:
				break;
			}
			break;
		/**
		 * Deposit inventory into bank.
		 */
		case 3:
			player.getInventory().setFiringEvents(false);
			player.getBank().setFiringEvents(false);
			try {
				for (int i = 0; i < Inventory.SIZE; i++) {
					Item bankedItem = player.getInventory().get(i);
					if (bankedItem != null)
						Bank.depositInventory(player, i, item.getId(), player.getInventory().getCount(item.getId()),
								false);
				}
			} finally {
				player.getInventory().setFiringEvents(true);
				player.getBank().setFiringEvents(true);

				player.getBank().fireItemsChanged();
				player.getInventory().fireItemsChanged();
			}
			break;
		}
	}
}
