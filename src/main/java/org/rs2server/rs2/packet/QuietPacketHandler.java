package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;

/**
 * A packet handler which takes no action i.e. it ignores the packet.
 * @author Graham Edgecombe
 *
 */
public class QuietPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
	}

}
