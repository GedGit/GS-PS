package org.rs2server.rs2.domain.service.api;

import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.map.path.PathFinder;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;

import java.util.List;

/**
 * Service providing path finding capabilities.
 *
 * @author tommo
 */
public interface PathfindingService {

	/**
	 * Attempts to find a path and traverse a mob along it.
	 * @param mob The entity for whom to find a path.
	 * @param gameObject The target destination.
	 */
	boolean travelToObject(final Mob mob, final GameObject gameObject);

	boolean travelToPlayer(final Mob mob, final Player player);

	boolean travelToNpc(Mob mob, NPC npc);

	/**
	 * Attempts to find a path and traverse an entity along it using the default A* path finder.
	 * @param mob The entity for whom to find a path.
	 * @param target The target destination.
	 */
	boolean travel(final Mob mob, final Location target);

	/**
	 * Attempts to find a path and traverse an entity along it using the given path finder.
	 * @param mob The entity for whom to find a path.
	 * @param target The target destination.
	 * @param pathFinder The path finding implementation to use.
	 */
	boolean travel(final Mob mob, final Location target, final PathFinder pathFinder);

	List<Location> findPath(final PathFinder pathFinder, final Location source, final Location destination);

}
