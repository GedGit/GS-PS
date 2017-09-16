package org.rs2server.rs2.model;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.EngineService;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a game object in the world with a location.
 * @author Graham Edgecombe 
 *
 */
public abstract class Entity {
	
    private int dynamicIndex = -1, staticIndex = -1;
    public int getIndex() {
        return dynamicIndex;
    }

    public void setIndex(int index) {
        this.dynamicIndex = index;
    }

    public void setStaticIndex(int index) {
        this.staticIndex = index;
    }

    public int getStaticIndex() {
        return staticIndex;
    }
    
	/**
	 * The default, i.e. spawn, location.
	 */
	public static final Location DEFAULT_LOCATION = Location.create(1643, 3672, 0);
	
	/**
	 * The current location.
	 */
	private Location location;
	
	private Map<Integer, Tickable> ticks = new HashMap<Integer, Tickable>();
	private Attributes attributes = new Attributes();

	protected final EngineService engineService;

	public Entity() {
		this.engineService = Server.getInjector().getInstance(EngineService.class); 
	}

	public void submitTimer(int id, int ticks) {
		attributes.setAttribute(id, true);
		World.getWorld().submit(new Tickable(ticks) {
			@Override
			public void execute() {
				attributes.setAttribute(id, false);
				stop();
			}
		});
	}
	
	public boolean hasTime(int id) {
		boolean exists = attr(id);
		return exists;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T attr(int key) {
		return (T) attributes.getAttribute(key);
	}

	public void attr(int key, Object value) {
		attributes.setAttribute(key, value);
	}
	
	public void submitTick(Tickable tickable) {
		while (!submitTick(1000 + Misc.random(10000), tickable)); // TODO A better random generator
	}
	
	public boolean submitTick(int id, Tickable tickable) {
		if (ticks.get(id) != null)
			return false;

		engineService.offerTask(() -> ticks.put(id, tickable));
		return true;
	}
	
	public Tickable getTick(int identifier) {
		return ticks.get(identifier);
	}

	public boolean hasTick(int identifier) {
		return ticks.containsKey(identifier);
	}

	public void processTicks() {
		if (ticks.isEmpty())
			return;
		Iterator<Entry<Integer, Tickable>> it = ticks.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Tickable> entry = it.next();
			entry.getValue().cycle();
			if (!entry.getValue().isRunning()) {
				it.remove();
			}
		}
	}

	/**
	 * Sets the current location, and nothing more.
	 * @param x The x location.
	 * @param y The y location.
	 * @param z The z location.
	 * @see #setLocation(Location) setLocation(Location) which updates the entity upon changing its location.
	 */
	public void setLocation(int x, int y, int z) {
		this.location = Location.create(x, y, z);
	}
	
	/**
	 * Sets the current location, and updates the region.
	 * @param location The current location.
	 */
	public void setLocation(Location location) {
		this.location = location;
		Region newRegion = World.getWorld().getRegionManager().getRegionByLocation(location);
		if(newRegion != getRegion()) {
			if(getRegion() != null) {
				removeFromRegion(getRegion());
			}
			setRegion(newRegion);
			addToRegion(getRegion());
		}
	}
	
	/**
	 * Gets the current location.
	 * @return The current location.
	 */
	public Location getLocation() {
		return location;
	}
	
	/**
	 * The direction this entity is facing.
	 * 7=SouthEast 6=South 5=SouthWest 4=East 3=West 2=NorthEast 1=North 0=NorthWest //wrong LOL.. w0t
	 * 0 = NorthWest 1 = North 2 = Northeast 3 = West 4 = East 5 = SouthWest 6 = South
	 */
	private int direction = WalkingQueue.SOUTH;
	
	/**
	 * Gets the direction this entity is facing.
	 * @return The direction this entity is facing.
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * Sets the direction that the entity is facing.
	 * @param direction The direction to set.
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	/**
	 * The current region.
	 */
	private Region currentRegion;
	
	/**
	 * Gets the current region.
	 * @return The current region.
	 */
	public Region getRegion() {
		return currentRegion;
	}

	/**
	 * Sets the current region.
	 * @param region The region to set.
	 */
	public void setRegion(Region region) {
		this.currentRegion = region;
	}
	
	/**
	 * Plays graphics.
	 * @param graphic The graphics.
	 */
	public void playProjectile(Projectile projectile) {
		if (this.isPlayer()) {
			Player player = (Player) this;
			if (player.isMultiplayerDisabled()) {
				player.getActionSender().sendProjectile(projectile.getStart(), projectile.getFinish(), projectile.getId(), projectile.getDelay(), projectile.getAngle(), projectile.getSpeed(), projectile.getStartHeight(), projectile.getEndHeight(), projectile.getSlope(), projectile.getRadius(), projectile.getLockon());
			} else {
				playGlobalProjectile(projectile);
			}
		} else if (projectile.getTarget() != null && projectile.getTarget().isPlayer()) {
			Player player = (Player) projectile.getTarget();
			if (player.isMultiplayerDisabled()) {
				player.getActionSender().sendProjectile(projectile.getStart(), projectile.getFinish(), projectile.getId(), projectile.getDelay(), projectile.getAngle(), projectile.getSpeed(), projectile.getStartHeight(), projectile.getEndHeight(), projectile.getSlope(), projectile.getRadius(), projectile.getLockon());
			} else {
				playGlobalProjectile(projectile);
			}
		} else {
			playGlobalProjectile(projectile);
		}
	}

    public void playGlobalProjectile(Projectile projectile) {
        for (Region r : currentRegion.getSurroundingRegions()) {
            for (Player p : r.getPlayers()) {
                if (p.getLocation().isWithinDistance(this.getLocation())) {
                    p.getActionSender().sendProjectile(projectile.getStart(), projectile.getFinish(), projectile.getId(), projectile.getDelay(), projectile.getAngle(), projectile.getSpeed(), projectile.getStartHeight(), projectile.getEndHeight(), projectile.getSlope(), projectile.getRadius(), projectile.getLockon());
                }
            }
        }
    }
	
	public int getX() {
		return location.getX();
	}
	
	public int getY() {
		return location.getY();
	}
	
	public int getPlane() {
		return location.getPlane();
	}

	/**
	 * Gets the width of the entity.
	 * @return The width of the entity.
	 */
	public abstract int getWidth();
	
	/**
	 * Gets the width of the entity.
	 * @return The width of the entity.
	 */
	public abstract int getHeight();

	/**
	 * Gets the centre location of the entity.
	 * @return The centre location of the entity.
	 */
	public abstract Location getCentreLocation();
	
	/**
	 * Is this entity a player.
	 */
	public abstract boolean isPlayer();
	
	/**
	 * Is this entity an NPC.
	 */
	public abstract boolean isNPC();
	
	/**
	 * Is this entity an NPC.
	 */
	public abstract boolean isObject();

	/**
	 * Gets the client-side index of an entity.
	 * @return The client-side index.
	 */
	public abstract int getClientIndex();
	
	/**
	 * Removes this entity from the specified region.
	 * @param region The region.
	 */
	public abstract void removeFromRegion(Region region);
	
	/**
	 * Adds this entity to the specified region.
	 * @param region The region.
	 */
	public abstract void addToRegion(Region region);



}
