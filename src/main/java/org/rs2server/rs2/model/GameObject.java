package org.rs2server.rs2.model;

import org.rs2server.cache.format.CacheObjectDefinition;
import org.rs2server.rs2.model.region.Region;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single game object.
 * @author Graham Edgecombe
 *
 */
public class GameObject extends Entity {

	/**
	 * The definition.
	 */
	private CacheObjectDefinition definition;

	/**
	 * The object id.
	 */
	private int id;

	/**
	 * The object's spawn location.
	 */
	private Location spawnLocation;

	/**
	 * The type.
	 */
	private int type;

	/**
	 * The loaded in landscape flag.
	 */
	private boolean loadedInLandscape;

	/**
	 * The maximum amount of health this object has (for trees).
	 */
	private int maxHealth = 0;

	/**
	 * The current health this object has (for trees).
	 */
	private int currentHealth = 0;

	/**
	 * The permanent attributes map. Items set here are only removed when told to.
	 */
	protected Map<String, Object> attributes = new HashMap<String, Object>();

	/**
	 * Creates the game object.
	 * @param definition The definition.
	 * @param location The location.
	 * @param type The type.
	 * @param rotation The rotation.
	 */
	public GameObject(Location location, int id, int type, int direction, boolean loadedInLandscape) {
		super();
		if(id != -1) {
			this.definition = CacheObjectDefinition.forID(id);
		}
		this.id = id;
		this.spawnLocation = location;
		this.type = type;
		this.setDirection(direction);
		this.loadedInLandscape = loadedInLandscape;
	}

	public int getOpposite() {
		switch(id) {
		case 11772:
		case 11774:
		case 11776:
			return id + 1;
		case 7122:
		case 7123:
		case 11773:
		case 11775:
			return id - 1;
		case 7129:
			return 7141;
		case 11778:
			return 11780;
		case 11780:
			return 11778;
		case 7141:
			return 7129;
		default:
			return -1;
		}
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the definition.
	 * @return The definition.
	 */
	public CacheObjectDefinition getDefinition() {
		return definition;
	}

	/**
	 * Gets the type.
	 * @return The type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the loadedInLandscape
	 */
	public boolean isLoadedInLandscape() {
		return loadedInLandscape;
	}

	public void setLoadedInLandscape(boolean loadedInLandscape) {
		this.loadedInLandscape = loadedInLandscape;
	}

	/**
	 * @return the maxHealth
	 */
	public int getMaxHealth() {
		return maxHealth;
	}

	/**
	 * @param maxHealth the maxHealth to set
	 */
	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
		this.currentHealth = maxHealth;
	}

	/**
	 * @return the currentHealth
	 */
	public int getCurrentHealth() {
		return currentHealth;
	}

	/**
	 * @param currentHealth the currentHealth to set
	 */
	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}

	/**
	 * @param currentHealth the currentHealth to set
	 */
	public void decreaseCurrentHealth(int amount) {
		this.currentHealth -= amount;
	}

	@Override
	public Location getCentreLocation() {
		return Location.create(getLocation().getX() + (getWidth() / 2), getLocation().getY() + (getHeight() / 2), getLocation().getPlane());
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	@Override
	public int getClientIndex() {
		return 0;
	}

	@Override
	public int getHeight() {
		if(definition == null) {
			return 1;
		}
		return definition.getSizeY();
	}

	@Override
	public int getWidth() {
		if(definition == null) {
			return 1;
		}
		return definition.getSizeX();
	}

	@Override
	public boolean isNPC() {
		return false;
	}

	@Override
	public boolean isObject() {
		return true;
	}

	@Override
	public boolean isPlayer() {
		return false;
	}

	@Override
	public void addToRegion(Region region) {
		region.addObject(this);
	}

	@Override
	public void removeFromRegion(Region region) {
		region.removeObject(this);
		this.setRegion(null);
	}

	@Override
	public boolean equals(Object other) {
		if(!(other instanceof GameObject)) {
			return false;
		}
		GameObject obj = (GameObject) other;
		return obj.getLocation().equals(this.getLocation()) && obj.getId() == this.getId() && obj.getType() == this.getType();
	}

	public void setSpawnLocation(Location spawnLocation) {
		this.spawnLocation = spawnLocation;
	}

	/**
	 * Removes an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The old value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T removeAttribute(String key) {
		return (T) attributes.remove(key);
	}

	/**
	 * Removes an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The old value.
	 */
	public void removeAllAttributes() {
		if (attributes != null && attributes.size() > 0 && attributes.keySet().size() > 0) {
			attributes = new HashMap<String, Object>();
		}
	}

	/**
	 * Sets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T>   The type of the value.
	 * @param key   The key.
	 * @param value The value.
	 * @return The old value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T setAttribute(String key, T value) {
		return (T) attributes.put(key, value);
	}

	/**
	 * Gets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		return (T) attributes.get(key);
	}


	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key, Object fail) {
		if (attributes.containsKey(key))
			return (T) attributes.get(key);
		else
			return (T) fail;
	}

	/**
	 * Gets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The value.
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public boolean hasAttribute(String string) {
		return attributes.containsKey(string);
	}
}
