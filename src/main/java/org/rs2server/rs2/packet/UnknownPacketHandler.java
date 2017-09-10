package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.container.Trade;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;

public class UnknownPacketHandler implements PacketHandler {

	/**
	 * WHY DO THEY DO THIS LOL
	 */
	@Override
	public void handle(Player player, Packet packet) {
		if (!player.getInterfaceAttributes().containsKey("button_press")) {
			return;
		}
		int slot = packet.get();
		int button = player.getInterfaceAttribute("button_press");
		Item item;
		switch(player.getInterfaceState().getCurrentInterface()) {
		case Trade.TRADE_INVENTORY_INTERFACE:
			switch(button) {
			case 33: //Items on the interface
				if (slot >= 0 && slot < Trade.SIZE) {
					item = player.getTrade().get(slot);
					if(item != null && item.getDefinition() != null) {
						Trade.removeItem(player, slot, item.getId(), 1);
					}
				}
				break;
			case 0: 
				if (slot >= 0 && slot < Trade.SIZE) {
					item = player.getInventory().get(slot);
					if(item != null && item.getDefinition() != null) {
						Trade.offerItem(player, slot, item.getId(), 1);
					}
				}
				break;
			}
			break;
		}
	}


}
