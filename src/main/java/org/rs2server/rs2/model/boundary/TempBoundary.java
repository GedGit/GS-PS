package org.rs2server.rs2.model.boundary;

import org.rs2server.rs2.model.Location;

import java.util.Arrays;
import java.util.List;

public class TempBoundary extends Boundary {

	private List<Location> points;

	private TempBoundary(final String name, final List<Location> points) {
		super(name, null, null);
		this.points = points;
	}

	public static TempBoundary create(String name, Location ... points) {
		return new TempBoundary(name, Arrays.asList(points));
	}

	/**
	 * Checks if a location is within this boundary.
	 * @param l The location to test.
	 * @return true if the location is inside the boundary, false otherwise
	 *
	 */
	public boolean containsPoint(Location l) {
		int i;
		int j;
		boolean result = false;
		for (i = 0, j = points.size() - 1; i < points.size(); j = i++) {
			if ((points.get(i).getY() > l.getY()) != (points.get(j).getY() > l.getY()) &&
					(l.getX() < (points.get(j).getX() - points.get(i).getX()) * (l.getY() - points.get(i).getY()) / (points.get(j).getY() - points.get(i).getY()) + points.get(i).getX())) {
				result = !result;
			}
		}
		return result;
	}

}
