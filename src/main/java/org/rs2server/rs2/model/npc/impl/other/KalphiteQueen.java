package org.rs2server.rs2.model.npc.impl.other;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.npc.*;
import org.rs2server.rs2.tickable.Tickable;

/**
 * Handles the Kalphite Queen NPC.
 * 
 * @author Vichy
 */
public final class KalphiteQueen extends NPC {

	/**
	 * Lets build the NPC and make it instanced to the player.
	 * 
	 * @param player
	 *            the player encountering the npc
	 * @param id
	 *            the npc id to spawn
	 * @param loc
	 *            the location to spawn on
	 */
	public KalphiteQueen(int id, Location loc) {
		super(id, loc);
		phaseTwo = false;
	}
	
	private boolean phaseTwo;

	/**
	 * Starts second phase.
	 */
	public void transform() {
		playAnimation(Animation.create(6242, 20));
		setAttackable(false);
		World.getWorld().submit(new Tickable(3) {
			@Override
			public void execute() {
				transformNPC(6501);
				getSkills().setLevel(Skills.HITPOINTS, 255);
				setAttackable(true);
				getCombatState().setDead(false);
				phaseTwo = true;
				this.stop();
			}
		});
	}
	
	@Override
	public void dropLoot(Mob killer) {
		this.transformNPC(6500);
		this.phaseTwo = false;
		super.dropLoot(killer);
	}
	
	@Override
	public Animation getDeathAnimation() {
		return Animation.create(6233);
	}
	
	@Override
	public Animation getDefendAnimation() {
		return Animation.create(isSecondPhase() ? 6237 : -1);
	}
	
	public boolean isSecondPhase() {
		return phaseTwo;
	}
}