package com.jabaddon.nearsoft.javaconcurrencyws_solutions.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class CachedThreadPoolApp {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService =  Executors.newCachedThreadPool();

        IntStream.range(1, 1000+1).forEach(v -> {
            executorService.submit(() -> {
                System.out.println("Running task #" + v);
            });
        });

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        System.out.println("PoolSize: " + ((ThreadPoolExecutor) executorService).getPoolSize());
        System.out.println("ActiveCount: " + ((ThreadPoolExecutor) executorService).getActiveCount());
        System.out.println("CompletedTaskCount: " + ((ThreadPoolExecutor) executorService).getCompletedTaskCount());
        executorService.shutdown();
    }
}
