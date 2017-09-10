package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;

public class MoveItemPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		player.resetAfkTolerance();
		switch (packet.getOpcode()) {
		case 59:
			handleMoveOnInterface(player, packet);
			break;
		case 6:
			handleInventorySwitch(player, packet);
			break;
		}
	}

	@SuppressWarnings("unused")
	public void handleInventorySwitch(Player player, Packet packet) {
		int fromId = packet.getShortA();
		int toId = packet.getShort();
		int someByte = packet.getByteA();
		int interfaceHash = packet.getInt1();
		int interfaceId = interfaceHash << 16;
		int interfaceChildId = interfaceHash & 0xFFFF;
		if (interfaceHash == 0) {
			player.getInventory().swap(fromId, toId);
		}
	}

	@SuppressWarnings("unused")
	public void handleMoveOnInterface(Player player, Packet packet) {
		int toId = packet.getLEShortA();
		int fromId = packet.getLEShort();
		int fromItemId = packet.getLEShort();
		int value4 = packet.getLEInt();
		int toItemId = packet.getShortA();
		int value6 = packet.getInt1();

		int fromInterfaceId = value4 >> 16;
		int toInterfaceId = value6 >> 16;
		int tabId = value6 & 0xFFFF;
		int tabId2 = value4 & 0xFFFF;

		switch (fromInterfaceId) {
		case Bank.BANK_INVENTORY_INTERFACE:
			/*
			 * Bank.
			 */
			// if (player.getBank().isCheckingBank()) {
			// return;
			// }
			int tabIndex = tabId == 10 ? toId - 9 : 11;
			if (toId >= 818)
				tabIndex = toId - 817;
			if (tabIndex == 1)
				tabIndex = 11;
			player.getBank().setFiringEvents(false);
			try {

				if (tabIndex == 11 && toItemId != -1) {
					if (fromId < 0 || fromId >= Bank.SIZE || toId < 0 || toId >= Bank.SIZE) {
						break;
					}
					if (player.getSettings().isSwapping()) {
						Item temp = player.getBank().get(fromId);
						Item temp2 = player.getBank().get(toId);
						player.getBank().set(fromId, temp2);
						player.getBank().set(toId, temp);
					} else {
						if (toId > fromId) {
							player.getBank().insert(fromId, toId - 1);
						} else if (fromId > toId) {
							player.getBank().insert(fromId, toId);
						}
					}
					break;
				} else {
					if (tabIndex > -1) {
						toId = tabIndex == 11 ? player.getBank().freeSlot()
								: player.getBanking().getTab()[tabIndex] + player.getBanking().getItemsInTab(tabIndex);
						int fromTab = player.getBanking().getTabByItemSlot(fromId);
						if (toId > fromId) {
							player.getBank().insert(fromId, toId - 1);
						} else if (fromId > toId) {
							player.getBank().insert(fromId, toId);
						}
						player.getBanking().increaseTabStartSlots(tabIndex);
						player.getBanking().decreaseTabStartSlots(fromTab);
						break;
					}
				}
			} finally {
				player.getBank().setFiringEvents(true);
				player.getBank().fireItemsChanged();
			}

			break;
		case Bank.PLAYER_INVENTORY_INTERFACE:
			player.getInventory().swap(fromId, toId);
			break;
		default:
			break;
		}
	}
}