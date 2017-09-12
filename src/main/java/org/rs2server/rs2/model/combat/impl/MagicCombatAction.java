package org.rs2server.rs2.model.combat.impl;

import org.rs2server.Server;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.ScriptManager;
import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.content.Teleporting;
import org.rs2server.rs2.domain.service.api.content.*;
import org.rs2server.rs2.domain.service.impl.content.KrakenServiceImpl;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.combat.*;
import org.rs2server.rs2.model.combat.CombatState.*;
import org.rs2server.rs2.model.container.*;
import org.rs2server.rs2.model.equipment.PoisonType;
import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.npc.impl.kraken.*;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.tickable.impl.SkillsUpdateTick;
import org.rs2server.rs2.util.*;

import java.util.*;

/**
 * Normal magic combat action.
 *
 * @author Graham Edgecombe
 */
public class MagicCombatAction extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final MagicCombatAction INSTANCE = new MagicCombatAction();

	/**
	 * Gets the singleton instance.
	 *
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	private boolean fire = false;
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	@SuppressWarnings("unused")
	private final StaffService staffService;

	/**
	 * Default private constructor.
	 */
	public MagicCombatAction() {
		this.staffService = Server.getInjector().getInstance(StaffService.class);
	}

	@Override
	public boolean canHit(Mob attacker, Mob victim, boolean messages, boolean cannon) {
		if (!super.canHit(attacker, victim, messages, cannon))
			return false;
		if (cannon)
			return true;
		if (attacker.getAutocastSpell() != null)
			attacker.getCombatState().setQueuedSpell(attacker.getAutocastSpell());
		return canCastSpell(attacker, victim, attacker.getCombatState().getQueuedSpell());
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		attacker.getCombatState().setCurrentSpell(attacker.getCombatState().getQueuedSpell());
		attacker.getCombatState().setQueuedSpell(null);
		if (attacker.getAttribute("castSpell") != null) {
			attacker.getCombatState().setCurrentSpell((Spell) attacker.getAttribute("castSpell"));
			attacker.removeAttribute("castSpell");
			attacker.removeAttribute("magicMove");
			attacker.setInteractingEntity(InteractionMode.TALK, victim);
		}
		Spell spell = attacker.getCombatState().getCurrentSpell();
		int clientSpeed;
		int gfxDelay;
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
		int delay = (gfxDelay / 20) - 1;

		executeSpell(spell, attacker, attacker, victim, spell, attacker.getAutocastSpell() != null, clientSpeed,
				INSTANCE, gfxDelay, delay);

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
	public void special(Mob attacker, Mob victim, int damage, boolean boltSpecial) {
		super.special(attacker, victim, damage, false);
	}

	@Override
	public void special(Mob attacker, final Item item) {
		super.special(attacker, item);
	}

	@Override
	public int distance(Mob attacker) {
		return 10;
	}

	@Override
	public int damage(int maxHit, Mob attacker, Mob victim, AttackType attackType, int skill, int prayer,
			boolean special, boolean ignorePrayers) {

		double EA = attacker.getSkills().getLevel(Skills.MAGIC)
				+ attacker.getSkills().getLevelForExperience(Skills.MAGIC);
		if (attacker.getCombatState().getPrayer(Prayers.MYSTIC_WILL)) {
			EA *= 1.05;
		} else if (attacker.getCombatState().getPrayer(Prayers.MYSTIC_LORE)) {
			EA *= 1.10;
		} else if (attacker.getCombatState().getPrayer(Prayers.MYSTIC_MIGHT)) {
			EA *= 1.15;
		} else if (attacker.getCombatState().getPrayer(Prayers.AUGURY)) {
			EA *= 1.25;
		}
		EA = Math.round(EA) + 14;

		if (CombatFormula.fullVoidMage(attacker))
			EA *= 1.45;
		if (CombatFormula.fullEliteVoidMage(attacker))
			maxHit *= 1.025;

		if (attacker.isPlayer() && victim.isNPC()) {
			final Player player = (Player) attacker;
			final NPC npc = (NPC) victim;
			if (player.getSlayer().getSlayerTask() != null && CombatFormula.hasImbuedSlayerHelm(player)) {
				for (String taskName : player.getSlayer().getSlayerTask().getName()) {
					if (npc.getDefinition().getName().contains(taskName)) {
						EA *= 1.15;
						maxHit *= 1.15;
					}
				}
			}
		}
		if (random.nextFloat() <= 0.25f && CombatFormula.fullAhrim(attacker)
				&& CombatFormula.hasAmuletOfTheDamned(attacker)) {
			EA *= 1.3;
		}

		double ED = victim.getSkills().getLevel(Skills.MAGIC) + victim.getSkills().getLevelForExperience(Skills.MAGIC);
		if (victim.getCombatState().getPrayer(Prayers.THICK_SKIN)) {
			ED *= 1.05;
		} else if (victim.getCombatState().getPrayer(Prayers.ROCK_SKIN)) {
			ED *= 1.10;
		} else if (victim.getCombatState().getPrayer(Prayers.STEEL_SKIN)) {
			ED *= 1.15;
		} else if (victim.getCombatState().getPrayer(Prayers.CHIVALRY)) {
			ED *= 1.20;
		} else if (victim.getCombatState().getPrayer(Prayers.PIETY)) {
			ED *= 1.25;
		} else if (victim.getCombatState().getPrayer(Prayers.AUGURY)) {
			ED *= 1.25;
		}
		ED = Math.round(ED) + 8;
		if (victim.getCombatState().getCombatStyle() == CombatStyle.DEFENSIVE) {
			ED += 3;
		} else if (victim.getCombatState().getCombatStyle() == CombatStyle.CONTROLLED_1
				|| victim.getCombatState().getCombatStyle() == CombatStyle.CONTROLLED_2
				|| victim.getCombatState().getCombatStyle() == CombatStyle.CONTROLLED_3) {
			ED += 1;
		}
		ED *= 0.7;

		double EM = victim.getSkills().getLevel(Skills.DEFENCE)
				+ victim.getSkills().getLevelForExperience(Skills.DEFENCE);
		EM *= 0.1;

		ED += EM;

		int bonus = attacker.getCombatState().getBonus(3);

		int bonus2 = victim.getCombatState().getBonus(8);

		double A = (EA * (1 + bonus)) / 64;
		double D = (ED * (1 + bonus2)) / 64;
		double accuracy = 0.05;
		if (A < D)
			accuracy = (A - 1) / (2 * D);
		else if (A > D)
			accuracy = 1 - (D + 1) / (2 * A);
		double rand = Math.random();

		/**
		 * Special item modifiers
		 */
		Item weapon = attacker.getEquipment().get(Equipment.SLOT_WEAPON);
		Item amulet = attacker.getEquipment().get(Equipment.SLOT_AMULET);
		Item shield = attacker.getEquipment().get(Equipment.SLOT_SHIELD);
		if (weapon != null && (weapon.getId() == 11791 || weapon.getId() == 12904))
			maxHit = (int) (maxHit * 1.15);

		if (amulet != null && amulet.getId() == 12002)
			maxHit = (int) (maxHit * 1.10);

		if (fire == true) {
			if (shield != null && shield.getId() == 20714) {
				if (attacker.isPlayer()) {
					Player player = (Player) attacker;
					int charges = player.getItemService().getCharges(player, new Item(20714));
					if (charges > 0) {
						maxHit = (int) (maxHit * 1.50);
						player.getItemService().setCharges(player, new Item(20714), charges - 1);
						charges = player.getItemService().getCharges(player, new Item(20714));
						if (charges == 0)
							player.sendMessage("Your tome has ran out of charges.");
					}
				}
			}
			// make fire = false on every cast
			fire = false;
		}
		/**
		 * The chance to succeed out of 1.0.
		 */
		double hitSucceed = accuracy > rand ? 1 : 0;
		if (hitSucceed < random.nextDouble()) {
			return -1; // NOTE: For magic, we return -1. This is because if a
			// spell has a max hit of 0, its damage will always
			// return 0, saying that it has failed, when it hasn't!
		} else {
			/**
			 * Protection prayers. Note: If an NPC is hitting on a protection prayer, it is
			 * 100% blocked, where as if a player is hitting on a protection prayer, their
			 * damage is simply reduced by 40%.
			 */
			int hit = maxHit; // +1 as its exclusive
			if (victim.getCombatState().getPrayer(prayer) && !ignorePrayers) {
				if (attacker.getProtectionPrayerModifier() == 0) {
					hit = -1;
				} else {
					hit = (int) (hit * attacker.getProtectionPrayerModifier());
				}
			}
			if (hit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				hit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			return hit;
		}
	}

	@Override
	public void addExperience(Mob attacker, int damage) {
		super.addExperience(attacker, damage);
		double exp = damage * Constants.COMBAT_EXP;
		if (attacker.getCombatState().getCombatStyle() == CombatStyle.DEFENSIVE_AUTOCAST) {
			attacker.getSkills().addExperience(Skills.MAGIC, exp / 2);
			attacker.getSkills().addExperience(Skills.DEFENCE, exp / 2);
		} else
			attacker.getSkills().addExperience(Skills.MAGIC, exp);
	}

	@Override
	public void recoil(Mob attacker, Mob victim, int damage) {
		super.recoil(attacker, victim, damage);
	}

	@Override
	public void smite(Mob attacker, Mob victim, int damage) {
		super.smite(attacker, victim, damage);
	}

	@SuppressWarnings("unused")
	public static void executeSpell(Spell spell, final Mob mob, Object... args) {
		if (spell != null && spell.getSpellName().contains("Teleport")) {
			if (!mob.canTeleport())
				return;
		}
		if (!canCastSpell(mob, null, spell))
			return;
		if (!checkRunes(mob, spell))
			return;
		if (mob.isNPC()) {
			NPC npc = (NPC) mob;
			Mob victim = (Mob) args[1];
			npc.getCombatState().setAttackDelay(npc.getCombatDefinition().getCombatCooldownDelay());
			npc.getCombatState().setSpellDelay(npc.getCombatDefinition().getCombatCooldownDelay());

			if (npc.getCombatDefinition().getSpell() == null
					|| npc.getCombatDefinition().getSpell() == Spell.CARRALLANGAR_TELEPORT) {
				npc.playAnimation(npc.getCombatDefinition().getAttack());
				if (npc.getDrawbackGraphic() != null)
					npc.playGraphics(npc.getDrawbackGraphic());
			}

			npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim.getCentreLocation(),
					npc.getCombatDefinition().getProjectileId(), 60, 50, (Integer) args[4], 43, 35,
					victim.getProjectileLockonIndex(), 15, 48));

			((MagicCombatAction) args[5]).hitEnemy(mob, victim, npc.getCombatDefinition().getSpell(), null,
					PoisonType.NONE, false, npc.getCombatDefinition().getMaxHit(), (Integer) args[7], 0);
		} else if (spell == Spell.HOME_TELEPORT_MODERN || spell == Spell.HOME_TELEPORT_LUNAR
				|| spell == Spell.HOME_TELEPORT_ANCIENT || spell == Spell.HOME_TELEPORT_ARCEUUS) {
			if (((Player) mob).getBountyHunter() != null) {
				mob.getActionSender().sendMessage("You can't teleport in here.");
				return;
			}
			mob.getWalkingQueue().reset();
			mob.getActionQueue().addAction(new Action(mob, 2) {
				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}

				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.ALWAYS;
				}

				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}

				@Override
				public void execute() {
					mob.playAnimation(Animation.create(4847));
					mob.playGraphics(Graphic.create(800));
					this.stop();
				}
			});
			mob.getActionQueue().addAction(new Action(mob, 7) {
				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}

				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.ALWAYS;
				}

				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}

				@Override
				public void execute() {
					mob.playAnimation(Animation.create(4850));
					mob.playGraphics(Graphic.create(801));
					this.stop();
				}
			});
			mob.getActionQueue().addAction(new Action(mob, 11) {
				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}

				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.ALWAYS;
				}

				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}

				@Override
				public void execute() {
					mob.playAnimation(Animation.create(4853));
					mob.playGraphics(Graphic.create(802));
					this.stop();
				}
			});
			mob.getActionQueue().addAction(new Action(mob, 13) {
				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}

				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.ALWAYS;
				}

				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}

				@Override
				public void execute() {
					mob.playAnimation(Animation.create(4855));
					mob.playGraphics(Graphic.create(803));
					this.stop();
				}
			});
			mob.getActionQueue().addAction(new Action(mob, 17) {
				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}

				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.ALWAYS;
				}

				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}

				@Override
				public void execute() {
					mob.playAnimation(Animation.create(4857));
					mob.playGraphics(Graphic.create(804));
					this.stop();
				}
			});
			mob.getActionQueue().addAction(new Action(mob, 19) {
				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}

				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.ALWAYS;
				}

				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}

				@Override
				public void execute() {
					mob.setTeleportTarget(Entity.DEFAULT_LOCATION);
					this.stop();
				}
			});
		} else if (!Teleporting.teleport(mob, spell)
				&& ScriptManager.getScriptManager().invokeWithFailTest(spell.getSpellName(), args)) {
			int random = Misc.random(7);
			for (int i = 0; i < spell.getRunes().length; i++) {
				if (mob.getInventory() != null && spell.getRune(i) != null) {
					if (mob.getInventory() != null && Inventory.hasItem(mob, spell.getRune(i)))
						Inventory.removeRune(mob, spell.getRune(i));
				}
			}
			if (mob.getCombatState().spellbookSwap() && spell != Spell.SPELLBOOK_SWAP) {
				mob.getActionSender().sendSidebarInterface(105,
						MagicCombatAction.SpellBook.LUNAR_MAGICS.getInterfaceId());
				mob.getCombatState().setSpellBook(MagicCombatAction.SpellBook.LUNAR_MAGICS.getSpellBookId());
				mob.getCombatState().setSpellbookSwap(false);
			}
		} else if (spell == Spell.BOUNTY_LOCATE || spell == Spell.BOUNTY_LOCATE_ANCIENTS
				|| spell == Spell.BOUNTY_LOCATE_LUNAR) {
			Player player = (Player) mob;
			if (player.hasAttribute("teleporting") || player.getBountyTarget() == null || !player.isInWilderness()
					|| !player.getBountyTarget().isInWilderness()
					|| player.getDatabaseEntity().getPlayerSettings().isTeleBlocked()) {
				return;
			}
			if (mob.hasAttribute("lastBountyTele")
					&& System.currentTimeMillis() - (long) mob.getAttribute("lastBountyTele") < 180000) {
				player.getActionSender().sendMessage("You can only cast this teleport once every 60 seconds.");
				return;
			}
			if (player.getCombatState().getLastHitTimer() > System.currentTimeMillis()) {
				player.getActionSender().sendMessage("You can't cast this until 10 seconds after the end of combat.");
				return;
			}
			final List<Location> locations = new ArrayList<>();
			Player victim = player.getBountyTarget();
			for (int x = victim.getLocation().getX() - 7; x < victim.getLocation().getX() + 7; x++) {
				for (int y = victim.getLocation().getY() - 7; y < victim.getLocation().getY() + 7; y++) {
					Location loc = Location.create(x, y, victim.getLocation().getPlane());
					if (Location.inWild(loc)
							&& RegionClipping.getClippingMask(loc.getX(), loc.getY(), loc.getPlane()) < 1) {
						locations.add(loc);
					}
				}
			}
			if (locations.isEmpty()) {
				return;
			}
			player.setAttribute("teleporting", true);
			for (int i = 0; i < spell.getRunes().length; i++) {
				if (mob.getInventory() != null && spell.getRune(i) != null) {
					if (mob.getInventory() != null && Inventory.hasItem(mob, spell.getRune(i))) {
						Inventory.removeRune(mob, spell.getRune(i));
					}
				}
			}
			mob.playAnimation(Animation.create(714));
			mob.playGraphics(Graphic.create(308, 48, 100));
			mob.setAttribute("lastBountyTele", System.currentTimeMillis());
			World.getWorld().submit(new Tickable(4) {
				public void execute() {
					mob.setTeleportTarget(locations.get(mob.getRandom().nextInt(locations.size())));
					mob.playAnimation(Animation.create(-1, 0));
					player.removeAttribute("teleporting");
					this.stop();
				}
			});
		}
	}

	@SuppressWarnings("incomplete-switch")
	public static boolean canCastSpell(final Mob attacker, Mob victim, Spell spell) {
		if (attacker.isNPC())
			return true;
		if (spell == null)
			return false;
		if (attacker.getSkills().getLevel(Skills.MAGIC) < spell.getLevelRequired()) {
			if (attacker.getActionSender() != null)
				attacker.getActionSender().sendMessage("Your Magic level is not high enough for this spell.");
			return false;
		}
		if (!attacker.isNPC()) {
			switch (spell.getSpellBook()) {
			case MODERN_MAGICS:
				if (spell != Spell.TRIDENT_OF_THE_SEAS && spell != Spell.TRIDENT_OF_THE_SWAMP
						&& (attacker.getCombatState().getSpellBook() == SpellBook.ANCIENT_MAGICKS.getSpellBookId()
								|| attacker.getCombatState().getSpellBook() == SpellBook.LUNAR_MAGICS
										.getSpellBookId())) {
					return false;
				}
				if (spell == Spell.TELE_BLOCK) {
					if (victim != null && victim.isPlayer()) {
						Player vic = (Player) victim;
						if (vic.getDatabaseEntity().getPlayerSettings().isTeleBlocked()) {
							attacker.getActionSender().sendMessage("This player is already affected by this spell.");
							return false;
						}
					}
				}
				if (spell == Spell.CHARGE) {
					if (attacker.getCombatState().isCharged()) {
						if (attacker.getActionSender() != null) {
							attacker.getActionSender().sendMessage("Your current charge is too powerful.");
						}
						return false;
					}
				}
				break;
			case ANCIENT_MAGICKS:
				if (attacker.getCombatState().getSpellBook() == SpellBook.MODERN_MAGICS.getSpellBookId()
						|| attacker.getCombatState().getSpellBook() == SpellBook.LUNAR_MAGICS.getSpellBookId()) {
					return false;
				}
				break;
			case LUNAR_MAGICS:
				if (attacker.getCombatState().getSpellBook() == SpellBook.MODERN_MAGICS.getSpellBookId()
						|| attacker.getCombatState().getSpellBook() == SpellBook.ANCIENT_MAGICKS.getSpellBookId()) {
					return false;
				}
				if (attacker.getSkills().getLevelForExperience(Skills.DEFENCE) < 45) {
					if (attacker.getActionSender() != null) {
						attacker.getActionSender().sendMessage("You need a Defence level of 45 to use Lunar spells.");
					}
					return false;
				}
				if (spell == Spell.VENGEANCE || spell == Spell.VENGEANCE_OTHER) {
					if (!attacker.getCombatState().canVengeance()) {
						if (attacker.getActionSender() != null) {
							attacker.getActionSender()
									.sendMessage("You can only cast vengeance spells every 30 seconds.");
						}
						return false;
					}
				}
				break;
			}
		}
		if (spell.getSpellName().contains("Tele") && !spell.getSpellName().contains("Other")) {
			if (attacker.isPlayer()) {
				Player player = (Player) attacker;
				if (player.getDatabaseEntity().getPlayerSettings().isTeleBlocked()) {
					if (attacker.getActionSender() != null) {
						attacker.getActionSender().sendMessage("A magical force stops you from teleporting.");
					}
					return false;
				}
			}
		}
		if (!checkRunes(attacker, spell))
			return false;

		/**
		 * Extra items required for casting.
		 */
		if (spell.getRequiredItem() != null) {
			if (attacker.isPlayer() && attacker.getEquipment().getCount(spell.getRequiredItem().getId()) < spell
					.getRequiredItem().getCount()) {
				attacker.getActionSender().sendMessage("You must be wielding a "
						+ spell.getRequiredItem().getDefinition2().getName() + " to cast this spell.");
				return false;
			}
		}
		switch (spell) {

		case CONFUSE:
		case STUN:
			if (victim != null && victim.isAttackDrained()) {
				attacker.getActionSender().sendMessage("Your foe's attack has already been weakened.");
				return false;
			}
			break;
		case WEAKEN:
		case ENFEEBLE:
			if (victim != null && victim.isStrengthDrained()) {
				attacker.getActionSender().sendMessage("Your foe's strength has already been weakened.");
				return false;
			}
			break;
		case CURSE:
		case VULNERABILITY:
			if (victim != null && victim.isDefenceDrained()) {
				attacker.getActionSender().sendMessage("Your foe's defence has already been weakened.");
				return false;
			}
			break;
		case CRUMBLE_UNDEAD:
			if (!victim.getDefinedName().toLowerCase().contains("skeleton")
					&& !victim.getDefinedName().toLowerCase().contains("zombie")
					&& !victim.getDefinedName().toLowerCase().contains("shade")
					&& !victim.getDefinedName().toLowerCase().contains("ghost")) {
				if (attacker.getActionSender() != null) {
					attacker.getActionSender()
							.sendMessage("This spell only affects skeletons, zombies, ghosts and shades.");
				}
				return false;
			}
			break;
		default:
			break;
		}
		return true;
	}

	public void hitEnemy(final Mob attacker, Mob victim, final Spell spell, final Graphic graphic,
			final PoisonType poisonType, boolean multi, int maxDamage, final int delay, final int freezeTimer) {
		if (!BoundaryManager.isWithinBoundaryNoZ(attacker.getLocation(), "MultiCombat") || !attacker.inMulti())
			multi = false;
		final ArrayList<Mob> enemies = new ArrayList<>();
		ArrayList<Location> locationsUsed = new ArrayList<>();
		enemies.add(victim);
		locationsUsed.add(victim.getLocation());
		if (multi) {
			for (Mob mob : victim.getRegion().getMobs()) {
				if (mob != attacker && mob != victim && mob.getLocation().isWithinDistance(attacker, victim, 1)
						&& !locationsUsed.contains(mob.getLocation()) && super.canHit(attacker, mob, false, false)
						&& (BoundaryManager.isWithinBoundaryNoZ(mob.getLocation(), "MultiCombat") || mob.inMulti())) {
					if ((mob.isPlayer() && !mob.isInWilderness()) || enemies.contains(mob))
						continue;
					enemies.add(mob);
					locationsUsed.add(mob.getLocation());
				}
			}
		}
		final ArrayList<Integer> hits = new ArrayList<Integer>();
		for (int i = 0; i < enemies.size(); i++) {
			final Mob enemy = enemies.get(i);
			int hit = damage(maxDamage, attacker, enemy, AttackType.MAGIC, Skills.MAGIC, Prayers.PROTECT_FROM_MAGIC,
					false, false);

			if (graphic != null) {
				boolean isPlayer = attacker.isPlayer();
				if (isPlayer) {
					Player player = (Player) attacker;

					// tome of fire exception
					if (spell != null && spell.getSpellName().startsWith("fire"))
						fire = true;

					if (player.isMultiplayerDisabled())
						player.getActionSender().sendStillGFX(hit < 0 ? 85 : graphic.getId(),
								hit < 0 ? 100 : graphic.getHeight(), enemy.getCentreLocation());
					else
						enemy.playGraphics(hit < 0 ? Graphic.create(85, graphic.getDelay(), 100) : graphic);
				} else
					enemy.playGraphics(hit < 0 ? Graphic.create(85, graphic.getDelay(), 100) : graphic);
			} else {
				if (hit <= 0)
					enemy.playGraphics(Graphic.create(85, delay, 100));
			}
			if (hit > -1) {
				hit = (hit > 0 ? random.nextInt(hit) : 0) + (maxDamage > 0 ? 1 : 0);
				if (hit > 0) {

					if (hit > enemy.getSkills().getLevel(Skills.HITPOINTS))
						hit = enemy.getSkills().getLevel(Skills.HITPOINTS);

					// freezing happens immediately
					if (freezeTimer > 0) {
						if (enemy.getCombatState().canMove() && enemy.getCombatState().canBeFrozen()) {
							/*
							 * If the enemy has protect from magic, freeze time is halved.
							 */
							int finalTimer = freezeTimer;
							if (enemy.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC))
								finalTimer = finalTimer / 2;

							enemy.setFrozenBy(attacker);
							enemy.getCombatState().setCanMove(false);
							enemy.getCombatState().setCanBeFrozen(false);
							enemy.getWalkingQueue().reset();
							if (enemy.getActionSender() != null)
								enemy.getActionSender().sendMessage("You have been frozen!");

							World.getWorld().submit(new Tickable(finalTimer) {
								public void execute() {
									enemy.getCombatState().setCanMove(true);
									enemy.setFrozenBy(null);
									this.stop();
								}
							});
							World.getWorld().submit(new Tickable(finalTimer + 3) {
								public void execute() {
									enemy.getCombatState().setCanBeFrozen(true);
									this.stop();
								}
							});
						}
					}

					if (CombatFormula.fullAhrim(attacker)) {
						int ahrim = random.nextInt(4);
						if (ahrim == 1) {
							victim.getSkills().decreaseLevelToMinimum(Skills.STRENGTH, 5);
							victim.playGraphics(Graphic.create(400, 0, 100));
						}
					}
				}
			}
			addExperience(attacker, hit < 0 ? 0 : hit);
			hits.add(i, hit);
		}
		World.getWorld().submit(new Tickable(delay) {
			public void execute() {
				for (int i = 0; i < enemies.size(); i++) {
					final Mob enemy = enemies.get(i);
					int hit = hits.get(i);
					if (hit > enemy.getSkills().getLevel(Skills.HITPOINTS)) {
						hit = enemy.getSkills().getLevel(Skills.HITPOINTS);
					}

					if (hit > 0) {
						enemy.inflictDamage(new Hit(hit), attacker);
						smite(attacker, enemy, hit);
						recoil(attacker, enemy, hit);
						vengeance(attacker, enemy, hit, 1);
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
					enemy.getActiveCombatAction().defend(attacker, enemy, false);

					if (hit != -1) { // the effect will not occur if we have
						// splashed
						if (poisonType != PoisonType.NONE) {
							if (enemy.getCombatState().getPoisonDamage() < 1 && enemy.getCombatState().canBePoisoned()
									&& random.nextInt(11) == 3) {
								enemy.getCombatState().setPoisonDamage(poisonType.getRangeDamage(), attacker);
								if (enemy.getActionSender() != null) {
									enemy.getActionSender().sendMessage("You have been poisoned!");
									player.getActionSender().sendConfig(102, 1);
								}
							}
						}

						if (attacker.isNPC())
							continue;

						int drainedLevel = -1;
						double drainModifier = 0;
						switch (spell) {
						case SHADOW_RUSH:
						case SHADOW_BURST:
							enemy.getSkills().decreaseLevelOnce(Skills.ATTACK,
									(int) (enemy.getSkills().getLevelForExperience(Skills.ATTACK) * 0.10));
							break;
						case SHADOW_BLITZ:
						case SHADOW_BARRAGE:
							enemy.getSkills().decreaseLevelOnce(Skills.ATTACK,
									(int) (enemy.getSkills().getLevelForExperience(Skills.ATTACK) * 0.15));
							break;
						case BLOOD_RUSH:
						case BLOOD_BURST:
						case BLOOD_BLITZ:
						case BLOOD_BARRAGE:
							int heal = (int) (hit * 0.25);
							attacker.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, heal);
							break;
						case TELE_BLOCK:
							if (enemy.isPlayer()) {
								Player opp = (Player) enemy;
								opp.getDatabaseEntity().getPlayerSettings().setTeleBlocked(true);
								opp.getDatabaseEntity().getPlayerSettings().setTeleBlockTimer(
										opp.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC) ? 250 : 500);
								enemy.inflictDamage(new Hit(1), attacker);
							}
							break;
						case SARADOMIN_STRIKE:
							enemy.getSkills().decreasePrayerPoints(1);
							break;
						case CLAWS_OF_GUTHIX:
							enemy.getSkills().decreaseLevelOnce(Skills.DEFENCE,
									(int) (enemy.getSkills().getLevelForExperience(Skills.DEFENCE) * 0.5));
							break;
						case FLAMES_OF_ZAMORAK:
							enemy.getSkills().decreaseLevelOnce(Skills.MAGIC, 5);
							break;
						case CONFUSE:
							drainedLevel = Skills.ATTACK;
							drainModifier = 0.05;
							enemy.setAttackDrained(true);
							break;
						case WEAKEN:
							drainedLevel = Skills.STRENGTH;
							drainModifier = 0.05;
							enemy.setStrengthDrained(true);
							break;
						case CURSE:
							drainedLevel = Skills.DEFENCE;
							drainModifier = 0.05;
							enemy.setDefenceDrained(true);
							break;
						case STUN:
							drainedLevel = Skills.ATTACK;
							drainModifier = 0.10;
							enemy.setAttackDrained(true);
							break;
						case ENFEEBLE:
							drainedLevel = Skills.STRENGTH;
							drainModifier = 0.10;
							enemy.setStrengthDrained(true);
							break;
						case VULNERABILITY:
							drainedLevel = Skills.DEFENCE;
							drainModifier = 0.10;
							enemy.setDefenceDrained(true);
							break;
						default:
							break;
						}
						if (drainedLevel != -1 && drainModifier != 0) {
							int levelBefore = enemy.getSkills().getLevel(drainedLevel);
							enemy.getSkills().decreaseLevelOnce(drainedLevel,
									(int) (drainModifier * enemy.getSkills().getLevelForExperience(drainedLevel)));
							int levelDifference = levelBefore - enemy.getSkills().getLevel(drainedLevel);
							if (levelDifference < 1) {
								levelDifference = 1;
							}
							World.getWorld().submit(new Tickable(levelDifference * SkillsUpdateTick.CYCLE_TIME) {
								public void execute() {
									switch (spell) {
									case CONFUSE:
									case STUN:
										enemy.setAttackDrained(false);
										break;
									case WEAKEN:
									case ENFEEBLE:
										enemy.setStrengthDrained(false);
										break;
									case CURSE:
									case VULNERABILITY:
										enemy.setDefenceDrained(false);
										break;
									default:
										break;
									}
								}
							});
						}
					}
				}
				attacker.getCombatState().setCurrentSpell(null);
				this.stop();
			}
		});
	}

	public static boolean checkRunes(Mob mob, Spell spell) {
		if (mob.isNPC())
			return true;
		if (spell.getRunes() != null) {
			for (int i = 0; i < spell.getRunes().length; i++) {
				if (spell.getRune(i).getId() == 554) { // tome of fire exception
					if (mob.getEquipment().contains(20714))
						continue;
				}
				if (spell.getRune(i) != null) {
					if (!deleteRune(mob, spell.getRune(i))) {
						if (mob.getInventory() != null && mob.getInventory().getCount(spell.getRune(i).getId()) < spell
								.getRune(i).getCount()) {
							String runeName = NameUtils.formatName(spell.getRune(i).getDefinition2().getName())
									.toLowerCase();
							if (mob.getActionSender() != null) {
								mob.getActionSender()
										.sendMessage("You do not have enough " + runeName + "s to cast this spell.");
							}
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public static void setAutocast(Mob mob, Spell spell, int config, boolean defensive) {

		// if (defensive)
		// config += 93;

		if (mob.getEquipment() != null && spell != null)
			mob.getEquipment().fireItemChanged(Equipment.SLOT_WEAPON);

		if (spell == null) {
			if (mob.getCombatState().getCombatStyle() == CombatStyle.AUTOCAST
					|| mob.getCombatState().getCombatStyle() == CombatStyle.DEFENSIVE_AUTOCAST) {
				if (mob.getAttribute("lastAttackType") != null) {
					mob.getCombatState().setAttackType((AttackType) mob.getAttribute("lastAttackType"));
					mob.getCombatState().setCombatStyle((CombatStyle) mob.getAttribute("lastCombatStyle"));
					mob.removeAttribute("lastAttackType");
					mob.removeAttribute("lastCombatStyle");
				} else {
					mob.getCombatState().setAttackType(AttackType.CRUSH);
					mob.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				}
				if (mob.getActionSender() != null) {
					mob.getActionSender().sendConfig(43, 0);
					mob.getActionSender().sendConfig(108, 0);
				}
			}
			mob.getCombatState().setAttackType(AttackType.CRUSH);
			mob.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
		} else {
			mob.setAttribute("lastAttackType", mob.getCombatState().getAttackType());
			mob.setAttribute("lastCombatStyle", mob.getCombatState().getCombatStyle());
			mob.getCombatState().setCombatStyle(defensive ? CombatStyle.DEFENSIVE_AUTOCAST : CombatStyle.AUTOCAST);
			mob.getInterfaceState().setLastUsedAutocast(config);
			if (mob.getActionSender() != null) {
				mob.getActionSender().sendConfig(43, 4); // 4
				mob.getActionSender().sendConfig(108, config);
				mob.getActionSender().sendString(593, 2, "Combat lvl: " + mob.getSkills().getCombatLevel());

				// mob.getActionSender().sendMessage("<img=32> <col=ff0000>Defensive
				// auto-casting: " + defensive + ".");
			}
		}
		int pane = mob.getAttribute("tabmode");
		if (pane == 161) {
			mob.getActionSender().sendSidebarInterface(61, 593);
		}
		if (pane == 164) {
			mob.getActionSender().sendSidebarInterface(59, 593);
		}
		if (pane == 548) {
			mob.getActionSender().sendSidebarInterface(63, 593);
		}
		mob.setAutocastSpell(spell);
	}

	public static final int FIRE_RUNE = 554, WATER_RUNE = 555, AIR_RUNE = 556, EARTH_RUNE = 557, MIND_RUNE = 558,
			BODY_RUNE = 559, DEATH_RUNE = 560, NATURE_RUNE = 561, CHAOS_RUNE = 562, LAW_RUNE = 563, COSMIC_RUNE = 564,
			BLOOD_RUNE = 565, SOUL_RUNE = 566, ASTRAL_RUNE = 9075;

	/**
	 * Represents spells.
	 *
	 * @author Michael Bull
	 */
	public static enum Spell {

		/**
		 * Wind Strike.
		 */
		WIND_STRIKE(1, 1, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(AIR_RUNE, 1), new Item(MIND_RUNE, 1) }, "windStrike", null),

		/**
		 * Water Strike.
		 */
		WATER_STRIKE(4, 5, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(WATER_RUNE, 1), new Item(AIR_RUNE, 1), new Item(MIND_RUNE, 1) }, "waterStrike",
				null),

		/**
		 * Earth Strike.
		 */
		EARTH_STRIKE(6, 9, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(EARTH_RUNE, 2), new Item(AIR_RUNE, 1), new Item(MIND_RUNE, 1) }, "earthStrike",
				null),

		/**
		 * Fire Strike.
		 */
		FIRE_STRIKE(8, 13, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(FIRE_RUNE, 3), new Item(AIR_RUNE, 2), new Item(MIND_RUNE, 1) }, "fireStrike",
				null),

		/**
		 * Wind Bolt.
		 */
		WIND_BOLT(10, 17, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(AIR_RUNE, 2), new Item(CHAOS_RUNE, 1) }, "windBolt", null),

		/**
		 * Water Bolt.
		 */
		WATER_BOLT(14, 23, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(AIR_RUNE, 2), new Item(WATER_RUNE, 2), new Item(CHAOS_RUNE, 1) }, "waterBolt",
				null),

		/**
		 * Earth Bolt.
		 */
		EARTH_BOLT(17, 29, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(AIR_RUNE, 2), new Item(EARTH_RUNE, 3), new Item(CHAOS_RUNE, 1) }, "earthBolt",
				null),

		/**
		 * Fire Bolt.
		 */
		FIRE_BOLT(20, 35, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(AIR_RUNE, 3), new Item(FIRE_RUNE, 5), new Item(CHAOS_RUNE, 1) }, "fireBolt",
				null),

		/**
		 * Wind Blast.
		 */
		WIND_BLAST(24, 41, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(AIR_RUNE, 3), new Item(DEATH_RUNE, 1) }, "windBlast", null),

		/**
		 * Water Blast.
		 */
		WATER_BLAST(27, 47, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(AIR_RUNE, 3), new Item(WATER_RUNE, 3), new Item(DEATH_RUNE, 1) }, "waterBlast",
				null),

		/**
		 * Earth Blast.
		 */
		EARTH_BLAST(33, 53, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(AIR_RUNE, 3), new Item(EARTH_RUNE, 4), new Item(DEATH_RUNE, 1) }, "earthBlast",
				null),

		/**
		 * Fire Blast.
		 */
		FIRE_BLAST(38, 59, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(AIR_RUNE, 4), new Item(FIRE_RUNE, 5), new Item(DEATH_RUNE, 1) }, "fireBlast",
				null),

		/**
		 * Wind Wave.
		 */
		WIND_WAVE(45, 62, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(AIR_RUNE, 5), new Item(BLOOD_RUNE, 1) }, "windWave", null),

		/**
		 * Water Wave.
		 */
		WATER_WAVE(48, 65, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(AIR_RUNE, 5), new Item(WATER_RUNE, 7), new Item(BLOOD_RUNE, 1) }, "waterWave",
				null),

		/**
		 * Earth Wave.
		 */
		EARTH_WAVE(53, 73, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(AIR_RUNE, 5), new Item(EARTH_RUNE, 7), new Item(BLOOD_RUNE, 1) }, "earthWave",
				null),

		/**
		 * Fire Wave.
		 */
		FIRE_WAVE(56, 75, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(AIR_RUNE, 5), new Item(FIRE_RUNE, 7), new Item(BLOOD_RUNE, 1) }, "fireWave",
				null),

		/**
		 * Trident of the seas
		 */
		TRIDENT_OF_THE_SEAS(-1, 75, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[0], "tridentOfTheSeas", null),

		/**
		 * Trident of the swamp
		 */
		TRIDENT_OF_THE_SWAMP(-1, 75, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[0], "tridentOfTheSwamp", null),

		/**
		 * Saradomin Strike.
		 */
		SARADOMIN_STRIKE(41, 60, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(FIRE_RUNE, 2), new Item(BLOOD_RUNE, 2), new Item(AIR_RUNE, 4) },
				"saradominStrike", new Item(2415)),

		/**
		 * Claws of Guthix.
		 */
		CLAWS_OF_GUTHIX(42, 60, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(FIRE_RUNE, 1), new Item(BLOOD_RUNE, 2), new Item(AIR_RUNE, 4) }, "clawsOfGuthix",
				new Item(2416)),

		/**
		 * Flames of Zamorak.
		 */
		FLAMES_OF_ZAMORAK(43, 60, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(FIRE_RUNE, 4), new Item(BLOOD_RUNE, 2), new Item(AIR_RUNE, 1) },
				"flamesOfZamorak", new Item(2417)),

		/**
		 * Iban Blast.
		 */
		IBAN_BLAST(29, 50, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(FIRE_RUNE, 5), new Item(DEATH_RUNE, 1) }, "ibanBlast", new Item(1409)),

		/**
		 * Magic Dart.
		 */
		MAGIC_DART(31, 50, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(MIND_RUNE, 4), new Item(DEATH_RUNE, 1) }, "magicDart", new Item(4170)),

		/**
		 * Crumble Undead.
		 */
		CRUMBLE_UNDEAD(22, 39, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(EARTH_RUNE, 2), new Item(AIR_RUNE, 2), new Item(CHAOS_RUNE, 1) }, "crumbleUndead",
				null),

		/**
		 * Smoke Rush.
		 */
		SMOKE_RUSH(74, 50, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT,
				new Item[] { new Item(DEATH_RUNE, 2), new Item(CHAOS_RUNE, 2), new Item(FIRE_RUNE, 1) }, "smokeRush",
				null),

		/**
		 * Shadow Rush.
		 */
		SHADOW_RUSH(78, 52, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT,
				new Item[] { new Item(DEATH_RUNE, 2), new Item(CHAOS_RUNE, 2), new Item(SOUL_RUNE, 1) }, "shadowRush",
				null),

		/**
		 * Blood Rush.
		 */
		BLOOD_RUSH(70, 56, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT,
				new Item[] { new Item(DEATH_RUNE, 2), new Item(CHAOS_RUNE, 2), new Item(BLOOD_RUNE, 1) }, "bloodRush",
				null),

		/**
		 * Ice Rush.
		 */
		ICE_RUSH(66, 58, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT,
				new Item[] { new Item(DEATH_RUNE, 2), new Item(CHAOS_RUNE, 2), new Item(WATER_RUNE, 2) }, "iceRush",
				null),

		/**
		 * Smoke Burst.
		 */
		SMOKE_BURST(76, 62, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT,
				new Item[] { new Item(DEATH_RUNE, 2), new Item(CHAOS_RUNE, 4), new Item(AIR_RUNE, 2) }, "smokeBurst",
				null),

		/**
		 * Shadow Burst.
		 */
		SHADOW_BURST(80, 64, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 2),
				new Item(CHAOS_RUNE, 4), new Item(AIR_RUNE, 2), new Item(AIR_RUNE, 2) }, "shadowBurst", null),

		/**
		 * Blood Burst.
		 */
		BLOOD_BURST(72, 68, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT,
				new Item[] { new Item(DEATH_RUNE, 2), new Item(CHAOS_RUNE, 4), new Item(BLOOD_RUNE, 2) }, "bloodBurst",
				null),

		/**
		 * Ice Burst.
		 */
		ICE_BURST(68, 70, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT,
				new Item[] { new Item(DEATH_RUNE, 2), new Item(CHAOS_RUNE, 4), new Item(WATER_RUNE, 2) }, "iceBurst",
				null),

		/**
		 * Smoke Blitz.
		 */
		SMOKE_BLITZ(75, 74, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 2),
				new Item(FIRE_RUNE, 2), new Item(BLOOD_RUNE, 2), new Item(AIR_RUNE, 2) }, "smokeBlitz", null),

		/**
		 * Shadow Blitz.
		 */
		SHADOW_BLITZ(79, 76, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 2),
				new Item(BLOOD_RUNE, 2), new Item(AIR_RUNE, 2), new Item(SOUL_RUNE, 2) }, "shadowBlitz", null),

		/**
		 * Blood Blitz.
		 */
		BLOOD_BLITZ(71, 80, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT,
				new Item[] { new Item(DEATH_RUNE, 2), new Item(BLOOD_RUNE, 4) }, "bloodBlitz", null),

		/**
		 * Ice Blitz.
		 */
		ICE_BLITZ(67, 82, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT,
				new Item[] { new Item(DEATH_RUNE, 2), new Item(BLOOD_RUNE, 2), new Item(WATER_RUNE, 3) }, "iceBlitz",
				null),

		/**
		 * Smoke Barrage.
		 */
		SMOKE_BARRAGE(77, 86, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 4),
				new Item(BLOOD_RUNE, 2), new Item(AIR_RUNE, 4), new Item(FIRE_RUNE, 4) }, "smokeBarrage", null),

		/**
		 * Shadow Barrage.
		 */
		SHADOW_BARRAGE(
				81, 88, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 4),
						new Item(BLOOD_RUNE, 2), new Item(AIR_RUNE, 4), new Item(SOUL_RUNE, 3) },
				"shadowBarrage", null),

		/**
		 * Blood Barrage.
		 */
		BLOOD_BARRAGE(73, 92, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT,
				new Item[] { new Item(DEATH_RUNE, 4), new Item(BLOOD_RUNE, 4), new Item(SOUL_RUNE, 1) }, "bloodBarrage",
				null),

		/**
		 * Ice Barrage.
		 */
		ICE_BARRAGE(69, 94, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT,
				new Item[] { new Item(DEATH_RUNE, 4), new Item(BLOOD_RUNE, 2), new Item(WATER_RUNE, 6) }, "iceBarrage",
				null),

		/**
		 * Bind.
		 */
		BIND(12, 20, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(EARTH_RUNE, 3), new Item(WATER_RUNE, 3), new Item(NATURE_RUNE, 2) }, "bind",
				null),

		/**
		 * Snare.
		 */
		SNARE(30, 50, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(EARTH_RUNE, 4), new Item(WATER_RUNE, 4), new Item(NATURE_RUNE, 3) }, "snare",
				null),

		/**
		 * Entangle.
		 */
		ENTANGLE(57, 79, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(EARTH_RUNE, 5), new Item(WATER_RUNE, 5), new Item(NATURE_RUNE, 4) }, "entangle",
				null),

		/**
		 * Confuse.
		 */
		CONFUSE(2, 3, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(WATER_RUNE, 3), new Item(EARTH_RUNE, 2), new Item(BODY_RUNE, 1) }, "confuse",
				null),

		/**
		 * Weaken.
		 */
		WEAKEN(7, 11, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(WATER_RUNE, 3), new Item(EARTH_RUNE, 2), new Item(BODY_RUNE, 1) }, "weaken",
				null),

		/**
		 * Curse.
		 */
		CURSE(11, 19, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(WATER_RUNE, 2), new Item(EARTH_RUNE, 3), new Item(BODY_RUNE, 1) }, "curse", null),

		/**
		 * Vulnerability.
		 */
		VULNERABILITY(51, 76, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(WATER_RUNE, 6), new Item(EARTH_RUNE, 6), new Item(SOUL_RUNE, 1) },
				"vulnerability", null),

		/**
		 * Enfeeble.
		 */
		ENFEEBLE(54, 73, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(WATER_RUNE, 8), new Item(EARTH_RUNE, 8), new Item(SOUL_RUNE, 1) }, "enfeeble",
				null),

		/**
		 * Stun.
		 */
		STUN(58, 80, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
				new Item[] { new Item(WATER_RUNE, 12), new Item(EARTH_RUNE, 12), new Item(SOUL_RUNE, 1) }, "stun",
				null),

		/**
		 * Varrock teleport.
		 */
		VARROCK_TELEPORT(16, 25, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 1), new Item(FIRE_RUNE, 1), new Item(AIR_RUNE, 3) }, "varrockTeleport",
				null),

		/**
		 * Bounty Locate
		 */
		BOUNTY_LOCATE(63, 85, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 1), new Item(CHAOS_RUNE, 1), new Item(DEATH_RUNE, 1) }, "bountyLocate",
				null),

		BOUNTY_LOCATE_ANCIENTS(92, 85, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 1), new Item(CHAOS_RUNE, 1), new Item(DEATH_RUNE, 1) }, "bountyLocate",
				null), BOUNTY_LOCATE_LUNAR(123, 85, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT,
						new Item[] { new Item(LAW_RUNE, 1), new Item(CHAOS_RUNE, 1), new Item(DEATH_RUNE, 1) },
						"bountyLocate", null),

		/**
		 * Lumbridge teleport.
		 */
		LUMBRIDGE_TELEPORT(19, 31, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 1), new Item(EARTH_RUNE, 1), new Item(AIR_RUNE, 3) },
				"lumbridgeTeleport", null),

		/**
		 * Falador teleport.
		 */
		FALADOR_TELEPORT(22, 37, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 1), new Item(WATER_RUNE, 1), new Item(AIR_RUNE, 3) }, "faladorTeleport",
				null),

		/**
		 * Camelot teleport.
		 */
		CAMELOT_TELEPORT(27, 45, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 1), new Item(AIR_RUNE, 5) }, "camelotTeleport", null),

		/**
		 * Ardougne teleport.
		 */
		ARDOUGNE_TELEPORT(33, 51, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 2), new Item(WATER_RUNE, 2) }, "ardougneTeleport", null),

		/**
		 * Watchtower teleport.
		 */
		WATCHTOWER_TELEPORT(38, 58, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 2), new Item(EARTH_RUNE, 2) }, "watchtowerTeleport", null),

		/**
		 * Trollheim teleport.
		 */
		TROLLHEIM_TELEPORT(45, 61, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 2), new Item(FIRE_RUNE, 2) }, "trollheimTeleport", null),

		/**
		 * Paddewwa teleport.
		 */
		PADDEWWA_TELEPORT(83, 54, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 2), new Item(FIRE_RUNE, 2) }, "paddewwaTeleport", null),

		/**
		 * Senntisten teleport.
		 */
		SENNTISTEN_TELEPORT(84, 60, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 2), new Item(SOUL_RUNE, 1) }, "senntistenTeleport", null),

		/**
		 * Kharyrll teleport.
		 */
		KHARYRLL_TELEPORT(85, 66, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 2), new Item(BLOOD_RUNE, 1) }, "kharyrllTeleport", null),

		/**
		 * Lassar teleport.
		 */
		LASSAR_TELEPORT(86, 72, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 2), new Item(WATER_RUNE, 4) }, "lassarTeleport", null),

		/**
		 * Dareeyak teleport.
		 */
		DAREEYAK_TELEPORT(87, 78, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 2), new Item(FIRE_RUNE, 3), new Item(AIR_RUNE, 2) }, "dareeyakTeleport",
				null),

		/**
		 * Carrallangar teleport.
		 */
		CARRALLANGAR_TELEPORT(88, 84, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 2), new Item(SOUL_RUNE, 2) }, "carrallangarTeleport", null),

		/**
		 * Annakarl teleport.
		 */
		ANNAKARL_TELEPORT(89, 90, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 2), new Item(BLOOD_RUNE, 2) }, "annakarlTeleport", null),

		/**
		 * Ghorrock teleport.
		 */
		GHORROCK_TELEPORT(90, 96, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 2), new Item(WATER_RUNE, 8) }, "ghorrockTeleport", null),

		/**
		 * Moonclan teleport
		 */
		MOONCLAN_TELEPORT(102, 69, SpellBook.LUNAR_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(ASTRAL_RUNE, 2), new Item(LAW_RUNE, 1), new Item(EARTH_RUNE, 2) },
				"moonclanTeleport", null),

		/**
		 * Waterbirth teleport
		 */
		WATERBIRTH_TELEPORT(106, 72, SpellBook.LUNAR_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(ASTRAL_RUNE, 2), new Item(LAW_RUNE, 1), new Item(WATER_RUNE, 1) },
				"waterbirthTeleport", null),

		/**
		 * Barbarian teleport
		 */
		BARBARIAN_TELEPORT(110, 75, SpellBook.LUNAR_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(ASTRAL_RUNE, 2), new Item(LAW_RUNE, 2), new Item(FIRE_RUNE, 3) },
				"barbarianTeleport", null),

		/**
		 * Khazard teleport
		 */
		KHAZARD_TELEPORT(114, 78, SpellBook.LUNAR_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(ASTRAL_RUNE, 2), new Item(LAW_RUNE, 2), new Item(WATER_RUNE, 4) },
				"khazardTeleport", null),

		/**
		 * Fishing guild teleport
		 */
		FISHING_GUILD_TELEPORT(122, 85, SpellBook.LUNAR_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(ASTRAL_RUNE, 3), new Item(LAW_RUNE, 3), new Item(WATER_RUNE, 10) },
				"fishingGuildTeleport", null),

		/**
		 * Catherby teleport
		 */
		CATHERBY_TELEPORT(126, 87, SpellBook.LUNAR_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(ASTRAL_RUNE, 3), new Item(LAW_RUNE, 3), new Item(WATER_RUNE, 10) },
				"catherbyTeleport", null),

		/**
		 * Ice Plateau teleport
		 */
		ICE_PLATEAU_TELEPORT(129, 89, SpellBook.LUNAR_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(ASTRAL_RUNE, 3), new Item(LAW_RUNE, 3), new Item(WATER_RUNE, 8) },
				"icePlateauTeleport", null),

		/**
		 * Vengeance
		 */
		VENGEANCE(134, 94, SpellBook.LUNAR_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(ASTRAL_RUNE, 4), new Item(DEATH_RUNE, 2), new Item(EARTH_RUNE, 10) }, "vengeance",
				null),

		/**
		 * Vengeance Other
		 */
		VENGEANCE_OTHER(132, 93, SpellBook.LUNAR_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(ASTRAL_RUNE, 4), new Item(DEATH_RUNE, 2), new Item(EARTH_RUNE, 10) },
				"vengeanceOther", null),

		/**
		 * Spellbook Swap
		 */
		SPELLBOOK_SWAP(136, 96, SpellBook.LUNAR_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(ASTRAL_RUNE, 3), new Item(COSMIC_RUNE, 2), new Item(LAW_RUNE, 1) },
				"spellbookSwap", null),

		/**
		 * Barrows teleport.
		 */
		BARROWS_TELEPORT(172, 83, SpellBook.ARCEUUS_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 2), new Item(SOUL_RUNE, 2), new Item(BLOOD_RUNE, 1) },
				"barrowsTeleport", null),

		/**
		 * Respawn teleport.
		 */
		RESPAWN_TELEPORT(154, 34, SpellBook.ARCEUUS_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(LAW_RUNE, 1), new Item(SOUL_RUNE, 1) }, "respawnTeleport", null),

		/**
		 * Home teleport modern
		 */
		HOME_TELEPORT_MODERN(1, 1, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT, new Item[0], "homeTeleport", null),

		/**
		 * Home teleport lunar
		 */
		HOME_TELEPORT_LUNAR(16, 1, SpellBook.LUNAR_MAGICS, SpellType.NON_COMBAT, new Item[0], "homeTeleport", null),

		/**
		 * Home teleport ancient
		 */
		HOME_TELEPORT_ANCIENT(91, 1, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT, new Item[0], "homeTeleport",
				null),

		/**
		 * Home teleport modern
		 */
		HOME_TELEPORT_ARCEUUS(139, 1, SpellBook.ARCEUUS_MAGICS, SpellType.NON_COMBAT, new Item[0], "homeTeleport",
				null),

		/**
		 * Charge
		 */
		CHARGE(58, 80, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT,
				new Item[] { new Item(FIRE_RUNE, 3), new Item(BLOOD_RUNE, 3), new Item(AIR_RUNE, 3) }, "charge", null),

		/**
		 * Bandos spiritual mage
		 */
		BANDOS_SPIRITUAL_MAGE(900, 1, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] {}, "bandosSpiritualMage",
				null), TELE_BLOCK(61, 85, SpellBook.MODERN_MAGICS, SpellType.COMBAT,
						new Item[] { new Item(CHAOS_RUNE, 1), new Item(DEATH_RUNE, 1), new Item(LAW_RUNE, 1) },
						"teleBlock", null);

		/**
		 * A map of spell IDs.
		 */
		private static List<Spell> spells = new ArrayList<Spell>();

		/**
		 * Gets a spell by its ID.
		 *
		 * @param spell
		 *            The Spell id.
		 * @return The spell, or <code>null</code> if the id is not a spell.
		 */
		public static Spell forId(int spellId, SpellBook spellBook) {
			for (Spell spell : spells) {
				if (spell.getSpellBook().getSpellBookId() == spellBook.getSpellBookId()
						&& spell.getSpellId() == spellId) {
					return spell;
				}
			}
			return null;
		}

		/**
		 * Populates the prayer map.
		 */
		static {
			for (Spell spell : Spell.values()) {
				spells.add(spell);
			}
		}

		/**
		 * The id of this spell.
		 */
		private int id;

		/**
		 * The level required to use this spell.
		 */
		private int levelRequired;

		/**
		 * The spellbook this spell is on.
		 */
		private SpellBook spellBook;

		/**
		 * The type of spell this is.
		 */
		private SpellType spellType;

		/**
		 * The runes required for this spell.
		 */
		private Item[] runes;

		/**
		 * The spell's name for script parsing..
		 */
		private String spellName;

		/**
		 * The item required to cast this spell.
		 */
		private Item requiredItem;

		/**
		 * Creates the spell.
		 *
		 * @param id
		 *            The spell id.
		 * @return
		 */
		private Spell(int id, int levelRequired, SpellBook spellBook, SpellType spellType, Item[] runes,
				String spellName, Item requiredItem) {
			this.id = id;
			this.levelRequired = levelRequired;
			this.spellBook = spellBook;
			this.spellType = spellType;
			this.runes = runes;
			this.spellName = spellName;
			this.requiredItem = requiredItem;
		}

		/**
		 * Gets the spell id.
		 *
		 * @return The spell id.
		 */
		public int getSpellId() {
			return id;
		}

		/**
		 * Gets the level required to use this spell.
		 *
		 * @return The level required to use this spell.
		 */
		public int getLevelRequired() {
			return levelRequired;
		}

		/**
		 * Gets the spell book this spell is on.
		 *
		 * @return The spell book this spell is on.
		 */
		public SpellBook getSpellBook() {
			return spellBook;
		}

		/**
		 * @return the spellType
		 */
		public SpellType getSpellType() {
			return spellType;
		}

		/**
		 * Gets the runes required for this spell.
		 *
		 * @return The runes required for this spell.
		 */
		public Item[] getRunes() {
			return runes;
		}

		/**
		 * Gets the rune required for this spell by its index.
		 *
		 * @return The rune required for this spell by its index.
		 */
		public Item getRune(int index) {
			return runes[index];
		}

		/**
		 * Gets the spell's name for script parsing.
		 *
		 * @return the spellName
		 */
		public String getSpellName() {
			return spellName;
		}

		/**
		 * Gets the required item to cast this spell.
		 *
		 * @return The required item to cast this spell.
		 */
		public Item getRequiredItem() {
			return requiredItem;
		}
	}

	/**
	 * Represents spell books.
	 *
	 * @author Michael Bull
	 */
	public static enum SpellBook {

		MODERN_MAGICS(0, 192),

		ANCIENT_MAGICKS(1, 193),

		LUNAR_MAGICS(2, 430),

		ARCEUUS_MAGICS(3, 192);

		/**
		 * A map of spell book IDs.
		 */
		private static Map<Integer, SpellBook> spellBooks = new HashMap<Integer, SpellBook>();

		/**
		 * Gets a spell book by its ID.
		 *
		 * @param spellBook
		 *            The Spell book id.
		 * @return The spell book, or <code>null</code> if the id is not a spell book.
		 */
		public static SpellBook forId(int spellBook) {
			return spellBooks.get(spellBook);
		}

		/**
		 * Populates the spell book map.
		 */
		static {
			for (SpellBook spellBook : SpellBook.values()) {
				spellBooks.put(spellBook.id, spellBook);
			}
		}

		/**
		 * The id of this spell book.
		 */
		private int id;

		/**
		 * The interface id of this spell book.
		 */
		private int interfaceId;

		/**
		 * Creates the spell book.
		 *
		 * @param id
		 *            The spellBook id.
		 * @return
		 */
		private SpellBook(int id, int interfaceId) {
			this.id = id;
			this.interfaceId = interfaceId;
		}

		/**
		 * Gets the spellBook id.
		 *
		 * @return The spellBook id.
		 */
		public int getSpellBookId() {
			return id;
		}

		/**
		 * @return The interfaceId.
		 */
		public int getInterfaceId() {
			return interfaceId;
		}
	}

	/**
	 * An enum containing different spell types.
	 *
	 * @author Michael
	 */
	public enum SpellType {

		COMBAT,

		NON_COMBAT;
	}

	public static boolean deleteRune(Mob mob, Item rune) {
		return Inventory.containsRune(mob, rune);
	}

}