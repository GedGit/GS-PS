package org.rs2server.rs2.model.skills.runecrafting;

import org.rs2server.rs2.action.impl.SkillAction;
import org.rs2server.rs2.content.dialogue.Dialogue;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.player.Player;

public class Runecrafting extends SkillAction {
	
	/**
	 * The item we are working with
	 */
	private Item item;

	private GameObject object;

	private Animation RUNECRAFTING_ANIMATION = Animation.create(791);

	private Graphic RUNECRAFTING_GRAPHIC = Graphic.create(186);

	/**
	 * 
	 * @param player
	 * @param fletchItem
	 */

	public Item getItem() {
		return item;
	}

	public GameObject getObject() {
		return object;
	}

	public Runecrafting(Player player, Item item) {
		super(player);
		this.item = item;
	}

	public Runecrafting(Player player, GameObject object) {
		super(player);
		this.object = object;
	}

	@Override
	public boolean commence(Player player) {
		if (player == null) {
			return false;
		}
		if (item != null) {
			Talisman talisman = Talisman.forId(item.getId());
			if (talisman != null) {
				return locateAlter(player, talisman);
			}
		} else {
			return true;
		}
		return false;
	}

	@Override
	public boolean execute(Player player) {
		if (player.getSkills().getLevel(Skills.RUNECRAFTING) < Talisman.getTalismanByAlter(getObject().getId())
				.getLevel()) {
			stop();
			Dialogue.message("You need a higher runecrafting level to craft "
					+ ItemDefinition.forId(Talisman.getTalismanByAlter(getObject().getId()).getRewardId()).getName()
					+ "s");
			return false;
		}
		if (!player.getInventory().contains(7936) && !player.getInventory().contains(1436)) {
			stop();
			Dialogue.message("You don't have any essence to craft.");
			return false;
		}
		if (Talisman.getTalismanByAlter(getObject().getId()).getRequirePureEss()
				&& !player.getInventory().contains(7936)) {
			stop();
			Dialogue.message("You need pure essence to craft this rune.");
			return false;
		}
		player.playAnimation(RUNECRAFTING_ANIMATION);
		player.playGraphics(RUNECRAFTING_GRAPHIC);
		return true;
	}

	@Override
	public boolean finish(Player player) {
		Talisman talisman = Talisman.getTalismanByAlter(getObject().getId());
		int amount = getAmount(player, talisman);
		int essCount = player.getInventory().getCount(getEssType(player, talisman));
		// player.sendMessage("You bind the temple's power into
		// "+ItemDefinition.forId(talisman.getRewardId()).getName()+"s");
		player.getSkills().addExperience(Skills.RUNECRAFTING, talisman.getRewardExp() * essCount);
		player.getInventory().removeAll(new Item(getEssType(player, talisman)));
		player.getInventory().add(new Item(talisman.getRewardId(), amount));
		return true;
	}

	private boolean locateAlter(Player player, Talisman talisman) {
		String mainDirection = "";
		String subDirection = "";
		if (talisman.getOutsideLocation().getY() > player.getLocation().getY()) {
			mainDirection = "north";
		} else if (talisman.getOutsideLocation().getY() < player.getLocation().getY()) {
			mainDirection = "south";
		}
		if (talisman.getOutsideLocation().getX() > player.getLocation().getX()) {
			subDirection = "east";
		} else if (talisman.getOutsideLocation().getX() < player.getLocation().getX()) {
			subDirection = "west";
		}
		if (talisman.getOutsideLocation() == player.getLocation()) {
			mainDirection = "center";
		}
		if (mainDirection != "" && subDirection != "") {
			player.getActionSender()
					.sendMessage("The talisman pulls towards the " + mainDirection + "-" + subDirection);
		} else {
			player.getActionSender().sendMessage("The talisman pulls towards the " + mainDirection + subDirection);
		}
		return false;
	}

	public static boolean handleObject(Player player, GameObject gameObject) {
		if (player.hasAttribute("teleporting") || player.hasAttribute("busy"))
			return false;
		if (Talisman.alterIds(gameObject.getId())) {
			Runecrafting runecrafting = new Runecrafting(player, gameObject);
			runecrafting.execute();
			player.submitTick("skill_action_tick", runecrafting, true);
			return true;
		}
		return false;
	}

	private int getAmount(Player player, Talisman talisman) {
		int rcLevel = player.getSkills().getLevel(Skills.RUNECRAFTING);
		int essCount = player.getInventory().getCount(getEssType(player, talisman));
		if (talisman.equals(Talisman.AIR_TALISMAN)) {
			if (rcLevel < 11) {
				return essCount;
			} else if (rcLevel >= 11 && rcLevel < 22) {
				return essCount * 2;
			} else if (rcLevel >= 22 && rcLevel < 33) {
				return essCount * 3;
			} else if (rcLevel >= 33 && rcLevel < 44) {
				return essCount * 4;
			} else if (rcLevel >= 44 && rcLevel < 55) {
				return essCount * 5;
			} else if (rcLevel >= 55 && rcLevel < 66) {
				return essCount * 6;
			} else if (rcLevel >= 66 && rcLevel < 77) {
				return essCount * 7;
			} else if (rcLevel >= 77 && rcLevel < 88) {
				return essCount * 8;
			} else if (rcLevel >= 88 && rcLevel < 99) {
				return essCount * 9;
			} else if (rcLevel >= 99) {
				return essCount * 10;
			}
		} else if (talisman.equals(Talisman.MIND_TALISMAN)) {
			if (rcLevel < 14) {
				return essCount;
			} else if (rcLevel >= 14 && rcLevel < 28) {
				return essCount * 2;
			} else if (rcLevel >= 28 && rcLevel < 42) {
				return essCount * 3;
			} else if (rcLevel >= 42 && rcLevel < 56) {
				return essCount * 4;
			} else if (rcLevel >= 56 && rcLevel < 70) {
				return essCount * 5;
			} else if (rcLevel >= 70 && rcLevel < 84) {
				return essCount * 6;
			} else if (rcLevel >= 84) {
				return essCount * 7;
			}
		} else if (talisman.equals(Talisman.WATER_TALISMAN)) {
			if (rcLevel < 19) {
				return essCount;
			} else if (rcLevel >= 19 && rcLevel < 38) {
				return essCount * 2;
			} else if (rcLevel >= 38 && rcLevel < 57) {
				return essCount * 3;
			} else if (rcLevel >= 57 && rcLevel < 76) {
				return essCount * 4;
			} else if (rcLevel >= 76 && rcLevel < 95) {
				return essCount * 5;
			} else if (rcLevel >= 95) {
				return essCount * 6;
			}
		} else if (talisman.equals(Talisman.EARTH_TALISMAN)) {
			if (rcLevel < 26) {
				return essCount;
			} else if (rcLevel >= 26 && rcLevel < 52) {
				return essCount * 2;
			} else if (rcLevel >= 52 && rcLevel < 78) {
				return essCount * 3;
			} else if (rcLevel >= 78) {
				return essCount * 4;
			}
		} else if (talisman.equals(Talisman.FIRE_TALISMAN)) {
			if (rcLevel < 35) {
				return essCount;
			} else if (rcLevel >= 35 && rcLevel < 70) {
				return essCount * 2;
			} else if (rcLevel >= 70) {
				return essCount * 3;
			}
		} else if (talisman.equals(Talisman.BODY_TALISMAN)) {
			if (rcLevel < 46) {
				return essCount;
			} else if (rcLevel >= 46 && rcLevel < 92) {
				return essCount * 2;
			} else if (rcLevel >= 92) {
				return essCount * 3;
			}
		} else if (talisman.equals(Talisman.COSMIC_TALISMAN)) {
			if (rcLevel < 59) {
				return essCount;
			} else if (rcLevel >= 59) {
				return essCount * 2;
			}
		} else if (talisman.equals(Talisman.CHAOS_TALISMAN)) {
			if (rcLevel < 74) {
				return essCount;
			} else if (rcLevel >= 74) {
				return essCount * 2;
			}
		} else if (talisman.equals(Talisman.NATURE_TALISMAN)) {
			if (rcLevel < 91) {
				return essCount;
			} else if (rcLevel >= 91) {
				return essCount * 2;
			}
		}
		return essCount;
	}

	private int getEssType(Player player, Talisman talisman) {
		if (player.getInventory().contains(7936)) {
			return 7936;
		} else if (player.getInventory().contains(1436) && !talisman.getRequirePureEss()) {
			return 1436;
		}
		return -1;
	}
}
