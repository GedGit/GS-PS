package org.rs2server.rs2.model.container;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.GroundItem;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.container.Container.Type;
import org.rs2server.rs2.model.container.impl.InterfaceContainerListener;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.player.RequestManager.RequestState;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.TextUtils;

public class OldDuel {

    /**
     * The duel size.
     */
    public static final int SIZE = 28;

    /**
     * The player inventory interface.
     */
    public static final int PLAYER_INVENTORY_INTERFACE = 336;

    /**
     * The duel interface.
     */
    public static final int DUEL_INTERFACE = 107;

    /**
     * The 2nd duel interface.
     */
    public static final int SECOND_DUEL_SCREEN = 106;


    public static void handleRules(Player player, int buttonId) {
        Player p2 = player.getDueling().getOpponent();
        if (buttonId == 77) {
            return;
        }
        int rule = -1;
        int slot = -1;
        for (int index = 0; index < player.getDueling().DUELING_BUTTON_IDS.length; index++) {
            if (player.getDueling().DUELING_BUTTON_IDS[index] == buttonId) {
                rule = player.getDueling().RULE_IDS[index];
                if (rule >= 11) {
                    slot = player.getDueling().DUEL_SLOT_IDS[rule - 11];
                }
                break;
            }
        }
        if (rule == -1) {
            return;
        }
        if (rule >= 11 && slot != -1) {
            if (!player.getDueling().rules[rule]) {
                player.getDueling().duelSpaceReq++;
            } else {
                player.getDueling().duelSpaceReq--;
            }
        }
        if (rule >= 11) {
            if (player.getInventory().freeSlots() < player.getDueling().duelSpaceReq || p2.getInventory().freeSlots() < player.getDueling().duelSpaceReq) {
                player.getActionSender().sendMessage("You or your opponent don't have the required space to set this rule.");
                return;
            }
        }
        if (player.getDueling().rules[rule]) {
            player.getDueling().totalDuelConfigs -= player.getDueling().DUELING_CONFIG_IDS[rule];
            p2.getDueling().totalDuelConfigs -= player.getDueling().DUELING_CONFIG_IDS[rule];
            player.getDueling().rules[rule] = false;
            p2.getDueling().rules[rule] = false;
        } else {
            player.getDueling().totalDuelConfigs += player.getDueling().DUELING_CONFIG_IDS[rule];
            p2.getDueling().totalDuelConfigs += player.getDueling().DUELING_CONFIG_IDS[rule];
            player.getDueling().rules[rule] = true;
            p2.getDueling().rules[rule] = true;
        }
        player.getActionSender().sendString(107, 51, "");
        p2.getActionSender().sendString(107, 51, "");
        player.getRequestManager().setState(RequestState.PARTICIPATING);
        p2.getRequestManager().setState(RequestState.PARTICIPATING);
        player.getActionSender().sendConfig(286, player.getDueling().totalDuelConfigs);
        p2.getActionSender().sendConfig(286, player.getDueling().totalDuelConfigs);
    }

    private static void beginCountDown(final Player player) {
        World.getWorld().submit(new Tickable(2) {
            int i = 3;

            @Override
            public void execute() {
                if (player == null) {
                    this.stop();
                    return;
                }
                if (i > 0) {
                    player.forceChat("" + i);
                } else {
                    player.getDueling().setDuelStatus(5);
                    player.forceChat("Fight!");
                    this.stop();
                }
                i--;
            }
        });
    }

    public static void refreshDuelRules(Player player, Player opp) {
        /*
		 * Resets the actual buttons first.
		 */
        for (int i = 0; i < player.getDueling().rules.length; i++) {
            if (player.getDueling().rules[i]) {
                player.getDueling().totalDuelConfigs -= player.getDueling().DUELING_CONFIG_IDS[i];
                player.getDueling().rules[i] = false;
                opp.getDueling().rules[i] = false;
            }
        }
        player.getActionSender().sendConfig(286, player.getDueling().totalDuelConfigs);
        opp.getActionSender().sendConfig(286, player.getDueling().totalDuelConfigs);
		/*
		 * Then we reset the serversided configs for the buttons.
		 */
        for (int i = 0; i < player.getDueling().rules.length; i++) {
            player.getDueling().rules[i] = false;
            opp.getDueling().rules[i] = false;
        }
        player.getActionSender().sendString(107, 56, "");
        opp.getActionSender().sendString(107, 83, "No Drinks");
    }

    private static void buildRulesString(Player player, Player opp) {
        boolean[] writeStrings = {true, true, true, true, true};
		/*
		 * Check the rules, and tell the array if we aren't going to write our strings on the interface.
		 */
        if (!(player.getDueling().duelSpaceReq > 0))
            writeStrings[0] = false;
        if (!player.getDueling().rules[Dueling.NO_FOOD])
            writeStrings[1] = false;
        if (!player.getDueling().rules[Dueling.NO_PRAYER])
            writeStrings[2] = false;
		/*
		 * Makes sure the interface is clean before we write stuff on it (even if we don't).
		 */
        for (int i : player.getDueling().BEFORE_THE_DUEL_STARTS_CHILD_IDS) {
            player.getActionSender().sendString(106, i, "");
            opp.getActionSender().sendString(106, i, "");
        }
        int nextString = 0;
        int nextChild = 0;
		/*
		 * Write all the needed strings.
		 */
        for (boolean write : writeStrings) {
            if (write) {
                player.getActionSender().sendString(106, player.getDueling().BEFORE_THE_DUEL_STARTS_CHILD_IDS[nextChild], player.getDueling().BEFORE_THE_DUEL_STARTS[nextString]);
                opp.getActionSender().sendString(106, player.getDueling().BEFORE_THE_DUEL_STARTS_CHILD_IDS[nextChild], player.getDueling().BEFORE_THE_DUEL_STARTS[nextString]);
                nextChild++;
            }
            nextString++;
        }
		/*
		 * Makes sure the interface is clean before we write stuff on it (even if we don't).
		 */
        for (int i : player.getDueling().DURING_THE_DUEL_CHILD_IDS) {
            player.getActionSender().sendString(106, i, "");
            opp.getActionSender().sendString(106, i, "");
        }
		/*
		 * This makes the correct rules(according to the rules array, go in the highest child available.
		 */
        nextString = 0;
        nextChild = 0;
        for (boolean rule : player.getDueling().rules) {
            if (nextString == 11) {
                break;
            }
            if (rule) {
                player.getActionSender().sendString(106, player.getDueling().DURING_THE_DUEL_CHILD_IDS[nextChild], player.getDueling().RULES[nextString]);
                opp.getActionSender().sendString(106, player.getDueling().DURING_THE_DUEL_CHILD_IDS[nextChild], player.getDueling().RULES[nextString]);
                nextChild++;
            }
            nextString++;
        }
    }

    public static void openInterface(Player player, Player p2) {
        //player.getDueling().setDuelStatus(1);
        //p2.getDueling().setDuelStatus(1);
        player.getRequestManager().setAcquaintance(p2);
        p2.getRequestManager().setAcquaintance(player);
        player.getActionSender().sendString(107, 99, p2.getName());
        player.getActionSender().sendString(107, 96, "" + p2.getSkills().getCombatLevel());
        p2.getActionSender().sendString(107, 99, player.getName());
        p2.getActionSender().sendString(107, 96, "" + player.getSkills().getCombatLevel());
//		player.getActionSender().sendString(107, 51, "");//Replaces the "waiting for other player".
//		player.getActionSender().sendConfig(286, 0);
//		p2.getActionSender().sendString(107, 51, "");//Replaces the "waiting for other player".
//		p2.getActionSender().sendConfig(286, 0);
        configureDuel(player, p2);
        configureDuel(p2, player);
//		refreshDuelRules(player, p2);
//		refreshDuelRules(p2, player);
//		player.getActionSender().sendString(107, 56, "");
//		p2.getActionSender().sendString(107, 83, "No Drinks");

    }

    public static void configureDuel(Player player, Player p2) {
        player.getActionSender().sendInterfaceInventory(109);
        player.getActionSender().sendInterface(107, false);
        player.getActionSender().sendCS2Script(917, new Object[]{-1, -1}, "ii");
        player.getActionSender().sendCS2Script(149, Constants.OFFER_OPTS, "IviiiIsssss");
        player.getActionSender().sendAccessMask(1086, 109, 0, 0, 27);
        player.getActionSender().sendAccessMask(1086, 107, 102, 0, 27);
        player.getActionSender().sendAccessMask(2, 107, 104, 0, 27);
        player.getActionSender().sendConfig(286, 8192);
        player.getInterfaceState().addListener(player.getInventory(), new InterfaceContainerListener(player, -1, 64209, 93));
        player.getInterfaceState().addListener(player.getDuelContainer(), new InterfaceContainerListener(player, -1, 64168, 134));
        player.getDuelContainer().add(new Item(4151, 1));
        player.getInterfaceState().addListener(p2.getDuelContainer(), new InterfaceContainerListener(player, -2, 60937, 32902));
    }

    /**
     * Offers an item.
     *
     * @param player The player.
     * @param slot   The slot in the player's inventory.
     * @param id     The item id.
     * @param amount The amount of the item to offer.
     */
    public static void offerItem(Player player, int slot, int id, int amount) {
//		if(player.getInterfaceState().getCurrentInterface() != DUEL_INTERFACE) {
//			return;
//		}
        player.getActionSender().removeChatboxInterface();
        Item item = player.getInventory().get(slot);
        if (item == null) {
            return; // invalid packet, or client out of sync
        }
        if (item.getId() != id) {
            return; // invalid packet, or client out of sync
        }
        if (!item.getDefinition().isTradable()) {
            player.getActionSender().sendMessage("You cannot stake this item.");
            return;
        }
        Player partner = player.getRequestManager().getAcquaintance();
        if (partner == null) {
            return;
        }
        player.getActionSender().sendString(107, 51, "");
        partner.getActionSender().sendString(107, 51, "");
        player.getRequestManager().setState(RequestState.PARTICIPATING);
        partner.getRequestManager().setState(RequestState.PARTICIPATING);
        boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
        player.getInventory().setFiringEvents(false);
        try {
            int transferAmount = player.getInventory().getCount(id);
            if (transferAmount >= amount) {
                transferAmount = amount;
            } else if (transferAmount == 0) {
                return; // invalid packet, or client out of sync
            }

            if (player.getDuelContainer().add(new Item(item.getId(), transferAmount), -1)) {
                player.getInventory().remove(new Item(item.getId(), transferAmount));
            }
            player.getInventory().fireItemsChanged();
        } finally {
            player.getInventory().setFiringEvents(inventoryFiringEvents);
            //updateFirstScreen(player);
            player.getActionSender().removeChatboxInterface();
        }
    }

    /**
     * Removes an offered an item.
     *
     * @param player The player.
     * @param slot   The slot in the player's inventory.
     * @param id     The item id.
     * @param amount The amount of the item to offer.
     */
    public static void removeItem(Player player, int slot, int id, int amount) {
        if (player.getInterfaceState().getCurrentInterface() != DUEL_INTERFACE) {
            return;
        }
        player.getActionSender().removeChatboxInterface();
        Player partner = player.getRequestManager().getAcquaintance();
        if (partner == null) {
            return;
        }
        player.getActionSender().sendString(107, 51, "");
        partner.getActionSender().sendString(107, 51, "");
        player.getRequestManager().setState(RequestState.PARTICIPATING);
        partner.getRequestManager().setState(RequestState.PARTICIPATING);
        boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
        player.getInventory().setFiringEvents(false);
        try {
            Item item = player.getDuelContainer().get(slot);
            if (item == null || item.getId() != id) {
                return; // invalid packet, or client out of sync
            }
            int transferAmount = player.getDuelContainer().getCount(id);
            if (transferAmount >= amount) {
                transferAmount = amount;
            } else if (transferAmount == 0) {
                return; // invalid packet, or client out of sync
            }

            if (player.getInventory().add(new Item(item.getId(), transferAmount), -1)) {
                player.getDuelContainer().remove(new Item(item.getId(), transferAmount));
            }
            player.getInventory().fireItemsChanged();
        } finally {
            player.getInventory().setFiringEvents(inventoryFiringEvents);
            //updateFirstScreen(player);
            player.getActionSender().removeChatboxInterface();
        }
    }

    public static void acceptDuel(Player player, int screenStage) {
        Player partner = player.getRequestManager().getAcquaintance();
        if (partner == null) {
            return;
        }
        switch (screenStage) {
            case 1:
                if (player.getInventory().freeSlots() < partner.getDuelContainer().size()) {
                    player.getActionSender().sendMessage("You do not have enough free inventory slots.");
                    player.getActionSender().sendString(107, 51, "");
                    partner.getActionSender().sendString(107, 51, "");
                    player.getRequestManager().setState(RequestState.PARTICIPATING);
                    partner.getRequestManager().setState(RequestState.PARTICIPATING);
                    return;
                }
                for (Item item : partner.getDuelContainer().toArray()) {
                    if (item != null && item.getDefinition().isStackable() && player.getInventory().getCount(item.getId()) > 0) {
                        long partnerCount = player.getInventory().getCount(item.getId());
                        long myCount = item.getCount();
                        long totalCount = (partnerCount + myCount);
                        if (totalCount > Integer.MAX_VALUE) {
                            player.getActionSender().sendMessage("You cannot accept this amount of " + item.getDefinition().getName() + (item.getDefinition().getName().endsWith("s") ? "" : "s") + ".");
                            player.getActionSender().sendString(107, 51, "");
                            partner.getActionSender().sendString(107, 51, "");
                            player.getRequestManager().setState(RequestState.PARTICIPATING);
                            partner.getRequestManager().setState(RequestState.PARTICIPATING);
                            return;
                        }
                    }
                }
                if (partner.getInventory().freeSlots() < player.getDuelContainer().size()) {
                    player.getActionSender().sendMessage("The other player does not have enough free inventory slots.");
                    player.getActionSender().sendString(107, 51, "");
                    partner.getActionSender().sendString(107, 51, "");
                    player.getRequestManager().setState(RequestState.PARTICIPATING);
                    partner.getRequestManager().setState(RequestState.PARTICIPATING);
                    return;
                }
                for (Item item : player.getDuelContainer().toArray()) {
                    if (item != null && item.getDefinition().isStackable() && partner.getInventory().getCount(item.getId()) > 0) {
                        long partnerCount = partner.getInventory().getCount(item.getId());
                        long myCount = item.getCount();
                        long totalCount = (partnerCount + myCount);
                        if (totalCount > Integer.MAX_VALUE) {
                            player.getActionSender().sendMessage("The other player cannot accept this amount of " + item.getDefinition().getName() + (item.getDefinition().getName().endsWith("s") ? "" : "s") + ".");
                            player.getActionSender().sendString(107, 51, "");
                            partner.getActionSender().sendString(107, 51, "");
                            player.getRequestManager().setState(RequestState.PARTICIPATING);
                            partner.getRequestManager().setState(RequestState.PARTICIPATING);
                            return;
                        }
                    }
                }
                if (partner.getRequestManager().getState() == RequestState.CONFIRM_1) {
                    secondScreen(player);
                    return;
                }
                //haters gonna hate
                player.getActionSender().sendString(107, 51, "Waiting for other player...                                     ");
                partner.getActionSender().sendString(107, 51, "Other player has accepted                                     ");
                player.getRequestManager().setState(RequestState.CONFIRM_1);
                break;
            case 2:
                if (partner.getRequestManager().getState() == RequestState.CONFIRM_2) {
                    removeWornEquipment(player);
                    removeWornEquipment(partner);
                    player.getDueling().setDuelStatus(4);
                    partner.getDueling().setDuelStatus(4);
                    beginCountDown(player);
                    beginCountDown(partner);
                    Location[] teleports = getArenaTeleport(player);
                    Location[] obteleports = getObstacleTeleport(player);
                    int random = TextUtils.random(1);
                    if (!player.getDueling().rules[8]) {
                        player.setTeleportTarget(random == 0 ? teleports[0] : teleports[1]);
                        partner.setTeleportTarget(random == 0 ? teleports[1] : teleports[0]);
                    } else {
                        player.setTeleportTarget(random == 0 ? obteleports[0] : obteleports[1]);
                        partner.setTeleportTarget(random == 0 ? obteleports[1] : obteleports[0]);
                    }
                    player.getActionSender().removeAllInterfaces();
                    partner.getActionSender().removeAllInterfaces();
                    //player.getActionSender().sendHintArrow(partner, 1, 0);
                    //partner.getActionSender().sendHintArrow(player, 1, 0);
                    //player.getActionSender().sendHintArrow(partner, 10, player.getIndex(), 0);
                    //partner.getActionSender().sendHintArrow(player, 10, partner.getIndex(), 0);
                } else {
                    player.getRequestManager().setState(RequestState.CONFIRM_2);
                    player.getActionSender().sendString(SECOND_DUEL_SCREEN, 44, "Waiting for other player...");
                    partner.getActionSender().sendString(SECOND_DUEL_SCREEN, 44, "Other player has accepted");
                }
                break;
        }
    }

    private static void removeWornEquipment(Player player) {
		/*
		 * Equipment.
		 * We loop through all equipment rules.
		 */
        for (int rule = 11; rule < player.getDueling().rules.length; rule++) {
			/*
			 * Make sure the rule applies.
			 */
            if (player.getDueling().rules[rule]) {
				/*
				 * If that is so, we get the equipment slot.
				 */
                int slot = player.getDueling().DUEL_SLOT_IDS[rule - 11];
				/*
				 * Check if we're actually wearing an item in this slot..
				 */
                if (player.getEquipment().get(slot) != null) {
					/*
					 * If so, we check if we can add it to the inventory.
					 */
                    if (player.getInventory().add(player.getEquipment().get(slot))) {
						/*
						 * If we can, (which we REALLY should be able to, unless I failed)
						 * we reset the equipment slot.
						 */
                        player.getEquipment().set(slot, null);
						/*
						 * And print a warning if we can't.
						 */
                    } else {
                        System.out.println("Duel wearing bug@!!!!");
                    }
                }
            }
        }
    }

    public static void setTied(Player player) {
        player.getDueling().setDied(true);
    }

    public static void finishDuel(final Player player, boolean lost, boolean dc) {
        if (player.isPlayer())
            if (player != null && player.getDueling() != null && player.getDueling().getDuelStatus() <= 0) {
                return;
            }
        boolean tie = false;
        if (player.getDueling() != null)
            player.getDueling().setDuelStatus(0);
        if (player != null && player.getDueling() != null && player.getDueling().getOpponent() != null) {
            final Player opp = player.getDueling().getOpponent();
            opp.getDueling().setDuelStatus(0);
            if (opp.getDueling().isDead()) {
                tie = true;
            }
            if (tie) {
                opp.getActionSender().sendMessage("The duel ended in a tie.");
                player.getActionSender().sendMessage("The duel ended in a tie.");
                giveBack(opp);
                giveBack(player);
                player.getDueling().setDied(false);
                opp.getDueling().setDied(false);
            } else if (!tie) {
                if (lost) {
                    opp.getActionSender().sendMessage("Well done! You have defeated " + player.getName() + "!");
                }
                final Container winables = new Container(Type.STANDARD, Inventory.SIZE);
                for (Item item : player.getDuelContainer().toArray()) {
                    if (item != null) {
                        winables.add(item);//add Persons items who lost to winables
                    }
                }
                player.getDuelContainer().clear();
                player.getActionSender().sendMessage("Better luck next time!");
                for (Item item : winables.toArray()) {
                    if (item != null) {
                        if (!opp.getInventory().add(item)) {
                            opp.getActionSender().sendMessage("You don't have enough space in your inventory.");
                            opp.getActionSender().sendMessage("The rest of your winnings was dropped onto the ground.");
                            World.getWorld().createGroundItem(new GroundItem(opp.getName(), item, opp.getLocation()), opp);
                        }
                    }
                }
                World.getWorld().submit(new Tickable(2) {
                    @Override
                    public void execute() {
                        this.stop();
                        opp.getActionSender().sendInterface(110, true);
                        opp.getActionSender().sendUpdateItems(-1, 33, 136, winables.toArray());
                        final Object[] winableScript = new Object[]{"", "", "", "", "", -1, 0, 6, 6, 136, 110 << 16 | 33};
                        opp.getActionSender().sendCS2Script(149, winableScript, "IviiiIsssss");
                        opp.getActionSender().sendAccessMask(1026, 110 << 16 | 33, 33, 0, 28);
                        opp.getActionSender().sendString(110, 23, player.getName());
                        opp.getActionSender().sendString(110, 22, "" + player.getSkills().getCombatLevel());
                        winables.clear();
                    }
                });
                giveBack(opp);
            }
            //player.getActionSender().sendHintArrow(opp, -1, 0, 0);
            //opp.getActionSender().sendHintArrow(player, -1, 0, 0);
            opp.resetVariousInformation();
            opp.setDefaultAnimations();
            opp.getActionSender().sendBonuses();
            opp.setTeleportTarget(Location.create(3360 + TextUtils.random(19), 3274 + TextUtils.random(3), 0));
            refreshDuelRules(player, opp);
        }
        if (!dc) {
            player.setTeleportTarget(Location.create(3360 + TextUtils.random(19), 3274 + TextUtils.random(3), 0));
        } else if (dc) {
            player.setLocation(Location.create(3360 + TextUtils.random(19), 3274 + TextUtils.random(3), 0));
        }
        player.getActionSender().sendBonuses();
        player.setDefaultAnimations();
        player.resetVariousInformation();
    }

    private static void giveBack(Player player) {
        for (Item item : player.getDuelContainer().toArray()) {
            if (item != null) {
                player.getInventory().add(item);
            }
        }
        player.getDuelContainer().clear();
    }

    private static Location[] getArenaTeleport(Player player) {
        final int arenaChoice = TextUtils.random(2);
        Location[] locations = new Location[2];
        int[] arenaBoundariesX = {3337, 3367, 3336};
        int[] arenaBoundariesY = {3246, 3227, 3208};
        int[] maxOffsetX = {14, 14, 16};
        int[] maxOffsetY = {10, 10, 10};
        int finalX = arenaBoundariesX[arenaChoice] + TextUtils.random(maxOffsetX[arenaChoice]);
        int finalY = arenaBoundariesY[arenaChoice] + TextUtils.random(maxOffsetY[arenaChoice]);
        locations[0] = Location.create(finalX, finalY, 0);
        if (player.getDueling().rules[1]) {
            int direction = TextUtils.random(1);
            if (direction == 0) {
                finalX--;
            } else {
                finalY++;
            }
        } else {
            finalX = arenaBoundariesX[arenaChoice] + TextUtils.random(maxOffsetX[arenaChoice]);
            finalY = arenaBoundariesY[arenaChoice] + TextUtils.random(maxOffsetY[arenaChoice]);
        }
        locations[1] = Location.create(finalX, finalY, 0);
        return locations;
    }

    private static Location[] getObstacleTeleport(Player player) {
        final int arenaChoice = TextUtils.random(2);
        Location[] locations = new Location[2];
        int[] arenaBoundariesX = {3337, 3355, 3342};
        int[] arenaBoundariesY = {3227, 3235, 3232};
        int[] maxOffsetX = {3, 3, 4};
        int[] maxOffsetY = {2, 2, 2};
        int finalX = arenaBoundariesX[arenaChoice] + TextUtils.random(maxOffsetX[arenaChoice]);
        int finalY = arenaBoundariesY[arenaChoice] + TextUtils.random(maxOffsetY[arenaChoice]);
        locations[0] = Location.create(finalX, finalY, 0);
        if (player.getDueling().rules[1]) {
            int direction = TextUtils.random(1);
            if (direction == 0) {
                finalX -= 1;
            } else {
                finalY += 1;
            }
        } else {
            finalX = arenaBoundariesX[arenaChoice] + TextUtils.random(maxOffsetX[arenaChoice]);
            finalY = arenaBoundariesY[arenaChoice] + TextUtils.random(maxOffsetY[arenaChoice]);
        }
        locations[1] = Location.create(finalX, finalY, 0);
        return locations;
    }

    @SuppressWarnings("unused")
    public static void secondScreen(Player player) {
        Player partner = player.getRequestManager().getAcquaintance();
        if (partner == null) {
            return;
        }
        clearSecondScreen(player);
        clearSecondScreen(partner);
        player.getActionSender().removeChatboxInterface();
        partner.getActionSender().removeChatboxInterface();
        int myFreeSpaces = player.getDuelContainer().freeSlots();
        int otherFreeSpaces = partner.getDuelContainer().freeSlots();
        boolean myOneLine = myFreeSpaces >= 14;
        boolean otherOneLine = otherFreeSpaces >= 14;
        if (!myOneLine) {
            Container firstHalf = new Container(Type.STANDARD, 14);
            Container secondHalf = new Container(Type.STANDARD, 14);
            for (Item item : player.getDuelContainer().toArray()) {
                if (!firstHalf.add(item)) {
                    secondHalf.add(item);
                }
            }
            String firstHalfString = listContainerContents(firstHalf).replace("<col=FFFFFF>Absolutely nothing!", "");
            String secondHalfString = listContainerContents(secondHalf).replace("<col=FFFFFF>Absolutely nothing!", "");
            //player.getActionSender().sendString(106, 31, firstHalfString);
            //partner.getActionSender().sendString(106, 32, secondHalfString);
            //player.getActionSender().sendString(334, 41, firstHalfString);
            //player.getActionSender().sendString(334, 42, secondHalfString);
        } else {
            //player.getActionSender().sendString(106, 37, listContainerContents(player.getDuelContainer()));
            //partner.getActionSender().sendString(106, 40, listContainerContents(player.getDuelContainer()));
        }
        player.getDueling().setDuelStatus(2);
        partner.getDueling().setDuelStatus(2);
        buildRulesString(partner, player);
        player.getActionSender().sendInterface(106, false);
        partner.getActionSender().sendInterface(106, false);
    }

    private static void clearSecondScreen(Player player) {
        player.getActionSender().sendString(106, 31, "");
        player.getActionSender().sendString(106, 32, "");
        //Before duel starts strings
        player.getActionSender().sendString(106, 49, "");
        player.getActionSender().sendString(106, 34, "");
        player.getActionSender().sendString(106, 35, "");
        player.getActionSender().sendString(106, 37, "");
        player.getActionSender().sendString(106, 38, "");
        //Durin the duel:
        player.getActionSender().sendString(106, 40, "");
        player.getActionSender().sendString(106, 41, "");
        player.getActionSender().sendString(106, 42, "");
        player.getActionSender().sendString(106, 43, "");
        player.getActionSender().sendString(106, 45, "");
        player.getActionSender().sendString(106, 46, "");
        player.getActionSender().sendString(106, 47, "");
        player.getActionSender().sendString(106, 48, "");
        player.getActionSender().sendString(106, 50, "");
        player.getActionSender().sendString(106, 51, "");
        player.getActionSender().sendString(106, 52, "");
        //Waitin for other..
        player.getActionSender().sendString(106, 44, "");
    }

    /**
     * Creates a string with a list of each item in a container and it's amount.
     *
     * @param container The container.
     * @return A string with a list of each item in a container and it's amount.
     */
    private static String listContainerContents(Container container) {
        if (container.freeSlots() == container.capacity()) {
            return "<col=FFFFFF>Absolutely nothing!";
        } else {
            StringBuilder bldr = new StringBuilder();
            for (int i = 0; i < container.capacity(); i++) {
                Item item = container.get(i);
                if (item != null) {
                    bldr.append("<col=FF9040>" + item.getDefinition().getName());
                    if (item.getCount() > 1) {
                        bldr.append(" <col=FFFFFF> x <col=FFFFFF>" + item.getCount());
                    }
                    bldr.append("<br>");
                }
            }
            return bldr.toString();
        }
    }

    /**
     * Gets the item container containing the items a
     * specific player staked.
     *
     * @param player The player who's container we're getting.
     * @return An item <code>Container</code>, with the stake that belongs to the player.
     */
    public Container getStake(Player player) {
        return player.getDuelContainer();
    }

}
