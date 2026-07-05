package com.arch.eventdriven;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncProcessor {
    private final EventBus eventBus;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public AsyncProcessor(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void start() {
        eventBus.subscribe("OrderCreated", event ->
            executor.submit(() -> {
                System.out.println("Async processing order: " + event.getAggregateId());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Completed async processing for: " + event.getAggregateId());
            })
        );
        System.out.println("AsyncProcessor started");
    }

    public void shutdown() {
        executor.shutdown();
    }
}
