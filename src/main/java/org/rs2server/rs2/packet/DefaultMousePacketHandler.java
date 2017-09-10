package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;

public class DefaultMousePacketHandler implements PacketHandler {

	@SuppressWarnings("unused")
	@Override
	public void handle(Player player, Packet packet) {
		int a = packet.getShort();
		int b = packet.getShort();//gotta be fucking kidding.
		int c = packet.getShort();//probably coordinates lol.
		
	}

}
