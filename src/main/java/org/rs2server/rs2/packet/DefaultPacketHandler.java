package org.rs2server.rs2.packet;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;

import java.util.logging.Logger;

/**
 * Reports information about unhandled packets.
 * 
 * @author Graham Edgecombe
 *
 */
public class DefaultPacketHandler implements PacketHandler {

	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(DefaultPacketHandler.class.getName());

	@Override
	public void handle(Player player, Packet packet) {
		if (packet.getOpcode() != 89 && packet.getOpcode() != 88) {
			if (Constants.DEBUG)
				logger.info("[" + player.getName() + "] Packet : [opcode=" + packet.getOpcode() + " length="
						+ packet.getLength() + " payload=" + packet.getPayload() + "]");
		}
	}

}
