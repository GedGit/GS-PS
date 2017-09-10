package org.rs2server.rs2.content;

import org.rs2server.rs2.model.npc.impl.cerberus.Cerberus;
import org.rs2server.rs2.model.npc.impl.zulrah.Zulrah;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.Helpers;

public class BossKillLog {

	/**
	 * Sends the Interface/CS2Script/Access Mask to the entity.
	 */
	public static void handleBossLog(Player player) {
		player.getActionSender().sendInterface(187, false);
		player.getActionSender().sendCS2Script(217, new Object[] { "Kree'Arra: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(3162), 0)
				+ "|Commander Zilyana: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(2205), 0)
				+ "|General Graardor: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(2215), 0)
				+ "|K'ril Tsutsaroth: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(3129), 0)
				+ "|Dagannoth Rex: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(2267), 0)
				+ "|Dagannoth Prime: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(2266), 0)
				+ "|Dagannoth Supreme: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(2265), 0)
				+ "|Giant Mole: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(5779), 0)
				+ "|King Black Dragon: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(239), 0)
				+ "|Callisto: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(6609), 0)
				+ "|Venenatis: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(6610), 0)
				+ "|Vet'ion: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(6611), 0)
				+ "|Chaos Elemental: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(2054), 0)
				+ "|Chaos Fanatic: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(6619), 0)
				+ "|Crazy Archaeologist: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(6618), 0)
				+ "|Scorpia: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(6615), 0)
				+ "|Barrows Chests: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBarrowsChestCount(), 0) + "|Zulrah: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(Zulrah.ID), 0)
				+ "|TzTok-Jad: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(3127), 0)
				+ "|Kraken: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(494), 0)
				+ "|Thermonuclear Smoke Devil: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(499), 0)
				+ "|Cerberus: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(Cerberus.NPC_ID),
						0)
				+ "|Abyssal Sire: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(5886), 0)
				+ "|Skotizo: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(7286), 0)

				// + "|Wintertodt: " +
				// Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(Wintertodt
				// id here once its added), 0)
				+ "|Obor: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(7416), 0)
				// + "|Chambers of Xeric: " +
				// Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get("Great
				// olm"), 0)//fix this one maybe
				+ "|Lizardman Shaman: "
				+ Helpers.fallback(player.getDatabaseEntity().getStatistics().getBossKillCount().get(6766), 0)// fix
																												// this
																												// one
																												// maybe
				, "Boss Kill Log", 0 }, "Iss");

		player.setAttribute("boss_log_manager", true);

		// player.sendAccess(Access.of(187, 3, NumberRange.of(0, 170),
		// AccessBits.CLICK_CONTINUE));//Only need to send this when the cs2 is using
		// options

	}

	/**
	 * Handles the Options
	 */
	public static boolean handleBossLogOptions(Player player, int option) {// Not needed for this manager since we are
																			// displaying info and don't need it to be
																			// clickable
		switch (option) {

		}
		return false;
	}

}
