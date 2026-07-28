package com.dataengineering.deep.lab04;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class StreamingOptimization {

    public static class SkewTracker {
        private final ConcurrentHashMap<String, Long> counts = new ConcurrentHashMap<>();
        private final long skewThreshold;

        public SkewTracker(long skewThreshold) { this.skewThreshold = skewThreshold; }

        public void record(String key) { counts.merge(key, 1L, Long::sum); }

        public boolean isSkewed(String key) { return counts.getOrDefault(key, 0L) > skewThreshold; }

        public Map<String, Long> getCounts() { return new HashMap<>(counts); }
    }

    public static class SubPartitioner {
        private static final int NUM_SUB_PARTITIONS = 10;

        public int subPartition(String key, int numPartitions) {
            int subIdx = ThreadLocalRandom.current().nextInt(NUM_SUB_PARTITIONS);
            String compositeKey = key + "#" + subIdx;
            return Utils.toPositive(Utils.murmur2(compositeKey.getBytes(StandardCharsets.UTF_8))) % numPartitions;
        }
    }

    public static class SkewAwarePartitioner {
        private final SkewTracker tracker = new SkewTracker(1000);
        private final SubPartitioner subPartitioner = new SubPartitioner();

        public int partition(String topic, Object key, int numPartitions) {
            if (key == null) return ThreadLocalRandom.current().nextInt(numPartitions);
            String k = key.toString();
            tracker.record(k);
            if (tracker.isSkewed(k)) {
                return subPartitioner.subPartition(k, numPartitions);
            }
            return Utils.toPositive(Utils.murmur2(k.getBytes(StandardCharsets.UTF_8))) % numPartitions;
        }
    }

    public static class Utils {
        public static int toPositive(int number) { return number & 0x7FFFFFFF; }

        public static int murmur2(byte[] data) {
            int length = data.length;
            int seed = 0x9747b28c;
            int m = 0x5bd1e995;
            int r = 24;
            int h = seed ^ length;
            int currentIndex = 0;
            while (length >= 4) {
                int k = data[currentIndex++] & 0xFF;
                k |= (data[currentIndex++] & 0xFF) << 8;
                k |= (data[currentIndex++] & 0xFF) << 16;
                k |= (data[currentIndex++] & 0xFF) << 24;
                k *= m;
                k ^= k >>> r;
                k *= m;
                h *= m;
                h ^= k;
                length -= 4;
            }
            switch (length) {
                case 3: h ^= (data[currentIndex + 2] & 0xFF) << 16;
                case 2: h ^= (data[currentIndex + 1] & 0xFF) << 8;
                case 1: h ^= (data[currentIndex] & 0xFF); h *= m;
            }
            h ^= h >>> 13;
            h *= m;
            h ^= h >>> 15;
            return h;
        }
    }

    public record WindowedAggregation(String key, long count, long sum, Instant windowStart, Instant windowEnd) {}

    public static class WindowedProcessor {
        private final Map<String, WindowedAggregation> state = new ConcurrentHashMap<>();
        private final Duration windowSize;
        private final Duration gracePeriod;

        public WindowedProcessor(Duration windowSize, Duration gracePeriod) {
            this.windowSize = windowSize;
            this.gracePeriod = gracePeriod;
        }

        public WindowedAggregation process(String key, long value, Instant eventTime) {
            Instant windowStart = getWindowStart(eventTime);
            if (Instant.now().isAfter(windowStart.plus(windowSize).plus(gracePeriod))) {
                return null; // too late
            }
            String stateKey = key + "@" + windowStart.toEpochMilli();
            var current = state.get(stateKey);
            long newCount = current == null ? 1 : current.count() + 1;
            long newSum = current == null ? value : current.sum() + value;
            var result = new WindowedAggregation(key, newCount, newSum, windowStart, windowStart.plus(windowSize));
            state.put(stateKey, result);
            return result;
        }

        private Instant getWindowStart(Instant time) {
            long millis = time.toEpochMilli();
            long windowMillis = windowSize.toMillis();
            return Instant.ofEpochMilli(millis - (millis % windowMillis));
        }
    }

    public static void main(String[] args) {
        var partitioner = new SkewAwarePartitioner();
        var keys = List.of("user_a", "user_b", "user_c");
        System.out.println("Partition assignments for 10 partitions:");
        for (int i = 0; i < 100; i++) {
            String key = i < 80 ? "hot_key" : keys.get(i % 3);
            int p = partitioner.partition("events", key, 10);
            if (i < 20) System.out.println("  key=" + key + " -> partition=" + p);
        }
        System.out.println("Skew counts: " + partitioner.tracker.getCounts());

        var processor = new WindowedProcessor(Duration.ofMinutes(5), Duration.ofMinutes(1));
        var now = Instant.now();
        var result = processor.process("user_1", 42, now);
        System.out.println("Windowed aggregation: " + result);
    }
}
