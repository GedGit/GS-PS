package org.rs2server.rs2.domain.service.impl;

import com.google.common.collect.ImmutableList;
import org.rs2server.rs2.domain.service.api.PathfindingService;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.map.path.PathFinder;
import org.rs2server.rs2.model.map.path.PathPrecondition;
import org.rs2server.rs2.model.map.path.astar.AStarPathFinder;
import org.rs2server.rs2.model.map.path.astar.NpcReachedPrecondition;
import org.rs2server.rs2.model.map.path.astar.ObjectReachedPrecondition;
import org.rs2server.rs2.model.map.path.astar.PlayerReachedPrecondition;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import java.util.List;

/**
 * @author tommo
 */
public class PathfindingServiceImpl implements PathfindingService {

	@Override
	public boolean travelToObject(Mob mob, GameObject gameObject) {
		if (mob.isPlayer()) {
			final Player player = (Player) mob;
			player.faceObject(gameObject);
		}

		final PathPrecondition objectReachedPrecondition = new ObjectReachedPrecondition(mob, gameObject);
		final PathFinder pathFinder = new AStarPathFinder(ImmutableList.of(objectReachedPrecondition));

		return travel(mob, gameObject.getLocation(), pathFinder);
	}

	@Override
	public boolean travelToPlayer(Mob mob, Player player) {
		mob.face(player.getLocation());

		final PathPrecondition playerReachedPrecondition = new PlayerReachedPrecondition(player);
		final PathFinder pathFinder = new AStarPathFinder(ImmutableList.of(playerReachedPrecondition));

		return travel(mob, player.getLocation(), pathFinder);
	}

	@Override
	public boolean travelToNpc(Mob mob, NPC npc) {
		if (mob.isPlayer())
			mob.face(npc.getLocation());

		final PathPrecondition npcReachedPrecondition = new NpcReachedPrecondition(npc);
		final PathFinder pathFinder = new AStarPathFinder(ImmutableList.of(npcReachedPrecondition));

		Location target = npc.getCentreLocation();
		if (npc.getSize() > 1) {
			Location nearest = null;

			for (int x = -((npc.getSize() / 2) - 1); x < npc.getSize() / 2; x++) {
				for (int y = -((npc.getSize() / 2) - 1); y < npc.getSize() / 2; y++) {
					final Location neighbour = npc.getCentreLocation().transform(x, y, 0);
					if (nearest == null || mob.getLocation().distance(neighbour) < mob.getLocation().distance(nearest))
						nearest = neighbour;
				}
			}

			assert nearest != null;
			target = nearest;
		}

		return travel(mob, target, pathFinder);
	}


	@Override
	public boolean travel(Mob mob, Location target) {
		return travel(mob, target, new AStarPathFinder());
	}

	@Override
	public boolean travel(Mob mob, Location target, PathFinder pathFinder) {
		if (!mob.getCombatState().canMove()) {
			return false;
		} else if (mob.getLocation().equals(target)) {
			return true;
		}

		final List<Location> path = findPath(pathFinder, mob.getLocation(), target);
		if (path == null) {
			return false;
		}
		if (mob.isPlayer() && mob.getActionSender() != null) {
			mob.getActionSender().closeAll();
		}
		mob.getWalkingQueue().reset();
		for (final Location tile : path) {
			mob.getWalkingQueue().addStep(tile.getX(), tile.getY());
		}
		mob.getWalkingQueue().finish();
		return true;
	}

	@Override
	public List<Location> findPath(final PathFinder pathFinder, Location source, Location destination) {
		return pathFinder.findPath(source, destination);
	}

}
