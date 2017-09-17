package org.rs2server.rs2.domain.service.impl.content;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.content.api.*;
import org.rs2server.rs2.content.dialogue.*;
import org.rs2server.rs2.domain.model.player.treasuretrail.PlayerTreasureTrail;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.ClueScrollType;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.TreasureTrailAnagramClue;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.TreasureTrailClue;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.TreasureTrailCoordinateClue;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.TreasureTrailEmoteClue;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.TreasureTrailMapClue;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.TreasureTrailRiddleClue;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailClueStep;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailDigCasketStep;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailDoubleAgentStep;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailOpenCasketStep;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailUriStep;
import org.rs2server.rs2.domain.service.api.*;
import org.rs2server.rs2.domain.service.api.content.TreasureTrailService;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.mysql.impl.NewsManager;
import org.rs2server.rs2.util.Misc;
import org.slf4j.*;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tommo
 */
public class TreasureTrailServiceImpl implements TreasureTrailService {

	private static final Logger logger = LoggerFactory.getLogger(TreasureTrailServiceImpl.class);
	private static final Random random = new Random();

	public static final int REWARDS_INTERFACE_ID = 364;

	/**
	 * The dialogue which appears when the player has found a casket.
	 */
	public static final Dialogue CASKET_DIALOGUE = Dialogue.messageItem(12543, "You've found a casket!");

	/**
	 * The dialogue which appears when the player has found clue.
	 */
	public static final Dialogue FOUND_CLUE_DIALOGUE = Dialogue.messageItem(12542, "You've found another clue!");

	private final PlayerService playerService;
	private final PlayerStatisticsService statisticsService;
	@SuppressWarnings("unused")
	private final PathfindingService pathfindingService;

	@Inject
	TreasureTrailServiceImpl(final PlayerService playerService, final PlayerStatisticsService statisticsService,
			final PathfindingService pathfindingService, final HookService hookService) {
		this.playerService = playerService;
		this.statisticsService = statisticsService;
		this.pathfindingService = pathfindingService;
		hookService.register(this);
	}

	@Subscribe
	public void onMobDeath(final GameMobDeathEvent event) {
		final Mob mob = event.getMob();
		final Mob killer = event.getKiller();
		if (mob instanceof NPC && killer instanceof Player) {
			final NPC npc = (NPC) mob;
			final Player player = (Player) killer;

			if (player.getDatabaseEntity().getTreasureTrail() != null) {
				final PlayerTreasureTrail trail = player.getDatabaseEntity().getTreasureTrail();

				if (trail.getCurrentClue() instanceof TreasureTrailEmoteClue
						&& npc.getId() == TreasureTrailDoubleAgentStep.DOUBLE_AGENT_NPC_ID) {
					getNextUncompletedStep(trail).setCompleted(true);
					World.getWorld().unregister(npc);
				}
			}
		}
	}

	@Subscribe
	public void onGameObjectClick(final GameObjectActionEvent event) {
		final Player player = event.getPlayer();
		final PlayerTreasureTrail trail = player.getDatabaseEntity().getTreasureTrail();
		if (trail == null || !hasClueInInventory(player, trail))
			return;

		if (event.getActionType() == GameObjectActionEvent.ActionType.OPTION_1) {
			final GameObject obj = event.getGameObject();

			if (trail.getCurrentClue() instanceof TreasureTrailRiddleClue) {
				final TreasureTrailRiddleClue clue = (TreasureTrailRiddleClue) trail.getCurrentClue();

				if (obj.getId() == clue.getObjectId()) {
					onClueScrollStep(player, trail);
				}
			}
		}
	}

	@Subscribe
	public void onPlayerEmote(final GamePlayerEmoteEvent event) {
		final Player player = event.getPlayer();
		final PlayerTreasureTrail trail = player.getDatabaseEntity().getTreasureTrail();

		if (trail == null)
			return;

		if (trail.getCurrentClue() instanceof TreasureTrailEmoteClue) {
			final TreasureTrailEmoteClue clue = (TreasureTrailEmoteClue) trail.getCurrentClue();
			final Location loc = player.getLocation();

			if (hasClueInInventory(player, trail)) {
				if (event.getEmote() != clue.getEmote())
					return;
				if (!clue.getArea().contains(loc))
					return;
				if (clue.getRequiredEquipment() == null || clue.getRequiredEquipment().size() == 0)
					onClueScrollStep(player, trail);
				else {
					boolean isWearingRequiredEquipment = true;
					for (final Item item : clue.getRequiredEquipment()) {
						if (!player.getEquipment().contains(item.getId())) {
							isWearingRequiredEquipment = false;
							break;
						}
					}

					if (isWearingRequiredEquipment)
						onClueScrollStep(player, trail);
				}
			}
		}
	}

	@Subscribe
	public void onNpcClick(final GameNpcActionEvent event) {
		final Player player = event.getPlayer();
		final NPC npc = event.getNpc();

		if (npc.getId() == TreasureTrailUriStep.URI_NPC_ID && npc.getInstancedPlayer() != player) {
			TreasureTrailUriStep.URI_NO_BUSINESS_DIALOGUE.open(player, 0);
			return;
		}

		final PlayerTreasureTrail trail = player.getDatabaseEntity().getTreasureTrail();
		if (trail == null || !hasClueInInventory(player, trail))
			return;

		if (event.getActionType() == GameNpcActionEvent.ActionType.OPTION_1) {
			if (trail.getCurrentClue() instanceof TreasureTrailAnagramClue) {
				final TreasureTrailAnagramClue clue = (TreasureTrailAnagramClue) trail.getCurrentClue();

				if (npc.getId() == clue.getNpcId())
					onClueScrollStep(player, trail);

			} else if (trail.getCurrentClue() instanceof TreasureTrailRiddleClue) {
				final TreasureTrailRiddleClue clue = (TreasureTrailRiddleClue) trail.getCurrentClue();

				if (npc.getId() == clue.getNpcId())
					onClueScrollStep(player, trail);

			} else if (trail.getCurrentClue() instanceof TreasureTrailEmoteClue
					&& npc.getId() == TreasureTrailUriStep.URI_NPC_ID) {
				final TreasureTrailUriStep uriStep = (TreasureTrailUriStep) getNextUncompletedStep(trail);
				final DialogueChain dialogue = DialogueChain.build(Dialogue.oneOf(TreasureTrailUriStep.URI_DIALOGUES))
						.then(TalkingDialogue.playerSaying(player, Animation.FacialAnimation.ON_ONE_HAND, "What?"))
						.then(CASKET_DIALOGUE).onClose(new Dialogue() {
							@Override
							public void open(@Nonnull Player player, int index) {
								uriStep.despawnUri(npc);
							}
						});

				onClueScrollStep(player, trail);
				dialogue.proceed(player, 0);
			}
		}
	}

	@Subscribe
	public void onSpadeDig(final GameSpadeDigEvent event) {
		final Player player = event.getPlayer();
		final Location loc = event.getLocation();
		final PlayerTreasureTrail trail = player.getDatabaseEntity().getTreasureTrail();

		if (trail == null || !hasClueInInventory(player, trail))
			return;

		if (trail.getCurrentClue() instanceof TreasureTrailMapClue) {
			final TreasureTrailMapClue clue = (TreasureTrailMapClue) trail.getCurrentClue();

			if (clue.getLocationX() == loc.getX() && clue.getLocationY() == loc.getY()
					&& clue.getLocationZ() == loc.getPlane()) {
				onClueScrollStep(player, trail);
			}
		} else if (trail.getCurrentClue() instanceof TreasureTrailRiddleClue) {
			final TreasureTrailRiddleClue clue = (TreasureTrailRiddleClue) trail.getCurrentClue();

			if (clue.getDigArea() != null && clue.getDigArea().contains(loc))
				onClueScrollStep(player, trail);

		} else if (trail.getCurrentClue() instanceof TreasureTrailCoordinateClue) {
			final TreasureTrailCoordinateClue clue = (TreasureTrailCoordinateClue) trail.getCurrentClue();

			if (clue.getLocationX() == loc.getX() && clue.getLocationY() == loc.getY()
					&& clue.getLocationZ() == loc.getPlane())
				onClueScrollStep(player, trail);

		}
	}

	@Subscribe
	public void onInventoryItemAction(final GameItemInventoryActionEvent event) {
		final Player player = event.getPlayer();
		final Item item = event.getItem();
		final PlayerTreasureTrail trail = player.getDatabaseEntity().getTreasureTrail();

		if (event.getClickType() == GameItemInventoryActionEvent.ClickType.DESTROY) {
			// If a clue scroll is destroyed, we destroy the player's treasure trail.
			Arrays.stream(ClueScrollType.values()).filter(c -> item.getId() == c.getClueScrollItemId())
					.forEach(c -> player.getDatabaseEntity().setTreasureTrail(null));
		} else if (event.getClickType() == GameItemInventoryActionEvent.ClickType.OPTION_1) {
			if (trail != null && item.getId() == trail.getType().getCasketItemId()
					&& getNextUncompletedStep(trail) instanceof TreasureTrailOpenCasketStep) {
				onClueScrollStep(player, trail);
			}

			Arrays.stream(ClueScrollType.values()).filter(c -> c.getClueScrollItemId() == item.getId()).limit(1)
					.forEach(c -> {
						if (trail == null) {
							player.getDatabaseEntity().setTreasureTrail(generateTreasureTrail(c));
						}

						player.getDatabaseEntity().getTreasureTrail().getCurrentClue().onRead(player, c);
					});
		}
	}

	/**
	 * Checks whether or not the player has the clue scroll in their inventory.
	 * 
	 * @param player
	 *            The player.
	 * @param trail
	 *            The treasure trail
	 * @return true if so, false if not.
	 */
	private boolean hasClueInInventory(final Player player, final PlayerTreasureTrail trail) {
		boolean hasClue = player.getInventory().contains(trail.getType().getClueScrollItemId());
		if (!hasClue && player.getInventory().containsOneItem(12179, 12029, 12542, 12073, 19835))
			return false;
		return hasClue;
	}

	/**
	 * Called every time a clue scroll step is completed. Advances the current step,
	 * the current clue, or finishes the trail depending on the current state of the
	 * trail.
	 * 
	 * @param player
	 *            The player.
	 * @param trail
	 *            The treasure trail.
	 */
	private void onClueScrollStep(final Player player, final PlayerTreasureTrail trail) {
		final TreasureTrailClueStep step = getNextUncompletedStep(trail);

		if (step != null) {
			if (step instanceof TreasureTrailDigCasketStep) {
				CASKET_DIALOGUE.open(player, 0);
				player.getInventory().remove(new Item(trail.getType().getClueScrollItemId(), 1));
				playerService.giveItem(player, new Item(trail.getType().getCasketItemId(), 1), true);
				step.setCompleted(true);
			} else if (step instanceof TreasureTrailUriStep) {
				final TreasureTrailUriStep uriStep = (TreasureTrailUriStep) step;
				final Optional<NPC> playerUri = World.getWorld().getNPCs().stream()
						.filter(n -> n.getId() == TreasureTrailUriStep.URI_NPC_ID && n.getInstancedPlayer() == player)
						.findFirst();

				if (playerUri.isPresent()) {
					player.getInventory().remove(new Item(trail.getType().getClueScrollItemId(), 1));
					playerService.giveItem(player, new Item(trail.getType().getCasketItemId(), 1), true);
					step.setCompleted(true);
				} else
					uriStep.spawnUri(player);
			} else if (step instanceof TreasureTrailOpenCasketStep) {
				player.getInventory().remove(new Item(trail.getType().getCasketItemId(), 1));
				playerService.giveItem(player, new Item(trail.getType().getClueScrollItemId(), 1), true);
				step.setCompleted(true);
			} else if (step instanceof TreasureTrailDoubleAgentStep) {
				final TreasureTrailDoubleAgentStep doubleAgentStep = (TreasureTrailDoubleAgentStep) step;

				final Optional<NPC> playerDoubleAgent = World.getWorld().getNPCs().stream()
						.filter(n -> n.getId() == TreasureTrailDoubleAgentStep.DOUBLE_AGENT_NPC_ID
								&& n.getInstancedPlayer() == player)
						.findFirst();

				if (!playerDoubleAgent.isPresent())
					doubleAgentStep.spawnDoubleAgent(player);
			}
		}

		if (hasCompletedAllSteps(trail) || player.isDiamondMember()) {
			// Diamond members get to one-click the clues for rewards :)
			if (isLastClue(trail) || player.isDiamondMember())
				finishTreasureTrail(player, trail);
			else {
				FOUND_CLUE_DIALOGUE.open(player, 0);
				advanceClue(trail);
			}
		}

	}

	@Override
	public void finishTreasureTrail(final Player player, final PlayerTreasureTrail trail) {
		player.getActionSender().sendMessage("Well done, you've completed the Treasure Trail!");
		statisticsService.increaseTreasureTrailCount(player, trail.getType(), 1);

		final List<Item> rewards = trail.getType().getLootTable().getRandomLoot(random.nextInt(5) + 3).stream().map(
				l -> new Item(l.getItemId(), (Misc.random(l.getMaxAmount() - l.getMinAmount()) + l.getMinAmount())))
				.collect(Collectors.toList());

		showRewardsInterface(player, rewards);
		player.getDatabaseEntity().setTreasureTrail(null);
		if (player.getInventory().contains(trail.getType().getCasketItemId()))
			player.getInventory().remove(new Item(trail.getType().getCasketItemId(), 1));
		if (player.getInventory().contains(trail.getType().getClueScrollItemId()))
			player.getInventory().remove(new Item(trail.getType().getClueScrollItemId(), 1));
		rewards.stream().forEach(i -> playerService.giveItem(player, i, true));
		rewards.stream().forEach(i -> announce(player, i));
	}

	/**
	 * Globally announces every item received from a clue scroll.
	 * 
	 * @param item
	 *            the item received from the trail.
	 */
	private void announce(Player player, Item item) {
		if (item == null)
			return;
		if (item.getPrice() > 500000 || item.getDefinition2().getName().contains("3rd")) {
			World.getWorld()
					.sendWorldMessage("<col=FF0000><img=33>Server</col>: " + player.getName() + " has just received "
							+ item.getCount() + " x " + item.getDefinition2().getName() + " from a Clue scroll.");

			new Thread(new NewsManager(player, "<img src='../resources/news/clue_scroll.png' width=13> received "
					+ item.getDefinition2().getName() + " from Clue scroll.")).start();
		}
	}

	/**
	 * Sets the current clue in the treasure trail to the next clue in the trail.
	 * 
	 * @param treasureTrail
	 *            The treasure trail.
	 */
	private void advanceClue(final PlayerTreasureTrail treasureTrail) {
		final int newCurrentClueIndex = treasureTrail.getCurrentClueIndex() + 1;
		final TreasureTrailClue newClue = treasureTrail.getTrail().get(newCurrentClueIndex);
		treasureTrail.setCurrentClue(newClue);
		treasureTrail.setCurrentClueIndex(newCurrentClueIndex);
		treasureTrail.setCurrentClueSteps(newClue.getStepsAsConcreteClasses(treasureTrail.getType()));
	}

	/**
	 * Checks if the current clue within the treasure trail is the last clue.
	 * 
	 * @param treasureTrail
	 *            The treasure trail.
	 * @return true if the current clue within the treasure trail is the last clue
	 *         in the trail.
	 */
	private boolean isLastClue(final PlayerTreasureTrail treasureTrail) {
		return treasureTrail.getCurrentClueIndex() == treasureTrail.getTrail().size() - 1;
	}

	@Override
	public void showRewardsInterface(@Nonnull Player player, List<Item> rewards) {
		player.getActionSender().sendInterface(REWARDS_INTERFACE_ID, false);
		player.getActionSender().sendUpdateItems(REWARDS_INTERFACE_ID, 1, 0,
				rewards.toArray(new Item[rewards.size() + 1]));
	}

	@Override
	public PlayerTreasureTrail generateTreasureTrail(@Nonnull ClueScrollType clueScrollType) {
		// We copy the list, and remove from it when we pick a clue to disallow
		// duplicates.
		final List<TreasureTrailClue> possibleClues = new ArrayList<>(clueScrollType.getClues());
		final List<TreasureTrailClue> clues = new ArrayList<>();

		final int trailLength = clueScrollType.getMinTrailSize()
				+ random.nextInt(clueScrollType.getMaxTrailSize() - clueScrollType.getMinTrailSize());
		for (int i = 1; i <= trailLength; i++) {
			final TreasureTrailClue c = possibleClues.remove(random.nextInt(possibleClues.size()));
			clues.add(c);
		}

		if (Constants.DEBUG)
			logger.info("Generated new {} treasure trail with {} clues.", clueScrollType.name(), trailLength);

		final TreasureTrailClue currentClue = clues.get(0);
		final PlayerTreasureTrail trail = new PlayerTreasureTrail();
		trail.setType(clueScrollType);
		trail.setTrail(clues);
		trail.setCurrentClue(currentClue);
		trail.setCurrentClueIndex(0);
		trail.setCurrentClueSteps(currentClue.getStepsAsConcreteClasses(clueScrollType));

		return trail;
	}

	/**
	 * Returns the next uncompleted step in the current clue or null.
	 * 
	 * @param trail
	 *            The trail.
	 * @return The next uncompleted step, or null if no steps are necessary or all
	 *         steps have been completed.
	 */
	private TreasureTrailClueStep getNextUncompletedStep(final PlayerTreasureTrail trail) {
		return trail.getCurrentClueSteps().stream().filter(s -> !s.isCompleted()).findFirst().orElse(null);
	}

	private boolean hasCompletedAllSteps(final PlayerTreasureTrail trail) {
		return trail.getCurrentClueSteps().size() == 0 || getNextUncompletedStep(trail) == null;
	}

}
