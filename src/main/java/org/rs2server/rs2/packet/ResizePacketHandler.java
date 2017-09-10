package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender.TabMode;
import org.rs2server.rs2.net.Packet;

public class ResizePacketHandler implements PacketHandler {

	@SuppressWarnings("unused")
	@Override
	public void handle(Player player, Packet packet) {
		int mode = packet.get();
		int width = packet.getShort();
		int height = packet.getShort();
		switch (mode) {
		case 1:
			player.setAttribute("tabmode", TabMode.FIXED.getPane());
			if (player.loadedIn) {
				player.getActionSender()
						.sendWindowPane(TabMode.FIXED.getPane());
				player.getActionSender().sendSidebarInterfaces();
			}
			break;
		case 2:
			if (player.getAttribute("tabArranged") == null || player.getAttribute("tabArranged") != null && !(Boolean) player.getAttribute("tabArranged")) {
				player.setAttribute("tabmode", TabMode.REARRANGED.getPane());
				if (player.loadedIn) {
					player.getActionSender().sendWindowPane(
							TabMode.REARRANGED.getPane());
					player.getActionSender().sendSidebarInterfaces();
				}
			} else if (player.getAttribute("tabArranged") != null && (Boolean) player.getAttribute("tabArranged")) {
				player.setAttribute("tabmode", TabMode.RESIZE.getPane());
				if (player.loadedIn) {
					player.getActionSender().sendWindowPane(
							TabMode.RESIZE.getPane());
					player.getActionSender().sendSidebarInterfaces();
				}
			}
			break;
		}
	}

}
