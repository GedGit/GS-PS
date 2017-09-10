package org.rs2server.rs2.content;

import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.mysql.impl.NewsManager;
import org.rs2server.rs2.util.Misc;

public class LevelUp {

	private static Graphic levelUp = Graphic.create(199, 2);

	/**
	 * Called when a player levels up.
	 * 
	 * @param player
	 *            The player.
	 * @param skill
	 *            The skill id.
	 */
	public static void levelUp(Player player, int skill) {
		player.setAttribute("leveledUp", skill);
		player.setAttribute("leveledUp[" + skill + "]", Boolean.TRUE);
		player.playGraphics(levelUp);
		DialogueManager.openDialogue(player, skill + 100);
		String skillName = Skills.SKILL_NAME[skill];
		player.getActionSender()
				.sendMessage("Congratulations, you've just advanced " + Misc.withPrefix(skillName) + " level.");
		if (player.getSkills().getLevelForExperience(skill) == 99) {
			World.getWorld().sendWorldMessage("<img=30><col=A85011> News: " + player.getName()
					+ " has just achieved 99 " + skillName + player.gameModeName());

			new Thread(new NewsManager(player, "<img src='../assets/img/skill_icons/" + Skills.SKILL_NAME[skill]
					+ "-icon.png' width=13>achieved 99 " + skillName + player.gameModeName())).start();
		}
	}

	/**
	 * Sends 104m experience world message.
	 * 
	 * @param player
	 *            the player achieving exp amount
	 * @param skill
	 *            the skill that has been achieved
	 */
	public static void send104m(Player player, int skill) {
		String skillName = Skills.SKILL_NAME[skill];

		World.getWorld().sendWorldMessage("<img=30><col=ff0000> News: " + player.getName() + " has just achieved 104m "
				+ skillName + " experience" + player.gameModeName());

		new Thread(
				new NewsManager(player,
						"<img src='../assets/img/skill_icons/" + Skills.SKILL_NAME[skill] + "-icon.png'"
								+ "width=13>achieved 104m " + skillName + " experience" + player.gameModeName()))
										.start();
	}

	/**
	 * Sends 104m experience world message.
	 * 
	 * @param player
	 *            the player achieving exp amount
	 * @param skill
	 *            the skill that has been achieved
	 */
	public static void send200m(Player player, int skill) {
		String skillName = Skills.SKILL_NAME[skill];

		player.getActionSender().sendMessage(
				"Congratulations, you've just achieved the maximum experience in " + Misc.withPrefix(skillName) + ".");

		World.getWorld().sendWorldMessage("<img=30><col=ff0000> News: " + player.getName() + " has just achieved 200m "
				+ skillName + " experience" + player.gameModeName());

		new Thread(
				new NewsManager(player,
						"<img src='../assets/img/skill_icons/" + Skills.SKILL_NAME[skill] + "-icon.png' "
								+ "width=13>achieved 200m " + skillName + " experience" + player.gameModeName()))
										.start();
	}

	/**
	 * Sends maximum total level world message.
	 * 
	 * @param player
	 *            the player achieving max total level
	 */
	public static void sendMax(Player player) {

		player.getActionSender().sendMessage("Congratulations, you've just achieved the maximum total level possible.");
		player.getActionSender().sendMessage("You can now claim your Max cape from the Wise old man who's located at home!");

		World.getWorld().sendWorldMessage("<img=30><col=ff0000> News: " + player.getName()
				+ " has just achieved all skills 99" + player.gameModeName());

		new Thread(new NewsManager(player, "<img src='../resources/news/max_cape.png' "
				+ "width=13>achieved all skills 99" + player.gameModeName())).start();
	}
}