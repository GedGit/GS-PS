package org.rs2server.rs2.action.impl;

import com.google.common.collect.Lists;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Consumables.Drink;
import org.rs2server.rs2.model.Consumables.Food;
import org.rs2server.rs2.model.Consumables.PotionType;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.UpdateFlags;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.tickable.impl.StaminaPotionTick;
import org.rs2server.rs2.tickable.impl.StoppingTick;
import org.rs2server.rs2.util.Misc;

import java.util.Collections;
import java.util.List;

public class ConsumeItemAction extends Action {

	/**
	 * The item we are consuming
	 */
	private Item item;

	/**
	 * The item's slot.
	 */
	private int slot;

	public enum Monkey {

		MONKEY(23),

		SMALL_NINJA(1462),

		MEDIUM_NINJA(1463),

		ANCIENT(1466),

		SMALL_ZOMBIE_MONKEY(1467),

		LARGE_ZOMBIE_MONKEY(1468),

		BLUE_MONKEY(1825),

		RED_MONKEY(1826);

		final int id;

		Monkey(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	public ConsumeItemAction(Mob mob, Item item, int slot) {
		super(mob, 0);
		this.item = item;
		this.slot = slot;
	}

	@Override
	public CancelPolicy getCancelPolicy() {
		return CancelPolicy.ALWAYS;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.NEVER;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_ALL;
	}

	@Override
	public void execute() {
		this.stop();
		if (getMob().getCombatState().isDead())
			return;
		final Food food = Food.forId(item.getId());
		final Drink drink = Drink.forId(item.getId());

		boolean inventoryFiringEvents = getMob().getInventory().isFiringEvents();
		getMob().getInventory().setFiringEvents(false);
		try {
			/**
			 * Food
			 */

			if (getMob().isPlayer() && item.getId() == 4012) {

				Player player = (Player) getMob();

				if (player.getMonkeyTime() > 0 || player.getMonkey() != null) {
					player.setPnpc(-1);
					player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
					World.getWorld().submit(new StoppingTick(2) {
						@Override
						public void executeAndStop() {
							handleMonkeyNuts(player, item);
						}
					});
				} else {
					handleMonkeyNuts(player, item);
				}
			} else {
				if (food != null) {
					Food last = getMob().getCombatState().getLastAte();
					if (food == Food.KARAMBWAN && last != Food.KARAMBWAN)
						getMob().getCombatState().setEatDelay(0);
					if (getMob().getCombatState().getEatDelay() == 0) {
						getMob().getCombatState().setCanEat(true);
						getMob().playAnimation(Animation.create(829));
						if (food != Food.PURPLE_SWEETS)
							getMob().getInventory().remove(item, slot);
						int delay = food == Food.KARAMBWAN && last == Food.KARAMBWAN ? 1 : 3;
						getMob().getCombatState().setEatDelay(delay);
						getMob().getCombatState().increaseAttackDelay(2);
						if (food == Food.PURPLE_SWEETS)
							getMob().getActionSender().sendMessage("The sugary goodness heals some energy.");
						else
							getMob().getActionSender()
									.sendMessage("You eat the " + item.getDefinition2().name.toLowerCase() + ".");
						getMob().getCombatState().setLastAte(food);
						int increasedBy = 0;
						int beforeHitpoints = getMob().getSkills().getLevel(Skills.HITPOINTS);

						if (food == Food.ANGLERFISH)
							getMob().getSkills().increaseLevelToSetMaximum(Skills.HITPOINTS, food.getHeal(),
									getMob().getSkills().getLevelForExperience(Skills.HITPOINTS)
											+ Constants.getModification(
													getMob().getSkills().getLevelForExperience(Skills.HITPOINTS)));
						else
							getMob().getSkills().increaseLevelToMaximum(Skills.HITPOINTS, food.getHeal());

						increasedBy = getMob().getSkills().getLevel(Skills.HITPOINTS) - beforeHitpoints;
						if (increasedBy > 0 && food != Food.PURPLE_SWEETS)
							getMob().getActionSender().sendMessage("It heals some health.");

						if (food == Food.PURPLE_SWEETS) {
							int energy = getMob().getWalkingQueue().getEnergy();
							int add = Misc.random(2, 5);
							if (energy + add > 100)
								energy = 100;
							else
								energy = energy + add;
							getMob().getWalkingQueue().setEnergy(energy);
							getMob().getActionSender().sendRunEnergy();
							getMob().getInventory().remove(new Item(food.getId(), 1));
						}

						/**
						 * If the item has a new id, add it (e.g. cakes decreasing in amount).
						 */
						if (food.getNewId() != -1)
							getMob().getInventory().add(new Item(food.getNewId(), 1), slot);
						getMob().resetInteractingEntity();
					}
				} else if (drink != null && getMob().getCombatState().canDrink()) {
					/**
					 * Drink
					 */
					getMob().playAnimation(Animation.create(829));
					getMob().getInventory().remove(item, slot);

					/**
					 * Stops the mob from drinking for 3 cycles (1.8 secs).
					 */
					getMob().getCombatState().setCanDrink(false);
					World.getWorld().submit(new Tickable(2) {
						public void execute() {
							getMob().getCombatState().setCanDrink(true);
							this.stop();
						}
					});
					if (item.getDefinition2() == null)
						return;

					/**
					 * Potion Types.
					 */
					String potionName = item.getDefinition2().name.toLowerCase()
							.substring(0, item.getDefinition2().name.length() - 3).replaceAll(" potion", "");
					switch (drink.getPotionType()) {
					case DEFAULT:
						getMob().getActionSender().sendMessage("You drink the " + potionName + ".");
						break;
					case NORMAL_POTION:
						getMob().getActionSender().sendMessage("You drink some of your " + potionName + " potion.");
						for (int i = 0; i < drink.getSkills().length; i++) {
							int skill = drink.getSkill(i);
							int modification = (int) Math.floor((drink == Drink.RANGE_POTION ? 4 : 3)
									+ (getMob().getSkills().getLevelForExperience(skill) * 0.1));
							getMob().getSkills().increaseLevelToMaximumModification(skill, modification);
						}
						break;
					case SUPER_POTION:
						getMob().getActionSender().sendMessage("You drink some of your " + potionName + " potion.");
						for (int i = 0; i < drink.getSkills().length; i++) {
							int skill = drink.getSkill(i);
							int modification = (int) Math
									.floor(5 + (getMob().getSkills().getLevelForExperience(skill) * 0.15));
							getMob().getSkills().increaseLevelToMaximumModification(skill, modification);
						}
						break;
					case PRAYER_POTION:
						getMob().getActionSender().sendMessage("You drink some of your prayer potion.");
						for (int i = 0; i < drink.getSkills().length; i++) {
							int skill = drink.getSkill(i);
							int modification = (int) Math
									.floor(7 + (getMob().getSkills().getLevelForExperience(skill) * 0.25));

							if (getMob().isPlayer()) {
								Player player = (Player) getMob();
								// Holy wrench increases by 2%
								if (player.getInventory().containsOneItem(6714) || Constants.hasMaxCape(player)
										|| player.getEquipment().containsOneItem(9759, 9760))
									modification *= 1.02;
							}
							/**
							 * Holy wrench increases prayer restoration.
							 */
							if (skill == Skills.PRAYER) {
								if (getMob().getInventory().contains(6714) || Constants.hasMaxCape(getMob())
										|| getMob().getEquipment().containsOneItem(9759, 9760)) {
									modification++;
									if (getMob().getSkills().getLevelForExperience(Skills.PRAYER) >= 40) {
										modification++;
									}
									if (getMob().getSkills().getLevelForExperience(Skills.PRAYER) >= 70) {
										modification++;
									}
								}
								getMob().getSkills().increasePrayerPoints(modification);
							} else {
								getMob().getSkills().increaseLevelToMaximum(skill, modification);
							}
						}
						break;
					case RESTORE:
					case SUPER_RESTORE:
						getMob().getActionSender().sendMessage("You drink some of your " + potionName + " potion.");
						for (int i = 0; i < drink.getSkills().length; i++) {
							int skill = drink.getSkill(i);
							int modification = (int) (getMob().getSkills().getLevelForExperience(skill) / 3);

							if (getMob().isPlayer() && skill == Skills.PRAYER) {
								Player player = (Player) getMob();
								// Holy wrench increases by 2%
								if (player.getInventory().containsOneItem(6714) || Constants.hasMaxCape(getMob())
										|| getMob().getEquipment().containsOneItem(9759, 9760))
									modification *= 1.02;
							}
							/**
							 * Holy wrench increases prayer restoration.
							 */
							if (skill == Skills.PRAYER) {
								if (getMob().getInventory().contains(6714) || Constants.hasMaxCape(getMob())
										|| getMob().getEquipment().containsOneItem(9759, 9760)) {
									modification++;
									if (getMob().getSkills().getLevelForExperience(Skills.PRAYER) >= 40)
										modification++;
									if (getMob().getSkills().getLevelForExperience(Skills.PRAYER) >= 70)
										modification++;
								}
								getMob().getSkills().increasePrayerPoints(modification);
							} else
								getMob().getSkills().increaseLevelToMaximum(skill, modification);
						}
						break;
					case SANFEW_SERUM:
						getMob().getActionSender().sendMessage("You drink some of your " + potionName + " potion.");
						for (int i = 0; i < drink.getSkills().length; i++) {
							int skill = drink.getSkill(i);
							int modification = (int) (getMob().getSkills().getLevelForExperience(skill) / 3);

							if (getMob().isPlayer() && skill == Skills.PRAYER) {
								Player player = (Player) getMob();
								// Holy wrench increases by 2%
								if (player.getInventory().containsOneItem(6714) || Constants.hasMaxCape(getMob())
										|| getMob().getEquipment().containsOneItem(9759, 9760))
									modification *= 1.02;
							}

							if (skill == Skills.PRAYER) {
								modification++;
								if (getMob().getSkills().getLevelForExperience(Skills.PRAYER) >= 40)
									modification++;
								if (getMob().getSkills().getLevelForExperience(Skills.PRAYER) >= 70)
									modification++;
								getMob().getSkills().increasePrayerPoints(modification);
							} else
								getMob().getSkills().increaseLevelToMaximum(skill, modification);
						}
						if (getMob().getCombatState().canBePoisoned()) {
							getMob().getCombatState().setCanBePoisoned(false);
							World.getWorld()
									.submit(new Tickable(drink.getPotionType() == PotionType.ANTIPOISON ? 150 : 1000) {
										public void execute() {
											getMob().getCombatState().setCanBePoisoned(true);
											this.stop();
										}
									});
						}
						if (getMob().getCombatState().getPoisonDamage() > 0)
							getMob().getCombatState().setPoisonDamage(0, null);
						break;
					case PLUS_5:
					case PLUS_10:
						getMob().getActionSender().sendMessage("You drink some of your " + potionName + " potion.");
						for (int i = 0; i < drink.getSkills().length; i++) {
							int skill = drink.getSkill(i);
							int modification = PotionType.PLUS_10 != null ? 10 : 5;
							getMob().getSkills().increaseLevelToMaximumModification(skill, modification);
						}
						break;
					case SARADOMIN_BREW:
						getMob().getActionSender().sendMessage("You drink some of the foul liquid.");
						for (int i = 0; i < drink.getSkills().length; i++) {
							int skill = drink.getSkill(i);
							if (skill == Skills.HITPOINTS) {
								int hitpointsModification = (int) (getMob().getSkills()
										.getLevelForExperience(Skills.HITPOINTS) * 0.15);
								getMob().getSkills().increaseLevelToMaximumModification(skill, hitpointsModification);
							} else if (skill == Skills.DEFENCE) {
								int defenceModification = (int) (getMob().getSkills()
										.getLevelForExperience(Skills.DEFENCE) * 0.25);
								getMob().getSkills().increaseLevelToMaximumModification(skill, defenceModification);
							} else {
								int modification = (int) (getMob().getSkills().getLevel(skill) * 0.10);
								getMob().getSkills().decreaseLevelToOne(skill, modification);
							}
						}
						break;
					case ZAMORAK_BREW:
						getMob().getActionSender().sendMessage("You drink some of the foul liquid.");
						for (int i = 0; i < drink.getSkills().length; i++) {
							int skill = drink.getSkill(i);
							if (skill == Skills.ATTACK) {
								int attackModification = (int) Math
										.floor(2 + (getMob().getSkills().getLevelForExperience(Skills.ATTACK)) * 0.20);
								getMob().getSkills().increaseLevelToMaximumModification(skill, attackModification);
							} else if (skill == Skills.STRENGTH) {
								int strengthModification = (int) Math.floor(
										2 + (getMob().getSkills().getLevelForExperience(Skills.STRENGTH) * 0.12));
								getMob().getSkills().increaseLevelToMaximumModification(skill, strengthModification);
							} else if (skill == Skills.PRAYER) {
								int prayerModification = (int) Math
										.floor(getMob().getSkills().getLevelForExperience(Skills.STRENGTH) * 0.10);
								getMob().getSkills().increaseLevelToMaximum(skill, prayerModification);
							} else if (skill == Skills.DEFENCE) {
								int defenceModification = (int) Math
										.floor(2 + (getMob().getSkills().getLevelForExperience(Skills.DEFENCE) * 0.10));
								getMob().getSkills().decreaseLevelToZero(skill, defenceModification);
							} else if (skill == Skills.HITPOINTS) {
								World.getWorld().submit(new Tickable(3) {
									@Override
									public void execute() {
										int hitpointsModification = (int) Math
												.floor(2 + (getMob().getSkills().getLevel(Skills.HITPOINTS) * 0.10));
										if (getMob().getSkills().getLevel(Skills.HITPOINTS)
												- hitpointsModification < 0) {
											hitpointsModification = getMob().getSkills().getLevel(Skills.HITPOINTS);
										}
										getMob().inflictDamage(new Hit(hitpointsModification), null);
										this.stop();
									}
								});
							}
						}
						break;
					case ANTIPOISON:
					case SUPER_ANTIPOISON:
					case ANTIDOTE_PLUS:
					case ANTIDOTE_PLUS_PLUS:
						getMob().getActionSender().sendMessage("You drink some of your " + item.getDefinition2().name
								.toLowerCase().substring(0, item.getDefinition2().getName().length() - 3) + ".");
						if (getMob().getCombatState().canBePoisoned()) {
							getMob().getCombatState().setCanBePoisoned(false);
							int ticks = drink.getPotionType() == PotionType.ANTIPOISON ? 150
									: drink.getPotionType() == PotionType.ANTIDOTE_PLUS ? 1500
											: drink.getPotionType() == PotionType.ANTIDOTE_PLUS_PLUS ? 2000 : 1000;
							World.getWorld().submit(new Tickable(ticks) {
								public void execute() {
									getMob().getCombatState().setCanBePoisoned(true);
									this.stop();
								}
							});
						}
						if (getMob().getCombatState().getPoisonDamage() > 0)
							getMob().getCombatState().setPoisonDamage(0, null);
						break;
					case BEER:
						getMob().getActionSender()
								.sendMessage("You drink the beer. You feel slightly reinvigorated...");
						getMob().getActionSender().sendMessage("...and slightly dizzy too.");
						for (int i = 0; i < drink.getSkills().length; i++) {
							int skill = drink.getSkill(i);
							if (skill == Skills.ATTACK) {
								int attackModification = (int) (getMob().getSkills()
										.getLevelForExperience(Skills.STRENGTH) * 0.07);
								getMob().getSkills().decreaseLevelToZero(Skills.ATTACK, attackModification);
							} else if (skill == Skills.STRENGTH) {
								int strengthModification = (int) (getMob().getSkills()
										.getLevelForExperience(Skills.STRENGTH) * 0.04);
								getMob().getSkills().increaseLevelToMaximumModification(Skills.STRENGTH,
										strengthModification);
							}
						}
						break;
					case WINE:
						getMob().getActionSender()
								.sendMessage("You drink the wine. You feel slightly reinvigorated...");
						getMob().getActionSender().sendMessage("...and slightly dizzy too.");
						for (int i = 0; i < drink.getSkills().length; i++) {
							int skill = drink.getSkill(i);
							if (skill == Skills.ATTACK) {
								int attackModification = 2;
								getMob().getSkills().decreaseLevelToZero(Skills.ATTACK, attackModification);
							} else if (skill == Skills.HITPOINTS) {
								getMob().getSkills().increaseLevelToMaximum(Skills.HITPOINTS, 11);
							}
						}
						break;
					case ANTIFIRE:
						getMob().getActionSender().sendMessage("You drink some of your " + item.getDefinition2().name
								.toLowerCase().substring(0, item.getDefinition2().getName().length() - 3) + ".");
						getMob().setAttribute("antiFire", System.currentTimeMillis());
						break;
					case EXTENDED_ANTIFIRE:
						getMob().getActionSender().sendMessage("You drink some of your " + item.getDefinition2().name
								.toLowerCase().substring(0, item.getDefinition2().getName().length() - 3) + ".");
						getMob().setAttribute("extended_antiFire", System.currentTimeMillis());
						break;
					case ANTI_VENOM:
						getMob().getActionSender().sendMessage("You drink some of your " + item.getDefinition2().name
								.toLowerCase().substring(0, item.getDefinition2().getName().length() - 3) + ".");
						if (getMob().getCombatState().canBePoisoned()) {
							getMob().getCombatState().setCanBePoisoned(false);
							World.getWorld().submit(new Tickable(150) {
								public void execute() {
									getMob().getCombatState().setCanBePoisoned(true);
									this.stop();
								}
							});
						}
						if (getMob().getCombatState().getPoisonDamage() > 0) {
							getMob().getCombatState().setPoisonDamage(0, null);
						}
						if (getMob().hasAttribute("venom")) {
							getMob().removeAttribute("venom");
						}
						break;
					case ANTI_VENOM_PLUS:
						getMob().getActionSender().sendMessage("You drink some of your " + item.getDefinition2().name
								.toLowerCase().substring(0, item.getDefinition2().getName().length() - 3) + ".");
						if (getMob().getCombatState().canBePoisoned()) {
							getMob().getCombatState().setCanBePoisoned(false);
							World.getWorld().submit(new Tickable(150) {
								public void execute() {
									getMob().getCombatState().setCanBePoisoned(true);
									this.stop();
								}
							});
						}
						if (getMob().getCombatState().getPoisonDamage() > 0) {
							getMob().getCombatState().setPoisonDamage(0, null);
						}
						getMob().setAttribute("antiVenom+", System.currentTimeMillis());
						break;
					case ENERGY:
						getMob().getActionSender().sendMessage("You drink some of your " + item.getDefinition2().name
								.toLowerCase().substring(0, item.getDefinition2().getName().length() - 3) + ".");
						int energy = 10 + getMob().getWalkingQueue().getEnergy();
						if (energy > 100) {
							energy = 100;
						}
						getMob().getWalkingQueue().setEnergy(energy);
						getMob().getActionSender().sendEnergy();
						break;
					case SUPER_ENERGY:
						getMob().getActionSender().sendMessage("You drink some of your " + item.getDefinition2().name
								.toLowerCase().substring(0, item.getDefinition2().getName().length() - 3) + ".");
						energy = 20 + getMob().getWalkingQueue().getEnergy();
						if (energy > 100) {
							energy = 100;
						}
						getMob().getWalkingQueue().setEnergy(energy);
						getMob().getActionSender().sendEnergy();
						break;
					case STAMINA_POTION:
						getMob().getActionSender().sendMessage("You drink some of your " + item.getDefinition2().name
								.toLowerCase().substring(0, item.getDefinition2().getName().length() - 3) + ".");
						energy = 20 + getMob().getWalkingQueue().getEnergy();
						if (energy > 100) {
							energy = 100;
						}
						getMob().getWalkingQueue().setEnergy(energy);
						getMob().getActionSender().sendEnergy();
						getMob().setAttribute("staminaPotion", true);
						World.getWorld().submit(new StaminaPotionTick(getMob()));
						break;
					}
					int currentPotionDose = 0;
					for (int i = 0; i < drink.getIds().length; i++) {
						if (item.getId() == drink.getId(i)) {
							currentPotionDose = i + 1;
							break;
						}
					}
					if (drink.getPotionType() != PotionType.BEER && drink.getPotionType() != PotionType.WINE
							&& drink.getPotionType() != PotionType.DEFAULT) {
						getMob().getActionSender()
								.sendMessage(currentPotionDose > 1
										? ("You have " + (currentPotionDose - 1) + " dose"
												+ (currentPotionDose > 2 ? "s" : "") + " of potion left.")
										: "You have finished your potion.");
					}
					int newPotion = 229;
					if (currentPotionDose > 1) {
						newPotion = drink.getId(currentPotionDose - 2);
					}
					getMob().getInventory().add(new Item(newPotion), slot);
					getMob().resetInteractingEntity();
				}
			}
			getMob().getInventory().fireItemsChanged();
		} finally {
			getMob().getInventory().setFiringEvents(inventoryFiringEvents);
		}
	}

	private void handleMonkeyNuts(Player mob, Item item) {
		List<Monkey> monkeyList = Lists.newArrayList(Monkey.values());

		Collections.shuffle(monkeyList);

		Player player = (Player) getMob();
		Monkey monkey = monkeyList.get(0);

		player.playAnimation(Animation.create(829));
		player.getInventory().remove(item, slot);
		player.setAttribute("busy", true);
		World.getWorld().submit(new StoppingTick(4) {
			@Override
			public void executeAndStop() {
				player.setPnpc(monkeyList.get(0).getId());
				player.setMonkey(monkey);
				player.setMonkeyTime(30);
				player.forceChat("Goin bananas!!!!");
				player.removeAttribute("busy");
				player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
			}
		});
	}

}
