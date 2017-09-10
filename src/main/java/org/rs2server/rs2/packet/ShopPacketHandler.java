package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Shop;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;

public class ShopPacketHandler implements PacketHandler {

	private static final int VALUE = 255;
	private static final int OPTION_1 = 149;
	private static final int OPTION_5 = 194;
	private static final int OPTION_10 = 159;
	private static final int SELL_50 = 148;

	@Override
	public void handle(Player player, Packet packet) {
		System.out.println("ATTEMPTING TO HANDLE PACKET:" + packet);
		switch (packet.getOpcode()) {
		case VALUE:
			handleItemValue(player, packet);
			break;
		case OPTION_1:
			handleItemOption1(player, packet);
			break;
		case OPTION_5:
			handleItemOption5(player, packet);
			break;
		case OPTION_10:
			handleItemOption10(player, packet);
			break;
		case SELL_50:
			handleItemOption50(player, packet);
			break;
		}
	}

	private void handleItemOption50(Player player, Packet packet) {
		int interfaceHash = packet.getInt();
		int interfaceId = (interfaceHash >> 16);
		int slot = packet.getShort();
		int itemId = packet.getShort();
		Item item = new Item(itemId);
		switch (interfaceId) {
		case 301:
			Shop.sellItem(player, slot, item.getId(), 50);
			break;
		}
	}

	private void handleItemValue(Player player, Packet packet) {
		int child = packet.getInt1();
		int itemId = packet.getShortA();
		int slot = packet.getLEShortA();

		Item item = new Item(itemId);
		switch (child) {
		case 0:// 0
			Shop.valueItem(player, slot, item.getId());
			break;
		case 75:
			Shop.costItem(player, slot, item.getId());
			break;
		}
	}

	private void handleItemOption1(Player player, Packet packet) {
		int itemId = packet.getShortA();
		int slot = packet.getShortA();
		int interfaceHash = packet.getInt();
		int interfaceId = interfaceHash >> 16;
		Item item = new Item(itemId);
		System.out.println("Interface: " + interfaceId + ", Item: " + itemId);
		switch (interfaceId) {
		case 300:
			Shop.buyItem(player, slot, item.getId(), 1);
			break;
		case 301:
			Shop.sellItem(player, slot, item.getId(), 1);
			break;
		}
	}

	private void handleItemOption5(Player player, Packet packet) {
		int slot = packet.getShort();
		int child = packet.getInt2();
		int itemId = packet.getLEShortA();
		Item item = new Item(itemId);
		System.out.println("Child: " + child);
		switch (child) {
		case 0:// 0
			Shop.sellItem(player, slot, item.getId(), 5);
			break;
		case 75:
			Shop.buyItem(player, slot, item.getId(), 5);
			break;
		}
	}

	private void handleItemOption10(Player player, Packet packet) {
		int slot = packet.getLEShortA();
		int interfaceHash = packet.getLEInt();
		int interfaceId = interfaceHash >> 16;
		int itemId = packet.getShort();

		Item item = new Item(itemId);
		switch (interfaceId) {
		case 300:
			DialogueManager.openDialogue(player, 60);
			player.getInterfaceState().setShopItem(itemId, slot);
			// Shop.buyItem(player, slot, item.getId(), 10);
			break;
		case 301:
			Shop.sellItem(player, slot, item.getId(), 10);
			break;
		}
	}

}
