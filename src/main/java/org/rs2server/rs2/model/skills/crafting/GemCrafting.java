package org.rs2server.rs2.model.skills.crafting;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.player.Player;

import java.util.HashMap;
import java.util.Map;

public class GemCrafting extends ProductionAction {

	CraftType type;
	int productionCount;

	public GemCrafting(Mob mob, CraftType type, int productionCount) {
		super(mob);
		this.type = type;
		this.productionCount = productionCount;
	}

	public enum CraftType {

		GOLD_RING(null, 5, 15, new Item(1635)), GOLD_BRACELET(null, 4, 25, new Item(11068)), GOLD_NECKLACE(null, 6, 20,
				new Item(1654)), GOLD_AMULET(null, 6, 20, new Item(1673)),

		SAPPHIRE_RING(new Item(1607), 20, 40, new Item(1637)), SAPPHIRE_NECKLACE(new Item(1607), 22, 55,
				new Item(1656)), SAPPHIRE_BRACELET(new Item(1607), 23, 60,
						new Item(11071)), SAPPHIRE_AMULET(new Item(1607), 24, 65, new Item(1675)),

		EMERALD_RING(new Item(1605), 27, 55, new Item(1639)), EMERALD_NECKLACE(new Item(1605), 29, 60,
				new Item(1658)), EMERALD_BRACELET(new Item(1605), 30, 65,
						new Item(11078)), EMERALD_AMULET(new Item(1605), 31, 70, new Item(1677)),

		RUBY_RING(new Item(1603), 34, 70, new Item(1641)), RUBY_NECKLACE(new Item(1603), 40, 75,
				new Item(1660)), RUBY_BRACELET(new Item(1603), 42, 80,
						new Item(11087)), RUBY_AMULET(new Item(1603), 50, 85, new Item(1679)),

		DIAMOND_RING(new Item(1601), 43, 85, new Item(1643)), DIAMOND_NECKLACE(new Item(1601), 56, 90,
				new Item(1662)), DIAMOND_BRACELET(new Item(1601), 58, 95,
						new Item(11094)), DIAMOND_AMULET(new Item(1601), 70, 100, new Item(1681)),

		DRAGONSTONE_RING(new Item(1615), 55, 100, new Item(1645)), DRAGONSTONE_NECKLACE(new Item(1615), 72, 105,
				new Item(1664)), DRAGONSTONE_BRACELET(new Item(1615), 74, 110,
						new Item(11117)), DRAGONSTONE_AMULET(new Item(1615), 80, 150, new Item(1683)),

		ONYX_RING(new Item(6573), 67, 115, new Item(6575)), ONYX_NECKLACE(new Item(6573), 82, 120,
				new Item(6577)), ONYX_BRACELET(new Item(6573), 84, 125,
						new Item(11132)), ONYX_AMULET(new Item(6573), 90, 165, new Item(6579)),

		ZENYTE_RING(new Item(19493), 89, 150, new Item(19538)),

		ZENYTE_NECKLACE(new Item(19493), 92, 165, new Item(19535)),

		ZENYTE_BRACELET(new Item(19493), 95, 180, new Item(19492)),

		ZENYTE_AMULET(new Item(19493), 98, 200, new Item(19501));

		Item gem;
		int levelReq;
		int experience;
		Item reward;

		private CraftType(Item gem, int levelReq, int experience, Item reward) {
			this.gem = gem;
			this.levelReq = levelReq;
			this.experience = experience;
			this.reward = reward;
		}

		private static Map<Integer, CraftType> items = new HashMap<Integer, CraftType>();

		public static CraftType forId(Item item) {
			return items.get(item.getId());
		}

		static {
			for (CraftType gem : CraftType.values()) {
				items.put(gem.reward.getId(), gem);
			}
		}

		public Item getGem() {
			return gem;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getXP() {
			return experience;
		}

		public Item getReward() {
			return reward;
		}

	}

	@Override
	public int getCycleCount() {
		return 3;
	}

	@Override
	public int getProductionCount() {
		return productionCount;
	}

	@Override
	public Item[] getRewards() {
		return new Item[] { type.getReward() };
	}

	@Override
	public Item[] getConsumedItems() {
		return new Item[] { type.getGem(), new Item(2357) };
	}

	@Override
	public int getSkill() {
		return Skills.CRAFTING;
	}

	@Override
	public int getRequiredLevel() {
		return type.getLevelReq();
	}

	@Override
	public double getExperience() {
		return type.getXP();
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a crafting level of " + type.getLevelReq() + " to craft this.";
	}

	@Override
	public String getSuccessfulProductionMessage() {
		return "You successfully craft a " + CacheItemDefinition.get(type.getReward().getId()).getName() + ".";
	}

	@Override
	public Animation getAnimation() {
		return Animation.create(3243);
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	@Override
	public boolean canProduce() {
		return true;
	}

	@Override
	public boolean isSuccessfull() {
		return true;
	}

	@Override
	public String getFailProductionMessage() {
		return null;
	}

	@Override
	public Item getFailItem() {
		return null;
	}

	/**
	 * Int array representing raw materials and products.
	 */
	public static int[][] ITEMS = { { 1673, 1692 }, { 1675, 1694 }, { 1677, 1696 }, { 1679, 1698 }, { 1681, 1700 },
			{ 1683, 1702 }, { 6579, 6581 }, { 1714, 1716 }, { 19501, 19541 } };

	/**
	 * Handles item combining (amulet stringing).
	 * 
	 * @param player
	 *            the player combining.
	 * @param rawProductId
	 *            the raw item used.
	 */
	public static void combine(final Player player, final int rawProductId) {
		if (!player.getInventory().contains(1759) || !player.getInventory().contains(ITEMS[rawProductId][0]))
			return;
		player.getInventory().remove(new Item(ITEMS[rawProductId][0], 1));
		player.getInventory().remove(new Item(1759, 1));
		player.getInventory().add(new Item(ITEMS[rawProductId][1], 1));
		player.getActionSender().sendMessage("You attach a string to the "
				+ CacheItemDefinition.get(ITEMS[rawProductId][0]).getName().toLowerCase() + ".");
	}

}
