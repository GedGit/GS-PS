package org.rs2server.rs2.domain.service.api.skill.farming.action;

import org.rs2server.Server;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.action.impl.InfiniteHarvestingAction;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPatchState;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPatchType;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingService;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skill;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.npc.Pet;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.Misc;

/**
 * Picking a plantable.
 *
 * @author tommo
 */
public class FarmingHarvestingAction extends InfiniteHarvestingAction {

	private static final Item ITEM_SPADE = new Item(952, 1);

	private final FarmingService farmingService = Server.getInjector().getInstance(FarmingService.class);
	private final FarmingPatchState patch;

	public FarmingHarvestingAction(Mob mob, FarmingPatchState patch) {
		super(mob);
		this.patch = patch;
	}

	@Override
	public void onSuccessfulHarvest(Item item) {
		final Player player = (Player) getMob();
		if (Misc.random(100) < 5) {
			// 5% yield increase while wearing a farming/max - capes
			if (player.getEquipment().containsOneItem(9810, 9811) || Constants.hasMaxCape(player))
				return;
		}
		if (Misc.random(100) > 94) {
			// 5% yield increase while wearing magic secateurs
			if (player.getEquipment().contains(7409))
				return;
		}
		patch.setYield(patch.getYield() - 1);
	}

	@Override
	public int getCycleCount() {
		return 1;
	}

	@Override
	public Item getReward() {
		final Player player = (Player) getMob();

		// TODO correct message and chopping part

		if (patch.getPatch().getType() == FarmingPatchType.TREE_PATCH) {
			getMob().getSkills().addExperience(Skills.FARMING, patch.getPlanted().getExperience());

			farmingService.clearPatch(player, patch);
			player.sendMessage("Your tree was healthy; you've gained some Farming experience.");
			handlePet(player);
			return null;
		}
		handlePet(player);
		return new Item(patch.getPlanted().getReward(), 1);
	}

	@Override
	public int getSkill() {
		return Skill.FARMING.getId();
	}

	@Override
	public int getRequiredLevel() {
		return 0;
	}

	@Override
	public double getExperience() {
		return patch.getPlanted() != null ? patch.getPlanted().getExperience() : 0;
	}

	@Override
	public String getLevelTooLowMessage() {
		return null;
	}

	@Override
	public String getHarvestStartedMessage() {
		return "You begin to harvest the " + patch.getPatch().getType().toString() + ".";
	}

	@Override
	public String getSuccessfulHarvestMessage() {
		return null;
	}

	@Override
	public String getInventoryFullMessage() {
		return "Your inventory is full.";
	}

	@Override
	public Animation getAnimation() {
		return patch.getPatch().getType().getYieldAnimation();
	}

	@Override
	public boolean canHarvest() {
		if (patch.getYield() <= 0) {
			final Player player = (Player) getMob();
			farmingService.clearPatch(player, patch);
			player.playAnimation(Animation.create(-1));
			return false;
		}

		if (patch.getPatch().getType() == FarmingPatchType.ALLOTMENT) {
			if (!getMob().getInventory().hasItem(ITEM_SPADE)) {
				getMob().getActionSender().sendMessage("You need a spade to do that.");
				return false;
			}
		}
		return true;
	}

	/**
	 * Handles the rare chance of obtaining a skilling pet
	 * 
	 * @param player
	 *            the player
	 * @param dropRate
	 *            the chance
	 */
	private void handlePet(Player player) {
		int dropRate = 10000 - (player.getSkills().getLevelForExperience(Skills.FARMING) * 25);
		int random = Misc.random(dropRate);
		if (random == 3000) {
			Pet.Pets pets = Pet.Pets.TANGLEROOT;
			Pet.givePet(player, new Item(pets.getItem()));
		}
	}
}
