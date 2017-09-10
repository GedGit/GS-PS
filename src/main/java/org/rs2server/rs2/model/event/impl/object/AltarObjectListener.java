package org.rs2server.rs2.model.event.impl.object;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.event.ClickEventManager;
import org.rs2server.rs2.model.event.EventListener;
import org.rs2server.rs2.model.player.Player;

/**
 *
 * @author `Discardedx2 (Shawn)
 */
public class AltarObjectListener extends EventListener {

	@Override
	public void register(ClickEventManager manager) {
		manager.registerObjectListener(47120, this);// sennestian altar
		manager.registerObjectListener(409, this);// normal altar
		manager.registerObjectListener(17010, this);// lunar altar
		manager.registerObjectListener(6552, this);// ancient altar
	}

	@Override
	public boolean objectAction(final Player player, int objectId, GameObject gameObject, Location location,
			ClickOption option) {
		switch (objectId) {
		default:
			player.playAnimation(Animation.create(645));
			switch (objectId) {
			case 6552:
				if (player.getCombatState().getSpellBook() != 193) {
					player.getCombatState().setSpellBook(193);
					player.getActionSender().sendMessage("You feel a strange wisdom fill your mind...");
					return true;
				}
				player.getCombatState().setSpellBook(192);
				player.getActionSender().sendMessage("You revert to modern magic.");
				return true;
			case 17010:
				if (option == ClickOption.SECOND) {
					if (player.getCombatState().getSpellBook() != 430) {
						player.getCombatState().setSpellBook(430);
						player.getActionSender().sendMessage("Lunar spells activated!");
						return true;
					}
					player.getCombatState().setSpellBook(192);
					player.getActionSender().sendMessage("You revert to modern magic.");
					return true;
				}
				return false;
			default:
				double amt = player.getSkills().getLevel(Skills.PRAYER);

				if ((amt + player.getSkills().getPrayerPoints()) > amt) {
					amt = amt - player.getSkills().getPrayerPoints();
				}
				player.getSkills().increasePrayerPoints(amt);
				player.getActionSender().sendMessage("You recharge your prayer points.");
				return true;
			}
		}
	}
}