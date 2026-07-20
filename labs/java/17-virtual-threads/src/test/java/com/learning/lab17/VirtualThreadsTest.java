package com.learning.lab17;

import org.junit.jupiter.api.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class VirtualThreadsTest {

    @Test
    @DisplayName("Virtual thread runs a task")
    void virtualThreadRunsTask() throws Exception {
        var result = new String[1];
        Thread vt = Thread.ofVirtual()
            .name("test-vt")
            .start(() -> result[0] = "ran");
        vt.join();
        assertEquals("ran", result[0]);
    }

    @Test
    @DisplayName("Virtual thread is virtual")
    void virtualThreadIsVirtual() {
        Thread vt = Thread.ofVirtual().start(() -> {});
        assertTrue(vt.isVirtual());
    }

    @Test
    @DisplayName("Multiple virtual threads run concurrently")
    void multipleVirtualThreads() throws Exception {
        int count = 10;
        var latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            Thread.ofVirtual().start(() -> latch.countDown());
        }
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("Virtual thread per task executor")
    void virtualThreadPerTaskExecutor() throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> future = executor.submit(() -> "virtual result");
            assertEquals("virtual result", future.get(1, TimeUnit.SECONDS));
        }
    }

    @Test
    @DisplayName("Virtual thread pool handles multiple tasks")
    void virtualThreadPoolMultipleTasks() throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = executor.invokeAll(List.of(
                () -> "task1", () -> "task2", () -> "task3"
            ));
            assertEquals(3, futures.size());
            assertEquals("task1", futures.get(0).get());
            assertEquals("task2", futures.get(1).get());
            assertEquals("task3", futures.get(2).get());
        }
    }

    @Test
    @DisplayName("Structured concurrency with task scope")
    void structuredConcurrency() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<String> task1 = scope.fork(() -> "result1");
            Future<String> task2 = scope.fork(() -> "result2");
            scope.join();
            scope.throwIfFailed();
            assertEquals("result1", task1.resultNow());
            assertEquals("result2", task2.resultNow());
        }
    }
}
