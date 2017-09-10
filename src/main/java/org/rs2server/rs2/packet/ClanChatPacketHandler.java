package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.player.PrivateChat;
import org.rs2server.rs2.model.player.PrivateChat.ClanRank;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.util.NameUtils;
import org.rs2server.rs2.util.TextUtils;

public class ClanChatPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		long nameAsLong;
		switch (packet.getOpcode()) {
		case 157:
			String clanPrefix = packet.getRS2String();
			player.getPrivateChat().setChannelName(clanPrefix);
			if (!World.getWorld().clanIsRegistered(player.getName())) {
				World.getWorld().getPrivateChat().put(player.getName(), player.getPrivateChat());
			}
			if (World.getWorld().clanIsRegistered(player.getName())) {
				player.getActionSender().sendString(590, 29, player.getPrivateChat().getChannelName());
			}
			player.getActionSender().sendString(590, 31, player.getPrivateChat().getEntryRank().getText());
			player.getActionSender().sendString(590, 33, player.getPrivateChat().getTalkRank().getText());
			player.getActionSender().sendString(590, 35, player.getPrivateChat().getKickRank().getText());
			break;
		case 241:
			if (player.getInterfaceState().getClan().length() < 1) {
				String firstName = packet.getRS2String();
				String name = NameUtils.formatName(firstName);
				player.getActionSender().sendClanMessage("Attempting to join channel...");
				if (!World.getWorld().privateIsRegistered(name)) {
					if (!World.getWorld().deserializePrivate(name)) {
						player.getActionSender().sendClanMessage("The channel you tried to join does not exist.");
						return;
					}
				}
				PrivateChat chat = World.getWorld().getPrivateChat().get(name);
				if (chat != null) {
					chat.addClanMember(player);
				}
			} else {
				player.getActionSender().sendClanMessage("You have left the channel.");
				World.getWorld().getPrivateChat().get(player.getInterfaceState().getClan()).removeClanMember(player);
			}
			break;
		case 29:
			String name = packet.getRS2String();
			int rank = packet.getByteS();
			nameAsLong = TextUtils.playerNameToLong(name);
			for (long l : player.getPrivateChat().getFriends().keySet()) {
				if (l == nameAsLong) {
					player.getPrivateChat().getFriends().put(nameAsLong, ClanRank.forId(rank));
					// player.getActionSender().sendFriend(l, 0, rank);
					player.getPrivateChat().updateClanMembers();
				}
			}
			break;
		case 204:
			@SuppressWarnings("unused")
			int stringlength = packet.get();
			name = packet.getRS2String();
			nameAsLong = TextUtils.playerNameToLong(name);
			if (player.getInterfaceState().getClan().length() < 1)
				return;

			PrivateChat privateChat = World.getWorld().getPrivateChat().get(player.getInterfaceState().getClan());
			if (!player.getName().equals(privateChat.getOwner())) {
				if (!privateChat.getFriends().containsKey(player.getNameAsLong()) || (privateChat.getFriends()
						.get(player.getNameAsLong()).getId() < privateChat.getKickRank().getId())) {
					player.getActionSender()
							.sendClanMessage("You do not have a high enough rank to kick in this clan channel.");
					return;
				}
				if (privateChat.getFriends().containsKey(nameAsLong)) {
					if (privateChat.getFriends().get(nameAsLong).getId() > privateChat.getFriends()
							.get(player.getNameAsLong()).getId()) {
						player.getActionSender()
								.sendClanMessage("You do not have a high enough rank to kick that person.");
						return;
					}
				}
			}
			for (Player p : privateChat.getMembers()) {
				if (p.getNameAsLong() == nameAsLong) {
					privateChat.removeClanMember(p);
					p.getActionSender().sendClanMessage("You have been kicked from the channel.");
					return;
				}
			}
			break;
		}
	}
}