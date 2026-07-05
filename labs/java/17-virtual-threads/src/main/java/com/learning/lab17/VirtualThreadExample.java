package com.learning.lab17;

/**
 * Demonstrates Thread.ofVirtual() for creating virtual threads (Preview in Java 19, final in Java 21).
 */
public class VirtualThreadExample {

    public static void showVirtualThreads() throws InterruptedException {
        System.out.println("=== Thread.ofVirtual() ===");

        Thread vThread = Thread.ofVirtual()
            .name("virtual-1")
            .start(() -> {
                System.out.println("  Running in: " + Thread.currentThread());
                System.out.println("  Is virtual? " + Thread.currentThread().isVirtual());
            });

        vThread.join();
        System.out.println("Virtual thread joined");

        System.out.println("\n--- Multiple Virtual Threads ---");
        var threads = java.util.stream.IntStream.range(0, 5)
            .mapToObj(i -> Thread.ofVirtual()
                .name("vtask-" + i)
                .unstarted(() -> {
                    System.out.println("  " + Thread.currentThread().getName() + " running");
                }))
            .toList();

        threads.forEach(Thread::start);
        for (Thread t : threads) {
            t.join();
        }
    }
}
