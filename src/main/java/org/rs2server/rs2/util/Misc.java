package org.rs2server.rs2.util;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.domain.service.api.PermissionService;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender.DialogueType;

import com.everythingrs.vote.Vote;

import java.text.*;
import java.util.*;

public class Misc {

	public static final String[] NUMBERWORD = { "zero", "one", "two", "three", "four", "five", "six", "seven", "eight",
			"nine", "ten", "eleven", "twelve" };

	/**
	 * The {@link Random} instance used for randomizing a value.
	 */
	private static Random r = new Random();

	public static int random(int i) {
		return r.nextInt(i + 1);
	}

	public static byte directionDeltaX[] = new byte[] { 0, 1, 1, 1, 0, -1, -1, -1 };

	public static byte directionDeltaY[] = new byte[] { 1, 1, 0, -1, -1, -1, 0, 1 };

	public static int random(int min, int max) {
		final int n = Math.abs(max - min);
		return Math.min(min, max) + (n == 0 ? 0 : random(n));
	}

	public static int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		Iterator<Integer> iterator = integers.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next();
		}
		return ret;
	}

	public static int getModIconForPerm(PermissionService.PlayerPermissions perm) {
		switch (perm) {
		case DEV:
		case ADMINISTRATOR:
			return 1;
		case MODERATOR:
			return 0;
		case HELPER:
			return 33;
		case YOUTUBER:
			return 41;
		case COM:
			return 50;
		case DIAMOND_MEMBER:
			return 36;
		case PLATINUM_MEMBER:
			return 37;
		case GOLD_MEMBER:
			return 38;
		case SILVER_MEMBER:
			return 39;
		case BRONZE_MEMBER:
			return 40;
		case PVP:
			return 9;
		case HARDCORE_IRON_MAN:
			return 10;
		case ULTIMATE_IRON_MAN:
			return 3;
		case IRON_MAN:
			return 2;
		default:
			break;
		}
		return -1;
	}

	public static Optional<Integer> forSkillName(String name) {
		List<String> list = Arrays.asList(Skills.SKILL_NAME);
		if (list.contains(name)) {
			return Optional.of(list.indexOf(name));
		}
		return Optional.empty();
	}

	public static int getDistance(Location loc1, Location loc2) {
		int deltaX = loc2.getX() - loc1.getX();
		int deltaY = loc2.getY() - loc1.getY();
		return ((int) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)));
	}

	public static String formatCurrency(int amount) {
		String shopAdd = "";
		if (amount >= 1000 && amount < 1000000)
			shopAdd = (amount / 1000) + "K";
		else if (amount >= 1000000)
			shopAdd = (amount / 1000000) + " million";
		else
			shopAdd = amount + "";
		return shopAdd;
	}

	public static String upperFirst(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	public static String withPrefix(String string) {
		String prefix = "a ";
		if (string.startsWith("a") || string.startsWith("e") || string.startsWith("i") || string.startsWith("o")
				|| string.startsWith("u"))
			prefix = "an ";
		// We don't use prefixes for items like arrows
		if (string.endsWith("s"))
			prefix = "";
		return prefix + string;
	}

	/**
	 * Handles vote reward claiming!
	 * 
	 * @param player
	 *            the player claiming the reward
	 */
	public static void handleVoteReward(Player player) {
		player.getActionSender().sendDialogue("Salve-PS Advisor", DialogueType.NPC, 276,
				FacialAnimation.DISTRESSED_CONTINUED, "Please wait, checking for rewards...");
		new Thread() {
			public void run() {
				try {

					String request = Vote.validate(Constants.EVERYTHINGRS_KEY, player.getName(), 1);

					String[][] errorMessage = {

							{ "error_invalid", "There was an error processing your request." },
							{ "error_non_existent_server", "This server is not registered at EverythingRS." },
							{ "error_invalid_reward", "The reward you're trying to claim doesn't exist." },
							{ "error_non_existant_rewards", "This server does not have any rewards set up yet." },
							{ "error_non_existant_player", "You haven't voted yet; do ::vote to start!" },
							{ "not_enough", "You have no vote points left to claim!" } };

					for (String[] message : errorMessage) {
						if (request.equalsIgnoreCase(message[0])) {
							player.getActionSender().sendDialogue("Salve-PS Advisor", DialogueType.NPC, 276,
									FacialAnimation.ALMOST_CRYING, message[1]);
							return;
						}
					}
					int amt = 0;
					outer: while (request.startsWith("complete")) {
						player.setAttribute("busy", true);
						amt++;
						request = Vote.validate(Constants.EVERYTHINGRS_KEY, player.getName(), 1);
						World.getWorld().increaseVotes(1);
						continue outer;
					}
					// 1 vote point = 15 minutes
					int expSecs = amt * 60 * 15;
					player.getDatabaseEntity().setVotePoints(player.getDatabaseEntity().getVotePoints() + amt);
					player.getDatabaseEntity().setDoubleExp(player.getDatabaseEntity().getDoubleExp() + expSecs);
					player.getActionSender().sendDialogue("Salve-PS Advisor", DialogueType.NPC, 276,
							FacialAnimation.HAPPY,
							"Sucess! You've received " + Misc.formatNumber(amt) + " vote points and "
									+ " boosted experience for " + secondsToMinutes(expSecs)
									+ "; you now have a total of: "
									+ Misc.formatNumber(player.getDatabaseEntity().getVotePoints())
									+ " vote points; thank you for voting!");
					player.removeAttribute("busy");

				} catch (Exception e) {
					player.getActionSender().sendDialogue("Salve-PS Advisor", DialogueType.NPC, 276,
							FacialAnimation.ALMOST_CRYING,
							"Uh-oh! I can't seem to communicate with our website; please try again later!");
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * Formats an Integer.
	 * 
	 * @param number
	 *            the integer to format.
	 * @return the formatted integer as string.
	 */
	public static String formatNumber(int number) {
		String formattedNumber = NumberFormat.getInstance(Locale.ENGLISH).format(number);
		return formattedNumber;
	}

	/**
	 * Returns the correct suffix for the last digit (1st, 2nd, .. , 13th, .. ,
	 * 23rd)
	 */
	public static String getLastDigitSufix(int number) {
		switch ((number < 20) ? number : number % 10) {
		case 1:
			return "st";
		case 2:
			return "nd";
		case 3:
			return "rd";
		default:
			return "th";
		}
	}

	/**
	 * Gets the current systems time.
	 * 
	 * @param dateFormat
	 *            The format to use.
	 * @return The formatted time.
	 */
	public static String time(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	public static String secondsToMinutes(int seconds) {
		int sec = seconds % 60;
		int min = (seconds / 60) % 60;
		int hours = (seconds / 60) / 60;
		return hours + "h : " + min + "m : " + sec + "s";
	}
}