package com.learning.lab.module24;

import redis.clients.jedis.*;

import java.util.*;

public class Lab {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;

    public static void main(String[] args) {
        System.out.println("=== Module 24: Redis Caching ===");

        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            stringDemo(jedis);
            listDemo(jedis);
            setDemo(jedis);
            hashDemo(jedis);
            sortedSetDemo(jedis);
            pubSubDemo(jedis);
            transactionDemo(jedis);
        }
    }

    static void stringDemo(Jedis jedis) {
        System.out.println("\n--- Redis String Operations ---");
        jedis.set("user:1", "John Doe");
        jedis.setex("temp:token", 3600, "abc123");
        System.out.println("Get: " + jedis.get("user:1"));
        jedis.incr("counter");
        System.out.println("Counter: " + jedis.get("counter"));
    }

    static void listDemo(Jedis jedis) {
        System.out.println("\n--- Redis List Operations ---");
        jedis.del("tasks");
        jedis.lpush("tasks", "task3", "task2", "task1");
        System.out.println("First: " + jedis.lindex("tasks", 0));
        System.out.println("Range: " + jedis.lrange("tasks", 0, -1));
    }

    static void setDemo(Jedis jedis) {
        System.out.println("\n--- Redis Set Operations ---");
        jedis.sadd("tags", "java", "redis", "cache", "java");
        System.out.println("Members: " + jedis.smembers("tags"));
        System.out.println("Count: " + jedis.scard("tags"));
        System.out.println("Exists: " + jedis.sismember("tags", "redis"));
    }

    static void hashDemo(Jedis jedis) {
        System.out.println("\n--- Redis Hash Operations ---");
        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", "Alice");
        userMap.put("email", "alice@example.com");
        userMap.put("age", "25");
        jedis.hmset("user:alice", userMap);
        System.out.println("Name: " + jedis.hget("user:alice", "name"));
        System.out.println("All: " + jedis.hgetAll("user:alice"));
    }

    static void sortedSetDemo(Jedis jedis) {
        System.out.println("\n--- Redis Sorted Set ---");
        jedis.zadd("leaderboard", 100, "player1");
        jedis.zadd("leaderboard", 200, "player2");
        jedis.zadd("leaderboard", 150, "player3");
        System.out.println("Top: " + jedis.zrevrange("leaderboard", 0, 2));
        System.out.println("Rank player2: " + jedis.zrank("leaderboard", "player2"));
    }

    static void pubSubDemo(Jedis jedis) {
        System.out.println("\n--- Redis Pub/Sub ---");
        System.out.println("Publisher: jedis.publish('channel', 'message')");
        System.out.println("Subscriber: jedis.subscribe(new JedisPubSub(), 'channel')");
    }

    static void transactionDemo(Jedis jedis) {
        System.out.println("\n--- Redis Transactions ---");
        Transaction tx = jedis.multi();
        tx.set("key1", "value1");
        tx.set("key2", "value2");
        tx.exec();
        System.out.println("Transaction executed");
    }
}