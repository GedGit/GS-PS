package org.rs2server.rs2.model.map.path.astar;

import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.gameobject.GameObjectCardinality;
import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.rs2.model.map.path.ClippingFlag;
import org.rs2server.rs2.model.map.path.PathPrecondition;
import org.rs2server.rs2.model.npc.NPC;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * A set of preconditions for when trying to interact with an npc. Currently
 * only used for the packet we call 'trade'.
 *
 * @author Twelve
 */
public class NpcReachedPrecondition implements PathPrecondition {

	private final NPC npc;

	public NpcReachedPrecondition(NPC npc) {
		this.npc = npc;
	}

	@Override
	public boolean targetReached(int currentX, int currentY, int destinationX, int destinationY) {
		final int flags = RegionClipping.getClippingMask(currentX, currentY, npc.getPlane());
		final int distance = distance(currentX, currentY, destinationX, destinationY);

		if (distance == 2) {
			if (npc.getId() == 394) {
				return true;
			}
			final List<GameObject> objects = npc.getRegion().getGameObjects().stream().filter(this::canInteractThrough)
					.filter(o -> o.getLocation().distance(destinationX, destinationY, npc.getPlane()) == 1)
					.collect(toList());

			Optional<GameObject> objectOption = objects.stream().filter(o -> {
				final GameObjectCardinality cardinality = GameObjectCardinality.forFace(o.getDirection());
				return o.getX() == (int) (currentX + cardinality.getFaceVector().getX())
						&& o.getY() == (int) (currentY + cardinality.getFaceVector().getY());
			}).findFirst();

			if (objectOption.isPresent()) {
				final GameObject object = objectOption.get();
				final GameObjectCardinality cardinality = GameObjectCardinality.forFace(object.getDirection());

				if (object.getX() + cardinality.getFaceVector().getX() == destinationX
						&& object.getY() + cardinality.getFaceVector().getY() == destinationY) {
					return true;
				}
			}
		}

		if (currentX == destinationX && currentY == destinationY + 1 && ClippingFlag.BLOCK_SOUTH.and(flags) != 0) {
			return false;
		}
		if (currentX == destinationX && currentY == destinationY - 1 && ClippingFlag.BLOCK_NORTH.and(flags) != 0) {
			return false;
		}
		if (currentX == destinationX + 1 && currentY == destinationY && ClippingFlag.BLOCK_WEST.and(flags) != 0) {
			return false;
		}
		if (currentX == destinationX - 1 && currentY == destinationY && ClippingFlag.BLOCK_EAST.and(flags) != 0) {
			return false;
		}
		if (currentX == destinationX - 1 && currentY == destinationY + 1
				&& ClippingFlag.BLOCK_SOUTH_WEST.and(flags) != 0) {
			return false;
		}
		if (currentX == destinationX + 1 && currentY == destinationY + 1
				&& ClippingFlag.BLOCK_SOUTH_EAST.and(flags) != 0) {
			return false;
		}
		if (currentX == destinationX + 1 && currentY == destinationY - 1
				&& ClippingFlag.BLOCK_NORTH_WEST.and(flags) != 0) {
			return false;
		}
		if (currentX == destinationX - 1 && currentY == destinationY - 1
				&& ClippingFlag.BLOCK_NORTH_EAST.and(flags) != 0) {
			return false;
		}
		return distance == 1;
	}

	private boolean canInteractThrough(GameObject object) {
		return object.getDefinition().getName().contains("Bank booth") || object.getId() == 24106; // Bar
																									// (falador
																									// bartender)
	}

	private static int distance(int sx, int sy, int dx, int dy) {
		int deltaX = sx - dx;
		int deltaY = sy - dy;
		return Math.abs(deltaX) + Math.abs(deltaY);
	}
}
