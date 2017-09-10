package org.rs2server.rs2.domain.model.player.treasuretrail.clue;

import com.google.common.collect.ImmutableList;
import org.rs2server.rs2.domain.service.api.loot.LootTable;

import java.util.Arrays;
import java.util.List;

/**
 * Defines the clue scroll types.
 *
 * @author tommo
 */
public enum ClueScrollType {

	EASY(2, 4, 12179, 12180, ImmutableList.of(TreasureTrailClues.CLUE_ANAGRAM_LOWE,
			TreasureTrailClues.CLUE_ANAGRAM_BARTENDER, TreasureTrailClues.CLUE_ANAGRAM_BOB,
			TreasureTrailClues.CLUE_ANAGRAM_KENT, TreasureTrailClues.CLUE_ANAGRAM_MATTHIAS,
			TreasureTrailClues.CLUE_ANAGRAM_PAUL, TreasureTrailClues.CLUE_ANAGRAM_PROSPECTOR_PERCY,
			TreasureTrailClues.CLUE_MAP_FALADOR_CROSSROADS, TreasureTrailClues.CLUE_MAP_ARDOUGNE_LEGENDS_GUILD,
			TreasureTrailClues.CLUE_MAP_RELLEKA_LIGHTHOUSE, TreasureTrailClues.CLUE_MAP_SEERS_TO_RELLEKA,
			TreasureTrailClues.CLUE_MAP_NORTH_FALADOR_RUINS, TreasureTrailClues.CLUE_MAP_CHAMPIONS_GUILD,
			TreasureTrailClues.CLUE_MAP_VARROCK_EAST_MINE, TreasureTrailClues.CLUE_MAP_DRAYNOR,
			TreasureTrailClues.CLUE_RIDDLE_FALADOR_CRATE, TreasureTrailClues.CLUE_RIDDLE_OZIACH,
			TreasureTrailClues.CLUE_RIDDLE_EDGEVILLE_YEW_TREE, TreasureTrailClues.CLUE_RIDDLE_DWARVEN_MINE_CART,
			TreasureTrailClues.CLUE_RIDDLE_LUMBRIDGE_CASTLE_DRAWERS, TreasureTrailClues.CLUE_RIDDLE_KAMFREENA,
			TreasureTrailClues.CLUE_RIDDLE_WIZARDS_TOWER, TreasureTrailClues.CLUE_EMOTE_LAUGH_FALADOR_BAR,
			TreasureTrailClues.CLUE_EMOTE_CRY_SEERS_BANK, TreasureTrailClues.CLUE_EMOTE_CHEER_GAME_ROOM,
			TreasureTrailClues.CLUE_EMOTE_JIG_FISHING_GUILD, TreasureTrailClues.CLUE_EMOTE_SPIN_VARROCK_CASTLE,
			TreasureTrailClues.CLUE_EMOTE_WAVE_LUMBER_YARD, TreasureTrailClues.CLUE_EMOTE_PANIC_YANILLE_BANK,
			TreasureTrailClues.CLUE_EMOTE_BOW_LEGENDS_GUILD, TreasureTrailClues.CLUE_COORDINATE_KARAMJA,
			TreasureTrailClues.CLUE_COORDINATE_BRIMHAVEN_GOLD_MINE, TreasureTrailClues.CLUE_COORDINATE_HAM_ENTRANCE,
			TreasureTrailClues.CLUE_COORDINATE_DRAYNOR_MINE, TreasureTrailClues.CLUE_COORDINATE_SLAYER_TOWER),
			ClueScrollRewards.EASY_REWARDS_TABLE), 
	
	MEDIUM(4, 6, 12029, 12030, ImmutableList.of(
					TreasureTrailClues.CLUE_ANAGRAM_LOWE, TreasureTrailClues.CLUE_ANAGRAM_BARTENDER,
					TreasureTrailClues.CLUE_ANAGRAM_BOB, TreasureTrailClues.CLUE_ANAGRAM_KENT,
					TreasureTrailClues.CLUE_ANAGRAM_MATTHIAS, TreasureTrailClues.CLUE_ANAGRAM_PAUL,
					TreasureTrailClues.CLUE_ANAGRAM_PROSPECTOR_PERCY, TreasureTrailClues.CLUE_MAP_FALADOR_CROSSROADS,
					TreasureTrailClues.CLUE_MAP_ARDOUGNE_LEGENDS_GUILD, TreasureTrailClues.CLUE_MAP_RELLEKA_LIGHTHOUSE,
					TreasureTrailClues.CLUE_MAP_SEERS_TO_RELLEKA, TreasureTrailClues.CLUE_MAP_NORTH_FALADOR_RUINS,
					TreasureTrailClues.CLUE_MAP_CHAMPIONS_GUILD, TreasureTrailClues.CLUE_MAP_VARROCK_EAST_MINE,
					TreasureTrailClues.CLUE_MAP_DRAYNOR, TreasureTrailClues.CLUE_RIDDLE_FALADOR_CRATE,
					TreasureTrailClues.CLUE_RIDDLE_OZIACH, TreasureTrailClues.CLUE_RIDDLE_EDGEVILLE_YEW_TREE,
					TreasureTrailClues.CLUE_RIDDLE_DWARVEN_MINE_CART,
					TreasureTrailClues.CLUE_RIDDLE_LUMBRIDGE_CASTLE_DRAWERS, TreasureTrailClues.CLUE_RIDDLE_KAMFREENA,
					TreasureTrailClues.CLUE_RIDDLE_WIZARDS_TOWER, TreasureTrailClues.CLUE_EMOTE_LAUGH_FALADOR_BAR,
					TreasureTrailClues.CLUE_EMOTE_CRY_SEERS_BANK, TreasureTrailClues.CLUE_EMOTE_CHEER_GAME_ROOM,
					TreasureTrailClues.CLUE_EMOTE_JIG_FISHING_GUILD, TreasureTrailClues.CLUE_EMOTE_SPIN_VARROCK_CASTLE,
					TreasureTrailClues.CLUE_EMOTE_WAVE_LUMBER_YARD, TreasureTrailClues.CLUE_EMOTE_PANIC_YANILLE_BANK,
					TreasureTrailClues.CLUE_EMOTE_BOW_LEGENDS_GUILD, TreasureTrailClues.CLUE_COORDINATE_KARAMJA,
					TreasureTrailClues.CLUE_COORDINATE_BRIMHAVEN_GOLD_MINE,
					TreasureTrailClues.CLUE_COORDINATE_HAM_ENTRANCE, TreasureTrailClues.CLUE_COORDINATE_DRAYNOR_MINE,
					TreasureTrailClues.CLUE_COORDINATE_SLAYER_TOWER, TreasureTrailClues.CLUE_COORDINATE_BURTHORPE,
					TreasureTrailClues.CLUE_COORDINATE_ICE_MOUNTAIN), ClueScrollRewards.MEDIUM_REWARDS_TABLE),

	HARD(6, 8, 12542, 12543, ImmutableList.of(TreasureTrailClues.CLUE_ANAGRAM_LOWE,
			TreasureTrailClues.CLUE_ANAGRAM_BARTENDER, TreasureTrailClues.CLUE_ANAGRAM_BOB,
			TreasureTrailClues.CLUE_ANAGRAM_KENT, TreasureTrailClues.CLUE_ANAGRAM_MATTHIAS,
			TreasureTrailClues.CLUE_ANAGRAM_PAUL, TreasureTrailClues.CLUE_ANAGRAM_PROSPECTOR_PERCY,
			TreasureTrailClues.CLUE_MAP_FALADOR_CROSSROADS, TreasureTrailClues.CLUE_MAP_ARDOUGNE_LEGENDS_GUILD,
			TreasureTrailClues.CLUE_MAP_RELLEKA_LIGHTHOUSE, TreasureTrailClues.CLUE_MAP_SEERS_TO_RELLEKA,
			TreasureTrailClues.CLUE_MAP_NORTH_FALADOR_RUINS, TreasureTrailClues.CLUE_MAP_CHAMPIONS_GUILD,
			TreasureTrailClues.CLUE_MAP_VARROCK_EAST_MINE, TreasureTrailClues.CLUE_MAP_DRAYNOR,
			TreasureTrailClues.CLUE_MAP_WEST_KHAZARD_BATTLEFIELD, TreasureTrailClues.CLUE_MAP_YANILLE,
			TreasureTrailClues.CLUE_RIDDLE_FALADOR_CRATE, TreasureTrailClues.CLUE_RIDDLE_OZIACH,
			TreasureTrailClues.CLUE_RIDDLE_EDGEVILLE_YEW_TREE, TreasureTrailClues.CLUE_RIDDLE_DWARVEN_MINE_CART,
			TreasureTrailClues.CLUE_RIDDLE_LUMBRIDGE_CASTLE_DRAWERS, TreasureTrailClues.CLUE_RIDDLE_KAMFREENA,
			TreasureTrailClues.CLUE_RIDDLE_WIZARDS_TOWER, TreasureTrailClues.CLUE_EMOTE_LAUGH_FALADOR_BAR,
			TreasureTrailClues.CLUE_EMOTE_CRY_SEERS_BANK, TreasureTrailClues.CLUE_EMOTE_CHEER_GAME_ROOM,
			TreasureTrailClues.CLUE_EMOTE_JIG_FISHING_GUILD, TreasureTrailClues.CLUE_EMOTE_SPIN_VARROCK_CASTLE,
			TreasureTrailClues.CLUE_EMOTE_WAVE_LUMBER_YARD, TreasureTrailClues.CLUE_EMOTE_PANIC_YANILLE_BANK,
			TreasureTrailClues.CLUE_EMOTE_BOW_LEGENDS_GUILD, TreasureTrailClues.CLUE_COORDINATE_KARAMJA,
			TreasureTrailClues.CLUE_COORDINATE_BRIMHAVEN_GOLD_MINE, TreasureTrailClues.CLUE_COORDINATE_HAM_ENTRANCE,
			TreasureTrailClues.CLUE_COORDINATE_DRAYNOR_MINE, TreasureTrailClues.CLUE_COORDINATE_SLAYER_TOWER,

			// Hard specific clues
			TreasureTrailClues.CLUE_MAP_WILDERNESS_50, TreasureTrailClues.CLUE_EMOTE_PANIC_WILDERNESS_VOLCANO,
			TreasureTrailClues.CLUE_EMOTE_SHRUG_WILDERNESS_ZAMORAK_TEMPLE,
			TreasureTrailClues.CLUE_EMOTE_YAWN_WILDERNESS_ROGUE_STORE, TreasureTrailClues.CLUE_EMOTE_BOW_LIGHTHOUSE),
			ClueScrollRewards.HARD_REWARDS_TABLE),

	ELITE(8, 10, 12073, 12084, ImmutableList.of(TreasureTrailClues.CLUE_ANAGRAM_LOWE,
			TreasureTrailClues.CLUE_ANAGRAM_BARTENDER, TreasureTrailClues.CLUE_ANAGRAM_BOB,
			TreasureTrailClues.CLUE_ANAGRAM_KENT, TreasureTrailClues.CLUE_ANAGRAM_MATTHIAS,
			TreasureTrailClues.CLUE_ANAGRAM_PAUL, TreasureTrailClues.CLUE_ANAGRAM_PROSPECTOR_PERCY,
			TreasureTrailClues.CLUE_MAP_FALADOR_CROSSROADS, TreasureTrailClues.CLUE_MAP_ARDOUGNE_LEGENDS_GUILD,
			TreasureTrailClues.CLUE_MAP_RELLEKA_LIGHTHOUSE, TreasureTrailClues.CLUE_MAP_SEERS_TO_RELLEKA,
			TreasureTrailClues.CLUE_MAP_NORTH_FALADOR_RUINS, TreasureTrailClues.CLUE_MAP_CHAMPIONS_GUILD,
			TreasureTrailClues.CLUE_MAP_VARROCK_EAST_MINE, TreasureTrailClues.CLUE_MAP_DRAYNOR,
			TreasureTrailClues.CLUE_MAP_WEST_KHAZARD_BATTLEFIELD, TreasureTrailClues.CLUE_MAP_YANILLE,
			TreasureTrailClues.CLUE_RIDDLE_FALADOR_CRATE, TreasureTrailClues.CLUE_RIDDLE_OZIACH,
			TreasureTrailClues.CLUE_RIDDLE_EDGEVILLE_YEW_TREE, TreasureTrailClues.CLUE_RIDDLE_DWARVEN_MINE_CART,
			TreasureTrailClues.CLUE_RIDDLE_LUMBRIDGE_CASTLE_DRAWERS, TreasureTrailClues.CLUE_RIDDLE_KAMFREENA,
			TreasureTrailClues.CLUE_RIDDLE_WIZARDS_TOWER, TreasureTrailClues.CLUE_EMOTE_LAUGH_FALADOR_BAR,
			TreasureTrailClues.CLUE_EMOTE_CRY_SEERS_BANK, TreasureTrailClues.CLUE_EMOTE_CHEER_GAME_ROOM,
			TreasureTrailClues.CLUE_EMOTE_JIG_FISHING_GUILD, TreasureTrailClues.CLUE_EMOTE_SPIN_VARROCK_CASTLE,
			TreasureTrailClues.CLUE_EMOTE_WAVE_LUMBER_YARD, TreasureTrailClues.CLUE_EMOTE_PANIC_YANILLE_BANK,
			TreasureTrailClues.CLUE_EMOTE_BOW_LEGENDS_GUILD, TreasureTrailClues.CLUE_COORDINATE_KARAMJA,
			TreasureTrailClues.CLUE_COORDINATE_BRIMHAVEN_GOLD_MINE, TreasureTrailClues.CLUE_COORDINATE_HAM_ENTRANCE,
			TreasureTrailClues.CLUE_COORDINATE_DRAYNOR_MINE, TreasureTrailClues.CLUE_COORDINATE_SLAYER_TOWER,
			TreasureTrailClues.CLUE_MAP_WILDERNESS_50, TreasureTrailClues.CLUE_EMOTE_PANIC_WILDERNESS_VOLCANO,
			TreasureTrailClues.CLUE_EMOTE_SHRUG_WILDERNESS_ZAMORAK_TEMPLE,
			TreasureTrailClues.CLUE_EMOTE_YAWN_WILDERNESS_ROGUE_STORE, TreasureTrailClues.CLUE_EMOTE_BOW_LIGHTHOUSE,
			// Elite specific clues
			TreasureTrailClues.CLUE_LAUGH_FOUNTAIN_OF_HEROES, TreasureTrailClues.CLUE_BOW_EDGEVILLE_MONASTERY,
			TreasureTrailClues.CLUE_ANAGRAM_ONEIROMANCER, TreasureTrailClues.CLUE_MAP_CASTLE_WARS,
			TreasureTrailClues.CLUE_ANAGRAM_OLD_CRONE, TreasureTrailClues.CLUE_ANAGRAM_MANDRITH,
			TreasureTrailClues.CLUE_RIDDLE_GYPSY_ARIS, TreasureTrailClues.CLUE_RIDDLE_WYSON,
			TreasureTrailClues.CLUE_RIDDLE_WARRIORS_GUILD, TreasureTrailClues.CLUE_RIDDLE_VANNAKA),
			ClueScrollRewards.ELITE_REWARDS_TABLE),

	MASTER(10, 12, 19835, 19836, ImmutableList.of(TreasureTrailClues.CLUE_ANAGRAM_LOWE,
			TreasureTrailClues.CLUE_ANAGRAM_BARTENDER, TreasureTrailClues.CLUE_ANAGRAM_BOB,
			TreasureTrailClues.CLUE_ANAGRAM_KENT, TreasureTrailClues.CLUE_ANAGRAM_MATTHIAS,
			TreasureTrailClues.CLUE_ANAGRAM_PAUL, TreasureTrailClues.CLUE_ANAGRAM_PROSPECTOR_PERCY,
			TreasureTrailClues.CLUE_MAP_FALADOR_CROSSROADS, TreasureTrailClues.CLUE_MAP_ARDOUGNE_LEGENDS_GUILD,
			TreasureTrailClues.CLUE_MAP_RELLEKA_LIGHTHOUSE, TreasureTrailClues.CLUE_MAP_SEERS_TO_RELLEKA,
			TreasureTrailClues.CLUE_MAP_NORTH_FALADOR_RUINS, TreasureTrailClues.CLUE_MAP_CHAMPIONS_GUILD,
			TreasureTrailClues.CLUE_MAP_VARROCK_EAST_MINE, TreasureTrailClues.CLUE_MAP_DRAYNOR,
			TreasureTrailClues.CLUE_MAP_WEST_KHAZARD_BATTLEFIELD, TreasureTrailClues.CLUE_MAP_YANILLE,
			TreasureTrailClues.CLUE_RIDDLE_FALADOR_CRATE, TreasureTrailClues.CLUE_RIDDLE_OZIACH,
			TreasureTrailClues.CLUE_RIDDLE_EDGEVILLE_YEW_TREE, TreasureTrailClues.CLUE_RIDDLE_DWARVEN_MINE_CART,
			TreasureTrailClues.CLUE_RIDDLE_LUMBRIDGE_CASTLE_DRAWERS, TreasureTrailClues.CLUE_RIDDLE_KAMFREENA,
			TreasureTrailClues.CLUE_RIDDLE_WIZARDS_TOWER, TreasureTrailClues.CLUE_EMOTE_LAUGH_FALADOR_BAR,
			TreasureTrailClues.CLUE_EMOTE_CRY_SEERS_BANK, TreasureTrailClues.CLUE_EMOTE_CHEER_GAME_ROOM,
			TreasureTrailClues.CLUE_EMOTE_JIG_FISHING_GUILD, TreasureTrailClues.CLUE_EMOTE_SPIN_VARROCK_CASTLE,
			TreasureTrailClues.CLUE_EMOTE_WAVE_LUMBER_YARD, TreasureTrailClues.CLUE_EMOTE_PANIC_YANILLE_BANK,
			TreasureTrailClues.CLUE_EMOTE_BOW_LEGENDS_GUILD, TreasureTrailClues.CLUE_COORDINATE_KARAMJA,
			TreasureTrailClues.CLUE_COORDINATE_BRIMHAVEN_GOLD_MINE, TreasureTrailClues.CLUE_COORDINATE_HAM_ENTRANCE,
			TreasureTrailClues.CLUE_COORDINATE_DRAYNOR_MINE, TreasureTrailClues.CLUE_COORDINATE_SLAYER_TOWER,
			TreasureTrailClues.CLUE_MAP_WILDERNESS_50, TreasureTrailClues.CLUE_EMOTE_PANIC_WILDERNESS_VOLCANO,
			TreasureTrailClues.CLUE_EMOTE_SHRUG_WILDERNESS_ZAMORAK_TEMPLE,
			TreasureTrailClues.CLUE_EMOTE_YAWN_WILDERNESS_ROGUE_STORE, TreasureTrailClues.CLUE_EMOTE_BOW_LIGHTHOUSE,
			TreasureTrailClues.CLUE_LAUGH_FOUNTAIN_OF_HEROES, TreasureTrailClues.CLUE_BOW_EDGEVILLE_MONASTERY,
			TreasureTrailClues.CLUE_ANAGRAM_ONEIROMANCER, TreasureTrailClues.CLUE_MAP_CASTLE_WARS,
			TreasureTrailClues.CLUE_ANAGRAM_OLD_CRONE, TreasureTrailClues.CLUE_ANAGRAM_MANDRITH,
			TreasureTrailClues.CLUE_RIDDLE_GYPSY_ARIS, TreasureTrailClues.CLUE_RIDDLE_WYSON,
			TreasureTrailClues.CLUE_RIDDLE_WARRIORS_GUILD, TreasureTrailClues.CLUE_RIDDLE_VANNAKA),
			ClueScrollRewards.MASTER_REWARDS_TABLE);

	private final int minTrailSize;
	private final int maxTrailSize;
	private final int clueScrollItemId;
	private final int casketItemId;
	private final List<TreasureTrailClue> clues;
	private final LootTable lootTable;

	ClueScrollType(final int minTrailSize, final int maxTrailSize, final int clueScrollItemId, final int casketItemId,
			final List<TreasureTrailClue> clues, final LootTable lootTable) {
		this.minTrailSize = minTrailSize;
		this.maxTrailSize = maxTrailSize;
		this.clueScrollItemId = clueScrollItemId;
		this.casketItemId = casketItemId;
		this.clues = clues;
		this.lootTable = lootTable;
	}

	public int getMinTrailSize() {
		return minTrailSize;
	}

	public int getMaxTrailSize() {
		return maxTrailSize;
	}

	public int getClueScrollItemId() {
		return clueScrollItemId;
	}

	public int getCasketItemId() {
		return casketItemId;
	}

	public List<TreasureTrailClue> getClues() {
		return clues;
	}

	public LootTable getLootTable() {
		return lootTable;
	}

	public static ClueScrollType forClueScrollItemId(int clueScrollItemId) {
		return Arrays.stream(values()).filter(c -> c.getClueScrollItemId() == clueScrollItemId).findFirst()
				.orElse(null);
	}
}
