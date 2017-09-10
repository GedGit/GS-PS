package org.rs2server.rs2.model.region;

import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Manages the world regions.
 * 
 * @author Graham Edgecombe
 *
 */
public class RegionManager {

	/**
	 * The region size.
	 */
	public static final int REGION_SIZE = 32;

	/**
	 * The lower bound that splits the region in half.
	 */
	@SuppressWarnings("unused")
	private static final int LOWER_BOUND = REGION_SIZE / 2 - 1;

	/**
	 * The active (loaded) region map.
	 */
	private Map<RegionCoordinates, Region> activeRegions = new HashMap<RegionCoordinates, Region>();

	/**
	 * Gets the local players around an entity.
	 * 
	 * @param mob
	 *            The entity.
	 * @return The collection of local players.
	 */
	public Collection<Player> getLocalPlayers(Mob mob) {
		List<Player> localPlayers = new LinkedList<Player>();
		Region[] regions = getSurroundingRegions(mob.getLocation());
		for (Region region : regions) {
			for (Player player : region.getPlayers()) {
				if (player.getLocation().isWithinDistance(mob.getLocation()))
					localPlayers.add(player);
			}
		}
		return Collections.unmodifiableCollection(localPlayers);
	}

	/**
	 * Gets the local NPCs around an entity.
	 * 
	 * @param mob
	 *            The entity.
	 * @return The collection of local NPCs.
	 */
	public Collection<NPC> getLocalNpcs(Mob mob) {
		List<NPC> localNpcs = new LinkedList<NPC>();
		Region[] regions = getSurroundingRegions(mob.getLocation());
		for (Region region : regions) {
			for (NPC npc : region.getNpcs()) {
				if (npc.getLocation().isWithinDistance(mob.getLocation()))
					localNpcs.add(npc);
			}
		}
		return Collections.unmodifiableCollection(localNpcs);
	}

	/**
	 * Gets the local mobs around an entity.
	 * 
	 * @param mob
	 *            The entity.
	 * @return The collection of local mobs.
	 */
	public Collection<Mob> getLocalMobs(Mob mob) {
		List<Mob> localMobs = new LinkedList<Mob>();
		Region[] regions = getSurroundingRegions(mob.getLocation());
		for (Region region : regions) {
			for (Mob mobs : region.getMobs()) {
				if (mobs.getLocation().isWithinDistance(mob.getLocation()))
					localMobs.add(mobs);
			}
		}
		return Collections.unmodifiableCollection(localMobs);
	}

	/**
	 * Gets a local game object.
	 * 
	 * @param location
	 *            The object's location.
	 * @param id
	 *            The object's id.
	 * @return The <code>GameObject</code> or <code>null</code> if no game
	 *         object was found to be existent.
	 */
	public GameObject getGameObject(Location location, int id) {
		Region[] regions = getSurroundingRegions(location);
		for (Region region : regions) {
			for (GameObject object : region.getGameObjects()) {
				if (object.getLocation().equals(location) && object.getDefinition().getId() == id)
					return object;
			}
		}
		return null;
	}

	public GameObject getGameObject(Location location, Predicate<GameObject> predicate) {
		Region[] regions = getSurroundingRegions(location);
		for (Region region : regions) {
			for (GameObject object : region.getGameObjects()) {
				if (object.getLocation().equals(location) && predicate.test(object))
					return object;
			}
		}
		return null;
	}

	/**
	 * Gets the regions surrounding a location.
	 * 
	 * @param location
	 *            The location.
	 * @return The regions surrounding the location.
	 */
	public Region[] getSurroundingRegions(Location location) {
		int regionX = location.getX() / REGION_SIZE;
		int regionY = location.getY() / REGION_SIZE;

		Region[] surrounding = new Region[9];
		surrounding[0] = getRegion(regionX, regionY);
		surrounding[1] = getRegion(regionX - 1, regionY - 1);
		surrounding[2] = getRegion(regionX + 1, regionY + 1);
		surrounding[3] = getRegion(regionX - 1, regionY);
		surrounding[4] = getRegion(regionX, regionY - 1);
		surrounding[5] = getRegion(regionX + 1, regionY);
		surrounding[6] = getRegion(regionX, regionY + 1);
		surrounding[7] = getRegion(regionX - 1, regionY + 1);
		surrounding[8] = getRegion(regionX + 1, regionY - 1);
		return surrounding;
	}

	/**
	 * Gets a region by location.
	 * 
	 * @param location
	 *            The location.
	 * @return The region.
	 */
	public Region getRegionByLocation(Location location) {
		return getRegion(location.getX() / REGION_SIZE, location.getY() / REGION_SIZE);
	}

	/**
	 * Gets a region by its x and y coordinates.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @return The region.
	 */
	public Region getRegion(int x, int y) {
		RegionCoordinates key = new RegionCoordinates(x, y);
		if (activeRegions.containsKey(key))
			return activeRegions.get(key);
		else {
			Region region = new Region(key);
			activeRegions.put(key, region);
			return region;
		}
	}

}
