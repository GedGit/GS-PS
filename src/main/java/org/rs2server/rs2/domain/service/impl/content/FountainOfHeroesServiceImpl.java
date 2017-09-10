package org.rs2server.rs2.domain.service.impl.content;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.Server;
import org.rs2server.rs2.content.api.GameObjectActionEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.PermissionService;
import org.rs2server.rs2.domain.service.api.content.FountainOfHeroesService;
import org.rs2server.rs2.domain.service.impl.PermissionServiceImpl;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;

import javax.annotation.Nonnull;

/**
 * Created by Tim on 12/7/2015.
 */
public class FountainOfHeroesServiceImpl implements FountainOfHeroesService {

    private static final int FOUNTAIN_ID = 2638;
    private static final int UNCHARGED_GLORY = 1704;
    private static final int CHARGED_4 = 1712;
    private static final int CHARGED_6 = 11978;
    private static final Animation BURY_ANIMATION = Animation.create(827);


    @Inject
    FountainOfHeroesServiceImpl(final HookService hookService) {
        hookService.register(this);
    }

    @Override
    public void rechargeGlories(@Nonnull Player player) {
        int amount = player.getInventory().getCount(UNCHARGED_GLORY);
        if (amount <= 0) {
            return;
        }
        player.getInventory().remove(new Item(UNCHARGED_GLORY, amount));
        PermissionService permissionService = Server.getInjector().getInstance(PermissionService.class);
        player.getInventory().add(new Item(permissionService.isAny(player, PermissionServiceImpl.SPECIAL_PERMISSIONS) ? CHARGED_6 : CHARGED_4, amount));
        player.playAnimation(BURY_ANIMATION);
        player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 1712, null, "You feel a power emanating from the fountain as it<br>recharges all your amulets. You can now run the<br>amulets to teleport and wear them to get more gems<br>whilst mining.");
    }


    @Subscribe
    public void onGameObjectActionEvent(final GameObjectActionEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();
        GameObject obj = event.getGameObject();
        if (item == null || item.getId() != UNCHARGED_GLORY || obj.getId() != FOUNTAIN_ID) {
            return;
        }
        if (event.getActionType() == GameObjectActionEvent.ActionType.ITEM_ON_OBJECT) {
            rechargeGlories(player);
        }
    }

}
