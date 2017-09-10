package org.rs2server.rs2.model.bit.component;

/**
 * @author twelve
 */
public final class Access {

	private final int root;
	private final int child;
	private final NumberRange range;
	private final int value;

	Access(int root, int child, NumberRange range, BitPackedValue... values) {
		this.root = root;
		this.child = child;
		this.range = range;

		int flags = 0;
		for (BitPackedValue bit : values) {
			flags |= bit.getValue();
		}
		this.value = flags;
	}

	public static Access of(int root, int child, BitPackedValue... values) {
		return of(root, child, NumberRange.of(-1, -1), values);
	}

	public static Access of(int root, int child, NumberRange range, BitPackedValue... values) {
		return new Access(root, child, range, values);
	}

	public int getRoot() {
		return root;
	}

	public int getChild() {
		return child;
	}

	public NumberRange getRange() {
		return range;
	}

	public int getValue() {
		return value;
	}
}
