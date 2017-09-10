package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;

/**
 * Switch item packet handler.
 * @author Graham Edgecombe
 *
 */
public class SetAppearancePacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int gender = packet.get() & 0xFF;
		int head = packet.get() & 0xFF;
		final int beard = packet.get() & 0xFF;
		final int chest = packet.get() & 0xFF;
		final int arms = packet.get() & 0xFF;
		final int hands = packet.get() & 0xFF;
		final int legs = packet.get() & 0xFF;
		final int feet = packet.get() & 0xFF;
		final int hairColour = packet.get() & 0xFF;
		final int torsoColour = packet.get() & 0xFF;
		final int legColour = packet.get() & 0xFF;
		final int feetColour = packet.get() & 0xFF;
		final int skinColour = packet.get() & 0xFF;

		int look[] = new int[13];
		look[0] = gender;

		look[6] = head;
		look[7] = chest;
		look[8] = arms;
		look[9] = hands;
		look[10] = legs;
		look[11] = feet;
		look[12] = beard;

		look[1] = hairColour;
		look[2] = torsoColour;
		look[3] = legColour;
		look[4] = feetColour;
		look[5] = skinColour;
		
		
		player.getAppearance().setLook(look);
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		player.getActionSender().removeInterface().removeInterface2();
	}

}
