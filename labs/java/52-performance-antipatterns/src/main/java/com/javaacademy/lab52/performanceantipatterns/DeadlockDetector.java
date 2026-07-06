package com.javaacademy.lab52.performanceantipatterns;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Deadlock detection using ThreadMXBean.
 * Creates a classic deadlock (two threads, two locks, different orders),
 * then uses the management API to detect and report the deadlock.
 */
public class DeadlockDetector {

    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void main(String[] args) throws Exception {
        System.out.println("=== Deadlock Detection Demo ===");

        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("Thread-1: acquired lockA");
                sleep(100);
                synchronized (lockB) {
                    System.out.println("Thread-1: acquired lockB");
                }
            }
        }, "Deadlock-Thread-1");

        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("Thread-2: acquired lockB");
                sleep(100);
                synchronized (lockA) {
                    System.out.println("Thread-2: acquired lockA");
                }
            }
        }, "Deadlock-Thread-2");

        t1.start();
        t2.start();

        Thread.sleep(500);

        // Detect deadlock
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long[] deadlockedIds = threadBean.findDeadlockedThreads();

        if (deadlockedIds != null) {
            System.out.println("\nDEADLOCK DETECTED! Threads:");
            for (long id : deadlockedIds) {
                var info = threadBean.getThreadInfo(id);
                System.out.println("  " + info.getThreadName() + " (state=" + info.getThreadState() + ")");
                for (var monitor : info.getLockedMonitors()) {
                    System.out.println("    Waiting to lock: " + monitor.getLockedStackFrame());
                }
                for (var sync : info.getLockedSynchronizers()) {
                    System.out.println("    Holding: " + sync);
                }
            }
        } else {
            System.out.println("No deadlock detected (unexpected)");
        }

        System.exit(0);
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
