package org.rs2server.rs2.model;


/**
 * Represents an in-game sound.
 * @author Michael
 *
 */
public class Sound {
	
    /**
     * 382 enter wilderness
     *
     * World sounds
     */

    /**
     * Item related world
     */
	
    public static final Sound DROP_COINS = Sound.create(10, 0);
    public static final Sound DROP = Sound.create(2739, 0);
    public static final Sound PICKUP = Sound.create(2582, 0);
    public static final Sound UNEQUIP = Sound.create(2238, 0);
    public static final Sound NO_INVENTORY_SPACE = Sound.create(2268, 0);
    public static final Sound EATING = Sound.create(2393, 0);
    public static final Sound DRINKING = Sound.create(2401, 0);

    /**
     * Object related world
     */

    public static final Sound BARROW_DOOR = Sound.create(41, 0);
    public static final Sound OPEN_CHEST = Sound.create(1775, 0);
    public static final Sound OPEN_METAL_GATE = Sound.create(70, 0);
    public static final Sound CLOSE_METAL_GATE = Sound.create(68, 0);
    public static final Sound OPEN_DOOR = Sound.create(81, 0);
    public static final Sound CLOSE_DOOR = Sound.create(43, 0);
    public static final Sound FORCE_DOOR = Sound.create(3419, 0);
    public static final Sound TELEPORT_MINI_ESS = Sound.create(125, 0);
    public static final Sound TELEPORT_OBELISK = Sound.create(204, 0);
    public static final Sound PULL_LEVER = Sound.create(2400, 0);
    public static final Sound ENTER_HOUSE = Sound.create(984, 0);
    public static final Sound HOPPER = Sound.create(3189, 0);
    public static final Sound POP_BALLOON = Sound.create(3252, 0);
    // 3692 To 3705 - Piano Keys
    public static final Sound CANNON_TURNING = Sound.create(2877, 0);

    /**
     * Misc related world
     */
    public static final Sound SOFT_WIND = Sound.create(2277, 0);
    public static final Sound STRONGER_WIND = Sound.create(2278, 0);
    public static final Sound LOUDER_WIND = Sound.create(2279, 0);
    public static final Sound VERY_STRONG_WIND = Sound.create(2280, 0);

    public static final Sound BANK_PIN_CORRECT = Sound.create(1040, 0);
    public static final Sound PRESSING_PIN = Sound.create(1041, 0);

    public static final Sound EARTHQUAKE = Sound.create(1464, 0);
    // 984

    /**
     * Skills related
     */
    public static final Sound CUTTING_GEM = Sound.create(2586, 0);

    public static final Sound PICK_FLAX = Sound.create(2581, 0);
    public static final Sound SPINNING_WHEEL = Sound.create(2590, 0);

    public static final Sound LIGH_FIRE_ATTEMPT = Sound.create(2599, 0);
    public static final Sound SUCCED_FIRE = Sound.create(2594, 0);

    public static final Sound CATCH_FISH = Sound.create(2600, 0);

    public static final Sound FLETCHING_SHAFT = Sound.create(760, 0);
    public static final Sound FLETCHING_BOW = Sound.create(761, 0);
    public static final Sound STRINGIN_BOW = Sound.create(2606, 0);

    public static final Sound MINE_ONCE = Sound.create(2656, 0);
    public static final Sound MINE_THIRD = Sound.create(2657, 0);
    public static final Sound MINE_FIFTH = Sound.create(2658, 0);
    public static final Sound MINE_FINISH = Sound.create(2659, 0);

    public static final Sound HAMMERING = Sound.create(3771, 0);

    public static final Sound COOKING = Sound.create(2577, 0);

    public static final Sound WOODCUT = Sound.create(3037, 0);

    public static final Sound BURY_BONES = Sound.create(2738, 0);
    public static final Sound BONES_ON_ALTAR = Sound.create(958, 0);

    public static final Sound THIEVING_STUNNED = Sound.create(1842, 0);
    public static final Sound PICKPOCKET_CHEST = Sound.create(37, 0);

    public static final Sound GENIE_LAMP = Sound.create(2655, 0);

    /**
     * Activate prayer TODO: SHARP EYE, THICK SKIN, RETRIBUTION
     */
    public static final Sound PRAYER_OFF = Sound.create(2663, 0);
    public static final Sound NO_PRAYER_LEFT = Sound.create(2672, 0);
    public static final Sound WRONG_LEVEL = Sound.create(2673, 0);
    public static final Sound RECHARGE = Sound.create(2674, 0);
    public static final Sound IMPROVED_REFLEXES = Sound.create(2662, 0);
    public static final Sound INCREDIBLE_REFLEXES = Sound.create(2667, 0);
    public static final Sound CLARITY_OF_THOUGHT = Sound.create(2664, 0);
    public static final Sound HAWK_EYE = Sound.create(2665, 0);
    public static final Sound EAGLE_EYE = Sound.create(2666, 0);
    public static final Sound MYSTIC_LORE = Sound.create(2668, 0);
    public static final Sound MISTIC_MIGHT = Sound.create(2669, 0);
    public static final Sound MISTIC_WILL = Sound.create(2670, 0);
    public static final Sound MAGIC_PROTECT = Sound.create(2675, 0);
    public static final Sound MELEE_PROTECT = Sound.create(2676, 0);
    public static final Sound RANGE_PROTECT = Sound.create(2677, 0);
    public static final Sound RAPID_HEAL = Sound.create(2678, 0);
    public static final Sound RAPID_RESTORE = Sound.create(2679, 0);
    public static final Sound REDEMPTION = Sound.create(2678, 0);
    public static final Sound REDEMPTION_USED = Sound.create(2679, 0);
    public static final Sound ROCK_SKIN = Sound.create(2684, 0);
    public static final Sound STEEL_SKIN = Sound.create(2687, 0);
    public static final Sound BURST_OF_STRENGTH = Sound.create(2689, 0);
    public static final Sound SUPERHUMAN_STRENGTH = Sound.create(2690, 0);
    public static final Sound ULTIMATE_STRENGTH = Sound.create(2691, 0);
    public static final Sound SMITE = Sound.create(2685, 0);
    public static final Sound PROT_ITEM = Sound.create(2671, 0);
    public static final Sound THICK_SKIN = Sound.create(10000, 0);
    public static final Sound SHARP_EYE = Sound.create(10000, 0);
    public static final Sound RETRIBUTION = Sound.create(10000, 0);

    /**
     * Other
     */

    public static final Sound FROZEN = Sound.create(154, 0);
    public static final Sound SPLASH = Sound.create(227, 0);

    /**
     * Non combat spells
     */
    public static final Sound HIGH_ALCHEMY = Sound.create(97, 0);
    public static final Sound LOW_ALCHEMY = Sound.create(98, 0);
    public static final Sound SUPER_HEAT_ITEM = Sound.create(114, 0);
    public static final Sound ENCHANTE_JEWELLERY = Sound.create(116, 0); //TODO: Identify 136 - 147
    public static final Sound TELE_GRAB = Sound.create(192, 0);
    public static final Sound HOME_TELEPORT_START = Sound.create(193, 0);
    public static final Sound HOME_TELEPORT_NEXT = Sound.create(194, 0);
    public static final Sound HOME_TELEPORT_END = Sound.create(195, 0);
    public static final Sound ANCIENT_END_TELE = Sound.create(197, 0);
    public static final Sound TELE_TAB = Sound.create(965, 0);
    public static final Sound TELE_OTHER = Sound.create(199, 0);

    /**
     * God spells
     */

    public static final Sound GUTHIX_FAIL = Sound.create(1652, 0);
    public static final Sound GUTHIX_HIT = Sound.create(1653, 0);
    public static final Sound ZAMMY_FAIL = Sound.create(1654, 0);
    public static final Sound ZAMMY_HIT = Sound.create(1655, 0);
    public static final Sound SARA_FAIL = Sound.create(1656, 0);
    public static final Sound SARA_HIT = Sound.create(1659, 0);

    /**
     * Modern defensive spells
     */

    public static final Sound BIND = Sound.create(100, 0);
    public static final Sound BIND_FAIL = Sound.create(101, 0);

    public static final Sound VULNERABILITY_CAST = Sound.create(148, 0);
    public static final Sound VULNERABILITY_FAIL = Sound.create(149, 0);
    public static final Sound VULNERABILITY_END = Sound.create(150, 0);

    public static final Sound ENTANGLE_CAST = Sound.create(151, 0);
    public static final Sound ENTANGLE_FAIL = Sound.create(152, 0);
    public static final Sound ENTANGLE_END = Sound.create(153, 0);

    public static final Sound WEAKEN_CAST = Sound.create(119, 0);
    public static final Sound WEAKEN_END = Sound.create(121, 0);
    public static final Sound CURSE_CAST = Sound.create(127, 0);
    public static final Sound CURSE_END = Sound.create(126, 0);

    public static final Sound TELEBLOCK_CAST = Sound.create(202, 0);
    public static final Sound TELEBLOCK_HIT = Sound.create(203, 0);

    /**
     * Modern offensive spells
     */

    public static final Sound CRUMBLE_UNDEAD_CAST = Sound.create(122, 0);
    public static final Sound CRUMBLE_UNDEAD_END = Sound.create(123, 0);

    public static final Sound AIR_BLAST_CAST = Sound.create(216, 0);
    public static final Sound AIR_BLAST_HIT = Sound.create(217, 0);
    public static final Sound AIR_BOLT_CAST = Sound.create(218, 0);
    public static final Sound AIR_BOLT_HIT = Sound.create(219, 0);
    public static final Sound AIR_STRIKE_CAST = Sound.create(220, 0);
    public static final Sound AIR_STRIKE_HIT = Sound.create(221, 0);
    public static final Sound AIR_WAVE_CAST = Sound.create(222, 0);
    public static final Sound AIR_WAVE_HIT = Sound.create(223, 0);

    public static final Sound EARTH_BLAST_CAST = Sound.create(128, 0);
    public static final Sound EARTH_BLAST_HIT = Sound.create(129, 0);
    public static final Sound EARTH_BOLT_CAST = Sound.create(130, 0);
    public static final Sound EARTH_BOLT_HIT = Sound.create(131, 0);
    public static final Sound EARTH_STRIKE_CAST = Sound.create(132, 0);
    public static final Sound EARTH_STRIKE_HIT = Sound.create(133, 0);
    public static final Sound EARTH_WAVE_CAST = Sound.create(134, 0);
    public static final Sound EARTH_WAVE_HIT = Sound.create(135, 0);

    public static final Sound WATER_BLAST_CAST = Sound.create(207, 0);
    public static final Sound WATER_BLAST_HIT = Sound.create(208, 0);
    public static final Sound WATER_BOLT_CAST = Sound.create(209, 0);
    public static final Sound WATER_BOLT_HIT = Sound.create(210, 0);
    public static final Sound WATER_STRIKE_CAST = Sound.create(211, 0);
    public static final Sound WATER_STRIKE_HIT = Sound.create(212, 0);
    public static final Sound WATER_WAVE_CAST = Sound.create(213, 0);
    public static final Sound WATER_WAVE_HIT = Sound.create(214, 0);

    public static final Sound FIRE_BLAST_CAST = Sound.create(155, 0);
    public static final Sound FIRE_BLAST_HIT = Sound.create(156, 0);
    public static final Sound FIRE_BOLT_CAST = Sound.create(157, 0);
    public static final Sound FIRE_BOLT_HIT = Sound.create(158, 0);
    public static final Sound FIRE_STRIKE_CAST = Sound.create(160, 0);
    public static final Sound FIRE_STRIKE_HIT = Sound.create(161, 0);
    public static final Sound FIRE_WAVE_CAST = Sound.create(162, 0);
    public static final Sound FIRE_WAVE_HIT = Sound.create(163, 0);

    /**
     * Ancient spells
     */

    public static final Sound BLOOD_RUSH = Sound.create(103, 0);
    public static final Sound BLOOD_BURST = Sound.create(104, 0);
    public static final Sound BLOOD_BLITZ = Sound.create(105, 0);
    public static final Sound BLOOD_BARRAGE = Sound.create(106, 0);
    public static final Sound BLOOD_FAIL = Sound.create(109, 0);
    public static final Sound BLOOD_START = Sound.create(108, 0);

    public static final Sound ICE_SPELL_CAST = Sound.create(171, 0);
    public static final Sound ICE_BARRAGE_HIT = Sound.create(168, 0);
    public static final Sound ICE_BLITZ_HIT = Sound.create(169, 0);
    public static final Sound ICE_BURST_HIT = Sound.create(170, 0);
    public static final Sound ICE_RUSH_HIT = Sound.create(173, 0);

    /**
     * Lunar spells
     */
    public static final Sound VENGEANCE = Sound.create(2908, 0);
    public static final Sound VENGEANCE_OTHER = Sound.create(2907, 0);
    public static final Sound HUMIDIFY = Sound.create(3614, 0);

	/**
	 * The id of this sound.
	 */
	private int id;
	
	/**
	 * The volume this sound is played at.
	 */
	private byte volume = 1;
	
	/**
	 * The delay before this sound is played.
	 */
	private int delay = 0;
	
	public Sound(int id, byte volume, int delay) {
		this.id = id;
		this.volume = volume;
		this.delay = delay;
	}
	
    /**
     * Creates a sound
     *
     * @param id    The id
     * @param delay The delay
     */
    public static Sound create(int id, int delay) {
        return new Sound(id, (byte) 1, delay);
    }
	
    /**
     * Creates a sound
     *
     * @param id     The id
     * @param volume The volume
     * @param delay  The delay
     */
	public static Sound create(int id, byte volume, int delay) {
		return new Sound(id, volume, delay);
	}

	public int getId() {
		return id;
	}

	public byte getVolume() {
		return volume;
	}

	public int getDelay() {
		return delay;
	}
}
