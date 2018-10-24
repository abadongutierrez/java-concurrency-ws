package com.jabaddon.nearsoft.javaconcurrencyws.threads;

public class UncaughtExceptionApp {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());

        Task task = new Task();
        Thread thread = new Thread(task);
        thread.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Finished!");
    }
}

class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("An uncaught exception was found.");
        System.out.println("Thread name: " + t.getName());
        System.out.println("Exception: " + e.getClass());
        System.out.println("Message: " + e.getMessage());
        e.printStackTrace(System.out);
    }
}

class Task implements Runnable {

    @Override
    public void run() {
        Integer.parseInt("TT");
    }
}
