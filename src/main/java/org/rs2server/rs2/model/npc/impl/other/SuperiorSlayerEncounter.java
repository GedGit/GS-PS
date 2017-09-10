package org.rs2server.rs2.model.npc.impl.other;

import java.util.Arrays;
import java.util.Optional;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.cache.format.CacheNPCDefinition;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.ClueScrollType;
import org.rs2server.rs2.domain.service.impl.content.ItemServiceImpl;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.npc.*;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.AltarAction.BoneType;
import org.rs2server.rs2.mysql.impl.NewsManager;
import org.rs2server.rs2.util.Misc;

/**
 * Handles the superior slayer encounter NPC.
 * 
 * @author Vichy
 */
public final class SuperiorSlayerEncounter extends NPC {

	/**
	 * Defining the time millis npc was spawned at.
	 */
	long spawnedTime;
	
	/**
	 * Defining the player we're spawning the superior for.
	 */
	private Player player;

	/**
	 * Lets build the NPC and make it instanced to the player.
	 * 
	 * @param player
	 *            the player encountering the npc
	 * @param id
	 *            the npc id to spawn
	 * @param loc
	 *            the location to spawn on
	 */
	public SuperiorSlayerEncounter(Player player, int id, Location loc) {
		super(id, loc);
		this.player = player;
		this.spawnedTime = System.currentTimeMillis() + 300000; // 5 minutes
		this.face(player.getLocation());
	}

	@Override
	public void dropLoot(Mob killer) {

		// The chance increase on drop rolling
		double chance = ItemServiceImpl.handleRingOfWealth(player) + 15.0;

		// Loop through npc's drop table to determine whether we're getting anything
		// with our current chance
		for (final NPCLoot loot : NPCLootTable.forID(this).getGeneratedLoot(chance)) {

			// Handle bonecrusher if bone drop
			if (player.getInventory().contains(13116)) {
				int charges = player.getItemService().getCharges(player, new Item(13116));
				BoneType type = BoneType.forId(loot.getItemID());
				if (charges > 0 && type != null) {
					player.getItemService().setCharges(player, new Item(13116), charges - 1);
					charges = player.getItemService().getCharges(player, new Item(13116));
					double exp = type.getXp() / 2;
					if (player.hasItem(new Item(13115))) // morytania legs 4 make it full exp
						exp *= 2;
					player.getSkills().addExperience(Skills.PRAYER, exp);
					if (charges < 1)
						player.sendMessage("<col=ff0000>Your Bonecrusher has just used its last charge.");
					continue;
				}
			}

			// Skip clue scroll drop if player already has one.
			for (ClueScrollType clueScroll : ClueScrollType.values()) {
				if (loot.getItemID() == clueScroll.getClueScrollItemId()) {
					if (player.getItemService().playerOwnsItem(player,
							clueScroll.getClueScrollItemId()))
						return;
				}
			}

			// If we rolled on an item or not
			if (loot != null) {
				final Item item = new Item(loot.getItemID(), Misc.random(loot.getMinAmount(), loot.getMaxAmount()));

				// If the rolled item is a pet
				Pet.Pets pets = Pet.Pets.from(item.getId());
				if (pets != null) {
					Pet.givePet(player, item);
					continue;
				}

				// Why make seperate droptables when we can just do this lil trick
				if (Misc.random(200) == 1) {
					item.setCount(1);
					int id = Misc.random(3) == 1 ? 20724 : Misc.random(2) == 1 ? 20730 : 20736;
					item.setId(id);
				}

				GroundItemDefinition g = new GroundItemDefinition(player.getName(), this.getCentreLocation(),
						item.getId(), item.getCount());
				CacheItemDefinition def = CacheItemDefinition.get(loot.getItemID());

				// Check the rolled items price and announce it if it's expensive
				if (item.getPrice() > 500000) {
					World.getWorld()
							.sendWorldMessage("<col=ff0000><img=33>Server</col>: " + player.getName()
									+ " has just received " + item.getCount() + "x " + def.getName() + " from "
									+ CacheNPCDefinition.get(this.getId()).getName() + ".");

					new Thread(new NewsManager(player, "<img src=\"../resources/news/universal_drop.png\" "
							+ "width=13> " + "received " + def.getName() + " as drop.")).start();

				}

				// Drops the rolled item
				World.getWorld().createGroundItem(g, player);
			}
		}

		// Awards slayer experience for killing this npc
		player.getSkills().addExperience(Skills.SLAYER,
				SuperiorEncounters.ofSuperiorId(this.getId()).get().getExp() / 2);
	}
	
	@Override
	public boolean canHit(Mob victim, boolean messages) {
		// TODO can't attack other player encounters
		return true;
	}

	@Override
	public void tick() {
		// we have despawn set to 5 minutes, wikia says its 2 mins
		if (System.currentTimeMillis() > spawnedTime) {
			unregister();
			return;
		}
		if (player == null) {
			unregister();
			return;
		}
		double distance = getLocation().distance(player.getLocation());
		if (distance >= 10)
			unregister();
	}

	/**
	 * An enum containing all super slayer encounter data.
	 * 
	 * @author Vichy
	 *
	 */
	public enum SuperiorEncounters {

		CRUSHING_HAND("Crawling hand", 7388, 550),

		CHASM_CRAWLER("Cave crawler", 7389, 600),

		SCREAMING_BANSHEE("Banshee", 7390, 610),

		GIANT_ROCKSLUG("Rock slug", 7392, 770),

		COCKATHRICE("Cockatrice", 7393, 950),

		FLAMING_PYRELORD("Pyrefiend", 7394, 1250),

		MONSTROUS_BASILISK("Basilisk", 7395, 1700),

		MALEVOLENT_MAGE("Infernal mage", 7396, 1750),

		INSATIABLE_BLOODVELD("Bloodveld", 7397, 2900),

		VITREOUS_JELLY("Jelly", 7399, 2050),

		CAVE_ABOMINATION("Cave horror", 7401, 1300),

		ABHORRENT_SPECTRE("Aberrant spectre", 7402, 2500),

		CHOKE_DEVIL("Dust devil", 7404, 3000),

		KING_KURASK("Kurask", 7405, 2767),

		MARBLE_GARGOYLE("Gargoyle", 7407, 3044),

		NECHRYARCH("Nechryael", 7411, 3068),

		GREATER_ABYSSAL_DEMON("Abyssal demon", 7410, 4200),

		NIGHT_BEAST("Dark beast", 7409, 6462),

		NUCLEAR_SMOKE_DEVIL("Smoke devil", 7406, 2400);

		private final String name;
		private final int id;
		private final double exp;

		SuperiorEncounters(String name, int id, double exp) {
			this.name = name;
			this.id = id;
			this.exp = exp;
		}

		public static Optional<SuperiorEncounters> ofSuperiorId(int npcId) {
			return Arrays.stream(SuperiorEncounters.values()).filter(i -> npcId == i.id).findFirst();
		}

		public static Optional<SuperiorEncounters> ofNpcName(String name) {
			return Arrays.stream(SuperiorEncounters.values()).filter(i -> name.equalsIgnoreCase(i.name)).findFirst();
		}

		public String getName() {
			return name;
		}

		public int getId() {
			return id;
		}

		public double getExp() {
			return exp;
		}
	}
}
