package org.rs2server.rs2.tickable.impl;

import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Hit.HitType;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.tickable.Tickable;

/**
 * An event which depletes and deals poison damage.
 * 
 * @author Michael Bull
 *
 */
public class PoisonDrainTick extends Tickable {

	/**
	 * The cycle time, in ticks.
	 */
	public static final int CYCLE_TIME = 30;

	/**
	 * The mob for who we are poisoning.
	 */
	public Mob mob;

	/**
	 * Creates the event to cycle every 18,000 milliseconds (18 seconds).
	 */
	public PoisonDrainTick(Mob mob) {
		super(CYCLE_TIME);
		this.mob = mob;
	}

	/**
	 * One damage is dealt 4 times before decreasing.
	 */
	private int drainAmount = 4;

	@Override
	public void execute() {
		int dmg = mob.getCombatState().getPoisonDamage();
		if (dmg > mob.getSkills().getLevel(Skills.HITPOINTS))
			dmg = mob.getSkills().getLevel(Skills.HITPOINTS);
		mob.inflictDamage(new Hit(HitType.POISON_HIT, dmg), null);
		drainAmount--;
		if (drainAmount == 0) {
			mob.getCombatState().decreasePoisonDamage(1);
			drainAmount = 4;
		}
	}
}