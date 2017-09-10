package org.rs2server.rs2.model.map.path;

import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.map.Directions;
import org.rs2server.rs2.model.map.Directions.NormalDirection;
import org.rs2server.rs2.model.map.RegionClipping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 'Mystic Flow <Steven@rune-server.org>
 */
public class ProjectilePathFinder {

	public static boolean clippedProjectile(Mob entity, Mob victim) {
		Location start = entity.getLocation();
		Location end = victim.getLocation();
		Location currentTile = start;
		NormalDirection globalDirection = null;
		NormalDirection localDirection = null;
		NormalDirection localDirectionInverse = null;
		while (currentTile != end) {
			globalDirection = Directions.directionFor(currentTile, end);
			if (globalDirection == null) {
				break;
			}
			Location nextTile = currentTile.transform(Directions.DIRECTION_DELTA_X[globalDirection.intValue()],
					Directions.DIRECTION_DELTA_Y[globalDirection.intValue()], 0);
			localDirection = Directions.directionFor(currentTile, nextTile);
			localDirectionInverse = Directions.directionFor(nextTile, currentTile);
			GameObject currentObject = currentTile.getWall();
			GameObject nextObject = nextTile.getWall();
			if (currentObject != null && currentObject.getLocation().getPlane() == currentTile.getPlane()) {
				if (nextObject != null) {
					if (!PrimitivePathFinder.canMove(currentTile, localDirection, 1, false)
							|| !PrimitivePathFinder.canMove(nextTile, localDirectionInverse, 1, false))
						break;
				} else {
					if (!PrimitivePathFinder.canMove(currentTile, localDirection, 1, false)
							|| !PrimitivePathFinder.canMove(nextTile, localDirectionInverse, 1, false))
						break;
				}
			} else if (nextObject != null && nextObject.getLocation().getPlane() == nextTile.getPlane()) {
				if (!PrimitivePathFinder.canMove(currentTile, localDirection, 1, false)
						|| !PrimitivePathFinder.canMove(nextTile, localDirectionInverse, 1, false))
					break;
			}
			if (PrimitivePathFinder.canMove(currentTile, localDirection, 1, false)
					&& PrimitivePathFinder.canMove(currentTile, localDirectionInverse, 1, false)) {
				currentTile = nextTile;
				continue;
			} else {
				boolean solid = (RegionClipping.getClippingMask(nextTile.getX(), nextTile.getY(), nextTile.getPlane())
						& 0x20000) != 0;
				boolean solid2 = (RegionClipping.getClippingMask(currentTile.getX(), currentTile.getY(),
						currentTile.getPlane()) & 0x20000) != 0;
				if (!solid && !solid2) {
					currentTile = nextTile;
					continue;
				} else {
					break;
				}
			}
		}
		return currentTile.equals(end);
	}

	public static int projectileClip(Mob mob, Location victimLoc) {
		return projectileClip(mob, victimLoc, true);
	}

	public static int projectileClip(Mob mob, Location victimLoc, boolean walkPath) {
		if (mob == null || victimLoc == null) {
			return 0;
		}
		Location loc = mob.getLocation();
		List<Location> available = new ArrayList<Location>();
		for (int x = -15; x <= 15; x++) {
			for (int y = -15; y <= 15; y++) {
				loc = mob.getLocation().transform(x, y, 0);
				if (clearPath(loc, victimLoc)) {
					available.add(loc);
				}
			}
		}
		Location to = null;
		int leastDistance = -1;
		for (Location l : available) {
			if (leastDistance == -1 || l.distance(victimLoc) < leastDistance) {
				to = l;
				leastDistance = (int) l.distance(victimLoc);
			}
		}
		if (to == null) {
			return 0;
		}
		TilePath state = World.getWorld().doPath(new DefaultPathFinder(), mob, to.getX(), to.getY(), false, false);
		if (to != null && state != null && state.isRouteFound()) {
			if (clearPath(mob.getLocation(), victimLoc) && state.isRouteFound()) {
				return 1;
			}
			if (walkPath)
				World.getWorld().doPath(mob, state);
			return 2;
		} else {
			if (mob.isPlayer()) {
				mob.getActionSender().sendMessage("You can't reach that.");
			}
			if (to != null && walkPath) {
				World.getWorld().doPath(new DefaultPathFinder(), mob, to.getX(), to.getY());
			}
			return 0;
		}
	}

	public static final int SOLID_FLAG = 256;

	public static boolean clearPath(Location loc, Location to) {
		int height = loc.getPlane();
		if (height != to.getPlane())
			return false;
		int i = 0, i2 = 0;
		int startX = loc.getX();
		int startY = loc.getY();
		int endX = to.getX();
		int endY = to.getY();
		int diffX = endX - startX, diffY = endY - startY;
		int max = Math.max(Math.abs(diffX), Math.abs(diffY));
		for (int ii = 0; ii < max; ii++) {
			if (diffX < 0)
				diffX++;
			else if (diffX > 0)
				diffX--;
			if (diffY < 0)
				diffY++;
			else if (diffY > 0)
				diffY--;

			int currentX = (endX - diffX);
			int currentY = (endY - diffY);
			if (diffX < 0 && diffY < 0) {
				if ((RegionClipping.getClippingMask(currentX + i - 1, currentY + i2 - 1, height) & SOLID_FLAG) != 0
						|| (RegionClipping.getClippingMask(currentX + i - 1, currentY + i2, height) & SOLID_FLAG) != 0
						|| (RegionClipping.getClippingMask(currentX + i, currentY + i2 - 1, height)
								& SOLID_FLAG) != 0) {
					return false;
				}
			} else if (diffX > 0 && diffY > 0) {
				if ((RegionClipping.getClippingMask(currentX + i + 1, currentY + i2 + 1, height) & SOLID_FLAG) != 0
						|| (RegionClipping.getClippingMask(currentX + i + 1, currentY + i2, height) & SOLID_FLAG) != 0
						|| (RegionClipping.getClippingMask(currentX + i, currentY + i2 + 1, height)
								& SOLID_FLAG) != 0) {
					return false;
				}
			} else if (diffX < 0 && diffY > 0) {
				if ((RegionClipping.getClippingMask(currentX + i - 1, currentY + i2 + 1, height) & SOLID_FLAG) != 0
						|| (RegionClipping.getClippingMask(currentX + i - 1, currentY + i2, height) & SOLID_FLAG) != 0
						|| (RegionClipping.getClippingMask(currentX + i, currentY + i2 + 1, height)
								& SOLID_FLAG) != 0) {
					return false;
				}
			} else if (diffX > 0 && diffY < 0) {
				if ((RegionClipping.getClippingMask(currentX + i + 1, currentY + i2 - 1, height) & SOLID_FLAG) != 0
						|| (RegionClipping.getClippingMask(currentX + i + 1, currentY + i2, height) & SOLID_FLAG) != 0
						|| (RegionClipping.getClippingMask(currentX + i, currentY + i2 - 1, height)
								& SOLID_FLAG) != 0) {
					return false;
				}
			} else if (diffX > 0 && diffY == 0) {
				if ((RegionClipping.getClippingMask(currentX + i + 1, currentY + i2, height) & SOLID_FLAG) != 0) {
					return false;
				}
			} else if (diffX < 0 && diffY == 0) {
				if ((RegionClipping.getClippingMask(currentX + i - 1, currentY + i2, height) & SOLID_FLAG) != 0) {
					return false;
				}
			} else if (diffX == 0 && diffY > 0) {
				if ((RegionClipping.getClippingMask(currentX + i, currentY + i2 + 1, height) & SOLID_FLAG) != 0) {
					return false;
				}
			} else if (diffX == 0 && diffY < 0) {
				if ((RegionClipping.getClippingMask(currentX + i, currentY + i2 - 1, height) & SOLID_FLAG) != 0) {
					return false;
				}
			}
		}
		return true;
	}
}
