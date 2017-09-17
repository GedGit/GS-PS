package org.rs2server.rs2.model.npc.impl.kraken;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.npc.impl.CombatNpc;
import org.rs2server.rs2.model.npc.impl.NpcCombatState;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.impl.StoppingTick;
import org.rs2server.rs2.util.Misc;

/**
 * @author Clank1337
 */
public class KrakenCombatState<T extends CombatNpc<?>> extends NpcCombatState<T> {

	private static final Animation ATTACK_ANIMATION = Animation.create(3991);

	public KrakenCombatState(T npc) {
		super(npc);
	}

	@Override
	public void perform() {
		if (npc.getTransformId() == 496) {
			System.out.println("transform: "+npc.getTransformId()+" real "+npc.getId());
			return;
		}
		magicAttack();
	}

	private void magicAttack() {
		Player player = (Player) npc.getInteractingEntity();
		if (player != null) {
			npc.playAnimation(ATTACK_ANIMATION);

			int clientSpeed;
			int gfxDelay;
			if(npc.getLocation().isWithinDistance(npc, player, 1)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if(npc.getLocation().isWithinDistance(npc, player, 5)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else if(npc.getLocation().isWithinDistance(npc, player, 8)) {
				clientSpeed = 110;
				gfxDelay = 120;
			} else {
				clientSpeed = 130;
				gfxDelay = 140;
			}
			player.getActionSender().sendProjectile(npc.getCentreLocation(), player.getLocation(), 156, 45, 50, clientSpeed, 43, 35, 10, 48, player.getProjectileLockonIndex());
			//npc.playProjectile(Projectile.create(npc.getCentreLocation(), player.getLocation(), 156, 45, 50, clientSpeed, 43, 35, player.getProjectileLockonIndex(), 10, 48));
			int delay = (gfxDelay / 20) - 1;
			final int damage = Misc.random(npc.getDefaultCombatAction().damage(28, npc, player, CombatState.AttackType.MAGIC, Skills.MAGIC, Prayers.PROTECT_FROM_MAGIC, false, false));
			World.getWorld().submit(new StoppingTick(delay) {

				@Override
				public void executeAndStop() {
					player.inflictDamage(new Hit(damage), npc);
					npc.getDefaultCombatAction().smite(npc, player, damage);
					npc.getDefaultCombatAction().recoil(npc, player, damage);
					player.getCombatState().setLastHitTimer(10000);
				}
			});

			npc.getDefaultCombatAction().vengeance(npc, player, damage, 1);

			player.getActiveCombatAction().defend(npc, player, false);


			npc.getCombatState().setAttackDelay(6);
		}
	}

	@Override
	public int getId() {
		return 492;
	}
}
