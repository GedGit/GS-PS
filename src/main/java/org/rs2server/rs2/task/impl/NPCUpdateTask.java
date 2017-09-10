package org.rs2server.rs2.task.impl;

import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.UpdateFlags;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.minigame.fightcave.FightCave;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.net.PacketBuilder;
import org.rs2server.rs2.task.Task;

import java.util.Iterator;

/**
 * A task which creates and sends the NPC update block.
 * 
 * @author Graham Edgecombe
 * 
 */
public class NPCUpdateTask implements Task {

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * Creates an npc update task.
	 * 
	 * @param player
	 *            The player.
	 */
	public NPCUpdateTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute() {
		/*
		 * The update block holds the update masks and data, and is written
		 * after the main block.
		 */
		PacketBuilder updateBlock = new PacketBuilder();

		/*
		 * The main packet holds information about adding, moving and removing
		 * NPCs.
		 */
		PacketBuilder packet = new PacketBuilder(178, Packet.Type.VARIABLE_SHORT);
		packet.startBitAccess();

		/*
		 * Write the current size of the npc list.
		 */
		packet.putBits(8, player.getLocalNPCs().size());

		final boolean isInFightCaves = FightCave.IN_CAVES.contains(player);

		/*
		 * Iterate through the local npc list.
		 */
		for (Iterator<NPC> it$ = player.getLocalNPCs().iterator(); it$.hasNext();) {
			NPC npc = it$.next();
			if (/*
				 * (!player.isRebuildNpcList() &&
				 * player.getLocation().isWithinDistance(npc.getLocation())) ||
				 */(npc.instancedPlayer != null && npc.instancedPlayer != player)
					|| World.getWorld().getNPCs().contains(npc) && !npc.isTeleporting()
							&& npc.getLocation().isWithinDistance(player.getLocation())
							&& (!isInFightCaves || player.getInstancedNPCs().contains(npc))) {
				/*
				 * Update the movement.
				 */
				updateNPCMovement(packet, npc);

				/*
				 * Check if an update is required, and if so, send the update.
				 */
				if (npc.getUpdateFlags().isUpdateRequired()) {// maybe call
																// update
																// required?
					updateNPC(updateBlock, npc);
				}
			} else {
				/*
				 * Otherwise, remove the NPC from the list.
				 */
				it$.remove();

				/*
				 * Tell the client to remove the NPC from the list.
				 */
				packet.putBits(1, 1);
				packet.putBits(2, 3);
			}
		}

		/*
		 * Loop through all NPCs in the world.
		 */
		for (NPC npc : player.isMultiplayerDisabled() ? player.getInstancedNPCs()
				: World.getWorld().getRegionManager().getLocalNpcs(player)) {
			/*
			 * Check if there is room left in the local list.
			 */
			if (player.getLocalNPCs().size() >= 255) {
				/*
				 * There is no more room left in the local list. We cannot add
				 * more NPCs, so we just ignore the extra ones. They will be
				 * added as other NPCs get removed.
				 */
				break;
			}

			if (/*
				 * (!player.isRebuildNpcList() &&
				 * player.getLocation().isWithinDistance(npc.getLocation())) ||
				 */(isInFightCaves && !player.getInstancedNPCs().contains(npc))
					|| !player.getLocation().isWithinDistance(npc.getLocation())) {
				continue;
			}

			/*
			 * If they should not be added ignore them.
			 */
			if (player.getLocalNPCs().contains(npc)) {
				continue;
			}

			/*
			 * Add the npc to the local list if it is within distance.
			 */
			player.getLocalNPCs().add(npc);

			/*
			 * Add the npc in the packet.
			 */
			addNewNPC(packet, npc);

			/*
			 * Check if an update is required.
			 */
			if (npc.getUpdateFlags().isUpdateRequired()) {

				/*
				 * If so, update the npc.
				 */
				updateNPC(updateBlock, npc);

			}
		}

		/*
		 * Check if the update block isn't empty.
		 */
		if (!updateBlock.isEmpty()) {
			/*
			 * If so, put a flag indicating that an update block follows.
			 */
			packet.putBits(15, 32767);
			packet.finishBitAccess();

			/*
			 * And append the update block.
			 */
			packet.put(updateBlock.toPacket().getPayload());
		} else {
			/*
			 * Terminate the packet normally.
			 */
			packet.finishBitAccess();// does it happen with players too? say 2
										// players r fighting in wild and a 3rd
										// came to see the fight r they fucked
		}

		/*
		 * Write the packet.
		 */
		player.write(packet.toPacket());
	}

	/**
	 * Adds a new NPC.
	 * 
	 * @param packet
	 *            The main packet.
	 * @param npc
	 *            The npc to add.
	 */
	public void addNewNPC(PacketBuilder packet, NPC npc) {
		/*
		 * Write the NPC's index.
		 */
		packet.putBits(15, npc.getIndex());

		/*
		 * Calculate the x and y offsets.
		 */
		int yPos = npc.getLocation().getY() - player.getLocation().getY();
		int xPos = npc.getLocation().getX() - player.getLocation().getX();

		packet.putBits(5, yPos);
		packet.putBits(5, xPos);
		packet.putBits(1, npc.getUpdateFlags().isUpdateRequired() ? 1 : 0);
		packet.putBits(1, 1);
		packet.putBits(14, npc.getId());
		packet.putBits(3, npc.getDirection());

		// if (npc.getInteractingEntity() != null) {
		// Mob mob = npc.getInteractingEntity();
		//
		// mob.face(npc.getLocation());
		// npc.face(mob.getLocation());
		// }
	}

	/**
	 * Update an NPC's movement.
	 * 
	 * @param packet
	 *            The main packet.
	 * @param npc
	 *            The npc.
	 */
	private void updateNPCMovement(PacketBuilder packet, NPC npc) {
		if (npc.getSprites().getPrimarySprite() == -1) {
			if (npc.getUpdateFlags().isUpdateRequired()) {
				packet.putBits(1, 1);
				packet.putBits(2, 0);
			} else {
				packet.putBits(1, 0);
			}
		} else {
			packet.putBits(1, 1);
			packet.putBits(2, 1);
			packet.putBits(3, npc.getSprites().getPrimarySprite());
			packet.putBits(1, npc.getUpdateFlags().isUpdateRequired() ? 1 : 0);
		}

	}

	/**
	 * Update an NPC.
	 * 
	 * @param packet
	 *            The update block.
	 * @param npc
	 *            The npc.
	 */
	private void updateNPC(PacketBuilder packet, NPC npc) {
		/*
		 * Calculate the mask.
		 */
		int mask = 0x0;
		final UpdateFlags flags = npc.getUpdateFlags();

		if (flags.get(UpdateFlag.HIT)) {
			mask |= 0x10;
		}
		if (flags.get(UpdateFlag.GRAPHICS)) {
			mask |= 0x2;
		}
		if (flags.get(UpdateFlag.TRANSFORM)) {
			mask |= 0x80;
		}
		if (flags.get(UpdateFlag.HIT_2)) {
			mask |= 0x40;
		}
		if (flags.get(UpdateFlag.ANIMATION)) {
			mask |= 0x20;
		}
		if (flags.get(UpdateFlag.FACE_COORDINATE) && npc.getFaceLocation() != null) {
			mask |= 0x4;
		}
		if (flags.get(UpdateFlag.FACE_ENTITY)) {
			mask |= 0x8;
		}
		if (flags.get(UpdateFlag.FORCED_CHAT)) {
			mask |= 0x1;
		}

		/*
		 * And write the mask.
		 */
		packet.put((byte) mask);

		if (flags.get(UpdateFlag.HIT)) {
			appendHit1Update(npc, packet);
		}

		if (flags.get(UpdateFlag.GRAPHICS)) {
			appendGraphicsUpdate(npc, packet);
		}

		if (flags.get(UpdateFlag.TRANSFORM)) {
			appendTransform(npc, packet);
		}

		if (flags.get(UpdateFlag.HIT_2)) {
			appendHit2Update(npc, packet);
		}

		if (flags.get(UpdateFlag.ANIMATION)) {
			appendAnimateUpdate(npc, packet);
		}

		if (flags.get(UpdateFlag.FACE_COORDINATE) && npc.getFaceLocation() != null) {
			appendFaceCoordUpdate(npc, packet);
		}

		if (flags.get(UpdateFlag.FACE_ENTITY)) {
			appendFaceToUpdate(npc, packet);
		}

		if (flags.get(UpdateFlag.FORCED_CHAT)) {
			appendForceText(npc, packet);
		}
	}

	private void appendTransform(NPC npc, PacketBuilder packet) {
		packet.putLEShort(npc.getTransformId());
	}

	private static void appendFaceCoordUpdate(NPC npc, PacketBuilder updateBlock) {
		// updateBlock.putShortA(npc.getFaceLocation().getX());
		// updateBlock.putLEShortA(npc.getFaceLocation().getY());

		if (npc.getFaceLocation() == null) {
			updateBlock.putShortA(0);
			updateBlock.putLEShortA(0);
		} else {
			updateBlock.putShortA(npc.getFaceLocation().getX() * 2 + 1);
			updateBlock.putLEShortA(npc.getFaceLocation().getY() * 2 + 1);
		}
	}

	private static void appendForceText(NPC npc, PacketBuilder updateBlock) {
		updateBlock.putRS2String(npc.getForcedChatMessage());
	}

	private static void appendHit1Update(NPC npc, PacketBuilder updateBlock) {
		int maxHp = npc.getSkills().getLevelForExperience(Skills.HITPOINTS);
		updateBlock.putByteC(npc.getPrimaryHit().getDamage());
		updateBlock.put((byte) npc.getPrimaryHit().getType().getId());
		updateBlock.putLEShort(npc.getSkills().getLevel(3));
		updateBlock.putShort(maxHp);
		/*
		 * int hpRatio = npc.getSkills().getLevel(Skills.HITPOINTS) * 30 /
		 * npc.getSkills().getLevelForExperience(Skills.HITPOINTS);
		 * updateBlock.putByteC(hpRatio);
		 */
	}

	private static void appendHit2Update(NPC npc, PacketBuilder updateBlock) {
		updateBlock.putByteC((byte) npc.getSecondaryHit().getDamage());
		updateBlock.putByteA((byte) npc.getSecondaryHit().getType().getId());
		updateBlock.putLEShort(npc.getSkills().getLevel(3));
		updateBlock.putLEShortA(npc.getSkills().getLevelForExperience(Skills.HITPOINTS));
	}

	private static void appendGraphicsUpdate(NPC npc, PacketBuilder updateBlock) {
		updateBlock.putShort(npc.getCurrentGraphic().getId());
		updateBlock.putLEInt(npc.getCurrentGraphic().getDelay());
	}

	private static void appendAnimateUpdate(NPC npc, PacketBuilder updateBlock) {
		updateBlock.putLEShortA(npc.getCurrentAnimation().getId());
		updateBlock.put((byte) npc.getCurrentAnimation().getDelay());
	}

	private static void appendFaceToUpdate(NPC npc, PacketBuilder updateBlock) {

		Mob interacting = npc.getInteractingEntity();
		updateBlock.putShortA(interacting != null ? interacting.getClientIndex() : -1);
	}

}