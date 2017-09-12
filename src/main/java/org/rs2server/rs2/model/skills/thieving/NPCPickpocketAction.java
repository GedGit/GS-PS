package org.rs2server.rs2.model.skills.thieving;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.action.impl.SkillAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.ThievingAction.PickpocketableNPC;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.tickable.Tickable;

public class NPCPickpocketAction extends SkillAction {

	/**
	 * The animation to execute.
	 */
	private static final Animation STUN_ANIMATION = Animation.create(422);

	/**
	 * The animation to execute.
	 */
	private static final Animation PICKPOCKETING_ANIMATION = Animation.create(881);

	/**
	 * The NPC to pickpocket from.
	 */
	private final NPC npc;

	/**
	 * The data for the npc to be pickpocketed.
	 */
	private final PickpocketableNPC npcData;

	/**
	 * If this pickpocket is succesful.
	 */
	private boolean succesful = true;

	/**
	 * The message to send.
	 */
	private String message;

	/**
	 * The slot to use in the levels required arrays.
	 */
	private byte slot = 0;

	/**
	 * The amount of ticks left.
	 */
	private byte ticks = 2;

	/**
	 * Constructs a new {@code NPCPickpocketAction} {@code Object}.
	 *
	 * @param player
	 *            The player.
	 * @param npc
	 *            The npc.
	 * @param npcData
	 *            The pickpocketing data for the npc.
	 */
	public NPCPickpocketAction(Player player, NPC npc, PickpocketableNPC npcData) {
		super(player);
		this.npc = npc;
		this.npcData = npcData;
	}

	@Override
	public boolean commence(Player player) {
		if (player.getSkills().getLevel(Skills.THIEVING) < npcData.getThievingLevels()[0]) {
			player.getActionSender().sendMessage(
					"You need a Thieving level of " + npcData.getThievingLevels()[0] + " to steal this npc's pocket.");
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
		player.face(npc.getLocation());
		int thievingLevel = player.getSkills().getLevel(Skills.THIEVING);
		int agilityLevel = player.getSkills().getLevel(Skills.AGILITY);
		if (RANDOM.nextInt((thievingLevel + agilityLevel) / 2) > RANDOM.nextInt(npcData.getThievingLevels()[0] + 65))
			slot = (byte) RANDOM.nextInt(npcData.getThievingLevels().length);
		while (npcData.getThievingLevels()[slot] > thievingLevel || npcData.getAgilityLevels()[slot] > agilityLevel)
			slot--;
		player.getActionSender()
				.sendMessage("You attempt to pick the " + npc.getDefinition().getName().toLowerCase() + "'s pocket...");
		message = getMessage(player);
		int increasedChance = getIncreasedChance(player);
		int level = RANDOM.nextInt(thievingLevel + increasedChance) + 1;
		double ratio = (double) level / (double) (RANDOM.nextInt(npcData.getThievingLevels()[0] + 5) + 1);
		if (Math.round(ratio * thievingLevel) < npcData.getThievingLevels()[0]) {
			succesful = false;
			message = "You fail to pick the " + npc.getDefinition().getName().toLowerCase() + "'s pocket.";
		}
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
		if (player.getEquipment().getSlot(Equipment.SLOT_GLOVES) == 10075)
			chance += 12;
		// Ardougne cloaks, thieving skillcape, max capes
		if (player.getEquipment().containsOneItem(13122, 13123, 13124, 20760, 9777, 9778)
				|| Constants.hasMaxCape(player))
			chance += 10;
		return chance;
	}

	@Override
	public boolean execute(Player player) {
		return --ticks == 0;
	}

	@Override
	public boolean finish(final Player player) {
		player.getActionSender().sendMessage(message);
		player.setAttribute("cantMove", false);
		player.setAttribute("isStealing", false);
		if (!succesful) {
			npc.face(player.getLocation());
			npc.playAnimation(STUN_ANIMATION);
			player.playAnimation(player.getDefendAnimation());
			World.getWorld().submit(new Tickable(1) {
				@Override
				public void execute() {
					player.getUpdateFlags().flag(UpdateFlag.HIT);
					player.inflictDamage(new Hit(1), player);
					// player.stun(npcData.getStunTime(), "You have been
					// stunned!", true);
					this.stop();
				}
			});
			if (npcData.equals(PickpocketableNPC.MASTER_FARMER) || npcData.equals(PickpocketableNPC.FARMER)) {
				npc.forceChat("Cor blimey mate, what are ye doing in me pockets?");
				return true;
			}
			npc.forceChat("What do you think you're doing?");
			return true;
		}
		player.getSkills().addExperience(Skills.THIEVING, npcData.getExperience());
		npcData.handlePet(player, npcData.getPetRate());
		for (int i = -1; i < slot; i++) {
			Item item = npcData.getLoot()[RANDOM.nextInt(npcData.getLoot().length)];
			player.getInventory().add(item);
		}
		return true;
	}

	/**
	 * Gets the message to send when finishing.
	 *
	 * @param player
	 *            The player.
	 * @return The message.
	 */
	private String getMessage(Player player) {
		player.playAnimation(PICKPOCKETING_ANIMATION);
		return "You succesfully pick the " + npc.getDefinition().getName().toLowerCase() + "'s pocket.";
	}
}