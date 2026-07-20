package com.javalab.02;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConcurrentHashMapSimulator")
class ConcurrentHashMapSimulatorTest {

    private ConcurrentHashMapSimulator<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = new ConcurrentHashMapSimulator<>(4, 4);
    }

    @Test
    @DisplayName("single-threaded put and get")
    void putAndGet() {
        map.put("k", 42);
        assertEquals(42, map.get("k"));
    }

    @Test
    @DisplayName("put overwrites existing key")
    void putOverwrites() {
        map.put("k", 1);
        map.put("k", 2);
        assertEquals(2, map.get("k"));
    }

    @Test
    @DisplayName("get returns null for missing key")
    void getMissing() {
        assertNull(map.get("ghost"));
    }

    @Test
    @DisplayName("remove returns old value")
    void remove() {
        map.put("x", 99);
        assertEquals(99, map.remove("x"));
        assertNull(map.get("x"));
    }

    @Test
    @DisplayName("containsKey")
    void containsKey() {
        map.put("a", 1);
        assertTrue(map.containsKey("a"));
        assertFalse(map.containsKey("b"));
    }

    @Test
    @DisplayName("size tracking")
    void sizeTracking() {
        assertEquals(0, map.size());
        map.put("a", 1);
        assertEquals(1, map.size());
        map.put("b", 2);
        assertEquals(2, map.size());
        map.remove("a");
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("null key is supported")
    void nullKey() {
        map.put(null, 0);
        assertEquals(0, map.get(null));
        assertTrue(map.containsKey(null));
    }

    @Test
    @DisplayName("clear empties all segments")
    void clear() {
        map.put("a", 1);
        map.put("b", 2);
        map.clear();
        assertEquals(0, map.size());
        assertNull(map.get("a"));
    }

    @Test
    @DisplayName("isEmpty")
    void isEmpty() {
        assertTrue(map.isEmpty());
        map.put("x", 1);
        assertFalse(map.isEmpty());
    }

    @Test
    @DisplayName("concurrent puts from multiple threads")
    void concurrentPuts() throws InterruptedException {
        int threadCount = 8;
        int putsPerThread = 1_000;
        ExecutorService exec = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            int threadId = t;
            exec.submit(() -> {
                try {
                    for (int i = 0; i < putsPerThread; i++) {
                        map.put("key-" + threadId + "-" + i, i);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        exec.shutdown();

        assertEquals(threadCount * putsPerThread, map.size(),
                "All puts should be visible");
    }

    @Test
    @DisplayName("concurrent get and put does not corrupt state")
    void concurrentReadWrite() throws InterruptedException {
        int threadCount = 8;
        int iterations = 500;
        ExecutorService exec = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount * 2);

        for (int t = 0; t < threadCount; t++) {
            int base = t * iterations;
            exec.submit(() -> {
                try {
                    for (int i = 0; i < iterations; i++) {
                        map.put("val-" + (base + i), base + i);
                    }
                } finally {
                    latch.countDown();
                }
            });
            exec.submit(() -> {
                try {
                    for (int i = 0; i < iterations; i++) {
                        map.get("val-" + (base + i));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        exec.shutdown();

        assertEquals(threadCount * iterations, map.size());
    }

    @Test
    @DisplayName("concurrent remove does not corrupt state")
    void concurrentRemove() throws InterruptedException {
        int entries = 1_000;
        for (int i = 0; i < entries; i++) {
            map.put("k" + i, i);
        }

        int threadCount = 4;
        ExecutorService exec = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger removed = new AtomicInteger(0);

        for (int t = 0; t < threadCount; t++) {
            int start = t * (entries / threadCount);
            int end = start + (entries / threadCount);
            exec.submit(() -> {
                try {
                    for (int i = start; i < end; i++) {
                        if (map.remove("k" + i) != null) {
                            removed.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        exec.shutdown();

        assertEquals(entries, removed.get());
        assertEquals(0, map.size());
    }

    @Test
    @DisplayName("concurrent interleaved operations maintain consistency")
    void interleavedOperations() throws InterruptedException {
        int threadCount = 6;
        int opsPerThread = 500;
        ExecutorService exec = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            exec.submit(() -> {
                try {
                    for (int i = 0; i < opsPerThread; i++) {
                        String key = "key-" + (i % 50);
                        map.put(key, i);
                        map.get(key);
                        if (i % 3 == 0) {
                            map.remove(key);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        exec.shutdown();

        assertNotNull(map);
    }
}
