package org.rs2server.rs2.model.skills;

import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;

import java.util.HashMap;

public class FletchingAction extends ProductionAction {

	public static final HashMap<Integer, FletchingItem[]> fletchingMap = new HashMap<Integer, FletchingItem[]>();
	public static final HashMap<Integer, FletchingGroup> groups = new HashMap<Integer, FletchingGroup>();

	public static final Item BOWSTRING = new Item(1777);
	public static final Animation CUT_ANIM = Animation.create(6702);
	public static final Animation NO_ANIMATION = Animation.create(-1);
	public static final Item FEATHER_15 = new Item(314, 15);
	public static final Item FEATHER_10 = new Item(314, 10);
	public static final Item HEADLESS_ARROW = new Item(53, 15);
	public static final Item CROSSBOW_STRING = new Item(9438);
	public static final Item JAVELIN_SHAFT = new Item(19584, 15);

	public enum FletchingType {

		STRINGING("You add a string to the @item@."),

		CUTTING("You carefully cut the wood into a @item@."),

		ATTACHING("You attach @item@ to @item2@."),

		CUTTING_BRUMA("You carefully cut the Bruma root into a @item@.");

		private String message;

		FletchingType(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

	}

	public enum FletchingGroup {

		LOGS(new Item(1511), new Item[] { new Item(52), new Item(50), new Item(48), new Item(9440) }),

		OAK_LOGS(new Item(1521), new Item[] { new Item(54), new Item(56), new Item(9442) }),

		WILLOW_LOGS(new Item(1519), new Item[] { new Item(60), new Item(58), new Item(9444) }),

		MAPLE_LOGS(new Item(1517), new Item[] { new Item(64), new Item(62), new Item(9448) }),

		YEW_LOGS(new Item(1515), new Item[] { new Item(68), new Item(66), new Item(9452) }), MAGIC_LOGS(new Item(1513),
				new Item[] { new Item(72), new Item(70) }),

		BRUMA_ROOT(new Item(20695), new Item[] { new Item(20696) });
		
		private Item itemUsed;
		private Item[] possibleCreations;

		FletchingGroup(Item itemUsed, Item[] possibleCreations) {
			this.setItemUsed(itemUsed);
			this.setPossibleCreations(possibleCreations);
		}

		public void setPossibleCreations(Item[] possibleCreations) {
			this.possibleCreations = possibleCreations;
		}

		public Item[] getPossibleCreations() {
			return possibleCreations;
		}

		public void setItemUsed(Item itemUsed) {
			this.itemUsed = itemUsed;
		}

		public Item getItemUsed() {
			return itemUsed;
		}
	}

	public enum FletchingItem {
		/*
		 * Cutting
		 */
		ARROW_SHAFTS(1, 5, CUT_ANIM, new Item[] { new Item(1511) }, new Item[] { new Item(52, 15) },
				FletchingType.CUTTING),

		SHORTBOW_U(5, 5, CUT_ANIM, new Item[] { new Item(1511) }, new Item[] { new Item(50) }, FletchingType.CUTTING),

		OAK_SHORTBOW_U(20, 25, CUT_ANIM, new Item[] { new Item(1521) }, new Item[] { new Item(54) },
				FletchingType.CUTTING),

		WILLOW_SHORTBOW_U(35, 33.3, CUT_ANIM, new Item[] { new Item(1519) }, new Item[] { new Item(60) },
				FletchingType.CUTTING),

		MAPLE_SHORTBOW_U(50, 50, CUT_ANIM, new Item[] { new Item(1517) }, new Item[] { new Item(64) },
				FletchingType.CUTTING),

		YEW_SHORTBOW_U(65, 67.5, CUT_ANIM, new Item[] { new Item(1515) }, new Item[] { new Item(68) },
				FletchingType.CUTTING),

		MAGIC_SHORTBOW_U(80, 83.3, CUT_ANIM, new Item[] { new Item(1513) }, new Item[] { new Item(72) },
				FletchingType.CUTTING),

		LONGBOW_U(10, 10, CUT_ANIM, new Item[] { new Item(1511) }, new Item[] { new Item(48) }, FletchingType.CUTTING),

		OAK_LONGBOW_U(25, 25, CUT_ANIM, new Item[] { new Item(1521) }, new Item[] { new Item(56) },
				FletchingType.CUTTING),

		WILLOW_LONGBOW_U(40, 41.5, CUT_ANIM, new Item[] { new Item(1519) }, new Item[] { new Item(58) },
				FletchingType.CUTTING),

		MAPLE_LONGBOW_U(55, 58.3, CUT_ANIM, new Item[] { new Item(1517) }, new Item[] { new Item(62) },
				FletchingType.CUTTING),

		YEW_LONGBOW_U(70, 75, CUT_ANIM, new Item[] { new Item(1515) }, new Item[] { new Item(66) },
				FletchingType.CUTTING),

		MAGIC_LONGBOW_U(85, 91.5, CUT_ANIM, new Item[] { new Item(1513) }, new Item[] { new Item(70) },
				FletchingType.CUTTING),

		WOODEN_STOCK(9, 6, CUT_ANIM, new Item[] { new Item(1511) }, new Item[] { new Item(9440) },
				FletchingType.CUTTING),

		OAK_STOCK(24, 16, CUT_ANIM, new Item[] { new Item(1521) }, new Item[] { new Item(9442) },
				FletchingType.CUTTING),

		WILLOW_STOCK(29, 22, CUT_ANIM, new Item[] { new Item(1519) }, new Item[] { new Item(9444) },
				FletchingType.CUTTING),

		TEAK_STOCK(46, 27, CUT_ANIM, new Item[] { new Item(6333) }, new Item[] { new Item(9446) },
				FletchingType.CUTTING),

		MAPLE_STOCK(54, 32, CUT_ANIM, new Item[] { new Item(1517) }, new Item[] { new Item(9448) },
				FletchingType.CUTTING),

		MAHOGANY_STOCK(61, 41, CUT_ANIM, new Item[] { new Item(6332) }, new Item[] { new Item(9446) },
				FletchingType.CUTTING),

		YEW_STOCK(69, 50, CUT_ANIM, new Item[] { new Item(1515) }, new Item[] { new Item(9452) },
				FletchingType.CUTTING),

		BRUMA_ROOT(1, 5, CUT_ANIM, new Item[] { new Item(20695) }, new Item[] { new Item(20696) },
				FletchingType.CUTTING_BRUMA),

		/*
		 * Attaching
		 */
		ARROW_HEADLESS(1, 15, NO_ANIMATION, new Item[] { new Item(52, 15), FEATHER_15 }, new Item[] { HEADLESS_ARROW },
				FletchingType.ATTACHING),

		BRONZE_ARROW(1, 39.5, NO_ANIMATION, new Item[] { new Item(39, 15), HEADLESS_ARROW },
				new Item[] { new Item(882, 15) }, FletchingType.ATTACHING),

		IRON_ARROW(15, 57.5, NO_ANIMATION, new Item[] { new Item(40, 15), HEADLESS_ARROW },
				new Item[] { new Item(884, 15) }, FletchingType.ATTACHING),

		STEEL_ARROW(30, 95, NO_ANIMATION, new Item[] { new Item(41, 15), HEADLESS_ARROW },
				new Item[] { new Item(886, 15) }, FletchingType.ATTACHING),

		MITH_ARROW(45, 132.5, NO_ANIMATION, new Item[] { new Item(42, 15), HEADLESS_ARROW },
				new Item[] { new Item(888, 15) }, FletchingType.ATTACHING),

		ADAMANT_ARROW(60, 170, NO_ANIMATION, new Item[] { new Item(43, 15), HEADLESS_ARROW },
				new Item[] { new Item(890, 15) }, FletchingType.ATTACHING),

		RUNE_ARROW(75, 185, NO_ANIMATION, new Item[] { new Item(44, 15), HEADLESS_ARROW },
				new Item[] { new Item(892, 15) }, FletchingType.ATTACHING),

		DRAGON_ARROW(90, 220.5, NO_ANIMATION, new Item[] { new Item(11237, 15), HEADLESS_ARROW },
				new Item[] { new Item(11212, 15) }, FletchingType.ATTACHING),

		/*
		 * CBOWS attaching
		 */
		BRONZE_CBOW_UNF(9, 12, Animation.create(4436), new Item[] { new Item(9420), new Item(9440) },
				new Item[] { new Item(9454) }, FletchingType.ATTACHING),

		BLURITE_CBOW_UNF(24, 32, Animation.create(4437), new Item[] { new Item(9422), new Item(9442) },
				new Item[] { new Item(9456) }, FletchingType.ATTACHING),

		IRON_CBOW_UNF(29, 44, Animation.create(4438), new Item[] { new Item(9423), new Item(9444) },
				new Item[] { new Item(9457) }, FletchingType.ATTACHING),

		STEEL_CBOW_UNF(46, 54, Animation.create(4439), new Item[] { new Item(9425), new Item(9446) },
				new Item[] { new Item(9459) }, FletchingType.ATTACHING),

		MITHRIL_CBOW_UNF(54, 64, Animation.create(4440), new Item[] { new Item(9427), new Item(9448) },
				new Item[] { new Item(9461) }, FletchingType.ATTACHING),

		ADAMANT_CBOW_UNF(61, 82, Animation.create(4441), new Item[] { new Item(9429), new Item(9450) },
				new Item[] { new Item(9463) }, FletchingType.ATTACHING),

		RUNITE_CBOW_UNF(69, 90, Animation.create(4442), new Item[] { new Item(9431), new Item(9452) },
				new Item[] { new Item(9465) }, FletchingType.ATTACHING),
		/*
		 * Javelins
		 */
		BRONZE_J(3, 15.5, NO_ANIMATION, new Item[] { new Item(19570, 15), JAVELIN_SHAFT },
				new Item[] { new Item(825, 15) }, FletchingType.ATTACHING),

		IRON_J(17, 30.5, NO_ANIMATION, new Item[] { new Item(19572, 15), JAVELIN_SHAFT },
				new Item[] { new Item(826, 15) }, FletchingType.ATTACHING),

		STEEL_J(32, 75, NO_ANIMATION, new Item[] { new Item(19574, 15), JAVELIN_SHAFT },
				new Item[] { new Item(827, 15) }, FletchingType.ATTACHING),

		MITH_J(47, 120.5, NO_ANIMATION, new Item[] { new Item(19576, 15), JAVELIN_SHAFT },
				new Item[] { new Item(828, 15) }, FletchingType.ATTACHING),

		ADAMANT_J(62, 150, NO_ANIMATION, new Item[] { new Item(19578, 15), JAVELIN_SHAFT },
				new Item[] { new Item(829, 15) }, FletchingType.ATTACHING),

		RUNE_J(77, 186.4, NO_ANIMATION, new Item[] { new Item(19580, 15), JAVELIN_SHAFT },
				new Item[] { new Item(830, 15) }, FletchingType.ATTACHING),

		DRAGON_J(92, 225.5, NO_ANIMATION, new Item[] { new Item(19582, 15), JAVELIN_SHAFT },
				new Item[] { new Item(19484, 15) }, FletchingType.ATTACHING),

		/*
		 * CBOWS stringing
		 */
		BRONZE_CBOW(9, 6, Animation.create(6671), new Item[] { new Item(9454), CROSSBOW_STRING },
				new Item[] { new Item(9174) }, FletchingType.STRINGING),

		BLURITE_CBOW(24, 16, Animation.create(6672), new Item[] { new Item(9456), CROSSBOW_STRING },
				new Item[] { new Item(9176) }, FletchingType.STRINGING),

		IRON_CBOW(29, 22, Animation.create(6673), new Item[] { new Item(9457), CROSSBOW_STRING },
				new Item[] { new Item(9177) }, FletchingType.STRINGING),

		STEEL_CBOW(46, 27, Animation.create(6674), new Item[] { new Item(9459), CROSSBOW_STRING },
				new Item[] { new Item(9179) }, FletchingType.STRINGING),

		MITHRIL_CBOW(54, 32, Animation.create(6675), new Item[] { new Item(9461), CROSSBOW_STRING },
				new Item[] { new Item(9181) }, FletchingType.STRINGING),

		ADAMANT_CBOW(61, 41, Animation.create(6676), new Item[] { new Item(9463), CROSSBOW_STRING },
				new Item[] { new Item(9183) }, FletchingType.STRINGING),

		RUNITE_CBOW(69, 50, Animation.create(6677), new Item[] { new Item(9465), CROSSBOW_STRING },
				new Item[] { new Item(9185) }, FletchingType.STRINGING),

		/*
		 * Bolts
		 */
		BRONZE_BOLTS(9, 5, NO_ANIMATION, new Item[] { new Item(9375, 10), FEATHER_10 },
				new Item[] { new Item(877, 10) }, FletchingType.ATTACHING),

		BLURITE_BOLTS(24, 10, NO_ANIMATION, new Item[] { new Item(9376, 10), FEATHER_10 },
				new Item[] { new Item(9139, 10) }, FletchingType.ATTACHING),

		IRON_BOLTS(39, 15, NO_ANIMATION, new Item[] { new Item(9377, 10), FEATHER_10 },
				new Item[] { new Item(9140, 10) }, FletchingType.ATTACHING),

		SILVER_BOLTS(43, 25, NO_ANIMATION, new Item[] { new Item(9382, 10), FEATHER_10 },
				new Item[] { new Item(9145, 10) }, FletchingType.ATTACHING),

		STEEL_BOLTS(46, 35, NO_ANIMATION, new Item[] { new Item(9378, 10), FEATHER_10 },
				new Item[] { new Item(9141, 10) }, FletchingType.ATTACHING),

		MITHRIL_BOLTS(54, 50, NO_ANIMATION, new Item[] { new Item(9379, 10), FEATHER_10 },
				new Item[] { new Item(9142, 10) }, FletchingType.ATTACHING),

		ADAMANT_BOLTS(61, 70, NO_ANIMATION, new Item[] { new Item(9380, 10), FEATHER_10 },
				new Item[] { new Item(9143, 10) }, FletchingType.ATTACHING),

		RUNITE_BOLTS(69, 75, NO_ANIMATION, new Item[] { new Item(9381, 10), FEATHER_10 },
				new Item[] { new Item(9144, 10) }, FletchingType.ATTACHING),

		OPAL_BOLTS(11, 16, NO_ANIMATION, new Item[] { new Item(877, 10), new Item(45, 10) },
				new Item[] { new Item(879, 10) }, FletchingType.ATTACHING),

		JADE_BOLTS(26, 24, NO_ANIMATION, new Item[] { new Item(9139, 10), new Item(9187, 10) },
				new Item[] { new Item(9335, 10) }, FletchingType.ATTACHING),

		PEARL_BOLTS(41, 32, NO_ANIMATION, new Item[] { new Item(9140, 10), new Item(46, 10) },
				new Item[] { new Item(880, 10) }, FletchingType.ATTACHING),

		RED_TOPAZ_BOLTS(48, 39, NO_ANIMATION, new Item[] { new Item(9141, 10), new Item(9188, 10) },
				new Item[] { new Item(9336, 10) }, FletchingType.ATTACHING),

		SAPPHIRE_BOLTS(56, 47, NO_ANIMATION, new Item[] { new Item(9142, 10), new Item(9189, 10) },
				new Item[] { new Item(9337, 10) }, FletchingType.ATTACHING),

		EMERALD_BOLTS(58, 55, NO_ANIMATION, new Item[] { new Item(9142, 10), new Item(9190, 10) },
				new Item[] { new Item(9338, 10) }, FletchingType.ATTACHING),

		RUBY_BOLTS(63, 70, NO_ANIMATION, new Item[] { new Item(9143, 10), new Item(9191, 10) },
				new Item[] { new Item(9339, 10) }, FletchingType.ATTACHING),

		DIAMOND_BOLTS(65, 70, NO_ANIMATION, new Item[] { new Item(9143, 10), new Item(9192, 10) },
				new Item[] { new Item(9340, 10) }, FletchingType.ATTACHING),

		DRAGON_BOLTS(71, 82, NO_ANIMATION, new Item[] { new Item(9144, 10), new Item(9193, 10) },
				new Item[] { new Item(9341, 10) }, FletchingType.ATTACHING),

		ONYX_BOLTS(73, 94, NO_ANIMATION, new Item[] { new Item(9144, 10), new Item(9194, 10) },
				new Item[] { new Item(9342, 10) }, FletchingType.ATTACHING),

		/*
		 * Darts
		 */
		BRONZE_DART(1, 18, NO_ANIMATION, new Item[] { new Item(819, 10), FEATHER_10 }, new Item[] { new Item(806, 10) },
				FletchingType.ATTACHING),

		IRON_DART(22, 38, NO_ANIMATION, new Item[] { new Item(820, 10), FEATHER_10 }, new Item[] { new Item(807, 10) },
				FletchingType.ATTACHING),

		STEEL_DART(37, 75, NO_ANIMATION, new Item[] { new Item(821, 10), FEATHER_10 }, new Item[] { new Item(808, 10) },
				FletchingType.ATTACHING), MITHRIL_DART(52, 112, NO_ANIMATION,
						new Item[] { new Item(822, 10), FEATHER_10 }, new Item[] { new Item(809, 10) },
						FletchingType.ATTACHING),

		ADAMANT_DART(67, 150, NO_ANIMATION, new Item[] { new Item(823, 10), FEATHER_10 },
				new Item[] { new Item(810, 10) }, FletchingType.ATTACHING),

		RUNE_DART(81, 188, NO_ANIMATION, new Item[] { new Item(824, 10), FEATHER_10 }, new Item[] { new Item(811, 10) },
				FletchingType.ATTACHING),

		DRAGON_DART(95, 200, NO_ANIMATION, new Item[] { new Item(11232, 10), FEATHER_10 },
				new Item[] { new Item(11230, 10) }, FletchingType.ATTACHING),

		/*
		 * Arrow tips
		 */
		BRONZE(1, 10, NO_ANIMATION, new Item[] { new Item(39, 10), HEADLESS_ARROW }, new Item[] { new Item(882, 10) },
				FletchingType.ATTACHING),

		IRON(15, 15, NO_ANIMATION, new Item[] { new Item(40, 10), HEADLESS_ARROW }, new Item[] { new Item(884, 10) },
				FletchingType.ATTACHING),

		STEEL(30, 60, NO_ANIMATION, new Item[] { new Item(41, 10), HEADLESS_ARROW }, new Item[] { new Item(886, 10) },
				FletchingType.ATTACHING),

		MITH(45, 70, NO_ANIMATION, new Item[] { new Item(42, 10), HEADLESS_ARROW }, new Item[] { new Item(888, 10) },
				FletchingType.ATTACHING),

		ADDY(60, 88, NO_ANIMATION, new Item[] { new Item(43, 10), HEADLESS_ARROW }, new Item[] { new Item(890, 10) },
				FletchingType.ATTACHING),

		RUNE(75, 100, NO_ANIMATION, new Item[] { new Item(44, 10), HEADLESS_ARROW }, new Item[] { new Item(892, 10) },
				FletchingType.ATTACHING),

		DRAGON(90, 112, NO_ANIMATION, new Item[] { new Item(11237, 10), HEADLESS_ARROW },
				new Item[] { new Item(11212, 10) }, FletchingType.ATTACHING),

		BROAD(35, 54, NO_ANIMATION, new Item[] { new Item(11232, 10), HEADLESS_ARROW },
				new Item[] { new Item(11230, 10) }, FletchingType.ATTACHING),
		/*
		 * Stringing
		 */
		SHORTBOW(5, 5, Animation.create(6678), new Item[] { new Item(50), BOWSTRING }, new Item[] { new Item(841) },
				FletchingType.STRINGING),

		OAK_SHORTBOW(20, 16.5, Animation.create(6679), new Item[] { new Item(54), BOWSTRING },
				new Item[] { new Item(843) }, FletchingType.STRINGING),

		WILLOW_SHORTBOW(35, 33.3, Animation.create(6680), new Item[] { new Item(60), BOWSTRING },
				new Item[] { new Item(849) }, FletchingType.STRINGING),

		MAPLE_SHORTBOW(50, 50, Animation.create(6681), new Item[] { new Item(64), BOWSTRING },
				new Item[] { new Item(853) }, FletchingType.STRINGING),

		YEW_SHORTBOW(65, 65, Animation.create(6682), new Item[] { new Item(68), BOWSTRING },
				new Item[] { new Item(857) }, FletchingType.STRINGING),

		MAGIC_SHORTBOW(80, 83.3, Animation.create(6683), new Item[] { new Item(72), BOWSTRING },
				new Item[] { new Item(861) }, FletchingType.STRINGING),

		LONGBOW(10, 10, Animation.create(6684), new Item[] { new Item(48), BOWSTRING }, new Item[] { new Item(839) },
				FletchingType.STRINGING),

		OAK_LONGBOW(25, 25, Animation.create(6685), new Item[] { new Item(56), BOWSTRING },
				new Item[] { new Item(845) }, FletchingType.STRINGING),

		WILLOW_LONGBOW(40, 41.5, Animation.create(6686), new Item[] { new Item(58), BOWSTRING },
				new Item[] { new Item(847) }, FletchingType.STRINGING),

		MAPLE_LONGBOW(55, 58.3, Animation.create(6687), new Item[] { new Item(62), BOWSTRING },
				new Item[] { new Item(851) }, FletchingType.STRINGING),

		YEW_LONGBOW(70, 75, Animation.create(6688), new Item[] { new Item(66), BOWSTRING },
				new Item[] { new Item(855) }, FletchingType.STRINGING),

		MAGIC_LONGBOW(85, 91.5, Animation.create(6689), new Item[] { new Item(70), BOWSTRING },
				new Item[] { new Item(859) }, FletchingType.STRINGING);

		private int level;
		private double xp;
		private Animation animation;
		private Item[] materials;
		private Item[] producedItem;
		private FletchingType type;

		FletchingItem(int level, double xp, Animation animation, Item[] materials, Item producedItem[],
				FletchingType type) {
			this.level = level;
			this.xp = xp;
			this.animation = animation;
			this.materials = materials;
			this.producedItem = producedItem;
			this.type = type;
		}

		public int getLevel() {
			return level;
		}

		public double getXp() {
			return xp;
		}

		public Animation getAnimation() {
			return animation;
		}

		public Item[] getMaterials() {
			return materials;
		}

		public Item[] getProducedItem() {
			return producedItem;
		}

		public FletchingType getType() {
			return type;
		}

	}

	private FletchingItem item;
	private FletchingType type;
	private int cycles;
	private int productionAmount;

	static {
		for (FletchingItem item : FletchingItem.values()) {
			for (int i = 0; i < item.getMaterials().length; i++) {
				if (fletchingMap.containsKey(item.getMaterials()[i].getId())) {
					FletchingItem[] items = fletchingMap.get(item.getMaterials()[i].getId());
					FletchingItem[] newItems = new FletchingItem[items.length + 1];
					System.arraycopy(items, 0, newItems, 0, items.length);
					newItems[items.length] = item;
					fletchingMap.put(item.getMaterials()[i].getId(), newItems);
				} else {
					fletchingMap.put(item.getMaterials()[i].getId(), new FletchingItem[] { item });
				}
			}
		}
		for (FletchingGroup group : FletchingGroup.values()) {
			groups.put(group.getItemUsed().getId(), group);
		}
	}

	public FletchingAction(Mob mob, int productionAmount, FletchingItem item) {
		super(mob);
		this.productionAmount = productionAmount;
		this.item = item;
		this.type = item.getType();
		this.cycles = getTicksForType(type);
	}

	private static int getTicksForType(FletchingType fletchType) {
		switch (fletchType) {
		case STRINGING:
			return 4;
		case CUTTING:
			return 2;
		case CUTTING_BRUMA:
			return 2;
		case ATTACHING:
			return 1;
		}
		return 1;
	}

	@Override
	public Item getFailItem() {
		return null;
	}

	@Override
	public Item[] getRewards() {
		return item.getProducedItem();
	}

	@Override
	public Item[] getConsumedItems() {
		return item.getMaterials();
	}

	@Override
	public int getRequiredLevel() {
		return item.getLevel();
	}

	@Override
	public int getSkill() {
		return Skills.FLETCHING;
	}

	@Override
	public double getExperience() {
		return item.getXp();
	}

	@Override
	public String getLevelTooLowMessage() {
		return "Your fletching level is not high enough for this!";
	}

	@Override
	public String getSuccessfulProductionMessage() {
		switch (type) {
		case CUTTING:
			return type.getMessage().replace("@item@", item.getProducedItem()[0].getDefinition2().getName());
		case CUTTING_BRUMA:
			return type.getMessage().replace("@item@", item.getProducedItem()[0].getDefinition2().getName());
		case STRINGING:
			return type.getMessage().replace("@item@",
					item.getProducedItem()[0].getDefinition2().getName().contains("crossbow") ? "crossbow" : "bow");
		case ATTACHING:
			if (!item.getProducedItem()[0].getDefinition2().getName().contains("c'bow")
					&& !item.getProducedItem()[0].getDefinition2().getName().contains("crossbow"))
				return type.getMessage()
						.replace("@item@",
								"the "+item.getMaterials()[0].getDefinition2().getName().toLowerCase()
										+ (!item.getMaterials()[0].getDefinition2().getName().endsWith("s") ? "s" : ""))
						.replace("@item2@", getItemType(item.getMaterials()[1]));
			else
				return "You attach the limbs and the stock and make an unstrung crossbow";
		}
		return type.getMessage().replace("@item@", item.getProducedItem()[0].getDefinition2().getName());
	}

	@Override
	public String getFailProductionMessage() {
		return "shouldnt happen";
	}

	private String getItemType(Item i) {
		String name = i.getDefinition2().getName();
		if (name.contains("arrow")) {
			return "arrows";
		} else if (name.contains("bolt")) {
			return "bolts";
		}
		return name;
	}

	@Override
	public Animation getAnimation() {
		return item.getAnimation();
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	@Override
	public boolean isSuccessfull() {
		return true;
	}

	public static FletchingItem getItemByGroupIndex(FletchingGroup group, int index) {
		FletchingItem[] items = fletchingMap.get(group.getItemUsed().getId());
		for (FletchingItem item : items)
			if (item.getProducedItem()[0].getId() == group.getPossibleCreations()[index].getId())
				return item;

		return null;
	}

	public static FletchingItem getItemForId(int used, int usedWith, boolean usingInterface) {
		FletchingItem[] items = fletchingMap.get(used);
		if (items == null)
			items = fletchingMap.get(usedWith);
		if (items == null)
			return null;
		for (FletchingItem item : items) {
			Item[] materials = item.getMaterials();
			if (item.getType() == FletchingType.CUTTING) {
				if (hasKnife(used, usedWith)) {
					return item;
				}
			}
			if (item.getType() == FletchingType.CUTTING_BRUMA) {
				if (hasKnife(used, usedWith))
					return item;
			} else if ((materials[0].getId() == used && item.getMaterials()[1].getId() == usedWith)
					|| (materials[0].getId() == usedWith && item.getMaterials()[1].getId() == used)) {
				return item;
			}
		}
		return null;
	}

	private static boolean hasKnife(int used, int usedWith) {
		return used == 946 || usedWith == 946;
	}

	@Override
	public int getCycleCount() {
		return cycles;
	}

	@Override
	public int getProductionCount() {
		return productionAmount;
	}

	@Override
	public boolean canProduce() {
		return true;
	}

}
