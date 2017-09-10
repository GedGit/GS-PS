package org.rs2server.rs2.content;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.player.Player;

/**
 * Created by Tim on 10/24/2015.
 */
public class SlayerHelmAction {

	private final static Item[] items = { new Item(4168), new Item(4164), new Item(4166), new Item(4551),
			new Item(4155), new Item(8921) };

	public static boolean handleItemOnItem(Player player, Item used, Item with) {
		if (!isRequirement(used) || !isRequirement(with))
			return false;
		if (!player.getDatabaseEntity().getSlayerSkill().isUnlockedSlayerHelm()) {
			player.getActionSender().sendMessage(
					"You have not yet unlocked this feature, unlock it by speaking with your Slayer master.");
			return false;
		}
		for (Item requirement : items) {
			if (!player.getInventory().contains(requirement.getId())) {
				player.getActionSender().sendMessage(
						"You need a nosepeg, facemask, earmuffs, spiny helmet, Slayer gem and a black mask in your inventory in order to construct a Slayer helm.");
				return false;
			}
		}
		if (player.getSkills().getLevelForExperience(Skills.CRAFTING) < 55) {
			player.getActionSender().sendMessage("You need a crafting level of 55 to complete this action.");
			return false;
		}
		for (Item requirement : items)
			player.getInventory().remove(requirement);
		player.getInventory().add(new Item(11864, 1));
		player.getActionSender().sendMessage("You combine the items into a Slayer helm.");
		return true;
	}

	public static boolean isRequirement(Item item) {
		for (Item requirements : items) {
			if (item.getId() == requirements.getId())
				return true;
		}
		return false;
	}

	/**
	 * Handles all slayer helmet disassembly.
	 * 
	 * @param player
	 *            The player.
	 * @param item
	 *            The slayer helmet.
	 * @return if can get disassembled.
	 */
	public static boolean disassembleHelm(Player player, Item item) {
		if (player.getInventory().freeSlots() < 7) {
			player.sendMessage("Not enough inventory space for disassembly.");
			return false;
		}
		player.getInventory().remove(item);
		
		if (item.getId() == 19639 || item.getId() == 19641) // black slayer helm
			player.getInventory().add(new Item(7980));
		if (item.getId() == 19643 || item.getId() == 19645) // green slayer helm
			player.getInventory().add(new Item(7981));
		if (item.getId() == 19647 || item.getId() == 19649) // red slayer helm
			player.getInventory().add(new Item(7979));

		for (Item requirement : items)
			player.getInventory().add(requirement);
		
		player.sendMessage("You've disassembled your "+item.getDefinition2().getName()+" and get back its components.");
		
		return true;
	}
}
