package org.rs2server.rs2.model.map.path;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Tile clipping flags as defined by the client.
 *
 * @author tommo
 */
public enum ClippingFlag {
	BLOCKED(0x100),
	INVALID(0x200000 | 0x40000),
	BLOCK_NORTH(0x2),
	BLOCK_EAST(0x8),
	BLOCK_SOUTH(0x20),
	BLOCK_WEST(0x80),
	BLOCK_NORTH_EAST(0x4, BLOCK_NORTH, BLOCK_EAST),
	BLOCK_NORTH_WEST(0x1, BLOCK_NORTH, BLOCK_WEST),
	BLOCK_SOUTH_EAST(0x10, BLOCK_SOUTH, BLOCK_EAST),
	BLOCK_SOUTH_WEST(0x40, BLOCK_SOUTH, BLOCK_WEST);

	private int mask;
	private List<ClippingFlag> dependencies;

	ClippingFlag(int mask, ClippingFlag... dependencies) {
		this.mask = mask;
		this.dependencies = ImmutableList.copyOf(dependencies);
	}

	public int getMask() {
		return mask;
	}

	public int and(final int mask) {
		int dependenciesMask = 0;
		for (ClippingFlag f : dependencies) {
			dependenciesMask |= (mask & f.getMask());
		}

		return (mask & BLOCKED.getMask()) | (mask & INVALID.getMask()) | (mask & dependenciesMask) | (mask & this.mask);
	}

}
