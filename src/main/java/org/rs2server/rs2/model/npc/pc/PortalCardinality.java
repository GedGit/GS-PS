package org.rs2server.rs2.model.npc.pc;

import org.rs2server.rs2.model.Location;

/**
 * @author twelve
 */
public enum PortalCardinality {
	BLUE("eastern", 15, 20, Location.create(2680, 2588, 0), new Location[] {
			Location.create(2644, 2571, 0),
			Location.create(2644, 2572, 0),
			Location.create(2645, 2572, 0),
			Location.create(2646, 2572, 0),
			Location.create(2647, 2572, 0),
			Location.create(2648, 2572, 0),
			Location.create(2648, 2571, 0)
	}),
	RED("south-western", 17, 24, Location.create(2645, 2569, 0),  new Location[] {
			Location.create(2680, 2587, 0),
			Location.create(2679, 2587, 0),
			Location.create(2679, 2588, 0),
			Location.create(2679, 2589, 0),
			Location.create(2679, 2590, 0),
			Location.create(2679, 2591, 0),
			Location.create(2680, 2591, 0)
	}),
	YELLOW("south-eastern", 16, 22, Location.create(2669, 2570, 0), new Location[] {
			Location.create(2668, 2572, 0),
			Location.create(2668, 2573, 0),
			Location.create(2669, 2573, 0),
			Location.create(2670, 2573, 0),
			Location.create(2671, 2573, 0),
			Location.create(2672, 2573, 0),
			Location.create(2672, 2572, 0)
	}),
	PURPLE("western", 14, 18, Location.create(2628, 2591, 0), new Location[]{
			Location.create(2630, 2594, 0),
			Location.create(2631, 2594, 0),
			Location.create(2631, 2593, 0),
			Location.create(2631, 2592, 0),
			Location.create(2631, 2591, 0),
			Location.create(2631, 2590, 0),
			Location.create(2630, 2590, 0)});

	private final String direction;
	private final int healthChild;
	private final int shieldChild;
	private final Location location;
	private final Location[] spawnLocations;

	PortalCardinality(String direction, int healthChild, int shieldChild, Location location, Location[] spawnLocations) {
		this.direction = direction;
		this.healthChild = healthChild;
		this.shieldChild = shieldChild;
		this.location = location;
		this.spawnLocations = spawnLocations;
	}

	public final String getDirection() {
		return this.direction;
	}

	public String getName() {
		return this.name().toLowerCase();
	}

	public int getHealthChild() {
		return healthChild;
	}

	public int getShieldChild() {
		return shieldChild;
	}

	public Location getLocation() {
		return location;
	}

	public Location[] getSpawnLocations() {
		return spawnLocations;
	}
}
