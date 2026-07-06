package com.javaacademy.lab38.memorymodel;

import java.util.concurrent.atomic.AtomicInteger;

public class VolatileExample {

    private volatile boolean running = true;
    private int counter = 0;

    public void startWorker() {
        Thread worker = new Thread(() -> {
            while (running) {
                counter++;
            }
            System.out.println("Worker stopped. Counter: " + counter);
        });
        worker.start();
    }

    public void stopWorker() {
        running = false;
    }

    public int getCounter() { return counter; }

    private volatile int volatileCounter = 0;
    private int synchronizedCounter = 0;
    private final AtomicInteger atomicCounter = new AtomicInteger(0);
    private final Object lock = new Object();

    public void incrementVolatile() { volatileCounter++; }

    public void incrementSynchronized() {
        synchronized (lock) { synchronizedCounter++; }
    }

    public void incrementAtomic() { atomicCounter.incrementAndGet(); }

    public int getVolatileCounter() { return volatileCounter; }
    public int getSynchronizedCounter() { return synchronizedCounter; }
    public int getAtomicCounter() { return atomicCounter.get(); }

    public void runIncrementComparison(int increments, int threads) throws Exception {
        Thread[] workers = new Thread[threads];
        for (int t = 0; t < threads; t++) {
            workers[t] = new Thread(() -> {
                for (int i = 0; i < increments; i++) {
                    incrementVolatile();
                    incrementSynchronized();
                    incrementAtomic();
                }
            });
        }
        for (Thread w : workers) w.start();
        for (Thread w : workers) w.join();
    }
}
