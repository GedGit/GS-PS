package org.rs2server.rs2.model.minigame.impl.bh;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author 'Mystic Flow
 */
public class BountyHunterNode {

	public static final int[][] TELEPORTS = { { 2794, 5650, 0 },
		{ 2818, 5653, 0 }, { 2826, 5665, 0 }, { 2843, 5680, 0 },
		{ 2851, 5685, 0 }, { 2860, 5692, 0 }, { 2862, 5696, 0 },
		{ 2866, 5716, 0 }, { 2864, 5738, 0 }, { 2861, 5768, 0 },
		{ 2851, 5785, 0 }, { 2850, 5794, 0 }, { 2843, 5801, 0 },
		{ 2815, 5806, 0 }, { 2773, 5808, 0 }, { 2757, 5808, 0 },
		{ 2737, 5802, 0 }, { 2726, 5795, 0 }, { 2707, 5783, 0 },
		{ 2702, 5765, 0 }, { 2705, 5759, 0 }, { 2703, 5717, 0 },
		{ 2702, 5704, 0 }, { 2707, 5690, 0 }, { 2712, 5676, 0 },
		{ 2717, 5666, 0 }, { 2724, 5654, 0 }, { 2740, 5649, 0 },
		{ 2752, 5652, 0 }, { 2772, 5648, 0 }
	};

	private Player player;
	private Player target;
	private BountyHunterCrater crater;

	private int skullId = -1;
	private int leavePenalty = -1;

	private boolean initiated;
	private int initiatedTime = 60;

	private Player lastTarget;

	public BountyHunterNode(Player player) {
		this.player = player;
	}

	public void setCrater(BountyHunterCrater crater) {
		this.crater = crater;
	}

	public void init(boolean teleportFlag) {
		initiated = true;
		if (!crater.getPlayers().contains(player))
			crater.getPlayers().add(player);
		int price = calculateTotalPrice();
		if (price >= 2500000 || price < 0) {
			skullId = 2;
		} else if (price <= 2500000 && price >= 1100000) {
			skullId = 3;
		} else if (price >= 500000 && price <= 1100000) {
			skullId = 4;
		} else if (price >= 100000 && price <= 500000) {
			skullId = 5;
		} else if (price <= 100000) {
			skullId = 6;
		}
		if (skullId != -1) {
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
		if (teleportFlag) {
			int teleportIndex = player.getRandom().nextInt(TELEPORTS.length);
			int[] teleport = TELEPORTS[teleportIndex];
			player.setTeleportTarget(Location.create(teleport[0], teleport[1], teleport[2]));
			initiatedTime = 0;
		}
		World.getWorld().submit(new Tickable(1) {
			@Override
			public void execute() {
				stop();
				player.getActionSender().sendWalkableInterface(653);
				player.getActionSender().sendInterfaceConfig(653, 11, (leavePenalty > 0 || player.pickupPenalty > 0) ? false : true);
				player.getActionSender().sendInterfaceConfig(653, 12, (leavePenalty > 0 || player.pickupPenalty > 0) ? false : true);
				if (player.pickupPenalty > 0) {
					player.getActionSender().sendString(653, 12, "Pickup penalty:");
					player.getActionSender().sendString(653, 13, player.pickupPenalty + " Sec");
				} else if (leavePenalty > 0) {
					player.getActionSender().sendString(653, 12, "Can't leave for:");
					player.getActionSender().sendString(653, 13, leavePenalty + " Sec");
				}
			}
		});
	}

	public void destroy() {
		crater.getPlayers().remove(player);
		player.getActionSender().sendWalkableInterface(-1);
		player.getActionSender().setHintIcon(10, -1, 0, -1);
		List<Location> teleports = new ArrayList<Location>();
		if (player.getAttribute("leavingBH") == null) {
			for (int x = 3168; x <= 3175; x++) {
				for (int y = 3674; y <= 3678; y++) {
					teleports.add(Location.create(x, y));
				}
			}
			player.setTeleportTarget(teleports.get(player.getRandom().nextInt(teleports.size())));
		} else {
			player.removeAttribute("leavingBH");
			player.setTeleportTarget(Location.create(3164, 3685, 0));
		}
		player.resetVariousInformation();
	}

	public Player find(int calls) {
		if (calls >= 5)
			return null;
		if (crater.getPlayers().size() == 0)
			return null;
		Player target = crater.getPlayers().get(player.getRandom().nextInt(crater.getPlayers().size()));
		int attempts = 5;
		while (target == player) {
			target = crater.getPlayers().get(player.getRandom().nextInt(crater.getPlayers().size()));
			attempts--;
			if (attempts == 0) {
				break;
			}
		}
		if (target == player)
			return null;
		int amount = 0;
		for (Player p : crater.getPlayers()) {
			if (p.getBountyHunter() != null && p.getBountyHunter().getTarget() == target) {
				amount++;
			}
		}
		if (amount >= 3 || (target.getBountyHunter() != null && target.getBountyHunter().initiatedTime > 0)) {
			return find(calls + 1);
		}
		return target;
	}

	public void tick() {
		if (!initiated) {
			if (initiatedTime == 60) {
				player.getActionSender().sendMessage("You shall be assigned a new target in 60 seconds.");
			}
			init(false);
			return;
		}
		if (!player.getInterfaceState().isWalkableInterface() && player.getInterfaceState().getCurrentInterface() == -1) {
			player.getActionSender().sendWalkableInterface(653);
		}
		if (player.pickupPenalty > 0) {
			player.pickupPenalty--;
			if (player.pickupPenalty % 6 == 0) {
				player.getActionSender().sendString(653, 13, player.pickupPenalty + " Sec");
			}
		} else if (player.pickupPenalty == 0) {
			player.pickupPenalty = -1;
			player.getActionSender().sendInterfaceConfig(653, 11, true);
			player.getActionSender().sendInterfaceConfig(653, 12, true);
		}
		if (leavePenalty > 0) {
			leavePenalty--;
			if (leavePenalty % 6 == 0) {
				player.getActionSender().sendString(653, 13, leavePenalty + " Sec");
			}
		} else if (leavePenalty == 0) {
			player.pickupPenalty = -1;
			leavePenalty = -1;
			player.getActionSender().sendInterfaceConfig(653, 11, true);
			player.getActionSender().sendInterfaceConfig(653, 12, true);
		}

		if (initiatedTime > 0) {
			initiatedTime--;
			return;
		}
		if (target == null) {
			target = find(0);
			if (target != null) {
				player.getActionSender().setHintIcon(10, target.getIndex(), 1, -1);
				player.getActionSender().sendString(653, 7, target.getName());
			} else {
				player.getActionSender().sendString(653, 7, "None");
			}
		}
		if (target != null) {
			player.getActionSender().sendString(653, 7, target.getName());
		} else {
			player.getActionSender().sendString(653, 7, "None");
		}
	}

	public int calculateTotalPrice() {
		int value = 0;
		Item[] inventory = player.getInventory().toArray();
		Item[] equipment = player.getEquipment().toArray();
		for (Item i : inventory) 
			if (i != null)
				value += (i.getCount() * i.getPrice());
		for (Item i : equipment) 
			if (i != null)
				value += (i.getCount() * i.getPrice());
		return value;
	}

	public int getSkullId() {
		return skullId;
	}

	public Player getTarget() {
		return target;
	}

	public Player getLastTarget() {
		return lastTarget;
	}

	public int getLeavePenalty() {
		return leavePenalty;
	}

	public void setLeavePenalty(int leavePenalty) {
		if (this.leavePenalty == -1 && leavePenalty != -1) {
			player.getActionSender().sendMessage("You should not be picking up items. Now you must wait before you can leave.");
		}
		this.leavePenalty = leavePenalty;
		if (leavePenalty != -1) {
			player.pickupPenalty = -1;
			player.getActionSender().sendInterfaceConfig(653, 11, false);
			player.getActionSender().sendInterfaceConfig(653, 12, false);
			player.getActionSender().sendString(653, 12, "Can't leave for:");
			player.getActionSender().sendString(653, 13, "180 Sec");
			if (player.getCombatState().getPrayer(Prayers.PROTECT_ITEM))
				Prayers.activatePrayer(player, Prayers.PROTECT_ITEM);
		} else {
			player.getActionSender().sendInterfaceConfig(653, 11, true);
			player.getActionSender().sendInterfaceConfig(653, 12, true);
		}
	}
	
	public void setLeavePenalty2(int leavePenalty) {
		this.leavePenalty = leavePenalty;
	}

	public BountyHunterCrater getCrater() {
		return crater;
	}

	public void setTarget(Player target) {
		this.target = target;
		if (target == null) {
			player.getActionSender().setHintIcon(10, -1, 0, -1);
		}
	}

	public void setLastTarget(Player mob) {
		this.lastTarget = mob;

	}

	public void updateSkull() {
		int price = calculateTotalPrice();
		if (price >= 2500000 || price < 0) {
			skullId = 2;
		} else if (price <= 2500000 && price >= 1100000) {
			skullId = 3;
		} else if (price >= 500000 && price <= 1100000) {
			skullId = 4;
		} else if (price >= 100000 && price <= 500000) {
			skullId = 5;
		} else if (price <= 100000) {
			skullId = 6;
		}
		if (skullId != -1) {
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
	}

	public void setPickupPenalty(boolean pickupPenalty) {
		if (leavePenalty != -1) {
			return;
		}
		player.pickupPenalty = 180;
		player.getActionSender().sendInterfaceConfig(653, 11, false);
		player.getActionSender().sendInterfaceConfig(653, 12, false);
		player.getActionSender().sendString(653, 12, "Pickup penalty:");
		player.getActionSender().sendString(653, 13, "180 Sec");
		if (player.getCombatState().getPrayer(Prayers.PROTECT_ITEM))
			Prayers.activatePrayer(player, Prayers.PROTECT_ITEM);
	}
}
