package com.jabaddon.nearsoft.javaconcurrencyws_solutions.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SimpleExampleApp {
    public static void main(String[] args) {
        thenApplyExample();
        thenComposeExample();
        thenCombineExample();
        completableFutureExample();

        CompletableFuture<Integer> futureNumber = new CompletableFuture<>();
        futureNumber
                .thenApply(n -> n * 10)
                .thenAccept(System.out::println)
                .thenRun(() -> System.out.println("Finished!"));
        futureNumber.completeAsync(() -> {
            sleepSeconds(2);
            return 5;
        });
        System.out.println("Number is " + futureNumber.join());

    }

    private static void completableFutureExample() {
        CompletableFuture<Integer> futureNumber = getNumber();
        System.out.println("My number is " + futureNumber.join());
    }

    private static CompletableFuture<Integer> getNumber() {
        CompletableFuture<Integer> futureNumber = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            sleepSeconds(2);
            futureNumber.complete(5);
        });
        return futureNumber;
    }

    private static void thenApplyExample() {

        CompletableFuture<Integer> beforeThenApplyCf = CompletableFuture.supplyAsync(() -> {
            sleepSeconds(1);
            return 5;
        });
        CompletableFuture<Integer> afterThenApplyCf =
                beforeThenApplyCf.thenApply(n -> n * 10);
        System.out.println("My number is " + afterThenApplyCf.join());

    }

    private static void thenComposeExample() {

        CompletableFuture<Integer> beforeThenApplyCf = CompletableFuture.supplyAsync(() -> {
            sleepSeconds(1);
            return 5;
        });
        CompletableFuture<Integer> afterThenApplyCf =
                beforeThenApplyCf.thenCompose(n -> CompletableFuture.supplyAsync(() -> {
                    sleepSeconds(1);
                    return n * 10;
                }));
        System.out.println("My number is " + afterThenApplyCf.join());

    }

    private static void thenCombineExample() {

        CompletableFuture<Integer> factor1Cf = CompletableFuture.supplyAsync(() -> {
            sleepSeconds(1);
            return 5;
        });
        CompletableFuture<Integer> factor2Cf = CompletableFuture.supplyAsync(() -> {
            sleepSeconds(1);
            return 10;
        });
        CompletableFuture<Integer> resultCf =
                factor1Cf.thenCombine(factor2Cf, (n1, n2) -> n1 * n2);
        System.out.println("My number is " + resultCf.join());

    }

    private static void sleepSeconds(int timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
