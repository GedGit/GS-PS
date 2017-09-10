package org.rs2server.rs2.domain.service.api.loot;

import com.google.gson.annotations.SerializedName;
import org.rs2server.rs2.model.Item;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Map;

/**
 * Provides random loot generation capabilities.
 *
 * @author tommo
 * @author twelve
 */
public interface LootGenerationService {
	@Immutable
	class NpcLootTable {

		public final int rolls;
		@SerializedName("npc-ids") public final List<Integer> npcIds;
		public final List<Loot> guaranteed;
		public final List<Loot> low;
		public final List<Loot> medium;
		public final List<Loot> high;

		public NpcLootTable(int rolls, List<Integer> npcIds, List<Loot> guaranteed, List<Loot> low, List<Loot> medium, List<Loot> high) {
			this.rolls = rolls;
			this.npcIds = npcIds;
			this.guaranteed = guaranteed;
			this.low = low;
			this.medium = medium;
			this.high = high;
		}

		public int getRolls() {
			return rolls;
		}

		@Override
		public final String toString() {
			return npcIds.toString() +", "+low.toString()+", "+medium.toString()+", "+high.toString();
		}
	}

	LootTable getRandomTable(NpcLootTable npcLootTable);

	Item generateCasketLoot();

	Map<Integer, NpcLootTable> dropsFromJson();

	NpcLootTable getNpcTable(int id);
}
