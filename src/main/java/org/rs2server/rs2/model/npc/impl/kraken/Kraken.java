package org.rs2server.rs2.model.npc.impl.kraken;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.ClueScrollType;
import org.rs2server.rs2.domain.service.impl.content.KrakenServiceImpl;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.npc.NPCLoot;
import org.rs2server.rs2.model.npc.NPCLootTable;
import org.rs2server.rs2.model.npc.Pet;
import org.rs2server.rs2.model.npc.impl.CombatNpc;
import org.rs2server.rs2.model.npc.impl.IdleCombatState;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.AltarAction.BoneType;
import org.rs2server.rs2.mysql.impl.NewsManager;
import org.rs2server.rs2.util.Misc;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Clank1337
 */
public final class Kraken extends CombatNpc<Kraken> {

	private final Set<Whirlpool> whirlpools;
	private final Set<Whirlpool> disturbedWhirlpools;

	private static final Animation BLOCK_ANIMATION = Animation.create(3990);
	private static final Animation DEATH_ANIMATION = Animation.create(3993);

	public static final int MAX_HEALTH = 255;
	private Player challenger;

	private static final int[] BONUSES = new int[] { 0, 0, 0, 25, 0, 0, 0, 0, 130, 15000, 0, 0, 0, };

	public Kraken(Player challenger, int id, Location loc) {
		super(id, loc);
		this.challenger = challenger;
		this.setAttackable(false);

		this.getSkills().setLevel(Skills.ATTACK, 1);
		this.getSkills().setLevel(Skills.DEFENCE, 2);
		this.getSkills().setLevel(Skills.STRENGTH, 1);
		this.getSkills().setLevel(Skills.HITPOINTS, MAX_HEALTH);
		this.getSkills().setLevel(Skills.MAGIC, 6);
		this.getCombatState().setBonuses(BONUSES);
		this.getCombatState().calculateBonuses();

		this.whirlpools = new HashSet<>();
		this.disturbedWhirlpools = new HashSet<>();

		for (Location pool : KrakenServiceImpl.TENTACLE_LOCATIONS) {
			whirlpools.add(new Whirlpool(this, KrakenServiceImpl.WHIRPOOL, pool));
		}

		whirlpools.forEach(i -> {
			World.getWorld().createNPC(i);
			i.instancedPlayer = challenger;
			challenger.getInstancedNPCs().add(i);
		});
		transformNPC(KrakenServiceImpl.WHIRLPOOL_LARGE);
		transition(new IdleCombatState<>(this));
	}

	@Override
	public void dropLoot(Mob killer) {
		getDisturbedWhirlpools().clear();
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
					continue;
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
							+ " has just received " + item.getCount() + "x " + def.getName() + " from Kraken.");

					new Thread(new NewsManager(challenger, "<img src=\"../resources/news/universal_drop.png\" "
							+ "width=13> " + "received " + def.getName() + " as drop.")).start();

				} else
					challenger.getActionSender().sendMessage("<col=00ff00><shad=000000>" + challenger.getName()
							+ " received a drop: " + item.getCount() + " x " + def.getName());

				World.getWorld().createGroundItem(g, challenger);
			}
		}

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
		return getCombatState().getAttackDelay() == 0 && getTransformId() == KrakenServiceImpl.KRAKEN;
	}

	public Set<Whirlpool> getDisturbedWhirlpools() {
		return disturbedWhirlpools;
	}

	public void destroySelf() {
		disturbedWhirlpools.clear();
		whirlpools.forEach(i -> {
			challenger.getInstancedNPCs().remove(i);
			World.getWorld().unregister(i);
		});

		challenger.getInstancedNPCs().forEach(World.getWorld()::unregister);
		challenger.getInstancedNPCs().remove(this);
	}

	public Set<Whirlpool> getWhirlPools() {
		return whirlpools;
	}
}
