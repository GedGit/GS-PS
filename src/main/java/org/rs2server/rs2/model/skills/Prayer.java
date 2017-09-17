package org.rs2server.rs2.model.skills;

import java.util.HashMap;
import java.util.Map;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;

/**
 * For prayer related activities.
 *
 * @author Tyluur <ItsTyluur@gmail.com
 */
public class Prayer {

    public void prayAltar() {
        if (player.getSkills().getPrayerPoints() >= player.getSkills().getLevelForExperience(Skills.PRAYER)) {
            player.getActionSender().sendMessage("You already have full prayer points.");
            return;
        }
        player.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevelForExperience(Skills.PRAYER));
        player.getSkills().setPrayerPoints(player.getSkills().getLevelForExperience(Skills.PRAYER), true);
        if (player.getActionSender() != null)
            player.getActionSender().sendSkills();
        player.getActionSender().sendMessage("You pray at the altar...");
        player.playAnimation(Animation.create(645));
    }

    public boolean handleBury(final Item bone, int slot) {
    	BoneType type = BoneType.forId(bone.getId());
		if (type == null)
			return false;
        if (player.getAttribute("can_bury") == null)
            player.setAttribute("can_bury", true);
        if (!(Boolean) (player.getAttribute("can_bury")))
            return false;
        player.setAttribute("can_bury", false);
        player.playAnimation(Animation.create(827));
        player.getActionSender().sendMessage("You dig a hole in the ground...");
        player.getInventory().remove(bone, slot);
        World.getWorld().submit(new Event(1250) {
            public void execute() {
                player.getSkills().addExperience(Skills.PRAYER, type.getXp());
                player.getActionSender().sendMessage("...you bury the bones.");
                player.setAttribute("can_bury", true);
                stop();
            }
        });
        return true;
    }

    private enum BoneType {

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
			return xp;
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

    private Player player;

    /**
     * Constructs a new prayer skill.
     *
     * @param player For this player.
     */
    public Prayer(Player player) {
        this.player = player;
    }
}
