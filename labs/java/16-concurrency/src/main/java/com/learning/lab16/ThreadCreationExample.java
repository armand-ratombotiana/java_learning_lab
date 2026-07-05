package com.learning.lab16;

/**
 * Demonstrates creating threads via extending Thread and implementing Runnable.
 */
public class ThreadCreationExample {

    public static void showThreadCreation() throws InterruptedException {
        System.out.println("=== Thread Creation ===");

        Thread thread1 = new Thread(() -> {
            System.out.println("  Runnable thread: " + Thread.currentThread().getName());
        }, "worker-1");

        Thread thread2 = new MyThread();

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Main thread: " + Thread.currentThread().getName());
    }
}

class MyThread extends Thread {
    public MyThread() {
        super("worker-2");
    }

    @Override
    public void run() {
        System.out.println("  Extended thread: " + getName());
    }
}
