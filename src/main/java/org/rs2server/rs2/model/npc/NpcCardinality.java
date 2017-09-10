package org.rs2server.rs2.model.npc;

import org.rs2server.rs2.model.Vector2;

import java.util.Arrays;

/**
 * Defines the cardinalities for a GameObject.
 *
 * @author tommo
 */
public enum NpcCardinality {
	
	NORTH(1, Vector2.of(0, 1)),
	EAST(2, Vector2.of(1, 0)),
	SOUTH(0, Vector2.of(0, -1)),
	WEST(3, Vector2.of(-1, 0)),
	UNDEFINED(-1, Vector2.of(0, 0));

	private final int face;
	private final Vector2 faceVector;

	NpcCardinality(int face, final Vector2 faceVector) {
		this.face = face;
		this.faceVector = faceVector;
	}

	public int getFace() {
		return face;
	}

	/**
	 * Returns a normalized vector representing the facing cardinality.
	 * @return The normalized [0..1, 0..1] vector.
	 */
	public Vector2 getFaceVector() {
		return faceVector;
	}

	public static NpcCardinality forFace(final int face) {
		return Arrays.stream(values()).filter(e -> e.getFace() == face).findFirst().orElse(UNDEFINED);
	}
}
