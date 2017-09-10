package org.rs2server.rs2.domain.service.impl;

import com.google.inject.Inject;
import org.rs2server.rs2.domain.service.api.loot.Loot;
import org.rs2server.rs2.domain.service.api.loot.LootGenerationService;
import org.rs2server.rs2.domain.service.api.loot.LootTable;
import org.rs2server.rs2.model.Item;

import java.util.*;

/**
 * @author tommo
 * @author twelve
 */
public final class LootGenerationServiceImpl implements LootGenerationService {

	private final Map<Integer, NpcLootTable> npcDropTables;

	private static final Random RANDOM = new Random();

	@Inject
	public LootGenerationServiceImpl() {
		this.npcDropTables = dropsFromJson();

		npcDropTables.values().forEach(t -> {
			System.out.println("Table for " + Arrays.toString(t.npcIds.toArray()) + " -> ");
			t.high.forEach(h -> System.out.println("\thigh: " + h.getItemId()));
			t.medium.forEach(m -> System.out.println("\tmedium: " + m.getItemId()));
			t.low.forEach(l -> System.out.println("\tlow: " + l.getItemId()));
		});
	}

	@Override
	public final Map<Integer, NpcLootTable> dropsFromJson() {
		return new HashMap<>();
		/*
		 * return Errors.log().getWithDefault(() -> { Map<Integer, NpcLootTable>
		 * tableMap = new HashMap<>();
		 * Files.list(Paths.get("./data/json/drops2/")) .map(p ->
		 * GSON.fromJson(Errors.log().getWithDefault(() -> new
		 * String(Files.readAllBytes(p)), ""), NpcLootTable.class)) .forEach(t
		 * -> t.npcIds.forEach(n -> tableMap.put(n, t))); return tableMap; },
		 * new HashMap<>());
		 */
	}

	@Override
	public NpcLootTable getNpcTable(int id) {
		return npcDropTables.get(id);
	}

	@Override
	public LootTable getRandomTable(NpcLootTable npcLootTable) {
		int roll = RANDOM.nextInt(256);
		if (roll >= 100 && roll < 200) {
			return LootTable.of(npcLootTable.medium);
		}
		if (roll >= 252) {
			return LootTable.of(npcLootTable.high);
		}
		return LootTable.of(npcLootTable.low);
	}

	@Override
	public final Item generateCasketLoot() {
		Optional<Loot> tableOption = Optional.of(LootTable.equalityTable(4012, 4012, 4012, 1038, 1040, 1042, 1044, 1046,
				1048, 1050, 12887, 12888, 12889, 12890, 12891).getRandomLoot());
		if (tableOption.isPresent()) {
			return tableOption.get().toSingleItem();
		}
		return null;
	}
}
