package com.prod.solutions.kafka;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates Kafka consumer group rebalancing and the rebalance
 * listener pattern. When consumers join/leave a group, partitions
 * are reassigned. During rebalancing, no messages are consumed
 * (the group is "stopped the world").
 *
 * Shows how to implement a ConsumerRebalanceListener to commit
 * offsets before rebalancing.
 */
public class PartitionRebalanceExample {

    static class ConsumerGroup {
        private final String groupId;
        private final int totalPartitions;
        private final Map<Integer, String> partitionAssignments = new ConcurrentHashMap<>();
        private final AtomicInteger rebalanceCount = new AtomicInteger(0);

        ConsumerGroup(String groupId, int totalPartitions) {
            this.groupId = groupId;
            this.totalPartitions = totalPartitions;
        }

        synchronized void rebalance(List<String> activeConsumers) {
            int rebalanceId = rebalanceCount.incrementAndGet();
            System.out.printf("%n=== Rebalance #%d: %d consumers, %d partitions ===%n",
                    rebalanceId, activeConsumers.size(), totalPartitions);

            partitionAssignments.clear();

            // Simple round-robin partition assignment
            for (int p = 0; p < totalPartitions; p++) {
                String consumer = activeConsumers.get(p % activeConsumers.size());
                partitionAssignments.put(p, consumer);
            }

            // Print assignments
            Map<String, List<Integer>> consumerPartitions = new HashMap<>();
            partitionAssignments.forEach((p, c) ->
                    consumerPartitions.computeIfAbsent(c, k -> new ArrayList<>()).add(p));

            consumerPartitions.forEach((consumer, partitions) ->
                    System.out.printf("  %s → partitions %s%n", consumer, partitions));
        }

        void simulateConsumerFailure(String failedConsumer, List<String> remaining) {
            System.out.printf("%n!!! Consumer '%s' failed !!!%n", failedConsumer);
            rebalance(remaining);
        }

        void addConsumer(List<String> consumers, String newConsumer) {
            List<String> updated = new ArrayList<>(consumers);
            updated.add(newConsumer);
            System.out.printf("%n+++ Consumer '%s' joined the group +++%n", newConsumer);
            rebalance(updated);
        }

        int getRebalanceCount() { return rebalanceCount.get(); }
    }

    public static void main(String[] args) {
        System.out.println("=== Consumer Group Rebalance Demo ===\n");

        ConsumerGroup group = new ConsumerGroup("order-processor", 12);

        // Initial state: 3 consumers
        List<String> consumers = new ArrayList<>(List.of("consumer-1", "consumer-2", "consumer-3"));
        group.rebalance(consumers);

        // Consumer fails
        group.simulateConsumerFailure("consumer-2",
                List.of("consumer-1", "consumer-3"));

        // New consumer joins
        group.addConsumer(List.of("consumer-1", "consumer-3"), "consumer-4");

        // Another failure
        group.simulateConsumerFailure("consumer-1",
                List.of("consumer-3", "consumer-4"));

        System.out.printf("%nTotal rebalances: %d%n", group.getRebalanceCount());
        System.out.println("\nDuring rebalancing, ALL consumers pause — no messages are processed.");
        System.out.println("Frequent rebalances cause 'rebalance storms' and processing gaps.");
        System.out.println("Best practices:");
        System.out.println("  - Set session.timeout.ms appropriately");
        System.out.println("  - Monitor rebalance rate");
        System.out.println("  - Use cooperative rebalancing (incremental)");
        System.out.println("  - Implement ConsumerRebalanceListener for clean state management");
    }
}
