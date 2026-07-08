package com.distributed.distributedcaching;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class WriteBehindCacheTest {

    @Test
    void testWriteAndRead() {
        AtomicInteger dbWrites = new AtomicInteger(0);
        WriteBehindCache<String, String> cache = new WriteBehindCache<>(
            (k, v) -> dbWrites.incrementAndGet(), 10, 500);
        cache.put("k1", "v1", Duration.ofMinutes(5));
        assertEquals("v1", cache.get("k1").get());
    }

    @Test
    void testBatchFlush() throws InterruptedException {
        ConcurrentHashMap<String, String> db = new ConcurrentHashMap<>();
        WriteBehindCache<String, String> cache = new WriteBehindCache<>(
            (k, v) -> db.put(k, v), 5, 100);
        cache.start();
        for (int i = 0; i < 10; i++) {
            cache.put("k" + i, "v" + i, Duration.ofMinutes(5));
        }
        Thread.sleep(300);
        assertEquals(10, db.size());
        cache.stop();
    }

    @Test
    void testDelete() {
        WriteBehindCache<String, String> cache = new WriteBehindCache<>((k, v) -> {}, 10, 1000);
        cache.put("key", "val", Duration.ofMinutes(5));
        assertTrue(cache.exists("key"));
        assertTrue(cache.delete("key"));
        assertFalse(cache.exists("key"));
    }
}
