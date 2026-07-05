package com.db.multimodel;

/**
 * Demonstrates polyglot persistence architecture.
 *
 * Polyglot persistence means using different database technologies
 * for different data storage needs within the same application.
 *
 * Example architecture:
 *   - PostgreSQL:  transactional/relational data (orders, accounts)
 *   - Redis:       caching, session state, rate limiting
 *   - MongoDB:     flexible document storage (catalog, analytics events)
 *   - Elasticsearch: full-text search
 */
public class PolyglotPersistence {

    record DataStore(String name, String type, String useCase, String reason) {}

    static DataStore[] architecture() {
        return new DataStore[] {
            new DataStore("PostgreSQL", "Relational (SQL)",
                "Orders, Customers, Payments",
                "ACID compliance, relations, joins, reporting"),
            new DataStore("Redis", "Key-Value / In-Memory",
                "Session cache, product cache, rate limits",
                "Sub-millisecond latency, TTL expiry, pub/sub"),
            new DataStore("MongoDB", "Document (NoSQL)",
                "Product catalog, user reviews, analytics events",
                "Flexible schema, nested documents, aggregation pipeline"),
            new DataStore("Elasticsearch", "Search Engine",
                "Full-text product search, log aggregation",
                "Inverted index, fuzzy search, faceted search")
        };
    }

    /**
     * Shows how different datasets map to different stores.
     */
    static void showDataMapping() {
        System.out.println("=== Data Mapping by Store ===\n");

        System.out.println("PostgreSQL (relational):");
        System.out.println("  - customers:      id, name, email, created_at");
        System.out.println("  - orders:         id, customer_id, total, status, created_at");
        System.out.println("  - payments:       id, order_id, amount, method, paid_at");
        System.out.println("  - inventory:      product_id, warehouse_id, quantity");

        System.out.println("\nRedis (cache layer):");
        System.out.println("  - session:{id} → user_id, expiry, last_access");
        System.out.println("  - product:{id} → serialized product JSON (TTL)");
        System.out.println("  - rate_limit:{ip} → counter, TTL");
        System.out.println("  - leaderboard    → sorted set of scores");

        System.out.println("\nMongoDB (documents):");
        System.out.println("  - products: { _id, name, variants:[], specs:{}, reviews:[] }");
        System.out.println("  - events:   { _id, type, userId, payload, timestamp }");
        System.out.println("  - sessions: { _id, userId, device, preferences }");
    }

    /**
     * Shows the query flow across stores for a typical request.
     */
    static void showQueryFlow() {
        System.out.println("\n=== Typical Request Flow ===");
        System.out.println("""
            GET /products/search?q=wireless+mouse
            
            1. Check Redis cache for "search:wireless mouse"
               → CACHE HIT: return cached results
               → CACHE MISS: continue
            
            2. Query Elasticsearch for full-text search
               → Get product IDs ranked by relevance
            
            3. Fetch product details from MongoDB
               → Find products by IDs, get full documents
            
            4. Look up inventory/pricing from PostgreSQL
               → Get stock levels, current prices
            
            5. Store results in Redis cache (TTL: 5 min)
               → Cache key: "search:wireless mouse"
            
            6. Log search event to MongoDB analytics collection
               → { event: "search", query: "wireless mouse", results: 42 }
            """);
    }

    public static void main(String[] args) {
        System.out.println("=== Polyglot Persistence Architecture ===\n");

        System.out.printf("%-15s %-22s %-35s %s%n", "Store", "Type", "Use Case", "Reason");
        System.out.println("=".repeat(110));
        for (DataStore ds : architecture()) {
            System.out.printf("%-15s %-22s %-35s %s%n",
                ds.name(), ds.type(), ds.useCase(), ds.reason());
        }

        showDataMapping();
        showQueryFlow();

        System.out.println("\n=== Benefits ===");
        System.out.println("  - Use the right tool for each data problem");
        System.out.println("  - Optimize for different access patterns");
        System.out.println("  - Scale each store independently");
        System.out.println("\n=== Challenges ===");
        System.out.println("  - Operational complexity (multiple DBs to run)");
        System.out.println("  - Data consistency across stores");
        System.out.println("  - Transaction coordination (no distributed TX)");
    }
}
