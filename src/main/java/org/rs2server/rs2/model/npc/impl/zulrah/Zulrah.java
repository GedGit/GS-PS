package org.rs2server.rs2.model.npc.impl.zulrah;

import org.joda.time.Duration;
import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.ClueScrollType;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.Attributes.AN;
import org.rs2server.rs2.model.cm.Content;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.npc.NPCLootTable;
import org.rs2server.rs2.model.npc.Pet;
import org.rs2server.rs2.model.npc.impl.CombatNpc;
import org.rs2server.rs2.model.npc.impl.NpcCombatState;
import org.rs2server.rs2.model.npc.impl.zulrah.ZulrahAttack.ZulrahAttackType;
import org.rs2server.rs2.model.npc.impl.zulrah.ZulrahBehavior.ZulrahPattern;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.AltarAction.BoneType;
import org.rs2server.rs2.mysql.impl.NewsManager;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.*;

/**
 * @author Nine
 * @author Twelve
 */
public final class Zulrah extends CombatNpc<Zulrah> {

	public static final int ID = 2042;

	private static final Animation DEATH_ANIMATION = Animation.create(5804);
	private static final Animation DEFEND_ANIMATION = Animation.create(5808);

	public static final Animation RISE_ANIMATION = Animation.create(5073);
	public static final Animation HIDE_ANIMATION = Animation.create(5072);

	public static final int MAX_HEALTH = 500;

	private int patternIndex;
	private int patternStage;
	private boolean startTransforming;

	private ZulrahBehavior.ZulrahPattern[] pattern;
	private ZulrahAttack currentAttack;
	private ZulrahPattern currentStage;

	private final ZulrahRangeState rangeState = new ZulrahRangeState(this);
	private final ZulrahMeleeState meleeState = new ZulrahMeleeState(this);
	private final ZulrahMageState mageState = new ZulrahMageState(this);

	private final Queue<ZulrahAttack> attacks = new LinkedList<>();
	private final List<NPC> activeMinions = new LinkedList<>();
	private final List<GameObject> activeVenomClouds = new LinkedList<>();

	private final Player challenger;
	// first 5 = attack bonuses
	private static final int[] RANGE_BONUSES = { 150, 150, 150, 150, 150, 200, 200, 200, -45, 50, 0, 100, 0 };
	private static final int[] MELEE_BONUSES = { 150, 150, 150, 150, 150, 200, 200, 200, 0, 300, 0, 100, 0 };
	private static final int[] MAGE_BONUSES = { 150, 150, 150, 150, 150, 200, 200, 200, 300, 0, 100, 0 };

	public Zulrah(Player challenger, Location location) {
		super(ID, location);
		super.transition(rangeState);

		this.patternIndex = random.nextInt(ZulrahBehavior.PATTERNS.length);
		this.pattern = ZulrahBehavior.PATTERNS[patternIndex];
		this.challenger = challenger;

		this.setAttackable(false);
		// this.startTransforming = true;
		this.updateAttackSequence();
		this.getSkills().setLevel(3, MAX_HEALTH);
		this.getSkills().setLevel(1, 400);
		this.face(ZulrahBehavior.SOUTH);
		this.getCombatState().setBonuses(RANGE_BONUSES);
	}

	@SuppressWarnings("rawtypes")
	private int[] getBonusesForTransition(NpcCombatState state) {
		if (state.equals(mageState)) {
			return MAGE_BONUSES;
		}
		if (state.equals(rangeState)) {
			return RANGE_BONUSES;
		}
		return MELEE_BONUSES;
	}

	@Override
	public final boolean isAutoRetaliating() {
		return false;
	}

	@Override
	public final boolean canHit(Mob victim, boolean messages) {
		return super.canHit(victim, messages) && !isTransforming() && !startTransforming;
	}

	public final Player getChallenger() {
		return challenger;
	}

	public void doNextTransition() {
		patternStage++;
		if (patternStage == pattern.length)
			patternStage = 0;
		updateAttackSequence();

		currentStage = pattern[patternStage];

		switch (currentStage.getCombatType()) {
		case MELEE:
			transition(meleeState);
			break;
		case RANGE:
			transition(rangeState);
			break;
		case MAGE:
			transition(mageState);
			break;
		}
		getCombatState().setBonuses(getBonusesForTransition(state));
		setAttackable(false);
	}

	public final void updateAttackSequence() {
		attacks.clear();
		switch (patternIndex) {
		case 0:
			switch (patternStage) {
			case 0: // Range
				for (int i = 0; i < 4; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_VENOM, 3)
							.setLocations(ZulrahBehavior.VENOM_CLOUD_LOCATIONS[i]));
				break;
			case 1: // Melee
				for (int i = 0; i < 2; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.MELEE, 8));
				break;
			case 2: // Mage
				for (int i = 0; i < 4; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.MAGE, 4));
				break;
			case 3: // Range
				for (int i = 0; i < 5; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.RANGE, 4));
				for (int i = 0; i < 2; i++) {
					attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_MINIONS, 3)
							.setLocations(new Location[] { ZulrahBehavior.VENOM_CLOUD_LOCATIONS[3][i] }));
				}
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_VENOM, 3)
						.setLocations(ZulrahBehavior.VENOM_CLOUD_LOCATIONS[1]));
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_VENOM, 3)
						.setLocations(ZulrahBehavior.VENOM_CLOUD_LOCATIONS[0]));
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_VENOM, 3)
						.setLocations(ZulrahBehavior.VENOM_CLOUD_LOCATIONS[3]));
				for (int i = 0; i < 2; i++) {
					attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_MINIONS, 3)
							.setLocations(new Location[] { ZulrahBehavior.VENOM_CLOUD_LOCATIONS[2][i] }));
				}
				break;
			case 4: // Melee
				for (int i = 0; i < 2; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.MELEE, 8));
				break;
			case 5: // Mage
				for (int i = 0; i < 5; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.MAGE, 4));
				break;
			case 6: // Range
				for (int i = 0; i < 3; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_VENOM, 3)
							.setLocations(ZulrahBehavior.VENOM_CLOUD_LOCATIONS[i]));
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_MINIONS, 3)
						.setLocations(new Location[] { ZulrahBehavior.VENOM_CLOUD_LOCATIONS[2][0] }));
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_MINIONS, 3)
						.setLocations(new Location[] { ZulrahBehavior.VENOM_CLOUD_LOCATIONS[2][0] }));
				for (int i = 0; i < 2; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_MINIONS, 3)
							.setLocations(new Location[] { ZulrahBehavior.VENOM_CLOUD_LOCATIONS[3][i] }));
				break;
			case 7:// Mage
				for (int i = 0; i < 4; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.MAGE, 4));
				break;
			case 8:// Hybrid
				attacks.add(new ZulrahAttack(ZulrahAttackType.JAD_RANGE, 4));
				attacks.add(new ZulrahAttack(ZulrahAttackType.JAD_MAGE, 4));
				attacks.add(new ZulrahAttack(ZulrahAttackType.JAD_RANGE, 4));
				attacks.add(new ZulrahAttack(ZulrahAttackType.JAD_MAGE, 4));
				break;
			}
			break;
		case 1:
			switch (patternStage) {
			case 0: // Range
				for (int i = 0; i < 4; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_VENOM, 3)
							.setLocations(ZulrahBehavior.VENOM_CLOUD_LOCATIONS[i]));
				break;
			case 1:
				for (int i = 0; i < 2; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.MELEE, 8));
				break;
			case 2:
				attacks.add(new ZulrahAttack(ZulrahAttackType.MAGE, 4));
				attacks.add(new ZulrahAttack(ZulrahAttackType.RANGE, 4));
				attacks.add(new ZulrahAttack(ZulrahAttackType.MAGE, 4));
				attacks.add(new ZulrahAttack(ZulrahAttackType.MAGE, 4));
				break;
			case 3:
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_VENOM, 3)
						.setLocations(ZulrahBehavior.VENOM_CLOUD_LOCATIONS[0]));
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_VENOM, 3)
						.setLocations(ZulrahBehavior.VENOM_CLOUD_LOCATIONS[3]));
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_VENOM, 3)
						.setLocations(ZulrahBehavior.VENOM_CLOUD_LOCATIONS[1]));
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_MINIONS, 3)
						.setLocations(new Location[] { ZulrahBehavior.VENOM_CLOUD_LOCATIONS[1][0] }));
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_MINIONS, 3)
						.setLocations(new Location[] { ZulrahBehavior.VENOM_CLOUD_LOCATIONS[1][1] }));
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_MINIONS, 3)
						.setLocations(new Location[] { ZulrahBehavior.VENOM_CLOUD_LOCATIONS[2][0] }));
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_MINIONS, 3)
						.setLocations(new Location[] { ZulrahBehavior.VENOM_CLOUD_LOCATIONS[2][1] }));
				break;
			case 4:
				attacks.add(new ZulrahAttack(ZulrahAttackType.MAGE, 4));
				attacks.add(new ZulrahAttack(ZulrahAttackType.RANGE, 4));
				attacks.add(new ZulrahAttack(ZulrahAttackType.RANGE, 4));
				attacks.add(new ZulrahAttack(ZulrahAttackType.MAGE, 4));
				attacks.add(new ZulrahAttack(ZulrahAttackType.MAGE, 4));
				for (int i = 0; i < 2; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_MINIONS, 3)
							.setLocations(new Location[] { ZulrahBehavior.VENOM_CLOUD_LOCATIONS[2][i] }));
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_VENOM, 3)
						.setLocations(ZulrahBehavior.VENOM_CLOUD_LOCATIONS[0]));
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_VENOM, 3)
						.setLocations(ZulrahBehavior.VENOM_CLOUD_LOCATIONS[1]));
				for (int i = 0; i < 2; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_MINIONS, 3)
							.setLocations(new Location[] { ZulrahBehavior.VENOM_CLOUD_LOCATIONS[3][i] }));
				break;
			case 5:
				for (int i = 0; i < 2; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.MELEE, 8));
				break;
			case 6:
				for (int i = 0; i < 4; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.RANGE, 4));
				break;
			case 7:
				attacks.add(new ZulrahAttack(ZulrahAttackType.RANGE, 4));
				for (int i = 0; i < 4; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.MAGE, 4));
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_MINIONS, 3)
						.setLocations(new Location[] { ZulrahBehavior.VENOM_CLOUD_LOCATIONS[1][0] }));
				attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_VENOM, 3)
						.setLocations(ZulrahBehavior.VENOM_CLOUD_LOCATIONS[1]));
				for (int i = 0; i < 2; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_MINIONS, 3)
							.setLocations(new Location[] { ZulrahBehavior.VENOM_CLOUD_LOCATIONS[2][i] }));
				break;
			case 8:
				for (int i = 0; i < 10; i++)
					attacks.add(new ZulrahAttack(i % 2 == 0 ? ZulrahAttackType.RANGE : ZulrahAttackType.MAGE, 4));
				for (int i = 0; i < 4; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.SPAWN_VENOM, 3)
							.setLocations(ZulrahBehavior.VENOM_CLOUD_LOCATIONS[i]));
				break;
			case 9:
				for (int i = 0; i < 2; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.MELEE, 8));
				break;
			case 10:
				for (int i = 0; i < 4; i++)
					attacks.add(new ZulrahAttack(ZulrahAttackType.RANGE, 4));
				break;
			}
			break;
		case 2:

			break;
		case 3:

			break;
		case 4:

			break;
		}
	}

	@Override
	public void transition(NpcCombatState<Zulrah> to) {
		playAnimation(Zulrah.HIDE_ANIMATION);
		resetFace();
		World.getWorld().submit(new Tickable(3) {

			private int stage;

			public void execute() {
				stage++;
				switch (stage) {
				case 1:
					// Rebuild NPC list
					setTeleportTarget(currentStage.getLocation().transform(0, 0, getPlane()));
					setTeleporting(true);
					setTickDelay(2);
					break;
				case 2:
					setAttackable(true);

					transformNPC(to.getId());

					Location base = getLocation();
					if (currentStage.getLocation() == ZulrahBehavior.SOUTH) {
						face(base.transform(0, 10, 0));
					} else if (currentStage.getLocation() == ZulrahBehavior.CENTER) {
						face(base.transform(0, -10, 0));
					} else if (currentStage.getLocation() == ZulrahBehavior.EAST) {
						face(base.transform(-10, 0, 0));
					} else if (currentStage.getLocation() == ZulrahBehavior.WEST) {
						face(base.transform(10, 0, 0));
					}
					setTickDelay(1);
					break;
				case 3:
					playAnimation(Zulrah.RISE_ANIMATION);
					getCombatState().setAttackDelay(4);
					stop();
					break;
				}
			}
		});
		state = to;
	}

	private void facePlayer() {
		if (currentAttack != null && challenger != null
				&& (currentAttack.getAttackType() == ZulrahAttackType.MELEE
						|| currentAttack.getAttackType() == ZulrahAttackType.SPAWN_VENOM
						|| currentAttack.getAttackType() == ZulrahAttackType.SPAWN_MINIONS))
			return;

		if (challenger != null && challenger.getInteractingEntity() == this) {
			super.face(challenger.getLocation());
		}
	}

	@Override
	public final void tick() {
		facePlayer();
		super.tick();

		if (!activeVenomClouds.isEmpty() && challenger.getCombatState().isAlive()) {
			boolean antiVenom = false;
			if (challenger.hasAttribute("antiVenom+")) {
				antiVenom = System.currentTimeMillis() - (long) challenger.getAttribute("antiVenom+", 0L) < 300000;
			}
			for (Item equip : challenger.getEquipment().toArray()) {
				if (equip == null) {
					continue;
				}
				VenomWeapons venomWeapons = VenomWeapons.of(equip.getId());
				if (venomWeapons != null) {
					antiVenom = true;
				}
			}
			if (!antiVenom) {
				activeVenomClouds.stream().map(Entity::getLocation).filter(this::inVenomZone).map(l -> {
					if (Misc.random(6) == 0 && !challenger.hasAttribute("venom")) {
						challenger.inflictVenom();
					}
					int damage = Misc.random(1, 4);
					if (damage > challenger.getSkills().getLevel(Skills.HITPOINTS)) {
						damage = challenger.getSkills().getLevel(Skills.HITPOINTS);
					}
					return new Hit(damage, Hit.HitType.VENOM_HIT);
				}).forEach(h -> challenger.inflictDamage(h, challenger));
			}
		}

		for (Iterator<NPC> itr = activeMinions.iterator(); itr.hasNext();) {
			NPC minion = itr.next();

			if (minion.getCombatState().isDead()) {
				itr.remove();
				continue;
			}

			minion.getCombatState().startAttacking(challenger, challenger.isAutoRetaliating());
		}
	}

	@Override
	public final void doCombat() {
		if (startTransforming) {
			startTransforming = false;
			submitTimer(AN.BOSS_TRANSFORMING, 10);
			World.getWorld().submit(new Tickable(2) {
				public void execute() {
					doNextTransition();
					stop();
				}
			});
		} else if (!isTransforming()) {
			state.perform();
			CombatState combatState = getCombatState();
			if (combatState.getAttackDelay() == 0) {
				setAttackable(true);
				currentAttack = attacks.poll();

				if (currentAttack == null) {
					startTransforming = true;
					return;
				}

				currentAttack.perform(this, challenger);
				combatState.setAttackDelay(currentAttack.getAttackDelay());
			}
		}
	}

	@Override
	public final void dropLoot(final Mob mob) {
		if (mob != challenger)
			return;
		Content zulrahContent = challenger.getContentManager().getActiveContent(Content.ZULRAH);
		zulrahContent.stopRecording();
		String addition = "";

		Map<Integer, Duration> bossKillTimes = challenger.getDatabaseEntity().getStatistics().getBossKillTimes();

		Duration bestTime = bossKillTimes.get(ID);
		if (bestTime == null || bestTime.getMillis() > zulrahContent.getTotalRecorded()) {
			addition = "(new personal best)";
			bossKillTimes.put(ID, Duration.millis(zulrahContent.getTotalRecorded()));
		}

		String time = String.format("%02d:%02d", zulrahContent.getRecordedMinutes(),
				zulrahContent.getRecordedSeconds());
		challenger.sendMessage("Fight duration: <col=c4260e>" + time + "<col=000000> " + addition);
		final double chance = challenger.getEquipment().get(Equipment.SLOT_RING) != null
				&& challenger.getEquipment().get(Equipment.SLOT_RING).getId() == 2572 ? 1.1 : 1.0;
		NPCLootTable.forID(this).getGeneratedLoot(chance).stream().filter(loot -> loot != null).forEach(loot -> {

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

			GroundItemDefinition item = new GroundItemDefinition(challenger.getUndefinedName(),
					challenger.getLocation(), loot.getItemID(), Misc.random(loot.getMinAmount(), loot.getMaxAmount()));
			CacheItemDefinition def = CacheItemDefinition.get(loot.getItemID());
			if (def != null) {
				Pet.Pets pets = Pet.Pets.from(item.getId());
				if (pets != null) {
					Pet.givePet(challenger, item);
					return;
				}

				if (item.getPrice() > 150000) {

					World.getWorld().sendWorldMessage("<col=ff0000><img=33>Server</col>: " + challenger.getName()
							+ " has just received " + item.getCount() + "x " + def.getName() + " from Zulrah.");

					new Thread(new NewsManager(challenger, "<img src='../resources/news/universal_drop.png' "
							+ "width=13> received " + def.getName() + " as drop.")).start();

				} else
					challenger.getActionSender().sendMessage("<col=00ff00><shad=000000>" + challenger.getName()
							+ " received a drop: " + item.getCount() + " x " + def.getName());
			}
			World.getWorld().createGroundItem(item, challenger);
		});
	}

	@Override
	public final Animation getDefendAnimation() {
		return DEFEND_ANIMATION;
	}

	@Override
	public final Animation getDeathAnimation() {
		return DEATH_ANIMATION;
	}

	public void onDeath() {
		destroySelf();
	}

	public final boolean inVenomZone(Location loc) {
		int x = challenger.getX();
		int y = challenger.getY();

		int lx = loc.getX();
		int ly = loc.getY();
		for (int ix = 0; ix < 3; ix++) {
			for (int iy = 0; iy < 3; iy++) {
				if (x == lx + ix && y == ly + iy) {
					return true;
				}
			}
		}
		return false;
	}

	public final boolean isTransforming() {
		return (boolean) attr(AN.BOSS_TRANSFORMING);
	}

	public final void destroySelf() {

		activeVenomClouds.forEach(o -> World.getWorld().unregister(o, true));
		activeMinions.forEach(World.getWorld()::unregister);
		activeMinions.forEach(World.getWorld().getNPCs()::remove);
		activeMinions.forEach(challenger.getInstancedNPCs()::remove);
		challenger.getInstancedNPCs().forEach(World.getWorld()::unregister);
		challenger.getInstancedNPCs().remove(this);
		challenger.getInstancedNPCs().clear();
		World.getWorld().unregister(this);
		World.getWorld().getNPCs().remove(this);
		activeMinions.clear();
	}

	@Override
	public final boolean isTurn() {
		return instancedPlayer != null && getCombatState().getAttackDelay() == 0 && !isTransforming();
	}

	public final List<GameObject> getActiveVenomClouds() {
		return activeVenomClouds;
	}

	public final List<NPC> getActiveMinions() {
		return activeMinions;
	}

}
