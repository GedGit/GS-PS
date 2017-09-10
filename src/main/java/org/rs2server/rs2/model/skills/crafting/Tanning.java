package org.rs2server.rs2.model.skills.crafting;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;

import java.util.HashMap;
import java.util.Map;

/*
 * Author Tim<Clank.1337>
 */
public class Tanning {
	
	public enum Hides {
		LEATHER(new int[]{148, 140, 132, 124}, 1739, 1741, 1, 1),
		
		HARD_LEATHER(new int[]{149, 141, 133, 125}, 1739, 1743, 28, 3),
		
		SNAKESKIN_LIGHT(new int[]{150, 142, 134, 126}, 7801, 6289, 45, 20),
		
		SNAKESKIN_DARK(new int[]{151, 143, 135, 127}, 6287, 6289, 45, 15),
		
		GREEN_DHIDE(new int[]{152, 144, 136, 128}, 1753, 1745, 57, 20),
		
		BLUE_DHIDE(new int[]{153, 145, 137, 129}, 1751, 2505, 66, 20),
		
		RED_DHIDE(new int[]{154, 146, 138, 130}, 1749, 2507, 73, 20),
		
		BLACK_DHIDE(new int[]{155, 147, 139, 131}, 1747, 2509, 79, 20);
		
		int[] childIds;
		int leatherHide;
		int tannedLeather;
		int requiredLevel;
		int cost;
		
		Hides(int[] childIds, int leatherHide, int tannedLeather, int requiredLevel, int cost) {
			this.childIds = childIds;
			this.leatherHide = leatherHide;
			this.tannedLeather = tannedLeather;
			this.requiredLevel = requiredLevel;
			this.cost = cost;
		}
		
		public int[] getChildIds() {
			return childIds;
		}
		
		public int getLeatherHide() {
			return leatherHide;
		}
		
		public int getTannedLeather() {
			return tannedLeather;
		}
		
		public int getRequiredLevel() {
			return requiredLevel;
		}
		
		public int getCost() {
			return cost;
		}
		
		private static Map<Integer, Hides> hides = new HashMap<Integer, Hides>();
		
		public static Hides forId(int child) {
			return hides.get(child);
		}
		
		static {
			for(Hides hide : Hides.values()) {
				for(int childId : hide.childIds) {
					hides.put(childId, hide);
				}
			}
		}
		
		
	}
	
	
	public static boolean startTanning(Player player, Hides hide, int childId, int amount) {
		player.getActionSender().removeChatboxInterface();
		player.getActionSender().removeInterface2();
		Item hides = new Item(hide.getLeatherHide());
		if (player.getSkills().getLevelForExperience(Skills.CRAFTING) < hide.getRequiredLevel()) {
			player.getActionSender().sendMessage("You need a crafting level of " + hide.getRequiredLevel() + " to tan this item.");
			return false;
		}
		if (!player.getInventory().hasItem(hides)) {
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE, -1, null, "You don't have any hides to tan.");
			player.getActionSender().sendMessage("You don't have any hides to tan.");
			return false;
		}
		if (amount > player.getInventory().getCount(hides.getId())) {
			amount = player.getInventory().getCount(hides.getId());
		}
		if (!player.getInventory().hasItem(new Item(995, hide.getCost() * amount))) {
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE, -1, null, "Not enough coins to complete this action.");
			player.getActionSender().sendMessage("Not enough coins to complete this action.");
			return false;
		}
		player.getInventory().remove(new Item(995, hide.getCost() * amount));
		player.getInventory().remove(new Item(hide.getLeatherHide(), amount));
		if (player.getInventory().add(new Item(hide.getTannedLeather(), amount))) {
			player.getActionSender().sendMessage("The tanner tans " + amount + " of cowhides for you.");
		}
		return true;
	}
	
	public static final String[] TANNING_INTERFACE = {"Soft leather", "Hard leather", "Snakeskin", "Snakeskin", "Green d'hide", "Blue d'hide", "Red d'hide", "Black d'hide", "1 coins", "3 coins", "20 coins", "15 coins", "20 coins", "20 coins", "20 coins", "20 coins"};

	public static final int[] TANNING_ITEMS = {1739, 1743, 7801, 6287, 1753, 1751, 1749, 1747};
}
