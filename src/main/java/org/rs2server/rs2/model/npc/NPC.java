package org.rs2server.rs2.model.npc;

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
import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.cache.format.CacheNPCDefinition;
import org.rs2server.rs2.content.api.GamePlayerNPCKillEvent;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.ClueScrollType;
import org.rs2server.rs2.domain.service.api.GroundItemService;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.impl.content.ItemServiceImpl;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.CombatNPCDefinition.Skill;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.boundary.Boundary;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.Spell;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.minigame.impl.WarriorsGuild;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.player.PrivateChat;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.model.skills.AltarAction.BoneType;
import org.rs2server.rs2.mysql.impl.NewsManager;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.*;

/**
 * <p>
 * Represents a non-player character in the in-game world.
 * </p>
 *
 * @author Graham Edgecombe
 */
public class NPC extends Mob {

	/**
	 * The definition.
	 */
	// private NPCDefinition definition;

	private CacheNPCDefinition definition;

	/**
	 * The combat definition.
	 */
	private CombatNPCDefinition combatDefinition = null;

	/**
	 * The npc's skill levels.
	 */
	private final Skills skills = new Skills(this);

	/**
	 * The minimum coordinate for this npc.
	 */
	private Location minLocation;

	/**
	 * The maximum coordinate for this npc.
	 */
	private Location maxLocation;

	/**
	 * The spawn coordinate for this npc.
	 */
	private Location spawnLocation;

	/**
	 * The spawn direction for this npc.
	 */
	private int spawnDirection;

	/**
	 * The combat cooldown delay.
	 */
	private int combatCooldownDelay = 4;

	private int id;

	private int transformId;

	public Player instancedPlayer;

	public Player owner;
	private boolean attackable;

	/**
	 * Creates the NPC with the specified definition.
	 */
	public NPC(NPCDefinition definition, int id, Location spawnLocation, Location minLocation, Location maxLocation,
			int direction) {
		// this.definition = definition;
		this.minLocation = minLocation;
		this.maxLocation = maxLocation;
		this.spawnLocation = spawnLocation;
		this.spawnDirection = direction;
		this.setDirection(direction);
		this.id = id;
		this.definition = CacheNPCDefinition.get(id);
	}

	public NPC(int id, Location location) {
		this(id, location, null);
	}

	public NPC(int id, Location location, CombatNPCDefinition definition) {
		this(id, location, location, location, 0);
		this.combatDefinition = definition;
	}

	public NPC(int id, Location spawnLocation, Location minLocation, Location maxLocation, int direction) {
		// this.definition = definition;
		this.minLocation = minLocation;
		this.maxLocation = maxLocation;
		this.spawnLocation = spawnLocation;
		this.spawnDirection = direction;
		this.setDirection(direction);
		this.setLocation(spawnLocation);
		this.id = id;
		this.definition = CacheNPCDefinition.get(id);
		this.attackable = true;
	}

	/**
	 * Creates the NPC with the specified definition.
	 */
	public NPC(NPCDefinition definition, Location spawnLocation, Location minLocation, Location maxLocation,
			int direction) {
		// this.definition = definition;
		this.minLocation = minLocation;
		this.maxLocation = maxLocation;
		this.spawnLocation = spawnLocation;
		this.spawnDirection = direction;
		this.setDirection(direction);
		this.definition = CacheNPCDefinition.get(id);
	}

	public int getId() {
		return id;
	}

	public CacheNPCDefinition getDefinition() {
		return definition;
	}

	/*	*//**
			 * Gets the NPC definition.
			 * 
			 * @return The NPC definition.
			 *//*
				 * public NPCDefinition getDefinition() { return definition; }
				 */

	/**
	 * Gets the NPC combat definition.
	 *
	 * @return The NPC combat definition.
	 */
	public CombatNPCDefinition getCombatDefinition() {
		return combatDefinition;
	}

	/**
	 * @param combatDefinition
	 *            the combatDefinition to set
	 */
	public void setCombatDefinition(CombatNPCDefinition combatDefinition) {
		this.combatDefinition = combatDefinition;
	}

	/**
	 * @return the minLocation
	 */
	public Location getMinLocation() {
		return minLocation;
	}

	/**
	 * @return the maxLocation
	 */
	public Location getMaxLocation() {
		return maxLocation;
	}

	public boolean canMove() {
		return minLocation != null && maxLocation != null
				&& !(minLocation == spawnLocation && maxLocation == spawnLocation);
	}

	/**
	 * @return the spawnLocation
	 */
	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public void setSpawnLocation(Location location) {
		this.spawnLocation = location;
	}

	public int getSpawnDirection() {
		return spawnDirection;
	}

	@Override
	public void addToRegion(Region region) {
		region.addNpc(this);
		region.addMob(this);
	}

	@Override
	public void removeFromRegion(Region region) {
		region.removeNpc(this);
		region.removeMob(this);
	}

	@Override
	public int getClientIndex() {
		return this.getIndex();
	}

	@Override
	public Skills getSkills() {
		return skills;
	}

	@Override
	public int getHeight() {
		return definition.occupiedTiles;// definition.getSize();
	}

	@Override
	public int getWidth() {
		return definition.occupiedTiles;// definition.getSize();
	}

	@Override
	public ActionSender getActionSender() {
		return null;
	}

	@Override
	public InterfaceState getInterfaceState() {
		return null;
	}

	@Override
	public Container getInventory() {
		return null;
	}

	@Override
	public boolean isNPC() {
		return true;
	}

	@Override
	public boolean isPlayer() {
		return false;
	}

	@Override
	public void register() {
		World.getWorld().register(this);
	}

	@Override
	public void unregister() {
		World.getWorld().unregister(this);
	}

	@Override
	public int getCombatCooldownDelay() {
		return combatCooldownDelay;
	}

	/**
	 * @param combatCooldownDelay
	 *            the combatCooldownDelay to set
	 */
	public void setCombatCooldownDelay(int combatCooldownDelay) {
		this.combatCooldownDelay = combatCooldownDelay;
	}

	@Override
	public CombatAction getDefaultCombatAction() {
		if (combatDefinition == null) {
			System.out.println("NULL DEF for " + getId());
		}
		return combatDefinition.getCombatAction();
	}

	@Override
	public Location getCentreLocation() {
		return Location.create(getLocation().getX() + getWidth() / 2, getLocation().getY() + getHeight() / 2,
				getLocation().getPlane());
	}

	@Override
	public boolean canHit(Mob victim, boolean messages) {
		if (victim.isPlayer()) {
			if (instancedPlayer != null && instancedPlayer != victim
					|| getInstancedPlayer() != null && getInstancedPlayer() != (Player) victim) {
				return false;
			}
		}
		return combatDefinition != null;
	}

	@Override
	public boolean isAutoRetaliating() {
		return true;
	}

	@Override
	public int getProjectileLockonIndex() {
		return getIndex() + 1;
	}

	@Override
	public double getProtectionPrayerModifier() {
		return 0; // * 0 to remove the entire hit
	}

	@Override
	public String getDefinedName() {
		return definition.getName();
	}

	@Override
	public String getUndefinedName() {
		return "";// rip
	}

	@Override
	public Animation getAttackAnimation() {
		return combatDefinition.getAttack();
	}

	@Override
	public Animation getDeathAnimation() {
		return combatDefinition.getDeath();
	}

	@Override
	public Animation getDefendAnimation() {
		return combatDefinition.getDefend();
	}

	@Override
	public Spell getAutocastSpell() {
		return combatDefinition.getSpell();
	}

	@Override
	public void setAutocastSpell(Spell spell) {
		if (spell != null) {
			combatDefinition.setSpell(spell);
		}
	}

	@Override
	public void setDefaultAnimations() {
	}

	@Override
	public void dropLoot(final Mob mob) {
		if (!mob.isPlayer())
			return;
		Player player = (Player) mob;
		final GroundItemService groundItemService = Server.getInjector().getInstance(GroundItemService.class);
		final double chance = ItemServiceImpl.handleRingOfWealth(player);

		if (this.getInstancedPlayer() == null || this.instancedPlayer == null) {

			if (Misc.random(50) <= chance) {
				groundItemService.createGroundItem(player,
						new GroundItemService.GroundItem(new Item(405, 1), getCentreLocation(), player, false));
				player.sendMessage("<col=ff0000><shad=000000>You've received a Casket drop from "
						+ getDefinition().getName() + ".");
			}

			for (final NPCLoot loot : NPCLootTable.forID(this).getGeneratedLoot(chance)) {

				if (player.getInventory().contains(13116) && loot != null) {
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

				if (ItemServiceImpl.handleRingOfWealth(player) > 1.0 && loot != null) {
					final Item item = new Item(loot.getItemID(), Misc.random(loot.getMinAmount(), loot.getMaxAmount()));
					if (item.getId() == 995 && player.getInventory().hasRoomFor(item)) {
						player.getInventory().add(item);
						continue;
					}
				}

				// Skip clue scroll drop if player already has one.
				if (loot != null) {
					for (ClueScrollType clueScroll : ClueScrollType.values()) {
						if (loot.getItemID() == clueScroll.getClueScrollItemId()) {
							if (player.getItemService().playerOwnsItem(player, clueScroll.getClueScrollItemId()))
								return;
						}
					}
				}

				if (loot != null) {
					final Item item = new Item(loot.getItemID(), Misc.random(loot.getMinAmount(), loot.getMaxAmount()));

					HookService hookService = Server.getInjector().getInstance(HookService.class);
					hookService.post(new GamePlayerNPCKillEvent(player, getDefinedName(), item));
					Pet.Pets pets = Pet.Pets.from(item.getId());
					if (pets != null) {
						Pet.givePet(player, item);
						continue;
					}

					CacheItemDefinition def = CacheItemDefinition.get(item.getId());
					if (def == null)
						continue;
					if (item.getPrice() > 500000) {

						World.getWorld()
								.sendWorldMessage("<col=FF0000><img=33>Server</col>: " + player.getName()
										+ " has just received " + item.getCount() + " x " + def.getName() + " from "
										+ getDefinedName() + ".");

						new Thread(new NewsManager(player,
								"<img src='../resources/news/universal_drop.png' width=13> received "
										+ item.getDefinition2().getName() + " as drop from " + getDefinedName() + "."))
												.start();
					}

					groundItemService.createGroundItem(player,
							new GroundItemService.GroundItem(item, getCentreLocation(), player, false));
				}
			}
		}
	}

	public void setTransformId(int transformId) {
		this.transformId = transformId;
	}

	public boolean isBossNPC(int id) {
		return Bosses.of(id) != null;
	}

	public Player getLooters(Player player, int id, int amount, PrivateChat clan) {
		Player done = player;
		if (clan != null) {
			if (clan.getLootRank().getId() > -1) {
				List<Player> playersGetLoot = new LinkedList<Player>();
				int best = 0;
				for (Player pl : getRegion().getPlayers()) { // TODO
																// Region.getLocalPlayers(location,
																// 16)) {
					if (pl.getLocation().getDistance(getLocation()) > 16 || !clan.canShareLoot(pl)) {
						continue;
					}
					if (clan.getMembers().contains(pl)) {
						if (!getCombatState().getDamageMap().getDamages().containsKey(pl)) {
							continue;
						}
						int damage = getCombatState().getDamageMap().getDamages().get(pl).getDamage()
								+ pl.getSettings().getChances();
						if (damage > best) {
							playersGetLoot.add(pl);
							best = damage;
						} else {
							if (Misc.random(2) == 1) {
								playersGetLoot.add(pl);
							}
						}
						pl.getSettings().incChances();
						if (pl.getSettings().getChances() < 0) {
							pl.getSettings().setChances(0);
						}
					}
				}
				for (Player pl : playersGetLoot) {
					if (Misc.random(2) == 1) {
						done = pl;
						for (int i = 0; i < 5; i++) {
							pl.getSettings().decChances();
						}
						break;
					}
				}
			}
		}
		if (clan != null && clan.getLootRank().getId() > -1) {
			done.getActionSender()
					.sendMessage("<col=009900>You received: " + amount + " " + ItemDefinition.forId(id).getName());
			for (final Player pl : getRegion().getPlayers()) { // TODO
																// Region.getLocalPlayers(location,
																// 16)) {
				if (pl.getLocation().getDistance(getLocation()) > 16 || !clan.canShareLoot(pl)) {
					continue;
				}
				if (pl.getIndex() == done.getIndex()) {
					continue;
				}
				if (clan.getMembers().contains(pl)) {
					pl.getActionSender().sendMessage(
							done.getName() + " received: " + amount + " " + ItemDefinition.forId(id).getName());
					World.getWorld().submit(new Tickable(6) {
						@Override
						public void execute() {
							pl.getActionSender().sendMessage("Your chance of receiving loot has improved.");
							this.stop();

						}
					});
				}
			}
		}
		return done;
	}

	@Override
	public boolean isObject() {
		return false;
	}

	@Override
	public Graphic getDrawbackGraphic() {
		return combatDefinition.getDrawbackGraphic();
	}

	@Override
	public int getProjectileId() {
		return combatDefinition.getProjectileId();
	}

	public int getTransformId() {
		return transformId;
	}

	public void transformNPC(int id) {
		// this.definition = NPCDefinition.forId(id);
		this.combatDefinition = CombatNPCDefinition.of(id);
		this.transformId = id;

		this.getUpdateFlags().flag(UpdateFlag.TRANSFORM);
	}

	@Override
	public void tick() {
		if (this.getId() == 2462 && Misc.random(40) < 2)
			this.forceChat(WarriorsGuild.SHANOMI_QUOTES[Misc.random(WarriorsGuild.SHANOMI_QUOTES.length - 1)]);
	}

	private int aggDistance = 5;

	public int getAggressiveDistance() {
		return aggDistance;
	}

	public void setAggressiveDistance(int distance) {
		this.aggDistance = distance;
	}

	public int spawnedAssistants = 0;

	public int completedRapidAttacks = 0;

	private Boundary area;

	public void setHomeArea(Boundary boundary) {
		this.area = boundary;
	}

	public Boundary getHomeArea() {
		return area;
	}

	public boolean isAttackable() {
		return attackable;
	}

	public void setAttackable(boolean attackable) {
		this.attackable = attackable;
	}

	public void setInstancedPlayer(Player player) {
		this.owner = player;
	}

	public Player getInstancedPlayer() {
		return owner;
	}

	public boolean isCaveNPC() {
		return getCombatDefinition() != null && getCombatDefinition().isFightCavesNPC();
	}

	public void loadCombatDefinition() {
		this.setLocation(this.getSpawnLocation());
		CombatNPCDefinition combatDefinition = CombatNPCDefinition.of(this.getDefinition().getId());
		if (combatDefinition != null) {
			this.setCombatDefinition(combatDefinition);
			this.setCombatCooldownDelay(combatDefinition.getCombatCooldownDelay());
			for (Skill skill : combatDefinition.getSkills().keySet()) {
				this.getSkills().setSkill(skill.getId(), combatDefinition.getSkills().get(skill),
						this.getSkills().getExperienceForLevel(combatDefinition.getSkills().get(skill)));
			}
			this.getCombatState().setCombatStyle(combatDefinition.getCombatStyle());
			this.getCombatState().setAttackType(combatDefinition.getAttackType());
			this.getCombatState().setBonuses(combatDefinition.getBonuses());
		}
	}

	public int getSize() {
		return getDefinition().getOccupiedTiles();
	}

	public enum Bosses {

		GENERAL_GRAARDOR(2215),

		KREE_ARRA(3162),

		COMMANDER_ZILYANA(2205),

		KRIL_TSUTSAROTH(3129),

		CHAOS_ELEMENTAL(2054),

		ZULRAH(2042),

		DAGANNOTH_REX(2267),

		DAGANNOTH_SUPREME(2265),

		DAGANNOTH_PRIME(2266),

		KING_BLACK_DRAGON(239),

		VENENATIS(6610),

		CHAOS_FANATIC(6619),

		TZTOK_JAD(3127),

		KRAKEN(494),

		SMOKE_DEVIL(499),

		LIZARDMAN_SHAMAN(6766),

		CERBERUS(5862),

		CALLISTO(6609),

		VETION(6611),

		VETION_REBORN(6612),

		SCORPIA(6615),

		CRAZY_ARCHAEOLOGIST(6618),

		CORPOREAL_BEAST(319),

		DEMONIC_GORILLA(7147),

		SKOTIZO(7286),

		ABYSSAL_SIRE(5890);

		private int npcId;

		Bosses(int _npcId) {
			this.npcId = _npcId;
		}

		private static Map<Integer, Bosses> bosses = new HashMap<>();

		static {
			for (Bosses boss : Bosses.values()) {
				bosses.put(boss.getNpcId(), boss);
			}
		}

		public static Bosses of(int npcId) {
			return bosses.get(npcId);
		}

		private int getNpcId() {
			return npcId;
		}
	}
}
