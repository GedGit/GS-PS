package org.rs2server.rs2.domain.service.api.content.mining;

import org.rs2server.Server;
import org.rs2server.rs2.action.impl.HarvestingAction;
import org.rs2server.rs2.domain.service.api.skill.MiningService;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.Mining;

/**
 * @author Clank1337
 */
public interface GemMiningService {


	class GemMiningAction extends HarvestingAction{

		private Mining.PickAxe pickaxe;
		private final GameObject object;
		private final GemMiningService gemMiningService;

		private final MiningService miningService;

		/**
		 * Creates the harvesting action for the specified mob.
		 *
		 * @param mob The mob to create the action for.
		 */
		public GemMiningAction(Mob mob, GameObject object) {
			super(mob);
			this.object = object;
			miningService = Server.getInjector().getInstance(MiningService.class);
			gemMiningService = Server.getInjector().getInstance(GemMiningService.class);
		}

		@Override
		public Animation getAnimation() {
			return pickaxe.getAnimation();
		}

		@Override
		public int getCycleCount() {
			int skill = getMob().getSkills().getLevel(getSkill());
			int level = 40;
			int modifier = pickaxe.getRequiredLevel();
			double cycleCount = 1;
			cycleCount = Math.ceil((level * 50 - skill * 10) / modifier * 0.0625 * 4);
			if (cycleCount < 1) {
				cycleCount = 1;
			}
			return (int) cycleCount;
		}

		@Override
		public double getExperience() {
			return 65 * (getMob().isPlayer() ? miningService.getProspectorKitExperienceModifier((Player) getMob()) : 1f) * 2;
		}

		@Override
		public GameObject getGameObject() {
			return object;
		}

		@Override
		public int getGameObjectMaxHealth() {
			return 1;
		}

		@Override
		public String getHarvestStartedMessage() {
			return "You swing your pick at the rock.";
		}

		@Override
		public String getLevelTooLowMessage() {
			return "You need a " + Skills.SKILL_NAME[getSkill()] + " level of 40 to mine this rock.";
		}

		@Override
		public int getObjectRespawnTimer() {
			return 105;
		}

		@Override
		public GameObject getReplacementObject() {
			return new GameObject(getGameObject().getLocation(), 7469, getGameObject().getType(), getGameObject().getDirection(), false);
		}

		@Override
		public int getRequiredLevel() {
			return 40;
		}

		@Override
		public Item getReward() {
			return gemMiningService.getReward();
		}

		@Override
		public int getSkill() {
			return Skills.MINING;
		}

		@Override
		public String getSuccessfulHarvestMessage() {
			return "You manage to mine some " + getReward().getDefinition2().getName().toLowerCase().replaceAll(" ore", "") + ".";
		}

		@Override
		public boolean canHarvest() {
			for(Mining.PickAxe pickaxe : Mining.PickAxe.values()) {
				if((getMob().getInventory().contains(pickaxe.getId()) || getMob().getEquipment().contains(pickaxe.getId()))
						&& getMob().getSkills().getLevelForExperience(getSkill()) >= pickaxe.getRequiredLevel()) {
					this.pickaxe = pickaxe;
					break;
				}
			}
			if(pickaxe == null) {
				getMob().getActionSender().sendMessage("You do not have a pickaxe that you can use.");
				return false;
			}
			return true;
		}

		@Override
		public String getInventoryFullMessage() {
			return "Your inventory is too full to hold any more " + getReward().getDefinition2().getName().toLowerCase().replaceAll(" ore", "") + ".";
		}
	}

	Item getReward();

}
