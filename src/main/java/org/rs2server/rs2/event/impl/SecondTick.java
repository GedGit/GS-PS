package org.rs2server.rs2.event.impl;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;

// WHO THE FUCK MADE THIS UGLY ASS CRAP ????
// TODO make these based on system.currenttimemillis, removed the issue of using more cpu than needed - Vichy
public class SecondTick extends Event {

	public SecondTick() {
		super(1000);
	}

	@Override
	public void execute() {
		for (Player p : World.getWorld().getPlayers())
			if (p != null)
				timers(p);
	}

	public void timers(Player p) {
		if (p.getBountyHunter() != null)
			p.getBountyHunter().tick();
		if (p.bountyDelay > 0)
			p.bountyDelay--;
		if (p.dfsWait > 0)
			p.dfsWait--;
	}
}