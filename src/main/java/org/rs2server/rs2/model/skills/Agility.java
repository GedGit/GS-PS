package org.rs2server.rs2.model.skills;

import org.rs2server.Server;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.ScriptManager;
import org.rs2server.rs2.domain.service.api.PermissionService;
import org.rs2server.rs2.domain.service.impl.PermissionServiceImpl;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.npc.Pet;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.ArrayList;
import java.util.List;

public class Agility {

	/**
	 * Represents an agility obstacle.
	 *
	 * @author Michael
	 */
	public enum Obstacle {

		WILDERNESS_DITCH(23271, Location.create(0, 0, 0), 1, 0, "wildernessDitch", true),

		TAVERLY_OBSTACLE_PIPE(16509, Location.create(2890, 9799, 0), 70, 0, "taverlyObstaclePipe", true),

		TAVERLY_OBSTACLE_PIPE2(16509, Location.create(2887, 9799, 0), 70, 0, "taverlyObstaclePipe", true),

		SHAMAN_ROCK_CLIMB(27362, Location.create(1455, 3690, 0), 0, 0, "shamanRockClimb1", true),

		SHAMAN_ROCK_CLIMB_TWO(27362, Location.create(1459, 3690, 0), 0, 0, "shamanRockClimb2", true),

		SHAMAN_ROCK_CLIMB_THREE(27362, Location.create(1471, 3687, 0), 0, 0, "shamanRockClimb3", true),

		SHAMAN_ROCK_CLIMB_FOUR(27362, Location.create(1475, 3687, 0), 0, 0, "shamanRockClimb4", true),

		// TAVERLY_GATE(2623, Location.create(0, 0, 0), 1, 0, "taverlyGate"),

		FALADOR_WESTERN_CRUMBLING_WALL(11844, Location.create(2935, 3355, 0), 5, 0.5, "faladorCrumblingWall", true),

		MOTHERLODE_DARK_TUNNEL(10047, Location.create(3760, 5670, 0), 54, 0.5, "motherlodeDarkTunnel", true),

		EDGE_DUNGEON_PIPE(16511, Location.create(3150, 9906, 0), 51, 0.5, "edgeDungeonPipe", true),

		EDGE_DUNGEON_PIPE_2(16511, Location.create(3153, 9906, 0), 51, 0.5, "edgeDungeonPipe", true),

		TAVERLY_SPIKE_JUMP(16510, Location.create(2879, 9813, 0), 80, 0.5, "taverlySpikeJump", true),

		FREMENIK_SPIKE_JUMP(16544, Location.create(2774, 10003, 0), 81, 0.5, "fremmySpikeJump", true),

		FREMENIK_SPIKE_JUMP_2(16544, Location.create(2769, 10002, 0), 81, 0.5, "fremmySpikeJump2", true),

		MOTHERLODE_DARK_TUNNEL_2(10047, Location.create(3764, 5671, 0), 54, 0.5, "motherlodeDarkTunnel2", true),

		ARDOUGNE_LOG_BALANCE(16548, Location.create(2601, 3336, 0), 33, 7, "ardougneLogBalance", true),

		ARDOUGNE_LOG_BALANCE_2(16546, Location.create(2599, 3336, 0), 33, 7, "ardougneLogBalance", true),

		ARDOUGNE_LOG_BALANCE_3(16547, Location.create(2600, 3336, 0), 33, 7, "ardougneLogBalance", true),

		/**
		 * Gnome obstacle course
		 */

		GNOME_COURSE_LOG_BALANCE(2295, Location.create(2474, 3435, 0), 1, 7, "gnomeLogBalance", false),

		GNOME_COURSE_OBSTACLE_NET_1(2285, Location.create(2471, 3425, 0), 1, 8, "gnomeObstacleNet", false),

		GNOME_COURSE_OBSTACLE_NET_2(2285, Location.create(2473, 3425, 0), 1, 8, "gnomeObstacleNet", false),

		GNOME_COURSE_OBSTACLE_NET_3(2285, Location.create(2475, 3425, 0), 1, 8, "gnomeObstacleNet", false),

		GNOME_COURSE_TREE_BRANCH(2313, Location.create(2473, 3422, 1), 1, 5, "gnomeTreeBranch", false),

		GNOME_COURSE_BALANCE_ROPE(2312, Location.create(2478, 3420, 2), 1, 7, "gnomeBalanceRope", false),

		GNOME_COURSE_TREE_BRANCH_2(2314, Location.create(2486, 3419, 2), 1, 5, "gnomeTreeBranch2", false),

		GNOME_COURSE_OBSTACLE_NET_4(2286, Location.create(2483, 3426, 0), 1, 8, "gnomeObstacleNet2", false),

		GNOME_COURSE_OBSTACLE_NET_5(2286, Location.create(2485, 3426, 0), 1, 8, "gnomeObstacleNet2", false),

		GNOME_COURSE_OBSTACLE_NET_6(2286, Location.create(2487, 3426, 0), 1, 8, "gnomeObstacleNet2", false),

		GNOME_COURSE_OBSTACLE_PIPE_1(154, Location.create(2484, 3431, 0), 1, 8, "gnomeObstaclePipe", false),

		GNOME_COURSE_OBSTACLE_PIPE_2(154, Location.create(2487, 3431, 0), 1, 8, "gnomeObstaclePipe", false),

		SLAYER_CAVE_ROCK_CLIMB(26724, Location.create(2427, 9763, 0), 67, 5, "slayerRockClimb", true),

		SLAYER_CAVE_ROCK_CLIMB_2(26724, Location.create(2427, 9766, 0), 67, 5, "slayerRockClimb", true),

		/**
		 * Draynor Village Rooftop Course
		 */
		DRAYNOR_VILLAGE_ROCK_CLIMB(10073, Location.create(3103, 3279, 0), 10, 5, "draynorRockClimb", false),

		DRAYNOR_VILLAGE_TIGHT_ROPE_1(10074, Location.create(3098, 3277, 3), 10, 8, "draynorTightRope1", false),

		DRAYNOR_VILLAGE_TIGHT_ROPE_2(10075, Location.create(3092, 3276, 3), 10, 7, "draynorTightRope2", false),

		DRAYNOR_VILLAGE_NARROW_WALL(10077, Location.create(3089, 3264, 3), 10, 7, "draynorNarrowWall", false),

		DRAYNOR_VILLAGE_JUMP_WALL(10084, Location.create(3088, 3256, 3), 10, 10, "draynorJumpWall", false),

		DRAYNOR_VILLAGE_JUMP_GAP(10085, Location.create(3095, 3255, 3), 10, 4, "draynorJumpGap", false),

		DRAYNOR_VILLAGE_CRATE_JUMP(10086, Location.create(3102, 3261, 3), 10, 79, "draynorCrateJump", false),

		/**
		 * Varrock Rooftop Course
		 */
		VARROCK_ROCK_CLIMB(10586, Location.create(3221, 3414, 0), 30, 12, "varrockRockClimb", false),

		VARROCK_TIGHT_ROPE(10587, Location.create(3213, 3414, 3), 30, 21, "varrockTightRope", false),

		VARROCK_LEAP_GAP_1(10642, Location.create(3200, 3416, 3), 30, 17, "varrockLeapGap1", false),

		VARROCK_BALANCE_WALL(10777, Location.create(3191, 3415, 1), 30, 25, "varrockBalanceWall", false),

		VARROCK_LEAP_GAP_2(10778, Location.create(3193, 3401, 3), 30, 9, "varrockLeapGap2", false),

		VARROCK_LEAP_GAP_3(10779, Location.create(3209, 3397, 3), 30, 22, "varrockLeapGap3", false),

		VARROCK_LEAP_GAP_4(10780, Location.create(3233, 3402, 3), 30, 4, "varrockLeapGap4", false),

		VARROCK_HURDLE_LEDGE(10781, Location.create(3236, 3409, 3), 30, 3, "varrockHurdleLedge", false),

		VARROCK_JUMPOFF_EDGE(10817, Location.create(3236, 3416, 3), 30, 125, "varrockJumpOffEdge", false),

		// int id, object location, int levelRequired, double experience, String
		// scriptString, boolean shortcut

		/**
		 * Seers Village Rooftop Course
		 */
		SEERS_WALL_CLIMB(11373, Location.create(2729, 3489, 0), 60, 65, "seersWallClimb", false),

		SEERS_GAP_JUMP(11374, Location.create(2720, 3492, 3), 60, 40, "seersGapJump", false),

		SEERS_TIGHT_ROPE(11378, Location.create(2710, 3489, 2), 60, 40, "seersTightRope", false),

		SEERS_GAP_JUMP_2(11375, Location.create(2710, 3476, 2), 60, 55, "seersGapJump2", false),

		SEERS_GAP_JUMP_3(11376, Location.create(2700, 3469, 3), 60, 35, "seersGapJump3", false),

		SEERS_GAP_JUMP_4(11377, Location.create(2703, 3461, 2), 60, 40, "seersGapJump4", false),

		/**
		 * Ardougne Rooftop Course
		 */
		ARDY_WOOD_CLIMB(11405, Location.create(2673, 3298, 0), 90, 65, "ardyWoodClimb", false),

		ARDY_GAP_JUMP(11406, Location.create(2670, 3310, 3), 90, 75, "ardyGapJump", false),

		ARDY_PLANK_WALK(11631, Location.create(2661, 3318, 3), 90, 65, "ardyPlankWalk", false),

		ARDY_GAP_JUMP_2(11429, Location.create(2653, 3317, 3), 90, 55, "ardyGapJump2", false),

		ARDY_GAP_JUMP_3(11430, Location.create(2653, 3308, 3), 90, 60, "ardyGapJump3", false),

		ARDY_STEP_ROOF(11633, Location.create(2654, 3300, 3), 90, 65, "ardyStepRoof", false),

		ARDY_GAP_JUMP_4(11630, Location.create(2656, 3296, 3), 90, 55, "ardyGapJump4", false),

		/**
		 * Al Kharid rooftop course
		 */
		ALKHARID_ROOF_WALL(10093, Location.create(3273, 3195, 0), 1, 5, "kharidWall", false),

		ALKHARID_TIGHT_ROPE(10284, Location.create(3272, 3181, 3), 1, 5, "kharidRope", false),

		ALKHARID_CABLE(10355, Location.create(3269, 3166, 3), 1, 5, "kharidCable", false),

		ALKHARID_ZIPLINE(10356, Location.create(3302, 3163, 3), 1, 5, "kharidZipline", false),

		ALKHARID_TREE(10357, Location.create(3318, 3166, 1), 1, 5, "kharidTree", false),

		ALKHARID_BEAM(10094, Location.create(3316, 3179, 2), 1, 5, "kharidBeam", false),

		ALKARID_ROPE2(10583, Location.create(3303, 3186, 3), 1, 5, "kharidRope2", false),

		/**
		 * Varrock rooftop course
		 */
		// TODO

		/**
		 * Barbarian agility course
		 */

		BARBARIAN_COURSE_OBSTACLE_PIPE(2287, Location.create(2552, 3559, 0), 35, 0, "barbarianObstaclePipe", false),

		BARBARIAN_COURSE_ROPE_SWING(2282, Location.create(2551, 3550, 0), 35, 22, "barbarianRopeSwing", false),

		BARBARIAN_COURSE_LOG_BALANCE(2294, Location.create(2550, 3546, 0), 35, 13.7, "barbarianLogBalance", false),

		BARBARIAN_COURSE_OBSTACLE_NET(2284, Location.create(2538, 3545, 0), 35, 8.2, "barbarianObstacleNet", false),

		BARBARIAN_COURSE_LEDGE(2302, Location.create(2535, 3547, 1), 35, 22, "barbarianLedge", false),

		BARBARIAN_COURSE_CRUMBLING_WALL_1(1948, Location.create(2536, 3553, 0), 35, 13.7, "barbarianCrumblingWall1",
				false),

		BARBARIAN_COURSE_CRUMBLING_WALL_2(1948, Location.create(2539, 3553, 0), 35, 13.7, "barbarianCrumblingWall2",
				false),

		BARBARIAN_COURSE_CRUMBLING_WALL_3(1948, Location.create(2542, 3553, 0), 35, 13.7, "barbarianCrumblingWall3",
				false),
		//
		;

		/**
		 * The list of obstacles.
		 */
		private static List<Obstacle> obstacles = new ArrayList<Obstacle>();

		/**
		 * Populates the obstacle list
		 */
		static {
			for (Obstacle obstacle : Obstacle.values()) {
				obstacles.add(obstacle);
			}
		}

		public Obstacle forId(int id) {
			for (Obstacle obstacle : obstacles) {
				if (obstacle.getId() == id) {
					return obstacle;
				}
			}
			return null;
		}

		public static Obstacle forLocation(Location location) {
			for (Obstacle obstacle : obstacles) {
				if (obstacle.getLocation().equals(location)) {
					return obstacle;
				}
			}
			return null;
		}

		/**
		 * Object id.
		 */
		private int id;

		/**
		 * The location of this obstacle.
		 */
		private Location location;

		/**
		 * The level required to use this obstacle.
		 */
		private int levelRequired;

		/**
		 * The experience granted for tackling this obstacle.
		 */
		private double experience;

		/**
		 * The script that is executed for this obstacle.
		 */
		private String scriptString;

		private boolean shortcut;

		private Obstacle(int id, Location location, int levelRequired, double experience, String scriptString,
				boolean shortcut) {
			this.id = id;
			this.location = location;
			this.levelRequired = levelRequired;
			this.experience = experience;
			this.scriptString = scriptString;
			this.shortcut = shortcut;
		}

		public int getId() {
			return id;
		}

		public Location getLocation() {
			return location;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public double getExperience() {
			return experience;
		}

		public String getScriptString() {
			return scriptString;
		}

		public boolean isShortCut() {
			return shortcut;
		}

		/**
		 * Handles the rare chance of obtaining a skilling pet
		 * 
		 * @param player
		 *            the player
		 * @param dropRate
		 *            the chance
		 */
		private void handlePet(Player player) {
			int dropRate = 10000 - (player.getSkills().getLevelForExperience(Skills.AGILITY) * 25);
			int random = Misc.random(dropRate);

			if (random == 3000) {
				Pet.Pets pets = Pet.Pets.GIANT_SQUIRREL;
				Pet.givePet(player, new Item(pets.getItem()));
			}
		}
	}

	public Agility() {
	}

	public static void tackleObstacle(Player player, Obstacle obstacle, GameObject object) {
		final PermissionService permissionService = Server.getInjector().getInstance(PermissionService.class);
		if ((!obstacle.isShortCut()
				&& player.getSkills().getLevelForExperience(Skills.AGILITY) < obstacle.getLevelRequired())
				|| (player.getSkills().getLevelForExperience(Skills.AGILITY) < obstacle.getLevelRequired()
						&& obstacle.isShortCut()
						&& !permissionService.isAny(player, PermissionServiceImpl.SPECIAL_PERMISSIONS))) {
			player.getActionSender().removeAllInterfaces();
			player.getActionSender().sendDialogue("", DialogueType.MESSAGE, -1, null,
					"You need an Agility level of " + obstacle.getLevelRequired() + " to tackle this obstacle.");
			player.playAnimation(Animation.create(-1));
			return;
		}
		if (player.hasAttribute("busy"))
			return;
		player.setAttribute("busy", true);
		if (ScriptManager.getScriptManager().invokeWithFailTest(obstacle.getScriptString(), player, obstacle, object))
			obstacle.handlePet(player);
		else {
			player.removeAttribute("busy");
			if (Constants.DEBUG)
				System.out.println("Script name NOT found!");
			player.getActionSender().sendMessage("Nothing interesting happens.");
		}
	}

	public static void forceMovement(final Player player, final Animation animation, final int[] forceMovement,
			int ticks, final boolean removeAttribute) {
		World.getWorld().submit(new Tickable(ticks) {
			@Override
			public void execute() {
				player.playAnimation(animation);
				player.setForceWalk(forceMovement, removeAttribute);
				player.getUpdateFlags().flag(UpdateFlag.FORCE_MOVEMENT);
				this.stop();
			}
		});
	}

	public static void reset(final Player player, int delay) {
		World.getWorld().submit(new Tickable(delay) {

			@Override
			public void execute() {
				this.stop();
				Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
				if (weapon != null && weapon.getEquipmentDefinition() != null) {
					player.setStandAnimation(weapon.getEquipmentDefinition().getAnimation().getStand());
					player.setRunAnimation(weapon.getEquipmentDefinition().getAnimation().getRun());
					player.setWalkAnimation(weapon.getEquipmentDefinition().getAnimation().getWalk());
					player.setStandTurnAnimation(weapon.getEquipmentDefinition().getAnimation().getStandTurn());
					player.setTurn180Animation(weapon.getEquipmentDefinition().getAnimation().getTurn180());
					player.setTurn90ClockwiseAnimation(
							weapon.getEquipmentDefinition().getAnimation().getTurn90ClockWise());
					player.setTurn90CounterClockwiseAnimation(
							weapon.getEquipmentDefinition().getAnimation().getTurn90CounterClockWise());
				} else {
					player.setDefaultAnimations();
				}
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			}
		});
	}

	public static void forceTeleport(final Player player, final Animation animation, final Location newLocation,
			int ticksBeforeAnim, int ticks) {
		if (animation != null) {
			if (ticksBeforeAnim < 1) {
				player.playAnimation(animation);
			} else {
				World.getWorld().submit(new Tickable(ticksBeforeAnim) {
					@Override
					public void execute() {
						player.playAnimation(animation);
						this.stop();
					}
				});
			}
		}
		World.getWorld().submit(new Tickable(ticks) {
			@Override
			public void execute() {
				player.setTeleportTarget(newLocation);
				player.removeAttribute("busy");
				this.stop();
			}
		});
	}

	public static void delayedAnimation(final Player player, Animation anim, int ticks) {
		World.getWorld().submit(new Tickable(ticks) {

			@Override
			public void execute() {
				this.stop();
				player.playAnimation(anim);
			}
		});
	}

	public static void forceWalkingQueue(final Player player, final Animation animation, final int x, final int y,
			int delayBeforeMovement, final int ticks, final boolean removeAttribute) {
		Tickable tick = new Tickable(delayBeforeMovement) {
			@Override
			public void execute() {

				if (animation != null) {
					player.setWalkAnimation(animation);
					player.setRunAnimation(animation);
					player.setStandAnimation(animation);
					player.setStandTurnAnimation(animation);
					player.setTurn90ClockwiseAnimation(animation);
					player.setTurn90CounterClockwiseAnimation(animation);
					player.setTurn180Animation(animation);
					player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				}

				player.getWalkingQueue().reset();
				player.getWalkingQueue().addStep(x, y);
				player.getWalkingQueue().finish();

				World.getWorld().submit(new Tickable(ticks) {
					@Override
					public void execute() {
						Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
						if (weapon != null && weapon.getEquipmentDefinition() != null) {
							player.setStandAnimation(weapon.getEquipmentDefinition().getAnimation().getStand());
							player.setRunAnimation(weapon.getEquipmentDefinition().getAnimation().getRun());
							player.setWalkAnimation(weapon.getEquipmentDefinition().getAnimation().getWalk());
							player.setStandTurnAnimation(weapon.getEquipmentDefinition().getAnimation().getStandTurn());
							player.setTurn180Animation(weapon.getEquipmentDefinition().getAnimation().getTurn180());
							player.setTurn90ClockwiseAnimation(
									weapon.getEquipmentDefinition().getAnimation().getTurn90ClockWise());
							player.setTurn90CounterClockwiseAnimation(
									weapon.getEquipmentDefinition().getAnimation().getTurn90CounterClockWise());
						} else {
							player.setDefaultAnimations();
						}
						player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
						if (removeAttribute) {
							player.removeAttribute("busy");
						}
						this.stop();
					}
				});
				this.stop();
			}
		};
		if (delayBeforeMovement < 1) {
			tick.execute();
		} else {
			World.getWorld().submit(tick);
		}
	}

	public static void animateObject(final GameObject gameObject, final Animation animation, int ticks) {
		Tickable tick = new Tickable(ticks) {
			@Override
			public void execute() {
				for (Region r : gameObject.getRegion().getSurroundingRegions()) {
					for (Player player : r.getPlayers()) {
						player.getActionSender().animateObject(gameObject, animation.getId());
					}
				}
				this.stop();
			}
		};
		if (tick.getTickDelay() >= 1) {
			World.getWorld().submit(tick);
		} else {
			tick.execute();
		}
	}

	public static void setRunningToggled(final Player player, boolean toggled, int ticks) {
		final boolean originalToggledState = player.getWalkingQueue().isRunningToggled();
		player.getWalkingQueue().setRunningToggled(toggled);
		Tickable tick = new Tickable(ticks) {
			@Override
			public void execute() {
				player.getWalkingQueue().setRunningToggled(originalToggledState);
				this.stop();
			}
		};
		if (tick.getTickDelay() >= 1) {
			World.getWorld().submit(tick);
		} else {
			tick.execute();
		}
	}

	public static void damage(final Player player, final int damage, int ticks) {
		Tickable tick = new Tickable(ticks) {
			@Override
			public void execute() {
				int dmg = damage;
				if (dmg > player.getSkills().getLevel(Skills.HITPOINTS)) {
					dmg = player.getSkills().getLevel(Skills.HITPOINTS);
				}
				player.inflictDamage(new Hit(damage), player);
				this.stop();
			}
		};
		if (tick.getTickDelay() >= 1) {
			World.getWorld().submit(tick);
		} else {
			tick.execute();
		}
	}
}
