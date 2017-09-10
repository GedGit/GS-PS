package org.rs2server.rs2.content.misc;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import java.util.*;

/**
 * Created by Tim on 11/8/2015.
 */
public class HerbBox {

	private static final Item GUAM = new Item(200, 1);
	private static final Item MARRENTILL = new Item(202, 1);
	private static final Item TARROMIN = new Item(204, 1);
	private static final Item HARRALANDER = new Item(206, 1);
	private static final Item RANARR = new Item(208, 1);
	private static final Item IRIT = new Item(210, 1);
	private static final Item AVANTOE = new Item(212, 1);
	private static final Item KWUARM = new Item(214, 1);
	private static final Item CADANTINE = new Item(216, 1);
	private static final Item LANTADYME = new Item(2486, 1);
	private static final Item DWARF_WEED = new Item(218, 1);

	private static final Map<Item, Double> HERB_REWARDS = new HashMap<>();

	public HerbBox(Player player) {
		Server.getInjector().getInstance(PlayerService.class);
	}

	public Optional<List<Item>> getHerbRewards() {
		
		List<Item> shuffled = new ArrayList<>();

		shuffled.addAll(HERB_REWARDS.keySet());

		Collections.shuffle(shuffled);

		List<Item> rewards = new ArrayList<>(10);

		outer: while (rewards.size() < 10) {
			for (Item i : shuffled) {
				if (rewards.size() >= 10)
					break outer;
				rewards.add(i);
			}
		}
		return Optional.of(rewards);
	}

	static {
		/**
		 * Grimy guam
		 */
		HERB_REWARDS.put(GUAM, 25.5);
		/**
		 * Grimy marrentil
		 */
		HERB_REWARDS.put(MARRENTILL, 18.5);
		/**
		 * Grimy tarromin
		 */
		HERB_REWARDS.put(TARROMIN, 13.5);
		/**
		 * Grimy harralander
		 */
		HERB_REWARDS.put(HARRALANDER, 11.5);
		/**
		 * Grimy ranarr weed
		 */
		HERB_REWARDS.put(RANARR, 8.5);
		/**
		 * Grimy irit leaf
		 */
		HERB_REWARDS.put(IRIT, 6.0);
		/**
		 * Grimy avantoe
		 */
		HERB_REWARDS.put(AVANTOE, 4.5);
		/**
		 * Grimy kwuarm
		 */
		HERB_REWARDS.put(KWUARM, 4.5);
		/**
		 * Grimy cadantine
		 */
		HERB_REWARDS.put(CADANTINE, 3.0);
		/**
		 * Grimy lantadyme
		 */
		HERB_REWARDS.put(LANTADYME, 2.5);
		/**
		 * Grimy dwarf weed
		 */
		HERB_REWARDS.put(DWARF_WEED, 2.5);
	}
}
