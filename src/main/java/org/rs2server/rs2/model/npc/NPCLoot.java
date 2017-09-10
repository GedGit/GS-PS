package org.rs2server.rs2.model.npc;

public class NPCLoot {

	/**
	 * The {@link Item} identifier for this <code>NPCLoot</code>.
	 */
	private final int itemID;

	/**
	 * The maximum amount of this <code>NPCLoot</code> possible to be dropped,
	 * or 'stacked'.
	 */
	private final int maxAmount;

	/**
	 * The minimum amount of this <code>NPCLoot</code> possible to be dropped,
	 * or 'stacked'.
	 */
	private final int minAmount;

	/**
	 * The hit ceiling value valid for this <code>NPCLoot</code> to potentially
	 * drop.
	 */
	private final double hitRollCeil;

	/**
	 * The constructor to create a <code>NPCLoot</code> instance.
	 * 
	 * @param itemID
	 *            The {@link Item} identifier for this <code>NPCLoot</code>.
	 * @param hitRollCeil
	 *            The hit ceiling value for this <code>NPCLoot</code>.
	 */
	public NPCLoot(final int itemID, final int maxAmount, final int minAmount,
			final double hitRollCeil) {
		this.itemID = itemID;
		this.maxAmount = maxAmount;
		this.minAmount = minAmount;
		this.hitRollCeil = hitRollCeil;
	}

	/**
	 * Gets the {@link Item} identifier for this <code>NPCLoot</code>.
	 * 
	 * @return {@link #itemID}.
	 */
	public int getItemID() {
		return itemID;
	}

	/**
	 * Gets the maximum amount of this <code>NPCLoot</code> possible to drop,
	 * stack.
	 * 
	 * @return {@link #maxAmount}.
	 */
	public int getMaxAmount() {
		return maxAmount;
	}

	/**
	 * Gets the minimum amount of this <code>NPCLoot</code> possible to drop,
	 * stack.
	 * 
	 * @return {@link #minAmount}.
	 */
	public int getMinAmount() {
		return minAmount;
	}

	/**
	 * Gets the the hit ceiling value for this <code>NPCLoot</code>.
	 * 
	 * @return {@link #hitRollCeil}.
	 */
	public double getHitRollCeil() {
		return hitRollCeil;
	}

	@Override
	public String toString() {
		return "[ "+itemID+", ("+minAmount+", "+maxAmount+"), probability = "+hitRollCeil+" ]";
	}


}

