package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.content.Following;
import org.rs2server.rs2.content.misc.DragonfireShield;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Projectile;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatFormula;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.map.path.ProjectilePathFinder;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

import java.util.Random;

public class SteelDragon extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final SteelDragon INSTANCE = new SteelDragon();

	/**
	 * Gets the singleton instance.
	 * 
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	/**
	 * Default private constructor.
	 */
	public SteelDragon() {

	}

	private enum CombatStyle {
		MELEE,

		MAGIC
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if (!attacker.isNPC()) {
			return; // this should be an NPC!
		}

		NPC npc = (NPC) attacker;

		CombatStyle style = CombatStyle.MAGIC;

		int maxHit;
		int damage;
		int randomHit;
		int hitDelay;
		final int hit;

		if (attacker.getLocation().isWithinDistance(attacker, victim, 2)) {
			switch (random.nextInt(3)) {
			case 0:
			case 1:
				style = CombatStyle.MELEE;
				break;
			case 2:
				style = CombatStyle.MAGIC;
				break;
			}
		}
		if (style == CombatStyle.MAGIC && !ProjectilePathFinder.clippedProjectile(attacker, victim)) {
			Following.combatFollow(attacker, victim);
			return;
		}
		switch (style) {
		case MELEE:
			Animation anim = attacker.getAttackAnimation();
			if (random.nextInt(2) == 1) {
				anim = Animation.create(91);
			}
			attacker.playAnimation(anim);

			hitDelay = 1;
			maxHit = npc.getCombatDefinition().getMaxHit();
			damage = damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(), Skills.ATTACK,
					Prayers.PROTECT_FROM_MELEE, false, false);
			randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		default:
		case MAGIC:
			attacker.playAnimation(Animation.create(81));
			int clientSpeed;
			int gfxDelay;
			if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 110;
				gfxDelay = 120;
			} else {
				clientSpeed = 130;
				gfxDelay = 140;
			}
			hitDelay = (gfxDelay / 25) - 1;
			int fireProjectileId = 54;

			if (victim instanceof Player) {
				if (((Player) victim).dfsCharges < 50)
					DragonfireShield.charge((Player) victim);
			}

			attacker.playAnimation(Animation.create(81));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(),
					fireProjectileId, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			maxHit = 65;
			randomHit = damage(maxHit, attacker, victim, AttackType.MAGIC, Skills.MAGIC, Prayers.PROTECT_FROM_MAGIC,
					false, true);
			randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			double dragonfireReduction = CombatFormula.dragonfireReduction(victim);
			if (dragonfireReduction > 0) {
				randomHit -= (randomHit * dragonfireReduction);
				if (randomHit < 0) {
					randomHit = 0;
				}
			}
			hit = randomHit;
			break;
		}
		attacker.getCombatState().setAttackDelay(5);
		attacker.getCombatState().setSpellDelay(5);

		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				victim.inflictDamage(new Hit(hit), attacker);
				smite(attacker, victim, hit);
				recoil(attacker, victim, hit);
				this.stop();
			}
		});
		vengeance(attacker, victim, hit, 1);

		victim.getActiveCombatAction().defend(attacker, victim, true);
	}

	@Override
	public int distance(Mob attacker) {
		return 5;
	}

}
