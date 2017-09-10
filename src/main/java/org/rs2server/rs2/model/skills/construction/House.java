package org.rs2server.rs2.model.skills.construction;

import java.util.ArrayList;
import java.util.List;

import org.rs2server.cache.format.CacheObjectDefinition;
import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Palette;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.construction.Room.RoomType;

public class House {

	private static final Location RESET = Location.create(0, 0, 0);

	/**
	 * Constructs the house class.
	 */
	public House(final Player player) {
		this.player = player;
	}

	/**
	 * Gets the room of a specific location..
	 * 
	 * @return The room, a specific object / location is in.
	 */
	public Room getCurrentRoom(Location location) {
		int localX = location.getX(); // Already local coordinates.
		int localY = location.getY(); // Already local coordinates.
		int height = player.getLocation().getPlane();
		for (Room room : rooms) {
			int plane = room.getHeight();
			int minX = room.getX();
			int minY = room.getY();
			int maxX = minX + 7;
			int maxY = minY + 7;
			
			// TODO the method below is auto-generated, not finished !!!
			if (Location.isInArea(localX, localY, height, minX, minY, plane, maxX, maxY, plane))
				return room;
		}
		return null;
	}

	/**
	 * Called when entering the house.
	 * 
	 * @param location
	 *            The "house" location.
	 * @param newLocation
	 *            The location to set the player.
	 */
	public void enter(Location location) {
		location = Location.create(1933, 5100, 0);
		// if(isHouseBought()) {
		reload(player, location);
		Construction.CONSTRUCTORS.add(player);
		player.getActionSender().sendMessage("You enter your house..");
		// } else {
		// player.getActionSender().sendMessage("You don't even have a house
		// yet!");
		// player.getActionSender().removeAllInterfaces();
		// }

	}

	/**
	 * Called when someone is attempting to enter this house.
	 * 
	 * @param player
	 *            The visitor.
	 */
	public void visitor(Player player) {
		if (visitors.size() > 100) {
			player.getActionSender().sendMessage("This house is currently full.");
		} else {
			visitors.add(player);
			reload(player, Location.create(1955, 5732, 0)); // TODO: Entrance
															// location.
		}

	}

	/**
	 * Entering the house, entering a dungeon etc.
	 * 
	 * @param location
	 *            The new location.
	 */
	public void reload(final Player p, final Location location) {
		p.setTeleportTarget(RESET);
		p.getActionSender().sendInterface(71, false);
		// TODO//p.getActionSender().send (minimap blackout here once finished
		// type =2;
		// TODO//p.setCanWalk(false);
		final Palette pal = new Palette();
		for (int x = 3; x < 10; x++) {
			for (int y = 3; y < 10; y++) {
				pal.setTile(x, y, 0, Room.GRASS);
			}
		}
		for (Room room : rooms) {
			int x = room.getX() / 8;
			int y = room.getY() / 8;
			pal.setTile(x, y, room.getHeight(), room.getPalette());
		}
		World.getWorld().submit(new Event(1200) {

			@Override
			public void execute() {
				player.getActionSender().sendConstructMapRegion(pal, location);// TODO
																				// 'value:
																				// location'
																				// (pal,
																				// location)
				World.getWorld().submit(new Event(1200) {

					@SuppressWarnings("unused")
					@Override
					public void execute() {
						for (Room room : rooms) {
							ConstructionObject[] objects = room.getObjects();
							ConstructionObject[] hotspots = room.getHotspots();
							for (int index = 0; index < hotspots.length; index++) {
								ConstructionObject object = objects[index];
								ConstructionObject hotspot = hotspots[index];
								// System.out.println(hotspot);
								if (hotspot.getLocation().getPlane() == p.getLocation().getPlane()) {
									Location local = Location.create(hotspot.getLocation().getX() + room.getX(),
											hotspot.getLocation().getY() + room.getY(), hotspot.getLocation().getPlane());
									if (player.getConstruction().isInBuildingMode()) {
										player.getActionSender().sendCreateLocalObject(hotspot.getDefinition().getId(),
												hotspot.getType(), hotspot.getDirection(), local);
									} else {
										player.getActionSender().sendDestroyLocalObject(hotspot.getType(),
												hotspot.getDirection(), local);
									}
									if (object != null) {
										System.out.println("Object:" + object);
										// TODO//
										// p.getActionSender().sendCreateLocalObject(object.getDefinition().getId(),
										// object.getType(),
										// object.getDirection(),
										// object.getLocation());
									}
								}
							}
							if (room.getRoomType() != RoomType.GARDEN) {
								for (ConstructionObject other : room.getOtherObjects()) {
									Location local = Location.create(other.getLocation().getX() + room.getX(),
											other.getLocation().getY() + room.getY(), other.getLocation().getPlane());
									if (other.isBuildingModeObject() && player.getConstruction().isInBuildingMode()
											|| !other.isBuildingModeObject()
													&& !player.getConstruction().isInBuildingMode()) {
										// TODO//
										// p.getActionSender().sendCreateLocalObject(other.getDefinition().getId(),
										// other.getType(),
										// other.getDirection(), local);
									}
								}
							}
						}
						// TODO//p.setCanWalk(true);
						// p.getActionSender().sendMinimapBlackout(0);
						p.getActionSender().removeAllInterfaces();
						this.stop();
					}

				});
				this.stop();
			}

		});
	}

	private boolean locked = false;

	public void switchLockedStage() {
		if (Construction.CONSTRUCTORS.contains(player)) {
			this.locked = !this.locked;
			player.getActionSender().sendMessage("Your house is now " + (locked ? "locked" : "unlocked") + ".");
		} else {
			player.getActionSender().sendMessage("This is not your house.");
		}

	}

	public boolean isLocked() {
		return locked;
	}

	/**
	 * Tells us whenever or not we have visitors.
	 * 
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	public boolean hasVisiters() {
		return visitors.size() > 0;
	}

	public void addRoom(Room room) {
		rooms.add(room);
	}

	public void addRoomAndReload(Room room) {
		addRoom(room);
		reload(player, player.getLocation());
	}

	/**
	 * Called when a new house is bought. This is adding the room with the exit
	 * portal etc.
	 */
	public void buy() {
		if (isHouseBought())
			player.getActionSender().sendMessage("You've already got a house.");
		else {
			Room room = new Room(RoomType.GARDEN, player, Room.RoomType.GARDEN.getPaletteTile(design), 6, 6, 0);
			rooms.add(room);
			room.addObject(new ConstructionObject(CacheObjectDefinition.forId(13405), Location.create(51, 51, 0), 10, 0,
					false), false);
		}
	}

	/**
	 * Gets an array of all the rooms in this house. Example usage: Saving.
	 * 
	 * @return An array of all the rooms in this house.
	 */
	public Room[] getRooms() {
		return rooms.toArray(new Room[0]);
	}

	public void setDesign(HouseDesign design) {
		this.design = design;
	}

	public HouseDesign getHouseDesign() {
		return design;
	}

	public boolean isHouseBought() {
		return rooms.size() > 0;
	}

	public boolean hasRoom(Location loc) {
		for (Room room : rooms) {
			if (room.getX() == loc.getX() * 8 && room.getY() == loc.getY() * 8 && room.getHeight() == loc.getPlane()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Contains all the different house designs in the game.
	 */
	public enum HouseDesign {
		BASIC_WOOD(1, 1000), BASIC_STONE(10, 5000), Whitewashed_Stone(20, 7500), Fremennik_Style_Wood(30,
				10000), Tropical_Wood(40, 15000), Fancy_Stone(50, 25000);

		/**
		 * Constructs a new house design.
		 * 
		 * @param lvl
		 *            The level requirement.
		 * @param price
		 *            The cost.
		 */
		private HouseDesign(int lvl, int price) {
			this.lvl = lvl;
			this.price = price;
		}

		/**
		 * Gets the level requirement for this house design.
		 */
		public int getLevel() {
			return this.lvl;
		}

		/**
		 * Gets the price of this house design.
		 */
		public int getPrice() {
			return this.price;
		}

		/**
		 * The level requirement of this house design.
		 */
		private final int lvl;

		/**
		 * The price of this house design.
		 */
		private final int price;

		public static HouseDesign getDesign(int level) {
			System.out.println("Level: " + level);
			switch (level) {
			case 1:
				return BASIC_WOOD;
			case 5:
				return BASIC_STONE;
			case 20:
				return Whitewashed_Stone;
			case 30:
				return Fremennik_Style_Wood;
			case 40:
				return Tropical_Wood;
			case 50:
				return Fancy_Stone;
			default:
				return null;
			}
		}

	}

	/**
	 * The current house design.
	 */
	private HouseDesign design = HouseDesign.BASIC_WOOD;

	/**
	 * The owner of this house.
	 */
	private final Player player;

	/**
	 * The players list of rooms.
	 */
	private final List<Room> rooms = new ArrayList<Room>();

	/**
	 * The houses list of players.
	 */
	private final List<Player> visitors = new ArrayList<Player>();

	private static final List<Location[]> list = new ArrayList<Location[]>();

	private static void createConsList() {
		// 1983,5119,0 //Max
		// 1856,5056,0 //Min
		for (int z = 0; z < 4; z++) {
			for (int x = 1856; x <= 1983; x += 8) {
				for (int y = 5056; y <= 5119; y += 8) {
					Location min = Location.create(x, y, z);
					Location max = Location.create(x + 7, y + 7, z);
					list.add(new Location[] { min, max });
				}
			}
		}

	}

	static {
		createConsList();
	}
}
