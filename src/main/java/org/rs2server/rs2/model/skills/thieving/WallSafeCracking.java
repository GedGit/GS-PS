package org.rs2server.rs2.model.skills.thieving;

import org.rs2server.rs2.action.impl.SkillAction;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * Handles cracking of wall safes at Rogues den.
 * 
 * @author Vichy
 */
public class WallSafeCracking extends SkillAction {

	/**
	 * Initiating the game object.
	 */
	private final GameObject object;

	/**
	 * If this pickpocket is succesful.
	 */
	private boolean succesful = true;

	/**
	 * Constructs a new {@code WallSafeCracking} {@code Object}.
	 *
	 * @param player
	 *            The player.
	 * @param object
	 *            The wall safe object.
	 */
	public WallSafeCracking(Player player, GameObject object) {
		super(player);
		this.object = object;
	}

	@Override
	public boolean commence(Player player) {
		if (player.getSkills().getLevel(Skills.THIEVING) < 50) {
			player.getActionSender().sendMessage("You need a Thieving level of 50 to do this.");
			return false;
		}
		if ((boolean) player.getAttribute("stunned", false))
			return false;
		if (player.getInventory().freeSlots() < 1) {
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE, -1, null,
					"Not enough space in your inventory.");
			player.getActionSender().sendMessage("Not enough space in your inventory.");
			return false;
		}
		player.face(object.getLocation());
		player.playAnimation(Animation.create(2248));
		int thievingLevel = player.getSkills().getLevel(Skills.THIEVING);
		player.getActionSender().sendMessage("You start cracking the safe..");
		int increasedChance = getIncreasedChance(player);
		int level = RANDOM.nextInt(thievingLevel + increasedChance) + 1;
		double ratio = (double) level / (double) (RANDOM.nextInt(55) + 1);
		if (Math.round(ratio * thievingLevel) < 50)
			succesful = false;
		player.setAttribute("cantMove", true);
		player.setAttribute("isStealing", true);
		return true;
	}

	/**
	 * Gets the increased chance for succesfully pickpocketing.
	 *
	 * @param player
	 *            The player.
	 * @return The amount of increased chance.
	 */
	private int getIncreasedChance(Player player) {
		int chance = 0;
		if (player.getEquipment().getSlot(Equipment.SLOT_GLOVES) == 10075) {
			/*
			 * Gloves of silence.
			 */
			chance += 12;
		}
		return chance;
	}

	private int ticks = 2;

	@Override
	public boolean execute(Player player) {
		return --ticks == 0;
	}

	@Override
	public boolean finish(final Player player) {
		player.setAttribute("cantMove", false);
		player.setAttribute("isStealing", false);
		if (!succesful) {
			player.playAnimation(Animation.create(1113));
			World.getWorld().submit(new Tickable(1) {
				@Override
				public void execute() {
					player.getUpdateFlags().flag(UpdateFlag.HIT);
					player.inflictDamage(new Hit(Misc.random(2, 6)), player);
					player.getActionSender().sendMessage("Your finger slips and you trigger a trap!");
					player.playAnimation(Animation.create(-1));
					this.stop();
				}
			});
			return true;
		}
		player.getActionSender().sendMessage("You get some loot.");
		player.getSkills().addExperience(Skills.THIEVING, 70);
		player.getInventory().add(addItem(player));
		return true;
	}

	/**
	 * Adds an item.
	 * 
	 * @param player
	 *            the player.
	 */
	private Item addItem(Player player) {
		Item item = new Item(995, Misc.random(100, 2500));
		int random = Misc.random(1000);
		if (random < 25) // uncut diamond
			item = new Item(1617, 1);
		else if (random > 50 && random < 150) // uncut ruby
			item = new Item(1619, 1);
		else if (random > 250 && random < 500) // uncut emerald
			item = new Item(1621, 1);
		else if (random > 600 && random < 999) // uncut sapphire
			item = new Item(1623, 1);
		return item;
	}
}