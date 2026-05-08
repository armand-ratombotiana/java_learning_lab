package com.learning.databases;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Redis Concepts ===\n");

        demonstrateDataStructures();
        demonstrateCaching();
        demonstratePubSub();
        demonstratePersistence();
        demonstrateHighAvailability();
        demonstrateUseCases();
    }

    private static void demonstrateDataStructures() {
        System.out.println("--- Redis Data Structures ---");
        System.out.println("String    -> SET key value, GET key (caching, counters)");
        System.out.println("  INCR page:views:1 -> atomic increment");
        System.out.println();
        System.out.println("List      -> LPUSH, RPUSH, LPOP, BRPOP (queues, stacks)");
        System.out.println("  BRPOP queue:orders 0  -> blocking deque with timeout");
        System.out.println();
        System.out.println("Set       -> SADD, SMEMBERS, SINTER (tags, uniqueness)");
        System.out.println("  SINTER user:1:tags user:2:tags -> common tags");
        System.out.println();
        System.out.println("Sorted Set-> ZADD, ZRANGEBYSCORE (leaderboards, rate limiters)");
        System.out.println("  ZADD leaderboard 1000 \"player42\"");
        System.out.println();
        System.out.println("Hash      -> HSET user:1 name \"Alice\" age \"30\"");
        System.out.println("  HGETALL user:1");
        System.out.println();
        System.out.println("Streams (5.0+) -> Consumer groups, message replay");
        System.out.println("Bitmaps, HyperLogLog, Geospatial indexes");
    }

    private static void demonstrateCaching() {
        System.out.println("\n--- Caching Patterns ---");
        System.out.println("Cache-Aside (Lazy Loading):");
        System.out.println("  1. Check cache: GET product:101");
        System.out.println("  2. If miss: query DB, SET product:101 <result>");
        System.out.println("  3. Return result");
        System.out.println("  4. TTL: EXPIRE product:101 3600");
        System.out.println();
        System.out.println("Write-Through:");
        System.out.println("  Write to cache first, then DB (always consistent)");
        System.out.println();
        System.out.println("Cache invalidation:");
        System.out.println("  DEL product:101 (on update), or passive TTL expiry");
        System.out.println("  SET product:101 <new-value> EX 3600 (refresh on write)");
    }

    private static void demonstratePubSub() {
        System.out.println("\n--- Publish / Subscribe ---");
        System.out.println("SUBSCRIBE channel:notifications");
        System.out.println("PUBLISH channel:notifications \"User logged in\"");
        System.out.println();
        System.out.println("Pattern subscription: PSUBSCRIBE order:*");
        System.out.println("  Matches: order:created, order:shipped, order:cancelled");
        System.out.println();
        System.out.println("Limitations:");
        System.out.println("  - No message persistence (fire-and-forget)");
        System.out.println("  - No replay (use Streams for persistence)");
        System.out.println("  - Subscribers only get messages while connected");
    }

    private static void demonstratePersistence() {
        System.out.println("\n--- Persistence Options ---");
        System.out.println("RDB (Redis Database):");
        System.out.println("  Snapshot to disk at intervals (save 900 1, save 300 10)");
        System.out.println("  Pros: compact, fast restore, good for backups");
        System.out.println("  Cons: may lose last few minutes of data");
        System.out.println();
        System.out.println("AOF (Append-Only File):");
        System.out.println("  Every write appended: always, everysec, no");
        System.out.println("  Pros: durable (everysec loses max 1 sec)");
        System.out.println("  Cons: larger files, slower restart");
        System.out.println();
        System.out.println("Hybrid (4.0+): RDB base + AOF incremental (fast + durable)");
    }

    private static void demonstrateHighAvailability() {
        System.out.println("\n--- High Availability ---");
        System.out.println("Redis Sentinel:");
        System.out.println("  Monitoring + automatic failover");
        System.out.println("  Quorum: sentinel monitor mymaster 127.0.0.1 6379 2");
        System.out.println("  Client uses sentinel to discover current master");
        System.out.println();
        System.out.println("Redis Cluster:");
        System.out.println("  Automatic sharding across 16384 hash slots");
        System.out.println("  Each node responsible for a subset of slots");
        System.out.println("  Replicas per master for fault tolerance");
        System.out.println("  Smart clients route directly to correct node");
    }

    private static void demonstrateUseCases() {
        System.out.println("\n--- Common Use Cases ---");
        System.out.println("1. Session store           -> EXPIRE-based auto-cleanup");
        System.out.println("2. Rate limiter            -> INCR + EXPIRE (sliding window)");
        System.out.println("3. Distributed locks       -> SETNX + Lua (Redlock)");
        System.out.println("4. Leaderboards            -> ZADD / ZRANK (sorted sets)");
        System.out.println("5. Message queues          -> BRPOP / Streams");
        System.out.println("6. Real-time analytics     -> HyperLogLog (UV counting)");
        System.out.println("7. Geofencing             -> GEOADD / GEORADIUS");
    }
}
