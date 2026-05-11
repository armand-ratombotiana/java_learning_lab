package com.learning.lab.module24;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExampleTest {

    @Test
    void testCacheServiceCreation() {
        CacheService cache = new CacheService();
        assertNotNull(cache);
    }

    @Test
    void testCachePut() {
        CacheService cache = new CacheService();
        cache.put("key1", "value1");
        assertTrue(cache.contains("key1"));
    }

    @Test
    void testCacheGet() {
        CacheService cache = new CacheService();
        cache.put("key1", "value1");
        assertEquals("value1", cache.get("key1"));
    }

    @Test
    void testCacheDelete() {
        CacheService cache = new CacheService();
        cache.put("key1", "value1");
        cache.delete("key1");
        assertFalse(cache.contains("key1"));
    }

    @Test
    void testCacheExpiration() {
        CacheService cache = new CacheService();
        cache.put("key1", "value1", 1);
        assertTrue(cache.isExpired("key1"));
    }

    @Test
    void testCacheTTL() {
        CacheService cache = new CacheService();
        cache.put("key1", "value1", 60);
        assertTrue(cache.getTTL("key1") > 0);
    }

    @Test
    void testRedisStringOperations() {
        RedisStringService strings = new RedisStringService();
        strings.set("name", "John");
        assertEquals("John", strings.get("name"));
    }

    @Test
    void testRedisListOperations() {
        RedisListService list = new RedisListService();
        list.push("queue", "item1");
        list.push("queue", "item2");
        assertEquals(2, list.size("queue"));
    }

    @Test
    void testRedisHashOperations() {
        RedisHashService hash = new RedisHashService();
        hash.hset("user:1", "name", "John");
        assertEquals("John", hash.hget("user:1", "name"));
    }

    @Test
    void testRedisSetOperations() {
        RedisSetService set = new RedisSetService();
        set.sadd("tags", "java", "redis", "cache");
        assertEquals(3, set.scard("tags"));
    }

    @Test
    void testCacheEvictionPolicy() {
        CacheService cache = new CacheService("LRU");
        assertEquals("LRU", cache.getEvictionPolicy());
    }

    @Test
    void testRedisConnection() {
        RedisConnection conn = new RedisConnection();
        assertTrue(conn.isConnected());
    }
}

class CacheService {
    private java.util.Map<String, CacheEntry> cache = new java.util.LinkedHashMap<>();
    private String evictionPolicy = "LRU";

    public CacheService() {}

    public CacheService(String policy) {
        this.evictionPolicy = policy;
    }

    public void put(String key, String value) {
        cache.put(key, new CacheEntry(value, Long.MAX_VALUE));
    }

    public void put(String key, String value, int ttlSeconds) {
        cache.put(key, new CacheEntry(value, System.currentTimeMillis() + ttlSeconds * 1000L));
    }

    public String get(String key) {
        CacheEntry entry = cache.get(key);
        return entry != null ? entry.value : null;
    }

    public void delete(String key) {
        cache.remove(key);
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }

    public boolean isExpired(String key) {
        CacheEntry entry = cache.get(key);
        return entry != null && System.currentTimeMillis() > entry.expiry;
    }

    public long getTTL(String key) {
        CacheEntry entry = cache.get(key);
        return entry != null ? Math.max(0, entry.expiry - System.currentTimeMillis()) / 1000 : 0;
    }

    public String getEvictionPolicy() {
        return evictionPolicy;
    }

    private static class CacheEntry {
        String value;
        long expiry;

        CacheEntry(String value, long expiry) {
            this.value = value;
            this.expiry = expiry;
        }
    }
}

class RedisStringService {
    private java.util.Map<String, String> store = new java.util.HashMap<>();

    public void set(String key, String value) {
        store.put(key, value);
    }

    public String get(String key) {
        return store.get(key);
    }
}

class RedisListService {
    private java.util.Map<String, java.util.List<String>> lists = new java.util.HashMap<>();

    public void push(String key, String value) {
        lists.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(value);
    }

    public int size(String key) {
        return lists.getOrDefault(key, java.util.Collections.emptyList()).size();
    }
}

class RedisHashService {
    private java.util.Map<String, java.util.Map<String, String>> hashes = new java.util.HashMap<>();

    public void hset(String key, String field, String value) {
        hashes.computeIfAbsent(key, k -> new java.util.HashMap<>()).put(field, value);
    }

    public String hget(String key, String field) {
        return hashes.getOrDefault(key, java.util.Collections.emptyMap()).get(field);
    }
}

class RedisSetService {
    private java.util.Map<String, java.util.Set<String>> sets = new java.util.HashMap<>();

    public void sadd(String key, String... members) {
        java.util.Set<String> set = sets.computeIfAbsent(key, k -> new java.util.HashSet<>());
        for (String member : members) {
            set.add(member);
        }
    }

    public int scard(String key) {
        return sets.getOrDefault(key, java.util.Collections.emptySet()).size();
    }
}

class RedisConnection {
    public boolean isConnected() {
        return true;
    }
}