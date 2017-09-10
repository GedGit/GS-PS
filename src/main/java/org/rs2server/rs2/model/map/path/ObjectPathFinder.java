package org.rs2server.rs2.model.map.path;

import org.rs2server.cache.format.CacheObjectDefinition;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.map.BasicPoint;
import org.rs2server.rs2.model.map.RegionClipping;

import java.util.List;

public class ObjectPathFinder implements PathFinder {

	private int z;
	private int[] queueX = new int[4096];
	private int[] queueY = new int[4096];
	private int[][] via = new int[104][104];
	private int[][] cost = new int[104][104];
	private int writePathPosition = 0;
	private GameObject obj;
	private int width;
	private int length;
	private int orientation;
	private int type;

	public ObjectPathFinder(GameObject obj, int movementType, int width, int length, int orientation, int type) {
		this.obj = obj;
		this.width = width;
		this.length = length;
		this.orientation = orientation;
		this.type = type;
	}

	public void check(Mob mob, int x, int y, int viaDir, int thisCost) {
		queueX[writePathPosition] = x;
		queueY[writePathPosition] = y;
		via[x][y] = viaDir;
		cost[x][y] = thisCost;
		writePathPosition = writePathPosition + 1 & 0xfff;
	}

	public double distanceTo(int x, int y, int ox, int oy) {
		double dx = x - ox;
		double dy = y - oy;
		return Math.sqrt(dx * dx + dy * dy);
	}

	@Override
	public List<Location> findPath(Location source, Location destination) {
		return null;
	}

	@Override
	public TilePath findPath(Mob mob, Location base, int srcX, int srcY, int dstX, int dstY, int z, int radius,
			boolean running, boolean ignoreLastStep, boolean moveNear) {
		TilePath state = new TilePath();

		CacheObjectDefinition definition = obj.getDefinition();
		int surroundings = definition.getSurroundings();

		if (type == 10 || type == 11 || type == 22) {

			if (orientation != 1 && orientation != 3) {
				width = definition.getSizeX();
				length = definition.getSizeY();
			} else {
				width = definition.getSizeY();
				length = definition.getSizeX();
			}
			if (orientation != 0) {
				surroundings = (surroundings << orientation & 0xF) + (surroundings >> 4 - orientation);
			}
		}
		if (srcX < 0 || srcY < 0 || srcX >= 104 || srcY >= 104 || dstX < 0 || dstY < 0 || dstX >= 104 || dstY >= 104) {
			state.routeFailed();
			return state;
		}
		if (srcX == dstX && srcY == dstY) {
			return state;
		}
		Location location = Location.create((base.getRegionX() - 6) << 3, (base.getRegionY() - 6) << 3,
				base.getPlane());

		boolean foundPath = false;

		for (int xx = 0; xx < 104; xx++) {
			for (int yy = 0; yy < 104; yy++) {
				via[xx][yy] = 0;
				cost[xx][yy] = 99999999;
			}
		}

		int curX = srcX;
		int curY = srcY;
		int attempts = 0;
		int readPosition = 0;
		check(mob, curX, curY, 99, 0);
		while (writePathPosition != readPosition) {
			curX = queueX[readPosition];
			curY = queueY[readPosition];
			readPosition = readPosition + 1 & 0xfff;
			if (curX == dstX && curY == dstY) {
				foundPath = true;
				break;
			}

			if (type != 0) {
				if ((type < 5 || type == 10) && reachedWall(curX, curY, dstX, dstY, orientation, type - 1)) {
					// System.out.println("Reached Wall");
					foundPath = true;
					break;
				}
				if (type < 10 && reachedDecoration(curX, curY, dstX, dstY, type - 1, orientation)) {
					foundPath = true;
					break;
				}
			} // sorry had 2 remove that not used

			if (width != 0 && length != 0 && reachedObject(curX, curY, dstX, dstY, length, surroundings, width)) {
				foundPath = true;
				break;
			} // u legit just undid everything no

			int absX = location.getX() + curX, absY = location.getY() + curY;
			int thisCost = cost[curX][curY] + 1;
			if (curY > 0 && via[curX][curY - 1] == 0
					&& (RegionClipping.getClippingMask(absX, absY - 1, z) & 0x1280102) == 0) {
				check(mob, curX, curY - 1, SOUTH_FLAG, thisCost);
			}
			if (curX > 0 && via[curX - 1][curY] == 0
					&& (RegionClipping.getClippingMask(absX - 1, absY, z) & 0x1280108) == 0) {
				check(mob, curX - 1, curY, WEST_FLAG, thisCost);
			}
			if (curY < 103 && via[curX][curY + 1] == 0
					&& (RegionClipping.getClippingMask(absX, absY + 1, z) & 0x1280120) == 0) {
				check(mob, curX, curY + 1, NORTH_FLAG, thisCost);
			}
			if (curX < 103 && via[curX + 1][curY] == 0
					&& (RegionClipping.getClippingMask(absX + 1, absY, z) & 0x1280180) == 0) {
				check(mob, curX + 1, curY, EAST_FLAG, thisCost);
			}
			if (curX > 0 && curY > 0 && via[curX - 1][curY - 1] == 0
					&& (RegionClipping.getClippingMask(absX - 1, absY - 1, z) & 0x128010e) == 0
					&& (RegionClipping.getClippingMask(absX - 1, absY, z) & 0x1280108) == 0
					&& (RegionClipping.getClippingMask(absX, absY - 1, z) & 0x1280102) == 0) {
				check(mob, curX - 1, curY - 1, SOUTH_WEST_FLAG, thisCost);
			}
			if (curX > 0 && curY < 103 && via[curX - 1][curY + 1] == 0
					&& (RegionClipping.getClippingMask(absX - 1, absY + 1, z) & 0x1280138) == 0
					&& (RegionClipping.getClippingMask(absX - 1, absY, z) & 0x1280108) == 0
					&& (RegionClipping.getClippingMask(absX, absY + 1, z) & 0x1280120) == 0) {
				check(mob, curX - 1, curY + 1, NORTH_WEST_FLAG, thisCost);
			}
			if (curX < 103 && curY > 0 && via[curX + 1][curY - 1] == 0
					&& (RegionClipping.getClippingMask(absX + 1, absY - 1, z) & 0x1280183) == 0
					&& (RegionClipping.getClippingMask(absX + 1, absY, z) & 0x1280180) == 0
					&& (RegionClipping.getClippingMask(absX, absY - 1, z) & 0x1280102) == 0) {
				check(mob, curX + 1, curY - 1, SOUTH_EAST_FLAG, thisCost);
			}
			if (curX < 103 && curY < 103 && via[curX + 1][curY + 1] == 0
					&& (RegionClipping.getClippingMask(absX + 1, absY + 1, z) & 0x12801e0) == 0
					&& (RegionClipping.getClippingMask(absX + 1, absY, z) & 0x1280180) == 0
					&& (RegionClipping.getClippingMask(absX, absY + 1, z) & 0x1280120) == 0) {
				check(mob, curX + 1, curY + 1, NORTH_EAST_FLAG, thisCost);
			}
		}
		if (!foundPath) {
			// System.out.println("No path found");
			state.routeFailed();
			if (moveNear) {
				int highCost = 100;
				for (int delta = 1; delta < 2; delta++) {
					for (int x = dstX - delta; x <= dstX + delta; x++) {
						for (int y = dstY - delta; y <= dstY + delta; y++) {
							if (x >= 0 && y >= 0 && x < 104 && y < 104 && cost[x][y] < highCost && RegionClipping
									.isPassable(location.getX() + x, location.getY() + y, location.getPlane())) {
								foundPath = true;
								highCost = cost[x][y];
								curX = x;
								curY = y;
							}
						}
					}
					if (foundPath) {
						break;
					}
				}
				/*
				 * int fullCost = 1000; int thisCost = 100; int depth = 15; int
				 * xLength = width;//obj.getDefinition().getSizeX();// xlength
				 * should be width and ylength should be length (which is
				 * actually height?) int yLength =
				 * length;//obj.getDefinition().getSizeY(); out of curiosity
				 * lets do getSizeY() and getSizeX() here again but try the tree
				 * see if its fixed. for (int x = dstX - depth; x <= dstX +
				 * depth; x++) { for (int y = dstY - depth; y <= dstY + depth;
				 * y++) { if (x >= 0 && y >= 0 && x < 104 && y < 104 &&
				 * cost[x][y] < 100) { int diffX = 0; if (x < dstX) diffX = dstX
				 * - x; else if (x > dstX + xLength - 1) diffX = x - (dstX +
				 * xLength - 1); int diffY = 0; if (y < dstY) diffY = dstY - y;
				 * else if (y > dstY + yLength - 1)//i was trying to implement
				 * his fix in here diffY = y - (dstY + yLength - 1); if
				 * (!RegionClipping.isPassable(location.getX() + x,
				 * location.getY() + y, obj.getZ())) { continue; } int totalCost
				 * = diffX * diffX + diffY * diffY;//yes.. lol -.- the
				 * regionclipping was the fix for most if (totalCost < fullCost
				 * || (totalCost == fullCost && (cost[x][y] < thisCost))) {
				 * fullCost = totalCost; thisCost = cost[x][y]; curX = x; curY =
				 * y; } } } }
				 */
				/*
				 * if (fullCost == 1000) {
				 * //System.out.println("Shit mothafucka."); return state; }
				 */
			}
			if (!foundPath) {
				return state;
			}
		}
		readPosition = 0;
		queueX[readPosition] = curX;
		queueY[readPosition++] = curY;
		int l5;
		attempts = 0;
		for (int j5 = l5 = via[curX][curY]; curX != srcX || curY != srcY; j5 = via[curX][curY]) {
			if (attempts++ > queueX.length) {
				state.routeFailed();
				return state;
			}
			if (j5 != l5) {
				l5 = j5;
				queueX[readPosition] = curX;
				queueY[readPosition++] = curY;
			}
			if ((j5 & WEST_FLAG) != 0) {
				curX++;
			} else if ((j5 & EAST_FLAG) != 0) {
				curX--;
			}
			if ((j5 & SOUTH_FLAG) != 0) {
				curY++;
			} else if ((j5 & NORTH_FLAG) != 0) {
				curY--;
			}
		}
		if (readPosition > 0) {
			int waypointCount = readPosition;
			if (waypointCount > 25) {
				waypointCount = 25;
			}
			readPosition--;
			int absX = location.getX() + queueX[readPosition];
			int absY = location.getY() + queueY[readPosition];
			state.getPoints().add(BasicPoint.create(absX, absY, z));
			for (int i = 1; i < waypointCount; i++) {
				readPosition--;
				absX = location.getX() + queueX[readPosition];
				absY = location.getY() + queueY[readPosition];
				state.getPoints().add(BasicPoint.create(absX, absY, z));
			}
		}
		return state;
	}

	public boolean reachedObject(int x, int y, int finalX, int finalY, int height, int surroundings, int width) {
		int maxX = finalX + width - 1;
		int maxY = finalY + height - 1;

		int clipping = RegionClipping.getClippingMask(x, y, z);
		if (x >= finalX && x <= maxX && y >= finalY && y <= maxY) {
			return true;
		} else if (x == finalX - 1 && y >= finalY && y <= maxY && (clipping & WALL_EAST) == 0
				&& (surroundings & WALL_EAST) == 0) {
			return true;
		} else if (x == maxX + 1 && y >= finalY && y <= maxY && (clipping & WALL_WEST) == 0
				&& (surroundings & WALL_NORTH) == 0) {
			return true;
		} else if (y == finalY - 1 && x >= finalX && x <= maxX && (clipping & WALL_NORTH) == 0
				&& (surroundings & WALL_NORTHEAST) == 0) {
			return true;
		}

		return y == maxY + 1 && x >= finalX && x <= maxX && (clipping & WALL_SOUTH) == 0
				&& (surroundings & WALL_NORTHWEST) == 0;
	}

	public boolean reachedWall(int initialX, int initialY, int finalX, int finalY, int orientation, int type) {
		if (initialX == finalX && initialY == finalY) {
			return true;
		}
		int clipping = RegionClipping.getClippingMask(initialX, initialY, z);
		if (type == 0) {
			if (orientation == Orientation.NORTH) {
				if (initialX == finalX - 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (clipping & 0x1280120) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (clipping & 0x1280102) == 0) {
					return true;
				}
			} else if (orientation == Orientation.EAST) {
				if (initialX == finalX && initialY == finalY + 1) {
					return true;
				} else if (initialX == finalX - 1 && initialY == finalY && (clipping & 0x1280108) == 0) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY && (clipping & 0x1280180) == 0) {
					return true;
				}
			} else if (orientation == Orientation.SOUTH) {
				if (initialX == finalX + 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (clipping & 0x1280120) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (clipping & 0x1280102) == 0) {
					return true;
				}
			} else if (orientation == Orientation.WEST) {
				if (initialX == finalX && initialY == finalY - 1) {
					return true;
				} else if (initialX == finalX - 1 && initialY == finalY && (clipping & 0x1280108) == 0) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY && (clipping & 0x1280180) == 0) {
					return true;
				}
			}
		}

		if (type == 2) {
			if (orientation == Orientation.NORTH) {
				if (initialX == finalX - 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY && (clipping & 0x1280180) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (clipping & 0x1280102) == 0) {
					return true;
				}
			} else if (orientation == Orientation.EAST) {
				// UNLOADED_TILE | BLOCKED_TILE | UNKNOWN | OBJECT_TILE |
				// WALL_EAST
				if (initialX == finalX - 1 && initialY == finalY && (clipping & 0x1280108) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (clipping & 0x1280102) == 0) {
					return true;
				}
			} else if (orientation == Orientation.SOUTH) {
				if (initialX == finalX - 1 && initialY == finalY && (clipping & 0x1280108) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (clipping & 0x1280120) == 0) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1) {
					return true;
				}
			} else if (orientation == Orientation.WEST) {
				if (initialX == finalX - 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (clipping & 0x1280120) == 0) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY && (clipping & 0x1280180) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1) {
					return true;
				}
			}
		}

		if (type == 9) {
			if (initialX == finalX && initialY == finalY + 1 && (clipping & WALL_SOUTH) == 0) {
				return true;
			} else if (initialX == finalX && initialY == finalY - 1 && (clipping & WALL_NORTH) == 0) {
				return true;
			} else if (initialX == finalX - 1 && initialY == finalY && (clipping & WALL_EAST) == 0) {
				return true;
			} else if (initialX == finalX + 1 && initialY == finalY && (clipping & WALL_WEST) == 0) {
				return true;
			}
		}

		return false;
	}

	public boolean reachedDecoration(int initialY, int initialX, int finalX, int finalY, int type, int orientation) {
		if (initialX == finalX && initialY == finalY) {
			return true;
		}
		int clipping = RegionClipping.getClippingMask(initialX, initialY, z);
		if (type == 6 || type == 7) {
			if (type == 7) {
				orientation = orientation + 2 & 3;
			}

			if (orientation == Orientation.NORTH) {
				if (initialX == finalX + 1 && initialY == finalY && (clipping & WALL_WEST) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (clipping & WALL_NORTH) == 0) {
					return true;
				}
			} else if (orientation == Orientation.EAST) {
				if (initialX == finalX - 1 && initialY == finalY && (clipping & WALL_EAST) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (clipping & WALL_NORTH) == 0) {
					return true;
				}
			} else if (orientation == Orientation.SOUTH) {
				if (initialX == finalX - 1 && initialY == finalY && (clipping & WALL_EAST) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (clipping & WALL_SOUTH) == 0) {
					return true;
				}
			} else if (orientation == Orientation.WEST) {
				if (initialX == finalX + 1 && initialY == finalY && (clipping & WALL_WEST) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (clipping & WALL_SOUTH) == 0) {
					return true;
				}
			}
		}

		if (type == 8) {
			if (initialX == finalX && initialY == finalY + 1 && (clipping & WALL_SOUTH) == 0) {
				return true;
			} else if (initialX == finalX && initialY == finalY - 1 && (clipping & WALL_NORTH) == 0) {
				return true;
			} else if (initialX == finalX - 1 && initialY == finalY && (clipping & WALL_EAST) == 0) {
				return true;
			} else if (initialX == finalX + 1 && initialY == finalY && (clipping & WALL_WEST) == 0) {
				return true;
			}
		}

		return false;
	}

	public static final int BLOCKED_TILE = 0x200000;
	public static final int OBJECT_TILE = 0x100;
	public static final int WALL_EAST = 0x8;
	public static final int WALL_NORTH = 0x2;
	public static final int WALL_NORTHEAST = 0x4;
	public static final int WALL_NORTHWEST = 0x1;
	public static final int WALL_SOUTH = 0x20;
	public static final int WALL_SOUTHEAST = 0x10;
	public static final int WALL_SOUTHWEST = 0x40;
	public static final int WALL_WEST = 0x80;

	public final class Orientation {

		public static final int NORTH = 0;
		public static final int EAST = 1;
		public static final int SOUTH = 2;
		public static final int WEST = 3;

	}

}
