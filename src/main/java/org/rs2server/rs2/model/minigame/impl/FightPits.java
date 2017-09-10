package org.rs2server.rs2.model.minigame.impl;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.boundary.Boundary;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FightPits extends AbstractMinigame {
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(FightPits.class.getName());
	
	/**
	 * The centre orb location.
	 */
	public static Location ORB_OBJECT = Location.create(2399, 5173, 0);
	
	/**
	 * The centre orb location.
	 */
	public static Location CENTRE_ORB = Location.create(2398, 5150, 0);
	
	/**
	 * The north west orb location.
	 */
	public static Location NORTH_WEST = Location.create(2384, 5157, 0);
	
	/**
	 * The south west orb location.
	 */
	public static Location SOUTH_WEST = Location.create(2388, 5138, 0);
	
	/**
	 * The north east orb location.
	 */
	public static Location NORTH_EAST = Location.create(2409, 5158, 0);
	
	/**
	 * The south east orb location.
	 */
	public static Location SOUTH_EAST = Location.create(2411, 5137, 0);
	
	/**
	 * The minimum amount of players there must be in the waiting room + participating before a game starts.
	 */
	public static final int MINIMUM_SIZE = 3;
	
	/**
	 * The game started flag.
	 */
	private boolean gameStarted = false;
	
	/**
	 * The winner string.
	 */
	private String winner = "Tzhaar-Xil-Huz";
	
	/**
	 * The list of players in the waiting room.
	 */
	private List<Player> waitingRoom;
	
	/**
	 * The list of participants in this instance.
	 */
	private List<Player> participants;
	
	public FightPits() {
		init(); //Begins initialization
		BoundaryManager.addBoundary(Boundary.create("Fight Pits Waiting Room", Location.create(2393, 5169, 0), Location.create(2406, 5176, 0))); //waiting area
		BoundaryManager.addBoundary(Boundary.create("PvP Zone", getBoundary().getBottomLeft(), getBoundary().getTopRight())); //attackable area
		BoundaryManager.addBoundary(Boundary.create("MultiCombat", getBoundary().getBottomLeft(), getBoundary().getTopRight())); //attackable area
		this.participants = new ArrayList<Player>();
		this.waitingRoom = new ArrayList<Player>();
		logger.info(getName() + " minigame started.");
	}
	
	public void addWaitingPlayer(Player participant) {
		this.waitingRoom.add(participant);
		participant.setMinigame(this);
	}
	
	public void removeWaitingPlayer(Player participant) {
		this.waitingRoom.remove(participant);
		participant.setMinigame(null);
	}
	
	public void addPariticpant(Player participant) {
		this.participants.add(participant);
	}
	
	@Override
	public void end() {
	}

	@Override
	public void quit(final Player player) {
		if(waitingRoom.contains(player)) {
			waitingRoom.remove(player);
		} else if(getParticipants().contains(player)) {
			player.resetVariousInformation();
			getParticipants().remove(player);
			if(getParticipants().size() == 1) { //one guy left
				for(Player participant : participants) {
					winGame(participant);
					break;
				}
			} else {
				for(Player participant : getParticipants()) {
					participant.getActionSender().sendString(373, 1, "Foes Remaining: " + (getParticipants().size() - 1));
				}
			}
		}
		player.setLocation(getStartLocation());
		player.setTeleportTarget(getStartLocation());
	}

	@Override
	public Boundary getBoundary() {
		return Boundary.create(getName(), Location.create(2373, 5126, 0), Location.create(2424, 5168, 0));
	}

	@Override
	public ItemSafety getItemSafety() {
		return ItemSafety.SAFE;
	}

	@Override
	public String getName() {
		return "Fight Pits";
	}

	@Override
	public List<Player> getParticipants() {
		return participants;
	}

	@Override
	public void start() {
		super.start();
		for(Player player : waitingRoom) {
			player.resetVariousInformation();
			player.setTeleportTarget(Location.create(2399, 5166, 0));
			player.getActionSender().sendWalkableInterface(373);
		}
		participants.addAll(waitingRoom);
		waitingRoom.clear();
		World.getWorld().submit(new Tickable(1) {
			@Override
			public void execute() {
				for(Player player : participants) {
					player.getActionSender().sendString(373, 0, "Current Champion: " + getWinner())
											.sendString(373, 1, "Foes Remaining: " + (getParticipants().size() - 1));
					DialogueManager.openDialogue(player, 212);
					player.getWalkingQueue().reset();
				}
				this.stop();
			}			
		});
		World.getWorld().submit(new Tickable(50) {
			@Override
			public void execute() {
				gameStarted = true;
				for(Player player : participants) {
					DialogueManager.openDialogue(player, 213);
				}
				this.stop();
			}			
		});
	}

	@Override
	public Tickable getGameCycle() {
		return new Tickable(10) {
			@Override
			public void execute() {
				if(!gameStarted && (waitingRoom.size() + participants.size()) >= FightPits.MINIMUM_SIZE && participants.size() <= 1) {
					start();
				}				
			}			
		};
	}

	@Override
	public Location getStartLocation() {
		return Location.create(2399, 5177, 0);
	}

	@Override
	public boolean deathHook(Player player) {
		player.setLocation(getStartLocation());
		player.setTeleportTarget(getStartLocation());
		return true;
	}

	@Override
	public void movementHook(Player player) {
		if(player.getInterfaceAttribute("fightPitOrbs") != null) {
			return;
		}
		if(getParticipants().contains(player)) {
			super.movementHook(player);
		} else if(waitingRoom.contains(player) && !BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Fight Pits Waiting Room")) {
			this.waitingRoom.remove(player);
		}
	}

	@Override
	public void killHook(Player player, Mob victim) {
		super.killHook(player, victim);
		if(victim.isPlayer()) {
			Player pVictim = (Player) victim;
			if(getParticipants().contains(pVictim)) {
				quit(pVictim);
			}
		}
		if(getParticipants().contains(player) && getParticipants().size() <= 1) { //only us left
			winGame(player);
		} else {
			for(Player participant : getParticipants()) {
				participant.getActionSender().sendString(373, 1, "Foes Remaining: " + (getParticipants().size() - 1));
			}
		}
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}
	
	public void winGame(Player player) {
		if(player.getCombatState().isDead()) {
			player.getSkills().setLevel(Skills.HITPOINTS, 1);
			player.getCombatState().setDead(false);
			player.playAnimation(Animation.create(-1));
			player.getCombatState().setCanMove(true);
		}
		player.setFightPitsWinner(true);
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		setWinner("JalYt-Ket-" + player.getName());
		player.getActionSender().sendString(373, 1, "You're the Winner!")
								.sendString(373, 0, "Current Champion: " + getWinner());
		gameStarted = false; //winner means the game ended
	}

	@Override
	public boolean attackMobHook(Player player, Mob victim) {
		if(!gameStarted) {
			player.getActionSender().sendMessage("You're not allowed to attack yet!");
			return false;
		}
		return true;
	}

}
