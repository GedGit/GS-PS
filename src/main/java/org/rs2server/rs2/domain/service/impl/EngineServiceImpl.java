package org.rs2server.rs2.domain.service.impl;

import com.diffplug.common.base.Errors;
import com.google.inject.Inject;
import org.rs2server.rs2.domain.service.api.EngineService;
import org.rs2server.rs2.task.Task;
import org.rs2server.util.BlockingExecutorService;

import javax.annotation.Nonnull;
import java.util.concurrent.*;

/**
 * An implementation of an EngineService.
 * @author twelve
 */
public class EngineServiceImpl implements EngineService, Runnable {

    /**
     * The queue of currently pending tasks.
     */
    private final BlockingQueue<Task> taskQueue;
    /**
     * A blocking executor service.
     */
    private final BlockingExecutorService blockingService;
    /**
     * A scheduled executor service for future events.
     */
    private final ScheduledExecutorService futureService;
    /**
     * A single threaded executor service.
     */
    private final ExecutorService singleService;
    /**
     * The thread this engine runs on.
     */
    private final Thread thread;
    /**
     * The running state of this engine.
     */
    private boolean running;

    @Inject
    public EngineServiceImpl() {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.blockingService = new BlockingExecutorService(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        this.futureService = Executors.newSingleThreadScheduledExecutor();
        this.singleService = Executors.newSingleThreadExecutor();
        this.thread = new Thread(this);
    }

    @Nonnull
    @Override
    public final ScheduledFuture<?> scheduleFuture(@Nonnull Runnable runnable, @Nonnull TimeUnit unit, long delay) {
        return futureService.schedule(Errors.log().wrap(runnable::run), delay, unit);
    }

    @Override
    public final void waitForPendingTasks() {
        Errors.log().run(blockingService::waitForPendingTasks);
    }

    @Override
    public final void offerTask(@Nonnull Task task) {
        taskQueue.offer(task);
    }

    @Override
    public final void offerToBlocking(@Nonnull Runnable runnable) {
        blockingService.submit(Errors.log().wrap(runnable::run));
    }

    @Override
    public final void offerToSingle(@Nonnull Runnable runnable) {
        singleService.submit(Errors.log().wrap(runnable::run));
    }

    @Override
    public final void offerFutureTask(@Nonnull Task task) {
        futureService.submit(Errors.log().wrap(task::execute));
    }

    @Override
    public final void setRunning(boolean running) {
        this.running = true;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public final void start() {
        setRunning(true);
        thread.start();
    }

    @Override
    public final void stop() {
        if(!running) {
            throw new IllegalStateException("The engine is already stopped.");
        }
        setRunning(false);
        thread.interrupt();
    }

    @Override
    public final Thread getThread() {
        return thread;
    }

    @Override
    public final void run() {
        while(isRunning()) {
            if (!taskQueue.isEmpty()) {
                Task task = Errors.log().getWithDefault(taskQueue::take, null);

                if (task == null) {
                    continue;
                }

                offerFutureTask(task);
            }
        }
    }
}
