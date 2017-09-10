package org.rs2server.rs2.model.npc.impl.demonicgorilla;

import java.util.Random;

import org.rs2server.rs2.content.Following;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.map.Directions;
import org.rs2server.rs2.model.npc.impl.CombatNpc;
import org.rs2server.rs2.model.npc.impl.IdleCombatState;
import org.rs2server.rs2.model.npc.impl.NpcCombatState;
import org.rs2server.rs2.model.npc.impl.demonicgorilla.styles.DemonicGorillaMagicAttackStyle;
import org.rs2server.rs2.model.npc.impl.demonicgorilla.styles.DemonicGorillaMeleeAttackStyle;
import org.rs2server.rs2.model.npc.impl.demonicgorilla.styles.DemonicGorillaRangedAttackStyle;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.impl.StoppingTick;

/**
 * 
 * @author Greco
 *
 */

public class DemonicGorilla extends CombatNpc<DemonicGorilla> {

	public static final int NPC_ID = 7147;

	private static final Animation BLOCK_ANIMATION = Animation.create(7224);

	private static final Animation DEATH_ANIMATION = Animation.create(7229);

	private static final int[] BONUSES = { 0, 50, 0, 50, 50, 50, 100, 25, 100, 100, 0, 0, 0 };
	public static final int MAX_HEALTH = 380;

	private final Player challenger;
	private int performedAttacks;

	private final NpcCombatState<DemonicGorilla> magicAttackStyle;
	private final NpcCombatState<DemonicGorilla> rangedAttackStyle;
	private final NpcCombatState<DemonicGorilla> meleeAttackStyle;

	private boolean canAttackPlayer;

	public DemonicGorilla(Player challenger, Location loc) {
		super(NPC_ID, loc);
		this.challenger = challenger;
		this.magicAttackStyle = new DemonicGorillaMagicAttackStyle<>(this);
		this.rangedAttackStyle = new DemonicGorillaRangedAttackStyle<>(this);
		this.meleeAttackStyle = new DemonicGorillaMeleeAttackStyle<>(this);

		this.getSkills().setLevel(Skills.ATTACK, 220);
		this.getSkills().setLevel(Skills.DEFENCE, 100);
		this.getSkills().setLevel(Skills.STRENGTH, 220);
		this.getSkills().setLevel(Skills.RANGE, 220);
		this.getSkills().setLevel(Skills.MAGIC, 220);
		this.getSkills().setLevel(Skills.HITPOINTS, MAX_HEALTH);
		this.getCombatState().setBonuses(BONUSES);
		this.setDirection(Directions.NormalDirection.WEST.npcIntValue());

		this.random = new Random();

		setInteractingEntity(InteractionMode.ATTACK, challenger);
		transition(new IdleCombatState<>(this));
	}

	public void destroySelf(boolean stop) {
		challenger.getInstancedNPCs().remove(this);
		if (stop) {
			challenger.getInstancedNPCs().forEach(World.getWorld()::unregister);
			challenger.getInstancedNPCs().clear();
		}
	}

	@Override
	public boolean isAutoRetaliating() {
		return false;
	}

	@Override
	public void tick() {
		if (getCombatState().isDead()) {
			return;
		}
		if (!canAttackPlayer && this.getCombatState().getDamageMap().getTotalDamages().containsKey(challenger)) {
			canAttackPlayer = true;
		}
		if (canAttackPlayer) {
			double distance = getLocation().distance(challenger.getLocation());
			if (distance >= 13) {
				Following.combatFollow(this, challenger);
				return;
			}
			if (performedAttacks == 0) {
				transition(magicAttackStyle);
			} else if (performedAttacks == 1) {
				transition(rangedAttackStyle);
			} else if (performedAttacks == 2) {
				transition(meleeAttackStyle);
				World.getWorld().submit(new StoppingTick(67) {
					@Override
					public void executeAndStop() {
						performedAttacks = 0;
					}
				});
			}
		}
	}

	@Override
	public final Animation getDefendAnimation() {
		return BLOCK_ANIMATION;
	}

	@Override
	public final Animation getDeathAnimation() {
		return DEATH_ANIMATION;
	}

	@Override
	public boolean isTurn() {
		return getCombatState().getAttackDelay() == 0;
	}

	public Player getChallenger() {
		return challenger;
	}

	public void incrementPerformedAttacks() {
		performedAttacks++;
	}

	public boolean canAttackPlayer() {
		return canAttackPlayer;
	}

	public void destroySelf() {
		// TODO Auto-generated method stub

	}
}
