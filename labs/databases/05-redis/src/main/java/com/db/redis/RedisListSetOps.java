package com.db.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Demonstrates Redis List (LPUSH/RPUSH/LPOP/RPUSH/LRANGE) and
 * Set (SADD/SMEMBERS/SINTER/SUNION) operations with Jedis.
 *
 * Lists: ordered, useful for queues and recent-items tracking.
 * Sets: unordered, useful for tagging, deduplication, and set operations.
 */
public class RedisListSetOps {

    static final String REDIS_HOST = "localhost";
    static final int REDIS_PORT = 6379;

    public static void main(String[] args) {
        try (JedisPool pool = new JedisPool(REDIS_HOST, REDIS_PORT);
             Jedis jedis = pool.getResource()) {

            jedis.flushDB();

            System.out.println("=== Redis List Operations ===");

            // RPUSH — add to tail (queue behavior)
            jedis.rpush("queue:notifications", "email:1", "sms:2", "push:3");
            jedis.rpush("queue:notifications", "email:4");

            // LRANGE
            var all = jedis.lrange("queue:notifications", 0, -1);
            System.out.println("  Queue contents: " + all);

            // LPOP — pop from head
            String first = jedis.lpop("queue:notifications");
            System.out.println("  Popped: " + first);
            System.out.println("  Remaining: " + jedis.lrange("queue:notifications", 0, -1));

            // LLEN
            System.out.println("  Queue length: " + jedis.llen("queue:notifications"));

            // LPUSH / RPOP — stack behavior (LIFO)
            jedis.lpush("stack:actions", "action:1", "action:2", "action:3");
            String last = jedis.rpop("stack:actions");
            System.out.println("  Stack pop (RPop): " + last);

            System.out.println("\n=== Redis Set Operations ===");

            // SADD — add tags
            jedis.sadd("tag:java", "spring", "jpa", "jdbc", "hibernate");
            jedis.sadd("tag:web", "spring", "react", "rest", "jpa");

            // SMEMBERS
            System.out.println("  Java tag members: " + jedis.smembers("tag:java"));

            // SINTER — common members
            var common = jedis.sinter("tag:java", "tag:web");
            System.out.println("  Common (SINTER): " + common);

            // SUNION — all members
            var union = jedis.sunion("tag:java", "tag:web");
            System.out.println("  Union (SUNION): " + union);

            // SDIFF — in java but not web
            var diff = jedis.sdiff("tag:java", "tag:web");
            System.out.println("  Java only (SDIFF): " + diff);

            // SCARD — cardinality
            System.out.println("  Java tag count: " + jedis.scard("tag:java"));

            // SISMEMBER
            System.out.println("  Is 'spring' in java? " + jedis.sismember("tag:java", "spring"));
            System.out.println("  Is 'react' in java? " + jedis.sismember("tag:java", "react"));

        } catch (Exception e) {
            System.out.println("Redis required: " + e.getMessage());
        }
    }
}
