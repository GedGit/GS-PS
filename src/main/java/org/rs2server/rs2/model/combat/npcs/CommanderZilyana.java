package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.content.Following;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Hit.HitType;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.combat.impl.MeleeCombatAction;
import org.rs2server.rs2.model.map.path.ProjectilePathFinder;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.Random;

/**
 * Commander Zilyana
 * 
 * @author Canownueasy
 *
 */
public class CommanderZilyana extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final CommanderZilyana INSTANCE = new CommanderZilyana();

	private static final String[] MESSAGES = { "Death to the enemies of the light!", "Slay the evil ones!",
			"Saradomin lend me strength!", "By the power of Saradomin!", "May Saradomin be my sword.",
			"Good will always triumph!", "Forward! Our allies are with us!", "Saradomin is with us!",
			"In the name of Saradomin!", "Attack! Find the Godsword!", "All praise Saradomin!" };

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
	private long lastMessage;

	/**
	 * Default private constructor.
	 */
	public CommanderZilyana() {

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
		if (random.nextInt(3) == 2 && System.currentTimeMillis() - lastMessage > 3000) {
			((NPC) attacker).forceChat(MESSAGES[random.nextInt(MESSAGES.length)]);
			lastMessage = System.currentTimeMillis();
		}
		NPC npc = (NPC) attacker;
		// 6969 = block, 6970 = mage
		CombatStyle style = CombatStyle.MELEE;
		int maxHit;
		int damage;
		int randomHit;
		int hitDelay;
		boolean blockAnimation;
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
			attacker.playAnimation(anim);

			hitDelay = 1;
			blockAnimation = true;
			maxHit = npc.getCombatDefinition().getMaxHit();
			damage = MeleeCombatAction.getAction().damage(maxHit, attacker, victim,
					attacker.getCombatState().getAttackType(), Skills.ATTACK, Prayers.PROTECT_FROM_MELEE, false, false);
			damage = random.nextInt(damage < 1 ? 1 : damage + 1);
			if (damage > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				damage = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = damage;
			break;
		default:
		case MAGIC:
			attacker.playAnimation(Animation.create(6970));
			victim.playGraphics(Graphic.create(1221, 60));

			maxHit = 32;
			final int maxx = maxHit;
			World.getWorld().submit(new Tickable(2) {
				public void execute() {
					for (final Player near : attacker.getLocalPlayers()) {
						if (near != null && near != attacker && near != victim
								&& near.getSkills().getLevel(Skills.HITPOINTS) > 0) {
							if (Misc.getDistance(near.getLocation(), attacker.getLocation()) <= 10) {
								near.playGraphics(Graphic.create(1207));
								World.getWorld().submit(new Tickable(1) {
									public void execute() {
										int maxHit = maxx;
										int randomHit = MagicCombatAction.getAction().damage(maxHit, attacker, near,
												AttackType.MAGIC, Skills.MAGIC, Prayers.PROTECT_FROM_MAGIC, false,
												false);
										randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
										if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
											randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
										}
										near.inflictDamage(
												new Hit(randomHit > 0 ? HitType.NORMAL_HIT : HitType.ZERO_DAMAGE_HIT,
														randomHit),
												near);
										int preDouble = (int) (randomHit / 2);
										int secondHit = Misc.random(preDouble);
										if (secondHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
											secondHit = victim.getSkills().getLevel(Skills.HITPOINTS);
										}
										near.inflictDamage(
												new Hit(secondHit > 0 ? HitType.NORMAL_HIT : HitType.ZERO_DAMAGE_HIT,
														secondHit),
												near);
										attacker.getSkills().increaseLevel(Skills.HITPOINTS, Misc.random(3));
										stop();
									}
								});
							}
						}
					}
					stop();
				}
			});

			hitDelay = 3;
			blockAnimation = false;
			maxHit = 32;

			randomHit = MagicCombatAction.getAction().damage(maxHit, attacker, victim, AttackType.MAGIC, Skills.MAGIC,
					Prayers.PROTECT_FROM_MAGIC, false, false);
			randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		}

		attacker.getCombatState().setAttackDelay(npc.getCombatDefinition().getCombatCooldownDelay());
		attacker.getCombatState().setSpellDelay(npc.getCombatDefinition().getCombatCooldownDelay());

		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				victim.inflictDamage(new Hit(hit > 0 ? HitType.NORMAL_HIT : HitType.ZERO_DAMAGE_HIT, hit), attacker);
				int preDouble = (int) (hit / 2);
				int secondHit = Misc.random(preDouble);
				victim.inflictDamage(new Hit(secondHit > 0 ? HitType.NORMAL_HIT : HitType.ZERO_DAMAGE_HIT, secondHit),
						victim);
				attacker.getSkills().increaseLevel(Skills.HITPOINTS, Misc.random(3));
				smite(attacker, victim, hit);
				recoil(attacker, victim, hit);
				this.stop();
			}
		});
		vengeance(attacker, victim, hit, 1);

		victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
	}

	@Override
	public boolean canHit(Mob attacker, Mob victim, boolean b, boolean b1) {
		return (attacker.getCentreLocation().isWithinDistance(attacker, victim, distance(attacker)));

	}

	@Override
	public int distance(Mob attacker) {
		return 2;
	}
}