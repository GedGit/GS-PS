package org.rs2server.rs2.action.impl;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.util.CycleState;

import java.util.Random;

public abstract class SkillAction extends Tickable {

	/**
	 * The random instance used.
	 */
	public static final Random RANDOM = new Random();

	/**
	 * The reset animation.
	 */
	public static final Animation RESET_ANIM = Animation.create(-1);

	/**
	 * The reset gfx.
	 */
	public static final Graphic RESET_GFX = Graphic.create(-1);

	/**
	 * The current cycle state.
	 */
	private CycleState cycleState = CycleState.COMMENCE;

	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * Constructs a new {@code SkillAction} {@code Object}.
	 */
	public SkillAction(Player player) {
		super(1);
		this.player = player;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void execute() {
		switch (cycleState) {
		case COMMENCE:
			if (commence(player)) {
				setCycleState(CycleState.EXECUTE);
				return;
			}
			stop();
			return;
		case EXECUTE:
			if (execute(player)) {
				setCycleState(CycleState.FINALIZE);
			}
			return;
		case FINALIZE:
			if (finish(player)) {
				stop();
			}
			return;
		}
	}

	/**
	 * Attempts to start the actual skilling action.
	 *
	 * @param player The player.
	 * @return {@code True} if the player can execute this action, {@code false} if not.
	 */
	 public abstract boolean commence(Player player);

	 /**
	  * Commences the skilling action.
	  *
	  * @param player The player.
	  * @return {@code True} if we can finish the skilling action, {@code false} if not.
	  */
	 public abstract boolean execute(Player player);

	 /**
	  * Attempts to finish the skilling action.
	  *
	  * @param player The player.
	  * @return {@code True} if the action has finished, {@code false} if we should re-execute.
	  */
	 public abstract boolean finish(Player player);

	 /**
	  * @param cycleState the cycleState to set
	  */
	 public void setCycleState(CycleState cycleState) {
		 this.cycleState = cycleState;
	 }

	 /**
	  * @return the cycleState
	  */
	 public CycleState getCycleState() {
		 return cycleState;
	 }

	 /**
	  * @return the player
	  */
	 public Player getPlayer() {
		 return player;
	 }

}