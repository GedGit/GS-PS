package org.rs2server.rs2.model;

/**
 * Represents a moving graphic.
 * @author Michael Bull
 *
 */
public class Projectile {
		
	public static Projectile create(Location start, Location finish, int id, int delay, int angle, int speed, int startHeight, int endHeight, int lockon, int slope, int radius) {
		return new Projectile(start, finish, id, delay, angle, speed, startHeight, endHeight, lockon, slope, radius);
	}

	public static Projectile convert(int id, Location start, Location finish, int angle, int speed, int startHeight, int endHeight, int slope) {
		return new Projectile(start, finish, id, 0, angle, speed, startHeight, endHeight, 0, slope, 64);
	}
	
	/**
	 * Creates a projectile.
	 * @param id The id.
	 * @param delay The delay.
	 * @return The new graphic object.
	 */
	public static Projectile create(Location start, Location finish, int id, int delay, int angle, int speed, int startHeight, int endHeight, int slope, int radius) {
		return new Projectile(start, finish, id, delay, angle, speed, startHeight, endHeight, slope, radius);
	}
	
	/**
	 * Creates a projectile.
	 * @param id The id.
	 * @param delay The delay.
	 * @return The new graphic object.
	 */
	public static Projectile create(Location start, Mob target, int id, int delay, int angle, int speed, int startHeight, int endHeight, int slope, int radius) {
		return new Projectile(start, target, id, delay, angle, speed, startHeight, endHeight, slope, radius);
	}
	
	
	
	/**
	 * The id.
	 */
	private int id;
	
	/**
	 * The delay.
	 */
	private int delay;
	
	/**
	 * The angle.
	 */
	private int angle;

	/**
	 * The speed.
	 */
	private int speed;
	
	/**
	 * The start height.
	 */
	private int startHeight;
	
	/**
	 * The end height.
	 */
	private int endHeight;
	
	/**
	 * The lockon.
	 */
	private int lockon = -1;
	
	/**
	 * The slope.
	 */
	private int slope;

	/**
	 * The starting location
	 */
	private Location start;

	/**
	 * The finishing location
	 */
	private Location finish;
	
	/**
	 * The radius that the projectile is launched from.
	 */
	private int radius;

	private Mob target;
	
	/**
	 * Creates a graphic.
	 * @param id The id.
	 * @param delay The delay.
	 */
	private Projectile(Location start, Location finish, int id, int delay, int angle, int speed, int startHeight, int endHeight, int slope, int radius) {
		this.id = id;
		this.delay = delay;
		this.start = start;
		this.finish = finish;
		this.angle = angle;
		this.speed = speed;
		this.startHeight = startHeight;
		this.endHeight = endHeight;
		this.slope = slope;
		this.radius = radius;
	}
	
	/**
	 * Creates a graphic.
	 * @param id The id.
	 * @param delay The delay.
	 */
	private Projectile(Location start, Mob target, int id, int delay, int angle, int speed, int startHeight, int endHeight, int slope, int radius) {
		this.id = id;
		this.delay = delay;
		this.start = start;
		this.target = target;
		this.angle = angle;
		this.speed = speed;
		this.startHeight = startHeight;
		this.endHeight = endHeight;
		this.slope = slope;
		this.radius = radius;
	}
	
	private Projectile(Location start, Location finish, int id, int delay, int angle, int speed, int startHeight, int endHeight, int lockon, int slope, int radius) {
		this.id = id;
		this.delay = delay;
		this.start = start;
		this.finish = finish;
		this.angle = angle;
		this.speed = speed;
		this.startHeight = startHeight;
		this.endHeight = endHeight;
		this.lockon = lockon;
		this.slope = slope;
		this.radius = radius;
	}

	/**
	 * Gets the id.
	 * @return The id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Gets the delay.
	 * @return The delay.
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Gets the angle.
	 * @return The angle.
	 */
	public int getAngle() {
		return angle;
	}

	/**
	 * Gets the lockon.
	 * @return The lockon.
	 */
	public int getLockon() {
		return target != null ? target.getProjectileLockonIndex() : lockon;
	}

	/**
	 * Gets the slope.
	 * @return The slope.
	 */
	public int getSlope() {
		return slope;
	}
	
	/**
	 * Gets the start height.
	 * @return The start height.
	 */
	public int getStartHeight() {
		return startHeight;
	}

	/**
	 * Gets the end height.
	 * @return The end height.
	 */
	public int getEndHeight() {
		return endHeight;
	}
	
	/**
	 * Gets the speed.
	 * @return The speed.
	 */
	public int getSpeed() {
		return speed;
	}
	
	/**
	 * Gets the starting location.
	 * @return The starting location.
	 */
	public Location getStart() {
		return start;
	}

	/**
	 * Gets the finishing location.
	 * @return The finishing location.
	 */
	public Location getFinish() {
		return target != null ? target.getCentreLocation() : finish;
	}

	/**
	 * Gets the radius.
	 * @return The radius.
	 */
	public int getRadius() {
		return radius;
	}

	public Mob getTarget() {
		return target;
	}
}
