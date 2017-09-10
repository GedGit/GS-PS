package org.rs2server.rs2.model.skills;

import org.rs2server.rs2.domain.service.api.content.magic.OrbChargingService;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.Cooking.CookingItem;
import org.rs2server.rs2.model.skills.Cooking.CookingMethod;
import org.rs2server.rs2.model.skills.FletchingAction.FletchingGroup;
import org.rs2server.rs2.model.skills.FletchingAction.FletchingItem;
import org.rs2server.rs2.model.skills.FletchingAction.FletchingType;
import org.rs2server.rs2.model.skills.herblore.Herblore;
import org.rs2server.rs2.model.skills.herblore.Herblore.HerbloreType;
import org.rs2server.rs2.model.skills.herblore.Herblore.PrimaryIngredient;
import org.rs2server.rs2.model.skills.herblore.Herblore.SecondaryIngredient;
import org.rs2server.rs2.model.skills.crafting.BoltCrafting;
import org.rs2server.rs2.model.skills.crafting.GemCutting;
import org.rs2server.rs2.model.skills.crafting.LeatherCrafting.Leather;
import org.rs2server.rs2.model.skills.crafting.LeatherCrafting.LeatherProduction;
import org.rs2server.rs2.model.skills.crafting.LeatherCrafting.LeatherProductionAction;
import org.rs2server.rs2.model.skills.crafting.SnakeskinCrafting;
import org.rs2server.rs2.model.skills.herblore.SuperCombatPotion;

public class DialogueSkillManager {

	public static boolean handleSkillAction(Player player, int child) {
		if (player.getInterfaceAttribute("cookItem") != null && player.getInterfaceAttribute("cookMethod") != null) {
			int amount = 0;
			switch (child) {
			case 6:
				amount = 1;
				break;
			case 5:
				amount = 5;
				break;
			case 4:
				player.getActionSender().removeInterfaces(162, 546);
				player.getActionSender().sendEnterAmountInterface();
				return false;
			case 3:
				amount = 28;
				break;
			}
			player.getActionQueue()
					.addAction(new Cooking(player, 2, amount, (CookingItem) player.getInterfaceAttribute("cookItem"),
							(CookingMethod) player.getInterfaceAttribute("cookMethod")));
			player.getActionSender().removeInterfaces(162, 546);
			player.removeInterfaceAttribute("cookItem");
			player.removeInterfaceAttribute("cookMethod");
			return true;
		} else if (player.getInterfaceAttribute("staff_type") != null) {
			int amount = 0;
			switch (child) {
			case 6:
				amount = 1;
				break;
			case 5:
				amount = 5;
				break;
			case 4:
				player.getActionSender().removeInterfaces(162, 546);
				player.getActionSender().sendEnterAmountInterface();
				return false;
			case 3:
				amount = 28;
				break;
			}
			OrbChargingService.StaffType type = player.getInterfaceAttribute("staff_type");
			if (type != null) {
				player.getActionSender().removeChatboxInterface();
				player.getActionQueue().addAction(new OrbChargingService.BattleStaffAction(player, type, amount));
				player.removeInterfaceAttribute("staff_type");
			}
			return true;
		} else if (player.getAttribute("snakeskin_crafting") != null) {
			SnakeskinCrafting.Snakeskin skin = null;
			if (child >= 7 && child <= 10) {
				skin = SnakeskinCrafting.Snakeskin.BODY;
			} else if (child >= 11 && child <= 14) {
				skin = SnakeskinCrafting.Snakeskin.CHAPS;
			} else if (child >= 15 && child <= 18) {
				skin = SnakeskinCrafting.Snakeskin.VAMBRACES;
			} else if (child >= 19 && child <= 22) {
				skin = SnakeskinCrafting.Snakeskin.BANDANA;
			} else if (child >= 23 && child <= 26) {
				skin = SnakeskinCrafting.Snakeskin.BOOTS;
			}
			int amount = 0;
			switch (child) {
			case 7:
			case 11:
			case 15:
			case 19:
			case 23:
				player.getActionSender().removeInterfaces(162, 546);
				player.getActionSender().sendEnterAmountInterface();
				return false;
			case 8:
			case 12:
			case 16:
			case 20:
			case 24:
				amount = 10;
				break;
			case 9:
			case 13:
			case 17:
			case 21:
			case 25:
				amount = 5;
				break;
			case 10:
			case 14:
			case 18:
			case 22:
			case 26:
				amount = 1;
				break;
			}
			if (skin != null) {
				SnakeskinCrafting.SnakeskinCraftingAction produceAction = new SnakeskinCrafting.SnakeskinCraftingAction(
						player, skin, amount);
				produceAction.execute();
				if (produceAction.isRunning()) {
					player.submitTick("skill_action_tick", produceAction, true);
				}
				player.getActionSender().removeInterfaces(162, 546);
				player.removeAttribute("snakeskin_crafting");
			}
			return true;
		} else if (player.getAttribute("craftingType") != null && player.getAttribute("dhide_type") != null
				&& (Integer) player.getAttribute("craftingType") == 2) {
			int amount = 0;
			int[] possibles = { 1, 5, 10, -1 };
			LeatherProduction prod = null;
			Leather leather = player.getAttribute("dhide_type");
			switch (child) {
			case 8:
			case 7:
			case 6:
				if (leather == Leather.HARD_LEATHER) {
					prod = LeatherProduction.HARD_LEATHER_BODY;
				} else if (leather == Leather.XERICIAN) {
					prod = LeatherProduction.XERICIAN_HAT;
				} else if (leather == Leather.GREEN_D_HIDE) {
					prod = LeatherProduction.GREEN_D_HIDE_BODY;
				} else if (leather == Leather.BLUE_D_HIDE) {
					prod = LeatherProduction.BLUE_D_HIDE_BODY;
				} else if (leather == Leather.RED_D_HIDE) {
					prod = LeatherProduction.RED_D_HIDE_BODY;
				} else if (leather == Leather.BLACK_D_HIDE) {
					prod = LeatherProduction.BLACK_D_HIDE_BODY;
				}
				amount = child == 8 ? possibles[0] : child == 7 ? possibles[1] : child == 6 ? possibles[2] : 0;
				break;
			case 12:
			case 11:
			case 10:
				if (leather == Leather.GREEN_D_HIDE) {
					prod = LeatherProduction.GREEN_D_HIDE_VAMB;
				} else if (leather == Leather.XERICIAN) {
					prod = LeatherProduction.XERICIAN_ROBE;
				} else if (leather == Leather.BLUE_D_HIDE) {
					prod = LeatherProduction.BLUE_D_HIDE_VAMB;
				} else if (leather == Leather.RED_D_HIDE) {
					prod = LeatherProduction.RED_D_HIDE_VAMB;
				} else if (leather == Leather.BLACK_D_HIDE) {
					prod = LeatherProduction.BLACK_D_HIDE_VAMB;
				}
				amount = child == 12 ? possibles[0] : child == 11 ? possibles[1] : child == 10 ? possibles[2] : 0;
				break;
			case 16:
			case 15:
			case 14:
				if (leather == Leather.GREEN_D_HIDE) {
					prod = LeatherProduction.GREEN_D_HIDE_CHAPS;
				} else if (leather == Leather.XERICIAN) {
					prod = LeatherProduction.XERICIAN_TOP;
				} else if (leather == Leather.BLUE_D_HIDE) {
					prod = LeatherProduction.BLUE_D_HIDE_CHAPS;
				} else if (leather == Leather.RED_D_HIDE) {
					prod = LeatherProduction.RED_D_HIDE_CHAPS;
				} else if (leather == Leather.BLACK_D_HIDE) {
					prod = LeatherProduction.BLACK_D_HIDE_CHAPS;
				}
				amount = child == 16 ? possibles[0] : child == 15 ? possibles[1] : child == 14 ? possibles[2] : 0;
				break;

			default:
				if (child == 5) {
					if (leather == Leather.GREEN_D_HIDE) {
						prod = LeatherProduction.GREEN_D_HIDE_BODY;
					} else if (leather == Leather.BLUE_D_HIDE) {
						prod = LeatherProduction.BLUE_D_HIDE_BODY;
					} else if (leather == Leather.RED_D_HIDE) {
						prod = LeatherProduction.RED_D_HIDE_BODY;
					} else if (leather == Leather.BLACK_D_HIDE) {
						prod = LeatherProduction.BLACK_D_HIDE_BODY;
					}
				} else if (child == 9) {
					if (leather == Leather.GREEN_D_HIDE) {
						prod = LeatherProduction.GREEN_D_HIDE_VAMB;
					} else if (leather == Leather.BLUE_D_HIDE) {
						prod = LeatherProduction.BLUE_D_HIDE_VAMB;
					} else if (leather == Leather.RED_D_HIDE) {
						prod = LeatherProduction.RED_D_HIDE_VAMB;
					} else if (leather == Leather.BLACK_D_HIDE) {
						prod = LeatherProduction.BLACK_D_HIDE_VAMB;
					}
				} else if (child == 13) {
					if (leather == Leather.GREEN_D_HIDE) {
						prod = LeatherProduction.GREEN_D_HIDE_CHAPS;
					} else if (leather == Leather.BLUE_D_HIDE) {
						prod = LeatherProduction.BLUE_D_HIDE_CHAPS;
					} else if (leather == Leather.RED_D_HIDE) {
						prod = LeatherProduction.RED_D_HIDE_CHAPS;
					} else if (leather == Leather.BLACK_D_HIDE) {
						prod = LeatherProduction.BLACK_D_HIDE_CHAPS;
					}
				}
				player.setAttribute("production", prod);
				player.getActionSender().removeInterfaces(162, 546);
				player.getActionSender().sendEnterAmountInterface();
				return true;
			}
			if (leather == Leather.HARD_LEATHER) {
				switch (child) {
				case 6:
					amount = 1;
					break;
				case 5:
					amount = 5;
					break;
				case 4:
					// player.getActionSender().removeInterfaces(162, 546);
					// player.getActionSender().sendEnterAmountInterface();
					amount = 28;
					return false;
				case 3:
					amount = 28;
					break;
				}
			}
			if (prod != null) {
				LeatherProduction toProduce = prod;
				if (toProduce != null) {
					LeatherProductionAction produceAction = new LeatherProductionAction(player, toProduce, amount);
					produceAction.execute();
					if (produceAction.isRunning()) {
						player.submitTick("skill_action_tick", produceAction, true);
					}
				}
				player.getActionSender().removeInterfaces(162, 546);
				player.removeAttribute("craftingType");
				player.removeAttribute("dhide_type");
				return true;
			}
		} else if (player.getInterfaceAttribute("herblore_index") != null
				&& player.getInterfaceAttribute("herblore_type") != null) {
			int amount = 0;
			switch (child) {
			case 6:
				amount = 1;
				break;
			case 5:
				amount = 5;
				break;
			case 4:
				player.getActionSender().removeInterfaces(162, 546);
				player.getActionSender().sendEnterAmountInterface();
				return false;
			case 3:
				amount = 28;
				break;
			}
			switch ((HerbloreType) player.getInterfaceAttribute("herblore_type")) {
			case PRIMARY_INGREDIENT:
				player.getActionQueue()
						.addAction(new Herblore(player, amount,
								PrimaryIngredient.forId((int) player.getInterfaceAttribute("herblore_index"),
										(int) player.getInterfaceAttribute("vial_index")),
								null, HerbloreType.PRIMARY_INGREDIENT));
				break;
			case SECONDARY_INGREDIENT:
				player.getActionQueue()
						.addAction(new Herblore(player, amount, null,
								SecondaryIngredient.forId((Integer) player.getInterfaceAttribute("herblore_index")),
								HerbloreType.SECONDARY_INGREDIENT));
				break;
			}
			player.getActionSender().removeInterfaces(162, 546);
			player.removeInterfaceAttribute("herblore_index");
			player.removeInterfaceAttribute("herblore_type");
			player.removeInterfaceAttribute("vial_index");
			return true;
		} else if (player.getInterfaceAttribute("orb_type") != null) {
			int amount = 0;
			switch (child) {
			case 6:
				amount = 1;
				break;
			case 5:
				amount = 5;
				break;
			case 4:
				player.getActionSender().removeChatboxInterface();
				player.getActionSender().sendEnterAmountInterface();
				return false;
			case 3:
				amount = 28;
				break;
			}
			OrbChargingService.ChargeSpell type = player.getInterfaceAttribute("orb_type");
			if (type != null) {
				player.getActionSender().removeChatboxInterface();
				player.getActionQueue().addAction(new OrbChargingService.OrbChargingAction(player, type, amount));
				player.removeInterfaceAttribute("orb_type");
			}
		} else if (player.getInterfaceAttribute("superCombat") != null) {
			int amount = 0;
			switch (child) {
			case 6:
				amount = 1;
				break;
			case 5:
				amount = 5;
				break;
			case 4:
				player.getActionSender().removeInterfaces(162, 546);
				player.getActionSender().sendEnterAmountInterface();
				return false;
			case 3:
				amount = 28;
				break;
			}
			player.getActionQueue().addAction(new SuperCombatPotion(player, amount));
			player.getActionSender().removeInterfaces(162, 546);
			player.removeInterfaceAttribute("superCombat");
			return true;
		} else if (player.getInterfaceAttribute("pestle_type") != null) {
			int amount = 0;
			switch (child) {
			case 6:
				amount = 1;
				break;
			case 5:
				amount = 5;
				break;
			case 4:
				player.getActionSender().removeInterfaces(162, 546);
				player.getActionSender().sendEnterAmountInterface();
				return false;
			case 3:
				amount = 28;
				break;
			}
			PestleAndMortar.Pestle pestle = player.getInterfaceAttribute("pestle_type");
			if (pestle != null) {
				player.getActionQueue().addAction(new PestleAndMortar(player, pestle, amount));
				player.getActionSender().removeInterfaces(162, 546);
				player.removeInterfaceAttribute("pestle_type");
			}
			return true;
		} else if (player.getInterfaceAttribute("gem_index") != null
				&& player.getInterfaceAttribute("gem_type") != null) {
			int amount = 0;
			switch (child) {
			case 6:
				amount = 1;
				break;
			case 5:
				amount = 5;
				break;
			case 4:
				player.getActionSender().removeInterfaces(162, 546);
				player.getActionSender().sendEnterAmountInterface();
				return false;
			case 3:
				amount = 28;
				break;
			}
			player.getActionQueue().addAction(new GemCutting(player, player.getInterfaceAttribute("gem_type"), amount));
			player.getActionSender().removeInterfaces(162, 546);
			player.removeInterfaceAttribute("gem_index");
			player.removeInterfaceAttribute("gem_type");
			return true;
		} else if (player.getInterfaceAttribute("tip_index") != null
				&& player.getInterfaceAttribute("tip_type") != null) {
			int amount = 0;
			switch (child) {
			case 6:
				amount = 1;
				break;
			case 5:
				amount = 5;
				break;
			case 4:
				player.getActionSender().removeInterfaces(162, 546);
				player.getActionSender().sendEnterAmountInterface();
				return false;
			case 3:
				amount = 28;
				break;
			}
			// System.out.println("Amount: " + amount);
			player.getActionQueue()
					.addAction(new BoltCrafting(player, player.getInterfaceAttribute("tip_type"), amount));
			player.getActionSender().removeInterfaces(162, 546);
			player.removeInterfaceAttribute("tip_index");
			player.removeInterfaceAttribute("tip_type");
		} else if (player.getInterfaceAttribute("fletch_group") != null) {
			boolean enterX = false;
			if (player.getInterfaceAttribute("fletch_group") != FletchingGroup.MAGIC_LOGS) {
				switch (child) {
				case 21:
				case 17:
				case 13:
				case 9:
					player.fletchAmount = 1;
					break;
				case 20:
				case 16:
				case 12:
				case 8:
					player.fletchAmount = 5;
					break;
				case 19:
				case 15:
				case 11:
				case 7:
					player.fletchAmount = 10;
					break;
				case 18:
				case 14:
				case 10:
				case 6:
					enterX = true;
					break;
				}
			} else {
				switch (child) {
				case 7:
				case 11:
					player.fletchAmount = 1;
					break;
				case 6:
				case 10:
					player.fletchAmount = 5;
					break;
				case 5:
				case 9:
					player.fletchAmount = 10;
					break;
				case 4:
				case 8:
					enterX = true;
					break;
				}
			}
			FletchingGroup group = player.getInterfaceAttribute("fletch_group");
			if (group != null && group != FletchingGroup.LOGS && group != FletchingGroup.MAGIC_LOGS) {
				if (child == 8 || child == 12 || child == 16) {
					player.fletchAmount = 1;
				} else if (child == 7 || child == 11 || child == 15) {
					player.fletchAmount = 5;
				} else if (child == 6 || child == 10 || child == 14) {
					enterX = true;
				} else if (child == 5 || child == 9 || child == 13) {
					player.fletchAmount = 28;
				}
			}
			if (child >= 5 && child <= 8) {
				if (group == FletchingGroup.OAK_LOGS) {
					player.fletchItem = FletchingItem.OAK_SHORTBOW_U;
				} else if (group == FletchingGroup.WILLOW_LOGS) {
					player.fletchItem = FletchingItem.WILLOW_SHORTBOW_U;
				} else if (group == FletchingGroup.MAPLE_LOGS) {
					player.fletchItem = FletchingItem.MAPLE_SHORTBOW_U;
				} else if (group == FletchingGroup.YEW_LOGS) {
					player.fletchItem = FletchingItem.YEW_SHORTBOW_U;
				} else if (group == FletchingGroup.MAGIC_LOGS) {
					player.fletchItem = FletchingItem.MAGIC_SHORTBOW_U;
				}
			} else if (child >= 9 && child <= 13) {
				if (group == FletchingGroup.LOGS) {
					player.fletchItem = FletchingItem.SHORTBOW_U;
				} else if (group == FletchingGroup.OAK_LOGS) {
					player.fletchItem = FletchingItem.OAK_LONGBOW_U;
				} else if (group == FletchingGroup.WILLOW_LOGS) {
					player.fletchItem = FletchingItem.WILLOW_LONGBOW_U;
				} else if (group == FletchingGroup.MAPLE_LOGS) {
					player.fletchItem = FletchingItem.MAPLE_LONGBOW_U;
				} else if (group == FletchingGroup.YEW_LOGS) {
					player.fletchItem = FletchingItem.YEW_LONGBOW_U;
				} else if (group == FletchingGroup.MAGIC_LOGS) {
					player.fletchItem = FletchingItem.MAGIC_LONGBOW_U;
				}
			} else if (child >= 14 && child <= 17) {
				if (group == FletchingGroup.LOGS) {
					player.fletchItem = FletchingItem.LONGBOW_U;
				} else if (group == FletchingGroup.OAK_LOGS) {
					player.fletchItem = FletchingItem.OAK_STOCK;
				} else if (group == FletchingGroup.WILLOW_LOGS) {
					player.fletchItem = FletchingItem.WILLOW_STOCK;
				} else if (group == FletchingGroup.MAPLE_LOGS) {
					player.fletchItem = FletchingItem.MAPLE_STOCK;
				} else if (group == FletchingGroup.YEW_LOGS) {
					player.fletchItem = FletchingItem.YEW_STOCK;
				}
			} else if (child >= 18 && child <= 21) {
				if (group == FletchingGroup.LOGS) {
					player.fletchItem = FletchingItem.WOODEN_STOCK;
				}
			}
			if ((child == 9 || child == 8 || child == 7 || child == 6) && group == FletchingGroup.LOGS) {
				player.fletchItem = FletchingItem.ARROW_SHAFTS;
			}
			if (child == 4 && group == FletchingGroup.MAGIC_LOGS) {
				player.fletchItem = FletchingItem.MAGIC_SHORTBOW_U;
			} else if (child == 8 && group == FletchingGroup.MAGIC_LOGS) {
				player.fletchItem = FletchingItem.MAGIC_LONGBOW_U;
			}
			if (child == 9) {
				if (group == FletchingGroup.OAK_LOGS) {
					player.fletchItem = FletchingItem.OAK_LONGBOW_U;
				} else if (group == FletchingGroup.WILLOW_LOGS) {
					player.fletchItem = FletchingItem.WILLOW_LONGBOW_U;
				} else if (group == FletchingGroup.MAPLE_LOGS) {
					player.fletchItem = FletchingItem.MAPLE_LONGBOW_U;
				} else if (group == FletchingGroup.YEW_LOGS) {
					player.fletchItem = FletchingItem.YEW_LONGBOW_U;
				}
			}
			if (player.fletchItem != null) {
				if (enterX) {
					player.setInterfaceAttribute("fletch_x", true);
					player.getActionSender().sendEnterAmountInterface();
				} else {
					player.getActionQueue()
							.addAction(new FletchingAction(player, player.fletchAmount, player.fletchItem));
					player.removeInterfaceAttribute("fletch_group");
				}
			}
			player.getActionSender().removeChatboxInterface();
			return true;
		} else if (player.getInterfaceAttribute("fletch_item") != null) {
			FletchingItem item = (FletchingItem) player.getInterfaceAttribute("fletch_item");
			int amount = 0;
			if (item.getType() == FletchingType.ATTACHING) {
				amount = child == 5 ? 1 : child == 4 ? 5 : 10;// that looks right to me. maybe the fact amount is 0 ?
																// idk print the fucking amount!!!
			} else {
				amount = child == 6 ? 1 : child == 5 ? 5 : child == 3 ? 28 : -1;
			}
			if (amount == -1) {
				player.getActionSender().removeInterfaces(162, 546);
				player.getActionSender().sendEnterAmountInterface();
				return false;
			}
			if (item != null) {
				player.getActionQueue().addAction(new FletchingAction(player, amount, item));
			}
			player.removeInterfaceAttribute("fletch_item");
			player.getActionSender().removeChatboxInterface();
			return true;
		} else if (player.getInterfaceAttribute("message_dialogue") != null) {
			player.getActionSender().removeChatboxInterface();
			player.removeInterfaceAttribute("message_dialogue");
			return true;
		}
		return false;
	}

}
