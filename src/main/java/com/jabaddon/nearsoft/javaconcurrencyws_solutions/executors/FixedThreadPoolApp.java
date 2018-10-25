package com.jabaddon.nearsoft.javaconcurrencyws_solutions.executors;

import java.util.concurrent.*;
import java.util.stream.IntStream;

public class FixedThreadPoolApp {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService =  Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ((ThreadPoolExecutor) executorService).setRejectedExecutionHandler((r, executor) -> {
            System.out.println("A runnable was rejected!");
        });

        IntStream.range(1, 1000+1).forEach(v -> {
            executorService.submit(() -> {
                System.out.println("Running task #" + v);
            });
        });

        executorService.awaitTermination(1, TimeUnit.SECONDS);
        System.out.println("PoolSize: " + ((ThreadPoolExecutor) executorService).getPoolSize());
        executorService.shutdown();
    }
}
