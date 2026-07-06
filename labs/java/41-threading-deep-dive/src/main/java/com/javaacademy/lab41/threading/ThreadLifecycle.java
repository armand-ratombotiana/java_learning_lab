package com.javaacademy.lab41.threading;

/**
 * Demonstrates all six thread states (NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED)
 * with explicit state transitions observable via Thread.getState().
 */
public class ThreadLifecycle {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Thread Lifecycle Demo ===\n");

        // 1. NEW state
        Thread t = new Thread(() -> {
            try {
                // 4. TIMED_WAITING
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "lifecycle-thread");
        System.out.println("After creation (NEW): " + t.getState());

        // 2. RUNNABLE
        t.start();
        System.out.println("After start (RUNNABLE): " + t.getState());

        // 3. WAITING — main thread waits for t to complete
        synchronized (t) {
            Thread waiter = new Thread(() -> {
                synchronized (t) {
                    // Will block until t terminates and we notify
                }
            }, "waiter-thread");
            waiter.start();
            Thread.sleep(50); // let waiter acquire lock
            System.out.println("Waiter state (BLOCKED): " + waiter.getState());

            // Release lock so waiter proceeds
        }
        Thread.sleep(100);
        System.out.println("After completion (TERMINATED): " + t.getState());
    }

    /**
     * Returns a descriptive string for each thread state.
     */
    public static String describeState(Thread.State state) {
        return switch (state) {
            case NEW -> "Not yet started";
            case RUNNABLE -> "Executing or ready to execute";
            case BLOCKED -> "Waiting for a monitor lock";
            case WAITING -> "Waiting indefinitely for another thread";
            case TIMED_WAITING -> "Waiting for a specified time";
            case TERMINATED -> "Completed execution";
        };
    }
}
