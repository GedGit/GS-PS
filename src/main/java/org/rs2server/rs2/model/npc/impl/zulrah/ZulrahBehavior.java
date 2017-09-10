package org.rs2server.rs2.model.npc.impl.zulrah;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.combat.npcs.CombatType;

/**
 * 
 * @author Nine
 *
 */
public class ZulrahBehavior {
	
	public static final Location CENTER = Location.create(2266, 3072, 0);
	
	public static final Location EAST = Location.create(2275, 3072, 0);
	public static final Location WEST = Location.create(2257, 3072, 0);
	public static final Location SOUTH = Location.create(2266, 3063, 0);
	
	public static final Location[][] VENOM_CLOUD_LOCATIONS = {
		{Location.create(2268, 3068, 0), Location.create(2271, 3068, 0)}, // top left
		{Location.create(2262, 3069, 0), Location.create(2265, 3068, 0)}, // top right
		{Location.create(2262, 3075, 0), Location.create(2262, 3072, 0)}, // right
		{Location.create(2272, 3074, 0), Location.create(2272, 3071, 0)}, // left
	};

	public static final int VENOM_CLOUD = 11700;
	public static final Animation ZULRAH_SHOOT_ANIMATION = Animation.create(5069);
	

	// There are two more, but I think these are best
	public static ZulrahPattern[][] PATTERNS = {
		{
			new ZulrahPattern(CENTER, CombatType.RANGE),
			new ZulrahPattern(CENTER, CombatType.MELEE), 
			new ZulrahPattern(CENTER, CombatType.MAGE),
			new ZulrahPattern(SOUTH, CombatType.RANGE),
			new ZulrahPattern(CENTER, CombatType.MELEE),
			new ZulrahPattern(WEST, CombatType.MAGE),
			new ZulrahPattern(SOUTH, CombatType.RANGE),
			new ZulrahPattern(SOUTH, CombatType.MAGE),
			new ZulrahPattern(WEST, CombatType.RANGE),
		},
		{
			new ZulrahPattern(CENTER, CombatType.RANGE), // range is 0
			new ZulrahPattern(CENTER, CombatType.MELEE),  // melee is 1
			new ZulrahPattern(CENTER, CombatType.MAGE),//jad what not jad #3? see?
			new ZulrahPattern(WEST, CombatType.RANGE),
			new ZulrahPattern(SOUTH, CombatType.MAGE),
			new ZulrahPattern(CENTER, CombatType.MELEE),
			new ZulrahPattern(EAST, CombatType.RANGE),
			new ZulrahPattern(SOUTH, CombatType.MAGE),
			new ZulrahPattern(WEST, CombatType.RANGE), 
			new ZulrahPattern(CENTER, CombatType.MELEE), 
			new ZulrahPattern(CENTER, CombatType.RANGE), 
		},
	};
	
	public static class ZulrahPattern {
		
		private Location location;
		private CombatType combatType;
		
		private ZulrahPattern(Location location, CombatType combatType) {
			this.location = location;
			this.combatType = combatType;
		}
		
		public Location getLocation() {
			return location;
		}
		
		public CombatType getCombatType() {
			return combatType;
		}

	}
	
}
