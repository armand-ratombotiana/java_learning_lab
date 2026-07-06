package com.javaacademy.lab34.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class AsyncLoggerExample {

    private static final Logger log = LoggerFactory.getLogger(AsyncLoggerExample.class);
    private final AtomicLong eventCount = new AtomicLong(0);
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public void logEventsAsync(int count) {
        for (int i = 0; i < count; i++) {
            final int id = i;
            executor.submit(() -> {
                log.info("Async log event #{} at time {}", id, System.currentTimeMillis());
                eventCount.incrementAndGet();
            });
        }
    }

    public void logWithBackpressure(int count) {
        for (int i = 0; i < count; i++) {
            if (eventCount.get() % 1000 == 0 && eventCount.get() > 0) {
                log.warn("High throughput warning - processed {} events", eventCount.get());
            }
            log.debug("Event {} processed", i);
            eventCount.incrementAndGet();
        }
    }

    public long getEventCount() {
        return eventCount.get();
    }

    public void simulateBurst(int burstSize, int bursts) {
        for (int b = 0; b < bursts; b++) {
            long start = System.nanoTime();
            for (int i = 0; i < burstSize; i++) {
                log.info("Burst {}/{} event {}", b + 1, bursts, i);
            }
            long elapsed = System.nanoTime() - start;
            log.info("Burst {} completed in {}ms", b + 1, elapsed / 1_000_000);
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
