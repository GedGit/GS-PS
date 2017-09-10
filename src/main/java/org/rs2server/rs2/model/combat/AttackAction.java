package org.rs2server.rs2.model.combat;

import org.rs2server.Server;
import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.content.Following;
import org.rs2server.rs2.domain.service.api.content.ItemService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.combat.impl.MeleeCombatAction;
import org.rs2server.rs2.model.combat.impl.RangeCombatAction;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.map.Directions;
import org.rs2server.rs2.model.map.path.PrimitivePathFinder;
import org.rs2server.rs2.model.map.path.ProjectilePathFinder;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.npc.impl.kraken.Whirlpool;
import org.rs2server.rs2.model.npc.impl.zulrah.Zulrah;
import org.rs2server.rs2.model.npc.pc.PestControlPortal;
import org.rs2server.rs2.model.npc.pc.VoidKnight;
import org.rs2server.rs2.model.player.Player;

/**
 * The periodic attack event.
 * 
 * @author Graham Edgecombe
 * 
 */
public class AttackAction extends Action {

	@SuppressWarnings("unused")
	private final ItemService itemService = Server.getInjector().getInstance(ItemService.class);

	/**
	 * Creates the attack event.
	 * 
	 * @param mob
	 *            The entity that is attacking.
	 */
	public AttackAction(Mob mob) {
		super(mob, 0);
	}

	@Override
	public CancelPolicy getCancelPolicy() {
		return CancelPolicy.ONLY_ON_WALK;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.NEVER;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_NONE;
	}

	@Override
	public void execute() {
		final Mob mob = getMob();
		if (mob.isDestroyed()) {
			this.stop(); // they disconnected/got removed
			return;
		}
		if (mob.getCombatState().isDead()) {
			stop();
			return;
		}
		if (mob.getSkills().getLevel(Skills.HITPOINTS) < 1) {
			stop();
			return;
		}
		InteractionMode mode = mob.getInteractionMode();
		Mob target = mob.getInteractingEntity();
		if (mode != InteractionMode.ATTACK) {
			stop();
			return;
		}
		if (target == null || target.isDestroyed() || target.getCombatState().isDead()
				|| target.getSkills().getLevel(Skills.HITPOINTS) < 1) {
			mob.resetInteractingEntity();
			this.stop(); // the target disconnected / got removed
			return;
		}
		if (mob.isNPC()) {
			if (!mob.getLocation().isWithinDistance(mob.getWidth(), mob.getHeight(), target.getLocation(),
					target.getWidth(), target.getHeight(), mob.getActiveCombatAction().distance(mob) + 5)) {
				this.stop();
				return;
			}
		}
		if (mob.isPlayer()) {
			Player player = (Player) mob;
			Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
			if (weapon != null && (weapon.getId() == 11905 || weapon.getId() == 11907)) {
				player.getCombatState().setCombatStyle(CombatState.CombatStyle.AUTOCAST);
				player.setAutocastSpell(MagicCombatAction.Spell.TRIDENT_OF_THE_SEAS);
			}
			if (weapon != null && weapon.getId() == 12899) {
				player.getCombatState().setCombatStyle(CombatState.CombatStyle.AUTOCAST);
				player.setAutocastSpell(MagicCombatAction.Spell.TRIDENT_OF_THE_SWAMP);
			}
		}
		int extraDistance = (mob.getSprites().getSecondarySprite() != -1) ? 3 : 0;
		int requiredDistance = mob.getActiveCombatAction().distance(mob);
		int distance = mob.getCentreLocation().distanceToEntity(mob, target);
		if (target instanceof Whirlpool)
			requiredDistance = requiredDistance + 6;
		if (distance > requiredDistance + extraDistance) {
			Following.combatFollow(mob, target);
			return;
		}

		/*
		 * Can't attack if standing on top of the target
		 */
		if (mob.getWidth() == 1 && target.getWidth() == 1 && distance == 0 && target.getWalkingQueue().isEmpty()) {
			if (!mob.getCombatState().canMove() || !mob.getCombatState().canBeFrozen())
				return;
			Location loc = getClosestTile(allowedLocations(mob.getLocation(), 1), mob, target);
			mob.getWalkingQueue().reset();
			mob.getWalkingQueue().addStep(loc.getX(), loc.getY());
			mob.getWalkingQueue().finish();
			mob.setAttribute("positionMove", true);
			return;
		}

		boolean diag = diagonal(mob, target);
		if ((mob.getWidth() > 1 && mob.getHeight() > 1) || (target.getWidth() > 1 && target.getHeight() > 1)) {
			diag = false;
		}
		CombatAction action_ = CombatFormula.getActiveCombatAction(mob);
		if (diag && !mob.getCombatState().canMove() && action_ == MeleeCombatAction.getAction())
			return;

		if (diag && target.getWalkingQueue().isEmpty() && action_ == MeleeCombatAction.getAction()) {
			Location loc = mob.getCentreLocation();
			Location loc2 = target.getCentreLocation();
			mob.getWalkingQueue().reset();
			mob.getWalkingQueue().addStep(loc.getX(), loc2.getY());
			mob.getWalkingQueue().finish();
		}
		// if(distance <= requiredDistance) { //only reset walking queue when
		// they are exactly in distance, and not still moving
		// mob.getWalkingQueue().reset();
		// }
		final CombatAction action = mob.getActiveCombatAction();
		if ((!(target instanceof Zulrah) && !(target instanceof Whirlpool))
				&& (action == MagicCombatAction.getAction() || action == RangeCombatAction.getAction())
				&& !ProjectilePathFinder.clippedProjectile(mob, target)) {

			return;
		}
		if (!action.canHit(mob, target, true, false)) {
			mob.getCombatState().setQueuedSpell(null);
			mob.resetInteractingEntity();
			this.stop();
			return;
		}
		/**
		 * Checks for attack types as spells run on a seperate timer.
		 */
		if (CombatFormula.getActiveCombatAction(mob) == MagicCombatAction.getAction()) {
			if (mob.getCombatState().getSpellDelay() > 0 || mob.getCombatState().getAttackDelay() > 2)
				return;
		} else {
			if (mob.getCombatState().getAttackDelay() > 0)
				return;
		}
		if (mob.getSkills().getLevel(Skills.HITPOINTS) < 1) {
			stop();
			return;
		}
		if (target.isNPC() && target.getInteractingEntity() != mob) {
			NPC n = (NPC) target;
			if (n.getCombatDefinition() != null && !(n instanceof PestControlPortal))
				n.getCombatState().startAttacking(mob, false);
		}
		// retaliating + not attacking an npc = retaliate
		// retalating + attacking an npc = not-retaliate
		if (target.isAutoRetaliating()) {
			if (target.getInteractingEntity() == null || (target.getInteractionMode() != InteractionMode.ATTACK)) {
				if (target.isNPC()) {
					NPC n = (NPC) target;
					if (n.getCombatDefinition() != null && !(n instanceof PestControlPortal))
						n.getCombatState().startAttacking(mob, false);
				} else if (target.isIdle()) {
					// The target should auto-retaliate if they are idling
					target.getCombatState().startAttacking(mob, false);
				}
			}
		}
		if (target != null && mode == InteractionMode.ATTACK) {
			attack(target);
			if (CombatFormula.getActiveCombatAction(mob) != MagicCombatAction.getAction()
					&& mob.getCombatState().getAttackDelay() == 0) {
				mob.getCombatState().setAttackDelay(mob.getCombatCooldownDelay());
				mob.getCombatState().setSpellDelay(mob.getCombatCooldownDelay());
			}
		} else {
			this.stop(); // will be restarted later if another attack starts
		}
	}

	private boolean diagonal(Mob mob, Mob target) {
		Location l = mob.getLocation();
		Location l2 = target.getLocation();
		int x = Math.abs(l.getX() - l2.getX());
		int y = Math.abs(l.getY() - l2.getY());
		return x == 1 && y == 1;
	}

	public Location[] allowedLocations(Location t, int size) {
		return new Location[] { Location.create(t.getX(), t.getY() + size, t.getPlane()),
				Location.create(t.getX() + size, t.getY(), t.getPlane()),
				Location.create(t.getX(), t.getY() - size, t.getPlane()),
				Location.create(t.getX() - size, t.getY(), t.getPlane()) };
	}

	public Location getClosestTile(Location[] array, Mob p, Mob opp) {
		Location tile = null;
		Location pt = p.getLocation();
		double dist = 123.0;
		for (Location t : array) {
			double newDist = pt.distanceToPoint(t);
			if (newDist < dist) {
				if (PrimitivePathFinder.canMove(p.getLocation(), Directions.directionFor(pt, t), false)) {
					dist = newDist;
					tile = t;
				}
			}
		}
		return tile;
	}

	/**
	 * Attacks the target, if possible.
	 * 
	 * @param target
	 *            The target..
	 */
	private void attack(Mob target) {
		final Mob mob = getMob();

		if (!(mob instanceof PestControlPortal || mob instanceof VoidKnight)) {
			final CombatAction action = mob.getActiveCombatAction();
			action.hit(mob, target);
			if (action == MagicCombatAction.getAction() && !mob.isNPC() && mob.getCombatState().getQueuedSpell() == null
					&& mob.getAutocastSpell() == null)
				this.stop();
			if (target.isNPC() && !(target instanceof Zulrah))
				target.setInteractingEntity(InteractionMode.ATTACK, mob);
		}
	}

	@Override
	public void stop() {
		super.stop();
		if (getMob().isNPC())
			getMob().resetInteractingEntity();
	}
}