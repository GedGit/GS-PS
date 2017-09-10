package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.util.NameUtils;

/**
 * A packet sent when the player enters a custom amount for banking etc.
 * @author Graham Edgecombe
 *
 */
public class EnterTextPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		String text = NameUtils.formatName(packet.getRS2String());
		if(player.getAttribute("cutScene") != null) {
			return;
		}
		if(text.length() > 15) {
			text = text.substring(0, 15);
		}
		if(player.getInterfaceState().isEnterAmountInterfaceOpen()) {
			player.getInterfaceState().closeEnterTextInterface(text);
		}
	}

}
