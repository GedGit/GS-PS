package org.rs2server.rs2.content;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.Spell;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.SpellBook;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.minigame.impl.WarriorsGuild;
import org.rs2server.rs2.model.minigame.impl.fightcave.FightCave;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

/**
 * @author Tyluur <itstyluur@gmail.com>
 */
public class Teleporting {

	/**
	 * @param mob
	 *            The mob being teleported
	 * @param spell
	 *            The teleport spell
	 * @return True on success
	 */
	public static boolean teleport(final Mob mob, Spell spell) {
		if (mob.getAttribute("busy") != null || !mob.isPlayer())
			return false;
		if (mob.getCombatState().isDead())
			return false;
		if (mob.getAttribute("stunned") != null)
			return false;

		if (BoundaryManager.isWithinBoundaryNoZ(mob.getLocation(), "ClanWarsFFAFull")) {
			mob.getActionSender().sendMessage("You can't teleport from here, please use the portal to leave.");
			return false;
		}
		String teleport = spell.getSpellName();

		for (int j = 0; j < teleports.length; j++) {
			if (teleport.equalsIgnoreCase((String) teleports[j][4])) {
				Player player = (Player) mob;

				if (player.getDatabaseEntity().getPlayerSettings().isTeleBlocked() || player.getMonkeyTime() > 0) {
					player.getActionSender().sendMessage("A magical force stops you from teleporting.");
					return false;
				}
				if (player.getRFD().isStarted() || FightCave.IN_CAVES.contains(player)) {
					player.getActionSender().sendMessage("You can't teleport from here!");
					return false;
				}
				if (BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControl")
						|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControlBoat"))
					return false;
				if (WarriorsGuild.IN_GAME.contains(player))
					WarriorsGuild.IN_GAME.remove(player);
				if (mob.getSkills().getLevel(Skills.MAGIC) < (Integer) teleports[j][2]) {
					if (mob.getActionSender() != null) {
						mob.getActionSender().sendMessage(
								"You need a magic level of " + (Integer) teleports[j][2] + " to cast " + teleport);
						return false;
					}
				} else if (mob.isPlayer() && Location.getWildernessLevel((Player) mob, mob.getLocation()) > 20) {
					if (mob.getActionSender() != null) {
						mob.getActionSender().sendMessage("You cannot teleport above level 20 wilderness.");
						return false;
					}
				}
				if (spell.getRunes() != null) {
					for (int i = 0; i < spell.getRunes().length; i++) {
						if (mob.getInventory() != null && spell.getRune(i) != null
								&& Inventory.hasItem(mob, spell.getRune(i))) {
							if (MagicCombatAction.deleteRune(mob, spell.getRune(i))) {
								if (Inventory.hasStaff(mob, spell.getRune(i)))
									continue;
								if (mob.getInventory() != null)
									Inventory.removeRune(mob, spell.getRune(i));
							}
						} else {
							mob.getActionSender().sendMessage("Not enough runes to cast this spell.");
							return false;
						}
					}
				}
				if (teleport.equalsIgnoreCase("duelTeleport") || teleport.equalsIgnoreCase("castlewarsTeleport")
						|| teleport.equalsIgnoreCase("edgevilleTeleport")
						|| teleport.equalsIgnoreCase("karamjaTeleport") || teleport.equalsIgnoreCase("draynorTeleport")
						|| teleport.equalsIgnoreCase("alkharidTeleport") && mob.isPlayer()) {
					degradeItem((Player) mob);
				}
				mob.resetBarrows();
				mob.resetInteractingEntity();
				mob.getActionQueue().clearAllActions();
				player.getActionManager().stopAction();
				final int i = j;
				mob.getAttributes().put("busy", true);
				mob.getAttributes().put("teleporting", true);
				mob.resetInteractingEntity();
				mob.resetFace();
				if (player.hasAttribute("ownedNPC")) {// player.setAttribute("ownedNPC",
														// n);
					NPC n = (NPC) player.getAttribute("ownedNPC");
					if (n != null)
						World.getWorld().unregister(n);
					player.removeAttribute("ownedNPC");
				}
				if (mob.getCombatState().getSpellBook() == 0 && (SpellBook) teleports[i][0] == SpellBook.MODERN_MAGICS
						|| mob.getCombatState().getSpellBook() == 2
								&& (SpellBook) teleports[i][0] == SpellBook.LUNAR_MAGICS
						|| mob.getCombatState().getSpellBook() == 3
								&& (SpellBook) teleports[i][0] == SpellBook.ARCEUUS_MAGICS) {
					mob.setCanBeDamaged(false);
					mob.playAnimation(Animation.create(714));
					mob.playGraphics(Graphic.create(308, 48, 100));
					mob.getSkills().addExperience(Skills.MAGIC, (Integer) teleports[i][3]);
					World.getWorld().submit(new Tickable(4) {
						@Override
						public void execute() {
							mob.setCanBeDamaged(true);
							mob.setTeleportTarget((Location) teleports[i][1]);
							mob.playAnimation(Animation.create(-1));
							mob.playAnimation(Animation.create(715));
							mob.removeAttribute("busy");
							mob.removeAttribute("teleporting");
							this.stop();
						}
					});
					return true;

				} else if (mob.getCombatState().getSpellBook() == 1
						&& (SpellBook) teleports[i][0] == SpellBook.ANCIENT_MAGICKS) {
					mob.setCanBeDamaged(false);
					mob.playAnimation(Animation.create(1979));
					mob.playGraphics(Graphic.create(392, 48, 0));
					mob.getSkills().addExperience(Skills.MAGIC, (Integer) teleports[i][3]);
					World.getWorld().submit(new Tickable(5) {

						@Override
						public void execute() {
							mob.setCanBeDamaged(true);
							mob.setTeleportTarget((Location) teleports[i][1]);
							mob.playAnimation(Animation.create(-1));
							mob.removeAttribute("busy");
							mob.removeAttribute("teleporting");
							this.stop();
						}

					});
					return true;
				} else {
					mob.removeAttribute("busy");
					mob.removeAttribute("teleporting");
				}
			}
		}
		return false;
	}

	/**
	 * Will tele the player using a teletab.
	 *
	 * @param player
	 *            The player who is breaking the tablet
	 * @param tablet
	 *            The item which is being broken
	 * @return True on success.
	 */
	public static void breakTablet(final Player player, final Item tablet) {
		for (int j = 0; j < teleTabs.length; j++) {
			final int i = j;
			if (tablet.getId() == teleTabs[i][0]) {
				if (tablet == null || player.getAttribute("busy") != null)
					return;
				if (player.getAttribute("busy") != null)
					return;
				if (player.getCombatState().isDead())
					return;
				if (player.getAttribute("stunned") != null)
					return;

				if (BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "ClanWarsFFAFull")) {
					player.getActionSender()
							.sendMessage("You can't teleport from here, please use the portal to leave.");
					return;
				}
				if (player.getDatabaseEntity().getPlayerSettings().isTeleBlocked()) {
					player.getActionSender().sendMessage("A magical force stops you from teleporting.");
					return;
				}
				if (player.getRFD().isStarted() || FightCave.IN_CAVES.contains(player)) {
					player.getActionSender().sendMessage("You can't teleport from here!");
					return;
				}
				if (Location.getWildernessLevel(player, player.getLocation()) > 20 && !player.isAdministrator()) {
					if (player.getActionSender() != null)
						player.getActionSender().sendMessage("You cannot teleport above level 20 wilderness.");
					return;
				}
				if (BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControl")
						|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControlBoat")) {
					player.getActionSender().sendMessage("You cannot teleport from here!");
					return;
				}

				if (WarriorsGuild.IN_GAME.contains(player))
					WarriorsGuild.IN_GAME.remove(player);

				player.getAttributes().put("busy", true);
				player.getAttributes().put("teleporting", true);
				player.resetBarrows();
				player.resetInteractingEntity();
				player.getActionQueue().clearAllActions();
				player.getActionManager().stopAction();
				player.resetFace();
				if (player.hasAttribute("ownedNPC")) {// player.setAttribute("ownedNPC",
														// n);
					NPC n = (NPC) player.getAttribute("ownedNPC");
					if (n != null)
						World.getWorld().unregister(n);
					player.removeAttribute("ownedNPC");
				}
				player.playAnimation(Animation.create(4731));
				player.playGraphics(Graphic.create(678));
				Item toRemove = new Item(tablet.getId(), 1);
				player.getInventory().remove(toRemove);
				player.setCanBeDamaged(false);
				World.getWorld().submit(new Tickable(3) {
					@Override
					public void execute() {
						player.setCanBeDamaged(true);
						player.setTeleportTarget(Location.create(teleTabs[i][1], teleTabs[i][2], 0));
						player.playAnimation(Animation.create(-1));
						player.removeAttribute("busy");
						player.removeAttribute("teleporting");
						this.stop();
					}
				});
				return;
			}
		}
		return;
	}

	/**
	 * Opens a teleport menu for the item.
	 *
	 * @param player
	 *            The player who is being effected
	 * @param item
	 *            The item they are preparing to use.
	 * @param type
	 *            The type of item it is.
	 */
	public static void prepItem(Player player, Item item, int type) {
		if (player.getAttribute("busy") != null || item == null)
			return;
		if (player.getRFD().isStarted()) {
			player.getActionSender().sendMessage("You can't teleport from here!");
			return;
		}
		if (player.getCombatState().isDead())
			return;
		player.setTempItem(item);
		switch (type) {
		case 0:
			DialogueManager.openDialogue(player, 300);
			break;
		case 1:
			if (player.getActionSender() != null && item.getId() == 1704) {
				player.getActionSender().sendMessage("This amulet has no charges left.");
				return;
			}
			DialogueManager.openDialogue(player, 303);
			break;
		}
	}

	/**
	 * Degrades rings, necklaces, amulets relating to teleporting.
	 *
	 * @param player
	 *            The player whos item is being degraded
	 */
	public static void degradeItem(Player player) {
		Item item = player.getTempItem();
		if (item == null)
			return;
		int itemMod = 0;
		if (isDuelingRing(item.getId()))
			itemMod = 2;
		else if (isGlory(item.getId()))
			itemMod = -2;
		switch (item.getId()) {
		case 2566:
			player.getInventory().remove(item);
			if (player.getActionSender() != null) {
				player.getActionSender().sendMessage("Your ring crumbles to dust.");
				return;
			}
			break;
		default:
			int id = item.getId();
			player.getInventory().remove(item);
			player.getInventory().add(new Item(id + itemMod));
			break;
		}
	}

	/**
	 * @param id
	 *            The id we are testing
	 * @return True If id was a dueling ring
	 */
	public static boolean isDuelingRing(int id) {
		for (int i = 0; i < 2566; i += 2) {
			if (id == 2552 + i) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param id
	 *            The id we are testing
	 * @return True if the id is a glory amulet id.
	 */
	public static boolean isGlory(int id) {
		for (int i = 0; i < 1712; i += 2) {
			if (id == 1704 + i) {
				return true;
			}
		}
		return false;
	}

	/*
	 * games neclaces case 3853: case 3855: case 3857: case 3859: case 3861: case
	 * 3863: case 3865: case 3867:
	 */
	private final static Object[][] teleports = {

			{ SpellBook.MODERN_MAGICS, Location.create(3213, 3424, 0), 25, 35, "varrockTeleport" },
			{ SpellBook.MODERN_MAGICS, Location.create(3225, 3218, 0), 31, 41, "lumbridgeTeleport" },
			{ SpellBook.MODERN_MAGICS, Location.create(2964, 3378, 0), 37, 48, "faladorTeleport" },
			{ SpellBook.MODERN_MAGICS, Location.create(2757, 3477, 0), 45, (int) 55.5, "camelotTeleport" },
			{ SpellBook.MODERN_MAGICS, Location.create(2662, 3305, 0), 51, 61, "ardougneTeleport" },
			{ SpellBook.MODERN_MAGICS, Location.create(2549, 3113, 0), 58, 64, "watchtowerTeleport" },
			{ SpellBook.MODERN_MAGICS, Location.create(2891, 3680, 0), 61, 68, "trollheimTeleport" },
			{ SpellBook.ANCIENT_MAGICKS, Location.create(3097, 9882, 0), 54, 64, "paddewwaTeleport" },
			{ SpellBook.ANCIENT_MAGICKS, Location.create(3322, 3336, 0), 60, 70, "senntistenTeleport" },
			{ SpellBook.ANCIENT_MAGICKS, Location.create(3493, 3477, 0), 66, 76, "kharyrllTeleport" },
			{ SpellBook.ANCIENT_MAGICKS, Location.create(3006, 3471, 0), 72, 82, "lassarTeleport" },
			{ SpellBook.ANCIENT_MAGICKS, Location.create(2978, 3698, 0), 78, 88, "dareeyakTeleport" },
			{ SpellBook.ANCIENT_MAGICKS, Location.create(3156, 3666, 0), 84, 94, "carrallangarTeleport" },
			{ SpellBook.ANCIENT_MAGICKS, Location.create(3288, 3886, 0), 90, 100, "annakarlTeleport" },
			{ SpellBook.ANCIENT_MAGICKS, Location.create(2977, 3873, 0), 96, 106, "ghorrockTeleport" },
			{ SpellBook.MODERN_MAGICS, Location.create(3317, 3235, 0), 0, 0, "duelTeleport" },
			{ SpellBook.ANCIENT_MAGICKS, Location.create(3317, 3235, 0), 0, 0, "duelTeleport" },
			{ SpellBook.LUNAR_MAGICS, Location.create(3317, 3235, 0), 0, 0, "duelTeleport" },
			{ SpellBook.MODERN_MAGICS, Location.create(2442, 3089, 0), 0, 0, "castlewarsTeleport" },
			{ SpellBook.ANCIENT_MAGICKS, Location.create(2442, 3089, 0), 0, 0, "castlewarsTeleport" },
			{ SpellBook.LUNAR_MAGICS, Location.create(2442, 3089, 0), 0, 0, "castlewarsTeleport" },
			{ SpellBook.MODERN_MAGICS, Location.create(3088, 3502, 0), 0, 0, "edgevilleTeleport" },
			{ SpellBook.ANCIENT_MAGICKS, Location.create(3088, 3502, 0), 0, 0, "edgevilleTeleport" },
			{ SpellBook.LUNAR_MAGICS, Location.create(3088, 3502, 0), 0, 0, "edgevilleTeleport" },
			{ SpellBook.MODERN_MAGICS, Location.create(2918, 3176, 0), 0, 0, "karamjaTeleport" },
			{ SpellBook.ANCIENT_MAGICKS, Location.create(2918, 3176, 0), 0, 0, "karamjaTeleport" },
			{ SpellBook.LUNAR_MAGICS, Location.create(2918, 3176, 0), 0, 0, "karamjaTeleport" },
			{ SpellBook.MODERN_MAGICS, Location.create(3104, 3249, 0), 0, 0, "draynorTeleport" },
			{ SpellBook.ANCIENT_MAGICKS, Location.create(3104, 3249, 0), 0, 0, "draynorTeleport" },
			{ SpellBook.LUNAR_MAGICS, Location.create(3104, 3249, 0), 0, 0, "draynorTeleport" },
			{ SpellBook.MODERN_MAGICS, Location.create(3293, 3177, 0), 0, 0, "alkharidTeleport" },
			{ SpellBook.ANCIENT_MAGICKS, Location.create(3293, 3177, 0), 0, 0, "alkharidTeleport" },
			{ SpellBook.LUNAR_MAGICS, Location.create(3293, 3177, 0), 0, 0, "alkharidTeleport" },
			{ SpellBook.ARCEUUS_MAGICS, Location.create(3565, 3314, 0), 83, 90, "barrowsTeleport" },
			{ SpellBook.ARCEUUS_MAGICS, Location.create(3088, 3502, 0), 83, 0, "respawnTeleport" },
			{ SpellBook.LUNAR_MAGICS, Location.create(2105, 3911, 0), 69, 66, "moonclanTeleport" },
			{ SpellBook.LUNAR_MAGICS, Location.create(2524, 3755, 0), 72, 71, "waterbirthTeleport" },
			{ SpellBook.LUNAR_MAGICS, Location.create(2543, 3577, 0), 75, 76, "barbarianTeleport" },
			{ SpellBook.LUNAR_MAGICS, Location.create(2662, 3161, 0), 78, 80, "khazardTeleport" },
			{ SpellBook.LUNAR_MAGICS, Location.create(2611, 3389, 0), 85, 89, "fishingGuildTeleport" },
			{ SpellBook.LUNAR_MAGICS, Location.create(2815, 3447, 0), 87, 92, "catherbyTeleport" },
			{ SpellBook.LUNAR_MAGICS, Location.create(2959, 3944, 0), 89, 96, "icePlateauTeleport" } };

	public final static int[][] teleTabs = { { 8007, 3213, 3424 }, { 8008, 3222, 3218 }, { 8009, 2965, 3379 },
			{ 8010, 2757, 3477 }, { 8011, 2661, 3305 }, { 8012, 2549, 3112 }, { 8013, 3088, 3489 }, // house
			{ 12781, 3097, 9882 }, { 12782, 3322, 3336 }, { 12775, 3288, 3886 }, { 12776, 3156, 3666 },
			{ 12777, 2978, 3698 }, { 12778, 2977, 3873 }, { 12779, 3493, 3477 }, { 12780, 3006, 3471 },
			{ 12405, 2096, 3915 }, { 19629, 3565, 3316 }, { 13249, 1240, 1226 }, { 12938, 2199, 3056 },
			{ 11745, 2744, 3148 }, { 11742, 2932, 3451 }, // tav
			{ 11744, 2658, 3658 }, // relekk
			{ 11746, 2605, 3093 }, // yanille
			{ 11741, 2956, 3216 }, // rimmington
			{ 11743, 3359, 2969 } // pollnivneach
	};
}
