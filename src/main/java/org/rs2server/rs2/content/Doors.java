package org.rs2server.rs2.content;

import org.rs2server.cache.format.CacheObjectDefinition;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.map.RegionClipping;

import java.util.ArrayList;

public class Doors {

	/**
	 * Constructs a new door.
	 *
	 * @param door
	 *            The door we're managing.
	 */
	public Doors(GameObject door) {
		this.object = door;
		this.originalId = object.getId();
		this.currentId = object.getId();
		this.originalX = object.getX();
		this.originalY = object.getY();
		this.currentX = originalX;
		this.currentY = originalY;
		this.originalRotation = object.getDirection();
		this.currentRotation = originalRotation;
		this.open = object.getDefinition().getOptions()[0].equalsIgnoreCase("Close");
	}

	/**
	 * The door we're managing.
	 */
	private GameObject object;

	/**
	 * The doors original object id.
	 */
	private int originalId;

	/**
	 * The doors current object id.
	 */
	private int currentId;

	/**
	 * The doors original x coordinate.
	 */
	private int originalX;

	/**
	 * The doors original y coordinate.
	 */
	private int originalY;

	/**
	 * The doors current x coordinate.
	 */
	private int currentX;

	/**
	 * The doors current y coordinate.
	 */
	private int currentY;

	/**
	 * The doors original rotation.
	 */
	private int originalRotation;

	/**
	 * The doors current rotation.
	 */
	private int currentRotation;

	/**
	 * Is the door open?
	 */
	private boolean open;

	/**
	 * An array of all the doors.
	 */
	private static ArrayList<Doors> doors = new ArrayList<Doors>();

	/**
	 * An array list of special doors (these doors do not change object id's).
	 */
	private static final int[] SpecialDoors = { 7129, 7141, 7168, 7173, 7169, 7174, 11774, 11775 };

	/**
	 * Gets the door to manage.
	 *
	 * @param door
	 *            The door to manage.
	 * @return The <code>Doors</code>.
	 */
	private static Doors getDoor(GameObject door) {
		for (Doors d : doors) {
			if (d != null)
				if (d.currentId == door.getId() && d.currentX == door.getX() && d.currentY == door.getY()) {
					return d;
				}
		}
		Doors d = new Doors(door);
		doors.add(d);
		return d;
	}

	/**
	 * Gets the rotation for the given object.
	 *
	 * @param object
	 *            The object.
	 * @return The rotation.
	 */
	@SuppressWarnings("unused")
	private static int getRotation(GameObject object) {
		int id = object.getId();
		if (id > 14236) {
			id -= 4;
		}
		switch (id) {
		case 14233:
			return (object.getDirection() + 1) % 4;
		case 14234:
			return object.getDirection() % 4;
		case 14235:
			return (object.getDirection() + 3) % 4;
		case 14236:
			return (object.getDirection() + 2) % 4;
		}
		return 0;
	}

	/**
	 * Manages a door.
	 *
	 * @param door
	 *            The door we're managing.
	 */
	public static boolean manageDoor(GameObject door) {
		String name = door.getDefinition().objectName.toLowerCase();
		if (!name.contains("door") && !name.contains("gate"))
			return false;
		// Temporary, find a better way of handling these
		if (door.getId() == 14880 || door.getId() == 7257 || door.getId() == 24318 || door.getId() == 24306
				|| door.getId() == 24309 || door.getId() == 1732 || door.getId() == 1733 || door.getId() == 1579
				|| door.getId() == 11867)
			return false;
		Doors d = getDoor(door);
		if (d == null)
			return false;
		if (door.getId() == 29489 || door.getId() == 29488 || door.getId() == 29486 || door.getId() == 29487)
			return false;

		if (door.getId() == 11726 || (door.getId() >= 26502 && door.getId() <= 26505) || door.getId() == 7179
				|| door.getId() == 7182 || door.getId() == 14234 || door.getId() == 14236 || door.getId() == 14235
				|| door.getId() == 14233 || door.getId() == 7407 || door.getId() == 7408 || door.getId() == 26760
				|| door.getId() == 20925) {
			return false;
		}
		if (door.getId() == 24309 && (door.getLocation().equals(Location.create(2847, 3541, 2)))
				|| door.getId() == 24306 && (door.getLocation().equals(Location.create(2847, 3540, 2)))) {
			return false;
		}
		if (door.getLocation().equals(Location.create(3283, 3210))) {
			World.getWorld().unregister(door, true);
			RegionClipping.removeClipping(door);
			return false;
		}

		int xAdjustment = 0, yAdjustment = 0;
		if (d.object.getType() == 0) {
			if (!d.open) {
				if (d.originalRotation == 0 && d.currentRotation == 0) {
					xAdjustment = -1;
				} else if (d.originalRotation == 1 && d.currentRotation == 1) {
					yAdjustment = 1;
				} else if (d.originalRotation == 2 && d.currentRotation == 2) {
					xAdjustment = 1;
				} else if (d.originalRotation == 3 && d.currentRotation == 3) {
					yAdjustment = -1;
				}
			} else {
				if (d.originalRotation == 0 && d.currentRotation == 0) {
					yAdjustment = 1;
				} else if (d.originalRotation == 1 && d.currentRotation == 1) {
					xAdjustment = 1;
				} else if (d.originalRotation == 2 && d.currentRotation == 2) {
					yAdjustment = -1;
				} else if (d.originalRotation == 3 && d.currentRotation == 3) {
					xAdjustment = -1;
				}
			}
		} else if (d.object.getType() == 9) {
			if (!d.open) {
				if (d.originalRotation == 0 && d.currentRotation == 0) {
					xAdjustment = 1;
				} else if (d.originalRotation == 1 && d.currentRotation == 1) {
					xAdjustment = 1;
				} else if (d.originalRotation == 2 && d.currentRotation == 2) {
					xAdjustment = -1;
				} else if (d.originalRotation == 3 && d.currentRotation == 3) {
					xAdjustment = -1;
				}
			} else {
				if (d.originalRotation == 0 && d.currentRotation == 0) {
					xAdjustment = 1;
				} else if (d.originalRotation == 1 && d.currentRotation == 1) {
					xAdjustment = 1;
				} else if (d.originalRotation == 2 && d.currentRotation == 2) {
					xAdjustment = -1;
				} else if (d.originalRotation == 3 && d.currentRotation == 3) {
					xAdjustment = -1;
				}
			}
		}
		if (d.originalX == d.currentX && d.originalY == d.currentY) {
			d.currentX += xAdjustment;
			d.currentY += yAdjustment;
		} else {
			d.currentX = d.originalX;
			d.currentY = d.originalY;
		}
		if (d.currentId == d.originalId) {
			if (!d.open) {
				d.currentId += 1;
			} else {
				d.currentId -= 1;
			}
		} else {
			if (!d.open) {
				d.currentId -= 1;
			} else {
				d.currentId += 1;
			}
		}
		if (d.object.getType() == 0) {
			if (!d.open) {
				if (d.originalRotation == 0 && d.currentRotation == 0) {
					d.currentRotation = 1;
				} else if (d.originalRotation == 1 && d.currentRotation == 1) {
					d.currentRotation = 2;
				} else if (d.originalRotation == 2 && d.currentRotation == 2) {
					d.currentRotation = 3;
				} else if (d.originalRotation == 3 && d.currentRotation == 3) {
					d.currentRotation = 0;
				} else if (d.originalRotation != d.currentRotation) {
					d.currentRotation = d.originalRotation;
				}
			} else {
				if (d.originalRotation == 0 && d.currentRotation == 0) {
					d.currentRotation = 3;
				} else if (d.originalRotation == 1 && d.currentRotation == 1) {
					d.currentRotation = 0;
				} else if (d.originalRotation == 2 && d.currentRotation == 2) {
					d.currentRotation = 1;
				} else if (d.originalRotation == 3 && d.currentRotation == 3) {
					d.currentRotation = 2;
				} else if (d.originalRotation != d.currentRotation) {
					d.currentRotation = d.originalRotation;
				}
			}
		} else if (d.object.getType() == 9) {
			if (!d.open) {
				if (d.originalRotation == 0 && d.currentRotation == 0) {
					d.currentRotation = 3;
				} else if (d.originalRotation == 1 && d.currentRotation == 1) {
					d.currentRotation = 2;
				} else if (d.originalRotation == 2 && d.currentRotation == 2) {
					d.currentRotation = 1;
				} else if (d.originalRotation == 3 && d.currentRotation == 3) {
					d.currentRotation = 0;
				} else if (d.originalRotation != d.currentRotation) {
					d.currentRotation = d.originalRotation;
				}
			} else {
				if (d.originalRotation == 0 && d.currentRotation == 0) {
					d.currentRotation = 3;
				} else if (d.originalRotation == 1 && d.currentRotation == 1) {
					d.currentRotation = 0;
				} else if (d.originalRotation == 2 && d.currentRotation == 2) {
					d.currentRotation = 1;
				} else if (d.originalRotation == 3 && d.currentRotation == 3) {
					d.currentRotation = 2;
				} else if (d.originalRotation != d.currentRotation) {
					d.currentRotation = d.originalRotation;
				}
			}
		}
		World.getWorld().unregister(door, true);
		RegionClipping.removeClipping(door);
		GameObject object = new GameObject(Location.create(d.currentX, d.currentY, door.getPlane()),
				getOpposite(door.getId()), door.getType(), d.currentRotation, false);
		World.getWorld().register(object);
		RegionClipping.addClipping(object);
		return true;
	}

	/**
	 * Is the object a door?
	 *
	 * @param name
	 *            The object name.
	 * @return If yes <code>true</code>, if no <code>false</code>.
	 */
	public static boolean isDoor(String name) {
		return name.equals("Door") || name.equals("Gate");
	}

	private static boolean special(int id) {
		for (int i : SpecialDoors) {
			if (i == id) {
				return true;
			}
		}
		return false;
	}

	public static int getOpposite(int id) {
		CacheObjectDefinition doorDef = CacheObjectDefinition.forID(id);
		String option = doorDef.getOptions()[0];
		if (!special(id)) {
			if (option.equalsIgnoreCase("open")) {
				for (int i = 0; i < 4; i++) {
					CacheObjectDefinition def = CacheObjectDefinition.forID(id + i);
					if (def.getOptions() != null && def.getOptions()[0] != null
							&& def.getOptions()[0].equalsIgnoreCase("close")
							&& def.objectName.equalsIgnoreCase(doorDef.objectName)) {
						return id + i;
					}
				}
				for (int i = 0; i > -4; i--) {
					CacheObjectDefinition def = CacheObjectDefinition.forID(id + i);
					if (def.getOptions() != null && def.getOptions()[0] != null
							&& def.getOptions()[0].equalsIgnoreCase("close")
							&& def.objectName.equalsIgnoreCase(doorDef.objectName)) {
						return id + i;
					}
				}
			} else if (option.equalsIgnoreCase("close")) {
				for (int i = 0; i > -4; i--) {
					CacheObjectDefinition def = CacheObjectDefinition.forID(id + i);
					if (def.getOptions() != null && def.getOptions()[0] != null
							&& def.getOptions()[0].equalsIgnoreCase("open")
							&& def.objectName.equalsIgnoreCase(doorDef.objectName)) {
						return id + i;
					}
				}
				for (int i = 0; i < 4; i++) {
					CacheObjectDefinition def = CacheObjectDefinition.forID(id + i);
					if (def.getOptions() != null && def.getOptions()[0] != null
							&& def.getOptions()[0].equalsIgnoreCase("open")
							&& def.objectName.equalsIgnoreCase(doorDef.objectName)) {
						return id + i;
					}
				}
			}
		} else {
			switch (id) {
			case 11774:
				return 11775;
			case 11775:
				return 11774;
			case 11772:
			case 11776:
			case 7122:
			case 24051:
			case 24055:
				return id + 1;
			case 7123:
			case 11773:
			case 24052:
			case 24056:
				return id - 1;
			case 7129:
				return 7141;
			case 11778:
				return 11780;
			case 11780:
				return 11778;
			case 7141:
				return 7129;
			case 7168:
				return 7173;
			case 7173:
				return 7168;
			case 7169:
				return 7174;
			case 7174:
				return 7169;

			default:
				return id;
			}
		}
		return id;
	}

}