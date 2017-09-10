package org.rs2server.rs2.content.misc;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.mysql.impl.NewsManager;
import org.rs2server.rs2.util.Misc;

import java.util.*;

/**
 * Handles everything related to mystery boxes.
 * 
 * @author Vichy
 */
public class MysteryBox {

	/**
	 * Will be representing the item rarities.
	 */
	private static final List<Item> COMMON = new ArrayList<>(), RARE = new ArrayList<>(), VERY_RARE = new ArrayList<>();

	/**
	 * Representing the mystery box item.
	 */
	private static final Item MYSTERY_BOX = new Item(6199);

	/**
	 * Instancing the player class.
	 */
	private final Player player;

	/**
	 * Instancing the random event.
	 */
	private final Random random;

	/**
	 * Initiating the mystery box.
	 * 
	 * @param player
	 *            the player to initiate onto.
	 */
	public MysteryBox(Player player) {
		Server.getInjector().getInstance(PlayerService.class);
		this.player = player;
		this.random = new Random();
	}

	/**
	 * Generate the optional reward item.
	 * 
	 * @return the item.
	 */
	public Optional<Item> getRewardItem() {
		List<Item> items = new ArrayList<>();
		items.addAll(getRandom() < 25 ? VERY_RARE : (getRandom() > 50 && getRandom() < 150) ? RARE : COMMON);
		Collections.shuffle(items);
		Optional<Item> reward = Optional.of(items.get(0));

		// Reshuffle a couple times if player already has the item
		if (player.hasItem(reward.get())) {
			for (int i = 0; i < 3; i++)
				Collections.shuffle(items);
		}
		if (player.hasItem(reward.get())) {
			for (int i = 0; i < 3; i++)
				Collections.shuffle(items);
		}

		return Optional.of(items.get(0));
	}

	/**
	 * We roll the dice.
	 * 
	 * @return chance % as integer.
	 */
	private int getRandom() {
		return random.nextInt(1000);
	}

	static {

		/**
		 * Full skeleton
		 */
		for (int i = 9921; i <= 9925; i++)
			RARE.add(new Item(i));

		/**
		 * Full chicken
		 */
		for (int i = 11019; i <= 11022; i++)
			RARE.add(new Item(i));

		/**
		 * Full camo
		 */
		for (int i = 6654; i <= 6656; i++)
			COMMON.add(new Item(i));

		/**
		 * Full zombie
		 */
		for (int i = 7592; i <= 7596; i++)
			COMMON.add(new Item(i));

		/**
		 * Full lederhosen
		 */
		for (int i = 6180; i <= 6182; i++)
			COMMON.add(new Item(i));

		/**
		 * Full prince/princess
		 */
		for (int i = 6184; i <= 6187; i++)
			COMMON.add(new Item(i));

		/**
		 * Silly jester
		 */
		for (int i = 10836; i <= 10839; i++)
			RARE.add(new Item(i));

		/**
		 * Random cosmetics
		 */
		RARE.add(new Item(6666));// Flipper
		RARE.add(new Item(7003));// Camel mask
		VERY_RARE.add(new Item(9920));// Jack'O lantern mask
		RARE.add(new Item(10507));// Reindeer hat
		VERY_RARE.add(new Item(1037));// Bunny ears
		RARE.add(new Item(5607));// Sack of grain
		VERY_RARE.add(new Item(1419));// Scythe
		RARE.add(new Item(6856));// Bobble hat
		RARE.add(new Item(6857));// Bobble scarf
		RARE.add(new Item(6860));// Tri-jester hat
		RARE.add(new Item(6861));// Tri-jester scarf
		RARE.add(new Item(6862));// Woolly hat
		RARE.add(new Item(6863));// Woolly scarf
		COMMON.add(new Item(9470));// Gnome scarf

		COMMON.add(new Item(10400));// Elegant start v
		COMMON.add(new Item(10402));
		COMMON.add(new Item(10404));
		COMMON.add(new Item(10406));
		COMMON.add(new Item(10408));
		COMMON.add(new Item(10410));
		COMMON.add(new Item(10412));
		COMMON.add(new Item(10414));
		COMMON.add(new Item(10416));
		COMMON.add(new Item(10418));
		COMMON.add(new Item(10420));
		COMMON.add(new Item(10422));
		COMMON.add(new Item(10424));
		COMMON.add(new Item(10426));
		COMMON.add(new Item(10428));
		COMMON.add(new Item(10430));
		COMMON.add(new Item(10432));
		COMMON.add(new Item(10434));
		COMMON.add(new Item(10436));
		COMMON.add(new Item(10438));// Elegant end ^

		COMMON.add(new Item(10392));// powdered wig
		COMMON.add(new Item(10396));// pantaloons
		COMMON.add(new Item(10398));// sleeping cap
		COMMON.add(new Item(10394));// Flared Trousers
		COMMON.add(new Item(10362));// glory (t)
		COMMON.add(new Item(10364));// strength (t)
		COMMON.add(new Item(10366));// magic (t)

		COMMON.add(new Item(10316));// bobs shirts v
		COMMON.add(new Item(10318));
		COMMON.add(new Item(10320));
		COMMON.add(new Item(10322));
		COMMON.add(new Item(10324));// bobs shirts ^

		COMMON.add(new Item(6188));// frog mask
		COMMON.add(new Item(995, Misc.random(50000, 450000)));// coins

	}

	/**
	 * Handles the actual player rewarding for opening the box.
	 * 
	 * @param player
	 *            the player to reward.
	 */
	public void handleReward(int slot) { 
		if (!player.getInventory().hasItem(MYSTERY_BOX)) // shouldn't happen?
			return;
		if (!getRewardItem().isPresent()) // shouldn't happen?
			return;
		Item reward = getRewardItem().get();

		player.getInventory().remove(MYSTERY_BOX, slot);
		player.getInventory().add(reward);

		player.getActionSender().sendItemDialogue(reward.getId(),
				"Inside the box you found <col=ff0000>" + reward.getCount() + "</col> x <col=ff0000>"
						+ Misc.withPrefix(reward.getDefinition2().getName()) + "</col>.");

		// Announce to world what expensiveness we received
		if (reward.getPrice() > 500000) {
			World.getWorld()
					.sendWorldMessage("<col=ff0000><img=33>Server</col>: " + player.getName() + " has just received "
							+ Misc.withPrefix(reward.getDefinition2().getName()) + " from Mystery box.");

			new Thread(new NewsManager(player, "<img src='../resources/news/mystery_box.png' width=13> "
					+ "received " + reward.getDefinition2().getName() + " from Mystery box.")).start();
		}
	}
}