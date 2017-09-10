package org.rs2server.rs2.content;

import org.rs2server.rs2.model.Entity;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.map.path.DefaultPathFinder;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.region.Region;

import java.util.ArrayList;
import java.util.List;

public class NPCFollow {

	public static boolean handleCombatFollowing(Mob n, Mob target, int attackingDistance) {
		// System.out.println("Handle combat following.." + npc);
		if (target == null)
			return false;
		NPC npc = (NPC) n;
		if (!npc.getCombatState().canMove())
			return true; // We're still in combat I guess?
		final double dist = target.getLocation().getDistance(npc.getLocation());
		if (dist <= 1 + npc.getDefinition().getSize() + attackingDistance && !npc.getWalkingQueue().isEmpty())
			return true;
		Location closest = Location.getClosestSpot(npc.getLocation(), Location.getValidSpots(1, target.getLocation()));

		return walkTowards(npc, target, closest == null ? target.getLocation() : closest);
	}

	public static boolean walkTowards(NPC npc, Mob other, Location target) {
		/*
		 * Check if we're already standing at our target.
		 */
		if (npc.getLocation().equals(target))
			return true;

		/*
		 * We get the closest of our own surrounding coordinates, to the target.
		 */
		Location[] validSpots = getValidSpots(npc.getSize(), npc.getLocation());
		List<Location> ignore = new ArrayList<Location>();
		Location closest = null;
		for (int i = 0; i < 3/* Three closest spots.. */; i++) {
			closest = getClosestSpot(target, validSpots, ignore);
			ignore.add(closest);
			if (closest != null) {
				Region r = World.getWorld().getRegionManager().getRegionByLocation(closest);
				boolean clip = false;
				/*
				 * We make sure the next step is clear of other NPC's / Players.
				 */
				for (Entity e : r.getMobs()) {
					if (e.getLocation().getPlane() == closest.getPlane()) {
						for (int xOffset1 = 1; xOffset1 < npc.getSize(); xOffset1++) {
							for (int yOffset1 = 1; yOffset1 < npc.getSize(); yOffset1++) {
								for (int xOffset2 = 1; xOffset2 < 1; xOffset2++) {
									for (int yOffset2 = 1; yOffset2 < 1; yOffset2++) {
										if (e.getLocation().getX() + xOffset2 == closest.getX() + xOffset1
												&& e.getLocation().getY() + yOffset2 == closest.getY() + yOffset1) {
											clip = true;
											break;
										}
									}
								}
							}
						}

					}
				}
				if (!clip) {
					/*
					 * Then we check if there is an object in the road..
					 */
					for (GameObject o : r.getGameObjects()) {
						if (o.getType() != 22) {
							/*
							 * This works for all NPCs with the size of one.. (Basicly checks the actual
							 * coordinate of the NPC)
							 */
							if (o.getLocation().equals(closest)) {
								// System.out.println("Attempting to clip: " + npc + " " + o);
								clip = true;
								break;
							}
							/*
							 * If the size is bigger than one (TzTokJad, Hill Giants etc) we do a few nasty
							 * for loops..
							 */
							if (o.getLocation().getPlane() == npc.getLocation().getPlane()) {
								for (int xOffset = 1; xOffset < npc.getSize(); xOffset++) {
									for (int yOffset = 1; yOffset < npc.getSize(); yOffset++) {
										if (o.getLocation().getX() == closest.getX() + xOffset
												&& o.getLocation().getY() == closest.getY() + yOffset) {
											// System.out.println("Attempting to clip: " + npc + " " + o);
											clip = true;
											break;
										}
									}
								}
							}
						}
					}
				}
				if (clip) {
					closest = null;// Makes sure we aren't walking towards it..
				} else {
					break; // We have the closest, walkable target and we break the loop.
				}
			}
		}
		/*
		 * Which is the coordinate we want to walk towards the next time.
		 */
		if (closest != null && closest.getDistance(target) < npc.getLocation().getDistance(target))
			World.getWorld().doPath(new DefaultPathFinder(), npc, closest.getX(), closest.getY());
		return true;
	}

	/**
	 * Gets the closest spot from a list of locations.
	 * 
	 * @param steps
	 *            The list of steps.
	 * @param location
	 *            The location we want to be close to.
	 * @return The closest location.
	 */
	public static Location getClosestSpot(Location target, Location[] steps, List<Location> ignore) {
		Location closestStep = null;
		for (Location p : steps) {
			if (closestStep == null || (closestStep.distance(target) > p.distance(target)) && !ignore.contains(p))
				closestStep = p;
		}
		return closestStep;
	}

	/**
	 * Gets a list of all the valid spots around another location, within a specific
	 * "size/range".
	 * 
	 * @param size
	 *            The size/range.
	 * @param location
	 *            The location we want to get locations within range from.
	 */
	public static Location[] getValidSpots(int size, Location location) {
		Location[] list = new Location[(size * 4) + 4];
		int index = 0;
		list[index++] = (Location.create(location.getX() - 1, location.getY() - 1, location.getPlane()));
		list[index++] = (Location.create(location.getX() + size, location.getY() - 1, location.getPlane()));
		list[index++] = (Location.create(location.getX() + size, location.getY() + size, location.getPlane()));
		list[index++] = (Location.create(location.getX() - 1, location.getY() + size, location.getPlane()));
		for (int i = 0; i < size; i++) {
			list[index++] = (Location.create(location.getX() - 1, location.getY() + i, location.getPlane()));
			list[index++] = (Location.create(location.getX() + i, location.getY() - 1, location.getPlane()));
			list[index++] = (Location.create(location.getX() + i, location.getY() + size, location.getPlane()));
			list[index++] = (Location.create(location.getX() + size, location.getY() + i, location.getPlane()));
		}
		return list;
	}

}
