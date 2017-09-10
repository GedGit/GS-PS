package org.rs2server.rs2.domain.service.impl.skill;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.rs2server.rs2.content.api.GameInterfaceButtonEvent;
import org.rs2server.rs2.content.api.GameNpcActionEvent;
import org.rs2server.rs2.content.api.GameObjectActionEvent;
import org.rs2server.rs2.content.api.GamePlayerLoginEvent;
import org.rs2server.rs2.content.api.GamePlayerRegionEvent;
import org.rs2server.rs2.domain.model.player.PlayerSkillFarmingEntity;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.PermissionService;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPatch;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPatchState;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPatchTreatment;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPatchType;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPlantable;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingService;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingTool;
import org.rs2server.rs2.domain.service.api.skill.farming.action.FarmingClearingAction;
import org.rs2server.rs2.domain.service.api.skill.farming.action.FarmingCureAction;
import org.rs2server.rs2.domain.service.api.skill.farming.action.FarmingHarvestingAction;
import org.rs2server.rs2.domain.service.api.skill.farming.action.FarmingPlantingAction;
import org.rs2server.rs2.domain.service.api.skill.farming.action.FarmingRakeAction;
import org.rs2server.rs2.domain.service.api.skill.farming.action.FarmingTreatmentAction;
import org.rs2server.rs2.domain.service.api.skill.farming.action.FarmingWateringAction;
import org.rs2server.rs2.model.bit.BitConfig;
import org.rs2server.rs2.model.bit.BitConfigBuilder;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Skill;
import org.rs2server.rs2.model.boundary.Area;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.util.Misc;
import javax.annotation.Nonnull;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Handles everything Farming related
 * 
 * @author tommo, Vichy
 */
public class FarmingServiceImpl implements FarmingService {

	private static final Item SEED_DIBBER = new Item(5343, 1);
	private static final Item GARDENING_TROWEL = new Item(5325, 1);
	private static final Item WATERING_CAN = new Item(5340, 1);

	@Inject
	FarmingServiceImpl(final HookService hookService, PermissionService permissionService) {
		hookService.register(this);
	}

	@Subscribe
	public void onPlayerLogin(final GamePlayerLoginEvent event) {
		final Player player = event.getPlayer();
		final PlayerSkillFarmingEntity farming = player.getDatabaseEntity().getFarmingSkill();

		farming.getPatches().entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry -> {
			final FarmingPatchState patch = entry.getValue();
			updatePatch(patch);
			sendPatches(player);
		});
	}

	@Subscribe
	public void onGameObjectActionEvent(final GameObjectActionEvent event) {
		final Player player = event.getPlayer();
		final GameObject object = event.getGameObject();
		final PlayerSkillFarmingEntity farming = player.getDatabaseEntity().getFarmingSkill();

		final FarmingPatch farmingPatch = FarmingPatch.forObjectIdAndLocation(object.getId(), object.getLocation());
		if (farmingPatch == null)
			return;
		if (!farming.getPatches().containsKey(farmingPatch))
			farming.getPatches().put(farmingPatch, new FarmingPatchState(farmingPatch));

		final FarmingPatchState patchState = farming.getPatches().get(farmingPatch);

		if (event.getActionType() == GameObjectActionEvent.ActionType.OPTION_1) {
			if (isPatchFullyGrown(patchState)) {
				harvest(player, patchState, object);
			} else if (patchState.isDiseased()) {
				if (player.getInventory().contains(FarmingCureAction.ITEM_PLANT_CURE.getId())) {
					cure(player, patchState, object);
				} else {
					player.getActionSender().sendMessage("You need plant cure to cure this patch.");
				}
			} else if (patchState.isDead()) {
				clear(player, patchState, object);
			} else if (patchState.getWeedLevel() < 3) {
				rake(player, patchState, object);
			}
		} else if (event.getActionType() == GameObjectActionEvent.ActionType.OPTION_2) {
			inspect(player, patchState);
		} else if (event.getActionType() == GameObjectActionEvent.ActionType.ITEM_ON_OBJECT) {
			assert event.getItem() != null;

			// Clearing
			if (event.getItem().getId() == 952 && patchState.getWeedLevel() == 3 && patchState.getPlanted() != null) {
				clear(player, patchState, object);
			}

			// Curing
			if (event.getItem().getId() == FarmingCureAction.ITEM_PLANT_CURE.getId() && !isPatchFullyGrown(patchState)
					&& patchState.isDiseased()
					&& player.getInventory().contains(FarmingCureAction.ITEM_PLANT_CURE.getId())) {
				cure(player, patchState, object);
			}

			// Raking
			if (event.getItem().getId() == FarmingTool.RAKE.getItemId() && patchState.getWeedLevel() < 3
					&& player.getInventory().contains(FarmingTool.RAKE.getItemId())) {
				rake(player, patchState, object);
			}

			// Treating
			if (patchState.getWeedLevel() == 3 && patchState.getTreatment() == FarmingPatchTreatment.NOT_TREATED) {
				final FarmingPatchTreatment treatment = FarmingPatchTreatment.forItemId(event.getItem().getId());

				if (treatment != null) {
					treat(player, patchState, treatment, object);
				}
			}

			// Watering
			if (patchState.getPlanted() != null && !patchState.isWatered()
					&& event.getItem().getId() == WATERING_CAN.getId() && patchState.getWeedLevel() == 3) {
				if (patchState.getPatch().getType().isWaterable() && !isPatchFullyGrown(patchState)
						&& !patchState.isDiseased() && !patchState.isDead()) {
					water(player, patchState, object);
				}
			}

			// Planting
			if (patchState.getWeedLevel() == 3 && patchState.getPlanted() == null) {
				final FarmingPlantable plantable = FarmingPlantable.forSeedItemId(event.getItem().getId());

				// Check if the plantable can be planted in this patch type
				if (plantable != null && farmingPatch.getType() == plantable.getType()) {
					if (player.getSkills().getLevel(Skill.FARMING.getId()) < plantable.getRequiredLevel()) {
						player.getActionSender().sendMessage(
								"You need a Farming level of " + plantable.getRequiredLevel() + " to plant that.");
						return;
					}
					if (farmingPatch.getType() == FarmingPatchType.TREE_PATCH) {
						if (!player.getInventory().contains(GARDENING_TROWEL.getId())) {
							player.getActionSender().sendMessage("You need a gardening trowel to plant sapplings.");
							return;
						}
						plant(player, patchState, plantable, object);
					} else {
						if (!player.getInventory().contains(SEED_DIBBER.getId())) {
							player.getActionSender().sendMessage("You need a Seed dibber to plant seeds.");
							return;
						}
						plant(player, patchState, plantable, object);
					}
				}
			}
		}
	}

	private void inspect(@Nonnull Player player, @Nonnull FarmingPatchState patch) {
		final StringBuilder builder = new StringBuilder();
		builder.append("This is a ").append(patch.getPatch().getType().toString()).append(".");

		if (patch.getTreatment() == FarmingPatchTreatment.NOT_TREATED) {
			builder.append(" The soil has not been treated.");
		} else {
			builder.append(" The soil has been treated with ").append(patch.getTreatment().name().toLowerCase())
					.append(".");
		}

		if (patch.getPlanted() != null) {
			if (isPatchFullyGrown(patch)) {
				builder.append(" The patch is fully grown.");
			} else if (patch.isDiseased()) {
				builder.append(" The patch is diseased and needs attending to before it dies.");
			} else if (patch.isDead()) {
				builder.append(" The patch has become infected by disease and has died.");
			} else {
				builder.append(" The patch has something growing in it.");
			}
		} else {
			if (patch.getWeedLevel() < 3) {
				builder.append(" The patch needs weeding.");
			} else {
				builder.append(" The patch is empty and weeded.");
			}
		}

		player.getActionSender().sendMessage(builder.toString());
	}

	private void cure(@Nonnull Player player, @Nonnull FarmingPatchState patch, @Nonnull GameObject object) {
		player.faceObject(object);
		player.getActionQueue().addAction(new FarmingCureAction(player, patch));
	}

	private void clear(@Nonnull Player player, @Nonnull FarmingPatchState patch, @Nonnull GameObject object) {
		player.faceObject(object);
		player.getActionQueue().addAction(new FarmingClearingAction(player, patch));
	}

	private void treat(@Nonnull Player player, @Nonnull FarmingPatchState patch,
			@Nonnull FarmingPatchTreatment treatment, @Nonnull GameObject object) {
		player.faceObject(object);
		player.getActionQueue().addAction(new FarmingTreatmentAction(player, patch, treatment));
	}

	private void water(@Nonnull Player player, @Nonnull FarmingPatchState patch, @Nonnull GameObject object) {
		player.faceObject(object);
		player.getActionQueue().addAction(new FarmingWateringAction(player, patch));
	}

	private void harvest(@Nonnull Player player, @Nonnull FarmingPatchState patch, @Nonnull GameObject object) {
		player.faceObject(object);
		player.getActionQueue().addAction(new FarmingHarvestingAction(player, patch));
	}

	private void plant(@Nonnull Player player, @Nonnull FarmingPatchState patch, @Nonnull FarmingPlantable plantable,
			@Nonnull GameObject object) {
		player.faceObject(object);
		player.getActionQueue().addAction(new FarmingPlantingAction(player, patch, plantable));
	}

	private void rake(@Nonnull Player player, @Nonnull FarmingPatchState patch, @Nonnull GameObject object) {
		player.faceObject(object);
		player.getActionQueue().addAction(new FarmingRakeAction(player, patch));
	}

	/**
	 * Randomly decides if a patch should become diseased based on the state of it.
	 *
	 * @param patch
	 *            The patch.
	 * @return true for diseased, false if not.
	 */
	private boolean randomlyDisease(@Nonnull FarmingPatchState patch) {
		if (!patch.getPatch().getType().isVulnerableToDisease()) {
			return false;
		}

		int modifier = 0;
		modifier += (patch.getTreatment().getYieldIncrease() * 3);
		modifier += (patch.isWatered() ? 3 : 0);

		return Misc.random(10 + modifier) == 1;
	}

	@Subscribe
	public void onRegionChange(final GamePlayerRegionEvent event) {
		final Player player = event.getPlayer();
		if (player.isActive()) {

			// TODO TEST MORE
			final PlayerSkillFarmingEntity farming = player.getDatabaseEntity().getFarmingSkill();
			for (GameObject object : player.getRegion().getGameObjects()) {
				final FarmingPatch farmingPatch = FarmingPatch.forObjectIdAndLocation(object.getId(),
						object.getLocation());
				if (farmingPatch != null && !farming.getPatches().containsKey(farmingPatch))
					farming.getPatches().put(farmingPatch, new FarmingPatchState(farmingPatch));
			}
			
			sendPatches(event.getPlayer());
		}
	}

	@Subscribe
	public void onNpcAction(final GameNpcActionEvent event) {
		final Player player = event.getPlayer();
		if (event.getActionType() == GameNpcActionEvent.ActionType.OPTION_1) {
			if (event.getNpc().getId() == 0) {
				DialogueManager.openDialogue(player, 3);
			}
		} else if (event.getActionType() == GameNpcActionEvent.ActionType.OPTION_TRADE) {
			if (event.getNpc().getId() == 0) {
				openToolInterface(player);
			}
		} else if (event.getActionType() == GameNpcActionEvent.ActionType.ITEM_ON_NPC) {
			final Item item = event.getItem();
			assert item != null;
			if (event.getNpc().getId() != 0)
				return;
			final FarmingPlantable plantable = FarmingPlantable.forRewardItemId(item.getId());

			if (plantable != null) {
				final int amount = player.getInventory().getCount(plantable.getReward());
				final Item unnoted = new Item(plantable.getReward(), amount);
				final Item noted = new Item(plantable.getReward() + 1, amount);
				player.getInventory().remove(unnoted);
				player.getInventory().addItemIgnoreStackPolicy(noted);
				player.getActionSender().sendMessage("The Leprechaun exchanges your items into notes.");
			}
		}
	}

	@Subscribe
	public void onInterfaceButtonClick(final GameInterfaceButtonEvent event) {
		final Player player = event.getPlayer();
		if (event.getInterfaceId() == 126) {
			final FarmingTool tool = FarmingTool.forInventoryActionButtonId(event.getButton());
			if (tool == null || !player.getInventory().contains(tool.getItemId())) {
				return;
			}

			final PlayerSkillFarmingEntity farming = player.getDatabaseEntity().getFarmingSkill();
			if (farming.getToolStore().containsKey(tool)) {
				final int stored = farming.getToolStore().get(tool);
				if (stored >= tool.getMaxAmount()) {
					player.getActionSender().sendMessage("You cannot store more than " + tool.getMaxAmount() + " "
							+ new Item(tool.getItemId()).getDefinition2().getName() + " in here.");
				} else {
					farming.getToolStore().put(tool, farming.getToolStore().get(tool) + 1);
					player.getInventory().remove(new Item(tool.getItemId(), 1));
				}
			} else {
				farming.getToolStore().put(tool, 1);
				player.getInventory().remove(new Item(tool.getItemId(), 1));
			}
		} else if (event.getInterfaceId() == 125) {
			final FarmingTool tool = FarmingTool.forStoreActionButtonId(event.getButton());
			if (tool == null) {
				return;
			}

			final PlayerSkillFarmingEntity farming = player.getDatabaseEntity().getFarmingSkill();
			if (farming.getToolStore().containsKey(tool)) {
				final int amount = farming.getToolStore().get(tool);
				if (amount > 0) {
					final Item item = new Item(tool.getItemId(), 1);
					if (!player.getInventory().hasRoomFor(item)) {
						player.getActionSender().sendMessage("Your inventory is full.");
						return;
					} else {
						player.getInventory().add(item);
						if (amount - 1 == 0) {
							farming.getToolStore().remove(tool);
						} else {
							farming.getToolStore().put(tool, amount - 1);
						}
					}
				}
			}
		}

		sendToolInterface(player);
	}

	private void sendToolInterface(@Nonnull Player player) {
		final BitConfigBuilder config = BitConfigBuilder.of(615);
		player.getDatabaseEntity().getFarmingSkill().getToolStore().entrySet().forEach(t -> {
			final FarmingTool tool = t.getKey();
			final int amount = t.getValue();

			if (tool == FarmingTool.RAKE) {
				config.or(0x1);
			} else if (tool == FarmingTool.SEED_DIBBER) {
				config.or(0x2);
			} else if (tool == FarmingTool.SPADE) {
				config.or(0x4);
			} else if (tool == FarmingTool.SECATEURS) {
				config.or(0x8);
			} else if (tool == FarmingTool.TROWEL) {
				config.or(0x100);
			} else if (tool == FarmingTool.WATERING_CAN) {
				config.or(0x30);
			} else if (tool == FarmingTool.BUCKET) {
				config.or(amount << 9);
			} else if (tool == FarmingTool.COMPOST) {
				config.or(amount << 14);
			} else if (tool == FarmingTool.SUPERCOMPOST) {
				config.or(amount << 22);
			}
		});

		player.getActionSender().sendConfig(config.build());
	}

	@Override
	public void openToolInterface(@Nonnull Player player) {
		player.getActionSender().sendInterface(125, false);
		player.getActionSender().sendInterfaceInventory(126);

		sendToolInterface(player);
	}

	@Override
	public void clearPatch(@Nonnull Player player, @Nonnull FarmingPatchState patch) {
		patch.setGrowth(0);
		patch.setPlanted(null);
		patch.setTreatment(FarmingPatchTreatment.NOT_TREATED);
		patch.setWatered(false);
		patch.setDiseased(false);
		patch.setDead(false);
		patch.setImmune(false);
		updateAndSendPatches(player, patch);
	}

	@Override
	public void updateAndSendPatches(@Nonnull Player player, @Nonnull FarmingPatchState patch) {
		updatePatch(patch);
		sendPatches(player);
	}

	@Override
	public void updatePatch(@Nonnull FarmingPatchState patch) {
		final DateTime now = DateTime.now(DateTimeZone.UTC);

		// Check if whatever is planted can grow
		if (patch.getPlanted() != null) {
			if (!isPatchFullyGrown(patch)) {
				// Check if the current stage is ready to grow
				if (now.isAfter(patch.getLastGrowthTime().plus(patch.getPlanted().getGrowthTime()))) {
					patch.setLastGrowthTime(now);
					patch.setWatered(false);

					// Choose whether to disease, kill, or grow the crop
					if (!patch.isDead() && !patch.isDiseased() && randomlyDisease(patch)
							&& patch.getPatch().getType() != FarmingPatchType.HERB_PATCH) {
						patch.setDiseased(true);
						// logger.info("Crop " + patch.getPlanted().name() + " diseased for " +
						// player.getName());
					} else if (patch.isDiseased()) {
						patch.setDiseased(false);
						patch.setDead(true);
						// logger.info("Crop " + patch.getPlanted().name() + " died for " +
						// player.getName());
					} else if (!patch.isDead()) {
						patch.setGrowth(patch.getGrowth() + 1);
						// logger.info("Growing...");

						if (isPatchFullyGrown(patch)) {
							// The plantable has finished growing.
							if (patch.getPatch().getType() != FarmingPatchType.TREE_PATCH)
								patch.setYield(Misc.random(patch.getPlanted().getMinYield() + 5,
										patch.getPlanted().getMaxYield() + 5)
										+ patch.getTreatment().getYieldIncrease());
						}
					}
				}
			} else {
				// The plantable is fully grown..
			}
		} else {
			// Check if weed should grow back
			if (patch.getWeedLevel() > 0 && (patch.getLastGrowthTime() == null
					|| now.isAfter(patch.getLastGrowthTime().plus(Duration.standardMinutes(1))))) {
				patch.setLastGrowthTime(now);
				patch.setWeedLevel(patch.getWeedLevel() - 1);
			}
		}
	}

	@Override
	public void sendPatches(@Nonnull Player player) {
		final Map<Integer, BitConfigBuilder> configMap = newHashMap();

		// We cannot send each patch config 1 by 1 since they are packed, and therefore
		// since
		// different patches may have different config ids, we eagerly construct them.
		player.getDatabaseEntity().getFarmingSkill().getPatches().entrySet().stream().filter(p -> {
			/*
			 * Oddly enough, RS uses the same config ID for all farming patches, and the
			 * config is region specific. This then inherently addresses the issue where all
			 * patches would be mirrored across Gielinor. /*
			 */
			final Area patchArea = p.getKey().getAreas().get(0);
			final Location patchLocation = Location.create(patchArea.getBottomLeftX(), patchArea.getBottomLeftY());
			return player.getLocation().distance(patchLocation) <= 64;
		}).forEach(p -> {
			final FarmingPatch key = p.getKey();
			final FarmingPatchType type = key.getType();
			final FarmingPatchState patch = p.getValue();
			final BitConfigBuilder config = configMap.getOrDefault(key.getConfigId(),
					new BitConfigBuilder(key.getConfigId()));

			if (patch.getPlanted() != null) {
				config.set(patch.getGrowth(), key.getConfigBitOffset());
			} else {
				config.set(patch.getWeedLevel(), key.getConfigBitOffset());
			}

			if (patch.isWatered()) {
				if (type == FarmingPatchType.ALLOTMENT || type == FarmingPatchType.FLOWER_PATCH) {
					config.set(1 << type.getStateBitOffset(), key.getConfigBitOffset());
				}
			} else if (patch.isDiseased()) {
				if (type == FarmingPatchType.ALLOTMENT || type == FarmingPatchType.FLOWER_PATCH) {
					config.set(2 << type.getStateBitOffset(), key.getConfigBitOffset());
				} else if (type == FarmingPatchType.HERB_PATCH) {
					// TODO fix this, doesn't work
					config.set(1 << type.getStateBitOffset(), key.getConfigBitOffset());
				}
			} else if (patch.isDead()) {
				if (type == FarmingPatchType.ALLOTMENT || type == FarmingPatchType.FLOWER_PATCH) {
					config.set(3 << type.getStateBitOffset(), key.getConfigBitOffset());
				} else if (type == FarmingPatchType.HERB_PATCH) {
					// TODO fix this, doesn't work
					config.set(0xAB, key.getConfigBitOffset());
				}
			}

			configMap.put(key.getConfigId(), config);
		});

		configMap.entrySet().stream().forEach(e -> {
			final BitConfig config = e.getValue().build();
			player.getActionSender().sendConfig(config.getId(), config.getValue());
		});
	}

	/**
	 * Checks if the patch has a fully grown product in it
	 * 
	 * @param patch
	 *            the patch to check
	 * @return if is fully grown
	 */
	private boolean isPatchFullyGrown(final FarmingPatchState patch) {
		return patch.getPlanted() != null && patch.getGrowth() >= patch.getPlanted().getMaxGrowth();
	}

	/**
	 * Handles the tool leprechaun's teleportation around patches.
	 * 
	 * @param player
	 *            the player teleporting
	 * @return if can be teleported
	 */
	public static boolean canTeleport(Player player) {
		if (player.getInventory().getCount(995) < 10000) {
			player.getActionSender().sendDialogue("Tool Leprechaun", DialogueType.NPC, 0,
					FacialAnimation.BOWS_HEAD_WHILE_SAD,
					"Sorry chum, you need 10k gold pieces to travel with me to this location!");
			player.getInterfaceState().setNextDialogueId(0, -1);
			return false;
		}
		player.getInventory().remove(new Item(995, 10000));
		player.sendMessage("The Leprechaun quickly took your 10k and disappeared.");
		return true;
	}

	/**
	 * Int array holding watering can ID's
	 */
	private static int[] WATERING_CANS = { 5333, 5334, 5335, 5336, 5337, 5338, 5339, 5340 };

	/**
	 * Handles item on item, specifically the tree sapling creation
	 * 
	 * @param player
	 *            the player doing the action
	 * @param usedItem
	 *            the used item
	 * @param usedWith
	 *            the item used with
	 * @return if can do it
	 */
	public static boolean handleItemOnItem(Player player, Item usedItem, Item usedWith) {
		if ((usedItem.getId() >= 5312 && usedItem.getId() <= 5316
				|| usedWith.getId() >= 5312 && usedWith.getId() <= 5316)
				&& (usedItem.getId() == 5356 || usedWith.getId() == 5356)) {

			if (!player.getInventory().containsOneItem(WATERING_CANS)) {
				player.sendMessage("You need a watering can with water in it to do this.");
				return true;
			}
			for (int i : WATERING_CANS)
				player.getInventory().replace(i, i - (i == 5333 ? 2 : 1));
			if (usedItem.getId() >= 5312 && usedItem.getId() <= 5316) {
				player.getInventory().remove(new Item(usedItem.getId(), 1));
				player.getInventory().remove(usedWith);
				player.getInventory().add(new Item(usedItem.getId() + 58));
			} else if (usedWith.getId() >= 5312 && usedWith.getId() <= 5316) {
				player.getInventory().remove(new Item(usedWith.getId(), 1));
				player.getInventory().remove(usedItem);
				player.getInventory().add(new Item(usedWith.getId() + 58));
			}
			return true;
		}
		return false;
	}
}
