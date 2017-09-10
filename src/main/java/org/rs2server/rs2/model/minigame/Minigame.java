package org.rs2server.rs2.model.minigame;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.boundary.Boundary;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

import java.util.List;

/**
 * An interface that holds all information about a minigame.
 * @author Michael
 *
 */
public interface Minigame {
	
	/**
	 * Gets the list of players participating in this minigame.
	 * @return The list of players participating in this minigame.
	 */
	public abstract List<Player> getParticipants();

	/**
	 * Starts the minigame.
	 */
	public abstract void start();
	
	/**
	 * Performs checks on ending the minigame.
	 */
	public abstract void end();
	
	/**
	 * Performs checks when a player quits
	 * @param player The player.
	 */
	public abstract void quit(Player player);
	
	/**
	 * Gets the boundary in which this minigame takes place.
	 * @return The boundary in which this minigame takes place.
	 */
	public abstract Boundary getBoundary();
	
	/**
	 * Gets the state of item safety.
	 * @return The state of item safety.
	 */
	public abstract ItemSafety getItemSafety();
	
	/**
	 * Gets the name of this minigame.
	 * @return The name of this minigame.
	 */
	public abstract String getName();
	
	/**
	 * Gets a tickable that performs actions such as depleting remaining time.
	 * @return A tickable that performs actions such as depleting remaining time.
	 */
	public abstract Tickable getGameCycle();
	
	/**
	 * Gets the location that the player is teleported to upon quitting the game.
	 * @return The location that the player is teleported to upon quitting the game.
	 */
	public abstract Location getStartLocation();
	
	/**
	 * Performs checks when a player dies in the minigame.
	 * @param player The player.
	 * @return True = don't drop loot, false = do drop loot.
	 */
	public abstract boolean deathHook(Player player);
	
	/**
	 * Performs checks for when the player moves.
	 * @param player The player.
	 */
	public abstract void movementHook(Player player);
	
	/**
	 * Performs checks for when the player kills another mob.
	 * @param player The player.
	 * @param victim The victim.
	 */
	public abstract void killHook(Player player, Mob victim);
	
	/**
	 * Performs checks when a player attacks another player.
	 * @param player The player.
	 * @param victim The victim.
	 * @return Whether the hook was successful.
	 */
	public abstract boolean attackMobHook(Player player, Mob victim);
	
	/**
	 * An enum that represents the state in which items are lost/kept upon death.
	 * @author Michael
	 *
	 */
	enum ItemSafety {
		
		/**
		 * This minigame is a safe zone, items will not be lost.
		 */
		SAFE,
		
		/**
		 * This minigame is a danger zone, items will be lost.
		 */
		UNSAFE
	}
}
