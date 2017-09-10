package org.rs2server.rs2.domain.service.api;

import org.rs2server.rs2.task.Task;

import javax.annotation.Nonnull;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A representation of an EngineService.
 * @author twelve
 */
public interface EngineService {


    /**
     * Schedules a runnable to be executed in the future.
     * @param runnable The runnable
     * @param unit The time unit of the delay
     * @param delay The delay relative to the time unit.
     * @return A scheduled future.
     */
    ScheduledFuture<?> scheduleFuture(@Nonnull Runnable runnable, @Nonnull TimeUnit unit, long delay);

    /**
     * Waits for pending tasks on the blocking executor.
     */
    void waitForPendingTasks();

    /**
     * Offers a task directly to the task queue
     * @param task The task.
     */
    void offerTask(@Nonnull Task task);

    /**
     * Offers a runnable to the blocking service.
     * @param runnable The runnable.
     */
    void offerToBlocking(@Nonnull Runnable runnable);

    /**
     * Offers a runnable to the single threaded executor.
     * @param runnable The runnable
     */
    void offerToSingle(@Nonnull Runnable runnable);

    /**
     * Submits a task directly to the scheduled executor.
     * @param task The task.
     */
    void offerFutureTask(@Nonnull Task task);

    /**
     * Sets if the engine is running or not.
     * @param running The running flag.
     */
    void setRunning(boolean running);

    /**
     * Gets if the engine is running or not.
     * @return {@code true} if the engine is running.
     */
    boolean isRunning();

    /**
     * Starts the engine service thread.
     */
    void start();

    /**
     * Stops the engine service thread.
     */
    void stop();

    /**
     * Gets the engine service thread.
     * @return The thread.
     */
    Thread getThread();

}
