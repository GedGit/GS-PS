package org.rs2server.rs2.model.map.path;

import org.rs2server.rs2.model.Vector2;

/**
 * Defines the standard NESW cardinalities with diagonals.
 *
 * @author tommo
 */
public enum Cardinality {
	NORTH(Vector2.of(0, 1)),
	EAST(Vector2.of(1, 0)),
	SOUTH(Vector2.of(0, -1)),
	WEST(Vector2.of(-1, 0)),
	NORTH_EAST(Vector2.of(1, 1)),
	SOUTH_EAST(Vector2.of(1, -1)),
	SOUTH_WEST(Vector2.of(-1, -1)),
	NORTH_WEST(Vector2.of(-1, 1)),
	UNDEFINED(Vector2.of(0, 0));

	private final Vector2 vector;

	Cardinality(final Vector2 vector) {
		this.vector = vector;
	}

	/**
	 * Returns a normalized vector representing the facing cardinality.
	 * @return The normalized [0..1, 0..1] vector.
	 */
	public Vector2 getVector() {
		return vector;
	}

}
