package org.rs2server.rs2.domain.service.impl.content.bounty;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.apache.mina.util.ConcurrentHashSet;
import org.rs2server.rs2.content.api.GameInterfaceButtonEvent;
import org.rs2server.rs2.content.api.GameMobDeathEvent;
import org.rs2server.rs2.content.api.GamePlayerLogoutEvent;
import org.rs2server.rs2.domain.model.player.PlayerBountyHunterEntity;
import org.rs2server.rs2.domain.service.api.GroundItemService;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.domain.service.api.PlayerVariableService;
import org.rs2server.rs2.domain.service.api.content.ItemService;
import org.rs2server.rs2.domain.service.api.content.bounty.BountyHunterService;
import org.rs2server.rs2.domain.service.api.content.bounty.BountyHunterVariables;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.bit.component.Access;
import org.rs2server.rs2.model.bit.component.AccessBits;
import org.rs2server.rs2.model.bit.component.NumberRange;
import org.rs2server.rs2.model.player.Bounty;
import org.rs2server.rs2.model.player.Player;
import javax.annotation.Nonnull;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * @author Clank1337
 * @author twelve
 */
public class BountyHunterServiceImpl implements BountyHunterService {

	private final PlayerVariableService variableService;
	private final ItemService itemService;
	private final GroundItemService groundItemService;
	private static final Random RANDOM = new Random();

	public static final Set<Player> WILDERNESS_PLAYER_LIST = new ConcurrentHashSet<>();

	private static final int BOUNTY_WIDGET_ID = 90;
	private static final int BOUNTY_STORE_WIDGET_ID = 178;
	private static final int TARGET_CHILD_ID = 19;
	private static final int LVL_CHILD_ID = 20;
	
	@SuppressWarnings("unused")
	private static final int KDR_CHILD_ID = 30, CURRENT_ROGUE_CHILD = 10, CURRENT_HUNTER_CHILD = 11,
			RECORD_HUNTER_CHILD = 12, RECORD_ROGUE_CHILD = 13;

	private static final Access BOUNTY_SHOP_ACCESS = Access.of(BOUNTY_STORE_WIDGET_ID, 2, NumberRange.of(0, 49),
			AccessBits.optionBit(10));

	@SuppressWarnings("unused")
	private static final int BOUNTY_POINT_CONFIG = 1132, HUNTER_RECORD_CONFIG = 1135;

	@Inject
	BountyHunterServiceImpl(PlayerVariableService variableService, ItemService itemService, HookService hookService,
			GroundItemService groundItemService, PlayerService playerService) {
		this.variableService = variableService;
		this.itemService = itemService;
		this.groundItemService = groundItemService;
		hookService.register(this);
	}

	@Override
	public final void openWidget(@Nonnull Player player) {
		boolean inWilderness = player.isInWilderness();

		player.getActionSender().sendInterfaceConfig(BOUNTY_WIDGET_ID, 26, inWilderness)// 23
																						// 26
				.sendInterfaceConfig(90, 26, !inWilderness).sendInterfaceConfig(90, 28, false)
				.sendInterfaceConfig(90, 29, false)// 29
				.sendInterfaceConfig(BOUNTY_WIDGET_ID, 30, true);
		player.getActionSender().sendWalkableInterface(90);
		variableService.set(player, BountyHunterVariables.KDR_TOGGLE, 1);
		variableService.send(player, BountyHunterVariables.KDR_TOGGLE);

		int kills = player.getDatabaseEntity().getBountyHunter().getKills();
		int deaths = player.getDatabaseEntity().getBountyHunter().getDeaths();
		player.getActionSender().sendConfig(1103, kills).sendConfig(1102, deaths);
	}

	@Override
	public final void updateWidget(@Nonnull Player player) {
		if (player.getBountyTarget() != null) {
			Player target = player.getBountyTarget();

			player.getActionSender().sendString(BOUNTY_WIDGET_ID, TARGET_CHILD_ID, target.getName());
			int depth = Location.getWildernessLevel(target, target.getLocation());
			String color;
			String wildernessLevel;
			if (!target.isInWilderness()) {
				wildernessLevel = "<col=00ff00>Safe, Cmb " + target.getSkills().getCombatLevel();
			} else {
				int distance = (int) player.getLocation().distance(target.getLocation());
				if (distance > 15 && distance <= 60) {
					color = "<col=cd853f>";
				} else if (distance > 60) {
					color = "<col=0174DF>";
				} else {
					color = "<col=00ff00>";
				}
				wildernessLevel = color + "Lvl " + depth + "-" + (depth + 3) + ", Cmb "
						+ target.getSkills().getCombatLevel() + "";
			}

			player.getActionSender().sendString(BOUNTY_WIDGET_ID, LVL_CHILD_ID, wildernessLevel);
			setWealth(target, Wealth.of(itemService.getNetWorth(player)));
		} else if (player.getBounty() != null) {
			Bounty bounty = player.getBounty();
			int delay = bounty.getTargetDelay() / 100;
			String time = delay <= 0 ? "1 min" : delay + " min";
			player.getActionSender().sendString(BOUNTY_WIDGET_ID, LVL_CHILD_ID, time);
		}
	}

	@Override
	public final void resetWidget(@Nonnull Player player) {
		player.getActionSender().sendString(BOUNTY_WIDGET_ID, TARGET_CHILD_ID, "").sendString(BOUNTY_WIDGET_ID,
				LVL_CHILD_ID, "");
		setWealth(player, Wealth.NONE);
	}

	@Override
	public final void assignTarget(@Nonnull Player player) {
		Optional<Player> availablePlayers = WILDERNESS_PLAYER_LIST.stream()
				.filter(p -> p != player && hasRequiredCombatLevel(player, p)).filter(p -> p.getBountyTarget() == null)
				.findFirst();
		if (availablePlayers.isPresent()) {
			Player target = availablePlayers.get();

			player.setBountyTarget(target);
			target.setBountyTarget(player);

			updateWidget(player);
			updateWidget(target);

			sendHintIcons(player);

			player.getActionSender().sendMessage("<col=FF0000>You have been assigned a target: " + target.getName());
			target.getActionSender().sendMessage("<col=FF0000>You have been assigned a target: " + player.getName());
		} else {
			player.getActionSender().sendMessage("<col=FF0000>Unable to find a suitable target...");
			if (player.getBounty() != null) {
				Bounty bounty = player.getBounty();
				bounty.setTargetDelay(100);

				resetWidget(player);
				updateWidget(player);
			}
		}
	}

	@Override
	public final void sendHintIcons(@Nonnull Player player) {
		player.getActionSender().setTargetHintIcon(player.getBountyTarget());
		player.getBountyTarget().getActionSender().setTargetHintIcon(player);
	}

	@Override
	public final void resetHintIcons(@Nonnull Player player) {
		player.getActionSender().resetTargetEntity();
		if (player.getBountyTarget() != null) {
			player.getBountyTarget().getActionSender().resetTargetEntity();
		}
	}

	@Override
	public final void setWealth(@Nonnull Player player, @Nonnull BountyHunterService.Wealth wealth) {
		variableService.set(player, BountyHunterVariables.WEALTH_VARIABLE, wealth.ordinal());
		variableService.send(player, BountyHunterVariables.WEALTH_VARIABLE);
	}

	@Override
	public final void openBountyShop(@Nonnull Player player) {
		player.getActionSender().sendInterface(BOUNTY_STORE_WIDGET_ID, false).sendConfig(BOUNTY_POINT_CONFIG,
				player.getDatabaseEntity().getBountyHunter().getBountyShopPoints());
		player.sendAccess(BOUNTY_SHOP_ACCESS);
	}

	@Override
	public final void tick(@Nonnull Player player) {

		if (player.getBounty() == null) {
			player.setBounty(new Bounty(player));
		} else {
			Bounty bounty = player.getBounty();

			if (!player.isInWilderness() && player.getBountyTarget() != null) {

				if (bounty.getSafeDelay() <= 0) {
					resetWidget(player);
					resetWidget(player.getBountyTarget());

					resetHintIcons(player);
					player.getBountyTarget().getActionSender().sendMessage(
							"<col=FF0000>Your target is no longer available, so you shall be assigned a new target.");
					bounty.reset();
					player.getActionSender().removeWalkableInterface();
					player.getActionSender().sendMessage(
							"<col=FF0000>You have not returned to the Wilderness in a timely manner and have lost your target.");
					return;
				}

				int delay = bounty.getSafeDelay() / 100;
				if (bounty.getSafeDelay() == 200 || bounty.getSafeDelay() == 100) {
					player.getActionSender().sendMessage(
							"<col=FF0000>You have " + (delay) + "" + (delay == 1 ? " minute " : " minutes ")
									+ "to return to the Wilderness before you lose your target.");
				}

				variableService.set(player, BountyHunterVariables.SAFE_TIMER_VARIABLE, bounty.getSafeDelay());
				variableService.send(player, BountyHunterVariables.SAFE_TIMER_VARIABLE);

				bounty.decrementSafeTimer();
			} else {

				bounty.decrementTargetDelay();

				if (bounty.isReadyForTarget()) {
					assignTarget(player);
				}
			}
		}
		updateWidget(player);
	}

	@Override
	public final void increaseKillCount(@Nonnull Player player) {
		int kills = player.getDatabaseEntity().getBountyHunter().getKills() + 1;
		player.getDatabaseEntity().getBountyHunter().setKills(kills);
		player.getActionSender().sendConfig(1103, kills);
	}

	@Override
	public final void increaseDeathCount(@Nonnull Player player) {
		int deaths = player.getDatabaseEntity().getBountyHunter().getDeaths() + 1;
		player.getDatabaseEntity().getBountyHunter().setDeaths(deaths);
		player.getActionSender().sendConfig(1102, deaths);
	}

	@Override
	@Subscribe
	public final void onPlayerLogout(@Nonnull GamePlayerLogoutEvent event) {
		final Player player = event.getPlayer();
		if (player.getBounty() != null) {
			if (player.getBountyTarget() != null) {
				Player target = player.getBountyTarget();
				if (!target.isInWilderness()) {
					target.getActionSender().removeWalkableInterface();
				}
				target.getActionSender().sendMessage(
						"<col=FF0000>Your target is no longer available, so you shall be assigned a new target.");
				resetWidget(target);
				resetHintIcons(target);
			}
			player.getBounty().reset();
		}
	}

	private static int getAmountForMenuIndex(int index) {
		switch (index) {
		case 0:
		case 1:
			return index;
		case 7:
			return 5;
		case 8:
			return 10;
		default:
			return 0;
		}
	}

	@Override
	@Subscribe
	public final void onBountyShopClick(@Nonnull GameInterfaceButtonEvent event) {
		if (event.getInterfaceId() == BOUNTY_STORE_WIDGET_ID) {
			Item clickedItem = new Item(event.getChildButton2(), getAmountForMenuIndex(event.getMenuIndex()));
			if (event.getMenuIndex() == 0) {
				BountyHunterService.BountyShopRewards.cost(clickedItem)
						.ifPresent(i -> event.getPlayer().getActionSender()
								.sendMessage(clickedItem.getDefinition2().getName() + " currently costs: "
										+ NumberFormat.getNumberInstance(Locale.ENGLISH).format(i) + " Bounties."));
				return;
			}

			if (clickedItem.getCount() >= 1) {
				Optional<Integer> costOption = BountyHunterService.BountyShopRewards.cost(clickedItem);

				if (costOption.isPresent()) {
					PlayerBountyHunterEntity bountyHunterEntity = event.getPlayer().getDatabaseEntity()
							.getBountyHunter();

					int emblemCost = costOption.get();
					int cost = clickedItem.getCount() * emblemCost;

					if (bountyHunterEntity.getBountyShopPoints() >= cost
							&& event.getPlayer().getInventory().add(clickedItem)) {
						int difference = bountyHunterEntity.getBountyShopPoints() - cost;
						bountyHunterEntity.setBountyShopPoints(difference);
						event.getPlayer().getActionSender().sendConfig(BOUNTY_POINT_CONFIG, difference);
					} else {
						int maxAmount = (int) Math.floor(cost / bountyHunterEntity.getBountyShopPoints());
						int maxCost = maxAmount * emblemCost;

						if (bountyHunterEntity.getBountyShopPoints() >= maxCost
								&& event.getPlayer().getInventory().add(new Item(clickedItem.getId(), maxAmount))) {
							int difference = bountyHunterEntity.getBountyShopPoints() - maxCost;
							bountyHunterEntity.setBountyShopPoints(difference);
							event.getPlayer().getActionSender().sendConfig(BOUNTY_POINT_CONFIG, difference);
						} else {
							event.getPlayer().getActionSender().sendMessage(
									"Either your inventory is too full to buy an item or you don't have enough Bounties.");
						}
					}
				} else {
					event.getPlayer().sendMessage("You can't buy that item right now.");
				}
			}
		}
	}

	@Subscribe
	public final void onMobDeath(final GameMobDeathEvent event) {
		final Mob mob = event.getMob();
		final Mob k = event.getKiller();
		if (mob.isPlayer() && k.isPlayer()) {
			Player player = (Player) mob;
			Player killer = (Player) k;
			if (player.getBounty() != null) {
				if (killer.getBounty() != null) {
					increaseDeathCount(player);
					increaseKillCount(killer);
				}
				if (player.getBountyTarget() != null) {
					Player target = player.getBountyTarget();
					if (killer == target) {
						killer.getActionSender()
								.sendMessage("<col=FF0000>You've killed your target: " + player.getName() + "!");
						Bounty bounty = player.getBounty();

						Item item = null;
						for (Emblems e : Emblems.values()) {
							int slot = killer.getInventory().getSlotById(e.getId());
							if (slot != -1) {
								Item slotItem = killer.getInventory().get(slot);

								if (item == null || slotItem.getId() > item.getId()) {
									item = slotItem;
								}
							}
						}
						if (item != null) {
							itemService.upgradeItem(killer, item, item);
						}

						if (RANDOM.nextInt(4) == 0) {
							groundItemService.createGroundItem(killer, new GroundItemService.GroundItem(new Item(12746),
									player.getLocation(), killer, false));
						}
						/*
						 * if (!killer.getDatabaseEntity().getBountyHunter().
						 * getLastKilled().equalsIgnoreCase(player.getName()) &&
						 * !killer.getDatabaseEntity().getBountyHunter().
						 * getLastKilledUUID().equalsIgnoreCase(player.
						 * getDetails().getUUID())) { int amount =
						 * Misc.random(200, 500); playerService.giveItem(killer,
						 * new Item(13330, amount), true);
						 * killer.getActionSender().sendMessage("You received "
						 * + amount + "x Blood money for killing " +
						 * player.getName() + "."); }
						 */

						resetHintIcons(player);
						bounty.reset();
						resetWidget(player);
						resetWidget(target);
						player.getActionSender().removeWalkableInterface();
						player.getActionSender().sendInteractionOption("null", 1, true).sendInteractionOption("null", 2,
								false);
					} /*
						 * else { if
						 * (!killer.getDatabaseEntity().getBountyHunter().
						 * getLastKilled().equalsIgnoreCase(player.getName()) &&
						 * !killer.getDatabaseEntity().getBountyHunter().
						 * getLastKilledUUID().equalsIgnoreCase(player.
						 * getDetails().getUUID())) { int amount =
						 * Misc.random(50, 100); playerService.giveItem(killer,
						 * new Item(13330, amount), true);
						 * killer.getActionSender().sendMessage("You received "
						 * + amount + "x Blood money for killing " +
						 * player.getName() + "."); } }
						 */
				}
				// player.getActionSender().updateQuestText();
				// killer.getActionSender().updateQuestText();

				killer.getDatabaseEntity().getBountyHunter().setLastKilled(player.getName());
				killer.getDatabaseEntity().getBountyHunter().setLastKilledUUID(player.getDetails().getUUID());
			}
		}
	}

	private boolean hasRequiredCombatLevel(@Nonnull Player player, @Nonnull Player target) {
		return Math.abs(player.getSkills().getCombatLevel() - target.getSkills().getCombatLevel()) <= 8;
	}
}
