package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Projectile;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.ArrayList;


/**
 * Chaos Elemental
 * @author Canownueasy
 *
 */
public class ChaosElemental extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final ChaosElemental INSTANCE = new ChaosElemental();

	/**
	 * Gets the singleton instance.
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	/**
	 * Default private constructor.
	 */
	public ChaosElemental() {

	}

	private enum CombatStyle {
		MAGIC,
		RANGE,
		TELEOTHER,
		DISARM
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}

		CombatStyle style = CombatStyle.MAGIC;

		int maxHit;
		int randomHit;
		int hitDelay;
		boolean blockAnimation = false;
		final int hit;
		int clientSpeed;
		int gfxDelay;
		int preHit = 0;

		if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			switch(Misc.random(4)) {
			default:
				style = CombatStyle.MAGIC;	
				break;
			case 1:
				style = CombatStyle.RANGE;
				break;
			case 2:
				style = CombatStyle.TELEOTHER;
				break;
			case 3:
				style = CombatStyle.DISARM;
				break;
			}
		}

		switch(style) {
		case DISARM:
			attacker.playAnimation(Animation.create(3146));
			//victim.playGraphics(Graphic.create(550));
			if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 110;
				gfxDelay = 120;
			} else {
				clientSpeed = 130;
				gfxDelay = 140;
			}
			hitDelay = (gfxDelay / 20) - 1;
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 551, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			break;
		case TELEOTHER:
			attacker.playAnimation(Animation.create(3146));
			//victim.playGraphics(Graphic.create(553));
			if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 110;
				gfxDelay = 120;
			} else {
				clientSpeed = 130;
				gfxDelay = 140;
			}
			hitDelay = (gfxDelay / 20) - 1;
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 554, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			break;
		case RANGE:
			maxHit = 28;
			attacker.playAnimation(Animation.create(3146));
			//victim.playGraphics(Graphic.create(556));
			if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 110;
				gfxDelay = 120;
			} else {
				clientSpeed = 130;
				gfxDelay = 140;
			}
			hitDelay = (gfxDelay / 20) - 1;
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 557, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			blockAnimation = false;
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MISSILES)) {
				maxHit = 8;
			}
			randomHit = Misc.random(maxHit);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			preHit = randomHit;
			break;
		case MAGIC:
			maxHit = 28;
			attacker.playAnimation(Animation.create(3146));
			//victim.playGraphics(Graphic.create(556));

			if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 110;
				gfxDelay = 120;
			} else {
				clientSpeed = 130;
				gfxDelay = 140;
			}
			hitDelay = (gfxDelay / 20) - 1;
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 557, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			blockAnimation = false;
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
				maxHit = 7;
			}
			randomHit = Misc.random(maxHit);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			preHit = randomHit;
			break;
		default:
			preHit = 0;
			blockAnimation = true;
			hitDelay = 1;
			break;
		}
		attacker.getCombatState().setAttackDelay(5);
		attacker.getCombatState().setSpellDelay(6);
		final CombatStyle preStyle = style;
		hit = preHit;
		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				this.stop();
				victim.playGraphics(breakGraphic(preStyle));
				switch(preStyle) {
				default:
				case MAGIC:
				case RANGE:
					victim.inflictDamage(new Hit(hit), attacker);
					smite(attacker, victim, hit);
					recoil(attacker, victim, hit);
					vengeance(attacker, victim, hit, 1);
					break;
				case TELEOTHER:
				case DISARM:
					applyEffect(victim, preStyle);
					break;
				}
			}			
		});
		victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
	}

	private Graphic breakGraphic(CombatStyle style) {
		int id;
		switch(style) {
		default:
		case MAGIC:
		case RANGE:
			id = 558;
			break;
		case TELEOTHER:
			id = 555;
			break;
		case DISARM:
			id = 552;
			break;
		}
		return Graphic.create(id, 0, 100);
	}

	private void applyEffect(final Mob victim, CombatStyle style) {
		switch(style) {
		case TELEOTHER:
			if(victim.getActionSender() != null) {
				victim.getActionSender().sendMessage("The fiend teleports you away.");
			}
			World.getWorld().submit(new Tickable(1) {
				public void execute() {
					this.stop();
					victim.setTeleportTarget(generateLocation());	
				}
			});
			break;
		case DISARM:
			if(victim.getActionSender() != null) {
				victim.getActionSender().sendMessage("The fiend attempts to disarm you.");
			}
			int slots[] = { Equipment.SLOT_WEAPON, Equipment.SLOT_SHIELD, Equipment.SLOT_CHEST,
					Equipment.SLOT_BOTTOMS, Equipment.SLOT_HELM, Equipment.SLOT_BOOTS,
					Equipment.SLOT_CAPE, Equipment.SLOT_GLOVES, Equipment.SLOT_AMULET,
					Equipment.SLOT_ARROWS, Equipment.SLOT_RING};
			ArrayList<Integer> equipUsed = new ArrayList<>();
			for (int slot : slots) {
				if (victim.getEquipment().get(slot) == null) {
					continue;
				}
				equipUsed.add(slot);
			}
			int randomUsed = equipUsed.get(Misc.random(equipUsed.size() - 1));
			if (victim.getEquipment().get(randomUsed) != null && victim.getInventory().add(victim.getEquipment().get(randomUsed))) {
				victim.getEquipment().set(randomUsed, null);
				equipUsed.clear();
			}
			break;
		default:
			break;
		}
	}

	private Location generateLocation() {
		Location loc = Location.create(3230 + Misc.random(3), 3917 + Misc.random(3));
		if(Misc.random(1) > 0) {
			loc = Location.create(3275 + Misc.random(2), 3912 + Misc.random(2));
		}
		return loc;
	}

	@Override
	public int distance(Mob attacker) {
		return 7;
	}
}