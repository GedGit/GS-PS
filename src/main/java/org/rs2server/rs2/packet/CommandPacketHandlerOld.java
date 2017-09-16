package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;

import java.util.ArrayList;

/**
 * Handles player commands (the ::words).
 * 
 * @author Graham Edgecombe
 *
 */
public class CommandPacketHandlerOld implements PacketHandler {

	public ArrayList<Location> locations = new ArrayList<Location>();
	static int i = 0;

	@SuppressWarnings("unused")
	@Override
	public void handle(final Player player, Packet packet) {
		String commandString = packet.getRS2String();
		if (player.getAttribute("cutScene") != null)
			return;
		commandString = commandString.replaceAll(":", "");
		String[] args = commandString.split(" ");
		String command = args[0].toLowerCase();
	}
}