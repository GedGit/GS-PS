package org.rs2server.rs2.model.skills.construction;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Palette.PaletteTile;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.construction.Construction.BuildingType;
import org.rs2server.rs2.model.skills.construction.House.HouseDesign;


/**
 * Defines a single player room for construction.
 * All rooms have a size of 8 * 8 squares.
 */
public class Room {
	
	/**
	 * Default "floors".
	 */
	public static final PaletteTile GRASS = new PaletteTile(1864, 5056, 0);
	public static final PaletteTile DUNGEON_GROUND = new PaletteTile(1880, 5056, 0);
		
	public enum RoomType {
		PARLOUR(1856, 5112, 1, 1000),
		GARDEN(1856, 5064, 1, 1000),
		Kitchen(1872, 5112, 5, 5000),
		Dining_Room(1890, 5112, 10, 5000),
		Workshop(1856, 5096, 15, 10000),
		Bedroom(1904, 5112, 20, 10000),
		Skill_Hall_Up(1864, 5104, 25, 15000),
		Skill_Hall_Down(1880, 5104, 25, 15000),
		Games_Room(1896, 5088, 30, 25000),
		Combat_Room(1880, 5088, 32, 25000),
		Quest_Hall_Up(1896, 5104, 35, 25000),
		Quest_Hall_Down(1912, 5104, 35, 25000),
		Study(1888, 5096, 40, 50000),
		Costume_Room(1904, 5064, 42, 50000),
		Chapel(1872, 5096, 45, 50000),
		Portal_Chamber(1864, 5088, 50, 100000),
		Formal_Garden(1872, 5064, 55, 75000),
		Throne_Room(1904, 5096, 60, 150000),
		Oubliette(1904, 5080, 65, 150000),
		Dungeon_Crossroads_Traps(1856, 5080, 70, 7500),
		Dungeon_Crossroads_Stair(1872, 5080, 70, 7500),
		Dungeon_Corridor(1888, 5080, 70, 7500),
		Treasure_Room(1912, 5088, 75, 250000);
						
		/**
		 * Constructs a new Room Type.
		 */
		private RoomType(int x, int y, int lvl, int price) {
			this.x = x;
			this.y = y;
			this.lvl = lvl;
			this.price = price;
		}
		
		/**
		 * Gets the location of this room, based on
		 * a specific house design.
		 * @param houseDesign The house design to get a room for.
		 * @return The area to construct.
		 */
		public PaletteTile getPaletteTile(HouseDesign houseDesign) {
			if(houseDesign.equals(HouseDesign.BASIC_WOOD)) {
				return new PaletteTile(x, y, 0);
			} else if(houseDesign.equals(HouseDesign.BASIC_STONE)) {
				return new PaletteTile(x, y, 1);
			} else if(houseDesign.equals(HouseDesign.Whitewashed_Stone)) {
				return new PaletteTile(x, y, 2);
			} else if(houseDesign.equals(HouseDesign.Fremennik_Style_Wood)) {
				return new PaletteTile(x, y, 3);
			} else if(houseDesign.equals(HouseDesign.Tropical_Wood)) {
				return new PaletteTile(x +64, y, 0);
			} else if(houseDesign.equals(HouseDesign.Fancy_Stone)) {
				return new PaletteTile(x +64, y, 1);
			}
			System.out.println("FATAL ERROR IN POH AREA GRABBER - HOUSE DESIGN NOT SPECIFIED.");
			return null;
		}
				
		public static RoomType getRoomType(Player player, int buttonId) {
			switch(buttonId) {
			case 160:
				return PARLOUR;
			case 161:
				return GARDEN;
			case 162:
				return Kitchen;
			case 163:
				return Dining_Room;
			case 164:
				return Workshop;
			case 165:
				return Bedroom;
			case 166:
				//		Skill_Hall_Up(1864, 5104, 25, 15000),
				//		Skill_Hall_Down(1880, 5104, 25, 15000),
				break;
			case 167:
				return Games_Room;
			case 168:
				return Combat_Room;
			case 169:
				//		Quest_Hall_Up(1896, 5104, 35, 25000),
				//		Quest_Hall_Down(1912, 5104, 35, 25000),
				break;
			case 170:
				return Study;
			case 171:
				return Costume_Room;
			case 172:
				return Chapel;
			case 173:
				return Portal_Chamber;
			case 174:
				return Formal_Garden;
			case 175:
				return Throne_Room;
			case 176:
				return Oubliette;
			case 177:
				//Dungeon - Corridor
				break;
			case 178:
				//Dungeon - Junction
				break;
			case 179:
				//Dungeon stairs.
				break;
			case 180:
				return Treasure_Room;
			}
			return null;
		}
		
		/**
		 * Gets the level requirement for
		 * this house design.
		 */
		public int getLevel() {
			return this.lvl;
		}
		
		/**
		 * Gets the price of
		 * this house design.
		 */
		public int getPrice() {
			return this.price;
		}
		
		/**
		 * The coordinates of the areas to
		 * construct with.
		 */
		private final int x;
		private final int y;
		
		/**
		 * The level requirement of this house design.
		 */
		private final int lvl;
		
		/**
		 * The price of this house design.
		 */
		private final int price;

		public static RoomType getType(int x, int y) {
			for(RoomType type : RoomType.values()) { //Not bad :o
				if(type.x == x && type.y == y) {
					return type;
				}
			}
			return null;
		}
	}
		
	public Room(RoomType type, Player player, PaletteTile tile, int x, int y, int z) {
		long time = System.nanoTime();
		ConstructionObject[] hotspots = Construction.loadHotspots(z, tile);
		ConstructionObject[] other = Construction.getOtherObjects(player, tile);
		ConstructionObject[] hsCopy = new ConstructionObject[hotspots.length];
		ConstructionObject[] oCopy = new ConstructionObject[other.length];
		for(int index = 0; index < hotspots.length; index++) {
			hsCopy[index] = hotspots[index];
		}
		for(int index = 0; index < other.length; index++) {
			oCopy[index] = other[index];
		}
		this.hotspots = hsCopy;
		this.objects = new ConstructionObject[hotspots.length];
		this.other = oCopy;
		this.x = x * 8;
		this.y = y * 8;
		this.z = z * 8;
		this.tile = tile;
		this.player = player;
		this.roomType = type;
		System.out.println("Time spend: " + (System.nanoTime() - time)); //Average: 53359
	}
	
	public Room(RoomType type, ConstructionObject[] objects, Player player, int x, int y, int z) {
		long time = System.nanoTime();
		PaletteTile tile = type.getPaletteTile(player.getConstruction().getHouse().getHouseDesign());
		ConstructionObject[] hotspots = Construction.loadHotspots(z, tile);
		ConstructionObject[] other = Construction.getOtherObjects(player, tile);
		ConstructionObject[] hsCopy = new ConstructionObject[hotspots.length];
		ConstructionObject[] oCopy = new ConstructionObject[other.length];
		for(int index = 0; index < hotspots.length; index++) {
			hsCopy[index] = hotspots[index];
		}
		for(int index = 0; index < other.length; index++) {
			oCopy[index] = other[index];
		}
		this.hotspots = hsCopy;
		this.objects = objects;
		this.other = oCopy;
		this.x = x * 8;
		this.y = y * 8;
		this.z = z * 8;
		this.tile = tile;
		this.player = player;
		this.roomType = type;
		System.out.println("Time spend: " + (System.nanoTime() - time));
		//this(type, player, type.getPaletteTile(player.getConstruction().getHouse().getHouseDesign()), x, y, z);
	}
	
	public int getHeight() {
		return z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public PaletteTile getPalette() {
		return tile;
	}

	@SuppressWarnings("incomplete-switch")
	public void build(Location location, int objectId) {
		if(!player.getConstruction().isInBuildingMode()) {
			player.getActionSender().sendMessage("You aren't in building mode at the moment."); //FIXME
		} else {
			System.out.println("Length: " + hotspots.length);
			for(int index = 0; index < hotspots.length; index++) {
				GameObject hotspot = hotspots[index];
				Location local = Location.create(hotspot.getLocation().getX() + x, hotspot.getLocation().getY() + y, hotspot.getLocation().getPlane());
				if(local.equals(location)) {
					GameObject current = objects[index];
					if(current != null) {
					//TODO//	player.setTemporaryAttribute("InterfaceOption", InterfaceOption.CONSTRUCTION);
						player.getActionSender().sendString(229, 0, "Really remove it?");
						player.getActionSender().sendString(229, 1, "Yes");
						player.getActionSender().sendString(229, 2, "No");
						player.getActionSender().sendChatboxInterface(229);
						player.getConstruction().setBuildingVariables(location, null, hotspot);
					} else {
						BuildingType type = Construction.BuildingType.getBuildingType(roomType, objectId);
						if(type != null) {
							Location loc = location;
							switch(type) {
							case DOOR_HOTSPOT:	
								/*
								 * Building towards north.
								 */
								if(location.getY() == this.getY() + 7) {
									loc = Location.create((x / 8), (y / 8) + 1, z);;
								/*
								 * Building towards south.
								 */
								} else if(location.getY() == this.getY()) {
									loc = Location.create((x / 8), (y / 8) - 1, z);
								/*
								 * Building towards east.
								 */
								} else if(location.getX() == this.getX() + 7) {
									loc = Location.create((x / 8) + 1, (y / 8), z);
								/*
								 * Building towards west.
								 */
								} else if(location.getX() == this.getX()) {
									loc = Location.create((x / 8) - 1, (y / 8), z);
								}
								if(player.getConstruction().getHouse().hasRoom(loc)) {
									player.getActionSender().sendMessage("You can't build a room on top of another room!");
									return;
								}
								break;
							}
							player.getConstruction().setBuildingVariables(loc, type, hotspot);
							type.displayInterface(player);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Adds a specific game object to this room.
	 * @param object The object to add.
	 * @param spawn Defines if we should spawn the object.
	 */
	public void addObject(ConstructionObject object, boolean spawn) {
		System.out.println("Object: " + object);
		for(int index = 0; index < hotspots.length; index++) {
			GameObject hotspot = hotspots[index];
			Location local = Location.create(hotspot.getLocation().getX() + x, hotspot.getLocation().getY() + y, hotspot.getLocation().getPlane());
			System.out.println("Local: " + local);
			if(local.equals(object.getLocation())) {
				objects[index] = object;
				if(spawn) {
					//TODO//player.getActionSender().sendCreateLocalObject(object.getDefinition().getId(), object.getType(), object.getDirection(), local);
				}
			}
		}		
	}
	
	@SuppressWarnings("unused")
	public void removeObject(Location buildingLocation) {
		for(int index = 0; index < objects.length; index++) {
			ConstructionObject object = objects[index];
			if(object != null) {
				if(object.getLocation().equals(buildingLocation)) {
					player.playAnimation(Animation.create(3685));
					//TODO//player.getActionSender().sendDestroyLocalObject(object.getType(), object.getDirection(), buildingLocation);
					objects[index] = null;
					if(player.getConstruction().isInBuildingMode()) {
						ConstructionObject hotspot = hotspots[index];
						Location local = Location.create(hotspot.getLocation().getX() + x, hotspot.getLocation().getY() + y, hotspot.getLocation().getPlane());
						//TODO//player.getActionSender().sendCreateLocalObject(hotspot.getDefinition().getId(), hotspot.getType(), hotspot.getDirection(), local);
					}
				}
			}
		}
		
	}
	
	/**
	 * Gets all the objects for this room.
	 * @return All the gameobjects in this room.
	 */
	public ConstructionObject[] getObjects() {
		return objects;
	}
	
	/**
	 * Gets all the hotspots for this room.
	 * @return All the hotspots in this room.
	 */
	public ConstructionObject[] getHotspots() {
		return hotspots;
	}
	
	/**
	 * Gets all the hotspots for this room, and turns 
	 * them either clockwise or anti-clockwise (90 degree)
	 * @return All the hotspots in this room.
	 */
	@SuppressWarnings("unused")
	public GameObject[] getHotspots(Player player, boolean clockwise) {
		int rotation = tile.getRotation(); //The rotation before we do anything..
		System.out.println("Rotation before we do anything: " + rotation);
		tile.setDirection(Construction.getNewRotation(rotation, clockwise ? 1 : -1));
		for(int index = 0; index < hotspots.length; index++) {
			ConstructionObject object = hotspots[index];
			//BuildingType type = Construction.BuildingType.getBuildingType(object.getDefinition().getId());
			Location local = Location.create(object.getLocation().getX() + this.getX(), object.getLocation().getY() + this.getY(), object.getLocation().getPlane());
			//TODO//player.getActionSender().sendDestroyLocalObject(object.getType(), object.getDirection(), local);
			Location location = null;
			int x = object.getLocation().getX(); // - 3.5;
			int y = object.getLocation().getY(); // - 3.5;
			int z = object.getLocation().getPlane();
			//System.out.println("Before: " + object.getLocation());
			if(clockwise) {
				int addX = getAddX(rotation);
				int addY = getAddY(rotation);
				int newX = y + addX;
				int newY = -x + addY;
				location = Location.create(newX, newY, z);
				object.setDirection(Construction.getNewRotation(object.getDirection(), 1));
			} else {
				//int add = getAdd(type, y);
				int newX = -y;// + add;
				int newY = x;
				location = Location.create(newX, newY, z);
				object.setDirection(Construction.getNewRotation(object.getDirection(), -1));
			}
			object.setLocation(location);
			//System.out.println("After: " + object.getLocation());
			hotspots[index] = object;
		}
		System.out.println("Rotation after we stuff anything: " + tile.getRotation());
		return getHotspots();
	}
	
	private int getAddY(int rotation) {
		if(rotation == 0 || rotation == 1) {
			return 7;
		} else if(rotation == 2 || rotation == 3) {
			return 0;
		}
		return -1; //Shouldn't happen.
	}

	private int getAddX(int rotation) {
		if(rotation == 0 || rotation == 3) {
			return 0;
		} else if(rotation == 1 || rotation == 2) {
			return 7;
		}
		return -1; //Shouldn't happen..
	}
	
	public void saveOtherObject(ConstructionObject obj) {
		for(int index = 0; index < other.length; index++) {
			ConstructionObject other = this.other[index];
			if(other.getLocation().equals(obj.getLocation())) {
				this.other[index] = obj;
			}
		}
		
	}
	
	public ConstructionObject[] getOtherObjects() {
		return other;
	}
	
	public RoomType getRoomType() {
		return roomType;
	}

	/**
	 * The owner of this room.
	 */
	private final Player player;
		
	/**
	 * Contains the objects the owner of this room, 
	 * builded in this room.
	 * If we havn't build up anything yet, the index is null,
	 * and it means we have to draw a "hotspot".
	 */
	private final ConstructionObject[] objects;
	
	/**
	 * Don't know what else to call it.
	 * This contains all the transparent - buildable objects
	 * in this room.
	 */
	private final ConstructionObject[] hotspots;
	
	/**
	 * Contains all the doors and certain walls we have
	 * to spawn manually. :s
	 */
	private final ConstructionObject[] other;
	
	/**
	 * The room type.
	 */
	private final RoomType roomType;
	
	/**
	 * The room coordinates.
	 */
	private final int x;
	private final int y;
	private final int z;
	
	/**
	 * The rooms tile.
	 */
	private final PaletteTile tile;

}
