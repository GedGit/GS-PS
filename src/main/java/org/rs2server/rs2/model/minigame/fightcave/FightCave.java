package org.rs2server.rs2.model.minigame.fightcave;

import com.google.common.collect.Iterables;
import org.rs2server.rs2.domain.model.player.PlayerSettingsEntity;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.npc.Pet;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.mysql.impl.NewsManager;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/** 
 * @author 'Mystic Flow
 */
public class FightCave {

	private Player player;
	private Wave wave;
	private int startTime = -1;
	private boolean startedFightWave;
	private boolean started;

	private static Random r = new Random();

	public static ArrayList<Player> IN_CAVES = new ArrayList<>();

	public static final Location maxLocation = Location.create(2431, 5119, 0);

	public static final Location minLocation = Location.create(2368, 5056, 0);

	public FightCave(Player player) {
		this.player = player;
	}

	public Wave getWave() {
		return wave;
	}

	public void setWave(Wave wave) {
		this.wave = wave;
	}

	public void start() {
		player.setTeleportTarget(Location.create(2413, 5117, 0));
		player.setMultiplayerDisabled(true);
		wave = null;
		World.getWorld().submit(new Tickable(1) {
			public void execute() {
				stop();
				DialogueManager.openDialogue(player, 22);
				started = true;
				IN_CAVES.add(player);
			}
		});
	}

	public void tick() {
		if (!started || !IN_CAVES.contains(player)) {
			return;
		}
		if (!startedFightWave) {
			startedFightWave = true;
			startTime = 10;
		}
		if (startTime > 0) {
			startTime--;
		} else if (startTime == 0) {
			startTime = -1;
			if (wave == null) {
				wave = new Wave();
				wave.set(1);
			}
			player.getActionSender().sendMessage("Now starting wave " + wave.getStage() + ".");

			int[] spawns = wave.spawns();
			for (int spawn : spawns) {
				Location spawnLoc = getRandomLocation(player);
				NPC npc = new NPC(spawn, spawnLoc, minLocation, maxLocation, 0);
				player.getInstancedNPCs().add(npc);
				World.getWorld().getNPCs().add(npc);
				npc.instancedPlayer = player;
				npc.setTeleporting(false);
				npc.setLocation(npc.getSpawnLocation());
				npc.loadCombatDefinition();
				npc.getCombatState().startAttacking(player, player.isAutoRetaliating());
			}
		} else if (startTime == -1) {
			if (player.isMultiplayerDisabled() && wave != null) {
				Iterator<NPC> it = player.getInstancedNPCs().iterator();
				while (it.hasNext()) {
					NPC n = it.next();
					if (n.getAttributes().containsKey("died")) {
						it.remove();
					} else {
						if (n.getInteractingEntity() != player)
							n.getCombatState().startAttacking(player, player.isAutoRetaliating());
					}
				}
				/*Checks invent, if invent is full, fire cape is banked else placed in inv 
				 * tokkul is dropped when inv is full*/
				if (player.getInstancedNPCs().isEmpty()) {
					if (wave != null && wave.getStage() == 9) {
						player.getActionSender().sendMessage("Congratulations, you have finished every wave!");
						
						//fire cape
						if (!player.getInventory().add(new Item(6570, 1))) { 
							if(!player.getInventory().add(new Item(6529, 8024))) { 
								
							//Tokkul 
							player.getBank().add(new Item(6570, 1)); //adding it to bank if invent is full
							Inventory.addDroppable(player, new Item(6529, 8024)); //dropping item to floor if inv is full
							player.getActionSender().sendMessage("You have received a firecape, it has been banked.");

						} else
							
							//if fire cape is put into invent, this message will appear 
							player.getActionSender().sendMessage("You have received a firecape.");
							
							// Spawning Jad Pet.
						if (Misc.random(100) == 1 && player.getPet() == null) {
							PlayerSettingsEntity settings = player.getDatabaseEntity().getPlayerSettings();
							Pet pet = new Pet(player, 5892);
							player.setPet(pet);
							settings.setPetSpawned(true);
							settings.setPetId(5892);
							World.getWorld().register(pet);
							World.getWorld().sendWorldMessage("<col=ff0000><img=24>Server</col>: " + player.getName()
									+ " has just received a TzRek-Jad pet.");

							new Thread(new NewsManager(player, "<img src='../resources/news/tzrek-jad.png' "
									+ "width=13> received a TzRek-Jad pet.")).start();
						}
						player.getSettings().setFightCaveState(1);
						player.setTeleportTarget(Location.create(2438, 5168, 0));
						player.setAttribute("defeated_caves", true);
						player.setMultiplayerDisabled(false);
						IN_CAVES.remove(player);
					} else if (wave != null && wave.getStage() != 9) {
						player.getActionSender()
								.sendMessage("Finished wave " + wave.getStage() + "; starting the next wave soon.");
						wave.set(wave.getStage() + 1);
						startTime = 10;
					}
				}
			}
		}
		}
	}

	public static Location getRandomLocation(Player player) {
		final int[] locationSet = LOCATIONS[r.nextInt(LOCATIONS.length)];
		final int x = r.nextInt(locationSet[2] - locationSet[0]) + locationSet[0];
		final int y = r.nextInt(locationSet[3] - locationSet[1]) + locationSet[1];
		final int z = player.getLocation().getPlane();
		return Location.create(x, y, z);
	}

	private static int[][] LOCATIONS = { { 2376, 5065, 2387, 5076 }, { 2376, 5099, 2387, 5112 },
			{ 2408, 5103, 2419, 5113 }, { 2412, 5078, 2422, 5086 }, };

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean b) {
		this.started = b;
	}

	public void stop() {
		Iterables.consumingIterable(player.getInstancedNPCs()).forEach(World.getWorld()::unregister);
		IN_CAVES.remove(player);
		startTime = -1;
		player.getSettings().setFightCaveState(1);
		player.setMultiplayerDisabled(false);
		player.getInstancedNPCs().clear();
		wave = null;
		started = false;
		if (!player.getAttributes().containsKey("defeated_caves"))
			player.getActionSender().sendMessage("Your session has ended.");
		player.setTeleportTarget(Location.create(2438, 5168, 0));
	}

	public void setStartedWave(boolean b) {
		this.startedFightWave = b;
	}

	public void setStartTime(int time) {
		this.startTime = time;
	}
}
