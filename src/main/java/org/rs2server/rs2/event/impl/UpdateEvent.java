package org.rs2server.rs2.event.impl;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.task.ConsecutiveTask;
import org.rs2server.rs2.task.ParallelTask;
import org.rs2server.rs2.task.Task;
import org.rs2server.rs2.task.impl.NPCResetTask;
import org.rs2server.rs2.task.impl.NPCTickTask;
import org.rs2server.rs2.task.impl.NPCUpdateTask;
import org.rs2server.rs2.task.impl.PlayerResetTask;
import org.rs2server.rs2.task.impl.PlayerTickTask;
import org.rs2server.rs2.task.impl.PlayerUpdateTask;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.tickable.impl.StoppingTick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An event which starts player update tasks.
 * 
 * @author Graham Edgecombe
 *
 */
public class UpdateEvent extends Event {

	private static final Logger logger = LoggerFactory.getLogger(UpdateEvent.class);

	/**
	 * The cycle time, in milliseconds.
	 */
	public static final int CYCLE_TIME = 600;

	/**
	 * Creates the update event to cycle every 600 milliseconds.
	 */
	public UpdateEvent() {
		super(CYCLE_TIME);
	}

	@Override
	public void execute() {
		Iterator<Tickable> tickIt$ = World.getWorld().getTickableManager().getTickables().iterator();
		while (tickIt$.hasNext()) {
			Tickable t = tickIt$.next();
			t.cycle();
			if (!t.isRunning()) {
				tickIt$.remove();
			}
		}

		List<Task> tickTasks = new ArrayList<Task>();
		List<Task> updateTasks = new ArrayList<Task>();
		List<Task> resetTasks = new ArrayList<Task>();

		for (NPC npc : World.getWorld().getNPCs()) {
			tickTasks.add(new NPCTickTask(npc));
			resetTasks.add(new NPCResetTask(npc));
		}

		Iterator<Player> it$ = World.getWorld().getPlayers().iterator();
		while (it$.hasNext()) {
			Player player = it$.next();

			// TODO perhaps find a better place for this.
			// If a session is closed remotely, MINA will close the session (and clear the
			// attribute map) automatically,
			// so we can't detect for which player the session was closed. Therefore, we
			// must check if the session
			// has become invalid and manually remove the player here.

			if (player.getSession() == null || player.getSession().isClosing()
					|| !player.getSession().containsAttribute("player") || !player.getSession().isConnected()
					|| player.getSession().getLastReadTime() <= (System.currentTimeMillis() - 30000)) {
				logger.info("Detected hanging session for player {}, removing from game world...", player.getName());
				if (player.getCombatState().getLastHitTimer() > System.currentTimeMillis()
						|| player.getCombatState().isDead() || player.getSkills().getLevel(3) <= 0) {
					World.getWorld().submit(new StoppingTick(16) {

						@Override
						public void executeAndStop() {
							World.getWorld().unregister(player);
							player.getSession().close(true);
						}
					});
				} else {
					World.getWorld().unregister(player);
					player.getSession().close(true);
				}
				it$.remove();
			} else if (player.isDestroyed()) {
				it$.remove();
			} else {
				tickTasks.add(new PlayerTickTask(player));
				updateTasks.add(new ConsecutiveTask(new PlayerUpdateTask(player), new NPCUpdateTask(player)));
				resetTasks.add(new PlayerResetTask(player));
			}
		}

		// ticks can no longer be parallel due to region code
		Task tickTask = new ConsecutiveTask(tickTasks.toArray(new Task[0]));
		Task updateTask = new ParallelTask(updateTasks.toArray(new Task[0]));
		Task resetTask = new ParallelTask(resetTasks.toArray(new Task[0]));

		engineService.offerTask(new ConsecutiveTask(tickTask, updateTask, resetTask));
	}

}
