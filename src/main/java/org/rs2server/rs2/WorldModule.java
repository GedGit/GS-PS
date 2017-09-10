package org.rs2server.rs2;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import org.rs2server.rs2.domain.dao.DaoModule;
import org.rs2server.rs2.domain.service.ServiceModule;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingUpdateEvent;
import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.event.impl.*;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.tickable.impl.CleanupTick;
import org.rs2server.rs2.tickable.impl.ItemSpawnTick;

/**
 * The top-level module binding the world instance and necessary submodules.
 *
 * @author tommo
 */
public class WorldModule extends AbstractModule {

	@Override
	protected void configure() {
		
		install(new DaoModule());
		install(new ServiceModule());

		// Bind events which the World instance will register for execution.
		final Multibinder<Event> eventBinder = Multibinder.newSetBinder(binder(), Event.class);
		eventBinder.addBinding().to(UpdateEvent.class);
		eventBinder.addBinding().to(WarriorsGuildEvent.class);
		eventBinder.addBinding().to(PlayerSaveEvent.class);
		eventBinder.addBinding().to(FarmingUpdateEvent.class);
		eventBinder.addBinding().to(SecondTick.class);
		eventBinder.addBinding().to(ServerMessages.class);
        eventBinder.addBinding().to(PestControlEvent.class);

		// Bind tickables which the World instance will register for execution.
		final Multibinder<Tickable> tickableBinder = Multibinder.newSetBinder(binder(), Tickable.class);
		tickableBinder.addBinding().to(CleanupTick.class);
		//tickableBinder.addBinding().to(GitCommitTick.class);
		tickableBinder.addBinding().to(ItemSpawnTick.class);

		bind(World.class).in(Singleton.class);
	}
}
