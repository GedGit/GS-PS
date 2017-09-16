package org.rs2server.rs2.task.impl; 

import org.apache.mina.core.session.IoSession;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player; 
import org.rs2server.rs2.task.Task;
import org.rs2server.rs2.tickable.impl.StoppingTick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A task that is executed when a session is closed.
 * @author Graham Edgecombe
 *
 */
public class SessionClosedTask implements Task {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = LoggerFactory.getLogger(SessionClosedTask.class); 

	/**
	 * The session that closed.
	 */
	@SuppressWarnings("unused")
	private IoSession session;

	/**
	 * The player for whom the session is being closed.
	 */
	private Player player;

	/**
	 * Creates the session closed task.
	 * @param session The session.
	 * @param player The player for whom the session is being closed.
	 */
	public SessionClosedTask(IoSession session, Player player) {
		this.session = session;
		this.player = player;
	}

	@Override
	public void execute() {
		// If the player login was rejected (already logged in, full world etc) then we do not need to perform any removals.
		if (player.getIndex() <= 0) {
			return;
		}

		if(player.getCombatState().getLastHitTimer() > System.currentTimeMillis() || player.getCombatState().isDead()) {
			logger.info("Player {} is still in combat... delaying disconnect for 15 ticks.", player.getName());
			World.getWorld().submit(new StoppingTick(16) {
				@Override
				public void executeAndStop() {
					unregisterAndRemove(player);
				}
			});
		} else {
			unregisterAndRemove(player);
		}
	}

	private void unregisterAndRemove(final Player player) {
		World.getWorld().unregister(player);
	}

}
