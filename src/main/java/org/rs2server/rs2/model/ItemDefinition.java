package org.rs2server.rs2.model;

import com.google.gson.Gson;
import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.io.FileUtilities;
import org.rs2server.rs2.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public final class ItemDefinition {

	private static ItemDefinition[] definitions;

	public static void init() throws IOException {
		Logger.getAnonymousLogger().info("Loading item definitions...");

		Gson gson = new Gson();
		ItemDefinition[] definitions = gson.fromJson(
				new BufferedReader(new FileReader("data/items/item_definitions.json")), ItemDefinition[].class);

		ItemDefinition.definitions = new ItemDefinition[25000];// 20740
		for (int i = 0; i < definitions.length; i++) {
			ItemDefinition def = definitions[i];
			ItemDefinition.definitions[def.id] = def;
		}

		loadExchangePrices();
		loadAlchPrices();
		loadItemExamines();
		setTradableItems(); // TODO doesn't seem to want to work lol
		Logger.getAnonymousLogger().info("Loaded " + definitions.length + " JSON item definitions.");
	}

	/**
	 * Loads the grand exchange prices.
	 */
	public static void loadExchangePrices() {
		try {
			// DataInputStream dat = new DataInputStream(new
			// FileInputStream("./data/grand_exchange_prices.dat"));
			// while (true) {
			// int itemId = dat.readShort();
			// if (itemId == -1) {
			// break;
			// }
			// if (itemId >= definitions.length)
			// break;
			// ItemDefinition def = forId(itemId);
			// def.storePrice = dat.readInt();
			// }
			for (String string : FileUtilities.readFile("data/items/itemPrices.txt")) {
				int id = Integer.parseInt(string.split(":")[0]);
				int price = Integer.parseInt(string.split(":")[1]);
				ItemDefinition def = forId(id);
				def.storePrice = price;
			}
			// dat.close();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	private static void loadAlchPrices() {
		try {
			for (String string : FileUtilities.readFile("data/items/alchprices.txt")) {
				int id = Integer.parseInt(string.split(":")[0]);
				int lowAlch = Integer.parseInt(string.split(":")[1]);
				int highAlch = Integer.parseInt(string.split(":")[2]);
				ItemDefinition def = forId(id);
				def.lowAlch = lowAlch;
				def.highAlch = highAlch;
			}
			// dat.close();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	private static void loadItemExamines() {
		try {
			for (String string : FileUtilities.readFile("data/items/itemExamines.txt")) {
				int id = Integer.parseInt(string.split(":")[0]);
				if (string.split(":").length < 2)
					continue;
				String examine = string.split(":")[1];
				ItemDefinition def = forId(id);
				if (def != null)
					def.setExamine(examine);
			}
			// dat.close();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	private static void setTradableItems() {
		try {
			for (String string : FileUtilities.readFile("data/items/tradableItems.txt")) {
				int id = Integer.parseInt(string.split(":")[0]);
				if (string.split(":").length < 2)
					continue;
				boolean tradable = Boolean.parseBoolean(string.split(":")[1]);
				ItemDefinition def = forId(id);
				if (def != null)
					def.setTradable(tradable);
			}
			// dat.close();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	public static ItemDefinition forId(int id) {
		if (id == -1)
			return null;
		if (definitions.length <= id)
			return null;
		ItemDefinition def = definitions[id];
		if (def == null) {
			definitions[id] = new ItemDefinition();
		}
		return definitions[id];
	}

	public static ItemDefinition forName(String name) {
		for (ItemDefinition definition : definitions) {
			if (definition.getCacheDefinition() == null)
				continue;
			if (definition.getCacheDefinition().name == null)
				continue;
			if (definition.getCacheDefinition().name.equalsIgnoreCase(name)) {
				return definition;
			}
		}
		return null;
	}

	public static void clear() {
		definitions = null;
	}

	private int id;
	private String name;
	private String examine;
	private int[] bonus = new int[15];
	private boolean stackable;
	private boolean noted;
	private double weight;
	private boolean members;
	private int attackSpeed, equipmentSlot;

	private boolean extraDefinitions;
	private transient CacheItemDefinition cacheDefinition;
	private boolean fullHat;
	private boolean fullMask;
	private boolean fullBody;
	@SuppressWarnings("unused")
	private boolean tradable;
	private boolean twoHanded;
	private boolean dropable;

	public int storePrice;
	private int lowAlch;
	private int highAlch;

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public int[] getBonus() {
		return bonus;
	}

	public boolean isStackable() {
		return stackable || noted || org.rs2server.cache.format.CacheItemDefinition.get(id).stackable
				|| org.rs2server.cache.format.CacheItemDefinition.get(id).isNoted();
	}

	public boolean isNoted() {
		return noted;
	}

	public String getExamine() { 
		return examine;
	}

	public double getWeight() {
		return weight;
	}

	public int getAttackSpeed() {
		return attackSpeed;
	}

	public int getEquipmentSlot() {
		return equipmentSlot;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setExamine(String examine) {
		this.examine = examine;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setBonus(int[] bonus) {
		this.bonus = bonus;
	}

	public void setBonusAtIndex(int index, int value) {
		this.bonus[index] = value;
	}

	public void setStackable(boolean stackable) {
		this.stackable = stackable;
	}

	public void setNoted(boolean noted) {
		this.noted = noted;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public void setAttackSpeed(int attackSpeed) {
		this.attackSpeed = attackSpeed;
	}

	public void setEquipmentSlot(int equipmentSlot) {
		this.equipmentSlot = equipmentSlot;
	}

	public void setMembers(boolean members) {
		this.members = members;
	}

	public boolean isMembers() {
		return members;
	}

	public void setExtraDefinitions(boolean extraDefinition) {
		this.extraDefinitions = extraDefinition;
	}

	public boolean isExtraDefinitions() {
		return extraDefinitions;
	}

	public CacheItemDefinition getCacheDefinition() {
		if (cacheDefinition == null) {
			cacheDefinition = CacheItemDefinition.get(id);
		}
		return cacheDefinition;
	}

	public static ItemDefinition[] getDefinitions() {
		return definitions;
	}

	/**
	 * @return the fullHat
	 */
	public boolean isFullHat() {
		return fullHat;
	}

	/**
	 * @param fullHat
	 *            the fullHat to set
	 */
	public void setFullHat(boolean fullHat) {
		this.fullHat = fullHat;
	}

	/**
	 * @return the fullMask
	 */
	public boolean isFullMask() {
		return fullMask;
	}

	/**
	 * @param fullMask
	 *            the fullMask to set
	 */
	public void setFullMask(boolean fullMask) {
		this.fullMask = fullMask;
	}

	public boolean isFullBody() {
		return fullBody;
	}

	public void setFullBody(boolean fullBody) {
		this.fullBody = fullBody;
	}

	public boolean isTradable() { 
		if (noted) {
			final ItemDefinition unnoted = definitions[id - 1];
			if (unnoted.isTradable())
				return true;
		}
		if (Constants.playerBoundItem(id))
			return false;
		return true; // TODO XXX return tradable;
	}

	public void setTradable(boolean tradable) {
		this.tradable = tradable;
	}

	public boolean isTwoHanded() {
		return twoHanded;
	}

	public void setTwoHanded(boolean twoHanded) {
		this.twoHanded = twoHanded;
	}

	public boolean isDropable() {
		return dropable;
	}

	public void setDropable(boolean dropable) {
		this.dropable = dropable;
	}

	public int getLowAlch() {
		return lowAlch;
	}

	public void setLowAlch(int lowAlch) {
		this.lowAlch = lowAlch;
	}

	public int getHighAlch() {
		return highAlch;
	}

	public void setHighAlch(int highAlch) {
		this.highAlch = highAlch;
	}

	public int getStorePrice() {
		if (isNoted())
			return forId(this.getId() - 1).getStorePrice();
		return storePrice;
	}

	public void setStorePrice(int i) {
		this.storePrice = i;
	}

	public boolean isNoteable() {
		CacheItemDefinition def = CacheItemDefinition.get(id);
		if (def == null) {
			return false;
		}
		return def.noted != -1;
	}

	public int getNotedId() {
		return id + 1;
	}

	public int getNormalId() {
		return id - 1;
	}

}
