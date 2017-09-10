package org.rs2server.rs2.model.npc;

import org.rs2server.Server;
import org.rs2server.rs2.domain.model.player.PlayerSettingsEntity;
import org.rs2server.rs2.domain.service.api.PathfindingService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.mysql.impl.NewsManager;
import org.rs2server.rs2.util.Misc;
import org.rs2server.util.functional.Optionals;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Twelve
 * @author Tim
 */
public class Pet extends NPC {

	public enum Pets {

		// item id, npc id
		ZULRAH_RANGE(12921, 2127, "Hisssss"),

		ZULRAH_MELEE(12939, 2128, "Hisssss"),

		ZULRAH_MAGE(12940, 2129, "Hisssss"),

		VETIONJR(13180, 5536, ""),

		BLOODHOUND(19730, 6296, ""),

		CHOMPY_CHICK(13071, 4001, "Chirp"),

		DAGANNOTH_SUPREME(12643, 6626, ""),

		DAGANNOTH_PRIME(12644, 6627, ""),

		DAGANNOTH_REX(12645, 6641, ""),

		ARMADYL(12649, 6643, ""),

		BANDOS(12650, 6644, ""),

		SARADOMIN(12651, 6646, ""),

		ZAMORAK(12652, 6647, ""),

		KING_BLACK_DRAGON(12653, 6652, ""),

		CHAOS_ELEMENTAL(11995, 5907, ""),

		KALPHITE_PRINCESS_FORM_1(12654, 6653, ""),

		KRAKEN(12655, 6656, ""),

		SCORPIA(13181, 5547, ""),

		CALLISTO(13178, 497, ""),

		VETION(13179, 5536, ""),

		VENENATIS(13177, 495, ""),

		JAD(13225, 5892, ""),

		SMOKE_DEVIL(12648, 6655, ""),

		BEAVER(13322, 6717, ""),

		HERON(13320, 6715, ""),

		ROCK_GOLEM(13321, 6716, ""),

		HELLPUPPY(13247, 964, ""),

		GIANT_SQUIRREL(20659, 7334, ""),

		TANGLEROOT(20661, 7335, ""),

		ROCKY(20663, 7336, ""),

		RIFT_GUARDIAN_I(20665, 7337, ""),

		RIFT_GUARDIAN_II(20667, 7338, ""),

		RIFT_GUARDIAN_III(20669, 7339, ""),

		RIFT_GUARDIAN_IV(20671, 7340, ""),

		RIFT_GUARDIAN_V(20673, 7341, ""),

		RIFT_GUARDIAN_VI(20675, 7342, ""),

		RIFT_GUARDIAN_VII(20677, 7343, ""),

		RIFT_GUARDIAN_VIII(20679, 7344, ""),

		RIFT_GUARDIAN_IX(20681, 7345, ""),

		RIFT_GUARDIAN_X(20681, 7345, ""),

		RIFT_GUARDIAN_XII(20683, 7346, ""),

		RIFT_GUARDIAN_XIII(20685, 7347, ""),

		RIFT_GUARDIAN_XIV(20687, 7348, ""),

		RIFT_GUARDIAN_XV(20689, 7349, ""),

		RIFT_GUARDIAN_XVI(20691, 7350, ""),

		PHOENIX(20693, 7368, ""),

		ABBYSAL_ORPHAN(13262, 5883, ""),

		BABY_MOLE(12646, 6651, ""),

		KALPHITE_PRINCESS(12647, 6654, ""),

		KALPHITE_PRINCESS2(12654, 6653, ""),

		DARK_CORE(12816, 388, "darkness..."),

		PENANCE_PET(12703, 6642, ""),

		BABY_CHIN(13323, 6718, ""),

		BABY_CHIN1(13324, 6719, ""),

		BABY_CHIN2(13325, 6720, ""),

		BABY_CHIN3(13326, 6721, ""),

		OLMET(20851, 7519, "")

		;

		private final int item;
		private final int npc;
		private final String randomMessage;

		Pets(int item, int npc, String randomMessage) {
			this.item = item;
			this.npc = npc;
			this.randomMessage = randomMessage;
		}

		private static Map<Integer, Pets> petItems = new HashMap<Integer, Pets>();
		private static Map<Integer, Pets> petNpcs = new HashMap<Integer, Pets>();

		public static Pets from(int item) {
			return petItems.get(item);
		}

		public static Pets fromNpc(int npc) {
			return petNpcs.get(npc);
		}

		static {
			for (Pets pet : Pets.values()) {
				petItems.put(pet.item, pet);
			}
			for (Pets pet : Pets.values()) {
				petNpcs.put(pet.npc, pet);
			}
		}

		public int getItem() {
			return item;
		}

		public int getNpc() {
			return npc;
		}

		public String getTextMessage() {
			return randomMessage;
		}
	}

	private final PathfindingService pathfindingService;
	private static final int MAX_DISTANCE = 12;

	public Pet(Player owner, int id) {
		super(id, owner.getLocation(), owner.getLocation(), owner.getLocation(), 0);
		this.pathfindingService = Server.getInjector().getInstance(PathfindingService.class);
		this.setInstancedPlayer(owner);
		this.setInteractingEntity(InteractionMode.FOLLOW, owner);
		this.forceChat(Pets.fromNpc(id).getTextMessage());
	}

	@Override
	public void tick() {
		double distance = getLocation().getDistance(owner.getLocation());
		if (getLocation().equals(owner.getLocation()))
			Optionals.nearbyFreeLocation(owner.getLocation()).ifPresent(l -> pathfindingService.travel(this, l));
		else if (distance > MAX_DISTANCE) {
			Optionals.nearbyFreeLocation(owner.getLocation()).ifPresent(l -> {
				this.setTeleportTarget(l);
				this.setInteractingEntity(InteractionMode.FOLLOW, owner);
			});
		} else if (distance > 1)
			pathfindingService.travelToPlayer(this, owner);

		// Just those little touches that will make Salve great again ^^
		if (Misc.random(100) < 2)
			forceChat(Pets.fromNpc(getId()).getTextMessage());
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	/**
	 * Handles giving a player the pet from the given item.
	 * 
	 * @param player
	 *            the player to give the pet to
	 * @param item
	 *            the pet item id
	 */
	public static void givePet(Player player, Item item) {
		Pet.Pets pets = Pet.Pets.from(item.getId());
		
		// Check if the pet exists
		if (pets == null)
			return;
		
		// For extra pet rarity
		if (Misc.random(5) != 1)
			return;

		// Check if we own the pet already
		if (!player.getItemService().playerOwnsItem(player, item.getId())) {

			PlayerSettingsEntity settings = player.getDatabaseEntity().getPlayerSettings();
			boolean announce = false;

			// Make it our follower if we don't have one yet
			if (player.getPet() == null && !settings.isPetSpawned()) {
				Pet pet = new Pet(player, pets.getNpc());
				player.setPet(pet);
				settings.setPetSpawned(true);
				settings.setPetId(pets.getNpc());
				World.getWorld().register(pet);
				player.sendMessage("You have a funny feeling like you're being followed.");
				announce = true;
			} else {
				// Place it in our inventory if we already had a follower out
				if (player.getInventory().freeSlots() > 0) {
					player.sendMessage("You feel something weird sneaking into your backpack.");
					player.getInventory().add(item);
					announce = true;

				} else // RS Wikia says you don't receive a pet if full inventory
					player.sendMessage(
							"<col=ff0000>You would've received a pet just now if you had some inventory space!");
			}

			// Lets announce it to the public and website that we got a pet.
			// tho with the full inventory thing it may or may not be announced
			if (announce) {
				World.getWorld().sendWorldMessage("<col=ff0000><img=24>Server</col>: " + player.getName()
						+ " has just received " + item.getCount() + " x " + item.getDefinition2().getName() + ".");

				new Thread(new NewsManager(player, "<img src='../resources/news/pets.png' width=13> " + "received "
						+ item.getDefinition2().getName() + " as drop.")).start();
			}
		}
	}
}
