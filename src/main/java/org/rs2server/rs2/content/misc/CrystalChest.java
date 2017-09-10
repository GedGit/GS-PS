package org.rs2server.rs2.content.misc;

import java.util.ArrayList;
import java.util.List;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.mysql.impl.NewsManager;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * Handles the Crystal chest distraction & diversion.
 * 
 * @author Vichy
 */
public class CrystalChest {

	/**
	 * Handles chest opening.
	 * 
	 * @param player
	 *            the player.
	 */
	public static void openChest(Player player, GameObject obj) {
		if (!player.getInventory().contains(989)) {
			player.getActionSender().sendItemDialogue(989, "The chest won't open without a key.");
			return;
		}
		player.setAttribute("busy", true);
		player.playAnimation(Animation.create(536));
		for (Region r : obj.getRegion().getSurroundingRegions()) {
			for (Player p : r.getPlayers())
				p.getActionSender().animateObject(obj, 2709);
		}
		World.getWorld().submit(new Tickable(1) {

			@Override
			public void execute() {
				this.stop();
				player.removeAttribute("busy");
				player.getInventory().remove(new Item(989, 1));
				Inventory.addDroppable(player, new Item(1631, 1));
				Item reward = CRYSTAL_CHEST_REWARDS[Misc.random(CRYSTAL_CHEST_REWARDS.length - 1)];
				Inventory.addDroppable(player, reward);

				if (reward.getPrice() > 500000) {

					World.getWorld()
							.sendWorldMessage("<col=ff0000><img=33>Server</col>: " + player.getName()
									+ " has just received " + reward.getCount() + " x "
									+ reward.getDefinition2().getName() + " from Crystal chest.");

					new Thread(new NewsManager(player, "<img src='../resources/news/chest.png' "
							+ "width=13> received " + reward.getDefinition2().getName() + " from Crystal chest."))
									.start();
				}
			}
		});
	}

	/**
	 * Opens the interface of all obtainable rewards from the chest.
	 * 
	 * @param player
	 *            The player to send the interface to.
	 */
	public static void openRewardsInterface(Player player) {
		player.getActionSender().sendInterface(275, false);
		final List<String> results = new ArrayList<>();
		for (Item reward : CRYSTAL_CHEST_REWARDS) {
			String name = reward.getDefinition2().getName();
			if (name == null)
				name = reward.getDefinition2().getNotedName();
			if (!results.contains("name"))
				results.add(name);
		}
		player.getActionSender().sendTextListInterface("Crystal chest Rewards",
				results.toArray(new String[results.size()]));
	}

	/**
	 * The array of possible crystal chest rewards.
	 */
	private static final Item[] CRYSTAL_CHEST_REWARDS = new Item[] { new Item(378, Misc.random(20, 75)),
			new Item(372, Misc.random(20, 50)), new Item(995, Misc.random(30000, 100000)),
			new Item(554, Misc.random(50, 250)), new Item(555, Misc.random(50, 250)),
			new Item(7945, Misc.random(20, 25)), new Item(384, Misc.random(10, 30)), new Item(440, Misc.random(20, 30)),
			new Item(437, Misc.random(20, 30)), new Item(441, Misc.random(20, 25)), new Item(445, Misc.random(20, 40)),
			new Item(208, Misc.random(10, 20)), new Item(214, Misc.random(20, 30)), new Item(3052, Misc.random(20, 30)),
			new Item(218, Misc.random(20, 30)), new Item(3050, Misc.random(20, 30)), new Item(220, Misc.random(20, 30)),
			new Item(1778, Misc.random(50, 250)), new Item(4087, 1), new Item(4585, 1), new Item(11840, 1),
			new Item(3105, 1), new Item(2579, 1), new Item(2581, 1), new Item(2577, 1), new Item(4151, 1),
			new Item(4153, 1), new Item(6528, 1), new Item(1712, 1), new Item(4587, 1), new Item(1187, 1),
			new Item(1512, Misc.random(20, 50)), new Item(1305, 1), new Item(1377, 1), new Item(7158, 1),
			new Item(1522, Misc.random(20, 50)), new Item(1520, Misc.random(20, 50)), new Item(10589, 1),
			new Item(1518, Misc.random(20, 50)), new Item(556, Misc.random(50, 250)), new Item(10564, 1),
			new Item(557, Misc.random(50, 250)), new Item(558, Misc.random(50, 250)), new Item(6809, 1),
			new Item(559, Misc.random(50, 250)), new Item(560, Misc.random(50, 250)), new Item(3122, 1),
			new Item(561, Misc.random(50, 250)), new Item(562, Misc.random(50, 250)), new Item(10828, 1),
			new Item(563, Misc.random(50, 250)), new Item(454, Misc.random(50, 150)), new Item(1079, 1),
			new Item(1113, 1), new Item(1127, 1), new Item(1147, 1), new Item(1093, 1), new Item(3751, 1),
			new Item(441, Misc.random(50, 100)), new Item(2364, Misc.random(10, 15)), new Item(1163, 1),
			new Item(1185, 1), new Item(1201, 1), new Item(1213, 1), new Item(1289, 1), new Item(1079, 1),
			new Item(1303, 1), new Item(1432, 1), new Item(1347, 1), new Item(1333, 1), new Item(1373, 1),
			new Item(1319, 1), new Item(4131, 1), new Item(1079, 1), new Item(1215, 1), new Item(1149, 1),
			new Item(2350, Misc.random(20, 50)), new Item(2352, Misc.random(20, 45)), new Item(3755, 1),
			new Item(2354, Misc.random(20, 40)), new Item(2356, Misc.random(20, 35)), new Item(3749, 1),
			new Item(2358, Misc.random(15, 30)), new Item(2360, Misc.random(10, 25)), new Item(3753, 1),
			new Item(2362, Misc.random(5, 20)) };
}