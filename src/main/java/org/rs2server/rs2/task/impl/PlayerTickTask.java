package org.rs2server.rs2.task.impl;

import org.rs2server.rs2.action.impl.WieldItemAction;
import org.rs2server.rs2.content.Following;
import org.rs2server.rs2.domain.service.impl.content.bounty.BountyHunterServiceImpl;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.Hit.HitPriority;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.map.path.DefaultPathFinder;
import org.rs2server.rs2.model.map.path.ProjectilePathFinder;
import org.rs2server.rs2.model.npc.impl.kraken.Whirlpool;
import org.rs2server.rs2.model.npc.impl.zulrah.Zulrah;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.task.Task;
import org.rs2server.util.functional.Optionals;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

/**
 * A task which is executed before an <code>UpdateTask</code>. It is similar to
 * the call to <code>process()</code> but you should use <code>Event</code>s
 * instead of putting timers in this class.
 * 
 * @author Graham Edgecombe
 *
 */
public class PlayerTickTask implements Task {

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * Creates a tick task for a player.
	 * 
	 * @param player
	 *            The player to create the tick task for.
	 */
	public PlayerTickTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute() {
		player.processPackets();
		player.getCombatState().calculateBonuses();
		Queue<ChatMessage> messages = player.getChatMessageQueue();
		if (messages.size() > 0) {
			player.getUpdateFlags().flag(UpdateFlag.CHAT);
			ChatMessage message = player.getChatMessageQueue().poll();
			player.setCurrentChatMessage(message);
		} else
			player.setCurrentChatMessage(null);
		if (player.getCombatState().getAttackDelay() > 0)
			player.getCombatState().decreaseAttackDelay(1);
		if (player.getCombatState().getSpellDelay() > 0)
			player.getCombatState().decreaseSpellDelay(1);
		if (player.getCombatState().getWeaponSwitchTimer() > 0)
			player.getCombatState().decreaseWeaponSwitchTimer(1);
		if (player.getCombatState().getSkullTicks() > 0)
			player.getCombatState().decreaseSkullTicks(1);
		if (player.getCombatState().getEatDelay() > 0)
			player.getCombatState().decreaseEatDelay(1);
		if (player.getDatabaseEntity().getPlayerSettings().getTeleBlockTimer() > 0) {
			player.getDatabaseEntity().getPlayerSettings().decreaseTeleBlockTimer(1);
			if (player.getDatabaseEntity().getPlayerSettings().getTeleBlockTimer() <= 0)
				player.getDatabaseEntity().getPlayerSettings().setTeleBlocked(false);
		}

		Integer weaponSwitchSlot;
		while ((weaponSwitchSlot = player.getWeaponSwitchQueue().poll()) != null) {
			Item item = player.getInventory().get(weaponSwitchSlot);
			if (item != null) // TODO Improve this ...
				new WieldItemAction(player, item.getId(), weaponSwitchSlot, 0).execute();
		}
		if (player.hasAttribute("antiFire")) {
			if (System.currentTimeMillis() - (long) player.getAttribute("antiFire", 0L) < 360000) {
				if (System.currentTimeMillis() - (long) player.getAttribute("antiFire", 0L) > 15000
						&& System.currentTimeMillis() - (long) player.getAttribute("aantiFire", 0L) < 14000) {
					player.getActionSender().sendMessage("<col=ff0000>Your anti fire potion is about to wear off!");
				}
			} else if ((long) player.getAttribute("antiFire", 0L) > 0L
					&& System.currentTimeMillis() - (long) player.getAttribute("antiFire", 0L) > 360000) {
				player.getActionSender().sendMessage("<col=ff0000>Your resistance to dragon breath has worn off!");
				player.removeAttribute("antiFire");
			}
		}
		player.getFightCave().tick();

		player.getRFD().tick();

		player.getMageArena().tick();

		player.tick();
		player.processTicks();

		if (BountyHunterServiceImpl.WILDERNESS_PLAYER_LIST.contains(player) && !player.isInWilderness()
				&& player.getBountyTarget() == null) {
			BountyHunterServiceImpl.WILDERNESS_PLAYER_LIST.remove(player);
		} else if (player.isInWilderness() && !BountyHunterServiceImpl.WILDERNESS_PLAYER_LIST.contains(player)) {
			BountyHunterServiceImpl.WILDERNESS_PLAYER_LIST.add(player);
		}

		/*
		 * Gets the next two hits from the queue.
		 */
		List<Hit> hits = player.getHitQueue();
		Hit first = null;
		if (hits.size() > 0) {
			for (int i = 0; i < hits.size(); i++) {
				Hit hit = hits.get(i);
				if (hit.getDelay() < 1) {
					first = hit;
					hits.remove(hit);
					break;
				}
			}
		}
		if (first != null) {
			player.setPrimaryHit(first);
			player.getUpdateFlags().flag(UpdateFlag.HIT);
		}
		Hit second = null;
		if (hits.size() > 0) {
			for (int i = 0; i < hits.size(); i++) {
				Hit hit = hits.get(i);
				if (hit.getDelay() < 1) {
					second = hit;
					hits.remove(hit);
					break;
				}
			}
		}
		if (second != null) {
			player.setSecondaryHit(second);
			player.getUpdateFlags().flag(UpdateFlag.HIT_2);
		}
		if (hits.size() > 0) {// tells us we still have more hits
			Iterator<Hit> hitIt = hits.iterator();
			while (hitIt.hasNext()) {
				Hit hit = hitIt.next();
				if (hit.getDelay() > 0) {
					hit.setDelay(hit.getDelay() - 1);
				}
				if (hit.getHitPriority() == HitPriority.LOW_PRIORITY) {
					hitIt.remove();
				}
			}
		}
		if (player.getInteractingEntity() != null && player.getInteractionMode() == InteractionMode.ATTACK) {
			if (player.getCombatState().canMove()) {
				int distance = player.getLocation().distanceToEntity(player, player.getInteractingEntity());
				int requiredDistance = player.getActiveCombatAction().distance(player.getInteractingEntity());
				if (player.getAttribute("magicMove") != null)
					requiredDistance = 8;
				Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
				if (weapon != null && weapon.getDefinition2().getName().contains("knife"))
					requiredDistance = 4; // 4 seems to work :thumbs_up:
				while (true) {
					if (requiredDistance != 1) {
						if (!ProjectilePathFinder.clippedProjectile(player, player.getInteractingEntity())
								&& !(player.getInteractingEntity() instanceof Zulrah)
								&& !(player.getInteractingEntity() instanceof Whirlpool)) {
							Following.combatFollow(player, player.getInteractingEntity());
							break;
						}
						if (player.getAttribute("positionMove") == null)
							player.getWalkingQueue().reset();
						else
							player.removeAttribute("positionMove");
					}
					if (distance > requiredDistance)
						Following.combatFollow(player, player.getInteractingEntity());
					break;
				}
			}
		}
		player.getWalkingQueue().processNextMovement();
		if (player.getInteractingEntity() != null && player.getInteractionMode() == InteractionMode.FOLLOW) {
			if (player.getCombatState().canMove()) {
				if (!player.getLocation().isWithinDistance(player.getInteractingEntity().getLocation())) {
					player.resetInteractingEntity();
					return;
				}

				Location last = player.getInteractingEntity().getLastLocation();
				if (last.equals(player.getInteractingEntity().getLocation())) {
					Optional<Location> lastOption = Optionals.nearbyFreeLocation(last);

					if (lastOption.isPresent())
						last = lastOption.get();
				}
				World.getWorld().doPath(new DefaultPathFinder(), player, last.getX(), last.getY());
			}
		}
	}
}
