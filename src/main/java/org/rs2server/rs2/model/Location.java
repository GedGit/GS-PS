package org.rs2server.rs2.model;

import org.rs2server.rs2.model.map.Directions;
import org.rs2server.rs2.model.map.Directions.NormalDirection;
import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single location in the game world.
 *
 * @author Graham Edgecombe
 */
public class Location {

	/**
	 * The x coordinate.
	 */
	private final int x;

	/**
	 * The y coordinate.
	 */
	private final int y;

	/**
	 * The z coordinate.
	 */
	private final int z;

	/**
	 * Creates a location.
	 *
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @param z
	 *            The z coordinate.
	 * @return The location.
	 */
	public static Location create(int x, int y, int z) {
		return new Location(x, y, z);
	}

	public static Location create(final Vector2 vector) {
		return new Location((int) vector.getX(), (int) vector.getY(), 0);
	}

	/**
	 * Creates a location.
	 *
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @param z
	 *            The z coordinate.
	 */
	private Location(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Gets the absolute x coordinate.
	 *
	 * @return The absolute x coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the absolute y coordinate.
	 *
	 * @return The absolute y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the z coordinate, or height.
	 *
	 * @return The z coordinate.
	 */
	public int getPlane() {
		return z;
	}

	/**
	 * Gets the local x coordinate relative to this region.
	 *
	 * @return The local x coordinate relative to this region.
	 */
	public int getLocalX() {
		return getLocalX(this);
	}

	/**
	 * Gets the local y coordinate relative to this region.
	 *
	 * @return The local y coordinate relative to this region.
	 */
	public int getLocalY() {
		return getLocalY(this);
	}

	/**
	 * Gets the local x coordinate relative to a specific region.
	 *
	 * @param l
	 *            The region the coordinate will be relative to.
	 * @return The local x coordinate.
	 */
	public int getLocalX(Location l) {
		return x - 8 * (l.getRegionX() - 6);
	}

	/**
	 * Gets the local y coordinate relative to a specific region.
	 *
	 * @param l
	 *            The region the coordinate will be relative to.
	 * @return The local y coordinate.
	 */
	public int getLocalY(Location l) {
		return y - 8 * (l.getRegionY() - 6);
	}

	/**
	 * Gets the region x coordinate.
	 *
	 * @return The region x coordinate.
	 */
	public int getRegionX() {
		return (x >> 3);
	}

	/**
	 * Gets the region y coordinate.
	 *
	 * @return The region y coordinate.
	 */
	public int getRegionY() {
		return (y >> 3);
	}

	/**
	 * Returns a new vector with the absolute coordinates of this location.
	 *
	 * @return A new vector.
	 */
	public Vector2 asVector() {
		return Vector2.of(getX(), getY());
	}

	/**
	 * Checks if this location is within range of another.
	 *
	 * @param other
	 *            The other location.
	 * @return <code>true</code> if the location is in range, <code>false</code>
	 *         if not.
	 */
	public boolean isWithinDistance(Location other) {
		if (z != other.z) {
			return false;
		}
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
	}

	public boolean withinGodwarsDistance(Location other) {
		if (z != other.z)
			return false;
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 55 && deltaX >= -56 && deltaY <= 55 && deltaY >= -56;
	}

	/**
	 * Checks if this location is within interaction range of another.
	 *
	 * @param other
	 *            The other location.
	 * @return <code>true</code> if the location is in range, <code>false</code>
	 *         if not.
	 */
	public boolean isWithinInteractionDistance(Location other) {
		if (z != other.z)
			return false;
		int deltaX = Math.abs(other.x - x), deltaY = Math.abs(other.y - y);
		return deltaX <= 2 && deltaX >= -3 && deltaY <= 2 && deltaY >= -3;
	}

	/**
	 * Checks if this location is next to another.
	 *
	 * @param other
	 *            The other location.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isNextTo(Location other) {
		if (z != other.z)
			return false;
		return (getX() == other.getX() && getY() != other.getY() || getX() != other.getX() && getY() == other.getY()
				|| getX() == other.getX() && getY() == other.getY());
	}

	/**
	 * Checks if a coordinate is within range of another.
	 *
	 * @return <code>true</code> if the location is in range, <code>false</code>
	 *         if not.
	 */
	public boolean isWithinDistance(Entity attacker, Entity victim, int distance) {
		if (attacker.getWidth() == 1 && attacker.getHeight() == 1 && victim.getWidth() == 1 && victim.getHeight() == 1
				&& distance == 1) {
			return distanceToPoint(victim.getLocation()) <= distance;
		}
		List<Location> myTiles = entityTiles(attacker);
		List<Location> theirTiles = entityTiles(victim);
		for (Location myTile : myTiles) {
			for (Location theirTile : theirTiles) {
				if (myTile.isWithinDistance(theirTile, distance))
					return true;
			}
		}
		return false;
	}

	/**
	 * @param l
	 *            The entity's location
	 * @return The entity's current wilderness level.
	 */
	public static int getWildernessLevel(Player player, Location l) {
		if (player.isInWilderness()) {
			int level = 1 + (l.getY() - 3520) / 8;
			if (level > 800)
				level -= 800;
			return level;
		}
		return 0;
	}

	/**
	 * Checks if a coordinate is within range of another.
	 *
	 * @return <code>true</code> if the location is in range, <code>false</code>
	 *         if not.
	 */
	public int distanceToEntity(Entity attacker, Entity victim) {
		if (attacker.getWidth() == 1 && attacker.getHeight() == 1 && victim.getWidth() == 1
				&& victim.getHeight() == 1)
			return distanceToPoint(victim.getLocation());
		int lowestDistance = 100;
		List<Location> myTiles = entityTiles(attacker);
		List<Location> theirTiles = entityTiles(victim);
		for (Location myTile : myTiles) {
			for (Location theirTile : theirTiles) {
				int dist = myTile.distanceToPoint(theirTile);
				if (dist <= lowestDistance)
					lowestDistance = dist;
			}
		}
		return lowestDistance;
	}

	/**
	 * The list of tiles this entity occupies.
	 *
	 * @param entity
	 *            The entity.
	 * @return The list of tiles this entity occupies.
	 */
	public List<Location> entityTiles(Entity entity) {
		List<Location> myTiles = new ArrayList<Location>();
		myTiles.add(entity.getLocation());
		if (entity.getWidth() > 1) {
			for (int i = 1; i < entity.getWidth(); i++) {
				myTiles.add(Location.create(entity.getLocation().getX() + i, entity.getLocation().getY(),
						entity.getLocation().getPlane()));
			}
		}
		if (entity.getHeight() > 1) {
			for (int i = 1; i < entity.getHeight(); i++) {
				myTiles.add(Location.create(entity.getLocation().getX(), entity.getLocation().getY() + i,
						entity.getLocation().getPlane()));
			}
		}
		int myHighestVal = (entity.getWidth() > entity.getHeight() ? entity.getWidth() : entity.getHeight());
		if (myHighestVal > 1) {
			for (int i = 1; i < myHighestVal; i++) {
				myTiles.add(Location.create(entity.getLocation().getX() + i, entity.getLocation().getY() + i,
						entity.getLocation().getPlane()));
			}
		}
		return myTiles;
	}

	/**
	 * Checks if a coordinate is within range of another.
	 *
	 * @return <code>true</code> if the location is in range, <code>false</code>
	 *         if not.
	 */
	public boolean isWithinDistance(int width, int height, Location otherLocation, int otherWidth, int otherHeight,
			int distance) {
		Location myClosestTile = this.closestTileOf(otherLocation, width, height);
		Location theirClosestTile = otherLocation.closestTileOf(this, otherWidth, otherHeight);

		return myClosestTile.distanceToPoint(theirClosestTile) <= distance;
	}

	/**
	 * Checks if a coordinate is within range of another.
	 *
	 * @return <code>true</code> if the location is in range, <code>false</code>
	 *         if not.
	 */
	public boolean isWithinDistance(Location location, int distance) {
		int objectX = location.getX();
		int objectY = location.getY();
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((objectX + i) == x && ((objectY + j) == y || (objectY - j) == y || objectY == y)) {
					return true;
				} else if ((objectX - i) == x && ((objectY + j) == y || (objectY - j) == y || objectY == y)) {
					return true;
				} else if (objectX == x && ((objectY + j) == y || (objectY - j) == y || objectY == y)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the closest tile of this location from a specific point.
	 */
	public Location closestTileOf(Location from, int width, int height) {
		if (width < 2 && height < 2) {
			return this;
		}
		Location location = null;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Location loc = Location.create(this.x + x, this.y + y, this.z);
				if (location == null || loc.distanceToPoint(from) < location.distanceToPoint(from)) {
					location = loc;
				}
			}
		}
		return location;
	}

	/**
	 * Gets the closest unblocked tile from a position, or if there is none it
	 * just returns the original position.
	 */
	public Location closestFreeTileOrSelf(Location from, int width, int height) {
		for (int x = -width; x <= width; x++) {
			for (int y = -height; y <= height; y++) {
				Location loc = Location.create(this.x + x, this.y + y, this.z);
				if (!RegionClipping.isPassable(loc.getX(), loc.getY(), loc.getPlane())) {
					continue;
				}
				return loc;
			}
		}
		return from;
	}

	/**
	 * Gets the tile that is opposite to where the player is standing.
	 */
	public Location oppositeTileOfEntity(Entity entity) {
		if (entity.getWidth() < 2 && entity.getHeight() < 2) {
			return entity.getLocation();
		}
		int newX = x;
		if (x < entity.getLocation().getX()) {
			newX += 1 + entity.getWidth();
		} else if (x > entity.getLocation().getX()) {
			newX -= 1 + entity.getWidth();
		}
		int newY = y;
		if (y < entity.getLocation().getY()) {
			newY += 1 + entity.getHeight();
		} else if (y > entity.getLocation().getY()) {
			newY -= 1 + entity.getHeight();
		}
		return Location.create(newX, newY, z);
	}

	/**
	 * Gets the distance to a location.
	 *
	 * @param other
	 *            The location.
	 * @return The distance from the other location.
	 */
	public int distanceToPoint(Location other) {
		int absX = x;
		int absY = y;
		int pointX = other.getX();
		int pointY = other.getY();
		return (int) Math.sqrt(Math.pow(absX - pointX, 2) + Math.pow(absY - pointY, 2));
	}

	@Override
	public int hashCode() {
		return z << 30 | x << 15 | y;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Location)) {
			return false;
		}
		Location loc = (Location) other;
		return loc.x == x && loc.y == y && loc.z == z;
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "," + z + "]";
	}

	/**
	 * Creates a new location based on this location.
	 *
	 * @param diffX
	 *            X difference.
	 * @param diffY
	 *            Y difference.
	 * @param diffZ
	 *            Z difference.
	 * @return The new location.
	 */
	public Location transform(int diffX, int diffY, int diffZ) {
		return Location.create(x + diffX, y + diffY, z + diffZ);
	}

	/**
	 * Creates a new location based on this location.
	 *
	 * @param vector
	 *            The vector to transform by.
	 * @return The new location.
	 */
	public Location transform(final Vector2 vector) {
		return Location.create(x + (int) vector.getX(), y + (int) vector.getY(), z);
	}

	public Location getLocation(Directions.NormalDirection direction) {
		switch (direction) {
		case SOUTH:
			return Location.create(x, y - 1, z);
		case WEST:
			return Location.create(x - 1, y, z);
		case NORTH:
			return Location.create(x, y + 1, z);
		case EAST:
			return Location.create(x + 1, y, z);
		case SOUTH_WEST:
			return Location.create(x - 1, y - 1, z);
		case NORTH_WEST:
			return Location.create(x - 1, y + 1, z);
		case SOUTH_EAST:
			return Location.create(x + 1, y - 1, z);
		case NORTH_EAST:
			return Location.create(x + 1, y + 1, z);
		default:
			return null;
		}
	}

	public NormalDirection direction(Location next) {
		return Directions.directionFor(this, next);
	}

	/**
	 * Creates a height 0 location.
	 *
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @return The location.
	 */
	public static Location create(int x, int y) {
		return new Location(x, y, 0);
	}

	/**
	 * Calculate the distance between a player and a point.
	 *
	 * @return The square distance.
	 */
	public double getDistance(Location other) {
		int xdiff = this.getX() - other.getX();
		int ydiff = this.getY() - other.getY();
		return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
	}

	/**
	 * Gets the closest tile of an entity to this location.
	 */
	public Location closestTileToMob(Mob mob) {
		int size = 1;
		if (mob.isNPC()) {
			size = ((NPC) mob).getDefinition().getSize();
		}
		if (size < 2 && size < 2) {// ? i didnt add that lol it might be a bug
									// thats causing this no were talking bout
									// objects ye and objects call these
									// methods...
			return mob.getLocation();
		}
		Location location = null;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				Location loc = Location.create(mob.getLocation().getX() + x, mob.getLocation().getY() + y,
						mob.getLocation().getPlane());
				if (loc.isNextTo(this) && (location == null || !location.isNextTo(this))) {
					location = loc;
				} else if (location == null || loc.distanceToPoint(this) < location.distanceToPoint(this)) {
					location = loc;
				}
			}
		}
		return location;
	}

	public Location getDelta(Location location) {
		return transform(getX() - location.getX(), getY() - location.getY(), 0);
	}

	public double distance(Location other) {
		if (z != other.z)
			return Double.MAX_VALUE - 1;
		return distanceFormula(x, y, other.x, other.y);
	}

	public double distance(int x, int y, int z) {
		if (this.z != z) {
			return Double.MAX_VALUE - 1;
		}
		return distanceFormula(this.x, this.y, x, y);
	}

	public static double distanceFormula(int x, int y, int x2, int y2) {
		return Math.sqrt(Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2));
	}

	public GameObject getWall() {
		for (GameObject obj : World.getWorld().getRegionManager().getRegionByLocation(this).getGameObjects()) {
			if (obj.getLocation().equals(this) && obj.getType() >= 0 && obj.getType() <= 4)
				return obj;
		}
		return null;
	}

	public GameObject getGameObjectType(int type) {
		for (GameObject obj : World.getWorld().getRegionManager().getRegionByLocation(this).getGameObjects()) {
			if (obj.getLocation().equals(this) && obj.getType() == type)
				return obj;
		}
		return null;
	}

	public static Location[] getValidSpots(int size, Location location) {
		Location[] list = new Location[size * 4];
		int index = 0;
		for (int i = 0; i < size; i++) {
			list[index++] = (new Location(location.getX() - 1, location.getY() + i, location.getPlane()));
			list[index++] = (new Location(location.getX() + i, location.getY() - 1, location.getPlane()));
			list[index++] = (new Location(location.getX() + i, location.getY() + size, location.getPlane()));
			list[index++] = (new Location(location.getX() + size, location.getY() + i, location.getPlane()));
		}
		return list;
	}

	public static boolean inWild(Location location) {
		return ((location.getX() >= 2941 && location.getX() <= 3392 && location.getY() >= 3525
				&& location.getY() <= 3967)
				|| (location.getX() >= 2941 && location.getX() <= 3392 && location.getY() >= 9918
						&& location.getY() <= 10366));
	}

	public static Location getClosestSpot(Location target, Location[] steps) {
		Location closestStep = null;
		for (Location p : steps) {
			if (closestStep == null || (closestStep.distance(target) > p.distance(target)))
				closestStep = p;
		}
		return closestStep;
	}

	public boolean withinRange(Location t, int distance) {
		return t.z == z && distance(t) <= distance;
	}

	public static boolean isInArea(int localX, int localY, int height, int minX, int minY, int plane, int maxX,
			int maxY, int plane2) {
		// TODO Auto-generated method stub
		return false;
	}
}
