package org.rs2server.rs2.task.impl;

import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.task.Task;

/**
 * A task which resets an NPC after an update cycle.
 * @author Graham Edgecombe
 *
 */
public class NPCResetTask implements Task {

	/**
	 * The npc to reset.
	 */
	private NPC npc;
	
	/**
	 * Creates the reset task.
	 * @param npc The npc to reset.
	 */
	public NPCResetTask(NPC npc) {
		this.npc = npc;
	}

	@Override
	public void execute() {
		npc.resetHits();
		npc.getUpdateFlags().reset();
		npc.setTeleporting(false);
		npc.reset();
	}

}
