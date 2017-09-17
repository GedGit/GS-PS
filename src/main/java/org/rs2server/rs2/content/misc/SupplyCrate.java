package org.rs2server.rs2.content.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.npc.Pet;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.mysql.impl.NewsManager;
import org.rs2server.rs2.util.Misc;

public class SupplyCrate {

	private static final List<Item> REWARD_ITEMS = new ArrayList<>();
	private static final List<Item> RARE_ITEMS = new ArrayList<>();
	private static final List<Item> VERY_RARE_ITEMS = new ArrayList<>();

	private final Player player;
	private static final Item SUPPLY_CRATE = new Item(20703);
	private final Random random;

	public SupplyCrate(Player player) {
		this.player = player;
		Server.getInjector().getInstance(PlayerService.class);
		this.random = new Random();
	}

	public Optional<Item> getRewardItem() {
		List<Item> items = new ArrayList<>();
		if (!player.getInventory().hasItem(SUPPLY_CRATE))
			return Optional.empty();
		items.addAll(getRandom() < 15 ? VERY_RARE_ITEMS : getRandom() > 40 ? REWARD_ITEMS : RARE_ITEMS);
		Collections.shuffle(items);
		return Optional.of(items.get(0));
	}

	private int getRandom() {
		return random.nextInt(5000);
	}

	static {

		REWARD_ITEMS.add(new Item(1522, Misc.random(3, 148)));
		REWARD_ITEMS.add(new Item(1520, Misc.random(3, 20)));
		REWARD_ITEMS.add(new Item(1518, Misc.random(3, 20)));
		REWARD_ITEMS.add(new Item(6334, Misc.random(3, 60)));
		REWARD_ITEMS.add(new Item(1516, Misc.random(3, 50)));
		REWARD_ITEMS.add(new Item(1514, Misc.random(3, 30)));
		REWARD_ITEMS.add(new Item(1624, Misc.random(3, 10)));
		REWARD_ITEMS.add(new Item(1622, Misc.random(3, 10)));
		REWARD_ITEMS.add(new Item(1618, Misc.random(3, 10)));
		REWARD_ITEMS.add(new Item(1620, Misc.random(3, 10)));
		REWARD_ITEMS.add(new Item(454, Misc.random(3, 15)));
		REWARD_ITEMS.add(new Item(441, Misc.random(3, 10)));
		REWARD_ITEMS.add(new Item(443, Misc.random(3, 15)));
		REWARD_ITEMS.add(new Item(445, Misc.random(3, 80)));
		REWARD_ITEMS.add(new Item(448, Misc.random(3, 10)));
		REWARD_ITEMS.add(new Item(450, Misc.random(3, 15)));
		REWARD_ITEMS.add(new Item(452, Misc.random(3, 10)));
		REWARD_ITEMS.add(new Item(200, Misc.random(3, 11)));
		REWARD_ITEMS.add(new Item(202, Misc.random(3, 11)));
		REWARD_ITEMS.add(new Item(212, Misc.random(3, 10)));
		REWARD_ITEMS.add(new Item(216, Misc.random(3, 10)));
		REWARD_ITEMS.add(new Item(208, Misc.random(3, 10)));
		REWARD_ITEMS.add(new Item(5312, Misc.random(3, 5)));
		REWARD_ITEMS.add(new Item(5313, Misc.random(3, 5)));
		REWARD_ITEMS.add(new Item(5295, Misc.random(3, 6)));
		REWARD_ITEMS.add(new Item(5293, Misc.random(3, 6)));
		REWARD_ITEMS.add(new Item(5296, Misc.random(3, 6)));
		REWARD_ITEMS.add(new Item(5294, Misc.random(3, 6)));
		REWARD_ITEMS.add(new Item(5300, Misc.random(3, 8)));
		REWARD_ITEMS.add(new Item(5284, Misc.random(3, 6)));
		REWARD_ITEMS.add(new Item(5314, Misc.random(3, 6)));
		REWARD_ITEMS.add(new Item(5315, Misc.random(3, 6)));
		REWARD_ITEMS.add(new Item(5316, Misc.random(3, 5)));
		REWARD_ITEMS.add(new Item(5317, Misc.random(3, 5)));
		REWARD_ITEMS.add(new Item(5321, Misc.random(3, 11)));
		REWARD_ITEMS.add(new Item(322, Misc.random(3, 15)));
		REWARD_ITEMS.add(new Item(336, Misc.random(3, 15)));
		REWARD_ITEMS.add(new Item(332, Misc.random(3, 15)));
		REWARD_ITEMS.add(new Item(378, Misc.random(3, 14)));
		REWARD_ITEMS.add(new Item(360, Misc.random(3, 15)));
		REWARD_ITEMS.add(new Item(372, Misc.random(3, 25)));
		REWARD_ITEMS.add(new Item(384, Misc.random(3, 25)));
		REWARD_ITEMS.add(new Item(995, Misc.random(5000, 50000)));
		REWARD_ITEMS.add(new Item(3212, Misc.random(3, 10)));

		REWARD_ITEMS.add(new Item(7937, Misc.random(250, 500)));
		REWARD_ITEMS.add(new Item(20718, 1));

		RARE_ITEMS.add(new Item(20714));// Tome of fire
		RARE_ITEMS.add(new Item(20720));// Bruma Torch
		RARE_ITEMS.add(new Item(20708));// Pyromancer hood
		RARE_ITEMS.add(new Item(20704));// Pyromancer garb
		RARE_ITEMS.add(new Item(20706));// Pyromancer robe
		RARE_ITEMS.add(new Item(20710));// Pyromancer boots
		RARE_ITEMS.add(new Item(20712));// Warm gloves

		VERY_RARE_ITEMS.add(new Item(20693)); // pet phoenix
		VERY_RARE_ITEMS.add(new Item(6739)); // Dragon axe

	}

	/**
	 * Handles adding the reward.
	 * 
	 * @param player
	 *            the player
	 */
	public void handleReward(Player player, int slot) {
		Optional<Item> reward = getRewardItem();
		if (reward.isPresent()) {
			player.getInventory().remove(new Item(20703), slot);

			if (reward.get().getId() == 20693) {
				Pet.Pets pets = Pet.Pets.PHOENIX;
				Pet.givePet(player, new Item(pets.getItem()));
			} else {
				if (reward.get().getId() == 6739) {
					World.getWorld().sendWorldMessage("<col=ff0000><img=33>Server</col>: " + player.getName()
							+ " has just received 1 x Dragon axe from Supply crate.");

					new Thread(new NewsManager(player,
							"<img src='../resources/news/supply_crate.png' width=13> received Dragon axe from Supply crate."))
									.start();
				}
				if (reward.get().getDefinition().isStackable() || reward.get().getDefinition().isNoted()) {
					if (player.isSilverMember())
						Inventory.addDroppable(player, reward.get());
					else if (player.isGoldMember())
						Inventory.addDroppable(player, reward.get());
					else if (player.isPlatinumMember())
						Inventory.addDroppable(player, reward.get());
					else if (player.isDiamondMember())
						Inventory.addDroppable(player, reward.get());
				}
				Inventory.addDroppable(player, reward.get());
			}
		}
	}
}