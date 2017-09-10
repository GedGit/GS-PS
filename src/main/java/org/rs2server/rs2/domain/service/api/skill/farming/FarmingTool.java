package org.rs2server.rs2.domain.service.api.skill.farming;

import java.util.Arrays;

/**
 * Defines the farming tools storable in the tool store (accessible via Tool
 * Leprechauns).
 *
 * @author tommo
 */
public enum FarmingTool {

	RAKE(5341, 1, 1, 3),

	SEED_DIBBER(5343, 1, 2, 4),

	SPADE(952, 1, 3, 5),

	SECATEURS(5329, 1, 4, 6),

	WATERING_CAN(5340, 1, 5, 7),

	TROWEL(5325, 1, 6, 8),

	BUCKET(3727, 255, 7, 9),

	COMPOST(6032, 255, 8, 10),

	SUPERCOMPOST(6034, 255, 9, 11);

	private int itemId;
	private int maxAmount;
	private int inventoryActionButtonId;
	private int storeActionButtonId;

	FarmingTool(int itemId, int maxAmount, int inventoryActionButtonId, int storeActionButtonId) {
		this.itemId = itemId;
		this.maxAmount = maxAmount;
		this.inventoryActionButtonId = inventoryActionButtonId;
		this.storeActionButtonId = storeActionButtonId;
	}

	public int getItemId() {
		return itemId;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public int getInventoryActionButtonId() {
		return inventoryActionButtonId;
	}

	public int getStoreActionButtonId() {
		return storeActionButtonId;
	}

	public static FarmingTool forInventoryActionButtonId(int inventoryActionButtonId) {
		return Arrays.stream(values()).filter(t -> t.getInventoryActionButtonId() == inventoryActionButtonId)
				.findFirst().orElse(null);
	}

	public static FarmingTool forStoreActionButtonId(int storeActionButtondId) {
		return Arrays.stream(values()).filter(t -> t.getStoreActionButtonId() == storeActionButtondId).findFirst()
				.orElse(null);
	}
}
