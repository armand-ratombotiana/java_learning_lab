package concurrencydeep;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * CompletableFuture deep dive demonstration.
 * 
 * Shows: async execution, composition (thenApply, thenCompose, thenCombine),
 *        error recovery (exceptionally, handle), timeouts, allOf/anyOf,
 *        custom executors.
 * 
 * Time: O(1) per stage
 * Space: O(stages)
 */
public class CompletableFutureDemo {

    private static final ExecutorService pool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws Exception {
        // 1. Basic supplyAsync
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello", pool);
        assert future.get().equals("Hello");

        // 2. Chaining
        String result = CompletableFuture.supplyAsync(() -> "Hello", pool)
            .thenApply(s -> s + " World")
            .thenApply(String::toUpperCase)
            .get();
        assert result.equals("HELLO WORLD");

        // 3. thenCompose (flatMap) — avoid nested futures
        String composed = CompletableFuture.supplyAsync(() -> "A", pool)
            .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + "B", pool))
            .get();
        assert composed.equals("AB");

        // 4. thenCombine — parallel tasks
        String combined = CompletableFuture.supplyAsync(() -> "Hello", pool)
            .thenCombine(
                CompletableFuture.supplyAsync(() -> "World", pool),
                (a, b) -> a + " " + b
            ).get();
        assert combined.equals("Hello World");

        // 5. Error recovery
        String recovered = CompletableFuture.<String>supplyAsync(() -> { throw new RuntimeException("fail"); })
            .exceptionally(ex -> "Fallback")
            .get();
        assert recovered.equals("Fallback");

        // 6. handle (success or failure)
        String handled = CompletableFuture.<String>supplyAsync(() -> { throw new RuntimeException("err"); })
            .handle((res, ex) -> ex == null ? res : "handled-" + ex.getMessage())
            .get();
        assert handled.contains("handled");

        // 7. allOf — wait for all
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "1", pool);
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "2", pool);
        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "3", pool);
        CompletableFuture.allOf(f1, f2, f3).join();
        assert f1.get().equals("1") && f2.get().equals("2") && f3.get().equals("3");

        // 8. anyOf — first to complete
        CompletableFuture<String> fast = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(10); } catch (Exception e) { }
            return "fast";
        }, pool);
        CompletableFuture<String> slow = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(100); } catch (Exception e) { }
            return "slow";
        }, pool);
        Object first = CompletableFuture.anyOf(fast, slow).get();
        assert first.equals("fast");

        // 9. Timeout
        try {
            CompletableFuture.supplyAsync(() -> {
                try { Thread.sleep(5000); } catch (Exception e) { }
                return "late";
            }, pool).orTimeout(100, TimeUnit.MILLISECONDS).get();
            assert false : "Should timeout";
        } catch (TimeoutException | ExecutionException e) { }

        pool.shutdown();
        System.out.println("All CompletableFutureDemo tests passed.");
    }
}