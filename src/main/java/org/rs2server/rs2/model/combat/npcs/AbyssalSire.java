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

public class AbyssalSire extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final AbyssalSire INSTANCE = new AbyssalSire();

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
		MELEE,

		MAGIC
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if (!attacker.isNPC())
			return; // this should be an NPC!

		final int maxHit;
		int randomHit;
		int hitDelay = 0;
		boolean blockAnimation = false;
		int hit = 0;

		switch (style) {
		case MELEE:
			Animation anim = attacker.getAttackAnimation();
			attacker.playAnimation(anim);

			hitDelay = 1;
			blockAnimation = true;
			maxHit = 30;

			randomHit = super.damage(maxHit, attacker, victim, AttackType.CRUSH, Skills.ATTACK,
					Prayers.PROTECT_FROM_MELEE, false, true);
			randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS))
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			hit = randomHit;
			break;
		case MAGIC:
			attacker.playGraphics(Graphic.create(1275, 0, 0));
			hitDelay = 2;
			blockAnimation = false;
			maxHit = 30;
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
						attacker.playProjectile(
								Projectile.create(attacker.getCentreLocation(), near.getCentreLocation(), 1275, 45, 50,
										rClientSpeed, 43, 35, near.getProjectileLockonIndex(), 10, 48));
					}
					super.hit(attacker, near);
				}
			}
			World.getWorld().submit(new Tickable(2) {
				public void execute() {
					stop();
					for (final Player near : localPlayers) {
						if (near != null && near != attacker && near != victim
								&& near.getSkills().getLevel(Skills.HITPOINTS) > 0) {
							if (attacker.getCentreLocation().isWithinDistance(attacker, near, 12)) {
								int hitz = MagicCombatAction.getAction().damage(maxHit, attacker, near,
										AttackType.MAGIC, Skills.MAGIC, Prayers.PROTECT_FROM_MISSILES, false, false);
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
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		}

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

	@Override
	public boolean canHit(Mob attacker, Mob victim, boolean b, boolean b1) {

		if (random.nextInt(6) > 3)
			style = CombatStyle.MAGIC;
		else
			style = CombatStyle.MELEE;
		int extraDistance = style == CombatStyle.MAGIC ? 5 : 0;
		if (style == CombatStyle.MAGIC) {
			if (!ProjectilePathFinder.clearPath(attacker.getLocation(), victim.getLocation())) {
				System.out.println("Error with projectile pathfinding.");
				Following.combatFollow(attacker, victim);
				//return false;
			}
		}
		return (attacker.getCentreLocation().isWithinDistance(attacker, victim, distance(attacker) + extraDistance));

	}

	@Override
	public int distance(Mob attacker) {
		return 12;
	}
}