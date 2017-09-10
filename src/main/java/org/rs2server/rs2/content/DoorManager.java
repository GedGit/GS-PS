package org.rs2server.rs2.content;

import org.rs2server.cache.format.CacheObjectDefinition;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.rs2.model.player.Player;

public class DoorManager {
	

    /**
     * Handles a door.
     *
     * @param player The player.
     * @param object The door gameobject.
     * @return {@code True} if the object was a door and got handled, {@code false} if not.
     */
    public static boolean handleDoor(Player player, final GameObject object) {
        String name = object.getDefinition().objectName.toLowerCase();
        if (!name.contains("door") && !name.contains("gate")) {
            return false;
        }
        if (object.getId() == 11726 || object.getId() == 7407 || object.getId() == 7408 || object.getId() == 26760 || object.getId() == 24309 && (object.getLocation().equals(Location.create(2847, 3541, 2))) || object.getId() == 24306 && (object.getLocation().equals(Location.create(2847, 3540, 2)))) {
        	return false;
        }
        if (object.getLocation().equals(Location.create(3283, 3210))) {
        	World.getWorld().unregister(object, true);
        	RegionClipping.removeClipping(object);
        	return false;
        }
        GameObject secondDoor = getSecondDoor(object);
        boolean doubleGate = secondDoor != null;
        boolean open = object.getDefinition().getOptions()[0].equalsIgnoreCase("open") || object.getDefinition().getOptions()[0].equalsIgnoreCase("search");
        if (doubleGate && open) {
            return openDoubleDoor(player, object, secondDoor);
        } else if (open) {
            return openDoor(player, object);
        } else if (!open && !doubleGate) {
        	closeDoor(player, object);
        }
        return false;
    }

    public static boolean closeDoubleDoor(Player player, GameObject object, GameObject secondDoor) {
        closeDoubleDoor(player, object);
        closeDoubleDoor(player, secondDoor);
        return true;
    }

    private static boolean closeDoubleDoor(Player player, GameObject object) {
        int direction = object.getDirection();
        if (direction < 0) {
            direction *= -1;
        }
        switch (object.getDirection() % 4) {
            case 2:
                direction--;
                break;
            case 1:
                direction--;
                break;
            case 3:
                direction++;
                break;
            case 4:
                direction++;
                break;
        }
        int transformX = 0, transformY = 0;
        switch (object.getDirection() % 4) {
            case 0:
                transformY--;
                break;
            case 1:
                transformX++;
                break;
            case 2:
                transformY--;
                break;
            case 3:
                transformX--;
                break;
        }
       // ObjectManager.removeCustomObject(object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getZ(), object.getType());
        World.getWorld().unregister(object, true);
        object.setLocation(object.getLocation().transform(transformX, transformY, 0));
        //GameObject toReplace = new GameObject(findNextDoor(object.getId()), object.getLocation(), object.getType(), direction);
        RegionClipping.removeClipping(object);
        GameObject toReplace = new GameObject(object.getLocation(), findNextDoor(object.getId()), object.getType(), direction, false);
        //Location location, int id, int type, int direction, boolean loadedInLandscape
        World.getWorld().register(toReplace);
        RegionClipping.addClipping(toReplace);
        return true;
    }

    public static boolean closeDoor(Player player, final GameObject object) {
        int direction = object.getDirection() - 1;
        if (direction < 0) {
            direction *= -1;
        }
        int transformX = 0, transformY = 0;
        switch (object.getDirection() % 4) {
            case 0:
            	//transformX--;
                //transformY--;
                break;
            case 1:
                transformX++;
                break;
            case 2:
                transformY--;
                break;
            case 3:
                transformX--;
                break;
        }
       // ObjectManager.removeCustomObject(object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getZ(), object.getType());
        World.getWorld().unregister(object, true);
        RegionClipping.removeClipping(object);
        object.setLocation(object.getLocation().transform(transformX, transformY, 0));
        GameObject toReplace = new GameObject(object.getLocation(), findNextDoor(object.getId()), object.getType(), direction, false);
        World.getWorld().register(toReplace);
        RegionClipping.addClipping(toReplace);
        //ObjectManager.addCustomObject(toReplace);
        return true;
    }

    private static boolean openDoor(Player player, final GameObject object) {
        int direction = object.getDirection() + 1;
        int transformX = 0, transformY = 0;
        switch (object.getDirection() % 4) {
            case 0:
                transformX--;
                break;
            case 1:
                transformY++;
                break;
            case 2:
                transformX++;
                break;
            case 3:
                transformY--;
                break;
        }
//        RegionClipping.removeClipping(object.getX() + 1, object.getY(), object.getZ(), 256);
//		RegionClipping.removeClipping(object.getX() - 1, object.getY(), object.getZ(), 256);
        World.getWorld().unregister(object, true);
        RegionClipping.removeClipping(object);
       // ObjectManager.removeCustomObject(object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getZ(), object.getType());
        object.setLocation(object.getLocation().transform(transformX, transformY, 0));
        GameObject toReplace = new GameObject(object.getLocation(), findNextDoor(object.getId()), object.getType(), direction, object.isLoadedInLandscape() ? true : false);
        World.getWorld().register(toReplace);
        RegionClipping.removeClipping(toReplace);
      //  ObjectManager.addCustomObject(toReplace);
        return true;
    }

    private static boolean openDoubleDoor(Player player, final GameObject door, final GameObject secondDoor) {
        openDoubleDoor(player, door);
        openDoubleDoor(player, secondDoor);
        return true;
    }

    private static void openDoubleDoor(Player player, GameObject object) {
        int direction = object.getDirection();
        int transformX = 0, transformY = 0;
        switch (object.getId() % 4) {
            case 2:
                direction += 1;
                break;
            case 1:
                direction -= 1;
                break;
            case 3:
                direction += 4;
            case 4:
                direction += 1;
                break;
        }
        switch (object.getDirection() % 4) {
            case 0:
                transformX--;
                break;
            case 1:
                transformY++;
                break;
            case 2:
                transformX++;
                break;
            case 3:
                transformY--;
                break;
        }
        World.getWorld().unregister(object, true);
       // ObjectManager.removeCustomObject(object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getZ(), object.getType());
        object.setLocation(object.getLocation().transform(transformX, transformY, 0));
        GameObject toReplace = new GameObject(object.getLocation(),findNextDoor(object.getId()), object.getType(), direction, false);
        World.getWorld().register(toReplace);
        // ObjectManager.addCustomObject(toReplace);

    }

    private static GameObject getSecondDoor(GameObject object) {
        GameObject o = null;
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                //o = object.getLocation().transform(i, j, 0).getGameObject();
                if (o != null && !object.equals(o) && (o.getDefinition().getName().toLowerCase().contains("door") || o.getDefinition().getName().toLowerCase().contains("gate"))) {
                    return o;
                }
            }
        }
        //return object.getLocation().transform(x, y, 0).getGameObject();
        return null;
    }

    private static int findNextDoor(int id) {
        CacheObjectDefinition doorDef = CacheObjectDefinition.forID(id);
        String option = doorDef.getOptions()[0];
        if (option.equalsIgnoreCase("open")) {
            for (int i = 0; i < 4; i++) {
                CacheObjectDefinition def = CacheObjectDefinition.forID(id + i);
                if (def.getOptions() != null && def.getOptions()[0] != null && def.getOptions()[0].equalsIgnoreCase("close") && def.objectName.equalsIgnoreCase(doorDef.objectName)) {
                    return id + i;
                }
            }
            for (int i = 0; i > -4; i--) {
                CacheObjectDefinition def = CacheObjectDefinition.forID(id + i);
                if (def.getOptions() != null && def.getOptions()[0] != null && def.getOptions()[0].equalsIgnoreCase("close") && def.objectName.equalsIgnoreCase(doorDef.objectName)) {
                    return id + i;
                }
            }
        } else if (option.equalsIgnoreCase("close")) {
            for (int i = 0; i > -4; i--) {
                CacheObjectDefinition def = CacheObjectDefinition.forID(id + i);
                if (def.getOptions() != null && def.getOptions()[0] != null && def.getOptions()[0].equalsIgnoreCase("open") && def.objectName.equalsIgnoreCase(doorDef.objectName)) {
                    return id + i;
                }
            }
            for (int i = 0; i < 4; i++) {
                CacheObjectDefinition def = CacheObjectDefinition.forID(id + i);
                if (def.getOptions() != null && def.getOptions()[0] != null && def.getOptions()[0].equalsIgnoreCase("open") && def.objectName.equalsIgnoreCase(doorDef.objectName)) {
                    return id + i;
                }
            }
        }
        return 0;
    }

}