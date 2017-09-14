package org.rs2server.rs2.content;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.MutableDateTime;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.Misc;
import org.rs2server.util.XMLController;

public class TimedPunishment {

	private Player player;
	private DateTime punishmentStart;
	private DateTime punishmentEnd;
	private PunishmentType type;

	public enum PunishmentType {

		BAN(0), MUTE(1);

		private int value;

		PunishmentType(int value) {
			this.value = value;
		}

		public int toInteger() {
			return value;
		}

		public static PunishmentType getType(int value) {
			return value == 0 ? BAN : MUTE;
		}
	}

	public TimedPunishment(Player player) {
		this.player = player;
	}

	public void punish(PunishmentType type, DateTime toPunish) {
		if (type == PunishmentType.MUTE) {
			setPunishmentStart(DateTime.now());
			setPunishmentEnd(toPunish);
			player.getSettings().setMuted(true);
			player.getActionSender().sendMessage("You have been muted for: "
					+ Days.daysBetween(getPunishmentStart(), getPunishmentEnd()).getDays() + " days and "
					+ Hours.hoursBetween(getPunishmentStart(), getPunishmentEnd()).getHours() % 24 + " hours and "
					+ Minutes.minutesBetween(getPunishmentStart(), getPunishmentEnd()).getMinutes() % 60 + " minutes");
		} else if (type == PunishmentType.BAN) {
			setPunishmentStart(DateTime.now());
			setPunishmentEnd(toPunish);
			player.getActionSender().sendLogout();
		}
		setPunishmentType(type);
		player.setPunished(true);
	}

	public void setPunishmentStart(DateTime date) {
		this.punishmentStart = date;
	}

	public DateTime getPunishmentStart() {
		return punishmentStart;
	}

	public void setPunishmentEnd(DateTime date) {
		this.punishmentEnd = date;
	}

	public DateTime getPunishmentEnd() {
		return punishmentEnd;
	}

	public void setPunishmentType(PunishmentType type) {
		this.type = type;
	}

	public PunishmentType getPunishmentType() {
		return type;
	}

	/**
	 * Handles timed muting.
	 * 
	 * @param punisher
	 *            the player punishing someone.
	 * @param victim
	 *            the player being punished.
	 * @param days
	 *            days to add mute for
	 * @param hours
	 *            hours to add mute for
	 * @param minutes
	 *            minutes to add mute for
	 */
	public static void handleMute(Player punisher, Player victim, int days, int hours, int minutes) {
		if (victim.getSettings().isMuted() && victim.isPunished()) {
			punisher.getActionSender().sendMessage("This user is already muted.");
			punisher.getActionSender().sendMessage(
					"Their mute will expire on " + victim.getPunishment().getPunishmentEnd().toDate() + ".");
			return;
		}

		DateTime dt = new DateTime();

		MutableDateTime mdt = dt.toMutableDateTime();
		mdt.addMinutes(minutes);
		mdt.addHours(hours);
		mdt.addDays(days);
		DateTime result = mdt.toDateTime();

		victim.getPunishment().punish(PunishmentType.MUTE, result);

		String message = victim.getName() + " has been muted until ";
		message += result.getHourOfDay() + ":" + result.getMinuteOfHour() + ", ";
		message += result.dayOfWeek().getAsText() + ", ";
		message += "the " + result.dayOfMonth().get() + Misc.getLastDigitSufix(result.dayOfMonth().get()) + " ";
		message += "of " + result.monthOfYear().getAsText() + ", " + result.getYear() + ".";
		punisher.sendMessage(message);

		File file = new File("data/punishments/mutedUsers.xml");
		List<String> mutedUsers = null;
		try {
			mutedUsers = XMLController.readXML(file);
			XMLController.writeXML(mutedUsers, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mutedUsers.add(victim.getName());
		return;
	}

	/**
	 * Handles timed banning.
	 * 
	 * @param punisher
	 *            the player punishing someone.
	 * @param victim
	 *            the player being punished.
	 * @param days
	 *            days to add mute for
	 * @param hours
	 *            hours to add mute for
	 * @param minutes
	 *            minutes to add mute for
	 */
	public static void handleBan(Player punisher, Player victim, int days, int hours, int minutes) {
		List<String> bannedUsers;
		try {
			bannedUsers = XMLController.readXML(new File("data/punishments/bannedUsers.xml"));
			for (String bannedName : bannedUsers) {
				if (bannedName.equalsIgnoreCase(victim.getName())) {
					punisher.sendMessage("This user is already banned.");
					punisher.getActionSender().sendMessage(
							"Their ban will expire on " + victim.getPunishment().getPunishmentEnd().toDate() + ".");
					return;
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		DateTime dt = new DateTime();

		MutableDateTime mdt = dt.toMutableDateTime();
		mdt.addMinutes(minutes);
		mdt.addHours(hours);
		mdt.addDays(days);
		DateTime result = mdt.toDateTime();

		victim.getPunishment().punish(PunishmentType.BAN, result);

		String message = victim.getName() + " has been banned until ";
		message += result.hourOfDay().getAsText() + ":" + result.getMinuteOfHour() + ", ";
		message += result.dayOfWeek().getAsText() + ", ";
		message += "the " + result.dayOfMonth().get() + Misc.getLastDigitSufix(result.dayOfMonth().get()) + " ";
		message += "of " + result.monthOfYear().getAsText() + ", " + result.getYear() + ".";
		punisher.sendMessage(message);

		File file = new File("data/punishments/bannedUsers.xml");
		bannedUsers = null;
		try {
			XMLController.writeXML(bannedUsers, file);
			bannedUsers = XMLController.readXML(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bannedUsers.add(victim.getName());
		return;
	}
}
