package org.rs2server.rs2.content.wintertodt;

import org.rs2server.rs2.action.impl.SkillAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.Misc;
import org.rs2server.util.CycleState;

import java.util.*;

/**
 * Handles the brazier object within Wintertodt.
 * 
 * @author Vichy
 */
public class BrazierAction extends SkillAction {

	/**
	 * The lightable items.
	 */
	private final Lightables lightables;

	/**
	 * The brazier object.
	 */
	private final GameObject object;

	/**
	 * Constructing this skill action
	 * 
	 * @param player
	 * @param lightables
	 * @param object
	 */
	public BrazierAction(Player player, Lightables lightables, GameObject object) {
		super(player);
		this.lightables = lightables;
		this.object = object;
	}

	/**
	 * Enum containing all lightable items.
	 * 
	 * @author Vichy
	 *
	 */
	public enum Lightables {

		BRUMA_ROOT(20695, 90),

		BRUMA_KINDLING(20696, 45);

		private final int itemId;
		private final double exp;

		Lightables(int itemId, double exp) {
			this.itemId = itemId;
			this.exp = exp;
		}

		private static Map<Integer, Lightables> lightables = new HashMap<Integer, Lightables>();

		public static Lightables forId(int item) {
			return lightables.get(item);
		}

		static {
			for (Lightables lit : Lightables.values()) {
				lightables.put(lit.itemId, lit);
			}
		}

		public int getItem() {
			return itemId;
		}

		public double getExp() {
			return exp;
		}
	}

	@Override
	public boolean commence(Player player) {
		player.setAttribute("lighting", true);
		if (handleChecks(player))
			return true;
		return false;
	}

	@Override
	public boolean execute(Player player) {
		if (!handleChecks(player))
			return false;
		player.face(object.getCentreLocation());
		player.playAnimation(Animation.create(832));
		player.getInventory().remove(new Item(lightables.getItem(), 1));
		player.getSkills().addExperience(Skills.FIREMAKING, lightables.getExp());
		Inventory.addDroppable(player,
				new Item(20527, Misc.random((int) lightables.getExp() - 10, (int) lightables.getExp() + 10)));
		if (Misc.random(7) == 0)
			player.inflictDamage(new Hit((int) (player.getSkills().getLevelForExperience(Skills.HITPOINTS) * 0.25)),
					null);
		return true;
	}

	@Override
	public boolean finish(Player player) {
		if (!handleChecks(player))
			return true;
		setCycleState(CycleState.EXECUTE);
		return false;
	}

	/**
	 * Checks for various attributes on every cycle.
	 * 
	 * @param player
	 * @return
	 */
	private boolean handleChecks(Player player) {
		if (player.getAttribute("lighting") == null) {
			stop();
			player.removeAttribute("lighting");
			return false;
		}
		if (!player.getInventory().contains(lightables.getItem())) {
			player.sendMessage("You've ran out of bruma roots.");
			stop();
			player.removeAttribute("lighting");
			return false;
		}
		if (World.getWorld().getRegionManager().getGameObject(object.getLocation(), object.getId()) == null) {
			player.sendMessage("The fire has extinguished.");
			stop();
			player.removeAttribute("lighting");
			return false;
		}
		return true;
	}
}
