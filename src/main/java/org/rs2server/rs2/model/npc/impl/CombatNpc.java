package org.rs2server.rs2.model.npc.impl;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.tickable.Tickable;

import java.util.Random;

/**
 * 
 * @author Twelve
 * 
 */
public abstract class CombatNpc<T extends CombatNpc<?>> extends NPC {

	private final CustomCombatAction defaultCombatAction = new CustomCombatAction();
	protected Random random = new Random();
	protected NpcCombatState<T> state;

	public CombatNpc(int id, Location loc) {
		super(id, loc, loc, loc, 0);
	}

	@Override
	public void tick() {
		super.tick();
		doCombat();
	}

	public void doCombat() {
		if (isTurn()) {
			state.perform();
		}
	}

	public void transition(NpcCombatState<T> to) {
		this.state = to;
	}

	public NpcCombatState<T> getState() {
		return state;
	}

	@Override
	public boolean canHit(Mob victim, boolean messages) {
		if (victim.isPlayer()) {
			if (instancedPlayer != null && instancedPlayer != victim || getInstancedPlayer() != null && getInstancedPlayer() != victim) {
				return false;
			}
		}
		return isAttackable();
	}

	public abstract boolean isTurn();

	@Override
	public CombatAction getDefaultCombatAction() {
		return defaultCombatAction;
	}

	public final class CustomCombatAction extends AbstractCombatAction {

		@Override
		public int damage(int maxHit, Mob attacker, Mob victim, CombatState.AttackType attackType, int skill, int prayer, boolean special, boolean ignorePrayers) {
			return super.damage(maxHit, attacker, victim, attackType, skill, prayer, special, ignorePrayers);
		}

		@Override
		public void hit(final Mob attacker, Mob victim) {
			super.hit(attacker, victim);
		}

		@Override
		public int distance(Mob attacker) {
			return 10;
		}

	}

	public void hit(final Mob attacker, Mob victim) {
		/**
		 * This is to prevent immediate teaming, EG: someone walking into Edgeville(1v1) and 2 people casting spells at the same time, usually it wouldn't set the timer till the damage had been inflicted
		 */
		victim.getCombatState().setLastHitTimer(10000);
		victim.getCombatState().setLastHitBy(attacker);
		victim.getCombatState().updateCombatWith(attacker);
		victim.getActionQueue().clearRemovableActions();
		/**
		 * Stops other emotes from overlapping important ones.
		 */
		attacker.getCombatState().setWeaponSwitchTimer(2);
		attacker.getCombatState().setCanAnimate(false);
		World.getWorld().submit(new Tickable(1) {
			public void execute() {
				attacker.getCombatState().setCanAnimate(true);
				this.stop();
			}
		});
	}

}
