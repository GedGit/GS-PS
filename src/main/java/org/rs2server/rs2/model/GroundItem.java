package org.rs2server.rs2.model;

import org.rs2server.rs2.model.region.Region;

/**
 * An instance of a ground item (items shown on
 * the floor when they are dropped).
 * @author Michael
 *
 */

public class GroundItem {

	/**
	 * The name of the controller for the current item.
	 */
	private String controllerName = "";

	/**
	 * Sets the name of the controller for the current item.
	 * @param controllerName The name of the controller to set the current item.
	 */
	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}
	
	/**
	 * Checks if this item is owned by someone.
	 * @param name The persons name.
	 * @return If the item is owned by them.
	 */
	public boolean isOwnedBy(String name) {
		return isGlobal() || controllerName.equalsIgnoreCase(name);
	}
	
	/**
	 * Gets the name of the controller for the current item.
	 * @return The name of the controller for the current item.
	 */
	public String getControllerName() {
		return controllerName;
	}
	/**
	 * The current item.
	 */
	private Item item;
	
	/**
	 * Gets the current item.
	 * @return The current item.
	 */
	public Item getItem() {
		return item;
	}
	
	/**
	 * Sets the current item.
	 * @param item The item to set.
	 * @return The current item.
	 */
	public Item setItem(Item item) {
		return this.item = item;
	}

	/**
	 * The location for the current item.
	 */	
	private Location location;
	
	/**
	 * Gets the items location.
	 * @return The items location.
	 */
	public Location getLocation() {
		return location;
	}
	
	/**
	 * The region this item is in.
	 */
	private Region region;
	
	/**
	 * Gets the items region.
	 * @return The items region.
	 */
	public Region getRegion() {
		return region;
	}

	/**
	 * The global boolean.
	 */
	private boolean global;

	/**
	 * Sets the item's global value.
	 * @param global The global value to set.
	 */
	public void setGlobal(boolean global) {
		this.global = global;
	}
	
	/**
	 * Gets the items global value.
	 * @return The items global value.
	 */
	public boolean isGlobal() {
		return global;
	}
	
	/**
	 * The registered boolean.
	 */
	private boolean registered = true;

	/**
	 * Sets the item's registered value.
	 * @param registered The registered value to set.
	 */
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}
	
	/**
	 * Gets the items registered value.
	 * @return The items registered value.
	 */
	public boolean isRegistered() {
		return registered;
	}

	public GroundItem(String controllerName, Item item, Location location) {
		this.controllerName = controllerName;
		this.item = item;
		this.location = location;
		this.region = World.getWorld().getRegionManager().getRegionByLocation(this.location);
		this.registered = true;
	}
	
	public GroundItem(String controllerName, boolean global, Item item, Location location) {
		this.controllerName = controllerName;
		this.item = item;
		this.location = location;
		this.region = World.getWorld().getRegionManager().getRegionByLocation(this.location);
		this.registered = false;
		this.global = global;
	}
	
	public GroundItem(String controllerName, Item item, Location location, boolean global) {
		this.controllerName = controllerName;
		this.item = item;
		this.location = location;
		this.region = World.getWorld().getRegionManager().getRegionByLocation(this.location);
		this.registered = true;
		this.global = global;
	}
	
	private boolean pvpDrop;

	public boolean isPvPDrop() {
		return pvpDrop;
	}
	
	public void setPvPDrop(boolean b) {
		this.pvpDrop = b;
	}
	
	private boolean respawns;
	
	public boolean respawns() {
		return respawns;
	}
	
	public void setRespawn(boolean b) {
		this.respawns = b;
	}

	private boolean spawned;
	public void setSpawned(boolean b) {
		this.spawned = b;
	}
	
	public boolean spawned() {
		return spawned;
	}
	
}
