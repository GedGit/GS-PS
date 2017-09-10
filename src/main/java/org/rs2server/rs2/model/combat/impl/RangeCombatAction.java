package org.rs2server.rs2.model.combat.impl;

import org.rs2server.Server;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.domain.service.api.content.ItemService;
import org.rs2server.rs2.domain.service.api.content.KrakenService;
import org.rs2server.rs2.domain.service.impl.content.KrakenServiceImpl;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatFormula;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.CombatState.CombatStyle;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.equipment.EquipmentDefinition;
import org.rs2server.rs2.model.equipment.PoisonType;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.npc.impl.kraken.Kraken;
import org.rs2server.rs2.model.npc.impl.kraken.KrakenCombatState;
import org.rs2server.rs2.model.npc.impl.kraken.Whirlpool;
import org.rs2server.rs2.model.npc.impl.zulrah.Zulrah;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.Random;

/**
 * Normal range combat action.
 *
 * @author Graham Edgecombe
 */
public class RangeCombatAction extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final RangeCombatAction INSTANCE = new RangeCombatAction();
	private final ItemService itemService;

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
	private RangeCombatAction() {
		itemService = Server.getInjector().getInstance(ItemService.class);
	}

	@Override
	public boolean canHit(Mob attacker, Mob victim, boolean messages, boolean cannon) {
		if (!super.canHit(attacker, victim, messages, cannon)) {
			return false;
		}
		if (cannon) {
			return true;
		}

		if (attacker.isPlayer()) {
			Item weapon = attacker.getEquipment().get(Equipment.SLOT_WEAPON);
			if (weapon == null) {
				return false;
			}

			EquipmentDefinition weaponEquipDef = weapon.getEquipmentDefinition();

			if (weapon.getId() == 12926) {
				Player player = (Player) attacker;

				int charges = itemService.getCharges(player, weapon);

				if (charges == 0) {
					attacker.getActionSender().sendMessage("Your blowpipe is empty.");
					return false;
				}
				return true;
			} else {

				BowType bowType = weaponEquipDef.getBowType();
				if (bowType != null && bowType != BowType.CRYSTAL_BOW) {

					Item arrows = attacker.getEquipment().get(Equipment.SLOT_ARROWS);
					if (arrows == null) {
						attacker.getActionSender()
								.sendMessage("There are no "
										+ ((bowType == BowType.KARILS_XBOW || bowType == BowType.BRONZE_CBOW
												|| bowType == BowType.IRON_CBOW || bowType == BowType.STEEL_CBOW
												|| bowType == BowType.MITH_CBOW || bowType == BowType.ADAMANT_CBOW
												|| bowType == BowType.RUNE_CBOW || bowType == BowType.DRAGON_CBOW
												|| bowType == BowType.DRAGON_CBOW || bowType == BowType.DORGESHUUN_CBOW)
												|| bowType == BowType.ARMADYL_CBOW ? "bolts" : "arrows")
										+ " left in your quiver.");
						return false;
					}

					if (bowType == BowType.DARK_BOW) {
						if (arrows.getCount() < 2) {
							attacker.getActionSender()
									.sendMessage("You need atleast 2 arrows to use the Dark bow's attack.");
							return false;
						}
					}

					EquipmentDefinition arrowEquipDef = arrows.getEquipmentDefinition();
					ArrowType arrowType = arrowEquipDef.getArrowType();

					boolean hasCorrectArrows = false;
					for (ArrowType correctArrowType : bowType.getValidArrows()) {
						if (correctArrowType == arrowType) {
							hasCorrectArrows = true;
							break;
						}
					}
					if (!hasCorrectArrows) {
						attacker.getActionSender().sendMessage("You can't use " + arrows.getDefinition2().getName()
								+ "s with a " + weapon.getDefinition2().getName() + ".");
						return false;
					}
				}
			}
		}
		return true; // TODO implement!
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		Graphic pullback = null;
		int projectile = -1;
		double dropRate = 0;
		int hitDelay = 2;

		Item ammunition = null;
		Item weapon = null;
		EquipmentDefinition arrowEquipDef = null;
		EquipmentDefinition weaponEquipDef = null;

		BowType bowType = null;
		RangeWeaponType rangeWeaponType = null;

		if (attacker.getDrawbackGraphic() != null) {
			pullback = attacker.getDrawbackGraphic();
		}
		if (attacker.getProjectileId() != -1) {
			projectile = attacker.getProjectileId();
		}

		if (pullback == null && projectile == -1 && attacker.getEquipment() != null) {
			weapon = attacker.getEquipment().get(Equipment.SLOT_WEAPON);
			if (weapon == null)
				return; // every range type requires a weapon
			weaponEquipDef = weapon.getEquipmentDefinition();
			bowType = weaponEquipDef.getBowType();
			if (attacker.isPlayer() && weapon.getId() == 12926) {
				Player player = (Player) attacker;

				int charges = itemService.getCharges(player, weapon);
				int chargedWith = itemService.getChargedItem(player, weapon);
				if (charges > 0 && chargedWith > 0) {
					EquipmentDefinition chargeDef = EquipmentDefinition.forId(chargedWith);

					rangeWeaponType = chargeDef.getRangeWeaponType();

					if (rangeWeaponType != null) {
						attacker.getCombatState().setBonus(12,
								(65 + (weaponEquipDef.getBonus(12) + chargeDef.getBonus(12))));
						ammunition = new Item(chargedWith, 1);

						pullback = rangeWeaponType.getPullbackGraphic();
						projectile = rangeWeaponType.getProjectileId();
						dropRate = rangeWeaponType.getDropRate();
					}
				}
				// Heavy ballista exception
			} else if (weapon.getId() == 19481)
				attacker.getCombatState().setBonus(12, (int) (attacker.getCombatState().getBonus(12) * 1.4));
			else {
				if (bowType != null) { // standard bow and arrow
					ArrowType arrowType;
					if (bowType == BowType.CRYSTAL_BOW) {
						arrowType = ArrowType.CRYSTAL_ARROW;
						ammunition = null;
					} else {
						Item arrows = attacker.getEquipment().get(Equipment.SLOT_ARROWS);
						arrowEquipDef = arrows.getEquipmentDefinition();
						arrowType = arrowEquipDef.getArrowType();
						attacker.getCombatState().setBonus(12, arrowEquipDef.getBonus(12));
						ammunition = arrows;
					}
					if (bowType == BowType.DARK_BOW) {
						if (ammunition != null && ammunition.getId() != 11212) {
							pullback = Graphic.create(arrowType.getPullbackGraphic().getId() + 1085,
									arrowType.getPullbackGraphic().getDelay(),
									arrowType.getPullbackGraphic().getHeight());
						} else {
							pullback = arrowType.getPullbackGraphic();
						}
					} else {
						pullback = arrowType.getPullbackGraphic();
					}
					projectile = arrowType.getProjectileId();
					dropRate = arrowType.getDropRate();
				} else { // ranged weapon
					rangeWeaponType = weaponEquipDef.getRangeWeaponType();
					if (rangeWeaponType != null) {
						attacker.getCombatState().setBonus(12, weaponEquipDef.getBonus(12));
						ammunition = weapon;

						pullback = rangeWeaponType.getPullbackGraphic();
						projectile = rangeWeaponType.getProjectileId();
						dropRate = rangeWeaponType.getDropRate();
					}
				}
			}
			attacker.getActionSender().sendBonuses();
		}

		boolean special = attacker.getCombatState().isSpecialOn() ? canSpecial(attacker, victim) : false;

		int clientSpeed;
		int showDelay;
		int slope;
		if (!special || (bowType == BowType.BRONZE_CBOW || bowType == BowType.IRON_CBOW || bowType == BowType.STEEL_CBOW
				|| bowType == BowType.MITH_CBOW || bowType == BowType.ADAMANT_CBOW || bowType == BowType.RUNE_CBOW
				|| bowType == BowType.DRAGON_CBOW || bowType == BowType.ARMADYL_CBOW
				|| bowType == BowType.DORGESHUUN_CBOW)) {

			if (pullback != null)
				attacker.playGraphics(pullback);

			if (rangeWeaponType != null) {
				if (rangeWeaponType == RangeWeaponType.BRONZE_DART || rangeWeaponType == RangeWeaponType.IRON_DART
						|| rangeWeaponType == RangeWeaponType.STEEL_DART
						|| rangeWeaponType == RangeWeaponType.MITHRIL_DART
						|| rangeWeaponType == RangeWeaponType.ADAMANT_DART
						|| rangeWeaponType == RangeWeaponType.RUNE_DART
						|| rangeWeaponType == RangeWeaponType.BLACK_DART) {
					clientSpeed = 35;
					showDelay = 20;
					slope = 13;
				} else {
					clientSpeed = 55;
					showDelay = 45;
					slope = 5;
				}
			} else {
				if (bowType == BowType.KARILS_XBOW || bowType == BowType.BRONZE_CBOW || bowType == BowType.IRON_CBOW
						|| bowType == BowType.STEEL_CBOW || bowType == BowType.MITH_CBOW
						|| bowType == BowType.ADAMANT_CBOW || bowType == BowType.RUNE_CBOW
						|| bowType == BowType.DRAGON_CBOW || bowType == BowType.ARMADYL_CBOW
						|| bowType == BowType.DORGESHUUN_CBOW) {
					clientSpeed = 55;
					showDelay = 45;
					slope = 5;
				} else {
					int distance = attacker.getLocation().distanceToEntity(attacker, victim);
					clientSpeed = 55 + (distance * 5);
					if (distance > 2) {
						hitDelay += 1;
					}
					showDelay = 45;
					slope = 15;
				}
			}
			if (bowType == BowType.DARK_BOW) {
				hitDelay += 1;
				clientSpeed += 15;
				attacker.playProjectile(Projectile.create(attacker.getLocation(), victim, projectile, showDelay, 50,
						clientSpeed - 10, 41, 31, slope - 6, 64));
				attacker.playProjectile(Projectile.create(attacker.getLocation(), victim, projectile, showDelay, 50,
						clientSpeed, 46, 36, slope + 3, 64));
			} else {
				attacker.playProjectile(Projectile.create(attacker.getLocation(), victim, projectile, showDelay, 50,
						clientSpeed, 38, 36, slope, 64));
			}

			if (weapon != null) {
				int attackAnimationIndex = attacker.getCombatState().getCombatStyle().getId();
				if (attackAnimationIndex > 3) {
					attackAnimationIndex = 3;
				}
				attacker.playAnimation(weapon.getEquipmentDefinition().getAnimation().getAttack(attackAnimationIndex));
			} else {
				attacker.playAnimation(attacker.getAttackAnimation());
			}
		} else { // spec attacks
			if (bowType == BowType.DARK_BOW) {
				if (pullback != null) {
					attacker.playGraphics(pullback);
				}
				if (ammunition != null && ammunition.getId() != 11212) {
					if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
						clientSpeed = 55;
					} else if (attacker.getLocation().isWithinDistance(attacker, victim, 3)) {
						clientSpeed = 55;
					} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
						clientSpeed = 65;
						hitDelay += 1;
					} else {
						clientSpeed = 75;
					}
					showDelay = 45;
					slope = 15;
					clientSpeed += 30;
					attacker.playProjectile(Projectile.create(attacker.getLocation(), victim, projectile, showDelay, 50,
							clientSpeed, 41, 31, 3, 36));
					attacker.playProjectile(Projectile.create(attacker.getLocation(), victim, projectile, showDelay, 50,
							clientSpeed + 10, 46, 36, slope + 6, 36));
				}
			}
			if (bowType == BowType.HEAVY_BALLISTA) {
				if (pullback != null) {
					attacker.playGraphics(pullback);
				}
				if (ammunition != null && ammunition.getId() != 19484) {
					if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
						clientSpeed = 55;
					} else if (attacker.getLocation().isWithinDistance(attacker, victim, 3)) {
						clientSpeed = 55;
					} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
						clientSpeed = 65;
						hitDelay += 1;
					} else {
						clientSpeed = 75;
					}
					showDelay = 45;
					slope = 15;
					clientSpeed += 30;
					attacker.playProjectile(Projectile.create(attacker.getLocation(), victim, projectile, showDelay, 50,
							clientSpeed, 41, 31, 3, 36));
					attacker.playProjectile(Projectile.create(attacker.getLocation(), victim, projectile, showDelay, 50,
							clientSpeed + 10, 46, 36, slope + 6, 36));
				}
			}
		}

		if (victim.getCombatState().getPoisonDamage() < 1 && random.nextInt(11) == 3
				&& victim.getCombatState().canBePoisoned()) {
			if (ammunition != null) {
				if (rangeWeaponType != null) {
					if (weaponEquipDef.getPoisonType() != PoisonType.NONE) {
						victim.getCombatState().setPoisonDamage(weaponEquipDef.getPoisonType().getRangeDamage(),
								attacker);
						if (victim.getActionSender() != null) {
							victim.getActionSender().sendMessage("You have been poisoned!");
							player.getActionSender().sendConfig(102, 1);
						}
					}
				} else if (bowType != null) {
					if (arrowEquipDef.getPoisonType() != PoisonType.NONE) {
						victim.getCombatState().setPoisonDamage(arrowEquipDef.getPoisonType().getRangeDamage(),
								attacker);
						if (victim.getActionSender() != null) {
							victim.getActionSender().sendMessage("You have been poisoned!");
							player.getActionSender().sendConfig(102, 1);
						}
					}
				}
			}
		}

		int maxHit = CombatFormula.calculateRangeMaxHit(attacker, victim, special);
		int damage = damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(), Skills.RANGE,
				Prayers.PROTECT_FROM_MISSILES, special, false);
		int randomHit = random.nextInt(damage < 1 ? 1 : damage + 1); // +1 as
																		// its
																		// exclusive
		if (weapon != null && weapon.getId() == 12926 && attacker.isPlayer()) {
			Player player = (Player) attacker;

			if (special) {
				player.playAnimation(Animation.create(5061));
				attacker.playProjectile(
						Projectile.create(attacker.getLocation(), victim, projectile, 20, 50, 35, 38, 36, 13, 64));
				randomHit *= 1.5;

				player.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, (randomHit / 2) + 1);
			}
		} else if (bowType != null) {
			if (bowType == BowType.BRONZE_CBOW || bowType == BowType.IRON_CBOW || bowType == BowType.STEEL_CBOW
					|| bowType == BowType.MITH_CBOW || bowType == BowType.ADAMANT_CBOW || bowType == BowType.RUNE_CBOW
					|| bowType == BowType.DRAGON_CBOW || bowType == BowType.ARMADYL_CBOW) {
				if (random.nextInt(10) == 1) {
					switch (ammunition.getId()) {
					case 9244:
						boolean fire = true;
						Item shield = victim.getEquipment().get(Equipment.SLOT_SHIELD);
						if (shield != null
								&& (shield.getId() == 11283 || shield.getId() == 1540 || shield.getId() == 20714)) {
							fire = false;
						}
						if (fire) {
							if (CombatFormula.fullVoidRange(attacker))
								randomHit = Misc.random(45, 57);
							else if (CombatFormula.fullEliteVoidRange(attacker))
								randomHit = Misc.random(47, 60);
							else
								randomHit = Misc.random(42, 51);

							if (attacker.getCombatState().getPrayer(Prayers.EAGLE_EYE))
								randomHit *= 1.15;

							if (attacker.getCombatState().getPrayer(Prayers.RIGOUR))
								randomHit *= 1.20;

							double protectionPrayer = victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MISSILES)
									? 0.40
									: 1;
							if (protectionPrayer != 1) {
								double protectionHit = randomHit * protectionPrayer; // +1
																						// as
																						// its
																						// exclusive
								randomHit -= protectionHit;
								if (randomHit < 1)
									randomHit = 0;
							}
						}
						break;
					case 9236:
					case 9238:
					case 9239:
					case 9240:
					case 9241:
					case 9243:
					case 9245:
						special = true;
						break;
					case 9242:
						int selfDamage = (int) (attacker.getSkills().getLevel(Skills.HITPOINTS) * 0.1);
						if (selfDamage < attacker.getSkills().getLevel(Skills.HITPOINTS)) {
							randomHit += victim.getSkills().getLevel(Skills.HITPOINTS) * 0.2;
							attacker.inflictDamage(new Hit(selfDamage), victim);
							special = true;
						}
						break;
					}
				}
			} else if (bowType == BowType.DARK_BOW) {
				if (special) {
					if (attacker.getEquipment().get(Equipment.SLOT_ARROWS) != null) {
						int minimum;
						if (attacker.getEquipment().get(Equipment.SLOT_ARROWS).getId() == 11212) {
							minimum = 8;
						} else {
							minimum = 5;
						}
						if (randomHit < minimum) {
							randomHit = minimum;
						}
					}
				}
			}
		}
		if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS))
			randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);

		final int hit = randomHit;
		if (special)
			special(attacker, victim, hit);

		boolean avaEffect = false;

		if (CombatFormula.hasAccumulator(attacker) && ammunition != null) {
			if (ammunition.getId() != 4740 && !ammunition.getDefinition2().getName().endsWith("javelin")) {
				if (attacker.getRandom().nextInt(10) < 8)
					avaEffect = true;
			}
		}
		if (avaEffect && weapon.getId() == 12926)
			attacker.setAttribute("avaEffect", true);

		if (weapon != null && weapon.getId() != 12926 && bowType != BowType.CRYSTAL_BOW && ammunition != null
				&& !avaEffect)
			attacker.getEquipment().remove(new Item(ammunition.getId(), 1));

		if (CombatFormula.fullKaril(attacker)) {
			int karil = random.nextInt(4);
			if (karil == 1) {
				victim.getSkills().decreaseLevelToMinimum(Skills.AGILITY,
						victim.getSkills().getLevelForExperience(Skills.AGILITY) / 4);
				victim.playGraphics(Graphic.create(401, 0, 100));
			}
		}

		final Item finalAmmunition = avaEffect ? null : ammunition;
		final double finalDropRate = dropRate;

		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				/*
				 * Double hitsplat with half damage if the attacker has full karil's and amulet
				 * of the damned
				 */
				if (random.nextFloat() <= 0.25f && CombatFormula.fullKaril(attacker)
						&& CombatFormula.hasAmuletOfTheDamned(attacker)) {
					victim.inflictDamage(new Hit(hit == 0 ? 0 : hit / 2), attacker);
				}
				if (attacker.isPlayer()) {
					Player player = (Player) attacker;
					KrakenService krakenService = Server.getInjector().getInstance(KrakenService.class);
					if (victim instanceof Whirlpool) {
						Whirlpool whirlpool = (Whirlpool) victim;
						krakenService.disturbWhirlpool(player, whirlpool);
					} else if (victim instanceof Kraken) {
						Kraken kraken = (Kraken) victim;
						if (kraken.getDisturbedWhirlpools().size() == 4
								&& kraken.getTransformId() != KrakenServiceImpl.KRAKEN) {
							kraken.transformNPC(KrakenServiceImpl.KRAKEN);
							kraken.playAnimation(Animation.create(3987));
							kraken.transition(new KrakenCombatState<>(kraken));
						}
					}
				}
				victim.inflictDamage(new Hit(hit), attacker);

				smite(attacker, victim, hit);
				recoil(attacker, victim, hit);
				if (finalAmmunition != null) {
					double r = random.nextDouble();
					if (r >= finalDropRate) {
						boolean dropUnder = false;
						if (victim.isNPC()) {
							NPC n = (NPC) victim;
							if (n.getId() == 1101 || n instanceof Zulrah || n instanceof Kraken
									|| n instanceof Whirlpool) {
								dropUnder = true;
							}
						}
						World.getWorld().createGroundItem(
								new GroundItem(attacker.getUndefinedName(), new Item(finalAmmunition.getId(), 1),
										dropUnder ? attacker.getLocation() : victim.getLocation()),
								attacker.isPlayer() ? ((Player) attacker) : null);
					}
				}
				victim.getActiveCombatAction().defend(attacker, victim, true);
				this.stop();
			}
		});
		vengeance(attacker, victim, hit, hitDelay);
		addExperience(attacker, hit);

		if (special || bowType == BowType.DARK_BOW) {
			final int hits[] = new int[weaponEquipDef.getSpecialHits()];
			for (int i = 1; i < hits.length; i++) {
				if (bowType != BowType.CRYSTAL_BOW && ammunition != null) {
					attacker.getEquipment().remove(new Item(ammunition.getId(), 1));
				}
				int dmg = damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(), Skills.RANGE,
						Prayers.PROTECT_FROM_MISSILES, special, false);
				int specialHit = random.nextInt(dmg < 1 ? 1 : dmg + 1);
				if (special && bowType == BowType.DARK_BOW) {
					int minimum = ammunition.getId() == 11212 ? 8 : 5;
					if (specialHit < minimum) {
						specialHit = minimum;
					}
				}
				if (specialHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
					specialHit = victim.getSkills().getLevel(Skills.HITPOINTS);
				}
				hits[i] = specialHit;
			}
			World.getWorld().submit(new Tickable(3 + (bowType == BowType.DARK_BOW ? 1 : 0)) {
				@Override
				public void execute() {
					for (int i = 1; i < hits.length; i++) {
						double r = random.nextDouble();
						if (r >= finalDropRate && finalAmmunition != null) {
							World.getWorld()
									.createGroundItem(
											new GroundItem(attacker.getUndefinedName(),
													new Item(finalAmmunition.getId(), 1), victim.getLocation()),
											attacker.isPlayer() ? ((Player) attacker) : null);
						}
						if (hits[i] > victim.getSkills().getLevel(Skills.HITPOINTS)) {
							hits[i] = victim.getSkills().getLevel(Skills.HITPOINTS);
						}
						victim.inflictDamage(new Hit(hits[i]), attacker);
						smite(attacker, victim, hits[i]);
						addExperience(attacker, hits[i]);
						recoil(attacker, victim, hits[i]);
					}
					this.stop();
				}
			});
		}
	}

	@Override
	public void defend(Mob attacker, Mob victim, boolean blockAnimation) {
		super.defend(attacker, victim, blockAnimation);

	}

	@Override
	public boolean canSpecial(Mob attacker, Mob victim) {
		return super.canSpecial(attacker, victim);
	}

	@Override
	public void special(Mob attacker, Mob victim, int damage) {
		super.special(attacker, victim, damage);
	}

	@Override
	public void special(Mob attacker, final Item item) {
		super.special(attacker, item);
	}

	@Override
	public int distance(Mob attacker) {
		int dist = 6;
		if (attacker.isPlayer()) {
			Item weapon = attacker.getEquipment().get(Equipment.SLOT_WEAPON);
			if (weapon != null) {
				EquipmentDefinition weaponEquipDef = weapon.getEquipmentDefinition();
				if (weaponEquipDef.getBowType() != null) {
					dist = weaponEquipDef.getBowType().getDistance(attacker.getCombatState().getCombatStyle().getId()
							- (attacker.getCombatState().getCombatStyle() == CombatStyle.DEFENSIVE ? 1 : 0));
				} else if (weaponEquipDef.getRangeWeaponType() != null) {
					dist = weaponEquipDef.getRangeWeaponType()
							.getDistance(attacker.getCombatState().getCombatStyle().getId()
									- (attacker.getCombatState().getCombatStyle() == CombatStyle.DEFENSIVE ? 1 : 0));
				}
			}
		} else {
			return 9;
		}
		return dist;
	}

	@Override
	public int damage(int maxHit, Mob attacker, Mob victim, AttackType attackType, int skill, int prayer,
			boolean special, boolean ignorePrayers) {
		return super.damage(maxHit, attacker, victim, attackType, skill, prayer, special, ignorePrayers);
	}

	@Override
	public void addExperience(Mob attacker, int damage) {
		super.addExperience(attacker, damage);
		if (attacker.getCombatState().getCombatStyle() == CombatStyle.DEFENSIVE) {
			// Longrange
			attacker.getSkills().addExperience(Skills.RANGE, (damage * Constants.COMBAT_EXP) / 2);
			attacker.getSkills().addExperience(Skills.DEFENCE, (damage * Constants.COMBAT_EXP) / 2);
		} else
			attacker.getSkills().addExperience(Skills.RANGE, damage * Constants.COMBAT_EXP);
	}

	@Override
	public void recoil(Mob attacker, Mob victim, int damage) {
		super.recoil(attacker, victim, damage);
	}

	@Override
	public void smite(Mob attacker, Mob victim, int damage) {
		super.smite(attacker, victim, damage);
	}

	/**
	 * An enum that represents a type of range bow.
	 *
	 * @author Michael
	 */
	public static enum BowType {
		LONGBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW }, new int[] { 10, 10, 10 }),

		SHORTBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW }, new int[] { 7, 7, 9 }),

		OAK_SHORTBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW }, new int[] { 7, 7, 9 }),

		TWISTED_BOW(new ArrowType[] { ArrowType.RUNE_ARROW, ArrowType.DRAGON_ARROW }, new int[] { 10, 10, 10 }),

		OAK_LONGBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW },
				new int[] { 10, 10, 10 }),

		WILLOW_LONGBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW },
				new int[] { 10, 10, 10 }),

		WILLOW_SHORTBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW },
				new int[] { 7, 7, 9 }),

		MAPLE_LONGBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW,
				ArrowType.MITHRIL_ARROW, ArrowType.ADAMANT_ARROW }, new int[] { 10, 10, 10 }),

		MAPLE_SHORTBOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW,
				ArrowType.MITHRIL_ARROW, ArrowType.ADAMANT_ARROW }, new int[] { 7, 7, 9 }),

		YEW_LONGBOW(
				new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW,
						ArrowType.MITHRIL_ARROW, ArrowType.ADAMANT_ARROW, ArrowType.RUNE_ARROW },
				new int[] { 10, 10, 10 }),

		YEW_SHORTBOW(
				new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW,
						ArrowType.MITHRIL_ARROW, ArrowType.ADAMANT_ARROW, ArrowType.RUNE_ARROW },
				new int[] { 7, 7, 9 }),

		MAGIC_LONGBOW(
				new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW,
						ArrowType.MITHRIL_ARROW, ArrowType.ADAMANT_ARROW, ArrowType.RUNE_ARROW },
				new int[] { 10, 10, 10 }),

		MAGIC_SHORTBOW(
				new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW,
						ArrowType.MITHRIL_ARROW, ArrowType.ADAMANT_ARROW, ArrowType.RUNE_ARROW },
				new int[] { 7, 7, 9 }), THIRD_AGE_BOW(
						new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW,
								ArrowType.MITHRIL_ARROW, ArrowType.ADAMANT_ARROW, ArrowType.RUNE_ARROW },
						new int[] { 7, 7, 9 }),

		CRYSTAL_BOW(new ArrowType[0], new int[] { 10, 10, 10 }),

		KARILS_XBOW(new ArrowType[] { ArrowType.BOLT_RACK }, new int[] { 7, 7, 9 }),

		LIGHT_BALLISTA(new ArrowType[] { ArrowType.BRONZE_JAVELIN, ArrowType.IRON_JAVELIN, ArrowType.STEEL_JAVELIN,
				ArrowType.MITHRIL_JAVELIN, ArrowType.ADAMANT_JAVELIN, ArrowType.RUNE_JAVELIN,
				ArrowType.DRAGON_JAVELIN }, new int[] { 9, 9, 10 }),

		HEAVY_BALLISTA(new ArrowType[] { ArrowType.BRONZE_JAVELIN, ArrowType.IRON_JAVELIN, ArrowType.STEEL_JAVELIN,
				ArrowType.MITHRIL_JAVELIN, ArrowType.ADAMANT_JAVELIN, ArrowType.RUNE_JAVELIN,
				ArrowType.DRAGON_JAVELIN }, new int[] { 9, 9, 10 }),

		DARK_BOW(new ArrowType[] { ArrowType.BRONZE_ARROW, ArrowType.IRON_ARROW, ArrowType.STEEL_ARROW,
				ArrowType.MITHRIL_ARROW, ArrowType.ADAMANT_ARROW, ArrowType.RUNE_ARROW, ArrowType.DRAGON_ARROW },
				new int[] { 10, 10, 10 }),

		BRONZE_CBOW(new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT }, new int[] { 7, 7, 9 }),

		IRON_CBOW(new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT }, new int[] { 7, 7, 9 }),

		STEEL_CBOW(new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT, ArrowType.STEEL_BOLT },
				new int[] { 7, 7, 9 }),

		MITH_CBOW(new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT, ArrowType.STEEL_BOLT,
				ArrowType.MITHRIL_BOLT }, new int[] { 7, 7, 9 }),

		ADAMANT_CBOW(new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT, ArrowType.STEEL_BOLT,
				ArrowType.MITHRIL_BOLT, ArrowType.ADAMANT_BOLT }, new int[] { 7, 7, 9 }),

		RUNE_CBOW(
				new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT, ArrowType.STEEL_BOLT,
						ArrowType.MITHRIL_BOLT, ArrowType.ADAMANT_BOLT, ArrowType.RUNE_BOLT, ArrowType.BROAD_BOLT },
				new int[] { 7, 7, 9 }),

		DRAGON_CBOW(
				new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT, ArrowType.STEEL_BOLT,
						ArrowType.MITHRIL_BOLT, ArrowType.ADAMANT_BOLT, ArrowType.RUNE_BOLT, ArrowType.BROAD_BOLT },
				new int[] { 7, 7, 9 }),

		ARMADYL_CBOW(
				new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT, ArrowType.STEEL_BOLT,
						ArrowType.MITHRIL_BOLT, ArrowType.ADAMANT_BOLT, ArrowType.RUNE_BOLT, ArrowType.BROAD_BOLT },
				new int[] { 7, 7, 9 }),

		DORGESHUUN_CBOW(new ArrowType[] { ArrowType.BRONZE_BOLT, ArrowType.IRON_BOLT, ArrowType.BONE_BOLT },
				new int[] { 6, 6, 8 });

		/**
		 * The arrows this bow can use.
		 */
		private ArrowType[] validArrows;

		/**
		 * The distances required to be near the victim based on the mob's combat style.
		 */
		private int[] distances;

		private BowType(ArrowType[] validArrows, int[] distances) {
			this.validArrows = validArrows;
			this.distances = distances;
		}

		/**
		 * Gets the valid arrows this bow can use.
		 *
		 * @return The valid arrows this bow can use.
		 */
		public ArrowType[] getValidArrows() {
			return validArrows;
		}

		/**
		 * Gets a valid arrow this bow can use by its index.
		 *
		 * @param index
		 *            The arrow index.
		 * @return The valid arrow this bow can use by its index.
		 */
		public ArrowType getValidArrow(int index) {
			return validArrows[index];
		}

		/**
		 * Gets a distance required to be near the victim.
		 *
		 * @param index
		 *            The combat style index.
		 * @return The distance required to be near the victim
		 */
		public int getDistance(int index) {
			return distances[index];
		}
	}

	/**
	 * An enum for all arrow types, this includes the drop rate percentage of this
	 * arrow (the higher quality the less likely it is to disappear).
	 *
	 * @author Michael Bull
	 * @author Sir Sean
	 */
	public static enum ArrowType {

		BRONZE_ARROW(0.75, Graphic.create(19, 0, 100), 10),

		IRON_ARROW(0.7, Graphic.create(18, 0, 100), 9),

		STEEL_ARROW(0.65, Graphic.create(20, 0, 100), 11),

		MITHRIL_ARROW(0.6, Graphic.create(21, 0, 100), 12),

		ADAMANT_ARROW(0.5, Graphic.create(22, 0, 100), 13),

		RUNE_ARROW(0.4, Graphic.create(24, 0, 100), 15),

		BOLT_RACK(1.1, null, 27),

		DRAGON_ARROW(0.3, Graphic.create(1111, 0, 100), 1115),

		BRONZE_BOLT(0.75, null, 27),

		IRON_BOLT(0.7, null, 27),

		STEEL_BOLT(0.65, null, 27),

		MITHRIL_BOLT(0.6, null, 27),

		ADAMANT_BOLT(0.5, null, 27),

		BROAD_BOLT(0.6, null, 27),

		RUNE_BOLT(0.4, null, 27),

		BONE_BOLT(0.65, null, 27),

		DRAGON_JAVELIN(1.1, null, 1301),

		BRONZE_JAVELIN(1.1, null, 1301), // TODO find all javelin correct gfx and projectiles

		IRON_JAVELIN(1.1, null, 1301),

		STEEL_JAVELIN(1.1, null, 1301),

		MITHRIL_JAVELIN(1.1, null, 1301),

		ADAMANT_JAVELIN(1.1, null, 1301),

		RUNE_JAVELIN(1.1, null, 1301),

		CRYSTAL_ARROW(1.1, Graphic.create(249, 0, 100), 250);

		/**
		 * The percentage chance for the arrow to disappear once fired.
		 */
		private double dropRate;

		/**
		 * The pullback graphic.
		 */
		private Graphic pullback;

		/**
		 * The projectile id.
		 */
		private int projectile;

		private ArrowType(double dropRate, Graphic pullback, int projectile) {
			this.dropRate = dropRate;
			this.pullback = pullback;
			this.projectile = projectile;
		}

		/**
		 * Gets the arrow's percentage chance to disappear once fired
		 *
		 * @return The arrow's percentage chance to disappear once fired.
		 */
		public double getDropRate() {
			return dropRate;
		}

		/**
		 * Gets the arrow's pullback graphic.
		 *
		 * @return The arrow's pullback graphic.
		 */
		public Graphic getPullbackGraphic() {
			return pullback;
		}

		/**
		 * Gets the arrow's projectile id.
		 *
		 * @return The arrow's projectile id.
		 */
		public int getProjectileId() {
			return projectile;
		}
	}

	/**
	 * An enum that represents all range weapons, e.g. throwing knives and darts.
	 *
	 * @author Michael
	 */
	public static enum RangeWeaponType {

		BRONZE_KNIFE(0.75, Graphic.create(219, 0, 100), 212, new int[] { 4, 4, 6 }),

		IRON_KNIFE(0.7, Graphic.create(220, 0, 100), 213, new int[] { 4, 4, 6 }),

		STEEL_KNIFE(0.65, Graphic.create(221, 0, 100), 214, new int[] { 4, 4, 6 }),

		MITHRIL_KNIFE(0.6, Graphic.create(223, 0, 100), 216, new int[] { 4, 4, 6 }),

		ADAMANT_KNIFE(0.5, Graphic.create(224, 0, 100), 217, new int[] { 4, 4, 6 }),

		RUNE_KNIFE(0.4, Graphic.create(225, 0, 100), 218, new int[] { 4, 4, 6 }),

		BLACK_KNIFE(0.6, Graphic.create(222, 0, 100), 215, new int[] { 4, 4, 6 }),

		BRONZE_DART(0.75, null, 232, new int[] { 3, 3, 5 }),

		IRON_DART(0.7, null, 233, new int[] { 3, 3, 5 }),

		STEEL_DART(0.65, null, 234, new int[] { 3, 3, 5 }),

		MITHRIL_DART(0.6, null, 235, new int[] { 3, 3, 5 }),

		ADAMANT_DART(0.5, null, 236, new int[] { 3, 3, 5 }),

		RUNE_DART(0.4, null, 237, new int[] { 3, 3, 5 }),

		DRAGON_DART(0.4, null, 1123, new int[] { 3, 3, 5 }),

		BLACK_DART(0.6, null, 232, new int[] { 3, 3, 5 }),

		BRONZE_THROWNAXE(0.75, Graphic.create(42, 0, 100), 36, new int[] { 4, 4, 6 }),

		IRON_THROWNAXE(0.7, Graphic.create(43, 0, 100), 35, new int[] { 4, 4, 6 }),

		STEEL_THROWNAXE(0.65, Graphic.create(44, 0, 100), 37, new int[] { 4, 4, 6 }),

		MITHRIL_THROWNAXE(0.6, Graphic.create(45, 0, 100), 38, new int[] { 4, 4, 6 }),

		ADAMANT_THROWNAXE(0.5, Graphic.create(46, 0, 100), 39, new int[] { 4, 4, 6 }),

		RUNE_THROWNAXE(0.4, Graphic.create(48, 0, 100), 41, new int[] { 4, 4, 6 }),

		DRAGON_THROWNAXE(0.3, Graphic.create(1320, 0, 100), 41, new int[] { 4, 4, 6 }),

		OBSIDIAN_RING(0.45, null, 442, new int[] { 7, 7, 9 }),;

		/**
		 * The percentage chance for the arrow to disappear once fired.
		 */
		private double dropRate;

		/**
		 * The pullback graphic.
		 */
		private Graphic pullback;

		/**
		 * The projectile id.
		 */
		private int projectile;

		/**
		 * The distances required for each attack type.
		 */
		private int[] distances;

		private RangeWeaponType(double dropRate, Graphic pullback, int projectile, int[] distances) {
			this.dropRate = dropRate;
			this.pullback = pullback;
			this.projectile = projectile;
			this.distances = distances;
		}

		/**
		 * @return the dropRate
		 */
		public double getDropRate() {
			return dropRate;
		}

		/**
		 * @return the pullback
		 */
		public Graphic getPullbackGraphic() {
			return pullback;
		}

		/**
		 * @return the projectile
		 */
		public int getProjectileId() {
			return projectile;
		}

		/**
		 * @return the distances
		 */
		public int[] getDistances() {
			return distances;
		}

		/**
		 * @return the distances
		 */
		public int getDistance(int index) {
			return distances[index];
		}
	}
}