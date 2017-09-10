package org.rs2server.rs2.model.minigame.impl;

import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.event.ClickEventManager;
import org.rs2server.rs2.model.event.EventListener;
import org.rs2server.rs2.model.player.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class PestControlManager extends EventListener {

	private static final PestControlManager INSTANCE = new PestControlManager();

	// multi dimensional for boat levels
	public static final int[][] PC_NPCS = new int[][] {
			{ 1694, 1695, 1696, 1697, 1714, 1715, 1716, 1717, 1718, 1709, 1710, 1711, 1724, 1725, 1726, 1727, 1734,
					1735, 1736, 1737, 1689 },
			{ 3736, 3737, 3738, 3739, 3756, 3757, 3758, 3759, 3749, 3766, 3767, 3744, 3768, 3769, 3773, 3774, 3775,
					3727, 3728, 3729, 3730 },
			{ 3740, 3741, 3760, 3761, 3750, 3751, 3745, 3746, 3770, 3771, 3776, 3731 } };

	public static PestControlManager getPestControlManager() {
		return INSTANCE;
	}

	public enum Boat {
		NOVICE(40, 14315, Location.create(2657, 2639, 0), Location.create(2660, 2639, 0)), INTERMEDIATE(70, 25631,
				Location.create(2644, 2644, 0), Location.create(2641, 2644, 0)), EXPERT(100, 25632,
						Location.create(2638, 2653, 0), Location.create(2635, 2653, 0));

		public int objId;
		public Location startLoc;
		public int lvl;
		public Location endLoc;

		Boat(int reqLvl, int objId, Location start, Location end) {
			this.lvl = reqLvl;
			this.objId = objId;
			this.startLoc = start;
			this.endLoc = end;
		}
	}

	public enum Portal {
		WEST(1747, 18, Location.create(2628, 2591, 0),
				new Location[] { Location.create(2630, 2594, 0), Location.create(2631, 2594, 0),
						Location.create(2631, 2593, 0), Location.create(2631, 2592, 0), Location.create(2631, 2591, 0),
						Location.create(2631, 2590, 0), Location.create(2630, 2590, 0) }), EAST(1748, 20,
								Location.create(2680, 2588, 0),
								new Location[] { Location.create(2644, 2571, 0), Location.create(2644, 2572, 0),
										Location.create(2645, 2572, 0), Location.create(2646, 2572, 0),
										Location.create(2647, 2572, 0), Location.create(2648, 2572, 0),
										Location.create(2648, 2571, 0) }),

		SOUTH_EAST(1749, 22, Location.create(2669, 2570, 0),
				new Location[] { Location.create(2668, 2572, 0), Location.create(2668, 2573, 0),
						Location.create(2669, 2573, 0), Location.create(2670, 2573, 0), Location.create(2671, 2573, 0),
						Location.create(2672, 2573, 0), Location.create(2672, 2572, 0) }),

		SOUTH_WEST(1750, 24, Location.create(2645, 2569, 0),
				new Location[] { Location.create(2680, 2587, 0), Location.create(2679, 2587, 0),
						Location.create(2679, 2588, 0), Location.create(2679, 2589, 0), Location.create(2679, 2590, 0),
						Location.create(2679, 2591, 0), Location.create(2680, 2591, 0) });

		public int shieldId;
		public int interfacConfig;
		public Location spawn;
		public Location[] spawnLocs;

		Portal(int shieldId, int interfaceConfig, Location spawn, Location[] spawns) {
			this.shieldId = shieldId;
			this.interfacConfig = interfaceConfig;
			this.spawn = spawn;
			this.spawnLocs = spawns;
		}
	}

	public static HashMap<Integer, Boat> boatMap = new HashMap<Integer, Boat>();

	static {
		for (Boat b : Boat.values()) {
			boatMap.put(b.objId, b);
		}
	}

	public ArrayList<PestControl> minigamesInProgress = new ArrayList<PestControl>();
	public PestControl[] minigamesWaiting = new PestControl[3];
	public boolean[] heights = new boolean[256];

	public void init() {
		minigamesWaiting[0] = new PestControl(Boat.NOVICE, findAvailableHeight());
		minigamesWaiting[1] = new PestControl(Boat.INTERMEDIATE, findAvailableHeight());
		minigamesWaiting[2] = new PestControl(Boat.EXPERT, findAvailableHeight());
	}

	private int findAvailableHeight() {
		for (int i = 0; i < heights.length; i++) {
			if (!heights[i]) {
				return i * 4;
			}
		}
		return 0;
	}

	@Override
	public void register(ClickEventManager manager) {
		// for (Boat b : boatMap.values()) {
		// manager.registerObjectListener(b.objId, this);
		// }
		// manager.registerObjectListener(14314, this);
		// manager.registerObjectListener(25629, this);
		// manager.registerObjectListener(25630, this);
	}

	public void addPlayerToBoat(Boat boat, Player player) {
		if (boat.lvl > player.getSkills().getCombatLevel()) {
			player.getActionSender().sendMessage("You need a combat level of " + boat.lvl + " to enter this boat!");
			return;
		}
		minigamesWaiting[boat.ordinal()].joinPlayer(player);
	}

	@Override
	public boolean objectAction(Player player, int objectId, GameObject gameObject, Location location,
			ClickOption option) {
		if (boatMap.containsKey(objectId)) {
			getPestControlManager().addPlayerToBoat(boatMap.get(objectId), player);
			return true;
		} else
			switch (objectId) {
			case 14314:
				getPestControlManager().leaveBoat(Boat.NOVICE, player);
				return true;
			case 25629:
				getPestControlManager().leaveBoat(Boat.INTERMEDIATE, player);
				return true;
			case 25630:
				getPestControlManager().leaveBoat(Boat.EXPERT, player);
				return true;
			}
		return false;
	}

	private void leaveBoat(Boat boat, Player player) {
		minigamesWaiting[boat.ordinal()].quit(player);

	}

	public void removeWaiting(Boat boat, int height) {
		heights[height / 4] = false;
		minigamesWaiting[boat.ordinal()] = new PestControl(boat, findAvailableHeight());
	}
}
