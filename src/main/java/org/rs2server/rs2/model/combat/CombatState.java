package org.rs2server.rs2.model.combat;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.Consumables.Food;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.Spell;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.tickable.impl.PoisonDrainTick;
import org.rs2server.rs2.tickable.impl.SpecialEnergyRestoreTick;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds details related to a specific mob's state in combat.
 * 
 * @author Graham Edgecombe
 *
 */
public final class CombatState {

	/**
	 * The mob whose combat state this is.
	 */
	private Mob mob;

	/**
	 * The damage map of this entity.
	 */
	private DamageMap damageMap = new DamageMap();

	/**
	 * The current attack event.
	 */
	private AttackAction attackEvent;
	private int eatDelay;

	/**
	 * Gets the current attack event.
	 * 
	 * @return The current attack event.
	 */
	public AttackAction getAttackEvent() {
		return attackEvent;
	}

	/**
	 * The attack delay. (Each value of 1 counts for 600ms, e.g. 3 = 1800ms).
	 */
	private int attackDelay;

	/**
	 * Gets the current attack delay.
	 * 
	 * @return The current attack delay.
	 */
	public int getAttackDelay() {
		return attackDelay;
	}

	/**
	 * Sets the current attack delay.
	 * 
	 * @param attackDelay
	 *            The attack delay to set.
	 */
	public void setAttackDelay(int attackDelay) {
		this.attackDelay = attackDelay;
	}

	/**
	 * Decreases the current attack delay.
	 * 
	 * @param amount
	 *            The amount to decrease by.
	 */
	public void decreaseAttackDelay(int amount) {
		this.attackDelay -= amount;
	}

	/**
	 * Increases the current attack delay.
	 * 
	 * @param amount
	 *            The amount to increase by.
	 */
	public void increaseAttackDelay(int amount) {
		this.attackDelay += amount;
	}

	/**
	 * The spell delay. (Each value of 1 counts for 600ms, e.g. 3 = 1800ms).
	 */
	private int spellDelay;

	/**
	 * @return the spellDelay
	 */
	public int getSpellDelay() {
		return spellDelay;
	}

	/**
	 * @param spellDelay
	 *            the spellDelay to set
	 */
	public void setSpellDelay(int spellDelay) {
		this.spellDelay = spellDelay;
	}

	/**
	 * @param spellDelay
	 *            the spellDelay to set
	 */
	public void decreaseSpellDelay(int amount) {
		this.spellDelay -= amount;
	}

	/**
	 * Creates the combat state class for the specified mob.
	 * 
	 * @param mob
	 *            The mob.
	 */
	public CombatState(Mob mob) {
		this.mob = mob;
	}

	/**
	 * Gets the damage map of this entity.
	 * 
	 * @return The damage map.
	 */
	public DamageMap getDamageMap() {
		return damageMap;
	}

	/**
	 * Begins an attack on the specified victim.
	 * 
	 * @param victim
	 *            The victim.
	 * @param retaliating
	 *            A boolean flag indicating if the attack is a retaliation.
	 */
	public void startAttacking(Mob victim, boolean retaliating) {
		if (mob.isNPC()) {
			NPC npc = (NPC) mob;
			if (npc.instancedPlayer != null && npc.instancedPlayer != victim)
				return;
		}
		mob.setInteractingEntity(InteractionMode.ATTACK, victim);
		// prevents running to the mob if you are already in distance
		boolean requiresDistance = true;
		if (mob.isNPC()) {
			NPC n = (NPC) mob;
			if (n.getCombatDefinition() != null && (n.getCombatDefinition().isFightCavesNPC() || n.getId() == 492))
				requiresDistance = false;
		}
		if (requiresDistance && mob.getLocation().isWithinDistance(victim.getLocation(),
				mob.getActiveCombatAction().distance(mob))) {
			mob.getWalkingQueue().reset();
		}
		if (attackEvent == null || !attackEvent.isRunning()) {
			attackEvent = new AttackAction(mob);
			// if(mob.getActionSender() != null) {
			// mob.getActionSender().sendFollowing(victim, 1);
			// }
			mob.getActionQueue().clearRemovableActions(); // cancels all actions
			mob.getActionQueue().addAction(attackEvent);
		} // else the attack event is reused to preserve cooldown period
	}

	public boolean isAlive() {
		return !isDead();
	}

	public void setEatDelay(int eatDelay) {
		this.eatDelay = eatDelay;
	}

	public int getEatDelay() {
		return eatDelay;
	}

	public void decreaseEatDelay(int i) {
		eatDelay -= i;
		if (eatDelay <= 0) {
			setCanEat(true);
		}
	}

	public static enum CombatStyle {

		ACCURATE(0, new int[] { Skills.ATTACK, Skills.HITPOINTS }, new double[] { 4, 1.33 }),

		AGGRESSIVE_1(1, new int[] { Skills.STRENGTH }, new double[] { 4 }),

		AGGRESSIVE_2(2, new int[] { Skills.STRENGTH }, new double[] { 4 }),

		DEFENSIVE(3, new int[] { Skills.DEFENCE }, new double[] { 4, 1.33 }),

		CONTROLLED_1(4, new int[] { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE },
				new double[] { 1.33, 1.33, 1.33 }),

		CONTROLLED_2(5, new int[] { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE },
				new double[] { 1.33, 1.33, 1.33 }),

		CONTROLLED_3(6, new int[] { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE },
				new double[] { 1.33, 1.33, 1.33 }),

		AUTOCAST(7, new int[] { Skills.MAGIC }, new double[] { 2 }),

		DEFENSIVE_AUTOCAST(8, new int[] { Skills.MAGIC, Skills.DEFENCE }, new double[] { 1.33, 1 }),

		BASH(9, new int[] { Skills.ATTACK }, new double[] { 1.33, 1 }),

		POUND(10, new int[] { Skills.STRENGTH }, new double[] { 1.33, 1 }),

		FOCUS(11, new int[] { Skills.DEFENCE }, new double[] { 1.33, 1 });

		/**
		 * A map of combat styles.
		 */
		private static Map<Integer, CombatStyle> combatStyles = new HashMap<Integer, CombatStyle>();

		/**
		 * Gets a combat style by its ID.
		 * 
		 * @param combatStyle
		 *            The combat style id.
		 * @return The combat style, or <code>null</code> if the id is not a
		 *         combat style.
		 */
		public static CombatStyle forId(int combatStyle) {
			return combatStyles.get(combatStyle);
		}

		/**
		 * Populates the combat style map.
		 */
		static {
			for (CombatStyle combatStyle : CombatStyle.values()) {
				combatStyles.put(combatStyle.id, combatStyle);
			}
		}

		/**
		 * The combat style's id.
		 */
		private int id;

		/**
		 * The skills this combat style adds experience to.
		 */
		private int[] skills;

		/**
		 * The amounts of experience this combat style adds.
		 */
		private double[] experiences;

		private CombatStyle(int id, int[] skills, double[] experiences) {
			this.id = id;
			this.skills = skills;
			this.experiences = experiences;
		}

		/**
		 * Gets the combat styles id.
		 * 
		 * @return The combat styles id.
		 */
		public int getId() {
			return id;
		}

		/**
		 * Gets the skills this attack type adds experience to.
		 * 
		 * @return The skills this attack type adds experience to.
		 */
		public int[] getSkills() {
			return skills;
		}

		/**
		 * Gets a skill this attack type adds experience to by its index.
		 * 
		 * @param index
		 *            The skill index.
		 * @return The skill this attack type adds experience to by its index.
		 */
		public int getSkill(int index) {
			return skills[index];
		}

		/**
		 * Gets the experience amounts this attack type adds.
		 * 
		 * @return The experience amounts this attack type adds.
		 */
		public double[] getExperiences() {
			return experiences;
		}

		/**
		 * Gets an amount of experience this attack type adds by its index.
		 * 
		 * @param index
		 *            The experience index.
		 * @return The amount of experience this attack type adds by its index.
		 */
		public double getExperience(int index) {
			return experiences[index];
		}
	}

	/**
	 * Used for defence calculation, EG: White mace vs Low crush defence.
	 * 
	 * @author Michael Bull
	 *
	 */
	public static enum AttackType {

		STAB(0),

		SLASH(1),

		CRUSH(2),

		MAGIC(3),

		RANGE(4);

		/**
		 * A map of attack types.
		 */
		private static Map<Integer, AttackType> attackTypes = new HashMap<Integer, AttackType>();

		/**
		 * Gets a attack type by its ID.
		 * 
		 * @param attackType
		 *            The attack type id.
		 * @return The attack type, or <code>null</code> if the id is not a
		 *         attack type.
		 */
		public static AttackType forId(int attackType) {
			return attackTypes.get(attackType);
		}

		/**
		 * Populates the attack type map.
		 */
		static {
			for (AttackType attackType : AttackType.values()) {
				attackTypes.put(attackType.id, attackType);
			}
		}

		/**
		 * The attack type's id.
		 */
		private int id;

		private AttackType(int id) {
			this.id = id;
		}

		/**
		 * Gets the attack types id.
		 * 
		 * @return The attack types id.
		 */
		public int getId() {
			return id;
		}
	}

	/*
	 * Combat attributes.
	 */

	/**
	 * The mob's combat style.
	 */
	private CombatStyle combatStyle = CombatStyle.ACCURATE;

	/**
	 * The mob's attack type.
	 */
	private AttackType attackType;

	/**
	 * The mob's state of life.
	 */
	private boolean isDead;

	/**
	 * The mob's spell book.
	 */
	private int spellBook = 0;

	/**
	 * The current spell this mob is casting.
	 */
	private Spell currentSpell;

	/**
	 * The spell to be performed once our combat cooldown is over.
	 */
	private Spell queuedSpell;

	/**
	 * The mob's poison damage.
	 */
	private int poisonDamage = 0;

	/**
	 * The mob's last hit timer.
	 */
	private long lastHitTimer;

	/**
	 * The mob who last hit this mob.
	 */
	private Mob lastHitBy;

	/**
	 * The delay before you can equip another weapon, used to endGame emotes
	 * overlapping (EG: whip using dds anim).
	 */
	private int weaponSwitchTimer;

	/**
	 * The mob's poisonable flag.
	 */
	private boolean canBePoisoned = true;

	/**
	 * Ring of Recoil use amount.
	 */
	private int ringOfRecoil = 40;

	/**
	 * The movement flag.
	 */
	private boolean canMove = true;

	/**
	 * The frozen flag.
	 */
	private boolean canBeFrozen = true;

	/**
	 * The teleblock flag.
	 */
	private boolean teleblocked;

	/**
	 * The charged flag.
	 */
	private boolean charged;

	/**
	 * The eating flag.
	 */
	private boolean canEat = true;

	/**
	 * The drinking flag.
	 */
	private boolean canDrink = true;

	/**
	 * The animation flag. This flag stops important emotes overlapping each
	 * other, EG: block emote overlapping attack emote.
	 */
	private boolean canAnimate = true;

	/**
	 * The teleport flag.
	 */
	private boolean canTeleport = true;

	/**
	 * Special attack flag.
	 */
	private boolean special = false;

	/**
	 * Special energy amount.
	 */
	private int specialEnergy = 100;

	/**
	 * The active prayers.
	 */
	private boolean[] prayers = new boolean[33];

	/**
	 * The active quick prayers.
	 */
	private boolean[] quickPrayers = new boolean[33];

	/**
	 * The players prayer head icon.
	 */
	private int prayerHeadIcon = -1;

	/**
	 * The mob's bonuses.
	 */
	private int[] bonuses = new int[13];

	/**
	 * The vengeance flag.
	 */
	private boolean vengeance = false;

	/**
	 * The can vengeance flag.
	 */
	private boolean canVengeance = true;

	/**
	 * The spellbook swap flag.
	 */
	private boolean spellbookSwap = false;

	/**
	 * The can spellbook swap flag.
	 */
	private boolean canSpellbookSwap = true;

	/**
	 * The amount of ticks left before this mobs skull is removed.
	 */
	private int skullTicks;

	/**
	 * Sets the mob's combat style.
	 * 
	 * @param combatStyle
	 *            The combat style to set.
	 */
	public void setCombatStyle(CombatStyle combatStyle) {
		this.combatStyle = combatStyle;
	}

	/**
	 * Gets the combat style.
	 * 
	 * @return The combat style.
	 */
	public CombatStyle getCombatStyle() {
		return combatStyle; // TODO
	}

	/**
	 * Sets the mob's attack type.
	 * 
	 * @param attackType
	 *            The attack type to set.
	 */
	public void setAttackType(AttackType attackType) {
		this.attackType = attackType;
	}

	/**
	 * Gets the attack type.
	 * 
	 * @return The attack type.
	 */
	public AttackType getAttackType() {
		return attackType; // TODO
	}

	/**
	 * Gets the mob's state of life.
	 * 
	 * @return The mob's state of life.
	 */
	public boolean isDead() {
		return isDead;
	}

	/**
	 * Sets the mob's state of life.
	 * 
	 * @param isDead
	 *            The state of life to set.
	 */
	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	/**
	 * @return the spellBook
	 */
	public int getSpellBook() {
		return spellBook;
	}

	/**
	 * @param spellBook
	 *            the spellBook to set
	 */
	public void setSpellBook(int spellBook) {
		this.spellBook = spellBook;
	}

	/**
	 * @return the currentSpell
	 */
	public Spell getCurrentSpell() {
		return currentSpell;
	}

	/**
	 * @param currentSpell
	 *            the currentSpell to set
	 */
	public void setCurrentSpell(Spell currentSpell) {
		this.currentSpell = currentSpell;
	}

	/**
	 * @return the queuedSpell
	 */
	public Spell getQueuedSpell() {
		return queuedSpell;
	}

	/**
	 * @param queuedSpell
	 *            the queuedSpell to set
	 */
	public void setQueuedSpell(Spell queuedSpell) {
		if (queuedSpell == null)
			mob.removeAttribute("magicMove");
		this.queuedSpell = queuedSpell;
	}

	/**
	 * @return the poisonDamage
	 */
	public int getPoisonDamage() {
		return poisonDamage;
	}

	/**
	 * @param poisonDamage
	 *            the poisonDamage to set
	 */
	public void setPoisonDamage(int poisonDamage, Mob attacker) {
		if (mob == null)
			return;
		if (mob.isPlayer() && (mob.getEquipment() != null && Constants.hasSerpHelm(mob)))
			return;
		this.poisonDamage = poisonDamage;
		if (mob.getPoisonDrainTick() == null && poisonDamage > 0) {
			mob.setPoisonDrainTick(new PoisonDrainTick(mob));
			World.getWorld().submit(mob.getPoisonDrainTick());
		} else if (mob.getPoisonDrainTick() != null && poisonDamage < 1) {
			mob.getPoisonDrainTick().stop();
			mob.setPoisonDrainTick(null);
		}
	}

	/**
	 * @param poisonDamage
	 *            the poisonDamage to set
	 */
	public void decreasePoisonDamage(int poisonDamage) {
		this.poisonDamage -= poisonDamage;
		// if (mob.isPlayer()) {
		// mob.getActionSender().sendConfig(102, this.poisonDamage > 0 ? 1 : 0);
		// }
		if (mob.getPoisonDrainTick() != null && this.poisonDamage < 1) {
			mob.getPoisonDrainTick().stop();
			mob.setPoisonDrainTick(null);
		}
	}

	/**
	 * @return the canBePoisoned
	 */
	public boolean canBePoisoned() {
		return canBePoisoned;
	}

	/**
	 * @param canBePoisoned
	 *            the canBePoisoned to set
	 */
	public void setCanBePoisoned(boolean canBePoisoned) {
		this.canBePoisoned = canBePoisoned;
	}

	/**
	 * @return the ringOfRecoil
	 */
	public int getRingOfRecoil() {
		return ringOfRecoil;
	}

	/**
	 * @param ringOfRecoil
	 *            the ringOfRecoil to set
	 */
	public void setRingOfRecoil(int ringOfRecoil) {
		this.ringOfRecoil = ringOfRecoil;
	}

	/**
	 * @return the lastHitTimer
	 */
	public long getLastHitTimer() {
		return lastHitTimer;
	}

	/**
	 * @param lastHitTimer
	 *            the lastHitTimer to set
	 */
	public void setLastHitTimer(long lastHitTimer) {
		this.lastHitTimer = lastHitTimer + System.currentTimeMillis();
	}

	/**
	 * @return the lastHitBy
	 */
	public Mob getLastHitBy() {
		return lastHitBy;
	}

	/**
	 * @param lastHitBy
	 *            the lastHitBy to set
	 */
	public void setLastHitBy(Mob lastHitBy) {
		this.lastHitBy = lastHitBy;
	}

	/**
	 * @return the weaponSwitchTimer
	 */
	public int getWeaponSwitchTimer() {
		return weaponSwitchTimer;
	}

	/**
	 * @param weaponSwitchTimer
	 *            the weaponSwitchTimer to set
	 */
	public void setWeaponSwitchTimer(int weaponSwitchTimer) {
		this.weaponSwitchTimer = weaponSwitchTimer;
	}

	/**
	 * @param weaponSwitchTimer
	 *            the weaponSwitchTimer to set
	 */
	public void decreaseWeaponSwitchTimer(int weaponSwitchTimer) {
		this.weaponSwitchTimer -= weaponSwitchTimer;
	}

	/**
	 * Sets the players prayer head icon.
	 * 
	 * @param prayerHeadIcon
	 *            The prayer head icon to set.
	 */
	public void setPrayerHeadIcon(int prayerHeadIcon) {
		this.prayerHeadIcon = prayerHeadIcon;
		mob.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	/**
	 * Gets the players prayer head icon.
	 * 
	 * @return The players prayer head icon.
	 */
	public int getPrayerHeadIcon() {
		return prayerHeadIcon;
	}

	/**
	 * Resets all the players prayers.
	 */
	public void resetPrayers() {
		for (int i = 0; i < prayers.length; i++)
			prayers[i] = false;
		if (mob.getActionSender() != null)
			Prayers.refresh((Player) mob);
		setPrayerHeadIcon(-1);
		if (mob.getPrayerUpdateTick() != null) {
			mob.getPrayerUpdateTick().stop();
			mob.setPrayerUpdateTick(null);
		}
	}

	/**
	 * Resets all the players quick-prayers.
	 */
	public void resetQuickPrayers() {
		for (int i = 0; i < quickPrayers.length; i++)
			quickPrayers[i] = false;
		if (mob.getActionSender() != null)
			Prayers.refreshQuickPrayers((Player) mob);
		setPrayerHeadIcon(-1);
		if (mob.getPrayerUpdateTick() != null) {
			mob.getPrayerUpdateTick().stop();
			mob.setPrayerUpdateTick(null);
		}
	}

	/**
	 * @return the canMove
	 */
	public boolean canMove() {
		return canMove;
	}

	/**
	 * @param canMove
	 *            the canMove to set
	 */
	public void setCanMove(boolean canMove) { 
		this.canMove = canMove;
	}

	/**
	 * @return the canBeFrozen
	 */
	public boolean canBeFrozen() {
		return canBeFrozen;
	}

	/**
	 * @param canBeFrozen
	 *            the canBeFrozen to set
	 */
	public void setCanBeFrozen(boolean canBeFrozen) {
		this.canBeFrozen = canBeFrozen;
	}

	/**
	 * @return the isTeleblocked
	 */
	public boolean isTeleblocked() {
		return teleblocked;
	}

	/**
	 * @param teleblocked
	 *            the teleblocked to set
	 */
	public void setTeleblocked(boolean teleblocked) {
		this.teleblocked = teleblocked;
	}

	/**
	 * @return the charged
	 */
	public boolean isCharged() {
		return charged;
	}

	/**
	 * @param charged
	 *            the charged to set
	 */
	public void setCharged(boolean charged) {
		this.charged = charged;
	}

	/**
	 * @param charged
	 *            the charged to set
	 */
	public void setCharged(int ticks) {
		this.charged = true;
		World.getWorld().submit(new Tickable(100 * 7) {
			@Override
			public void execute() {
				charged = false;
				if (mob.getActionSender() != null) {
					mob.getActionSender().sendMessage("Your magical charge fades away.");
				}
			}
		});
	}

	/**
	 * Sets the mob's eating flag.
	 * 
	 * @param canEat
	 *            The eating flag to set.
	 */
	public void setCanEat(boolean canEat) {
		this.canEat = canEat;
	}

	/**
	 * Gets the mob's eating flag.
	 * 
	 * @return The mob's eating flag.
	 */
	public boolean canEat() {
		return canEat;
	}

	/**
	 * Sets the mob's drinking flag.
	 * 
	 * @param canEat
	 *            The drinking flag to set.
	 */
	public void setCanDrink(boolean canDrink) {
		this.canDrink = canDrink;
	}

	/**
	 * Gets the mob's drinking flag.
	 * 
	 * @return The mob's drinking flag.
	 */
	public boolean canDrink() {
		return canDrink;
	}

	/**
	 * Sets the mob's animation flag.
	 * 
	 * @param canAnimate
	 *            The animation flag to set.
	 */
	public void setCanAnimate(boolean canAnimate) {
		this.canAnimate = canAnimate;
	}

	/**
	 * Gets the mob's animation flag.
	 * 
	 * @return The mob's animation flag.
	 */
	public boolean canAnimate() {
		return canAnimate;
	}

	/**
	 * @return the canTeleport
	 */
	public boolean canTeleport() {
		return canTeleport;
	}

	/**
	 * @param canTeleport
	 *            the canTeleport to set
	 */
	public void setCanTeleport(boolean canTeleport) {
		this.canTeleport = canTeleport;
	}

	/**
	 * Gets the special attack flag.
	 * 
	 * @return The special attack flag.
	 */
	public boolean isSpecialOn() {
		return special;
	}

	/**
	 * Inverses the special attack flag.
	 */
	public void inverseSpecial() {  
		this.special = !this.special;
		if (mob.getActionSender() != null)
			mob.getActionSender().sendConfig(301, isSpecialOn() ? 1 : 0);
	}

	/**
	 * Sets the special attack flag.
	 * 
	 * @param special
	 *            The special attack flag to set.
	 */
	public void setSpecial(boolean special) {
		this.special = special;
		if (mob.getActionSender() != null) {
			mob.getActionSender().sendConfig(301, isSpecialOn() ? 1 : 0);
		}
	}

	/**
	 * Increases the special energy amount.
	 * 
	 * @param amount
	 *            The amount to increase by.
	 */
	public void increaseSpecial(int amount) {
		if (amount > (100 - this.specialEnergy))
			amount = 100 - this.specialEnergy;
		this.specialEnergy += amount;
		if (mob.getActionSender() != null)
			mob.getActionSender().updateSpecialConfig();
	}

	/**
	 * Decreases the special energy amount.
	 * 
	 * @param amount
	 *            The amount to decrease by.
	 */
	public void decreaseSpecial(int amount) {
		if (amount > specialEnergy) {
			amount = specialEnergy;
		}
		this.specialEnergy -= amount;
		if (this.specialEnergy < 100 && mob.getSpecialUpdateTick() == null) {
			mob.setSpecialUpdateTick(new SpecialEnergyRestoreTick(mob));
			World.getWorld().submit(mob.getSpecialUpdateTick());
		}
		if (mob.getActionSender() != null) {
			mob.getActionSender().updateSpecialConfig();
		}
	}

	/**
	 * Gets the special energy amount.
	 * 
	 * @return The special energy amount.
	 */
	public int getSpecialEnergy() {
		return specialEnergy;
	}

	/**
	 * Sets the special energy amount.
	 * 
	 * @param specialEnergy
	 *            The special energy amount to set.
	 */
	public void setSpecialEnergy(int specialEnergy) {
		/**
		 * Indicate the special energy has decreased and needs refilling.
		 */
		if (specialEnergy < this.specialEnergy && specialEnergy < 100) {
			if (mob.getSpecialUpdateTick() == null) {
				mob.setSpecialUpdateTick(new SpecialEnergyRestoreTick(mob));
				World.getWorld().submit(mob.getSpecialUpdateTick());
			}
		} else if (specialEnergy > 99) {
			if (mob.getSpecialUpdateTick() != null) {
				mob.getSpecialUpdateTick().stop();
				mob.setSpecialUpdateTick(null);
			}
		}
		this.specialEnergy = specialEnergy;
	}

	/**
	 * @return the prayers
	 */
	public boolean[] getPrayers() {
		return prayers;
	}

	/**
	 * @param index
	 * @return the prayers
	 */
	public boolean getPrayer(int index) {
		return prayers[index];
	}

	/**
	 * @param prayers
	 *            the prayers to set
	 */
	public void setPrayers(boolean[] prayers) {
		this.prayers = prayers;
	}

	/**
	 * Sets a prayer by its index.
	 * 
	 * @param index
	 *            The index.
	 * @param prayer
	 *            The flag.
	 */
	public void setPrayer(int index, boolean prayer) {
		this.prayers[index] = prayer;
	}

	/**
	 * @return the quick-prayers
	 */
	public boolean[] getQuickPrayers() {
		return quickPrayers;
	}

	/**
	 * @param index
	 * @return the quick prayers
	 */
	public boolean getQuickPrayer(int index) {
		return quickPrayers[index];
	}

	/**
	 * Sets a quick prayer by its index.
	 * 
	 * @param index
	 *            The index.
	 * @param prayer
	 *            The flag.
	 */
	public void setQuickPrayer(int index, boolean prayer) {
		this.quickPrayers[index] = prayer;
	}

	/**
	 * Sets one of the mob's bonuses.
	 * 
	 * @param index
	 *            The bonus index.
	 * @param amount
	 *            The bonus to set.
	 */
	public void setBonus(int index, int amount) {
		bonuses[index] = amount;
	}

	/**
	 * Sets one of the mob's bonuses.
	 * 
	 * @param index
	 *            The bonus index.
	 * @param fletchAmount
	 *            The bonus to set.
	 */
	public void setBonuses(int[] bonuses) {
		this.bonuses = bonuses;
	}

	/**
	 * Resets the mob's bonuses.
	 */
	public void resetBonuses() {
		bonuses = new int[13];
	}

	/**
	 * Gets the mob's bonuses.
	 * 
	 * @return The mob's bonuses.
	 */
	public int[] getBonuses() {
		return bonuses;
	}

	/**
	 * Gets a bonus by its index.
	 * 
	 * @param index
	 *            The bonus index.
	 * @return The bonus.
	 */
	public int getBonus(int index) {
		return bonuses[index];
	}

	/**
	 * Calculates the bonuses.
	 */
	public void calculateBonuses() {
		resetBonuses();
		for (Item item : mob.getEquipment().toArray()) {
			if (item != null && item.getEquipmentDefinition() != null) {
				for (int i = 0; i < item.getEquipmentDefinition().getBonuses().length; i++) {
					setBonus(i, getBonus(i) + item.getEquipmentDefinition().getBonus(i));
				}
			}
		}
	}

	/**
	 * @return the vengeance
	 */
	public boolean hasVengeance() {
		return vengeance;
	}

	/**
	 * @param vengeance
	 *            the vengeance to set
	 */
	public void setVengeance(boolean vengeance) {
		this.vengeance = vengeance;
	}

	/**
	 * @return the canVengeance
	 */
	public boolean canVengeance() {
		return canVengeance;
	}

	/**
	 * @param canVengeance
	 *            the canVengeance to set
	 */
	public void setCanVengeance(boolean canVengeance) {
		this.canVengeance = canVengeance;
	}

	/**
	 * @param canVengeance
	 *            the canVengeance to set
	 */
	public void setCanVengeance(int ticks) {
		World.getWorld().submit(new Tickable(ticks) {
			@Override
			public void execute() {
				canVengeance = true;
				this.stop();
			}
		});
	}

	/**
	 * @return The spellbookSwap.
	 */
	public boolean spellbookSwap() {
		return spellbookSwap;
	}

	/**
	 * @param spellbookSwap
	 *            The spellbookSwap to set.
	 */
	public void setSpellbookSwap(boolean spellbookSwap) {
		this.spellbookSwap = spellbookSwap;
	}

	/**
	 * @return The canSpellbookSwap.
	 */
	public boolean canSpellbookSwap() {
		return canSpellbookSwap;
	}

	/**
	 * @param canSpellbookSwap
	 *            The canSpellbookSwap to set.
	 */
	public void setCanSpellbookSwap(boolean canSpellbookSwap) {
		this.canSpellbookSwap = canSpellbookSwap;
	}

	/**
	 * @param canSpellbookSwap
	 *            The canSpellbookSwap to set.
	 */
	public void setCanSpellbookSwap(int ticks) {
		World.getWorld().submit(new Tickable(ticks) {
			@Override
			public void execute() {
				canSpellbookSwap = true;
			}
		});
	}

	/**
	 * @return the skullTicks
	 */
	public int getSkullTicks() {
		return skullTicks;
	}

	/**
	 * @param skullTicks
	 *            the skullTicks to set
	 */
	public void setSkullTicks(int skullTicks) {
		this.skullTicks = skullTicks;
		if (skullTicks > 0) {
			mob.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
	}

	/**
	 * @param skullTicks
	 *            the skullTicks to set
	 */
	public void decreaseSkullTicks(int skullTicks) {
		this.skullTicks -= skullTicks;
		if (skullTicks < 1) {
			mob.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
	}

	private Mob attackerWith;
	private long lastCombatWith;

	private Food lastAte;

	public void updateCombatWith(Mob attacker) {
		if (mob.isPlayer() && attacker.isPlayer()) {
			Player player = (Player) mob;
			Player player2 = (Player) attacker;
			if (player.getBounty() != null && player.getBountyTarget() != null && player.getBountyTarget() == player2) {
				return;
			}
			if (player2.getBounty() != null && player2.getBountyTarget() != null
					&& player2.getBountyTarget() == player) {
				return;
			}
		}
		if (attacker != attackerWith) {
			attackerWith = attacker;
		}
		lastCombatWith = System.currentTimeMillis();
	}

	public long getLastCombatWith() {
		return lastCombatWith;
	}

	public Mob getLastAttackerWith() {
		return attackerWith;
	}

	public Food getLastAte() {
		return lastAte;
	}

	public void setLastAte(Food lastAte) {
		this.lastAte = lastAte;
	}
}
