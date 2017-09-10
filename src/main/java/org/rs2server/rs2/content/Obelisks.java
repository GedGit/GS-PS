package org.rs2server.rs2.content;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.region.Region;

import java.util.Random;

public class Obelisks {

	private static final Random r = new Random();

	private static final Location[][] OBELISKS = { { // Level 13 wilderness.
			Location.create(3154, 3622, 0), Location.create(3158, 3622, 0), Location.create(3154, 3618, 0),
			Location.create(3158, 3618, 0), },
			{ // Level 19 wilderness.
					Location.create(3225, 3669, 0), Location.create(3229, 3669, 0), Location.create(3225, 3665, 0),
					Location.create(3229, 3665, 0), },
			{ // Level 27 wilderness.
					Location.create(3033, 3734, 0), Location.create(3037, 3734, 0), Location.create(3033, 3730, 0),
					Location.create(3037, 3730, 0),

			}, { // Level 35 wilderness.
					Location.create(3104, 3796, 0), Location.create(3108, 3796, 0), Location.create(3108, 3792, 0),
					Location.create(3104, 3792, 0), },
			{ // Level 44 wilderness.
					Location.create(2978, 3868, 0), Location.create(2982, 3868, 0), Location.create(2982, 3864, 0),
					Location.create(2978, 3864, 0), },
			{ // Level 50 wilderness.
					Location.create(3305, 3918, 0), Location.create(3309, 3918, 0), Location.create(3305, 3914, 0),
					Location.create(3309, 3914, 0), },

	};

	private static final Location[][][] OBELISK_POSITIONS = { { // Level 13 wilderness.
			{ Location.create(3155, 3621, 0), Location.create(3156, 3621, 0), Location.create(3157, 3621, 0), },
			{ Location.create(3155, 3620, 0), Location.create(3156, 3620, 0), Location.create(3157, 3620, 0), },
			{ Location.create(3155, 3619, 0), Location.create(3156, 3619, 0), Location.create(3157, 3619, 0), },

			}, { // Level 19 wilderness.
					{ Location.create(3226, 3668, 0), Location.create(3227, 3668, 0), Location.create(3228, 3668, 0), },
					{ Location.create(3226, 3667, 0), Location.create(3227, 3667, 0), Location.create(3228, 3667, 0), },
					{ Location.create(3226, 3666, 0), Location.create(3227, 3666, 0), Location.create(3228, 3666, 0), },

			}, { // Level 27 wilderness.
					{ Location.create(3034, 3733, 0), Location.create(3035, 3733, 0), Location.create(3036, 3733, 0), },
					{ Location.create(3034, 3732, 0), Location.create(3035, 3732, 0), Location.create(3036, 3732, 0), },
					{ Location.create(3034, 3731, 0), Location.create(3035, 3731, 0), Location.create(3036, 3731, 0), },

			}, { // Level 35 wilderness.
					{ Location.create(3105, 3795, 0), Location.create(3106, 3795, 0), Location.create(3107, 3795, 0), },
					{ Location.create(3105, 3794, 0), Location.create(3106, 3794, 0), Location.create(3107, 3794, 0), },
					{ Location.create(3105, 3793, 0), Location.create(3106, 3793, 0), Location.create(3107, 3793, 0), },

			}, { // Level 44 wilderness.
					{ Location.create(2979, 3867, 0), Location.create(2980, 3867, 0), Location.create(2981, 3867, 0), },
					{ Location.create(2979, 3866, 0), Location.create(2980, 3866, 0), Location.create(2981, 3866, 0), },
					{ Location.create(2979, 3865, 0), Location.create(2980, 3865, 0),
							Location.create(2981, 3865, 0), }, },
			{ // Level 50 wilderness.
					{ Location.create(3306, 3917, 0), Location.create(3307, 3917, 0), Location.create(3308, 3917, 0), },
					{ Location.create(3306, 3916, 0), Location.create(3307, 3916, 0), Location.create(3308, 3916, 0), },
					{ Location.create(3306, 3915, 0), Location.create(3307, 3915, 0),
							Location.create(3308, 3915, 0), }, }, };

	private static boolean[] obeliskActivated = { false, false, false, false, false, false, };

	public static boolean handle(Player player, final int objectId, Location loc) {
		/*
		 * The index of the obelisk we clicked, -1 if none.
		 */
		int index = -1;
		for (int i = 0; i < OBELISKS.length; i++) { // All the obelisks.
			Location[] set = OBELISKS[i]; // Set of the four pillars per obelisk area.
			for (Location l : set) {
				if (l.equals(loc)) { // If we clicked at this location, we have the obelisk area.
					index = i; // Save the obelisk area.
					break;
				}
			}
		}
		/*
		 * We clicked an obelisk pillar.
		 */
		if (index != -1) {
			if (obeliskActivated[index]) {
				player.getActionSender().sendMessage("The obelisk is already activated!");
				return true;
			}

			obeliskActivated[index] = true;
			player.getActionSender().sendMessage("You activate the obelisk.");

			final Region region = World.getWorld().getRegionManager().getRegionByLocation(loc);

			for (Location position : OBELISKS[index])
				World.getWorld().register(new GameObject(position, 14825, 10, 0, false));

			int random = index;
			while (random == index)
				random = r.nextInt(OBELISKS.length);

			final Location[][] startingPositions = OBELISK_POSITIONS[index];
			final Location[][] endingPositions = OBELISK_POSITIONS[random];
			final int fIndex = index;
			World.getWorld().submit(new Event(5000) {

				@Override
				public void execute() {
					for (final Player p : region.getPlayers()) {
						for (Location position : OBELISKS[fIndex])
							World.getWorld().register(new GameObject(position, objectId, 10, 0, false));

						for (int posIndex1 = 0; posIndex1 < startingPositions.length; posIndex1++) {
							for (int posIndex2 = 0; posIndex2 < startingPositions[posIndex1].length; posIndex2++) {
								/*
								 * We have to make those final in order to be able to use them in events.
								 */
								final int fPosindex1 = posIndex1;
								final int fPosindex2 = posIndex2;
								/*
								 * We delay the graphic displayed slightly.
								 */
								World.getWorld().submit(new Event(400) {

									@Override
									public void execute() {
										/*
										 * I don't know if this is correct, its a decent fix though. Sends the TeleOther
										 * graphics on all the starting positions.
										 */
										p.getActionSender().sendStillGFX(342, 0,
												startingPositions[fPosindex1][fPosindex2]);
										this.stop();
									}
								});

								/*
								 * If there's a player standing on the obelisk take-off,
								 */
								if (startingPositions[posIndex1][posIndex2].equals(p.getLocation())) {
									if (p.getDatabaseEntity().getPlayerSettings().isTeleBlocked()) {
										p.getActionSender().sendMessage("A magical force stops you from teleporting.");
										return;
									}
									
									/*
									 * We play the teleother animation..
									 */
									p.playAnimation(Animation.create(1816)); // Teleother xD
									/*
									 * Makes sure the GFX and animation displaying is done, then we teleport.
									 */
									World.getWorld().submit(new Event(2000) {

										@Override
										public void execute() {
											p.setTeleportTarget(endingPositions[fPosindex1][fPosindex2]);
											p.playAnimation(Animation.create(-1)); // Resets.
											this.stop();
										}
									});
								}
							}
						}
						this.stop();
					}
					obeliskActivated[fIndex] = false;
					this.stop();
				}
			});
			return true;
			/*
			 * We clicked another object.
			 */
		} else
			return false;
	}
}