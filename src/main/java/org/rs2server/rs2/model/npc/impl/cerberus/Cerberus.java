package org.rs2server.rs2.model.npc.impl.cerberus;

import com.google.common.collect.ImmutableList;
import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.content.Following;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.ClueScrollType;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.map.Directions;
import org.rs2server.rs2.model.npc.NPCLoot;
import org.rs2server.rs2.model.npc.NPCLootTable;
import org.rs2server.rs2.model.npc.Pet;
import org.rs2server.rs2.model.npc.impl.CombatNpc;
import org.rs2server.rs2.model.npc.impl.IdleCombatState;
import org.rs2server.rs2.model.npc.impl.NpcCombatState;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.*;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.tickables.CerberusGhostRegisterTick;
import org.rs2server.rs2.model.npc.impl.cerberus.styles.*;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.AltarAction.BoneType;
import org.rs2server.rs2.mysql.impl.NewsManager;
import org.rs2server.rs2.tickable.impl.StoppingTick;
import org.rs2server.rs2.util.Misc;

import java.util.List;
import java.util.Random;

/**
 * @author Twelve
 */
public class Cerberus extends CombatNpc<Cerberus> {

	public static final int NPC_ID = 5862;

	private static final Animation BLOCK_ANIMATION = Animation.create(4489);
	private static final Animation DEATH_ANIMATION = Animation.create(4495);
	private static final Animation HOWL_ANIMATION = Animation.create(4485);

	private static final int[] BONUSES = { 220, 220, 150, 220, 220, 120, 120, 120, 190, 350, 100, 20, 0 };

	public static final int MAX_HEALTH = 600;

	private static final String GHOST_SPAWN_TEXT = "Aaarrrooooooo";
	private static final String LAVA_SPAWN_TEXT = "Grrrrrrrrrrrrrr";

	private final Player challenger;
	private List<CerberusGhost> ghosts;
	private int performedAttacks;

	private final NpcCombatState<Cerberus> magicAttackStyle;
	private final NpcCombatState<Cerberus> rangedAttackStyle;
	private final NpcCombatState<Cerberus> meleeAttackStyle;

	private boolean canSpawnGhosts = true;
	private boolean canSpawnLavaPools = true;
	private boolean canAttackPlayer;

	public Cerberus(Player challenger, Location loc) {
		super(NPC_ID, loc);
		this.challenger = challenger;
		this.magicAttackStyle = new CerberusMagicAttackStyle<>(this);
		this.rangedAttackStyle = new CerberusRangedAttackStyle<>(this);
		this.meleeAttackStyle = new CerberusMeleeAttackStyle<>(this);

		this.getSkills().setLevel(Skills.ATTACK, 220);
		this.getSkills().setLevel(Skills.DEFENCE, 100);
		this.getSkills().setLevel(Skills.STRENGTH, 220);
		this.getSkills().setLevel(Skills.RANGE, 220);
		this.getSkills().setLevel(Skills.MAGIC, 220);
		this.getSkills().setLevel(Skills.HITPOINTS, MAX_HEALTH);
		this.getCombatState().setBonuses(BONUSES);
		this.setDirection(Directions.NormalDirection.WEST.npcIntValue());

		this.random = new Random();

		setInteractingEntity(InteractionMode.ATTACK, challenger);
		transition(new IdleCombatState<>(this));
	}

	@Override
	public boolean isAutoRetaliating() {
		return false;
	}

	@Override
	public void tick() {
		if (getCombatState().isDead())
			return;
		if (!canAttackPlayer && this.getCombatState().getDamageMap().getTotalDamages().containsKey(challenger))
			canAttackPlayer = true;
		if (canAttackPlayer) {
			double distance = getLocation().distance(challenger.getLocation());
			if (distance >= 13) {
				Following.combatFollow(this, challenger);
				return;
			}
			if (performedAttacks == 0)
				transition(magicAttackStyle);
			else if (performedAttacks == 1)
				transition(rangedAttackStyle);
			else if (performedAttacks == 2) {
				transition(meleeAttackStyle);
				World.getWorld().submit(new StoppingTick(67) {
					@Override
					public void executeAndStop() {
						performedAttacks = 0;
					}
				});
			} else {

				int currentHitpoints = getSkills().getLevel(Skills.HITPOINTS);

				if (currentHitpoints <= 0)
					return;

				float lavaPoolProbability = random.nextFloat();
				if (currentHitpoints <= 200 && canSpawnLavaPools && lavaPoolProbability >= 0.95) {
					canSpawnLavaPools = false;
					forceChat(LAVA_SPAWN_TEXT);
					World.getWorld().submit(new LavaPoolTickable(this, challenger.getLocation()));
				}

				float ghostProbability = random.nextFloat();

				if (canSpawnGhosts && ghostProbability >= 0.95 && currentHitpoints <= 500 && currentHitpoints >= 250)
					spawnGhosts();
				else {
					float styleProbability = random.nextFloat();

					if (styleProbability <= 0.33)
						transition(magicAttackStyle);
					else if (styleProbability >= 0.70)
						transition(rangedAttackStyle);
					else
						transition(meleeAttackStyle);
				}
			}
		} else
			transition(new IdleCombatState<>(this));
	}

	public void spawnGhosts() {
		if (ghosts == null || !canSpawnGhosts
				|| !BoundaryManager.isWithinBoundaryNoZ(challenger.getLocation(), "Cerberus"))
			return;

		playAnimation(HOWL_ANIMATION);
		forceChat(GHOST_SPAWN_TEXT);
		ghosts.forEach(g -> {
			challenger.getInstancedNPCs().add(g);
			g.register();
		});
		canSpawnGhosts = false;
		World.getWorld().submit(new CerberusGhostRegisterTick(this));
	}

	@Override
	public final Animation getDefendAnimation() {
		return BLOCK_ANIMATION;
	}

	@Override
	public final Animation getDeathAnimation() {
		return DEATH_ANIMATION;
	}

	@Override
	public boolean isTurn() {
		return getCombatState().getAttackDelay() == 0;
	}

	public void destroySelf(boolean stop) {
		challenger.getInstancedNPCs().remove(this);
		if (stop) {
			if (ghosts != null) {
				ghosts.forEach(g -> {
					if (g != null) {
						g.unregister();
						challenger.getInstancedNPCs().remove(g);
					}
				});
			}
			ghosts = null;
			challenger.getInstancedNPCs().forEach(World.getWorld()::unregister);
			challenger.getInstancedNPCs().clear();
		}
	}

	// 2874, 9846, 0

	@Override
	public void dropLoot(Mob killer) {
		final double chance = challenger.getEquipment().get(Equipment.SLOT_RING) != null
				&& challenger.getEquipment().get(Equipment.SLOT_RING).getId() == 2572 ? 1.1 : 1.0;
		for (final NPCLoot loot : NPCLootTable.forID(this).getGeneratedLoot(chance)) {

			if (challenger.getInventory().contains(13116)) {
				int charges = challenger.getItemService().getCharges(challenger, new Item(13116));
				BoneType type = BoneType.forId(loot.getItemID());
				if (charges > 0 && type != null) {
					challenger.getItemService().setCharges(challenger, new Item(13116), charges - 1);
					charges = challenger.getItemService().getCharges(challenger, new Item(13116));
					double exp = type.getXp() / 2;
					if (challenger.hasItem(new Item(13115))) // morytania legs 4 make it full exp
						exp *= 2;
					challenger.getSkills().addExperience(Skills.PRAYER, exp);
					if (charges < 1)
						challenger.sendMessage("<col=ff0000>Your Bonecrusher has just used its last charge.");
					return;
				}
			}

			// Skip clue scroll drop if player already has one.
			for (ClueScrollType clueScroll : ClueScrollType.values()) {
				if (loot.getItemID() == clueScroll.getClueScrollItemId()) {
					if (challenger.getItemService().playerOwnsItem(challenger, clueScroll.getClueScrollItemId()))
						return;
				}
			}

			if (loot != null) {
				final Item item = new Item(loot.getItemID(), Misc.random(loot.getMinAmount(), loot.getMaxAmount()));
				Pet.Pets pets = Pet.Pets.from(item.getId());
				if (pets != null) {
					Pet.givePet(challenger, item);
					continue;
				}
				GroundItemDefinition g = new GroundItemDefinition(challenger.getName(), challenger.getLocation(),
						item.getId(), item.getCount());
				CacheItemDefinition def = CacheItemDefinition.get(loot.getItemID());
				if (item.getPrice() > 150000) {

					World.getWorld().sendWorldMessage("<col=ff0000><img=33>Server</col>: " + challenger.getName()
							+ " has just received " + item.getCount() + "x " + def.getName() + " from Cerberus.");

					new Thread(new NewsManager(challenger, "<img src=\"../resources/news/universal_drop.png\" "
							+ "width=13> " + "received " + def.getName() + " as drop.")).start();

				} else
					challenger.getActionSender().sendMessage("<col=00ff00><shad=000000>" + challenger.getName()
							+ " received a drop: " + item.getCount() + " x " + def.getName());

				World.getWorld().createGroundItem(g, challenger);
			}
		}

	}

	public void setGhosts(ImmutableList<CerberusGhost> ghosts) {
		this.ghosts = ghosts;
	}

	public List<CerberusGhost> getGhosts() {
		return ghosts;
	}

	public Player getChallenger() {
		return challenger;
	}

	public void incrementPerformedAttacks() {
		performedAttacks++;
	}

	public void setCanSpawnGhosts(boolean canSpawnGhosts) {
		this.canSpawnGhosts = canSpawnGhosts;
	}

	public void setCanSpawnLavaPools(boolean canSpawnLavaPools) {
		this.canSpawnLavaPools = canSpawnLavaPools;
	}

	public boolean canAttackPlayer() {
		return canAttackPlayer;
	}
}
