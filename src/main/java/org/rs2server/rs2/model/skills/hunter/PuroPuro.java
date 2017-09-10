package org.rs2server.rs2.model.skills.hunter;

import java.util.ArrayList;
import java.util.List;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.task.Task;
import org.rs2server.rs2.task.impl.NPCTickTask;
import org.rs2server.rs2.util.Misc;

public class PuroPuro {

  public static final int[][] implings = {
    /**
     * Baby imps
    */
//    {6055, 2612, 4318}, {6055, 2602, 4314}, {6055, 2610, 4338}, {6055, 2582, 4344},
//    {6055, 2578, 4344}, {6055, 2568, 4311}, {6055, 2583, 4295}, {6055, 2582, 4330},
//    {6055, 2600, 4303}, {6055, 2611, 4301}, {6055, 2618, 4329},

    /**
     * Young imps
    */
//    {6056, 2591, 4332}, {6056, 2600, 4338}, {6056, 2595, 4345}, {6056, 2610, 4327},
//    {6056, 2617, 4314}, {6056, 2619, 4294}, {6056, 2599, 4294}, {6056, 2575, 4303},
//    {6056, 2570, 4299},

    /**
     * Gourment imps
    */
//    {6057, 2573, 4339}, {6057, 2567, 4328}, {6057, 2593, 4297}, {6057, 2618, 4305},
//    {6057, 2605, 4316}, {6057, 2596, 4333},

    /**
     * Earth imps
    */
//    {6058, 2592, 4338}, {6058, 2611, 4345}, {6058, 2617, 4339}, {6058, 2614, 4301},
//    {6058, 2606, 4295}, {6058, 2581, 4299},

    /**
     * Essence imps
    */
 //   {6059, 2602, 4328}, {6059, 2608, 4333}, {6059, 2609, 4296}, {6059, 2581, 4304},
//    {6059, 2570, 4318},

    /**
     * Eclectic imps
    */
 //   {6060, 2611, 4310}, {6060, 2617, 4319}, {6060, 2600, 4347}, {6060, 2570, 4326},
//   {6060, 2579, 4310},

    /**
     * Spirit imps
    */

    /**
     * Nature imps
    */
 //   {6061, 2581, 4310}, {6061, 2581, 4310}, {6061, 2603, 4333}, {6061, 2576, 4335},
//    {6061, 2588, 4345},

    /**
     * Magpie imps
    */
//    {6062, 2612, 4324}, {6062, 2602, 4323}, {6062, 2587, 4348}, {6062, 2564, 4320},
//    {6062, 2566, 4295},

    /**
     * Ninja imps
    */
//    {6063, 2570, 4347}, {6063, 2572, 4327}, {6063, 2578, 4318}, {6063, 2610, 4312},
//    {6063, 2594, 4341},

    /**
     * Dragon imps
    */
 //   {6064, 2613, 4341}, {6064, 2585, 4337}, {6064, 2576, 4319}, {6064, 2576, 4294},
 //   {6064, 2592, 4305},

  };

//  public static void spawn() {
//    for (int i = 0; i < implings.length; i++) {
//      NPC n = new NPC(implings[i][0], new Location(implings[i][1], implings[i][2], 0));
//      n.setWalkingRadius(4);
//      World.register(n);
//    }

    /**
     * Kingly imps
     * Randomly spawned
     */
//    int random = Misc.random(6);
//    Location pos = pos.getl(2596, 4351);
//    switch (random) {
//      case 1:
//        pos = new Position(2620, 4348);
//        break;
//      case 2:
//        pos = new Location(2607, 4321);
//        break;
//      case 3:
//        pos = new Position(2588, 4289);
//        break;
//      case 4:
//        pos = new Position(2576, 4305);
//        break;
//    }
//    n.setWalkingRadius(4);
//    World.register(n);
//
//  }

  /**
   * Catches an Impling
   *
   * @param player The player catching an Imp
   * @param npc The NPC (Impling) to catch
   */
  public static void catchImpling(Player player, final NPC imp) {
    ImpData implingData = ImpData.forId(imp.getId());
    if (player == null || imp == null || implingData == null)
      return;
    if (player.getSkills().getLevel(Skills.HUNTER) < implingData.levelReq) {
      player.getActionSender().sendMessage(
        "You need a Hunter level of at least " + implingData.levelReq + " to catch this impling.");
      return;
    }
    if (!player.getInventory().contains(10010) && !player.getEquipment().contains(10010)
    	&& !player.getInventory().contains(11259) && !player.getEquipment().contains(11259)) {
      player.getActionSender().sendMessage("You do not have any net to catch this impling with.");
      return;
    }
    if (!player.getInventory().contains(11260)) {
      player.getActionSender()
        .sendMessage("You do not have any empty jars to hold this impling with.");
      return;
    }
    player.playAnimation(Animation.create(6605));
    boolean sucess = player.getSkills().getLevel(Skills.HUNTER) > 8 ?
                       Misc.random(player.getSkills().getLevel(Skills.HUNTER) / 2)
                         > 1 :
                       true;
    if (sucess) {
      //if (imp.isInteracting()) {
    	List<Task> tickTasks = new ArrayList<Task>();
        //World.getWorld().unregister(imp);
        tickTasks.add(new NPCTickTask(imp));
        player.getInventory().remove(new Item(11260, 1));
        player.getInventory().add(new Item(implingData.impJar, 1));
        player.getActionSender().sendMessage("You successfully catch the impling.");
        player.getSkills().addExperience(Skills.HUNTER, implingData.XPReward);
   //   }
    } else
      player.getActionSender().sendMessage("You failed to catch the impling.");
    	//player.getClickDelay().reset();
  }

  /**
   * Handles pushing through walls in Puro puro
   *
   * @param player The player pushing a wall
   */
//  public static void goThroughWheat(final Player player, GameObject object) {
//    if (!player.getClickDelay().elapsed(2000))
//      return;
//    player.getClickDelay().reset();
//    int x = player.getPosition().getX(), x2 = x;
//    int y = player.getPosition().getY(), y2 = y;
//    if (x == 2584) {
//      x2 = 2582;
//    } else if (x == 2582) {
//      x2 = 2584;
//    } else if (x == 2599) {
//      x2 = 2601;
//    } else if (x == 2601) {
//      x2 = 2599;
//    }
//    if (y == 4312) {
//      y2 = 4310;
//    } else if (y == 4310) {
//      y2 = 4312;
//    } else if (y == 4327) {
//      y2 = 4329;
//    } else if (y == 4329) {
//      y2 = 4327;
//    }
//    x2 -= x;
//    y2 -= y;
//    player.getPacketSender().sendMessage("You use your strength to push through the wheat.");
//    final int goX = x2, goY = y2;
//    TaskManager.submit(new Task(1, player, false) {
//      int tick = 0;
//
//      @Override
//      protected void execute() {
//        if (tick == 1) {
//          player.playAnimation(6594).setCrossingObstacle(true);
//          player.flag(UpdateFlag.APPEARANCE);
//          player.getMovementQueue().walkStep(goX, goY);
//        } else if (tick == 2)
//          stop();
//        tick++;
//      }
//
//      @Override
//      public void stop() {
//        setEventRunning(false);
//        player.playAnimation(-1).setCrossingObstacle(false);
//        player.flag(UpdateFlag.APPEARANCE);
//      }
//    });
//  }

  /**
   * Handles Impling Jars looting
   *
   * @param player The player looting the jar
   * @param itemId The jar the player is looting
   */
//  public static void lootJar(final Player player, Item jar, JarData jarData) {
//    if (player == null || jar == null || jarData == null || !player.getClickDelay().elapsed(600))
//      return;
//    player.getInventory().delete(jar);
//    player.getInventory().add(11260, 1);
//
//    ArrayList<JarLootItem> veryRares = jarData.getLootWithRarity(ItemRarity.VERY_RARE);
//    ArrayList<JarLootItem> rares = jarData.getLootWithRarity(ItemRarity.RARE);
//    ArrayList<JarLootItem> uncommons = jarData.getLootWithRarity(ItemRarity.UNCOMMON);
//    ArrayList<JarLootItem> commons = jarData.getLootWithRarity(ItemRarity.COMMON);
//
//    JarLootItem loot = null;
//    if (!veryRares.isEmpty() && Misc.random(ItemRarity.VERY_RARE.rarity) == 0) {
//      loot = veryRares.get(Misc.random(veryRares.size()));
//    } else if (!rares.isEmpty() && Misc.random(ItemRarity.RARE.rarity) == 0) {
//      loot = rares.get(Misc.random(rares.size()));
//    } else if (!uncommons.isEmpty() && Misc.random(ItemRarity.UNCOMMON.rarity) == 0) {
//      loot = uncommons.get(Misc.random(uncommons.size()));
//    } else if (!commons.isEmpty()) {
//      loot = commons.get(Misc.random(commons.size()));
//    }
//
//    if (jarData.getClueScroll() != null && Misc.random(35) == 0 && !player.getTreasureTrails()
//                                                                      .hasClueScroll(
//                                                                        jarData.getClueScroll())) {
//      player.getInventory().addOrCreateGroundItem(new Item(jarData.getClueScroll().getId()));
//      player.message(Colors.GE_GREEN + "You've been offered a Clue scroll!");
//    }
//
//    if (loot == null) {
//      player.message("You loot the jar, but find nothing.");
//      player.getClickDelay().reset();
//      return;
//    }
//
//    String rewardName = loot.getDefinition().getName();
//    String s = Misc.anOrA(rewardName);
//    if (loot.getAmount() > 1) {
//      s = "" + loot.getAmount() + "";
//      if (!rewardName.endsWith("s")) {
//        if (rewardName.contains("potion")) {
//          String l = rewardName.substring(0, rewardName.indexOf(" potion"));
//          String l2 = rewardName.substring(rewardName.indexOf("potion"), 8);
//          l2 += rewardName.contains("(3)") ? "(3)" : "(4)";
//          rewardName = "" + l + " potions " + l2 + "";
//        } else
//          rewardName = rewardName + "s";
//      }
//    }
//
//    player.getInventory().addOrCreateGroundItem(new Item(loot.getId(), loot.getAmount()));
//    player.getPacketSender().sendMessage(
//      "You loot the " + jar.getDefinition().getName() + " and find " + s + " " + rewardName + ".");
//    player.getClickDelay().reset();
//  }

}
