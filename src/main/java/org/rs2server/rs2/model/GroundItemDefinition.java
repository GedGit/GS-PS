package org.rs2server.rs2.model;

import org.rs2server.rs2.model.region.Region;

/**
 * Created by Tim on 10/29/2015.
 */
public class GroundItemDefinition extends Item {

    /**
     * The location of this item.
     */
    private final Location location;

    /**
     * The region of the item location.
     */
    private final Region[] regions;

    /**
     * The name of the ground item owner.
     */
    private String owner;

    /**
     * A ground item have two stages, each stage is 2 minutes.
     * Thats 240 seconds.
     */
    private int time = 240;

    private boolean respawns;

    private boolean global;

    /**
     * Creates a new ground item.
     *
     * @param owner    The ground item owner.
     * @param location The location of the ground item.
     * @param id       The id of the item.
     * @param amount   The amount of the item.
     */
    public GroundItemDefinition(String owner, Location location, int id, int amount) {
        super(id, amount);
        this.location = location;
        this.owner = owner;
        this.regions = World.getWorld().getRegionManager().getSurroundingRegions(location);
    }

    /**
     * Creates a new ground item.
     *
     * @param owner    The ground item owner.
     * @param location The location of the ground item.
     * @param id       The id of the item.
     * @param fletchAmount   The amount of the item.
     */
    public GroundItemDefinition(String owner, Location location, Item item) {
        super(item.getId(), item.getCount());
        this.location = location;
        this.owner = owner;
        this.regions = World.getWorld().getRegionManager().getSurroundingRegions(location);
    }



	/**
     * Gets the ground item location.
     *
     * @return The ground item's location.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the name of the item owner.
     *
     * @return The item owner's name.
     */
    public String getOwner() {
        return owner;
    }

	public void setOwner(String owner) {
		this.owner = owner;
	}

    /**
     * Sets the ground item time.
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * Gets the ground item time.
     *
     * @return The ground item time.
     */
    public int getTime() {
        return time;
    }

    /**
     * Decreases the ground items time.
     */
    public void decreaseTime() {
        this.time--;
    }

    /**
     * Gets the ground item region array.
     *
     * @return The regions of the ground item.
     */
    public Region[] getRegions() {
        return regions;
    }

    @Override
	public boolean respawns() {
        return respawns;
    }

    @Override
	public void setRespawns(boolean respawns) {
        this.respawns = respawns;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

}

