package org.rs2server.rs2.task;

/**
 * A task is a class which carries out a unit of work.
 * @author Graham Edgecombe
 *
 */
public interface Task {
	
	/**
	 * Executes the task. The general contract of the execute method is that it
	 * may take any action whatsoever.
	 */
	void execute();

}
