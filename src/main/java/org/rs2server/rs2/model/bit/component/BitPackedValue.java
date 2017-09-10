package org.rs2server.rs2.model.bit.component;

/**
 * @author twelve
 */
public final class BitPackedValue {

	private final int value;

	public BitPackedValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static BitPackedValue of(int set, int bit) {
		return new BitPackedValue(set << bit);
	}
}
