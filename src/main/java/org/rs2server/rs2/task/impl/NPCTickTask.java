package org.rs2server.rs2.task.impl;

import org.rs2server.rs2.content.Following;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.CombatNPCDefinition.GodWarsMinion;
import org.rs2server.rs2.model.Hit.HitPriority;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.boundary.Boundary;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.map.path.DefaultPathFinder;
import org.rs2server.rs2.model.map.path.SizedPathFinder;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.npc.Pet;
import org.rs2server.rs2.model.npc.impl.abyssSyre.AbyssalSire;
import org.rs2server.rs2.model.npc.impl.zulrah.Zulrah;
import org.rs2server.rs2.model.npc.pc.PestControlNpc;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.task.Task;
import java.util.*;

/**
 * A task which performs pre-update tasks for an NPC.
 * 
 * @author Graham Edgecombe
 *
 */
public class NPCTickTask implements Task {

	/**
	 * The npc who we are performing pre-update tasks for.
	 */
	private NPC npc;

	/**
	 * The random number generator.
	 */
	private static final Random random = new Random(); 

	/**
	 * Creates the tick task.
	 * 
	 * @param npc
	 *            The npc.
	 */
	public NPCTickTask(NPC npc) {
		this.npc = npc;
	}

	@Override
	public void execute() {
		/*
		 * If the map region changed set the last known region.
		 */
		if (npc.isMapRegionChanging()) {
			npc.setLastKnownRegion(npc.getLocation());
		}

		if (npc.getInteractingEntity() != null
				&& (npc.getInteractingEntity().isDestroyed() || npc.getInteractingEntity().getCombatState().isDead())) {
			npc.resetInteractingEntity();
		}

		if (npc.getInteractingEntity() != null && npc.hasAttribute("facing") && !(npc instanceof Zulrah)
				&& !(npc instanceof Pet)) {
			Mob mob = npc.getInteractingEntity();
			if (npc.getAttribute("facing") != mob.getLocation()) {
				mob.face(npc.getLocation());
				npc.face(mob.getLocation());
			}
		}

		if (npc.isCaveNPC() && npc.instancedPlayer != null) {
			if (npc.getInteractingEntity() != npc.instancedPlayer) {
				npc.resetInteractingEntity();
				Player player = npc.instancedPlayer;
				npc.getCombatState().startAttacking(player, player.isAutoRetaliating());
			}
		}
		if (npc.getId() == 319) {
			List<Mob> enemiesInArea = new ArrayList<>();
			if (npc.getRegion().getPlayers().size() > 0) {
				for (Player player : World.getWorld().getPlayers())
					enemiesInArea.add(player);
			}
			if (enemiesInArea.size() > 0 && npc.getInteractingEntity() == null) {
				int randomPlayer = random.nextInt(enemiesInArea.size());
				Mob p = enemiesInArea.get(randomPlayer);
				npc.getCombatState().startAttacking(p, false);
			}
		}
		if (npc.getId() == 1101) {
			if (npc.getInstancedPlayer() != null && npc.getInstancedPlayer() instanceof Player) {
				if (npc.getLocation().distance(npc.getInstancedPlayer().getLocation()) > 12) {
					npc.getInstancedPlayer().getActionSender()
							.sendMessage("The Giant Sea Snake goes back into the water...");
					npc.setInstancedPlayer(null);
					World.getWorld().unregister(npc);
				}
			}
		}
		Region[] regions = npc.getRegion().getSurroundingRegions();
		boolean active = false;
		for (Region region : regions) {
			if (region.playerSize() > 0) {
				active = true;
				break;
			}
		}
		if (!active) {
			return;
		}
		npc.tick();
		npc.processTicks();
		switch (npc.getId()) {
		case 1635:
		case 1636:
		case 1637:
		case 1638:
		case 1639:
		case 1640:
		case 1641:
		case 1642:
		case 1643:
		case 1644:
			if (npc.getRandom().nextInt(10) == 5) {
				npc.resetInteractingEntity();
			}
		case 2216:
		case 2217:
		case 2218:
		case 3163:
		case 3164:
		case 3165:
		case 2206:
		case 2207:
		case 2208:
		case 3130: // implings lmao
		case 3131:
		case 3132:
			if (npc.getRandom().nextInt(10) == 5) {
				npc.resetInteractingEntity();
			}
			break;
		}
		if (npc.getCombatDefinition() != null && npc.getCombatDefinition().isAggressive()
				&& !npc.getCombatState().isDead() && npc.getInteractingEntity() == null) {
			List<Mob> enemiesInArea = new ArrayList<>();
			if (npc.getRegion().getPlayers().size() > 0) {
				for (Player player : World.getWorld().getPlayers()) {
					boolean withinDistance = npc.getLocation().isWithinDistance(player.getLocation());
					if (!withinDistance || player.getLocation().getPlane() != npc.getLocation().getPlane())
						continue;
					boolean canContinue = true;
					if (npc.getCombatDefinition().getGodWarsTeam() != null) {
						int[] itemsToCheck = new int[0];
						switch (npc.getCombatDefinition().getGodWarsTeam()) {
						case ZAMORAK:
							itemsToCheck = Equipment.ZAMORAK_ITEMS;
							break;
						case SARADOMIN:
							itemsToCheck = Equipment.SARADOMIN_ITEMS;
							break;
						case BANDOS:
							itemsToCheck = Equipment.BANDOS_ITEMS;
							break;
						case ARMADYL:
							itemsToCheck = Equipment.ARMADYL_ITEMS;
							break;
						}
						for (Item item : player.getEquipment().toArray()) {
							if (item != null) {
								for (int i = 0; i < itemsToCheck.length; i++) {
									if (item.getId() == itemsToCheck[i]) {
										canContinue = false;
										break;
									}
								}
							}
						}
					}
					if (!canContinue)
						continue;
					int npcCombatLvl = 100 * 2;
					Boundary bounds = npc.getHomeArea();
					if (player != null && !player.getCombatState().isDead()) {
						if (bounds != null && BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), bounds)
								|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "MultiCombat_Bandos")
								|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "MultiCombat_Zammy")
								|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "MultiCombat_Armadyl")
								|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "MultiCombat_Sara")
								|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "RFD"))
							enemiesInArea.add(player);
						else if (bounds == null && npc.getActiveCombatAction().canHit(npc, player, true, false)
								&& player.getSkills().getCombatLevel() <= npcCombatLvl
								&& player.getLocation().isWithinDistance(npc, player, 5))
							enemiesInArea.add(player);
					}
				}
			}
			if (enemiesInArea.size() > 0) {
				int randomPlayer = random.nextInt(enemiesInArea.size());
				Mob p = enemiesInArea.get(randomPlayer);
				npc.getCombatState().startAttacking(p, false);
			} else if (npc.getCombatDefinition().getGodWarsTeam() != null) {
				if (npc.getRegion().getNpcs().size() > 0) {
					for (NPC enemy : npc.getRegion().getNpcs()) {
						if (enemy.getLocation().getPlane() != npc.getLocation().getPlane())
							continue;
						if (enemy.getCombatDefinition() == null)
							continue;
						boolean canContinue = true;
						if (npc.getCombatDefinition().getGodWarsTeam() != null) {
							switch (npc.getCombatDefinition().getGodWarsTeam()) {
							case ZAMORAK:
								if (enemy.getCombatDefinition().getGodWarsTeam() == GodWarsMinion.ZAMORAK) {
									canContinue = false;
								}
								break;
							case SARADOMIN:
								if (enemy.getCombatDefinition().getGodWarsTeam() == GodWarsMinion.SARADOMIN) {
									canContinue = false;
								}
								break;
							case BANDOS:
								if (enemy.getCombatDefinition().getGodWarsTeam() == GodWarsMinion.BANDOS) {
									canContinue = false;
								}
								break;
							case ARMADYL:
								if (enemy.getCombatDefinition().getGodWarsTeam() == GodWarsMinion.ARMADYL) {
									canContinue = false;
								}
								break;
							}
						}
						if (!canContinue)
							continue;
						if (enemy != null && !enemy.getCombatState().isDead()
								&& npc.getActiveCombatAction().canHit(npc, enemy, true, false)
								&& enemy.getLocation().isWithinDistance(npc, enemy, 4)
								&& enemy.getCombatDefinition().getGodWarsTeam() != null) {
							enemiesInArea.add(enemy);
						}
					}
				}
				if (enemiesInArea.size() > 0) {
					int randomPlayer = random.nextInt(enemiesInArea.size());
					Mob p = enemiesInArea.get(randomPlayer);
					npc.getCombatState().startAttacking(p, false);
				}
			}
		}

		if (npc.canMove()) {
			if (!npc.isInteracting() && !npc.getCombatState().isDead() && npc.getCombatState().canMove()
					&& npc.getWalkingQueue().isEmpty() && random.nextInt(7) == 1) {
				int distance = Math.abs(npc.getMaxLocation().getX() - npc.getMinLocation().getX());
				if (distance > 0) {
					int x = npc.getMinLocation().getX() + random.nextInt(distance);
					distance = Math.abs(npc.getMaxLocation().getY() - npc.getMinLocation().getY());
					int y = npc.getMinLocation().getY() + random.nextInt(distance);

					if (x >= npc.getMinLocation().getX() && x <= npc.getMaxLocation().getX()
							&& y >= npc.getMinLocation().getY() && y <= npc.getMaxLocation().getY()) {
						World.getWorld().doPath(new SizedPathFinder(true), npc, x, y);
					}
				}
			}
			if (npc.getInteractingEntity() != null && npc.getInteractionMode() == InteractionMode.ATTACK) {
				if (npc.getCombatState().canMove()) {
					Boundary bounds = npc.getHomeArea();
					boolean caveNPC = (npc.getCombatDefinition() != null
							&& npc.getCombatDefinition().isFightCavesNPC());
					if (bounds != null) {
						if (!caveNPC && !BoundaryManager.isWithinBoundaryNoZ(npc.getInteractingEntity().getLocation(),
								bounds)) {
							npc.resetInteractingEntity();
						}
					} else if (!caveNPC && npc.getLocation().distanceToEntity(npc, npc.getInteractingEntity()) > 10) {// ||
																														// npc.getLocation().distance(npc.getHomeArea().getCenterPoint())
																														// >
																														// 10)
																														// {
						npc.resetInteractingEntity();
					}
					if (npc.getInteractingEntity() != null) {
						int distance = npc.getLocation().distanceToEntity(npc, npc.getInteractingEntity());
						int requiredDistance = npc.getActiveCombatAction().distance(npc.getInteractingEntity());
						while (true) {
							if (requiredDistance != 1) {
								if (npc.getAttribute("positionMove") == null)
									npc.getWalkingQueue().reset();
								else
									npc.removeAttribute("positionMove");
							}
							if (distance > requiredDistance) {
								if (npc instanceof PestControlNpc || caveNPC || npc instanceof AbyssalSire
										|| npc.getLocation().distance(npc.getSpawnLocation()) < 15
										|| npc.getCombatDefinition().getGodWarsTeam() != null) {
									Following.combatFollow(npc, npc.getInteractingEntity());
								} else if (npc.getLocation().distance(npc.getSpawnLocation()) >= 15
										&& npc.getLocation().distance(npc.getInteractingEntity().getLocation()) > 5) {
									npc.resetInteractingEntity();
									World.getWorld().doPath(new DefaultPathFinder(), npc, npc.getSpawnLocation().getX(),
											npc.getSpawnLocation().getY());
								}
							}
							break;
						}
					}

				}
			}
		}

		if (npc.getCombatState().getAttackDelay() > 0) {
			npc.getCombatState().decreaseAttackDelay(1);
		}
		if (npc.getCombatState().getSpellDelay() > 0) {
			npc.getCombatState().decreaseSpellDelay(1);
		}

		/*
		 * Gets the next two hits from the queue.
		 */
		List<Hit> hits = npc.getHitQueue();
		Hit first = null;
		if (hits.size() > 0) {
			for (int i = 0; i < hits.size(); i++) {
				Hit hit = hits.get(i);
				if (hit.getDelay() < 1) {
					first = hit;
					hits.remove(hit);
					break;
				}
			}
		}
		if (first != null) {
			npc.setPrimaryHit(first);
			npc.getUpdateFlags().flag(UpdateFlag.HIT);
		}
		Hit second = null;
		if (hits.size() > 0) {
			for (int i = 0; i < hits.size(); i++) {
				Hit hit = hits.get(i);
				if (hit.getDelay() < 1) {
					second = hit;
					hits.remove(hit);
					break;
				}
			}
		}
		if (second != null) {
			npc.setSecondaryHit(second);
			npc.getUpdateFlags().flag(UpdateFlag.HIT_2);
		}
		if (hits.size() > 0) {// tells us we still have more hits
			Iterator<Hit> hitIt = hits.iterator();
			while (hitIt.hasNext()) {
				Hit hit = hitIt.next();
				if (hit.getDelay() > 0) {
					hit.setDelay(hit.getDelay() - 1);
				}
				if (hit.getHitPriority() == HitPriority.LOW_PRIORITY) {
					hitIt.remove();
				}
			}
		}

		/*
		 * Process the next movement in the NPC's walking queue.
		 */
		npc.getWalkingQueue().processNextMovement();
	}

}
