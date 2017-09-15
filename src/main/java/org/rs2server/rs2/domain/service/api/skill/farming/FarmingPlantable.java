package org.rs2server.rs2.domain.service.api.skill.farming;

import org.joda.time.Duration;
import java.util.Arrays;

/**
 * Defines all of the plantable crops and their types.
 *
 * @author tommo
 */ 
public enum FarmingPlantable {
	
	/*
	 * Herbs
	 */
	HERB_GUAM(FarmingPatchType.HERB_PATCH, 9, 12, 5291, 199, 0x04, 0x08, 5, 7, Duration.standardMinutes(10)),
	
	HERB_MARRENTILL(FarmingPatchType.HERB_PATCH, 14, 15, 5292, 201, 0x0b, 0x0f, 5, 7, Duration.standardMinutes(10)),
	
	HERB_TARROMIN(FarmingPatchType.HERB_PATCH, 19, 18, 5293, 203, 0x12, 0x16, 5, 7, Duration.standardMinutes(10)),
	
	HERB_HARRALANDER(FarmingPatchType.HERB_PATCH, 26, 24, 5294, 205, 0x19, 0x1d, 5, 7, Duration.standardMinutes(10)),
	
	HERB_RANARR(FarmingPatchType.HERB_PATCH, 32, 30, 5295, 207, 0x20, 0x24, 5, 7, Duration.standardMinutes(10)),
	
	HERB_TOADFLAX(FarmingPatchType.HERB_PATCH, 38, 38, 5296, 2998, 0x27, 0x2b, 5, 7, Duration.standardMinutes(10)),
	
	HERB_IRIT(FarmingPatchType.HERB_PATCH, 44, 48, 5297, 209, 0x2e, 0x32, 5, 7, Duration.standardMinutes(10)),
	
	HERB_AVANTOE(FarmingPatchType.HERB_PATCH, 50, 61, 5298, 211, 0x35, 0x39, 5, 7, Duration.standardMinutes(10)),
	
	HERB_KWUARM(FarmingPatchType.HERB_PATCH, 56, 78, 5299, 213, 0x44, 0x48, 5, 7, Duration.standardMinutes(10)),
	
	HERB_SNAPDRAGON(FarmingPatchType.HERB_PATCH, 62, 99, 5300, 3051, 0x4b, 0x4f, 5, 7, Duration.standardMinutes(10)),
	
	HERB_CADANTINE(FarmingPatchType.HERB_PATCH, 67, 120, 5301, 215, 0x52, 0x56, 5, 7, Duration.standardMinutes(10)),
	
	HERB_LANTADYME(FarmingPatchType.HERB_PATCH, 73, 152, 5302, 2485, 0x59, 0x5d, 5, 7, Duration.standardMinutes(10)),
	
	HERB_DWARF_WEED(FarmingPatchType.HERB_PATCH, 79, 192, 5303, 217, 0x60, 0x64, 5, 7, Duration.standardMinutes(10)),
	
	HERB_TORSTOL(FarmingPatchType.HERB_PATCH, 85, 225, 5304, 219, 0x67, 0x6b, 5, 7, Duration.standardMinutes(10)),

	/**
	 * FLowers
	 */
	FLOWER_MARIGOLDS(FarmingPatchType.FLOWER_PATCH, 2, 47, 5096, 6010, 0x08, 0x0c, 1, 1, Duration.standardMinutes(5)),
	
	FLOWER_ROSEMARY(FarmingPatchType.FLOWER_PATCH, 11, 67, 5097, 6014, 0x0d, 0x11, 1, 1, Duration.standardMinutes(5)),
	
	FLOWER_NASTURTIUM(FarmingPatchType.FLOWER_PATCH, 24, 111, 5098, 6012, 0x12, 0x16, 1, 1, Duration.standardMinutes(5)),
	
	FLOWER_WOAD(FarmingPatchType.FLOWER_PATCH, 25, 116, 5099, 1793, 0x17, 0x1b, 1, 1, Duration.standardMinutes(5)),
	
	FLOWER_LIMPWURT(FarmingPatchType.FLOWER_PATCH, 26, 120, 5100, 225, 0x1c, 0x20, 5, 7, Duration.standardMinutes(5)),

	/**
	 * Allotments
	 */
	ALLOTMENT_POTATOES(FarmingPatchType.ALLOTMENT, 1, 9, 5318, 1942, 0x06, 0x0a, 5, 7, Duration.standardMinutes(5)),
	
	ALLOTMENT_ONIONS(FarmingPatchType.ALLOTMENT, 5, 11, 5319, 1957, 0x0d, 0x12, 5, 7, Duration.standardMinutes(5)),
	
	ALLOTMENT_CABBAGES(FarmingPatchType.ALLOTMENT, 7, 12, 5324, 1965, 0x14, 0x18, 5, 7, Duration.standardMinutes(5)),
	
	ALLOTMENT_TOMATOES(FarmingPatchType.ALLOTMENT, 12, 14, 5322, 1982, 0x1b, 0x1f, 5, 7, Duration.standardMinutes(5)),
	
	ALLOTMENT_SWEETCORN(FarmingPatchType.ALLOTMENT, 20, 19, 5320, 5986, 0x22, 0x28, 5, 7, Duration.standardMinutes(5)),
	
	ALLOTMENT_STRAWBERRY(FarmingPatchType.ALLOTMENT, 31, 29, 5323, 5504, 0x2b, 0x32, 5, 7, Duration.standardMinutes(5)),
	
	ALLOTMENT_WATERMELON(FarmingPatchType.ALLOTMENT, 47, 55, 5321, 5982, 0x34, 0x3e, 5, 7, Duration.standardMinutes(5)),
	
	/**
	 * Trees
	 */
	TREE_OAK(FarmingPatchType.TREE_PATCH, 15, 467, 5370, -1, 8, 12, 1, 1, Duration.standardMinutes(45)),

	TREE_WILLOW(FarmingPatchType.TREE_PATCH, 30, 1456, 5371, -1, 15, 21, 1, 1, Duration.standardMinutes(45)),

	TREE_MAPLE(FarmingPatchType.TREE_PATCH, 45, 3403, 5372, -1, 24, 32, 1, 1, Duration.standardMinutes(45)),

	TREE_YEW(FarmingPatchType.TREE_PATCH, 60, 7070, 5373, -1, 35, 45, 1, 1, Duration.standardMinutes(45)),

	TREE_MAGIC(FarmingPatchType.TREE_PATCH, 75, 13768, 5374, -1, 48, 60, 1, 1, Duration.standardMinutes(45));

	private FarmingPatchType type;
	private int requiredLevel;
	private int experience;
	private int seedItemId;
	private int reward;
	private int minGrowth;
	private int maxGrowth;
	private int minYield;
	private int maxYield;
	private Duration growthTime;

	FarmingPlantable(FarmingPatchType type, int requiredLevel, int experience, int seedItemId, int reward,
					 int minGrowth, int maxGrowth, int minYield, int maxYield, Duration growthTime) {
		this.type = type;
		this.requiredLevel = requiredLevel;
		this.experience = experience;
		this.seedItemId = seedItemId;
		this.reward = reward;
		this.minGrowth = minGrowth;
		this.maxGrowth = maxGrowth;
		this.minYield = minYield;
		this.maxYield = maxYield;
		this.growthTime = growthTime;
	}

	public FarmingPatchType getType() {
		return type;
	}

	public int getRequiredLevel() {
		return requiredLevel;
	}

	public int getExperience() {
		return experience;
	}

	public int getSeedItemId() {
		return seedItemId;
	}

	public int getReward() {
		return reward;
	}

	public int getMinGrowth() {
		return minGrowth;
	}

	public int getMaxGrowth() {
		return maxGrowth;
	}

	public int getMinYield() {
		return minYield;
	}

	public int getMaxYield() {
		return maxYield;
	}

	public Duration getGrowthTime() {
		return growthTime;
	}

	public static FarmingPlantable forSeedItemId(int seedItemId) {
		return Arrays.stream(values())
				.filter(p -> p.getSeedItemId() == seedItemId)
				.findAny()
				.orElse(null);
	}

	public static FarmingPlantable forRewardItemId(int rewardItemId) {
		return Arrays.stream(values())
				.filter(p -> p.getReward() == rewardItemId)
				.findAny()
				.orElse(null);
	}
}
