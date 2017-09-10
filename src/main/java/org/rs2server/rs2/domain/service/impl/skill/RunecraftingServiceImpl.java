package org.rs2server.rs2.domain.service.impl.skill;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.content.api.GameItemInventoryActionEvent;
import org.rs2server.rs2.content.api.GameObjectActionEvent;
import org.rs2server.rs2.content.api.GamePlayerLoginEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.skill.RunecraftingService;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.runecrafting.Talisman;
import org.rs2server.rs2.tickable.impl.StoppingTick;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Clank1337
 */
public class RunecraftingServiceImpl implements RunecraftingService {

	private static final int RUNE_ESSENCE = 1436;
	private static final int PURE_ESSENCE = 7936;
	private static final int RUNE_ESSENCE_OBJECT = 7471;
	private static final int RUNE_ESSENCE_PORTAL = 7479;

	private static final Animation BONE_BURYING_ANIMATION = Animation.create(827);

	@Inject
	public RunecraftingServiceImpl(HookService hookService) {
		hookService.register(this);
	}

	@Override
	public void depositEssenceInPouch(@Nonnull Player player, @Nonnull PouchType type) {
		if (player.getSkills().getLevelForExperience(Skills.RUNECRAFTING) < type.getLevelReq()) {
			player.getActionSender().sendMessage("You need a Runecrafting level of " + type.getLevelReq() + " to use this pouch.");
			return;
		}
		int amount = player.getInventory().getCount(PURE_ESSENCE);
		amount = amount > type.getCapacity() ? type.getCapacity() : amount;
		Map<PouchType, Integer> pouchMap = player.getDatabaseEntity().getRunePouches();

		if (isPouchFree(player, type)) {
			player.getInventory().remove(new Item(PURE_ESSENCE, amount));
			pouchMap.put(type, amount);
		}
	}

	@Override
	public void claimEssenceInPouch(@Nonnull Player player, @Nonnull PouchType type) {
		if (player.getSkills().getLevelForExperience(Skills.RUNECRAFTING) < type.getLevelReq()) {
			player.getActionSender().sendMessage("You need a Runecrafting level of " + type.getLevelReq() + " to use this pouch.");
			return;
		}
		Map<PouchType, Integer> pouchMap = player.getDatabaseEntity().getRunePouches();
		int freeSlots = player.getInventory().freeSlots();
		int available = pouchMap.get(type);
		if (freeSlots < available) {
			available = freeSlots;
		}
		if (!isPouchEmpty(player, type) && player.getInventory().add(new Item(PURE_ESSENCE, available))) {
			pouchMap.put(type, pouchMap.get(type) - available);
		}
	}

	@Override
	public void checkEssenceInPouch(@Nonnull Player player, @Nonnull PouchType type) {
		Map<PouchType, Integer> pouchMap = player.getDatabaseEntity().getRunePouches();
		if (!isPouchEmpty(player, type)) {
			int amount = pouchMap.get(type);
			player.getActionSender().sendMessage("There is currently " + amount + " essence in this pouch.");
		}
	}

	@Override
	public void handleMysteriousAltarInteraction(@Nonnull Player player, @Nonnull MysteriousAltarType type, @Nonnull GameObjectActionEvent clickEvent) {
		if (clickEvent.getActionType() == GameObjectActionEvent.ActionType.ITEM_ON_OBJECT) {
			GameObject object = clickEvent.getGameObject();
			Item item = clickEvent.getItem();
			if (item == null || item.getId() != type.getTalisman()) {
				return;
			}
			player.faceObject(object);
			player.getActionSender().sendMessage("You hold the " + CacheItemDefinition.get(type.getTalisman()).getName() + " towards the mysterious ruins.");
		}
		player.playAnimation(BONE_BURYING_ANIMATION);
		World.getWorld().submit(new StoppingTick(3) {
			@Override
			public void executeAndStop() {
				player.getActionSender().sendMessage("You feel a powerful force take hold of you...");
				player.setTeleportTarget(type.getLocation());
			}
		});
	}

	@Override
	public void handleAltarInteraction(@Nonnull Player player, @Nonnull AltarType type) {
		if (player.getInventory().getCount(RUNE_ESSENCE) <= 0 && player.getInventory().getCount(PURE_ESSENCE) <= 0) {
			return;
		}
		player.getActionQueue().addAction(new RuneCraftingAction(player, type));
	}

	@Override
	public void handleEssenceMineInteraction(@Nonnull Player player, @Nonnull EssenceMine mine, @Nonnull GameObjectActionEvent clickEvent) {
		if (clickEvent.getActionType() == GameObjectActionEvent.ActionType.OPTION_1) {
			GameObject obj = clickEvent.getGameObject();
			if (obj == null) {
				return;
			}
			switch (obj.getId()) {
				case RUNE_ESSENCE_OBJECT:
					player.getActionQueue().addAction(new EssenceMiningAction(player, obj));
					break;
				case RUNE_ESSENCE_PORTAL:
					World.getWorld().submit(new StoppingTick(1) {
						@Override
						public void executeAndStop() {
							player.setTeleportTarget(Location.create(3252, 3401));
						}
					});
					break;
			}
		}
	}

	@SuppressWarnings("incomplete-switch")
	@Subscribe
	public void onInventoryItemAction(final GameItemInventoryActionEvent event) {
		final Player player = event.getPlayer();
		final Item item = event.getItem();
		PouchType type = PouchType.of(item.getId());
		if (type != null) {
			switch (event.getClickType()) {
				case OPTION_1:
					depositEssenceInPouch(player, type);
					break;
				case WIELD_OPTION:
					claimEssenceInPouch(player, type);
					break;
				case OPTION_2:
					checkEssenceInPouch(player, type);
					break;
			}
		}
	}

	@Subscribe
	public final void onPlayerLogin(@Nonnull GamePlayerLoginEvent event) {
		Player player = event.getPlayer();
		Item helmet = player.getEquipment().get(Equipment.SLOT_HELM);
		if (helmet != null) {
			Talisman talisman = Talisman.getTalismanByTiara(helmet.getId());
			if (talisman != null) {
				if (helmet.getId() == talisman.getTiaraId()) {
					player.getActionSender().sendConfig(491, talisman.getTiaraConfig());
				}
			}
		}
	}

	@Subscribe
	public void onObjectClick(GameObjectActionEvent clickEvent) {
		final Player player = clickEvent.getPlayer();
		final GameObject obj = clickEvent.getGameObject();
		MysteriousAltarType altar = MysteriousAltarType.of(obj.getId());
		AltarType type = AltarType.of(obj.getId());
		EssenceMine mine = EssenceMine.of(obj.getId());
		if (altar != null) {
			handleMysteriousAltarInteraction(player, altar, clickEvent);
		}
		if (type != null) {
			handleAltarInteraction(player, type);
		}
		if (mine != null) {
			handleEssenceMineInteraction(player, mine, clickEvent);
		}
	}

	@Override
	public boolean isPouchFree(@Nonnull Player player, @Nonnull PouchType type) {
		Map<PouchType, Integer> pouchMap = player.getDatabaseEntity().getRunePouches();
		if (pouchMap.containsKey(type) && pouchMap.get(type) >= type.getCapacity()) {
			player.getActionSender().sendMessage("This pouch seems to be full.");
			return false;
		}
		return true;
	}

	@Override
	public boolean isPouchEmpty(@Nonnull Player player, @Nonnull PouchType type) {
		Map<PouchType, Integer> pouchMap = player.getDatabaseEntity().getRunePouches();
		if (!pouchMap.containsKey(type) || (pouchMap.containsKey(type) && pouchMap.get(type) <= 0)) {
			player.getActionSender().sendMessage("This pouch seems to be empty.");
			return true;
		}
		return false;
	}

}
