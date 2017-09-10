package org.rs2server.rs2.domain.service.impl.content.duel;

import com.google.common.eventbus.Subscribe;
import org.rs2server.rs2.content.api.GameInterfaceButtonEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.PlayerVariableService;
import org.rs2server.rs2.domain.service.api.content.duel.DuelArenaService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.container.impl.InterfaceContainerListener;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

import com.google.inject.Inject;

import java.util.Optional;

/**
 * @author Clank1337
 * @author Twelve
 */
public class DuelArenaServiceImpl implements DuelArenaService {

	private static final int CONFIRM_WIDGET = 106;
	private static final int RULES_WIDGET = 107;
	private static final int INVENTORY_WIDGET = 109;

	private static final int OPPONENT_CHILD = 99;
	private static final int OPPONENT_COMBAT_CHILD = 96;
	private static final int ACCEPT_CHILD = 51;
	private static final Object[] PARAMETERS = new Object[]{"Stake X", "Stake All", "Stake 10", "Stake 5", "Stake 1", -1, 0, 7, 4, 93, 7143424};

	private final PlayerVariableService variableService;

	@Inject
	public DuelArenaServiceImpl(HookService service, PlayerVariableService variableService) {
		service.register(this);
		this.variableService = variableService;
	}

	@Override
	public void openRulesInterface(@Nonnull Player player, @Nonnull Player partner) {
		Duel duel = new Duel(player, partner);
		player.setDuel(duel);
		partner.setDuel(duel);

		player.getActionSender().sendConfig(286, 0)
				.sendCS2Script(209, new Object[]{partner.getName()}, "s")
				.sendString(RULES_WIDGET, OPPONENT_CHILD, partner.getName())
				.sendString(RULES_WIDGET, OPPONENT_COMBAT_CHILD, "" + partner.getSkills().getCombatLevel())
				.sendString(RULES_WIDGET, ACCEPT_CHILD, "")
				.sendCS2Script(917, new Object[]{-1, -1}, "ii")
				.sendInterface(RULES_WIDGET, false)
				.sendInterfaceInventory(INVENTORY_WIDGET)
				.sendCS2Script(149, PARAMETERS, "IviiiIsssss")
				.sendAccessMask(1086, 109, 0, 0, 27)
				.sendAccessMask(1086, 107, 102, 0, 27)
				.sendAccessMask(2, 107, 104, 0, 27);

		partner.getActionSender().sendConfig(286, 0)
				.sendCS2Script(209, new Object[]{player.getName()}, "s")
				.sendString(RULES_WIDGET, OPPONENT_CHILD, player.getName())
				.sendString(RULES_WIDGET, OPPONENT_COMBAT_CHILD, "" + player.getSkills().getCombatLevel())
				.sendString(RULES_WIDGET, ACCEPT_CHILD, "")
				.sendCS2Script(917, new Object[]{-1, -1}, "ii")
				.sendInterface(RULES_WIDGET, false)
				.sendInterfaceInventory(INVENTORY_WIDGET)
				.sendCS2Script(149, PARAMETERS, "IviiiIsssss")
				.sendAccessMask(1086, 109, 0, 0, 27)
				.sendAccessMask(1086, 107, 102, 0, 27)
				.sendAccessMask(2, 107, 104, 0, 27);
		player.getInterfaceState().addListener(duel.getPlayerStakes(), new InterfaceContainerListener(player, -1, 64168, 134));
		player.getInterfaceState().addListener(duel.getPartnerStakes(), new InterfaceContainerListener(player, -2, 60937, 134));
		player.getInterfaceState().addListener(player.getInventory(), new InterfaceContainerListener(player, -1, 64209, 93));

		partner.getInterfaceState().addListener(duel.getPartnerStakes(), new InterfaceContainerListener(partner, -1, 64168, 134));
		partner.getInterfaceState().addListener(duel.getPlayerStakes(), new InterfaceContainerListener(partner, -2, 60937, 134));
		partner.getInterfaceState().addListener(partner.getInventory(), new InterfaceContainerListener(partner, -1, 64209, 93));
	}

	@Override
	public void openConfirmationInterface(@Nonnull Player player, @Nonnull Player partner) {// for example this function i would make take @Nonnull Duel duel yea
		player.getActionSender().sendInterface(106, false);
		partner.getActionSender().sendInterface(106, false);
	}

	@Override
	public void handleEquipmentRules(@Nonnull Player player, int button) {
		Duel duel = player.getDuel();
		if (duel == null) {
			return;
		}
		Player partner = duel.getOpponent();
		duel.setEquipmentRule(button, !duel.getEquipmentRule(button));
		updateRulesInterface(player);
		updateRulesInterface(partner);
	}


	@Override
	public void updateRulesInterface(@Nonnull Player player) {
		Duel duel = player.getDuel();
		if (duel == null) {
			return;
		}
		int value = 0;
		for (int i = 0; i < duel.getEquipmentRules().length; i++) {
			Optional<VarpForButton> varpOption = VarpForButton.of(i);
			if (varpOption.isPresent() && duel.getEquipmentRule(i)) {
				value |= 1 << varpOption.get().getEquipmentIndex();
			}
		}
		variableService.set(player, DuelArenaVariables.DUEL_VARIABLE, value);
		variableService.send(player, DuelArenaVariables.DUEL_VARIABLE);
	}


	@Subscribe
	public void onButtonClick(GameInterfaceButtonEvent event) {
		Player player = event.getPlayer();
		Player partner = player.getRequestManager().getAcquaintance();
		if (partner == null) {
			return;
		}
		int interfaceId = event.getInterfaceId();
		if (interfaceId != INVENTORY_WIDGET && interfaceId != RULES_WIDGET && interfaceId != CONFIRM_WIDGET) {
			return;
		}
		int button = event.getButton();
		if (button >= 74 && button <= 84) {
			handleEquipmentRules(player, button - 74);
			return;
		}
		Item item;
		switch (interfaceId) {
			case INVENTORY_WIDGET:
				item = player.getInventory().get(event.getChildButton());
				if (item == null || item.getId() != event.getChildButton2()) {
					return;
				}
				switch (event.getMenuIndex()) {
					case 0:
//						OldDuel.offerItem(player, event.getChildButton(), item.getId(), 1);
						break;
				}
				break;
			case RULES_WIDGET:
				switch (event.getButton()) {
					case 102:
						item = player.getDuelContainer().get(event.getChildButton());
						if (item == null || item.getId() != event.getChildButton2()) {
							return;
						}
//						OldDuel.removeItem(player, event.getChildButton(), event.getChildButton2(), 1);
						break;
				}
				break;
		}
	}
}
