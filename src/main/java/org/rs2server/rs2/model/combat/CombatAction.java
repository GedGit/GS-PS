package org.rs2server.rs2.model.combat;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.combat.CombatState.AttackType;

/**
 * An interface that handles the logic of different types of combat, e.g.
 * ranged, magic, melee and various specials.
 * @author Graham Edgecombe
 *
 */
public interface CombatAction {

	/**
	 * Checks if the attacker is able to hit the victim in combat.
	 * <p>
	 * Implementations should be such that
	 * <code>canHit(attacker, victim) = canHit(victim, attacker)</code>.
	 * @param attacker The attacker.
	 * @param victim The victim.
	 * @param messages The flag that determines whether messages are sent.
	 * @return <code>true</code> if an attack can proceed, <code>false</code>
	 * if not.
	 */
	public boolean canHit(Mob attacker, Mob victim, boolean messages, boolean cannon);
	
	/**
	 * Makes the attacker hit the victim.
	 * @param attacker The attacker.
	 * @param victim The victim.
	 */
	public void hit(Mob attacker, Mob victim);
	
	/**
	 * Shows defence emotes for the victim (taking defence into account for
	 * hits should be done in the hit method).
	 * @param attacker The attacker.
	 * @param victim The victim.
	 */
	public void defend(Mob attacker, Mob victim, boolean blockAnimation);
	
	/**
	 * Gets the hit after defence calculations.
	 * @param maxHit The max hit.
	 * @param attacker The attacker.
	 * @param victim The victim.
	 * @param attackType The AttackType
	 * @param skill The skill.
	 * @param prayer The protection prayer.
	 * @return
	 */
	public int damage(int maxHit, Mob attacker, Mob victim, AttackType attackType, int skill, int prayer, boolean special, boolean ignorePrayers);
	
	/**
	 * Checks if the attacker can perform a special attack.
	 * @param attacker The attacker.
	 * @param victim The victim.
	 */
	public boolean canSpecial(Mob attacker, Mob victim);
	
	/**
	 * Performs a special attack on the victim.
	 * @param attacker The attacker.
	 * @param victim The victim.
	 */
	public void special(Mob attacker, Mob victim, int damage);
	
	/**
	 * Performs a special attack using a specific item (Dragon battleaxe, God books etc).
	 * @param attacker The attacker.
	 * @param item The item.
	 */
	public void special(Mob attacker, Item item);
	
	/**
	 * Gets the distance required for this attack to continue.
	 * @param attacker The attacker.
	 * @return The distance requierd for this attack to continue.
	 */
	public int distance(Mob attacker);

	/**
	 * Adds experience.
	 * @param attacker The attacker.
	 * @param damage The damage dealt.
	 */
	public void addExperience(Mob attacker, int damage);
	
	/**
	 * Takes care of the ring of recoil effect.
	 * @param attacker The attacker.
	 * @param victim The victim.
	 * @param damage The damage dealt.
	 */
	public void recoil(Mob attacker, Mob victim, int damage);
	
	/**
	 * Performs the Smite prayer effect.
	 * @param attacker The attacker.
	 * @param victim The victim.
	 * @param damage The damage dealt.
	 */
	public void smite(Mob attacker, Mob victim, int damage);
	
	/**
	 * Performs the vengeance spell effect.
	 * @param attacker The attacker.
	 * @param victim The victim.
	 * @param damage The damage dealt.
	 * @param delay The delay before showing the hit.
	 */
	public void vengeance(Mob attacker, Mob victim, int damage, int delay);
	
}
