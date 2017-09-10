package org.rs2server.rs2.task.impl;

import org.apache.mina.core.session.IoSession;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.net.PacketManager;
import org.rs2server.rs2.task.Task;


/**
 * A task that is executed when a session receives a message.
 * @author Graham Edgecombe
 *
 */
public class SessionMessageTask implements Task {
	
	/**
	 * The session.
	 */
	private IoSession session;
	
	/**
	 * The packet.
	 */
	private Packet message;

	/**
	 * Creates the session message task.
	 * @param session The session.
	 * @param message The packet.
	 */
	public SessionMessageTask(IoSession session, Packet message) {
		this.session = session;
		this.message = message;
	}

	@Override
	public void execute() {
		PacketManager.getPacketManager().handle((Player) session.getAttribute("player"), message);
	}

}
