package org.rs2server.rs2.model.bit.component;

/**
 * Created by shawn on 19/12/15.
 */
public class NumberRange {

	private final int start;
	private final int end;

	public NumberRange(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public static NumberRange of(int start, int end) {
		return new NumberRange(start, end);
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}
}
