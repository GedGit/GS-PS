package org.rs2server.rs2.model.map.path;

/**
 * A premature path termination condition.
 *
 * This is used for example when pathing towards objects, and determining when the object is in reach
 * before the path has finished.
 *
 * @author tommo
 */
public interface PathPrecondition {

	/**
	 * Checks whether the implementing precondition has been met.
	 * @param currentX The current x coordinate.
	 * @param currentY The current y coordinate.
	 * @param destinationX The destination x coordinate.
	 * @param destinationY The destination y coordinate.
	 * @return true if the target is within reaching distance and the path searching should be prematurely terminated.
	 */
	boolean targetReached(int currentX, int currentY, int destinationX, int destinationY);
}
