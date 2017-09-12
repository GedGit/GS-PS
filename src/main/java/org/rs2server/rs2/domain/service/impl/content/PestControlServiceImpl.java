package org.rs2server.rs2.domain.service.impl.content;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.content.api.GameObjectActionEvent;
import org.rs2server.rs2.content.api.GamePlayerLoginEvent;
import org.rs2server.rs2.content.api.GamePlayerLogoutEvent;
import org.rs2server.rs2.content.api.pestcontrol.PestControlShopEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.content.PestControlService;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.gameobject.GameObjectCardinality;
import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.rs2.model.npc.pc.VoidKnight;
import org.rs2server.rs2.model.player.pc.PestControlBoat;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.player.pc.PestControlInstance;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.util.Misc;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.logging.Logger;

/**
 * Implementation of a pest control service.
 *
 * @author twelve
 */
public final class PestControlServiceImpl implements PestControlService {

	private final List<PestControlBoat> boats;

	private static final Logger LOGGER = Logger.getAnonymousLogger();

	public static final Location BOAT_ONE_ENTER = Location.create(2661, 2639);
	public static final Location BOAT_ONE_EXIT = Location.create(2657, 2639);
 
	private static final int BOAT_WIDGET_ID = 407;

	private static final int SHOP_INTERFACE_ID = 267;
	private static final int POINT_CHILD_ID = 150;

	@SuppressWarnings("unused")
	private static final int ATTACK_ONE_POINT = 86, STRENGTH_ONE_POINT = 87, DEFENCE_ONE_POINT = 88,
			RANGED_ONE_POINT = 89, MAGIC_ONE_POINT = 90, HITPOINTS_ONE_POINT = 91, PRAYER_ONE_POINT = 92;

	@SuppressWarnings("unused")
	private static final int TEN_POINT_OFFSET = 15, ONE_HUNDRED_POINT_OFFSET = 7;

	private static final PestControlBoat BOAT_ONE = new PestControlBoat(BOAT_ONE_ENTER, BOAT_ONE_EXIT);

	@Inject
	public PestControlServiceImpl(HookService service) {
		service.register(this);
		this.boats = ImmutableList.of(BOAT_ONE);
	}

	@Override
	public void setPestControlPoints(@Nonnull Player player, int points) {
		player.getDatabaseEntity().getStatistics().setPestControlPoints(points);
	}

	@Override
	public void openShop(@Nonnull Player player) {
		player.getActionSender()
				.sendString(SHOP_INTERFACE_ID, POINT_CHILD_ID,
						"Points: " + player.getDatabaseEntity().getStatistics().getPestControlPoints())
				.sendInterface(SHOP_INTERFACE_ID, false);
	}

	@Override
	public List<PestControlBoat> getBoats() {
		return boats;
	}

	@Subscribe
	public void onObjectClick(final GameObjectActionEvent clickEvent) {
		if (clickEvent.getActionType() == GameObjectActionEvent.ActionType.OPTION_1) {
			final Player player = clickEvent.getPlayer();
			final GameObject object = clickEvent.getGameObject();

			PestControlBoat boat = boats.get(0);// TODO: redo code to support
												// multiple boats in future
			switch (object.getId()) {
			case 14314:
				removeBoatMember(boat, player);
				break;
			case 14315:
				addBoatMember(boat, player);
				break;
			case 14234:
			case 14236:
			case 14235:
			case 14233:
				handleGates(player, object);
				break;
			}
		}
	}

	@Subscribe
	public void onShopClick(final PestControlShopEvent event) {
		Player player = event.getPlayer();
		if (Constants.DEBUG)
			player.sendMessage("PC Rewards: buttonID: " + event.getButton());
		PestControlItem clickedButton = PestControlItem.ofButton(event.getButton());
		PestControlItem item = PestControlItem.ofItem(player.getSelectedItem());
		if (event.getButton() == 146 && player.getSelectedItem() != -1 && item != null) {
			int cost = item.getCost();
			int points = player.getDatabaseEntity().getStatistics().getPestControlPoints();
			if (cost > points && !(item.getItemId() >= 0 && item.getItemId() <= 6)) {
				player.getActionSender().sendMessage("You don't have enough points to purchase this reward");
				System.out.println("1 "+cost);
				return;
			}
			int child = player.getAttribute("pc_button");
			int ppoints = child >= 86 && child <= 92 ? 1 : child >= 101 && child <= 107 ? 10 : 100;
			if (ppoints > points && item.getItemId() >= 0 && item.getItemId() <= 6) {
				player.getActionSender().sendMessage("You don't have enough points to purchase this reward");
				System.out.println("2 "+cost);
				return;
			}
			// Exception for experience rewards
			if (item.getItemId() >= 0 && item.getItemId() <= 6) {
				if (player.getAttribute("pc_button") == null)
					return;
				int baseXP = (item.getItemId() == 4 || item.getItemId() == 6) ? 8 : item.getItemId() == 5 ? 1 : 35;
				double exp = ppoints * baseXP * player.getSkills().getLevelForExperience(item.getItemId());

				// Lower for prayer, much lower.
				if (item.getItemId() == 5)
					exp = exp / 5;
				player.getSkills().addExperience(item.getItemId(), exp);
				player.getDatabaseEntity().getStatistics().setPestControlPoints(points - ppoints);
				player.getActionSender().sendString(SHOP_INTERFACE_ID, POINT_CHILD_ID,
						"Points: " + player.getDatabaseEntity().getStatistics().getPestControlPoints());
				return;
			}
			player.getDatabaseEntity().getStatistics().setPestControlPoints(points - cost);
			player.setSelectedItem(-1);
			player.getActionSender().sendString(SHOP_INTERFACE_ID, 149, "");
			player.getActionSender().sendString(SHOP_INTERFACE_ID, POINT_CHILD_ID,
					"Points: " + player.getDatabaseEntity().getStatistics().getPestControlPoints());
			Item reward = new Item(item.getItemId());
			if (item.getItemId() == 453) {
				Inventory.addDroppable(player, new Item(454, Misc.random(21, 29)));
				Inventory.addDroppable(player, new Item(441, Misc.random(16, 20)));
			} else if (item.getItemId() == 5320) {
				Inventory.addDroppable(player, new Item(5320, Misc.random(3, 8)));
				Inventory.addDroppable(player, new Item(5322, Misc.random(8, 15)));
				Inventory.addDroppable(player, new Item(5100, Misc.random(2, 6)));
			} else
				player.getInventory().add(reward);
			return;
		}
		if (clickedButton != null) {
			player.setSelectedItem(clickedButton.getItemId());
			player.setAttribute("pc_button", clickedButton.getButtonId());
			String name = clickedButton.toString().replace("_", " ").toLowerCase();
			player.getActionSender().sendString(SHOP_INTERFACE_ID, 149, Misc.upperFirst(name));
		}
	}

	@Override
	public void addBoatMember(PestControlBoat boat, @Nonnull Player player) {
		if (player.getSkills().getCombatLevel() < 40) {
			player.getActionSender().sendDialogue("", DialogueType.MESSAGE, 1755, FacialAnimation.DEFAULT,
					"You need to have a combat level of at least 40 to play Pest Control.");
			return;
		}
		player.setTeleportTarget(boat.getEntrance());
		player.getActionSender()
				.sendString(407, 15, "Points: " + player.getDatabaseEntity().getStatistics().getPestControlPoints())
				.sendString(407, 16, "Novice").sendWalkableInterface(BOAT_WIDGET_ID);
		boat.getPlayers().add(player);
	}

	@Override
	public void removeBoatMember(PestControlBoat boat, @Nonnull Player player) {
		player.setTeleportTarget(boat.getExit());
		player.getActionSender().removeWalkableInterface();
		boat.getPlayers().remove(player);
	}

	@Override
	public boolean containsPlayer(@Nonnull Player player) {
		PestControlBoat boat = getBoats().get(0);
		return (boat != null && (boat.getPlayers().contains(player)
				|| (boat.getInstance() != null && boat.getInstance().getPlayers().contains(player))));
	}

	@Override
	public void handleDeath(@Nonnull Player player) {
		player.setTeleportTarget(
				PestControlInstance.START_LOCATION_BASE.transform(Misc.random(0, 1), Misc.random(0, 1), 0));
		player.getCombatState().setDead(false);
		player.resetVariousInformation();
		PestControlInstance instance = boats.get(0).getInstance();
		if (instance != null) {
			int damage = (player.getAttribute("hits_dealt") == null ? 0 : (int) player.getAttribute("hits_dealt"));
			player.getActionSender().sendString(408, VoidKnight.CHILD, "%" + instance.getKnight().getHealthPercentage())
					.sendString(408, 4, damage > 50 ? ("<col=00FF00>" + damage) : damage + "");
			instance.getPortals().stream().forEach(p -> player.getActionSender().sendString(408,
					p.getCardinality().getHealthChild(), "" + (p.isDestroyed() ? 0 : p.getSkills().getLevel(3))));
			player.getActionSender().sendWalkableInterface(408);
		}
	}

	@Subscribe
	public final void onPlayerLogout(@Nonnull GamePlayerLogoutEvent event) {
		final Player player = event.getPlayer();
		PestControlBoat boat = boats.get(0);
		if (boat != null) {
			Optional<Player> boatOptional = boat.getPlayers().stream()
					.filter(p -> p.getName().equalsIgnoreCase(player.getName())).findAny();
			if (boatOptional.isPresent()) {
				boat.getPlayers().remove(player);
				player.setLocation(boat.getExit());
			}
			PestControlInstance instance = boat.getInstance();
			if (instance != null) {
				Optional<Player> playerOptional = instance.getPlayers().stream()
						.filter(p -> p.getName().equalsIgnoreCase(player.getName())).findAny();
				if (playerOptional.isPresent()) {
					instance.getPlayers().remove(player);
					player.setLocation(boat.getExit());
					if (instance.getPlayers().size() <= 0) {
						LOGGER.info("All players have left the game... Destroying Game");
						boat.endGame();
					}
				}
			}
		}
	}

	@Subscribe
	public final void onPlayerLogin(@Nonnull GamePlayerLoginEvent event) {
		final Player player = event.getPlayer();
		if (BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControl")) {
			player.setTeleportTarget(BOAT_ONE_EXIT);
			LOGGER.info(player.getName() + " moved from Pest Control Island...");
		}
	}

	/**
	 * Opens the gates.
	 * 
	 * @param player
	 *            The player.
	 * @param object
	 *            The game object.
	 */
	private void handleGates(Player player, GameObject object) {
		boolean open = (object.getId() % 2) != 0;
		GameObject second = getSecondDoor(object);
		if (second == null) {
			return;
		}
		if (object.getId() > 14240 || second.getId() > 14240) {
			player.getActionSender().sendMessage("It's too damaged to be moved!");
			return;
		}
		int rotation = getRotation(object);
		int dir = open ? object.getDirection() : rotation;
		GameObjectCardinality cardinality = GameObjectCardinality.forFace(!open ? dir : ((3 + dir) % 4));

		World.getWorld().unregister(object, true);
		World.getWorld().unregister(second, true);
		RegionClipping.removeClipping(object);
		RegionClipping.removeClipping(second);

		Location l = object.getLocation().transform((int) cardinality.getFaceVector().getX(),
				(int) cardinality.getFaceVector().getY(), 0);

		GameObject replacement = new GameObject(l, object.getId() + (open ? 1 : -1), 0,
				open ? rotation : ((cardinality.getFace() + 3) % 4), false);

		World.getWorld().register(replacement);
		RegionClipping.addClipping(replacement);

		l = second.getLocation().transform((int) cardinality.getFaceVector().getX(),
				(int) cardinality.getFaceVector().getY(), 0);

		replacement = new GameObject(l, second.getId() + (open ? 1 : -1), 0,
				open ? getRotation(second) : ((cardinality.getFace() + 3) % 4), false);

		World.getWorld().register(replacement);
		RegionClipping.addClipping(replacement);
	}

	/**
	 * Gets the rotation for the given object.
	 * 
	 * @param object
	 *            The object.
	 * @return The rotation.
	 */
	private int getRotation(GameObject object) {
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
	 * Gets the second door.
	 *
	 * @param object
	 *            The first door object.
	 * @return The second door object.
	 */
	private GameObject getSecondDoor(GameObject object) {
		Location l = object.getLocation();
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				if (x == y) {
					continue;
				}
				GameObject other = object.getRegion().getGameObject(l.transform(x, y, 0),
						o -> o.getDefinition().getName().equals(object.getDefinition().getName()));
				if (other != null) {
					return other;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the object type for the given object id.
	 * 
	 * @param objectId
	 *            The object id.
	 * @return The object type.
	 */
	@SuppressWarnings("unused")
	private int getObjectType(int objectId) {
		if (objectId == 14225 || objectId == 14226 || objectId == 14228 || objectId == 14229) {
			return 9;
		}
		return 0;
	}
}
