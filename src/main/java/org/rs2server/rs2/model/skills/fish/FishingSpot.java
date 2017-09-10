package org.rs2server.rs2.model.skills.fish;

import org.rs2server.rs2.model.Animation;

import java.util.HashMap;
import java.util.Map;

public enum FishingSpot {

	NET_NET_AND_BAIT(1, 1518, Animation.create(621), 303, -1, -1, 13000, Fish.SHRIMP, Fish.ANCHOVIES),

	BAIT_NET_AND_BAIT(2, 1518, Animation.create(622), 307, -1, 313, 12000, Fish.SARDINE, Fish.HERRING),

	LURE_LURE_AND_BAIT(1, 1512, Animation.create(622), 309, -1, 314, 11000, Fish.TROUT, Fish.SALMON),

	BAIT_LURE_AND_BAIT(2, 1512, Animation.create(622), 307, -1, 313, 10000, Fish.PIKE),

	CAGE_AND_HARPOON(1, 1510, Animation.create(619), 301, -1, -1, 9500, Fish.LOBSTER),

	CAGE_AND_HARPOON2(2, 1510, Animation.create(618), 311, -1, -1, 9000, Fish.TUNA, Fish.SWORDFISH),

	BIG_NET_NET_AND_HARPOON(1, 1520, Animation.create(621), 305, -1, -1, 8000, Fish.MACKEREL, Fish.COD, Fish.BASS),

	NET_AND_HARPOON(2, 1520, Animation.create(618), 311, -1, -1, 6000, Fish.SHARK),

	BIG_NET_AND_HARPOON(1, 4316, Animation.create(621), 303, -1, -1, 8000, Fish.MONKFISH),

	BIG_NET_AND_HARPOON2(2, 4316, Animation.create(618), 311, -1, -1, 7000, Fish.TUNA, Fish.SWORDFISH),

	HARPOON(2, 4316, Animation.create(618), 311, 21028, -1, 6000, Fish.SHARK),

	BAIT(1, 6825, Animation.create(622), 307, -1, 13431, 3500, Fish.ANGLER_FISH),

	CAGE(1, 1536, Animation.create(619), 301, -1, 11940, 3500, Fish.DARK_CRAB),

	KARAMBWAN_VESSEL(1, 1517, Animation.create(621), 3159, -1, -1, 6500, Fish.KARAMBWAN),

	SACRED_EEL(1, 6825, Animation.create(622), 307, -1, 313, 7000, Fish.SACRED_EEL);

	private final int click;
	private final int npcId;
	private final Animation animation;
	private final int item;
	private final int item2;
	private final int bait;

	private final Fish[] fish;
	private final int petChance;

	private static final Map<Integer, FishingSpot> fishingSpot = new HashMap<>();

	static {
		for (FishingSpot fishSpot : FishingSpot.values()) {
			fishingSpot.put(fishSpot.getNpcId() | (fishSpot.getClick() << 24), fishSpot);
		}
	}

	public static FishingSpot forId(int object) {
		return fishingSpot.get(object);
	}

	FishingSpot(int click, int npcId, Animation animation, int item, int item2, int bait, int petChance, Fish... fish) {
		this.npcId = npcId;
		this.item = item;
		this.item2 = item2;
		this.bait = bait;
		this.fish = fish;
		this.animation = animation;
		this.click = click;
		this.petChance = petChance;
	}

	public int getNpcId() {
		return npcId;
	}

	public int getItem() {
		return item;
	}

	public int getItem2() {
		return item2;
	}

	public int getBait() {
		return bait;
	}

	public Animation getAnimation() {
		return animation;
	}

	public int getClick() {
		return click;
	}

	public int getPetChance() {
		return petChance;
	}

	public Fish[] getHarvest() {
		return fish;
	}

}
