package org.rs2server.rs2.model.skills.construction;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.GameObjectDefinition;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Palette.PaletteTile;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.construction.Room.RoomType;

public class Construction {

	private static boolean loaded = false;

	public static final List<Player> CONSTRUCTORS = new ArrayList<Player>();

	private static final Map<String, ConstructionObject[]> hotspots = new HashMap<String, ConstructionObject[]>();

	private static final Map<String, ConstructionObject[]> objects = new HashMap<String, ConstructionObject[]>();

	private static final Random r = new Random();

	/**
	 * All the non-transparent objects such as walls, grass, etc should go in
	 * here.
	 */
	@SuppressWarnings("unused")
	private static final int[] NON_HOTSPOT_OBJECTS = { 13410, // Grass.
	};

	private static final int[] HOTSPOT_OBJECTS = { 13728, // Window space
			13729, // Window space
			13730, // Window space
			13731, // Window space
			13732, // Window space
			13733, // Window space
			15256, // Treasure space
			15257, // Monster space
			15258, // Monster space 3
			15259, // Decoration space
			15260, // Bed space
			15261, // Wardrobe space
			15262, // Dresser space
			15263, // Curtain space
			15264, // Rug space
			15265, // Rug space
			15266, // Rug space
			15267, // Fireplace space
			15268, // Clock space
			15269, // Icon space
			15270, // Altar space
			15271, // Lamp space
			15272, // Rug space
			15273, // Rug space
			15274, // Rug space
			15275, // Statue space
			15276, // Musical space
			15277, // Combat ring space
			15278, // Combat ring space
			15279, // Combat ring space
			15280, // Combat ring space
			15281, // Combat ring space
			15282, // Combat ring space
			15283, // Combat ring space
			15284, // Combat ring space
			15285, // Combat ring space
			15286, // Combat ring space
			15287, // Combat ring space
			15288, // Combat ring space
			15289, // Combat ring space
			15290, // Combat ring space
			15291, // Combat ring space
			15292, // Combat ring space
			15293, // Combat ring space
			15294, // Combat ring space
			15295, // Combat ring space
			15296, // Storage space
			15297, // Decoration space
			15298, // Table space
			15299, // Seating space
			15300, // Seating space
			15301, // Fireplace space
			15302, // Curtain space
			15303, // Decoration space
			15304, // Bell pull space
			15305, // Door hotspot
			15306, // Door hotspot
			15307, // Door hotspot
			15308, // Door hotspot
			15309, // Door hotspot
			15310, // Door hotspot
			15311, // Door hotspot
			15312, // Door hotspot
			15313, // Door hotspot
			15314, // Door hotspot
			15315, // Door hotspot
			15316, // Door hotspot
			15317, // Door hotspot
			15318, // Door hotspot
			15319, // Door hotspot
			15320, // Door hotspot
			15321, // Door hotspot
			15322, // Door hotspot
			15323, // Guard space
			15324, // Trap space
			15325, // Trap space
			15326, // Door space
			15327, // Door space
			15328, // Door space
			15329, // Door space
			15330, // Lighting space
			15331, // Decoration space
			15332, // Rug space
			15333, // Rug space
			15334, // Rug space
			15335, // Rug space
			15336, // Guard space
			15337, // Guard space
			15338, // Door space
			15339, // Door space
			15340, // Lighting space
			15341, // Decoration space
			15342, // Game space
			15343, // Prize chest space
			15344, // Stone space
			15345, // Elemental balance space
			15346, // Ranging game space
			15347, // Floor space mid
			15348, // Floor space side
			15349, // Floor space corner
			15350, // Floor space
			15351, // Floor space
			15352, // Prison space
			15353, // Prison space
			15354, // Guard space
			15355, // Lighting space
			15356, // Ladder space
			15357, // Door space
			15358, // Door space
			15359, // Door space
			15360, // Door space
			15361, // Centrepiece space
			15362, // Big Tree space
			15363, // Tree space
			15364, // Big Plant space 1
			15365, // Big Plant space 2
			15366, // Small Plant space 1
			15367, // Small Plant space 2
			15368, // Centrepiece space
			15369, // Fencing
			15370, // Hedging
			15371, // Hedging
			15372, // Hedging
			15373, // Big plant
			15374, // Big plant 2
			15375, // Small plant
			15376, // Small plant 2
			15377, // Rug space
			15378, // Rug space
			15379, // Rug space
			15380, // Stair Space
			15381, // Stair Space
			15382, // Head trophy space
			15383, // Fishing trophy space
			15384, // Armour space
			15385, // Armour space
			15386, // Rune case space
			15387, // Rug space
			15388, // Rug space
			15389, // Rug space
			15390, // Stair Space
			15391, // Stair Space
			15392, // Portrait space
			15393, // Landscape space
			15394, // Guild trophy space
			15395, // Sword space
			15396, // Map space
			15397, // Bookcase space
			15398, // Stove space
			15399, // Shelf space
			15400, // Shelf space
			15401, // Barrel space
			15402, // Cat basket space
			15403, // Larder space
			15404, // Sink space
			15405, // Table space
			15406, // Portal space
			15407, // Portal space
			15408, // Portal space
			15409, // Centrepiece space
			15410, // Chair space
			15411, // Chair space
			15412, // Chair space
			15413, // Rug space
			15414, // Rug space
			15415, // Rug space
			15416, // Bookcase space
			15417, // Bookcase space
			15418, // Fireplace space
			15419, // Curtain space
			15420, // Lectern space
			15421, // Globe space
			15422, // Crystal ball space
			15423, // Wall chart space
			15424, // Telescope space
			15425, // Bookcase space
			15426, // Throne space
			15427, // Floor space
			15428, // Floor space
			15429, // Floor space
			15430, // Floor space
			15431, // Floor space
			15432, // Floor space
			15433, // Decoration space
			15434, // Decoration space
			15435, // Lever space
			15436, // Seating space
			15437, // Seating space
			15438, // Trapdoor space
			15439, // Workbench space
			15440, // Nothing
			15441, // Clockmaking space
			15442, // Nothing
			15443, // Tool space
			15444, // Tool space
			15445, // Tool space
			15446, // Tool space
			15447, // Tool space
			15448, // Repair space
			15449, // Nothing
			15450, // Heraldry space
			18810, // Cape rack space
			18811, // Magic wardrobe space
			18812, // Toy box space
			18813, // Treasure chest space
			18814, // Fancy dress box space
			18815, // Armour case space
			22457, // Crate
	};

	/**
	 * Attempts to load the hotspots for a specific PaletteTiles.
	 */
	@SuppressWarnings("deprecation")
	public static void loadHotspots() {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		try {
			File[] files = new File("./data/hotspots/").listFiles();
			for (File file : files) {
				String key = file.getName().replace(".txt", "");
				String[] info = key.split(" ");
				int minX = Integer.valueOf(info[0]);
				int minY = Integer.valueOf(info[1]);
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				dis = new DataInputStream(bis);
				List<ConstructionObject> hotspots1 = new ArrayList<ConstructionObject>();
				List<ConstructionObject> objects1 = new ArrayList<ConstructionObject>();
				while (dis.available() != 0) {
					String line = null;
					try {
						line = dis.readLine();
						// System.out.println(line);
						// p.println(objectId + " " + objX + " " + objY + " " +
						// plane + " " + objectType + " " + objectRotation);
						String[] obj = line.split("	");
						int id = Integer.parseInt(obj[0]);
						int x = Integer.parseInt(obj[1]) - minX;
						int y = Integer.parseInt(obj[2]) - minY;
						int z = Integer.parseInt(obj[3]);
						Location loc = Location.create(x, y, z);
						int type = Integer.parseInt(obj[4]);
						int face = Integer.parseInt(obj[5]);
						boolean hotspot = false;
						for (int spot : HOTSPOT_OBJECTS) {
							if (spot == id) {
								hotspot = true;
							}
						}
						ConstructionObject object = new ConstructionObject(GameObjectDefinition.forId(id), loc, type,
								face, hotspot);
						if (hotspot) {
							hotspots1.add(object);
						}
						objects1.add(object);
					} catch (Exception e) {
						// System.out.println(line + " " + key);
						// e.printStackTrace();
					}

				}
				dis.close();
				objects.put(key, objects1.toArray(new ConstructionObject[0]));
				hotspots.put(key, hotspots1.toArray(new ConstructionObject[0]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("REMAINDER: Fix the 508+ objects dumped lul.");
		loaded = true;
	}

	/**
	 * Constructs the players construction class.
	 */
	public Construction(Player player) {
		this.player = player;
		this.house = new House(this.player);
	}

	/**
	 * Called every time a player attempts to build an object.
	 */
	public void build(int id, Location loc) {
		Room room = house.getCurrentRoom(loc);
		if (room == null) {
			System.out.println("WARNING: Your POH room getter is bugged :(");
		} else {
			// System.out.println("Room: " + room);
			room.build(loc, id);
			buildingRoom = room;
		}
	}

	/**
	 * Switches the current construction mode.. Building mode on /off..
	 * 
	 * @param on
	 */
	public boolean switchBuildingMode(boolean on) {
		if (inBuildingMode != on) {
			if (CONSTRUCTORS.contains(player)) {
				if (house.hasVisiters()) {
					player.getActionSender().sendMessage("You can't switch building mode while you're hosting guests.");
					return false;
				} else {
					inBuildingMode = !inBuildingMode;
					house.reload(player, player.getLocation());
					player.getActionSender()
							.sendMessage("Building mode is now " + (inBuildingMode ? "on" : "off") + ".");
					return true;
				}
			} else {
				player.getActionSender().sendMessage("You're not in your house.");
				return false;
			}
		}
		return false;
	}

	/**
	 * Called when attempting to enter our house. Example usage: Teleporting,
	 * portal.
	 * 
	 * @param buildingMode
	 *            Defines if we enter the house in Building Mode.
	 */
	public void enterHouse(boolean buildingMode) {
		inBuildingMode = buildingMode;
		house.enter(Location.create(1955, 5732, 0)); // TODO: New location for
														// each constructor.
	}

	/**
	 * Gets the players house.
	 * 
	 * @return The players house.
	 */
	public House getHouse() {
		return house;
	}

	/**
	 * Defines whenever or not we're in building mode.
	 * 
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	public boolean isInBuildingMode() {
		return inBuildingMode;
	}

	@SuppressWarnings("unused")
	public boolean handleBuildingInterfaces(int interfaceId, int buttonId) {
		switch (interfaceId) {
		/*
		 * Room Creation Menu.
		 */
		case 402:
			RoomType roomType = Room.RoomType.getRoomType(player, buttonId);
			if (roomType != null) {
				if (player.getSkills().getLevelForExperience(Skills.CONSTRUCTION) < roomType.getLevel()) {
					player.getActionSender().sendMessage("You need a Construction level of " + roomType.getLevel()
							+ " in order to build this room.");
					return true;
				}
				if (!player.getInventory().containsItems(new Item(995, roomType.getPrice()))) {
					player.getActionSender().sendMessage("You don't have enough coins to buy this room.");
					return true;
				}
				System.out.println("buildingLocation: " + buildingLocation);
				Room room = new Room(roomType, player, roomType.getPaletteTile(house.getHouseDesign()),
						buildingLocation.getX(), buildingLocation.getY(), buildingLocation.getPlane());
				setBuildingRoom(room, roomType.getPrice());
				player.getActionSender().sendString(233, 1, "Rotate clockwise");
				player.getActionSender().sendString(233, 2, "Rotate anticlockwise");
				player.getActionSender().sendString(233, 3, "Build");
				player.getActionSender().sendString(233, 4, "Cancel");
				player.getActionSender().sendChatboxInterface(233);
				for (GameObject hotspot : buildingRoom.getHotspots()) {
					Location local = Location.create(hotspot.getLocation().getX() + buildingRoom.getX(),
							hotspot.getLocation().getY() + buildingRoom.getY(), hotspot.getLocation().getPlane());
					// TODO//
					// player.getActionSender().sendCreateLocalObject(hotspot.getDefinition().getId(),
					// hotspot.getType(), hotspot.getDirection(), local);
				}
			}
			return true;
		default:
			return false;
		}
	}

	@SuppressWarnings("unused")
	public void spawnRoomForRotation(boolean clockwise) {
		player.getActionSender().sendChatboxInterface(233);
		for (GameObject hotspot : buildingRoom.getHotspots(player, clockwise)) {
			Location local = Location.create(hotspot.getLocation().getX() + buildingRoom.getX(),
					hotspot.getLocation().getY() + buildingRoom.getY(), hotspot.getLocation().getPlane());
			// player.getActionSender().sendCreateLocalObject(hotspot.getDefinition().getId(),
			// hotspot.getType(), hotspot.getDirection(), local);
		}
	}

	/**
	 * Gets the hotspots for a specific tile set.
	 * 
	 * @param tile
	 *            The palette tile to get hotspots for.
	 * @return An object[] containing all the hotspots.
	 */
	public static ConstructionObject[] loadHotspots(int z, PaletteTile tile) {
		/*
		 * I had to add this, to make sure our definitions are all loaded before
		 * we start getting content from it. (Player loading)
		 */
		while (!loaded) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		String key = tile.getActualX() + " " + tile.getActualY() + " " + tile.getZ();
		ConstructionObject[] objects = hotspots.get(key);
		if (objects != null) {
			for (int index = 0; index < objects.length; index++) {
				ConstructionObject obj = objects[index];
				obj.setDirection(getNewRotation(obj.getDirection(), tile.getRotation()));
				obj.setLocation(Location.create(obj.getLocation().getX(), obj.getLocation().getY(), z));
				objects[index] = obj;
			}
		} else {
			System.out.println("WARNING: Didn't have hotspot data for: " + key);
			objects = new ConstructionObject[] {};
		}
		return objects;
	}

	@SuppressWarnings("incomplete-switch")
	public static ConstructionObject[] getOtherObjects(Player player, PaletteTile tile) {
		/*
		 * I had to add this, to make sure our definitions are all loaded before
		 * we start getting content from it. (Player loading)
		 */
		while (!loaded) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		String key = tile.getActualX() + " " + tile.getActualY() + " " + tile.getZ();
		ConstructionObject[] objs = objects.get(key);
		List<ConstructionObject> objects = new ArrayList<ConstructionObject>();
		if (objs == null) {
			objs = new ConstructionObject[0];
			System.out.println("WARNING: Didn't have object data for: " + key);
		}
		for (ConstructionObject obj : objs) {
			int id = obj.getDefinition().getId();
			switch (id) {
			case 13830: // Stupid transparent window
				switch (player.getConstruction().getHouse().getHouseDesign()) {
				case BASIC_WOOD:
					objects.add(new ConstructionObject(GameObjectDefinition.forId(13730), obj.getLocation(),
							obj.getType(), obj.getDirection(), true));
					objects.add(new ConstructionObject(GameObjectDefinition.forId(13730), obj.getLocation(),
							obj.getType(), obj.getDirection(), false)); // TODO:
																		// Real
																		// id.
					// id = 13730; //TODO: This is building mode objects LOL
					break;
				case BASIC_STONE:
					objects.add(new ConstructionObject(GameObjectDefinition.forId(13728), obj.getLocation(),
							obj.getType(), obj.getDirection(), true));
					objects.add(new ConstructionObject(GameObjectDefinition.forId(13728), obj.getLocation(),
							obj.getType(), obj.getDirection(), false)); // TODO:
																		// Real
																		// id.
					// id = 13728;
					break;
				}
				break;
			case 15305: // Door hotspot
			case 15306: // Door hotspot
			case 15307: // Door hotspot //Basic stone
			case 15308: // Door hotspot //Basic stone
			case 15309: // Door hotspot
			case 15310: // Door hotspot
			case 15311: // Door hotspot
			case 15312: // Door hotspot
			case 15313: // Door hotspot //Basic wood
			case 15314: // Door hotspot //Basic wood
			case 15315: // Door hotspot
			case 15316: // Door hotspot
			case 15317: // Door hotspot
			case 15318: // Door hotspot
			case 15319: // Door hotspot
			case 15320: // Door hotspot
			case 15321: // Door hotspot
			case 15322: // Door hotspot
				int id2 = id -= 15304;
				// if(id2 % 2 == 0) {
				id2 = 15644;
				// } else {
				// id2 = 15645; //Those are wrong, but it looks fine for now.
				// //Open door lol
				// }
				objects.add(new ConstructionObject(GameObjectDefinition.forId(id2), obj.getLocation(), obj.getType(),
						obj.getDirection(), false));
				// TODO: Ids for each house designs.
				break;
			}
		}
		return objects.toArray(new ConstructionObject[0]);
	}

	public static int getNewRotation(int rot, int draw) {
		if (rot == 0 && draw == -1) {
			rot = 3;
		} else {
			rot = (rot += draw);
		}
		return rot % 4;
	}

	public static boolean handleConstructionObjects(Player player, Location loc, int objectId) {
		switch (objectId) {
		/*
		 * Portals lol.
		 */
		case 15477:
		case 15478:
		case 15479:
		case 15480:
		case 15481:
		case 15482:
			player.getActionSender().sendString(233, 1, "Go to your house");
			player.getActionSender().sendString(233, 2, "Go to your house (Building mode)");
			player.getActionSender().sendString(233, 3, "Go to a friend's house");
			player.getActionSender().sendString(233, 4, "Never mind");
			player.getActionSender().sendChatboxInterface(233);
			return true;
		default:
			return false;
		}
	}

	/**
	 * Called when attempting to enter a friends house. TODO ALL.
	 */
	public static void enterFriendsHouse(Player player, long name) {
		// String string = NameUtils.formatName(NameUtils.longToName(name));
		// for(long n : player.getPrivateChat().getFriends()) {
		// if(n == name) { //"Online" stuff? Lol.
		// for(Player p : World.getWorld().getPlayers()) {
		// if(p.getNameAsLong() == name) {
		// if(CONSTRUCTORS.contains(p)) {
		// if(p.getConstruction().getHouse().isLocked()) {
		// player.getActionSender().sendMessage(string + " doesn't want visitors
		// at the moment.");
		// } else if(p.getConstruction().isInBuildingMode()) {
		// player.getActionSender().sendMessage(string + " is currently in
		// building mode.");
		// } else {
		// p.getConstruction().getHouse().visitor(player);
		// }
		// } else {
		// player.getActionSender().sendMessage(string + " is not home at the
		// moment. Try again later.");
		// }
		// return;
		// }
		// }
		// player.getActionSender().sendMessage("That player is currently
		// offline.");
		// }
		// }
		// player.getActionSender().sendMessage("Unable to find the player: " +
		// string + ". Please try again.");
	}

	public enum BuildingType {
		/**
		 * Defines a simple door hotspot.
		 */
		DOOR_HOTSPOT(402), // Room creation menu.

		GARDEN_CENTER(396, new String[][] {}, new Item[] {}, new Item[][] {

		}, new int[] {

		}, new int[] {

		}, new double[] {

		}, new int[] {

		}), SMALL_TREES(396, new String[][] { { "Level 5", "Tree", "1 Bagged dead tree", "Watering can", "", "" }, { "Level 10", "Nice tree", "1 Bagged nice tree", "Watering can", "", "" }, { "Level 15", "Oak tree", "1 Bagged oak tree", "Watering can", "", "" }, { "Level 30", "Willow tree", "1 Bagged willow tree", "Watering can", "", "" }, { "Level 45", "Maple tree", "1 Bagged maple tree", "Watering can", "", "" }, { "Level 60", "Yew tree", "1 Bagged yew tree", "Watering can", "", "" }, { "Level 75", "Magic tree", "1 Bagged magic tree", "Watering can", "", "" }, }, new Item[] { new Item(8173), new Item(8174), new Item(8175), new Item(8176), new Item(8177), new Item(8178), new Item(8179) }, new Item[][] { { new Item(8417) }, // Bagged
																																																																																																																																																																																								// dead
																																																																																																																																																																																								// tree
				{ new Item(8419) }, // Bagged nice tree
				{ new Item(8421) }, // Bagged oak tree
				{ new Item(8423) }, // Bagged willow tree
				{ new Item(8425) }, // Bagged maple tree
				{ new Item(8427) }, // Bagged yew tree
				{ new Item(8429) }, // Bagged magic tree
		}, new int[] { 13418, // 'Dead tree'
				13419, // 'Tree'
				13420, // 'Oak tree'
				13421, // 'Willow tree'
				13423, // 'Maple tree'
				13422, // 'Yew tree'
				13424, // 'Magic tree'
		}, new int[] { 10, // 'Dead tree'
				10, // 'Tree'
				10, // 'Oak tree'
				10, // 'Willow tree'
				10, // 'Maple tree'
				10, // 'Yew tree'
				10, // 'Magic tree'
		}, new double[] { 31, 44, 70, 100, 122, 141, 223 }, new int[] { 2293, 2293, 2293, 2293, 2293, 2293, 2293 }), // watering
																														// can.

		BIG_TREES(396,
				new String[][] { { "Level 5", "Tree", "1 Bagged dead tree", "Watering can", "", "" },
						{ "Level 10", "Nice tree", "1 Bagged nice tree", "Watering can", "", "" },
						{ "Level 15", "Oak tree", "1 Bagged oak tree", "Watering can", "", "" },
						{ "Level 30", "Willow tree", "1 Bagged willow tree", "Watering can", "", "" },
						{ "Level 45", "Maple tree", "1 Bagged maple tree", "Watering can", "", "" },
						{ "Level 60", "Yew tree", "1 Bagged yew tree", "Watering can", "", "" },
						{ "Level 75", "Magic tree", "1 Bagged magic tree", "Watering can", "", "" }, },
				new Item[] { // The items to put on the interface. Seems kinda
								// weird that they did items for this O,o..
						new Item(8173), // Tree
						new Item(8174), // Nice tree
						new Item(8175), // Oak tree
						new Item(8176), // Willow tree
						new Item(8177), // Maple tree
						new Item(8178), // Yew tree
						new Item(8179) // Magic tree
				}, new Item[][] { // Item requirements.. Notice: Watering cans
									// are hardcoded.
						{ new Item(8417) }, // Bagged dead tree
						{ new Item(8419) }, // Bagged nice tree
						{ new Item(8421) }, // Bagged oak tree
						{ new Item(8423) }, // Bagged willow tree
						{ new Item(8425) }, // Bagged maple tree
						{ new Item(8427) }, // Bagged yew tree
						{ new Item(8429) }, // Bagged magic tree
				}, new int[] { // Actual objects to spawn..
						13411, // 'Dead tree'
						13412, // 'Tree'
						13413, // 'Oak tree'
						13414, // 'Willow tree'
						13415, // 'Maple tree'
						13416, // 'Yew tree'
						13417, // 'Magic tree'

				}, new int[] { // Object types..
						10, // 'Dead tree'
						10, // 'Tree'
						10, // 'Oak tree'
						10, // 'Willow tree'
						10, // 'Maple tree'
						10, // 'Yew tree'
						10, // 'Magic tree'

				}, new double[] { 31, 44, 70, 100, 122, 141, 223 } // experience
				, new int[] { 2293, 2293, 2293, 2293, 2293, 2293, 2293 }// watering
																		// can.
		),

		BOOK_CASE(394,
				new String[][] { { "Level 5", "Wooden bookcase", "4 Planks", "4 Nails", "", "" },
						{ "Level 10", "Oak bookcase", "3 Oak planks", "", "", "" },
						{ "Level 15", "Mahogany bookcase", "3 Mahogany planks", "", "", "" }, },
				new Item[] { // The items to put on the interface. Seems kinda
								// weird that they did items for this O,o..
						new Item(8319), // Wooden bookcase
						new Item(8320), // Oak bookcase
						new Item(8321) // Mahogany bookcase
				}, new Item[][] { // Item requirements.. Notice: Watering cans
									// are hardcoded.
						{ new Item(960, 4), new Item(-1, 4) }, // Wooden
																// bookcase
						{ new Item(8778, 3) }, // Oak bookcase
						{ new Item(8782, 3) }, // Mahogany bookcase
				}, new int[] { // Actual objects to spawn..
						13597, // Wooden bookcase
						13598, // Oak bookcase
						13599, // Mahogany bookcase
				}, new int[] { // Object types..
						10, // Wooden bookcase
						10, // Oak bookcase
						10, // Mahogany bookcase

				}, new double[] { 115, 180, 420 } // experience
				, new int[] { 3684, 3684, 3684 }// Hammering like craazy
		),

		RUGS(394,
				new String[][] { { "Level 2", "Brown rug", "2 bolts of cloth", "", "", "" },
						{ "Level 13", "Rug", "4 bolts of cloth", "", "", "" },
						{ "Level 65", "Opulent rug", "4 bolts of cloth", "1 gold leaf", "", "" }, },
				new Item[] { // The items to put on the interface. Seems kinda
								// weird that they did items for this O,o..
						new Item(8316), // Brown rug
						new Item(8317), // Rug
						new Item(8318) // Opulent rug
				}, new Item[][] { // Item requirements.. Notice: Watering cans
									// are hardcoded.
						{ new Item(8790, 2) }, // Brown rug
						{ new Item(8790, 4) }, // Rug
						{ new Item(8790, 4), new Item(8784) }, // Opulent rug
				}, new int[] { // Actual objects to spawn..
						13597, // Wooden bookcase
						13598, // Oak bookcase
						13599, // Mahogany bookcase
				}, new int[] { // Object types..
						10, // Wooden bookcase
						10, // Oak bookcase
						10, // Mahogany bookcase

				}, new double[] { 115, 180, 420 } // experience
				, new int[] { 3684, 3684, 3684 }// Hammering like craazy
		),;

		/**
		 * Constructs a new building type.
		 * 
		 * @param interfaceId
		 *            The interface ID.
		 */
		private BuildingType(int interfaceId) {
			this.interfaceId = interfaceId;
			this.messages = null;
			this.items = null;
			this.requirements = null;
			this.objects = null;
			this.exp = null;
			this.objectTypes = null;
			this.animations = null;
		}

		private final String[][] messages;

		private final Item[] items;

		private final Item[][] requirements;

		private final int[] objects;

		private final int[] objectTypes;

		private final double[] exp;

		private Animation[] animations;

		/**
		 * Constructs a new building type.
		 * 
		 * @param interfaceId
		 *            The interface ID.
		 */
		private BuildingType(int interfaceId, String[][] messages, Item[] items, Item[][] requirements, int[] objects,
				int[] objectTypes, double[] exp, int[] animations) {
			this.interfaceId = interfaceId;
			this.messages = messages;
			this.items = items;
			this.requirements = requirements;
			this.objects = objects;
			this.objectTypes = objectTypes;
			this.exp = exp;
			this.animations = new Animation[animations.length];
			for (int index = 0; index < animations.length; index++) {
				this.animations[index] = Animation.create(animations[index]);
			}
		}

		/**
		 * Gets the building types interface ID.
		 * 
		 * @return This building types interface ID.
		 */
		public void displayInterface(Player player) {
			switch (interfaceId) {
			/*
			 * Interface to send items and stuff in.
			 */
			case 396:
				final int[][] childs = { { 140, 97, 98, 99, 100, 101 }, { 144, 117, 118, 119, 120, 121 },
						{ 141, 102, 103, 104, 105, 106 }, { 145, 122, 123, 124, 125, 126 },
						{ 142, 107, 108, 109, 110, 111 }, { 146, 127, 128, 129, 130, 131 },
						{ 143, 112, 113, 114, 115, 116 }, };
				for (int i = 0; i < childs.length; i++) {
					for (int index = 0; index < childs[i].length; index++) {
						player.getActionSender().sendBackwardsString(messages[i][index], interfaceId, childs[i][index]);
					}
				}
				player.getActionSender().sendUpdateItems(interfaceId, 132, 93, items);
				break;
			case 394:
				final int[][] childs2 = { { 110, 91, 92, 93, 94, 95 }, { 111, 96, 97, 98, 99, 100 },
						{ 112, 101, 102, 103, 104, 105 }, };
				for (int i = 0; i < childs2.length; i++) {
					for (int index = 0; index < childs2[i].length; index++) {
						player.getActionSender().sendBackwardsString(messages[i][index], interfaceId,
								childs2[i][index]);
					}
				}
				player.getActionSender().sendUpdateItems(interfaceId, 106, 93, items);
				break;
			}
			player.getActionSender().sendInterface(interfaceId, false);
		}

		/**
		 * Gets a building type based on a specific object.
		 * 
		 * @return A building type based on a specific object.
		 */
		@SuppressWarnings("incomplete-switch")
		public static BuildingType getBuildingType(RoomType type, int objectId) {
			switch (objectId) {
			/*
			 * Basic wood doors.
			 */
			case 15313:
			case 15314:
				/*
				 * Basic stone doors.
				 */
			case 15307:
			case 15308:
				return BuildingType.DOOR_HOTSPOT;
			default:
				System.out.println("Unhandled building object: " + objectId);
			}
			switch (type) {
			case GARDEN:
				switch (objectId) {
				case 15361:
					return GARDEN_CENTER;
				case 15362:
					return BIG_TREES;
				case 15363:
					return SMALL_TREES;
				}
				break;
			case PARLOUR:
				switch (objectId) {
				case 15416:
					return BOOK_CASE;
				}
				break;
			}
			return null;
		}

		/**
		 * The building types interface ID.
		 */
		private final int interfaceId;

		public boolean build(final Player player, int interfaceId, int slot, int itemId) {
			final boolean garden = (this.equals(SMALL_TREES) || this.equals(BIG_TREES));
			final int[] nails = { 4819, // Bronze nails
					4820, // Iron nails
					1539, // Steel nails
					4821, // Black nails
					4822, // Mithril nails
					4823, // Adamantite nails
					4824, // Rune nails
			};
			if (this.interfaceId != interfaceId) {
				return false;
			}
			boolean found = false;
			int index;
			for (index = 0; index < items.length; index++) {
				if (items[index].getId() == itemId) {
					found = true;
					int levelReq = Integer.valueOf(messages[index][0].replace("Level ", ""));
					if (player.getSkills().getLevel(Skills.CONSTRUCTION) < levelReq) {
						player.getActionSender().sendMessage("You need a Construction level of level " + levelReq
								+ " in order to build this object.");
						return true;
					}
					for (Item item : requirements[index]) {
						if (item.getId() != -1) { // Nail stuff..
							if (!player.getInventory().containsItems(nails)) {
								player.getActionSender()
										.sendMessage("You need " + item.getCount() + " "
												+ item.getDefinition().getName().toLowerCase()
												+ (item.getCount() > 1 ? "s" : "") + " in order to build this object.");
								found = false;
							}
						}
					}
					break;
				}
				if (found) {
					break;
				}
			}
			final int fIndex = index;
			if (found) {
				player.getActionQueue().addAction(new Action(player, 0) {

					private boolean found = false;
					private boolean firstRound = true;
					private boolean nailCheck = false;
					private int nailAmount = 0;

					@Override
					public void execute() {
						final int[] watering_cans = { 5331, // Empty one
								5333, // Watering can(1)
								5334, // Watering can(2)
								5335, // Watering can(3)
								5336, // Watering can(4)
								5337, // Watering can(5)
								5338, // Watering can(6)
								5339, // Watering can(7)
								5340, // Watering can(8)
						};
						/*
						 * Check for watering cans..
						 */
						found = false;
						if (firstRound) {
							if (garden) {
								for (int i = 1; i < watering_cans.length; i++) {
									int can = watering_cans[i];
									if (player.getInventory().contains(can)) {
										// player.getInventory().replace(can,
										// watering_cans[i -1]);
										found = true;
										break;
									}
								}
								if (!found) {
									player.getActionSender().sendMessage(
											"You need a watering can with water in order to build this object.");
								}
							} else {
								// Check if we're using planks building the
								// current object..
								Item[] required = requirements[fIndex];
								for (Item item : required) {
									if (item.getId() == 960) {// regular plank
										for (Item i : required) {
											if (i.getId() == -1) { // We found
																	// or nail
																	// requirements..
												nailCheck = true;
												nailAmount = i.getCount();
												int amount = 0;
												Item[] inventory = player.getInventory().toArray();
												for (Item inv : inventory) {
													if (inv != null) {
														if (inv.getDefinition().getName().contains("nails")) {
															for (int nail : nails) {
																if (inv.getId() == nail) {
																	amount += inv.getCount();
																	if (amount >= nailAmount) {
																		found = true;
																	}
																}
															}

														}
													}

												}
												if (!found) {
													player.getActionSender().sendMessage(
															"You don't have enough nails to build this item.");
												}
											}
										}
									}
								}
							}
							player.getActionSender().removeAllInterfaces();
							if (!found && nailCheck) {
								this.stop();
								return;
							}
						}
						if (!nailCheck && !firstRound) {
							if (garden) {
								for (int i = 1; i < watering_cans.length; i++) {
									int can = watering_cans[i];
									if (player.getInventory().contains(can)) {
										player.getInventory().replace(can, watering_cans[i - 1]);
										break;
									}
								}
							}
							Item[] required = requirements[fIndex];
							for (Item req : required) {
								if (req.getId() != -1) { // This is nails, and
															// they were removed
															// above..
									player.getInventory().remove(req);
								}
							}
							player.getSkills().addExperience(Skills.CONSTRUCTION, exp[fIndex]);
							if (garden) {
								player.getSkills().addExperience(Skills.FARMING, exp[fIndex]);
							}
							player.getConstruction().buildingRoom
									.addObject(
											new ConstructionObject(GameObjectDefinition.forId(objects[fIndex]),
													player.getConstruction().buildingLocation, objectTypes[fIndex],
													player.getConstruction().buildingObject.getDirection(), false),
											true);
							player.getActionSender().sendMessage("You build a "
									+ GameObjectDefinition.forId(objects[fIndex]).getName().toLowerCase() + ".");
							this.stop();
						} else if (nailCheck) {
							boolean have = false;
							Item[] inventory = player.getInventory().toArray();
							for (int index = 0; index < nails.length; index++) {
								int nail = nails[index];
								for (Item inv : inventory) {
									if (inv != null) {
										if (inv.getDefinition().getName().contains("nails")) {
											if (inv.getId() == nail) {
												have = true;
												if (r.nextInt(nails.length + 2) < index) { // Means
																							// the
																							// nail
																							// didn't
																							// break..
													if (--nailAmount == 0) {
														nailCheck = false;
													}
												} else {
													player.getActionSender().sendMessage("You bend a nail.");
												}
												player.getInventory().remove(new Item(nail, 1));
											}
										}
									}
								}
								if (have) {
									break;
								}
							}
							if (!have) {
								player.getActionSender().sendMessage("You ran out of nails..");
								this.stop();
								return;
							}
							this.setTicks(1800);
						}
						player.playAnimation(animations[fIndex]);
						firstRound = false;
					}

					@Override
					public CancelPolicy getCancelPolicy() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public StackPolicy getStackPolicy() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public AnimationPolicy getAnimationPolicy() {
						// TODO Auto-generated method stub
						return null;
					}

				});
			}
			return found;
		}

	}

	/**
	 * Saves the building variables to use again, once we click interface
	 * buttons..
	 * 
	 * @param hotspot
	 */
	public void setBuildingVariables(Location loc, BuildingType type, GameObject hotspot) {
		this.buildingLocation = loc;
		this.buildingType = type;
		this.buildingObject = hotspot;
	}

	/**
	 * Gets the room we're currently about to build.
	 * 
	 * @return The room we're currently about to build.
	 */
	public Room getBuildingRoom() {
		return buildingRoom;
	}

	/**
	 * Called when a player decides to remove a construction object.
	 */
	public void remove() {
		if (buildingLocation != null) {
			Room room = house.getCurrentRoom(buildingLocation);
			if (room != null) {
				room.removeObject(buildingLocation);
			}
		}

	}

	public boolean handleObjectPackets(Location loc, int id, int option) {

		if (option == 1) {
			switch (id) {
			/*
			 * Doors.
			 */
			case 15644:
			case 15645:
				Location local = Location.create(loc.getX() - 8 * (player.getLastKnownRegion().getRegionX() - 6),
						loc.getY() - 8 * (player.getLastKnownRegion().getRegionY() - 6),
						player.getLocation().getPlane());
				Room room = house.getCurrentRoom(local);
				if (room != null) {
					ConstructionObject obj = null;
					int x = local.getX() - room.getX();
					int y = local.getY() - room.getY();
					if (x == 8) {
						x = 7;
					}
					if (y == 8) {
						y = 7;
					}
					Location local2 = Location.create(x, y, local.getPlane());
					System.out.println("Local: " + local2);
					for (ConstructionObject o : room.getOtherObjects()) {
						System.out.println("Object: " + o);
						if (o.getLocation().equals(local2)) {
							obj = o;
							System.out.println("Sexy.");
							break;
						}
					}
					if (obj != null) {
						// DoorManager.handleConstructionDoor(player, obj, room,
						// local, id);
					}
				}
				return true;
			}
		}
		return false;
	}

	public boolean handleItemOptions(int interfaceId, int slot, int itemId, int i) {
		if (i == 1 && buildingType != null) {
			return buildingType.build(player, interfaceId, slot, itemId);
		}
		return false;
	}

	/**
	 * 
	 */
	public int getRoomPrice() {
		if (buildingPrice == -1) {
			System.out.println("Wtf? ");
			buildingPrice = 0;
		}
		return buildingPrice;
	}

	public void setBuildingRoom(Room room, int price) {
		System.out.println("Setting building room: " + room);
		buildingRoom = room;
		if (price != -1) {
			buildingPrice = price;
		}
	}

	private Location buildingLocation = null;;
	private BuildingType buildingType = null;
	private Room buildingRoom = null;
	private GameObject buildingObject = null;
	private int buildingPrice = 0;
	private final Player player;
	private final House house;
	private boolean inBuildingMode = false;

}
