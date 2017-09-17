package org.rs2server.rs2.model.combat.impl;

import org.rs2server.Server;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.domain.service.api.content.ItemService;
import org.rs2server.rs2.domain.service.api.content.trade.TradeService;
import org.rs2server.rs2.domain.service.impl.content.ItemServiceImpl;
import org.rs2server.rs2.domain.service.impl.content.KrakenServiceImpl;
import org.rs2server.rs2.domain.service.impl.content.trade.TradeServiceImpl;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.Hit.HitPriority;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatFormula;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.CombatState.CombatStyle;
import org.rs2server.rs2.model.combat.impl.RangeCombatAction.BowType;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.container.PriceChecker;
import org.rs2server.rs2.model.container.Trade;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.npc.impl.kraken.Kraken;
import org.rs2server.rs2.model.npc.impl.kraken.Whirlpool;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.tickable.impl.StoppingTick;
import org.rs2server.rs2.util.Misc;
import org.rs2server.util.NPCUtils;

import java.util.Random;

/**
 * Provides a skeletal implementation for <code>CombatAction</code>s on which
 * other code should base their code off.
 * <p>
 * This implementation contains code common to ALL implementations, e.g. the
 * 'canHit' method checks wilderness levels of the players.
 *
 * @author Graham Edgecombe
 */
public abstract class AbstractCombatAction implements CombatAction {

	Player player;

	/**
	 * The random number generator.
	 */
	protected final Random random = new Random();

	private final ItemService itemService;

	public AbstractCombatAction() {
		itemService = Server.getInjector().getInstance(ItemService.class);
	}

	@Override
	public boolean canHit(Mob attacker, Mob victim, boolean messages, boolean cannon) {
		if (attacker.getCombatState().isDead() || victim.getCombatState().isDead())
			return false;
		if (!attacker.canHit(victim, messages))
			return false;
		if (!victim.canHit(attacker, messages))
			return false;
		if (attacker.isPlayer() && victim.isPlayer()
				&& BoundaryManager.isWithinBoundaryNoZ(attacker.getLocation(), "ClanWarsFFA")
				&& BoundaryManager.isWithinBoundaryNoZ(victim.getLocation(), "ClanWarsFFA"))
			return true;
		if (attacker.isPlayer() && victim.isPlayer() && !victim.isInWilderness())
			return false;
		if (victim.isNPC() && attacker.isPlayer()) {
			NPC n = (NPC) victim;
			if (n.getCombatDefinition() != null && attacker.getSkills().getLevelForExperience(Skills.SLAYER) < n
					.getCombatDefinition().getSlayerLevelReq()) {/// my swag
																	/// code.
				attacker.getActionSender().sendMessage("You need a Slayer level of "
						+ n.getCombatDefinition().getSlayerLevelReq() + " to attack this npc.");
				return false;
			}
		}
		if (attacker.getAttribute("stunned") != null)
			return false;
		if (victim.getAttribute("stunned") != null)
			return false;
		if ((attacker.isPlayer() && !attacker.isInWilderness() && victim.isPlayer())
				|| (victim.isPlayer() && !victim.isInWilderness() && attacker.isPlayer())) {
			attacker.getActionSender().sendMessage("You or the other player are not in the Wilderness.");
			return false;
		}
		if (attacker.isNPC()) {
			NPC n = (NPC) attacker;
			if (n.getCombatDefinition() != null && n.getCombatDefinition().isFightCavesNPC())
				return true;
		}

		boolean requires = true;
		if (attacker.isPlayer() && victim.isNPC()) {
			NPC n = (NPC) victim;
			if (n.isCaveNPC())
				requires = false;
		}

		boolean bhTarget = false;
		boolean bh = false, bh2 = false;
		if (attacker.isPlayer()) {
			Player pAttacker = (Player) attacker;
			bh = pAttacker.getBounty() != null;
			if (pAttacker.getBounty() != null && pAttacker.getBountyTarget() != null
					&& pAttacker.getBountyTarget() == victim) {
				bhTarget = true;
			}
		}
		if (victim.isPlayer()) {
			Player pVictim = (Player) victim;
			bh2 = pVictim.getBounty() != null;
			if (pVictim.getBounty() != null && pVictim.getBountyTarget() != null
					&& pVictim.getBountyTarget() == attacker) {
				bhTarget = true;
			}
		}
		if (victim.inMulti() || BoundaryManager.isWithinBoundaryNoZ(victim.getLocation(), "MultiCombat"))
			return true;
		if (bh && bh2) {
			if (!bhTarget) {
				if (attacker.getCombatState().getLastHitTimer() > (System.currentTimeMillis() + 4000)) { // 10
																											// cycles
																											// for
																											// tagging
																											// timer
					if (attacker.getCombatState().getLastHitBy() != null
							&& victim != attacker.getCombatState().getLastHitBy()) {
						if (attacker.getCombatState().getLastAttackerWith() == victim
								&& System.currentTimeMillis() - attacker.getCombatState().getLastCombatWith() < 6000) {
							return true;
						}
						if (messages && attacker.getActionSender() != null) {
							attacker.getActionSender().sendMessage("I'm already under attack.");
						}
						return false;
					}
				}
				if (victim.getCombatState().getLastHitTimer() > (System.currentTimeMillis() + 4000)) { // 10
																										// cycles
																										// for
																										// tagging
																										// timer
					if (victim.getCombatState().getLastHitBy() != null
							&& attacker != victim.getCombatState().getLastHitBy()) {
						if (victim.getCombatState().getLastAttackerWith() == attacker
								&& System.currentTimeMillis() - victim.getCombatState().getLastCombatWith() < 6000) {
							if (victim.getCombatState().getLastAttackerWith() != ((Player) victim).getBountyTarget()) {
								attacker.resetInteractingEntity();
								victim.resetInteractingEntity();
								return false;
							}
							return true;
						}
						if (messages && attacker.getActionSender() != null) {
							attacker.getActionSender().sendMessage("Someone else is already fighting your opponent.");
						}
						return false;
					}
				}
			}
		} else {
			if (requires
					&& (!BoundaryManager.isWithinBoundaryNoZ(attacker.getLocation(), "MultiCombat")
							|| !BoundaryManager.isWithinBoundaryNoZ(victim.getLocation(), "MultiCombat"))
					&& (!attacker.inMulti()) || !victim.inMulti()) {
				// System.out.println("Attacker cant hit");
				if (attacker.getCombatState().getLastHitTimer() > (System.currentTimeMillis() + 4000)) {
					if (attacker.getCombatState().getLastHitBy() != null
							&& victim != attacker.getCombatState().getLastHitBy()) {
						if (bhTarget) {
							return true;
						}
						if (attacker.getCombatState().getLastAttackerWith() == victim
								&& System.currentTimeMillis() - attacker.getCombatState().getLastCombatWith() < 6000) {
							return true;
						}
						if (messages && attacker.getActionSender() != null) {
							attacker.getActionSender().sendMessage("I'm already under attack.");
						}
						return false;
					}
				}
				if (victim.getCombatState().getLastHitTimer() > (System.currentTimeMillis() + 4000)) {
					if (victim.getCombatState().getLastHitBy() != null
							&& attacker != victim.getCombatState().getLastHitBy()) {
						if (bhTarget) {
							return true;
						}
						if (victim.getCombatState().getLastAttackerWith() == attacker
								&& System.currentTimeMillis() - victim.getCombatState().getLastCombatWith() < 6000) {
							return true;
						}
						if (messages && attacker.getActionSender() != null) {
							attacker.getActionSender().sendMessage("Someone else is already fighting your opponent.");
						}
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public void hit(final Mob attacker, Mob victim) {
		/**
		 * This is to prevent immediate teaming, EG: someone walking into Edgeville(1v1)
		 * and 2 people casting spells at the same time, usually it wouldn't set the
		 * timer till the damage had been inflicted
		 */
		victim.getCombatState().setLastHitTimer(10000);
		victim.getCombatState().setLastHitBy(attacker);
		victim.getCombatState().updateCombatWith(attacker);
		victim.getActionQueue().clearRemovableActions();
		/**
		 * Stops other emotes from overlapping important ones.
		 */
		attacker.getCombatState().setWeaponSwitchTimer(2);
		attacker.getCombatState().setCanAnimate(false);
		World.getWorld().submit(new Tickable(1) {
			public void execute() {
				attacker.getCombatState().setCanAnimate(true);
				this.stop();
			}
		});
		if (!BoundaryManager.isWithinBoundaryNoZ(attacker.getLocation(), "ClanWarsFFAFull")
				&& !BoundaryManager.isWithinBoundaryNoZ(victim.getLocation(), "ClanWarsFFAFull")
				&& !attacker.getCombatState().getDamageMap().getTotalDamages().containsKey(victim)
				&& attacker.isPlayer() && victim.isPlayer()) {
			attacker.getCombatState().setSkullTicks(100 * 10); // 10 * 1 min
			victim.getCombatState().getDamageMap().incrementTotalDamage(attacker, 0);
		}
		if (victim instanceof Whirlpool) {
			Whirlpool whirlpool = (Whirlpool) victim;
			if (whirlpool.getTransformId() != KrakenServiceImpl.TENTACLE) {
				attacker.getActionQueue().clearAllActions();
			}
		}
		if (victim instanceof Kraken) {
			Kraken kraken = (Kraken) victim;
			if (kraken.getTransformId() != KrakenServiceImpl.KRAKEN) {
				attacker.getActionQueue().clearAllActions();
			}
		}
		// Item on-attack degradation
		for (final Item item : attacker.getEquipment().getItems()) {
			if (item == null)
				continue;

			final ItemServiceImpl.Degradable degradable = ItemServiceImpl.Degradable.forItem(item);
			if (degradable != null
					&& degradable.getDegradationStrategy() == ItemServiceImpl.DegradationStrategy.ON_ATTACK) {
				itemService.degradeItem(attacker, item);
			}
		}

		for (final Item item : victim.getEquipment().getItems()) {
			if (item == null)
				continue;

			final ItemServiceImpl.Degradable degradable = ItemServiceImpl.Degradable.forItem(item);
			if (degradable != null
					&& degradable.getDegradationStrategy() == ItemServiceImpl.DegradationStrategy.ON_DEFEND) {
				itemService.degradeItem(victim, item);
			}
		}
	}

	@Override
	public void defend(Mob attacker, Mob victim, boolean blockAnimation) {
		if (victim.isAutoRetaliating()) {

			// Only auto-retaliate if the victim is idle
			if (victim.isIdle() && victim.getCombatState().getAttackEvent() == null)
				victim.getCombatState().startAttacking(attacker, true);
		}
		if (blockAnimation && victim.getCombatState().canAnimate()) {
			Animation defend = Animation.create(404);
			Item shield = victim.getEquipment().get(Equipment.SLOT_SHIELD);
			Item weapon = victim.getEquipment().get(Equipment.SLOT_WEAPON);
			String shieldName = shield != null ? shield.getDefinition2().getName() : "";
			if (shieldName == null)
				return;
			if (shieldName.contains("shield") || shieldName.contains("ket-xil") || shieldName.contains("defender")) {
				defend = shield.getEquipmentDefinition().getAnimation().getDefend();
			} else if (weapon != null) {
				defend = weapon.getEquipmentDefinition().getAnimation().getDefend();
			} else if (shield != null) {
				defend = shield.getEquipmentDefinition().getAnimation().getDefend();
			} else {
				defend = victim.getDefendAnimation();
			}
			victim.playAnimation(defend);
		}
	}

	@Override
	public boolean canSpecial(final Mob attacker, final Mob victim) {
		Item weapon = attacker.getEquipment().get(Equipment.SLOT_WEAPON);
		if (weapon != null && weapon.getEquipmentDefinition() != null) {
			if (victim != null && !victim.getCombatState().isDead()
					&& attacker.getActiveCombatAction().canHit(attacker, victim, true, false)) {
				if (attacker.getCombatState().getSpecialEnergy() >= weapon.getEquipmentDefinition()
						.getSpecialConsumption()) {
					if (attacker.getCombatState().getAttackDelay() == 0) {
						switch (weapon.getId()) {
						case 861:
						case 11235:
							if (attacker.getEquipment().get(Equipment.SLOT_ARROWS).getCount() < 2) {
								attacker.getActionSender()
										.sendMessage("You need atleast 2 arrows to perform this special.");
								attacker.getCombatState().setSpecial(false);
								return false;
							}
							break;
						case 11785:
							if (attacker.getEquipment().get(Equipment.SLOT_ARROWS).getCount() < 1) {
								attacker.getActionSender()
										.sendMessage("You need atleast 1 bolt to perform this special.");
								attacker.getCombatState().setSpecial(false);
								return false;
							}
							break;
						case 12926:
							ItemService service = Server.getInjector().getInstance(ItemService.class);
							if (attacker instanceof Player && service.getCharges((Player) attacker, weapon) < 1) {
								attacker.getActionSender().sendMessage(
										"You need atleast 1 dart loaded in your blowpipe to perform this special.");
								attacker.getCombatState().setSpecial(false);
								return false;
							}
							break;
						}
					} else {
						switch (weapon.getId()) {
						case 4153:
						case 12848:
							if (!attacker.canAnimate()
									|| attacker.getActiveCombatAction() != MeleeCombatAction.getAction()
									|| !attacker.getActiveCombatAction().canHit(attacker, victim, true, false)
									|| !attacker.getLocation().isWithinDistance(attacker, victim,
											attacker.getActiveCombatAction().distance(attacker))) {
								attacker.getCombatState().setSpecial(false);
								return false;
							}
							break;
						}
					}
					return true;
				} else {
					attacker.getActionSender().sendMessage("You do not have enough special energy left.");
					attacker.getCombatState().setSpecial(false);
					return false;
				}
			} else {
				switch (weapon.getId()) {
				case 4153:
				case 12848:
					break;
				}
				return false;
			}
		}
		return false;
	}

	@Override
	public void special(final Mob attacker, final Mob victim, int damage, boolean boltSpecial) {
		Item weapon = attacker.getEquipment().get(Equipment.SLOT_WEAPON);
		BowType bow = weapon.getEquipmentDefinition().getBowType();
		if (bow == BowType.BRONZE_CBOW || bow == BowType.IRON_CBOW || bow == BowType.STEEL_CBOW
				|| bow == BowType.MITH_CBOW || bow == BowType.ADAMANT_CBOW || bow == BowType.RUNE_CBOW
				|| bow == BowType.DRAGON_CBOW || bow == BowType.DRAGON_CBOW) {
			weapon = attacker.getEquipment().get(Equipment.SLOT_ARROWS);
		}
		attacker.getCombatState().inverseSpecial();
		switch (weapon.getId()) {
		case 4587:
			attacker.playGraphics(Graphic.create(347, (100 << 16)));
			attacker.playAnimation(Animation.create(1872));
			if (damage > 0 && victim.isPlayer()) {
				Player vic = (Player) victim;
				vic.getCombatState().setPrayer(Prayers.PROTECT_FROM_MAGIC, false);
				vic.getCombatState().setPrayer(Prayers.PROTECT_FROM_MISSILES, false);
				vic.getCombatState().setPrayer(Prayers.PROTECT_FROM_MELEE, false);
				vic.getCombatState().setPrayerHeadIcon(-1);
				Prayers.refresh(vic);
				vic.setAttribute("noProtectionPrayer", true);
				World.getWorld().submit(new StoppingTick(8) {

					@Override
					public void executeAndStop() {
						vic.removeAttribute("noProtectionPrayer");
					}
				});
			}
			break;
		case 4151:
		case 12006:
			attacker.playAnimation(weapon.getEquipmentDefinition().getAnimation().getAttack(0));
			victim.playGraphics(Graphic.create(341, 0, 100));
			break;
		case 1215:
		case 1231:
		case 5680:
		case 5698:
			attacker.playAnimation(Animation.create(1062));
			attacker.playGraphics(Graphic.create(252, 0, 100));
			break;
		case 13265:
		case 13271:
			attacker.playAnimation(Animation.create(1062));
			attacker.playGraphics(Graphic.create(1283, 0, 100));
			break;
		case 13263:
			attacker.playAnimation(Animation.create(7010));
			victim.playGraphics(Graphic.create(1284));
			break;
		case 4153:
		case 12848:
			attacker.playAnimation(Animation.create(1667));
			attacker.playGraphics(Graphic.create(340, 0, 100));
			break;
		case 11802:
			attacker.playAnimation(Animation.create(7061));
			attacker.playGraphics(Graphic.create(1211, 0, 0));
			break;
		case 19481:
			attacker.playAnimation(Animation.create(7222));
			attacker.playGraphics(Graphic.create(1301, 0, 0));
			break;
		case 3204:
			attacker.playAnimation(Animation.create(1203));
			attacker.playGraphics(Graphic.create(1172, 0, 0));
			break;
		case 21009:
			attacker.playAnimation(Animation.create(7515));
			break;
		case 13652:
			attacker.playAnimation(Animation.create(5283));
			attacker.playGraphics(Graphic.create(1950, 0, 100));
			break;
		case 1305:
			attacker.playAnimation(Animation.create(1058));
			attacker.playGraphics(Graphic.create(248, 0, 100));
			break;
		case 1434:
			attacker.playAnimation(Animation.create(1060));
			attacker.playGraphics(Graphic.create(251, 0, 100));
			break;
		case 11838:
			attacker.playAnimation(Animation.create(1132));
			attacker.playGraphics(Graphic.create(1213, 0, 100));
			break;
		case 13576:
			attacker.playAnimation(Animation.create(1378));
			attacker.playGraphics(Graphic.create(1292, 0, 0));
			if (damage > 0) {
				int current = victim.getSkills().getLevel(Skills.DEFENCE);
				int decrement = (int) (current - (current * 0.7)); // current lvl - 30% = decrement
				victim.getSkills().decreaseLevelToZero(Skills.DEFENCE, decrement);
			}
			break;
		case 11804:
			int[] skills = new int[] { Skills.DEFENCE, Skills.STRENGTH, Skills.PRAYER, Skills.ATTACK, Skills.MAGIC,
					Skills.RANGE };
			int newDmg = damage / 10;
			for (int i = 0; i < skills.length; i++) {
				if (newDmg > 0) {
					if (victim.getSkills().getLevel(skills[i]) > 0) {
						int before = victim.getSkills().getLevel(skills[i]);
						victim.getSkills().decreaseLevelToZero(skills[i], newDmg);
						int after = before - victim.getSkills().getLevel(skills[i]);
						newDmg -= after;
					}
				} else
					break;
			}
			attacker.playAnimation(Animation.create(7060));
			attacker.playGraphics(Graphic.create(1212, 0, 0));
			break;
		case 11806:
			attacker.playAnimation(Animation.create(7058));
			attacker.playGraphics(Graphic.create(1209, 0, 0));
			int hitpointsHeal = damage / 2;
			int prayerHeal = damage / 4;
			if (damage < 22) {
				hitpointsHeal = 10;
				prayerHeal = 5;
			}
			attacker.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, hitpointsHeal);
			if (attacker.getSkills().getPrayerPoints() + prayerHeal <= attacker.getSkills()
					.getLevelForExperience(Skills.PRAYER))
				attacker.getSkills().setPrayerPoints(attacker.getSkills().getPrayerPoints() + prayerHeal, true);
			attacker.getActionSender().sendSkillLevels();
			break;
		case 11808:
			attacker.playAnimation(Animation.create(7057));
			attacker.playGraphics(Graphic.create(1210, 0, 0));
			victim.playGraphics(Graphic.create(369, 0, 0));
			int freezeTimer = 33;
			if (victim.getCombatState().canMove() && victim.getCombatState().canBeFrozen()) {
				if (victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC))
					freezeTimer = freezeTimer / 2;
				victim.getCombatState().setCanMove(false);
				victim.getCombatState().setCanBeFrozen(false);
				victim.getWalkingQueue().reset();
				if (victim.getActionSender() != null)
					victim.getActionSender().sendMessage("You have been frozen!");
				World.getWorld().submit(new Tickable(freezeTimer) {
					public void execute() {
						victim.getCombatState().setCanMove(true);
						this.stop();
					}
				});
				World.getWorld().submit(new Tickable(freezeTimer + 5) {
					public void execute() {
						victim.getCombatState().setCanBeFrozen(true);
						this.stop();
					}
				});
			}
			break;
		case 11235:
			attacker.playAnimation(Animation.create(426));
			int clientSpeed;
			int showDelay;
			int slope;
			if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 55;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 3)) {
				clientSpeed = 55;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 65;
			} else {
				clientSpeed = 75;
			}
			showDelay = 45;
			slope = 15;
			clientSpeed += 30;
			attacker.playProjectile(Projectile.create(attacker.getLocation(), victim,
					attacker.getEquipment().get(Equipment.SLOT_ARROWS).getId() == 11212 ? 1099 : 1101, showDelay, 50,
					clientSpeed - 10, 41, 31, 3, 36));
			attacker.playProjectile(Projectile.create(attacker.getLocation(), victim,
					attacker.getEquipment().get(Equipment.SLOT_ARROWS).getId() == 11212 ? 1099 : 1101, showDelay, 50,
					clientSpeed + 10, 46, 36, slope + 6, 36));
			victim.playGraphics(
					Graphic.create(attacker.getEquipment().get(Equipment.SLOT_ARROWS).getId() == 11212 ? 1100 : 1103,
							clientSpeed, 100));
			break;
		case 861:
			int distance = attacker.getLocation().distanceToEntity(attacker, victim);
			attacker.playAnimation(Animation.create(1074));
			attacker.playGraphics(Graphic.create(256, 0, 100));
			World.getWorld().submit(new Tickable(1) {
				public void execute() {
					attacker.playGraphics(Graphic.create(256, 0, 100));
					this.stop();
				}
			});
			attacker.playProjectile(Projectile.create(attacker.getLocation(), victim, 249, 30, 50, 40 + (distance * 5),
					43, 35, 10, 36));
			attacker.playProjectile(Projectile.create(attacker.getLocation(), victim, 249, 60, 50, 65 + (distance * 5),
					43, 35, 10, 36));
			break;
		case 11785:
			int d = attacker.getLocation().distanceToEntity(attacker, victim);
			attacker.playAnimation(Animation.create(4230));
			attacker.playProjectile(
					Projectile.create(attacker.getLocation(), victim, 301, 60, 50, 65 + (d * 5), 43, 35, 10, 36));
			break;
		case 805:
			Mob[] closeMobs = new Mob[3];
			int[] distances = new int[] { 10, 10, 10 };
			if (BoundaryManager.isWithinBoundaryNoZ(attacker.getLocation(), "MultiCombat") || attacker.inMulti()) {
				for (Mob mob : World.getWorld().getRegionManager().getLocalMobs(victim)) {
					boolean canContinue = true;
					for (int i = 0; i < closeMobs.length; i++) {
						if (canContinue) {
							int newDist = attacker.getLocation().distanceToEntity(attacker, mob);
							if (newDist <= distances[i]) {
								if (mob != attacker && mob != victim
										&& mob.getLocation().isWithinDistance(attacker, mob, 5)
										&& attacker.getActiveCombatAction().canHit(attacker, mob, false, false)
										&& BoundaryManager.isWithinBoundaryNoZ(mob.getLocation(), "MultiCombat")
										|| mob.inMulti()) {
									closeMobs[i] = mob;
									distances[i] = newDist;
									canContinue = false;
								}
							}
						} else {
							break;
						}
					}
				}
			}

			int count = 3;
			final int maxHit = CombatFormula.calculateRangeMaxHit(attacker, victim, true);
			final int newDamage = damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(),
					Skills.RANGE, Prayers.PROTECT_FROM_MISSILES, true, false);

			if (BoundaryManager.isWithinBoundaryNoZ(attacker.getLocation(), "MultiCombat") || attacker.inMulti()) {
				Mob lastMob = victim;
				int lastDelay = 60;
				int lastSpeed = 90;
				for (final Mob mob : closeMobs) {
					if (mob == null) {
						continue;
					}
					attacker.getCombatState().decreaseSpecial(10);
					lastMob.playProjectile(Projectile.create(lastMob.getLocation(), mob, 258, lastDelay, 50, lastSpeed,
							43, 35, 13, 48));
					World.getWorld().submit(new Tickable(count) {
						@Override
						public void execute() {
							int hit = random.nextInt(newDamage < 1 ? 1 : newDamage + 1); // +1
																							// as
																							// its
																							// exclusive
							mob.inflictDamage(new Hit(hit), attacker);
							smite(attacker, mob, hit);
							recoil(attacker, mob, hit);
							victim.getActiveCombatAction().defend(attacker, mob, true);
							this.stop();
						}
					});
					lastDelay += 40;
					lastSpeed += 35;
					lastMob = mob;
					count++;
				}
			}

			attacker.playAnimation(Animation.create(1068));
			attacker.playGraphics(Graphic.create(257, 0, 100));
			attacker.playProjectile(Projectile.create(attacker.getLocation(), victim, 258, 45, 50, 55, 43, 35, 13, 48));
			attacker.getCombatState().setSpecial(false);
			break;
		case 9236:
			victim.playGraphics(Graphic.create(749));
			break;
		case 9238:
			victim.playGraphics(Graphic.create(750));
			break;
		case 9239:
			victim.playGraphics(Graphic.create(757));
			break;
		case 9240:
			victim.playGraphics(Graphic.create(745, 0, 100));
			if (victim.isPlayer()) {
				int prayerReduction = random.nextInt(9);
				int before = victim.getSkills().getLevel(Skills.PRAYER);
				victim.getSkills().decreaseLevelToZero(Skills.PRAYER, prayerReduction);
				int addition = before - victim.getSkills().getLevel(Skills.PRAYER);
				attacker.getSkills().increaseLevelToMaximum(Skills.PRAYER, addition);
			}
			break;
		case 9241:
			victim.playGraphics(Graphic.create(752));
			victim.getCombatState().setPoisonDamage(6, attacker);
			if (victim.getActionSender() != null) {
				victim.getActionSender().sendMessage("You have been poisoned!");
				player.getActionSender().sendConfig(102, 1);
			}
			break;
		case 9242:
			victim.playGraphics(Graphic.create(754));
			break;
		case 9243:
			victim.playGraphics(Graphic.create(758));
			break;
		case 9244:
			victim.playGraphics(Graphic.create(756));
			break;
		case 9245:
			victim.playGraphics(Graphic.create(753));
			break;
		}
		if (!boltSpecial)
			attacker.getCombatState().decreaseSpecial(weapon.getEquipmentDefinition().getSpecialConsumption());
	}

	@Override
	public void special(Mob attacker, final Item item) {
		if (attacker.getInteractingEntity() == null || attacker.getInteractionMode() != InteractionMode.ATTACK)
			return;
		switch (item.getId()) {
		case 4153:
		case 12848:
			attacker.getCombatState().inverseSpecial();
			int damage = CombatFormula.calculateMeleeMaxHit(attacker, attacker.getInteractingEntity(), true);
			Hit hit = new Hit(random
					.nextInt(attacker.getActiveCombatAction().damage(damage, attacker, attacker.getInteractingEntity(),
							AttackType.CRUSH, Skills.ATTACK, Prayers.PROTECT_FROM_MELEE, true, false) + 1));
			if (hit.getDamage() > attacker.getInteractingEntity().getSkills().getLevel(Skills.HITPOINTS)) {
				hit = new Hit(hit.getDamage());
			}
			attacker.getInteractingEntity().inflictDamage(hit, attacker);
			special(attacker, attacker.getInteractingEntity(), hit.getDamage(), false);
			break;
		}
	}

	@Override
	public int damage(int maxHit, Mob attacker, Mob victim, AttackType attackType, int skill, int prayer,
			boolean special, boolean ignorePrayers) {
		boolean veracEffect = false;
		if (skill == Skills.ATTACK) {
			if (CombatFormula.fullVerac(attacker)) {
				if (random.nextInt(4) == 1) {
					veracEffect = true;
				}
			}
		}
		if (attacker.isPlayer() && attacker.getInterfaceState().hasChatboxInterfaceOpen()) {
			attacker.getActionSender().removeChatboxInterface();
		}
		if (victim.isPlayer()) {
			Player vic = (Player) victim;
			if (vic.getTransaction() != null && (vic.getInterfaceState().isInterfaceOpen(TradeServiceImpl.TRADE_WIDGET)
					|| vic.getInterfaceState().isInterfaceOpen(TradeServiceImpl.CONFIRMATION_WIDGET))) {
				TradeService tradeService = Server.getInjector().getInstance(TradeService.class);
				tradeService.endTransaction(vic.getTransaction(), true);
			}
			if (victim.getInterfaceState().isInterfaceOpen(PriceChecker.PRICE_INVENTORY_INTERFACE)) {
				Player player = (Player) victim;
				PriceChecker.returnItems(player);
			}
			if (victim.getInterfaceState().isInterfaceOpen(Equipment.SCREEN)
					|| victim.getInterfaceState().isInterfaceOpen(Bank.BANK_INVENTORY_INTERFACE)
					|| victim.getInterfaceState().isInterfaceOpen(Trade.TRADE_INVENTORY_INTERFACE)
					|| victim.getInterfaceState().isInterfaceOpen(Shop.SHOP_INVENTORY_INTERFACE)
					|| victim.getInterfaceState().isInterfaceOpen(PriceChecker.PRICE_INVENTORY_INTERFACE)) {
				victim.getActionSender().removeInventoryInterface();
				victim.getInterfaceState().setOpenShop(-1);
			}
		}
		if (victim.isPlayer() && victim.getInterfaceState().hasChatboxInterfaceOpen()) {
			victim.getActionSender().removeChatboxInterface();
		}
		double attackBonus = attacker.getCombatState().getBonus(attackType.getId()) == 0 ? 1
				: attacker.getCombatState().getBonus(attackType.getId());
		if (attackBonus < 1)
			attackBonus = 1;
		double bonus = 1;
		if (special) {
			if (attacker.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
				switch (attacker.getEquipment().get(Equipment.SLOT_WEAPON).getId()) {
				case 3101:
				case 1215:
				case 1231:
				case 5680:
				case 5698:
				case 13265:
				case 13263:
					bonus = 1.25;
					break;
				case 1305:
					bonus = 1.20;
					break;
				case 21009:
					bonus = 1.30;
					break;
				case 11235:
					bonus = 1.15;
					break;
				case 11802:
					bonus = 1.20;
					break;
				case 19481:
					bonus = 1.25;
					break;
				case 3204:
					bonus = 1.10;
					break;
				case 13576:
					bonus = 1.5;
					break;
				case 11785:
					attackBonus = (attackBonus * 2);
					break;
				case 11806:
					attackBonus = 1.1;
					break;
				}
			}
		} else {
			if (attacker.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
				switch (attacker.getEquipment().get(Equipment.SLOT_WEAPON).getId()) {
				case 11802:
					bonus = 1.1;
					break;
				case 19481:
					bonus = 1.1;
					break;
				}
			}
		}
		double attackCalc = attackBonus * (attacker.getSkills().getLevel(skill) * bonus);

		/**
		 * Prayer calculations.
		 */
		if (skill == Skills.ATTACK) {
			// melee attack prayer modifiers
			if (attacker.getCombatState().getPrayer(Prayers.CLARITY_OF_THOUGHT)) {
				attackCalc *= 1.05;
			} else if (attacker.getCombatState().getPrayer(Prayers.IMPROVED_REFLEXES)) {
				attackCalc *= 1.10;
			} else if (attacker.getCombatState().getPrayer(Prayers.INCREDIBLE_REFLEXES)) {
				attackCalc *= 1.15;
			} else if (attacker.getCombatState().getPrayer(Prayers.CHIVALRY)) {
				attackCalc *= 1.15;
			} else if (attacker.getCombatState().getPrayer(Prayers.PIETY)) {
				attackCalc *= 1.20;
			}
			if (CombatFormula.fullVoidMelee(attacker))
				attackCalc *= 1.10;
			if (CombatFormula.fullEliteVoidMelee(attacker))
				attackCalc *= 1.125;
			if (attacker.isPlayer() && victim.isNPC()) {
				NPC npc = (NPC) victim;
				if (NPCUtils.isUndeadNPC(npc.getDefinition().getName())) {
					Player player = (Player) attacker;
					if (player.getEquipment().contains(4081))
						attackCalc *= 1.15;
					else if (player.getEquipment().contains(10588))
						attackCalc *= 1.2;
				}
			}
		} else if (skill == Skills.RANGE) {
			if (CombatFormula.fullVoidRange(attacker))
				attackCalc *= 1.10;
			if (CombatFormula.fullEliteVoidRange(attacker))
				attackCalc *= 1.125;
		}

		/**
		 * As with the melee/range max hit calcs, combat style bonuses are added AFTER
		 * the modifiers have taken place.
		 */
		if (attacker.getCombatState().getCombatStyle() == CombatStyle.ACCURATE)
			attackCalc += 3;
		else if (attacker.getCombatState().getCombatStyle() == CombatStyle.CONTROLLED_1
				|| attacker.getCombatState().getCombatStyle() == CombatStyle.CONTROLLED_2
				|| attacker.getCombatState().getCombatStyle() == CombatStyle.CONTROLLED_3) {
			attackCalc += 1;
		}

		double defenceBonus = victim.getCombatState().getBonus(attackType.getId() + 5) == 0 ? 1
				: victim.getCombatState().getBonus(attackType.getId() + 5);
		double defenceCalc = defenceBonus * victim.getSkills().getLevel(Skills.DEFENCE);

		/**
		 * Prayer calculations.
		 */
		if (victim.getCombatState().getPrayer(Prayers.THICK_SKIN)) {
			defenceCalc *= 1.05;
		} else if (victim.getCombatState().getPrayer(Prayers.ROCK_SKIN)) {
			defenceCalc *= 1.10;
		} else if (victim.getCombatState().getPrayer(Prayers.STEEL_SKIN)) {
			defenceCalc *= 1.15;
		} else if (victim.getCombatState().getPrayer(Prayers.CHIVALRY)) {
			defenceCalc *= 1.20;
		} else if (victim.getCombatState().getPrayer(Prayers.PIETY) || victim.getCombatState().getPrayer(Prayers.RIGOUR)
				|| victim.getCombatState().getPrayer(Prayers.AUGURY)) {
			defenceCalc *= 1.25;
		}

		/**
		 * As with the melee/range max hit calcs, combat style bonuses are added AFTER
		 * the modifiers have taken place.
		 */
		if (victim.getCombatState().getCombatStyle() == CombatStyle.DEFENSIVE) {
			defenceCalc += 3;
		} else if (victim.getCombatState().getCombatStyle() == CombatStyle.CONTROLLED_1
				|| victim.getCombatState().getCombatStyle() == CombatStyle.CONTROLLED_2
				|| victim.getCombatState().getCombatStyle() == CombatStyle.CONTROLLED_3) {
			defenceCalc += 1;
		}

		if (veracEffect)
			defenceCalc = 0;

		/**
		 * The chance to succeed out of 1.0.
		 */
		double hitSucceed = (attackCalc / defenceCalc);
		if (hitSucceed > 1.0) {
			hitSucceed = 1;
		}
		if (hitSucceed < random.nextDouble()) {
			return 0;
		} else {
			/**
			 * Protection prayers. Note: If an NPC is hitting on a protection prayer, it is
			 * 100% blocked, where as if a player is hitting on a protection prayer, their
			 * damage is simply reduced by 40%. Also, if the attacker has the Verac effect
			 * active, it will ignore the opponent's protection prayers.
			 */
			int hit = maxHit;
			double protectionPrayer = (victim.getCombatState().getPrayer(prayer) && !veracEffect)
					? (attacker.isNPC() ? 1.0 : 0.40)
					: 1;
			if (ignorePrayers) {
				protectionPrayer = 1;
			} // kk try
			if (protectionPrayer != 1 || (victim.getCombatState().getPrayer(prayer) && attacker.isNPC())) {

				if ((!ignorePrayers && attacker.isNPC()) || attacker.isPlayer()) {
					double protectionHit = hit * protectionPrayer; // +1 as its
																	// exclusive
					hit -= protectionHit;
				}
				if (hit < 1)
					hit = 0;
			}
			if (victim.isPlayer()) {
				Item shield = victim.getEquipment().get(Equipment.SLOT_SHIELD);

				if (shield != null && shield.getId() == 12817 && Misc.random(9) <= 6) {
					hit *= 0.75;
				}

				Item necklace = victim.getEquipment().get(Equipment.SLOT_AMULET);
				if (necklace != null && necklace.getId() == 11090) {
					if ((victim.getSkills().getLevel(Skills.HITPOINTS) - hit) < victim.getSkills()
							.getLevelForExperience(Skills.HITPOINTS) * 0.20000000000000001D
							&& victim.getSkills().getLevel(Skills.HITPOINTS) - hit > 0) {
						victim.getEquipment().set(Equipment.SLOT_AMULET, null);
						victim.getActionSender()
								.sendMessage("The Pheonix Necklace saves you but was destroyed in the process.");
						victim.getSkills().setLevel(Skills.HITPOINTS,
								(int) (victim.getSkills().getLevel(Skills.HITPOINTS)
										+ victim.getSkills().getLevelForExperience(Skills.HITPOINTS) * 0.3D));
					}
				}
			}
			if (victim.isNPC()) {
				NPC n = (NPC) victim;
				if (n.getCombatDefinition() != null) {
					if (n.getCombatDefinition().isArenaNPC()
							&& attacker.getCombatState().getAttackType() != AttackType.MAGIC) {
						hit = 0;
					}
					if (n.getId() == 2266 && attacker.getCombatState().getAttackType() != AttackType.RANGE
							|| n.getId() == 2265 && attacker.getCombatState().getAttackType() == AttackType.RANGE
							|| attacker.getCombatState().getAttackType() == AttackType.MAGIC) {
						hit = 0;
					}
					if (n.getId() == 1101) {
						boolean bolts = false;
						if (attacker.getCombatState().getAttackType() == AttackType.RANGE
								&& attacker.getEquipment().get(Equipment.SLOT_ARROWS) != null) {

							for (int i = 9236; i <= 9245; i++) {
								if (attacker.getEquipment().get(Equipment.SLOT_ARROWS).getId() == i) {
									bolts = true;
								}
							}
							if (!bolts) {
								if (attacker.getActionSender() != null) {
									attacker.getActionSender().sendMessage(
											"The bolts you are using seem to have no effect on the sea snake!");
								}
								hit = 0;
							}
						} else {
							if (attacker.getActionSender() != null)
								attacker.getActionSender().sendMessage("You're attacks dont seem to phase the Snake!");
							hit = 0;
						}
					}
					if (n.getSkills().getLevel(Skills.HITPOINTS) < 1)
						hit = 0;
				}
			}
			return hit;
		}
	}

	@Override
	public void addExperience(Mob attacker, int damage) {
		double xp = damage * 1.333333333 * Constants.COMBAT_EXP;
		attacker.getSkills().addExperience(Skills.HITPOINTS, xp / 2);
	}

	@Override
	public void recoil(Mob attacker, Mob victim, int damage) {
		if (victim.getEquipment().get(Equipment.SLOT_RING) != null
				&& victim.getEquipment().get(Equipment.SLOT_RING).getId() == 2550) {
			if (damage > 0) {
				int recoil = (int) Math.ceil(damage / 10);
				if (recoil < 1) {
					recoil = 1;
				}
				if (recoil > victim.getCombatState().getRingOfRecoil()) {
					recoil = victim.getCombatState().getRingOfRecoil();
				}
				if (recoil > attacker.getSkills().getLevel(Skills.HITPOINTS)) {
					recoil = attacker.getSkills().getLevel(Skills.HITPOINTS);
				}
				if (recoil < 1) {
					return;
				}
				victim.getCombatState().setRingOfRecoil(victim.getCombatState().getRingOfRecoil() - recoil);
				attacker.inflictDamage(new Hit(recoil, HitPriority.LOW_PRIORITY), victim);
				if (victim.getCombatState().getRingOfRecoil() < 1) {
					victim.getEquipment().remove(new Item(2550));
					victim.getCombatState().setRingOfRecoil(40);
					victim.getActionSender().sendMessage("Your Ring of Recoil has shattered.");
				}
			}
		}
		if (random.nextFloat() <= 0.25f && CombatFormula.fullDharok(attacker)
				&& CombatFormula.hasAmuletOfTheDamned(attacker) && damage > 0) {
			int recoil = (int) Math.ceil((float) damage * 0.15);

			if (recoil < 1) {
				recoil = 1;
			}

			if (recoil > attacker.getSkills().getLevel(Skills.HITPOINTS)) {
				recoil = attacker.getSkills().getLevel(Skills.HITPOINTS);
			}
			if (recoil < 1) {
				return;
			}

			attacker.inflictDamage(new Hit(recoil, HitPriority.LOW_PRIORITY), victim);
		}
	}

	@Override
	public void smite(Mob attacker, Mob victim, int damage) {
		if (attacker.getCombatState().getPrayer(Prayers.SMITE)) {
			int prayerDrain = (int) (damage * 0.25);
			victim.getSkills().decreasePrayerPoints(prayerDrain);
		}
	}

	@Override
	public void vengeance(final Mob attacker, final Mob victim, final int damage, int delay) {
		if (!victim.getCombatState().hasVengeance()) {
			return;
		}
		World.getWorld().submit(new Tickable(delay) {
			@Override
			public void execute() {
				if (damage < 2) {
					return;
				}
				int hit = (int) (damage * 0.75);
				if (hit < 1) {
					return;
				}
				if (victim.getCombatState().isDead()) {
					hit = 0;
				}
				victim.forceChat("Taste vengeance!");
				victim.getCombatState().setVengeance(false);
				attacker.inflictDamage(new Hit(hit > attacker.getSkills().getLevel(Skills.HITPOINTS)
						? attacker.getSkills().getLevel(Skills.HITPOINTS)
						: hit), victim);
				this.stop();
			}
		});
	}
}