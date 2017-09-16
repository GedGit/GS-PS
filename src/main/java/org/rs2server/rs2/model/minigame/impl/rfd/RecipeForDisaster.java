package org.rs2server.rs2.model.minigame.impl.rfd;

import com.google.common.collect.Iterables;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.tickable.Tickable;

import java.util.Iterator;

/**
 * Handles the Recipe for Disaster mini-quest boss fights.
 * 
 * @author Vichy
 */
public class RecipeForDisaster {

	/**
	 * Initiating the Player.
	 */
	private Player player;

	/**
	 * Initiating the wave instance.
	 */
	private RFDWave wave;

	/**
	 * Initiating the start timer as integer.
	 */
	private int startTime = -1;

	/**
	 * Initiating whether the wave has begun.
	 */
	private boolean startedFightWave;

	/**
	 * Initiating whether the mini-quest has starter.
	 */
	private boolean started;

	/**
	 * Constructing the class for our player.
	 * 
	 * @param player
	 *            the player.
	 */
	public RecipeForDisaster(Player player) {
		this.player = player;
	}

	/**
	 * Gets the wave instance.
	 * 
	 * @return the wave.
	 */
	public RFDWave getWave() {
		return wave;
	}

	/**
	 * Sets the wave instance.
	 * 
	 * @param wave
	 *            the wave.
	 */
	public void setWave(RFDWave wave) {
		this.wave = wave;
	}

	/**
	 * Starts the mini-quest.
	 */
	public void start() {
		player.setTeleportTarget(Location.create(1899, 5365, 2));
		player.setMultiplayerDisabled(true);
		player.getCombatState().resetPrayers();
		World.getWorld().submit(new Tickable(1) {
			@Override
			public void execute() {
				stop();
				if (player.getSettings().getRFDState() == 4) {
					player.getActionSender().sendMessage("You've already completed this minigame.");
					return;
				}
				started = true;
				if (wave == null) {
					wave = new RFDWave();
					wave.set(player.getSettings().getRFDState());
				}
			}
		});
	}

	/**
	 * What to do on each game tick sequence.
	 */
	public void tick() {
		if (!started)
			return;
		if (!startedFightWave) {
			startedFightWave = true;
			startTime = 10;
		}
		if (startTime > 0)
			startTime--;
		else if (startTime == 0) {
			startTime = -1;
			if (wave == null) {
				wave = new RFDWave();
				wave.set(0);
			}
			player.getActionSender().sendMessage("Now starting wave " + wave.getStage() + ".");

			int[] spawns = wave.spawns();

			for (int spawn : spawns) {
				Location l = Location.create(1899, 5360, 2);
				NPC npc = new NPC(spawn, l, Location.create(3572, 3293), Location.create(3578, 3301), 0);
				player.getInstancedNPCs().add(npc);
				npc.instancedPlayer = player;
				World.getWorld().register(npc);
				npc.getCombatState().startAttacking(player, player.isAutoRetaliating());
			}
		} else if (startTime == -1) {
			if (player.isMultiplayerDisabled() && wave != null) {
				Iterator<NPC> it = player.getInstancedNPCs().iterator();
				while (it.hasNext()) {
					NPC n = it.next();
					if (n.getAttributes().containsKey("died"))
						it.remove();
					else {
						if (n.getInteractingEntity() != player)
							n.getCombatState().startAttacking(player, player.isAutoRetaliating());
					}
				}
				if (player.getInstancedNPCs().isEmpty()) {
					if (wave.getStage() == 4) {
						started = false; // stops checking on each tick
						player.teleport(Location.create(1655, 3671, 0), 3, 3, false);
						World.getWorld().submit(new Tickable(3) {
							@Override
							public void execute() {
								player.getActionSender().sendMessage(
										"Congratulations, you have finished the Recipe for Disaster mini-quest!");
								player.getSettings().setRFDState(4);
								player.getSettings().setBestRFDState(4);
								player.setMultiplayerDisabled(false);
								player.setAttribute("defeated_rfd", true);
								player.getActionSender().sendDialogue("Cook", DialogueType.NPC, 4626,
										FacialAnimation.HAPPY,
										"Thanks for helping me defeat Culinaromancer!<br>I've granted you full access to my chest.");
								this.stop();
							}
						});
					} else {
						player.getActionSender()
								.sendMessage("Finished wave " + wave.getStage() + "; starting the next wave soon.");
						wave.set(wave.getStage() + 1);
						int currentWave = wave.getStage() - 1;
						if (currentWave > player.getSettings().getBestRFDState())
							player.getSettings().setBestRFDState(currentWave);
						startTime = 10;
					}
				}
			}
		}
	}

	/**
	 * Stop everything related to the mini-quest.
	 * 
	 * @param died
	 *            if the player died.
	 */
	public void stop(boolean died) {
		Iterables.consumingIterable(player.getInstancedNPCs()).forEach(World.getWorld()::unregister);
		startTime = -1;
		player.getSettings().setRFDState(wave.getStage());
		int currentWave = wave.getStage();
		if (currentWave > player.getSettings().getBestRFDState())
			player.getSettings().setBestRFDState(currentWave);
		player.setMultiplayerDisabled(false);
		player.getInstancedNPCs().clear();
		wave = null;
		started = false;
		if (!died) {
			player.setTeleportTarget(Location.create(1655, 3671, 0));
			if (!player.getAttributes().containsKey("defeated_rfd"))
				player.getActionSender().sendMessage("Your session has ended.");
		}
	}

	/**
	 * Checks whether the mini-quest has begun.
	 * 
	 * @return if has started.
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * Sets whether the mini-quest has begun or not.
	 * 
	 * @param started
	 *            if started or not.
	 */
	public void setStarted(boolean started) {
		this.started = started;
	}

	/**
	 * Sets whether the mini-quests wave has begun or not.
	 * 
	 * @param startedWave
	 *            if wave started or not.
	 */
	public void setStartedWave(boolean startedWave) {
		this.startedFightWave = startedWave;
	}
}