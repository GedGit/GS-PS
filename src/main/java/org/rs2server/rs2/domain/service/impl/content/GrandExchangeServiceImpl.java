package org.rs2server.rs2.domain.service.impl.content;

import com.google.inject.Inject;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.content.GrandExchangeService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by Tim on 12/21/2015.
 */
public class GrandExchangeServiceImpl implements GrandExchangeService {

	private Item selectedItem;

	public static final int GRAND_EXCHANGE_WIDGET = 465;
	public static final int GRAND_EXCHANGE_IVENTORY_WIDGET = 467;

	private static final int EXAMINE_CHILD = 25;
	private static final int GUIDE_PRICE_CHILD = 26;
	private static final int CONFIRM_CHILD_CONFIG = 26;

	@Inject
	public GrandExchangeServiceImpl(final HookService hookService) {
		hookService.register(this);
	}

	@Override
	public void openGrandExchange(@Nonnull Player player) {
		player.getActionSender().sendCS2Script(828, new Object[] { 1 }, "i")
				.sendCS2Script(917, new Object[] { -1, -1 }, "ii").sendInterface(465, false)
				.sendInterfaceInventory(GRAND_EXCHANGE_IVENTORY_WIDGET).sendGEAccess(2, 2, 465, 6, 6)
				.sendGEAccess(3, 4, 465, 6, 2).sendGEAccess(2, 2, 465, 7, 6).sendGEAccess(3, 4, 465, 7, 2)
				.sendGEAccess(2, 2, 465, 8, 6).sendGEAccess(3, 4, 465, 8, 2).sendGEAccess(2, 2, 465, 9, 6)
				.sendGEAccess(3, 4, 465, 9, 2).sendGEAccess(2, 2, 465, 10, 6).sendGEAccess(3, 4, 465, 10, 2)
				.sendGEAccess(2, 2, 465, 11, 6).sendGEAccess(3, 4, 465, 11, 2).sendGEAccess(2, 2, 465, 12, 6)
				.sendGEAccess(3, 4, 465, 12, 2).sendGEAccess(2, 2, 465, 13, 6).sendGEAccess(3, 4, 465, 13, 2)
				.sendGEAccess(0, 0, 465, 21, 2).sendGEAccess(2, 3, 465, 22, 1038).sendGEAccess(0, 0, 465, 5, 6)
				.sendGEAccess(0, 13, 465, 23, 2).sendGEAccess(0, 27, 467, 0, 1026).sendConfig(1043, 0)
				.sendConfig(563, 0).sendConfig(375, 0).sendConfig(1151, -1).sendCS2Script(915, new Object[] { 3 }, "i");
	}

	@Override
	public void sendBuyScreen(@Nonnull Player player, Item item) {
		// setSelectedItem(item);
		player.getActionSender().sendConfig(563, -2147483648).sendConfig(375, 48)
				.sendConfig(563, player.getInventory().getCount(item.getId()))
				.sendConfig(1043, item.getDefinition().getStorePrice()).sendConfig(1151, item.getId())
				.sendString(GRAND_EXCHANGE_WIDGET, EXAMINE_CHILD, item.getDefinition().getExamine())
				.sendString(GRAND_EXCHANGE_WIDGET, GUIDE_PRICE_CHILD,
						convertCurrency(item.getDefinition().getStorePrice()))
				.sendInterfaceConfig(GRAND_EXCHANGE_WIDGET, CONFIRM_CHILD_CONFIG, false);

	}

	@Override
	public void sendSellScreen(@Nonnull Player player, int slot) {
		int configId = slot == 0 ? 16
				: slot == 1 ? 32
						: slot == 2 ? 48 : slot == 3 ? 64 : slot == 4 ? 80 : slot == 5 ? 96 : slot == 6 ? 112 : 128;
		player.getActionSender().sendConfig(375, configId).sendString(GRAND_EXCHANGE_WIDGET, EXAMINE_CHILD,
				"Choose an item from your inventory to sell.");
	}

	@Override
	public void sendSellScreenItem(@Nonnull Player player, Item item) {
		setSelectedItem(item);
		player.getActionSender().sendConfig(375, 16).sendConfig(563, -2147483648)
				.sendConfig(1043, item.getDefinition().getStorePrice()).sendConfig(1151, item.getId())
				.sendConfig(563, player.getInventory().getCount(item.getId()))
				.sendString(GRAND_EXCHANGE_WIDGET, EXAMINE_CHILD, item.getDefinition().getExamine())
				.sendString(GRAND_EXCHANGE_WIDGET, GUIDE_PRICE_CHILD,
						convertCurrency(item.getDefinition().getStorePrice()))
				.sendInterfaceConfig(GRAND_EXCHANGE_WIDGET, CONFIRM_CHILD_CONFIG, false)
				.sendInterfaceInventory(GRAND_EXCHANGE_IVENTORY_WIDGET);
	}

	@Override
	public void refresh(@Nonnull Player player) {
		player.getActionSender().sendConfig(375, 0)// 48
				.sendConfig(563, -2147483648).sendConfig(1043, 0).sendConfig(1151, -1).sendConfig(563, -2147483648)
				.sendString(GRAND_EXCHANGE_WIDGET, GUIDE_PRICE_CHILD, "")
				.sendInterfaceConfig(GRAND_EXCHANGE_WIDGET, CONFIRM_CHILD_CONFIG, true);
	}

	public void setSelectedItem(Item item) {
		this.selectedItem = item;
	}

	public Item getSelectedItem() {
		return selectedItem;
	}

	private final String convertCurrency(int amount) {
		DecimalFormatSymbols separator = new DecimalFormatSymbols();
		separator.setGroupingSeparator(',');
		DecimalFormat formatter = new DecimalFormat("#,###,###,###", separator);
		return formatter.format(amount);
	}
}
