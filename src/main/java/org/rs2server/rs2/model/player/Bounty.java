package org.rs2server.rs2.model.player;

import org.rs2server.rs2.domain.service.impl.content.bounty.BountyHunterServiceImpl;

/**
 * @author Clank1337
 */
public final class Bounty {

	public static final int MAX_TARGET_TIME = 300;
	private static final int MAX_SAFE_TIME = 200;

	private final Player player;
	private int targetDelay = MAX_TARGET_TIME;
	private int safeDelay = MAX_SAFE_TIME;

	public Bounty(Player player) {
		this.player = player;
	}

	public int decrementTargetDelay() {
		return targetDelay--;
	}

	public int decrementSafeTimer() { return safeDelay--;}

	public void reset() {
		if (player.getBountyTarget() != null) {
			player.getBountyTarget().setBountyTarget(null);
		}
		player.setBountyTarget(null);
		player.setBounty(null);
		this.targetDelay = MAX_TARGET_TIME;
		this.safeDelay = MAX_SAFE_TIME;

		if (!player.isInWilderness()) {
			player.getActionSender().sendInteractionOption("null", 1, true);
		}
		BountyHunterServiceImpl.WILDERNESS_PLAYER_LIST.remove(player);
	}

	public boolean isReadyForTarget() {
		return targetDelay <= 0 && player.getBountyTarget() == null;
	}

	public int getTargetDelay() {
		return targetDelay;
	}

	public int getSafeDelay() { return safeDelay;}

	public void setTargetDelay(int delay) {
		this.targetDelay = delay;
	}

	public Player getPlayer() {
		return player;
	}

}
