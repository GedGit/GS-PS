package org.rs2server.rs2.content.minigames;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.npc.NPCDefinition;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.Misc;

import java.util.ArrayList;

/**
 * For the barrelchest minigame.
 * @author Canownueasy
 */
@SuppressWarnings("static-access")
public class Barrelchest {
	
	public boolean GAME_RUNNING = false, GAME_STARTING = false;
	
	public void update() {
		for(final Player pz : players) {
			if(pz != null) {
				if(!pz.getInterfaceState().isInterfaceOpen(194)) {
					pz.getActionSender().sendInterface(194, true);
					pz.getActionSender().sendWalkableInterface(194);
				}
				pz.getActionSender().sendString(194, 1, "Waves of Guthix");
				pz.getActionSender().sendString(194, 2, "Wave: ");
				pz.getActionSender().sendString(194, 3, " " + Integer.toString(wave));
				pz.getActionSender().sendString(194, 4, "Allies left: ");
				pz.getActionSender().sendString(194, 9, Integer.toString((players.size() - 1)));
				pz.getActionSender().sendString(194, 5, "Enemies left: ");
				pz.getActionSender().sendString(194, 10, Integer.toString(npcs.size()));
				pz.getActionSender().sendString(194, 6, "WOG Points: ");
				pz.getActionSender().sendString(194, 11, Integer.toString(pz.barrelPoints));
				int blank[] = { 7, 12, 8, 13 };
				for(int i : blank) {
					pz.getActionSender().sendString(194, i, "");	
				}
			}
		}
		if(!GAME_RUNNING) {
			return;
		}
		if(npcs.size() < 1) {
			progressWave();
		}
	}
	
	/**
	 * Captivates the wave.
	 */
	public int wave = -1;
	
	/**
	 * Holds all NPCs of the game.
	 */
	public ArrayList<NPC> npcs = new ArrayList<NPC>();
	
	/**
	 * Holds all players of the game.
	 */
	public ArrayList<Player> players = new ArrayList<Player>();
	
	/**
	 * Enters a game for a player.
	 * @param player The player who is entering.
	 * @param teleport If the player needs to be teleported or not.
	 */
	public void enterGame(final Player player, final boolean teleport) {
		players.add(player);
		for(Player pz : players) {
			if(pz != null && pz != player) {
				pz.getActionSender().sendMessage(player.getName() + " has joined the forces!");
			}
		}
		if(teleport) {
			player.setTeleportTarget(Misc.random(1) > 0 ? Location.create(2759, 10064) : Location.create(2807, 10105));
		}
		player.getActionSender().sendInterface(194, true);
		player.getActionSender().sendWalkableInterface(194);
		if(players.size() >= 3 && !GAME_RUNNING && !GAME_STARTING) {
			for(final Player pz : players) {
				if(pz != null) {
					GAME_STARTING = true;
					pz.getActionSender().sendMessage("Game starting in... 5 seconds.");
					World.getWorld().submit(new Event(5000) {
						public void execute() {
							if(players.size() < 3) {
								pz.getActionSender().sendMessage("[NOTICE]: Game could not start. Not enough players.");
								GAME_STARTING = false;
								this.stop(); return;
							}
							startGame();
							this.stop();
						}
					});
				}
			}
		}
	}
	
	/**
	 * Makes the player leave the game.
	 * @param player The player to leave.
	 */
	public void leaveGame(final Player player) {
		players.remove(player);
		player.setTeleportTarget(player.DEFAULT_LOCATION);
		player.getActionSender().removeAllInterfaces();
		for(Player pz : players) {
			if(pz != null && pz != player) {
				pz.getActionSender().sendMessage(player.getName() + " has left the forces!");
			}
		}
	}
	
	public void death(final Player player) {
	/*	if(player.getRights() != Rights.ADMINISTRATOR) {
			player.barrelWait = 300;
		}*/
		player.barrelWait = 300;
		players.remove(player);
		player.getActionSender().removeAllInterfaces();
		for(Player pz : players) {
			if(pz != null && pz != player) {
				pz.getActionSender().sendMessage(player.getName() + " put up a strong-willed fight, but was sadly killed.");
			}
		}
	}
	
	public void startGame() {
		GAME_STARTING = false;
		progressWave();
		GAME_RUNNING = true;
	}
	
	public void endGame(boolean won) {
		for(NPC nz : npcs) {
			if(nz != null) {
				World.getWorld().unregister(nz);
			}
		}
		final ArrayList<Player> completed = new ArrayList<Player>();
		for(Player pz : players) {
			if(pz != null && !completed.contains(pz)) {
				pz.setTeleportTarget(pz.DEFAULT_LOCATION);
				pz.getActionSender().removeAllInterfaces();
				if(won) {
					pz.getActionSender().sendMessage("Congratulations! You've defeated Barrelchest!");
					pz.getActionSender().sendMessage("You are rewarded an extra 15 points for completion of the whole game.");
					pz.barrelPoints += 15;
				} else {
					pz.getActionSender().sendMessage("Game ended because not enough forces left.");
				}
				completed.add(pz);
			}
		}
		wave = 0;
		GAME_RUNNING = false;
		completed.clear();
		players.clear();
	}
	
	public void progressWave() {
		if(!GAME_RUNNING) {
			return;
		}
		if(wave >= 10) {
			endGame(true);
			return;
		}
		wave++;
		spawnNPC();
	}
	
	public void spawn(final int id, final Location loc) {
		final NPC npc = new NPC(NPCDefinition.forId(id), loc, loc, loc, Misc.random(3));
		npcs.add(npc);
		World.getWorld().register(npc);
		npc.setAggressiveDistance(15);
	}
	
	private void spawnNPC() {
		switch(wave) {
		case 1:
			spawn(5308, Location.create(2773, 10074));
			spawn(5307, Location.create(2779, 10072));
			spawn(5306, Location.create(2785, 10074));
			spawn(5305, Location.create(2770, 10080));
			spawn(5304, Location.create(2771, 10089));
			spawn(5306, Location.create(2775, 10093));
			spawn(5303, Location.create(2768, 10097));
			spawn(5305, Location.create(2760, 10089));
			spawn(5307, Location.create(2762, 10082));
			spawn(5306, Location.create(2772, 10056));
			spawn(5304, Location.create(2801, 10063));
			spawn(5308, Location.create(2789, 10085));
			spawn(5304, Location.create(2784, 10102));
			break;
		case 2:
			spawn(5303, Location.create(2777, 10067));
			spawn(5304, Location.create(2788, 10070));
			spawn(5305, Location.create(2789, 10079));
			spawn(5306, Location.create(2798, 10070));
			spawn(5307, Location.create(2801, 10062));
			spawn(5308, Location.create(2789, 10060));
			spawn(5309, Location.create(2773, 10056));
			spawn(5310, Location.create(2771, 10080));
			spawn(5305, Location.create(2771, 10094));
			spawn(5306, Location.create(2782, 10091));
			spawn(5309, Location.create(2789, 10083));
			spawn(5306, Location.create(2784, 10103));
			spawn(5304, Location.create(2768, 10099));
			spawn(5303, Location.create(2761, 10086));
			spawn(5311, Location.create(2774, 10078));
			for(Player pz : players) {
				if(pz != null && pz.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					pz.barrelPoints += wave;
				}
			}
			break;
		case 3:
			spawn(5308, Location.create(2772, 10072));
			spawn(5309, Location.create(2787, 10071));
			spawn(5312, Location.create(2788, 10079));
			spawn(5313, Location.create(2776, 10092));
			spawn(5304, Location.create(2766, 10097));
			spawn(5312, Location.create(2783, 10103));
			spawn(5313, Location.create(2789, 10078));
			spawn(5313, Location.create(2772, 10057));
			spawn(5304, Location.create(2775, 10055));
			spawn(5308, Location.create(2788, 10061));
			spawn(5309, Location.create(2800, 10060));
			spawn(5303, Location.create(2799, 10068));
			spawn(5314, Location.create(2769, 10084));
			spawn(5304, Location.create(2760, 10085));
			spawn(5313, Location.create(2761, 10090));
			spawn(5307, Location.create(2774, 10094));
			spawn(5304, Location.create(2781, 10095));
			for(Player pz : players) {
				if(pz != null && pz.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					pz.barrelPoints += wave;
				}
			}
			break;
		case 4:
			spawn(5304, Location.create(2777, 10071));
			spawn(5305, Location.create(2770, 10083));
			spawn(5314, Location.create(2776, 10086));
			spawn(5315, Location.create(2771, 10096));
			spawn(5305, Location.create(2760, 10089));
			spawn(5303, Location.create(2761, 10084));
			spawn(5309, Location.create(2773, 10055));
			spawn(5316, Location.create(2769, 10057));
			spawn(5306, Location.create(2788, 10060));
			spawn(5305, Location.create(2785, 10069));
			spawn(5304, Location.create(2790, 10081));
			spawn(5303, Location.create(2784, 10096));
			spawn(5305, Location.create(2774, 10100));
			spawn(5316, Location.create(2779, 10073));
			spawn(5309, Location.create(2785, 10069));
			for(Player pz : players) {
				if(pz != null && pz.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					pz.barrelPoints += wave;
				}
			}
			break;
		case 5:
			spawn(5303, Location.create(2777, 10071));
			spawn(5307, Location.create(2781, 10072));
			spawn(5308, Location.create(2785, 10076));
			spawn(5313, Location.create(2783, 10085));
			spawn(5314, Location.create(2772, 10084));
			spawn(5315, Location.create(2771, 10076));
			spawn(5327, Location.create(2785, 10071));
			spawn(5325, Location.create(2776, 10067));
			spawn(5312, Location.create(2772, 10055));
			spawn(5306, Location.create(2770, 10057));
			spawn(5329, Location.create(2788, 10070));
			spawn(5308, Location.create(2778, 10091));
			spawn(5304, Location.create(2783, 10104));
			spawn(5313, Location.create(2766, 10097));
			spawn(5314, Location.create(2758, 10087));
			spawn(5328, Location.create(2769, 10079));
			spawn(5317, Location.create(2772, 10087));
			spawn(5375, Location.create(2780, 10091));
			spawn(5331, Location.create(2771, 10095));
			spawn(5306, Location.create(2765, 10095));
			spawn(5313, Location.create(2778, 10072));
			spawn(5321, Location.create(2791, 10069));
			spawn(5320, Location.create(2802, 10064));
			spawn(5310, Location.create(2802, 10070));
			spawn(5311, Location.create(2792, 10057));
			spawn(5309, Location.create(2781, 10056));
			for(Player pz : players) {
				if(pz != null && pz.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					pz.barrelPoints += wave;
				}
			}
			break;
		case 6:
			spawn(5328, Location.create(2781, 10072));
			spawn(5308, Location.create(2773, 10078));
			spawn(5376, Location.create(2783, 10091));
			spawn(5375, Location.create(2771, 10096));
			spawn(5307, Location.create(2770, 10092));
			spawn(5313, Location.create(2759, 10085));
			spawn(5314, Location.create(2760, 10090));
			spawn(5315, Location.create(2773, 10076));
			spawn(5377, Location.create(2785, 10071));
			spawn(5377, Location.create(2784, 10059));
			spawn(5378, Location.create(2773, 10082));
			spawn(5330, Location.create(2785, 10066));
			spawn(5329, Location.create(2778, 10070));
			spawn(5327, Location.create(2770, 10058));
			spawn(5326, Location.create(2774, 10055));
			spawn(5304, Location.create(2788, 10060));
			spawn(5379, Location.create(2798, 10069));
			spawn(5380, Location.create(2787, 10085));
			spawn(5308, Location.create(2790, 10080));
			spawn(5306, Location.create(2783, 10099));
			spawn(5312, Location.create(2780, 10094));
			spawn(5311, Location.create(2766, 10100));
			for(Player pz : players) {
				if(pz != null && pz.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					pz.barrelPoints += wave;
				}
			}
			break;
		case 7:
			spawn(5317, Location.create(2779, 10089));
			spawn(5313, Location.create(2773, 10085));
			spawn(5379, Location.create(2772, 10079));
			spawn(5398, Location.create(2758, 10085));
			spawn(5314, Location.create(2766, 10096));
			spawn(5377, Location.create(2772, 10101));
			spawn(5399, Location.create(2783, 10096));
			spawn(5308, Location.create(2771, 10090));
			spawn(5399, Location.create(2775, 10076));
			spawn(5398, Location.create(2767, 10067));
			spawn(5400, Location.create(2787, 10063));
			spawn(5378, Location.create(2790, 10072));
			spawn(5303, Location.create(2790, 10082));
			spawn(5312, Location.create(2783, 10084));
			spawn(5314, Location.create(2785, 10098));
			spawn(5315, Location.create(2779, 10104));
			spawn(5376, Location.create(2768, 10099));
			spawn(5331, Location.create(2759, 10089));
			spawn(5328, Location.create(2763, 10083));
			spawn(5326, Location.create(2772, 10082));
			spawn(5317, Location.create(2783, 10068));
			spawn(5313, Location.create(2797, 10069));
			spawn(5330, Location.create(2800, 10060));
			spawn(5318, Location.create(2789, 10060));
			spawn(5305, Location.create(2785, 10066));
			spawn(5303, Location.create(2781, 10069));
			spawn(5378, Location.create(2772, 10072));
			spawn(5379, Location.create(2764, 10082));
			for(Player pz : players) {
				if(pz != null && pz.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					pz.barrelPoints += wave;
				}
			}
			break;
		case 8:
			spawn(5314, Location.create(2785, 10086));
			spawn(5327, Location.create(2772, 10084));
			spawn(5317, Location.create(2774, 10092));
			spawn(5303, Location.create(2790, 10077));
			spawn(5400, Location.create(2789, 10082));
			spawn(5399, Location.create(2784, 10069));
			spawn(5398, Location.create(2772, 10075));
			spawn(5378, Location.create(2760, 10084));
			spawn(5376, Location.create(2760, 10089));
			spawn(5377, Location.create(2771, 10097));
			spawn(5312, Location.create(2774, 10089));
			spawn(5403, Location.create(2773, 10073));
			spawn(5406, Location.create(2790, 10060));
			spawn(5375, Location.create(2801, 10062));
			spawn(5316, Location.create(2798, 10071));
			spawn(5314, Location.create(2784, 10066));
			spawn(5308, Location.create(2782, 10059));
			spawn(5307, Location.create(2775, 10055));
			spawn(5325, Location.create(2769, 10057));
			spawn(5313, Location.create(2782, 10067));
			spawn(5331, Location.create(2784, 10074));
			spawn(5376, Location.create(2788, 10082));
			spawn(5317, Location.create(2774, 10069));
			spawn(5327, Location.create(2773, 10090));
			spawn(5326, Location.create(2775, 10093));
			spawn(5306, Location.create(2782, 10098));
			spawn(5380, Location.create(2779, 10103));
			spawn(5378, Location.create(2772, 10098));
			spawn(5379, Location.create(2765, 10096));
			spawn(5403, Location.create(2776, 10091));
			for(Player pz : players) {
				if(pz != null && pz.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					pz.barrelPoints += wave;
				}
			}
			break;
		case 9:
			spawn(5408, Location.create(2787, 10071));
			spawn(5317, Location.create(2782, 10067));
			spawn(5317, Location.create(2792, 10060));
			spawn(5317, Location.create(2791, 10081));
			spawn(5380, Location.create(2786, 10076));
			spawn(5398, Location.create(2783, 10072));
			spawn(5400, Location.create(2775, 10067));
			spawn(5403, Location.create(2784, 10060));
			spawn(5406, Location.create(2773, 10054));
			spawn(5399, Location.create(2780, 10060));
			spawn(5328, Location.create(2776, 10071));
			spawn(5331, Location.create(2776, 10075));
			spawn(5330, Location.create(2776, 10085));
			spawn(5303, Location.create(2781, 10094));
			spawn(5304, Location.create(2774, 10095));
			spawn(5307, Location.create(2763, 10091));
			spawn(5310, Location.create(2760, 10087));
			spawn(5311, Location.create(2770, 10081));
			spawn(5312, Location.create(2775, 10079));
			spawn(5313, Location.create(2784, 10077));
			spawn(5378, Location.create(2791, 10087));
			spawn(5399, Location.create(2783, 10090));
			spawn(5403, Location.create(2785, 10102));
			spawn(423, Location.create(2780, 10104));
			spawn(5400, Location.create(2772, 10078));
			spawn(5331, Location.create(2778, 10073));
			spawn(5326, Location.create(2787, 10072));
			spawn(5324, Location.create(2788, 10078));
			spawn(5400, Location.create(2784, 10102));
			for(Player pz : players) {
				if(pz != null && pz.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					pz.barrelPoints += wave;
				}
			}
			break;
		case 10:
			spawn(5666, Location.create(2774, 10076));
			spawn(424, Location.create(2785, 10086));
			spawn(423, Location.create(2769, 10085));
			spawn(424, Location.create(2778, 10075));
			spawn(5317, Location.create(2786, 10075));
			spawn(5317, Location.create(2784, 10067));
			spawn(5317, Location.create(2775, 10072));
			spawn(5380, Location.create(2772, 10092));
			spawn(5398, Location.create(2780, 10094));
			spawn(5399, Location.create(2785, 10083));
			spawn(5378, Location.create(2787, 10078));
			spawn(5376, Location.create(2791, 10069));
			spawn(5328, Location.create(2784, 10061));
			spawn(5303, Location.create(2781, 10058));
			spawn(5304, Location.create(2777, 10055));
			spawn(5310, Location.create(2773, 10055));
			spawn(5320, Location.create(2770, 10057));
			spawn(5328, Location.create(2776, 10071));
			spawn(5331, Location.create(2776, 10075));
			spawn(5330, Location.create(2776, 10085));
			spawn(5303, Location.create(2781, 10094));
			spawn(5304, Location.create(2774, 10095));
			spawn(5307, Location.create(2763, 10091));
			spawn(5310, Location.create(2760, 10087));
			spawn(5311, Location.create(2770, 10081));
			spawn(5312, Location.create(2775, 10079));
			spawn(5313, Location.create(2784, 10077));
			spawn(5314, Location.create(2760, 10090));
			spawn(5315, Location.create(2773, 10076));
			spawn(5377, Location.create(2785, 10071));
			spawn(5377, Location.create(2784, 10059));
			spawn(5378, Location.create(2773, 10082));
			spawn(5330, Location.create(2785, 10066));
			spawn(5329, Location.create(2778, 10070));
			spawn(5327, Location.create(2770, 10058));
			spawn(5326, Location.create(2774, 10055));
			spawn(5304, Location.create(2788, 10060));
			spawn(5379, Location.create(2798, 10069));
			spawn(5380, Location.create(2787, 10085));
			for(Player pz : players) {
				if(pz != null && pz.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					pz.barrelPoints += wave;
				}
			}
			break;
		}
	}

}
