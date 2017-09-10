package org.rs2server.rs2.model;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.tickable.impl.EnergyRestoreTick;
import org.rs2server.rs2.util.DirectionUtils;

import java.util.Deque;
import java.util.LinkedList;

/**
 * <p>
 * A <code>WalkingQueue</code> stores steps the client needs to walk and allows
 * this queue of steps to be modified.
 * </p>
 * 
 * <p>
 * The class will also process these steps when {@link #processNextMovement()}
 * is called. This should be called once per server cycle.
 * </p>
 * 
 * @author Graham Edgecombe
 * 
 */
// TODO implement 'travelback' algorithm so you are unable to noclip while map
// TODO region is loading?
public class WalkingQueue {

	/**
	 * Represents a single point in the queue.
	 * 
	 * @author Graham Edgecombe
	 * 
	 */
	public static class Point {

		/**
		 * The x-coordinate.
		 */
		private final int x;

		/**
		 * The y-coordinate.
		 */
		private final int y;

		/**
		 * The direction to walk to this point.
		 */
		private final int dir;

		/**
		 * Creates a point.
		 * 
		 * @param x
		 *            X coord.
		 * @param y
		 *            Y coord.
		 * @param dir
		 *            Direction to walk to this point.
		 */
		public Point(int x, int y, int dir) {
			this.x = x;
			this.y = y;
			this.dir = dir;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

	/**
	 * The maximum size of the queue. If there are more points than this size,
	 * they are discarded.
	 */
	public static final int MAXIMUM_SIZE = 256;

	/**
	 * Direction variables.
	 */
	public static final int NORTH = 1, SOUTH = 6, EAST = 4, WEST = 3, NORTH_EAST = 2, SOUTH_EAST = 7, NORTH_WEST = 0,
			SOUTH_WEST = 5;

	/**
	 * The entity.
	 */
	private Mob mob;

	/**
	 * The queue of waypoints.
	 */
	private Deque<Point> waypoints = new LinkedList<Point>();

	/**
	 * Run toggle (button in client).
	 */
	private boolean runToggled = false;

	/**
	 * Run for this queue (CTRL-CLICK) toggle.
	 */
	private boolean runQueue = false;

	/**
	 * The entity's energy to run.
	 */
	private int energy = 100;

	/**
	 * Creates the <code>WalkingQueue</code> for the specified
	 * <code>Entity</code>.
	 * 
	 * @param entity
	 *            The entity whose walking queue this is.
	 */
	public WalkingQueue(Mob mob) {
		this.mob = mob;
	}

	/**
	 * Sets the run toggled flag.
	 * 
	 * @param runToggled
	 *            The run toggled flag.
	 */
	public void setRunningToggled(boolean runToggled) {
		this.runToggled = runToggled;
	}

	/**
	 * Sets the run queue flag.
	 * 
	 * @param runQueue
	 *            The run queue flag.
	 */
	public void setRunningQueue(boolean runQueue) {
		this.runQueue = runQueue;
	}

	/**
	 * Gets the run toggled flag.
	 * 
	 * @return The run toggled flag.
	 */
	public boolean isRunningToggled() {
		return runToggled;
	}

	/**
	 * Gets the running queue flag.
	 * 
	 * @return The running queue flag.
	 */
	public boolean isRunningQueue() {
		return runQueue;
	}

	/**
	 * Checks if any running flag is set.
	 * 
	 * @return <code>true</code. if so, <code>false</code> if not.
	 */
	public boolean isRunning() {
		return runToggled || runQueue;
	}

	/**
	 * @return The energy.
	 */
	public int getEnergy() {
		return energy;
	}

	/**
	 * @param energy
	 *            The energy to set.
	 */
	public void setEnergy(int energy) {
		this.energy = energy;
		if (this.energy < 100) {
			if (mob.getEnergyRestoreTick() == null) {
				EnergyRestoreTick energyRestoreTick = new EnergyRestoreTick(mob);
				mob.setEnergyRestoreTick(energyRestoreTick);
				World.getWorld().submit(energyRestoreTick);
			}
		} else {
			if (mob.getEnergyRestoreTick() != null) {
				mob.getEnergyRestoreTick().stop();
				mob.setEnergyRestoreTick(null);
			}
		}
	}

	/**
	 * Resets the walking queue so it contains no more steps.
	 */
	public void reset() {
		runQueue = false;
		waypoints.clear();
		waypoints.add(new Point(mob.getLocation().getX(), mob.getLocation().getY(), -1));
	}

	/**
	 * Checks if the queue is empty.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isEmpty() {
		return waypoints.isEmpty();
	}

	/**
	 * Gets the queue's size.
	 * 
	 * @return The queue's size.
	 */
	public int size() {
		return waypoints.size();
	}

	/**
	 * Removes the first waypoint which is only used for calculating directions.
	 * This means walking begins at the correct time.
	 */
	public void finish() {
		waypoints.removeFirst();
	}

	/**
	 * Adds a single step to the walking queue, filling in the points to the
	 * previous point in the queue if necessary.
	 * 
	 * @param x
	 *            The local x coordinate.
	 * @param y
	 *            The local y coordinate.
	 */
	public void addStep(int x, int y) {
		
		/*
		 * The RuneScape client will not send all the points in the queue. It
		 * just sends places where the direction changes.
		 * 
		 * For instance, walking from a route like this:
		 * 
		 * <code> ***** * * ***** </code>
		 * 
		 * Only the places marked with X will be sent:
		 * 
		 * <code> X***X * * X***X </code>
		 * 
		 * This code will 'fill in' these points and then add them to the queue.
		 */

		/*
		 * We need to know the previous point to fill in the path.
		 */
		if (waypoints.size() == 0) {
			/*
			 * There is no last point, reset the queue to add the player's
			 * current location.
			 */
			reset();
		}

		/*
		 * We retrieve the previous point here.
		 */
		Point last = waypoints.peekLast();

		/*
		 * We now work out the difference between the points.
		 */
		int diffX = x - last.x;
		int diffY = y - last.y;

		/*
		 * The following code is unique to Rs2-Server, as I have never seen it
		 * fixed in another server.
		 * 
		 * As you know, on RuneScape, if you move a direction, and someone logs
		 * in or teleports to that area, when they are added, they are facing
		 * the correct direction. However, on every RSPS, they face south. I
		 * have experienced this when creating a video for example, having lots
		 * of friends facing north, relogging, and then they are all south,
		 * however this code fixes it.
		 */

		int newDirection = -1;

		if (diffX != 0) { // We are moving left or right
			if (diffX > 0) { // We are moving east
				if (diffY != 0) { // We are also moving up/down
					if (diffY > 0) { // We are moving north east
						newDirection = NORTH_EAST;
					} else { // We are moving south east
						newDirection = SOUTH_EAST;
					}
				} else {
					newDirection = EAST;
				}
			} else { // We are moving west
				if (diffY != 0) { // We are also moving up/down
					if (diffY > 0) { // We are moving north west
						newDirection = NORTH_WEST;
					} else { // We are moving south west
						newDirection = SOUTH_WEST;
					}
				} else {
					newDirection = WEST;
				}
			}
		}
		if (newDirection == -1) { // We aren't moving left or right, so the
									// direction will still be at -1
			if (diffY != 0) { // We are moving up or down
				// Bear in mind we do not have to recheck for diagonals,
				// otherwise diffX would not of == 0 at the previous check
				if (diffY > 0) { // We are moving north
					newDirection = NORTH;
				} else { // We are moving south
					newDirection = SOUTH;
				}
			}
		}
		if (newDirection != -1) {
			mob.setDirection(newDirection);
		}

		/*
		 * And calculate the number of steps there is between the points.
		 */
		int max = Math.max(Math.abs(diffX), Math.abs(diffY));
		for (int i = 0; i < max; i++) {
			/*
			 * Keep lowering the differences until they reach 0 - when our route
			 * will be complete.
			 */
			if (diffX < 0) {
				diffX++;
			} else if (diffX > 0) {
				diffX--;
			}
			if (diffY < 0) {
				diffY++;
			} else if (diffY > 0) {
				diffY--;
			}

			/*
			 * Add this next step to the queue.
			 */
			addStepInternal(x - diffX, y - diffY);
		}
	}

	/**
	 * Adds a single step to the queue internally without counting gaps. This
	 * method is unsafe if used incorrectly so it is private to protect the
	 * queue.
	 * 
	 * @param x
	 *            The x coordinate of the step.
	 * @param y
	 *            The y coordinate of the step.
	 */
	private void addStepInternal(int x, int y) {
		/*
		 * Check if we are going to violate capacity restrictions.
		 */
		if (waypoints.size() >= MAXIMUM_SIZE) {
			/*
			 * If we are we'll just skip the point. The player won't get a
			 * complete route by large routes are not probable and are more
			 * likely sent by bots to crash servers.
			 */
			return;
		}

		/*
		 * We retrieve the previous point (this is to calculate the direction to
		 * move in).
		 */
		Point last = waypoints.peekLast();

		/*
		 * Now we work out the difference between these steps.
		 */
		int diffX = x - last.x;
		int diffY = y - last.y;
		/*
		 * And calculate the direction between them.
		 */
		int dir = DirectionUtils.direction(diffX, diffY);
		/*
		 * Check if we actually move anywhere.
		 */
		if (dir > -1) {
			/*
			 * We now have the information to add a point to the queue! We
			 * create the actual point object and add it.
			 */
			waypoints.add(new Point(x, y, dir));

		}
	}

	/**
	 * Processes the next player's movement.
	 */
	public void processNextMovement() {

		/*
		 * Store the teleporting flag.
		 */
		boolean teleporting = mob.hasTeleportTarget();

		/*
		 * The points which we are walking to.
		 */
		Point walkPoint = null, runPoint = null;

		/*
		 * Checks if the player is teleporting i.e. not walking.
		 */
		if (teleporting) {
			/*
			 * Reset the walking queue as it will no longer apply after the
			 * teleport.
			 */
			reset();

			/*
			 * Set the 'teleporting' flag which indicates the player is
			 * teleporting.
			 */
			mob.setTeleporting(true);

			/*
			 * Sets the player's new location to be their target.
			 */
			mob.setLocation(mob.getTeleportTarget());
			mob.setLastLocation(mob.getLocation());
			/*
			 * Resets the teleport target.
			 */
			mob.resetTeleportTarget();
		} else {
			/*
			 * If the player isn't teleporting, they are walking (or standing
			 * still). We get the next direction of movement here.
			 */
			walkPoint = getNextPoint();

			/*
			 * Technically we should check for running here.
			 */
			if ((runToggled || runQueue) && mob.getWalkingQueue().getEnergy() > 0) {
				runPoint = getNextPoint();
			}

			/*
			 * Now set the sprites.
			 */
			int walkDir = walkPoint == null ? -1 : walkPoint.dir;
			int runDir = runPoint == null ? -1 : runPoint.dir;
			mob.getSprites().setSprites(walkDir, runDir);
		}

		/*
		 * Check for a map region change, and if the map region has changed, set
		 * the appropriate flag so the new map region packet is sent.
		 */
		boolean changed = false;
		if ((mob.getLastKnownRegion().getRegionX() - mob.getLocation().getRegionX()) >= 4
				|| (mob.getLastKnownRegion().getRegionX() - mob.getLocation().getRegionX()) <= -4) {
			changed = true;
		}
		if ((mob.getLastKnownRegion().getRegionY() - mob.getLocation().getRegionY()) >= 4
				|| (mob.getLastKnownRegion().getRegionY() - mob.getLocation().getRegionY()) <= -4) {
			changed = true;
		}
		if (changed) {
			/*
			 * Set the map region changing flag so the new map region packet is
			 * sent upon the next update.
			 */
			mob.setMapRegionChanging(true);
		}

	}

	/**
	 * Gets the next point of movement.
	 * 
	 * @return The next point.
	 */
	private Point getNextPoint() {
		/*
		 * Take the next point from the queue.
		 */
		Point p = waypoints.poll();
		/*
		 * Checks if there are no more points.
		 */
		if (p == null || p.dir == -1) {
			/*
			 * Return <code>null</code> indicating no movement happened.
			 */
			return null;
		} else {
			/*
			 * Set the player's new location.
			 */
			int diffX = Constants.DIRECTION_DELTA_X[p.dir];
			int diffY = Constants.DIRECTION_DELTA_Y[p.dir];
			mob.setLocation(mob.getLocation().transform(diffX, diffY, 0));
			/*
			 * And return the direction.
			 */
			return p;
		}
	}

	public Deque<Point> getWaypoints() {
		return waypoints;
	}
}
