package org.rs2server.rs2.model.equipment;
/**
 * The types of poison a weapon has
 * @author Sir Sean
 *
 */
public enum PoisonType  {
	
	/**
	 * Poison not set
	 */
	NONE(0, 0),
	
	/**
	 * Poison hit (4, 2)
	 */
	POISON(4, 2),
	
	/**
	 * Poison hit (5, 3)
	 */
	EXTRA_POISON(5, 3),
	
	/**
	 * Poison hit (6, 4)
	 */
	SUPER_POISON(6, 4);
	
	/**
	 * The damage amount
	 */
	private int meleeDamage;
	
	/**
	 * The damage amount
	 */
	private int rangeDamage;

	/**
	 * Damage amount set in the constructor
	 * @param damage The damage amount
	 */
	private PoisonType(int meleeDamage, int rangeDamage) {
		this.meleeDamage = meleeDamage;
		this.rangeDamage = rangeDamage;
	}

	/**
	 * Gets the damage amount
	 * @return the damage amount
	 */
	public int getMeleeDamage() {
		return meleeDamage;
	}

	/**
	 * Gets the damage amount
	 * @return the damage amount
	 */
	public int getRangeDamage() {
		return rangeDamage;
	}
}
