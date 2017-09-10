package org.rs2server.rs2.domain.service.api.skill.farming;

import java.util.Arrays;

/**
 * The types of treatment for patches.
 *
 * @author tommo
 */
public enum FarmingPatchTreatment {

	NOT_TREATED(-1, 0), COMPOST(6032, 1), SUPERCOMPOST(6034, 2);

	private int itemId;
	private int yieldIncrease;

	FarmingPatchTreatment(int itemId, int yieldIncrease) {
		this.itemId = itemId;
		this.yieldIncrease = yieldIncrease;
	}

	public int getItemId() {
		return itemId;
	}

	public int getYieldIncrease() {
		return yieldIncrease;
	}

	public static FarmingPatchTreatment forItemId(int itemId) {
		return Arrays.stream(values()).filter(t -> t.getItemId() == itemId).findFirst().orElse(null);
	}
}
