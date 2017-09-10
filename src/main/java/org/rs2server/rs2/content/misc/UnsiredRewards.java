package org.rs2server.rs2.content.misc;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.npc.Pet;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.mysql.impl.NewsManager;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * Handles the Crystal chest distraction & diversion.
 * 
 * @author Vichy
 */
public class UnsiredRewards {

	/**
	 * Handles chest opening.
	 * 
	 * @param player
	 *            the player.
	 */
	public static void offerUnsired(Player player, GameObject obj) {

		if (!player.getInventory().contains(13273)) {
			player.getActionSender().sendItemDialogue(13273, "You'll need an unsired in order to do this.");
			return;
		}
		player.setAttribute("busy", true);
		player.playAnimation(Animation.create(827));
		player.playGraphics(Graphic.create(1294));
		player.getInventory().remove(new Item(13273, 1));

		World.getWorld().submit(new Tickable(1) {

			@Override
			public void execute() {
				this.stop();
				player.removeAttribute("busy");
				addReward(player);
			}
		});
	}

	/**
	 * Handles the reward.
	 * 
	 * @param player
	 *            The player.
	 */
	public static void addReward(Player player) {
		int random = Misc.random(7000);
		Item reward = new Item(995, Misc.random(50000, 500000));

		if (random >= 0 && random <= 781)
			reward = new Item(13262); // Abyssal head

		else if (random >= 782 && random <= 2031)
			reward = new Item(13265); // Abyssal dagger

		else if (random >= 2032 && random <= 2800)
			reward = new Item(4151); // Abyssal whip

		else if (random >= 2801 && random <= 6645) {
			Item[] part = { new Item(13275), new Item(13274), new Item(13276) };
			reward = part[Misc.random(part.length) - 1];

			/** Kinda loop it three times I guess to shuffle a bit **/
			if (player.getBank().contains(reward.getId()))
				reward = part[Misc.random(part.length) - 1];

			else if (player.getBank().contains(reward.getId()))
				reward = part[Misc.random(part.length) - 1];

			else if (player.getBank().contains(reward.getId()))
				reward = part[Misc.random(part.length) - 1];

		} else {

			// Pet
			Pet.Pets pets = Pet.Pets.ABBYSAL_ORPHAN;
			Pet.givePet(player, new Item(pets.getItem()));
		}

		// Handling pet announcement above
		if (reward.getId() != 13262) {
			World.getWorld()
					.sendWorldMessage("<col=ff0000><img=33>Server</col>: " + player.getName() + " has just received "
							+ reward.getCount() + " x " + reward.getDefinition2().getName()
							+ " from Font of Consumption.");

			new Thread(new NewsManager(player, "<img src='../resources/news/unsired.png' width=13> "
					+ "received " + reward.getDefinition2().getName() + " from Font of Consumption.")).start();
			
			Inventory.addDroppable(player, reward);
		}
	}
}