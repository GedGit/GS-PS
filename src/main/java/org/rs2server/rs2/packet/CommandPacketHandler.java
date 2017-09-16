package org.rs2server.rs2.packet;

import org.apache.commons.lang3.StringUtils;
import org.rs2server.Server;
import org.rs2server.cache.Cache;
import org.rs2server.cache.format.*;
import org.rs2server.rs2.*;
import org.rs2server.rs2.content.TimedPunishment;
import org.rs2server.rs2.domain.dao.api.PlayerEntityDao;
import org.rs2server.rs2.domain.model.player.*;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.*;
import org.rs2server.rs2.domain.service.api.*;
import org.rs2server.rs2.domain.service.api.PermissionService.PlayerPermissions;
import org.rs2server.rs2.domain.service.api.content.*;
import org.rs2server.rs2.domain.service.api.content.cerberus.CerberusService;
import org.rs2server.rs2.domain.service.api.loot.*;
import org.rs2server.rs2.domain.service.impl.*;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.bit.*;
import org.rs2server.rs2.model.cm.Content;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.container.*;
import org.rs2server.rs2.model.map.*;
import org.rs2server.rs2.model.map.path.ClippingFlag;
import org.rs2server.rs2.model.map.path.astar.ObjectReachedPrecondition;
import org.rs2server.rs2.model.npc.*;
import org.rs2server.rs2.model.npc.impl.other.KalphiteQueen;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.quests.impl.*;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.model.skills.slayer.SlayerTask;
import org.rs2server.rs2.model.skills.slayer.SlayerTask.Master;
import org.rs2server.rs2.net.*;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.tickable.impl.SystemUpdateTick;
import org.rs2server.rs2.util.*;
import org.rs2server.rs2.varp.PlayerVariable;
import org.rs2server.tools.ReadCharacterFiles;
import org.rs2server.util.XMLController;

import plugin.discord.utils.Message;

import java.io.*;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

public class CommandPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) throws IOException {
		String commandString = packet.getRS2String();

		if (player.getAttribute("cutScene") != null)
			return;

		commandString = commandString.replaceAll(":", "");
		String[] args = commandString.split(" ");
		String command = args[0].toLowerCase();

		if (command.equals("home")) {
			player.teleport(Constants.HOME_TELEPORT, 4, 4, false);
			return;
		}

		if (command.equals("time")) {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM,
					new Locale("en", "EN"));
			String formattedDate = df.format(new Date());
			player.getActionSender().sendItemDialogue(2575, Constants.SERVER_NAME + "'s time is now: " + formattedDate);
			return;
		}

		if (command.startsWith("claim") || command.contains("reward")) {
			player.sendMessage("Donations: rewards are handled automatically; please wait a moment!");
			player.sendMessage("Votes: speak to Salve-PS Advisor at home to claim your rewards!");
		}
		if (command.equals("empty")) {
			player.getInventory().clear();
			return;
		}
		if (command.equals("train")) {
			DialogueManager.openDialogue(player, 100042);
			return;
		}
		if (command.equals("commands")) {
			final List<String> list = new ArrayList<>();
			list.add("--------------------------------------------");
			list.add("::home - Teleports you home");
			list.add("::train - Teleports you to low-level training");
			list.add("::empty - <col=ff0000>Deletes your inventory</col>");
			list.add("::claim - Claims your donations");
			list.add("::reward (1-3) - Claims your vote reward");
			list.add("::players - Shows players online");
			list.add("::toggle (skill_name) - lock/unlock experience in that skill");
			list.add("::yell (message) - Sends a world message");
			list.add("::openbank - Opens your bank account");
			list.add("::dz - Teleports you to Donator zone");
			player.getActionSender().sendTextListInterface(
					"<col=ff0000><shad=000000>" + Constants.SERVER_NAME + " Commands List",
					list.toArray(new String[list.size()]));
		}
		if (command.equals("website")) {
			player.sendMessage("Please type this URL into your browser: '" + Constants.WEBSITE_URLS[0] + "'.");
			return;
		}
		if (command.startsWith("forum")) {
			player.sendMessage("Please type this URL into your browser: '" + Constants.WEBSITE_URLS[1] + "'.");
			return;
		}
		if (command.equals("store") || command.equals("donate")) {
			player.sendMessage("Please type this URL into your browser: '" + Constants.WEBSITE_URLS[2] + "'.");
			player.sendMessage("After you've donated, type ::claim in order to claim your donations.");
			return;
		}
		if (command.equals("vote")) {
			player.sendMessage("Please type this URL into your browser: '" + Constants.WEBSITE_URLS[3] + "'.");
			player.sendMessage("After you've voted speak to Salve-PS Advisor at home to claim!");
			return;
		}
		if (command.equals("hiscores")) {
			player.sendMessage("Please type this URL into your browser: '" + Constants.WEBSITE_URLS[4] + "'.");
			return;
		}

		if (command.equals("changepass")) {
			if (!player.isEnteredPinOnce() && player.getDatabaseEntity().getPlayerSettings().isBankSecured()) {
				player.getActionSender()
						.sendMessage("Please go to a bank and enter your pin before changing your password.");
				return;
			}
			player.getActionSender().sendMessage("To change your password please visit our community website.");
			return;
		}

		if (command.equals("admin")) {
			if (player.getName().equalsIgnoreCase("salve") || Constants.DEBUG) {
				permissionService.give(player, PlayerPermissions.ADMINISTRATOR);
				player.sendMessage("Success; you now have administrator permissions!");
				return;
			}
		}

		if (command.equals("toggle")) {
			String name = TextUtils.upperFirst(args[1]);
			Optional<Integer> lock = Misc.forSkillName(name);
			if (lock.isPresent()) {
				int skillId = lock.get();
				List<Integer> locked = player.getDatabaseEntity().getPlayerSettings().getLockedSkills();
				if (locked.contains(skillId)) {
					locked.remove(locked.indexOf(skillId));
					player.getActionSender().sendMessage("You have unlocked your experience for " + name + ".");
					return;
				}
				locked.add(skillId);
				player.getActionSender().sendMessage("You have locked your experience for " + name + ".");
			}
			return;
		}
		if (commandString.equalsIgnoreCase("players")) {
			player.getActionSender()
					.sendMessage("There are currently " + World.getWorld().getPlayers().size() + " players online.");
			return;
		}

		if (command.equals("placeholders")) {
			if (args.length > 2 || !args[1].equalsIgnoreCase("false") && !args[1].equalsIgnoreCase("true"))
				return;
			boolean enabled = Boolean.parseBoolean(args[1]);
			player.getDatabaseEntity().getPlayerSettings().setPlaceHolderEnabled(enabled);
			player.getActionSender().sendMessage("Placeholders; " + (enabled ? " Enabled " : "Disabled"));
			if (!enabled) {
				player.getBank().stream().filter(Objects::nonNull).filter(i -> i.getCount() == 0).forEach(i -> {
					int slot = player.getBank().getSlotById(i.getId());
					int tabId = player.getBanking().getTabByItemSlot(slot);
					player.getBank().set(slot, null);
					player.getBanking().decreaseTabStartSlots(tabId);
				});
				player.getBank().shift();
			}
			return;
		}

		if (command.startsWith("yell")) {
			if (player.getSettings().isMuted()) {
				player.getActionSender().sendMessage("You are muted and cannot speak.");
				return;
			}
			int icon = Misc.getModIconForPerm(permissionService.getHighestPermission(player));
			String msg = commandString.substring(5);
			if (msg.length() > 80) // Limit maximum characters
				msg = msg.substring(0, 80);

			// Non-administrators shouldn't be able to insert this crap into messages
			if (!player.isAdministrator()) {
				String[] invalid = { "<euro", "<img", "<img=", "<col", "<col=", "<shad", "<shad=", "<str>", "<u>" };
				for (String s : invalid) {
					if (msg.contains(s)) {
						player.sendMessage("You are not allowed to add additional code to the message.");
						return;
					}
				}
				if ((msg.toLowerCase().contains("www.") && !msg.toLowerCase().contains(Constants.SERVER_NAME))
						|| msg.toLowerCase().contains(".org")
						|| (msg.toLowerCase().contains("http:") && !msg.toLowerCase().contains(Constants.SERVER_NAME))
						|| (msg.toLowerCase().contains(".com") && !msg.toLowerCase().contains(Constants.SERVER_NAME))
						|| msg.toLowerCase().contains(".net") || msg.toLowerCase().contains(".tv")
						|| msg.toLowerCase().contains(".us") || msg.toLowerCase().contains(".io")) {
					player.sendMessage("You are not allowed to advertise/insert URL's into the yell channel.");
					return;
				}
			}

			String formattedMsg = null;

			// TODO colors for each rank
			if (permissionService.is(player, PermissionService.PlayerPermissions.DEV))
				formattedMsg = (icon != -1 ? ("[<col=FF0000>Developer</col>]") : "<col=FF0000>") + "<col=FF0000><img="
						+ icon + ">" + player.getName() + "<col=FF0000>: <col=FF0000>" + msg;
			else if (permissionService.is(player, PermissionService.PlayerPermissions.ADMINISTRATOR))
				formattedMsg = (icon != -1 ? ("[<col=FF0000>Administrator</col>]") : "<col=FF0000>")
						+ "<col=FF0000><img=" + icon + ">" + player.getName() + "<col=FF0000>: <col=FF0000>" + msg;
			else if (permissionService.is(player, PermissionService.PlayerPermissions.MODERATOR))
				formattedMsg = (icon != -1 ? ("[<col=FF0000>Moderator</col>]") : "<col=FF0000>") + "<col=FF0000><img="
						+ icon + ">" + player.getName() + "<col=FF0000>: <col=FF0000>" + msg;
			else if (permissionService.is(player, PermissionService.PlayerPermissions.HELPER))
				formattedMsg = (icon != -1 ? ("[<col=FF0000>Helper</col>]") : "<col=FF0000>") + "<col=FF0000><img="
						+ icon + ">" + player.getName() + "<col=FF0000>: <col=FF0000>" + msg;
			else if (permissionService.is(player, PermissionService.PlayerPermissions.YOUTUBER))
				formattedMsg = (icon != -1 ? ("[<col=FF0000>YouTuber</col>]") : "<col=FF0000>") + "<col=FF0000><img="
						+ icon + ">" + player.getName() + "<col=FF0000>: <col=FF0000>" + msg;
			else if (permissionService.is(player, PermissionService.PlayerPermissions.COM))
				formattedMsg = (icon != -1 ? ("[<col=FF0000>Com. Manager</col>]") : "<col=FF0000>")
						+ "<col=FF0000><img=" + icon + ">" + player.getName() + "<col=FF0000>: <col=FF0000>" + msg;
			else if (permissionService.is(player, PermissionService.PlayerPermissions.PVP))
				formattedMsg = (icon != -1 ? ("[<col=FF0000>PvP Legend</col>]") : "<col=FF0000>") + "<col=FF0000><img="
						+ icon + ">" + player.getName() + "<col=FF0000>: <col=FF0000>" + msg;
			else if (permissionService.is(player, PermissionService.PlayerPermissions.YOUTUBER))
				formattedMsg = (icon != -1 ? ("[<col=FF0000>YouTuber</col>]") : "<col=FF0000>") + "<col=FF0000><img="
						+ icon + ">" + player.getName() + "<col=FF0000>: <col=FF0000>" + msg;
			else if (permissionService.is(player, PermissionService.PlayerPermissions.DIAMOND_MEMBER))
				formattedMsg = (icon != -1 ? ("[<col=FF0000>Diamond Member</col>]") : "<col=FF0000>")
						+ "<col=FF0000><img=" + icon + ">" + player.getName() + "<col=FF0000>: <col=FF0000>" + msg;
			else if (permissionService.is(player, PermissionService.PlayerPermissions.PLATINUM_MEMBER))
				formattedMsg = (icon != -1 ? ("[<col=FF0000>Platinum Member</col>]") : "<col=FF0000>")
						+ "<col=FF0000><img=" + icon + ">" + player.getName() + "<col=FF0000>: <col=FF0000>" + msg;
			else if (permissionService.is(player, PermissionService.PlayerPermissions.GOLD_MEMBER))
				formattedMsg = (icon != -1 ? ("[<col=FF0000>Gold Member</col>]") : "<col=FF0000>") + "<col=FF0000><img="
						+ icon + ">" + player.getName() + "<col=FF0000>: <col=FF0000>" + msg;
			else if (permissionService.is(player, PermissionService.PlayerPermissions.SILVER_MEMBER))
				formattedMsg = (icon != -1 ? ("[<col=FF0000>Silver Member</col>]") : "<col=FF0000>")
						+ "<col=FF0000><img=" + icon + ">" + player.getName() + "<col=FF0000>: <col=FF0000>" + msg;
			else if (permissionService.is(player, PermissionService.PlayerPermissions.BRONZE_MEMBER))
				formattedMsg = (icon != -1 ? ("[<col=FF0000>Bronze Member</col>]") : "<col=FF0000>")
						+ "<col=FF0000><img=" + icon + ">" + player.getName() + "<col=FF0000>: <col=FF0000>" + msg;
			else if (player.getName().equalsIgnoreCase("salve"))
				formattedMsg = (icon != -1 ? ("[<col=FF0000>Owner</col>]") : "<col=FF0000>") + "<col=FF0000><img="
						+ icon + ">" + player.getName() + "<col=FF0000>: <col=FF0000>" + msg;
			else {
				player.sendMessage("You do not have access to this command.");
				return;
			}

			if (formattedMsg != null) {
				World.getWorld().sendWorldMessage(formattedMsg);
				return;
			}
		}

		/**
		 * Bronze Member commands
		 */
		if (permissionService.is(player, PermissionService.PlayerPermissions.BRONZE_MEMBER)) {
			if (command.startsWith("dz")) {
				player.teleport(Constants.DONATOR_ZONE, 5, 5, false);
				return;
			}
		}

		/**
		 * Silver Member commands
		 */
		if (permissionService.is(player, PermissionService.PlayerPermissions.SILVER_MEMBER)) {
			// TODO
		}

		/**
		 * Gold Member commands
		 */
		if (permissionService.is(player, PermissionService.PlayerPermissions.GOLD_MEMBER)) {
			if (command.equals("openbank") || command.equals("o") || command.equals("b")) {
				if (player.getCombatState().getLastHitTimer() > System.currentTimeMillis()) {
					player.sendMessage("You cannot open the bank until 10 seconds after the end of combat.");
					return;
				}
				if (player.getBountyHunter() != null && player.getBountyHunter().getLeavePenalty() > 0) {
					player.sendMessage("You cannot bank while on penalty.");
					return;
				}
				if (player.getAttribute("busy") != null) {
					player.sendMessage("You cannot bank while you're in the middle of something.");
					return;
				}
				if (player.isInWilderness()) {
					if (Location.getWildernessLevel(player, player.getLocation()) > 5) {
						player.sendMessage("You cannot bank in above level 5 wilderness.");
						return;
					}
				}
				Bank.open(player);
				return;
			}
		}

		/**
		 * Platinum Member commands
		 */
		if (permissionService.is(player, PermissionService.PlayerPermissions.PLATINUM_MEMBER)) {
			// TODO
		}

		/**
		 * Diamond Member commands
		 */
		if (permissionService.is(player, PermissionService.PlayerPermissions.DIAMOND_MEMBER)) {
			// TODO
		}

		/**
		 * Staff - Server Support - commands
		 */
		if (permissionService.is(player, PermissionService.PlayerPermissions.HELPER)) {
			handleHelperCommands(player, args);
			return;
		}

		/**
		 * Staff - Moderator - commands
		 */
		if (permissionService.is(player, PermissionService.PlayerPermissions.MODERATOR)) {
			handleModeratorCommands(player, commandString, args);
			return;
		}

		/**
		 * Staff - Administrator+ commands
		 */
		if (player.isAdministrator()) {
			handleModeratorCommands(player, commandString, args);
			handleAdminCommands(player, commandString, args);
			return;
		}
	}

	public void handleHelperCommands(Player player, String[] args) {
		String command = args[0].toLowerCase();

		if (command.equals("kick")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			target.getActionSender().sendLogout();
			World.getWorld().unregister(target);
			player.getActionSender().sendMessage("Successfully kicked " + target.getName() + ".");
			return;
		}

		if (command.equals("mute")) {
			if (args.length > 2 || args.length < 2) {
				player.sendMessage("You have to do ::mute player_name");
				return;
			}
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			TimedPunishment.handleMute(player, target, 0, 1, 0);
		}
	}

	public void handleModeratorCommands(Player player, String playerCommand, String[] args) {
		try {
			String command = args[0].toLowerCase();
			if (command.equals("checkbank")) {
				final String playerName = NameUtils.formatName(args[1]);
				final Player target = playerService.getPlayer(playerName);
				if (target == null) {
					player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
					return;
				}
				player.getBanking().openPlayerBank(target);
				return;
			}
			if (command.equals("checkinv")) {
				final String playerName = NameUtils.formatName(args[1]);
				final Player target = playerService.getPlayer(playerName);
				if (target == null) {
					player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
					return;
				}
				player.getActionSender().sendMessage("--Start of " + target.getName() + "'s Inventory--");
				for (Item item : target.getInventory().toArray()) {
					if (item != null)
						player.getActionSender().sendMessage(item.getCount() + "x " + item.getDefinition2().getName());
				}
				player.getActionSender().sendMessage("--End of " + target.getName() + "'s Inventory--");
				return;
			}
			if (command.equals("mute")) {
				if (args.length < 5 || args.length > 5) {
					player.sendMessage("You have to do ::mute player_name days hours minutes");
					return;
				}
				final String playerName = NameUtils.formatName(args[1]);
				final Player target = playerService.getPlayer(playerName);
				if (target == null) {
					player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
					return;
				}
				int days = Integer.parseInt(args[2]), hours = Integer.parseInt(args[3]),
						minutes = Integer.parseInt(args[4]);
				TimedPunishment.handleMute(player, target, days, hours, minutes);

				return;
			}
			if (command.equals("ban")) {
				if (args.length < 5 || args.length > 5) {
					player.sendMessage("You have to do ::ban player_name days hours minutes");
					return;
				}
				final String playerName = NameUtils.formatName(args[1]);
				final Player target = playerService.getPlayer(playerName);
				if (target == null) {
					player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
					return;
				}
				int days = Integer.parseInt(args[2]), hours = Integer.parseInt(args[3]),
						minutes = Integer.parseInt(args[4]);
				TimedPunishment.handleBan(player, target, days, hours, minutes);
				return;
			}

			if (command.equals("ipmute")) {
				if (args.length < 1) {
					player.sendMessage("You have to do ::ipmute player_name");
					return;
				}
				final String playerName = NameUtils.formatName(args[1]);
				final Player target = playerService.getPlayer(playerName);
				if (target == null) {
					player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
					return;
				}
				File file = new File("data/punishments/ipMutedUsers.xml");
				List<String> mutedUsers = XMLController.readXML(file);
				mutedUsers.add(playerName);
				XMLController.writeXML(mutedUsers, file);
				return;
			}
			if (command.equals("checkonline")) {
				final String search = StringUtils.join(args, " ", 1, args.length).toLowerCase();
				Player target = playerService.getPlayer(search);
				if (target != null) {
					List<String> names = new ArrayList<>();
					World.getWorld().getPlayers().stream().filter(Objects::nonNull)
							.filter(p -> p.getIP().equals(target.getIP())).forEach(p -> names.add(p.getName()));
					player.getActionSender()
							.sendMessage(target.getIP() + " accounts: [" + Arrays.toString(names.toArray()) + " ]");
				}
				return;
			}
			if (command.equals("teleto")) {
				final String playerName = NameUtils.formatName(args[1]);
				final Player target = playerService.getPlayer(playerName);
				if (target == null) {
					player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
					return;
				}
				Location uncreate = target.getLocation();
				player.setTeleportTargetObj(uncreate);
				return;
			}
			if (command.equals("teletome")) {
				final String playerName = NameUtils.formatName(args[1]);
				final Player target = playerService.getPlayer(playerName);
				if (target == null) {
					player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
					return;
				}
				Location create = player.getLocation();
				target.teleport(create, 0, 0, false);
				return;
			}
			if (command.equals("kick")) {
				final String playerName = NameUtils.formatName(args[1]);
				final Player target = playerService.getPlayer(playerName);
				if (target == null) {
					player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
					return;
				}
				if (target.getCombatState().getLastHitTimer() > System.currentTimeMillis()) {
					player.getActionSender()
							.sendMessage("Please wait for that player to leave combat before kicking them.");
				} else {
					target.getActionSender().sendLogout();
					World.getWorld().unregister(target);
					player.getActionSender().sendMessage("Successfully kicked " + target.getName() + ".");
				}
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleAdminCommands(Player player, String commandString, String[] args) throws IOException {
		String command = args[0].toLowerCase();

		if (command.equals("song")) {
			int songId = Integer.parseInt(args[1]);
			if (songId > Cache.getAmountOfSongTracks()) {
				player.sendMessage("Song tracks only go from 0 to " + Cache.getAmountOfSongTracks() + ".");
				return;
			}
			musicService.play(player, Song.of(Integer.parseInt(args[1])));
			return;
		}
		if (command.equals("max")) {
			player.getSkills().addExperience(Skills.STRENGTH, 200000000);
			return;
		}
		if (command.equals("o")) {
			Bank.open(player);
			return;
		}

		if (command.equals("emptybank")) {
			player.getBank().clear();
			return;
		}

		if (command.equals("img")) {
			for (int i = 0; i <= 53; i++)
				player.getActionSender().sendMessage("Image ID: [" + i + "] - <img=" + i + ">");
			return;
		}

		if (command.equals("addspawn")) {
			int npcID = Integer.parseInt(args[1]);
			try {
				NPCSpawnLoader.addSpawn("Salve", npcID,
						Location.create(player.getX(), player.getY(), player.getPlane()));
				player.sendMessage("Added NPC spawn: " + CacheNPCDefinition.get(npcID).getName() + " [ID: " + npcID
						+ "], tile: " + player.getX() + ", " + player.getY() + ", " + player.getPlane() + ".");
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
		}

		if (command.equals("openbank")) {
			ScriptManager.getScriptManager().invoke("openbankcmd", player);
			return;
		}
		if (command.equals("npcemote")) {
			Mob mob = World.getWorld().getNPCs().get(Integer.parseInt(args[1]));
			int anim = Integer.parseInt(args[2]);
			if (mob.isNPC()) {
				NPC n = (NPC) mob;
				n.playAnimation(Animation.create(anim));
			}
			return;
		}
		if (command.equals("c")) {
			player.getActionSender().sendConfig(1055, Integer.parseInt(args[1]));
		}

		if (command.startsWith("spec")) {
			player.getCombatState().setSpecialEnergy(9500);
			player.getActionSender().sendConfig(300, 1000);
			return;
		}
		if (command.equals("god")) {
			player.getSkills().setLevel(Skills.ATTACK, Integer.MAX_VALUE);
			player.getSkills().setLevel(Skills.STRENGTH, Integer.MAX_VALUE);
			player.getSkills().setLevel(Skills.DEFENCE, Integer.MAX_VALUE);
			player.getSkills().setLevel(Skills.RANGE, Integer.MAX_VALUE);
			player.getSkills().setLevel(Skills.MAGIC, Integer.MAX_VALUE);
			player.getSkills().setLevel(Skills.PRAYER, Integer.MAX_VALUE);
			player.getSkills().setLevel(3, Integer.MAX_VALUE);
			return;
		}

		if (command.equals("resettask")) {
			player.getSlayer().setSlayerTask(null);
			player.sendMessage("You've resetted your slayer task.");
			return;
		}

		if (command.startsWith("makediamond")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			permissionService.give(target, PermissionService.PlayerPermissions.DIAMOND_MEMBER);
			target.sendMessage("You've been given the Diamond Member status.");
			player.sendMessage("You've given " + target.getName() + " the Diamond Member status.");
			return;
		}

		if (command.startsWith("makeplatinum")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			permissionService.give(target, PermissionService.PlayerPermissions.PLATINUM_MEMBER);
			target.sendMessage("You've been given the Platinum Member status.");
			player.sendMessage("You've given " + target.getName() + " the Platinum Member status.");
			return;
		}

		if (command.startsWith("makegold")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			permissionService.give(target, PermissionService.PlayerPermissions.GOLD_MEMBER);
			target.sendMessage("You've been given the Gold Member status.");
			player.sendMessage("You've given " + target.getName() + " the Gold Member status.");
			return;
		}

		if (command.startsWith("makesilver")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			permissionService.give(target, PermissionService.PlayerPermissions.SILVER_MEMBER);
			target.sendMessage("You've been given the Silver Member status.");
			player.sendMessage("You've given " + target.getName() + " the Silver Member status.");
			return;
		}

		if (command.startsWith("makeironman")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			permissionService.remove(target, PermissionService.PlayerPermissions.HARDCORE_IRON_MAN);
			permissionService.remove(target, PermissionService.PlayerPermissions.ULTIMATE_IRON_MAN);
			permissionService.give(target, PermissionService.PlayerPermissions.IRON_MAN);
			target.sendMessage("You've been given Regular Ironman status.");
			player.sendMessage("You've given " + target.getName() + " the Regular Ironman status.");
			return;
		}

		if (command.startsWith("makeuim")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			permissionService.remove(target, PermissionService.PlayerPermissions.HARDCORE_IRON_MAN);
			permissionService.remove(target, PermissionService.PlayerPermissions.IRON_MAN);
			permissionService.give(target, PermissionService.PlayerPermissions.ULTIMATE_IRON_MAN);
			target.sendMessage("You've been given the Ultimate Ironman status.");
			player.sendMessage("You've given " + target.getName() + " the Ultimate Ironman status.");
			return;
		}

		if (command.startsWith("makehcm")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			permissionService.remove(target, PermissionService.PlayerPermissions.ULTIMATE_IRON_MAN);
			permissionService.remove(target, PermissionService.PlayerPermissions.IRON_MAN);
			permissionService.give(target, PermissionService.PlayerPermissions.HARDCORE_IRON_MAN);
			target.sendMessage("You've been given the Hardcore Ironman status.");
			player.sendMessage("You've given " + target.getName() + " the Hardcore Ironman status.");
			return;
		}

		if (command.startsWith("makehelper")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			permissionService.give(target, PermissionService.PlayerPermissions.HELPER);
			target.sendMessage("You've been given the Support Team (Helper) status.");
			player.sendMessage("You've given " + target.getName() + " the Support Team (Helper) status.");
			return;
		}

		if (command.startsWith("makemod")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			permissionService.give(target, PermissionService.PlayerPermissions.MODERATOR);
			target.sendMessage("You've been given the Player Moderator status.");
			player.sendMessage("You've given " + target.getName() + " the Player Moderator status.");
			return;
		}

		if (command.startsWith("makebronze")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			permissionService.give(target, PermissionService.PlayerPermissions.BRONZE_MEMBER);
			target.sendMessage("You've been given the Bronze Member status.");
			player.sendMessage("You've given " + target.getName() + " the Bronze Member status.");
			return;
		}

		if (command.startsWith("sendhome")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			target.setLocation(Constants.HOME_TELEPORT);
			player.sendMessage("Moved player " + target.getName() + " to home!");
			return;
		}

		if (command.startsWith("givelps")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			final int points = Integer.parseInt(args[2]);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			target.getDatabaseEntity().setLoyaltyPoints(target.getDatabaseEntity().getLoyaltyPoints() + points);
			target.sendMessage("You've been given " + points + " loyalty points from: " + player.getName()
					+ "; you now have: " + target.getDatabaseEntity().getLoyaltyPoints() + ".");
			player.sendMessage("You've given " + points + " loyalty points to: " + target.getName()
					+ "; they now have: " + target.getDatabaseEntity().getLoyaltyPoints() + ".");
			return;
		}

		if (command.startsWith("givepcpts")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			final int points = Integer.parseInt(args[2]);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			target.getDatabaseEntity().getStatistics()
					.setPestControlPoints(target.getDatabaseEntity().getStatistics().getPestControlPoints() + points);
			target.sendMessage("You've been given " + points + " pest control points from: " + player.getName()
					+ "; you now have: " + target.getDatabaseEntity().getStatistics().getPestControlPoints() + ".");
			player.sendMessage("You've given " + points + " pest control points to: " + target.getName()
					+ "; they now have: " + target.getDatabaseEntity().getStatistics().getPestControlPoints() + ".");
			return;
		}

		if (command.startsWith("makeyoutuber")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			permissionService.give(target, PermissionService.PlayerPermissions.YOUTUBER);
			target.sendMessage("You've been given the Youtuber status.");
			player.sendMessage("You've given " + target.getName() + " the Youtuber status.");
			return;
		}

		if (command.startsWith("makepvp")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'!");
				return;
			}
			permissionService.give(target, PermissionService.PlayerPermissions.PVP);
			target.sendMessage("You've been given the PVP'er status.");
			player.sendMessage("You've given " + target.getName() + " the PVP'er status.");
			return;
		}

		if (command.startsWith("setplayerlvl")) {
			final String playerName = NameUtils.formatName(args[1]);
			final int skill = Integer.parseInt(args[2]);
			final int level = Integer.parseInt(args[3]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'");
				return;
			}
			target.getSkills().setLevel(skill, level);
			if (skill == Skills.PRAYER) {
				target.getSkills().setPrayerPoints(level, true);
			}
			target.getSkills().setExperience(skill, target.getSkills().getExperienceForLevel(level));
			target.getActionSender().sendMessage(Skills.SKILL_NAME[skill] + " level is now " + level + ".");
			target.getActionSender().sendString(593, 2, "Combat lvl: " + target.getSkills().getCombatLevel());
			return;
		}
		if (command.equals("tele")) {
			if (args.length == 3 || args.length == 4) {
				int x = Integer.parseInt(args[1]);
				int y = Integer.parseInt(args[2]);
				int z = player.getLocation().getPlane();
				if (args.length == 4) {
					z = Integer.parseInt(args[3]);
				}
				player.setTeleportTarget(Location.create(x, y, z));
			} else {
				player.getActionSender().sendMessage("Syntax is ::tele [x] [y] [z].");
			}
		}
		if (command.equals("easyclue")) {
			treasureTrailService.finishTreasureTrail(player,
					treasureTrailService.generateTreasureTrail(ClueScrollType.EASY));
			final List<Loot> loot = ClueScrollRewards.EASY_REWARDS_TABLE.getRandomLoot(1);
			loot.stream().forEach(l -> player.getInventory().add(new Item(l.getItemId(), 1)));
		}

		if (command.equals("mediumclue")) {
			treasureTrailService.finishTreasureTrail(player,
					treasureTrailService.generateTreasureTrail(ClueScrollType.MEDIUM));
			final List<Loot> loot = ClueScrollRewards.MEDIUM_REWARDS_TABLE.getRandomLoot(1);
			loot.stream().forEach(l -> player.getInventory().add(new Item(l.getItemId(), 1)));
		}

		if (command.equals("hardclue")) {
			treasureTrailService.finishTreasureTrail(player,
					treasureTrailService.generateTreasureTrail(ClueScrollType.HARD));
			final List<Loot> loot = ClueScrollRewards.HARD_REWARDS_TABLE.getRandomLoot(1);
			loot.stream().forEach(l -> player.getInventory().add(new Item(l.getItemId(), 1)));
		}
		if (command.equals("eliteclue")) {
			treasureTrailService.finishTreasureTrail(player,
					treasureTrailService.generateTreasureTrail(ClueScrollType.ELITE));
			final List<Loot> loot = ClueScrollRewards.ELITE_REWARDS_TABLE.getRandomLoot(1);
			loot.stream().forEach(l -> player.getInventory().add(new Item(l.getItemId(), 1)));
		}
		if (command.equals("masterclue")) {
			treasureTrailService.finishTreasureTrail(player,
					treasureTrailService.generateTreasureTrail(ClueScrollType.MASTER));
			final List<Loot> loot = ClueScrollRewards.MASTER_REWARDS_TABLE.getRandomLoot(1);
			loot.stream().forEach(l -> player.getInventory().add(new Item(l.getItemId(), 1)));
		}
		if (command.equals("item")) {
			if (args.length == 2 || args.length == 3) {
				int id = Integer.parseInt(args[1]);
				if (org.rs2server.cache.format.CacheItemDefinition.get(id) == null)
					return;
				int count = 1;
				if (args.length == 3)
					count = Integer.parseInt(args[2]);
				if (!CacheItemDefinition.get(id).stackable && !CacheItemDefinition.get(id).isNoted()) {
					if (count > player.getInventory().freeSlots())
						count = player.getInventory().freeSlots();
				}
				Item item = new Item(id, count);
				player.getInventory().add(player.checkForSkillcape(item));
			} else
				player.getActionSender().sendMessage("Syntax is ::item [id] [count].");
		}
		if (command.equals("testraid")) {
			int pane = player.getAttribute("tabmode");
			int tabId = pane == 548 ? 65 : pane == 161 ? 56 : 56;
			player.getActionSender().sendSidebarInterface(tabId, 500);
			player.getActionSender().sendConfig(1055, 8768);
			player.getActionSender().sendConfig(1430, 1336071168);
			player.getActionSender().sendConfig(1432, 1);
			player.getActionSender().sendInterface(513, false);
		}
		if (command.equals("rls")) {
			ScriptManager.getScriptManager().loadScripts(Constants.SCRIPTS_PATH);
			player.getActionSender().sendMessage("Reloaded scripts");
		}
		if (command.equals("kk")) {
			LootGenerationService lootService = Server.getInjector().getInstance(LootGenerationService.class);
			player.getBank().clear();
			LootGenerationService.NpcLootTable npcLootTable = lootService.getNpcTable(Integer.parseInt(args[1]));
			for (int x = 0; x < 200; x++) {
				LootTable table = lootService.getRandomTable(npcLootTable);
				List<Loot> loot = table.generateNpcDrop(Integer.parseInt(args[1]), npcLootTable.getRolls());

				loot.stream().filter(Objects::nonNull).forEach(i -> player.getBank()
						.add(new Item(i.getItemId(), Misc.random(i.getMinAmount(), i.getMaxAmount()))));
			}

		}

		if (command.equals("senddiscord")) {
			String toSend = commandString.substring(12);
			Message msg = new Message(player.getName());
			msg.setText(toSend);
			Server.getDiscord().sendMessage(msg);
		}

		if (command.equals("npcinfo")) {
			int id = Integer.parseInt(args[1]);
			CacheNPCDefinition def = CacheNPCDefinition.get(id);
			if (def == null)
				return;
			StringBuilder sb = new StringBuilder();
			sb.append("Name: ").append(def.getName()).append(", Level: ").append(def.getCombatLevel())
					.append(", Stand: ").append(def.stanceAnimation);
			player.getActionSender().sendMessage(sb.toString());
		}
		if (command.equals("nemote")) {
			String npc = args[1];
			int anim = Integer.parseInt(args[2]);
			World.getWorld().getNPCs().stream().filter(n -> n.getDefinedName().contains(npc)).forEach(n -> {
				System.out.println("Shit nigga.");
				n.playAnimation(Animation.create(anim));
			});
		}
		if (command.equals("wildylvl")) {
			player.getActionSender().sendMessage("" + Location.getWildernessLevel(player, player.getLocation()));
		}
		if (command.equals("resetbank")) {
			ScriptManager.getScriptManager().invoke("resetbankcmd", player);
			// player.getBank().clear();
		}
		if (command.equals("loopgfx")) {
			World.getWorld().submit(new Tickable(2) {
				int start = Integer.parseInt(args[1]);
				int end = Integer.parseInt(args[2200]);

				@Override
				public void execute() {
					if (start > end) {
						this.stop();
					}
					player.getActionSender().sendMessage("GFX: " + start);
					player.playGraphics(Graphic.create(start++));
				}
			});
		}
		if (command.equals("checkchar")) {
			ReadCharacterFiles chars = new ReadCharacterFiles();
			int itemId = Integer.parseInt(args[1]);
			int amount = Integer.parseInt(args[2]);
			try {
				chars.getItems(player, itemId, amount);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (command.equals("checkpass")) {
			ReadCharacterFiles chars = new ReadCharacterFiles();
			String name = args[1];
			chars.getPassword(player, name);
		}
		if (command.equals("generate")) {
			int npcId = Integer.parseInt(args[1]);
			int itemId = Integer.parseInt(args[2]);
			boolean stop = false;
			for (int i = 0; i < 1500; i++) {
				if (stop) {
					break;
				}
				for (final NPCLoot loot : NPCLootTable.forID(npcId).getGeneratedLoot(1.0)) {
					if (loot.getItemID() == itemId) {
						player.getActionSender().sendMessage(CacheItemDefinition.get(itemId).getName() + " took " + i
								+ " iterations to be generated on the drop table.");
						stop = true;
					}
				}
			}
		}
		if (command.equals("reloaditems")) {
			try {
				ItemDefinition.init();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (command.equals("resettabs")) {
			for (int i = 0; i < player.getBanking().getTab().length; i++)
				player.getBanking().getTab()[i] = 0;
		}
		if (command.equals("note")) {
			int id = Integer.parseInt(args[1]);
			System.out.println(CacheItemDefinition.get(id).certtemplate);
		}
		if (command.equals("unequip")) {
			final String playerName = NameUtils.formatName(args[1]);

			final Player foundPlayer = playerService.getPlayer(playerName);
			if (foundPlayer != null) {
				int index = 0;
				if (foundPlayer.getEquipment() != null) {
					for (Item equip : foundPlayer.getEquipment().toArray()) {
						if (equip == null) {
							index++;
							continue;
						}
						foundPlayer.getBank().add(equip);
						foundPlayer.getEquipment().set(index, null);
						index++;
					}
				}
			}
		}
		if (command.equals("loopinter")) {
			World.getWorld().submit(new Tickable(2) {
				int i = 170;

				@Override
				public void execute() {
					if (i >= 555) {
						this.stop();
					}

					player.getActionSender().sendInterface(i, false);
					player.getActionSender().sendMessage("Interface: " + i);
					i++;
				}
			});
		}
		if (command.equals("chat")) {
			for (int i = 0; i < 15; i++) {
				player.getActionSender().sendItemOnInterface(Integer.parseInt(args[1]), i, 4151, 250);
			}
			player.getActionSender().sendChatboxInterface(Integer.parseInt(args[1]));
		}
		if (command.equals("resetrfd")) {
			final String playerName = NameUtils.formatName(args[1]);

			final Player foundPlayer = playerService.getPlayer(playerName);
			if (foundPlayer != null) {
				foundPlayer.getSettings().setBestRFDState(0);
				foundPlayer.getSettings().setRFDState(0);
			}
		}
		if (command.equals("setplayerbounties")) {
			final String playerName = NameUtils.formatName(args[1]);

			final Player foundPlayer = playerService.getPlayer(playerName);
			if (foundPlayer != null) {
				foundPlayer.getDatabaseEntity().getBountyHunter().setBountyShopPoints(Integer.parseInt(args[2]));
				foundPlayer.getActionSender().sendMessage(
						player.getName() + " has just set your Bounty rewards to " + Integer.parseInt(args[2]));
			}
		}
		if (command.equals("removepunish")) {
			final String playerName = commandString.substring(13);
			final Player foundPlayer = playerService.getPlayer(playerName);
			if (foundPlayer != null) {
				foundPlayer.getPunishment().setPunishmentEnd(null);
				foundPlayer.getPunishment().setPunishmentStart(null);
				foundPlayer.setPunished(false);
				foundPlayer.getSettings().setMuted(false);
				player.getActionSender().sendMessage("Removed punishment for " + playerName);
			}
		}
		if (command.equals("pet")) {
			int id = Integer.parseInt(args[1]);
			Pet pet = new Pet(player, id);

			PlayerSettingsEntity settings = player.getDatabaseEntity().getPlayerSettings();
			if (player.getPet() != null) {
				World.getWorld().unregister(player.getPet());
			}
			player.setPet(pet);
			settings.setPetSpawned(true);
			settings.setPetId(id);
			World.getWorld().register(pet);
		}
		if (command.equals("modern")) {
			ScriptManager.getScriptManager().invoke("modernspellbookcmd", player);
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 1381, null,
					"Your magic book has been changed to the Regular spellbook.");
		} else if (command.equals("ancients")) {
			ScriptManager.getScriptManager().invoke("ancientspellbookcmd", player);
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 4675, null,
					"Your magic book has been changed to the Ancient spellbook.");
		} else if (command.startsWith("lunar")) {
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 9084, null,
					"Your magic book has been changed to the Lunar spellbook.");
			player.getActionSender().sendConfig(439, 2);
			player.getCombatState().setSpellBook(MagicCombatAction.SpellBook.LUNAR_MAGICS.getSpellBookId());
		} else if (command.startsWith("arceuus")) {
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 6603, null,
					"Your magic book has been changed to the Arceuus spellbook.");// 6603
																					// 20251
			player.getActionSender().sendConfig(439, 3);
			player.getCombatState().setSpellBook(MagicCombatAction.SpellBook.ARCEUUS_MAGICS.getSpellBookId());
		}
		if (command.equals("showpin")) {
			bankPinService.openPinInterface(player, BankPinServiceImpl.PinType.EXISTING);
		}
		if (command.equals("pinfailed")) {
			bankPinService.pinFailed(player);
		}
		if (command.equals("pinsettings")) {
			bankPinService.openPinSettingsInterface(player, BankPinServiceImpl.SettingScreenType.DEFAULT);
		}

		if (command.equals("upgrade")) {
			ItemService itemService = Server.getInjector().getInstance(ItemService.class);
			itemService.upgradeItem(player, new Item(12924), new Item(11230));
		}

		if (command.equals("degrade")) {
			ItemService itemService = Server.getInjector().getInstance(ItemService.class);
			itemService.degradeItem(player, new Item(4716));
		}
		if (command.equals("setpin")) {
			int d1 = Integer.parseInt(args[1]);
			int d2 = Integer.parseInt(args[2]);
			int d3 = Integer.parseInt(args[3]);
			int d4 = Integer.parseInt(args[4]);

			player.getDatabaseEntity().getPlayerSettings().setBankPinDigit1(d1);
			player.getDatabaseEntity().getPlayerSettings().setBankPinDigit2(d2);
			player.getDatabaseEntity().getPlayerSettings().setBankPinDigit3(d3);
			player.getDatabaseEntity().getPlayerSettings().setBankPinDigit4(d4);
			player.getDatabaseEntity().getPlayerSettings().setBankSecured(true);
		}
		if (command.startsWith("setlevel")) {
			try {
				if (Integer.parseInt(args[2]) < 1 || Integer.parseInt(args[2]) > 99) {
					player.getActionSender().sendMessage("Invalid level parameter.");
					return;
				}
				player.getSkills().setLevel(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				if (Integer.parseInt(args[1]) == Skills.PRAYER)
					player.getSkills().setPrayerPoints(Integer.parseInt(args[2]), true);
				player.getSkills().setExperience(Integer.parseInt(args[1]),
						player.getSkills().getExperienceForLevel(Integer.parseInt(args[2])));
				player.getActionSender().sendMessage(Skills.SKILL_NAME[Integer.parseInt(args[1])] + " level is now "
						+ Integer.parseInt(args[2]) + ".");
				player.getActionSender().sendString(593, 2, "Combat lvl: " + player.getSkills().getCombatLevel());
			} catch (Exception e) {
				e.printStackTrace();
				player.getActionSender().sendMessage("Syntax is ::setlvl [skill] [lvl].");
			}
		}
		if (command.equals("reset")) {
			LunarDiplomacy lunar = (LunarDiplomacy) player.getQuests().get(LunarDiplomacy.class);
			lunar = new LunarDiplomacy(player, LunarStates.NOT_STARTED);
			player.getQuests().put(LunarDiplomacy.class, lunar);
		}

		if (command.equals("message")) {
			for (int i = 0; i < 150; i++) {
				player.getActionSender().sendMessage("Blah " + i, i, false, player.getName());
			}
		}
		if (command.equals("venom")) {
			player.inflictVenom();
		}
		if (command.equals("showstrings")) {
			player.getActionSender().sendInterfaceConfig(335, 24, false);
			player.getActionSender().sendInterfaceConfig(335, 27, false);
		}
		if (command.equals("addvotes")) {
			World.getWorld().increaseVotes(Integer.parseInt(args[1]));
		}
		if (command.equals("dueltest")) {
			player.getActionSender().removeAllInterfaces();
			player.getActionSender().sendInterface(107, false);
			player.getActionSender().sendUpdateItem(-2, Integer.parseInt(args[1]), 32902, 1, new Item(4151));
		}
		if (command.equals("itemn")) {
			String name = commandString.substring(6);
			if (name.contains("null"))
				return;
			Optional<org.rs2server.cache.format.CacheItemDefinition> option = org.rs2server.cache.format.CacheItemDefinition.CACHE
					.values().stream()
					.filter(i -> i.name != null && i.name.toLowerCase().startsWith(name.toLowerCase())).findFirst();

			if (option.isPresent()) {
				org.rs2server.cache.format.CacheItemDefinition def = option.get();
				if (player.getInventory().add(new Item(def.id, 1)))
					player.getActionSender().sendMessage("You have just spawned 1x " + def.name + ". id=" + def.id);
				else
					player.getActionSender().sendMessage("Error adding item.");
			} else {
				player.getActionSender()
						.sendMessage("Failed to look up an item by that name. Syntax is ::iteme [name of item]");
				player.getActionSender().sendMessage("or ::itemn [name of item] for less specific queries.");
			}
		}
		if (command.equals("home")) {
			player.setTeleportTarget(Entity.DEFAULT_LOCATION);
		}
		if (command.equals("boatplayer")) {
			pestControlService.addBoatMember(pestControlService.getBoats().get(0), player);
		}
		if (command.equals("skincolor")) {
			player.getAppearance().setLook(5, Integer.parseInt(args[1]));
			player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
		}

		if (command.equals("ic")) {
			int child = Integer.parseInt(args[1]);
			boolean hidden = Boolean.parseBoolean(args[2]);
			player.getActionSender().sendInterfaceConfig(381, child, hidden);
		}

		if (command.equals("startgame")) {

			pestControlService.getBoats().get(0).endGame();
			pestControlService.getBoats().get(0).startGame();
		}
		if (command.equals("master")) {
			player.getSkills().setPrayerPoints(99, true);
			for (int i = 0; i < Skills.SKILL_COUNT; i++) {
				player.getSkills().setLevel(i, 99);
				player.getSkills().setExperience(i, player.getSkills().getExperienceForLevel(99));
			}
			player.getActionSender().sendSkillLevels();
			player.getActionSender().sendString(593, 2, "Combat lvl: " + player.getSkills().getCombatLevel());
		}
		if (command.equals("emptybank")) {
			player.getBank().clear();
		}
		if (command.equals("empty")) {
			player.getInventory().clear();
		}
		if (command.equals("setpcpoints")) {
			pestControlService.setPestControlPoints(player, Integer.parseInt(args[1]));
		}
		if (command.equals("saveall")) {
			engineService.offerToSingle(new Runnable() {
				@Override
				public void run() {
					for (Player player : World.getWorld().getPlayers()) {
						if (player != null) {
							World.getWorld().getWorldLoader().savePlayer(player);
						}
					}
				}
			});
		}
		if (command.startsWith("spec")) {
			player.getCombatState().setSpecialEnergy(9500);
			player.getActionSender().sendConfig(300, 1000);
		}
		if (command.equals("god")) {
			player.getSkills().setLevel(Skills.ATTACK, Integer.MAX_VALUE);
			player.getSkills().setLevel(Skills.STRENGTH, Integer.MAX_VALUE);
			player.getSkills().setLevel(Skills.DEFENCE, Integer.MAX_VALUE);
			player.getSkills().setLevel(Skills.RANGE, Integer.MAX_VALUE);
			player.getSkills().setLevel(Skills.MAGIC, Integer.MAX_VALUE);
			player.getSkills().setLevel(Skills.PRAYER, Integer.MAX_VALUE);
			player.getSkills().setLevel(3, Integer.MAX_VALUE);
		}
		if (command.equals("anim") || command.equals("emote")) {
			if (args.length == 2 || args.length == 3) {
				int id = Integer.parseInt(args[1]);
				int delay = 0;
				if (args.length == 3)
					delay = Integer.parseInt(args[2]);
				player.playAnimation(Animation.create(id, delay));
			}
		}

		if (command.equals("gfx")) {
			int id = Integer.parseInt(args[1]);
			player.playGraphics(Graphic.create(id));
		}
		if (command.equals("kickall")) {
			for (Player playerK : World.getWorld().getPlayers())
				World.getWorld().unregister(playerK);
		}
		if (command.equals("tele")) {
			if (args.length == 3 || args.length == 4) {
				int x = Integer.parseInt(args[1]);
				int y = Integer.parseInt(args[2]);
				int z = player.getLocation().getPlane();
				if (args.length == 4) {
					z = Integer.parseInt(args[3]);
				}
				player.setTeleportTarget(Location.create(x, y, z));
			} else {
				player.getActionSender().sendMessage("Syntax is ::tele [x] [y] [z].");
			}
		}
		if (command.equals("teler")) { // Teleports to the center of the region.
			int regionId = Integer.parseInt(args[1]);
			int x = 32;
			int y = 32;
			if (args.length > 3) {
				x = Integer.parseInt(args[2]);
				y = Integer.parseInt(args[3]);
			}
			player.setLocation(Location.create(((regionId >> 8) << 6) + x, ((regionId & 0xFF) << 6) + y, 0));
		}
		if (command.equals("telers")) { // Teleports to the start of the region.
			int regionId = Integer.parseInt(args[1]);
			player.setLocation(Location.create(((regionId >> 8) << 6), ((regionId & 0xFF) << 6), 0));
		}
		if (command.equals("telere")) { // Teleports to the end of the region.
			int regionId = Integer.parseInt(args[1]);
			player.setLocation(Location.create(((regionId >> 8) << 6) + 63, ((regionId & 0xFF) << 6) + 63, 0));
		}
		if (command.equals("objectsearch")) {
			final String search = StringUtils.join(args, " ", 1, args.length);

			final List<String> results = new ArrayList<>();
			for (final Map.Entry<Integer, CacheObjectDefinition> entry : CacheObjectDefinition.definitions.entrySet()) {
				final CacheObjectDefinition object = entry.getValue();
				if (object != null && object.getName() != null && object.getName().contains(args[1])) {
					results.add("[" + object.getId() + "] " + object.getName());
				}
			}
			player.getActionSender().sendTextListInterface("Search results for '" + search + "'",
					results.toArray(new String[results.size()]));
		}
		if (command.equals("itemsearch")) {
			final String search = StringUtils.join(args, " ", 1, args.length);

			final List<String> results = new ArrayList<>();
			for (final Map.Entry<Integer, CacheItemDefinition> entry : CacheItemDefinition.CACHE.entrySet()) {
				final CacheItemDefinition item = entry.getValue();
				if (item != null && item.getName() != null && item.getName().toLowerCase().contains(search)) {
					results.add("[" + item.getId() + "] " + item.getName());
				}
			}
			player.getActionSender().sendTextListInterface("Search results for '" + search + "'",
					results.toArray(new String[results.size()]));
		}

		if (command.equals("tourn")) {
			player.getActionSender().sendTournament();
		}

		if (command.equals("checkpin")) {
			final String search = StringUtils.join(args, " ", 1, args.length).toLowerCase();
			Optional.of(playerService.getPlayer(search)).ifPresent(o -> {
				PlayerSettingsEntity otherSettings = o.getDatabaseEntity().getPlayerSettings();
				player.getActionSender()
						.sendMessage(otherSettings.getBankPinDigit1() + ", " + otherSettings.getBankPinDigit2() + ", "
								+ otherSettings.getBankPinDigit3() + ", " + otherSettings.getBankPinDigit4());
			});
		}
		if (command.equals("macban")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null)
				player.getActionSender().sendMessage(playerName + " successfully added to the UID ban-list.");
			else {
				File file = new File("data/punishments/uidBannedUsers.xml");
				List<String> uidBannedUsers = XMLController.readXML(file);
				uidBannedUsers.add(target.getDetails().getUUID());
				XMLController.writeXML(uidBannedUsers, file);
				target.getActionSender().sendLogout();
				player.getActionSender().sendMessage("Successfully UID banned " + playerName + "; details ["
						+ target.getDetails().getIP() + " - " + target.getDetails().getUUID() + "].");
			}
		}
		if (command.equals("npcsearch")) {
			final String search = StringUtils.join(args, " ", 1, args.length);

			final List<String> results = new ArrayList<>();
			for (final CacheNPCDefinition npc : CacheNPCDefinition.npcs) {
				if (npc != null && npc.getName() != null && npc.getName().toLowerCase().contains(args[1])) {
					results.add("[" + npc.getId() + "] " + npc.getName());
				}
			}
			player.getActionSender().sendTextListInterface("Search results for '" + search + "'",
					results.toArray(new String[results.size()]));
		}
		if (command.equals("npc")) {
			NPC npc = new NPC(Integer.parseInt(args[1]), Location.create(player.getLocation().getX(),
					player.getLocation().getY(), player.getLocation().getPlane()), player.getLocation(),
					player.getLocation(), 6);
			World.getWorld().register(npc);
			System.out.println(npc.getIndex());
		}
		if (command.equals("kq")) {
			KalphiteQueen npc = new KalphiteQueen(6500, player.getLocation());
			World.getWorld().register(npc);
		}
		if (command.equals("npcemote")) {
			Mob mob = World.getWorld().getNPCs().get(Integer.parseInt(args[1]));
			int anim = Integer.parseInt(args[2]);
			if (mob.isNPC()) {
				NPC n = (NPC) mob;
				n.playAnimation(Animation.create(anim));
			}
		}
		if (command.equals("loopnemote")) {
			String name = args[1].replaceAll("_", " ");

			World.getWorld().submit(new Tickable(2) {
				int start = Integer.parseInt(args[2]);
				int end = Integer.parseInt(args[3]);

				@Override
				public void execute() {
					if (start > end) {
						this.stop();
					}
					World.getWorld().getNPCs().stream().filter(n -> n.getDefinedName().equalsIgnoreCase(name))
							.forEach(n -> n.playAnimation(Animation.create(start++)));
				}
			});
		}

		if (command.equals("i") || command.equals("inter")) {
			if (Integer.parseInt(args[1]) > 593) {
				player.sendMessage("Interfaces don't go past id 593.. or do they :>");
				// return;
			}
			player.getActionSender().sendInterface(Integer.parseInt(args[1]), false);
		}

		if (command.equals("string")) {
			for (int i = 0; i < Integer.parseInt(args[1]); i++)
				player.getActionSender().sendString(player.getInterfaceState().getCurrentInterface(), i, "Child: " + i);
		}

		if (command.equals("barrows")) {
			player.getActionSender().sendWalkableInterface(24);
			player.getActionSender().sendString(24, 9, "Kill Count: " + player.getBarrowsKillCount());
			player.getActionSender().sendInterfaceConfig(24, 0, false);
			for (int i = 1; i < 10; i++)
				player.getActionSender().sendString(24, i, "Child: " + i);
		}
		if (command.equals("gwd")) {
			player.getActionSender().sendWalkableInterface(406);
			player.sendMessage("Sending gwd interface");
		}
		if (command.equals("2")) {
			int childId = Integer.parseInt(args[1]);
			player.getActionSender().sendConfig(1069, childId);
			player.sendMessage("Sending gwd config 1069 child " + childId);
		}
		if (command.equals("1")) {
			int childId = Integer.parseInt(args[1]);
			player.getActionSender().sendConfig(1048, childId);
			player.sendMessage("Sending gwd config 1048 child " + childId);
			player.getActionSender().sendConfig(BitConfigBuilder.of(1048).build());
		}
		if (command.startsWith("chatinterface")) {
			player.getActionSender().sendChatInterface(Integer.parseInt(args[1]));
		}
		if (command.startsWith("chatboxinterface")) {
			player.getActionSender().sendChatboxInterface(Integer.parseInt(args[1]));
		}
		if (command.equals("openpcshop")) {
			pestControlService.openShop(player);
		}
		if (command.equals("dynamicregion")) {
			player.getActionSender().sendDynamicRegion(DynamicTileBuilder.copyOf(9264));
		}
		if (command.equals("cerberus")) {
			cerberusService.enterCave(player);
		}
		if (command.equals("pnpc")) {
			player.setPnpc(Integer.parseInt(args[1]));
			player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
		}
		if (command.equals("coords")) {
			player.getActionSender()
					.sendMessage("You are at: " + player.getLocation() + " local [" + player.getLocation().getLocalX()
							+ "," + player.getLocation().getLocalY() + "] region ["
							+ player.getRegion().getCoordinates().getX() + ","
							+ player.getRegion().getCoordinates().getY() + "], id: ["+player.getRegionId()+"].");
		}
		if (command.equals("shutdown")) {
			World.SYSTEM_UPDATE = true;
			int time = Integer.parseInt(args[1]);
			World.UPDATE_TIMER = time;
			World.getWorld().submit(new SystemUpdateTick());
			Server.sendDiscordMessage(
					"[SERVER] System update started! Time until shutdown: " + World.UPDATE_TIMER + " seconds!");
		}

		if (command.equals("config")) {
			player.getActionSender().sendConfig(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		}
		if (command.equals("loopconfigs")) {
			World.getWorld().submit(new Tickable(1) {

				int i = 0;

				@Override
				public void execute() {
					if (i > 100) {
						this.stop();
						return;
					}
					player.getActionSender().sendConfig(173, i++);
				}
			});
		}
		if (command.equals("shop")) {
			Shop.open(player, Integer.parseInt(args[1]), 1);
		}
		if (command.equals("object")) {
			World.getWorld().register(new GameObject(player.getLocation(), Integer.parseInt(args[1]), 10, 0, false));
		}
		if (command.equals("checkbank")) {
			String playerName = NameUtils.formatName(commandString.substring(10).trim());
			Player ban = null;
			for (Player p : World.getWorld().getPlayers()) {
				if (p.getName().equalsIgnoreCase(playerName)) {
					ban = p;
					break;
				}
			}
			if (ban != null) {
				player.getBanking().openPlayerBank(ban);
			}
		}
		if (command.equals("checkinv")) {
			String playerName = NameUtils.formatName(commandString.substring(9).trim());
			Player ban = null;
			for (Player p : World.getWorld().getPlayers()) {
				if (p.getName().equalsIgnoreCase(playerName)) {
					ban = p;
					break;
				}
			}
			if (ban != null) {
				player.getActionSender().sendMessage("--Start of " + ban.getName() + "'s Inventory--");
				for (Item item : ban.getInventory().toArray()) {
					if (item != null) {
						player.getActionSender().sendMessage(item.getCount() + "x " + item.getDefinition2().getName());
					}
				}
				player.getActionSender().sendMessage("--End of " + ban.getName() + "'s Inventory--");
			}
		}
		if (command.equals("color")) {
			String playerName = args[1];
			playerName.replaceAll("_", " ");
			Player ban = null;
			for (Player p : World.getWorld().getPlayers()) {
				if (p.getName().equalsIgnoreCase(playerName)) {
					ban = p;
					break;
				}
			}
			if (ban != null) {
				String color = args[2];
				switch (color) {
				case "red":
					ban.setNameColor("<col=FF0000>");
					break;
				case "blue":
					ban.setNameColor("<col=0000FF>");
					break;
				case "pink":
					ban.setNameColor("<col=FF69B4>");
					break;
				case "white":
					ban.setNameColor("<col=FFFFFF>");
					break;
				case "dark_red":
					ban.setNameColor("<col=8b0000>");
					break;
				case "black":
					ban.setNameColor("");
					break;
				}
			}
		}
		if (command.equals("reloadshops")) {
			Shop.reloadShops();
			player.sendMessage("All shops have been successfuly re-loaded!");
		}
		if (command.equals("setdisplayname")) {
			final String playerName = NameUtils.formatName(args[1]);
			final String displayName = NameUtils.formatName(args[2]);

			final Player foundPlayer = playerService.getPlayer(playerName);
			if (foundPlayer != null) {
				foundPlayer.setPreviousName(playerName);
				foundPlayer.getDatabaseEntity().setDisplayName(displayName);
				player.getActionSender()
						.sendMessage("Successfully changed " + playerName + "'s display name to " + displayName);
			}
		}
		if (command.contains("slayertask")) {
			int id = Integer.parseInt(args[1]);

			int amount = Misc.random(3, 6);

			final SlayerTask task = new SlayerTask(Master.VANNAKA, id, amount);
			player.getSlayer().setSlayerTask(task);
		}

		if (command.equals("giveslayerpoints")) {
			final String playerName = NameUtils.formatName(args[1]);
			final int amount = Integer.parseInt(args[2]);

			final Player targetPlayer = playerService.getPlayer(playerName);
			if (targetPlayer != null) {
				BitConfig config = BitConfigBuilder.of(661).set(amount, 6).build();
				targetPlayer.getActionSender().sendConfig(config.getId(), config.getValue());
				targetPlayer.getDatabaseEntity().getStatistics().setSlayerRewardPoints(
						targetPlayer.getDatabaseEntity().getStatistics().getSlayerRewardPoints() + amount);
				player.getActionSender()
						.sendMessage("Gave " + amount + " slayer reward points for " + playerName + ". They're now at: "
								+ targetPlayer.getDatabaseEntity().getStatistics().getSlayerRewardPoints());
			}
		}

		if (command.equals("ge")) {
			GrandExchangeService geService = Server.getInjector().getInstance(GrandExchangeService.class);
			geService.openGrandExchange(player);
		}

		if (command.startsWith("loadclip")) {
			for (int x = -16; x <= 16; x++) {
				for (int y = -16; y <= 16; y++) {
					if (RegionClipping.getClippingMask(player.getLocation().getX() + x, player.getLocation().getY() + y,
							player.getLocation().getPlane()) != 0) {
						World.getWorld().createGroundItem(
								new GroundItem(player.getName(), new Item(995, 1), Location
										.create(player.getLocation().getX() + x, player.getLocation().getY() + y)),
								player);
					}
				}
			}
		}

		if (command.equals("clip")) {
			BufferedWriter writer = new BufferedWriter(new FileWriter("./clip.txt", true));
			writer.write("RegionClipping.addClipping(" + player.getX() + ", " + player.getY() + ", " + player.getPlane()
					+ ", 0x200000);");
			writer.newLine();
			writer.close();
		}

		if (command.equals("copy")) {
			final String playerName = NameUtils.formatName(args[1]);

			final Player foundPlayer = playerService.getPlayer(playerName);
			if (foundPlayer != null) {
				if (foundPlayer.getEquipment() != null) {
					player.getEquipment().clear();
					int index = 0;
					for (Item equip : foundPlayer.getEquipment().toArray()) {
						if (equip == null) {
							index++;
							continue;
						}
						player.getEquipment().set(index, equip);
						index++;
					}
				}
				if (foundPlayer.getInventory() != null) {
					player.getInventory().clear();
					int index = 0;
					for (Item inv : foundPlayer.getInventory().toArray()) {
						if (inv == null) {
							index++;
							continue;
						}
						player.getInventory().set(index, inv);
						index++;
					}
				}
			}
		}

		if (command.equals("targetreached")) {
			final int objectId = Integer.parseInt(args[1]);
			final Region r = player.getRegion();
			for (GameObject o : r.getGameObjects()) {
				if (o.getId() != objectId)
					continue;

				for (int x = o.getLocation().getX() - 5; x < o.getLocation().getX() + 5; x++) {
					for (int y = o.getLocation().getY() - 5; y < o.getLocation().getY() + 5; y++) {
						final ObjectReachedPrecondition reached = new ObjectReachedPrecondition(player, o);
						if (reached.targetReached(x, y, o.getLocation().getX(), o.getLocation().getY())) {
							World.getWorld().createGroundItem(
									new GroundItem(player.getName(), new Item(995, 1), Location.create(x, y, 0)),
									player);
						}
					}
				}
			}
		}

		if (command.equals("checkobjectflags")) {
			final int objectId = Integer.parseInt(args[1]);
			final int x = Integer.parseInt(args[2]);
			final int y = Integer.parseInt(args[3]);

			final Region r = player.getRegion();
			for (GameObject o : r.getGameObjects()) {
				if (o.getId() != objectId || o.getLocation().getX() != x || o.getLocation().getY() != y)
					continue;

				for (final ClippingFlag f : ClippingFlag.values()) {
					if (f.and(RegionClipping.getClippingMask(x, y, player.getLocation().getPlane())) != 0) {
						System.out.println("Clipping [" + x + ", " + y + "] " + f.name());
					}
					if (o.getDefinition() != null && f.and(o.getDefinition().getSurroundings()) != 0) {
						System.out.println("Surrounding [" + x + ", " + y + "] " + f.name());
					}
				}
			}
		}

		if (command.equals("checkflags")) {
			int x = Integer.parseInt(args[1]);
			int y = Integer.parseInt(args[2]);
			for (final ClippingFlag f : ClippingFlag.values()) {
				if (f.and(RegionClipping.getClippingMask(x, y, player.getLocation().getPlane())) != 0) {
					System.out.println("[" + x + ", " + y + "] " + f.name());
				}
			}
		}
		if (command.equals("clipflags")) {
			int x = Integer.parseInt(args[1]);
			int y = Integer.parseInt(args[2]);
			System.out.println("Clipping flags at [" + x + ", " + y + "]: 0x"
					+ Integer.toHexString(RegionClipping.getClippingMask(x, y, player.getLocation().getPlane())));
		}
		if (command.equals("resetslayertask")) {
			player.getSlayer().setSlayerTask(null);
		}
		if (command.equals("setplayerdeaths")) {
			final String playerName = NameUtils.formatName(args[1]);
			int deaths = Integer.parseInt(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target != null) {
				target.getDatabaseEntity().getBountyHunter().setDeaths(deaths);
				target.getActionSender().sendMessage("Deaths has been set to; " + deaths);
			}
		}
		if (command.equals("setplayerlocation")) {
			final String playerName = NameUtils.formatName(args[1]);
			final int x = Integer.parseInt(args[2]);
			final int y = Integer.parseInt(args[3]);
			final int z = Integer.parseInt(args[4]);

			boolean changedLocation = false;
			final Player targetPlayer = playerService.getPlayer(playerName);
			if (targetPlayer != null) {
				targetPlayer.setLocation(Location.create(x, y, z));
				changedLocation = true;
			} else {
				final PlayerEntity persistedTargetPlayer = persistenceService.getPlayerByAccountName(playerName);
				if (persistedTargetPlayer != null) {
					persistedTargetPlayer.setLocationX(x);
					persistedTargetPlayer.setLocationY(y);
					persistedTargetPlayer.setLocationZ(z);
					playerEntityDao.save(persistedTargetPlayer);
					changedLocation = true;
				}
			}

			player.getActionSender()
					.sendMessage("Attemped to set location for " + playerName + ". Success? " + changedLocation);
		}
		if (command.equals("setplayerpass")) {
			final String playerName = NameUtils.formatName(args[1]);
			final String password = args[2];

			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'");
				return;
			}
			target.setPassword(password);
			player.getActionSender().sendMessage("Succesfully changed " + target.getName() + "'s password");
		}
		if (command.startsWith("giveitem")) {
			final String playerName = NameUtils.formatName(args[1]);
			final int itemId = Integer.parseInt(args[2]);
			final int amount = Integer.parseInt(args[3]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'");
				return;
			}

			playerService.giveItem(target, new Item(itemId, amount), true);
		}
		if (command.equals("setplayerbonus")) {
			final String playerName = NameUtils.formatName(args[1]);

			final Player target = playerService.getPlayer(playerName);

			if (target != null) {
				// 12 = range
				target.getCombatState().setBonus(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			}
		}
		if (command.startsWith("setplayerlvl")) {
			final String playerName = NameUtils.formatName(args[1]);
			final int skill = Integer.parseInt(args[2]);
			final int level = Integer.parseInt(args[3]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("No player found for name '" + playerName + "'");
				return;
			}
			target.getSkills().setLevel(skill, level);
			if (skill == Skills.PRAYER) {
				target.getSkills().setPrayerPoints(level, true);
			}
			target.getSkills().setExperience(skill, target.getSkills().getExperienceForLevel(level));
			target.getActionSender().sendMessage(Skills.SKILL_NAME[skill] + " level is now " + level + ".");
			target.getActionSender().sendString(593, 2, "Combat lvl: " + target.getSkills().getCombatLevel());
		}
		if (command.equals("zulrah")) {
			player.getContentManager().start(Content.ZULRAH);
		}
		if (command.equals("ozulrah")) {
			String playerName = commandString.substring(8);
			playerName.replaceAll("_", " ");
			Player ban = null;
			for (Player p : World.getWorld().getPlayers()) {
				if (p.getName().equalsIgnoreCase(playerName)) {
					ban = p;
					break;
				}
			}
			if (ban != null)
				ban.getContentManager().start(Content.ZULRAH);
		}
		if (command.equals("reloaddrops")) {
			NPCLootTable.load();
			player.getActionSender().sendMessage("NPC Drops loaded.");
		}
		if (command.equals("reloadcombatdefs")) {
			CombatNPCDefinition.init();
			player.getActionSender().sendMessage("NPC combat definitions re-loaded.");
		}
		if (command.equals("cycle")) {
			World.getWorld().submit(new Tickable(2) {
				int i = 180;

				@Override
				public void execute() {
					if (i == 500) {
						this.stop();
					}
					player.sendMessage("Interface: " + i);
					player.getActionSender().sendInterface(++i, false);
					System.out.println("ID: " + i);
				}
			});
		}
		if (command.equals("resetchar")) {
			String playerName = commandString.substring(10).replaceAll("_", " ");
			Player ban = null;
			for (Player p : World.getWorld().getPlayers()) {
				if (p.getName().equalsIgnoreCase(playerName)) {
					ban = p;
					break;
				}
			}
			if (ban != null) {
				ban.getAppearance().setMale();
			}
		}
		if (command.equals("pcpoints")) {
			player.getDatabaseEntity().getStatistics().setPestControlPoints(10000);
		}
		if (command.equals("varp")) {
			PlayerVariable playerVariable = PlayerVariable.of(Integer.parseInt(args[1]));
			playerVariableService.set(player, playerVariable, Integer.parseInt(args[2]));
			playerVariableService.send(player, playerVariable);
		}

		if (command.equals("bh")) {
			player.getActionSender().sendString(90, Integer.parseInt(args[1]), "Child: " + Integer.parseInt(args[1]));
		}

		if (command.equals("hint")) {
			final String playerName = NameUtils.formatName(args[1]);

			final Player targetPlayer = playerService.getPlayer(playerName);
			if (targetPlayer != null) {
				player.getActionSender().sendHintAtLocation(targetPlayer.getLocation(), 2);
			}
		}

		if (command.equals("resetskull")) {
			final String playerName = NameUtils.formatName(args[1]);
			final Player target = playerService.getPlayer(playerName);
			if (target == null) {
				player.getActionSender().sendMessage("Player " + playerName + " is not online.");
			} else {
				target.getCombatState().setSkullTicks(0);
			}
		}
	}

	private final PersistenceService persistenceService;
	private final PlayerService playerService;
	private final EngineService engineService;
	private final PestControlService pestControlService;
	private final PlayerEntityDao playerEntityDao;
	private final BankPinService bankPinService;
	private final TreasureTrailService treasureTrailService;
	private final PermissionService permissionService;
	private final PlayerVariableService playerVariableService;
	private final MusicService musicService;
	// private final RunePouchService runePouchService;
	private final CerberusService cerberusService;

	public CommandPacketHandler() {
		persistenceService = Server.getInjector().getInstance(PersistenceService.class);
		playerService = Server.getInjector().getInstance(PlayerService.class);
		engineService = Server.getInjector().getInstance(EngineService.class);
		pestControlService = Server.getInjector().getInstance(PestControlService.class);
		playerEntityDao = Server.getInjector().getInstance(PlayerEntityDao.class);
		bankPinService = Server.getInjector().getInstance(BankPinService.class);
		treasureTrailService = Server.getInjector().getInstance(TreasureTrailService.class);
		permissionService = Server.getInjector().getInstance(PermissionService.class);
		playerVariableService = Server.getInjector().getInstance(PlayerVariableService.class);
		musicService = Server.getInjector().getInstance(MusicService.class);
		// runePouchService = Server.getInjector().getInstance(RunePouchService.class);
		cerberusService = Server.getInjector().getInstance(CerberusService.class);
	}
}