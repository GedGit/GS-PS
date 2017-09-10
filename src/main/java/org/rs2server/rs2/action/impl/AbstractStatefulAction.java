package org.rs2server.rs2.action.impl;

import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.model.Mob;

/**
 * An abstract implementation of an Action which can take several states, and execute several times.
 *
 * @author tommo
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractStatefulAction<S extends Enum> extends Action {

	private S state;

	public AbstractStatefulAction(Mob mob, int ticks, S initialState) {
		super(mob, ticks);
		this.state = initialState;
	}

	@Override
	public void execute() {
		state = onState(state);
		if (state == null) {
			stop();
		}
	}

	/**
	 * Called when this action is executed.
	 * @param state The current state.
	 * @return The next state. If null is returned, this action will be stopped.
	 */
	public abstract S onState(S state);

}
