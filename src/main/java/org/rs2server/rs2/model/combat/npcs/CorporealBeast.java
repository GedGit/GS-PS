package org.rs2server.rs2.model.combat.npcs;

import java.util.Collection;
import java.util.Random;

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
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.map.path.ProjectilePathFinder;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

/**
 * @author Script <Rune-Server> CamelCrusher
 */

public class CorporealBeast extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final CorporealBeast INSTANCE = new CorporealBeast();

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
	private CombatStyle style;

	private enum CombatStyle {
		MELEE, MAGIC, RANGE
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if (!attacker.isNPC())
			return; // this should be an NPC!

		final int maxHit;
		int randomHit;

		if (random.nextInt(6) < 2)
			style = CombatStyle.RANGE;
		else if (random.nextInt(6) > 4)
			style = CombatStyle.MAGIC;
		else
			style = CombatStyle.MELEE;

		switch (style) {
		case MELEE:
			if (attacker.getLocation().getDistance(victim.getLocation()) > 3) {
				if (random.nextInt(6) < 3)
					handleMagicAttack(attacker, victim);
				else
					handleRangedAttack(attacker, victim);
				break;
			}
			attacker.playAnimation(Animation.create(1683));
			maxHit = 20;

			randomHit = super.damage(maxHit, attacker, victim, AttackType.CRUSH, Skills.ATTACK,
					Prayers.PROTECT_FROM_MELEE, false, false);
			randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS))
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);

			int hit = randomHit;

			attacker.getCombatState().setAttackDelay(5);
			attacker.getCombatState().setSpellDelay(5);

			final int fHit = hit;
			World.getWorld().submit(new Tickable(1) {

				@Override
				public void execute() {
					victim.inflictDamage(new Hit(fHit), attacker);
					smite(attacker, victim, fHit);
					recoil(attacker, victim, fHit);
					this.stop();
				}
			});

			vengeance(attacker, victim, hit, 1);

			victim.getActiveCombatAction().defend(attacker, victim, true);
			style = null;
			break;
		case RANGE:
			handleRangedAttack(attacker, victim);
			break;
		case MAGIC:
			handleMagicAttack(attacker, victim);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean canHit(Mob attacker, Mob victim, boolean b, boolean b1) {

		int extraDistance = style == CombatStyle.MAGIC ? 5 : 0;
		if (style == CombatStyle.MAGIC) {
			if (!ProjectilePathFinder.clearPath(attacker.getLocation(), victim.getLocation())) {
				System.out.println("Error with projectile pathfinding.");
				Following.combatFollow(attacker, victim);
				style = CombatStyle.MELEE;
				return false;
			}
		}
		boolean canAttack = attacker.getLocation().isWithinDistance(attacker, victim,
				distance(attacker) + extraDistance);
		if (!canAttack)
			canAttack = true;
		return canAttack;
	}

	@Override
	public int distance(Mob attacker) {
		return 64;
	}

	/**
	 * Handles corp's magic attack.
	 * 
	 * @param attacker
	 * @param victim
	 */
	private void handleMagicAttack(Mob attacker, Mob victim) {
		style = CombatStyle.MAGIC;
		int randomHit;
		int hit = 0;
		int hitDelay = 2;
		boolean blockAnimation = false;
		attacker.playAnimation(Animation.create(1679));
		attacker.playGraphics(Graphic.create(319, 0, 0));
		int maxHit = 35;
		final Collection<Player> localPlayers = World.getWorld().getRegionManager().getLocalPlayers(attacker);
		for (final Player near : localPlayers) {
			if (near != null && near != attacker && near.getSkills().getLevel(Skills.HITPOINTS) > 0) {
				if (attacker.getCentreLocation().isWithinDistance(attacker, near, 10)) {
					int rClientSpeed;
					if (attacker.getLocation().isWithinDistance(attacker, near, 1)) {
						rClientSpeed = 70;
					} else if (attacker.getLocation().isWithinDistance(attacker, near, 5)) {
						rClientSpeed = 90;
					} else if (attacker.getLocation().isWithinDistance(attacker, near, 8)) {
						rClientSpeed = 110;
					} else {
						rClientSpeed = 130;
					}
					attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), near.getCentreLocation(),
							316, 45, 50, rClientSpeed, 43, 35, near.getProjectileLockonIndex(), 10, 48));
				}
				super.hit(attacker, near);
			}
		}
		World.getWorld().submit(new Tickable(2) {
			@Override
			public void execute() {
				stop();
				for (final Player near : localPlayers) {
					if (near != null && near != attacker && near != victim
							&& near.getSkills().getLevel(Skills.HITPOINTS) > 0) {
						if (attacker.getCentreLocation().isWithinDistance(attacker, near, 10)) {
							int hitz = MagicCombatAction.getAction().damage(maxHit, attacker, near, AttackType.MAGIC,
									Skills.MAGIC, Prayers.PROTECT_FROM_MAGIC, false, true);
							hitz = random.nextInt(hitz < 1 ? 1 : hitz + 1);
							if (hitz > near.getSkills().getLevel(Skills.HITPOINTS))
								hitz = near.getSkills().getLevel(Skills.HITPOINTS);
							near.inflictDamage(new Hit(hitz), near);
						}
					}
				}
			}
		});
		randomHit = MagicCombatAction.getAction().damage(maxHit, attacker, victim, AttackType.MAGIC, Skills.MAGIC,
				Prayers.PROTECT_FROM_MAGIC, false, false);
		randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
		if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS))
			randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
		hit = randomHit;

		attacker.getCombatState().setAttackDelay(5);
		attacker.getCombatState().setSpellDelay(5);

		final int fHit = hit;
		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				victim.inflictDamage(new Hit(fHit), attacker);
				smite(attacker, victim, fHit);
				recoil(attacker, victim, fHit);
				this.stop();
			}
		});

		vengeance(attacker, victim, hit, 1);

		victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
		style = null;
	}

	/**
	 * Handles corp's ranged attack.
	 * 
	 * @param attacker
	 * @param victim
	 */
	private void handleRangedAttack(Mob attacker, Mob victim) {
		style = CombatStyle.RANGE;
		int randomHit;
		int hit = 0;
		int hitDelay = 2;
		boolean blockAnimation = false;
		attacker.playAnimation(Animation.create(1679));
		attacker.playGraphics(Graphic.create(319, 0, 0));
		int maxHit = 30;
		final Collection<Player> localPlayers = World.getWorld().getRegionManager().getLocalPlayers(attacker);
		for (final Player near : localPlayers) {
			if (near != null && near != attacker && near.getSkills().getLevel(Skills.HITPOINTS) > 0) {
				if (attacker.getCentreLocation().isWithinDistance(attacker, near, 10)) {
					int rClientSpeed;
					if (attacker.getLocation().isWithinDistance(attacker, near, 1)) {
						rClientSpeed = 70;
					} else if (attacker.getLocation().isWithinDistance(attacker, near, 5)) {
						rClientSpeed = 90;
					} else if (attacker.getLocation().isWithinDistance(attacker, near, 8)) {
						rClientSpeed = 110;
					} else {
						rClientSpeed = 130;
					}
					attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), near.getCentreLocation(),
							315, 45, 50, rClientSpeed, 43, 35, near.getProjectileLockonIndex(), 10, 48));
				}
				super.hit(attacker, near);
			}
		}
		World.getWorld().submit(new Tickable(2) {
			@Override
			public void execute() {
				stop();
				for (final Player near : localPlayers) {
					if (near != null && near != attacker && near != victim
							&& near.getSkills().getLevel(Skills.HITPOINTS) > 0) {
						if (attacker.getCentreLocation().isWithinDistance(attacker, near, 10)) {
							int hitz = MagicCombatAction.getAction().damage(maxHit, attacker, near, AttackType.RANGE,
									Skills.RANGE, Prayers.PROTECT_FROM_MISSILES, false, true);
							hitz = random.nextInt(hitz < 1 ? 1 : hitz + 1);
							if (hitz > near.getSkills().getLevel(Skills.HITPOINTS))
								hitz = near.getSkills().getLevel(Skills.HITPOINTS);
							near.inflictDamage(new Hit(hitz), near);
						}
					}
				}
			}
		});
		randomHit = MagicCombatAction.getAction().damage(maxHit, attacker, victim, AttackType.RANGE, Skills.RANGE,
				Prayers.PROTECT_FROM_MISSILES, false, false);
		randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
		if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS))
			randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
		hit = randomHit;

		attacker.getCombatState().setAttackDelay(5);
		attacker.getCombatState().setSpellDelay(5);

		final int fHit = hit;
		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				victim.inflictDamage(new Hit(fHit), attacker);
				smite(attacker, victim, fHit);
				recoil(attacker, victim, fHit);
				this.stop();
			}
		});

		vengeance(attacker, victim, hit, 1);

		victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
		style = null;
	}
}