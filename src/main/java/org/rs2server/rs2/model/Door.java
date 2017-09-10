package org.rs2server.rs2.model;

import org.rs2server.cache.format.CacheObjectDefinition;
import org.rs2server.rs2.model.map.RegionClipping;

import java.util.Map;


@SuppressWarnings("unused")
public class Door {

	public static enum Directions {
		SOUTH(0), NORTH(1), WEST(2), EAST(3);
		
		int direction;
		
		Directions(int dir) {
			this.direction = dir;
		}
		
		
		public int valueOf() {
			return direction;
		}
	}
	
	public static int originalFace = -1;
	
	public static void handleDoor(GameObject obj) {
		boolean open = obj.getDefinition().getOptions()[0].toLowerCase().equals("open");

		Map<Integer, Integer> doors = open ? CacheObjectDefinition.CLOSE_DOOR_MAP : CacheObjectDefinition.OPEN_DOOR_MAP; 

		int newId = obj.getOpposite();
		if (originalFace == -1) {
			originalFace = obj.getDirection();
		}

		if (newId != -1) {
			int newRotation = getNextFace(obj, open);//findRotation(obj, open);
			RegionClipping.removeClipping(obj);
			World.getWorld().unregister(obj, true);//could be why lol
			GameObject replacement = new GameObject(obj.getLocation(), newId, obj.getType(), newRotation, false);
			RegionClipping.addClipping(replacement);
			World.getWorld().register(replacement);
		}
	}

	private static int findRotation(GameObject obj, boolean opening) {
		int rotation = 0;
		rotation = obj.getDirection();
		return rotation;
	}

	private static int findCloseDoorId(GameObject obj) {
		int newId = 0;
		for (int i = 0; i < 4; i++) {
			CacheObjectDefinition def = CacheObjectDefinition.forID(obj.getId() + i);
			if (def.getOptions()[0] == null)
				continue;
			if (def.getOptions()[0].toLowerCase().equals("close")) {
				newId = def.getId();
				break;
			}
		}
		return newId;
	}

	private static int findOpenDoorId(GameObject obj) {
		int newId = 0;
		for (int i = 0; i < 4; i++) {
			CacheObjectDefinition def = CacheObjectDefinition.forID(obj.getId() - i);
			if (def.getOptions()[0] == null)
				continue;
			if (def.getOptions()[0].toLowerCase().equals("open")) {
				newId = def.getId();
				break;
			}
		}
		return newId;

	}
	
	private static int getNextFace(GameObject obj, boolean open) {
		int face = -1;
		 if (obj.getDirection() == 0 && originalFace == 0) {
             face = 1;
         } else if (obj.getDirection() == 1 && originalFace == 1) {
             face = 2;
         } else if (obj.getDirection() == 2 && originalFace == 2) {
             face = 3;
         } else if (obj.getDirection() == 3 && originalFace == 3) {
             face = 0;
         } else if (originalFace != obj.getDirection()) {
             face = originalFace;
         }
		 if (face == originalFace) {
			 originalFace = -1;
		 }
		 return face;
//		 if (obj.type == 0) {
//             if (open) {
//                 if (door.originalFace == 0 && door.currentFace == 0) {
//                     face = 1;
//                 } else if (door.originalFace == 1 && door.currentFace == 1) {
//                     face = 2;
//                 } else if (door.originalFace == 2 && door.currentFace == 2) {
//                     face = 3;
//                 } else if (door.originalFace == 3 && door.currentFace == 3) {
//                     face = 0;
//                 } else if (door.originalFace != door.currentFace) {
//                     face = door.originalFace;
//                 }
//             } else {
//                 if (door.originalFace == 0 && door.currentFace == 0) {
//                     face = 3;
//                 } else if (door.originalFace == 1 && door.currentFace == 1) {
//                     face = 0;
//                 } else if (door.originalFace == 2 && door.currentFace == 2) {
//                     face = 1;
//                 } else if (door.originalFace == 3 && door.currentFace == 3) {
//                     face = 2;
//                 } else if (door.originalFace != door.currentFace) {
//                     face = door.originalFace;
//                 }
//             }
//         } else if (door.type == 9) {
//             if (door.open == 0) {
//                 if (door.originalFace == 0 && door.currentFace == 0) {
//                     face = 3;
//                 } else if (door.originalFace == 1 && door.currentFace == 1) {
//                     face = 2;
//                 } else if (door.originalFace == 2 && door.currentFace == 2) {
//                     face = 1;
//                 } else if (door.originalFace == 3 && door.currentFace == 3) {
//                     face = 0;
//                 } else if (door.originalFace != door.currentFace) {
//                     face = door.originalFace;
//                 }
//             } else if (door.open == 1) {
//                 if (door.originalFace == 0 && door.currentFace == 0) {
//                     face = 3;
//                 } else if (door.originalFace == 1 && door.currentFace == 1) {
//                     face = 0;
//                 } else if (door.originalFace == 2 && door.currentFace == 2) {
//                     face = 1;
//                 } else if (door.originalFace == 3 && door.currentFace == 3) {
//                     face = 2;
//                 } else if (door.originalFace != door.currentFace) {
//                     face = door.originalFace;
//                 }
//             }
//         }
	}


}