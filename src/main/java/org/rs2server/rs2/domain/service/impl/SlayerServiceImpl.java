package org.rs2server.rs2.domain.service.impl;

import org.rs2server.cache.format.CacheNPCDefinition;
import org.rs2server.rs2.content.dialogue.Dialogue;
import org.rs2server.rs2.content.dialogue.TalkingDialogue;
import org.rs2server.rs2.domain.model.player.PlayerSkillSlayerEntity;
import org.rs2server.rs2.domain.model.player.PlayerStatisticsEntity;
import org.rs2server.rs2.domain.service.api.PlayerStatisticsService;
import org.rs2server.rs2.domain.service.api.SlayerService;
import org.rs2server.rs2.model.bit.BitConfigBuilder;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.npc.impl.other.SuperiorSlayerEncounter;
import org.rs2server.rs2.model.npc.impl.other.SuperiorSlayerEncounter.SuperiorEncounters;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.slayer.SlayerTask;
import org.rs2server.rs2.util.Misc;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author tommo
 */
public class SlayerServiceImpl implements SlayerService {

	public static final int TASK_WIDGET_ID = 426;

	private static final int TASK_CONFIG = 1096;

	private static final int CHECK_CONFIG = 1076;

	@SuppressWarnings("unused")
	private static final int UNLOCK_BIT = 8;

	private static final int REWARD_POINT_CONFIG = 661;
	private static final int REWARD_BIT = 6;

	private static final int FIFTH_SLOT_CONFIG = 1191;
	private static final int FIFTH_SLOT = 7;

	private static final int FIRST_ROW_BIT = 24;

	private final PlayerStatisticsService statisticsService;

	@Inject
	SlayerServiceImpl(final PlayerStatisticsService statisticsService) {
		this.statisticsService = statisticsService;
	}

	@Nonnull
	@Override
	public SlayerTask assignTask(@Nonnull Player player, @Nonnull SlayerTask.Master master) {
		if (player.getSkills().getCombatLevel() < master.getCombatRequired()) {

			Dialogue dialogue = TalkingDialogue.npcSaying(master.getId(),
					CacheNPCDefinition.get(master.getId()).getName(), Animation.FacialAnimation.SAD,
					"You are not strong enough to select this Slayer task difficulty..");

			dialogue.open(player, 0);

			player.sendMessage("Your combat level is too low to select this difficulty; needed: "
					+ master.getCombatRequired() + ".");
			return null;
		}
		int iterations = 0;
		while (true) {
			// Hacky solution to solve a stack overflow where the player has too
			// many blocked tasks which leaves no more available tasks since
			// their
			// combat level is too low. Keep in mind that to have enough points
			// to block several tasks, this is impossible.
			iterations++;
			if (iterations >= 20) {
				Dialogue dialogue = TalkingDialogue.npcSaying(master.getId(),
						CacheNPCDefinition.get(master.getId()).getName(), Animation.FacialAnimation.SAD,
						"You are not strong enough to receive a boss task..");

				dialogue.open(player, 0);
				break;
			}

			final int random = player.getRandom().nextInt(master.getData().length);
			final SlayerTask.TaskGroup group = (SlayerTask.TaskGroup) master.getData()[random][5];
			final int requiredLevel = (Integer) master.getData()[random][1];

			if (isTaskGroupBlocked(player, group))
				continue;
			else if (player.getSkills().getLevel(Skills.SLAYER) < requiredLevel)
				continue;
			else if (random == 0 && !player.getRandom().nextBoolean())
				continue;

			int minimum = (Integer) master.getData()[random][2];
			int maximum = (Integer) master.getData()[random][3];

			if (extendTask(player, group)) {
				minimum += 50;
				maximum += 50;
			}

			int amount = Misc.random(minimum, maximum);

			final SlayerTask task = new SlayerTask(master, random, (int) (amount * 0.75));
			player.getSlayer().setSlayerTask(task);
			return task;
		}

		return null;
	}

	private boolean extendTask(Player player, SlayerTask.TaskGroup group) {
		PlayerSkillSlayerEntity slayer = player.getDatabaseEntity().getSlayerSkill();
		if (group == SlayerTask.TaskGroup.ABYSSAL_DEMONS && slayer.isExtendTaskAbyssalDemon()) {
			return true;
		}
		if (group == SlayerTask.TaskGroup.DARK_BEASTS && slayer.isExtendTaskDarkBeast()) {
			return true;
		}
		if (group == SlayerTask.TaskGroup.CAVE_KRAKEN && slayer.isExtendTaskCaveKraken()) {
			return true;
		}
		if (group == SlayerTask.TaskGroup.BRONZE_DRAGONS || group == SlayerTask.TaskGroup.IRON_DRAGONS
				|| group == SlayerTask.TaskGroup.STEEL_DRAGONS && slayer.isExtendTaskMetalDragons()) {
			return true;
		}
		if (group == SlayerTask.TaskGroup.BLACK_DRAGONS && slayer.isExtendTaskBlackDragon()) {
			return true;
		}
		if (group == SlayerTask.TaskGroup.GREATER_DEMONS && slayer.isExtendTaskGreaterDemon()) {
			return true;
		}
		if (group == SlayerTask.TaskGroup.BLACK_DEMONS && slayer.isExtendTaskBlackDemon()) {
			return true;
		}
		if (group == SlayerTask.TaskGroup.CAVE_HORRORS && slayer.isExtendTaskCaveHorror()) {
			return true;
		}
		if (group == SlayerTask.TaskGroup.SKELETAL_WYVERN && slayer.isExtendTaskSkeletalWyvern()) {
			return true;
		}
		if (group == SlayerTask.TaskGroup.GARGOYLES && slayer.isExtendTaskGargoyle()) {
			return true;
		}
		if (group == SlayerTask.TaskGroup.NECHRYAEL && slayer.isExtendTaskNechryael()) {
			return true;
		}
		return false;
	}

	@Override
	public void onTaskKill(@Nonnull Player player, @Nonnull NPC npc) {
		final SlayerTask task = player.getSlayer().getSlayerTask();
		double exp = task.getXPAmount();

		// Increase slayer experience when wearing morytania legs
		if (player.getEquipment().contains(13112))
			exp *= 1.025;
		if (player.getEquipment().contains(13113))
			exp *= 1.05;
		if (player.getEquipment().contains(13114))
			exp *= 1.075;
		if (player.getEquipment().contains(13115))
			exp *= 1.1;

		player.getSkills().addExperience(Skills.SLAYER, exp);
		task.decreaseAmount();
		if (task.getTaskAmount() < 1) {
			statisticsService.increaseSlayerTasksCompleted(player, 1);
			statisticsService.increaseSlayerConsecutiveTasksCompleted(player, 1);

			for (final CacheNPCDefinition npcDefinition : CacheNPCDefinition.npcs) {
				if (npcDefinition != null && npcDefinition.getName() != null) {

					String taskName = player.getSlayer().getSlayerTask().getName().toLowerCase();
					if (npc.getDefinition().getName().toLowerCase().contains(taskName)) {
						final int npcId = npcDefinition.getId();
						statisticsService.increaseSlayerMonsterKillCount(player, npcId, task.getInitialAmount());
						break;
					}
				}
			}
			rewardPlayer(player, task);
			player.getSlayer().setSlayerTask(null);
		}

		// 1 in a 250 chance to spawn a superior encounter
		if (Misc.random(250) == 1) {
			Optional<SuperiorEncounters> encounter = SuperiorEncounters
					.ofNpcName(CacheNPCDefinition.get(npc.getId()).getName());
			// Register it
			if (encounter.isPresent()) {
				player.sendMessage("<col=ff0000>A superior foe has appeared...");
				SuperiorSlayerEncounter superiorFoe = new SuperiorSlayerEncounter(player, encounter.get().getId(),
						npc.getCentreLocation());
				superiorFoe.register();
			}
		}
	}

	@Override
	public void rewardPlayer(@Nonnull Player player, @Nonnull SlayerTask task) {
		int consecutiveTasks = player.getDatabaseEntity().getStatistics().getSlayerConsecutiveTasksCompleted();
		int rewardPoints = task.getMaster().getTaskRewardPoints();

		if (consecutiveTasks % 50 == 0) {
			rewardPoints *= 10;
		} else if (consecutiveTasks % 10 == 0) {
			rewardPoints *= 5;
		} else if (consecutiveTasks % 5 == 0) {
			rewardPoints *= 2;
		}

		statisticsService.increaseSlayerRewardPoints(player, rewardPoints);

		player.getActionSender().sendMessage("You've completed " + consecutiveTasks + " tasks in a row and received "
				+ rewardPoints + " points; return to a Slayer master.");
	}

	@Override
	public void sendCheckTaskMessage(@Nonnull Player player) {
		if (player.getSlayer().getSlayerTask() != null) {
			player.getActionSender()
					.sendMessage("Your current assignment is: " + player.getSlayer().getSlayerTask().getName()
							+ "; only " + player.getSlayer().getSlayerTask().getTaskAmount() + " more to go.");
		} else
			player.getActionSender().sendMessage("You currently have no task.");
	}

	@Override
	public void openRewardsScreen(@Nonnull Player player) {
		final SlayerTask.TaskGroup[] blockedTasks = getBlockedTaskGroups(player);
		int taskId = 0;
		int taskAmount = 0;
		if (player.getSlayer().getSlayerTask() != null) {
			final SlayerTask task = player.getSlayer().getSlayerTask();
			taskId = getTaskGroup(task).getId();
			taskAmount = task.getTaskAmount();
		}

		final BitConfigBuilder slayerPointConfig = rewardPointBuilder(player.getDatabaseEntity().getStatistics())
				.set(blockedTasks[0] != null ? blockedTasks[0].getId() : 0, FIRST_ROW_BIT);

		player.sendBitConfig(slayerPointConfig.build());
		player.sendBitConfig(blockedTaskConfig(blockedTasks).build());
		player.sendBitConfig(rewardUnlockBuilder(player.getDatabaseEntity().getSlayerSkill()).build());

		player.sendBitConfig(fifthSlotBuilder().build());

		player.getActionSender().sendAccessMask(1052, TASK_WIDGET_ID, 23, 0, 3)
				.sendAccessMask(2, TASK_WIDGET_ID, 8, 0, 37).sendConfig(262, taskId).sendConfig(261, taskAmount)
				.sendConfig(101, 1000)// Set quest points so rows are 'Empty'.
				.sendInterface(TASK_WIDGET_ID, false);
	}

	@Override
	public BitConfigBuilder rewardPointBuilder(@Nonnull PlayerStatisticsEntity statistics) {
		return BitConfigBuilder.of(REWARD_POINT_CONFIG).set(statistics.getSlayerRewardPoints(), REWARD_BIT);
	}

	@Override
	public BitConfigBuilder fifthSlotBuilder() {
		return BitConfigBuilder.of(FIFTH_SLOT_CONFIG).set(FIFTH_SLOT, FIFTH_SLOT);
	}

	@Override
	public BitConfigBuilder blockedTaskConfig(@Nonnull final SlayerTask.TaskGroup[] blockedTasks) {
		BitConfigBuilder taskBuilder = BitConfigBuilder.of(TASK_CONFIG);
		for (int i = 1; i < 5; i++)
			taskBuilder.set(blockedTasks[i] != null ? blockedTasks[i].getId() : 0, i << 3);
		return taskBuilder;
	}

	@Override
	public BitConfigBuilder unlockBuilder(@Nonnull PlayerStatisticsEntity statistics) {
		BitConfigBuilder unlockBuilder = BitConfigBuilder.of(CHECK_CONFIG);

		for (int i = 0; i < 11; i++)
			unlockBuilder.set(i, 4);

		return unlockBuilder;
	}

	private BitConfigBuilder rewardUnlockBuilder(@Nonnull final PlayerSkillSlayerEntity slayer) {
		return BitConfigBuilder.of(CHECK_CONFIG).set(slayer.isUnlockedSlayerHelm() ? 32 : 0)
				.set(slayer.isExtendTaskDarkBeast() ? 16 : 0).set(slayer.isExtendTaskAbyssalDemon() ? 8192 : 0)
				.set(slayer.isExtendTaskMetalDragons() ? 2048 : 0).set(slayer.isExtendTaskCaveKraken() ? 536870912 : 0)
				.set(slayer.isExtendTaskBlackDemon() ? 16384 : 0).set(slayer.isExtendTaskGreaterDemon() ? 32768 : 0)
				.set(slayer.isExtendTaskCaveHorror() ? 16777216 : 0)
				.set(slayer.isExtendTaskSkeletalWyvern() ? 67108864 : 0)
				.set(slayer.isExtendTaskGargoyle() ? 134217728 : 0).set(slayer.isExtendTaskNechryael() ? 268435456 : 0)
				.set(slayer.isExtendTaskBlackDragon() ? 1024 : 0);
	}

	@Override
	public void onRubSlayerRing(@Nonnull Player player) {
		DialogueManager.openDialogue(player, 1353);
	}

	@Override
	public SlayerTask.TaskGroup getTaskGroup(SlayerTask task) {
		return (SlayerTask.TaskGroup) task.getMaster().getData()[task.getTaskId()][5];
	}

	@Override
	public boolean isTaskGroupBlocked(@Nonnull Player player, @Nonnull SlayerTask.TaskGroup group) {
		return Arrays.stream(getBlockedTaskGroups(player)).filter(Objects::nonNull).filter(g -> g.equals(group))
				.findFirst().isPresent();
	}

	@Nonnull
	@Override
	public SlayerTask.TaskGroup[] getBlockedTaskGroups(@Nonnull Player player) {
		final PlayerSkillSlayerEntity slayer = player.getDatabaseEntity().getSlayerSkill();
		final SlayerTask.TaskGroup[] blockedGroups = new SlayerTask.TaskGroup[5];

		blockedGroups[0] = SlayerTask.TaskGroup.forName(slayer.getBlockedTask1());
		blockedGroups[1] = SlayerTask.TaskGroup.forName(slayer.getBlockedTask2());
		blockedGroups[2] = SlayerTask.TaskGroup.forName(slayer.getBlockedTask3());
		blockedGroups[3] = SlayerTask.TaskGroup.forName(slayer.getBlockedTask4());
		blockedGroups[4] = SlayerTask.TaskGroup.forName(slayer.getBlockedTask5());

		return blockedGroups;
	}

	@Override
	public void cancelTask(@Nonnull final Player player, boolean deductPoints) {
		int points = player.getSkills().getLevel(Skills.SLAYER) < 50 ? 10
				: player.getSkills().getLevelForExperience(Skills.SLAYER) < 75 ? 20 : 30;
		if (player.isBronzeMember())
			points -= 5;
		if (player.isSilverMember())
			points -= 5;
		if (player.isGoldMember())
			points -= 5;
		if (player.isPlatinumMember())
			points -= 5;
		if (player.isDiamondMember())
			points -= 5;
		if (points < 1)
			points = 0;
		if (deductPoints && player.getDatabaseEntity().getStatistics().getSlayerRewardPoints() < points) {
			player.sendMessage("You do not have enough points to reset your task.");
			return;
		}
		player.sendMessage("Your assignment has been reset; talk to the Slayer master for a new one.");
		player.getSlayer().setSlayerTask(null);
		if (deductPoints)
			player.getDatabaseEntity().getStatistics()
					.setSlayerRewardPoints(player.getDatabaseEntity().getStatistics().getSlayerRewardPoints() - points);
	}

	@Override
	public void blockTask(@Nonnull Player player) {
		if (player.getSlayer().getSlayerTask() == null)
			return;
		else if (player.getDatabaseEntity().getStatistics().getSlayerRewardPoints() < 100) {
			player.sendMessage("You do not have enough Slayer points to block your current task.");
			return;
		}

		final PlayerSkillSlayerEntity slayer = player.getDatabaseEntity().getSlayerSkill();
		final SlayerTask.TaskGroup group = getTaskGroup(player.getSlayer().getSlayerTask());

		boolean blocked = false;
		if (slayer.getBlockedTask1() == null) {
			slayer.setBlockedTask1(group.name());
			blocked = true;
		} else if (slayer.getBlockedTask2() == null) {
			slayer.setBlockedTask2(group.name());
			blocked = true;
		} else if (slayer.getBlockedTask3() == null) {
			slayer.setBlockedTask3(group.name());
			blocked = true;
		} else if (slayer.getBlockedTask4() == null) {
			slayer.setBlockedTask4(group.name());
			blocked = true;
		} else if (slayer.getBlockedTask5() == null) {
			slayer.setBlockedTask5(group.name());
			blocked = true;
		}

		if (blocked) {
			player.getDatabaseEntity().getStatistics()
					.setSlayerRewardPoints(player.getDatabaseEntity().getStatistics().getSlayerRewardPoints() - 100);
			cancelTask(player, false);
		}
	}

	@Override
	public void unblockTask(@Nonnull Player player, int index) {
		final PlayerSkillSlayerEntity slayer = player.getDatabaseEntity().getSlayerSkill();
		if (index == 0) {
			slayer.setBlockedTask1(null);
		} else if (index == 1) {
			slayer.setBlockedTask2(null);
		} else if (index == 2) {
			slayer.setBlockedTask3(null);
		} else if (index == 3) {
			slayer.setBlockedTask4(null);
		} else if (index == 4) {
			slayer.setBlockedTask5(null);
		}
		player.sendMessage("Your blocked task has been unblocked; no Slayer points have been returned.");
	}
}