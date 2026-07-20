package com.capstone.spark;

import org.junit.jupiter.api.*;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;

class TaskExecutorTest {
    private TaskExecutor executor;

    @BeforeEach
    void setUp() { executor = new TaskExecutor("exec-1", 4); }

    @Test void testSubmitTask() {
        var future = executor.submitTask(() -> 42, "compute");
        var result = future.join();
        assertEquals(42, result.result());
        assertEquals(TaskExecutor.TaskStatus.SUCCESS, result.status());
    }

    @Test void testFailedTask() {
        var future = executor.submitTask(() -> { throw new RuntimeException("fail"); }, "compute");
        var result = future.join();
        assertEquals(TaskExecutor.TaskStatus.FAILED, result.status());
    }

    @Test void testBatchSubmit() {
        List<Callable<String>> tasks = List.of(
            () -> "a", () -> "b", () -> "c");
        var results = executor.submitBatch(tasks, "batch");
        assertEquals(3, results.size());
    }

    @Test void testCounts() {
        executor.submitTask(() -> 1, "ok");
        executor.submitTask(() -> { throw new RuntimeException(); }, "fail");
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        assertEquals(1, executor.completedTaskCount());
        assertEquals(1, executor.failedTaskCount());
    }

    @Test void testShutdown() {
        executor.shutdown();
        assertFalse(executor.isRunning());
    }
}
