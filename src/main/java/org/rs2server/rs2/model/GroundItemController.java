package org.rs2server.rs2.model;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.region.Region;

import java.util.ArrayList;

/**
 * Controls all GroundItems world-wide.
 *
 * @author Martin
 * @author Brown
 */
public class GroundItemController {

	/**
	 * This ArrayList contains all ground items.
	 */
	private static final ArrayList<GroundItemDefinition> items = new ArrayList<GroundItemDefinition>();

	/**
	 * Gets the ArrayList that contains all ground items.
	 *
	 * @return The ArrayList that contains all ground items.
	 */
	public static ArrayList<GroundItemDefinition> getGroundItems() {
		return items;
	}

	/**
	 * Spawns a ground item for everyone to see.
	 *
	 * @param g
	 *            The ground item object to spawn.
	 */
	public static void spawnForEveryone(GroundItemDefinition g) {
		if (g.getDefinition().isTradable()) {
			for (final Region r : g.getRegions()) {
				for (final Player player : r.getPlayers()) {
					if (player.getName().equals(g.getOwner()) || player.isMultiplayerDisabled())
						continue;

					player.getActionSender().sendGroundItem2(g);
				}
			}
		}
	}

	/**
	 * Remove a ground item for all players.
	 *
	 * @param g
	 *            The ground item object to remove.
	 */
	public static void removeGroundItemForAll(GroundItemDefinition g) {
		synchronized (items) {
			// Remove the GroundItem from the ArrayList.
			if (items.remove(g)) {
				for (final Region r : g.getRegions()) {
					for (final Player player : r.getPlayers()) {
						player.getActionSender().removeGroundItem2(g);
					}
				}
			}
		}
	}

	/*
	 * Constants, the stages of the ground item. Total span: 4 minutes.
	 */
	public static final int APPEAR_FOR_EVERYONE = 120;
	public static final int DISAPPEAR = 0;
}