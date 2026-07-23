package com.prod.solutions.deadlock;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * Programmatic deadlock detection using ThreadMXBean.
 * In production, this can be exposed via JMX or a health check endpoint
 * to automatically detect and alert on deadlocked threads.
 */
public class DeadlockDetection {

    private final ThreadMXBean threadMXBean;

    public DeadlockDetection() {
        this.threadMXBean = ManagementFactory.getThreadMXBean();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ThreadMXBean Deadlock Detection Demo ===");

        DeadlockDetection detector = new DeadlockDetection();

        // Create deadlock scenario
        final Object lock1 = new Object();
        final Object lock2 = new Object();

        Thread thread1 = new Thread(() -> {
            synchronized (lock1) {
                sleep(100);
                synchronized (lock2) {
                    System.out.println("Thread1 acquired both locks");
                }
            }
        }, "Thread-1");

        Thread thread2 = new Thread(() -> {
            synchronized (lock2) {
                sleep(100);
                synchronized (lock1) {
                    System.out.println("Thread2 acquired both locks");
                }
            }
        }, "Thread-2");

        thread1.start();
        thread2.start();

        Thread.sleep(500);

        long[] deadlockedThreadIds = detector.findDeadlockedThreads();

        if (deadlockedThreadIds != null && deadlockedThreadIds.length > 0) {
            System.out.println("\n!!! DEADLOCK DETECTED !!!");
            ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(deadlockedThreadIds, true, true);

            for (ThreadInfo info : threadInfos) {
                if (info != null) {
                    System.out.printf("Thread: %s (ID=%d)%n", info.getThreadName(), info.getThreadId());
                    System.out.printf("  State: %s%n", info.getThreadState());
                    if (info.getLockName() != null) {
                        System.out.printf("  Waiting on: %s%n", info.getLockName());
                        System.out.printf("  Locked by: %s (ID=%d)%n",
                                info.getLockOwnerName(), info.getLockOwnerId());
                    }
                    for (StackTraceElement ste : info.getStackTrace()) {
                        System.out.printf("    at %s.%s(%s:%d)%n",
                                ste.getClassName(), ste.getMethodName(),
                                ste.getFileName(), ste.getLineNumber());
                    }
                }
            }
            System.out.println("\nFix: Ensure consistent lock ordering or use tryLock() with timeout.");
        } else {
            System.out.println("No deadlock detected.");
        }

        thread1.interrupt();
        thread2.interrupt();
    }

    /**
     * Detects deadlocked threads using ThreadMXBean.
     * @return array of deadlocked thread IDs, or null if none
     */
    public long[] findDeadlockedThreads() {
        return threadMXBean.findDeadlockedThreads();
    }

    /**
     * Returns detailed information about all deadlocked threads.
     */
    public ThreadInfo[] getDeadlockedThreadInfo() {
        long[] ids = findDeadlockedThreads();
        if (ids == null || ids.length == 0) return new ThreadInfo[0];
        return threadMXBean.getThreadInfo(ids, true, true);
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
