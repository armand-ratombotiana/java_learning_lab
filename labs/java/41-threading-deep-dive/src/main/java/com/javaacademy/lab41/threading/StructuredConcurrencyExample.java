package com.javaacademy.lab41.threading;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

/**
 * Demonstrates StructuredTaskScope (finalized in Java 22, redesigned in later versions).
 * Uses Joiner.anySuccessfulResultOrThrow (first successful result cancels remaining)
 * and Joiner.awaitAllSuccessfulOrThrow (waits for all, fails fast).
 */
public class StructuredConcurrencyExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Structured Concurrency ===\n");
        shutdownOnSuccessDemo();
        shutdownOnFailureDemo();
    }

    static void shutdownOnSuccessDemo() throws Exception {
        try (var scope = StructuredTaskScope.<String, String>open(StructuredTaskScope.Joiner.<String>anySuccessfulResultOrThrow())) {
            Subtask<String> user = scope.fork(() -> fetchUser(200));
            Subtask<String> config = scope.fork(() -> fetchConfig(100));
            String result = scope.join();
            System.out.println("anySuccessfulResultOrThrow result: " + result);
            System.out.println("User state: " + user.state() + ", Config state: " + config.state());
        }
    }

    static void shutdownOnFailureDemo() throws Exception {
        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow())) {
            Subtask<String> task1 = scope.fork(() -> fetchUser(150));
            Subtask<String> task2 = scope.fork(() -> fetchConfig(50));
            scope.join();
            System.out.println("awaitAllSuccessfulOrThrow: " + task1.get() + ", " + task2.get());
        }
    }

    static String fetchUser(long delay) throws InterruptedException {
        Thread.sleep(delay);
        return "User(name=Alice)";
    }

    static String fetchConfig(long delay) throws InterruptedException {
        Thread.sleep(delay);
        return "Config(theme=dark)";
    }
}
