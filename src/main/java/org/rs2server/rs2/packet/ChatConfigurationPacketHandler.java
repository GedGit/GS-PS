package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;

public class ChatConfigurationPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int privateBefore = player.getInterfaceState().getPrivateChat();
		player.getInterfaceState().setPublicChat(packet.get());
		player.getInterfaceState().setPrivateChat(packet.get());
		player.getInterfaceState().setTrade(packet.get());
		if(privateBefore != player.getInterfaceState().getPrivateChat()) { //private chat has been toggled
			player.getPrivateChat().updateFriendList(true);
		}
	}

}