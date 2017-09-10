package org.rs2server.rs2.model.map.path;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.map.BasicPoint;
import org.rs2server.rs2.model.map.RegionClipping;

import java.util.List;

/**
 * @author 'Mystic Flow
 */
public class VariablePathFinder implements PathFinder {

	private TilePath state = new TilePath();

	private int z;
	private int type;
	private int walkToData;
	private int direction;
	private int sizeX;
	private int sizeY;

	private int[] queueX = new int[4096];
	private int[] queueY = new int[4096];
	private int[][] via = new int[104][104];
	private int[][] cost = new int[104][104];
	private int writePathPosition = 0;

	public VariablePathFinder(int type, int walkToData, int direction, int sizeX, int sizeY) {
		this.type = type;
		this.walkToData = walkToData;
		this.direction = direction;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
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
		
		z = mob.getLocation().getPlane();
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
		}

		int curX = srcX;
		int curY = srcY;
		int requestX = dstX;
		int requestY = dstY;
		int attempts = 0;
		int readPosition = 0;
		check(mob, curX, curY, 99, 0);
		while (writePathPosition != readPosition) {
			curX = queueX[readPosition];
			curY = queueY[readPosition];
			readPosition = readPosition + 1 & 0xfff;
			if (type == -2 && curX == dstX && curY == dstY) {
				requestX = curX;
				requestY = curY;
				foundPath = true;
				break;
			}
			int absX = location.getX() + curX, absY = location.getY() + curY;
			int thisCost = cost[curX][curY] + 1;
			if (type != -1) {
				if (type == 0 || type == 1 || type == 2 || type == 3 || type == 9) {
					if (reachedObject(dstX, dstY, absX, absY, curX, curY, type, direction)) {
						requestX = curX;
						requestY = curY;
						foundPath = true;
						break;
					}
				} else {
					if (reachedObject2(dstX, dstY, absX, absY, curX, curY, type, direction)) {
						requestX = curX;
						requestY = curY;
						foundPath = true;
						break;
					}
				}
			} else {
				if (reachedObject3(dstX, dstY, absX, absY, curX, curY, sizeX, sizeY, walkToData)) {
					requestX = curX;
					requestY = curY;
					foundPath = true;
					break;
				}
			}
			if (curY > 0 && via[curX][curY - 1] == 0 && (RegionClipping.getClippingMask(absX, absY - 1, z) & 0x1280102) == 0) {
				check(mob, curX, curY - 1, SOUTH_FLAG, thisCost);
			}
			if (curX > 0 && via[curX - 1][curY] == 0 && (RegionClipping.getClippingMask(absX - 1, absY, z) & 0x1280108) == 0) {
				check(mob, curX - 1, curY, WEST_FLAG, thisCost);
			}
			if (curY < 103 && via[curX][curY + 1] == 0 && (RegionClipping.getClippingMask(absX, absY + 1, z) & 0x1280120) == 0) {
				check(mob, curX, curY + 1, NORTH_FLAG, thisCost);
			}
			if (curX < 103 && via[curX + 1][curY] == 0 && (RegionClipping.getClippingMask(absX + 1, absY, z) & 0x1280180) == 0) {
				check(mob, curX + 1, curY, EAST_FLAG, thisCost);
			}
			if (curX > 0 && curY > 0 && via[curX - 1][curY - 1] == 0 && (RegionClipping.getClippingMask(absX - 1, absY - 1, z) & 0x128010e) == 0 && (RegionClipping.getClippingMask(absX - 1, absY, z) & 0x1280108) == 0 && (RegionClipping.getClippingMask(absX, absY - 1, z) & 0x1280102) == 0) {
				check(mob, curX - 1, curY - 1, SOUTH_WEST_FLAG, thisCost);
			}
			if (curX > 0 && curY < 103 && via[curX - 1][curY + 1] == 0 && (RegionClipping.getClippingMask(absX - 1, absY + 1, z) & 0x1280138) == 0 && (RegionClipping.getClippingMask(absX - 1, absY, z) & 0x1280108) == 0 && (RegionClipping.getClippingMask(absX, absY + 1, z) & 0x1280120) == 0) {
				check(mob, curX - 1, curY + 1, NORTH_WEST_FLAG, thisCost);
			}
			if (curX < 103 && curY > 0 && via[curX + 1][curY - 1] == 0 && (RegionClipping.getClippingMask(absX + 1, absY - 1, z) & 0x1280183) == 0 && (RegionClipping.getClippingMask(absX + 1, absY, z) & 0x1280180) == 0 && (RegionClipping.getClippingMask(absX, absY - 1, z) & 0x1280102) == 0) {
				check(mob, curX + 1, curY - 1, SOUTH_EAST_FLAG, thisCost);
			}
			if (curX < 103 && curY < 103 && via[curX + 1][curY + 1] == 0 && (RegionClipping.getClippingMask(absX + 1, absY + 1, z) & 0x12801e0) == 0 && (RegionClipping.getClippingMask(absX + 1, absY, z) & 0x1280180) == 0 && (RegionClipping.getClippingMask(absX, absY + 1, z) & 0x1280120) == 0) {
				check(mob, curX + 1, curY + 1, NORTH_EAST_FLAG, thisCost);
			}
		}
		curX = requestX;
		curY = requestY;
		if (!foundPath) {
			state.routeFailed();
			if (moveNear) {
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
								foundPath = true;
							}
						}
					}
				}
				state.routeFailed();
				if (!foundPath) {
					return state;
				}
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

	public boolean reachedObject(int dstX, int dstY, int absX, int absY, int curX, int curY, int type, int direction) {
		if (curX == dstX && curY == dstY)
			return true;
		int clipping = RegionClipping.getClippingMask(absX, absY, z);
		if (type == 0)
			if (direction == 0) {
				if (curX == dstX - 1 && curY == dstY)
					return true;
				if (curX == dstX && curY == dstY + 1 && (clipping & 0x2c0120) == 0)
					return true;
				if (curX == dstX && curY == dstY - 1 && (clipping & 0x2c0102) == 0)
					return true;
			} else if (direction == 1) {
				if (curX == dstX && curY == dstY + 1)
					return true;
				if (curX == dstX - 1 && curY == dstY && (clipping & 0x2c0108) == 0)
					return true;
				if (curX == dstX + 1 && curY == dstY && (clipping & 0x2c0180) == 0)
					return true;
			} else if (direction == 2) {
				if (curX == dstX + 1 && curY == dstY)
					return true;
				if (curX == dstX && curY == dstY + 1 && (clipping & 0x2c0120) == 0)
					return true;
				if (curX == dstX && curY == dstY - 1 && (clipping & 0x2c0102) == 0)
					return true;
			} else if (direction == 3) {
				if (curX == dstX && curY == dstY - 1)
					return true;
				if (curX == dstX - 1 && curY == dstY && (clipping & 0x2c0108) == 0)
					return true;
				if (curX == dstX + 1 && curY == dstY && (clipping & 0x2c0180) == 0)
					return true;
			}
		if (type == 2)
			if (direction == 0) {
				if (curX == dstX - 1 && curY == dstY)
					return true;
				if (curX == dstX && curY == dstY + 1)
					return true;
				if (curX == dstX + 1 && curY == dstY && (clipping & 0x2c0180) == 0)
					return true;
				if (curX == dstX && curY == dstY - 1 && (clipping & 0x2c0102) == 0)
					return true;
			} else if (direction == 1) {
				if (curX == dstX - 1 && curY == dstY && (clipping & 0x2c0108) == 0)
					return true;
				if (curX == dstX && curY == dstY + 1)
					return true;
				if (curX == dstX + 1 && curY == dstY)
					return true;
				if (curX == dstX && curY == dstY - 1 && (clipping & 0x2c0102) == 0)
					return true;
			} else if (direction == 2) {
				if (curX == dstX - 1 && curY == dstY && (clipping & 0x2c0108) == 0)
					return true;
				if (curX == dstX && curY == dstY + 1 && (clipping & 0x2c0120) == 0)
					return true;
				if (curX == dstX + 1 && curY == dstY)
					return true;
				if (curX == dstX && curY == dstY - 1)
					return true;
			} else if (direction == 3) {
				if (curX == dstX - 1 && curY == dstY)
					return true;
				if (curX == dstX && curY == dstY + 1 && (clipping & 0x2c0120) == 0)
					return true;
				if (curX == dstX + 1 && curY == dstY && (clipping & 0x2c0180) == 0)
					return true;
				if (curX == dstX && curY == dstY - 1)
					return true;
			}
		if (type == 9) {
			if (curX == dstX && curY == dstY + 1 && (clipping & 0x20) == 0)
				return true;
			if (curX == dstX && curY == dstY - 1 && (clipping & 2) == 0)
				return true;
			if (curX == dstX - 1 && curY == dstY && (clipping & 8) == 0)
				return true;
			if (curX == dstX + 1 && curY == dstY && (clipping & 0x80) == 0)
				return true;
		}
		return false;
	}

	public boolean reachedObject2(int dstX, int dstY, int absX, int absY, int curX, int curY, int type, int direction) {
		if (curX == dstX && curY == dstY)
			return true;
		int clipping = RegionClipping.getClippingMask(absX, absY, z);
		if (type == 6 || type == 7) {// maybe we should try the variable path finder? ;o we can try
			if (type == 7)
				direction = direction + 2 & 3;
			if (direction == 0) {
				if (curX == dstX + 1 && curY == dstY && (clipping & 0x80) == 0)
					return true;
				if (curX == dstX && curY == dstY - 1 && (clipping & 2) == 0)
					return true;
			} else if (direction == 1) {
				if (curX == dstX - 1 && curY == dstY && (clipping & 8) == 0)
					return true;
				if (curX == dstX && curY == dstY - 1 && (clipping & 2) == 0)
					return true;
			} else if (direction == 2) {
				if (curX == dstX - 1 && curY == dstY && (clipping & 8) == 0)
					return true;
				if (curX == dstX && curY == dstY + 1 && (clipping & 0x20) == 0)
					return true;
			} else if (direction == 3) {
				if (curX == dstX + 1 && curY == dstY && (clipping & 0x80) == 0)
					return true;
				if (curX == dstX && curY == dstY + 1 && (clipping & 0x20) == 0)
					return true;
			}
		}
		if (type == 8) {
			if (curX == dstX && curY == dstY + 1 && (clipping & 0x20) == 0)
				return true;
			if (curX == dstX && curY == dstY - 1 && (clipping & 2) == 0)
				return true;
			if (curX == dstX - 1 && curY == dstY && (clipping & 8) == 0)
				return true;
			if (curX == dstX + 1 && curY == dstY && (clipping & 0x80) == 0)
				return true;
		}
		return false;
	}

	private boolean reachedObject3(int dstX, int dstY, int absX, int absY, int curX, int curY, int sizeX, int sizeY, int walkToData) {
		int maxX = (dstX + sizeX) - 1;
		int maxY = (dstY + sizeY) - 1;
		int clipping = RegionClipping.getClippingMask(absX, absY, z);
		if (curX >= dstX && maxX >= curX && dstY <= curY && maxY >= curY)
			return true;
		if (curX == dstX - 1 && dstY <= curY && curY <= maxY && (clipping & 0x8) == 0 && (walkToData & 0x8) == 0)
			return true;
		if (maxX + 1 == curX && dstY <= curY && maxY >= curY && (clipping & 0x80) == 0 && (walkToData & 0x2) == 0)
			return true;
		if (dstY - 1 == curY && curX >= dstX && maxX >= curX && (clipping & 0x2) == 0 && (walkToData & 0x4) == 0)
			return true;
		if (maxY + 1 == curY && dstX <= curX && maxX >= curX && (clipping & 0x20) == 0 && (walkToData & 0x1) == 0)
			return true;
		return false;
	}
}