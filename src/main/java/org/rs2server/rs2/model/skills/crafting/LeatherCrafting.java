package org.rs2server.rs2.model.skills.crafting;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.action.impl.SkillAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.Misc;
import org.rs2server.util.CycleState;

import java.util.HashMap;
import java.util.Map;

public class LeatherCrafting {

	public static final Animation CRAFT_ANIMATION = Animation.create(1249);

	public static Item NEEDLE = new Item(1733);
	public static Item THREAD = new Item(1734);

	public static final LeatherProduction[] NORMAL_LEATHER_PRODUCTIONS = { LeatherProduction.LEATHER_GLOVES,
			LeatherProduction.LEATHER_BOOTS, LeatherProduction.LEATHER_COWL, LeatherProduction.LEATHER_VAMBRACES,
			LeatherProduction.LEATHER_BODY, LeatherProduction.LEATHER_CHAPS, LeatherProduction.COIF };

	public static final LeatherProduction[] HARD_LEATHER_PRODUCTIONS = { LeatherProduction.HARD_LEATHER_BODY };

	public static final LeatherProduction[] GREEN_DRAGONHIDE_PRODUCTIONS = { LeatherProduction.GREEN_D_HIDE_BODY,
			LeatherProduction.GREEN_D_HIDE_VAMB, LeatherProduction.GREEN_D_HIDE_CHAPS };

	public static final LeatherProduction[] BLUE_DRAGONHIDE_PRODUCTIONS = { LeatherProduction.BLUE_D_HIDE_BODY,
			LeatherProduction.BLUE_D_HIDE_VAMB, LeatherProduction.BLUE_D_HIDE_CHAPS };

	public static final LeatherProduction[] RED_DRAGONHIDE_PRODUCTIONS = { LeatherProduction.RED_D_HIDE_BODY,
			LeatherProduction.RED_D_HIDE_VAMB, LeatherProduction.RED_D_HIDE_CHAPS };

	public static final LeatherProduction[] BLACK_DRAGONHIDE_PRODUCTIONS = { LeatherProduction.BLACK_D_HIDE_BODY,
			LeatherProduction.BLACK_D_HIDE_VAMB, LeatherProduction.BLACK_D_HIDE_CHAPS };

	public static final LeatherProduction[] XERICIAN_PRODUCTIONS = { LeatherProduction.XERICIAN_HAT,
			LeatherProduction.XERICIAN_ROBE, LeatherProduction.XERICIAN_TOP };

	public enum Leather {

		NORMAL_LEATHER(1, new Item(1741), LeatherCrafting.NORMAL_LEATHER_PRODUCTIONS, "leather"),

		XERICIAN(14, new Item(13383), LeatherCrafting.XERICIAN_PRODUCTIONS, "xerician"),

		HARD_LEATHER(28, new Item(1743), LeatherCrafting.HARD_LEATHER_PRODUCTIONS, "hardLeather"),

		GREEN_D_HIDE(57, new Item(1745), LeatherCrafting.GREEN_DRAGONHIDE_PRODUCTIONS, "green d'hide"),

		BLUE_D_HIDE(66, new Item(2505), LeatherCrafting.BLUE_DRAGONHIDE_PRODUCTIONS, "blue d'hide"),

		RED_D_HIDE(73, new Item(2507), LeatherCrafting.RED_DRAGONHIDE_PRODUCTIONS, "red d'hide"),

		BLACK_D_HIDE(79, new Item(2509), LeatherCrafting.BLACK_DRAGONHIDE_PRODUCTIONS, "black d'hide");

		private LeatherProduction[] possibleProductions;
		private int levelReq;
		private Item leatherItem;
		private String type;

		private Leather(int levelReq, Item leatherItem, LeatherProduction[] possibleProductions, String type) {
			this.levelReq = levelReq;
			this.leatherItem = leatherItem;
			this.possibleProductions = possibleProductions;
			this.type = type;
			for (LeatherProduction productable : possibleProductions) {
				productable.setLeather(this);
			}
		}

		private static Map<Integer, Leather> leatherTypes = new HashMap<Integer, Leather>();

		public static Leather forId(int id) {
			return leatherTypes.get(id);
		}

		static {
			for (Leather leather : Leather.values()) {
				leatherTypes.put(leather.leatherItem.getId(), leather);
			}
		}

	}

	public enum LeatherChilds {

		LEATHER_GLOVES(LeatherProduction.LEATHER_GLOVES, new int[] { 127, 126, 116, 108 }, 1059, 13.75, 1),

		LEATHER_BOOTS(LeatherProduction.LEATHER_BOOTS, new int[] { 129, 128, 117, 109 }, 1061, 16.25, 7),

		LEATHER_COWL(LeatherProduction.LEATHER_COWL, new int[] { 137, 136, 121, 113 }, 1167, 18.5, 9),

		LEATHER_VAMBRACES(LeatherProduction.LEATHER_VAMBRACES, new int[] { 131, 130, 118, 110 }, 1063, 22.0, 11),

		LEATHER_BODY(LeatherProduction.LEATHER_BODY, new int[] { 125, 124, 115, 114 }, 1129, 25.0, 14),

		LEATHER_CHAPS(LeatherProduction.LEATHER_CHAPS, new int[] { 133, 132, 119, 111 }, 1095, 27.0, 18),

		COIF(LeatherProduction.COIF, new int[] { 135, 134, 120, 112 }, 1169, 37.0, 38);

		private LeatherProduction leatherProduction;
		private int[] child;

		LeatherChilds(LeatherProduction leatherProduction, int[] child, int itemId, double experience,
				int levelRequired) {
			this.leatherProduction = leatherProduction;
			this.child = child;
		}

		public LeatherProduction getLeatherProduction() {
			return leatherProduction;
		}

		public int[] getChild() {
			return child;
		}

		public int getAmount(int child) {
			switch (child) {
			case 127:
				return 1;
			case 126:
				return 5;
			case 116:
				return -1;
			case 108:
				return 28;

			case 129:
				return 1;
			case 128:
				return 5;
			case 117:
				return -1;
			case 109:
				return 28;

			case 137:
				return 1;
			case 136:
				return 5;
			case 121:
				return -1;
			case 113:
				return 28;

			case 131:
				return 1;
			case 130:
				return 5;
			case 118:
				return -1;
			case 110:
				return 28;

			case 125:
				return 1;
			case 124:
				return 5;
			case 115:
				return -1;
			case 114:
				return 28;

			case 133:
				return 1;
			case 132:
				return 5;
			case 119:
				return -1;
			case 111:
				return 28;

			case 135:
				return 1;
			case 134:
				return 5;
			case 120:
				return -1;
			case 112:
				return 28;
			}
			return 0;
		}

		private static Map<Integer, LeatherChilds> hides = new HashMap<Integer, LeatherChilds>();

		public static LeatherChilds forId(int child) {
			return hides.get(child);
		}

		static {
			for (LeatherChilds hide : LeatherChilds.values()) {
				for (int childId : hide.child) {
					hides.put(childId, hide);
				}
			}
		}
	}

	public enum LeatherProduction {

		LEATHER_GLOVES(1059, 1, 13.75, 1),

		LEATHER_BOOTS(1061, 1, 16.25, 7),

		LEATHER_COWL(1167, 1, 18.5, 9),

		LEATHER_VAMBRACES(1063, 1, 22.0, 11),

		LEATHER_BODY(1129, 1, 25.0, 14),

		LEATHER_CHAPS(1095, 1, 27.0, 18),

		XERICIAN_HAT(13385, 3, 66.0, 14),

		XERICIAN_ROBE(13389, 4, 88.0, 17),

		XERICIAN_TOP(13387, 5, 110.0, 22),

		COIF(1169, 1, 37.0, 38),

		GREEN_D_HIDE_VAMB(1065, 1, 62, 57),

		GREEN_D_HIDE_CHAPS(1099, 2, 124, 60),

		GREEN_D_HIDE_BODY(1135, 3, 186, 63),

		BLUE_D_HIDE_VAMB(2487, 1, 70, 66),

		BLUE_D_HIDE_CHAPS(2493, 2, 140, 68),

		BLUE_D_HIDE_BODY(2499, 3, 210, 71),

		RED_D_HIDE_VAMB(2489, 1, 78, 73),

		RED_D_HIDE_CHAPS(2495, 2, 156, 75),

		RED_D_HIDE_BODY(2501, 3, 234, 77),

		BLACK_D_HIDE_VAMB(2491, 1, 86, 79),

		BLACK_D_HIDE_CHAPS(2497, 2, 172, 82),

		BLACK_D_HIDE_BODY(2503, 3, 258, 84),

		HARD_LEATHER_BODY(1131, 1, 35, 28);

		private int itemId;
		private int amount;
		private double experience;
		private int levelRequired;
		private boolean isPair, pairSet;
		private boolean coif;

		private CacheItemDefinition def;
		private String basicName;

		private Leather leather;

		private LeatherProduction(int itemId, int amount, double experience, int levelRequired) {
			this.itemId = itemId;
			this.amount = amount;
			this.experience = experience;
			this.levelRequired = levelRequired;
			this.coif = toString().equals("COIF");
		}

		public void setLeather(Leather leather) {
			this.leather = leather;
		}

		public int getRequiredLevel() {
			return levelRequired;
		}

		public boolean isPair() {
			if (!pairSet) {
				setDef();
				String name = def.getName().toLowerCase();
				pairSet = true;
				isPair = !name.contains("body") && !name.contains("coif") && !name.contains("cowl");
			}
			return isPair;
		}

		private void setDef() {
			if (def == null) {
				def = CacheItemDefinition.get(itemId);
			}
		}

		public String getName() {
			setDef();
			return def.getName();
		}

		public String getBasicName() {
			setDef();
			if (basicName == null) {
				String newString = def.getName().toLowerCase().replace(getType(), "");
				newString = newString.substring(coif ? 0 : 1);
				StringBuilder sb = new StringBuilder("");
				if (getType().contains(" ")) {
					sb.append(getType().split(" ")[0]).append(" ").append(newString);
				} else {
					sb.append(newString);
				}
				basicName = Misc.upperFirst(sb.toString());
			}
			return basicName;
		}

		public String getType() {
			return leather.type;
		}
	}

	public static boolean handleItemOnItem(Player player, Item itemUsed, Item usedWith) {
		Item item;
		if (itemUsed.getId() == NEEDLE.getId()) {
			item = usedWith;
		} else if (usedWith.getId() == NEEDLE.getId()) {
			item = itemUsed;
		} else {
			return false;
		}
		Leather leather = Leather.forId(item.getId());
		if (leather != null) {
			if (leather == Leather.NORMAL_LEATHER) {
				player.getActionSender().sendCraftingInterface();
				player.setAttribute("leather_crafting", Boolean.TRUE);
			} else {
				if (leather == Leather.HARD_LEATHER) {
					LeatherProduction leatherProduction = leather.possibleProductions[0];
					player.getActionSender().sendChatboxInterface(309);
					player.getActionSender().sendItemOnInterface(309, 2, item.getId(), 160);
					player.getActionSender().sendString(309, 6,
							"<br><br><br><br>" + CacheItemDefinition.get(leatherProduction.itemId).getName());
					player.setAttribute("craftingType", 2);
					player.setAttribute("dhide_type", leather);
					//player.setPossibleProductions(productionsToIntArray(leather.possibleProductions));
				} else {
					for (int index = 0; index < leather.possibleProductions.length; index++) {
						LeatherProduction leatherProduction = leather.possibleProductions[index];
						if (player.getSkills().getLevel(Skills.CRAFTING) < leather.levelReq) {
							return false;
						}
						player.getActionSender().sendItemOnInterface(304, 2 + index, leatherProduction.itemId, 175);
						player.getActionSender().sendString(304, (index + 2) * 4,
								"<br><br><br><br>" + leatherProduction.getName());
					}
					player.getActionSender().sendInterface(162, 546, 304, false);
					player.getActionSender().sendString(304, 17, "What would you like to make?");
					player.setAttribute("craftingType", 2);
					player.setAttribute("dhide_type", leather);
					//player.setPossibleProductions(productionsToIntArray(leather.possibleProductions));
				}
				return true;
			}
		}
		return false;
	}

	public static int[] productionsToIntArray(LeatherProduction[] productions) {
		int[] newArray = new int[productions.length];
		for (int i = 0; i < productions.length; i++) {
			newArray[i] = productions[i].ordinal();
		}
		return newArray;
	}

	public static class LeatherProductionAction extends SkillAction {

		private LeatherProduction toProduce;
		private int amount;

		public LeatherProductionAction(Player player, LeatherProduction toProduce, int amount) {
			super(player);
			this.toProduce = toProduce;
			this.amount = amount;
		}

		@Override
		public boolean commence(Player player) {
			if (player.getSkills().getLevel(Skills.CRAFTING) < toProduce.getRequiredLevel()) {
				StringBuilder sb = new StringBuilder("You need a Crafting level of ")
						.append(toProduce.getRequiredLevel());
				if (!toProduce.isPair()) {
					sb.append(" to make a ").append(toProduce.getName().toLowerCase()).append(".");
				} else {
					sb.append(" to make a pair of ").append(toProduce.getType());
					sb.append("<br>").append(toProduce.getName().toLowerCase().replaceAll(toProduce.getType(), ""));
				}
				player.getActionSender().sendMessage(sb.toString());
				return false;
			}
			if (!player.getInventory().contains(THREAD.getId())) {
				player.getActionSender().sendMessage("You have no threads to craft with!");
				return false;
			}

			player.getActionSender().removeInterface();
			setTickDelay(2);
			return true;
		}

		@Override
		public boolean execute(Player player) {
			if (amount > 0) {
				if (!player.getInventory().contains(THREAD.getId())) {
					amount = 0;
					player.getActionSender().sendMessage("You have run out of threads!");
					return false;
				} else if (!player.getInventory().contains(NEEDLE.getId())) {
					amount = 0;
					player.getActionSender().sendMessage("You don't have a needle to craft with!");
					return false;
				} else if (player.getInventory().getCount(toProduce.leather.leatherItem.getId()) < toProduce.amount) {
					amount = 0;
					player.getActionSender().sendMessage("You have ran out of " + toProduce.getType() + ".");
					return false;
				}
				player.playAnimation(CRAFT_ANIMATION);
			}
			return true;
		}

		@Override
		public boolean finish(Player player) {
			if (amount < 1) {
				return true;
			}
			amount--;
			if (player.getRandom().nextDouble() >= 0.80) {
				player.getActionSender().sendMessage("You use up one of your reels of thread.");
				player.getInventory().remove(THREAD);
			}
			if (player.getRandom().nextDouble() >= 0.97) {
				player.getActionSender().sendMessage("Your needle broke.");
				player.getInventory().remove(NEEDLE);
			}
			StringBuilder createdMessage = new StringBuilder("You make a ");
			if (!toProduce.isPair() && toProduce.getName().endsWith("s")) {
				createdMessage.append(toProduce.getName().substring(0, toProduce.getName().length() - 1));
			} else {
				if (toProduce.isPair()) {
					createdMessage.append("pair of ").append(toProduce.getName().toLowerCase());
				} else {
					createdMessage.append(toProduce.getName().toLowerCase());
				}
			}
			createdMessage.append(".");
			player.getActionSender().sendMessage(createdMessage.toString());
			player.getInventory().remove(new Item(toProduce.leather.leatherItem.getId(), toProduce.amount));
			player.getInventory().add(new Item(toProduce.itemId));
			player.getSkills().addExperience(Skills.CRAFTING, toProduce.experience * 3);
			setCycleState(CycleState.EXECUTE);
			return false;
		}

	}

}
