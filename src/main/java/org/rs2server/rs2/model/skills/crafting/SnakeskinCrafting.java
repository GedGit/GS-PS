package org.rs2server.rs2.model.skills.crafting;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.action.impl.SkillAction;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.TextUtils;
import org.rs2server.util.CycleState;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tim on 12/4/2015.
 */
public class SnakeskinCrafting {

	public static final Animation CRAFT_ANIMATION = Animation.create(1249);

	public static class SnakeskinCraftingAction extends SkillAction {

		private Snakeskin toProduce;
		private int amount;

		public SnakeskinCraftingAction(Player player, Snakeskin toProduce, int amount) {
			super(player);
			this.toProduce = toProduce;
			this.amount = amount;
		}

		@Override
		public boolean commence(Player player) {
			if (player.getSkills().getLevel(Skills.CRAFTING) < toProduce.getLevelReq()) {
				StringBuilder sb = new StringBuilder("You need a Crafting level of ").append(toProduce.getLevelReq());
				sb.append(" to make a ")
						.append(CacheItemDefinition.get(toProduce.getReward().getId()).getName().toLowerCase())
						.append(".");
				player.getActionSender().sendMessage(sb.toString());
				return false;
			}
			if (!player.getInventory().contains(THREAD.getId())) {
				player.getActionSender().sendMessage("You have no threads to craft with!");
				return false;
			}

			player.getActionSender().removeInterface();
			setTickDelay(2);
			return true;
		}

		@Override
		public boolean execute(Player player) {
			if (amount > 0) {
				if (!player.getInventory().contains(THREAD.getId())) {
					amount = 0;
					player.getActionSender().sendMessage("You have run out of threads!");
				} else if (!player.getInventory().contains(NEEDLE.getId())) {
					amount = 0;
					player.getActionSender().sendMessage("You don't have a needle to craft with!");
				} else if (player.getInventory().getCount(SNAKESKIN) < toProduce.getSkinAmount()) {
					amount = 0;
					player.getActionSender()
							.sendMessage("You have ran out of " + CacheItemDefinition.get(SNAKESKIN).getName() + ".");
					return false;
				}
				player.playAnimation(CRAFT_ANIMATION);
			}
			return true;
		}

		@Override
		public boolean finish(Player player) {
			if (amount < 1) {
				return true;
			}
			amount--;
			if (player.getRandom().nextDouble() >= 0.80) {
				player.getActionSender().sendMessage("You use up one of your reels of thread.");
				player.getInventory().remove(THREAD);
			}
			if (player.getRandom().nextDouble() >= 0.97) {
				player.getActionSender().sendMessage("Your needle broke.");
				player.getInventory().remove(NEEDLE);
			}
			StringBuilder createdMessage = new StringBuilder("You make a ");
			String name = CacheItemDefinition.get(toProduce.getReward().getId()).getName();
			if (name.endsWith("s")) {
				createdMessage.append(name.substring(0, name.length() - 1));
			} else {
				createdMessage.append(name.toLowerCase());
			}
			createdMessage.append(".");
			player.getActionSender().sendMessage(createdMessage.toString());
			player.getInventory().remove(new Item(SNAKESKIN, toProduce.getSkinAmount()));
			player.getInventory().add(toProduce.getReward());
			player.getSkills().addExperience(Skills.CRAFTING, toProduce.getXp());
			setCycleState(CycleState.EXECUTE);
			return false;
		}
	}

	public enum Snakeskin {

		BODY(new Item(6322), 15, 53, 95),

		CHAPS(new Item(6324), 12, 51, 90),

		VAMBRACES(new Item(6330), 8, 47, 75),

		BANDANA(new Item(6326), 5, 48, 85),

		BOOTS(new Item(6328), 6, 45, 70);

		private Item reward;
		private int skinAmount;
		private int levelReq;
		private int xp;

		Snakeskin(Item reward, int skinAmount, int levelReq, int xp) {
			this.reward = reward;
			this.skinAmount = skinAmount;
			this.levelReq = levelReq;
			this.xp = xp;
		}

		private static Map<Integer, Snakeskin> snakeskinMap = new HashMap<>();

		public static Snakeskin of(int id) {
			return snakeskinMap.get(id);
		}

		static {
			for (Snakeskin skin : Snakeskin.values()) {
				snakeskinMap.put(skin.getReward().getId(), skin);
			}
		}

		public Item getReward() {
			return reward;
		}

		public int getSkinAmount() {
			return skinAmount;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getXp() {
			return xp * 2;
		}
	}

	public static int SNAKESKIN = 6289;
	public static Item NEEDLE = new Item(1733);
	public static Item THREAD = new Item(1734);

	public static boolean handleItemOnItem(Player player, Item itemUsed, Item usedWith) {
		Item item;
		if (itemUsed.getId() == NEEDLE.getId() || usedWith.getId() == NEEDLE.getId()) {
			item = usedWith;
		} else {
			return false;
		}
		if (item.getId() != SNAKESKIN) {
			return false;
		}
		int child = 2;
		int textChild = 10;
		for (Snakeskin skin : Snakeskin.values()) {
			player.getActionSender().sendItemOnInterface(306, child++, skin.getReward().getId(), 175);
			String name = TextUtils
					.upperFirst(CacheItemDefinition.get(skin.getReward().getId()).getName().replace("Snakeskin ", ""));
			player.getActionSender().sendString(306, textChild, "<br><br><br><br>" + name);
			textChild += 4;
		}
		player.getActionSender().sendChatboxInterface(306);
		player.setAttribute("snakeskin_crafting", Boolean.TRUE);
		return false;
	}
}
