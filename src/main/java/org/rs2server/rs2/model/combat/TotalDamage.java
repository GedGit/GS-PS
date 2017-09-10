package org.rs2server.rs2.model.combat;

/**
 * Represents the total damage done to an entity in combat.
 * @author Graham Edgecombe
 *
 */
public final class TotalDamage {
	
	/**
	 * The total damage dealt.
	 */
	private int damage = 0;
	
	/**
	 * The last time the damage was increased.
	 */
	private long lastHitTime = System.currentTimeMillis();
	
	/**
	 * Increaes the damage by the specified amount.
	 * @param amount How much to increase the damage by.
	 */
	public void increment(int amount) {
		this.damage += amount;
		this.lastHitTime = System.currentTimeMillis();
	}
	
	/**
	 * Gets the last hit time.
	 * @return The last hit time.
	 */
	public long getLastHitTime() {
		return lastHitTime;
	}
	
	/**
	 * Gets the total damage dealt.
	 * @return The total damage dealt.
	 */
	public int getDamage() {
		return damage;
	}

}
