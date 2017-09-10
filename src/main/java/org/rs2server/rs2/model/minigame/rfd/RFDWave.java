package org.rs2server.rs2.model.minigame.rfd;

/**
 * Stores each waves information.
 * 
 * @author Vichy
 */
public class RFDWave {

	/**
	 * Ints representing each waves NPC id's.
	 */
	public static final int CULINAROMANCER = 4878, AGRITH = 4880, FLAMBEED = 4881, KARAMEL = 4882, DESSOURT = 4883;

	/**
	 * Int arrays representing wave npc spawns.
	 */
	private static final int[][] SPAWNS = { { CULINAROMANCER }, { AGRITH }, { FLAMBEED }, { KARAMEL }, { DESSOURT } };

	/**
	 * Initiating wave ID.
	 */
	private int stage;

	/**
	 * Setting the wave ID.
	 * 
	 * @param stage
	 *            the wave id.
	 */
	public void set(int stage) {
		this.stage = stage;
	}

	/**
	 * Getting current waves NPC ID spawn.
	 * 
	 * @return the npc id to spawn.
	 */
	public int[] spawns() {
		return SPAWNS[stage];
	}

	/**
	 * Gets current wave ID.
	 * 
	 * @return the wave id.
	 */
	public int getStage() {
		return stage;
	}
}