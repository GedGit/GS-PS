package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.content.Following;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Projectile;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.map.path.ProjectilePathFinder;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.Random;

/**
 * @author Script <Rune-Server> CamelCrusher
 */
public class KrilTsutsaroth extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final KrilTsutsaroth INSTANCE = new KrilTsutsaroth();

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
	public KrilTsutsaroth() {

	}

	private enum CombatStyle {
		MELEE,

		MAGIC
	}

	@SuppressWarnings("unused")
	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if (!attacker.isNPC()) {
			return; // this should be an NPC!
		}

		NPC npc = (NPC) attacker;

		CombatStyle style = CombatStyle.MAGIC;

		int maxHit = 0;
		int damage;
		int randomHit;
		int hitDelay;
		boolean blockAnimation;
		final int hit;

		if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			if (random.nextInt(6) == 4)
				style = CombatStyle.MELEE;
		}

		if (style == CombatStyle.MAGIC && !ProjectilePathFinder.clippedProjectile(attacker, victim)) {
			Following.combatFollow(attacker, victim);
			return;
		}
		switch (style) {
		case MELEE:
			attacker.playAnimation(attacker.getAttackAnimation());

			hitDelay = 2;
			blockAnimation = true;
			boolean ignore = false;
			if (victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MELEE)) {
				if (Misc.random(8) == 7) {
					maxHit = 49;
					ignore = true;
					victim.getActionSender().sendMessage(
							"K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.");
					victim.getSkills().decreaseLevel(Skills.PRAYER, victim.getSkills().getLevel(Skills.PRAYER) / 2);
				}
			} else {
				maxHit = npc.getCombatDefinition().getMaxHit();
			}
			randomHit = damage(maxHit, attacker, victim, AttackType.CRUSH, Skills.STRENGTH, Prayers.PROTECT_FROM_MELEE,
					false, ignore);
			randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		default:
		case MAGIC:
			maxHit = 30;
			int rClientSpeed;
			int rGfxDelay;
			if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				rClientSpeed = 70;
				rGfxDelay = 80;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				rClientSpeed = 90;
				rGfxDelay = 100;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				rClientSpeed = 110;
				rGfxDelay = 120;
			} else {
				rClientSpeed = 130;
				rGfxDelay = 140;
			}
			hitDelay = (rGfxDelay / 20) - 1;

			attacker.playAnimation(Animation.create(6950));
			attacker.playGraphics(Graphic.create(1224, 0, 100));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1225,
					45, 50, rClientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			// victim.playGraphics(Graphic.create(346, rGfxDelay, 100));

			blockAnimation = false;
			randomHit = damage(maxHit, attacker, victim, AttackType.MAGIC, Skills.MAGIC, Prayers.PROTECT_FROM_MAGIC,
					false, false);
			randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		}

		attacker.getCombatState().setAttackDelay(6);
		attacker.getCombatState().setSpellDelay(6);

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

		victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
	}

	@Override
	public int distance(Mob attacker) {
		return 1;
	}
}