package org.rs2server.rs2.model.minigame.magearena;

import com.google.common.collect.Iterables;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

import java.util.ArrayList;
import java.util.Iterator;

public class MageArena {

	private Player player;

	private MageArenaWave wave;
	private int startTime = -1;
	private boolean startedFightWave;
	private boolean started;

	public static ArrayList<Player> IN_ARENA = new ArrayList<>();

	private static final Location ARENA = Location.create(3103, 3934, 0);

	private String[] messages = { "", "You must prove yourself... now!",
			"This is only the beginning; you can't beat me!", "Foolish mortal; I am unstoppable!",
			"Now you feel it... The dark energy!", "Aaaaaaaaarrgghhhhh! The power." };

	public MageArena(Player player) {
		this.player = player;
	}

	public void start() {
		player.setTeleportTarget(ARENA);
		player.setMultiplayerDisabled(true);
		World.getWorld().submit(new Tickable(1) {
			public void execute() {
				stop();
				started = true;
				IN_ARENA.add(player);
			}
		});
		player.getActionSender().sendAreaInterface(null, ARENA);
	}

	public void tick() {
		if (!started || !IN_ARENA.contains(player)) {
			return;
		}
		if (!startedFightWave) {
			startedFightWave = true;
			startTime = 3;
		}
		if (startTime > 0) {
			startTime--;
		} else if (startTime == 0) {
			startTime = -1;
			if (wave == null) {
				wave = new MageArenaWave();
				wave.set(1);
			}
			int[] spawns = wave.spawns();
			for (int spawn : spawns) {
				Location spawnLoc = Location.create(3108, 3934, 0);
				NPC npc = new NPC(spawn, spawnLoc, spawnLoc, spawnLoc, 0);
				player.getInstancedNPCs().add(npc);
				World.getWorld().getNPCs().add(npc);
				npc.instancedPlayer = player;
				npc.setTeleporting(false);
				npc.setLocation(npc.getSpawnLocation());
				npc.loadCombatDefinition();
				npc.getCombatState().startAttacking(player, player.isAutoRetaliating());
				npc.forceChat(messages[wave.getStage()]);
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
				if (player.getInstancedNPCs().isEmpty()) {
					if (wave.getStage() == 5) {
						player.getSettings().setMageArenaState(1);
						player.getSettings().setCompletedMageArena(true);
						player.setTeleportTarget(Location.create(2539, 4715, 0));
						player.setMultiplayerDisabled(false);
						wave = null;
						startTime = -1;
						IN_ARENA.remove(player);
						started = false;
					} else {
						wave.set(wave.getStage() + 1);
						startTime = 3;
					}
				}
			}
		}
	}

	public void appendDeath() {
		Iterables.consumingIterable(player.getInstancedNPCs()).forEach(World.getWorld()::unregister);
		IN_ARENA.remove(player);
		startTime = -1;
		player.getSettings().setMageArenaState(1);
		player.setMultiplayerDisabled(false);
		player.getInstancedNPCs().clear();
		wave = null;
	}

	public void stop() {
		Iterables.consumingIterable(player.getInstancedNPCs()).forEach(World.getWorld()::unregister);
		IN_ARENA.remove(player);
		startTime = -1;
		player.getSettings().setMageArenaState(1);
		player.setMultiplayerDisabled(false);
		player.getInstancedNPCs().clear();
		wave = null;
		started = false;
		player.setTeleportTarget(Location.create(3104, 3956, 0));
	}

	public boolean isStarted() {
		return started;
	}

}
