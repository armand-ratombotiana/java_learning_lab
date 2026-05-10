package com.learning.lab.module55;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 55: Kafka Lab ===\n");

        System.out.println("1. Kafka Streams:");
        System.out.println("   - KafkaStreams topology");
        System.out.println("   - Processor API: low-level");
        System.out.println("   - DSL: high-level operations");
        System.out.println("   - Exactly-once semantics (EOS)");

        System.out.println("\n2. DSL Operations:");
        System.out.println("   - map, filter, flatMap");
        System.out.println("   - join, windowed joins");
        System.out.println("   - groupBy, aggregate");
        System.out.println("   - Session windows, tumbling windows");

        System.out.println("\n3. State Stores:");
        System.out.println("   - RocksDB state stores");
        System.out.println("   - Global stores");
        System.out.println("   - Interactive queries");

        System.out.println("\n4. ksqlDB:");
        System.out.println("   - SQL-like stream processing");
        System.out.println("   - CREATE STREAM, CREATE TABLE");
        System.out.println("   - Push queries, pull queries");

        System.out.println("\n5. Exactly-Once Processing:");
        System.out.println("   - enable.idempotence = true");
        System.out.println("   - transactional.id = settings");
        System.out.println("   - Processing guarantees");

        System.out.println("\n6. Performance:");
        System.out.println("   - Partition strategy");
        System.out.println("   - Consumer group balancing");
        System.out.println("   - Rebalance listeners");

        System.out.println("\n7. Testing:");
        System.out.println("   - TopologyTestDriver");
        System.out.println("   - Integration tests");

        System.out.println("\n=== Kafka Lab Complete ===");
    }
}