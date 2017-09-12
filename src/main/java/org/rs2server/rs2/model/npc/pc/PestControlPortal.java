package org.rs2server.rs2.model.npc.pc;

import com.google.common.collect.ImmutableList;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.pc.PestControlInstance;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A single portal inside of a {@link org.rs2server.rs2.model.player.pc.PestControlInstance} that either has a shield
 * or is able to be attacked.
 *
 * @author twelve
 */
public class PestControlPortal extends NPC {

	private static final Random RANDOM = new Random();
	private final int attackPortalId;
	private final PortalCardinality cardinality;
	private final List<PestControlNpc> npcs;
	private final PestControlInstance instance; 
	private static final int HEALTH = 250;
	public long lastSpawn;
	private final List<Integer> npcList;

	PestControlPortal(PestControlInstance instance, PortalCardinality cardinality, int id, int attackPortalId) {
		super(id, cardinality.getLocation());
		this.attackPortalId = attackPortalId;
		this.cardinality = cardinality;
		this.instance = instance;
		this.npcs = new ArrayList<>();
		this.npcList = ImmutableList.of(1694, 1695, 1696, 1697, 1714, 1715, 1716, 1717, 1718, 1709, 1710, 1711, 1724, 1725, 1726, 1727, 1734, 1735, 1736, 1737, 1689);


		setAttackable(false);
		getCombatState().setCanMove(false);

		getSkills().setLevel(Skills.HITPOINTS, HEALTH);
		setCombatDefinition(CombatNPCDefinition.of(attackPortalId));
		getCombatDefinition().getSkills().put(CombatNPCDefinition.Skill.HITPOINTS, HEALTH);
		clip(1);
	}

	public static PestControlPortal in(PestControlInstance instance, PortalCardinality cardinality, int id, int attackPortalId) {
		return new PestControlPortal(instance, cardinality, id, attackPortalId);
	}

	public List<PestControlNpc> getNpcs() {
		return npcs;
	}

	@Override
	public void tick() {
		if (getCombatState().isDead()) {

		}
		if (isAttackable()) {
			setTransformId(attackPortalId);
			getUpdateFlags().flag(UpdateFlags.UpdateFlag.TRANSFORM);
			if (npcs.size() < 14 && System.currentTimeMillis() - lastSpawn > 7500) {
				int chosenNpc = npcList.get(RANDOM.nextInt(npcList.size()));
				PestControlNpc pestControlNpc;
				Location spawnLocation = cardinality.getSpawnLocations()[RANDOM.nextInt(cardinality.getSpawnLocations().length)];
				switch (chosenNpc) {
					case 1694:
					case 1695:
					case 1696:
					case 1697:
						pestControlNpc = new Shifter(chosenNpc, spawnLocation, instance, this);
						break;
					case 1709:
					case 1710:
					case 1711:
						pestControlNpc = new Spinner(chosenNpc, spawnLocation, instance, this);
						break;
					case 1724:
					case 1725:
					case 1726:
					case 1727:
						pestControlNpc = new Defiler(chosenNpc, spawnLocation, instance, this);
						break;
					case 1734:
					case 1735:
					case 1736:
					case 1737:
						pestControlNpc = new Brawler(chosenNpc, spawnLocation, instance, this);
						break;
					case 1689:
						pestControlNpc = new Splatter(chosenNpc, spawnLocation, instance, this);
						break;
					default: //TORCHER IS DEFAULT
						pestControlNpc = new Torcher(1714, spawnLocation, instance, this);
						break;
				}
				World.getWorld().register(pestControlNpc);
				npcs.add(pestControlNpc);
				lastSpawn = System.currentTimeMillis();
			}
		}
	}

	private void clip(int state) {
		if (cardinality == PortalCardinality.BLUE || cardinality == PortalCardinality.PURPLE) { //0x200000
			Location base = getSpawnLocation().transform(1, -1, 0);
			for (int i = 0; i < 5; i++) {
				RegionClipping.addClipping(base.getX(), base.getY() + i, base.getPlane(), 256);
				if (i >= 1 && 4 < i) {
					if (state == 0) {
						RegionClipping.addClipping(base.getX() - 1, base.getY() + i, base.getPlane(), 256);
						RegionClipping.addClipping(base.getX() + 1, base.getY() + i, base.getPlane(), 256);
					} else if (state == 1) {
						RegionClipping.removeClipping(base.getX() - 1, base.getY() + i, base.getPlane(), 256);
						RegionClipping.removeClipping(base.getX() + 1, base.getY() + i, base.getPlane(), 256);
					}
				}
			}
		} else {
			Location base = getSpawnLocation().transform(-1, 1, 0);
			for (int i = 0; i < 5; i++) {
				RegionClipping.addClipping(base.getX() + i, base.getY(), base.getPlane(), 256);
				if (i >= 1 && 4 < i) {
					if (state == 0) {
						RegionClipping.addClipping(base.getX() + i, base.getY() - 1, base.getPlane(), 256);
						RegionClipping.addClipping(base.getX() + i, base.getY() + 1, base.getPlane(), 256);
					} else if (state == 1) {
						RegionClipping.removeClipping(base.getX() + i, base.getY() - 1, base.getPlane(), 256);
						RegionClipping.removeClipping(base.getX() + i, base.getY() + 1, base.getPlane(), 256);
					}
				}
			}
		}

	}

	@Override
	public void dropLoot(Mob mob) {
		instance.getKnight().getSkills().increaseLevelToMaximum(Skills.HITPOINTS, 50);
	}

	public PortalCardinality getCardinality() {
		return cardinality;
	}

	public PestControlInstance getInstance() {
		return instance;
	}
}
