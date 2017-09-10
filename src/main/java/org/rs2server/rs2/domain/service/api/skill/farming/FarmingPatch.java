package org.rs2server.rs2.domain.service.api.skill.farming;

import com.google.common.collect.ImmutableList;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.boundary.Area;

import java.util.Arrays;
import java.util.List;

/**
 * Defines all of the farming patches and their data.
 * 
 * @author Vichy
 */
public enum FarmingPatch {

	/**
	 * Herbs
	 */
	HERB_PATCH_CAMELOT(FarmingPatchType.HERB_PATCH, 529, 24, ImmutableList.of(8151),
			ImmutableList.of(Area.create(2813, 3463))),

	HERB_PATCH_ARDOUGNE(FarmingPatchType.HERB_PATCH, 529, 24, ImmutableList.of(8152),
			ImmutableList.of(Area.create(2670, 3374))),

	HERB_PATCH_CANIFIS(FarmingPatchType.HERB_PATCH, 529, 24, ImmutableList.of(8153),
			ImmutableList.of(Area.create(3605, 3529))),

	HERB_PATCH_FALADOR(FarmingPatchType.HERB_PATCH, 529, 24, ImmutableList.of(8150),
			ImmutableList.of(Area.create(3058, 3311))),

	/**
	 * Allotments - vegetables
	 */
	ALLOTMENT_CAMELOT_NORTH(FarmingPatchType.ALLOTMENT, 529, 0, ImmutableList.of(8552),
			ImmutableList.of(Area.create(2805, 3466, 2814, 3468))),

	ALLOTMENT_CAMELOT_SOUTH(FarmingPatchType.ALLOTMENT, 529, 8, ImmutableList.of(8553),
			ImmutableList.of(Area.create(2805, 3459, 2814, 3461))),

	ALLOTMENT_ARDOUGNE_NORTH(FarmingPatchType.ALLOTMENT, 529, 0, ImmutableList.of(8554),
			ImmutableList.of(Area.create(2662, 3377, 2671, 3379))),

	ALLOTMENT_ARDOUGNE_SOUTH(FarmingPatchType.ALLOTMENT, 529, 8, ImmutableList.of(8555),
			ImmutableList.of(Area.create(2662, 3370, 2671, 3372))),

	ALLOTMENT_CANIFIS_SOUTH_EAST(FarmingPatchType.ALLOTMENT, 529, 8, ImmutableList.of(8557),
			ImmutableList.of(Area.create(3602, 3521, 3606, 3526))),

	ALLOTMENT_CANIFIS_NORTH_WEST(FarmingPatchType.ALLOTMENT, 529, 0, ImmutableList.of(8556),
			ImmutableList.of(Area.create(3597, 3525, 3601, 3530))),

	ALLOTMENT_FALADOR_NORTH_WEST(FarmingPatchType.ALLOTMENT, 529, 0, ImmutableList.of(8550),
			ImmutableList.of(Area.create(3049, 3307, 3054, 3312))),

	ALLOTMENT_FALADOR_SOUTH_EAST(FarmingPatchType.ALLOTMENT, 529, 8, ImmutableList.of(8551),
			ImmutableList.of(Area.create(3055, 3303, 3059, 3308))),

	/**
	 * Flowers
	 */
	FLOWER_PATCH_CAMELOT(FarmingPatchType.FLOWER_PATCH, 529, 16, ImmutableList.of(7848),
			ImmutableList.of(Area.create(2809, 3463))),

	FLOWER_PATCH_ARDOUGNE(FarmingPatchType.FLOWER_PATCH, 529, 16, ImmutableList.of(7849),
			ImmutableList.of(Area.create(2666, 3374))),

	FLOWER_PATCH_CANIFIS(FarmingPatchType.FLOWER_PATCH, 529, 16, ImmutableList.of(7850),
			ImmutableList.of(Area.create(3601, 3525))),

	FLOWER_PATCH_FALADOR(FarmingPatchType.FLOWER_PATCH, 529, 16, ImmutableList.of(7847),
			ImmutableList.of(Area.create(3054, 3307))),

	/**
	 * Trees
	 */
	TREE_PATCH_VARROCK(FarmingPatchType.TREE_PATCH, 529, 0, ImmutableList.of(8390),
			ImmutableList.of(Area.create(3228, 3458))),

	TREE_PATCH_LUMBRIDGE(FarmingPatchType.TREE_PATCH, 529, 0, ImmutableList.of(8391),
			ImmutableList.of(Area.create(3192, 3230))),

	TREE_PATCH_FALADOR(FarmingPatchType.TREE_PATCH, 529, 0, ImmutableList.of(8389),
			ImmutableList.of(Area.create(3003, 3372))),

	TREE_PATCH_TAVERLEY(FarmingPatchType.TREE_PATCH, 529, 0, ImmutableList.of(8388),
			ImmutableList.of(Area.create(2935, 3437))),

	TREE_PATCH_GNOME_STRONGHOLD(FarmingPatchType.TREE_PATCH, 529, 0, ImmutableList.of(19147),
			ImmutableList.of(Area.create(2435, 3414)));

	private final FarmingPatchType type;
	private int configId;
	private int configBitOffset;
	private final List<Integer> objectIds;
	private final List<Area> areas;

	FarmingPatch(final FarmingPatchType type, final int configId, final int configBitOffset,
			final List<Integer> objectIds, final List<Area> locations) {
		this.type = type;
		this.configId = configId;
		this.configBitOffset = configBitOffset;
		this.objectIds = objectIds;
		this.areas = locations;
	}

	public FarmingPatchType getType() {
		return type;
	}

	public int getConfigId() {
		return configId;
	}

	public int getConfigBitOffset() {
		return configBitOffset;
	}

	public List<Integer> getObjectIds() {
		return objectIds;
	}

	public List<Area> getAreas() {
		return areas;
	}

	public static FarmingPatch forObjectIdAndLocation(Integer objectId, Location location) {
		return Arrays.stream(FarmingPatch.values())
				.filter(t -> t.getObjectIds().contains(objectId)
						&& t.getAreas().stream().filter(a -> a.contains(location)).findAny().isPresent())
				.findAny().orElse(null);
	}
}
