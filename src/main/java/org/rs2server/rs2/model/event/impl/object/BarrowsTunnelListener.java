package org.rs2server.rs2.model.event.impl.object;

import org.rs2server.Server;
import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.event.ClickEventManager;
import org.rs2server.rs2.model.event.EventListener;
import org.rs2server.rs2.model.minigame.impl.Barrows.BarrowsBrother;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.mysql.impl.NewsManager;
import org.rs2server.rs2.util.Misc;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Luke132
 * @author 'Mystic Flow
 */
public class BarrowsTunnelListener extends EventListener {

	private static final int REWARDS_INTERFACE_ID = 364;

	private static final int[][] COMMON_REWARDS = { { 561, 1, 35 }, { 558, 1, 100 }, { 562, 1, 63 }, { 560, 1, 50 },
			{ 565, 1, 32 }, { 995, 800, 50000 }, { 4740, 15, 45 } };

	private static final int[] RARE_REWARDS = { 1149, 985, 987 };

	private static final int[] BARROW_REWARDS = {

			4757, 4759, 4753, 4755, // Verac's
			4736, 4738, 4734, 4732, // Karil's
			4745, 4747, 4749, 4751, // Torag's
			4708, 4710, 4712, 4714, // Ahrim's
			4716, 4718, 4720, 4722, // Dharok's
			4724, 4726, 4728, 4730, // Guthan's
			12851 // Amulet of the damned
	};

	protected static final int[] DOORS = { 6747, 6741, 6735, 6739, 6746, 6745, 6737, 6735, 6728, 6722, 6716, 6720, 6727,
			6726, 6718, 6716, 6731, 6728, 6722, 6720, 6727, 6731, 6726, 6718, 6750, 6747, 6741, 6739, 6746, 6750, 6745,
			6737, 6742, 6749, 6748, 6743, 6744, 6740, 6742, 6738, 6723, 6730, 6729, 6724, 6725, 6723, 6721, 6719, 6749,
			6748, 6736, 6743, 6744, 6740, 6738, 6736, 6730, 6729, 6717, 6724, 6725, 6721, 6719, 6717, };

	protected static final int[][] DOOR_LOCATION = { { 3569, 9684 }, { 3569, 9701 }, { 3569, 9718 }, { 3552, 9701 },
			{ 3552, 9684 }, { 3535, 9684 }, { 3535, 9701 }, { 3535, 9718 }, { 3568, 9684 }, { 3568, 9701 },
			{ 3568, 9718 }, { 3551, 9701 }, { 3551, 9684 }, { 3534, 9684 }, { 3534, 9701 }, { 3534, 9718 },
			{ 3569, 9671 }, { 3569, 9688 }, { 3569, 9705 }, { 3552, 9705 }, { 3552, 9688 }, { 3535, 9671 },
			{ 3535, 9688 }, { 3535, 9705 }, { 3568, 9671 }, { 3568, 9688 }, { 3568, 9705 }, { 3551, 9705 },
			{ 3551, 9688 }, { 3534, 9671 }, { 3534, 9688 }, { 3534, 9705 }, { 3575, 9677 }, { 3558, 9677 },
			{ 3541, 9677 }, { 3541, 9694 }, { 3558, 9694 }, { 3558, 9711 }, { 3575, 9711 }, { 3541, 9711 },
			{ 3575, 9678 }, { 3558, 9678 }, { 3541, 9678 }, { 3541, 9695 }, { 3558, 9695 }, { 3575, 9712 },
			{ 3558, 9712 }, { 3541, 9712 }, { 3562, 9678 }, { 3545, 9678 }, { 3528, 9678 }, { 3545, 9695 },
			{ 3562, 9695 }, { 3562, 9712 }, { 3545, 9712 }, { 3528, 9712 }, { 3562, 9677 }, { 3545, 9677 },
			{ 3528, 9677 }, { 3545, 9694 }, { 3562, 9694 }, { 3562, 9711 }, { 3545, 9711 }, { 3528, 9711 }, };

	protected static final int[][] DOOR_OPEN_DIRECTION = { { 6732, 2, 4 }, { 6732, 2, 4 }, { 6732, 2, 4 },
			{ 6732, 2, 4 }, { 6732, 2, 4 }, { 6732, 2, 4 }, { 6732, 2, 4 }, { 6732, 2, 4 }, { 6713, 0, 4 },
			{ 6713, 0, 4 }, { 6713, 0, 4 }, { 6713, 0, 4 }, { 6713, 0, 4 }, { 6713, 0, 4 }, { 6713, 0, 4 },
			{ 6713, 0, 4 }, { 6713, 2, 2 }, { 6713, 2, 2 }, { 6713, 2, 2 }, { 6713, 2, 2 }, { 6713, 2, 2 },
			{ 6713, 2, 2 }, { 6713, 2, 2 }, { 6713, 2, 2 }, { 6732, 4, 2 }, { 6732, 4, 2 }, { 6732, 4, 2 },
			{ 6732, 4, 2 }, { 6732, 4, 2 }, { 6732, 4, 2 }, { 6732, 4, 2 }, { 6732, 4, 2 }, { 6732, 3, 3 },
			{ 6732, 3, 3 }, { 6732, 3, 3 }, { 6732, 3, 3 }, { 6732, 3, 3 }, { 6732, 3, 3 }, { 6732, 3, 3 },
			{ 6732, 3, 3 }, { 6713, 1, 3 }, { 6713, 1, 3 }, { 6713, 1, 3 }, { 6713, 1, 3 }, { 6713, 1, 3 },
			{ 6713, 1, 3 }, { 6713, 1, 3 }, { 6713, 1, 3 }, { 6732, 1, 1 }, { 6732, 1, 1 }, { 6732, 1, 1 },
			{ 6732, 1, 1 }, { 6732, 1, 1 }, { 6732, 1, 1 }, { 6732, 1, 1 }, { 6732, 1, 1 }, { 6713, 3, 1 },
			{ 6713, 3, 1 }, { 6713, 3, 1 }, { 6713, 3, 1 }, { 6713, 3, 1 }, { 6713, 3, 1 }, { 6713, 3, 1 },
			{ 6713, 3, 1 }, };

	protected static final int[][] DB = { { 3532, 9665, 3570, 9671 }, { 3575, 9676, 3581, 9714 },
			{ 3534, 9718, 3570, 9723 }, { 3523, 9675, 3528, 9712 }, { 3541, 9711, 3545, 9712 },
			{ 3558, 9711, 3562, 9712 }, { 3568, 9701, 3569, 9705 }, { 3551, 9701, 3552, 9705 },
			{ 3534, 9701, 3535, 9705 }, { 3541, 9694, 3545, 9695 }, { 3558, 9694, 3562, 9695 },
			{ 3568, 9684, 3569, 9688 }, { 3551, 9684, 3552, 9688 }, { 3534, 9684, 3535, 9688 },
			{ 3541, 9677, 3545, 9678 }, { 3558, 9677, 3562, 9678 }, };

	private final PlayerService playerService = Server.getInjector().getInstance(PlayerService.class);

	@Override
	public void register(ClickEventManager manager) {
		List<Integer> doors = new ArrayList<Integer>();
		for (int i : DOORS) {
			if (!doors.contains(i)) {
				doors.add(i);
			}
		}
		manager.registerObjectListener(20973, this);
	}

	@Override
	public boolean objectAction(Player player, int objectId, GameObject gameObject, Location location,
			ClickOption option) {
		if (option != ClickOption.FIRST)
			return false;
		if (objectId == 20973) {
			if (player.getAttribute("canLoot") == Boolean.TRUE) {
				player.removeAttribute("canLoot");

				player.getDatabaseEntity().getStatistics()
						.setBarrowsChestCount(player.getDatabaseEntity().getStatistics().getBarrowsChestCount() + 1);

				int chests = player.getDatabaseEntity().getStatistics().getBarrowsChestCount();

				if (chests % 500 == 0) {
					World.getWorld().sendWorldMessage("<col=ff0000><img=23>Server</col>: " + player.getName()
							+ " has just opened his " + Misc.formatNumber(chests) + "th Barrows chest.");

					new Thread(new NewsManager(player, "<img src='../resources/news/consecutive.png' width=13> "
							+ "opened his " + Misc.formatNumber(chests) + "th Barrows chest.")).start();
				} else
					player.getActionSender().sendMessage(
							"Your Barrows chest count is: <col=ff0000>" + Misc.formatNumber(chests) + "</col>.");

				final List<Item> rewards = new ArrayList<>();

				if (player.getRandom().nextDouble() > 0.95) {
					int id = RARE_REWARDS[Misc.random(RARE_REWARDS.length - 1)];
					rewards.add(new Item(id, (id == 4740 ? Misc.random(10, 45) : 1)));
				}

				for (int[] data : COMMON_REWARDS) {
					if (player.getRandom().nextDouble() > 0.45) {
						int id = data[0];
						int amount = Misc.random(data[1], data[2]);

						// 50% more common rewards while wearing morytania legs
						if (player.getEquipment().containsOneItem(13114, 13115))
							amount *= 1.5;

						rewards.add(new Item(id, amount));
					}
				}

				if (Misc.random(200) == 0)
					rewards.add(new Item(12073));

				final int chance = player.getBarrowsKillCount();

				player.getKilledBrothers().clear();

				int random = player.getRandom().nextInt(99);

				if (random <= chance && chance > 1) {
					int item = BARROW_REWARDS[player.getRandom().nextInt(BARROW_REWARDS.length)];
					World.getWorld().sendWorldMessage("<col=ff0000><img=33>Server</col>: " + player.getName()
							+ " has just received 1 x " + CacheItemDefinition.get(item).getName() + " from Barrows.");
					rewards.add(new Item(item, 1));

					new Thread(new NewsManager(player, "<img src='../resources/news/barrows.png' width=13> "
							+ "received " + CacheItemDefinition.get(item).getName() + " from Barrows.")).start();

				}

				// Not utilised anywhere
				player.setBarrowsKillcount(0);

				player.removeAttribute("barrows_tunnel");

				for (int i = 0; i < BarrowsBrother.values().length; i++) {
					BarrowsBrother brother = BarrowsBrother.values()[i];
					player.getKilledBrothers().put(brother.getNpcId(), false);
				}

				player.getActionSender().sendString(24, 9, "Kill Count: " + player.getBarrowsKillCount());
				player.setAttribute("looted_barrows", Boolean.TRUE);

				player.getActionSender().sendInterface(REWARDS_INTERFACE_ID, false);
				player.getActionSender().sendUpdateItems(REWARDS_INTERFACE_ID, 1, 0,
						rewards.toArray(new Item[rewards.size()]));
				rewards.stream().forEach(i -> playerService.giveItem(player, i, true));
			} else {
				if (player.getAttribute("currentlyFightingBrother") == null
						&& player.getAttribute("looted_barrows") != Boolean.TRUE) {
					BarrowsBrother brother = player.getAttribute("barrows_tunnel");
					NPC spawnedBrother = new NPC(brother.getNpcId(), player.getLocation().transform(-1, 0, 0), null,
							null, 0);
					World.getWorld().register(spawnedBrother);
					spawnedBrother.setInstancedPlayer(player);
					spawnedBrother.getCombatState().startAttacking(player, false);
					player.setAttribute("currentlyFightingBrother", spawnedBrother);
				}
			}
			return true;
		}
		return true;
	}

}
