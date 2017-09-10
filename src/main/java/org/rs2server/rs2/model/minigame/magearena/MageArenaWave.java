package org.rs2server.rs2.model.minigame.magearena;

public class MageArenaWave {
	
	public static final int HUMAN = 1605;
	public static final int OGRE = 1606;
	public static final int SPIDER = 1607;
	public static final int SPIRIT = 1608;
	public static final int DEMON = 1609;

	private static final int[][] SPAWNS = {
		{}, {HUMAN}, {OGRE}, {SPIDER}, {SPIRIT}, {DEMON}
	};
	
	private int stage;

	public void set(int stage) {
		this.stage = stage;
	}

	public int[] spawns() {
		return SPAWNS[stage];
	}

	public int getStage() {
		return stage;
	}

}
