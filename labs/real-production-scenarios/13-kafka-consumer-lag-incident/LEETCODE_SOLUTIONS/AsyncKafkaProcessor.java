package com.prod.solutions.kafka;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Fixes slow Kafka consumer by using async processing with a thread pool.
 * Instead of processing each message synchronously in the poll loop,
 * messages are handed off to a thread pool for parallel processing.
 *
 * FIX: Decouple polling from processing using a work queue.
 */
public class AsyncKafkaProcessor {

    static class AsyncConsumer {
        private final String groupId;
        private final String topic;
        private final AtomicLong messagesConsumed = new AtomicLong(0);
        private final ExecutorService processingPool;
        private final BlockingQueue<Runnable> workQueue;
        private volatile boolean running = true;

        AsyncConsumer(String groupId, String topic, int workers, int queueSize) {
            this.groupId = groupId;
            this.topic = topic;
            this.workQueue = new LinkedBlockingQueue<>(queueSize);
            this.processingPool = new ThreadPoolExecutor(
                    workers, workers, 0L, TimeUnit.MILLISECONDS, workQueue);
        }

        void processMessageAsync(int msgId) {
            if (!running) return;

            try {
                processingPool.submit(() -> {
                    try {
                        // Simulate async processing
                        Thread.sleep(ThreadLocalRandom.current().nextInt(50, 150));
                        messagesConsumed.incrementAndGet();
                        System.out.printf("  [Async] Processed message %d (queue: %d)%n",
                                msgId, workQueue.size());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            } catch (RejectedExecutionException e) {
                System.out.printf("  [Async] Queue full! Message %d rejected (backpressure)%n", msgId);
            }
        }

        long getLag(long produced) {
            return produced - messagesConsumed.get();
        }

        void shutdown() {
            running = false;
            processingPool.shutdown();
            try {
                processingPool.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                processingPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Async Kafka Consumer (Fix) Demo ===\n");

        AsyncConsumer consumer = new AsyncConsumer("order-processor", "orders", 8, 100);

        ScheduledExecutorService producer = Executors.newSingleThreadScheduledExecutor();
        AtomicLong producedCount = new AtomicLong(0);

        // Producer: 10 msg/s
        producer.scheduleAtFixedRate(() -> {
            producedCount.incrementAndGet();
        }, 0, 100, TimeUnit.MILLISECONDS);

        // Consumer: poll loop that hands off to thread pool
        long start = System.currentTimeMillis();
        int msgId = 0;
        while (System.currentTimeMillis() - start < 5000) {
            msgId++;
            consumer.processMessageAsync(msgId);

            long lag = consumer.getLag(producedCount.get());
            if (msgId % 5 == 0) {
                System.out.printf("Produced: %d, Consumed: %d, Lag: %d%n",
                        producedCount.get(), consumer.messagesConsumed.get(), lag);
            }

            Thread.sleep(100);
        }

        producer.shutdown();
        consumer.shutdown();

        System.out.printf("Final lag: %d%n", consumer.getLag(producedCount.get()));
        System.out.println("\nAsync processing with thread pool eliminated the lag bottleneck.");
        System.out.println("The poll loop no longer blocks on slow processing.");
    }
}
