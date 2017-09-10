package org.rs2server.rs2.model.minigame.impl;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.boundary.Boundary;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.minigame.Minigame;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

import java.util.List;


/**
 * Provides a skeletal implementation for <code>Minigame</code>s on which
 * other code should base their code off.
 * <p>
 * This implementation contains code common to ALL implementations.
 * @author Michael Bull
 *
 */

public class AbstractMinigame implements Minigame {
	
	/**
	 * Initializes the minigames instance.
	 */
	public void init() {
		if(getItemSafety() == ItemSafety.SAFE) {
			BoundaryManager.addBoundary(Boundary.create("SafeZone", getBoundary().getBottomLeft(), getBoundary().getTopRight())); //we only need to add safe zones as it would be default to lose items
		}
		BoundaryManager.addBoundary(getBoundary());
		if(getGameCycle() != null) {
			World.getWorld().submit(getGameCycle());
		}
	}

	@Override
	public void end() {
		for(Player participant : getParticipants()) {
			participant.setTeleportTarget(getStartLocation());
			participant.resetVariousInformation();
		}
		if(getGameCycle() != null) {
			getGameCycle().stop();
		}
	}

	@Override
	public void quit(Player player) {
		player.setMinigame(null);
		player.setAttribute("temporaryHeight", null);
		player.setTeleportTarget(getStartLocation());
		player.setLocation(getStartLocation());
		player.resetVariousInformation();
		if(getParticipants() != null) {
			getParticipants().remove(player);
			if(getParticipants().size() < 1) {
				end();
			}
		}
	}

	@Override
	public Boundary getBoundary() {
		return null;
	}

	@Override
	public ItemSafety getItemSafety() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public List<Player> getParticipants() {
		return null;
	}

	@Override
	public void start() {
	}

	@Override
	public Tickable getGameCycle() {
		return null;
	}

	@Override
	public Location getStartLocation() {
		return null;
	}

	@Override
	public boolean deathHook(Player player) {
		return false;
	}

	@Override
	public void movementHook(Player player) {
		if(!BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), getBoundary())) {
			quit(player);
		}
	}

	@Override
	public void killHook(Player player, Mob victim) {
	}

	@Override
	public boolean attackMobHook(Player player, Mob victim) {
		return true;
	}

}
