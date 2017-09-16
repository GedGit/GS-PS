package org.rs2server.rs2.model.combat.npcs;

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
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.Random;


/**
 * King black dragon
 * @author Canownueasy
 * @author 'Mystic Flow
 */
public class KingBlackDragon extends AbstractCombatAction {

	//Player player;
	
	/**
	 * The singleton instance.
	 */
	private static final KingBlackDragon INSTANCE = new KingBlackDragon();

	/**
	 * Gets the singleton instance.
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}

		NPC npc = (NPC) attacker;
		int maxHit;
		int damage;
		int randomHit;
		final int hitDelay;
		final int hit;
		int fireProjectileId = -1;

		int attackType = 1;

		if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			if (attacker.getRandom().nextInt(10) < 7) {
				attackType = 0;
			}
		}
		switch(attackType) {
		default:
		case 0:
			Animation anim = attacker.getAttackAnimation();
			if(random.nextInt(2) == 1) {
				anim = Animation.create(91);
			}
			attacker.playAnimation(anim);

			hitDelay = 1;
			maxHit = npc.getCombatDefinition().getMaxHit();
			damage = damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(), Skills.ATTACK , Prayers.PROTECT_FROM_MELEE, false, false);
			randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		case 1:
			int clientSpeed;
			int gfxDelay;
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
			hitDelay = (gfxDelay / 25) - 1;
			fireProjectileId = Misc.random(393, 396);
            maxHit = 65;
            switch (fireProjectileId) {
                case 394:
                case 395:
                case 396:
                    boolean dragonfireShield = victim.getEquipment() != null && (victim.getEquipment().contains(1540) || victim.getEquipment().contains(11283) || victim.getEquipment().contains(20714)|| victim.getEquipment().contains(11284) || victim.getEquipment().contains(11285));
                    boolean dragonfirePotion = false;
                    if (victim.hasAttribute("antiFire")) {
                        dragonfirePotion = System.currentTimeMillis() - (long)victim.getAttribute("antiFire", 0L) < 360000;
                    }
                    if (dragonfireShield && !dragonfirePotion || (!dragonfireShield && dragonfirePotion)) {
                        maxHit = 10;
                    } else if (dragonfireShield && dragonfirePotion) {
                        maxHit = 0;
                    }
                    break;
            }
			attacker.playAnimation(Animation.create(81));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), fireProjectileId, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			randomHit = damage(maxHit, attacker, victim, AttackType.MAGIC, Skills.MAGIC , Prayers.PROTECT_FROM_MAGIC, false, true);

			randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			if (fireProjectileId == 393) {
				double dragonfireReduction = CombatFormula.dragonfireReduction(victim);
				if(dragonfireReduction > 0) {
					randomHit -= (randomHit * dragonfireReduction);
					if (randomHit < 0) {
						randomHit = 0;
					}
				}
			}
			hit = randomHit;
			break;
		}		

		attacker.getCombatState().setAttackDelay(4);
		attacker.getCombatState().setSpellDelay(4);

		final int fireProjectileId_ = fireProjectileId;
		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				victim.inflictDamage(new Hit(hit), attacker);
				smite(attacker, victim, hit);
				recoil(attacker, victim, hit);
				if (fireProjectileId_ != -1) {
					switch (fireProjectileId_) {
					case 393:
						if(victim instanceof Player) {
							if(((Player) victim).dfsCharges < 50) {
								DragonfireShield.charge((Player) victim);
							}
						}
						break;
					case 394:
						if(victim.getCombatState().getPoisonDamage() < 1 && random.nextInt(10) < 7 && victim.getCombatState().canBePoisoned()) {
							victim.getCombatState().setPoisonDamage(8, attacker);
							if (victim.getActionSender() != null) {
								victim.getActionSender().sendMessage("You have been poisoned!");
								//player.getActionSender().sendConfig(102, 1);
							}
						}
						break;
					case 395:
						if (victim.getRandom().nextInt(10) < 7) {
							if(victim.getCombatState().canMove() && victim.getCombatState().canBeFrozen()) {
								int freezeTimer = 25;
								int finalTimer = freezeTimer;
								if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
									finalTimer = freezeTimer / 2;
								}
								victim.getCombatState().setCanMove(false);
								victim.getCombatState().setCanBeFrozen(false);
								victim.getWalkingQueue().reset();
								if(victim.getActionSender() != null) {
									victim.getActionSender().sendMessage("You have been frozen!");
								}
								World.getWorld().submit(new Tickable(finalTimer + hitDelay) {
									@Override
									public void execute() {
										victim.getCombatState().setCanMove(true);
										this.stop();
									}
								});
								World.getWorld().submit(new Tickable(finalTimer + hitDelay + 5) {
									@Override
									public void execute() {
										victim.getCombatState().setCanBeFrozen(true);
										this.stop();
									}
								});
							}
						}
						break;
					case 396:
						if (victim.getRandom().nextInt(10) < 3) {
							Player player = (Player) victim;
							player.getSkills().decreaseLevelToZero(player.getRandom().nextInt(3), 5);
							player.getActionSender().sendMessage("You have been shocked.");
						}
						break;
					}
				}
				this.stop();
			}			
		});
		vengeance(attacker, victim, hit, 1);

		victim.getActiveCombatAction().defend(attacker, victim, true);
	}

	@Override
	public int distance(Mob attacker) {
		return 8;
	}
}