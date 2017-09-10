package org.rs2server.rs2.model;

import java.util.Arrays;

/**
 * Defines RuneScape skills alongside their literal names and index.
 *
 * @author tommo
 */
public enum Skill {

	ATTACK("Attack", 0),

	DEFENCE("Defence", 1),

	STRENGTH("Strength", 2),

	HITPOINTS("Hitpoints", 3),

	RANGE("Range", 4),

	PRAYER("Prayer", 5),

	MAGIC("Magic", 6),

	COOKING("Cooking", 7),

	WOODCUTTING("Woodcutting", 8),

	FLETCHING("Fletching", 9),

	FISHING("Fishing", 10),

	FIREMAKING("Firemaking", 11),

	CRAFTING("Crafting", 12),

	SMITHING("Smithing", 13),

	MINING("Mining", 14),

	HERBLORE("Herblore", 15),

	AGILITY("Agility", 16),

	THIEVING("Thieving", 17),

	SLAYER("Slayer", 18),

	FARMING("Farming", 19),

	RUNECRAFTING("Runecrafting", 20),

	HUNTER("Hunter", 21),

	CONSTRUCTION("Construction", 22);

	private String name;
	private int id;

	Skill(final String name, final int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public Skill forName(final String name) {
		return Arrays.stream(Skill.values()).filter(s -> s.getName().equals(name)).findFirst().orElse(null);
	}

	public Skill forId(final int id) {
		return Arrays.stream(Skill.values()).filter(s -> s.getId() == id).findFirst().orElse(null);
	}
}
