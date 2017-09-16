package org.rs2server.rs2.model.npc.impl.zulrah;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.map.path.SizedPathFinder;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.Arrays;

/**
 *
 * @author Nine
 * @author twelve
 */
public final class ZulrahAttack {

	public enum ZulrahAttackType {
		RANGE,
		MAGE,
		JAD_RANGE,
		JAD_MAGE,
		SPAWN_MINIONS,
		SPAWN_VENOM,
		MELEE
	}

	private final ZulrahAttackType attackType;
	private final int nextDelay;

	private Location[] locations;

	public ZulrahAttack(ZulrahAttackType attackType, int nextDelay) {
		this.attackType = attackType;
		this.nextDelay = nextDelay;
	}

	public void perform(Zulrah zulrah, Player challenger) {
		switch(attackType) {
			case SPAWN_VENOM:
				zulrah.playAnimation(ZulrahBehavior.ZULRAH_SHOOT_ANIMATION);//pewpew
				zulrah.face(Location.create((locations[0].getX() + locations[1].getX()) / 2, (locations[0].getY() + locations[1].getY()) / 2));
				//Arrays.stream(locations).forEach(l -> zulrah.playProjectile(Projectile.create(zulrah.getCentreLocation(), l.transform(1, 1, zulrah.getZ()), 1045, 30, 50, 100, 60, 25, 0, 10, 48)));
                Arrays.stream(locations).forEach(l -> challenger.getActionSender().sendProjectile(zulrah.getCentreLocation(), l.transform(1, 1, zulrah.getPlane()), 1045, 30, 50, 100, 60, 25, 10, 48, 0));
				World.getWorld().submit(new Tickable(3) {
					@Override
					public void execute() {
						Arrays.stream(locations).map(l -> {
							GameObject venomCloud = new GameObject(l.transform(0, 0, zulrah.getPlane()), ZulrahBehavior.VENOM_CLOUD, 10, 0, true);
							zulrah.getActiveVenomClouds().add(venomCloud);
                            venomCloud.setLocation(venomCloud.getSpawnLocation() != null ? venomCloud.getSpawnLocation()
                                    : venomCloud.getLocation());
                            challenger.getActionSender().sendObject(venomCloud);
							return venomCloud;
						}).forEach(c -> World.getWorld().submit(new Tickable(32) {
							@Override
							public void execute() {
								this.stop();
                                challenger.getActionSender().removeObject(c);
								zulrah.getActiveVenomClouds().remove(c);
							}
						}));
						stop();
					}
				});
				break;
			case MELEE:
				Player target = zulrah.getChallenger();

				zulrah.face(target.getLocation());
				((ZulrahMeleeState) zulrah.getState()).setTarget(target.getLocation());
				break;
			case RANGE:
			case JAD_RANGE:
				zulrah.face(challenger.getCentreLocation());
				zulrah.playAnimation(ZulrahBehavior.ZULRAH_SHOOT_ANIMATION);
				//zulrah.playProjectile(Projectile.create(zulrah.getCentreLocation(), challenger.getLocation(), 1044, 30, 50, 100, 60, 25, challenger.getProjectileLockonIndex(), 10, 48));
                challenger.getActionSender().sendProjectile(zulrah.getCentreLocation(), challenger.getLocation(), 1044, 30, 50, 100, 60, 25, 10, 48, challenger.getProjectileLockonIndex());
                World.getWorld().submit(new Tickable(getHitDelay(zulrah, challenger)) {
					@Override
					public void execute() {
                        int dmg = challenger.getCombatState().getPrayer(Prayers.PROTECT_FROM_MISSILES) ? 0 : Misc.random(41);
                        Hit damage = new Hit(dmg);
						challenger.inflictDamage(damage, zulrah);
						stop();
					}
				});
				break;
			case MAGE:
			case JAD_MAGE:
				zulrah.face(challenger.getCentreLocation());
				zulrah.playAnimation(ZulrahBehavior.ZULRAH_SHOOT_ANIMATION);
				//zulrah.playProjectile(Projectile.create(zulrah.getCentreLocation(), challenger.getLocation(), 1046, 30, 50, 100, 60, 25, challenger.getProjectileLockonIndex(), 10, 48));
                challenger.getActionSender().sendProjectile(zulrah.getCentreLocation(), challenger.getLocation(), 1046, 30, 50, 100, 60, 25, 10, 48, challenger.getProjectileLockonIndex());
                World.getWorld().submit(new Tickable(getHitDelay(zulrah, challenger)) {
					@Override
					public void execute() {

                        int dmg = challenger.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC) ? 0 : Misc.random(41);
                        Hit damage = new Hit(dmg);
						challenger.inflictDamage(damage, zulrah);
						stop();
					}
				});
				break;
			case SPAWN_MINIONS:
				Location spawn = locations[0].transform(2, 2, zulrah.getPlane());

				zulrah.playAnimation(ZulrahBehavior.ZULRAH_SHOOT_ANIMATION);
				zulrah.resetFace();
				zulrah.face(spawn);
				//zulrah.playProjectile(Projectile.create(zulrah.getCentreLocation(), spawn, 1047, 30, 50, 100, 60, 25, 0, 10, 48));
                challenger.getActionSender().sendProjectile(zulrah.getCentreLocation(), spawn, 1047, 30, 50, 100, 60, 25, 10, 48, 0);
                World.getWorld().submit(new Tickable(3) {
					@Override
					public void execute() {
						stop();
						createMinion(zulrah, challenger, spawn);
					}
				});
				break;
		}
	}

	private int getHitDelay(Zulrah attacker, Player victim) {
		int gfxDelay;
		if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			gfxDelay = 80;
		} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
			gfxDelay = 100;
		} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
			gfxDelay = 120;
		} else {
			gfxDelay = 140;
		}
		return (gfxDelay / 25) - 1;
	}

	public void createMinion(Zulrah zulrah, Player challenger, Location spawn) {
		NPC npc = new NPC(2045, spawn, spawn, spawn, 0);
		World.getWorld().createNPC(npc);
		npc.instancedPlayer = challenger;
		challenger.getInstancedNPCs().add(npc);
		zulrah.getActiveMinions().add(npc);
		World.getWorld().doPath(new SizedPathFinder(false), npc, challenger.getX(), challenger.getY());
	}

	public final ZulrahAttack setLocations(Location[] locations) {
		this.locations = locations;
		return this;
	}

	public final int getAttackDelay() {
		return nextDelay;
	}

	public final ZulrahAttackType getAttackType() {
		return attackType;
	}
}
