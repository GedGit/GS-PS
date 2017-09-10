package org.rs2server.rs2.model.skills.herblore;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.player.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tim on 12/19/2015.
 */
public class SuperCombatPotion extends ProductionAction {

    private static final int SUPER_COMBAT = 12695;

    public enum Ingredients {
    	
        ATTACK(2436),

        STRENGTH(2440),

        DEFENCE(2442),

        TORSTOL(269);

        private int itemId;

        Ingredients(int itemId) {
            this.itemId = itemId;
        }


        private static Map<Integer, Ingredients> ingredients = new HashMap<>();

        public static Ingredients forId(int itemId) {
            return ingredients.get(itemId);
        }

        static {
            for(Ingredients ingredient : Ingredients.values()) {
                ingredients.put(ingredient.getItemId(), ingredient);
            }
        }


        public int getItemId() {
            return itemId;
        }
    }

    private int amount;

    public SuperCombatPotion(Mob mob, int amount) {
        super(mob);
        this.amount = amount;
    }

    public static boolean handleItemOnItem(Player player, Item one, Item two) {
        Ingredients ingredOne = Ingredients.forId(one.getId());
        Ingredients ingredTwo = Ingredients.forId(two.getId());
        if (ingredOne == null || ingredTwo == null) {
            return false;
        }
        player.getActionSender().sendItemOnInterface(309, 2,
                SUPER_COMBAT, 130);
        String itemName = CacheItemDefinition.get(SUPER_COMBAT).getName();
        player.getActionSender().sendString(309, 6,
                "<br><br><br><br>" + itemName);
        player.getActionSender().sendInterface(162, 546, 309, false);
        player.setInterfaceAttribute("superCombat", true);
        return true;
    }

    @Override
    public int getCycleCount() {
        return 4;
    }

    @Override
    public int getProductionCount() {
        return amount;
    }

    @Override
    public Item[] getRewards() {
        return new Item[] {new Item(SUPER_COMBAT)};
    }

    @Override
    public Item[] getConsumedItems() {
        return new Item[] {new Item(269), new Item(2436), new Item(2440), new Item(2442)};
    }

    @Override
    public int getSkill() {
        return Skills.HERBLORE;
    }

    @Override
    public int getRequiredLevel() {
        return 90;
    }

    @Override
    public double getExperience() {
        return 300;
    }

    @Override
    public String getLevelTooLowMessage() {
        return "You need a " + Skills.SKILL_NAME[getSkill()] + " level of " + getRequiredLevel() + " to combine these ingredients.";
    }

    @Override
    public String getSuccessfulProductionMessage() {
        return "You mix the torstol into your potion.";
    }

    @Override
    public Animation getAnimation() {
        return Animation.create(363);
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
