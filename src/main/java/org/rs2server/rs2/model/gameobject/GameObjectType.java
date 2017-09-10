package org.rs2server.rs2.model.gameobject;

import java.util.Arrays;

/**
 * Defines the possible game object types as defined in the client.
 *
 * @author tommo
 */
public enum GameObjectType {
	UNDEFINED(-1),
	WALL_OBJECT(0, 1, 2, 3),
	WALL_DECORATION(4, 5, 6, 7, 8),
	INTERACTABLE_OBJECT(9, 10, 11, 12),
	GROUND_DECORATION(22);

	private final int[] types;

	GameObjectType(final int... types) {
		this.types = types;
	}

	public int[] getTypes() {
		return types;
	}

	/**
	 * Checks if the given type id is of the given GameObjectType.
	 * @param type The type id.
	 * @param objectType The game object type.
	 * @return true if so, false if not.
	 */
	public static boolean isOfType(final int type, final GameObjectType objectType) {
		return Arrays.stream(objectType.getTypes()).filter(v -> v == type).findFirst().isPresent();
	}

	public static GameObjectType forType(final int type) {
		return Arrays.stream(values()).filter(t -> isOfType(type, t)).findFirst().orElse(UNDEFINED);
	}

/*
OBJECT TYPES:

0	- straight walls, fences etc
1	- diagonal walls corner, fences etc connectors
2	- entire walls, fences etc corners
3	- straight wall corners, fences etc connectors
4	- straight inside wall decoration
5	- straight outside wall decoration
6	- diagonal outside wall decoration
7	- diagonal inside wall decoration
8	- diagonal in wall decoration
9	- diagonal walls, fences etc
10	- all kinds of objects, trees, statues, signs, fountains etc etc
11	- ground objects like daisies etc
12	- straight sloped roofs
13	- diagonal sloped roofs
14	- diagonal slope connecting roofs
15	- straight sloped corner connecting roofs
16	- straight sloped corner roof
17	- straight flat top roofs
18	- straight bottom egde roofs
19	- diagonal bottom edge connecting roofs
20	- straight bottom edge connecting roofs
21	- straight bottom edge connecting corner roofs
22	- ground decoration + map signs (quests, water fountains, shops etc)
*/
}
