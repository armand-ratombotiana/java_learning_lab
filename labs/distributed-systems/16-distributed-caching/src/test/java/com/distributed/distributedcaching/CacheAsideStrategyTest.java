package com.distributed.distributedcaching;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

class CacheAsideStrategyTest {

    @Test
    void testCacheHitAndMiss() {
        CacheAsideStrategy<String, String> cache = new CacheAsideStrategy<>();
        assertTrue(cache.get("key1").isEmpty());
        cache.put("key1", "value1", Duration.ofSeconds(60));
        assertEquals("value1", cache.get("key1").get());
    }

    @Test
    void testTTLExpiry() throws InterruptedException {
        CacheAsideStrategy<String, String> cache = new CacheAsideStrategy<>();
        cache.put("temp", "value", Duration.ofMillis(50));
        Thread.sleep(100);
        assertTrue(cache.get("temp").isEmpty());
    }

    @Test
    void testDelete() {
        CacheAsideStrategy<String, String> cache = new CacheAsideStrategy<>();
        cache.put("key", "val", Duration.ofSeconds(60));
        assertTrue(cache.delete("key"));
        assertFalse(cache.exists("key"));
    }

    @Test
    void testHitRate() {
        CacheAsideStrategy<String, String> cache = new CacheAsideStrategy<>();
        cache.get("miss1");
        cache.get("miss2");
        cache.put("hit", "val", Duration.ofSeconds(60));
        cache.get("hit");
        assertEquals(0.333, cache.getHitRate(), 0.01);
    }
}
