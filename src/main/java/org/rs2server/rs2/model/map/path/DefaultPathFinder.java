package org.rs2server.rs2.model.map.path;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.map.BasicPoint;
import org.rs2server.rs2.model.map.RegionClipping;

import java.util.List;

/**
 * @author 'Mystic Flow
 */
public class DefaultPathFinder implements PathFinder {

	private int[] queueX = new int[4096];
	private int[] queueY = new int[4096];
	private int[][] via = new int[104][104];
	private int[][] cost = new int[104][104];
	private int writePathPosition = 0;

	public static final int DIRECTION_NORTH = 0x2;
	public static final int DIRECTION_EAST = 0x8;
	public static final int DIRECTION_SOUTH = 0x20;
	public static final int DIRECTION_WEST = 0x80;
	public static final int BLOCKED = 0x100;
	public static final int INVALID = 0x200000 | 0x40000;

	public DefaultPathFinder() {//think it has todo with surroundings, that accounts for it and tells if it reached object or not
	}

	public void check(Mob mob, int x, int y, int viaDir, int thisCost) {
		queueX[writePathPosition] = x;
		queueY[writePathPosition] = y;
		via[x][y] = viaDir;
		cost[x][y] = thisCost;
		writePathPosition = writePathPosition + 1 & 0xfff;
	}

	@Override
	public List<Location> findPath(Location source, Location destination) {
		return null;
	}

	@Override
	public TilePath findPath(Mob mob, Location base, int srcX, int srcY, int dstX, int dstY, int z, int radius, boolean running, boolean ignoreLastStep, boolean moveNear) {
		TilePath state = new TilePath();
		if (srcX < 0 || srcY < 0 || srcX >= 104 || srcY >= 104 || dstX < 0 || dstY < 0 || dstX >= 104 || dstY >= 104) {
			state.routeFailed();
			return state;
		}
		if (srcX == dstX && srcY == dstY) {
			return state;
		}
		Location location = Location.create((base.getRegionX() - 6) << 3, (base.getRegionY() - 6) << 3, base.getPlane());

		boolean foundPath = false;

		for (int xx = 0; xx < 104; xx++) {
			for (int yy = 0; yy < 104; yy++) {
				via[xx][yy] = 0;
				cost[xx][yy] = 99999999;
			}
		}// what if u get closer to the tree but like, more o the correct path, would it go to right spot

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
			int absX = location.getX() + curX, absY = location.getY() + curY;

			if (curY > 0 && via[curX][curY - 1] == 0 && (RegionClipping.getClippingMask(absX, absY - 1, z) & 0x1280102) == 0) {
				check(mob, curX, curY - 1, SOUTH_FLAG, 2);
			}
			if (curX > 0 && via[curX - 1][curY] == 0 && (RegionClipping.getClippingMask(absX - 1, absY, z) & 0x1280108) == 0) {
				check(mob, curX - 1, curY, WEST_FLAG, 8);
			}
			if (curY < 103 && via[curX][curY + 1] == 0 && (RegionClipping.getClippingMask(absX, absY + 1, z) & 0x1280120) == 0) {
				check(mob, curX, curY + 1, NORTH_FLAG, 1);
			}
			if (curX < 103 && via[curX + 1][curY] == 0 && (RegionClipping.getClippingMask(absX + 1, absY, z) & 0x1280180) == 0) {
				check(mob, curX + 1, curY, EAST_FLAG, 4);
			}
			if (curX > 0 && curY > 0 && via[curX - 1][curY - 1] == 0 && (RegionClipping.getClippingMask(absX - 1, absY - 1, z) & 0x128010e) == 0 && (RegionClipping.getClippingMask(absX - 1, absY, z) & 0x1280108) == 0 && (RegionClipping.getClippingMask(absX, absY - 1, z) & 0x1280102) == 0) {
				check(mob, curX - 1, curY - 1, SOUTH_WEST_FLAG, 3);
			}
			if (curX > 0 && curY < 103 && via[curX - 1][curY + 1] == 0 && (RegionClipping.getClippingMask(absX - 1, absY + 1, z) & 0x1280138) == 0 && (RegionClipping.getClippingMask(absX - 1, absY, z) & 0x1280108) == 0 && (RegionClipping.getClippingMask(absX, absY + 1, z) & 0x1280120) == 0) {
				check(mob, curX - 1, curY + 1, NORTH_WEST_FLAG, 9);
			}
			if (curX < 103 && curY > 0 && via[curX + 1][curY - 1] == 0 && (RegionClipping.getClippingMask(absX + 1, absY - 1, z) & 0x1280183) == 0 && (RegionClipping.getClippingMask(absX + 1, absY, z) & 0x1280180) == 0 && (RegionClipping.getClippingMask(absX, absY - 1, z) & 0x1280102) == 0) {
				check(mob, curX + 1, curY - 1, SOUTH_EAST_FLAG, 6);
			}
			if (curX < 103 && curY < 103 && via[curX + 1][curY + 1] == 0 && (RegionClipping.getClippingMask(absX + 1, absY + 1, z) & 0x12801e0) == 0 && (RegionClipping.getClippingMask(absX + 1, absY, z) & 0x1280180) == 0 && (RegionClipping.getClippingMask(absX, absY + 1, z) & 0x1280120) == 0) {
				check(mob, curX + 1, curY + 1, NORTH_EAST_FLAG, 12);
			}
		}
		if (!foundPath) {
			state.routeFailed();
			/*if (moveNear) {
				int fullCost = 1000;
				int thisCost = 100;
				int depth = 10;
				int xLength = mob.getWidth();
				int yLength = mob.getHeight();
				for (int x = dstX - depth; x <= dstX + depth; x++) {
					for (int y = dstY - depth; y <= dstY + depth; y++) {
						if (x >= 0 && y >= 0 && x < 104 && y < 104 && cost[x][y] < 100) {
							int diffX = 0;
							if (x < dstX)
								diffX = dstX - x;
							else if (x > dstX + xLength - 1)
								diffX = x - (dstX + xLength - 1);
							int diffY = 0;
							if (y < dstY)
								diffY = dstY - y;
							else if (y > dstY + yLength - 1)
								diffY = y - (dstY + yLength - 1);
							int totalCost = diffX * diffX + diffY * diffY;
							if (totalCost < fullCost || (totalCost == fullCost && (cost[x][y] < thisCost))) {
								fullCost = totalCost;
								thisCost = cost[x][y];
								curX = x;
								curY = y;
							}
						}
					}
				}
				if (fullCost == 1000)
					return state;
			}*/
			if (moveNear) {
				int highCost = 100;
				for (int delta = 1; delta < 5; delta++) {
					for (int x = dstX - delta; x <= dstX + delta; x++) {
						for (int y = dstY - delta; y <= dstY + delta; y++) {
							if (x >= 0 && y >= 0 && x < 104 && y < 104
									&& cost[x][y] < highCost && RegionClipping.isPassable(location.getX() + x, location.getY() + y, location.getPlane())) {
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
		int size = readPosition--;
		int absX = location.getX() + queueX[readPosition];
		int absY = location.getY() + queueY[readPosition];
		state.getPoints().add(BasicPoint.create(absX, absY, z));
		for (int i = 1; i < size; i++) {
			readPosition--;
			absX = location.getX() + queueX[readPosition];
			absY = location.getY() + queueY[readPosition];
			state.getPoints().add(BasicPoint.create(absX, absY, z));
		}
		return state;
	}
}