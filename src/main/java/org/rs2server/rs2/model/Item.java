package org.rs2server.rs2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.rs2server.rs2.model.equipment.EquipmentDefinition;

/**
 * DO NOT ADD FIELDS TO THIS CLASS. ANY CHANGES CAN POSSIBLY BREAK
 * (DE-)SERIALIATION TO THE DATABASE AND REQUIRE CAREFUL ATTENTION.
 *
 * Yours truly, tommo and the wrath which will be bestowed upon you if you do.
 */
public class Item {

	/**
	 * The id.
	 */
	private int id;

	/**
	 * The number of items.
	 */
	private int count;

	/**
	 * Flag whether or not this item is a PVP drop. This is transient and
	 * ignored so that it's never serialized.
	 */
	@JsonIgnore
	private transient boolean pvpDrop;
	
	@JsonIgnore
	private boolean respawns;

	public Item() {

	}

	/**
	 * Creates a single item.
	 *
	 * @param id
	 *            The id.
	 */
	public Item(int id) {
		this(id, 1);
	}

	/**
	 * Creates a stacked item.
	 *
	 * @param id
	 *            The id.
	 * @param count
	 *            The number of items.
	 * @throws IllegalArgumentException
	 *             if count is negative.
	 */
	public Item(int id, int count) {
		if (count < 0) {
			// throw new IllegalArgumentException("Count cannot be negative.");
			return;
		}
		this.id = id;
		this.count = count;
	}

	/**
	 * Gets the definition of this item.
	 *
	 * @return The definition.
	 */
	@JsonIgnore
	public ItemDefinition getDefinition() {
		return ItemDefinition.forId(id);
	}

	@JsonIgnore
	public org.rs2server.cache.format.CacheItemDefinition getDefinition2() {
		return org.rs2server.cache.format.CacheItemDefinition.get(id);
	}

	/**
	 * Gets the definition of this item.
	 *
	 * @return The definition.
	 */
	@JsonIgnore
	public EquipmentDefinition getEquipmentDefinition() {
		return EquipmentDefinition.forId(id);
	}

	/**
	 * Gets the item id.
	 *
	 * @return The item id.
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the count.
	 *
	 * @return The count.
	 */
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Sets the item count.
	 */
	public Item setItemCount(int am) {
		this.count = am;
		if (this.count == 0) {
			return null;
		}
		return this;
	}

	/**
	 * Increases the count of this item by a certain amount.
	 *
	 * @param amount
	 *            The amount to increase the original count with.
	 */
	@JsonIgnore
	public void increaseCount(int amount) {
		count += amount;
	}

	/**
	 * Increases the count of this item.
	 */
	@JsonIgnore
	public void increaseCount() {
		count++;
	}

	@Override
	public String toString() {
		return Item.class.getName() + " [id=" + id + ", count=" + count + "]";
	}

	@JsonIgnore
	public boolean isPvpDrop() {
		return pvpDrop;
	}

	@JsonIgnore
	public void setPvpDrop(boolean pvpDrop) {
		this.pvpDrop = pvpDrop;
	}

	@JsonIgnore
	public boolean respawns() {
		return respawns;
	}

	@JsonIgnore
	public void setRespawns(boolean respawns) {
		this.respawns = respawns;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Item item = (Item) o;

		return id == item.id && count == item.count && pvpDrop == item.pvpDrop;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + count;
		result = 31 * result + (pvpDrop ? 1 : 0);
		return result;
	}

	@JsonIgnore
	public int getPrice() {
		int price = this.getDefinition().getStorePrice();
		if (price < 1)
			return this.getDefinition2().getStorePrice();
		return price;
	}
}
