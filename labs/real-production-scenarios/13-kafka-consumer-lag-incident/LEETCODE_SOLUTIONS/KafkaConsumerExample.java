package com.prod.solutions.kafka;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simulates a Kafka consumer that monitors and reports consumer lag.
 * In production, this would use the KafkaConsumer API with
 * kafka-consumer-groups to track lag metrics.
 *
 * BUG: The consumer processes messages too slowly, causing lag to grow.
 */
public class KafkaConsumerExample {

    static class ConsumerMetrics {
        final String groupId;
        final String topic;
        final AtomicLong messagesConsumed = new AtomicLong(0);
        final AtomicLong totalLag = new AtomicLong(0);
        final AtomicLong maxLag = new AtomicLong(0);

        ConsumerMetrics(String groupId, String topic) {
            this.groupId = groupId;
            this.topic = topic;
        }

        void reportLag(int partition, long lag) {
            totalLag.addAndGet(lag);
            if (lag > maxLag.get()) maxLag.set(lag);
        }

        void print() {
            System.out.printf("""
                    Group:     %s
                    Topic:     %s
                    Consumed:  %d
                    Total Lag: %d
                    Max Lag:   %d
                    """, groupId, topic, messagesConsumed.get(), totalLag.get(), maxLag.get());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Kafka Consumer Lag Monitoring ===\n");

        ConsumerMetrics metrics = new ConsumerMetrics("order-processor", "orders");

        System.out.println("Simulating Kafka consumer with increasing lag...\n");

        // Simulate producer (adds messages faster than consumer can process)
        ScheduledExecutorService producer = Executors.newSingleThreadScheduledExecutor();
        AtomicLong producedCount = new AtomicLong(0);

        // Producer adds 10 msg/s
        producer.scheduleAtFixedRate(() -> {
            producedCount.incrementAndGet();
        }, 0, 100, TimeUnit.MILLISECONDS);

        // Simulate consumer processing
        long consumerStart = System.currentTimeMillis();
        while (System.currentTimeMillis() - consumerStart < 5000) {
            long produced = producedCount.get();
            long consumed = metrics.messagesConsumed.get();

            // BUG: Consumer processes only 5 msg/s (cannot keep up)
            metrics.messagesConsumed.addAndGet(2);
            metrics.messagesConsumed.addAndGet(-1); // net +1 every 500ms
            Thread.sleep(500);

            // Simulate partition lag
            long lag = produced - consumed;
            for (int p = 0; p < 3; p++) {
                metrics.reportLag(p, lag / 3);
            }

            System.out.printf("Produced: %d, Consumed: %d, Lag: %d%n",
                    produced, metrics.messagesConsumed.get(), lag);

            if (lag > 20) {
                System.out.println("  >>> LAG WARNING: Consumer falling behind! <<<");
            }
        }

        producer.shutdown();

        System.out.println("\n--- Final Metrics ---");
        metrics.print();

        System.out.println("\nRoot cause: Consumer processes messages too slowly (5 msg/s vs 10 msg/s produced).");
        System.out.println("Fixes: Increase consumer threads, optimize processing, or scale partitions.");
    }
}
