package org.rs2server.rs2.model.minigame.impl;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.boundary.Boundary;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.tickable.Tickable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author 'Mystic Flow
 */
public class Barrows {

	private static final Logger logger = LoggerFactory.getLogger(Barrows.class);

	public static enum BarrowsBrother {

		AHRIM(1672, 20770, Location.create(3557, 9703, 3), Location.create(3554, 9700, 3)),

		DHAROK(1673, 20720, Location.create(3556, 9718, 3), Location.create(3553, 9716, 3)),

		GUTHAN(1674, 20722, Location.create(3534, 9704, 3), Location.create(3537, 9705, 3)),

		KARIL(1675, 20771, Location.create(3546, 9684, 3), Location.create(3549, 9684, 3)),

		TORAG(1676, 20721, Location.create(3568, 9683, 3), Location.create(3568, 9687, 3)),

		VERAC(1677, 20772, Location.create(3578, 9706, 3), Location.create(3574, 9704, 3));

		private int npcId;
		private String name;
		private int coffinId;
		private Location loc;
		private Location spawn;

		private BarrowsBrother(int npcId, int coffinId, Location loc, Location spawn) {
			this.npcId = npcId;
			this.coffinId = coffinId;
			this.name = super.toString().toLowerCase();
			this.loc = loc;
			this.spawn = spawn;
		}

		public int getNpcId() {
			return npcId;
		}

		public int getCoffinId() {
			return coffinId;
		}

		public Location getLocation() {
			return loc;
		}

		public Location getSpawn() {
			return spawn;
		}

		public Boundary getHillArea() {
			return BoundaryManager.boundaryForName(name + "_hill").get(0);
		}

		private static Map<Integer, BarrowsBrother> brothers = new HashMap<Integer, BarrowsBrother>();

		public static Map<Integer, BarrowsBrother> getBrothers() {
			return brothers;
		}

		public static BarrowsBrother forId(int npcId) {
			return brothers.get(npcId);
		}

		static {
			for (BarrowsBrother brother : BarrowsBrother.values()) {
				brothers.put(brother.getNpcId(), brother);
			}
		}
	}

	public static final Random RAND = new Random();

	public static final int[] TUNNEL_NPCSs = { 2036, 2032, 4920, 2031 };

	public static boolean enterCrypt(final Player player) {
		final BarrowsBrother[] brothers = BarrowsBrother.values();
		for (BarrowsBrother brother : brothers) {
			final Boundary hill = brother.getHillArea();
			if (BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), hill)) {
				World.getWorld().submit(new Tickable(2) {
					@Override
					public void execute() {
						if (player.getAttribute("barrows_tunnel") == null) {
							final BarrowsBrother tunnel = brothers[RAND.nextInt(brothers.length)];
							if (Constants.DEBUG)
								logger.info("Barrows tunnel selected as {} for player {}", tunnel.name(), player.getName());
							player.setAttribute("barrows_tunnel", tunnel);
						}
						player.setTeleportTarget(brother.getLocation());
						player.getActionSender().sendMessage("You've broken into a crypt!");
						player.getActionSender().updateMinimap(ActionSender.BLACKOUT_MAP);
						stop();
					}
				});
				return true;
			}
		}
		return false;
	}

	public static boolean stairInteraction(Player player, int stairs) {

		if (player.hasAttribute("teleporting") || player.hasAttribute("busy"))
			return false;
		NPC npc = player.getAttribute("currentlyFightingBrother");
		switch (stairs) {
		case 20667:
			player.setTeleportTarget(Location.create(3565, 3288, 0));
			if (npc != null && npc.getSkills().getLevel(Skills.HITPOINTS) > 0) {
				World.getWorld().unregister(npc);
				player.removeAttribute("currentlyFightingBrother");
			}
			break;
		case 20668:
			player.setTeleportTarget(Location.create(3574, 3297, 0));
			if (npc != null && npc.getSkills().getLevel(Skills.HITPOINTS) > 0) {
				World.getWorld().unregister(npc);
				player.removeAttribute("currentlyFightingBrother");
			}
			break;
		case 20669:
			player.setTeleportTarget(Location.create(3577, 3282, 0));
			if (npc != null && npc.getSkills().getLevel(Skills.HITPOINTS) > 0) {
				World.getWorld().unregister(npc);
				player.removeAttribute("currentlyFightingBrother");
			}
			break;
		case 20670:
			player.setTeleportTarget(Location.create(3566, 3275, 0));
			if (npc != null && npc.getSkills().getLevel(Skills.HITPOINTS) > 0) {
				World.getWorld().unregister(npc);
				player.removeAttribute("currentlyFightingBrother");
			}
			break;
		case 20671:
			player.setTeleportTarget(Location.create(3554, 3282, 0));
			if (npc != null && npc.getSkills().getLevel(Skills.HITPOINTS) > 0) {
				World.getWorld().unregister(npc);
				player.removeAttribute("currentlyFightingBrother");
			}
			break;
		case 20672:
			player.setTeleportTarget(Location.create(3557, 3297, 0));
			if (npc != null && npc.getSkills().getLevel(Skills.HITPOINTS) > 0) {
				World.getWorld().unregister(npc);
				player.removeAttribute("currentlyFightingBrother");
			}
			break;
		}
		player.getActionSender().updateMinimap(ActionSender.NO_BLACKOUT);
		return false;
	}

	public static int getIndexForBrother(NPC npc) {
		for (BarrowsBrother brother : BarrowsBrother.values()) {
			if (brother.npcId == npc.getId())
				return brother.ordinal();
		}
		return -1;
	}
}