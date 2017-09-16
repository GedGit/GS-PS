package org.rs2server.rs2.model.skills;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.GroundItemService;
import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.map.path.DefaultPathFinder;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

/**
 * @author Tyluur <ItsTyluur@gmail.com>
 */
public class Firemaking {// don't use that system. o lol, try looking for firemaking usign that system
							// itd be way easier to implmenent

	private Player player;

	/**
	 * The player who is firemaking.
	 * 
	 * @return player
	 */
	public Player getPlayer() {
		return player;
	}

	public Firemaking(Player player) {
		this.player = player;
	}

	private enum Log {

		NORMAL(1511, 1, 40),

		PYRE(3438, 5, 50),

		OAK(1521, 15, 60),

		OAK_PYRE(3340, 20, 70),

		WILLOW(1519, 30, 90),

		WILLOW_PYRE(3442, 35, 100),

		TEAK(6333, 35, 105),

		TEAK_PYRE(6211, 40, 120),

		MAPLE(1517, 45, 135),

		MAPLE_PYRE(3444, 50, 175),

		ARCTIC_PINE(10810, 45, 125),

		ARCTIC_PINE_PYRE(10808, 47, 158),

		MAHOGANY(6332, 50, 157.5),

		MAHOG_PYRE(6213, 55, 210),

		YEW(1515, 60, 202.5),

		YEW_PYRE(3446, 65, 225),

		MAGIC(1513, 75, 303.8),

		MAGIC_PYRE(3448, 80, 404),

		REDWOOD(19669, 90, 350),

		REDWOOD_PYRE(19672, 95, 500);

		private int id, reqLevel;
		private double exp;

		/**
		 * The logging item.
		 * 
		 * @return new Item(id)
		 */
		public Item getItem() {
			return new Item(id);
		}

		/**
		 * The required level to light the logging.
		 * 
		 * @return reqLevel
		 */
		public int getRequiredLevel() {
			return reqLevel;
		}

		/**
		 * The experience one gains from lighting the logging.
		 * 
		 * @return exp
		 */
		public double getExperience() {
			return exp;
		}

		/**
		 * Constructs a new logging.
		 * 
		 * @param id
		 *            The item id of the logging.
		 * @param reqLevel
		 *            The required level to light the logging.
		 * @param exp
		 *            The experience one gains from lighting the logging.
		 */
		Log(int id, int reqLevel, double exp) {
			this.id = id;
			this.reqLevel = reqLevel;
			this.exp = exp;
		}

	}

	/**
	 * Lights a logging.
	 * 
	 * @param log
	 *            The logging to light.
	 */
	public void light(final Log log) {
		if (player.isLighting())
			return;
		final Item item = log.getItem();
		if (log.getRequiredLevel() > player.getSkills().getLevel(Skills.FIREMAKING)) {
			String vowels[] = { "a", "e", "i", "o", "u" };
			for (String vowel : vowels) {
				String itemName = item.getDefinition2().getName().trim().replaceAll("_", " ");
				player.getActionSender().sendMessage("You must have a Firemaking level of " + log.getRequiredLevel()
						+ " to light " + (itemName.startsWith(vowel) ? "an" : "a") + " " + itemName + ".");
			}
			return;
		}
		for (GameObject obj : player.getRegion().getGameObjects()) {
			if (obj != null && obj.getType() != 22 && obj.getLocation().equals(player.getLocation())) {
				player.getActionSender().sendMessage("You cannot light a fire here.");
				return;
			}
		}
		if (player.getRegionId() == 6200) {
			player.sendMessage("You cannot light a fire at the home area.");
			return;
		}

		player.setLighting(true);
		player.getInventory().remove(item);
		player.playAnimation(Animation.create(733));
		player.getWalkingQueue().reset();
		GroundItemService groundItemService = Server.getInjector().getInstance(GroundItemService.class);
		groundItemService.createGroundItem(player,
				new GroundItemService.GroundItem(item, player.getLocation(), player, false));
		player.getActionSender().sendMessage("You attempt to light the logs..");
		World.getWorld().submit(new Event(lightDelay(log)) {
			@Override
			public void execute() {

				player.playAnimation(Animation.create(-1));
				Optional<GroundItemService.GroundItem> logs = groundItemService.getGroundItem(item.getId(),
						player.getLocation());
				if (!logs.isPresent()) {
					this.stop();
					return;
				}
				groundItemService.removeGroundItem(logs.get());
				final GameObject fire = new GameObject(player.getLocation(), 5249, 10, 0, false);
				World.getWorld().register(fire);
				player.getActionSender().sendMessage("The fire catches and the logs begin to burn.");
				if (addExperience(log)) {
					player.playGraphics(Graphic.create(199));
					player.getActionSender().sendMessage("Congratulations! You have advanced a level in Firemaking to "
							+ player.getSkills().getLevelForExperience(Skills.FIREMAKING) + ".");
				}
				World.getWorld().submit(new Tickable(180) {
					@Override
					public void execute() {
						groundItemService.createGroundItem(player,
								new GroundItemService.GroundItem(new Item(592), fire.getLocation(), player, true));
						World.getWorld().unregister(fire, true);
						this.stop();
					}
				});
				ArrayList<String> badDirections = new ArrayList<String>();
				for (GameObject obj : player.getRegion().getGameObjects()) {
					if (obj != null && obj.getType() != 22 && obj.getType() != 0) {
						if (obj.getLocation().equals(Location.create(player.getLocation().getX() - 1,
								player.getLocation().getY(), player.getLocation().getPlane()))
								&& obj.getHeight() == player.getHeight()) {
							badDirections.add("West");
						} else if (obj
								.getLocation().equals(Location.create(player.getLocation().getX(),
										player.getLocation().getY(), player.getLocation().getPlane()))
								&& obj.getHeight() == player.getHeight()) {
							badDirections.add("North");
						} else if (obj
								.getLocation().equals(Location.create(player.getLocation().getX(),
										player.getLocation().getY(), player.getLocation().getPlane()))
								&& obj.getHeight() == player.getHeight()) {
							badDirections.add("South");
						} else if (obj
								.getLocation().equals(Location.create(player.getLocation().getX(),
										player.getLocation().getY(), player.getLocation().getPlane()))
								&& obj.getHeight() == player.getHeight()) {
							badDirections.add("East");
						}
					}
					Location newLocation = null;
					if (!badDirections.contains("West")) {
						newLocation = Location.create(player.getLocation().getX() - 1, player.getLocation().getY(), 0);
					} else if (!badDirections.contains("North")) {
						newLocation = Location.create(player.getLocation().getX(), player.getLocation().getY() + 1, 0);
					} else if (!badDirections.contains("South")) {
						newLocation = Location.create(player.getLocation().getX(), player.getLocation().getY() - 1, 0);
					} else if (!badDirections.contains("East")) {
						newLocation = Location.create(player.getLocation().getX() + 1, player.getLocation().getY(), 0);
					}
					badDirections.clear();
					if (newLocation.equals(null)) {
						this.stop();
						return;
					}

					World.getWorld().doPath(new DefaultPathFinder(), player, newLocation.getX(), newLocation.getY());
				}
				World.getWorld().submit(new Tickable(1) {
					@Override
					public void execute() {
						this.stop();
						player.setLighting(false);
					}
				});
				this.stop();
			}
		});
	}

	/**
	 * Adds experience to the player for a specific logging.
	 * 
	 * @param log
	 *            The logging we will add experience for.
	 * @return true if the player has gained a level.
	 */
	private boolean addExperience(Log log) {
		int beforeLevel = player.getSkills().getLevelForExperience(Skills.FIREMAKING);
		player.getSkills().addExperience(Skills.FIREMAKING, log.getExperience());
		int afterLevel = player.getSkills().getLevelForExperience(Skills.FIREMAKING);
		if (afterLevel > beforeLevel) {
			return true;
		}
		return false;
	}

	/**
	 * Light delay for a specific logging.
	 * 
	 * @param log
	 *            The logging.
	 * @return The light delay.
	 */
	private int lightDelay(Log log) {
		return random(800, (int) ((Math.sqrt(log.getRequiredLevel() * 1000)
				* (99 - player.getSkills().getLevel(Skills.FIREMAKING)))));
	}

	/**
	 * Finds the logging for an item.
	 * 
	 * @param item
	 *            The logging item.
	 * @return The logging for the item.
	 */
	public Log findLog(Item item) {
		switch (item.getDefinition2().getId()) {
		case 1511:
			return Log.NORMAL;
		case 1521:
			return Log.OAK;
		case 1519:
			return Log.WILLOW;
		case 6333:
			return Log.TEAK;
		case 1517:
			return Log.MAPLE;
		case 6332:
			return Log.MAHOGANY;
		case 1515:
			return Log.YEW;
		case 1513:
			return Log.MAGIC;
		case 3438:
			return Log.PYRE;
		case 3440:
			return Log.OAK_PYRE;
		case 3442:
			return Log.WILLOW_PYRE;
		case 10810:
			return Log.ARCTIC_PINE;
		case 10808:
			return Log.ARCTIC_PINE_PYRE;
		case 2613:
			return Log.MAHOG_PYRE;
		case 3444:
			return Log.MAPLE_PYRE;
		case 3446:
			return Log.YEW_PYRE;
		case 3448:
			return Log.MAGIC_PYRE;
		case 19669:
			return Log.REDWOOD;
		case 19672:
			return Log.REDWOOD_PYRE;
		}
		return null;
	}

	/**
	 * Returns a random integer with min as the inclusive lower bound and max as the
	 * exclusive upper bound.
	 *
	 * @param min
	 *            The inclusive lower bound.
	 * @param max
	 *            The exclusive upper bound.
	 * @return Random integer min <= n < max.
	 */
	private int random(int min, int max) {
		Random random = new Random();
		int n = Math.abs(max - min);
		return Math.min(min, max) + (n == 0 ? 0 : random.nextInt(n));
	}

}