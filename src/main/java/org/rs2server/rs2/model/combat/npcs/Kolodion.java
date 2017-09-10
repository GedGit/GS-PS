package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.Spell;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

public class Kolodion extends AbstractCombatAction{


	/**
	 * The singleton instance.
	 */
	private static final Kolodion INSTANCE = new Kolodion();

	/**
	 * Gets the singleton instance.
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		int damage;
		int maxHit = 20;
		NPC npc = (NPC) attacker;
		attacker.playAnimation(getAttackAnim(npc));
		Spell[] spell = {Spell.CLAWS_OF_GUTHIX, Spell.FLAMES_OF_ZAMORAK, Spell.SARADOMIN_STRIKE};
		int random = Misc.random(spell.length - 1);

		int gfxDelay;
		if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			gfxDelay = 80;
		} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
			gfxDelay = 100;
		} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
			gfxDelay = 120;
		} else {
			gfxDelay = 140;
		}
		Graphic[] gfx = {Graphic.create(77, gfxDelay, 100), Graphic.create(78, gfxDelay, 0), Graphic.create(76, gfxDelay, 100)};
		npc.setAutocastSpell(spell[random]);
		damage = Misc.random(damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(), Skills.MAGIC , Prayers.PROTECT_FROM_MAGIC, false, false));
		victim.playGraphics(damage <= 0 ? Graphic.create(85, gfxDelay, 100) : gfx[random]);
		int delay = (gfxDelay / 20) - 1;
		if (damage > 0) {
			World.getWorld().submit(new Tickable(delay) {

				@Override
				public void execute() {
					this.stop();
					victim.inflictDamage(new Hit(damage), attacker);
					smite(attacker, victim, damage);
					recoil(attacker, victim, damage);
				}

			});
			vengeance(attacker, victim, damage, 1);
		}

		victim.getActiveCombatAction().defend(attacker, victim, false);
		
		attacker.getCombatState().setSpellDelay(5);
		attacker.getCombatState().setAttackDelay(4);
	}
	
	private Animation getAttackAnim(NPC n) {
		switch (n.getId()) {
		case 1606:
			return Animation.create(132);
		case 1607:
			return Animation.create(5319);
		case 1608:
			return Animation.create(729);
		case 1609:
			return Animation.create(69);
		}
		return Animation.create(811);
	}

	@Override
	public int distance(Mob attacker) {
		return 5;
	}

}
