package org.rs2server.rs2.domain.service.api.skill.farming;

import org.joda.time.DateTime;

/**
 * A player's state of a single farming patch.
 *
 * @author tommo
 * @author dx2 
 */
public class FarmingPatchState {

	private FarmingPatch patch;

	/**
	 * The seed planted in this patch.
	 */
	private FarmingPlantable planted;

	/**
	 * The growth stage.
	 * This value is between the <code>minGrowth</code> and <code>maxGrowth</code> values defined
	 * by the {@link FarmingPlantable} in this patch.
	 */
	private int growth;

	/**
	 * The instant in time when the last growth took place.
	 */
	private DateTime lastGrowthTime;

	/**
	 * The amount of weed removed. [0..3]
	 */
	private int weedLevel = 0;

	/**
	 * The amount still left in this patch to be yielded.
	 */
	private int yield;

	/**
	 * The treatment for this patch.
	 */
	private FarmingPatchTreatment treatment = FarmingPatchTreatment.NOT_TREATED;

	/**
	 * Whether or not the patch has been watered.
	 */
	private boolean watered;

	/**
	 * Whether or not the patch is diseased.
	 */
	private boolean diseased;

	/**
	 * Whether or not the patch has died.
	 */
	private boolean dead;

	/**
	 * Is the crop immune to disease.
	 * Achievable by planting a flower which protects it, or paying a farmer.
	 */
	private boolean immune;

	/**
	 * Required.
	 */
	public FarmingPatchState() {

	}

	public FarmingPatchState(final FarmingPatch patch) {
		this.patch = patch;
	}

	public FarmingPatch getPatch() {
		return patch;
	}

	public int getWeedLevel() {
		return weedLevel;
	}

	public void setWeedLevel(int weedLevel) {
		this.weedLevel = weedLevel;
	}

	public void setPatch(FarmingPatch patch) {
		this.patch = patch;
	}

	public FarmingPlantable getPlanted() {
		return planted;
	}

	public void setPlanted(FarmingPlantable planted) {
		this.planted = planted;
	}

	public int getGrowth() {
		return growth;
	}

	public void setGrowth(int growth) {
		this.growth = growth;
	}

	public DateTime getLastGrowthTime() {
		return lastGrowthTime;
	}

	public void setLastGrowthTime(DateTime lastGrowthTime) {
		this.lastGrowthTime = lastGrowthTime;
	}

	public int getYield() {
		return yield;
	}

	public void setYield(int yield) {
		this.yield = yield;
	}

	public FarmingPatchTreatment getTreatment() {
		return treatment;
	}

	public void setTreatment(FarmingPatchTreatment treatment) {
		this.treatment = treatment;
	}

	public boolean isWatered() {
		return watered;
	}

	public void setWatered(boolean watered) {
		this.watered = watered;
	}

	public boolean isDiseased() {
		return diseased;
	}

	public void setDiseased(boolean diseased) {
		this.diseased = diseased;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public boolean isImmune() {
		return immune;
	}

	public void setImmune(boolean immune) {
		this.immune = immune;
	}
}
