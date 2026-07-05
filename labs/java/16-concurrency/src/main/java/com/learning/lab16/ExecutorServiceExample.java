package com.learning.lab16;

import java.util.concurrent.*;

/**
 * Demonstrates ExecutorService with thread pools and Future.
 */
public class ExecutorServiceExample {

    public static void showExecutorService() throws InterruptedException, ExecutionException {
        System.out.println("=== ExecutorService ===");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Callable<String> task1 = () -> {
            Thread.sleep(200);
            return "Task 1 result";
        };
        Callable<String> task2 = () -> {
            Thread.sleep(100);
            return "Task 2 result";
        };
        Callable<String> task3 = () -> "Task 3 immediate";

        Future<String> future1 = executor.submit(task1);
        Future<String> future2 = executor.submit(task2);
        Future<String> future3 = executor.submit(task3);

        System.out.println("Future 3 (immediate): " + future3.get());
        System.out.println("Future 2: " + future2.get());
        System.out.println("Future 1: " + future1.get());

        executor.shutdown();
        boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("Executor terminated: " + terminated);
    }
}
