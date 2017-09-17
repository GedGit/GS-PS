package org.rs2server.rs2.model.npc.impl.abyssSyre;

import java.util.Random;

import org.rs2server.rs2.content.Following;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.map.Directions;
import org.rs2server.rs2.model.npc.impl.CombatNpc;
import org.rs2server.rs2.model.npc.impl.IdleCombatState;
import org.rs2server.rs2.model.npc.impl.NpcCombatState;
import org.rs2server.rs2.model.npc.impl.abyssSyre.styles.AbyssalSireMeleeAttackStyle;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.impl.StoppingTick;

public class AbyssalSire extends CombatNpc<AbyssalSire> {

	public static final int NPC_ID = 5890;

	private static final Animation BLOCK_ANIMATION = Animation.create(4529);

	private static final Animation DEATH_ANIMATION = Animation.create(7100);

	private static final int[] BONUSES = { 180, 136, 250, 1, 200, 40, 60, 50, 20, 60, 0, 0, 65 };
	public static final int MAX_HEALTH = 400;

	private final Player challenger;
	private int performedAttacks;

	private final NpcCombatState<AbyssalSire> meleeAttackStyle;

	private boolean canAttackPlayer;

	public AbyssalSire(Player challenger, Location loc) {
		super(NPC_ID, loc);
		this.challenger = challenger;
		this.meleeAttackStyle = new AbyssalSireMeleeAttackStyle<>(this);

		this.getSkills().setLevel(Skills.ATTACK, 220);
		this.getSkills().setLevel(Skills.DEFENCE, 250);
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

	private boolean CanSpawnPoisonPool = true;

	public boolean CanSpawnPoisonPool(boolean CanSpawnPoisonPool) {
		return this.setCanSpawnPoisonPool(CanSpawnPoisonPool);
	}

	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if (!attacker.isNPC())
			return; // this should be an NPC!
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
		if (getCombatState().isDead())
			return;
		System.out.println("hello? 0");
		if (!canAttackPlayer && this.getCombatState().getDamageMap().getTotalDamages().containsKey(challenger)) {
			canAttackPlayer = true;
			System.out.println("hello? 1");
		}
		if (canAttackPlayer) {
			if (getLocation().getDistance(challenger.getLocation()) > 1) {
				Following.combatFollow(this, challenger);
				System.out.println("following challenger, distance: "+getLocation().getDistance(challenger.getLocation()));
				return;
			}
			if (performedAttacks == 2) {
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

	public boolean isCanSpawnPoisonPool() {
		return CanSpawnPoisonPool;
	}

	public boolean setCanSpawnPoisonPool(boolean canSpawnPoisonPool) {
		CanSpawnPoisonPool = canSpawnPoisonPool;
		return canSpawnPoisonPool;
	}
}
