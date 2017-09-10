package org.rs2server.rs2.model.player.pc;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.util.functional.Streamable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * Represents a single pest control pre-game boat.
 * @author twelve
 */
public final class PestControlBoat implements Streamable<Player> {
	private static final int MAX_TIME_REMAINING = 50;
	private static final int MAX_GAME_TIME_REMAINING = 2000;
	private static final int MINIMUM_PLAYERS_REQUIRED = 3;

	private final Set<Player> players = new HashSet<>();
	private final Location entrance;
	private final Location exit;

	private PestControlInstance instance;
	private int boatTimeRemaining;//TODO: Convert to joda in future
	private int gameTimeRemaining;

	public PestControlBoat(Location entrance, Location exit) {
		this.entrance = entrance;
		this.exit = exit;
		this.boatTimeRemaining = MAX_TIME_REMAINING;
		this.gameTimeRemaining = MAX_GAME_TIME_REMAINING;
	}

	@Override
	public Stream<Player> stream() {
		return players.stream();
	}


	public void setBoatTimeRemaining(int timeRemaining) {
		this.boatTimeRemaining = timeRemaining;
	}

	public final Set<Player> getPlayers() {
		return players;
	}

	public Location getEntrance() {
		return entrance;
	}

	public Location getExit() {
		return exit;
	}

	public PestControlInstance getInstance() {
		return instance;
	}

	public void setInstance(PestControlInstance instance) {
		this.instance = instance;
	}

	public int decrementBoatTime() {
		return boatTimeRemaining--;
	}

	public int decrementGameTime() {
		return gameTimeRemaining--;
	}

	public void startGame() {
		if (players.size() >= MINIMUM_PLAYERS_REQUIRED) {
			Set<Player> gamePlayers = stream().limit(PestControlInstance.CAPACITY).collect(toSet());
			players.removeAll(gamePlayers);

			setInstance(new PestControlInstance(this, gamePlayers));
			instance.start();
			setGameTimeRemaining(MAX_GAME_TIME_REMAINING);
		}
		setBoatTimeRemaining(MAX_TIME_REMAINING);
	}

	public void endGame() {
		if (instance != null) {
			instance.endGame();
			setInstance(null);
		}
	}


	public int getGameTimeRemaining() {
		return gameTimeRemaining;
	}

	public void setGameTimeRemaining(int gameTimeRemaining) {
		this.gameTimeRemaining = gameTimeRemaining;
	}
}
