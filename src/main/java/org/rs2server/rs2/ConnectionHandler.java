package org.rs2server.rs2;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.EngineService;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.net.RS2CodecFactory;
import org.rs2server.rs2.task.impl.SessionClosedTask;
import org.rs2server.rs2.task.impl.SessionMessageTask;
import org.rs2server.rs2.task.impl.SessionOpenedTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>ConnectionHandler</code> processes incoming events from MINA,
 * submitting appropriate tasks to the <code>GameEngine</code>.
 *
 * @author Graham Edgecombe
 */
public class ConnectionHandler extends IoHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
	private final EngineService engineService;

	public ConnectionHandler() {
		this.engineService = Server.getInjector().getInstance(EngineService.class);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		if (Constants.DEBUG)
			logger.error("Exception occurred in connection handler for player " + session.getAttribute("player"),
					throwable);
		closeSession(session);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		engineService.offerTask(new SessionMessageTask(session, (Packet) message));
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		closeSession(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		if (Constants.DEBUG)
			logger.info("Session idled for player {} with status {}, closing...", session.getAttribute("player"),
					status.toString());
		Player player = (Player) session.getAttribute("player");
		if (player != null) {
			engineService.offerTask(new SessionClosedTask(session, player));
		} else {
			session.close(false);
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		session.setAttribute("remote", session.getRemoteAddress());
		session.getFilterChain().addFirst("protocol", new ProtocolCodecFilter(RS2CodecFactory.LOGIN));
		engineService.offerTask(new SessionOpenedTask(session));
	}

	private void closeSession(final IoSession session) {
		if (!session.isClosing())
			session.close(false);

		final Player player = (Player) session.getAttribute("player");
		if (player != null) {

			if (Constants.DEBUG)
				logger.info("Closing session for player {}", player);

			engineService.offerTask(new SessionClosedTask(session, player));

			// This ensures only a single session closed task will be processed at one
			// single time.
			session.setAttribute("player", null);
		} else {
			for (Player p : World.getWorld().getPlayers()) {
				if (p.getSession() == session) {
					if (Constants.DEBUG)
						logger.info("Session closed via fallback.");
					engineService.offerTask(new SessionClosedTask(session, p));
					break;
				}
			}
		}
	}
}
