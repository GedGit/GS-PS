package org.rs2server.rs2.model.npc.impl.cerberus.ghosts.tickables;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.npc.impl.cerberus.Cerberus;
import org.rs2server.rs2.tickable.Tickable;

/**
 * @author Twelve
 */
public final class CerberusGhostRegisterTick extends Tickable {

	private final Cerberus cerberus;

	private boolean startTick;

	public CerberusGhostRegisterTick(Cerberus cerberus) {
		super(1);
		this.cerberus = cerberus;
	}

	@Override
	public void execute() {
		if (cerberus.getGhosts() == null || cerberus.getSkills().getLevel(Skills.HITPOINTS) > 400) {
			this.stop();
			return;
		}
		if (startTick) {
			World.getWorld().submit(new CerberusGhostCycleTick(cerberus));
			this.stop();
			return;
		}
		cerberus.getGhosts().forEach(g -> {
			if (!g.hasAttribute("startedWalking")) {
				Location targetLocation = g.getLocation().transform(0, -10, 0);
				g.getWalkingQueue().addStep(targetLocation.getX(), targetLocation.getY());
				g.setAttribute("startedWalking", targetLocation);
			} else {
				if (g.getLocation().equals(g.getAttribute("startedWalking"))) {
					startTick = true;
					g.removeAttribute("startedWalking");
				}
			}
		});
	}
}
