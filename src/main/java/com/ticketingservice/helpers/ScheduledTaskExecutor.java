package com.ticketingservice.helpers;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Helper class to Schedule task execution. Executor service to schedule tasks after given delay.
 * <p>
 * Created by Chandramohan on 4/29/16.
 */
public class ScheduledTaskExecutor {

    private ScheduledExecutorService executorService;

    public ScheduledTaskExecutor(int threadPoolSize) {
        executorService = Executors.newScheduledThreadPool(threadPoolSize);
    }

    public <T> void schedule(Callable<T> callable, long delayInSeconds) {
        executorService.schedule(callable, delayInSeconds, TimeUnit.SECONDS);
    }

    public void destroy() {
        this.executorService.shutdown();
    }

}
