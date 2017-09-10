package org.rs2server.rs2.net;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.packet.DefaultPacketHandler;
import org.rs2server.rs2.packet.PacketHandler;

/**
 * Managers <code>PacketHandler</code>s.
 * 
 * @author Graham Edgecombe
 *
 */
public class PacketManager {

	/**
	 * The instance.
	 */
	private static final PacketManager INSTANCE = new PacketManager();

	/**
	 * Gets the packet manager instance.
	 * 
	 * @return The packet manager instance.
	 */
	public static PacketManager getPacketManager() {
		return INSTANCE;
	}

	/**
	 * The packet handler array.
	 */
	private PacketHandler[] packetHandlers = new PacketHandler[256];

	/**
	 * Creates the packet manager.
	 */
	public PacketManager() {
		
		//packetHandlers[235] = new CommandPacketHandlerOld();
		
		/*
		 * Set default handlers.
		 */
		final PacketHandler defaultHandler = new DefaultPacketHandler();
		for (int i = 0; i < packetHandlers.length; i++) {
			if (packetHandlers[i] == null) {
				packetHandlers[i] = defaultHandler;
			}
		}
	}

	/**
	 * Binds an opcode to a handler.
	 * 
	 * @param id
	 *            The opcode.
	 * @param handler
	 *            The handler.
	 */
	public void bind(int id, PacketHandler handler) {
		packetHandlers[id] = handler;
	}

	/**
	 * Handles a packet.
	 * 
	 * @param session
	 *            The session.
	 * @param packet
	 *            The packet.
	 */
	public void handle(Player player, Packet packet) {
		try {
			packetHandlers[packet.getOpcode()].handle(player, packet);
		} catch (Throwable ex) {
			System.out.println("Packet Error " + packet.getOpcode());
			ex.printStackTrace();
		}
	}

}
