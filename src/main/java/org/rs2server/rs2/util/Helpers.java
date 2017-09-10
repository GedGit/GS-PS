package org.rs2server.rs2.util;

import java.util.Random;

/**
 * Statis helper methods.
 *
 * @author tommo
 */
public class Helpers {

	private static final Random random = new Random();

	/**
	 * Returns either the given value, or a fallback
	 * if the value is null.
	 * @param value The value.
	 * @param fallback The fallback value to return if the value is null.
	 */
	public static <T> T fallback(T value, T fallback) {
		return value != null ? value : fallback;
	}

	public static int randInt(int upperBound) {
		return random.nextInt(upperBound);
	}
}
