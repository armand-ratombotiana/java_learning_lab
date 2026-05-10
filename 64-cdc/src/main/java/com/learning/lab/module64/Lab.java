package com.learning.lab.module64;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 64: CDC Lab ===\n");

        System.out.println("1. Change Data Capture:");
        System.out.println("   - Capture database changes in real-time");
        System.out.println("   - CDC from binlog (MySQL), WAL (PostgreSQL)");
        System.out.println("   - Debezium for CDC implementation");

        System.out.println("\n2. Debezium Architecture:");
        System.out.println("   - Connectors: MySQL, PostgreSQL, MongoDB");
        System.out.println("   - Source connector: capture changes");
        System.out.println("   - Message format: Envelope, Source, Payload");
        System.out.println("   - Schema history topic");

        System.out.println("\n3. Kafka Connect:");
        System.out.println("   - Distributed mode for scalability");
        System.out.println("   - Single message transformation (SMT)");
        System.out.println("   - Configuration-driven connectors");
        System.out.println("   - Offset management");

        System.out.println("\n4. Event Structure:");
        System.out.println("   - Operation: Create, Update, Delete, Read");
        System.out.println("   - Before/After state");
        System.out.println("   - Transaction metadata");
        System.out.println("   - Timestamp and sequence");

        System.out.println("\n5. Data Integration:");
        System.out.println("   - Debezium to Elasticsearch");
        System.out.println("   - Debezium to data warehouse");
        System.out.println("   - CDC to search index update");
        System.out.println("   - Cache invalidation via CDC");

        System.out.println("\n6. Testing CDC:");
        System.out.println("   - Embedded engine for testing");
        System.out.println("   - Testcontainers with Debezium");
        System.out.println("   - Verification of capture events");

        System.out.println("\n7. Best Practices:");
        System.out.println("   - Schema evolution handling");
        System.out.println("   - Idempotent consumers");
        System.out.println("   - Snapshot vs streaming");
        System.out.println("   - Monitoring lag metrics");

        System.out.println("\n=== CDC Lab Complete ===");
    }
}