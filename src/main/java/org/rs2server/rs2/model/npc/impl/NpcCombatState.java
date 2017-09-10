package org.rs2server.rs2.model.npc.impl;

import org.rs2server.rs2.model.Entity;

import java.util.Random;


/**
 * 
 * @author Twelve
 */
public abstract class NpcCombatState<T extends CombatNpc<?>> {

	protected T npc;

	public NpcCombatState(T npc) {
		this.npc = npc;
	}


	/**
	 * TODO: Rewrite the rest of the code to be based on entity instead of just Player and Client.
	 * so that NPCvNPC combat will work.
	 * Used in the case that we want to deal damage to multiple enemies in one tick.
	 * Use {@code deal(int)} for dealing damage to a single opponent.
	 * @param entity {@link Entity} An entity opponent to deal damage upon. 
	 * @param damage
	 * @return {@code true} if damage was dealt to the entity.
	 */
	public boolean deal(Entity entity, int damage) {
/*
		if (entity == null) {
			return false;
		}

		if (npc.getCombatState().isDead()) {
			return false;
		}

		if (entity instanceof Player) {
			Player old = (Player) entity;

			if (old.getSkills().getLevel(Skills.HITPOINTS) < 1) {
				return false;
			}

			if (old.playerIndex <= 0 && old.npcIndex <= 0) {
				if (old.autoRet == 1) {
					old.npcIndex = npc.npcSlot;
				}
			}

			if (old.attackTimer <= 3 || old.attackTimer == 0 && old.npcIndex == 0 && old.oldNpcIndex == 0) {
				old.startAnimation(old.getCombat().getBlockEmote());
			}
			entity.dealDamage(damage);

			Client challenger = (Client) entity;

			challenger.getPA().refreshSkill(3);
			challenger.handleHitMask(damage);
			npc.oldIndex = challenger.playerId;
			return true;
		}

		NPC npc = (NPC) entity;

		npc.handleHitMask(damage);
		npc.oldIndex = npc.npcSlot;*/
		return true;
	}

	public Random getRandom() {
		return npc.random;
	}

	public int getAttackDelay() {
		return -1;
	}

	public abstract void perform();

	public abstract int getId();
}
