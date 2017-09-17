package org.rs2server.rs2.util;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;

/**
 * Stores all data related to interfaces.
 * 
 * @author Vichy
 *
 */
public class InterfaceUtils {

	/**
	 * Sends quest tab information
	 * 
	 * @param player
	 */
	public static void sendQuestTabData(Player player) {
		String memberRank = "<col=FFFFFF>", doubleExp;
		if (player.isDiamondMember())
			memberRank += "<img=36><col=00FFE5>Diamond";
		else if (player.isPlatinumMember())
			memberRank += "<img=37><col=13D695>Platinum";
		else if (player.isGoldMember())
			memberRank += "<img=38><col=B6E314>Gold";
		else if (player.isSilverMember())
			memberRank += "<img=39>Silver<col=C9C9C9>";
		else if (player.isBronzeMember())
			memberRank += "<img=40>Bronze<col=CC9F3D>";

		if (World.isWeekend() || Constants.DOUBLE_EXP)
			doubleExp = "Active";
		else
			doubleExp = "<col=ff0000>Not active";

		player.getActionSender().sendString(400, 9,
				"<col=00ff00><shad=000000>Salve-PS Dashboard<br><br><col=14B891>Players online: <col=FFFFFF>"
						+ World.getWorld().getPlayers().size() + "<br><col=14B891>Server time: <col=FFFFFF>"
						+ Misc.time("hh:mm:ss a") + "<br><col=14B891>Server votes: <col=FFFFFF>"
						+ Misc.formatNumber(World.getWorld().getTotalVotes()) + "<br><col=14B891>Double EXP: </col>"
						+ doubleExp + "<br><br><col=16D9AA>Member rank: " + memberRank
						+ "<br><col=16D9AA>Vote points: <col=FFFFFF>"
						+ Misc.formatNumber(player.getDatabaseEntity().getVotePoints())
						+ "<br><col=16D9AA>Double EXP: <col=FFFFFF>"
						+ (player.getDatabaseEntity().getDoubleExp() > 0
								? Misc.secondsToMinutes(player.getDatabaseEntity().getDoubleExp())
								: "None")
						+ "<br><col=16D9AA>Loyalty points: <col=FFFFFF>"
						+ Misc.formatNumber(player.getDatabaseEntity().getLoyaltyPoints())
						+ "<br><col=16D9AA>Pest Control points: <col=FFFFFF>"
						+ Misc.formatNumber(player.getDatabaseEntity().getStatistics().getPestControlPoints())
						+ "<br><col=16D9AA>Slayer points: <col=FFFFFF>"
						+ Misc.formatNumber(player.getDatabaseEntity().getStatistics().getSlayerRewardPoints())
						+ "<br><br><br><br><br><br><br><br><br><br><br>");
	}
}