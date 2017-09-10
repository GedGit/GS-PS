package org.rs2server.rs2.model;

import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.util.XMLController;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Manages all of the in-game objects.
 * 
 * @author Graham Edgecombe
 *
 */
public class ObjectManager {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(ObjectManager.class.getName());

	/**
	 * The number of definitions loaded.
	 */
	private int definitionCount = 0;

	/**
	 * The count of objects loaded.
	 */
	@SuppressWarnings("unused")
	private int objectCount = 0;

	public void load() throws IOException {
		logger.info("Loaded " + definitionCount + " object definitions.");
		int customObjectCount = 0;
		List<GameObject> customObjects = XMLController.readXML(new File("./data/customObjects.xml"));
		for (GameObject obj : customObjects) {
			if (obj == null)
				continue;

			GameObject object = new GameObject(obj.getLocation(), obj.getId(), obj.getType(), obj.getDirection(),
					obj.isLoadedInLandscape());

			World.getWorld().register(object);

			if (object.getId() != 0)
				RegionClipping.addClipping(object);

			customObjectCount++;
		}

		logger.info("Loaded " + customObjectCount + " custom objects.");
	}

	/**
	 * Find a better way to handle these.
	 * 
	 * @param object
	 *            the game object.
	 * @return if removing.
	 */
	public static boolean handleObjectRemoval(GameObject object) {
		/** Home area mainly **/
		if ((object.getId() == 11731 && object.getLocation().getX() == 1639)
				|| (object.getId() == 1276 && object.getLocation().getX() == 3090)
				|| (object.getId() == 8714 && object.getLocation().getX() == 1645)
				|| (object.getId() == 3633 && object.getLocation().getX() == 1646)
				|| (object.getId() == 28823 && object.getLocation().getX() == 1633)
				|| (object.getId() == 11734 && object.getLocation().getX() == 1626)
				|| (object.getId() == 11729 && object.getLocation().getX() == 1626)
				|| (object.getId() == 635 && object.getLocation().getX() == 1648)
				|| (object.getId() == 11731 && object.getLocation().getX() == 1634)
				|| (object.getId() == 29495 && object.getLocation().getX() == 3041) // dzone fairy ring
				|| (object.getId() == 29496 && object.getLocation().getX() == 3041) // dzone fairy ring
				|| (object.getId() == 11730 && object.getLocation().getX() == 1638)
				|| (object.getId() == 27538 && object.getLocation().getX() == 1635)
				|| (object.getId() == 12546 && object.getLocation().getX() == 1637)
				|| (object.getId() == 27364 && object.getLocation().getX() == 1631)
				|| (object.getId() == 3641 && object.getLocation().getX() == 1650)
				|| (object.getId() == 12548 && object.getLocation().getX() != 1635) || object.getId() == 12547
				|| object.getId() == 27520 || object.getId() == 3045 || object.getId() == 3205
				|| object.getId() == 16260 || object.getId() == 16261 || object.getId() == 16262
				|| object.getId() == 16263 || object.getId() == 27364 || object.getId() == 16265
				|| object.getId() == 26873 || object.getId() == 16270 || object.getId() == 16268
				|| object.getId() == 16283 || object.getId() == 16269 || object.getId() == 16267
				|| object.getId() == 29028 || object.getId() == 29047 || object.getId() == 16266
				|| object.getId() == 29320 || object.getId() == 29060) {
			World.getWorld().unregister(object, true);
			RegionClipping.removeClipping(object);
			return true;
		}
		return false;
	}
}