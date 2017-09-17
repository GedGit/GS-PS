package org.rs2server.rs2.content;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.minigame.impl.WarriorsGuild;
import org.rs2server.rs2.model.minigame.impl.fightcave.FightCave;
import org.rs2server.rs2.model.player.Player;

public class Jewellery {

	private static final Animation GEM_PRE_CAST_ANIMATION = Animation.create(714);
	private static final Graphic GEM_PRE_CAST_GRAPHICS = Graphic.create(308, 48, 100);
	private static final Animation TELEPORTING_ANIMATION = Animation.create(715);

	private static Object[][] GLORY_DATA = { { 1706, 1704, "You use your amulets last charge." },
			{ 1708, 1706, "Your amulet has one charge left." }, { 1710, 1708, "Your amulet has two charges left." },
			{ 1712, 1710, "Your amulet has three charges left." },
			{ 11976, 1712, "Your amulet has four charges left." },
			{ 11978, 11976, "Your amulet has five charges left." },
			{ 19707, 19707, "Your amulet is forever charged." }, };

	private static Object[][] SLAYER_RING_DATA = { { 11873, 4155, "Your slayer ring crumbles to dust." },
			{ 11872, 11873, "Your slayer ring has one charge left." },
			{ 11871, 11872, "Your slayer ring has two charges left." },
			{ 11870, 11871, "Your slayer ring has three charges left." },
			{ 11869, 11870, "Your slayer ring has four charges left." },
			{ 11868, 11869, "Your slayer ring has five charges left." },
			{ 11867, 11868, "Your slayer ring has six charges left." },
			{ 11866, 11867, "Your slayer ring has seven charges left." }, };

	private static Object[][] RING_OF_DUELING_DATA = { { 2566, -1, "Your ring of dueling crumbles to dust." },
			{ 2564, 2566, "Your ring of dueling has one charge left." },
			{ 2562, 2564, "Your ring of dueling has two charges left." },
			{ 2560, 2562, "Your ring of dueling has three charge left." },
			{ 2558, 2560, "Your ring of dueling has four charge left." },
			{ 2556, 2558, "Your ring of dueling has five charge left." },
			{ 2554, 2556, "Your ring of dueling has six charge left." },
			{ 2552, 2554, "Your ring of dueling has seven charge left." }, };

	private static Object[][] GAMES_NECKLACE_DATA = { { 3867, -1, "Your games necklace crumbles to dust." },
			{ 3865, 3867, "Your games necklace has one charge left." },
			{ 3863, 3865, "Your games necklace has two charges left." },
			{ 3861, 3863, "Your games necklace has three charges left." },
			{ 3859, 3861, "Your games necklace has four charges left." },
			{ 3857, 3859, "Your games necklace has five charges left." },
			{ 3855, 3857, "Your games necklace has six charges left." },
			{ 3853, 3855, "Your games necklace has seven charges left." }, };

	public enum GemType {
		GLORY, RING_OF_DUELING, SLAYER_RING, GAMES_NECKLACE
	}

	public static boolean rubItem(Player player, int slot, int itemId, boolean operating) {
		if (player.getRFD().isStarted() | FightCave.IN_CAVES.contains(player)
				|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControl")
				|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControlBoat")) {
			player.getActionSender().sendMessage("You can't teleport from here!");
			return false;
		}
		if (!operating || slot == Equipment.SLOT_AMULET) {
			if (itemId == 1704) {
				player.getActionSender().sendMessage("The amulet has lost its charge.");
				player.getActionSender().sendMessage("It will need to be recharged before you can use it again.");
				return true;
			}
			if (itemId > 1704 && itemId <= 1712) {
				itemId -= 1704;
				if (itemId % 2 == 0) { // Its an equal number..
					int divided = itemId / 2;
					DialogueManager.openDialogue(player, 1712);
					player.getJewellery().setGem(GemType.GLORY, divided, operating);
					return true;
				}
			} else if (itemId == 11976 || itemId == 11978) {
				itemId -= 11966;
				if (itemId % 2 == 0) { // Its an equal number..
					int divided = itemId / 2;
					DialogueManager.openDialogue(player, 1712);
					player.getJewellery().setGem(GemType.GLORY, divided, operating);
					return true;
				}
			} else if (itemId >= 3852 && itemId <= 3867) {
				itemId -= 3851;
				if (itemId % 2 == 0) { // Its an equal number..
					int divided = itemId / 2;
					DialogueManager.openDialogue(player, 1718);
					player.getJewellery().setGem(GemType.GAMES_NECKLACE, 9 - divided, operating);
					return true;
				}

			}
		}
		if (!operating || slot == Equipment.SLOT_RING) {
			if (itemId >= 2552 && itemId <= 2566) {
				itemId -= 2550;
				if (itemId % 2 == 0) { // Its an equal number..
					int divided = itemId / 2;
					DialogueManager.openDialogue(player, 1722);
					player.getJewellery().setGem(GemType.RING_OF_DUELING, 9 - divided, operating);
					return true;
				}
			} else if (itemId >= 11866 && itemId <= 11873) {
				itemId = 11874 - itemId;
				System.out.println(itemId);
				if (itemId >= 0) {
					DialogueManager.openDialogue(player, 1353);
					player.getJewellery().setGem(GemType.SLAYER_RING, itemId, operating);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Notice: This may be set, without being used at all. (Eg. if the player clicks
	 * rub, but then clickes the minimap). Sets how many charges the glory have
	 * left, so we can display the correct message.
	 *
	 * @param gemId
	 *            The id of the specific gem, Glory = 1, Ring of Dueling = 2, Games
	 *            Necklace = 3.
	 * @param charge
	 *            The charge our gem have left. :)
	 * @param isOperating
	 *            true if you're operating the gem, false if not.
	 */
	public void setGem(GemType gem, int charge, boolean isOperating) {
		this.gem = gem;
		this.gemCharge = charge;
		this.operate = isOperating;
	}

	/**
	 * Gets the players currently rubbed GemType.
	 *
	 * @return The players GemType.
	 */
	public GemType getGemType() {
		return gem;
	}

	/**
	 * Used for teleporting, while using a glory/ring of duelling/games necklage.
	 *
	 * @param player
	 *            The player, who's using a gem for teleporting.
	 * @param location
	 *            The location of where to go. (Edgewille, Castle wars, Duel arena).
	 */
	public void gemTeleport(final Player player, final Location location) {
		if (gem == null || gemCharge == -1 || player.getCombatState().isDead())
			return;
		if (player.getCombatState().isDead())
			return;
		if (player.getAttribute("busy") != null)
			return;
		if (player.hasAttribute("teleporting"))
			return;
		/*
		 * Prevents mass clicking them.
		 */
		if (player.getSettings().getLastTeleport() < 3000)
			return;
		if (player.getRFD().isStarted() | FightCave.IN_CAVES.contains(player)
				|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControl")
				|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControlBoat")) {
			player.getActionSender().sendMessage("You can't teleport from here!");
			return;
		}
		if (WarriorsGuild.IN_GAME.contains(player))
			WarriorsGuild.IN_GAME.remove(player);
		if (player.getDatabaseEntity().getPlayerSettings().isTeleBlocked()) {
			player.getActionSender().sendMessage("A magical force stops you from teleporting.");
			return;
		}
		player.getInstancedNPCs().clear();
		Container con = operate ? player.getEquipment() : player.getInventory();
		switch (gem) {
		/*
		 * Player is using a glory.
		 */
		case GLORY:
			if (Location.getWildernessLevel(player, player.getLocation()) > 30) {
				player.getActionSender().sendMessage("You cannot teleport above level 30 wilderness.");
				return;
			}
			Object[] data1 = GLORY_DATA[gemCharge - 1];
			if (!con.replace((Integer) data1[0], (Integer) data1[1])) {
				return;
			}
			player.getActionSender().sendMessage("<col=7f00ff>" + (String) data1[2]);
			break;
		/*
		 * Player is using a Ring of Dueling.
		 */
		case RING_OF_DUELING:
			if (Location.getWildernessLevel(player, player.getLocation()) > 30) {
				player.getActionSender().sendMessage("You cannot teleport above level 30 wilderness.");
				return;
			}
			Object[] data2 = RING_OF_DUELING_DATA[gemCharge - 1];
			if (!con.replace((Integer) data2[0], (Integer) data2[1])) {
				return;
			}
			player.getActionSender().sendMessage("<col=7f00ff>" + (String) data2[2]);
			break;
		/*
		 * Player is using a Games Necklace.
		 */
		case GAMES_NECKLACE:
			if (Location.getWildernessLevel(player, player.getLocation()) > 30) {
				player.getActionSender().sendMessage("You cannot teleport above level 30 wilderness.");
				return;
			}
			Object[] data3 = GAMES_NECKLACE_DATA[gemCharge - 1];
			if (!con.replace((Integer) data3[0], (Integer) data3[1])) {
				return;
			}
			player.getActionSender().sendMessage("<col=7f00ff>" + (String) data3[2]);
			break;
		/*
		 * Player is using a Slayer ring
		 */
		case SLAYER_RING:
			if (Location.getWildernessLevel(player, player.getLocation()) > 30) {
				player.getActionSender().sendMessage("You cannot teleport above level 30 wilderness.");
				return;
			}
			Object[] data4 = SLAYER_RING_DATA[gemCharge - 1];
			if (!con.replace((Integer) data4[0], (Integer) data4[1])) {
				return;
			}
			player.getActionSender().sendMessage("<col=7f00ff>" + (String) data4[2]);
			break;
		}
		player.setCanBeDamaged(false);
		player.resetBarrows();
		player.playAnimation(GEM_PRE_CAST_ANIMATION);
		player.playGraphics(GEM_PRE_CAST_GRAPHICS);
		World.getWorld().submit(new Event(1800) {
			public void execute() {
				player.setTeleportTarget(location);
				player.playAnimation(TELEPORTING_ANIMATION);
				player.setCanBeDamaged(true);
				if (player.getPet() != null) {
					player.getPet().setTeleportTarget(location);
					player.getPet().setInteractingEntity(InteractionMode.FOLLOW, player.getPet().getInstancedPlayer());
				}
				this.stop();
			}
		});
		player.getSettings().setLastTeleport(System.currentTimeMillis());
	}

	/**
	 * What is our gemtype?
	 */
	private GemType gem = null;
	/**
	 * What is the current gem charge?
	 */
	private int gemCharge = -1;
	/**
	 * Are we operating our glory/ring of dueling/games necklace?
	 */
	private boolean operate = false;

}
