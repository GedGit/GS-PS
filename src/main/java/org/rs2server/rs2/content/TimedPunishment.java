package org.rs2server.rs2.content;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.rs2server.rs2.model.player.Player;

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

}
