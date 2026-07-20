package com.javaacademy.lab48.structuredconcurrency;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.*;

class StructuredConcurrencyUltraDeepTest {

    @Test
    void structuredTaskScopeShutdownOnSuccess() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            scope.fork(() -> { Thread.sleep(200); return "slow"; });
            scope.fork(() -> "fast");
            scope.join();
            String result = scope.result();
            assertEquals("fast", result);
        }
    }

    @Test
    void structuredTaskScopeShutdownOnFailureJoin() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<String> ok = scope.fork(() -> "ok");
            Future<String> alsoOk = scope.fork(() -> "also ok");
            scope.join();
            scope.throwIfFailed();
            assertEquals("ok", ok.resultNow());
            assertEquals("also ok", alsoOk.resultNow());
        }
    }

    @Test
    void virtualThreadWithStructuredConcurrency() throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> future = executor.submit(() -> "virtual result");
            assertEquals("virtual result", future.get(1, TimeUnit.SECONDS));
        }
    }

    @Test
    void completableFutureAllOfStructured() throws Exception {
        CompletableFuture<String> f1 = CompletableFuture.completedFuture("a");
        CompletableFuture<String> f2 = CompletableFuture.completedFuture("b");
        var all = CompletableFuture.allOf(f1, f2);
        all.get(1, TimeUnit.SECONDS);
        assertTrue(f1.isDone());
        assertTrue(f2.isDone());
    }
}
