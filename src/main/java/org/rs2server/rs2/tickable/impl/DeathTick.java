package org.rs2server.rs2.tickable.impl;

import org.rs2server.Server;
import org.rs2server.cache.format.CacheNPCDefinition;
import org.rs2server.rs2.content.api.GameMobDeathEvent;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailDoubleAgentStep;
import org.rs2server.rs2.domain.service.api.DeadmanService;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.PermissionService;
import org.rs2server.rs2.domain.service.api.PlayerStatisticsService;
import org.rs2server.rs2.domain.service.api.SlayerService;
import org.rs2server.rs2.domain.service.api.content.PestControlService;
import org.rs2server.rs2.model.CombatNPCDefinition;
import org.rs2server.rs2.model.Entity;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.UpdateFlags;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.WorldType;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.minigame.barrows.Barrows;
import org.rs2server.rs2.model.minigame.barrows.Barrows.BarrowsBrother;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.npc.impl.cerberus.Cerberus;
import org.rs2server.rs2.model.npc.impl.kraken.Kraken;
import org.rs2server.rs2.model.npc.impl.kraken.Whirlpool;
import org.rs2server.rs2.model.npc.impl.other.SuperiorSlayerEncounter;
import org.rs2server.rs2.model.npc.impl.zulrah.Zulrah;
import org.rs2server.rs2.model.npc.pc.PestControlNpc;
import org.rs2server.rs2.model.npc.pc.Splatter;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.mysql.impl.NewsManager;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * The death tickable handles player and npc deaths. Drops loot, does animation,
 * teleportation, etc.
 * 
 * @author Graham
 * @author Scu11
 */
public class DeathTick extends Tickable {

	private static NPC[][] godNPCS = new NPC[4][4];

	private final PlayerStatisticsService statisticsService = Server.getInjector()
			.getInstance(PlayerStatisticsService.class);
	private final DeadmanService deadmanService = Server.getInjector().getInstance(DeadmanService.class);
	private final SlayerService slayerService = Server.getInjector().getInstance(SlayerService.class);
	private final HookService hookService = Server.getInjector().getInstance(HookService.class);
	private final PermissionService permissionService;
	private final PestControlService pestControlService;

	/**
	 * The mob who has just died.
	 */
	private Mob mob;

	/**
	 * Creates the death event for the specified entity.
	 * 
	 * @param mob
	 *            The mob whose death has just happened.
	 */
	public DeathTick(Mob mob, int ticks) {
		super(ticks);
		this.mob = mob;
		this.permissionService = Server.getInjector().getInstance(PermissionService.class);
		this.pestControlService = Server.getInjector().getInstance(PestControlService.class);
	}

	@SuppressWarnings("unused")
	@Override
	public void execute() {
		this.stop();
		if (mob.getAttribute("is_portal") == null && mob.getCombatState().isDead()) {
			/*
			 * If set to true, the minigame handles items such as teleporting.
			 */
			boolean minigameDeathHook = false;

			/*
			 * The killer of this mob.
			 */
			Mob killer = (mob.getCombatState().getDamageMap().highestDamage() != null && !mob.isDestroyed())
					? mob.getCombatState().getDamageMap().highestDamage()
					: mob;
			hookService.post(new GameMobDeathEvent(mob, killer));

			if (mob.isPlayer()) {
				final Player player = (Player) mob;
				if (!killer.isPlayer() || killer == mob) {
					int highestDealt = 0;
					if (killer.isNPC())
						killer = mob;
				}
				Player pKiller = (Player) killer;
				if (pKiller != null && pKiller != mob)
					pKiller.getActionSender().sendMessage("You killed " + player.getName() + ".");
				if (player.getCombatState().getPrayer(Prayers.PROTECT_ITEM))
					player.setAttribute("protectItem", Boolean.TRUE);
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);

				mob.getCombatState().resetBonuses();
				player.getContentManager().onDeath();
			}
			/*
			 * Drops the loot and performs mini-game kill hooks.
			 */
			boolean drop = true;

			if (killer.isPlayer()) {
				Player k = (Player) killer;
				if (mob.isNPC()) {

					if (k.getWarriorsGuild().getCurrentArmour() == mob)
						k.getWarriorsGuild().setCurrentArmour(null);

					mob.dropLoot(killer);
				} else {
					if (permissionService.isNot(k, PermissionService.PlayerPermissions.DEV)) {
						mob.dropLoot(k);
						if (World.getWorld().getType() == WorldType.DEADMAN_MODE)
							deadmanService.onPlayerKill((Player) killer, (Player) mob);
					}
				}
			} else {
				if (mob.isPlayer()) {
					Player player = (Player) mob;
					if (player.getRFD().isStarted() || player.getFightCave().isStarted())
						drop = false;
				}
				if (drop)
					mob.dropLoot(mob);
			}

			/*
			 * The location to teleport to.
			 */
			Location teleportTo = Mob.DEFAULT_LOCATION;

			/*
			 * Resets the opponents tag timer. Player only as NPC's reset their killer as
			 * soon as they die.
			 */
			if (!mob.isNPC() && mob.getCombatState().getLastHitBy() != null
					&& mob.getCombatState().getLastHitBy().getCombatState()
							.getLastHitTimer() > (System.currentTimeMillis() + 4000)
					&& mob.getCombatState().getLastHitBy().getCombatState().getLastHitBy() == mob) {
				mob.getCombatState().getLastHitBy().getCombatState().setLastHitBy(null);
				mob.getCombatState().getLastHitBy().getCombatState().setLastHitTimer(0);
			}

			/*
			 * Performs checks for players/npcs.
			 */
			if (mob.isPlayer()) {
				final Player player = (Player) mob;
				player.getActionSender().updateSpecialConfig();
				player.getActionSender().sendMessage("Oh dear, you are dead!");// player.getSkills().getTotalLevel()
				player.getActionSender().sendBonuses();
				player.getActionSender().updateRunningConfig();

				if (permissionService.is(player, PermissionService.PlayerPermissions.HARDCORE_IRON_MAN)) {

					if (!player.getRFD().isStarted() && !player.getFightCave().isStarted()
							&& !BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControl")) {

						permissionService.remove(player, PermissionService.PlayerPermissions.HARDCORE_IRON_MAN);
						permissionService.give(player, PermissionService.PlayerPermissions.IRON_MAN);
						player.getActionSender().sendConfig(499, 1621114888);

						if (player.getBank().contains(20792))
							player.getBank().replace(20792, 12810);
						if (player.getBank().contains(20794))
							player.getBank().replace(20794, 12811);
						if (player.getBank().contains(20796))
							player.getBank().replace(20796, 12812);

						if (player.getSkills().getTotalLevel() >= 500) {
							String killerName = "to an unknown power";

							if (killer != null && killer.isNPC())
								killerName = "fighting against: "
										+ CacheNPCDefinition.get(((NPC) killer).getId()).getName();

							if (killer != null && killer.isPlayer())
								killerName = "in a PVP battle with " + ((Player) killer).getName();

							World.getWorld()
									.sendWorldMessage("<img=10><col=FF0000><shad=000000>" + player.getName()
											+ " just died in Hardcore Ironman mode with a skill total of "
											+ Misc.formatNumber(player.getSkills().getTotalLevel()) + ".");

							new Thread(new NewsManager(player, "<img src='../resources/news/hc_ironman.png' width=13> "
									+ "died on hardcore ironman; total level: "
									+ Misc.formatNumber(player.getSkills().getTotalLevel()) + " " + killerName + "."))
											.start();
						}
					}
				}

				player.resetInteractingEntity();
				player.getCombatState().getDamageMap().reset();
				player.getCombatState().resetPrayers();
				player.getSkills().resetStats();

				if (player.getPoisonDrainTick() != null)
					player.getPoisonDrainTick().stop();
				player.removeVenom();
				player.removeAttribute("venom");
				player.venomDamage = 6;
				player.getActionQueue().clearAllActions();
				player.getDatabaseEntity().getCombatEntity().setVenomDamage(0);
				player.getDatabaseEntity().getPlayerSettings().setTeleBlockTimer(0);
				player.getDatabaseEntity().getPlayerSettings().setTeleBlocked(false);
				player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
				player.getActionSender().removeWalkableInterface();
				player.getCombatState().setPoisonDamage(0, null);
				player.setTeleportTarget(Entity.DEFAULT_LOCATION);

				if (BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControl")) {
					pestControlService.handleDeath(player);
					return;
				}
				if (player.getRFD().isStarted())
					player.getRFD().stop(true);
				if (player.getFightCave().isStarted()) {
					player.getFightCave().stop();
					player.getFightCave().setStarted(false);
				}
			} else if (mob.isNPC()) {
				final NPC npc = (NPC) mob;
				if (killer.isPlayer()) {
					final Player player = (Player) killer;
					if (player.getSlayer().getSlayerTask() != null) {
						String task = player.getSlayer().getSlayerTask().getName().toLowerCase();
						String npcName = npc.getDefinition().getName().toLowerCase();

						if (task.equalsIgnoreCase(npcName) || task.contains(npcName) || task.endsWith(npcName)
								|| task.startsWith(npcName) || task.regionMatches(0, npcName, 0, 4)
								|| task.regionMatches(5, npcName, 5, 4))
							slayerService.onTaskKill((Player) killer, npc);
					}
				}
				if (killer.isPlayer()) {
					Player player = (Player) killer;
					if (player.getInstancedNPCs() != null && player.getInstancedNPCs().contains(npc)
							&& !(npc instanceof Zulrah)) {
						player.getInstancedNPCs().remove(npc);
						npc.instancedPlayer = null;
						if (World.getWorld().getNPCs().contains(npc))
							World.getWorld().getNPCs().remove(npc);
					}
				}
				if (npc != null && killer.isPlayer() && (npc.getId() == 2465 || npc.getId() == 2466)) {
					Player player = (Player) killer;
					player.getWarriorsGuild().onCyclopsKill(npc.getLocation());
				}
				NPC.Bosses bosses = NPC.Bosses.of(npc.getId());
				if (bosses != null && killer.isPlayer()) {
					Player player = (Player) killer;
					statisticsService.increaseBossKillCount(player, npc.getId(), 1);

					int amount = player.getDatabaseEntity().getStatistics().getBossKillCount().get(npc.getId());

					if (amount % 500 == 0) {

						World.getWorld()
								.sendWorldMessage("<col=ff0000><img=23>Server</col>: " + player.getName()
										+ " has just killed his " + Misc.formatNumber(amount) + "th "
										+ npc.getDefinedName() + ".");

						new Thread(
								new NewsManager(player,
										"<img src='../resources/news/consecutive.png' width=13> " + "killed his "
												+ Misc.formatNumber(amount) + "th " + npc.getDefinedName() + "."))
														.start();

					} else
						killer.getActionSender().sendMessage("Your " + npc.getDefinedName()
								+ " kill count is: <col=c4260e>" + Misc.formatNumber(amount) + "</col>.");
				}
				if (CombatNPCDefinition.GWD_PAIRS.containsKey(npc.getId())) {
					teleportTo = Location.create(1, 1, 0);
					final int index = CombatNPCDefinition.GWD_PAIRS.get(npc.getId()).ordinal();
					boolean spawn = false;
					for (int idx = 0; idx < 4; idx++) {
						if (godNPCS[index][idx] == null) {
							godNPCS[index][idx] = npc;
							if (idx == 3)
								spawn = true;
							break;
						}
					}
					if (spawn) {
						World.getWorld().submit(new Tickable(50) {
							public void execute() {
								for (int i = 0; i < 4; i++) {
									final NPC godnpc = godNPCS[index][i];
									if (godnpc != null) {
										godnpc.resetInteractingEntity();
										godnpc.setCombatDefinition(CombatNPCDefinition.of(godnpc.getId()));
										godnpc.setTeleportTarget(godnpc.getSpawnLocation());
										godnpc.setLocation(godnpc.getSpawnLocation());
										godnpc.setDirection(godnpc.getSpawnDirection());
									}
									godNPCS[index][i] = null;
								}
								this.stop();
							}
						});
					}
				} else if (npc instanceof Whirlpool) {
					Whirlpool whirlpool = (Whirlpool) npc;
					if (killer.isPlayer()) {
						Player player = (Player) killer;
						whirlpool.getKraken().getDisturbedWhirlpools().remove(whirlpool);
						whirlpool.getKraken().getWhirlPools().remove(whirlpool);
						player.getInstancedNPCs().remove(whirlpool);
						World.getWorld().unregister(whirlpool);
					}
				} else if (npc instanceof Cerberus) {
					Cerberus cerberus = (Cerberus) npc;
					cerberus.destroySelf();
					teleportTo = null;
				} else if (npc instanceof Kraken) {
					Kraken kraken = (Kraken) npc;
					kraken.destroySelf();
				} else if (npc instanceof Zulrah) {
					Zulrah zulrah = (Zulrah) npc;
					zulrah.destroySelf();
				} else if (npc instanceof TreasureTrailDoubleAgentStep.DoubleAgent) {
					World.getWorld().unregister(npc);
				} else if (npc instanceof SuperiorSlayerEncounter) {
					World.getWorld().unregister(npc);
					teleportTo = null;
				} else if (npc instanceof Splatter) {
					Splatter splatter = (Splatter) npc;
					splatter.handleDeath();
					splatter.getPortal().getNpcs().remove(splatter);
					return;
				} else if (npc instanceof PestControlNpc) {
					PestControlNpc pcNpc = (PestControlNpc) npc;
					if (pcNpc.getPortal() != null)
						pcNpc.getPortal().getNpcs().remove(pcNpc);
					World.getWorld().unregister(pcNpc);
					return;
				} else if (Barrows.BarrowsBrother.forId(npc.getId()) != null && killer.isPlayer()) {

					Player player = (Player) killer;
					player.increaseBarrowsKillcount();
					player.getActionSender().sendString(24, 9, "Kill Count: " + player.getBarrowsKillCount());
					player.removeAttribute("currentlyFightingBrother");
					if (Barrows.BarrowsBrother
							.forId(npc.getId()) == (BarrowsBrother) killer.getAttribute("barrows_tunnel"))
						player.setAttribute("canLoot", true);
					player.getKilledBrothers().put(npc.getId(), true);
					teleportTo = null;
					World.getWorld().unregister(npc);
				} else if (npc.getCombatDefinition() != null && npc.getCombatDefinition().getRespawnTicks() > 0) {
					teleportTo = Location.create(1, 1, 0);
					World.getWorld().submit(new Tickable(npc.getCombatDefinition().getRespawnTicks()) {
						public void execute() {
							npc.setTeleportTarget(npc.getSpawnLocation());
							npc.setLocation(npc.getSpawnLocation());
							npc.setDirection(npc.getSpawnDirection());
							this.stop();
						}
					});
				} else {
					teleportTo = null;
					World.getWorld().unregister(npc);
				}
			}

			/*
			 * Teleports the mob if the minigame hasn't handled it.
			 */
			if (teleportTo != null && !(mob instanceof Zulrah) && !(mob instanceof Kraken)
					&& !(mob instanceof Whirlpool) && !(mob instanceof PestControlNpc)
					&& !(mob instanceof SuperiorSlayerEncounter) && !(mob instanceof Cerberus)
					&& !(mob instanceof TreasureTrailDoubleAgentStep.DoubleAgent)) {
				mob.setTeleportTarget(teleportTo);
				if (!mob.isNPC())
					mob.getCombatState().resetBonuses();
				if (mob.getEquipment().get(Equipment.SLOT_WEAPON) != null)
					mob.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				else
					mob.setDefaultAnimations();
			}

			/*
			 * Resets our tag timer.
			 */
			mob.getCombatState().setLastHitBy(null);
			mob.getCombatState().setLastHitTimer(0);

			/*
			 * Resets various attributes.
			 */
			mob.getCombatState().setDead(false);
			mob.resetVariousInformation();

			/*
			 * Update the Walking queue to update location
			 */
			mob.getWalkingQueue().reset();
			mob.getWalkingQueue().processNextMovement();
		}
	}
}