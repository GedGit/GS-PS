package org.rs2server.rs2.tickable.impl;

import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * A tickable which adds 1 loyalty point every minute for the given player.
 * 
 * @author Vichy
 */
public class LoyaltyPointTick extends Tickable {

	/**
	 * Defining the player.
	 */
	private Player player;

	/**
	 * Creates the tickable to run every 100 ticks which is 60 seconds.
	 */
	public LoyaltyPointTick(Player player) {
		super(100); // TODO change back to 100
		this.player = player;
	}

	@Override
	public void execute() {
		// Checks if player is existent
		if (player == null) {
			this.stop();
			return;
		}
		// Checks if player is still online ?
		if (!World.getWorld().isPlayerOnline(player.getName())) {
			this.stop();
			return;
		}
		player.increaseAfkTolerance();
		// After 10 minutes of being AFK it warns the player
		if (player.getAfkTolerance() == 10) {
			player.getActionSender().sendDialogue("Salve-PS Advisor", DialogueType.NPC, 276, FacialAnimation.ANGER_3,
					"You've been AFK for 10 minutes now; you'll stop gaining Loyalty points after another 5 minutes.");
		}
		if (player.getAfkTolerance() == 60) {
			this.stop();
			player.resetAfkTolerance();
			player.getActionSender().sendLogout();
			return;
		}
		if (player.getAfkTolerance() == 50) {
			player.getActionSender().sendDialogue("Salve-PS Advisor", DialogueType.NPC, 276, FacialAnimation.ANGER_4,
					"You've been AFK for 50 minutes now; you will be automatically kicked out after another 10 minutes.");
			return;
		}
		if (player.getAfkTolerance() >= 15) {
			if (player.getAfkTolerance() == 15)
				player.getActionSender().sendDialogue("Salve-PS Advisor", DialogueType.NPC, 276,
						FacialAnimation.ANGER_3,
						"You've been AFK for 15 minutes now; you will no longer gain Loyalty points.");
			return;
		}
		int currentLoyalty = player.getDatabaseEntity().getLoyaltyPoints();
		// Add 1 point every minute.
		player.getDatabaseEntity().setLoyaltyPoints(currentLoyalty + 1);
		currentLoyalty = player.getDatabaseEntity().getLoyaltyPoints();

		if (currentLoyalty % 15 == 0)
			player.sendMessage("You've received <col=ff0000>15</col> Loyalty points; you're now at: <col=ff0000>"
					+ Misc.formatNumber(currentLoyalty) + "</col> points.");
	}
}