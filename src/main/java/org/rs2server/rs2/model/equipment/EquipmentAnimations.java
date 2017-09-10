package org.rs2server.rs2.model.equipment;

import org.rs2server.rs2.model.Animation;

/**
 * The weapon animations class
 * @author Sir Sean
 *
 */
public class EquipmentAnimations {
	
	private static Animation STAND = Animation.create(808);
	
	private static Animation WALK = Animation.create(819);
	
	private static Animation RUN = Animation.create(824);
	
	private static Animation[] ATTACK = new Animation[] { Animation.create(422), Animation.create(423), Animation.create(422), Animation.create(422) };
	
	private static Animation DEFEND = Animation.create(424);
	
	private static Animation STAND_TURN = Animation.create(823);
	
	private static Animation TURN_180 = Animation.create(820);
	
	private static Animation TURN_90_CLOCKWISE = Animation.create(821);
	
	private static Animation TURN_90_COUNTER_CLOCKWISE = Animation.create(822);
	
	/**
	 * The weapon stand animation
	 */
	private Animation stand;
	
	/**
	 * The weapon run animation
	 */
	private Animation run;
	
	/**
	 * The weapon walk animation
	 */
	private Animation walk;
	
	/**
	 * The weapon attack animations based on
	 * attack style.
	 */
	private Animation attack[];
	
	/**
	 * The weapon defend animation
	 */
	private Animation defend;
	
	/**
	 * The weapon stand turn animation
	 */
	private Animation standTurn;
	
	/**
	 * The weapon turn 180D animation
	 */
	private Animation turn180;
	
	/**
	 * The weapon turn 90D clock wise
	 */
	private Animation turn90ClockWise;
	
	/**
	 * The weapon turn 90D counter clock wise
	 */
	private Animation turn90CounterClockWise;
	
	/**
	 * Sets the equipment animations per weapon in the constructor :)
	 * @param stand The stand animation
	 * @param run The run animation
	 * @param walk The walk animation
	 * @param attack The attack animation
	 * @param standTurn The stand turn animation
	 * @param turn180 The turn 180 turn animation
	 * @param turn90ClockWise The turn 90 clock wise animation
	 * @param turn90CounterClockWise The turn 90 counter clock wise animation
	 */
	public EquipmentAnimations(Animation stand, Animation walk, Animation run, Animation attack[],
			Animation defend, Animation standTurn, Animation turn180,
			Animation turn90ClockWise, Animation turn90CounterClockWise) {
		this.stand = stand;
		this.run = run;
		this.walk = walk;
		this.attack = attack;
		this.defend = defend;
		this.standTurn = standTurn;
		this.turn180 = turn180;
		this.turn90ClockWise = turn90ClockWise;
		this.turn90CounterClockWise = turn90CounterClockWise;
	}
	
	/**
	 * Gets the stand animation
	 * @return the stand animation
	 */
	public Animation getStand() {
		if(stand == null) {
			return STAND;
		}
		return stand;
	}
	
	/**
	 * Gets the run animation
	 * @return the run animation
	 */
	public Animation getRun() {
		if(run == null) {
			return RUN;
		}
		return run;
	}
	
	/**
	 * Gets the walk animation
	 * @return the walk animation
	 */
	public Animation getWalk() {
		if(walk == null) {
			return WALK;
		}
		return walk;
	}
	
	/**
	 * The attack animation
	 * @param index The combat style index
	 * @return the attack animation
	 */
	public Animation[] getAttacks() {
		if(attack == null) {
			return ATTACK;
		}
		return attack;
	}
	
	/**
	 * The attack animation
	 * @param index The combat style index
	 * @return the attack animation
	 */
	public Animation getAttack(int index) {
		if(attack == null) {
			return ATTACK[index];
		}
		return attack[index];
	}
	
	/**
	 * The defend animation
	 * @return the defend animation
	 */
	public Animation getDefend() {
		if(defend == null) {
			return DEFEND;
		}
		return defend;
	}
	
	/**
	 * Gets the stand turn animation
	 * @return the stand turn animation
	 */
	public Animation getStandTurn() {
		if(standTurn == null) {
			return STAND_TURN;
		}
		return standTurn;
	}
	
	/**
	 * Gets the 180 turn animation
	 * @return the turn 180 animation
	 */
	public Animation getTurn180() {
		if(turn180 == null) {
			return TURN_180;
		}
		return turn180;
	}
	
	/**
	 * Gets the 90 clock wise turn animation
	 * @return the turn 90 clock wise animation
	 */
	public Animation getTurn90ClockWise() {
		if(turn90ClockWise == null) {
			return TURN_90_CLOCKWISE;
		}
		return turn90ClockWise;
	}
	
	/**
	 *Gets the 90 counter clockwise turn animation 
	 * @return the turn 90 counter clock wise turn animation
	 */
	public Animation getTurn90CounterClockWise() {
		if(turn90CounterClockWise == null) {
			return TURN_90_COUNTER_CLOCKWISE;
		}
		return turn90CounterClockWise;
	}
}
