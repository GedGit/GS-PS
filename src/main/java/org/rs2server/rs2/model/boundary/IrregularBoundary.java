package org.rs2server.rs2.model.boundary;

import org.rs2server.rs2.model.Location;

import java.awt.*;

public class IrregularBoundary extends Boundary {

	public Polygon area;

	public IrregularBoundary(String name, Location bottonLeft, Location topRight) {
		super(name, bottonLeft, topRight);
	}
	public IrregularBoundary() {
		super(null, null, null);
	}

	public boolean containsPoint(Location l) {
		if (area.getBounds2D().contains(l.getX(), l.getY())) {
			return (area.contains(l.getX(), l.getY()));
		}
		return false;
	}
	
}
