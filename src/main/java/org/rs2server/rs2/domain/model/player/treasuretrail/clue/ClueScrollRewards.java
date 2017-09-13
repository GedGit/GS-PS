package org.rs2server.rs2.domain.model.player.treasuretrail.clue;

import org.rs2server.rs2.domain.service.api.loot.Loot;
import org.rs2server.rs2.domain.service.api.loot.LootTable;

/**
 * Defines clue scroll rewards.
 *
 * @author tommo
 */
public class ClueScrollRewards {

	public static final LootTable JUNK_REWARDS_TABLE = LootTable.of(

			Loot.of(1269, 30), // Steel Pickaxe

			Loot.of(849, 30), // Willow Shortbow

			Loot.of(1271, 30), // Adamant Pickaxe

			Loot.of(1381, 30), // Staff of Air

			Loot.of(1169, 30), // Coif

			Loot.of(1095, 30), // Leather Chaps

			Loot.of(1131, 30), // Hardleather Body

			// Black items
			Loot.of(1217, 30), // Black Dagger
			Loot.of(1367, 30), // Black Battleaxe
			Loot.of(1361, 30), // Black Axe
			Loot.of(1297, 30), // Black Longsword
			Loot.of(1077, 30), // Black Platelegs
			Loot.of(1125, 30), // Black Platebody
			Loot.of(1165, 30) // Black Full helm
	);

	public static final LootTable BASIC_REWARDS_TABLE = LootTable.of(

			Loot.of(995, 5000, 25000, 75), // Cash money

			Loot.of(4561, 5, 50, 75), // Purple sweets

			Loot.of(380, 5, 60, 30), // lobsters

			Loot.of(556, 80, 300, 30), // Air Runes

			Loot.of(554, 80, 300, 30), // Fire Runes

			Loot.of(555, 80, 300, 30), // Water Runes

			Loot.of(557, 80, 300, 30), // Earth Runes

			Loot.of(558, 80, 300, 30), // Mind Runes

			Loot.of(559, 15, 45, 30), // Body Runes

			Loot.of(561, 15, 60, 30), // Nature Runes

			Loot.of(562, 30, 60, 30), // Chaos Runes

			Loot.of(563, 15, 75, 30), // Law Runes

			Loot.of(565, 15, 75, 30), // Blood Runes

			Loot.of(1725, 30), // Amulet of Strength

			Loot.of(1727, 30), // Amulet of Magic

			Loot.of(1731, 30), // Amulet of Power

			Loot.of(1478, 30), // Amulet of Accuracy

			// Black d'hide
			Loot.of(2491, 1, 2, 20), // vambs
			Loot.of(2497, 1, 2, 20), // chaps
			Loot.of(2503, 1, 2, 20), // body

			// Dragon
			Loot.of(1377, 15), // Battleaxe
			Loot.of(1434, 15), // Mace

			// Rune
			Loot.of(1359, 20), // Axe
			Loot.of(1275, 20), // Pickaxe
			Loot.of(1163, 20), // Full helm
			Loot.of(1127, 20), // Platebody
			Loot.of(1079, 20), // Platelegs
			Loot.of(1201, 20), // Kiteshield
			Loot.of(9185, 20), // Crossbow

			// Magic shortbow
			Loot.of(861, 20));

	public static final LootTable EASY_REWARDS_TABLE = JUNK_REWARDS_TABLE.merge(BASIC_REWARDS_TABLE).merge(LootTable.of(
			// Bronze (t)
			Loot.of(12215, 10), Loot.of(12217, 10), Loot.of(12219, 10), Loot.of(12221, 10), Loot.of(12223, 10),

			// Bronze (g)
			Loot.of(12205, 10), // body
			Loot.of(12207, 10), // legs
			Loot.of(12209, 10), // skirt
			Loot.of(12211, 10), // helm
			Loot.of(12213, 10), // kite

			// Iron (t)
			Loot.of(12225, 10), Loot.of(12227, 10), Loot.of(12229, 10), Loot.of(12231, 10), Loot.of(12233, 10),

			// Iron (g)
			Loot.of(12235, 10), // body
			Loot.of(12237, 10), // legs
			Loot.of(12239, 10), // skirt
			Loot.of(12241, 10), // helm
			Loot.of(12243, 10), // kite

			// Black (t)
			Loot.of(2583, 10), Loot.of(2585, 10), Loot.of(2587, 10), Loot.of(2589, 10),

			// Black (g)
			Loot.of(2591, 10), // body
			Loot.of(2593, 10), // legs
			Loot.of(2595, 10), // helm
			Loot.of(2597, 10), // kite

			// Berets
			Loot.of(2633, 10), Loot.of(2635, 10), Loot.of(2637, 10), Loot.of(12247, 10),

			// Highwayman mask
			Loot.of(2631, 10),

			// Beanie
			Loot.of(12245, 10),

			// Blue wizard (t)
			Loot.of(7388, 10), // skirt
			Loot.of(7392, 10), // robe
			Loot.of(7396, 10), // hat

			// Blue wizard (g)
			Loot.of(7386, 10), // skirt
			Loot.of(7390, 10), // robe
			Loot.of(7394, 10), // hat

			// Black wizard (t)
			Loot.of(12447, 10), // skirt
			Loot.of(12451, 10), // robe
			Loot.of(12455, 10), // hat

			// Black wizard (g)
			Loot.of(12445, 10), // skirt
			Loot.of(12449, 10), // robe
			Loot.of(12453, 10), // hat

			// Studded leather (t)
			Loot.of(7364, 10), // body
			Loot.of(7368, 10), // chaps

			// Studded leather (g)
			Loot.of(7362, 10), // body
			Loot.of(7366, 10), // chaps

			// Blue elegant
			Loot.of(10408, 10), // shirt
			Loot.of(10410, 10), // legs

			// Red elegant
			Loot.of(10404, 10), // shirt
			Loot.of(10406, 10), // legs

			// Green elegant
			Loot.of(10412, 10), // shirt
			Loot.of(10414, 10), // legs

			// Amulet of magic (t)
			Loot.of(10366, 10),

			// Black cane
			Loot.of(12375, 10),

			// Guthix robe
			Loot.of(10462, 10), // top
			Loot.of(10466, 10), // legs

			// Saradomin robe
			Loot.of(10458, 10), // top
			Loot.of(10464, 10), // legs

			// Zamorak robe
			Loot.of(10460, 10), // top
			Loot.of(10468, 10), // legs

			// Bandos robe
			Loot.of(12265, 10), // top
			Loot.of(12267, 10), // legs

			// Armadyl robe
			Loot.of(12253, 10), // top
			Loot.of(12255, 10), // legs

			// Imp mask
			Loot.of(12249, 10),

			// Golden chef's hat
			Loot.of(20205, 10),

			// Golden apron
			Loot.of(20208, 10),

			// monk top g
			Loot.of(20199, 10),

			// monk bot g
			Loot.of(20202, 10),

			// team cape zero
			Loot.of(20211, 5),

			// team cape x
			Loot.of(20214, 5),

			// team cape i
			Loot.of(20217, 5),

			// holy blessing
			Loot.of(20220, 4),
			// unholy blessing
			Loot.of(20223, 4),
			// peaceful blessing
			Loot.of(20226, 4),
			// honourable blessing
			Loot.of(20229, 4),
			// war blessing
			Loot.of(20232, 4),
			// ancient blessing
			Loot.of(20235, 4),

			// Goblin mask
			Loot.of(12251, 10)));

	public static final LootTable MEDIUM_REWARDS_TABLE = JUNK_REWARDS_TABLE.merge(BASIC_REWARDS_TABLE)
			.merge(LootTable.of(
					// Mithril (t)
					Loot.of(12287, 10), Loot.of(12289, 10), Loot.of(12291, 10), Loot.of(12293, 10), Loot.of(12295, 10),

					// Mithril (g)
					Loot.of(12277, 10), // body
					Loot.of(12279, 10), // legs
					Loot.of(12281, 10), // kite
					Loot.of(12283, 10), // helm
					Loot.of(12285, 10), // skirt

					// Adamant (t)
					Loot.of(2599, 10), // body
					Loot.of(2601, 10), // legs
					Loot.of(2603, 10), // kite
					Loot.of(2605, 10), // helm

					// Adamant (g)
					Loot.of(2607, 10), // body
					Loot.of(2609, 10), // legs
					Loot.of(2611, 10), // kite
					Loot.of(2613, 10), // helm

					// steel (g)
					Loot.of(20169, 10), // body
					Loot.of(20172, 10), // legs
					Loot.of(20175, 10), // skirt
					Loot.of(20178, 10), // helm
					Loot.of(20181, 10), // shield

					// steel (t)
					Loot.of(20184, 10), // body
					Loot.of(20187, 10), // legs
					Loot.of(20190, 10), // skirt
					Loot.of(20193, 10), // helm
					Loot.of(20196, 10), // shield
					
					// wooden shield (g)
					Loot.of(20166, 10),

					// Ranger boots
					Loot.of(2577, 10),

					// Holy sandals
					Loot.of(12598, 10),

					// Wizard boots
					Loot.of(2579, 10),

					// Halos
					Loot.of(12637, 10), Loot.of(12638, 10), Loot.of(12639, 10),

					// Headbands
					Loot.of(2645, 10), // red
					Loot.of(2647, 10), // black
					Loot.of(2649, 10), // brown
					Loot.of(12299, 10), // white
					Loot.of(12301, 10), // blue
					Loot.of(12303, 10), // gold
					Loot.of(12305, 10), // pink
					Loot.of(12307, 10), // green

					// Boaters
					Loot.of(7319, 10), // red
					Loot.of(7321, 10), // orange
					Loot.of(7323, 10), // green
					Loot.of(7325, 10), // blue
					Loot.of(7327, 10), // black
					Loot.of(12309, 10), // pink
					Loot.of(12311, 10), // purple
					Loot.of(12313, 10), // white

					// Green d'hide (t)
					Loot.of(7372, 10), // body
					Loot.of(7380, 10), // chaps

					// Green d'hide (g)
					Loot.of(7370, 10), // body
					Loot.of(7378, 10), // chaps

					// Black elegant
					Loot.of(10400, 10), // shirt
					Loot.of(10402, 10), // legs

					// White elegant
					Loot.of(10420, 10), // shirt
					Loot.of(10422, 10), // legs

					// Purple elegant
					Loot.of(10416, 10), // shirt
					Loot.of(10418, 10), // legs

					// Pink elegant
					Loot.of(12315, 10), // shirt
					Loot.of(12317, 10), // legs

					// Gold elegant
					Loot.of(12347, 10), // shirt
					Loot.of(12349, 10), // legs

					// Strength amulet (t)
					Loot.of(10364, 10),

					// God mitres
					Loot.of(10452, 10), // saradomin
					Loot.of(10454, 10), // guthix
					Loot.of(10456, 10), // zamorak
					Loot.of(12203, 10), // ancient
					Loot.of(12259, 10), // armadyl
					Loot.of(12271, 10), // bandos

					// God cloaks
					Loot.of(10446, 10), // saradomin
					Loot.of(10448, 10), // guthix
					Loot.of(10450, 10), // zamorak
					Loot.of(12197, 10), // ancient
					Loot.of(12261, 10), // armadyl
					Loot.of(12273, 10), // bandos

					// Penguin mask
					Loot.of(12428, 10),

					// Cat mask
					Loot.of(12361, 10),

					// Crier hat
					Loot.of(12319, 10),

					// black leprechaun hat
					Loot.of(20246, 10),

					// black uni mask
					Loot.of(20266, 10),

					// holy blessing
					Loot.of(20220, 4),
					// unholy blessing
					Loot.of(20223, 4),
					// peaceful blessing
					Loot.of(20226, 4),
					// honourable blessing
					Loot.of(20229, 4),
					// war blessing
					Loot.of(20232, 4),
					// ancient blessing
					Loot.of(20235, 4),

					// white uni mask
					Loot.of(20269, 10),

					// Leprechaun hat
					Loot.of(12359, 10)));

	public static final LootTable HARD_REWARDS_TABLE = JUNK_REWARDS_TABLE.merge(BASIC_REWARDS_TABLE).merge(LootTable.of(
			// Rune (t)
			Loot.of(2623, 10), // body
			Loot.of(2625, 10), // legs
			Loot.of(2627, 10), // full helm
			Loot.of(2629, 10), // kite
			Loot.of(3477, 10), // skirt

			// Dragon masks
			Loot.of(12518, 10), // Green dragon mask
			Loot.of(12520, 10), // Blue dragon mask
			Loot.of(12522, 10), // Red dragon mask
			Loot.of(12524, 10), // Black dragon mask

			// Rune (g)
			Loot.of(2615, 10), // body
			Loot.of(2617, 10), // legs
			Loot.of(2619, 10), // full helm
			Loot.of(2621, 10), // kite
			Loot.of(3476, 10), // skirt

			// Rune (guthix)
			Loot.of(2669, 10), // body
			Loot.of(2671, 10), // legs
			Loot.of(2673, 10), // full helm
			Loot.of(2675, 10), // kite
			Loot.of(3480, 10), // skirt

			// Rune (saradomin)
			Loot.of(2661, 10), // body
			Loot.of(2663, 10), // legs
			Loot.of(2665, 10), // full helm
			Loot.of(2667, 10), // kite
			Loot.of(3479, 10), // skirt

			// Rune (zamorak)
			Loot.of(2653, 10), // body
			Loot.of(2655, 10), // legs
			Loot.of(2657, 10), // full helm
			Loot.of(2659, 10), // kite
			Loot.of(3478, 10), // skirt

			// Gilded
			Loot.of(3481, 3), // body
			Loot.of(3483, 3), // legs
			Loot.of(3485, 3), // skirt
			Loot.of(3486, 3), // full helm
			Loot.of(3488, 3), // kite
			Loot.of(20146, 3), // med helm
			Loot.of(20149, 3), // chainbody
			Loot.of(20152, 3), // sq shield

			// Blue d'hide (t)
			Loot.of(7376, 10), // body
			Loot.of(7384, 10), // chaps

			// Blue d'hide (g)
			Loot.of(7374, 10), // body
			Loot.of(7382, 10), // chaps

			// Red d'hide (t)
			Loot.of(12331, 10), // body
			Loot.of(12333, 10), // chaps

			// Red d'hide (g)
			Loot.of(12327, 10), // body
			Loot.of(12329, 10), // chaps

			// Enchanted
			Loot.of(7398, 10), // robe bottom
			Loot.of(7399, 10), // robe top
			Loot.of(7400, 10), // hat

			// Robin hood hat
			Loot.of(2581, 5),

			// Cavaliers
			Loot.of(2639, 10), // tan
			Loot.of(2641, 10), // dark
			Loot.of(2643, 10), // black
			Loot.of(12321, 10), // white
			Loot.of(12323, 10), // red
			Loot.of(12325, 10), // navy

			// Infinity Set
			Loot.of(6916, 10), // top
			Loot.of(6918, 10), // hat
			Loot.of(6920, 10), // boots
			Loot.of(6922, 10), // gloves
			Loot.of(6924, 10), // bottoms
			Loot.of(6916, 10), // top

			// Mage's book
			Loot.of(6889, 10), // mage's book

			// Master wand
			Loot.of(6914, 10), // master wand

			// Pirate's hat
			Loot.of(2651, 10),

			// Third-age (range)
			Loot.of(10330, 1), // top
			Loot.of(10332, 1), // legs
			Loot.of(10334, 1), // coif
			Loot.of(10336, 1), // vambs

			// Third-age (mage)
			Loot.of(10338, 1), // robe top
			Loot.of(10340, 1), // robe bottom
			Loot.of(10342, 1), // hat
			Loot.of(10344, 1), // amulet

			// Third-age (melee)
			Loot.of(10346, 1), // legs
			Loot.of(10348, 1), // platebody
			Loot.of(10350, 1), // helmet
			Loot.of(10352, 1), // kite

			// Amulet of glory (t)
			Loot.of(10362, 10),

			// Guthix d'hide
			Loot.of(10376, 10), // vambs
			Loot.of(10378, 10), // body
			Loot.of(10380, 10), // chaps
			Loot.of(10382, 10), // coif

			// Saradomin d'hide
			Loot.of(10384, 10), // vambs
			Loot.of(10386, 10), // body
			Loot.of(10388, 10), // chaps
			Loot.of(10390, 10), // coif

			// Zamorak d'hide
			Loot.of(10368, 10), // vambs
			Loot.of(10370, 10), // body
			Loot.of(10372, 10), // chaps
			Loot.of(10374, 10), // coif

			// Armadyl d'hide
			Loot.of(12506, 10), // vambs
			Loot.of(12508, 10), // body
			Loot.of(12510, 10), // chaps
			Loot.of(12512, 10), // coif

			// Ancient d'hide
			Loot.of(12490, 10), // vambs
			Loot.of(12492, 10), // body
			Loot.of(12494, 10), // chaps
			Loot.of(12496, 10), // coif

			// Bandos d'hide
			Loot.of(12498, 10), // vambs
			Loot.of(12500, 10), // body
			Loot.of(12502, 10), // chaps
			Loot.of(12504, 10), // coif

			// Vestment stoles
			Loot.of(10470, 10), // saradomin
			Loot.of(10472, 10), // guthix
			Loot.of(10474, 10), // zamorak
			Loot.of(12257, 10), // armadyl
			Loot.of(12201, 10), // ancient
			Loot.of(12269, 10), // bandos

			// Vestment croziers
			Loot.of(10440, 10), // saradomin
			Loot.of(10442, 10), // guthix
			Loot.of(10444, 10), // zamorak
			Loot.of(12263, 10), // armadyl
			Loot.of(12199, 10), // ancient
			Loot.of(12275, 10), // bandos

			// Pith helmet
			Loot.of(12516, 10),

			// Explorer backpack
			Loot.of(12514, 10),

			// holy blessing
			Loot.of(20220, 4),
			// unholy blessing
			Loot.of(20223, 4),
			// peaceful blessing
			Loot.of(20226, 4),
			// honourable blessing
			Loot.of(20229, 4),
			// war blessing
			Loot.of(20232, 4),
			// ancient blessing
			Loot.of(20235, 4),

			// Rune cane
			Loot.of(12379, 10)));

	public static final LootTable ELITE_REWARDS_TABLE = JUNK_REWARDS_TABLE.merge(BASIC_REWARDS_TABLE)
			.merge(LootTable.of(

					// Potions
					Loot.of(6686, 25, 55, 3), // Saradomin Brew
					Loot.of(3025, 25, 55, 3), // Super restore
					Loot.of(11952, 25, 55, 3), // Extended antifire
					Loot.of(2445, 25, 55, 3), // Ranging potion

					Loot.of(989, 3), // Crystal key
					Loot.of(12357, 10), // Katana

					// Black D'hide (g)
					Loot.of(12381, 10), // body
					Loot.of(12383, 10), // chaps

					// Black D'hide (t)
					Loot.of(12385, 10), // body
					Loot.of(12387, 10), // chaps

					// Musketeer
					Loot.of(12351, 10), // hat
					Loot.of(12441, 10), // tabard
					Loot.of(12443, 10), // pants

					// Ornament kits
					Loot.of(12538, 10), // dragon full helm
					Loot.of(12534, 10), // dragon chain
					Loot.of(12536, 10), // dragon plate/skirt
					Loot.of(12532, 10), // dragon sq
					Loot.of(12530, 10), // light infinity
					Loot.of(12528, 10), // dark infinity
					Loot.of(12526, 10), // fury ornament kit

					// Hats
					Loot.of(12355, 10), // big pirate hat
					Loot.of(12430, 10), // afro
					Loot.of(12432, 10), // top hat
					Loot.of(12363, 10), // bronze dragon mask
					Loot.of(12365, 10), // iron dragon mask
					Loot.of(12367, 10), // steel dragon mask
					Loot.of(12369, 10), // mith dragon mask
					Loot.of(12371, 3), // lava dragon mask
					Loot.of(12337, 10), // sagacious spectacles
					Loot.of(12353, 10), // monocle
					Loot.of(12540, 10), // deerstalker

					Loot.of(12596, 10), // Rangers' tunic
					Loot.of(12335, 10), // Briefcase
					Loot.of(12373, 10), // Dragon cane

					// Books
					Loot.of(12608, 3), // war
					Loot.of(12610, 3), // law
					Loot.of(12612, 3), // darkness

					// Royal
					Loot.of(12393, 10), // Royal gown top
					Loot.of(12395, 10), // Royal gown bottom
					Loot.of(12397, 10), // Royal crown
					Loot.of(12439, 10), // Royal sceptre

					// Gilded
					Loot.of(3481, 3), // body
					Loot.of(3483, 3), // legs
					Loot.of(3485, 3), // skirt
					Loot.of(3486, 3), // full helm
					Loot.of(3488, 3), // kite
					Loot.of(12389, 3), // scimitar
					Loot.of(12391, 3), // boots
					Loot.of(20158, 3), // spear
					Loot.of(20161, 3), // hasta
					
					// Large spade
					Loot.of(20164, 6), // large spade

					// Third-age (range)
					Loot.of(10330, 1), // top
					Loot.of(10332, 1), // legs
					Loot.of(10334, 1), // coif
					Loot.of(10336, 1), // vambs

					// Third-age (mage)
					Loot.of(10338, 1), // robe top
					Loot.of(10340, 1), // robe bottom
					Loot.of(10342, 1), // hat
					Loot.of(10344, 1), // amulet

					// Third-age (melee)
					Loot.of(10346, 1), // legs
					Loot.of(10348, 1), // platebody
					Loot.of(10350, 1), // helmet
					Loot.of(10352, 1), // kite

					// Third-age (various)
					Loot.of(12426, 1), // longsword
					Loot.of(12424, 1), // bow
					Loot.of(12422, 1), // wand
					Loot.of(12437, 1), // cloak

					// holy blessing
					Loot.of(20220, 4),
					// unholy blessing
					Loot.of(20223, 4),
					// peaceful blessing
					Loot.of(20226, 4),
					// honourable blessing
					Loot.of(20229, 4),
					// war blessing
					Loot.of(20232, 4),
					// ancient blessing
					Loot.of(20235, 4),

					Loot.of(19991, 10), // bucket helm
					Loot.of(19994, 10), // ranger gloves
					Loot.of(19997, 10) // holy wraps
	));

	public static final LootTable MASTER_REWARDS_TABLE = JUNK_REWARDS_TABLE.merge(BASIC_REWARDS_TABLE)
			.merge(LootTable.of(

					// Potions
					Loot.of(6686, 25, 55, 3), // Saradomin Brew
					Loot.of(3025, 25, 55, 3), // Super restore
					Loot.of(11952, 25, 55, 3), // Extended antifire
					Loot.of(2445, 25, 55, 3), // Ranging potion

					Loot.of(989, 3), // Crystal key
					Loot.of(12357, 10), // Katana
					Loot.of(4561, 15), // purple sweets

					// Black D'hide (g)
					Loot.of(12381, 10), // body
					Loot.of(12383, 10), // chaps

					// Black D'hide (t)
					Loot.of(12385, 10), // body
					Loot.of(12387, 10), // chaps

					// Musketeer
					Loot.of(12351, 10), // hat
					Loot.of(12441, 10), // tabard
					Loot.of(12443, 10), // pants

					// Ornament kits
					Loot.of(12538, 10), // dragon full helm
					Loot.of(12534, 10), // dragon chain
					Loot.of(12536, 10), // dragon plate/skirt
					Loot.of(12532, 10), // dragon sq
					Loot.of(12530, 10), // light infinity
					Loot.of(12528, 10), // dark infinity
					Loot.of(12526, 10), // fury ornament kit

					// Hats
					Loot.of(12355, 10), // big pirate hat
					Loot.of(12430, 10), // afro
					Loot.of(12432, 10), // top hat
					Loot.of(12363, 10), // bronze dragon mask
					Loot.of(12365, 10), // iron dragon mask
					Loot.of(12367, 10), // steel dragon mask
					Loot.of(12369, 10), // mith dragon mask
					Loot.of(12371, 3), // lava dragon mask
					Loot.of(12337, 10), // sagacious spectacles
					Loot.of(12353, 10), // monocle
					Loot.of(12540, 10), // deerstalker

					Loot.of(12596, 10), // Rangers' tunic
					Loot.of(12335, 10), // Briefcase
					Loot.of(12373, 10), // Dragon cane

					// Books
					Loot.of(12608, 3), // war
					Loot.of(12610, 3), // law
					Loot.of(12612, 3), // darkness

					// Rune (t)
					Loot.of(2623, 10), // body
					Loot.of(2625, 10), // legs
					Loot.of(2627, 10), // full helm
					Loot.of(2629, 10), // kite
					Loot.of(3477, 10), // skirt

					// Dragon masks
					Loot.of(12518, 10), // Green dragon mask
					Loot.of(12520, 10), // Blue dragon mask
					Loot.of(12522, 10), // Red dragon mask
					Loot.of(12524, 10), // Black dragon mask

					// Rune (g)
					Loot.of(2615, 10), // body
					Loot.of(2617, 10), // legs
					Loot.of(2619, 10), // full helm
					Loot.of(2621, 10), // kite
					Loot.of(3476, 10), // skirt

					// Rune (guthix)
					Loot.of(2669, 10), // body
					Loot.of(2671, 10), // legs
					Loot.of(2673, 10), // full helm
					Loot.of(2675, 10), // kite
					Loot.of(3480, 10), // skirt

					// Rune (saradomin)
					Loot.of(2661, 10), // body
					Loot.of(2663, 10), // legs
					Loot.of(2665, 10), // full helm
					Loot.of(2667, 10), // kite
					Loot.of(3479, 10), // skirt

					// Rune (zamorak)
					Loot.of(2653, 10), // body
					Loot.of(2655, 10), // legs
					Loot.of(2657, 10), // full helm
					Loot.of(2659, 10), // kite
					Loot.of(3478, 10), // skirt

					// Rune
					Loot.of(1359, 20), // Axe
					Loot.of(1275, 20), // Pickaxe
					Loot.of(1163, 20), // Full helm
					Loot.of(1127, 20), // Platebody
					Loot.of(1079, 20), // Platelegs
					Loot.of(1201, 20), // Kiteshield
					Loot.of(9185, 20), // Crossbow

					// Black d'hide
					Loot.of(2491, 1, 2, 20), // vambs
					Loot.of(2497, 1, 2, 20), // chaps
					Loot.of(2503, 1, 2, 20), // body

					// Dragon
					Loot.of(1377, 15), // Battleaxe
					Loot.of(1434, 15), // Mace

					// Elder robes
					Loot.of(20517, 1), // Robe top
					Loot.of(20520, 1), // Robe bottoms

					// Guthix robe
					Loot.of(10462, 10), // top
					Loot.of(10466, 10), // legs

					// Saradomin robe
					Loot.of(10458, 10), // top
					Loot.of(10464, 10), // legs

					// Zamorak robe
					Loot.of(10460, 10), // top
					Loot.of(10468, 10), // legs

					// Bandos robe
					Loot.of(12265, 10), // top
					Loot.of(12267, 10), // legs

					// Armadyl robe
					Loot.of(12253, 10), // top
					Loot.of(12255, 10), // legs

					// Imp mask
					Loot.of(12249, 10),

					// Goblin mask
					Loot.of(12251, 10),

					// Infinity Set
					Loot.of(6916, 10), // top
					Loot.of(6918, 10), // hat
					Loot.of(6920, 10), // boots
					Loot.of(6922, 10), // gloves
					Loot.of(6924, 10), // bottoms
					Loot.of(6916, 10), // top

					// Mage's book
					Loot.of(6889, 10), // mage's book

					// Master wand
					Loot.of(6914, 10), // master wand

					// Royal
					Loot.of(12393, 10), // Royal gown top
					Loot.of(12395, 10), // Royal gown bottom
					Loot.of(12397, 10), // Royal crown
					Loot.of(12439, 10), // Royal sceptre

					// Gilded
					Loot.of(3481, 3), // body
					Loot.of(3483, 3), // legs
					Loot.of(3485, 3), // skirt
					Loot.of(3486, 3), // full helm
					Loot.of(3488, 3), // kite
					Loot.of(12389, 3), // scimitar
					Loot.of(12391, 3), // boots

					// Third-age (range)
					Loot.of(10330, 1), // top
					Loot.of(10332, 1), // legs
					Loot.of(10334, 1), // coif
					Loot.of(10336, 1), // vambs

					// Third-age (mage)
					Loot.of(10338, 1), // robe top
					Loot.of(10340, 1), // robe bottom
					Loot.of(10342, 1), // hat
					Loot.of(10344, 1), // amulet

					// Third-age (melee)
					Loot.of(10346, 1), // legs
					Loot.of(10348, 1), // platebody
					Loot.of(10350, 1), // helmet
					Loot.of(10352, 1), // kite

					// Third-age (various)
					Loot.of(12426, 1), // longsword
					Loot.of(12424, 1), // bow
					Loot.of(12422, 1), // wand
					Loot.of(12437, 1), // cloak
					Loot.of(20011, 1), // axe
					Loot.of(20014, 1), // pickaxe

					// holy blessing
					Loot.of(20220, 4),
					// unholy blessing
					Loot.of(20223, 4),
					// peaceful blessing
					Loot.of(20226, 4),
					// honourable blessing
					Loot.of(20229, 4),
					// war blessing
					Loot.of(20232, 4),
					// ancient blessing
					Loot.of(20235, 4),

					// Bloodhound
					Loot.of(19730, 1))); // Bloodhound
}
