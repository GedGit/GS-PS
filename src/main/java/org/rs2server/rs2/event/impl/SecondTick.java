package org.rs2server.rs2.event.impl;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;

public class SecondTick extends Event {

	public SecondTick() {
		super(1000);
	}

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
		if (p.barrelWait > 0)
			p.barrelWait--;
		if (p.dfsWait > 0)
			p.dfsWait--;
	}
}