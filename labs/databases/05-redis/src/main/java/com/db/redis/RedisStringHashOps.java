package com.db.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

/**
 * Demonstrates Redis String and Hash data structures with Jedis.
 *
 * Strings: SET, GET, INCR, MSET, MGET, TTL
 * Hashes: HSET, HGET, HGETALL, HINCRBY
 *
 * Requires: Redis running on localhost:6379
 */
public class RedisStringHashOps {

    static final String REDIS_HOST = "localhost";
    static final int REDIS_PORT = 6379;

    public static void main(String[] args) {
        try (JedisPool pool = new JedisPool(REDIS_HOST, REDIS_PORT);
             Jedis jedis = pool.getResource()) {

            jedis.flushDB();
            System.out.println("=== Redis String Operations ===");

            // SET / GET
            jedis.set("user:1:name", "Alice");
            jedis.set("user:1:email", "alice@example.com");
            System.out.println("  Name:  " + jedis.get("user:1:name"));
            System.out.println("  Email: " + jedis.get("user:1:email"));

            // INCR / DECR
            jedis.set("counter:page_views", "0");
            jedis.incr("counter:page_views");
            jedis.incrBy("counter:page_views", 5);
            System.out.println("  Page views: " + jedis.get("counter:page_views"));

            // MSET / MGET
            jedis.mset("product:1", "Mouse", "product:2", "Keyboard", "product:3", "Monitor");
            var products = jedis.mget("product:1", "product:2", "product:3");
            System.out.println("  Products: " + products);

            // TTL / EXPIRE
            jedis.setex("temp:session", 60, "active");
            long ttl = jedis.ttl("temp:session");
            System.out.println("  Session TTL: " + ttl + " seconds");

            System.out.println("\n=== Redis Hash Operations ===");

            // HSET / HMSET
            Map<String, String> userData = new HashMap<>();
            userData.put("name", "Bob");
            userData.put("email", "bob@example.com");
            userData.put("age", "28");
            userData.put("city", "New York");
            jedis.hset("user:2", userData);

            // HGET
            System.out.println("  User 2 name: " + jedis.hget("user:2", "name"));

            // HGETALL
            Map<String, String> all = jedis.hgetAll("user:2");
            System.out.println("  User 2 data: " + all);

            // HINCRBY
            jedis.hincrBy("user:2", "age", 1);
            System.out.println("  User 2 age (after incr): " + jedis.hget("user:2", "age"));

            // HEXISTS
            System.out.println("  Has email? " + jedis.hexists("user:2", "email"));
            System.out.println("  Has phone? " + jedis.hexists("user:2", "phone"));

        } catch (Exception e) {
            System.out.println("Redis required: " + e.getMessage());
        }
    }
}
