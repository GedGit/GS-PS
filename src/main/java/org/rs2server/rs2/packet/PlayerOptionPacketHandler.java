package org.rs2server.rs2.packet;

import org.rs2server.Server;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.domain.service.api.PathfindingService;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.Spell;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.SpellBook;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.SpellType;
import org.rs2server.rs2.model.container.*;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.player.RequestManager.RequestType;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.util.Misc;

public class PlayerOptionPacketHandler implements PacketHandler {

	private static final int OPTION_ATTACK = 250, OPTION_CHALLENGE = 39, OPTION_2 = 111, OPTION_3 = 239, OPTION_4 = 247,
			OPTION_5 = 65, OPTION_SPELL = 150, ITEM_ON_PLAYER = 170;
	private final PathfindingService pathfindingService;

	public PlayerOptionPacketHandler() {
		this.pathfindingService = Server.getInjector().getInstance(PathfindingService.class);
	}

	@Override
	public void handle(Player player, Packet packet) {

		if (player != null) {
			boolean starter = player.getAttribute("starter");
			if ((!player.isIronMan() && !player.isHardcoreIronMan() && !player.isUltimateIronMan()) && starter) {
				DialogueManager.openDialogue(player, 19000);
				return;
			}
		}
		if (player.getAttribute("cutScene") != null || player.getAttribute("busy") != null)
			return;
		if (player.getInterfaceAttribute("fightPitOrbs") != null)
			return;
		if (player.isLighting())
			return;
		if (player.getInterfaceState().isInterfaceOpen(Equipment.SCREEN)
				|| player.getInterfaceState().isInterfaceOpen(Bank.BANK_INVENTORY_INTERFACE)
				|| player.getInterfaceState().isInterfaceOpen(Trade.TRADE_INVENTORY_INTERFACE)
				|| player.getInterfaceState().isInterfaceOpen(Shop.SHOP_INVENTORY_INTERFACE)
				|| player.getInterfaceState().isInterfaceOpen(PriceChecker.PRICE_INVENTORY_INTERFACE))
			player.getActionSender().removeInventoryInterface();
		player.getActionQueue().clearAllActions();
		player.getActionManager().stopAction();
		player.resetAfkTolerance();
		switch (packet.getOpcode()) {
		case OPTION_ATTACK:
			optionAttack(player, packet);
			break;
		case OPTION_2:
			option2(player, packet);
			break;
		case OPTION_3:
			option3(player, packet);
			break;
		case OPTION_4:
			option4(player, packet);
			break;
		case OPTION_5:
			option5(player, packet);
			break;
		case OPTION_SPELL:
			optionSpell(player, packet);
			break;
		case ITEM_ON_PLAYER:
			itemOnPlayer(player, packet);
			break;
		case OPTION_CHALLENGE:
			optionChallenge(player, packet);
			break;
		}
	}

	private void optionChallenge(Player player, Packet packet) {
		int id = packet.getShort();
		packet.get();
		if (id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		if (player.getCombatState().isDead()) {
			return;
		}
		Player other = (Player) World.getWorld().getPlayers().get(id);
		Action action = new Action(player, 0) {

			@Override
			public CancelPolicy getCancelPolicy() {
				return CancelPolicy.ALWAYS;
			}

			@Override
			public StackPolicy getStackPolicy() {
				return StackPolicy.NEVER;
			}

			@Override
			public AnimationPolicy getAnimationPolicy() {
				return AnimationPolicy.RESET_ALL;
			}

			@Override
			public void execute() {
				this.stop();
				/*
				 * if (BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "DuelArena") &&
				 * BoundaryManager.isWithinBoundaryNoZ(other.getLocation(), "DuelArena")) {
				 * player.getRequestManager().request(RequestType.DUEL, other); }
				 */
			}
		};
		player.addCoordinateAction(player.getWidth(), player.getHeight(), other.getLocation(), 1, 1, 1, action);
		pathfindingService.travelToPlayer(player, other);
	}

	@SuppressWarnings("unused")
	private void itemOnPlayer(Player player, Packet packet) {
		int slot = packet.getShortA();
		int interfaceHash = packet.getBEInt();
		int itemId = packet.getLEShortA();
		int index = packet.getShortA();
		int e = packet.getByteA();
		int interfaceId = interfaceHash >> 16;
		int childId = interfaceHash & 0xFFFF;

		Player recipient = (Player) World.getWorld().getPlayers().get(index);

		if (recipient == null || !recipient.isPlayer())
			return;
		Action action = null;
		action = new Action(player, 0) {
			@Override
			public CancelPolicy getCancelPolicy() {
				return CancelPolicy.ALWAYS;
			}

			@Override
			public StackPolicy getStackPolicy() {
				return StackPolicy.NEVER;
			}

			@Override
			public AnimationPolicy getAnimationPolicy() {
				return AnimationPolicy.RESET_ALL;
			}

			@Override
			public void execute() {
				switch (interfaceId) {
				case Inventory.INTERFACE:
					Item item = player.getInventory().get(slot);
					if (item == null)
						return;
					// Incase cheat-engine ^^
					if (!player.getInventory().hasItem(item))
						return;
					switch (item.getId()) {
					case 962:
						player.face(recipient.getLocation());
						if (recipient.hasAttribute("busy") || recipient.hasAttribute("teleporting")) {
							player.sendMessage("The other player is busy.");
							return;
						}
						recipient.face(player.getLocation());
						player.getInventory().remove(item);
						int[] reward = { 1038, 1040, 1042, 1044, 1046, 1048 };
						Item partyhat = new Item(reward[Misc.random(reward.length - 1)]);
						player.getInventory().add(partyhat);
						player.getActionSender().sendItemDialogue(partyhat.getId(),
								"You pull the cracker on " + recipient.getName() + " and receive a partyhat!");
						player.forceChat("Hey, I got the cracker!");
						break;
					}
					break;
				}
				this.stop();
			}
		};
		player.addCoordinateAction(player.getWidth(), player.getHeight(), recipient.getLocation(), recipient.getWidth(),
				recipient.getHeight(), 1, action);
		pathfindingService.travelToPlayer(player, recipient);
	}

	/**
	 * Handles the first option on a player option menu.
	 *
	 * @param player
	 * @param packet
	 */
	private void optionAttack(final Player player, Packet packet) {
		packet.getByteS();
		int id = packet.getLEShortA();
		if (player.getMonkey() != null || player.getMonkeyTime() > 0) {
			player.setPnpc(-1);
			player.setMonkey(null);
			player.setMonkeyTime(0);
			player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
		}
		if (id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		if (player.getCombatState().isDead()) {
			return;
		}
		Player victim = (Player) World.getWorld().getPlayers().get(id);
		if (victim == null) {
			return;
		}
		/*
		 * if (BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "DuelArena") &&
		 * BoundaryManager.isWithinBoundaryNoZ(victim.getLocation(), "DuelArena")) {
		 * Action action = new Action(player, 0) {
		 * 
		 * @Override public CancelPolicy getCancelPolicy() { return CancelPolicy.ALWAYS;
		 * }
		 * 
		 * @Override public StackPolicy getStackPolicy() { return StackPolicy.NEVER; }
		 * 
		 * @Override public AnimationPolicy getAnimationPolicy() { return
		 * AnimationPolicy.RESET_ALL; }
		 * 
		 * @Override public void execute() { //
		 * player.getRequestManager().request(RequestType.DUEL, victim); this.stop(); }
		 * }; pathfindingService.travelToPlayer(player, victim);
		 * player.addCoordinateAction(player.getWidth(), player.getHeight(),
		 * victim.getLocation(), 1, 1, 1, action); return; }
		 */
		if (player.getMinigame() != null) {
			if (!player.getMinigame().attackMobHook(player, victim)) {
				return;
			}
		}
		if (victim != player) {
			player.getCombatState().setQueuedSpell(null);
			player.getCombatState().startAttacking(victim, false);
		}
		player.getActionSender().sendDebugPacket(packet.getOpcode(), "PlayerAttack", new Object[] { "Index: " + id });
	}

	/**
	 * Handles the second option on a player option menu.
	 *
	 * @param player
	 * @param packet
	 */
	@SuppressWarnings("unused")
	private void option2(Player player, Packet packet) {
		int id = packet.getShortA() & 0xFFFF;
		packet.getByteC();
		if (id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		if (player.getCombatState().isDead()) {
			return;
		}
		Player other = (Player) World.getWorld().getPlayers().get(id);
		if (other == null) {
			return;
		}
		int followX = other.getLocation().getX();
		int followY = other.getLocation().getY();
		if (player.getLocation().getY() < other.getLocation().getY()) {
			followY--;
		} else if (player.getLocation().getY() > other.getLocation().getY()) {
			followY++;
		} else if (player.getLocation().getX() < other.getLocation().getX()) {
			followX--;
		} else if (player.getLocation().getX() > other.getLocation().getX()) {
			followX++;
		}
		int distance = other.getLocation().distanceToEntity(other, player);
		// World.getWorld().doPath(new SizedPathFinder(true), player, followX,
		// followY);//this is what steven told me 2 implement but it walks under
		// the npc ;l
		Action action = null;
		action = new Action(player, 0) {
			@Override
			public CancelPolicy getCancelPolicy() {
				return CancelPolicy.ALWAYS;
			}

			@Override
			public StackPolicy getStackPolicy() {
				return StackPolicy.NEVER;
			}

			@Override
			public AnimationPolicy getAnimationPolicy() {
				return AnimationPolicy.RESET_ALL;
			}

			@Override
			public void execute() {
				player.getRequestManager().request(RequestType.TRADE, other);
				player.getActionSender().sendDebugPacket(packet.getOpcode(), "Opt2", new Object[] { "Index: " + id });
				this.stop();
			}
		};
		// if (distance > 1) {
		player.addCoordinateAction(player.getWidth(), player.getHeight(), other.getLocation(), other.getWidth(),
				other.getHeight(), 1, action);
		pathfindingService.travelToPlayer(player, other);
		// World.getWorld().doPath(new SizedPathFinder(true), player, followX,
		// followY);
		/*
		 * } else { action.execute(); }
		 */
	}

	/**
	 * Handles the third option on a player option menu.
	 *
	 * @param player
	 * @param packet
	 */
	private void option3(Player player, Packet packet) {
		int id = packet.getLEShort() & 0xFFFF;
		packet.getByteC();
		if (id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		if (player.getCombatState().isDead()) {
			return;
		}
		Player target = (Player) World.getWorld().getPlayers().get(id);
		if (target != null && target != player) {
			player.getActionSender().removeChatboxInterface();

			if (player.getInterfaceState().isInterfaceOpen(PriceChecker.PRICE_INVENTORY_INTERFACE)) {
				PriceChecker.returnItems(player);
			}

			if (player.getInterfaceState().isEnterAmountInterfaceOpen()) {
				player.getActionSender().removeEnterAmountInterface();
			}

			if (player.getAttribute("bank_searching") != null) {
				player.getActionSender().removeEnterAmountInterface();
				player.removeAttribute("bank_searching");
			}

			if (player.getInterfaceState().isInterfaceOpen(Bank.BANK_INVENTORY_INTERFACE)
					|| player.getInterfaceState().isInterfaceOpen(Trade.TRADE_INVENTORY_INTERFACE)
					|| player.getInterfaceState().isInterfaceOpen(Shop.SHOP_INVENTORY_INTERFACE)
					|| player.getInterfaceState().isInterfaceOpen(PriceChecker.PRICE_INVENTORY_INTERFACE)) {
				// player.getActionSender().removeInterfaces(player.getAttribute("tabmode"),
				// ((int) player.getAttribute("tabmode") == 548 ? 59 : 53));
				player.getActionSender().removeInventoryInterface();
				player.resetInteractingEntity();
			}

			if (player.isLighting()) {
				return;
			}

			player.getActionSender().removeAllInterfaces().removeInterface2();

			player.setInteractingEntity(InteractionMode.FOLLOW, target);
		}
	}

	/**
	 * Handles the fourth option on a player option menu.
	 *
	 * @param player
	 * @param packet
	 */
	private void option4(Player player, Packet packet) {// not actually trade
														// but ok.
		int id = packet.getShortA() & 0xFFFF;
		if (id < 0 || id >= Constants.MAX_PLAYERS)
			return;
		if (player.getCombatState().isDead())
			return;
		Player target = (Player) World.getWorld().getPlayers().get(id);
		if (target != null && target != player)
			player.getRequestManager().request(RequestType.TRADE, target);
	}

	/**
	 * Handles the five option on a player option menu.
	 *
	 * @param player
	 * @param packet
	 */
	private void option5(Player player, Packet packet) {
		int id = packet.getLEShort() & 0xFFFF;
		packet.getByteC();
		if (id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		if (player.getCombatState().isDead()) {
			return;
		}
		Player target = (Player) World.getWorld().getPlayers().get(id);
		if (target != null && target != player) {
			Action action = null;
			action = new Action(player, 0) {
				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}

				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.NEVER;
				}

				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}

				@Override
				public void execute() {
					// player.getRequestManager().request(RequestType.DICE,
					// target);
					this.stop();
				}
			};
			// if (distance > 1) {
			player.addCoordinateAction(player.getWidth(), player.getHeight(), target.getLocation(), target.getWidth(),
					target.getHeight(), 1, action);
			pathfindingService.travelToPlayer(player, target);
		}
	}

	/**
	 * Handles player spell option.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	@SuppressWarnings("unused")
	private void optionSpell(Player player, Packet packet) {
		int idk = packet.getShort();
		int idk2 = packet.get();
		int interfaceHash = packet.getLEInt();
		int id = packet.getShortA();
		int interfaceId = interfaceHash >> 16;
		int childButton = interfaceHash & 0xFFFF;
		if (id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		if (player.getCombatState().isDead()) {
			return;
		}
		Player victim = (Player) World.getWorld().getPlayers().get(id);
		if (player.getMinigame() != null) {
			if (!player.getMinigame().attackMobHook(player, victim)) {
				return;
			}
		}
		int spellOffset = player.getCombatState().getSpellBook() == SpellBook.ANCIENT_MAGICKS.getSpellBookId() ? -2
				: -1;
		int spellId = (childButton) + (spellOffset);
		Spell spell = Spell.forId(spellId, SpellBook.forId(player.getCombatState().getSpellBook()));
		if (victim != null && victim != player && spell != null) {
			if (spell.getSpellType() == SpellType.COMBAT) {
				// MagicCombatAction.setAutocast(player, null, -1, false);
				player.setAttribute("magicMove", true);
				player.setAttribute("castSpell", spell);
				player.getCombatState().setQueuedSpell(spell);
				player.getCombatState().startAttacking(victim, false);
			} else if (spell.getSpellType() == SpellType.NON_COMBAT) {
				// if(!BoundaryManager.isWithinBoundaryNoZ(player.getLocation(),
				// "MultiCombat") || !player.inMulti()) {
				// player.getActionSender().sendMessage("You must be in a multi
				// area to cast that spell.");
				// return;
				// }
				// if(!victim.getSettings().isAcceptingAid()) {
				// player.getActionSender().sendMessage("That person doesn't
				// have accept aid on.");
				// return;
				// }
				if (player.getLocation().isWithinDistance(victim.getLocation(), 10)) {
					player.getWalkingQueue().reset();
				}
				player.setInteractingEntity(InteractionMode.TALK, victim);
				MagicCombatAction.executeSpell(spell, player, player, victim);
			}
		}
	}

}
