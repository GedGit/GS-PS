package org.rs2server.rs2.content.misc;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.Projectile;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

/**
 * Handles the Dragonfire shield
 * 
 * @author Canownueasy <tgpn1996@hotmail.com>
 */

public class DragonfireShield {

	public static void empty(final Player player) {
		if (player == null) {
			return;
		}
		if (player.getSkills().getLevel(Skills.HITPOINTS) < 1) {
			return;
		}
		if (player.dfsCharges < 1) {
			player.getActionSender().sendMessage("Your dragonfire shield has no charges to empty.");
			return;
		}
		player.playAnimation(Animation.create(6700));
		player.dfsCharges = 0;
		World.getWorld().submit(new Tickable(1) {
			@Override
			public void execute() {
				player.playGraphics(Graphic.create(1168));
				this.stop();
				player.getActionSender().sendMessage("You empty the dragonfire shield's charges.");
			}
		});
	}

	public static void charge(final Player player) {
		if (player == null)
			return;
		if (player.getSkills().getLevel(Skills.HITPOINTS) < 1)
			return;
		player.dfsCharges++;
	}

	public static void dfsSpec(final Player player, final Mob victim) {
		if (player == null || victim == null) {
			return;
		}
		if (player.getSkills().getLevel(Skills.HITPOINTS) < 1 || victim.getSkills().getLevel(Skills.HITPOINTS) < 1) {
			return;
		}
		if (!(player.getInteractionMode() == InteractionMode.ATTACK)) {
			player.getActionSender().sendMessage("You must be in combat to operate your dragonfire shield.");
			return;
		}
		if (victim instanceof Player) {
			if (!BoundaryManager.isWithinBoundaryNoZ(victim.getLocation(), "PvP Zone")) {
				return;
			}
		}
		if (player.dfsCharges < 1) {
			player.getActionSender().sendMessage("Your dragonfire shield is uncharged!");
			return;
		}
		if (player.dfsWait > 0) {
			player.getActionSender()
					.sendMessage("Let your dragonfire shield cool down for " + player.dfsWait + " more seconds.");
			return;
		}
		player.dfsCharges--;
		player.dfsWait = 240;
		player.getCombatState().setAttackDelay(6);
		player.getCombatState().setSpellDelay(6);
		player.face(victim.getLocation());
		player.playAnimation(Animation.create(6696));
		player.playGraphics(Graphic.create(1165));
		World.getWorld().submit(new Tickable(3) {
			@Override
			public void execute() {
				int hitDelay;
				int clientSpeed;
				int gfxDelay;
				if (player.getLocation().isWithinDistance(player, victim, 1)) {
					clientSpeed = 70;
					gfxDelay = 80;
				} else if (player.getLocation().isWithinDistance(player, victim, 5)) {
					clientSpeed = 90;
					gfxDelay = 100;
				} else if (player.getLocation().isWithinDistance(player, victim, 8)) {
					clientSpeed = 110;
					gfxDelay = 120;
				} else {
					clientSpeed = 130;
					gfxDelay = 140;
				}
				hitDelay = (gfxDelay / 20) - 1;

				player.playProjectile(Projectile.create(player.getCentreLocation(), victim.getCentreLocation(), 1166,
						45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));

				World.getWorld().submit(new Tickable(hitDelay) {
					@Override
					public void execute() {
						victim.playGraphics(Graphic.create(1167, 0, 100));
						World.getWorld().submit(new Tickable(1) {
							@Override
							public void execute() {
								int hit = (int) (Math.ceil(Math.random() * 37));
								victim.inflictDamage(new Hit(hit), player);
								this.stop();
							}
						});
						this.stop();
					}
				});
				this.stop();
			}
		});
	}
}