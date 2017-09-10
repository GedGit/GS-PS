package org.rs2server.rs2.model;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.GroundItemService;
import org.rs2server.util.XMLController;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class ItemSpawn {

	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(ItemSpawn.class.getName());

	/**
	 * A list of item spawns.
	 */
	private static List<ItemSpawn> spawns;

	public static List<ItemSpawn> getSpawns() {
		return spawns;
	}

	/**
	 * The item.
	 */
	private Item item;

	/**
	 * The location of the item.
	 */
	private Location location;

	private static GroundItemService groundItemService;

	private ItemSpawn(Item item, Location location) {
		this.item = item;
		this.location = location;
	}

	/**
	 * Loads the item spawn list from xml, and spawns all the item's.
	 * 
	 * @throws IOException
	 */
	public static void init() throws IOException {
		logger.info("Loading item spawns...");
		File file = new File("data/items/itemSpawns.xml");
		groundItemService = Server.getInjector().getInstance(GroundItemService.class);
		if (file.exists()) {
			spawns = XMLController.readXML(file);
			for (ItemSpawn spawn : spawns)
				groundItemService.createGroundItem(null,
						new GroundItemService.SpawnedGroundItem(spawn.getItem(), spawn.getLocation(), "", true));
			logger.info("Loaded " + spawns.size() + " item spawns.");
		} else
			logger.info("Item spawns not found!");
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param groundItem
	 *            the groundItem to set
	 */
	public void setGroundItem(GroundItemDefinition groundItem) {
	}

}
