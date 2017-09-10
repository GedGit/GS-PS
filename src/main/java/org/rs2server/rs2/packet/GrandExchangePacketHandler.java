package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;

import java.text.NumberFormat;
import java.util.Locale;

public class GrandExchangePacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int itemId = packet.getShort();
		if (player.getInterfaceAttribute("priceSearch") != null) {
			Item item = new Item(itemId);
			if (item.getDefinition() == null || item.getDefinition2() == null) {
				return;
			}
			player.getActionSender().sendItemOnInterface(464, 8, itemId, 1)
					.sendCS2Script(600, new Object[] { 30408716, 15, 1, 0 }, "iiiI").sendString(464, 12,
							item.getDefinition2().getName() + ":<br><col=ffffff>" + NumberFormat
									.getNumberInstance(Locale.ENGLISH).format(item.getDefinition().getStorePrice())
									+ "</col>");
			player.removeInterfaceAttribute("priceSearch");
		} else
			player.getActionSender().sendConfig(1043, 17639).sendConfig(563, 1).sendConfig(1151, itemId); // XXX was disabled
	}

}
