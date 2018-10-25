package com.jabaddon.nearsoft.javaconcurrencyws_solutions.executors;

import java.util.concurrent.*;

public class WorkStealingPoolApp {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService workStealingPoolExecutorService =  Executors.newWorkStealingPool();
        Future<Integer> integerFuture1 = workStealingPoolExecutorService.submit(() -> 1);
        System.out.println("Class: " + integerFuture1.getClass());

        ExecutorService fixedThreadPoolExecutorService =  Executors.newFixedThreadPool(2);
        Future<Integer> integerFuture2 = fixedThreadPoolExecutorService.submit(() -> 1);
        System.out.println("Class: " + integerFuture2.getClass());

        workStealingPoolExecutorService.awaitTermination(1, TimeUnit.SECONDS);
        System.out.println("PoolSize: " + ((ThreadPoolExecutor) workStealingPoolExecutorService).getPoolSize());
        workStealingPoolExecutorService.shutdown();
    }
}
