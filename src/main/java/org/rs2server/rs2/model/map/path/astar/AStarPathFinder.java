package org.rs2server.rs2.model.map.path.astar;

import com.google.common.collect.ImmutableList;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.rs2.model.map.path.ClippingFlag;
import org.rs2server.rs2.model.map.path.PathFinder;
import org.rs2server.rs2.model.map.path.PathPrecondition;
import org.rs2server.rs2.model.map.path.TilePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AStarPathFinder implements PathFinder {

	/**
	 * Represents a node used by the A* algorithm.
	 *
	 * @author Graham Edgecombe
	 */
	private static class Node implements Comparable<Node> {
		/**
		 * The cost.
		 */
		private int cost = 1000;
		/**
		 * The parent node.
		 */
		private Node parent = null;
		/**
		 * The x coordinate.
		 */
		private final int x;
		/**
		 * The y coordinate.
		 */
		private final int y;

		/**
		 * Creates a node.
		 *
		 * @param x
		 *            The x coordinate.
		 * @param y
		 *            The y coordinate.
		 */
		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getCost() {
			return cost;
		}

		/**
		 * Gets the parent node.
		 *
		 * @return The parent node.
		 */
		public Node getParent() {
			return parent;
		}

		/**
		 * Gets the X coordinate.
		 *
		 * @return The X coordinate.
		 */
		public int getX() {
			return x;
		}

		/**
		 * Gets the Y coordinate.
		 *
		 * @return The Y coordinate.
		 */
		public int getY() {
			return y;
		}

		public void setCost(int cost) {
			this.cost = cost;
		}

		/**
		 * Sets the parent.
		 *
		 * @param parent
		 *            The parent.
		 */
		public void setParent(Node parent) {
			this.parent = parent;
		}

		@Override
		public int compareTo(Node node) {
			return cost < node.cost ? 1 : cost > node.cost ? -1 : 0;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(AStarPathFinder.class);
	private static final int MAX_PATH_SIZE = 512;
	private static final int MAX_ITERATIONS = 2056;

	private int lowestH = 999999;
	private Node lowestN;
	private Node dest;

	private final List<PathPrecondition> preconditions;
	private final Set<Node> closed = new HashSet<Node>();
	private final Node[][] nodes = new Node[104][104];
	private final Set<Node> open = new HashSet<>();

	private Node current;

	public AStarPathFinder() {
		this(ImmutableList.of());
	}

	public AStarPathFinder(final List<PathPrecondition> preconditions) {
		this.preconditions = preconditions;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				nodes[x][y] = new Node(x, y);
			}
		}
	}

	/**
	 * Estimates a distance between the two points.
	 *
	 * @param src
	 *            The source node.
	 * @param dst
	 *            The distance node.
	 * @return The distance.
	 */
	public int estimateDistance(Node src, Node dst) {
		int deltaX = src.getX() - dst.getX();
		int deltaY = src.getY() - dst.getY();
		return Math.abs(deltaX) + Math.abs(deltaY);
	}

	/**
	 * Opens or closes the given node depending on the cost.
	 *
	 * @param n
	 */
	private void examineNode(Node n) {
		int heuristic = estimateDistance(current, n);
		int nextStepCost = current.getCost() + heuristic;

		int hEnd = estimateDistance(current, dest);
		if (hEnd < lowestH) {
			lowestH = hEnd;
			lowestN = current;
		}
		if (nextStepCost < n.getCost()) {
			open.remove(n);
			closed.remove(n);
		}
		if (!open.contains(n) && !closed.contains(n)) {
			n.setParent(current);
			n.setCost(nextStepCost);
			open.add(n);
		}
	}

	@SuppressWarnings("unused")
	@Override
	public List<Location> findPath(Location source, Location destination) {
		final int srcX = source.getLocalX();
		final int srcY = source.getLocalY();
		final int dstX = destination.getLocalX(source);
		final int dstY = destination.getLocalY(source);

		if (srcX < 0 || srcY < 0 || srcX >= 104 || srcY >= 104 || dstX < 0 || dstY < 0 || dstX >= 104 || dstY >= 104) {
			if (Constants.DEBUG)
				logger.info("Source or destination coordinates outside of region space!");
			return null;
		}

		if (srcX == dstX && srcY == dstY) {
			return ImmutableList.of(source);
		}

		// The base region coordinates (bottom left of the region?).
		Location regionBase = Location.create((source.getRegionX() - 6) << 3, (source.getRegionY() - 6) << 3,
				source.getPlane());
		final int z = regionBase.getPlane();

		Node src = nodes[srcX][srcY];
		src.setCost(0);

		this.dest = nodes[dstX][dstY];

		open.add(src);

		int iterations = 0;
		final long startTime = System.currentTimeMillis();

		search: while (open.size() > 0 && ++iterations < MAX_ITERATIONS) {
			current = getLowestCostOpenNode();
			if (current == dest) {
				break;
			}
			open.remove(current);
			closed.add(current);

			final int x = current.getX();
			final int y = current.getY();
			final int absX = regionBase.getX() + x;
			final int absY = regionBase.getY() + y;

			// Terminate the path if any preconditions are met
			for (final PathPrecondition precondition : preconditions) {
				if (precondition.targetReached(absX, absY, destination.getX(), destination.getY())) {
					// Since the precondition is satisfied at the current
					// location, this is now the destination.
					dest = current;
					open.clear();
					break search;
				}
			}

			if (y > 0 && ClippingFlag.BLOCK_NORTH.and(RegionClipping.getClippingMask(absX, absY - 1, z)) == 0) {
				Node n = nodes[x][y - 1];
				examineNode(n);
			}

			if (x > 0 && ClippingFlag.BLOCK_EAST.and(RegionClipping.getClippingMask(absX - 1, absY, z)) == 0) {
				Node n = nodes[x - 1][y];
				examineNode(n);
			}

			if (y < 104 - 1 && ClippingFlag.BLOCK_SOUTH.and(RegionClipping.getClippingMask(absX, absY + 1, z)) == 0) {
				Node n = nodes[x][y + 1];
				examineNode(n);
			}

			if (x < 104 - 1 && ClippingFlag.BLOCK_WEST.and(RegionClipping.getClippingMask(absX + 1, absY, z)) == 0) {
				Node n = nodes[x + 1][y];
				examineNode(n);
			}

			int mask = RegionClipping.getClippingMask(absX, absY, z);
			int maskN = RegionClipping.getClippingMask(absX, absY + 1, z);
			int maskNE = RegionClipping.getClippingMask(absX + 1, absY + 1, z);
			int maskE = RegionClipping.getClippingMask(absX + 1, absY, z);
			int maskSE = RegionClipping.getClippingMask(absX + 1, absY - 1, z);
			int maskS = RegionClipping.getClippingMask(absX, absY - 1, z);
			int maskSW = RegionClipping.getClippingMask(absX - 1, absY - 1, z);
			int maskW = RegionClipping.getClippingMask(absX - 1, absY, z);
			int maskNW = RegionClipping.getClippingMask(absX - 1, absY + 1, z);

			// South-west
			if (x > 0 && y > 0) {
				if (ClippingFlag.BLOCK_NORTH_EAST.and(RegionClipping.getClippingMask(absX - 1, absY - 1, z)) == 0
						&& ClippingFlag.BLOCK_EAST.and(RegionClipping.getClippingMask(absX - 1, absY, z)) == 0
						&& ClippingFlag.BLOCK_NORTH.and(RegionClipping.getClippingMask(absX, absY - 1, z)) == 0) {
					Node n = nodes[x - 1][y - 1];
					examineNode(n);
				}
			}

			// North-west
			if (x > 0 && y < 104 - 1) {
				if (ClippingFlag.BLOCK_SOUTH_EAST.and(RegionClipping.getClippingMask(absX - 1, absY + 1, z)) == 0
						&& ClippingFlag.BLOCK_EAST.and(RegionClipping.getClippingMask(absX - 1, absY, z)) == 0
						&& ClippingFlag.BLOCK_SOUTH.and(RegionClipping.getClippingMask(absX, absY + 1, z)) == 0
				/*
				 * && ClippingFlag.BLOCK_NORTH_WEST.and(mask) == 0 &&
				 * ClippingFlag.BLOCK_NORTH.and(mask) == 0 && ClippingFlag.BLOCK_WEST.and(mask)
				 * == 0
				 */) {
					Node n = nodes[x - 1][y + 1];
					examineNode(n);
				}
			}

			// South-east
			if (x < 104 - 1 && y > 0) {
				if (ClippingFlag.BLOCK_NORTH_WEST.and(RegionClipping.getClippingMask(absX + 1, absY - 1, z)) == 0
						&& ClippingFlag.BLOCK_WEST.and(RegionClipping.getClippingMask(absX + 1, absY, z)) == 0
						&& ClippingFlag.BLOCK_NORTH.and(RegionClipping.getClippingMask(absX, absY - 1, z)) == 0
				/*
				 * && ClippingFlag.BLOCK_SOUTH_EAST.and(mask) == 0 &&
				 * ClippingFlag.BLOCK_SOUTH.and(mask) == 0 && ClippingFlag.BLOCK_EAST.and(mask)
				 * == 0
				 */) {
					Node n = nodes[x + 1][y - 1];
					examineNode(n);
				}
			}
			// North-east
			if (x < 104 - 1 && y < 104 - 1) {
				if (ClippingFlag.BLOCK_SOUTH_WEST.and(RegionClipping.getClippingMask(absX + 1, absY + 1, z)) == 0
						&& ClippingFlag.BLOCK_WEST.and(RegionClipping.getClippingMask(absX + 1, absY, z)) == 0
						&& ClippingFlag.BLOCK_SOUTH.and(RegionClipping.getClippingMask(absX, absY + 1, z)) == 0
				/*
				 * && ClippingFlag.BLOCK_NORTH_EAST.and(mask) == 0 &&
				 * ClippingFlag.BLOCK_NORTH.and(mask) == 0 && ClippingFlag.BLOCK_EAST.and(mask)
				 * == 0
				 */) {
					Node n = nodes[x + 1][y + 1];
					examineNode(n);
				}
			}
		}
		final long duration = System.currentTimeMillis() - startTime;
		if (duration > 30) {
			if (Constants.DEBUG)
				logger.warn("Path search with {} iterations took {}ms.", iterations, duration);
		}

		if (dest.getParent() == null) {
			dest = lowestN;
		}

		if (dest == null || dest.getParent() == null) {
			return null;
		}

		final LinkedList<Location> nodeList = new LinkedList<>();
		int prependedNodes = 0;

		// Walks the list backwards, from destination to source, and constructs
		// a path.
		Node node = dest;
		while (node != null && dest != src) {
			if (++prependedNodes >= MAX_PATH_SIZE) {
				break;
			}
			int absX = regionBase.getX() + node.getX();
			int absY = regionBase.getY() + node.getY();

			nodeList.addFirst(Location.create(absX, absY, z));
			node = node.getParent();
		}

		return nodeList;
	}

	@Override
	public TilePath findPath(Mob mob, Location base, int srcX, int srcY, int dstX, int dstY, int z, int radius,
			boolean running, boolean ignoreLastStep, boolean moveNear) {
		return null;
	}

	private Node getLowestCostOpenNode() {
		Node curLowest = null;
		for (Node n : open) {
			if (curLowest == null) {
				curLowest = n;
			} else {
				if (n.getCost() < curLowest.getCost()) {
					curLowest = n;
				}
			}
		}
		return curLowest;
	}

}
