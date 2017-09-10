package org.rs2server.rs2.model.skills.hunter;

import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.player.Player;

public class BoxTrap extends Trap {

	  private TrapState state;

	  public BoxTrap(GameObject obj, TrapState state, int ticks, Player p) {
	    super(obj, state, ticks, p);
	  }

	  /**
	   * @return the state
	   */
	  public TrapState getState() {
	    return state;
	  }

	  /**
	   * @param state the state to set
	   */
	  public void setState(TrapState state) {
	    this.state = state;
	  }

	}
