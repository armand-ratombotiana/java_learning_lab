package com.learning.lab30;

import java.lang.management.*;

/**
 * Demonstrates thread information and thread dump hints using ManagementFactory.
 */
public class ThreadDumpHintExample {

    public static void showThreadInfo() {
        System.out.println("=== Thread Dump Hints ===");

        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

        int threadCount = threadBean.getThreadCount();
        int peakCount = threadBean.getPeakThreadCount();
        int daemonCount = threadBean.getDaemonThreadCount();

        System.out.println("Current threads: " + threadCount);
        System.out.println("Peak threads: " + peakCount);
        System.out.println("Daemon threads: " + daemonCount);

        System.out.println("\nCurrent thread info:");
        Thread current = Thread.currentThread();
        System.out.println("  Name: " + current.getName());
        System.out.println("  ID: " + current.threadId());
        System.out.println("  Priority: " + current.getPriority());
        System.out.println("  State: " + current.getState());
        System.out.println("  Is daemon: " + current.isDaemon());

        System.out.println("\nTo get a full thread dump:");
        System.out.println("  jstack <pid>");
        System.out.println("  jcmd <pid> Thread.print");
        System.out.println("  Or programmatically: threadBean.dumpAllThreads(true, true)");
    }
}
