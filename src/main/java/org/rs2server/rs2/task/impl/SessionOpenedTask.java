package org.rs2server.rs2.task.impl;

import org.apache.mina.core.session.IoSession;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.task.Task;

import java.util.logging.Logger;

/**
 * A task that is executed when a session is opened.
 * 
 * @author Graham Edgecombe
 *
 */
public class SessionOpenedTask implements Task {

	/**
	 * The logger class.
	 */
	private static final Logger logger = Logger.getLogger(SessionOpenedTask.class.getName());

	/**
	 * The session.
	 */
	private IoSession session;

	/**
	 * Creates the session opened task.
	 * 
	 * @param session
	 *            The session.
	 */
	public SessionOpenedTask(IoSession session) {
		this.session = session;
	}

	@Override
	public void execute() {
		if (Constants.DEBUG)
			logger.fine("Session opened : " + session.getRemoteAddress());
	}
}