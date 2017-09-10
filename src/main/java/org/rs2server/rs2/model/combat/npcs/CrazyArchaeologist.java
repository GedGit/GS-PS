package org.rs2server.rs2.model.combat.npcs;

import java.util.ArrayList;
import java.util.List;

import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Projectile;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * @author Script <Rune-Server> CamelCrusher
 */

public class CrazyArchaeologist extends AbstractCombatAction {

	private static final CrazyArchaeologist INSTANCE = new CrazyArchaeologist();

	private static final int MAGIC_GFX = 1259;

	private static final String[] MESSAGES = { "Get off my site!", "I'm Bellock - respect me!",
			"No-one messes with Bellock's dig!", "These ruins are mine!", "Taste my knowledge!",
			"You belong in a museum!" };

	public static CombatAction getAction() {
		return INSTANCE;
	}

	public CrazyArchaeologist() {

	}

	private enum CombatStyle {
		RANGE,

		MAGIC
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if (!attacker.isNPC()) {
			return; // this should be an NPC!
		}

		int randomHit, clientSpeed, gfxDelay, preHit = 0;
		final int hit;

		if (Misc.random(5) == 0)
			((NPC) attacker).forceChat(MESSAGES[random.nextInt(MESSAGES.length)]);

		CombatStyle style = CombatStyle.RANGE;

		switch (Misc.random(3)) {
		case 3:
			style = CombatStyle.MAGIC;
			break;
		default:
			style = CombatStyle.RANGE;
			break;
		}
		
		Location firstLocation = victim.getLocation();
		Location secondLocation = victim.getLocation().transform(1, 1, 0);
		Location thirdLocation = victim.getLocation().transform(-1, -1, 0);
		switch (style) {
		case RANGE:
			// victim.playGraphics(Graphic.create(550));;
			if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 50;
				gfxDelay = 60;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else {
				clientSpeed = 110;
				gfxDelay = 120;
			}
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(),
					MAGIC_GFX, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			randomHit = damage(13, attacker, victim, CombatState.AttackType.RANGE, Skills.RANGE,
					Prayers.PROTECT_FROM_MISSILES, false, false);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			preHit = randomHit;
			break;
		case MAGIC:
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
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), firstLocation, 551, 45, 50,
					clientSpeed, 43, 35, 0, 10, 48));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), secondLocation, 551, 45, 50,
					clientSpeed, 43, 35, 0, 10, 48));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), thirdLocation, 551, 45, 50,
					clientSpeed, 43, 35, 0, 10, 48));
			break;
		default:
			gfxDelay = 0;
			break;
		}
		attacker.getCombatState().setAttackDelay(5);
		attacker.getCombatState().setSpellDelay(5);

		final CombatStyle preStyle = style;
		hit = preHit;
		World.getWorld().submit(new Tickable((gfxDelay / 20) - 1) {

			@SuppressWarnings("incomplete-switch")
			@Override
			public void execute() {
				this.stop();
				List<Player> enemies = new ArrayList<>();
				switch (preStyle) {
				case RANGE:
					for (Player p : victim.getLocalPlayers()) {
						if (p.getLocation().equals(firstLocation) || p.getLocation().equals(secondLocation)
								|| p.getLocation().equals(thirdLocation)) {
							if (p == victim) {
								continue;
							}
							enemies.add(p);
						}
					}
					victim.getActionSender().sendStillGFX(157, 100, firstLocation);
					victim.getActionSender().sendStillGFX(157, 100, secondLocation);
					victim.getActionSender().sendStillGFX(157, 100, thirdLocation);
					break;
				}
				int finalHit = preStyle == CombatStyle.RANGE ? Misc.random(15) : hit;
				boolean doDamage = true;
				switch (preStyle) {
				case RANGE:
					if (!victim.getLocation().equals(firstLocation) && !victim.getLocation().equals(secondLocation)
							&& !victim.getLocation().equals(thirdLocation)) {
						doDamage = false;
					}
					break;
				}
				if (doDamage) {
					victim.inflictDamage(new Hit(finalHit), attacker);
					smite(attacker, victim, finalHit);
					recoil(attacker, victim, finalHit);
					vengeance(attacker, victim, finalHit, 1);
				}
				for (Player p : enemies) {
					p.inflictDamage(new Hit(finalHit), attacker);
					smite(attacker, p, finalHit);
					recoil(attacker, p, finalHit);
					vengeance(attacker, p, finalHit, 1);
				}
				enemies.clear();
			}
		});
	}

	@Override
	public int distance(Mob attacker) {
		return 6;
	}
}
