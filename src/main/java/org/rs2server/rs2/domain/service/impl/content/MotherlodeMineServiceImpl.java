package org.rs2server.rs2.domain.service.impl.content;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import org.rs2server.rs2.action.impl.CrawlingAction;
import org.rs2server.rs2.content.api.GameNpcActionEvent;
import org.rs2server.rs2.content.api.GameObjectActionEvent;
import org.rs2server.rs2.content.api.GameWorldLoadedEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.RegionService;
import org.rs2server.rs2.domain.service.api.content.MotherlodeMineService;
import org.rs2server.rs2.domain.service.api.skill.MiningService;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Shop;
import org.rs2server.rs2.model.Skill;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.map.Directions;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.model.region.RegionManager;
import org.rs2server.rs2.model.skills.Mining;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.tickable.Tickable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tommo
 */
public class MotherlodeMineServiceImpl implements MotherlodeMineService {

	private static final Random random = new Random();

	/**
	 * The maximum amount of pay-dirt in the sack.
	 */
	private static final int MAX_SACK_SIZE = 108;

	/**
	 * The amount of pay-dirt in the sack when the player should be warned it is
	 * getting full.
	 */
	private static final int WARN_SACK_SIZE = 81;

	private static final int PROSPECTOR_PERCY_NPC_ID = 6562;
	private static final int PAY_DIRT_ITEM_ID = 12011;
	private static final Animation DEPOSIT_PAY_DIRT_ANIMATION = Animation.create(1649);

	private static final Location PAY_DIRT_NPC_LOCATION = Location.create(3748, 5671, 0);
	private static final Location PAY_DIRT_NPC_LOCATION_TARGET = PAY_DIRT_NPC_LOCATION.transform(0, -12, 0);

	private static final Location LADDER_LOCATION_BOTTOM = Location.create(3755, 5672, 0);
	private static final Location LADDER_LOCATION_TOP = Location.create(3755, 5675, 0);

	private static final Location TUNNEL_LOCATION_EXIT = Location.create(3728, 5692);
	private static final Location TUNNEL_LOCATION_ENTRANCE = Location.create(3059, 9766);

	private final RegionService regionService;
	private final MiningService miningService;

	/**
	 * A map of players to the amount of pay dirt they've put through the hopper and
	 * is waiting in the sack for them.
	 */
	private Map<Player, Integer> payDirtInSackMap = new HashMap<>();

	@Inject
	public MotherlodeMineServiceImpl(final HookService hookService, final MiningService miningService,
			final RegionService regionService) {
		this.miningService = miningService;
		this.regionService = regionService;
		hookService.register(this);
	}

	@Subscribe
	public void onGameWorldLoaded(final GameWorldLoadedEvent event) {
		World.getWorld().submit(new DepletedVeinsRestorationTickable(this));
	}

	@Subscribe
	public void onNpcClick(final GameNpcActionEvent clickEvent) {
		if (clickEvent.getNpc().getId() != PROSPECTOR_PERCY_NPC_ID)
			return;

		if (clickEvent.getActionType() == GameNpcActionEvent.ActionType.OPTION_1) {
			Shop.open(clickEvent.getPlayer(), 36, -1);
		} else if (clickEvent.getActionType() == GameNpcActionEvent.ActionType.OPTION_TRADE) {
			Shop.open(clickEvent.getPlayer(), 36, -1);
		}
	}

	@Subscribe
	public void onObjectClick(final GameObjectActionEvent clickEvent) {
		if (clickEvent.getActionType() == GameObjectActionEvent.ActionType.OPTION_1) {
			final Player player = clickEvent.getPlayer();
			final GameObject object = clickEvent.getGameObject();
			switch (object.getId()) {
			/* Ore vein */
			case 26663:
				player.getActionQueue().addAction(new MotherlodeMiningAction(player, object));
				break;

			/* Ladder to second floor */
			case 19044:
				if (player.getSkills().getLevel(Skill.MINING.getId()) < 72) {
					player.getActionSender().sendDialogue("", DialogueType.MESSAGE, 1, FacialAnimation.DEFAULT,
							"You need a higher Mining level to go up there.");
					player.getActionSender()
							.sendMessage("You need a Mining level of at least 72 to mine at the upper level.");
					break;
				}
				if (player.getLocation().getY() <= LADDER_LOCATION_BOTTOM.getY()) {
					player.climbStairsUp(LADDER_LOCATION_TOP);
					return;
				}
				player.climbStairsUp(LADDER_LOCATION_BOTTOM);
				break;

			/* Sack */
			case 26688:
				claimOreInSack(player);
				break;

			/* Hopper */
			case 26674:
				depositPayDirt(player);
				break;

			/* Chest */
			case 26707:
				Bank.open(player);
				break;

			/* Rockfall */
			case 26679:
			case 26680:
				final Mining.PickAxe pickAxe = miningService.getPickaxe(player);
				if (pickAxe == null) {
					player.getActionSender().sendMessage("You do not have a pickaxe that you can use.");
					break;
				} else if (player.getSkills().getLevel(Skill.MINING.getId()) < 30) {
					player.getActionSender().sendMessage("You need a Mining level of 30 to mine this rock.");
					break;
				}

				player.playAnimation(pickAxe.getAnimation());
				World.getWorld().submit(new Tickable(3) {
					@Override
					public void execute() {
						player.playAnimation(Animation.create(-1));
						World.getWorld().replaceObject(object, null, 60);
						stop();
					}
				});
				break;

			/* Entrance */
			case 26654:
				player.getActionQueue().addAction(new CrawlingAction(player, TUNNEL_LOCATION_EXIT));
				break;

			/* Exit */
			case 26655:
				player.getActionQueue().addAction(new CrawlingAction(player, TUNNEL_LOCATION_ENTRANCE));
				break;
			}
		}
	}

	@Override
	public void claimOreInSack(@Nonnull Player player) {
		final int amount = getPayDirtInSack(player);
		final int freeSpace = player.getInventory().freeSlots();

		if (amount == 0) {
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, PAY_DIRT_ITEM_ID,
					null, "The sack is empty.");
			return;
		}

		if (freeSpace == 0) {
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, PAY_DIRT_ITEM_ID,
					null, "Your inventory is full.");
			return;
		}

		if (amount > freeSpace) {
			final int remaining = amount - freeSpace;
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, PAY_DIRT_ITEM_ID,
					null, "You collect your ore from the sack.", "The sack still contains " + remaining + " ores.");
			generateOres(player, freeSpace).stream().forEach(i -> {
				player.getInventory().add(new Item(i.getItemId(), 1));
				player.getSkills().addExperience(Skill.MINING.getId(), i.getExperience());
			});
			payDirtInSackMap.put(player, getPayDirtInSack(player) - freeSpace);
		} else {
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, PAY_DIRT_ITEM_ID,
					null, "You collect your ore from the sack.", "The sack is now empty");
			generateOres(player, amount).stream().forEach(i -> {
				player.getInventory().add(new Item(i.getItemId(), 1));
				player.getSkills().addExperience(Skill.MINING.getId(), i.getExperience());
			});
			payDirtInSackMap.put(player, 0);
		}

	}

	@Override
	public void addPayDirtToSack(@Nonnull final Player player, final int amount) {
		payDirtInSackMap.put(player, getPayDirtInSack(player) + amount);
	}

	@Override
	public int getPayDirtInSack(@Nonnull Player player) {
		return payDirtInSackMap.containsKey(player) ? payDirtInSackMap.get(player) : 0;
	}

	@Override
	public void depositPayDirt(@Nonnull Player player) {
		if (player.getInventory().getCount(PAY_DIRT_ITEM_ID) == 0) {
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, PAY_DIRT_ITEM_ID,
					null, "You have no pay-dirt in your inventory.");
			return;
		}

		final int depositedPayDirt = getPayDirtInSack(player);
		if (depositedPayDirt == MAX_SACK_SIZE) {
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, PAY_DIRT_ITEM_ID,
					null, "The sack is full.");
			return;
		}

		final int paydirtInInventory = player.getInventory().getCount(PAY_DIRT_ITEM_ID);
		final int depositedPayDirtAfterDeposit = Math.min(depositedPayDirt + paydirtInInventory, MAX_SACK_SIZE);
		final int paydirtToDeposit = depositedPayDirtAfterDeposit - depositedPayDirt;

		if (depositedPayDirtAfterDeposit >= WARN_SACK_SIZE) {
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, PAY_DIRT_ITEM_ID,
					null, "The sack is getting full.");
		}

		player.getInventory().remove(new Item(PAY_DIRT_ITEM_ID, paydirtToDeposit));
		World.getWorld().register(new PayDirtNPC(this, player, paydirtToDeposit));
		player.playAnimation(DEPOSIT_PAY_DIRT_ANIMATION);
	}

	private List<PayDirtOre> generateOres(@Nonnull final Player player, final int amount) {
		final List<PayDirtOre> items = new ArrayList<>(amount);
		for (int i = 0; i < amount; i++) {
			// This will attempt to get the ores with the lowest chance in order.
			// Note that atleast 1 ore MUST have a 100% chance else this will throw a null
			// pointer (in this case, coal).

			final float chance = random.nextFloat();

			PayDirtOre ore = Arrays.stream(PayDirtOre.values())
					.filter(o -> player.getSkills().getLevel(Skill.MINING.getId()) >= o.getMiningLevel())
					.sorted((o1, o2) -> o1.getChance() < o2.getChance() ? -1 : o1.getChance() == o2.getChance() ? 0 : 1)
					.filter(o -> (chance >= 0.9f ? 1f : chance) <= o.getChance()) // Round it if necessary since the
																					// upper bound is exclusive
					.findFirst().get();

			items.add(ore);
		}
		return items;
	}

	/**
	 * Replaces all depleted veins within the Motherlode mine regions with mineable
	 * ore veins.
	 */
	private void replaceDepletedVeinObjects() {
		final RegionManager regionManager = World.getWorld().getRegionManager();

		final Set<Region> regions = ImmutableSet.of(regionManager.getRegion(116, 176),
				regionManager.getRegion(116, 177), regionManager.getRegion(117, 176),
				regionManager.getRegion(117, 177));

		final int veinId = 26663;
		final Set<Integer> depletedVeinIds = ImmutableSet.of(26665, 26666, 26667, 26668);

		// We first build a set of all veins to replace, instead of replacing them
		// instantly to avoid ConcurrentModificationExceptions.
		final Set<GameObject> depletedVeins = new HashSet<>();
		regions.stream().forEach(r -> depletedVeins.addAll(r.getGameObjects().stream().filter(Objects::nonNull)
				.filter(o -> depletedVeinIds.contains(o.getId())).collect(Collectors.toSet())));

		depletedVeins.stream().filter(o -> o.getRegion() != null)
				// .forEach(o -> regionService.addGameObject(o.getRegion(), new
				// GameObject(o.getLocation(), veinId, o.getType(), o.getDirection(), true)));
				// .forEach(o -> regionService.removeGameObject(o.getRegion(), o));
				.forEach(o -> regionService.replaceObject(o.getRegion(), o,
						new GameObject(o.getLocation(), veinId, o.getType(), o.getDirection(), false)));
	}

	private enum PayDirtOre {
		COAL(453, 30, 0, 1f), GOLDEN_NUGGET(12012, 30, 0, 0.09f), GOLD(444, 40, 15, 0.1f), MITHRIL(447, 55, 30,
				0.3f), ADAMANTITE(449, 70, 45, 0.15f), RUNITE(451, 85, 75, 0.04f);

		private final int itemId;
		private final int miningLevel;
		private final int experience;
		private final float chance;

		/**
		 * @param itemId
		 *            The ore item id.
		 * @param miningLevel
		 *            The required mining level.
		 * @param experience
		 *            The mining experience gained (the base experience, not multiplied)
		 * @param chance
		 *            The chance for this ore to be found in range [0..1]
		 */
		PayDirtOre(final int itemId, final int miningLevel, final int experience, final float chance) {
			this.itemId = itemId;
			this.miningLevel = miningLevel;
			this.experience = experience;
			this.chance = chance;
		}

		public int getItemId() {
			return itemId;
		}

		public int getMiningLevel() {
			return miningLevel;
		}

		public int getExperience() {
			return experience;
		}

		public float getChance() {
			return chance;
		}
	}

	/**
	 * A wrapper around an NPC for pay dirt which slides along the water after
	 * placing pay dirt in the hopper.
	 *
	 * @author tommo
	 */
	public static class PayDirtNPC extends NPC {

		private static final int NPC_ID = 6564;
		private final MotherlodeMineService motherlode;
		private final Player player;

		/**
		 * The amount of pay dirt this npc represents.
		 */
		private final int amount;

		public PayDirtNPC(final MotherlodeMineServiceImpl motherlode, final Player player, int amount) {
			super(NPC_ID, PAY_DIRT_NPC_LOCATION, PAY_DIRT_NPC_LOCATION, PAY_DIRT_NPC_LOCATION,
					Directions.NormalDirection.SOUTH.npcIntValue());

			this.motherlode = motherlode;
			this.player = player;
			this.amount = amount;

			getWalkingQueue().addStep(getLocation().getX(), getLocation().getY() - 12);
		}

		@Override
		public void tick() {
			super.tick();
			if (getLocation().equals(PAY_DIRT_NPC_LOCATION_TARGET)) {
				motherlode.addPayDirtToSack(player, amount);
				World.getWorld().unregister(this);
			}
		}

		@Override
		public boolean canMove() {
			return true;
		}
	}

	/**
	 * A tickable which restores all depleted veins within the Motherlode mine.
	 *
	 * @author tommo
	 */
	public static class DepletedVeinsRestorationTickable extends Tickable {

		private final MotherlodeMineServiceImpl motherlodeService;

		public DepletedVeinsRestorationTickable(final MotherlodeMineServiceImpl motherlodeService) {
			super(30);
			this.motherlodeService = motherlodeService;
		}

		@Override
		public void execute() {
			// After the initial tick to replace all depleted veins with ore veins, extend
			// the tick delay.
			setTickDelay(280);
			motherlodeService.replaceDepletedVeinObjects();
		}
	}

}
