package org.rs2server.rs2.model.skills;

import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.player.Player;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tim on 10/20/2015.
 */
public class AltarAction extends ProductionAction {

	private BoneType type;
	private int amount;

	public AltarAction(Mob mob, BoneType type, int amount, GameObject object) {
		super(mob);
		this.type = type;
		this.amount = amount;
	}

	public static boolean handleItemOnObject(Player player, GameObject obj, Item item) {
		BoneType type = BoneType.forId(item.getId());
		if (type == null)
			return false;
		if (obj.getId() != 29150) {
			player.sendMessage("You can only use your bones on the Altar of the Occult at home.");
			return false;
		}
		player.getActionQueue().addAction(new AltarAction(player, type, player.getInventory().getCount(type.getId()), obj));
		return true;
	}

	public enum BoneType {

		NORMAL_BONES(526, 4.5),

		BURNT_BONES(528, 4),

		BAT_BONES(530, 5),

		BIG_BONES(532, 15),

		BABYDRAGON_BONES(534, 30),

		DRAGON_BONES(536, 72),

		LAVA_DRAGON_BONES(11943, 85),

		WOLF_BONES(2859, 4),

		JOGRE_BONES(3125, 15),

		DAGANNOTH_BONES(6729, 125),

		WYVERN_BONES(6812, 72),

		SHAIKAHAN_BONES(3123, 25),

		OURG_BONES(4834, 140);

		private int id;
		private double xp;

		public int getId() {
			return id;
		}

		public double getXp() {
			return xp * 3;
		}

		private BoneType(int id, double xp) {
			this.id = id;
			this.xp = xp;
		}

		private static Map<Integer, BoneType> bones = new HashMap<Integer, BoneType>();

		public static BoneType forId(int bone) {
			return bones.get(bone);
		}

		static {
			for (BoneType type : BoneType.values()) {
				bones.put(type.getId(), type);
			}
		}
	}

	@Override
	public int getCycleCount() {
		return 2;
	}

	@Override
	public int getProductionCount() {
		return amount;
	}

	@Override
	public Item[] getRewards() {
		return null;
	}

	@Override
	public Item[] getConsumedItems() {
		return new Item[] { new Item(type.getId(), 1) };
	}

	@Override
	public int getSkill() {
		return Skills.PRAYER;
	}

	@Override
	public int getRequiredLevel() {
		return 0;
	}

	@Override
	public double getExperience() {
		return type.getXp();
	}

	@Override
	public String getLevelTooLowMessage() {
		return null;
	}

	@Override
	public String getSuccessfulProductionMessage() {
		return "The gods are very pleased with your offering.";
	}

	@Override
	public Animation getAnimation() {
		return Animation.create(896);
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
}
