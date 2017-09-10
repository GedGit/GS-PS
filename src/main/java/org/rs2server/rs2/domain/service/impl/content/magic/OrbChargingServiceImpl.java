package org.rs2server.rs2.domain.service.impl.content.magic;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.content.api.GameObjectSpellEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.content.magic.OrbChargingService;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.player.Player;
import java.util.Optional;

/**
 * @author Clank1337
 */
public class OrbChargingServiceImpl implements OrbChargingService {

	@Inject
	public OrbChargingServiceImpl(HookService hookService) {
		hookService.register(this);
	}

	@Subscribe
	public void handleSpellOnObject(GameObjectSpellEvent event) {
		final Player player = event.getPlayer();
		final GameObject obj = event.getObject();
		if (player != null && obj != null) {
			player.faceObject(obj);
			Optional<ChargeSpell> chargeOptional = ChargeSpell.of(obj.getId(), event.getSpellId());
			if (chargeOptional.isPresent()) {
				player.getActionSender().sendInterface(162, 546, 309, false);
				player.getActionSender().sendItemOnInterface(309, 2,
						chargeOptional.get().getOrbId(), 130);
				player.getActionSender().sendString(309, 6,
						"<br><br><br><br>" + CacheItemDefinition.get(chargeOptional.get().getOrbId()).getName());
				player.setInterfaceAttribute("orb_type",
						chargeOptional.get());
			}
		}
	}
}
