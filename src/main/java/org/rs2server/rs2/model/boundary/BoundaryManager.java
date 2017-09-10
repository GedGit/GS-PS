package org.rs2server.rs2.model.boundary;

import org.rs2server.rs2.model.Location;
import org.rs2server.util.XMLController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * Loads the boundary information from a XML file
 * 
 * @author Sir Sean
 * 
 */
public class BoundaryManager {

	/**
	 * The map of the all boundar's in the XML file Before you cry, no i'm not
	 * making it a constant, looks ugly
	 */
	public static List<Boundary> boundaries = new ArrayList<Boundary>();
	
	public static void addBoundary(Boundary boundary) {
		if(boundaries.contains(boundary)) {
			return;
		}
		boundaries.add(boundary);
	}

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(BoundaryManager.class
			.getName());

	/**
	 * Adds all information to the boundary map
	 * 
	 * @throws IOException
	 */
	public static void init() throws IOException {
		logger.info("Loading boundary definitions...");
		File file = new File("data/boundaries.xml");
		if (file.exists()) {
			boundaries = XMLController.readXML(file);
			logger.info("Loaded " + boundaries.size()
					+ " boundary definitions.");
		} else {
			logger.info("Boundary definitions not found.");
		}
	}

	/**
	 * If a location is within a boundary.
	 * 
	 * @param location The location.
	 * @param name The name of the boundary.
	 * @return If the location is within a boundary.
	 */
	public static boolean isWithinBoundary(Location location, String name) {
		for (Boundary boundary : boundaryForName(name)) {
			if (isWithinBoundary(location, boundary)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If a location is within a boundary.
	 *
	 * @param location The location.
	 * @param boundary The boundary.
	 * @return If the location is within a boundary.
	 */
	public static boolean isWithinBoundary(Location location, Boundary boundary) {
		if (boundary instanceof IrregularBoundary) {
			return ((IrregularBoundary) boundary).containsPoint(location);
		} else if (location.getPlane() == boundary.getBottomLeft().getPlane()
				&& location.getPlane() == boundary.getTopRight().getPlane()) {
			if (location.getX() >= boundary.getBottomLeft().getX()
					&& location.getX() <= boundary.getTopRight().getX()
					&& location.getY() >= boundary.getBottomLeft().getY()
					&& location.getY() <= boundary.getTopRight().getY()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If a location is within a boundary.
	 * 
	 * @param location
	 *            The location.
	 * @param boundary
	 *            The boundary.
	 * @return If the location is within a boundary.
	 */
	public static boolean isWithinBoundaryNoZ(Location location, String name) {
		for (Boundary boundary : boundaryForName(name)) {
			if (boundary instanceof IrregularBoundary)
				return ((IrregularBoundary) boundary).containsPoint(location);
			else if (location.getX() >= boundary.getBottomLeft().getX()
					&& location.getX() <= boundary.getTopRight().getX()
					&& location.getY() >= boundary.getBottomLeft().getY()
					&& location.getY() <= boundary.getTopRight().getY()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If a location is within a boundary.
	 * 
	 * @param location
	 *            The location.
	 * @param boundary
	 *            The boundary.
	 * @return If the location is within a boundary.
	 */
	public static boolean isWithinBoundaryNoZ(Location location, Boundary boundary) {
		if (boundary instanceof IrregularBoundary) { 
			return ((IrregularBoundary) boundary).containsPoint(location);
		} else if (location.getX() >= boundary.getBottomLeft().getX()
						&& location.getX() <= boundary.getTopRight().getX()
						&& location.getY() >= boundary.getBottomLeft().getY()
						&& location.getY() <= boundary.getTopRight().getY()) {
					return true;
		}
		return false;
	}

	/**
	 * Gets a boundary by its name.
	 * 
	 * @param name
	 *            The name.
	 * @return The boundary.
	 */
	public static List<Boundary> boundaryForName(String name) {
		List<Boundary> bounds = new ArrayList<Boundary>();
		for (Boundary boundary : boundaries) {
			if (boundary.getName().toLowerCase().contains(name.toLowerCase())) {
				bounds.add(boundary);
			}
		}
		return bounds;
	}
}