package org.rs2server.rs2.domain.service.api.content.magic;

import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.player.Player;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Clank1337
 */
public interface OrbChargingService {

	enum ChargeSpell {
		
		CHARGE_WATER(36, 2151, 56, 100, 571, 149, new Item(567), new Item(555, 30), new Item(564, 3)),
		
		CHARGE_EARTH(40, 2150, 60, 120, 575, 151, new Item(567), new Item(557, 30), new Item(564, 3)),
		
		CHARGE_FIRE(47, 2153, 63, 135, 569, 152, new Item(567), new Item(554, 30), new Item(564, 3)),
		
		CHARGE_AIR(50, 2152, 66, 150, 573, 150, new Item(567), new Item(556, 30), new Item(564, 3));

		private final int spellId;
		private final int objectId;
		private final int levelReq;
		private final int xp;
		private final int orbId;
		private final int gfx;
		private Item[] requiredItems;

		ChargeSpell(int spellId, int objectId, int levelReq, int xp, int orbId, int gfx, Item... requiredItems) {
			this.spellId = spellId;
			this.objectId = objectId;
			this.levelReq = levelReq;
			this.xp = xp;
			this.orbId = orbId;
			this.gfx = gfx;
			this.requiredItems = requiredItems;
		}

		public static Optional<ChargeSpell> of(int objectId, int spellId) {
			return Arrays.stream(values()).filter(o -> o.getObjectId() == objectId).filter(o -> o.spellId == spellId).findAny();
		}

		public int getSpellId() {
			return spellId;
		}

		public int getObjectId() {
			return objectId;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getXp() {
			return xp;
		}

		public int getOrbId() {
			return orbId;
		}

		public int getGFX() {
			return gfx;
		}

		public Item[] getRequiredItems() {
			return requiredItems;
		}
	}

	enum StaffType {
		
		WATER(571, 1395, 150, 54),
		
		EARTH(575, 1399, 175, 58),
		
		FIRE(569, 1393, 200, 62),
		
		AIR(573, 1397, 225, 66);

		private final int orbId;
		private final int staffId;
		private final int xp;
		private final int levelReq;

		StaffType(int orbId, int staffId, int xp, int levelReq) {
			this.orbId = orbId;
			this.staffId = staffId;
			this.xp = xp;
			this.levelReq = levelReq;
		}

		public static Optional<StaffType> of(int orbId) {
			return Arrays.stream(values()).filter(i -> i.getOrbId() == orbId).findAny();
		}

		public int getOrbId() {
			return orbId;
		}

		public int getStaffId() {
			return staffId;
		}

		public int getXp() {
			return xp;
		}

		public int getLevelReq() {
			return levelReq;
		}
	}

	class BattleStaffAction extends ProductionAction {

		private StaffType type;
		private int amount;

		public BattleStaffAction(Player player, StaffType type, int amount) {
			super(player);
			this.type = type;
			this.amount = amount;
		}

		@Override
		public int getCycleCount() {
			return 3;
		}

		@Override
		public int getProductionCount() {
			return amount;
		}

		@Override
		public Item[] getRewards() {
			return new Item[] {new Item(type.getStaffId())};
		}

		@Override
		public Item[] getConsumedItems() {
			return new Item[] {new Item(1391), new Item(type.getOrbId())};
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
			return type.getXp();
		}

		@Override
		public String getLevelTooLowMessage() {
			return "You need a Crafting level of " + type.getLevelReq() + " to craft this.";
		}

		@Override
		public String getSuccessfulProductionMessage() {
			return "";
		}

		@Override
		public Animation getAnimation() {
			return null;
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

	class OrbChargingAction extends ProductionAction {

		private final ChargeSpell spell;
		private final int amount;

		private static final Animation CHARGE_ANIMATION = Animation.create(726);

		public OrbChargingAction(Player player, ChargeSpell spell, int amount) {
			super(player);
			this.spell = spell;
			this.amount = amount;
		}

		@Override
		public int getCycleCount() {
			return 5;
		}

		@Override
		public int getProductionCount() {
			return amount;
		}

		@Override
		public Item[] getRewards() {
			return new Item[] {new Item(spell.getOrbId())};
		}

		@Override
		public Item[] getConsumedItems() {
			return spell.getRequiredItems();
		}

		@Override
		public int getSkill() {
			return Skills.MAGIC;
		}

		@Override
		public int getRequiredLevel() {
			return spell.getLevelReq();
		}

		@Override
		public double getExperience() {
			return spell.getXp();
		}

		@Override
		public String getLevelTooLowMessage() {
			return "You need a Magic level of " + spell.getLevelReq() + " to cast this spell.";
		}

		@Override
		public String getSuccessfulProductionMessage() {
			return "";
		}

		@Override
		public Animation getAnimation() {
			return CHARGE_ANIMATION;
		}

		@Override
		public Graphic getGraphic() {
			return Graphic.create(spell.getGFX(), 0, 100);
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
}
