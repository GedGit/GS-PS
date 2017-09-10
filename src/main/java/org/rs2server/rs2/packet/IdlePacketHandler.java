package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;

public class IdlePacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		/*player.getActionSender().sendLogout();*/
	}

}
