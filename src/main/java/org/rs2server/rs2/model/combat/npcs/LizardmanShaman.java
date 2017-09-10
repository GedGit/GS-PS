package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.Server;
import org.rs2server.rs2.content.Following;
import org.rs2server.rs2.domain.service.api.PathfindingService;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.map.path.SizedPathFinder;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.tickable.impl.StoppingTick;
import org.rs2server.rs2.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * @author Script <Rune-Server> CamelCrusher
 */
public class LizardmanShaman extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final LizardmanShaman INSTANCE = new LizardmanShaman();

	/**
	 * Gets the singleton instance.
	 * 
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	/**
	 * Default private constructor.
	 */
	public LizardmanShaman() {
		Server.getInjector().getInstance(PathfindingService.class);
	}

	private int hitDelay;
	private boolean blockAnimation;
	private int maxHit;
	private int damage;
	private int randomHit;
	private int gfxDelay;
	private int clientSpeed;
	private int hit;

	private static final Animation JUMP_HIDE = Animation.create(7152);
	private static final Animation JUMP_DOWN = Animation.create(6946);

	private static final int SPAWN_ID = 6768;

	List<NPC> minions = new ArrayList<>();

	private enum CombatStyle {
		MELEE,

		MAGIC,

		ACID_ATTACK,

		JUMP_ATTACK,

		RANGE
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if (!attacker.isNPC()) {
			return; // this should be an NPC!
		}

		NPC npc = (NPC) attacker;
		CombatStyle style = CombatStyle.MELEE;
		if (npc.getLocation().distance(victim.getLocation()) <= 3) {
			switch (random.nextInt(10)) {
			case 1:
			case 2:
			case 3:
				style = CombatStyle.RANGE;
				break;
			case 4:
				style = CombatStyle.MAGIC;
				break;
			case 6:
				style = CombatStyle.ACID_ATTACK;
				break;
			case 7:
				style = CombatStyle.JUMP_ATTACK;
				break;
			}
		} else {
			switch (random.nextInt(10)) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 7:
				style = CombatStyle.RANGE;
				break;
			case 5:
			case 10:
				style = CombatStyle.JUMP_ATTACK;
				break;
			case 6:
				style = CombatStyle.MAGIC;
				break;
			case 8:
			case 9:
				style = CombatStyle.ACID_ATTACK;
				break;
			}
		}

		switch (style) {
		case MELEE:
			handleLizardMelee(npc, victim);
			break;
		case RANGE:
			handleLizardRange(npc, victim);
			break;
		case JUMP_ATTACK:
			handleLizardJumpAttack(npc, victim);
			break;
		case MAGIC:
			handleLizardMagicAttack(npc, victim);
			break;
		case ACID_ATTACK:
			handleLizardAcidAttack(npc, victim);
			break;
		}
	}

	private void handleLizardAcidAttack(NPC npc, Mob victim) {
		npc.playAnimation(Animation.create(7193));
		if (npc.getLocation().isWithinDistance(npc, victim, 1)) {
			clientSpeed = 70;
			gfxDelay = 80;
		} else if (npc.getLocation().isWithinDistance(npc, victim, 5)) {
			clientSpeed = 90;
			gfxDelay = 100;
		} else if (npc.getLocation().isWithinDistance(npc, victim, 8)) {
			clientSpeed = 110;
			gfxDelay = 120;
		} else {
			clientSpeed = 130;
			gfxDelay = 140;
		}
		hitDelay = (gfxDelay / 20) - 1;
		npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim.getCentreLocation(), 1293, 45, 50,
				clientSpeed, 100, 35, victim.getProjectileLockonIndex(), 10, 48));
		blockAnimation = false;
		npc.getCombatState().setAttackDelay(4);
		npc.getCombatState().setSpellDelay(5);
		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				this.stop();
				int damage = hasShayzienArmour(victim) ? 0 : Misc.random(25, 30);
				damage = random.nextInt(damage < 1 ? 1 : damage + 1);
				if (damage > victim.getSkills().getLevel(Skills.HITPOINTS)) {
					damage = victim.getSkills().getLevel(Skills.HITPOINTS);
				}
				victim.playGraphics(Graphic.create(1294));
				victim.inflictDamage(new Hit(damage), npc);
				if (damage > 0 && Misc.random(10) <= 3) {
					victim.getCombatState().setPoisonDamage(10, npc);
				}
				smite(npc, victim, damage);
				recoil(npc, victim, damage);
				vengeance(npc, victim, damage, 1);
			}
		});
		victim.getActiveCombatAction().defend(npc, victim, blockAnimation);
	}

	private void handleLizardMagicAttack(NPC npc, Mob victim) {
		if (victim.isPlayer()) {
			npc.playAnimation(Animation.create(7157));
			for (int i = 0; i < Misc.random(3, 4); i++) {
				minions.add(new NPC(SPAWN_ID, victim.getLocation().closestFreeTileOrSelf(npc.getLocation(),
						npc.getWidth(), npc.getHeight())));
			}
			npc.getCombatState().setAttackDelay(8);
			npc.getCombatState().setSpellDelay(9);
			minions.forEach(NPC::register);
			minions.forEach(n -> n.setInteractingEntity(Mob.InteractionMode.TALK, victim));
			minions.forEach(n -> World.getWorld().submit(new Tickable(1) {
				private int ticks = 10;
				private int curTicks;

				@Override
				public void execute() {
					if (curTicks >= ticks) {
						this.stop();
						n.playAnimation(Animation.create(7159));
						n.playGraphics(Graphic.create(1295, 10, 0));
						World.getWorld().submit(new Tickable(1) {

							@Override
							public void execute() {
								this.stop();
								if (n.getLocation().distance(victim.getLocation()) <= 3) {
									victim.inflictDamage(new Hit(Misc.random(4, 9)), n);
								}
								n.unregister();
							}
						});
					}
					Location to = Following.getDestination(n, victim);
					World.getWorld().doPath(new SizedPathFinder(true), n, to.getX(), to.getY());
					curTicks++;
				}
			}));
			minions.clear();
		}
	}

	private void handleLizardJumpAttack(NPC npc, Mob victim) {
		Location toJump = victim.getLocation();
		npc.setAttribute("attack", false);
		npc.playAnimation(JUMP_HIDE);
		npc.getCombatState().setAttackDelay(9);
		npc.getCombatState().setSpellDelay(10);
		World.getWorld().submit(new StoppingTick(6) {
			@Override
			public void executeAndStop() {
				npc.removeAttribute("attack");
				npc.setTeleportTarget(toJump.closestFreeTileOrSelf(toJump, npc.getWidth(), npc.getHeight()));
				npc.playAnimation(JUMP_DOWN);
				npc.getRegion().getPlayers().stream().filter(Objects::nonNull)
						.filter(i -> i.getLocation().distance(toJump) <= 2)
						.forEach(i -> i.inflictDamage(new Hit(Misc.random(20, 25)), npc));
			}
		});
	}

	private void handleLizardRange(NPC npc, Mob victim) {
		npc.playAnimation(npc.getAttackAnimation());

		if (npc.getLocation().isWithinDistance(npc, victim, 1)) {
			clientSpeed = 70;
			gfxDelay = 80;
		} else if (npc.getLocation().isWithinDistance(npc, victim, 5)) {
			clientSpeed = 90;
			gfxDelay = 100;
		} else if (npc.getLocation().isWithinDistance(npc, victim, 8)) {
			clientSpeed = 110;
			gfxDelay = 120;
		} else {
			clientSpeed = 130;
			gfxDelay = 140;
		}
		hitDelay = (gfxDelay / 20) - 1;
		npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim.getCentreLocation(), 1291, 45, 50,
				clientSpeed, 100, 35, victim.getProjectileLockonIndex(), 10, 48));
		blockAnimation = false;
		npc.getCombatState().setAttackDelay(4);
		npc.getCombatState().setSpellDelay(5);
		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				this.stop();
				int damage = damage(maxHit, npc, victim, CombatState.AttackType.RANGE, Skills.RANGE,
						Prayers.PROTECT_FROM_MISSILES, false, false);
				damage = random.nextInt(damage < 1 ? 1 : damage + 1);
				if (damage > victim.getSkills().getLevel(Skills.HITPOINTS)) {
					damage = victim.getSkills().getLevel(Skills.HITPOINTS);
				}
				victim.inflictDamage(new Hit(damage), npc);
				smite(npc, victim, damage);
				recoil(npc, victim, damage);
				vengeance(npc, victim, damage, 1);
			}
		});
		victim.getActiveCombatAction().defend(npc, victim, blockAnimation);
	}

	private void handleLizardMelee(NPC npc, Mob victim) {
		npc.playAnimation(random.nextInt(1) == 0 ? Animation.create(7192) : Animation.create(7158));
		hitDelay = 1;
		blockAnimation = true;
		maxHit = npc.getCombatDefinition().getMaxHit();
		damage = damage(maxHit, npc, victim, npc.getCombatState().getAttackType(), Skills.ATTACK,
				Prayers.PROTECT_FROM_MELEE, false, false);
		randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
		if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
			randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
		}
		hit = randomHit;
		npc.getCombatState().setAttackDelay(4);
		npc.getCombatState().setSpellDelay(5);

		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				victim.inflictDamage(new Hit(hit), npc);
				smite(npc, victim, hit);
				recoil(npc, victim, hit);
				this.stop();
			}
		});
		vengeance(npc, victim, hit, 1);

		victim.getActiveCombatAction().defend(npc, victim, blockAnimation);
	}

	public boolean hasShayzienArmour(Mob victim) {
		return victim.getEquipment() != null && victim.getEquipment().contains(13377)
				&& victim.getEquipment().contains(13378) && victim.getEquipment().contains(13379)
				&& victim.getEquipment().contains(13380) && victim.getEquipment().contains(13381);
	}

	@Override
	public int distance(Mob attacker) {
		return 7;
	}
}
