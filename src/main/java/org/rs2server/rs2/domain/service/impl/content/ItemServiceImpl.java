package org.rs2server.rs2.domain.service.impl.content;

import org.apache.commons.lang3.ArrayUtils;
import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.cache.format.CacheNPCDefinition;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.domain.service.api.content.ItemService;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.npc.Pet;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.util.Misc;
import org.rs2server.util.functional.QuadConsumer;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * @author Clank1337
 * @author twelve
 * @author tommo
 */
public class ItemServiceImpl implements ItemService {

	/**
	 * Defines the degradation types.
	 */
	public enum DegradationStrategy {

		ON_ATTACK, ON_DEFEND, ON_USE, ON_WOODCUT
	}

	/**
	 * Defines types of ways to upgrade an item.
	 */
	enum UpgradeStrategy {
		ON_KILL, ON_ITEM_USE
	}

	/**
	 * Defines an upgradable item.
	 */
	enum Upgradable {

		/**
		 * Takes an empty toxic blowpipe and defines an Upgradable that would result in
		 * a full blowpipe upon success.
		 */
		TOXIC_BLOWPIPE_EMPTY(new Item(12924), new Item(12926), UpgradeStrategy.ON_ITEM_USE, (s, p, i, c) -> {
			int charges = s.getCharges(p, c);
			s.setChargesWithItem(p, i, c, s.getCharges(p, i) + c.getCount() + (charges < 0 ? 0 : charges));
			p.getActionSender().sendMessage("You load " + c.getCount() + "x " + c.getDefinition2().getName()
					+ " into the " + i.getDefinition2().name);
		}, 806, 807, 808, 809, 810, 811, 11230),
		/**
		 * The toxic blowpipe full is the same as the toxic blowpipe empty except it
		 * outputs an item the same as it's input, which is the full blowpipe.
		 */
		TOXIC_BLOWPIPE_FULL(TOXIC_BLOWPIPE_EMPTY.to, TOXIC_BLOWPIPE_EMPTY.to, UpgradeStrategy.ON_ITEM_USE,
				TOXIC_BLOWPIPE_EMPTY.callback, TOXIC_BLOWPIPE_EMPTY.parameters),

		/**
		 * Serpentine helmets take Zulrah scales to be charged.
		 */
		SERPENTINE_HELM_UNCHARGED(new Item(12929), new Item(12931), UpgradeStrategy.ON_ITEM_USE,
				TOXIC_BLOWPIPE_EMPTY.callback, 12934),

		SERPENTINE_HELM(SERPENTINE_HELM_UNCHARGED.to, SERPENTINE_HELM_UNCHARGED.to, UpgradeStrategy.ON_ITEM_USE,
				SERPENTINE_HELM_UNCHARGED.callback, SERPENTINE_HELM_UNCHARGED.parameters),

		/**
		 * Trident of the seas
		 */
		UNCHARGED_TRIDENT_OF_THE_SEAS(new Item(11908), new Item(11907), UpgradeStrategy.ON_ITEM_USE,
				TOXIC_BLOWPIPE_EMPTY.callback, 12934),

		TRIDENT_OF_THE_SEAS(UNCHARGED_TRIDENT_OF_THE_SEAS.to, UNCHARGED_TRIDENT_OF_THE_SEAS.to,
				UpgradeStrategy.ON_ITEM_USE, UNCHARGED_TRIDENT_OF_THE_SEAS.callback,
				UNCHARGED_TRIDENT_OF_THE_SEAS.parameters),

		/**
		 * Trident of the swamp
		 */
		TRIDENT_OF_THE_SWAMP_UNCHARGED(new Item(12900), new Item(12899), UpgradeStrategy.ON_ITEM_USE,
				TOXIC_BLOWPIPE_EMPTY.callback, 12934),

		TRIDENT_OF_THE_SWAP(TRIDENT_OF_THE_SWAMP_UNCHARGED.to, TRIDENT_OF_THE_SWAMP_UNCHARGED.to,
				UpgradeStrategy.ON_ITEM_USE, TRIDENT_OF_THE_SWAMP_UNCHARGED.callback,
				TRIDENT_OF_THE_SWAMP_UNCHARGED.parameters),
		/**
		 * Toxic staff of the dead
		 */
		TOXIC_STAFF_UNCHARGED(new Item(12902), new Item(12904), UpgradeStrategy.ON_ITEM_USE,
				TOXIC_BLOWPIPE_EMPTY.callback, 12934),

		TOXIC_STAFF_OF_THE_DEAD(TOXIC_STAFF_UNCHARGED.to, TOXIC_STAFF_UNCHARGED.to, UpgradeStrategy.ON_ITEM_USE,
				TOXIC_STAFF_UNCHARGED.callback, TOXIC_STAFF_UNCHARGED.parameters),
		/**
		 * Tanzanite helmets are the same as Serpentine helmets, except with different
		 * to and from ids.
		 */
		TANZANITE_HELM_UNCHARGED(new Item(13196), new Item(13197), UpgradeStrategy.ON_ITEM_USE,
				SERPENTINE_HELM_UNCHARGED.callback, SERPENTINE_HELM_UNCHARGED.parameters),

		TANZANITE_HELM(TANZANITE_HELM_UNCHARGED.to, TANZANITE_HELM_UNCHARGED.to, UpgradeStrategy.ON_ITEM_USE,
				SERPENTINE_HELM.callback, SERPENTINE_HELM.parameters),

		/**
		 * Magma helmets are the same as Serpentine helmets, except with different to
		 * and from ids.
		 */
		MAGMA_HELM_UNCHARGED(new Item(13198), new Item(13199), UpgradeStrategy.ON_ITEM_USE,
				SERPENTINE_HELM_UNCHARGED.callback, SERPENTINE_HELM_UNCHARGED.parameters),

		MAGMA_HELM(MAGMA_HELM_UNCHARGED.to, MAGMA_HELM_UNCHARGED.to, UpgradeStrategy.ON_ITEM_USE,
				SERPENTINE_HELM.callback, SERPENTINE_HELM.parameters),

		PRIMORDIAL_CRYSTAL(new Item(13231), new Item(13239), UpgradeStrategy.ON_ITEM_USE, (s, p, i, c) -> {
			p.getActionSender().sendMessage(
					"You combine the crystal with your boots to create " + i.getDefinition2().getName() + ".");
			p.getSkills().addExperience(Skills.MAGIC, 200);
			p.getSkills().addExperience(Skills.RUNECRAFTING, 200);
		}, 11840),

		ABYSSAL_DAGGER_POISON(new Item(13265), new Item(13271), UpgradeStrategy.ON_ITEM_USE,
				(s, p, i, c) -> p.getActionSender().sendMessage("You combine the poison with the Abyssal dagger"), 187),

		PEGASIAN_CRYSTAL(new Item(13229), new Item(13237), UpgradeStrategy.ON_ITEM_USE, PRIMORDIAL_CRYSTAL.callback,
				2577),

		ETERNAL_CRYSTAL(new Item(13227), new Item(13235), UpgradeStrategy.ON_ITEM_USE, PRIMORDIAL_CRYSTAL.callback,
				6920),

		DYNAMITE_POT(new Item(13572), new Item(13571, 1), UpgradeStrategy.ON_ITEM_USE,
				(s, p, i, c) -> p.getActionSender()
						.sendMessage("You combine the sulphur and pot to create " + i.getDefinition2().getName() + "."),
				1931),

		DYNAMITE(new Item(13573), new Item(13572, 1), UpgradeStrategy.ON_ITEM_USE,
				(s, p, i, c) -> p.getActionSender()
						.sendMessage("You combine the ball of wool and dynamite pot to create "
								+ i.getDefinition2().getName() + "."),
				1759),

		INFERNAL_AXE(new Item(13233), new Item(13241), UpgradeStrategy.ON_ITEM_USE,
				(s, p, i, c) -> p.getActionSender()
						.sendMessage("You combine the smouldering stone with your axe to create "
								+ i.getDefinition2().getName() + "."),
				6739),

		INFERNAL_PICKAXE(new Item(13233), new Item(13243), UpgradeStrategy.ON_ITEM_USE,
				(s, p, i, c) -> p.getActionSender()
						.sendMessage("You combine the smouldering stone with your axe to create "
								+ i.getDefinition2().getName() + "."),
				11920),

		EMBLEM_TIER_ONE(new Item(12746), new Item(12748), UpgradeStrategy.ON_KILL,
				(s, p, i, u) -> p.getActionSender().sendMessage("Your emblem has been upgraded to the next tier."),
				12746), EMBLEM_TIER_TWO(new Item(12748), new Item(12749), UpgradeStrategy.ON_KILL,
						EMBLEM_TIER_ONE.callback, 12748), EMBLEM_TIER_THREE(new Item(12749), new Item(12750),
								UpgradeStrategy.ON_KILL, EMBLEM_TIER_ONE.callback,
								12749), EMBLEM_TIER_FOUR(new Item(12750), new Item(12751), UpgradeStrategy.ON_KILL,
										EMBLEM_TIER_ONE.callback, 12750), EMBLEM_TIER_FIVE(new Item(12751),
												new Item(12752), UpgradeStrategy.ON_KILL, EMBLEM_TIER_ONE.callback,
												12751), EMBLEM_TIER_SIX(new Item(12752), new Item(12753),
														UpgradeStrategy.ON_KILL, EMBLEM_TIER_ONE.callback,
														12752), EMBLEM_TIER_SEVEN(new Item(12753), new Item(12754),
																UpgradeStrategy.ON_KILL, EMBLEM_TIER_ONE.callback,
																12753), EMBLEM_TIER_EIGHT(new Item(12754),
																		new Item(12755), UpgradeStrategy.ON_KILL,
																		EMBLEM_TIER_ONE.callback,
																		12754), EMBLEM_TIER_NINE(new Item(12755),
																				new Item(12756),
																				UpgradeStrategy.ON_KILL,
																				EMBLEM_TIER_ONE.callback, 12755);

		/**
		 * The item to be upgraded.
		 */
		private final Item from;
		/**
		 * The result of the item upgrade.
		 */
		private final Item to;
		/**
		 * The strategy to be used.
		 */
		private final UpgradeStrategy strategy;
		/**
		 * The parameters are the values that an upgradable item can take as input.
		 */
		private final List<Item> parameters;
		/**
		 * The callback is executed upon finishing an upgrade.
		 */
		private final QuadConsumer<ItemService, Player, Item, Item> callback;

		Upgradable(@Nonnull Item from, @Nonnull Item to, @Nonnull UpgradeStrategy strategy,
				@Nonnull QuadConsumer<ItemService, Player, Item, Item> callback, @Nonnull int... parameters) {
			this(from, to, strategy, callback, Arrays.asList(parameters).stream().flatMapToInt(IntStream::of).boxed()
					.map(Item::new).collect(toList()));
		}

		Upgradable(@Nonnull Item from, @Nonnull Item to, @Nonnull UpgradeStrategy strategy,
				@Nonnull QuadConsumer<ItemService, Player, Item, Item> callback, @Nonnull List<Item> parameters) {
			this.from = from;
			this.to = to;
			this.strategy = strategy;
			this.callback = callback;
			this.parameters = parameters;
		}

		/**
		 * Creates an {@link Optional} that may or may not return an
		 * {@link org.rs2server.rs2.domain.service.impl.content.ItemServiceImpl.Upgradable}
		 * depending on if an Upgradable exists that takes the given item and parameter
		 * as input.
		 *
		 * @param item
		 *            The item to find an upgradable for.
		 * @param parameter
		 *            The parameter item that is to be passed to the callback.
		 * @return An optional.
		 */
		@Nonnull
		public static Optional<Upgradable> of(Item item, Item parameter) {
			return Arrays.stream(values()).filter(i -> i.from.getId() == item.getId()).findFirst().filter(p -> {
				for (Item i : p.parameters) {
					if (i.getId() == parameter.getId()) {
						return true;
					}
				}
				return false;
			});
		}

		@Nonnull
		public final UpgradeStrategy getStrategy() {
			return strategy;
		}

		@Nonnull
		public final Item from() {
			return from;
		}

		@Nonnull
		public final Item to() {
			return to;
		}
	}

	/**
	 * Defines the degradable items.
	 */
	public enum Degradable {
		// DHAROK_FULL_HELM(1000, new Item[]{ new Item(4716), new Item(4880),
		// new Item(4881), new Item(4882), new Item(4883), new Item(4884)},
		// true),

		ABYSSAL_TENTACLE(10000, 10000, DegradationStrategy.ON_ATTACK, new Item[] { new Item(12006), new Item(12004) },
				false),

		TRIDENT_OF_THE_SEAS(0, 0, DegradationStrategy.ON_ATTACK, new Item[] { new Item(11907), new Item(11908) },
				false),

		TOXIC_BLOWPIPE(0, 0, DegradationStrategy.ON_ATTACK, new Item[] { new Item(12926), new Item(12924) }, false),

		TOXIC_STAFF_OF_THE_DEAD(0, 0, DegradationStrategy.ON_ATTACK, new Item[] { new Item(12904), new Item(12902) },
				false),

		TRIDENT_OF_THE_SWAMP(0, 0, DegradationStrategy.ON_ATTACK, new Item[] { new Item(12899), new Item(12900) },
				false),

		SERPENTINE_HELM(0, 0, DegradationStrategy.ON_DEFEND, new Item[] { new Item(12931), new Item(12929) }, false),

		TANZANITE_HELM(0, 0, DegradationStrategy.ON_DEFEND, new Item[] { new Item(13197), new Item(13196) }, false),

		MAGMA_HELM(0, 0, DegradationStrategy.ON_DEFEND, new Item[] { new Item(13199), new Item(13198) }, false);

		/**
		 * The amount of initial charges.
		 */
		private final int initialCharge;

		/**
		 * The amount of charges between each stage of item degrading.
		 */
		private final int charges;

		/**
		 * The degradation type.
		 */
		private DegradationStrategy degradationStrategy;

		/**
		 * The item degradation stages, starting from full to empty.
		 */
		private final Item[] items;

		private final boolean barrows;

		Degradable(int initialCharge, int charges, DegradationStrategy degradationStrategy, Item[] items,
				boolean barrows) {
			this.initialCharge = initialCharge;
			this.charges = charges;
			this.degradationStrategy = degradationStrategy;
			this.items = items;
			this.barrows = barrows;
		}

		/**
		 * Returns a degradable for a given item (can be any stage in the degradation
		 * process)
		 *
		 * @param item
		 *            The item
		 * @return The degradable, or null.
		 */
		public static Degradable forItem(@Nonnull Item item) {
			return Arrays.stream(Degradable.values())
					.filter(d -> Arrays.stream(d.items).anyMatch(i -> i.getId() == item.getId())).findFirst()
					.orElse(null);
		}

		public int getInitialCharge() {
			return initialCharge;
		}

		public int getCharges() {
			return charges;
		}

		public DegradationStrategy getDegradationStrategy() {
			return degradationStrategy;
		}

		public Item[] getItems() {
			return items;
		}

		public boolean isBarrows() {
			return barrows;
		}
	}

	private final PlayerService playerService;

	@Inject
	ItemServiceImpl(final PlayerService playerService) {
		this.playerService = playerService;
	}

	/**
	 * Checks if an item is fully degraded (last item in the degradation process)
	 *
	 * @param item
	 *            The item.
	 * @return true is fully degraded, false if not.
	 */
	@Override
	public boolean isFullyDegraded(@Nonnull Item item) {
		final Degradable degradable = Degradable.forItem(item);

		return degradable != null
				&& ArrayUtils.indexOf(degradable.getItems(), item) == degradable.getItems().length - 1;
	}

	@Override
	public void degradeItem(@Nonnull Mob mob, Item item) {
		if (item.getEquipmentDefinition() != null && !item.getEquipmentDefinition().isDegradable() || !mob.isPlayer()) {
			return;
		}
		final Player player = (Player) mob;
		final Degradable degradable = Degradable.forItem(item);
		if (degradable == null || isFullyDegraded(item)) {
			return;
		}
		// Reduce the charges by 1, and check for degradation...
		boolean remove = true;
		if (item.getId() == 12926 && mob.hasAttribute("avaEffect")) {
			mob.removeAttribute("avaEffect");
			remove = false;
		}
		final int charges = remove
				? (player.getDatabaseEntity().getEquipment().getItemCharges().getOrDefault(item.getId(),
						degradable.getInitialCharge()) - 1)
				: player.getDatabaseEntity().getEquipment().getItemCharges().getOrDefault(item.getId(),
						degradable.getInitialCharge());
		mob.removeAttribute("avaEffect");
		if (charges <= 0) {
			final Item replacement = getNextItem(item);

			setCharges(player, item, degradable.getCharges());

			final Item equipment = player.getEquipment().getById(item.getId());
			if (equipment != null) {
				int index = player.getEquipment().getSlotById(item.getId());
				if (replacement.getEquipmentDefinition() != null) {
					player.getEquipment().set(index, replacement);
				} else {
					player.getEquipment().set(index, null);
					playerService.giveItem(player, replacement, true);
				}
			} else {
				final Item inventory = player.getInventory().getById(item.getId());
				if (inventory != null) {
					int index = player.getInventory().getSlotById(item.getId());
					player.getInventory().set(index, null);
					playerService.giveItem(player, replacement, true);
				}
			}
			player.getActionSender().sendMessage("Your " + item.getDefinition2().getName() + " has degraded.");
			player.getDatabaseEntity().getEquipment().getItemCharges().put(replacement.getId(),
					degradable.getCharges());
		} else {
			player.getDatabaseEntity().getEquipment().getItemCharges().put(item.getId(), charges);
		}
	}

	@Override
	public void upgradeItem(@Nonnull Player player, Item item, Item parameter) {
		Upgradable.of(item, parameter).ifPresent(u -> {
			player.getInventory().set(player.getInventory().getSlotById(item.getId()), u.to());
			player.getInventory().remove(parameter);
			u.callback.accept(this, player, u.to(), parameter);
		});
	}

	@Override
	public int getCharges(@Nonnull Player player, @Nonnull Item item) {
		return player.getDatabaseEntity().getEquipment().getItemCharges().getOrDefault(item.getId(), 0);
	}

	@Override
	public void setCharges(@Nonnull Player player, @Nonnull Item item, int amount) {
		player.getDatabaseEntity().getEquipment().getItemCharges().put(item.getId(), amount);
	}

	@Override
	public void setChargesWithItem(@Nonnull Player player, @Nonnull Item item, @Nonnull Item with, int chargeAmount) {
		setCharges(player, item, chargeAmount);
		player.getDatabaseEntity().getEquipment().getItemChargedWith().put(item.getId(), with.getId());
	}

	@Override
	public int getChargedItem(@Nonnull Player player, @Nonnull Item item) {
		return player.getDatabaseEntity().getEquipment().getItemChargedWith().getOrDefault(item.getId(), -1);
	}

	@Override
	public int getNetWorth(@Nonnull Player player) {
		int worth = 0;
		for (Item inv : player.getInventory().toArray()) {
			if (inv == null || inv.getDefinition() == null) {
				continue;
			}
			worth += inv.getPrice() * inv.getCount();
		}

		for (Item equip : player.getEquipment().toArray()) {
			if (equip == null || equip.getDefinition() == null) {
				continue;
			}
			worth += equip.getPrice() * equip.getCount();
		}
		return worth;
	}

	@Override
	public void exchangeToNote(@Nonnull Player player, @Nonnull Item item) {
		CacheItemDefinition def = CacheItemDefinition.get(item.getId());
		if (def == null) {
			return;
		}
		if (def.noted == -1) {
			player.getActionSender().sendMessage("This item doesn't have a noted form.");
			return;
		}
		int amount = player.getInventory().getCount(item.getId());
		if (amount <= 0) {
			return;
		}
		player.getInventory().remove(new Item(item.getId(), amount));
		player.getInventory().add(new Item(def.noted, amount));
	}

	@Override
	public void exchangeToUnNote(@Nonnull Player player, @Nonnull Item item) {
		CacheItemDefinition def = CacheItemDefinition.get(item.getId());
		if (def == null) {
			return;
		}
		if (def.noted == -1) {
			return;
		}
		int amount = player.getInventory().getCount(item.getId());
		if (amount <= 0) {
			return;
		}
		ItemDefinition itemDef = ItemDefinition.forId(def.noted);
		if (itemDef == null) {
			return;
		}
		if (!itemDef.isStackable() && amount > player.getInventory().freeSlots()) {
			amount = player.getInventory().freeSlots();
		}
		player.getInventory().remove(new Item(item.getId(), amount));
		player.getInventory().add(new Item(def.noted, amount));
	}

	@Override
	public Container[] getItemsKeptOnDeath(@Nonnull Player player) {
		int count = 3;
		if (player.isBronzeMember())
			count ++;
		if (player.isSilverMember())
			count ++;
		if (player.isGoldMember())
			count ++;
		if (player.isPlatinumMember())
			count ++;
		if (player.isDiamondMember())
			count ++;
		if (player.getCombatState().getPrayer(Prayers.PROTECT_ITEM)
				|| player.getAttribute("protectItem") == Boolean.TRUE) {
			count++;
			player.removeAttribute("protectItem");
		}
		if (player.getCombatState().getSkullTicks() > 0)
			count -= 3;
		List<Item> items = new ArrayList<>();
		player.getInventory().stream().filter(Objects::nonNull).forEach(items::add);
		player.getEquipment().stream().filter(Objects::nonNull).forEach(items::add);
		Container topItems = new Container(Container.Type.NEVER_STACK, count);
		Container lostItems = new Container(Container.Type.STANDARD, Inventory.SIZE + Equipment.SIZE);
		Collections.sort(items, (o1, o2) -> {

			if (o1.getDefinition2().getCost() > o2.getDefinition2().getCost())
				return -1;
			if (o1.getDefinition2().getCost() == o2.getDefinition2().getCost())
				return o1.getPrice() > o2.getPrice() ? -1 : 1;
			return 1;
		});
		for (int i = 0; i < items.size(); i++) {
			if (i < count) {
				topItems.add(items.get(i));
				continue;
			}
			lostItems.add(items.get(i));
		}

		if (lostItems.contains(20792))
			lostItems.replace(20792, 12810);
		if (lostItems.contains(20794))
			lostItems.replace(20794, 12811);
		if (lostItems.contains(20796))
			lostItems.replace(20796, 12812);
		if (topItems.contains(20792))
			topItems.replace(20792, 12810);
		if (topItems.contains(20794))
			topItems.replace(20794, 12811);
		if (topItems.contains(20796))
			topItems.replace(20796, 12812);

		return new Container[] { topItems, lostItems };
	}

	@Override
	public void gambleFireCapes(@Nonnull Player player, int amount) {
		int capes = player.getInventory().getCount(6570);
		if (amount > capes)
			amount = capes;
		if (amount <= 0)
			return;
		if (playerOwnsItem(player, 13225)) {
			player.getActionSender().sendMessage("You already own a Pet; you may not continue gambling.");
			return;
		}
		for (int i = 0; i < amount; i++) {
			player.getInventory().remove(new Item(6570, 1));
			String title = CacheNPCDefinition.get(2180).getName();
			if (Misc.random(100) == 1) {
				Pet.Pets pets = Pet.Pets.JAD;
				Pet.givePet(player, new Item(pets.getItem()));
				player.getActionSender().removeChatboxInterface();
				return;
			}
			player.getActionSender().sendDialogue(title, DialogueType.NPC, 2180, FacialAnimation.SAD,
					"Unlucky, maybe next time?");
		}
	}

	@Override
	public boolean playerOwnsItem(@Nonnull Player player, int id) {
		Pet.Pets pets = Pet.Pets.from(id);
		if (pets != null && player.getPet() != null)
			return player.getPet().getId() == pets.getNpc();
		return player.getInventory().contains(id) || player.getBank().contains(id);
	}

	private Item getNextItem(@Nonnull Item item) {
		Degradable degradable = Degradable.forItem(item);

		if (degradable == null || isFullyDegraded(item)) {
			return item;
		}

		return degradable.getItems()[ArrayUtils.indexOf(degradable.getItems(), item) + 1];
	}

	/**
	 * Handles the ring of wealth drop rate boost.
	 * 
	 * @param player
	 *            the player wearing the ring
	 * @param imbued
	 *            if ring is imbued
	 * @return
	 */
	public static double handleRingOfWealth(Player player) {
		double increase = 1.0;
		if (player.getEquipment().containsOneItem(2572, 11980, 11982, 11984, 11986, 11988))
			increase += 0.5;
		if (player.getEquipment().containsOneItem(12785, 20786, 20787, 20788, 20789, 20790))
			increase += 1.0;
		return increase;
	}
}
