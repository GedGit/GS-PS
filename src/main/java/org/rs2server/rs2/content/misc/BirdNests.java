package org.rs2server.rs2.content.misc;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.Misc;

import java.util.*;

/**
 * Handles everything related to bird nests.
 * 
 * @author Vichy
 */
public class BirdNests {

	/**
	 * Will be representing the bird nest item tables
	 */
	private static final List<Item> TREE_SEED_NEST = new ArrayList<>(), RING_NEST = new ArrayList<>();

	/**
	 * Instancing the player class.
	 */
	private final Player player;

	/**
	 * Instancing the bird nest item.
	 */
	private final Item birdNest;

	/**
	 * Initiating the bird nest
	 * 
	 * @param player
	 *            the player to initiate onto.
	 */
	public BirdNests(Player player, Item birdNest) {
		Server.getInjector().getInstance(PlayerService.class);
		this.player = player;
		this.birdNest = birdNest;
	}

	/**
	 * Generate the optional reward item.
	 * 
	 * @return the item.
	 */
	public Optional<Item> getRewardItem() {
		List<Item> items = new ArrayList<>();
		items.addAll(birdNest.getId() == 5073 ? TREE_SEED_NEST : RING_NEST);
		Collections.shuffle(items);
		return Optional.of(items.get(0));
	}

	/**
	 * Building rewards upon each request :/
	 */
	static {

		TREE_SEED_NEST.add(new Item(5312, 1)); // acorn
		TREE_SEED_NEST.add(new Item(5283, 1)); // apple tree
		TREE_SEED_NEST.add(new Item(5284, 1)); // banana tree
		TREE_SEED_NEST.add(new Item(5285, 1)); // orange tree
		TREE_SEED_NEST.add(new Item(5313, 1)); // willow
		TREE_SEED_NEST.add(new Item(5286, 1)); // curry tree
		TREE_SEED_NEST.add(new Item(5314, 1)); // maple
		TREE_SEED_NEST.add(new Item(5287, 1)); // pineapple
		TREE_SEED_NEST.add(new Item(5288, 1)); // papaya tree
		TREE_SEED_NEST.add(new Item(5289, 1)); // palm tree
		TREE_SEED_NEST.add(new Item(5290, 1)); // calquat
		TREE_SEED_NEST.add(new Item(5315, 1)); // yew
		TREE_SEED_NEST.add(new Item(5316, 1)); // magic

		RING_NEST.add(new Item(1635, 1)); // gold ring
		RING_NEST.add(new Item(1637, 1)); // sapphire ring
		RING_NEST.add(new Item(1639, 1)); // emerald ring
		RING_NEST.add(new Item(1641, 1)); // ruby ring
		RING_NEST.add(new Item(1643, 1)); // diamond ring

	}

	/**
	 * Handles the actual player rewarding for searching the bird nest
	 * 
	 * @param player
	 *            the player to reward.
	 */
	public void handleReward(int slot) {
		if (!player.getInventory().hasItem(birdNest)) // shouldn't happen?
			return;
		if (!getRewardItem().isPresent()) // shouldn't happen?
			return;
		if (player.getInventory().freeSlots() < 1) {
			player.sendMessage("Not enough inventory space.");
			return;
		}
		Item reward = null;
		if (birdNest.getId() >= 5070 && birdNest.getId() <= 5072)
			reward = new Item(birdNest.getId() == 5070 ? 5076 : birdNest.getId() == 5071 ? 5078 : 5077);
		else
			reward = getRewardItem().get();

		player.getActionSender().sendItemDialogue(5075,
				"Inside the bird nest you find " + Misc.withPrefix(reward.getDefinition2().getName()) + ".");

		player.getInventory().remove(birdNest);
		player.getInventory().add(new Item(5075, 1));
		player.getInventory().add(reward);
	}
}