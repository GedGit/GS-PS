package org.rs2server.rs2.model.map.path.astar;

import org.rs2server.cache.format.CacheObjectDefinition;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.gameobject.GameObjectCardinality;
import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.rs2.model.map.path.ClippingFlag;
import org.rs2server.rs2.model.map.path.PathPrecondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A precondition which evaluates to true when the current x,y coordinate in a
 * path finding iteration is in reach of a target game object.
 *
 * @author tommo
 */
public class ObjectReachedPrecondition implements PathPrecondition {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ObjectReachedPrecondition.class);

	@SuppressWarnings("unused")
	private final Mob mob;
	@SuppressWarnings("unused")
	private final GameObject gameObject;

	private int surroundings;
	private int orientation;
	private int type;
	private int width;
	private int length;
	private int z;

	public ObjectReachedPrecondition(final Mob mob, final GameObject gameObject) {
		this.mob = mob;
		this.gameObject = gameObject;

		final CacheObjectDefinition definition = gameObject.getDefinition();
		this.type = gameObject.getType();
		this.surroundings = definition.getSurroundings();
		this.orientation = gameObject.getDirection();
		this.width = definition.getSizeX();
		this.length = definition.getSizeY();
		this.z = gameObject.getLocation().getPlane();

		if (type == 10 || type == 11 || type == 22) {
			if (orientation % 2 == 0) {
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
	}

	@Override
	public boolean targetReached(int currentX, int currentY, int destinationX, int destinationY) {
		if (type != 0) {
			if ((type < 5 || type == 10)
					&& reachedWall(currentX, currentY, destinationX, destinationY, orientation, type - 1)) {
				return true;
			}
			if (type < 10 && reachedDecoration(currentX, currentY, destinationX, destinationY, type - 1, orientation)) {
				return true;
			}
		}

		if (width != 0 && length != 0
				&& reachedObject(currentX, currentY, destinationX, destinationY, width, length, surroundings)) {
			return true;
		}

		return false;
	}

	public boolean reachedObject(int x, int y, int targetX, int targetY, int width, int height, int surroundings) {
		int maxX = targetX + width - 1;
		int maxY = targetY + height - 1;

		final int clipping = RegionClipping.getClippingMask(x, y, z);

		if (x >= targetX && x <= maxX && y >= targetY && y <= maxY) {
			return true;
		}

		if (x == targetX - 1 && y >= targetY && y <= maxY && (clipping & ClippingFlag.BLOCK_EAST.getMask()) == 0
				&& (surroundings & ClippingFlag.BLOCK_EAST.getMask()) == 0) {
			return true;
		} else if (x == maxX + 1 && y >= targetY && y <= maxY && (clipping & ClippingFlag.BLOCK_WEST.getMask()) == 0
				&& (surroundings & ClippingFlag.BLOCK_NORTH.getMask()) == 0) {
			return true;
		} else if (y == targetY - 1 && x >= targetX && x <= maxX && (clipping & ClippingFlag.BLOCK_NORTH.getMask()) == 0
				&& (surroundings & ClippingFlag.BLOCK_NORTH_EAST.getMask()) == 0) {
			return true;
		}

		// 1x1 objects should be interacted with from the basic cardinal
		// directions (NESW) and not diagonal.
		if (width == 1 && height == 1) {
			// from the north
			if (x == targetX && y == targetY + 1 && (surroundings & ClippingFlag.BLOCK_NORTH.getMask()) == 0) {
				return true;
			}
			// from the east
			if (x == targetX + 1 && y == targetY && (surroundings & ClippingFlag.BLOCK_EAST.getMask()) == 0) {
				return true;
			}
			// from the south
			if (x == targetX && y == targetY - 1 && (surroundings & ClippingFlag.BLOCK_SOUTH.getMask()) == 0) {
				return true;
			}
			// from the west
			if (x == targetX - 1 && y == targetY && (surroundings & ClippingFlag.BLOCK_WEST.getMask()) == 0) {
				return true;
			}
		}

		return y == maxY + 1 && x >= targetX && x <= maxX && (clipping & ClippingFlag.BLOCK_SOUTH.getMask()) == 0
				&& (surroundings & ClippingFlag.BLOCK_NORTH_WEST.getMask()) == 0;
	}

	public boolean canInteract(int x, int y, int destX, int destY, int sizeX, int sizeY, int walkFlag, int z) {
		int flag = RegionClipping.getClippingMask(x, y, z);
		int cornerX = destX + sizeX - 1;
		int cornerY = destY + sizeY - 1;
		if (destX <= x && cornerX >= x && y >= destY && y <= cornerY) {
			return true;
		}
		if (x == destX - 1 && destY <= y && y <= cornerY && (0x8 & flag) == 0 && (0x8 & walkFlag) == 0) {
			return true;
		}
		if (x == cornerX + 1 && destY <= y && cornerY >= y && (flag & 0x80) == 0 && (0x2 & walkFlag) == 0) {
			return true;
		}
		if (y == destY - 1 && destX <= x && cornerX >= x && (0x2 & flag) == 0 && (0x4 & walkFlag) == 0) {
			return true;
		}
		if (y == cornerY + 1 && destX <= x && cornerX >= x && (flag & 0x20) == 0 && (0x1 & walkFlag) == 0) {
			return true;
		}
		return false;
	}

	public boolean reachedWall(int initialX, int initialY, int finalX, int finalY, int orientation, int type) {
		if (initialX == finalX && initialY == finalY) {
			return true;
		}
		int clipping = RegionClipping.getClippingMask(initialX, initialY, z);
		if (type == 0) {
			if (orientation == GameObjectCardinality.NORTH.getFace()) {
				if (initialX == finalX - 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (clipping & 0x1280120) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (clipping & 0x1280102) == 0) {
					return true;
				}
			} else if (orientation == GameObjectCardinality.EAST.getFace()) {
				if (initialX == finalX && initialY == finalY + 1) {
					return true;
				} else if (initialX == finalX - 1 && initialY == finalY && (clipping & 0x1280108) == 0) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY && (clipping & 0x1280180) == 0) {
					return true;
				}
			} else if (orientation == GameObjectCardinality.SOUTH.getFace()) {
				if (initialX == finalX + 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (clipping & 0x1280120) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (clipping & 0x1280102) == 0) {
					return true;
				}
			} else if (orientation == GameObjectCardinality.WEST.getFace()) {
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
			if (orientation == GameObjectCardinality.NORTH.getFace()) {
				if (initialX == finalX - 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY && (clipping & 0x1280180) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (clipping & 0x1280102) == 0) {
					return true;
				}
			} else if (orientation == GameObjectCardinality.EAST.getFace()) {
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
			} else if (orientation == GameObjectCardinality.SOUTH.getFace()) {
				if (initialX == finalX - 1 && initialY == finalY && (clipping & 0x1280108) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (clipping & 0x1280120) == 0) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1) {
					return true;
				}
			} else if (orientation == GameObjectCardinality.WEST.getFace()) {
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

			if (orientation == GameObjectCardinality.NORTH.getFace()) {
				if (initialX == finalX + 1 && initialY == finalY
						&& (clipping & ClippingFlag.BLOCK_WEST.getMask()) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1
						&& (clipping & ClippingFlag.BLOCK_NORTH.getMask()) == 0) {
					return true;
				}
			} else if (orientation == GameObjectCardinality.EAST.getFace()) {
				if (initialX == finalX - 1 && initialY == finalY
						&& (clipping & ClippingFlag.BLOCK_EAST.getMask()) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1
						&& (clipping & ClippingFlag.BLOCK_NORTH.getMask()) == 0) {
					return true;
				}
			} else if (orientation == GameObjectCardinality.SOUTH.getFace()) {
				if (initialX == finalX - 1 && initialY == finalY
						&& (clipping & ClippingFlag.BLOCK_EAST.getMask()) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1
						&& (clipping & ClippingFlag.BLOCK_SOUTH.getMask()) == 0) {
					return true;
				}
			} else if (orientation == GameObjectCardinality.WEST.getFace()) {
				if (initialX == finalX + 1 && initialY == finalY
						&& (clipping & ClippingFlag.BLOCK_WEST.getMask()) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1
						&& (clipping & ClippingFlag.BLOCK_SOUTH.getMask()) == 0) {
					return true;
				}
			}
		}

		if (type == 8) {
			if (initialX == finalX && initialY == finalY + 1 && (clipping & ClippingFlag.BLOCK_SOUTH.getMask()) == 0) {
				return true;
			} else if (initialX == finalX && initialY == finalY - 1
					&& (clipping & ClippingFlag.BLOCK_NORTH.getMask()) == 0) {
				return true;
			} else if (initialX == finalX - 1 && initialY == finalY
					&& (clipping & ClippingFlag.BLOCK_EAST.getMask()) == 0) {
				return true;
			} else if (initialX == finalX + 1 && initialY == finalY
					&& (clipping & ClippingFlag.BLOCK_WEST.getMask()) == 0) {
				return true;
			}
		}

		return false;
	}

}
