package com.jabaddon.nearsoft.javaconcurrencyws.threads;

public class VolatileApp {
    public static void main(String[] args) {
        GlobalValue globalValue = new GlobalValue(0);
        new ValueChangeListener(globalValue).start();
        new ValueIncrementer(globalValue).start();
    }

    private static class GlobalValue {
        private int value;

        GlobalValue(int initial) {
            value = initial;
        }

        public void increment() {
            value++;
        }

        public int value() {
            return value;
        }
    }

    private static class ValueChangeListener extends Thread {

        private final GlobalValue globalValue;

        ValueChangeListener(GlobalValue globalValue) {
            this.globalValue = globalValue;
        }

        @Override
        public void run() {
            int localValue = globalValue.value();
            while (localValue < 5) {
                if (localValue != globalValue.value()) {
                    System.out.println("Value changed: " + globalValue.value());
                    localValue = globalValue.value();
                }
            }
        }
    }

    private static class ValueIncrementer extends Thread {
        private final GlobalValue globalValue;

        ValueIncrementer(GlobalValue globalValue) {
            this.globalValue = globalValue;
        }

        @Override
        public void run() {
            while (globalValue.value() < 5) {
                System.out.println("Incrementing value: " + globalValue.value());
                globalValue.increment();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

