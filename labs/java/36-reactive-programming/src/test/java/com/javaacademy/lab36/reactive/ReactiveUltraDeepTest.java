package com.javaacademy.lab36.reactive;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.*;

class ReactiveUltraDeepTest {

    @Test
    void completableFutureSupplyAsync() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "async");
        assertEquals("async", future.get(1, TimeUnit.SECONDS));
    }

    @Test
    void completableFutureThenAccept() throws Exception {
        var result = new StringBuilder();
        CompletableFuture.supplyAsync(() -> "hello")
            .thenAccept(s -> result.append(s))
            .get(1, TimeUnit.SECONDS);
        assertEquals("hello", result.toString());
    }

    @Test
    void completableFutureExceptionally() throws Exception {
        String result = CompletableFuture.supplyAsync(() -> {
                throw new RuntimeException("fail");
            })
            .exceptionally(ex -> "recovered")
            .get(1, TimeUnit.SECONDS);
        assertEquals("recovered", result);
    }

    @Test
    void flowPublisherSubscription() throws InterruptedException {
        var result = new CopyOnWriteArrayList<Integer>();
        var submissionPublisher = new SubmissionPublisher<Integer>();
        submissionPublisher.subscribe(new Flow.Subscriber<>() {
            private Flow.Subscription subscription;
            @Override
            public void onSubscribe(Flow.Subscription s) { subscription = s; s.request(Long.MAX_VALUE); }
            @Override
            public void onNext(Integer item) { result.add(item); }
            @Override
            public void onError(Throwable t) {}
            @Override
            public void onComplete() {}
        });
        submissionPublisher.submit(1);
        submissionPublisher.submit(2);
        submissionPublisher.submit(3);
        Thread.sleep(100);
        submissionPublisher.close();
        assertEquals(3, result.size());
        assertTrue(result.contains(1));
        assertTrue(result.contains(2));
        assertTrue(result.contains(3));
    }
}
