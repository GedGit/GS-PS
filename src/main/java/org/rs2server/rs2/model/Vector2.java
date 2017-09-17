package org.rs2server.rs2.model;

/**
 * A 2d vector.
 *
 * @author tommo
 */
public class Vector2 {

	private double x;
	private double y;

	private Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public static Vector2 of(double x, double y) {
		return new Vector2(x, y);
	}

	public static Vector2 of(Location location) {
		return of(location.getX(), location.getY());
	}

	/**
	 * @return the angle (argument) of the vector in polar coordinates in the
	 *         range [-pi/2, pi/2]
	 */
	public double getTheta() {
		return Math.atan2(y, x);
	}

	public Vector2 set(double x, double y) {
		return new Vector2(x, y);
	}

	public Vector2 plus(Vector2 vector) {
		return new Vector2(x + vector.x, y + vector.y);
	}

	public Vector2 plus(double x, double y) {
		return new Vector2(this.x + x, this.y + y);
	}

	public Vector2 minus(double x, double y) {
		return new Vector2(this.x - x, this.y - y);
	}

	public Vector2 minus(Vector2 vector) {
		return new Vector2(x - vector.x, y - vector.y);
	}

	public boolean equals(Vector2 other) {
		return x == other.x && y == other.y;
	}

	public Vector2 mul(double scalar) {
		return new Vector2(scalar * x, scalar * y);
	}

	public double dotProduct(Vector2 vector) {
		return x * vector.x + y * vector.y;
	}

	public double crossProduct(Vector2 vector) {
		return x * vector.y - y * vector.x;
	}

	public double componentProduct() {
		return x * y;
	}

	/**
	 * Returns a new vector with the same direction as the vector but with
	 * length 1, except in the case of zero vectors, which return a copy of
	 * themselves.
	 */
	public Vector2 unitVector() {
		if (length() != 0) {
			return new Vector2(x / length(), y / length());
		}
		return new Vector2(0,0);
	}

	/**
	 * @return the radius (length, modulus) of the vector in polar coordinates
	 */
	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	/** @return Standard string representation of a vector: "<x, y>" */
	public String toString() {
		return "<" + x + ", " + y + ">";
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
}
