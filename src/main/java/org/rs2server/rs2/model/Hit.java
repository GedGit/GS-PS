package org.rs2server.rs2.model;

/**
 * Represents a single hit.
 * @author Graham Edgecombe
 *
 */
public final class Hit {
	
	/**
	 * Holds the different types of hit.
	 * @author Graham Edgecombe
	 *
	 */
	public enum HitType {
		
		/**
		 * The zero damage, blue hit.
		 */
		ZERO_DAMAGE_HIT(0),
		
		/**
		 * The normal, red hit.
		 */
		NORMAL_HIT(1),
		
		/**
		 * The poison, green hit.
		 */
		POISON_HIT(2),

		/**
		 * Yellow hit
		 */
		YELLOW_HIT(3),

		/**
		 * The big ass yellow hit
		 */
		BIG_ASS_YELLOW_HIT(4),

		VENOM_HIT(5);
		
		/**
		 * The id of the hit.
		 */
		private int id;
		
		/**
		 * Creates the hit type.
		 * @param id The id of the hit.
		 */
		private HitType(int id) {
			this.id = id;
		}

		/**
		 * Gets the id of the hit.
		 * @return The id of the hit.
		 */
		public int getId() {
			return id;
		}
		
	}
	
	/**
	 * Holds the hit priority types.
	 */
	public enum HitPriority {
		
		/**
		 * Low priority means that when the next loop is called that checks the hit queue, if the hit
		 * is not picked out, it is never displayed, used for hits such as Ring of Recoil.
		 */
		LOW_PRIORITY,
		
		/**
		 * High priority means that the hit will wait in the queue until it's displayed, used for
		 * hits such as special attacks.
		 */
		HIGH_PRIORITY;
		
	}
	
	/**
	 * The type of the hit.
	 */
	private final HitType type;
	
	/**
	 * The damage dealt by the hit.
	 */
	private final int damage;
	
	/**
	 * The delay for the hit.
	 */
	private int delay;
	
	/**
	 * The hit's priority.
	 */
	private HitPriority hitPriority;

	/**
	 * Creates a standard hit.
	 * @param damage The damage dealt by the hit.
	 */
	public Hit(int damage) {
		this(damage > 0 ? HitType.NORMAL_HIT : HitType.ZERO_DAMAGE_HIT, damage, HitPriority.HIGH_PRIORITY, 0);
	}

	public Hit(int damage, HitType type) {
		this(damage > 0 ? type : HitType.ZERO_DAMAGE_HIT, damage, HitPriority.HIGH_PRIORITY, 0);
	}
	
	/**
	 * Creates a standard hit.
	 * @param damage The damage dealt by the hit.
	 */
	public Hit(int damage, int delay) {
		this(damage > 0 ? HitType.NORMAL_HIT : HitType.ZERO_DAMAGE_HIT, damage, HitPriority.HIGH_PRIORITY, delay);
	}
	
	/**
	 * Creates a standard hit.
	 * @param damage The damage dealt by the hit.
	 */
	public Hit(int damage, HitPriority hitPriority) {
		this(damage > 0 ? HitType.NORMAL_HIT : HitType.ZERO_DAMAGE_HIT, damage, hitPriority, 0);
	}
	
	/**
	 * Creates a standard hit.
	 * @param damage The damage dealt by the hit.
	 */
	public Hit(HitType type, int damage) {
		this(type, damage, HitPriority.HIGH_PRIORITY, 0);
	}
	
	/**
	 * Creates a standard hit.
	 * @param damage The damage dealt by the hit.
	 */
	public Hit(int damage, HitPriority hitPriority, int delay) {
		this(damage > 0 ? HitType.NORMAL_HIT : HitType.ZERO_DAMAGE_HIT, damage, hitPriority, delay);
	}

	/**
	 * Creates a hit.
	 * @param type The hit type.
	 * @param damage The damage.
	 */
	public Hit(HitType type, int damage, HitPriority hitPriority, int delay) {
		this.type = type;
		this.damage = damage;
		this.delay = delay;
		this.hitPriority = hitPriority;
	}
	
	/**
	 * Gets the type of the hit.
	 * @return The type of the hit.
	 */
	public HitType getType() {
		return type;
	}
	
	/**
	 * Gets the damage of the hit.
	 * @return The damage of the hit.
	 */
	public int getDamage() {
		return damage;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getDelay() {
		return delay;
	}
	
	public HitPriority getHitPriority() {
		return hitPriority;
	}
	
}
