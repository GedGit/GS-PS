package org.rs2server.rs2.task;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.EngineService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * A task which can execute multiple child tasks simultaneously.
 * @author Graham Edgecombe
 *
 */
public class ParallelTask implements Task {
	
	/**
	 * The child tasks.
	 */
	private Collection<Task> tasks;

	private final EngineService engineService;
	
	/**
	 * Creates the parallel task.
	 * @param tasks The child tasks.
	 */
	public ParallelTask(Task... tasks) {
		List<Task> taskList = new ArrayList<>();
		Collections.addAll(taskList, tasks);
		this.tasks = Collections.unmodifiableCollection(taskList);
		this.engineService = Server.getInjector().getInstance(EngineService.class);
	}
	
	@Override
	public void execute() {

		tasks.forEach(t -> engineService.offerToBlocking(t::execute));
		engineService.waitForPendingTasks();
	}

}
