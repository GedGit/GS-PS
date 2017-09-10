package org.rs2server.rs2.packet;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.player.PrivateChat.ClanRank;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.util.NameUtils;
import org.rs2server.rs2.util.TextUtils;

public class PrivateMessagingPacketHandler implements PacketHandler {

	private static final int SEND_MESSAGE = 171, ADD_FRIEND = 128, REMOVE_FRIEND = 49, ADD_IGNORE = 179,
			REMOVE_IGNORE = 180;

	public PrivateMessagingPacketHandler() {
		Server.getInjector().getInstance(PlayerService.class);
	}

	@Override
	public void handle(Player player, Packet packet) {
		String name = packet.getRS2String();
		long nameAsLong = NameUtils.nameToLong(name);

		if (player.getAttribute("cutScene") != null)
			return;
		switch (packet.getOpcode()) {
		case SEND_MESSAGE: // d
			int numChars = packet.getSmart();
			String text = TextUtils.decryptPlayerChat(packet, numChars);
			if (player.getSettings().isMuted()) {
				player.getActionSender().sendMessage("You are muted and cannot speak.");
				return;
			}
			player.getPrivateChat().sendMessage(nameAsLong, text);
			break;
		case ADD_FRIEND: // A
			player.getPrivateChat().addFriend(nameAsLong, ClanRank.FRIEND);
			break;
		case REMOVE_FRIEND: // d
			player.getPrivateChat().removeFriend(nameAsLong);
			break;
		case ADD_IGNORE: // d
			player.getPrivateChat().addIgnore(nameAsLong);
			break;
		case REMOVE_IGNORE:
			player.getPrivateChat().removeIgnore(nameAsLong);
			break;
		}
	}

}