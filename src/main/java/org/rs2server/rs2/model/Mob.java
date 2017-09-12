package org.rs2server.rs2.model;

/*
 * IMPORTANT MESSAGE - READ BEFORE ADDING NEW METHODS/FIELDS TO THIS CLASS
 * 
 * Before you create a field (variable) or method in this class, which is specific to a particular
 * skill, quest, minigame, etc, THINK! There is almost always a better way (e.g. attribute system,
 * helper methods in other classes, etc.)
 * 
 * We don't want this to turn into another client.java! If you need advice on alternative methods,
 * feel free to discuss it with me.
 * 
 * Graham
 */

import org.rs2server.Server;
import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.action.ActionQueue;
import org.rs2server.rs2.action.impl.CoordinateAction;
import org.rs2server.rs2.domain.service.api.DeadmanService;
import org.rs2server.rs2.model.Hit.HitPriority;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatFormula;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.combat.TotalDamage;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.Spell;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.container.PriceChecker;
import org.rs2server.rs2.model.container.Trade;
import org.rs2server.rs2.model.minigame.impl.WarriorsGuild;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.npc.Pet;
import org.rs2server.rs2.model.npc.impl.cerberus.Cerberus;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.CerberusGhost;
import org.rs2server.rs2.model.npc.impl.zulrah.Zulrah;
import org.rs2server.rs2.model.npc.pc.PestControlNpc;
import org.rs2server.rs2.model.npc.pc.PestControlPortal;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.fish.Fishing;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.tickable.impl.*;
import org.rs2server.rs2.util.Misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represents a character in the game world, i.e. a <code>Player</code> or an
 * <code>NPC</code>.
 *
 * @author Graham Edgecombe
 */
public abstract class Mob extends Entity {

	private VenomDrainTick venomDrainTick;
	private Mob frozenBy;

	public abstract void register();

	public abstract void unregister();

	public void setFrozenBy(Mob frozenBy) {
		this.frozenBy = frozenBy;
	}

	public Mob getFrozenBy() {
		return frozenBy; 
	}

	/**
	 * The interaction mode.
	 *
	 * @author Graham Edgecombe
	 */
	public enum InteractionMode {
		ATTACK, TALK, REQUEST, FOLLOW
	}

	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	/**
	 * Gets the random number generator.
	 *
	 * @return The random number generator.
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * Gets this mob instance.
	 *
	 * @return This mob instance.
	 */
	public Mob getMob() {
		return this;
	}

	/**
	 * The index in the <code>EntityList</code>.
	 */
	private int index = 0;

	/**
	 * The temporary interface attributes map. items set here are removed when
	 * interfaces are closed.
	 */
	private Map<String, Object> interfaceAttributes = new HashMap<String, Object>();

	/**
	 * The permanent attributes map. Items set here are only removed when told to.
	 */
	protected Map<String, Object> attributes = new HashMap<String, Object>();

	/**
	 * The teleportation target.
	 */
	private Location teleportTarget = null;

	/**
	 * The update flags.
	 */
	private final UpdateFlags updateFlags = new UpdateFlags();

	/**
	 * The list of local players.
	 */
	private final List<Player> localPlayers = new LinkedList<Player>();

	/**
	 * The list of local npcs.
	 */
	private final List<NPC> localNpcs = new LinkedList<NPC>();

	/**
	 * The teleporting flag.
	 */
	private boolean teleporting = false;

	/**
	 * Destroyed flag.
	 */
	private boolean destroyed = false;

	/**
	 * The walking queue.
	 */
	private final WalkingQueue walkingQueue = new WalkingQueue(this);

	/**
	 * The mob's equipment.
	 */
	private final Container equipment = new Container(Container.Type.STANDARD, Equipment.SIZE);

	/**
	 * The sprites i.e. walk directions.
	 */
	private final Sprites sprites = new Sprites();

	/**
	 * A queue of actions.
	 */
	private final ActionQueue actionQueue = new ActionQueue(this);

	/**
	 * The combat state.
	 */
	private final CombatState combatState = new CombatState(this);

	/**
	 * A set of hits done in this entity during the current update cycle.
	 */
	private final List<Hit> hits = new LinkedList<Hit>();

	/**
	 * The primary hit to display this update cycle.
	 */
	private Hit primaryHit;

	/**
	 * The secondary hit to display this update cycle.
	 */
	private Hit secondaryHit;

	/**
	 * The skill restoration tick.
	 */
	private Tickable skillsUpdateTick;

	/**
	 * The prayer restoration tick.
	 */
	private Tickable prayerUpdateTick;

	/**
	 * The mob's poison drain tick.
	 */
	private PoisonDrainTick poisonDrainTick;

	/**
	 * The mob's special energy update tick.
	 */
	private SpecialEnergyRestoreTick specialUpdateTick;

	/**
	 * The last known map region.
	 */
	private Location lastKnownRegion = this.getLocation();

	/**
	 * Map region changing flag.
	 */
	private boolean mapRegionChanging = false;

	/**
	 * The current animation.
	 */
	private Animation currentAnimation;

	/**
	 * The current graphic.
	 */
	private Graphic currentGraphic;

	/**
	 * The force walk variables.
	 */
	private int[] forceWalk;

	/**
	 * The interaction mode.
	 */
	private InteractionMode interactionMode;

	/**
	 * The interacting entity.
	 */
	private Mob interactingEntity;

	/**
	 * The face location.
	 */
	private Location face;

	/**
	 * The attack drained flag.
	 */
	private boolean attackDrained;

	/**
	 * The strength drained flag.
	 */
	private boolean strengthDrained;

	/**
	 * The defence drained flag.
	 */
	private boolean defenceDrained;

	/**
	 * The text to display with the force chat mask.
	 */
	private String forcedChat;

	/**
	 * The mob's emote flag.
	 */
	private boolean emote = true;

	/**
	 * The mob's animatable flag.
	 */
	private boolean animate = true;

	/**
	 * The players energy restore tickable.
	 */
	private Tickable energyRestoreTick;

	private Location lastLocation;

	private long lastRunRecovery;

	public int venomDamage = 6;

	public Mob inflictVenom() {
		setAttribute("venom", true);
		setVenomDrainTick(new VenomDrainTick(this));
		World.getWorld().submit(getVenomDrainTick());
		if (isPlayer()) {
			Player player = (Player) this;
			if (hasAttribute("venom")) {
				getActionSender().sendMessage("You have been poisoned by venom!");
				player.getActionSender().sendConfig(102, 1000000);
			}
			player.getDatabaseEntity().getCombatEntity().setVenomDamage(6);
		} else if (isNPC()) {
			venomDamage = 6;
		}
		return this;
	}

	public Mob removeVenom() {
		setAttribute("venom", false);
		if (getVenomDrainTick() != null)
			getVenomDrainTick().stop();
		if (isPlayer()) {
			Player player = (Player) this;
			player.getActionSender().sendConfig(102, 0);
			player.getDatabaseEntity().getCombatEntity().setVenomDamage(6);
		} else if (isNPC())
			venomDamage = 6;
		return this;
	}

	/**
	 * Creates the entity.
	 */
	public Mob() {
		if (!this.isDestroyed()) {
			setLocation(DEFAULT_LOCATION);
			this.lastKnownRegion = getLocation();
			World.getWorld().submit(skillsUpdateTick = new SkillsUpdateTick(this));
			if (combatState.getPoisonDamage() > 0) {
				World.getWorld().submit(this.poisonDrainTick = new PoisonDrainTick(this));
			}
			if (this.combatState.getSpecialEnergy() < 100) {
				World.getWorld().submit(this.specialUpdateTick = new SpecialEnergyRestoreTick(this));
			}
			this.setAttribute("newPlayer", true);
		}
	}

	/**
	 * Resets misc information about the mob.
	 */
	public void resetVariousInformation() {
		resetInteractingEntity();
		getCombatState().setSpecialEnergy(100);
		getCombatState().setPoisonDamage(0, null);
		getCombatState().getDamageMap().reset();
		getCombatState().setCurrentSpell(null);
		getCombatState().setQueuedSpell(null);
		getCombatState().setVengeance(false);
		getCombatState().setSkullTicks(0);
		getCombatState().setLastAte(null);
		getCombatState().setTeleblocked(false);
		getCombatState().resetPrayers();
		getActionQueue().clearAllActions();
		getSkills().resetStats();
		getWalkingQueue().setEnergy(100);
		// getActionSender().sendConfig(102, 0);
		setAutocastSpell(null);
		removeAttribute("venom");
		venomDamage = 6;
		if (isPlayer()) {
			Player player = (Player) this;
			player.setFightPitsWinner(false);
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			player.getActionSender().removeWalkableInterface();
			player.getDatabaseEntity().getCombatEntity().setVenomDamage(0);
			player.getDatabaseEntity().getPlayerSettings().setTeleBlockTimer(0);
			player.getDatabaseEntity().getPlayerSettings().setTeleBlocked(false);
		}
	}

	public void setTempInteractingEntity(InteractionMode talk, Mob mob) {
		setInteractingEntity(talk, mob);
		World.getWorld().submit(new Tickable(3) {

			@Override
			public void execute() {
				this.stop();
				resetInteractingEntity();

			}

		});

	}

	/**
	 * Gets the action queue.
	 *
	 * @return The action queue.
	 */
	public ActionQueue getActionQueue() {
		return actionQueue;
	}

	/**
	 * Gets the combat state.
	 *
	 * @return The combat state.
	 */
	public CombatState getCombatState() {
		return combatState;
	}

	/**
	 * Makes this entity face a location.
	 *
	 * @param location
	 *            The location to face.
	 */
	public void face(Location location) {
		this.face = location;
		this.updateFlags.flag(UpdateFlag.FACE_COORDINATE);
		setAttribute("facing", location);
	}

	/**
	 * Checks if this entity is facing a location.
	 *
	 * @return The entity face flag.
	 */
	public boolean isFacing() {
		return face != null;
	}

	/**
	 * Resets the facing location.
	 */
	public void resetFace() {
		this.face = null;
		this.updateFlags.flag(UpdateFlag.FACE_COORDINATE);
	}

	/**
	 * Gets the face location.
	 *
	 * @return The face location, or <code>null</code> if the entity is not facing.
	 */
	public Location getFaceLocation() {
		return face;
	}

	/**
	 * Checks if the mob is idle. If the mob is not interacting, is not moving, and
	 * has no pending actions, it is idling.
	 *
	 * @return true if the mob is idle, false if not
	 */
	public boolean isIdle() {
		return getInteractingEntity() == null && getWalkingQueue().isEmpty() && actionQueue.isEmpty();
	}

	/**
	 * Checks if this entity is interacting with another entity.
	 *
	 * @return The entity interaction flag.
	 */
	public boolean isInteracting() {
		return interactingEntity != null;
	}

	/**
	 * Gets the interaction mode.
	 *
	 * @return The interaction mode.
	 */
	public InteractionMode getInteractionMode() {
		return interactionMode;
	}

	/**
	 * Sets the interacting entity.
	 *
	 * @param mode
	 *            The interaction mode.
	 * @param mob
	 *            The new entity to interact with.
	 */
	public void setInteractingEntity(InteractionMode mode, Mob mob) {
		this.interactionMode = mode;
		this.interactingEntity = mob;
		this.updateFlags.flag(UpdateFlag.FACE_ENTITY);
	}

	/**
	 * Resets the interacting entity.
	 */
	public void resetInteractingEntity() {
		if (this.getInteractingEntity() != null) {
			if (this.getInteractionMode() == InteractionMode.TALK
					&& this.getInteractingEntity().getInteractionMode() == InteractionMode.TALK
					&& this.getInteractingEntity().getInteractingEntity() == this) {
				this.getInteractingEntity().setInteractingEntity(null, null); // endGame
																				// an
																				// infinite
																				// for
																				// loop
																				// of
																				// each
																				// other
																				// cancelling
				this.getInteractingEntity().resetInteractingEntity();
				// this will be used for an NPC, as NPC's do not walk whilst in
				// dialogue with someone.
			}
		}
		this.interactionMode = null;
		this.interactingEntity = null;
		this.updateFlags.flag(UpdateFlag.FACE_ENTITY);
	}

	/**
	 * Gets the interacting entity.
	 *
	 * @return The entity to interact with.
	 */
	public Mob getInteractingEntity() {
		return interactingEntity;
	}

	/**
	 * Gets this mob in entity form.
	 *
	 * @return This mob in entity form.
	 */
	public Entity getEntity() {
		return this;
	}

	/**
	 * Gets the current animation.
	 *
	 * @return The current animation;
	 */
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}

	/**
	 * Gets the current graphic.
	 *
	 * @return The current graphic.
	 */
	public Graphic getCurrentGraphic() {
		return currentGraphic;
	}

	public int[] getForceWalk() {
		return forceWalk;
	}

	public void setForceWalk(final int[] forceWalk, final boolean removeAttribute) {
		this.forceWalk = forceWalk;
		if (forceWalk.length > 0) {
			World.getWorld().submit(new Tickable(forceWalk[7]) {
				@Override
				public void execute() {
					setTeleportTarget(getLocation().transform(forceWalk[2], forceWalk[3], 0));
					if (removeAttribute)
						removeAttribute("busy");
					this.stop();
				}
			});
		}
	}

	/**
	 * Resets attributes after an update cycle.
	 */
	public void reset() {
		this.currentAnimation = null;
		this.currentGraphic = null;
	}

	/**
	 * Animates the entity.
	 *
	 * @param animation
	 *            The animation.
	 */
	public void playAnimation(Animation animation) {
		this.currentAnimation = animation;
		if (animation != null) {
			this.getUpdateFlags().flag(UpdateFlag.ANIMATION);
		}
	}

	/**
	 * Plays graphics.
	 *
	 * @param graphic
	 *            The graphics.
	 */
	public void playGraphics(Graphic graphic) {
		this.currentGraphic = graphic;
		this.getUpdateFlags().flag(UpdateFlag.GRAPHICS);
	}

	/**
	 * Gets the walking queue.
	 *
	 * @return The walking queue.
	 */
	public WalkingQueue getWalkingQueue() {
		return walkingQueue;
	}

	/**
	 * Sets the last known map region.
	 *
	 * @param lastKnownRegion
	 *            The last known map region.
	 */
	public void setLastKnownRegion(Location lastKnownRegion) {
		this.lastKnownRegion = lastKnownRegion;
	}

	/**
	 * Gets the last known map region.
	 *
	 * @return The last known map region.
	 */
	public Location getLastKnownRegion() {
		return lastKnownRegion;
	}

	/**
	 * Checks if the map region has changed in this cycle.
	 *
	 * @return The map region changed flag.
	 */
	public boolean isMapRegionChanging() {
		return mapRegionChanging;
	}

	/**
	 * Sets the map region changing flag.
	 *
	 * @param mapRegionChanging
	 *            The map region changing flag.
	 */
	public void setMapRegionChanging(boolean mapRegionChanging) {
		this.mapRegionChanging = mapRegionChanging;
	}

	/**
	 * Checks if this entity has a target to teleport to.
	 *
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean hasTeleportTarget() {
		return teleportTarget != null;
	}

	/**
	 * Gets the teleport target.
	 *
	 * @return The teleport target.
	 */
	public Location getTeleportTarget() {
		return teleportTarget;
	}

	/**
	 * Sets the teleport target.
	 *
	 * @param teleportTarget
	 *            The target location.
	 */
	public void setTeleportTarget(Location teleportTarget) {
		if (isPlayer() && hasAttribute("looted_barrows"))
			setAttribute("looted_barrows", false);
		if (isPlayer() && getInterfaceState().getBlackout())
			getActionSender().updateMinimap(ActionSender.NO_BLACKOUT);
		if (WarriorsGuild.IN_GAME.contains(this))
			WarriorsGuild.IN_GAME.remove(this);

		getLocalNPCs().stream()
				.filter(n -> n.getInteractingEntity() != null && !(n.getInteractingEntity() instanceof Pet))
				.forEach(n -> {
					Mob mob = n.getInteractingEntity();

					n.face(mob.getLocation());
					if (mob.getInteractingEntity() == n)
						mob.face(n.getLocation());
				});

		getLocalPlayers().stream()
				.filter(p -> p.getInteractingEntity() != null && !(p.getInteractingEntity() instanceof Pet))
				.forEach(p -> {
					Mob mob = p.getInteractingEntity();

					p.face(mob.getLocation());
					if (mob.getInteractingEntity() == p)
						mob.face(p.getLocation());
				});
		getWalkingQueue().reset();
		this.teleportTarget = teleportTarget;
		if (this.getActionSender() != null)
			this.getActionSender().sendAreaInterface(null, teleportTarget);
	}

	public Object setTeleportTargetObj(Location teleportTarget) {
		if (isPlayer() && hasAttribute("looted_barrows")) {
			setAttribute("looted_barrows", false);
		}
		if (isPlayer() && getInterfaceState().getBlackout()) {
			getActionSender().updateMinimap(ActionSender.NO_BLACKOUT);
		}
		if (WarriorsGuild.IN_GAME.contains(this)) {
			WarriorsGuild.IN_GAME.remove(this);
		}
		getLocalNPCs().stream()
				.filter(n -> n.getInteractingEntity() != null && !(n.getInteractingEntity() instanceof Pet))
				.forEach(n -> {
					Mob mob = n.getInteractingEntity();

					n.face(mob.getLocation());
					if (mob.getInteractingEntity() == n) {
						mob.face(n.getLocation());
					}
				});

		getLocalPlayers().stream()
				.filter(p -> p.getInteractingEntity() != null && !(p.getInteractingEntity() instanceof Pet))
				.forEach(p -> {
					Mob mob = p.getInteractingEntity();

					p.face(mob.getLocation());
					if (mob.getInteractingEntity() == p) {
						mob.face(p.getLocation());
					}
				});
		getWalkingQueue().reset();
		this.teleportTarget = teleportTarget;
		if (this.getActionSender() != null)
			this.getActionSender().sendAreaInterface(null, teleportTarget);
		return null;
	}

	/**
	 * Resets the teleport target.
	 */
	public void resetTeleportTarget() {
		this.teleportTarget = null;
	}

	/**
	 * Gets the sprites.
	 *
	 * @return The sprites.
	 */
	public Sprites getSprites() {
		return sprites;
	}

	/**
	 * Checks if this player is teleporting.
	 *
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isTeleporting() {
		return teleporting;
	}

	/**
	 * Sets the teleporting flag.
	 *
	 * @param teleporting
	 *            The teleporting flag.
	 */
	public void setTeleporting(boolean teleporting) {
		this.teleporting = teleporting;
	}

	/**
	 * Gets the list of local players.
	 *
	 * @return The list of local players.
	 */
	public List<Player> getLocalPlayers() {
		return localPlayers;
	}

	/**
	 * Gets the list of local npcs.
	 *
	 * @return The list of local npcs.
	 */
	public List<NPC> getLocalNPCs() {
		return localNpcs;
	}

	/**
	 * Sets the entity's index.
	 *
	 * @param index
	 *            The index.
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Gets the entity's index.
	 *
	 * @return The index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the current location.
	 *
	 * @param location
	 *            The current location.
	 */
	@Override
	public void setLocation(Location location) {
		Location before = getLocation();
		super.setLocation(location);
		setLastLocation(before);
		if (isPlayer()) {
			Player player = (Player) this;
			if (player.isActive()) {
				getActionSender().sendAreaInterface(before, getLocation());
				if (player.getMinigame() != null) {
					player.getMinigame().movementHook(player);
				}
			}
		}
	}

	/**
	 * Destroys this entity.
	 */
	public void destroy() {
		if (!destroyed) {
			if (energyRestoreTick != null) {
				energyRestoreTick.stop();
				energyRestoreTick = null;
			}
			if (skillsUpdateTick != null) {
				skillsUpdateTick.stop();
				skillsUpdateTick = null;
			}
			if (prayerUpdateTick != null) {
				prayerUpdateTick.stop();
				prayerUpdateTick = null;
			}
			if (poisonDrainTick != null) {
				poisonDrainTick.stop();
				poisonDrainTick = null;
			}
			if (venomDrainTick != null) {
				venomDrainTick.stop();
				venomDrainTick = null;
			}
			if (specialUpdateTick != null) {
				specialUpdateTick.stop();
				specialUpdateTick = null;
			}
			resetInteractingEntity(); // so that if someone logs in dialogue, it
										// resets the interacting entity
										// allowing them to move again.
			destroyed = true;
			removeFromRegion(getRegion());
		}
	}

	/**
	 * Gets the current combat cooldown delay in milliseconds.
	 *
	 * @return The current combat cooldown delay.
	 */
	public abstract int getCombatCooldownDelay();

	/**
	 * Gets the mob's default combat action.
	 *
	 * @return The mob's default combat action.
	 */
	public abstract CombatAction getDefaultCombatAction();

	/**
	 * Gets the mob's autocast spell.
	 *
	 * @return The mob's autocast spell.
	 */
	public abstract Spell getAutocastSpell();

	/**
	 * Sets the mob's autocast spell.
	 *
	 * @param spell
	 *            The spell to set.
	 */
	public abstract void setAutocastSpell(Spell spell);

	/**
	 * Gets the hit flag defined by the entity type (EG Players wilderness level)
	 *
	 * @param victim
	 *            The victim.
	 * @return The hit flag.
	 */
	public abstract boolean canHit(Mob victim, boolean messages);

	/**
	 * Checks if this entity will auto retaliate to any attacks.
	 *
	 * @return <code>true</code> if the entity will auto retaliate,
	 *         <code>false</code> if not.
	 */
	public abstract boolean isAutoRetaliating();

	/**
	 * Gets the mob's attack animation.
	 *
	 * @return The mob's attack animation.
	 */
	public abstract Animation getAttackAnimation();

	/**
	 * Gets the mob's defend animation.
	 *
	 * @return The mob's defend animation.
	 */
	public abstract Animation getDefendAnimation();

	/**
	 * Gets the mob's death animation.
	 *
	 * @return The mob's death animation.
	 */
	public abstract Animation getDeathAnimation();

	/**
	 * Gets the projectile lockon index of this mob.
	 *
	 * @return The projectile lockon index of this mob.
	 */
	public abstract int getProjectileLockonIndex();

	/**
	 * The protection prayer modifier. EG: NPCs = 1, players = 0.6.
	 */
	public abstract double getProtectionPrayerModifier();

	/**
	 * Gets the mob's defined name.
	 *
	 * @return The mob's defined name.
	 */
	public abstract String getDefinedName();

	/**
	 * Gets the mob's undefined name (Players).
	 *
	 * @return The mob's undefined name (Players).
	 */
	public abstract String getUndefinedName();

	/**
	 * Resets the mob's animations.
	 *
	 * @return Resets the mob's animations.
	 */
	public abstract void setDefaultAnimations();

	/**
	 * Drops the loot for the killer.
	 *
	 * @param mob
	 *            The killer to drop the items for.
	 */
	public abstract void dropLoot(Mob mob);

	/**
	 * Gets the default drawback graphic for this mob's range attack.
	 *
	 * @return The default drawback graphic for this mob's range attack.
	 */
	public abstract Graphic getDrawbackGraphic();

	/**
	 * Gets the default projectile id for this mob's range attack.
	 *
	 * @return The default projectile id for this mob's range attack.
	 */
	public abstract int getProjectileId();

	/**
	 * Gets the update flags.
	 *
	 * @return The update flags.
	 */
	public UpdateFlags getUpdateFlags() {
		return updateFlags;
	}

	/**
	 * Gets the player's equipment.
	 *
	 * @return The player's equipment.
	 */
	public Container getEquipment() {
		return equipment;
	}

	/**
	 * Checks if this entity has been destroyed.
	 *
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isDestroyed() {
		return destroyed;
	}

	/**
	 * Gets the hit queue.
	 *
	 * @return The hit queue.
	 */
	public List<Hit> getHitQueue() {
		return hits;
	}

	/**
	 * Sets the primary hit.
	 *
	 * @param hit
	 *            The primary hit.
	 */
	public void setPrimaryHit(Hit hit) {
		this.primaryHit = hit;
	}

	/**
	 * Sets the secondary hit.
	 *
	 * @param hit
	 *            The secondary hit.
	 */
	public void setSecondaryHit(Hit hit) {
		this.secondaryHit = hit;
	}

	/**
	 * Gets the primary hit.
	 *
	 * @return The primary hit.
	 */
	public Hit getPrimaryHit() {
		return primaryHit;
	}

	/**
	 * Gets the secondary hit.
	 *
	 * @return The secondary hit.
	 */
	public Hit getSecondaryHit() {
		return secondaryHit;
	}

	/**
	 * Resets the primary and secondary hits.
	 */
	public void resetHits() {
		primaryHit = null;
		secondaryHit = null;
	}

	public abstract void tick();

	/**
	 * Gets the mob's action sender.
	 *
	 * @return The mob's action sender.
	 */
	public abstract ActionSender getActionSender();

	/**
	 * Gets the mob's interface state.
	 *
	 * @return The mob's interface state.
	 */
	public abstract InterfaceState getInterfaceState();

	/**
	 * Gets a mob's inventory.
	 *
	 * @return The mob's inventory.
	 */
	public abstract Container getInventory();

	/**
	 * Gets the mob's skill levels.
	 *
	 * @return The mob's skill levels.
	 */
	public abstract Skills getSkills();

	/**
	 * Creates the force chat mask.
	 *
	 * @param message
	 */
	public void forceChat(String message) {
		forcedChat = message;
		updateFlags.flag(UpdateFlag.FORCED_CHAT);
	}

	/**
	 * Creates the force chat mask.
	 *
	 * @param message
	 */
	public void setForceChat(String message) {
		forcedChat = message;
	}

	/**
	 * Gets the message to display with the force chat mask.
	 *
	 * @return The message to display with the force chat mask.
	 */
	public String getForcedChatMessage() {
		return forcedChat;
	}

	/**
	 * Gets the emote flag.
	 *
	 * @return The emote flag.
	 */
	public boolean canEmote() {
		return emote;
	}

	/**
	 * Sets the emote flag.
	 *
	 * @param animate
	 *            The emote flag to set.
	 */
	public void setEmote(boolean emote) {
		this.emote = emote;
	}

	/**
	 * Gets the animatable flag.
	 *
	 * @return The animatable flag.
	 */
	public boolean canAnimate() {
		return animate;
	}

	/**
	 * Sets the animatable flag.
	 *
	 * @param animate
	 *            The animatable flag to set.
	 */
	public void setAnimate(boolean animate) {
		this.animate = animate;
	}

	/**
	 * @return the energyRestoreTick
	 */
	public Tickable getEnergyRestoreTick() {
		return energyRestoreTick;
	}

	/**
	 * @param energyRestoreTick
	 *            the energyRestoreTick to set
	 */
	public void setEnergyRestoreTick(Tickable energyRestoreTick) {
		this.energyRestoreTick = energyRestoreTick;
	}

	/**
	 * @return the prayerUpdateTick
	 */
	public Tickable getPrayerUpdateTick() {
		return prayerUpdateTick;
	}

	/**
	 * @param prayerUpdateTick
	 *            the prayerUpdateTick to set
	 */
	public void setPrayerUpdateTick(Tickable prayerUpdateTick) {
		this.prayerUpdateTick = prayerUpdateTick;
	}

	/**
	 * @param poisonDrainTick
	 *            the poisonDrainTick to set
	 */
	public void setPoisonDrainTick(PoisonDrainTick poisonDrainTick) {
		this.poisonDrainTick = poisonDrainTick;
	}

	public void setVenomDrainTick(VenomDrainTick venomDrainTick) {
		this.venomDrainTick = venomDrainTick;
	}

	/**
	 * Gets the mob's poison drain tick.
	 *
	 * @return The mob's poisonDrainTick.
	 */
	public PoisonDrainTick getPoisonDrainTick() {
		return poisonDrainTick;
	}

	public VenomDrainTick getVenomDrainTick() {
		return venomDrainTick;
	}

	/**
	 * @param specialUpdateTick
	 *            The specialUpdateTick to set.
	 */
	public void setSpecialUpdateTick(SpecialEnergyRestoreTick specialUpdateTick) {
		this.specialUpdateTick = specialUpdateTick;
	}

	/**
	 * Gets the mob's special energy update tick.
	 *
	 * @return The mob's prayer energy tick.
	 */
	public SpecialEnergyRestoreTick getSpecialUpdateTick() {
		return specialUpdateTick;
	}

	/**
	 * Inflicts damage to the mob.
	 *
	 * @param hit
	 *            The hit to deal.
	 * @param mob
	 *            The damage dealer.
	 */
	public void inflictDamage(Hit hit, Mob attacker) {

		if (!canDamaged) {
			return;
		}

		if (getActionSender() != null) {
			if (this.isPlayer()) {
				Player player = (Player) this;

				player.getActionSender().removeAllInterfaces();
				player.getActionSender().removeChatboxInterface();

				if (getInterfaceState().isInterfaceOpen(PriceChecker.PRICE_INVENTORY_INTERFACE)) {
					PriceChecker.returnItems(player);
				}

				if (player.getInterfaceState().isEnterAmountInterfaceOpen()) {
					player.getActionSender().removeEnterAmountInterface();
				}

				if (player.getAttribute("bank_searching") != null) {
					player.getActionSender().removeEnterAmountInterface();
					player.removeAttribute("bank_searching");
				}

				if (player.getInterfaceState().isInterfaceOpen(Equipment.SCREEN)
						|| player.getInterfaceState().isInterfaceOpen(Bank.BANK_INVENTORY_INTERFACE)
						|| player.getInterfaceState().isInterfaceOpen(Trade.TRADE_INVENTORY_INTERFACE)
						|| player.getInterfaceState().isInterfaceOpen(Shop.SHOP_INVENTORY_INTERFACE)
						|| player.getInterfaceState().isInterfaceOpen(PriceChecker.PRICE_INVENTORY_INTERFACE)) {
					// int tab = (int) player.getAttribute("tabmode") == 164 ?
					// 56 : (int)player.getAttribute("tabmode") == 161 ? 58 :
					// 60;
					// player.getActionSender().removeInterfaces(player.getAttribute("tabmode"),
					// tab);
					player.getActionSender().removeInventoryInterface();
					player.resetInteractingEntity();
				}

			}
		}
		if (attacker != null) {
			if (attacker.getLocation().distance(this.getLocation()) > 32) {
				return;
			}
		}
		if (hit.getDamage() > getSkills().getLevel(Skills.HITPOINTS)) {
			hit = new Hit(getSkills().getLevel(Skills.HITPOINTS));
		}

		if (getSkills().getLevel(Skills.HITPOINTS) < 1 && hit.getDamage() > 0) {
			return;
		}

		if (combatState.isDead() && hit.getDamage() > 0) { // If its a double
															// hitting spec, we
															// still want to see
															// their 0 damage
			return;
		}
		if (attacker != null && (this instanceof PestControlNpc || this instanceof PestControlPortal)) {
			int hitsDealt = (int) (attacker.getAttribute("hits_dealt") == null ? 0
					: attacker.getAttribute("hits_dealt"));
			attacker.setAttribute("hits_dealt", hitsDealt + hit.getDamage());
		}
		getSkills().decreaseLevel(Skills.HITPOINTS, hit.getDamage());
		if (combatState.getPrayer(Prayers.REDEMPTION) && getSkills().getLevel(Skills.HITPOINTS) > 0) {
			if (getSkills().getLevel(Skills.HITPOINTS) < (getSkills().getLevelForExperience(Skills.HITPOINTS) * 0.10)) {
				combatState.resetPrayers();
				if (getActionSender() != null) {
					getActionSender().sendMessage("You have run out of prayer points; you must recharge at an altar.");
				}
				getSkills().setPrayerPoints(0, true);
				getSkills().increaseLevel(Skills.HITPOINTS,
						(int) (getSkills().getLevelForExperience(Skills.PRAYER) * 0.25));
				playGraphics(Graphic.create(436));
			}
		}
		// if(getEquipment().contains(2570) &&
		// getSkills().getLevel(Skills.HITPOINTS) > 0) {
		// if(combatState.canTeleport() &&
		// getSkills().getLevel(Skills.HITPOINTS) <=
		// (getSkills().getLevelForExperience(Skills.HITPOINTS) * 0.10)) {
		// initiateTeleport(TeleportType.NORMAL_TELEPORT, Location.create(3225 +
		// random.nextInt(1), 3218 + random.nextInt(1), 0));
		// getEquipment().remove(new Item(2570, 1));
		// if(getActionSender() != null) {
		// getActionSender().sendMessage("Your Ring of Life saves you and is
		// destroyed in the process.");
		// }
		// }
		// }
		if (getHitQueue().size() >= 4) {
			hit = new Hit(hit.getDamage(), HitPriority.LOW_PRIORITY);// if
																		// multiple
																		// people
																		// are
																		// hitting
																		// on an
																		// opponent,
																		// this
																		// prevents
																		// hits
																		// from
																		// stacking
																		// up
																		// for a
																		// long
																		// time
																		// and
																		// looking
																		// off-beat
		}
		getHitQueue().add(hit);
		if (attacker != null) {
			if (attacker.isPlayer() && isPlayer()) {
				Map<Mob, TotalDamage> damage = getCombatState().getDamageMap().getDamages();
				damage.keySet().stream().filter(a -> a instanceof NPC).forEach(damage::remove);
			}
			getCombatState().getDamageMap().incrementTotalDamage(attacker, hit.getDamage());
			if (!BoundaryManager.isWithinBoundaryNoZ(attacker.getLocation(), "ClanWarsFFAFull")
					&& !BoundaryManager.isWithinBoundaryNoZ(getLocation(), "ClanWarsFFAFull")) {
				if (!attacker.getCombatState().getDamageMap().getTotalDamages().containsKey(this) && attacker.isPlayer()
						&& isPlayer()) {
					attacker.getCombatState().setSkullTicks(100 * 10); // 10 * 1
																		// min
				}
			}
			boolean immune = false;
			if (isNPC()) {
				NPC n = (NPC) this;
				if (n.getId() == Zulrah.ID || (n.getId() >= 5867 && n.getId() <= 5869) || n instanceof Cerberus) {
					immune = true;
				}
			}
			if (hasAttribute("antiVenom+")) {
				immune = System.currentTimeMillis() - (long) getAttribute("antiVenom+", 0L) < 300000;
			}
			if (!immune) {
				boolean attackerVenomItems = false;
				boolean venomItems = false;
				for (Item equip : attacker.getEquipment().toArray()) {
					if (equip == null) {
						continue;
					}
					VenomWeapons venomWeapons = VenomWeapons.of(equip.getId());
					if (venomWeapons != null) {
						attackerVenomItems = true;
					}
				}
				for (Item equip : getEquipment().toArray()) {
					if (equip == null) {
						continue;
					}
					VenomWeapons venomWeapons = VenomWeapons.of(equip.getId());
					if (venomWeapons != null) {
						venomItems = true;
					}
				}
				if (attackerVenomItems && !venomItems) {
					if (Misc.random(5) == 0 && !hasAttribute("venom")) {
						inflictVenom();
					}
				} else if (!attackerVenomItems && venomItems) {
					if (Misc.random(5) == 0 && !attacker.hasAttribute("venom")) {
						attacker.inflictVenom();
					}
				}
			}
		}
		if (getSkills().getLevel(Skills.HITPOINTS) <= 0 && !combatState.isDead()) {
			combatState.setDead(true);
			getActionQueue().clearRemovableActions();
			resetInteractingEntity();
			getWalkingQueue().reset();
			final Mob thisMob = this;
			if (combatState.getPrayer(Prayers.RETRIBUTION)) {
				playGraphics(Graphic.create(437, 40));
				final ArrayList<Location> locationsUsed = new ArrayList<Location>();
				World.getWorld().submit(new Tickable(2) {
					@Override
					public void execute() {
						for (Mob mob : getRegion().getMobs()) {
							if (!mob.getCombatState().isDead() && mob.isInWilderness()) {
								if (combatState.getLastHitTimer() > (System.currentTimeMillis() + 4000)) { // 10
																											// cycles
																											// for
																											// tagging
																											// timer
									if (combatState.getLastHitBy() != null && mob != combatState.getLastHitBy()) {
										continue;
									}
								}
								if (mob.getCombatState().getLastHitTimer() > (System.currentTimeMillis() + 4000)) { // 10
																													// cycles
																													// for
																													// tagging
																													// timer
									if (mob.getCombatState().getLastHitBy() != null
											&& getMob() != mob.getCombatState().getLastHitBy()) {
										continue;
									}
								}
								if (mob.getLocation().isNextTo(getLocation())
										&& !locationsUsed.contains(mob.getLocation())
										&& !(mob instanceof CerberusGhost)) {
									locationsUsed.add(mob.getLocation());
									int dmg = random
											.nextInt((int) (getSkills().getLevelForExperience(Skills.PRAYER) * 0.25)); // +1
																														// as
																														// its
																														// exclusive
									mob.inflictDamage(new Hit(dmg), thisMob);
								}
							}
						}
						this.stop();
					}
				});
			}
			if (isNPC()) {
				if (attacker != null) {
					attacker.getCombatState().setLastHitBy(null);
					attacker.getCombatState().setLastHitTimer(0);
				}
			}
			World.getWorld().submit(new Tickable(2) {
				public void execute() {
					playAnimation(getDeathAnimation());
					this.stop();
				}
			});
			World.getWorld().submit(new Tickable(5) {
				public void execute() {
					playAnimation(Animation.create(-1));
					getCombatState().setCanMove(true);
					this.stop();
				}
			});
			World.getWorld().submit(new DeathTick(this, 5));
		}
	}

	/**
	 * Gets the active combat action.
	 *
	 * @return The active combat action.
	 */
	public CombatAction getActiveCombatAction() {
		return CombatFormula.getActiveCombatAction(this);
	}

	/**
	 * Checks if the mob can teleport.
	 *
	 * @return If a mob can teleport.
	 */
	public boolean canTeleport() {
		if (!combatState.canTeleport()) {
			return false;
		}
		if (this.isPlayer()) {
			Player player = (Player) this;
			if (player.getDatabaseEntity().getPlayerSettings().isTeleBlocked()) {
				getActionSender().sendMessage("A magical force stops you from teleporting.");
				return false;
			}
		}
		if (getActionSender() != null && ((Player) this).getBountyHunter() != null) {
			getActionSender().sendMessage("You can't teleport in here.");
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @return the attackDrained
	 */
	public boolean isAttackDrained() {
		return attackDrained;
	}

	/**
	 * @param attackDrained
	 *            the attackDrained to set
	 */
	public void setAttackDrained(boolean attackDrained) {
		this.attackDrained = attackDrained;
	}

	/**
	 * @return the strengthDrained
	 */
	public boolean isStrengthDrained() {
		return strengthDrained;
	}

	/**
	 * @param strengthDrained
	 *            the strengthDrained to set
	 */
	public void setStrengthDrained(boolean strengthDrained) {
		this.strengthDrained = strengthDrained;
	}

	/**
	 * @return the defenceDrained
	 */
	public boolean isDefenceDrained() {
		return defenceDrained;
	}

	/**
	 * @param defenceDrained
	 *            the defenceDrained to set
	 */
	public void setDefenceDrained(boolean defenceDrained) {
		this.defenceDrained = defenceDrained;
	}

	/**
	 * Adds an action that requires to be within distance to it.
	 *
	 * @param distance
	 *            The distance.
	 * @param width
	 *            The width.
	 * @param height
	 *            The height.
	 * @param location
	 *            The location.
	 * @param action
	 *            The action.
	 */
	public void addCoordinateAction(int width, int height, Location location, int otherWidth, int otherHeight,
			int distance, Action action) {
		actionQueue.clearAllActions();
		/*
		 * if (getLocation().isWithinDistance(width, height, location, otherWidth,
		 * otherHeight, distance)) { actionQueue.addAction(action); } else {
		 */
		actionQueue.addAction(
				new CoordinateAction(this, width, height, location, otherWidth, otherHeight, distance, action));
		// }
	}

	public void addCoordinateAction(int width, int height, Location location, int otherWidth, int otherHeight,
			int distance, Action action, int id) {
		actionQueue.clearAllActions();
		/*
		 * if (getLocation().isWithinDistance(width, height, location, otherWidth,
		 * otherHeight, distance)) { actionQueue.addAction(action); } else {
		 */
		actionQueue.addAction(
				new CoordinateAction(this, width, height, location, otherWidth, otherHeight, distance, action, id));
		// }
	}

	public void addCoordinateAction(int width, int height, Location location, int otherWidth, int otherHeight,
			int distance, Action action, Fishing fishing) {
		actionQueue.clearAllActions();
		/*
		 * if (getLocation().isWithinDistance(width, height, location, otherWidth,
		 * otherHeight, distance)) { actionQueue.addAction(action); } else {
		 */
		actionQueue.addAction(new CoordinateAction(this, width, height, location, otherWidth, otherHeight, distance,
				action, fishing));
		// }
	}

	/**
	 * Removes an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T>
	 *            The type of the value.
	 * @param key
	 *            The key.
	 * @return The old value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T removeInterfaceAttribute(String key) {
		return (T) interfaceAttributes.remove(key);
	}

	/**
	 * Removes an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T>
	 *            The type of the value.
	 * @param key
	 *            The key.
	 * @return The old value.
	 */
	public void removeAllInterfaceAttributes() {
		if (interfaceAttributes != null && interfaceAttributes.size() > 0 && interfaceAttributes.keySet().size() > 0)
			interfaceAttributes = new HashMap<String, Object>();
	}

	/**
	 * Sets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T>
	 *            The type of the value.
	 * @param key
	 *            The key.
	 * @param value
	 *            The value.
	 * @return The old value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T setInterfaceAttribute(String key, T value) {
		return (T) interfaceAttributes.put(key, value);
	}

	/**
	 * Gets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T>
	 *            The type of the value.
	 * @param key
	 *            The key.
	 * @return The value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getInterfaceAttribute(String key) {
		return (T) interfaceAttributes.get(key);
	}

	/**
	 * Gets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T>
	 *            The type of the value.
	 * @param key
	 *            The key.
	 * @return The value.
	 */
	public Map<String, Object> getInterfaceAttributes() {
		return interfaceAttributes;
	}

	/**
	 * Removes an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T>
	 *            The type of the value.
	 * @param key
	 *            The key.
	 * @return The old value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T removeAttribute(String key) {
		return (T) attributes.remove(key);
	}

	/**
	 * Removes an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T>
	 *            The type of the value.
	 * @param key
	 *            The key.
	 * @return The old value.
	 */
	public void removeAllAttributes() {
		if (attributes != null && attributes.size() > 0 && attributes.keySet().size() > 0) {
			attributes = new HashMap<String, Object>();
		}
	}

	/**
	 * Sets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T>
	 *            The type of the value.
	 * @param key
	 *            The key.
	 * @param value
	 *            The value.
	 * @return The old value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T setAttribute(String key, T value) {
		return (T) attributes.put(key, value);
	}

	/**
	 * Gets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T>
	 *            The type of the value.
	 * @param key
	 *            The key.
	 * @return The value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		return (T) attributes.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key, Object fail) {
		if (attributes.containsKey(key))
			return (T) attributes.get(key);
		else
			return (T) fail;
	}

	/**
	 * Gets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 *
	 * @param <T>
	 *            The type of the value.
	 * @param key
	 *            The key.
	 * @return The value.
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	/**
	 * Sends the mob a sound, if they have an action sender.
	 *
	 * @param id
	 *            The id.
	 * @param volume
	 *            The volume.
	 * @param delay
	 *            The delay.
	 */
	public void playSound(Sound sound) {
		if (getActionSender() != null) {
			getActionSender().playSound(sound);
		}
	}

	public Location getLastLocation() {
		if (lastLocation == null)
			return getLocation();
		return lastLocation;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	private boolean canDamaged = true;

	public boolean canBeDamaged() {
		return canDamaged;
	}

	public void setCanBeDamaged(boolean b) {
		canDamaged = b;
	}

	public boolean canAttack = false;

	public boolean isInWilderness() {
		if (isPlayer() && World.getWorld().getType() == WorldType.DEADMAN_MODE)
			return !Server.getInjector().getInstance(DeadmanService.class).inSafeZone((Player) this);

		return (!(getLocation().getX() >= 3187 && getLocation().getX() <= 3194 && getLocation().getY() >= 3958
				&& getLocation().getY() <= 3962)
				&& (getLocation().getX() >= 2941 && getLocation().getX() <= 3392 && getLocation().getY() >= 3525
						&& getLocation().getY() <= 3967)
				|| (getLocation().getX() >= 2941 && getLocation().getX() <= 3392 && getLocation().getY() >= 9918
						&& getLocation().getY() <= 10366));
	}

	public boolean inMulti() {// TODO CONVERT THIS TO BOUNDARIES
		int absX = getLocation().getX();
		int absY = getLocation().getY();
		if (BoundaryManager.isWithinBoundaryNoZ(getLocation(), "MultiCombat_Bandos")
				|| BoundaryManager.isWithinBoundaryNoZ(getLocation(), "MultiCombat_Zammy")
				|| BoundaryManager.isWithinBoundaryNoZ(getLocation(), "MultiCombat_Armadyl")
				|| BoundaryManager.isWithinBoundaryNoZ(getLocation(), "MultiCombat_Bandos")
				|| BoundaryManager.isWithinBoundaryNoZ(getLocation(), "MultiCombat_Sara")
				|| BoundaryManager.isWithinBoundaryNoZ(getLocation(), "RFD"))
			return true;

		if ((absX >= 3264 && absX <= 3381 && absY >= 4777 && absY <= 4863)
				|| (absX >= 2936 && absX <= 3062 && absY >= 3309 && absY <= 3394)
				|| (absX >= 2502 && absX <= 2530 && absY >= 3024 && absY <= 3059)
				|| (absX >= 2323 && absX <= 2369 && absY >= 3686 && absY <= 3715)
				|| (absX >= 3136 && absX <= 3327 && absY >= 3519 && absY <= 3607)
				|| (absX >= 3190 && absX <= 3327 && absY >= 3648 && absY <= 3839)
				|| (absX >= 3200 && absX <= 3390 && absY >= 3840 && absY <= 3967)
				|| (absX >= 2992 && absX <= 3007 && absY >= 3912 && absY <= 3967)
				|| (absX >= 2946 && absX <= 2959 && absY >= 3816 && absY <= 3831)
				|| (absX >= 3008 && absX <= 3199 && absY >= 3856 && absY <= 3903)
				|| (absX >= 2824 && absX <= 2944 && absY >= 5258 && absY <= 5369)
				|| (absX >= 3008 && absX <= 3071 && absY >= 3600 && absY <= 3711)
				|| (absX >= 3072 && absX <= 3327 && absY >= 3608 && absY <= 3647)
				|| (absX >= 2624 && absX <= 2690 && absY >= 2550 && absY <= 2619)
				|| (absX >= 2371 && absX <= 2422 && absY >= 5062 && absY <= 5117)
				|| (absX >= 2896 && absX <= 2927 && absY >= 3595 && absY <= 3630)
				|| (absX >= 2892 && absX <= 2932 && absY >= 4435 && absY <= 4464)
				|| (absX >= 2256 && absX <= 2287 && absY >= 4680 && absY <= 4711)
				|| (absX >= 2250 && absX <= 2290 && absY >= 3060 && absY <= 3085)
				|| (absX >= 1791 && absX <= 1665 && absY >= 3505 && absY <= 3471)
				|| (absX >= 2812 && absX <= 2793 && absY >= 9189 && absY <= 9209)
				|| (absX >= 2783 && absX <= 2808 && absY >= 2802 && absY <= 2770)
				|| (absX >= 2654 && absX <= 2690 && absY >= 3702 && absY <= 3740)) {
			return true;
		}
		return false;
	}

	private Map<String, Tickable> ticks = new HashMap<String, Tickable>();

	public void submitTick(String identifier, Tickable tick, boolean replace) {
		if (ticks.containsKey(identifier) && !replace)
			return;
		ticks.put(identifier, tick);
	}

	public void submitTick(String identifier, Tickable tick) {
		submitTick(identifier, tick, false);
	}

	public void removeTick(String identifier) {
		Tickable tick = ticks.get(identifier);
		if (tick != null) {
			tick.stop();
			ticks.remove(identifier);
		}
	}

	public Tickable getTick(String identifier) {
		return ticks.get(identifier);
	}

	public boolean hasTick(String string) {
		return ticks.containsKey(string);
	}

	/**
	 * This method IS used, don't remove it lmao
	 * Processes a mob every tick
	 */
	public void processTicks() {
		if (ticks.isEmpty())
			return;
		
		Map<String, Tickable> ticks = new HashMap<String, Tickable>(this.ticks);

		for (Iterator<Map.Entry<String, Tickable>> itr = ticks.entrySet().iterator(); itr.hasNext();) {
			Map.Entry<String, Tickable> entry = itr.next();
			if (entry.getKey().equals("following_mob"))
				continue;
			if (!entry.getValue().run())
				this.ticks.remove(entry.getKey());
		}
	}

	public void resetBarrows() {
		if (hasAttribute("currentlyFightingBrother")) {
			NPC n = (NPC) getAttribute("currentlyFightingBrother");
			if (n != null) {
				World.getWorld().unregister(n);
				removeAttribute("currentlyFightingBrother");
			}
		}
	}

	public long getLastRunRecovery() {
		return lastRunRecovery;
	}

	public void setLastRunRecovery(long lastRunRecovery) {
		this.lastRunRecovery = lastRunRecovery;
	}

	public double getAgilityRunRestore() {
		return 2260 - (this.getSkills().getLevelForExperience(Skills.AGILITY) * 10);
	}

	// kk just thought of good solution for this.
	private Container smithingInterface = new Container(Container.Type.NEVER_STACK, 29);

	public Container getSmithingInterface() {
		return smithingInterface;
	}

	public boolean hasAttribute(String string) {
		return attributes.containsKey(string);
	}

	public enum VenomWeapons {

		TOXIC_BLOW_PIPE(12926),

		TOXIC_STAFF_OF_THE_DEAD(12904),

		TRIDENT_OF_THE_SWAMP(12899),

		SERPENTINE_HELMET(12931),

		TANZANITE_HELMET(13197),

		MAGMA_HELM(13199);

		private int id;

		VenomWeapons(int id) {
			this.id = id;
		}

		private static Map<Integer, VenomWeapons> venomItemsMap = new HashMap<>();

		public static VenomWeapons of(int id) {
			return venomItemsMap.get(id);
		}

		static {
			for (VenomWeapons zulrahItem : VenomWeapons.values()) {
				venomItemsMap.put(zulrahItem.getId(), zulrahItem);
			}
		}

		public int getId() {
			return id;
		}
	}
}
