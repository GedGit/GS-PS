package org.rs2server.rs2.content;

/**
 * Created by Tim on 12/21/2015.
 */
public enum Potion {

    STRENGTH(113, 115, 117, 119),

    NOTED_STRENGTH(114, 116, 118, 120),

    ATTACK(2428, 121, 123, 125),

    RESTORE(2430, 127, 129, 131),

    DEFENCE(2432, 133, 135, 137),

    PRAYER(2434, 139, 141, 143),

    FISHING(2438, 151, 153, 155),

    RANGING(2444, 169, 171, 173),

    ANTIFIRE(2452, 2454, 2456, 2458),

    ENERGY(3008, 3010, 3012, 3014),

    AGILITY(3032, 3034, 3036, 3038),

    MAGIC(3040, 3042, 3044, 3046),

    COMBAT(9739, 9741, 9743, 9745),

    SUPER_ATTACK(2436, 145, 147, 149),

    SUPER_STRENGTH(2440, 157, 159, 161),

    SUPER_DEFENCE(2442, 163, 165, 167),

    SUPER_ENERGY(3016, 3018, 3020, 3022),

    SUPER_RESTORE(3024, 3026, 3028, 3030),

    SARA_BREW(6685, 6687, 6689, 6691),

    EXTENDED_ANTI_FIRE(11951, 11953, 11955, 11957),

    SUPER_COMBAT(12695, 12697, 12699, 12701),


    NOTED_SARA_BREW(6686, 6688, 6690, 6692),

    NOTED_RESTORE(2431, 128, 130, 132),

    NOTED_DEFENCE(2433, 134, 136, 138),

    NOTED_PRAYER(2435, 140, 142, 144),

    NOTED_FISHING(2439, 152, 154, 156),

    NOTED_RANGING(2445, 170, 172, 174),

    NOTED_ANTIFIRE(2453, 2455, 2457, 2459),

    NOTED_ENERGY(3009, 3011, 3013, 3015),

    NOTED_MAGIC(3041, 3043, 3045, 3047),

    NOTED_COMBAT(9740, 9742, 9744, 9746),

    NOTED_SUPER_ATTACK(2437, 146, 148, 150),

    NOTED_SUPER_STRENGTH(2441, 158, 160, 162),

    NOTED_SUPER_DEFENCE(2443, 164, 166, 168),

    NOTED_SUPER_ENERGY(3017, 3019, 3021, 3023),

    NOTED_SUPER_RESTORE(3025, 3027, 3029, 3031),

    NOTED_EXTENDED_ANTI_FIRE(11952, 11954, 11956, 11958),

    NOTED_SUPER_COMBAT(12696, 12698, 12700, 12702);

    Potion(int fullId, int threeQuartersId, int halfId, int quarterId) {
        this.quarterId = quarterId;
        this.halfId = halfId;
        this.threeQuartersId = threeQuartersId;
        this.fullId = fullId;
    }

    private int quarterId, halfId, threeQuartersId, fullId;

    public int getQuarterId() {
        return this.quarterId;
    }

    public int getHalfId() {
        return this.halfId;
    }

    public int getThreeQuartersId() {
        return this.threeQuartersId;
    }

    public int getFullId() {
        return this.fullId;
    }

}
