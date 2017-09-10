package org.rs2server.rs2.model.boundary;

import org.rs2server.rs2.model.Location;

/**
 * A basic, rectangular, serializable area.
 *
 * @author tommo
 */
public class Area {

	private int bottomLeftX;
	private int bottomLeftY;
	private int topRightX;
	private int topRightY;
	private int z;

	public Area() {

	}

	public Area(int bottomLeftX, int bottomLeftY, int topRightX, int topRightY) {
		this(bottomLeftX, bottomLeftY, topRightX, topRightY, 0);
	}

	public Area(int bottomLeftX, int bottomLeftY, int topRightX, int topRightY, int z) {
		this.bottomLeftX = bottomLeftX;
		this.bottomLeftY = bottomLeftY;
		this.topRightX = topRightX;
		this.topRightY = topRightY;
		this.z = z;
	}

	public static Area create(int x, int y) {
		return new Area(x, y, x, y);
	}

	public static Area create(int bottomLeftX, int bottomLeftY, int topRightX, int topRightY) {
		return new Area(bottomLeftX, bottomLeftY, topRightX, topRightY);
	}

	public boolean contains(Location location) {
		return location.getPlane() == z
				&& location.getX() >= bottomLeftX && location.getX() <= topRightX
				&& location.getY() >= bottomLeftY && location.getY() <= topRightY;
	}

	public int getBottomLeftX() {
		return bottomLeftX;
	}

	public void setBottomLeftX(int bottomLeftX) {
		this.bottomLeftX = bottomLeftX;
	}

	public int getBottomLeftY() {
		return bottomLeftY;
	}

	public void setBottomLeftY(int bottomLeftY) {
		this.bottomLeftY = bottomLeftY;
	}

	public int getTopRightX() {
		return topRightX;
	}

	public void setTopRightX(int topRightX) {
		this.topRightX = topRightX;
	}

	public int getTopRightY() {
		return topRightY;
	}

	public void setTopRightY(int topRightY) {
		this.topRightY = topRightY;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
}
