package org.rs2server.rs2.model.skills.slayer;

/**
 * Stores slayer task info.
 * 
 * @author Vichy
 *
 */
public class Slayer {

	/**
	 * The task.
	 */
	private SlayerTask task;

	public SlayerTask getSlayerTask() {
		return task;
	}

	public void setSlayerTask(SlayerTask slayerTask) {
		this.task = slayerTask;
	}

	/**
	 * For setting the last slayer task to prevent it repeating again.
	 */
	public String taskName;
}
