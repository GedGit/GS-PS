package org.rs2server.rs2.domain.service.api.skill.farming;

import org.rs2server.rs2.model.Animation;

/**
 * Defines the farming patch types (herb, allotment, flower, etc) and what can
 * be planted in them.
 *
 * @author tommo
 */
public enum FarmingPatchType {

	HERB_PATCH(Animation.create(2282), false, true, 6),

	ALLOTMENT(Animation.create(831), true, true, 6),

	FLOWER_PATCH(Animation.create(2286), true, true, 6),

	TREE_PATCH(Animation.create(-1), false, true, 6),

	FRUIT_TREE_PATCH(null, true, true, 0);

	private Animation yieldAnimation;
	private boolean waterable;
	private boolean vulnerableToDisease;
	private int stateBitOffset;

	FarmingPatchType(Animation yieldAnimation, boolean waterable, boolean vulnerableToDisease, int stateBitOffset) {
		this.yieldAnimation = yieldAnimation;
		this.waterable = waterable;
		this.vulnerableToDisease = vulnerableToDisease;
		this.stateBitOffset = stateBitOffset;
	}

	public Animation getYieldAnimation() {
		return yieldAnimation;
	}

	public boolean isWaterable() {
		return waterable;
	}

	public boolean isVulnerableToDisease() {
		return vulnerableToDisease;
	}

	public int getStateBitOffset() {
		return stateBitOffset;
	}

	@Override
	public String toString() {
		return name().toLowerCase().replaceAll("_", " ");
	}
}
