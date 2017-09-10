package org.rs2server.rs2.event.impl;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.GroundItemDefinition;
import org.rs2server.rs2.model.GroundItemController;

public class GroundItemEvent extends Event {

	public GroundItemEvent() {
		super(600);
	}

	@Override
	public void execute() {
		for (GroundItemDefinition g : GroundItemController.getGroundItems()) {
			// Decrease the timer for the ground item, so it loops
			// Through various stages and eventually, disappears.
			g.decreaseTime();

			/*
			 * Removes the ground item, if time is.
			 */
			if (g.getTime() == GroundItemController.DISAPPEAR) {
				GroundItemController.removeGroundItemForAll(g);
				return;
			}

			/*
			 * Makes sure that a player bound item doesn't appear for everyone..
			 */
			if (Constants.playerBoundItem(g.getId()) || !g.getDefinition().isTradable())
				continue;

			/*
			 * Makes the ground item appear for everyone, if the player is null
			 * (or an npc killed an npc).
			 */
			if (g.getOwner() == null && g.getTime() > GroundItemController.APPEAR_FOR_EVERYONE)
				g.setTime(GroundItemController.APPEAR_FOR_EVERYONE);
			/*
			 * Spawns the ground item for everyone, if time is.
			 */
			if (g.getTime() == GroundItemController.APPEAR_FOR_EVERYONE) {
				GroundItemController.spawnForEveryone(g);
				g.setGlobal(true);
			}

		}

	}

}
