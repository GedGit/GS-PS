package org.rs2server.rs2.model.skills.hunter;

import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.player.Player;

public class Trap {

	  /**
	   * The WorldObject linked to this HunterObject
	   */
	  private GameObject gameObject;

	  /**
	   * The amount of ticks this object should stay for
	   */
	  private int ticks;

	  /**
	   * This trap's state
	   */
	  private TrapState trapState;

	  private Player player;

	  /**
	   * Reconstructs a new Trap
	   *
	   * @param object
	   * @param state
	   */
	  public Trap(GameObject object, TrapState state, int ticks, Player owner) {
	    gameObject = object;
	    trapState = state;
	    this.ticks = ticks;
	    this.player = owner;
	  }

	  /**
	   * Gets the GameObject
	   */
	  public GameObject getGameObject() {
	    return gameObject;
	  }

	  /**
	   * Sets the GameObject
	   *
	   * @param gameObject
	   */
	  public void setGameObject(GameObject gameObject) {
	    this.gameObject = gameObject;
	  }

	  /**
	   * @return the ticks
	   */
	  public int getTicks() {
	    return ticks;
	  }

	  /**
	   * @param ticks the ticks to set
	   */
	  public void setTicks(int ticks) {
	    this.ticks = ticks;
	  }

	  /**
	   * Gets a trap's state
	   */
	  public TrapState getTrapState() {
	    return trapState;
	  }

	  /**
	   * Sets a trap's state
	   *
	   * @param state
	   */
	  public void setTrapState(TrapState state) {
	    trapState = state;
	  }

	  public Player getOwner() {
	    return player;
	  }

	  public void setOwner(Player player) {
	    this.player = player;
	  }

	  /**
	   * The possible states a trap can be in
	   */
	  public static enum TrapState {

	    SET, CAUGHT
	  }
	}
