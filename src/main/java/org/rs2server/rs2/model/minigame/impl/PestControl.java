package org.rs2server.rs2.model.minigame.impl;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.CombatNPCDefinition;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skill;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.boundary.Boundary;
import org.rs2server.rs2.model.map.Directions.NormalDirection;
import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.rs2.model.minigame.impl.PestControlManager.Boat;
import org.rs2server.rs2.model.minigame.impl.PestControlManager.Portal;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.npc.NPCDefinition;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PestControl extends AbstractMinigame {

	public static final Random RANDOM = new Random();
	public static final Location BOAT_START = Location.create(2656, 2614, 0);
	public static final Location VOID_KNIGHT_POS = Location.create(2656, 2592, 0);
	public int waitTicks = 100;
	public int gameTicks = 2000;

	private Boat level;
	public List<Player> players = new ArrayList<Player>();
	public NPC voidKnight;
	private List<NPC> npcs = new ArrayList<NPC>();
	private Tickable gameTick; 
	private int height = 0;
	public long[] lastSpawn = new long[4];

	public PestControl(Boat level, int height) {
		this.level = level;
		PestControlManager.getPestControlManager().heights[height / 4] = true;
		init();
	}

	public void joinPlayer(Player player) {
		players.add(player);
		player.setTeleportTarget(level.endLoc);
		updateInterfaces(true);
	}

	@Override
	public Tickable getGameCycle() {
		return new Tickable(1) {
			@Override
			public void execute() {
				if (players.size() >= 25 || waitTicks == 0 || Constants.DEBUG) {
					start();
				} else if (waitTicks == 0 && players.size() < 5) {
					waitTicks = 500;
				} else if (players.size() > 0) {
					updateInterfaces(false);
					waitTicks--;
				}
			}
		};
	}

	protected void updateInterfaces(boolean sendInterface) {
		String first = "Next Departure: " + Integer.toString((waitTicks + 100) / 100) + " min"
				+ (waitTicks > 100 ? "s" : "");
		String second = "Players Ready: " + players.size();
		String third = "Points:";
		String fourth = TextUtils.upperFirst(level.name());
		for (Player player : players) {
			player.getActionSender().sendString(407, 11, first);
			player.getActionSender().sendString(407, 12, second);
			player.getActionSender().sendString(407, 15,
					third + Integer.toString(player.getSettings().getPestPoints()));
			player.getActionSender().sendString(407, 16, fourth);
			if (sendInterface)
				player.getActionSender().sendWalkableInterface(407);
		}
	}

	@Override
	public void start() {
		super.start();
		PestControlManager.getPestControlManager().removeWaiting(level, height);
		int id = getRandomId();
		voidKnight = new NPC(NPCDefinition.forId(id), id, VOID_KNIGHT_POS.transform(0, 0, height), null, null,
				NormalDirection.SOUTH.npcIntValue());
		World.getWorld().register(voidKnight);
		for (Player player : players) {
			player.removeAttribute("hits_dealt");
			player.getActionSender().sendWalkableInterface(408);
			player.setTeleportTarget(getStartLocation());
		}
		Portal[] portals = Portal.values();
		Collections.shuffle(Arrays.asList(portals));
		for (Portal portal : portals) {
			NPC npc = new NPC(NPCDefinition.forId(portal.shieldId), portal.shieldId, portal.spawn, null, null, 0);
			npc.setCombatDefinition(CombatNPCDefinition.of(portal.shieldId));
			if (level != Boat.NOVICE)
				npc.getSkills().setLevel(Skills.HITPOINTS, 2500);
			npc.setAttribute("is_portal", Boolean.TRUE);
			npc.setAttribute("portal", portal);
			npcs.add(npc);
			World.getWorld().register(npc);
			clip(npc, portal, 0);
		}

		// game tick
		gameTick = new Tickable(1) {

			@SuppressWarnings("unlikely-arg-type")
			@Override
			public void execute() {
				gameTicks--;
				String knightHp = Integer.toString(voidKnight.getSkills().getLevel(Skills.HITPOINTS));
				String time = Integer.toString((gameTicks + 100) / 100) + " min" + (gameTicks > 100 ? "s" : "");
				for (Player player : players) {
					player.getActionSender().sendString(408, 1, knightHp);
					player.getActionSender().sendString(408, 11,
							Integer.toString((Integer) player.getAttribute("hits_dealt", 0)));
					player.getActionSender().sendString(408, 0, time);
					for (int i = 0; i < 4; i++) {
						player.getActionSender().sendString(408,
								((Portal) npcs.get(i).getAttribute("portal")).interfacConfig / 2 + 4,
								Integer.toString(npcs.get(i).getSkills().getLevel(Skills.HITPOINTS)));
					}
				}
				if (gameTicks >= 1900) {
					if (gameTicks == 1975 || gameTicks == 1950 || gameTicks == 1925 || gameTicks == 1900) {
						NPC npc = npcs.get(((2000 - gameTicks) % 4));
						Portal portal = npc.getAttribute("portal");
						updatePortal(portal);
						npc.transformNPC(npc.getDefinition().getId() - 4);
						if (level != Boat.NOVICE)
							npc.getSkills().setLevel(Skills.HITPOINTS, 2500);
						else
							npc.getSkills().setLevel(Skills.HITPOINTS,
									npc.getCombatDefinition().getSkills().get(Skill.HITPOINTS));
						clip(npc, portal, 1);
					}
				}
				if (voidKnight.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
					World.getWorld().unregister(voidKnight);
					end(false);
				}

				for (NPC npc : npcs) {
					if (npc.isDestroyed())
						continue;
					Portal portal = npc.getAttribute("portal");
					if (System.currentTimeMillis() - lastSpawn[portal.ordinal()] > 5000) {
						for (int i = PestControlManager.PC_NPCS.length - 1; i > 0; i--) {
							boolean spawn = false;
							if (level == Boat.NOVICE && i > 1)
								spawn = RANDOM.nextInt(1 + i * 15) == 3;
							else if (level == Boat.INTERMEDIATE && i > 1 || i == 0)
								spawn = RANDOM.nextInt(1 + i * 7) == 3;
							else if (level == Boat.EXPERT && i != 0)
								spawn = RANDOM.nextInt(1 + i * 10) == 3;
							else if (level.ordinal() == i)
								spawn = true;

							if (spawn) {
								int id = PestControlManager.PC_NPCS[level.ordinal()][RANDOM
										.nextInt(PestControlManager.PC_NPCS[level.ordinal()].length)];
								NPC spawnNpc = new NPC(NPCDefinition.forId(id), id,
										portal.spawnLocs[RANDOM.nextInt(portal.spawnLocs.length)].transform(0, 0,
												height),
										portal.spawn.transform(-10, -10, height),
										portal.spawn.transform(10, 10, height), RANDOM.nextInt(4));
								World.getWorld().register(spawnNpc);
								lastSpawn[portal.ordinal()] = System.currentTimeMillis();
								break;
							}
						}
					}
				}
			}

		};
		World.getWorld().submit(gameTick);
	}

	private void clip(NPC npc, Portal portal, int state) {
		if (portal == Portal.WEST || portal == Portal.EAST) { // 0x200000
			Location base = portal.spawn.transform(1, -1, height);
			for (int i = 0; i < 5; i++) {
				RegionClipping.addClipping(base.getX(), base.getY() + i, base.getPlane(), 256);
				if (i >= 1 && 4 < i) {
					if (state == 0) {
						RegionClipping.addClipping(base.getX() - 1, base.getY() + i, base.getPlane(), 256);
						RegionClipping.addClipping(base.getX() + 1, base.getY() + i, base.getPlane(), 256);
					} else if (state == 1) {
						RegionClipping.removeClipping(base.getX() - 1, base.getY() + i, base.getPlane(), 256);
						RegionClipping.removeClipping(base.getX() + 1, base.getY() + i, base.getPlane(), 256);
					}
				}
			}
		} else {
			Location base = portal.spawn.transform(-1, 1, height);
			for (int i = 0; i < 5; i++) {
				RegionClipping.addClipping(base.getX() + i, base.getY(), base.getPlane(), 256);
				if (i >= 1 && 4 < i) {
					if (state == 0) {
						RegionClipping.addClipping(base.getX() + i, base.getY() - 1, base.getPlane(), 256);
						RegionClipping.addClipping(base.getX() + i, base.getY() + 1, base.getPlane(), 256);
					} else if (state == 1) {
						RegionClipping.removeClipping(base.getX() + i, base.getY() - 1, base.getPlane(), 256);
						RegionClipping.removeClipping(base.getX() + i, base.getY() + 1, base.getPlane(), 256);
					}
				}
			}
		}

	}

	protected void updatePortal(Portal portal) {
		for (Player player : players) {
			player.getActionSender().sendInterfaceConfig(408, portal.interfacConfig, true);
		}

	}

	private int getRandomId() {
		if (RANDOM.nextBoolean())
			return 3782;
		else
			return 3784 + RANDOM.nextInt(2);
	}

	public void quit(Player player) {
		player.getInterfaceState().interfaceClosed();
		player.getActionSender().removeAllInterfaces();
		player.setMinigame(null);
		player.setAttribute("temporaryHeight", null);
		player.setTeleportTarget(level.startLoc.transform(0, 0, height));
		player.resetVariousInformation();
		if (players != null) {
			players.remove(player);
			if (players.size() < 1 && gameTick != null) {
				end(false);
			}
		}
	}

	public void end(boolean won) {
		//gameTick.endGame();
		for (Player player : players)
			player.setTeleportTarget(level.startLoc.transform(0, 0, height));
	}

	@Override
	public Location getStartLocation() {
		return BOAT_START.transform(RANDOM.nextInt(4), -RANDOM.nextInt(6), height);
	}

	@Override
	public boolean deathHook(Player player) {
		player.setLocation(getStartLocation());
		player.setTeleportTarget(getStartLocation());
		return true;
	}

	@Override
	public void movementHook(Player player) {

	}

	@Override
	public void killHook(Player player, Mob victim) {

	}

	@Override
	public ItemSafety getItemSafety() {
		return ItemSafety.SAFE;
	}

	public String getName() {
		return "Pest Control";
	}

	@Override
	public Boundary getBoundary() {
		return null;
	}

}
