package com.db.multimodel;

/**
 * Demonstrates data synchronization strategies across multi-model databases.
 *
 * When using multiple databases, keeping data consistent is a key challenge.
 * Common strategies include:
 *   - Dual-writes (write to both stores)
 *   - Transactional outbox (write to primary, async propagate)
 *   - Change Data Capture (CDC) — Debezium, Kafka Connect
 *   - Periodic batch sync (read-replica style)
 */
public class DataSynchronizer {

    // Simulates a product update propagated across three stores.
    static void dualWriteSync(long productId, String name, double price) {
        System.out.println("  [PostgreSQL] UPDATE products SET name='%s', price=%.2f WHERE id=%d"
            .formatted(name, price, productId));
        System.out.println("  [MongoDB]    db.products.updateOne({_id:%d}, {$set:{name:'%s',price:%.2f}})"
            .formatted(productId, name, price));
        System.out.println("  [Redis]      DEL product:%d (cache invalidation)".formatted(productId));
    }

    // Simulates a transactional outbox pattern.
    static void transactionalOutbox() {
        System.out.println("\n--- Transactional Outbox Pattern ---");
        System.out.println("""
            1. BEGIN transaction
            2. UPDATE products SET ... WHERE id = 42
            3. INSERT INTO outbox (aggregate_id, event_type, payload)
               VALUES (42, 'product_updated', '{"name":"...", "price":...}')
            4. COMMIT transaction
            
            --- Async relay process (separate service) ---
            5. SELECT * FROM outbox WHERE processed = false
            6. Publish event to Kafka/RabbitMQ
            7. Consumer updates MongoDB, invalidates Redis
            8. UPDATE outbox SET processed = true WHERE id = ...
            """);
    }

    static void changeDataCapture() {
        System.out.println("\n--- Change Data Capture (CDC) ---");
        System.out.println("""
            PostgreSQL WAL → Debezium → Kafka → Consumers
            
            Benefits:
              - Zero impact on application code
              - Guaranteed ordered delivery
              - Exactly-once semantics with Kafka
              - Realtime sync across all stores
            
            Downside:
              - Additional infrastructure (Kafka, Debezium, connectors)
              - Propagation delay (milliseconds to seconds)
            """);
    }

    static void consistencyPatterns() {
        System.out.println("\n=== Consistency Patterns ===");
        System.out.println("""
            +------------------+-------------------+---------------------------+
            | Pattern          | Consistency       | Complexity                |
            +------------------+-------------------+---------------------------+
            | Dual-writes      | Eventual          | Low (app-level)           |
            | Outbox pattern   | Strong (primary)  | Medium (needs relay)      |
            | CDC (Debezium)   | Strong (primary)  | High (infrastructure)     |
            | Batch sync every | Eventual (minutes)| Low (cron job)            |
            +------------------+-------------------+---------------------------+
            
            Recommendation:
            - Start with dual-writes + cache invalidation
            - Add outbox pattern for critical data
            - Adopt CDC when you need scale and reliability
            """);
    }

    public static void main(String[] args) {
        System.out.println("=== Multi-Model Data Synchronization ===\n");

        System.out.println("Scenario: Update product #42 across 3 stores\n");

        System.out.println("1. Dual-Write Sync:");
        dualWriteSync(42, "Wireless Mouse v2", 34.99);

        transactionalOutbox();
        changeDataCapture();
        consistencyPatterns();

        System.out.println("\nKey takeaways:");
        System.out.println("  - Multi-model = multiple sources of truth");
        System.out.println("  - Designate ONE store as the primary/authoritative source");
        System.out.println("  - Cache and search indexes are derived/secondary");
        System.out.println("  - Use idempotent operations for safe retries");
    }
}
