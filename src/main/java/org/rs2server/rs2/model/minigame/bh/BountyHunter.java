package org.rs2server.rs2.model.minigame.bh;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.util.XMLController;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * 
 * @author 'Mystic Flow
 */
public class BountyHunter {

	public static final BountyHunter INSTANCE = new BountyHunter();

	public Comparator<String> comparator = new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			int score1 = Integer.parseInt(o1.split(":")[1]);
			int score2 = Integer.parseInt(o2.split(":")[1]);
			if (score1 > score2) {
				return -1;
			} else if (score1 < score2) {
				return 1;
			} else {
				return 0;
			}
		}

	};
	public String[] hunters = new String[11], rogues = new String[11];

	public void load() {
		try {
			File hunters = new File("data/hunterscores.xml"), rogues = new File("data/roguescores.xml");
			if (hunters.exists())
				this.hunters = XMLController.readXML(hunters);
			if (rogues.exists())
				this.rogues = XMLController.readXML(rogues);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			XMLController.writeXML(hunters, new File("data/hunterscores.xml"));
			XMLController.writeXML(rogues, new File("data/roguescores.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean handleInteraction(final Player player, GameObject obj) {
		if (obj.getId() == 28122) {
			if (player.getBountyHunter() != null) {
				if (player.getBountyHunter().getLeavePenalty() > 0) {
					player.getActionSender().sendMessage("You can't leave the crater yet!");
					return true;
				}
				if (player.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
					return true;
				}
				player.setAttribute("leavingBH", Boolean.TRUE);
				player.getBountyHunter().destroy();

				for (Player playerInBh : player.getBountyHunter().getCrater().getPlayers()) {
					if (playerInBh.getBountyHunter() != null && playerInBh.getBountyHunter().getTarget() != null && playerInBh.getBountyHunter().getTarget() == player) {
						playerInBh.getBountyHunter().setTarget(null);
						playerInBh.getActionSender().sendMessage("Your target has left the crater. You shall be found a new target.");
					}
				}
				player.setBountyHunter(null);
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.face(Location.create(3164, 3685, 0).transform(0, -1, 0));
				player.bountyDelay = 30;
				return true;
			}
		} else for (final BountyHunterCrater crater : BountyHunterCrater.values()) {
			if (obj.getId() == crater.getObjectId()) {
				if (player.getSkills().getCombatLevel() >= crater.getRequiredLevel()) {
					if (player.bountyDelay > 0) {
						player.getActionSender().sendDialogue("", DialogueType.MESSAGE, -1, null, "You must wait " + player.bountyDelay + " seconds before re-entering.");
						return true;
					}
					for (int i = 0; i < 28; i++) {
						Item item = player.getInventory().get(i);
						if (item != null) {
							if (item.getDefinition().isNoted()) {
								player.getActionSender().sendDialogue("", DialogueType.MESSAGE, -1, null, "You can't bring noted items into the crater.");
								return true;
							} else if (item.getDefinition().isStackable()) {
								if (item.getCount() >= 1000) {
									player.getActionSender().sendDialogue("", DialogueType.MESSAGE, -1, null, "You can't bring 1000 or more of the same item into the crater.");
									return true;
								}
							}
						}
					}
					for (int i = 0; i < Equipment.SIZE; i++) {
						Item item = player.getEquipment().get(i);
						if (item != null) {
							if (item.getDefinition().isStackable()) {
								if (item.getCount() >= 1000) {
									player.getActionSender().sendDialogue("", DialogueType.MESSAGE, -1, null, "You can't bring 1000 or more of the same item into the crater.");
									return true;
								}
							}
						}
					}
					player.playAnimation(Animation.create(7376));
					player.setAttribute("busy", Boolean.TRUE);
					World.getWorld().submit(new Tickable(1) {
						public void execute() {
							stop();
							player.removeAttribute("busy");
							player.setBountyHunter(new BountyHunterNode(player));
							player.getBountyHunter().setCrater(crater);
							player.getBountyHunter().init(true);
							player.playAnimation(Animation.create(7377));
						}
					});
					return true;
				} else {
					player.getActionSender().sendDialogue("", DialogueType.MESSAGE, -1, null, "You need a combat level of " + crater.getRequiredLevel() + " or more to enter this crater!");
					return true;
				}
			}
		}
		return false;
	}

	public void checkScoreBoard(Player player, boolean wasTarget) {
		String[] newData = wasTarget ? hunters : rogues;
		int kills = wasTarget ? player.getSettings().getHunterBHKills() : player.getSettings().getRogueBHKills();
		int existingIndex = getIndex(player, newData);
		if (existingIndex == -1) {
			newData[10] = player.getName() + ":" + kills;
		} else {
			newData[existingIndex] = player.getName() + ":" + kills;
		}
		Collections.sort(Arrays.asList(newData), comparator);
		if (wasTarget)
			hunters = newData;
		else
			rogues = newData;

	}

	private int getIndex(Player player, String[] newData) {
		for (int i = 0; i < newData.length - 1; i++) {
			String name = newData[i].split(":")[0];
			if (name.toLowerCase().equals(player.getName().toLowerCase()))
				return i;
		}
		return -1;
	}

	public void openScoreBoard(Player player, int i) {
		int index = 15;
		String[] scoreboard = i == 0 ? hunters : rogues;
		for (int idx = 0; idx < scoreboard.length - 1; idx++) {
			String string = scoreboard[idx];
			if (string != null) {
				String[] data = string.split(":");
				player.getActionSender().sendString(654 + i, index, data[0]);
				player.getActionSender().sendString(654 + i, index + 10, data[1]);
				index++;
			}
		}
		player.getActionSender().sendInterface(654 + i, false);

	}

}
