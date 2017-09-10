package org.rs2server.rs2.domain.service.api.loot;

import org.rs2server.Server;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.util.Misc;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * A collection of loot entries.
 *
 * @author tommo
 * @author twelve
 */
public class LootTable {

	private static final Random random = new Random();

	private List<Loot> loot = new ArrayList<>();

	private LootTable() {

	}

	public static LootTable of() {
		return new LootTable();
	}

	public static LootTable of(Loot... loot) {
		return of(Arrays.asList(loot));
	}

	public static LootTable of(Collection<Loot> collection) {
		return of().addAll(collection);
	}

	public LootTable add(Loot loot) {
		this.loot.add(loot);
		return this;
	}

	public LootTable addAll(Collection<Loot> loot) {
		this.loot.addAll(loot);
		return this;
	}

	/**
	 * Returns a new loot table which is the merged table between this and given
	 * loot table.
	 * 
	 * @param lootTable
	 *            The loot table to merge with.
	 * @return A new loot table.
	 */
	public LootTable merge(LootTable lootTable) {
		return of(loot).addAll(lootTable.loot);
	}

	/**
	 * Generates a drop map containing a specified amount of entries, in addition to
	 * all of the guarenteed drops.
	 * 
	 * @param amount
	 *            The goal amount of elements in our drop table.
	 * @return A map of loot to the amount of loot to be dropped.
	 */
	public Map<Loot, Integer> dropMap(int amount) {
		Map<Loot, Integer> partitioned = getLoot().stream().filter(l -> l.getProbability() == 100)
				.collect(toMap(Function.identity(), Loot::getMinAmount));

		int guarenteed = partitioned.size();
		int i = 0;
		while (partitioned.size() < guarenteed + amount) {// i think it has to do with this while loop
			Loot loot = getNonGuaranteedLoot();

			if (loot != null)
				partitioned.put(loot, Misc.random(loot.getMinAmount(), loot.getMaxAmount()));

			if (i++ == 10)
				break;
		}
		return partitioned;
	}

	/**
	 * Creates a loot table where all of the elements have the same default
	 * probability.
	 * 
	 * @param ids
	 *            The ids of the elements in the loot table.
	 * @return The equality table.
	 */
	@SuppressWarnings("unused")
	public static LootTable equalityTable(Integer... ids) {
		List<Integer> itemList = Arrays.asList(ids);
		double probability = itemList.size() / 100D;
		return of(itemList.stream().map(i -> Loot.of(i, 6)).collect(Collectors.toList()));
	}

	public List<Item> randomItemList(int amount) {
		return toItemList(dropMap(amount));
	}

	public List<Item> toItemList(Map<Loot, Integer> loot) {
		return loot.keySet().stream().map(l -> new Item(l.getItemId(), loot.get(l))).collect(toList());
	}

	/**
	 * Picks a random loot entry from the table based on the loot probability.
	 * 
	 * @return The random loot entry. Never null.
	 */
	public Loot getNonGuaranteedLoot() {
		Loot loot = getRandomLoot();

		if (loot == null)
			return null;
		if (loot.getProbability() == 100 && getLoot().size() > 1)
			return getNonGuaranteedLoot();
		return loot;
	}

	public List<Loot> generateNpcDrop(int npcId, int rolls) {
		LootGenerationService lootService = Server.getInjector().getInstance(LootGenerationService.class);
		LootGenerationService.NpcLootTable npcLootTable = lootService.getNpcTable(npcId);

		List<Loot> loot = npcLootTable.guaranteed.stream().collect(Collectors.toList());
		for (int i = 0; i < rolls; i++)
			loot.add(getRandomLoot());
		return loot;
	}

	/**
	 * Picks a random loot entry from the table based on the loot probability.
	 * 
	 * @return The random loot entry. Never null.
	 */
	public Loot getRandomLoot() {
		final double factor = getProbabilityFactor();
		final double rand = random.nextDouble() * 100D;

		double cumulatingProbabilities = 0;
		for (final Loot l : loot) {
			final double scaledProbability = l.getProbability() / factor;
			if (rand >= cumulatingProbabilities && rand < (cumulatingProbabilities + scaledProbability))
				return l;
			cumulatingProbabilities += scaledProbability;
		}
		return null;
	}

	/**
	 * The probabilities of all of the items in the loot table may not add up to
	 * 100%, therefore we calculate a factor by which all of the probability must be
	 * divided by to essentially treat the loot table as all items adding up to
	 * 100%.
	 * 
	 * @return The probability scale factor.
	 */
	public double getProbabilityFactor() {
		final double summedProbabilities = loot.stream().mapToDouble(Loot::getProbability).sum();
		return summedProbabilities / 100D;
	}

	/**
	 * Picks x random loot entries from the table based on the loot probability.
	 * 
	 * @param amount
	 *            the amount of loot to generate.
	 * @return The random loot entry. Never null.
	 */
	public List<Loot> getRandomLoot(int amount) {
		final List<Loot> randomLoot = new ArrayList<>(amount);
		for (int i = 0; i < amount; i++)
			randomLoot.add(getRandomLoot());
		return randomLoot;
	}

	public List<Loot> getLoot() {
		return loot;
	}

	public List<Item> toSingularItemList() {
		return loot.stream().map(Loot::toSingleItem).collect(toList());
	}

}
