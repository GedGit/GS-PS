package org.rs2server.rs2.domain.service.impl.content;

import org.rs2server.Server;
import org.rs2server.rs2.action.impl.HarvestingAction;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.domain.service.api.skill.MiningService;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.npc.Pet;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.Misc;

import java.util.Random;

/**
 * A mining action specific to the ore veins in the Motherlode mine.
 *
 * @author tommo
 */
public class MotherlodeMiningAction extends HarvestingAction {

	private static final int MINING_EXP_PER_PAY_DIRT = 60;
	private static final int REQUIRED_MINING_LEVEL = 30;

	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	private final MiningService miningService = Server.getInjector().getInstance(MiningService.class);
	private final PlayerService playerService = Server.getInjector().getInstance(PlayerService.class);

	/**
	 * The ore vein we are mining.
	 */
	private GameObject object;

	/**
	 * The pickaxe we are using.
	 */
	private MotherlodeMinePickaxe pickaxe;

	public MotherlodeMiningAction(final Mob mob, final GameObject object) {
		super(mob);
		this.object = object;
	}

	@Override
	public void onSuccessfulHarvest(final Item item) {
		if (getMob().isPlayer()) {
			final Item randomGem = miningService.getRandomChanceGem();
			if (randomGem != null) {
				getMob().getActionSender()
						.sendMessage("You just found a " + randomGem.getDefinition2().getName() + "!");
				playerService.giveItem((Player) getMob(), randomGem, true);
			}
		}

	}

	@Override
	public Animation getAnimation() {
		return pickaxe.getAnimation();
	}

	@Override
	public int getCycleCount() {
		int level = getMob().getSkills().getLevel(Skill.MINING.getId());
		int diff = level / 10;
		int delay = (10 - diff);
		if (delay <= 2) {
			delay = 2;
		}
		if (delay > 5) {
			delay = 5;
		}
		return ((random.nextInt(delay) + 1) + random.nextInt(3));
	}

	@Override
	public double getExperience() {
		int random = Misc.random(10000);
		if (random == 1000) {
			Pet.Pets pets = Pet.Pets.ROCK_GOLEM;
			Pet.givePet((Player) getMob(), new Item(pets.getItem()));
		}
		return MINING_EXP_PER_PAY_DIRT
				* (getMob().isPlayer() ? miningService.getProspectorKitExperienceModifier((Player) getMob()) : 1);
	}

	@Override
	public GameObject getGameObject() {
		return object;
	}

	@Override
	public int getGameObjectMaxHealth() {
		return Misc.random(6, 24);
	}

	@Override
	public String getHarvestStartedMessage() {
		return "You swing your pick at the rock.";
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a " + Skills.SKILL_NAME[getSkill()] + " level of " + getRequiredLevel() + " to mine this ore.";
	}

	@Override
	public int getObjectRespawnTimer() {
		return 60;
	}

	@Override
	public GameObject getReplacementObject() {
		final GameObject object = getGameObject();
		return new GameObject(object.getLocation(), 26666, object.getType(), object.getDirection(), false);
	}

	@Override
	public int getRequiredLevel() {
		return REQUIRED_MINING_LEVEL;
	}

	@Override
	public Item getReward() {
		return new Item(12011, 1);
	}

	@Override
	public int getSkill() {
		return Skills.MINING;
	}

	@Override
	public String getSuccessfulHarvestMessage() {
		return "You manage to mine some pay-dirt.";
	}

	@Override
	public boolean canHarvest() {
		for (MotherlodeMinePickaxe pickaxe : MotherlodeMinePickaxe.values()) {
			if ((getMob().getInventory().contains(pickaxe.getId()) || getMob().getEquipment().contains(pickaxe.getId()))
					&& getMob().getSkills().getLevelForExperience(getSkill()) >= pickaxe.getRequiredLevel()) {
				this.pickaxe = pickaxe;
				break;
			}
		}
		if (pickaxe == null) {
			getMob().getActionSender().sendMessage("You do not have a pickaxe that you can use.");
			return false;
		}
		return true;
	}

	@Override
	public String getInventoryFullMessage() {
		return "Your inventory is full.";
	}

	/**
	 * Defines the pickaxes and animations specific to the Motherlode mine.
	 */
	public enum MotherlodeMinePickaxe {

		INFERNAL(13243, 61, Animation.create(4483)),

		DRAGON(11920, 61, Animation.create(6758)),

		RUNE(1275, 41, Animation.create(6752)),

		ADAMANT(1271, 31, Animation.create(6756)),

		MITHRIL(1273, 21, Animation.create(6757)),

		STEEL(1269, 6, Animation.create(6755)),

		IRON(1267, 1, Animation.create(6754)),

		BRONZE(1265, 1, Animation.create(6753));

		private int id;
		private int level;
		private Animation animation;

		MotherlodeMinePickaxe(int id, int level, Animation animation) {
			this.id = id;
			this.level = level;
			this.animation = animation;
		}

		public int getId() {
			return id;
		}

		public int getRequiredLevel() {
			return level;
		}

		public Animation getAnimation() {
			return animation;
		}
	}
}