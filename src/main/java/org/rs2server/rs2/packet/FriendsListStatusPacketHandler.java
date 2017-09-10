package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;

public class FriendsListStatusPacketHandler implements PacketHandler{

	@SuppressWarnings("unused")
	@Override
	public void handle(Player player, Packet packet) {
		int a = packet.get();
		int b = packet.get();
		int c = packet.get();
	}

}
