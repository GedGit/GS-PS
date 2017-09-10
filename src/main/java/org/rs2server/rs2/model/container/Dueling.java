package org.rs2server.rs2.model.container;

import org.rs2server.rs2.model.container.Equipment.EquipmentType;
import org.rs2server.rs2.model.player.Player;


public class Dueling {

	public int duelStatus = 0;
	public int duelSpaceReq;
	
	/**
	 * The is where we keep all our rule constants.
	 */
	public static final int NO_FORFEIT = 0;
	public static final int NO_MOVEMENT = 1;
	public static final int NO_RANGE = 2;
	public static final int NO_MELEE = 3;
	public static final int NO_MAGIC = 4;
	public static final int NO_DRINKS = 5;
	public final static int NO_FOOD = 6;
	public final static int NO_PRAYER = 7;
	public static final int OBSTACLES = 8;
	public static final int FUN_WEAPONS = 9;
	public static final int NO_SPECIAL_ATTACKS = 10;
	public static final int NO_HATS = 11;
	public static final int NO_CAPES = 12;
	public static final int NO_AMULETS = 13;
	public static final int NO_SWORDS = 14;
	public static final int NO_BODIES = 15;
	public static final int NO_SHIELDS = 16;
	public static final int NO_LEGS = 17;
	public static final int NO_GLOVES = 18;
	public static final int NO_BOOTS = 19;
	public static final int NO_RINGS = 20;
	public static final int NO_ARROWS = 21;

	public int totalDuelConfigs = 0;
	
	/**
	 * Defines if a rule is set to on or off.
	 */
	public boolean[] rules = new boolean[22];
	
	public final int[] DURING_THE_DUEL_CHILD_IDS = {40, 41, 42, 43, 45, 46, 47, 48, 50, 51, 52};
	
	public final int[] BEFORE_THE_DUEL_STARTS_CHILD_IDS = {49, 34, 35, 37, 38};
	
	public final String[] RULES = {"You cannot forfeit the duel.", "You cannot move.", "You cannot use ranged attacks.", 
	"You cannot use melee attacks.", "You cannot use magic attacks.", "You cannot use drinks.", "You cannot use food.", 
	"You cannot use prayer.", "There will be obstacles in the arena.", "You cannot use drinks.", 
	"You cannot use special attacks."};
	
	public final String[] BEFORE_THE_DUEL_STARTS = {"Some user items will be taken off.", "Boosted stats will be restored.", 
	"Existing prayers will be stopped.", "", ""};
	
	public final int[] DUELING_CONFIG_IDS = { 1, 2, 16, 32, 64, 128,
		256, 512, 1024, 4096, 8192, 16384, 32768, 65536, 131072, 262144,
		524288, 2097152, 8388608, 16777216, 67108864, 134217728, 268435456 };
	
	public final int[] RULE_IDS = { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5,
		6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
		20, 21 };
	public final int[] DUEL_SLOT_IDS = { Equipment.SLOT_HELM,
		Equipment.SLOT_CAPE, Equipment.SLOT_AMULET, Equipment.SLOT_WEAPON,
		Equipment.SLOT_CHEST, Equipment.SLOT_SHIELD,
		Equipment.SLOT_BOTTOMS, Equipment.SLOT_GLOVES,
		Equipment.SLOT_BOOTS, Equipment.SLOT_RING, Equipment.SLOT_ARROWS,
	};
	
	public final int[] DUELING_BUTTON_IDS = { 124, 76, 132, 80, 126,
		71, 127, 72, 125, 73, 129, 77, 130, 78, 131, 79, 154, 81,
		156, 75, 159, 74, 113, 114, 115, 117, 118, 119, 120, 123, 122,
		121, 116 };
	
	private Player player;
	private Player opponent;
	private boolean died;
	
	public Dueling(Player player, Player opponent) {
		this.setPlayer(player);
		this.setOpponent(opponent);
		this.died = false;
	}
	
	
	public void setDuelStatus(int status) {
		this.duelStatus = status;
	}
	
	public int getDuelStatus() {
		return this.duelStatus;
	}

	public Player getOpponent() {
		return opponent;
	}

	public void setOpponent(Player opponent) {
		this.opponent = opponent;
	}

	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	/**
	 * Checks if we can wear a specific item during this duel.
	 * @param player The player attempting to wear an item.
	 * @param type The equipment type.
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	public boolean canUseItem(Player player, EquipmentType type) {
		/*
		 * We loop through all equipment rules.
		 */
		for(int rule = 11; rule < player.getDueling().rules.length; rule++) {
			/*
			 * Make sure the rule applies.
			 */
			if(player.getDueling().rules[rule]) {
				/*
				 * If that is so, we get the equipment slot.
				 */
				int slot = player.getDueling().DUEL_SLOT_IDS[rule - 11];
				/*
				 * Check if the item we're about to wear is heading for this slot..
				 */
				if(slot == type.getSlot()) {
					/*
					 * If so, we get the EquipmentType description..
					 */
					String desc = type.getDescription().toLowerCase();
					/*
					 * Modify it slightly..
					 */
					if(!desc.endsWith("s")) {
						desc += "s";
					}
					/*
					 * Notify the player, and return false.
					 */
					player.getActionSender().sendMessage("Wearing " + desc + " has been disabled during this duel.");
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * Defines if a specific rule is toggled.
	 * @param rule The rule toggled.
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	public boolean isRuleToggled(int rule) {
		return rules[rule];
	}


	public boolean isDead() {
		return died;
	}


	public void setDied(boolean died) {
		this.died = died;
	}
}
