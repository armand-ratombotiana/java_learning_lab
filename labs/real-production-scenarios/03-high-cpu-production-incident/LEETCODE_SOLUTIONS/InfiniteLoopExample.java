package com.prod.solutions.highcpu;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simulates a CPU spike caused by an infinite loop or spin-wait pattern.
 * In production, this could be caused by:
 * - Busy-wait loop waiting for a condition
 * - Polling loop without sleep
 * - Tight retry loop without backoff
 * - While(true) without break condition on error path
 */
public class InfiniteLoopExample {

    private static final AtomicBoolean running = new AtomicBoolean(true);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== CPU Spike Simulator ===");
        System.out.println("Starting CPU-intensive thread...");

        Thread cpuSpikeThread = new Thread(() -> {
            long iterations = 0;
            while (running.get()) {
                // BUG: Tight loop without any yield, sleep, or blocking call
                // This will consume 100% CPU on one core
                iterations++;
                if (iterations % 100_000_000 == 0) {
                    System.out.printf("[CPU Thread] %d iterations... (CPU at 100%%)%n", iterations);
                }
            }
            System.out.printf("[CPU Thread] Stopped after %d iterations%n", iterations);
        }, "cpu-spike-thread");

        cpuSpikeThread.start();

        // Let it run for 2 seconds
        Thread.sleep(2000);

        // Stop the CPU spike (in production: fix the infinite loop bug)
        running.set(false);
        cpuSpikeThread.join(1000);

        System.out.println("\n=== Fixed version with proper waiting ===");

        // Fixed version: use wait/notify or sleep in the loop
        AtomicBoolean flag = new AtomicBoolean(true);
        Thread fixedThread = new Thread(() -> {
            while (flag.get()) {
                // FIX: Use blocking wait instead of busy-spin
                synchronized (flag) {
                    try {
                        flag.wait(100); // Sleep 100ms instead of busy-wait
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            System.out.println("[Fixed Thread] Stopped (used 0% CPU while waiting)");
        }, "fixed-cpu-thread");

        fixedThread.start();
        Thread.sleep(500);
        flag.set(false);
        synchronized (flag) { flag.notify(); }
        fixedThread.join();

        System.out.println("=== Done ===");
    }
}
