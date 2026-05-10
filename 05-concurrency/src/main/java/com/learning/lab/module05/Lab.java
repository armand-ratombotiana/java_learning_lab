package com.learning.lab.module05;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Lab {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Module 05: Concurrency ===");
        threadCreationDemo();
        threadSynchronizationDemo();
        executorServiceDemo();
        concurrentCollectionsDemo();
        atomicVariablesDemo();
        completableFutureDemo();
    }

    static void threadCreationDemo() {
        System.out.println("\n--- Thread Creation ---");
        Thread thread = new Thread(() -> System.out.println("Running in new thread"));
        thread.start();

        Runnable task = () -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("Task running: " + i);
            }
        };
        Thread t = new Thread(task);
        t.start();
        try { t.join(); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    static void threadSynchronizationDemo() {
        System.out.println("\n--- Synchronization ---");
        Counter counter = new Counter();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });
        t1.start(); t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}
        System.out.println("Synchronized counter: " + counter.getCount());

        AtomicCounter atomicCounter = new AtomicCounter();
        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) atomicCounter.increment();
        });
        Thread t4 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) atomicCounter.increment();
        });
        t3.start(); t4.start();
        try { t3.join(); t4.join(); } catch (InterruptedException e) {}
        System.out.println("Atomic counter: " + atomicCounter.getCount());
    }

    static void executorServiceDemo() {
        System.out.println("\n--- Executor Service ---");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<Integer> future = executor.submit(() -> {
            Thread.sleep(100);
            return 42;
        });
        try {
            System.out.println("Future result: " + future.get());
        } catch (Exception e) { e.printStackTrace(); }

        ExecutorService cachedExecutor = Executors.newCachedThreadPool();
        cachedExecutor.submit(() -> System.out.println("Task 1"));
        cachedExecutor.submit(() -> System.out.println("Task 2"));
        executor.shutdown();
        cachedExecutor.shutdown();
    }

    static void concurrentCollectionsDemo() {
        System.out.println("\n--- Concurrent Collections ---");
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.putIfAbsent("C", 3);
        System.out.println("ConcurrentHashMap: " + map);

        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        queue.offer("First");
        queue.offer("Second");
        System.out.println("Poll: " + queue.poll());
        System.out.println("Remaining: " + queue);
    }

    static void atomicVariablesDemo() {
        System.out.println("\n--- Atomic Variables ---");
        AtomicInteger ai = new AtomicInteger(0);
        System.out.println("AtomicInteger: " + ai.incrementAndGet());
        System.out.println("Get and add: " + ai.getAndAdd(5));
        System.out.println("Current: " + ai.get());

        AtomicBoolean ab = new AtomicBoolean(false);
        ab.compareAndSet(false, true);
        System.out.println("AtomicBoolean: " + ab.get());

        AtomicReference<String> ar = new AtomicReference<>("initial");
        ar.set("updated");
        System.out.println("AtomicReference: " + ar.get());
    }

    static void completableFutureDemo() {
        System.out.println("\n--- CompletableFuture ---");
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            return "Result";
        });
        cf.thenAccept(System.out::println);
        cf.join();
    }
}

class Counter {
    private int count = 0;
    public synchronized void increment() { count++; }
    public synchronized int getCount() { return count; }
}

class AtomicCounter {
    private AtomicInteger count = new AtomicInteger(0);
    public void increment() { count.incrementAndGet(); }
    public int getCount() { return count.get(); }
}