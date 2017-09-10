package org.rs2server.rs2.content.misc;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.Misc;

import java.util.*;

/**
 * Handles Caskets and everything related to them.
 * 
 * @author Vichy
 */
public class Casket {

	private static final List<Item> REWARD_ITEMS = new ArrayList<>();

	private final Player player;
	private static final Item CASKET = new Item(405);

	public Casket(Player player) {
		this.player = player;
		Server.getInjector().getInstance(PlayerService.class);
		new Random();
	}

	public Optional<Item> getRewardItem() {
		List<Item> items = new ArrayList<>();
		items.addAll(REWARD_ITEMS);
		Collections.shuffle(items);
		return Optional.of(items.get(0));
	}

	static {

		REWARD_ITEMS.add(new Item(995, 100));// coins
		REWARD_ITEMS.add(new Item(995, 100));// coins
		REWARD_ITEMS.add(new Item(995, 100));// coins
		REWARD_ITEMS.add(new Item(995, 100));// coins
		REWARD_ITEMS.add(new Item(995, 100));// coins
		REWARD_ITEMS.add(new Item(11738, 1));// herb box
		REWARD_ITEMS.add(new Item(985, 1));// crystal half
		REWARD_ITEMS.add(new Item(987, 1));// crystal half
		REWARD_ITEMS.add(new Item(989, 1));// crystal key
		REWARD_ITEMS.add(new Item(995, 100));// coins
		REWARD_ITEMS.add(new Item(139, 1));// prayer potion 3 dose
		REWARD_ITEMS.add(new Item(13441, 1));// anglerfish
		REWARD_ITEMS.add(new Item(12701, 1));// super combat 1 dose
		REWARD_ITEMS.add(new Item(173, 1));// ranging potion 1 dose
		REWARD_ITEMS.add(new Item(3046, 1));// ranging potion 1 dose
		REWARD_ITEMS.add(new Item(995, 100));// coins
		REWARD_ITEMS.add(new Item(995, 100));// coins
		REWARD_ITEMS.add(new Item(7937, 150));// pure essence
		REWARD_ITEMS.add(new Item(1437, 250));// rune essence
		REWARD_ITEMS.add(new Item(995, 100));// coins
		REWARD_ITEMS.add(new Item(995, 100));// coins
		
	}

	public void open(int slot) {
		Optional<Item> rewards = getRewardItem();
		Item item = rewards.get();
		if (item.getCount() == 100)
			item.setCount(Misc.random(250, item.getCount() * (player.getSkills().getCombatLevel() * 5)));
		player.getInventory().remove(CASKET, slot);
		player.getInventory().add(rewards.get());
	}
}