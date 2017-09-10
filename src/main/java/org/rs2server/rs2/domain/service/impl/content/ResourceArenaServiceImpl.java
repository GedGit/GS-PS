package org.rs2server.rs2.domain.service.impl.content;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.Server;
import org.rs2server.rs2.content.api.GameNpcActionEvent;
import org.rs2server.rs2.content.api.GameObjectActionEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.PermissionService;
import org.rs2server.rs2.domain.service.api.content.ResourceArenaService;
import org.rs2server.rs2.domain.service.impl.PermissionServiceImpl;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.Agility;
import org.rs2server.rs2.tickable.Tickable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tim on 12/1/2015.
 */
public class ResourceArenaServiceImpl implements ResourceArenaService {

    private static final int MANDRITH = 6599;
    private static final int PILES = 13;

    public static List<Player> IN_ARENA = new ArrayList<>();

    public enum ResourceItems {
        GOLD_ORE(444),

        YEW_LOGS(1515),

        COAL(453),

        IRON_ORE(440),

        MITHRIL_ORE(447),

        ADAMANTITE_ORE(449),

        RAW_DARK_CRAB(11934),

        DARK_CRAB(11936),

        BURNT_DARK_CRAB(11938),

        GOLD_BAR(2357),

        IRON_BAR(2351),

        MITHRIL_BAR(2359),

        ADAMANTITE_BAR(2361),

        MAGIC_LOG(1513);

        private final int itemId;

        ResourceItems(int itemId) {
            this.itemId = itemId;
        }

        private static final Map<Integer, ResourceItems> resources = new HashMap<>();

        public static ResourceItems forId(int itemId) {
            return resources.get(itemId);
        }

        static {
            for (ResourceItems items : ResourceItems.values()) {
                resources.put(items.getItemId(), items);
            }
        }

        public int getItemId() {
            return itemId;
        }
    }

    @Inject
    public ResourceArenaServiceImpl(final HookService hookService) {
        hookService.register(this);
    }

    @Subscribe
    public void onObjectClick(final GameObjectActionEvent clickEvent) {
        if (clickEvent.getActionType() == GameObjectActionEvent.ActionType.OPTION_1) {
            final Player player = clickEvent.getPlayer();
            final GameObject object = clickEvent.getGameObject();
            switch (object.getId()) {
                case 26760:
                    if (object.getLocation().equals(Location.create(3184, 3944, 0))) {
                        handleDoorInteraction(player, object);
                    }
                    break;
            }
        }
    }

    @Subscribe
    public void onNpcClick(final GameNpcActionEvent clickEvent) {
        if (clickEvent.getNpc().getId() != MANDRITH && clickEvent.getNpc().getId() != PILES) return;
        final Player player = clickEvent.getPlayer();
        if (clickEvent.getActionType() == GameNpcActionEvent.ActionType.OPTION_1) {
            switch (clickEvent.getNpc().getId()) {
                case MANDRITH:
                    DialogueManager.openDialogue(player, 6599);
                    break;
                case PILES:
                    break;
            }
        }
    }

    @Override
    public void handleDoorInteraction(@Nonnull Player player, @Nonnull GameObject obj) {
        if (player.getLocation().equals(Location.create(3184, 3945, 0)) || player.getLocation().equals(Location.create(3184, 3944, 0))) {
            if (player.getLocation().equals(Location.create(3184, 3945, 0)) && !IN_ARENA.contains(player)) {
                PermissionService service = Server.getInjector().getInstance(PermissionService.class);
                Item requirement = new Item(995, service.isAny(player, PermissionServiceImpl.SPECIAL_PERMISSIONS) ? 35000 : 35000);
                if (!player.getInventory().hasItem(requirement)) {
                    player.getActionSender().sendMessage("You do not have enough coins to enter the Arena.");
                    return;
                }
                player.getInventory().remove(requirement);
                IN_ARENA.add(player);
            } else if (player.getLocation().equals(Location.create(3184, 3944, 0)) && IN_ARENA.contains(player)) {
                IN_ARENA.remove(player);
            }
            player.getActionSender().removeObject(obj);
            Location loc = obj.getLocation().transform(0, 1, 0);
            GameObject replacement = new GameObject(loc, obj.getId(), obj.getType(), 2, false);
            replacement.setLocation(loc);
            player.getActionSender().sendObject(replacement);
            player.setAttribute("busy", true);
            Agility.forceWalkingQueue(player, player.getWalkAnimation(), player.getX(), player.getY() == 3944 ? player.getY() + 1 : player.getY() - 1, 0, 1, false);
            World.getWorld().submit(new Tickable(2) {

                @Override
                public void execute() {
                    this.stop();
                    player.getActionSender().removeObject(replacement);
                    replacement.setLocation(Location.create(0, 0, 0));
                    player.getActionSender().sendObject(obj);
                    player.removeAttribute("busy");
                }
            });
        }
    }

    @Override
    public void handlePilesInteraction(@Nonnull Player player) {

    }

    @Override
    public void handleItemOnNPC(@Nonnull Player player, @Nonnull NPC n, @Nonnull Item item) {
        if (n.getId() != PILES) {
			return;
		}
        ResourceItems resource = ResourceItems.forId(item.getId());
        if (resource != null) {
            int amount = player.getInventory().getCount(item.getId());
            int cashAmount = player.getInventory().getCount(item.getId()) * 50;
            Item toAdd = new Item(item.getId() + 1, amount);
            Item toRemove = new Item(995, cashAmount);
            if (player.getInventory().hasItem(toRemove)) {
                player.getInventory().remove(new Item(item.getId(), amount));
                player.getInventory().remove(toRemove);
                player.getInventory().addItemIgnoreStackPolicy(toAdd);
                player.getActionSender().sendMessage("Piles exchanges your items into notes for " + cashAmount + " coins.");
            } else {
                player.getActionSender().sendMessage("Not enough coins to do this.");
            }
        }
    }
}
