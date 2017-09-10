package org.rs2server.rs2.model.npc.impl.kraken;

import org.rs2server.rs2.domain.service.impl.content.KrakenServiceImpl;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.npc.impl.CombatNpc;
import org.rs2server.rs2.model.npc.impl.IdleCombatState;

/**
 * @author Clank1337
 */
public class Whirlpool extends CombatNpc<Whirlpool> {

	public static final int MAX_HEALTH = 120;

	private static final int[] BONUSES = new int[] {0, 0, 0, 65, 0, 0, 0, 0, -15, 270, 0, 0, 0};

	private final Kraken kraken;

	private static final Animation BLOCK_ANIMATION = Animation.create(3619);

	private static final Animation DEATH_ANIMATION = Animation.create(3620);

	public Whirlpool(Kraken kraken, int id, Location location) {
		super(id, location);
		this.setAttackable(true);

		this.getSkills().setLevel(Skills.HITPOINTS, MAX_HEALTH);
		this.getSkills().setLevel(Skills.RANGE, 150);
		this.getSkills().setLevel(Skills.MAGIC, 75);
		this.getCombatState().setBonuses(BONUSES);

		this.kraken = kraken;

		transition(new IdleCombatState<>(this));

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
		return getCombatState().getAttackDelay() == 0 && getTransformId() == KrakenServiceImpl.TENTACLE;
	}

	public Kraken getKraken() {
		return kraken;
	}
}
