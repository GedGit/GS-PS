package org.rs2server.util;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

/**
 * Holds all shop junk.
 * 
 * @author Vichy
 */
public final class ShopUtils {

	/**
	 * Gets the price for items requiring blood money to purchase.
	 * 
	 * @param item
	 *            the item that's being bought
	 * @return the value as integer.
	 */
	public static int getBloodMoneyPrice(Item item) {
		switch (item.getId()) {
		case 989: // c key
			return 1000;
		case 12757: // d bow paints
		case 12759: // d bow paints
		case 12761: // d bow paints
		case 12763: // d bow paints
		case 12849: // granite clamp
			return 50000;
		case 12769: // whip paints
		case 12771:
			return 75000;
		case 12798: // steam staff upg
		case 12800: // d pick upg
		case 12802: // ward upg
			return 25000;
		case 11941: // lootin bag
			return 5000;
		case 10548: // fighter hat
			return 10000;
		case 10551: // fighter torso
			return 15000;
		case 11840: // d boots
			return 5000;
		case 19484: // dragon jav
		case 20849: // dragon thrownaxe
			return 250;
		default:
			return -1;
		}
	}

	/**
	 * Gets items value for the loyalty point store.
	 * 
	 * @param item
	 *            the item to value.
	 * @return the value as integer.
	 */
	public static int getLoyaltyPrice(Item item) {
		for (Item t1 : firstDiaryTier) {
			if (t1.getId() == item.getId())
				return 150; // Achievement diary first item tier - 2.5 hour play-time to get
		}
		for (Item t1 : secondDiaryTier) {
			if (t1.getId() == item.getId())
				return 300; // Achievement diary second item tier - 5 hour play-time to get
		}
		for (Item t1 : thirdDiaryTier) {
			if (t1.getId() == item.getId())
				return 900; // Achievement diary third item tier - 15 hour play-time to get
		}
		for (Item t1 : fourthDiaryTier) {
			if (t1.getId() == item.getId())
				return 1800; // Achievement diary fourth item tier - 30 hour play-time to get
		}
		switch (item.getId()) {
		case 13116: // Bonecrusher - 10 hour play-time to get
		case 13226: // Herb sack - 10 hour play-time to get
			return 600;
		case 4081: // Salve amulet - 3 hour play-time to get
			return 180;
		case 10588: // Salve amulet (e) - 10 hour play-time to get
			return 180;
		case 6714: // Holy wrench - 3 hour play-time to get
		case 775: // Cooking gauntlets - 2 hours 30 minutes play-time to get
		case 776: // Goldsmith gauntlets - 2 hours 30 minutes play-time to get
		case 777: // Chaos gauntlets - 2 hours 30 minutes play-time to get
			return 150;
		default:
			return 0; // shop won't allow to buy items with value of a 0 ;)
		}
	}

	/**
	 * An Item array holding all achievement diary first tier items.
	 */
	public static Item[] firstDiaryTier = { new Item(13137), new Item(13104), new Item(13112), new Item(13121),
			new Item(13129), new Item(11136), new Item(13141), new Item(13125) };

	/**
	 * An Item array holding all achievement diary second tier items.
	 */
	public static Item[] secondDiaryTier = { new Item(13138), new Item(13105), new Item(13113), new Item(13122),
			new Item(13130), new Item(11138), new Item(13142), new Item(13126) };

	/**
	 * An Item array holding all achievement diary third tier items.
	 */
	public static Item[] thirdDiaryTier = { new Item(13139), new Item(13106), new Item(13114), new Item(13123),
			new Item(13131), new Item(11140), new Item(13143), new Item(13127) };

	/**
	 * An Item array holding all achievement diary fourth tier items.
	 */
	public static Item[] fourthDiaryTier = { new Item(13140), new Item(13107), new Item(13115), new Item(13124),
			new Item(13132), new Item(13103), new Item(13144), new Item(13128) };

	/**
	 * Checks if we can buy an item from the shop; returns true if we can.
	 * 
	 * @param player
	 *            the player trying to buy an item.
	 * @param item
	 *            the item that is getting bought
	 * @return if can be bought
	 */
	public static boolean canBuy(Player player, Item item) {

		for (Item t2 : secondDiaryTier) {
			if (t2.getId() == item.getId()) {
				for (Item t1 : firstDiaryTier) {
					String t1Name = t1.getDefinition2().getName();
					String t2Name = t2.getDefinition2().getName();
					if (t1Name.regionMatches(5, t2Name, 5, 4)) {
						if (!player.getInventory().hasItem(t1)) {
							player.getActionSender().sendItemDialogue(item.getId(), "You need to have " + t1Name
									+ " in your inventory to be able to upgrade to " + t2Name + ".");
							return false;
						}
					}
				}
			}
		}
		for (Item t2 : thirdDiaryTier) {
			if (t2.getId() == item.getId()) {
				for (Item t1 : secondDiaryTier) {
					String t1Name = t1.getDefinition2().getName();
					String t2Name = t2.getDefinition2().getName();
					if (t1Name.regionMatches(5, t2Name, 5, 4)) {
						if (!player.getInventory().hasItem(t1)) {
							player.getActionSender().sendItemDialogue(item.getId(), "You need to have " + t1Name
									+ " in your inventory to be able to upgrade to " + t2Name + ".");
							return false;
						}
					}
				}
			}
		}
		for (Item t2 : fourthDiaryTier) {
			if (t2.getId() == item.getId()) {
				for (Item t1 : thirdDiaryTier) {
					String t1Name = t1.getDefinition2().getName();
					String t2Name = t2.getDefinition2().getName();
					if (t1Name.regionMatches(5, t2Name, 5, 4)) {
						if (!player.getInventory().hasItem(t1)) {
							player.getActionSender().sendItemDialogue(item.getId(), "You need to have " + t1Name
									+ " in your inventory to be able to upgrade to " + t2Name + ".");
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Gets items value for the vote point store.
	 * 
	 * @param item
	 *            the item to value.
	 * @return the value as integer.
	 */
	public static int getVotePrice(Item item) {
		switch (item.getId()) {
		case 989: // crystal key
		case 6199: // mistery box
		case 11738: // herb box
			return 3;
		case 1038: // v start of partyhats
		case 1040:
		case 1042:
		case 1044:
		case 1046:
		case 1048: // ^ end of partyhats
			return 400; // this is 30 days to vote :)
		case 1050: // santa hat
			return 350;
		case 1053: // green h'ween mask
		case 1055: // blue h'ween mask
		case 1057: // red h'ween mask
			return 360;
		case 7409: // magic secateurs
			return 25;
		case 21034: // dexterous prayer scroll
			return 450;
		case 21079: // arcane prayer scroll
			return 350;
		case 21047: // torn prayer scroll
			return 250;
		case 12637: // saradomin halo
		case 12638: // zamorak halo
		case 12639: // guthix halo
			return 60;
		default:
			return 0; // shop won't allow to buy items with value of a 0 ;)
		}
	}
}