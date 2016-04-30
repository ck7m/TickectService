package com.ticketingservice.helpers;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by lva833 on 4/29/16.
 */
public class ScheduledTaskExecutor {

    private ScheduledExecutorService executorService;

    public ScheduledTaskExecutor(int threadPoolSize) {
        executorService = Executors.newScheduledThreadPool(threadPoolSize);
    }

    public static void scheduleWithTimer(Runnable runnable, int timeToWait) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, timeToWait);
    }

    public <T> void schedule(Callable<T> callable, long delayInSeconds) {
        executorService.schedule(callable, delayInSeconds, TimeUnit.SECONDS);
    }

    public void destroy() {
        this.executorService.shutdown();
    }

}
