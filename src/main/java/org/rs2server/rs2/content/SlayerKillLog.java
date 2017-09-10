package org.rs2server.rs2.content;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.Helpers;

public class SlayerKillLog {
	/**
	 * Sends the Interface/CS2Script/Access Mask to the entity.
	 */
	public static void handleSlayerLog(Player player) {
	//	final CacheNPCDefinition npcDefinition = new CacheNPCDefinition();
	//	final int npcId = npcDefinition.getId();
		
		player.getActionSender().sendInterface(187, false);
		player.getActionSender().sendCS2Script(217, new Object[] {
				
		  "Crawling hands: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get("Crawling hand"), 0)
		+ "|Cave bugs: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get("Cave bug"), 0)
		+ "|Cave crawlers: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get("Cave crawler"), 0)
		+ "|Banshees: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get("Banshee"), 0)		
		+ "|Cave slime: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get("Cave slime"), 0)
		+ "|Rockslugs: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get("Rockslug"), 0)
		+ "|Desert lizards: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get("Desert lizard"), 0)
		+ "|Cockatrice: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Cockatrice"), 0)
		+ "|Pyrefiends: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Pyrefiend"), 0)
		+ "|Mogres: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Morgre"), 0)
		+ "|Harpie bug swarms: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Harpie bug swarm"), 0)
		+ "|Wall beasts: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Wall beast"), 0)
		+ "|Killerwatts: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Killerwatt"), 0)
		+ "|Molanisks: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Molanisk"), 0)
		+ "|Basilisks: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Basilisk"), 0)
		+ "|Sea snakes: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Sea snake"), 0)
		+ "|Terror dogs: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Terror dog"), 0)
		+ "|Fever spiders: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Fever spider"), 0)
		+ "|Infernal mages: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Infernal mage"), 0)
		+ "|Brine rats: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Brine rat"), 0)
		+ "|Bloodvelds: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Bloodveld"), 0)
		+ "|Jellies: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Jellie"), 0)
		+ "|Turoth: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Turoth"), 0)
		+ "|Mutated zygomites: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Mutated zygomite"), 0)
		+ "|Cave horrors: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Cave horror"), 0)
		+ "|Aberrant spectres: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Aberrant spectre"), 0)
		+ "|Spiritual rangers: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Spiritual ranger"), 0)
		+ "|Dust devils: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Dust devil"), 0)
		+ "|Spiritual warriors: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Spiritual warrior"), 0)
		+ "|Kurask: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Kurask"), 0)
		+ "|Skeletal wyverns: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Skeletal wyvern"), 0)
		+ "|Gargoyles: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Gargoyle"), 0)
		+ "|Nechryael: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Nechryael"), 0)
		+ "|Spiritual mages: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Spiritual mage"), 0)
		+ "|Abyssal demons: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Abyssal demon"), 0)
		+ "|Cave Kraken: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Cave Kraken"), 0)
		+ "|Dark beasts: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Dark beast"), 0)
		+ "|Smoke devils: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Smoke devil"), 0)
		+ "|Superior Creatures: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Superior Creature"), 0)
		+ "|Rock crabs: " + Helpers.fallback(player.getDatabaseEntity().getStatistics().getSlayerMonsterKillCount().get( "Rock crab"), 0)
		, "Slayer Kill Log", 0}, "Iss");
		
		player.setAttribute("slayerlogmanager", true);
		
//		player.sendAccess(Access.of(187, 3, NumberRange.of(0, 170), AccessBits.CLICK_CONTINUE));//Only need to send this when the cs2 is using options
	
	}
	
	/**
	  * Handles the Options
	 */
	public static boolean handleSlayerLogOptions(Player player, int option) {
		switch (option) {
		
		
		}
		return false;
	}
}

