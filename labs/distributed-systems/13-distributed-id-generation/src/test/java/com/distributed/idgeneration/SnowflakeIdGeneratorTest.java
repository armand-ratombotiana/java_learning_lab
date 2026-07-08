package com.distributed.idgeneration;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class SnowflakeIdGeneratorTest {

    @Test
    void testGenerateReturnsUniqueIds() {
        SnowflakeIdGenerator gen = new SnowflakeIdGenerator(1);
        Set<Long> ids = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            assertTrue(ids.add(gen.generate()));
        }
    }

    @Test
    void testConcurrentGeneration() throws InterruptedException {
        SnowflakeIdGenerator gen = new SnowflakeIdGenerator(1);
        int threads = 10;
        int idsPerThread = 1000;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        Set<Long> allIds = ConcurrentHashMap.newKeySet();
        CountDownLatch latch = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            exec.submit(() -> {
                for (int i = 0; i < idsPerThread; i++) {
                    allIds.add(gen.generate());
                }
                latch.countDown();
            });
        }
        latch.await(10, TimeUnit.SECONDS);
        assertEquals(threads * idsPerThread, allIds.size());
        exec.shutdown();
    }

    @Test
    void testExtractTimestamp() {
        SnowflakeIdGenerator gen = new SnowflakeIdGenerator(1);
        long id = gen.generate();
        long ts = gen.extractTimestamp(id);
        long now = System.currentTimeMillis();
        assertTrue(ts <= now && ts > now - 1000);
    }

    @Test
    void testMonotonicOrder() {
        SnowflakeIdGenerator gen = new SnowflakeIdGenerator(1);
        long prev = 0;
        for (int i = 0; i < 10000; i++) {
            long id = gen.generate();
            assertTrue(id > prev);
            prev = id;
        }
    }
}
