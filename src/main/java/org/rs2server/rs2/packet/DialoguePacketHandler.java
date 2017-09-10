package org.rs2server.rs2.packet;

import org.rs2server.Server;
import org.rs2server.rs2.content.TeleportManager;
import org.rs2server.rs2.content.api.GameItemInventoryActionEvent;
import org.rs2server.rs2.content.api.bank.BankSettingsClickEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.impl.BankPinServiceImpl;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.quests.Quest;
import org.rs2server.rs2.model.skills.DialogueSkillManager;
import org.rs2server.rs2.model.skills.smithing.DragonfireShieldAction;
import org.rs2server.rs2.net.Packet;

public class DialoguePacketHandler implements PacketHandler {

	private final HookService hookService;

	public DialoguePacketHandler() {
		hookService = Server.getInjector().getInstance(HookService.class);
	}

	public static final Class<?> getQuestByNpc(NPC npc) {
		switch (npc.getId()) {
		// case 684:
		// case 1902:
		// return DesertTreasure.class;
		// case 4552:
		// return Deerquest.class;
		// case 3855:
		// case 3839:
		// return LunarDiplomacy.class;
		default:
			return null;
		}
	}

	@Override
	public void handle(Player player, Packet packet) {
		int option = packet.getLEShort();
		int interfaceHash = packet.getInt1();
		int interfaceId = (interfaceHash >> 16);
		int child = (interfaceHash & 0xFFFF);

		// The new dialogue system takes priority.
		if (player.getDialogueChain() != null) {
			player.getDialogueChain().proceed(player, option > 0 ? (option - 1) : (child - 2));
			return;
		}

		if (player.getInterfaceState().isInterfaceOpen(BankPinServiceImpl.BANK_SETTINGS_WIDGET)) {
			hookService.post(new BankSettingsClickEvent(player, child));
			return;
		}

		// Destroy item chatbox interface
		if (player.getInterfaceState().getChatboxInterface() == 94 && interfaceId == 0 && option == -1) {
			if (player.getInterfaceState().getDestroyItemId() > 0) {
				if (child == 4) { // Yes, destroy item
					player.getInventory().remove(new Item(player.getInterfaceState().getDestroyItemId(), 1));
					hookService.post(
							new GameItemInventoryActionEvent(player, GameItemInventoryActionEvent.ClickType.DESTROY,
									new Item(player.getInterfaceState().getDestroyItemId(), 1), -1));
				} else // No, cancel
					player.getInterfaceState().setDestroyItemId(-1);
			}
		}

		if (player.hasAttribute("forging_dfs")) {
			player.getActionSender().removeChatboxInterface();
			player.getActionQueue().addAction(new DragonfireShieldAction(player));
			player.removeAttribute("forging_dfs");
			return;
		}
		if (player.hasAttribute("questnpc") && (boolean) player.getAttribute("questnpc")) {
			NPC npc = (NPC) player.getInteractingEntity();
			if (npc != null) {

				Class<?> cls = getQuestByNpc(npc);

				if (cls == null) {
					player.getActionSender().removeChatboxInterface();
					return;
				}

				Quest<?> quest = player.getQuests().get(cls);

				if (quest == null) {
					player.getActionSender().removeChatboxInterface();
					return;
				}

				if (option > 0)
					quest.advanceDialogue(option - 1);
				else
					quest.advanceDialogue(child - 2);
				return;
			}
		}
		if (player.getInterfaceState().isInterfaceOpen(187)) {
			TeleportManager.handleTeleportActions(player, option);
			return;
		}
		if (DialogueSkillManager.handleSkillAction(player, child))
			return;
		else {
			if (option > 0)
				DialogueManager.advanceDialogue(player, (option - 1)); 
			else
				DialogueManager.advanceDialogue(player, (child - 2));
		}
	}
}