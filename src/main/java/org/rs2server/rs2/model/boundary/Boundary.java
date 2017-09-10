package org.rs2server.rs2.model.boundary;

import org.rs2server.rs2.model.Location;

/**
 * The bounds for setting certain areas such as pk zones
 * @author Sir Sean
 *
 */
public class Boundary {

	/**
	 * The boundary name
	 */
	private final String name;
	
	/**
	 * The bottom left location
	 */
	private final Location bottomLeft;
	
	/**
	 * The top right location
	 */
	private final Location topRight;
	
	/**
	 * Sets the boundaries in the constructor
	 * @param buttonLeft The bottom left coordinates
	 * @param topRight The top right coordinates
	 */
	public Boundary(String name, Location bottonLeft, Location topRight) {
		this.name = name;
		this.bottomLeft = bottonLeft;
		this.topRight = topRight;
	}
	
	public static Boundary create(String name, Location bottomLeft, Location topRight) {
		return new Boundary(name, bottomLeft, topRight);
	}

	/**
	 * The boundary name
	 * @return The boundary name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the button left location
	 * @return the bottom left
	 */
	public Location getBottomLeft() {
		return bottomLeft;
	}

	/**
	 * Gets the top left location
	 * @return the top right
	 */
	public Location getTopRight() {
		return topRight;
	}
	
	@Override
	public String toString() {
		return "[name=" + name + " bottomLeft" + bottomLeft + " topRight" + topRight + "]";		
	}

	public int getCenterX() {
		return bottomLeft.getX() + ((topRight.getX() - bottomLeft.getX()) / 2);
	}
	
	public int getCenterY() {
		return bottomLeft.getY() + ((topRight.getY() - bottomLeft.getY()) / 2);
	}

	public Location getCenterPoint() {
		return Location.create(getCenterX(), getCenterY(), topRight.getPlane());
	}

}
