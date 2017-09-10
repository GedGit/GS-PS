package org.rs2server.rs2.task.impl;

import org.rs2server.Server;
import org.rs2server.cache.format.CacheNPCDefinition;
import org.rs2server.rs2.domain.service.api.PermissionService;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.container.Equipment.EquipmentType;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.net.PacketBuilder;
import org.rs2server.rs2.task.Task;
import org.rs2server.rs2.util.TextUtils;

import java.util.Iterator;

/**
 * A task which creates and sends the player update block.
 * 
 * @author Graham Edgecombe
 *
 */
public class PlayerUpdateTask implements Task {
	/**
	 * The player.
	 */
	private Player player;

	/**
	 * Creates an update task.
	 * 
	 * @param player
	 *            The player.
	 */
	public PlayerUpdateTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute() {
		/*
		 * If the map region changed send the new one. We do this immediately as the
		 * client can begin loading it before the actual packet is received.
		 */
		if (player.isMapRegionChanging()) {
			player.getActionSender().sendMapRegion();
			player.getActionSender().sendGameObjectsInArea();
		}

		/*
		 * The update block packet holds update blocks and is send after the main
		 * packet.
		 */
		PacketBuilder updateBlock = new PacketBuilder();

		/*
		 * The main packet is written in bits instead of bytes and holds information
		 * about the local list, players to add and remove, movement and which updates
		 * are required.
		 */
		PacketBuilder packet = new PacketBuilder(129, Packet.Type.VARIABLE_SHORT);
		packet.startBitAccess();

		/*
		 * Updates this player.
		 */
		updateThisPlayerMovement(packet);
		updatePlayer(updateBlock, player, false); // tbh doesn't matter. Writes
													// to update block

		/*
		 * Write the current size of the player list.
		 */
		packet.putBits(8, player.getLocalPlayers().size());

		/*
		 * Iterate through the local player list.
		 */
		for (Iterator<Player> it$ = player.getLocalPlayers().iterator(); it$.hasNext();) {
			/*
			 * Get the next player.
			 */
			Player otherPlayer = it$.next();

			/*
			 * If the player should still be in our list.
			 */
			if (World.getWorld().getPlayers().contains(otherPlayer) && !otherPlayer.isTeleporting()
					&& otherPlayer.getLocation().isWithinDistance(player.getLocation())
					&& !player.isMultiplayerDisabled()) {
				/*
				 * Update the movement.
				 */
				updatePlayerMovement(packet, otherPlayer);

				/*
				 * Check if an update is required, and if so, send the update.
				 */
				if (otherPlayer.getUpdateFlags().isUpdateRequired()) {
					updatePlayer(updateBlock, otherPlayer, false);
				}
			} else {
				/*
				 * Otherwise, remove the player from the list.
				 */
				it$.remove();

				/*
				 * Tell the client to remove the player from the list.
				 */
				packet.putBits(1, 1);
				packet.putBits(2, 3);
			}
		}

		/*
		 * Loop through every player.
		 */
		if (!player.isMultiplayerDisabled() && player.isActive()) {
			for (Player otherPlayer : World.getWorld().getRegionManager().getLocalPlayers(player)) {
				/*
				 * Check if there is room left in the local list.
				 */
				if (player.getLocalPlayers().size() >= 255) {
					/*
					 * There is no more room left in the local list. We cannot add more players, so
					 * we just ignore the extra ones. They will be added as other players get
					 * removed.
					 */
					break;
				}

				/*
				 * If they should not be added ignore them.
				 */
				if (otherPlayer == player || player.getLocalPlayers().contains(otherPlayer)) {
					continue;
				}

				/*
				 * Add the player to the local list if it is within distance.
				 */
				player.getLocalPlayers().add(otherPlayer);

				/*
				 * Add the player in the packet.
				 */
				addNewPlayer(packet, otherPlayer);

				/*
				 * Update the player, forcing the appearance flag.
				 */
				updatePlayer(updateBlock, otherPlayer, true);
			}
		}

		/*
		 * Check if the update block is not empty.
		 */
		if (!updateBlock.isEmpty()) {
			/*
			 * Write a magic id indicating an update block follows.
			 */
			packet.putBits(11, 2047);
			packet.finishBitAccess();
			/*
			 * Add the update block at the end of this packet.
			 */
			packet.put(updateBlock.toPacket().getPayload());
		} else {
			/*
			 * Terminate the packet normally.
			 */
			packet.finishBitAccess();
		}
		/*
		 * Write the packet.
		 */
		player.write(packet.toPacket());
	}

	/**
	 * Updates a non-this player's movement.
	 * 
	 * @param packet
	 *            The packet.
	 * @param otherPlayer
	 *            The player.
	 */
	public void updatePlayerMovement(PacketBuilder packet, Player otherPlayer) {

		/*
		 * Check which type of movement took place.
		 */
		if (otherPlayer.getSprites().getPrimarySprite() == -1) {
			/*
			 * If no movement did, check if an update is required.
			 */
			if (otherPlayer.getUpdateFlags().isUpdateRequired()) {
				/*
				 * Signify that an update happened.
				 */
				packet.putBits(1, 1);

				/*
				 * Signify that there was no movement.
				 */
				packet.putBits(2, 0);
			} else {
				/*
				 * Signify that nothing changed.
				 */
				packet.putBits(1, 0);
			}
		} else if (otherPlayer.getSprites().getSecondarySprite() == -1) {
			/*
			 * The player moved but didn't run. Signify that an update is required.
			 */
			packet.putBits(1, 1);

			/*
			 * Signify we moved one tile.
			 */
			packet.putBits(2, 1);

			/*
			 * Write the primary sprite (i.e. walk direction).
			 */
			packet.putBits(3, otherPlayer.getSprites().getPrimarySprite());

			/*
			 * Write a flag indicating if a block update happened.
			 */
			packet.putBits(1, otherPlayer.getUpdateFlags().isUpdateRequired() ? 1 : 0);
		} else {
			/*
			 * The player ran. Signify that an update happened.
			 */
			packet.putBits(1, 1);

			/*
			 * Signify that we moved two tiles.
			 */
			packet.putBits(2, 2);

			/*
			 * Write the primary sprite (i.e. walk direction).
			 */
			packet.putBits(3, otherPlayer.getSprites().getPrimarySprite());

			/*
			 * Write the secondary sprite (i.e. run direction).
			 */
			packet.putBits(3, otherPlayer.getSprites().getSecondarySprite());

			/*
			 * Write a flag indicating if a block update happened.
			 */
			packet.putBits(1, otherPlayer.getUpdateFlags().isUpdateRequired() ? 1 : 0);
		}
	}

	/**
	 * Adds a new player.
	 * 
	 * @param packet
	 *            The packet.
	 * @param otherPlayer
	 *            The player.
	 */
	public void addNewPlayer(PacketBuilder packet, Player otherPlayer) {
		int yPos = otherPlayer.getLocation().getY() - player.getLocation().getY();
		int xPos = otherPlayer.getLocation().getX() - player.getLocation().getX();
		/*
		 * Write the player index.
		 */
		packet.putBits(11, otherPlayer.getIndex());// fairly certain ive
													// completed this method
		// System.out.println("Player: " + otherPlayer.getName() + ", Face: " +
		// otherPlayer.getDirection());
		packet.putBits(1, 1);// its just so weird rofl ask leanbow if he knows
								// anything about facing when entering a new
								// region
		packet.putBits(5, xPos)// maybe we're setting direction wrong when you
								// click attack? we could check
				.putBits(3, otherPlayer.getDirection()) // ye should be correct
														// lol, but we could
														// check. maybe
														// directions are
														// fucked? rofl
				.putBits(1, 1).putBits(5, yPos);// we sure this isnt wrong
		// if (otherPlayer.getInteractingEntity() != null) {
		// Mob mob = otherPlayer.getInteractingEntity();
		//
		// mob.face(otherPlayer.getLocation());
		// otherPlayer.face(mob.getLocation());
		// }
	}

	/**
	 * Updates a player.
	 * 
	 * @param packet
	 *            The packet.
	 * @param otherPlayer
	 *            The other player.
	 * @param forceAppearance
	 *            The force appearance flag.
	 * @param noChat
	 *            Indicates chat should not be relayed to this player.
	 */
	/**
	 * 
	 * AppendUpdateBlock(Player p, PacketBuilder updateBlock, boolean
	 * forceAppearance)
	 * 
	 * @param player,
	 *            player we're updating.
	 * @param updateBlock,
	 *            block being written to.
	 * @param forceAppearance,
	 *            for login, and new players Mask values 0x400 - Graphics 0x40 -
	 *            ChatText 0x80 - Hit1 0x10 - ForceText 0x1 - Animation 0x20 -
	 *            FaceToEntity 0x4 - FaceToCoordinate 0x100 - ForceMovement 0x200 -
	 *            Hit2 0x2 - Appearance
	 */
	public void updatePlayer(PacketBuilder packet, Player otherPlayer, boolean forceAppearance) {
		/*
		 * If no update is required and we don't have to force an appearance update,
		 * don't write anything.
		 */
		if (!otherPlayer.getUpdateFlags().isUpdateRequired() && !forceAppearance) {
			return;
		}

		/*
		 * We can used the cached update block!
		 */
		synchronized (otherPlayer) {
			if (otherPlayer.hasCachedUpdateBlock() && otherPlayer != player && !forceAppearance) {
				packet.put(otherPlayer.getCachedUpdateBlock().getPayload().flip());
				return;
			}

			/*
			 * We have to construct and cache our own block.
			 */
			PacketBuilder block = new PacketBuilder();

			/*
			 * Calculate the bitmask.
			 */
			int mask = 0;
			final UpdateFlags flags = otherPlayer.getUpdateFlags();

			if (flags.get(UpdateFlag.HIT)) {
				mask |= 0x200;
			}

			if (flags.get(UpdateFlag.CHAT) && otherPlayer.getCurrentChatMessage() != null) {
				mask |= 0x10;
			}

			if (flags.get(UpdateFlag.HIT_2)) {
				mask |= 0x8;
			}

			if (flags.get(UpdateFlag.FORCED_CHAT)) {
				mask |= 0x40;
			}

			if (flags.get(UpdateFlag.ANIMATION) && otherPlayer.getCurrentAnimation() != null) {// &&
																								// player.getCurrentAnimation()
																								// !=
																								// null)
																								// {
				mask |= 0x4;
			}

			if (flags.get(UpdateFlag.GRAPHICS) && otherPlayer.getCurrentGraphic() != null) {
				mask |= 0x400;
			}

			if (flags.get(UpdateFlag.APPEARANCE) || forceAppearance) {
				mask |= 0x1;
			}

			if (flags.get(UpdateFlag.FACE_ENTITY)) {
				mask |= 0x2;
			}

			if (flags.get(UpdateFlag.FACE_COORDINATE)) {
				mask |= 0x20;
			}

			if (flags.get(UpdateFlag.FORCE_MOVEMENT)) {
				mask |= 0x100;
			}
			/*
			 * if(flags.get(UpdateFlag.FACE_ENTITY) || forceAppearance) { mask |= 0x20; }
			 * 
			 * if (flags.get(UpdateFlag.FACE_COORDINATE)) { mask |= 0x4; }
			 * 
			 * if(flags.get(UpdateFlag.FORCE_MOVEMENT)) { mask |= 0x100; }
			 * 
			 * if(flags.get(UpdateFlag.HIT_2)) { mask |= 0x200; }
			 * if(flags.get(UpdateFlag.APPEARANCE) || forceAppearance) { mask |= 0x2; }
			 */
			/*
			 * Check if the bitmask would overflow a byte.
			 */
			if (mask >= 0x100) {
				mask |= 0x80;
				/*
				 * Write it as a short and indicate we have done so.
				 */
				// mask |= 0x8;
				// LEShort lol ~ john
				block.put((byte) (mask & 0xFF));
				block.put((byte) (mask >> 8));
			} else {
				/*
				 * Write it as a byte.
				 */
				block.put((byte) mask);
			}
			if (flags.get(UpdateFlag.HIT)) {
				appendHitUpdate(otherPlayer, block);
			}

			if (flags.get(UpdateFlag.CHAT) && otherPlayer.getCurrentChatMessage() != null) {
				appendChatTextUpdate(otherPlayer, block);
			}

			if (flags.get(UpdateFlag.HIT_2)) {
				appendHit2Update(otherPlayer, block);
			}

			if (flags.get(UpdateFlag.ANIMATION) && otherPlayer.getCurrentAnimation() != null) {// &&
																								// player.getCurrentAnimation()
																								// !=
																								// null)
																								// {
				appendAnimationUpdate(otherPlayer, block);
			}

			if (flags.get(UpdateFlag.FORCED_CHAT)) {
				appendForceText(otherPlayer, block);
			}

			if (flags.get(UpdateFlag.GRAPHICS) && otherPlayer.getCurrentGraphic() != null) {
				appendGraphicsUpdate(otherPlayer, block);
			}

			if (flags.get(UpdateFlag.APPEARANCE) || forceAppearance) {
				appendPlayerAppearanceUpdate(block, otherPlayer);
			}

			if (flags.get(UpdateFlag.FACE_ENTITY)) {
				appendFaceToUpdate(otherPlayer, block);
			}

			if (flags.get(UpdateFlag.FACE_COORDINATE)) {
				appendFaceCoordUpdate(otherPlayer, block);
			}

			if (flags.get(UpdateFlag.FORCE_MOVEMENT)) {
				appendForceMovement(otherPlayer, block);
			}
			/*
			 * if(flags.get(UpdateFlag.FACE_ENTITY) || forceAppearance) {
			 * appendFaceToUpdate(otherPlayer, block); } if(flags.get(UpdateFlag.APPEARANCE)
			 * || forceAppearance) { appendPlayerAppearanceUpdate(block, otherPlayer); }
			 */
			/*
			 * Convert the block builder to a packet.
			 */
			Packet blockPacket = block.toPacket();

			/*
			 * Now it is over, cache the block if we can.
			 */
			if (otherPlayer != player && !forceAppearance) {
				otherPlayer.setCachedUpdateBlock(blockPacket);
			}

			/*
			 * And finally append the block at the end.
			 */
			packet.put(blockPacket.getPayload());
		}
	}

	private static void appendGraphicsUpdate(Player p, PacketBuilder updateBlock) {
		updateBlock.putShort(p.getCurrentGraphic().getId());
		updateBlock.putInt(p.getCurrentGraphic().getDelay() + (p.getCurrentGraphic().getHeight() * 65536));
	}

	private static void appendChatTextUpdate(Player p, PacketBuilder updateBlock) {
		byte[] text = p.getCurrentChatMessage().getText();
		if (text != null) {
			final PermissionService permissionService = Server.getInjector().getInstance(PermissionService.class);
			String unpacked = new String(text);
			byte[] chatStr = new byte[256];
			chatStr[0] = (byte) unpacked.length();
			int offset = 1 + TextUtils.encryptPlayerChat(chatStr, 0, 1, unpacked.length(), unpacked.getBytes());
			ChatMessage cm = p.getCurrentChatMessage();
			updateBlock.putLEShort(((cm.getColour() & 0xFF) << 8) | (cm.getEffects() & 0xFF));
			updateBlock.putByteC((byte) permissionService.getHighestPermission(p).ordinal());
			updateBlock.putByteA(0);

			updateBlock.putByteC((byte) offset);
			updateBlock.putReverseA(chatStr, 0, offset);
		}
	}

	private static void appendHitUpdate(Player p, PacketBuilder updateBlock) {
		Hit primary = p.getPrimaryHit();
		updateBlock.putLEShort(primary.getDamage());
		updateBlock.put((byte) primary.getType().getId());

		updateBlock.putByteC(p.getSkills().getLevel(3));
		updateBlock.putByteC(p.getSkills().getLevelForExperience(Skills.HITPOINTS));
	}

	private static void appendForceText(Player p, PacketBuilder updateBlock) {
		updateBlock.putRS2String(p.getForcedChatMessage());
	}

	private static void appendAnimationUpdate(Player p, PacketBuilder updateBlock) {
		updateBlock.putShort(p.getCurrentAnimation().getId());
		updateBlock.putByteS((byte) p.getCurrentAnimation().getDelay());
	}

	private static void appendFaceToUpdate(Player p, PacketBuilder updateBlock) {
		Mob entity = p.getInteractingEntity();
		updateBlock.putShortA(entity == null ? -1 : entity.getClientIndex());
	}

	private static void appendFaceCoordUpdate(Player p, PacketBuilder updateBlock) {
		if (p.getFaceLocation() == null) {
			updateBlock.putShortA(0);
			updateBlock.putShortA(0);
		} else {
			updateBlock.putShortA(p.getFaceLocation().getX() * 2 + 1);
			updateBlock.putShortA(p.getFaceLocation().getY() * 2 + 1);
		}
	}

	private void appendForceMovement(Player otherPlayer, PacketBuilder updateBlock) {
		Location myLocation = player.getLastKnownRegion();
		Location location = otherPlayer.getLocation();
		updateBlock.putByteC((byte) (location.getLocalX(myLocation) + otherPlayer.getForceWalk()[0])); // first
																										// x
																										// to
																										// go
																										// to
		updateBlock.putByteC((byte) (location.getLocalY(myLocation) + otherPlayer.getForceWalk()[1])); // first
																										// y
																										// to
																										// go
																										// to
		updateBlock.putByteC((byte) (location.getLocalX(myLocation) + otherPlayer.getForceWalk()[2])); // second
																										// x
																										// to
																										// go
																										// to
		updateBlock.putByteC((byte) (location.getLocalY(myLocation) + otherPlayer.getForceWalk()[3])); // second
																										// y
																										// to
																										// go
																										// to
		updateBlock.putLEShortA(otherPlayer.getForceWalk()[4]); // speed going
																// to location
		updateBlock.putShortA(otherPlayer.getForceWalk()[5]); // speed returning
																// to original
																// location
		updateBlock.putByteA((byte) otherPlayer.getForceWalk()[6]); // direction

	}

	private static void appendHit2Update(Player p, PacketBuilder updateBlock) {
		Hit secondary = p.getSecondaryHit();
		updateBlock.putLEShort(secondary.getDamage());
		updateBlock.put((byte) secondary.getType().getId());

		updateBlock.putByteS((byte) p.getSkills().getLevel(3));
		updateBlock.put((byte) p.getSkills().getLevelForExperience(Skills.HITPOINTS));
	}

	/**
	 * Appends an appearance update.
	 * 
	 * @param packet
	 *            The packet.
	 * @param otherPlayer
	 *            The player.
	 */
	private void appendPlayerAppearanceUpdate(PacketBuilder packet, Player otherPlayer) {// its
																							// this
																							// lol
		Appearance app = otherPlayer.getAppearance();
		Container eq = otherPlayer.getEquipment();

		PacketBuilder playerProps = new PacketBuilder();
		playerProps.put((byte) app.getGender());

		int skull = -1;
		if (otherPlayer.getBountyHunter() != null && otherPlayer.getBountyHunter().getSkullId() != -1) {
			skull = otherPlayer.getBountyHunter().getSkullId();
		} else if (otherPlayer.isFightPitsWinner()) {
			skull = 1;
		} else if (otherPlayer.getCombatState().getSkullTicks() > 0) {
			skull = 0;
		}

		playerProps.put((byte) skull);
		playerProps.put((byte) otherPlayer.getCombatState().getPrayerHeadIcon());

		if (otherPlayer.getPnpc() != -1) {
			playerProps.put((byte) 255);// client expects (id << 8) + i to =
										// 65535
			playerProps.put((byte) 255);// so (255 << 8) + 255 = 65535
			playerProps.putShort(otherPlayer.getPnpc());
		} else {
			for (int i = 0; i < 4; i++) {
				if (eq.isSlotUsed(i)) {
					playerProps.putShort(512 + eq.get(i).getDefinition().getId());
				} else {
					playerProps.put((byte) 0);
				}
			}
			if (eq.isSlotUsed(Equipment.SLOT_CHEST)) {
				playerProps.putShort((short) 512 + eq.get(Equipment.SLOT_CHEST).getId());
			} else {
				playerProps.putShort((short) 0x100 + app.getChest()); // chest
			}

			if (eq.isSlotUsed(Equipment.SLOT_SHIELD))
				playerProps.putShort((short) 512 + eq.get(Equipment.SLOT_SHIELD).getId());
			else
				playerProps.put((byte) 0);

			Item chest = eq.get(Equipment.SLOT_CHEST);
			if (chest != null) {
				boolean chestBody = (chest.getEquipmentDefinition() != null
						&& chest.getEquipmentDefinition().getType() == EquipmentType.PLATEBODY);
				if (!chestBody) {
					playerProps.putShort((short) 0x100 + app.getArms());
				} else {
					playerProps.put((byte) 0);
				}
			} else {
				playerProps.putShort((short) 0x100 + app.getArms());
			}
			if (eq.isSlotUsed(Equipment.SLOT_BOTTOMS)) {
				playerProps.putShort((short) 512 + eq.get(Equipment.SLOT_BOTTOMS).getId());
			} else {
				playerProps.putShort((short) 0x100 + app.getLegs());
			}
			Item helm = eq.get(Equipment.SLOT_HELM);
			if (helm != null) {
				if (!Equipment.is(EquipmentType.FULL_HELM, helm) && !Equipment.is(EquipmentType.FULL_MASK, helm)) {
					playerProps.putShort((short) 0x100 + app.getHead());
				} else {
					playerProps.put((byte) 0);
				}
			} else {
				playerProps.putShort((short) 0x100 + app.getHead());
			}
			if (eq.isSlotUsed(Equipment.SLOT_GLOVES)) {
				playerProps.putShort((short) 512 + eq.get(Equipment.SLOT_GLOVES).getId());
			} else {
				playerProps.putShort((short) 0x100 + app.getHands());
			}
			if (eq.isSlotUsed(Equipment.SLOT_BOOTS)) {
				playerProps.putShort((short) 512 + eq.get(Equipment.SLOT_BOOTS).getId());
			} else {
				playerProps.putShort((short) 0x100 + app.getFeet());
			}
			boolean fullHelm = false;
			if (helm != null) {
				fullHelm = !Equipment.is(EquipmentType.FULL_HELM, helm) && !Equipment.is(EquipmentType.HAT, helm);
			}
			if (fullHelm || app.getGender() == 1) {
				playerProps.put((byte) 0);
			} else {
				playerProps.putShort((short) 0x100 + app.getBeard());
			}
		}
		playerProps.put((byte) app.getHairColour());
		playerProps.put((byte) app.getTorsoColour());
		playerProps.put((byte) app.getLegColour());
		playerProps.put((byte) app.getFeetColour());
		playerProps.put((byte) app.getSkinColour());

		if (otherPlayer.getPnpc() != -1) {
			CacheNPCDefinition def = CacheNPCDefinition.get(otherPlayer.getPnpc());
			playerProps.putShort((short) def.stanceAnimation);
			playerProps.putShort((short) def.standTurnAnimation);
			playerProps.putShort((short) def.walkAnimation);
			playerProps.putShort((short) def.rotate180Animation);
			playerProps.putShort((short) def.rotate90RightAnimation);
			playerProps.putShort((short) def.rotate90LeftAnimation);
			playerProps.putShort((short) def.runAnimation);
		} else {
			playerProps.putShort((short) otherPlayer.getStandAnimation().getId());
			playerProps.putShort((short) otherPlayer.getStandTurnAnimation().getId());
			playerProps.putShort((short) otherPlayer.getWalkAnimation().getId());
			playerProps.putShort((short) otherPlayer.getTurn180Animation().getId());
			playerProps.putShort((short) otherPlayer.getTurn90ClockwiseAnimation().getId());
			playerProps.putShort((short) otherPlayer.getTurn90CounterClockwiseAnimation().getId());
			playerProps.putShort((short) otherPlayer.getRunAnimation().getId());
		}

		playerProps.putRS2String(TextUtils.upperFirst(otherPlayer.getName()));
		playerProps.put((byte) otherPlayer.getSkills().getCombatLevel());
		playerProps.putShort(0); // otherPlayer.getSkills().getTotalLevel());

		playerProps.put((byte) 0);// spot animating bool (aka doing a gfx)
		Packet propsPacket = playerProps.toPacket();

		packet.putByteC((byte) (propsPacket.getLength() & 0xff));
		packet.putBackwards(propsPacket.getPayload().array(), 0, propsPacket.getLength());
	}

	/**
	 * Updates this player's movement.
	 * 
	 * @param packet
	 *            The packet.
	 */
	private void updateThisPlayerMovement(PacketBuilder packet) {
		/*
		 * Check if the player is teleporting.
		 */
		if (player.isTeleporting() || player.isMapRegionChanging()) {
			/*
			 * They are, so an update is required.
			 */
			packet.putBits(1, 1);

			/*
			 * This value indicates the player teleported.
			 */
			packet.putBits(2, 3);

			/*
			 * This flag indicates if an update block is appended.
			 */
			packet.putBits(1, player.isTeleporting() ? 1 : 0);
			/*
			 * These are the positions.
			 */
			packet.putBits(7, player.getLocation().getLocalX(player.getLastKnownRegion()));

			packet.putBits(2, player.getLocation().getPlane());
			/*
			 * This indicates that the client should discard the walking queue.
			 */
			packet.putBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
			/*
			 * These are the positions.
			 */
			packet.putBits(7, player.getLocation().getLocalY(player.getLastKnownRegion()));
		} else {
			/*
			 * Otherwise, check if the player moved.
			 */
			if (player.getSprites().getPrimarySprite() == -1) {
				/*
				 * The player didn't move. Check if an update is required.
				 */
				if (player.getUpdateFlags().isUpdateRequired()) {
					/*
					 * Signifies an update is required.
					 */
					packet.putBits(1, 1);

					/*
					 * But signifies that we didn't move.
					 */
					packet.putBits(2, 0);
				} else {
					/*
					 * Signifies that nothing changed.
					 */
					packet.putBits(1, 0);
				}
			} else {
				/*
				 * Check if the player was running.
				 */
				if (player.getSprites().getSecondarySprite() == -1) {
					/*
					 * The player walked, an update is required.
					 */
					packet.putBits(1, 1);

					/*
					 * This indicates the player only walked.
					 */
					packet.putBits(2, 1);

					/*
					 * This is the player's walking direction.
					 */
					packet.putBits(3, player.getSprites().getPrimarySprite());

					/*
					 * This flag indicates an update block is appended.
					 */
					packet.putBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
				} else {
					/*
					 * The player ran, so an update is required.
					 */
					packet.putBits(1, 1);

					/*
					 * This indicates the player ran.
					 */
					packet.putBits(2, 2);

					/*
					 * This is the walking direction.
					 */
					packet.putBits(3, player.getSprites().getPrimarySprite());

					/*
					 * And this is the running direction.
					 */
					packet.putBits(3, player.getSprites().getSecondarySprite());

					/*
					 * And this flag indicates an update block is appended.
					 */
					packet.putBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);

					if (player.getWalkingQueue().isRunning() && player.getWalkingQueue().getEnergy() > 0) {
						player.getWalkingQueue().setEnergy(player.getWalkingQueue().getEnergy() - 1);
						player.getActionSender().sendRunEnergy();
						if (player.getWalkingQueue().getEnergy() < 1) {
							player.getWalkingQueue().setRunningQueue(false);
							player.getWalkingQueue().setRunningToggled(false);
							player.getActionSender().updateRunningConfig();
						}
					}
				}
			}
		}
	}
}