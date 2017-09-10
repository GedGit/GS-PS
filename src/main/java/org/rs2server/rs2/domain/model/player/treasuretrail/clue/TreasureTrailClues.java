package org.rs2server.rs2.domain.model.player.treasuretrail.clue;

import com.google.common.collect.ImmutableList;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.boundary.Area;

/**
 * Defines all of the possible treasure trail clues.
 *
 * @author tommo
 */
public class TreasureTrailClues {

	public static final TreasureTrailClue CLUE_MAP_FALADOR_CROSSROADS = new TreasureTrailMapClue(2970, 3415, 0, 337);
	public static final TreasureTrailClue CLUE_MAP_WILDERNESS_50 = new TreasureTrailMapClue(3021, 3911, 0, 338);
	public static final TreasureTrailClue CLUE_MAP_ARDOUGNE_LEGENDS_GUILD = new TreasureTrailMapClue(2723, 3338, 0, 339);
	public static final TreasureTrailClue CLUE_MAP_RELLEKA_LIGHTHOUSE = new TreasureTrailMapClue(2578, 3597, 0, 343);
	public static final TreasureTrailClue CLUE_MAP_SEERS_TO_RELLEKA = new TreasureTrailMapClue(2666, 3562, 0, 344);
	public static final TreasureTrailClue CLUE_MAP_WEST_KHAZARD_BATTLEFIELD = new TreasureTrailMapClue(2456, 3230, 0, 342);
	public static final TreasureTrailClue CLUE_MAP_CHAMPIONS_GUILD = new TreasureTrailMapClue(3166, 3360, 0, 346);
	public static final TreasureTrailClue CLUE_MAP_VARROCK_EAST_MINE = new TreasureTrailMapClue(3289, 3374, 0, 347);
	public static final TreasureTrailClue CLUE_MAP_DRAYNOR = new TreasureTrailMapClue(3092, 3227, 0, 348);
	public static final TreasureTrailClue CLUE_MAP_NORTH_FALADOR_RUINS = new TreasureTrailMapClue(3043, 3398, 0, 351);
	public static final TreasureTrailClue CLUE_MAP_YANILLE = new TreasureTrailMapClue(2616, 3077, 0, 353);
	public static final TreasureTrailClue CLUE_MAP_CASTLE_WARS = new TreasureTrailMapClue(2450, 3129, 0, 86);


	public static final TreasureTrailClue CLUE_ANAGRAM_LOWE = new TreasureTrailAnagramClue(536, "El Ow");
	public static final TreasureTrailClue CLUE_ANAGRAM_PROSPECTOR_PERCY = new TreasureTrailAnagramClue(6562, "Copper Ore Crypts");

	public static final TreasureTrailClue CLUE_ANAGRAM_BARTENDER = new TreasureTrailAnagramClue(687, "Bad Renter");
	public static final TreasureTrailClue CLUE_ANAGRAM_KENT = new TreasureTrailAnagramClue(5074, "Nekt");
	public static final TreasureTrailClue CLUE_ANAGRAM_PAUL = new TreasureTrailAnagramClue(317, "La Up");
	public static final TreasureTrailClue CLUE_ANAGRAM_MATTHIAS = new TreasureTrailAnagramClue(1340, "Aha Mitts");
	public static final TreasureTrailClue CLUE_ANAGRAM_BOB = new TreasureTrailAnagramClue(505, "boB");
	public static final TreasureTrailClue CLUE_ANAGRAM_ONEIROMANCER = new TreasureTrailAnagramClue(3835, "Career In Moon");
	public static final TreasureTrailClue CLUE_ANAGRAM_OLD_CRONE = new TreasureTrailAnagramClue(2996, "Cool Nerd");
	public static final TreasureTrailClue CLUE_ANAGRAM_MANDRITH = new TreasureTrailAnagramClue(6599, "Dr Hitman");

	public static final TreasureTrailClue CLUE_EMOTE_LAUGH_FALADOR_BAR = new TreasureTrailEmoteClue(new Area(2953, 3366, 2960, 3374, 0), Animation.Emote.LAUGH, null,
			"in Falador bar.");

    public static final TreasureTrailClue CLUE_EMOTE_CHEER_GAME_ROOM = new TreasureTrailEmoteClue(new Area(2194, 4946, 2221, 4973, 0), Animation.Emote.CHEER, null, "at the games room.");

	public static final TreasureTrailClue CLUE_EMOTE_JIG_FISHING_GUILD = new TreasureTrailEmoteClue(new Area(2604, 3388, 2616, 3393, 0), Animation.Emote.JIG, ImmutableList.of(new Item(1694), new Item(1103), new Item(1639)), "by the entrance of the Fishing Guild.");

    public static final TreasureTrailClue CLUE_EMOTE_SPIN_VARROCK_CASTLE = new TreasureTrailEmoteClue(new Area(3203, 3458, 3223, 3465, 0), Animation.Emote.SPIN, ImmutableList.of(new Item(1361), new Item(1169), new Item(1641)), "in the Varrock Castle courtyard.");

	public static final TreasureTrailClue CLUE_EMOTE_WAVE_LUMBER_YARD = new TreasureTrailEmoteClue(new Area(3305, 3489, 3313, 3492, 0), Animation.Emote.WAVE, ImmutableList.of(new Item(1131), new Item(1095), new Item(1351)), "along the south fence of the Lumber Yard.");

    public static final TreasureTrailClue CLUE_EMOTE_CRY_SEERS_BANK = new TreasureTrailEmoteClue(new Area(2719, 3486, 2731, 3497, 0), Animation.Emote.CRY,
			ImmutableList.of(new Item(1163, 1), new Item(1099, 1), new Item(1393, 1)),
			"in Seers Village bank.");

	public static final TreasureTrailClue CLUE_EMOTE_PANIC_YANILLE_BANK = new TreasureTrailEmoteClue(new Area(2609, 3088, 2614, 3097, 0), Animation.Emote.PANIC,
			ImmutableList.of(new Item(1727, 1), new Item(1311, 1), new Item(1063, 1)),
			"in Yanille bank.");

	public static final TreasureTrailClue CLUE_EMOTE_BOW_LEGENDS_GUILD = new TreasureTrailEmoteClue(new Area(2725, 3348, 2732, 3349, 0), Animation.Emote.BOW,
			ImmutableList.of(new Item(1067, 1), new Item(1478, 1), new Item(845, 1)),
			"outside the entrance to the Legends' Guild.");

	public static final TreasureTrailClue CLUE_EMOTE_PANIC_WILDERNESS_VOLCANO = new TreasureTrailEmoteClue(new Area(3362, 3935, 3370, 3937, 0), Animation.Emote.PANIC,
			ImmutableList.of(new Item(1093, 1), new Item(10828, 1), new Item(1540, 1)),
			"on the Wilderness volcano bridge.");

	public static final TreasureTrailClue CLUE_EMOTE_SHRUG_WILDERNESS_ZAMORAK_TEMPLE = new TreasureTrailEmoteClue(new Area(3234, 3606, 3244, 3613, 0), Animation.Emote.SHRUG,
			ImmutableList.of(new Item(1079, 1), new Item(577, 1), new Item(1065, 1)),
			"in the Zamorak temple found in the Eastern Wilderness.");

	public static final TreasureTrailClue CLUE_EMOTE_YAWN_WILDERNESS_ROGUE_STORE = new TreasureTrailEmoteClue(new Area(3024, 3699, 3047, 3704, 0), Animation.Emote.YAWN,
			ImmutableList.of(new Item(1275, 1), new Item(1181, 1), new Item(1725, 1)),
			"in the rogues' general store.");

	public static final TreasureTrailClue CLUE_EMOTE_BOW_LIGHTHOUSE = new TreasureTrailEmoteClue(new Area(2504, 3635, 2514, 3645, 2), Animation.Emote.BOW,
			ImmutableList.of(new Item(2487), new Item(2499)), "at the top of the lighthouse.");

	public static final TreasureTrailClue CLUE_LAUGH_FOUNTAIN_OF_HEROES = new TreasureTrailEmoteClue(new Area(2916, 9891, 2919, 9895, 0), Animation.Emote.LAUGH,
			ImmutableList.of(new Item(3389), new Item(1303), new Item(11840)), "by the fountain of heroes.");

	public static final TreasureTrailClue CLUE_BOW_EDGEVILLE_MONASTERY = new TreasureTrailEmoteClue(new Area(3044, 3482, 3059, 3499, 1), Animation.Emote.BOW,
			ImmutableList.of(new Item(3842)), "upstairs in the Edgeville Monastery.");


	public static final TreasureTrailClue CLUE_RIDDLE_FALADOR_CRATE = new TreasureTrailRiddleClue("Look in the ground floor crates of houses in Falador.",
			-1, 24088, null);
	public static final TreasureTrailClue CLUE_RIDDLE_OZIACH = new TreasureTrailRiddleClue("A strange little man who sells armour only to those who've proven themselves to be unafraid of dragons.",
			822, -1, null);
	public static final TreasureTrailClue CLUE_RIDDLE_EDGEVILLE_YEW_TREE = new TreasureTrailRiddleClue("Come to the evil ledge. Yew know yew want to. Try not to get stung.",
			-1, -1, new Area(3088, 3468, 3091, 3472, 0));
	public static final TreasureTrailClue CLUE_RIDDLE_DWARVEN_MINE_CART = new TreasureTrailRiddleClue("It seems to have reached the end of the line, and it's still empty.",
			-1, 6045, null);
	public static final TreasureTrailClue CLUE_RIDDLE_LUMBRIDGE_CASTLE_DRAWERS = new TreasureTrailRiddleClue("My home is grey, and made of stone; A castle with a search for a meal."
			+ " Hidden in some drawers I am, across from a wooden wheel.",
			-1, 5618, null);
	public static final TreasureTrailClue CLUE_RIDDLE_KAMFREENA = new TreasureTrailRiddleClue("I am the one who watches the giants. The giants in turn watch me. I watch with two while they watch with one. Come seek where I may be.",
			2461, -1, null);
	public static final TreasureTrailClue CLUE_RIDDLE_WIZARDS_TOWER = new TreasureTrailRiddleClue("Search a bookcase in the Wizards tower.",
			-1, 12539, null);
	public static final TreasureTrailClue CLUE_RIDDLE_WYSON = new TreasureTrailRiddleClue("Speak to a Wyse man.",
			3253, -1, null);
	public static final TreasureTrailClue CLUE_RIDDLE_GYPSY_ARIS = new TreasureTrailRiddleClue("Varrock is where I reside not the land of the dead, but I am so old, I should be there instead. Let's hope your reward is as good as it says, just 1 gold one and you can have it read.",
			5082, -1, null);
	public static final TreasureTrailClue CLUE_RIDDLE_WARRIORS_GUILD = new TreasureTrailRiddleClue("W marks the spot.",
			-1, -1, new Area(2862, 3537, 2875, 3555, 0));

	public static final TreasureTrailClue CLUE_RIDDLE_VANNAKA = new TreasureTrailRiddleClue("You were 3 and I was the 6th. Come speak to me.",
			403, -1, null);



	public static final TreasureTrailClue CLUE_COORDINATE_KARAMJA = new TreasureTrailCoordinateClue("00 degrees 13 minutes south,<br>13 degrees 58 minutes east", 2888, 3153, 0);
	public static final TreasureTrailClue CLUE_COORDINATE_BRIMHAVEN_GOLD_MINE = new TreasureTrailCoordinateClue("00 degrees 18 minutes south,<br>09 degrees 28 minutes east", 2743, 3151, 0);
	public static final TreasureTrailClue CLUE_COORDINATE_HAM_ENTRANCE = new TreasureTrailCoordinateClue("02 degrees 48 minutes north,<br>22 degrees 30 minutes east", 3161, 3251, 0);
	public static final TreasureTrailClue CLUE_COORDINATE_DRAYNOR_MINE = new TreasureTrailCoordinateClue("06 degrees 58 minutes north,<br>21 degrees 16 minutes east", 3120, 3384, 0);
	public static final TreasureTrailClue CLUE_COORDINATE_SLAYER_TOWER = new TreasureTrailCoordinateClue("11 degrees 03 minutes north,<br>31 degrees 20 minutes east", 3442, 3515, 0);
	public static final TreasureTrailClue CLUE_COORDINATE_ICE_MOUNTAIN = new TreasureTrailCoordinateClue("09 degrees 48 minutes north,<br>17 degrees 39 minutes east", 3006, 3475, 0);
	public static final TreasureTrailClue CLUE_COORDINATE_BURTHORPE = new TreasureTrailCoordinateClue("11 degrees 41 minutes north,<br>14 degrees 58 minutes east", 2920, 3534, 0);

}
