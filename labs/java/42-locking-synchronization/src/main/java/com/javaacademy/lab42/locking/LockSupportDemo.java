package com.javaacademy.lab42.locking;

import java.util.concurrent.locks.LockSupport;

/**
 * Demonstrates LockSupport.park/unpark with Blocker introspection.
 * LockSupport is the foundation for all java.util.concurrent locks.
 */
public class LockSupportDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== LockSupport: park/unpark ===\n");

        Thread t = new Thread(() -> {
            System.out.println("Thread parking...");
            LockSupport.park();
            System.out.println("Thread unparked!");
        }, "park-thread");

        t.start();
        Thread.sleep(200);

        System.out.println("Main unparking thread");
        LockSupport.unpark(t);

        t.join();

        // Park with Blocker object
        Object blocker = new Object();
        Thread t2 = new Thread(() -> {
            System.out.println("Parking with blocker: " +
                java.util.concurrent.locks.LockSupport.getBlocker(Thread.currentThread()));
            LockSupport.park(blocker);
            System.out.println("Unparked from blocker park");
        }, "blocker-thread");

        t2.start();
        Thread.sleep(100);
        System.out.println("Blocker: " + LockSupport.getBlocker(t2));
        LockSupport.unpark(t2);
        t2.join();
    }
}
