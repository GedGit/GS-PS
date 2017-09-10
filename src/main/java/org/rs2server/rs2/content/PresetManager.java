package org.rs2server.rs2.content;

import org.rs2server.rs2.model.bit.component.Access;
import org.rs2server.rs2.model.bit.component.AccessBits;
import org.rs2server.rs2.model.bit.component.NumberRange;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender.DialogueType;

/**
 * @author Script
 * @since 02/17/2017
 */
public class PresetManager {
	/**
	 * Sends the Interface/CS2Script/Access Mask to the entity.
	 */
	public static void handlePresets(Player player) {
		player.getActionSender().sendInterface(187, false);
		player.getActionSender().sendCS2Script(217, new Object[] {
				"||||<col=880000><u=000000>Preset Management"
				+ "|<img=25> Preset Information|||"
				+ "|<col=880000><u=000000>Pre-defined Setups"
				+ "|<img=28> Hybrid Setup"
				+ "|<img=31> Melee Setup"
				+ "|<img=30> Pure Setup"
				+ "|<img=29> Zerker Setup"
				
				, "Preset Management", 0}, "Iss");//Iss  "Astrect Preset Menu"
		player.sendAccess(Access.of(187, 3, NumberRange.of(0, 170), AccessBits.CLICK_CONTINUE));
		player.setAttribute("presets_menu", true);
	}
	
	/**
	 * Handles the Astrect Preset Options
	 */
	public static boolean handlePresetOptions(Player player, int option) {
			
		switch (option) {
		case 5://Information
			player.getActionSender().closeAll();
			player.getActionSender().sendDialogue(null, DialogueType.MESSAGE, -1, null, "Presets are used to modify player stats, ease of access to preset equipment and inventory setup");
			
			break;
		
		case 10://Hybrid Setup Option
			
			int[] equipment = new int[] {
					Equipment.SLOT_BOOTS, Equipment.SLOT_BOTTOMS, Equipment.SLOT_CHEST, Equipment.SLOT_CAPE, Equipment.SLOT_GLOVES,
					Equipment.SLOT_HELM, Equipment.SLOT_SHIELD
				};
				for(int i = 0; i < equipment.length; i++) {
					if(player.getEquipment().get(equipment[i]) != null) {
						player.getActionSender().closeAll();
						player.getActionSender().sendMessage("You can't change your preset whilst wearing equipment.");
						player.getActionSender().sendDialogue(null, DialogueType.MESSAGE, -1, null, "You can't change your preset whilst wearing equipment");
						//player.getActionSender().closeAll();
						return false;
					}
				}
				if(player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
					player.getActionSender().closeAll();
					player.getActionSender().sendMessage("You can't change your preset whilst wielding any equipment.");
					player.getActionSender().sendDialogue(null, DialogueType.MESSAGE, -1, null, "You can't change your preset whilst wielding any equipment");
					//player.getActionSender().closeAll();
					return false;
				}
			player.getSkills().setExperience(0, player.getSkills().getExperienceForLevel(99));
			player.getSkills().setLevel(0, 99);
			player.getSkills().setExperience(1, player.getSkills().getExperienceForLevel(99));
			player.getSkills().setLevel(1, 99);
			player.getSkills().setExperience(2, player.getSkills().getExperienceForLevel(99));
			player.getSkills().setLevel(2, 99);
			player.getSkills().setExperience(3, player.getSkills().getExperienceForLevel(99));
			player.getSkills().setLevel(3, 99);
			player.getSkills().setExperience(4, player.getSkills().getExperienceForLevel(99));
			player.getSkills().setLevel(4, 99);
			player.getSkills().setExperience(6, player.getSkills().getExperienceForLevel(99));
			player.getSkills().setLevel(6, 99);
			player.getSkills().setExperience(5, player.getSkills().getExperienceForLevel(99));
			player.getSkills().setLevel(5, 99);//Prayer
			player.getSkills().setPrayerPoints(99, true);
			player.getActionSender().sendString(593, 2, "Combat lvl: " + player.getSkills().getCombatLevel());
			player.getActionSender().closeAll();
			player.getActionSender().sendMessage("Your preset has been changed to <img=28> <col=880000>Hybrid Setup");
			player.getActionSender().sendDialogue(null, DialogueType.MESSAGE, -1, null, "Your preset has been changed to <img=28> <col=880000>Hybrid Setup");
			
			//player.getActionSender().closeAll();
			break;
		case 11:
			int[] equipment1 = new int[] {
					Equipment.SLOT_BOOTS, Equipment.SLOT_BOTTOMS, Equipment.SLOT_CHEST, Equipment.SLOT_CAPE, Equipment.SLOT_GLOVES,
					Equipment.SLOT_HELM, Equipment.SLOT_SHIELD
				};
				for(int i = 0; i < equipment1.length; i++) {
					if(player.getEquipment().get(equipment1[i]) != null) {
						player.getActionSender().closeAll();
						player.getActionSender().sendMessage("You can't change your preset whilst wearing equipment.");
						player.getActionSender().sendDialogue(null, DialogueType.MESSAGE, -1, null, "You can't change your preset whilst wearing equipment");
						//player.getActionSender().closeAll();
						return false;
					}
				}
				if(player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
					player.getActionSender().closeAll();
					player.getActionSender().sendMessage("You can't change your preset whilst wielding any equipment.");
					player.getActionSender().sendDialogue(null, DialogueType.MESSAGE, -1, null, "You can't change your preset whilst wielding any equipment");
					//player.getActionSender().closeAll();
					return false;
				}
				player.getSkills().setExperience(0, player.getSkills().getExperienceForLevel(99));
				player.getSkills().setLevel(0, 99);
				player.getSkills().setExperience(1, player.getSkills().getExperienceForLevel(99));
				player.getSkills().setLevel(1, 99);
				player.getSkills().setExperience(2, player.getSkills().getExperienceForLevel(99));
				player.getSkills().setLevel(2, 99);
				player.getSkills().setExperience(3, player.getSkills().getExperienceForLevel(99));
				player.getSkills().setLevel(3, 99);
				player.getSkills().setExperience(4, player.getSkills().getExperienceForLevel(99));
				player.getSkills().setLevel(4, 99);
				player.getSkills().setExperience(6, player.getSkills().getExperienceForLevel(99));
				player.getSkills().setLevel(6, 99);
				player.getSkills().setExperience(5, player.getSkills().getExperienceForLevel(99));
				player.getSkills().setLevel(5, 99);//Prayer
				player.getSkills().setPrayerPoints(99, true);
				player.getActionSender().sendString(593, 2, "Combat lvl: " + player.getSkills().getCombatLevel());
				player.getActionSender().closeAll();
				player.getActionSender().sendMessage("Your preset has been changed to <img=31> <col=880000>Melee Setup");
				player.getActionSender().sendDialogue(null, DialogueType.MESSAGE, -1, null, "Your preset has been changed to <img=31> <col=880000>Melee Setup");
			
			//player.getActionSender().closeAll();
			break;
		case 12:
			int[] equipment2 = new int[] {
					Equipment.SLOT_BOOTS, Equipment.SLOT_BOTTOMS, Equipment.SLOT_CHEST, Equipment.SLOT_CAPE, Equipment.SLOT_GLOVES,
					Equipment.SLOT_HELM, Equipment.SLOT_SHIELD
				};
				for(int i = 0; i < equipment2.length; i++) {
					if(player.getEquipment().get(equipment2[i]) != null) {
						player.getActionSender().closeAll();
						player.getActionSender().sendMessage("You can't change your preset whilst wearing equipment.");
						player.getActionSender().sendDialogue(null, DialogueType.MESSAGE, -1, null, "You can't change your preset whilst wearing equipment");
					//	player.getActionSender().closeAll();
						return false;
					}
				}
				if(player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
					player.getActionSender().closeAll();
					player.getActionSender().sendMessage("You can't change your preset whilst wielding any equipment.");
					player.getActionSender().sendDialogue(null, DialogueType.MESSAGE, -1, null, "You can't change your preset whilst wielding any equipment");
				//	player.getActionSender().closeAll();
					return false;
				}
				player.getSkills().setExperience(0, player.getSkills().getExperienceForLevel(75));
				player.getSkills().setLevel(0, 75);
				player.getSkills().setExperience(1, player.getSkills().getExperienceForLevel(1));
				player.getSkills().setLevel(1, 1);
				player.getSkills().setExperience(2, player.getSkills().getExperienceForLevel(99));
				player.getSkills().setLevel(2, 99);
				player.getSkills().setExperience(3, player.getSkills().getExperienceForLevel(99));
				player.getSkills().setLevel(3, 99);
				player.getSkills().setExperience(4, player.getSkills().getExperienceForLevel(99));
				player.getSkills().setLevel(4, 99);
				player.getSkills().setExperience(6, player.getSkills().getExperienceForLevel(99));
				player.getSkills().setLevel(6, 99);
				player.getSkills().setExperience(5, player.getSkills().getExperienceForLevel(52));
				player.getSkills().setLevel(5, 52);//Prayer
				player.getSkills().setPrayerPoints(52, true);
				player.getActionSender().sendString(593, 2, "Combat lvl: " + player.getSkills().getCombatLevel());
				player.getActionSender().closeAll();
				player.getActionSender().sendMessage("Your preset has been changed to <img=30> <col=880000>Pure Setup");
				player.getActionSender().sendDialogue(null, DialogueType.MESSAGE, -1, null, "Your preset has been changed to <img=30> <col=880000>Pure Setup");
			
		//	player.getActionSender().closeAll();
			break;
		case 13:
			int[] equipment3 = new int[] {
					Equipment.SLOT_BOOTS, Equipment.SLOT_BOTTOMS, Equipment.SLOT_CHEST, Equipment.SLOT_CAPE, Equipment.SLOT_GLOVES,
					Equipment.SLOT_HELM, Equipment.SLOT_SHIELD
				};
				for(int i = 0; i < equipment3.length; i++) {
					if(player.getEquipment().get(equipment3[i]) != null) {
						player.getActionSender().closeAll();
						player.getActionSender().sendMessage("You can't change your preset whilst wearing equipment.");
						player.getActionSender().sendDialogue(null, DialogueType.MESSAGE, -1, null, "You can't change your preset whilst wearing equipment");
				//		player.getActionSender().closeAll();
						return false;
					}
				}
				if(player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
					player.getActionSender().closeAll();
					player.getActionSender().sendMessage("You can't change your preset whilst wielding any equipment.");
					player.getActionSender().sendDialogue(null, DialogueType.MESSAGE, -1, null, "You can't change your preset whilst wielding any equipment");
				//	player.getActionSender().closeAll();
					return false;
				}
				player.getSkills().setExperience(0, player.getSkills().getExperienceForLevel(75));
				player.getSkills().setLevel(0, 75);
				player.getSkills().setExperience(1, player.getSkills().getExperienceForLevel(45));
				player.getSkills().setLevel(1, 45);
				player.getSkills().setExperience(2, player.getSkills().getExperienceForLevel(99));
				player.getSkills().setLevel(2, 99);
				player.getSkills().setExperience(3, player.getSkills().getExperienceForLevel(99));
				player.getSkills().setLevel(3, 99);
				player.getSkills().setExperience(4, player.getSkills().getExperienceForLevel(99));
				player.getSkills().setLevel(4, 99);
				player.getSkills().setExperience(6, player.getSkills().getExperienceForLevel(99));
				player.getSkills().setLevel(6, 99);
				player.getSkills().setExperience(5, player.getSkills().getExperienceForLevel(52));
				player.getSkills().setLevel(5, 52);//Prayer
				player.getSkills().setPrayerPoints(52, true);
				player.getActionSender().sendString(593, 2, "Combat lvl: " + player.getSkills().getCombatLevel());
				player.getActionSender().closeAll();
			player.getActionSender().sendMessage("Your preset has been changed to <img=29> <col=880000>Zerker Setup");
			player.getActionSender().sendDialogue(null, DialogueType.MESSAGE, -1, null, "Your preset has been changed to <img=29> <col=880000>Zerker Setup");
			
			//player.getActionSender().closeAll();
			break;
		
		}
		return false;
	}

}

