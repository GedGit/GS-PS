package org.rs2server.rs2.model.skills;

import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.player.Player;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Life on 28 jan 17
 */
public class BlastCrafting extends ProductionAction {

    private BoneType type;
    private int amount;

    public BlastCrafting(Mob mob, BoneType type, int amount) {
        super(mob);
        this.type = type;
        this.amount = amount;
    }

    public static boolean handleItemOnObject(Player player, GameObject obj, Item item) {
        BoneType type = BoneType.forId(item.getId());
        if (type == null || obj.getId() != 28582)
            return false;
        player.getActionQueue().addAction(new BlastCrafting(player, type, player.getInventory().getCount(type.getId())));
        return false;
    }


    private enum BoneType {

        DYNAMITE(13573, 400);

        private int id;
        private int xp;

        public int getId() {
            return id;
        }

        public int getXp() {
            return xp;
        }

        private BoneType(int id, int xp) {
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
        return new Item[] {new Item(type.getId(), 1)};
    }

    @Override
    public int getSkill() {
        return Skills.CRAFTING;
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
        return "Default TExt.";
    }

    @Override
    public Animation getAnimation() {
        return Animation.create(7199);
    }

    @Override
    public Graphic getGraphic() {
        return Graphic.create(624);
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
