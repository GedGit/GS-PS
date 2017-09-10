package org.rs2server.rs2.domain.service.api.loot;

import com.google.gson.annotations.SerializedName;
import org.rs2server.rs2.model.Item;

import javax.annotation.concurrent.Immutable;

/**
 * A loose representation of item loot.
 *
 * @author tommo
 */
@Immutable
public class Loot {

	@SerializedName("id") private int itemId;
	private int minAmount;
	private int maxAmount;

	/**
	 * The loot probability [0..100] in percent.
	 */
	private double probability;

	private Loot(int itemId, int minAmount, int maxAmount, double probability) {
		this.itemId = itemId;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.probability = probability;
	}

	public static Loot of(int itemId, double probability) {
		return new Loot(itemId, 1, 1, probability);
	}

	public static Loot of(int itemId, int minAmount, int maxAmount, double probability) {
		return new Loot(itemId, minAmount, maxAmount, probability);
	}

	public int getItemId() {
		return itemId;
	}

	public int getMinAmount() {
		return minAmount;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public double getProbability() {
		return probability;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Loot loot = (Loot) o;

		if (itemId != loot.itemId) return false;
		if (minAmount != loot.minAmount) return false;
		if (maxAmount != loot.maxAmount) return false;
		return Double.compare(loot.probability, probability) == 0;

	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = itemId;
		result = 31 * result + minAmount;
		result = 31 * result + maxAmount;
		temp = Double.doubleToLongBits(probability);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	public Item toSingleItem() {
		return new Item(itemId);
	}

	@Override
	public final String toString() {
		return "["+itemId + ", " + "(" + minAmount + ", " + maxAmount + ") probability = " + probability + "] \n";
	}
}
