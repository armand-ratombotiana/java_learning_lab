package com.databases.deep.lab07;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * TimeSeriesDbLab — implements delta-of-delta compression (Gorilla),
 * time-based partitioning, and downsampling logic.
 */
public class TimeSeriesDbLab {

    // --- Gorilla-style compression ---
    static class CompressedBlock {
        final List<Integer> deltas = new ArrayList<>();
        int baseTimestamp;
        int prevDelta = 0;
        int prevTimestamp = 0;

        void add(long ts) {
            int t = (int) (ts / 1000);
            if (deltas.isEmpty()) {
                baseTimestamp = t;
                prevTimestamp = t;
                deltas.add(0);
                return;
            }
            int delta = t - prevTimestamp;
            int deltaOfDelta = delta - prevDelta;
            deltas.add(deltaOfDelta);
            prevDelta = delta;
            prevTimestamp = t;
        }

        long estimateBits() {
            long bits = 64; // base timestamp
            for (int d : deltas.subList(1, deltas.size())) {
                if (d == 0) bits += 1;
                else if (d >= -63 && d <= 64) bits += 2 + 7;
                else if (d >= -255 && d <= 256) bits += 2 + 9;
                else bits += 2 + 32;
            }
            return bits;
        }

        double compressionRatio(long rawBytes) {
            return (double) rawBytes * 8 / estimateBits();
        }
    }

    // --- Time-partitioned store ---
    static class TimePartitionedStore {
        final Map<String, Map<LocalDate, List<String>>> partitions = new HashMap<>();

        void write(String metric, Instant ts, String value) {
            LocalDate day = ts.atZone(ZoneOffset.UTC).toLocalDate();
            partitions.computeIfAbsent(metric, k -> new HashMap<>())
                      .computeIfAbsent(day, k -> new ArrayList<>())
                      .add(ts + " " + value);
        }

        List<String> query(String metric, LocalDate start, LocalDate end) {
            var result = new ArrayList<String>();
            var metricParts = partitions.getOrDefault(metric, Map.of());
            for (var date = start; !date.isAfter(end); date = date.plusDays(1)) {
                var data = metricParts.get(date);
                if (data != null) result.addAll(data);
            }
            return result;
        }
    }

    // --- Downsampling ---
    static Map<LocalDateTime, Double> downsample(List<Double> values, LocalDateTime start, int windowMinutes) {
        var result = new TreeMap<LocalDateTime, Double>();
        int windowSize = windowMinutes;
        for (int i = 0; i < values.size(); i++) {
            LocalDateTime bucket = start.plusMinutes((i / windowSize) * windowSize);
            result.merge(bucket, values.get(i), Double::sum);
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("=== Delta-of-Delta Compression ===");
        CompressedBlock block = new CompressedBlock();
        long now = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            block.add(now + i * 1000L + (i % 5 == 0 ? (long)(Math.random() * 100) : 0));
        }
        System.out.println("Estimated compression ratio: " + String.format("%.1fx", block.compressionRatio(1000 * 8)));

        System.out.println("\n=== Time Partitioned Store ===");
        TimePartitionedStore store = new TimePartitionedStore();
        store.write("cpu_usage", Instant.parse("2024-06-01T10:00:00Z"), "45.2");
        store.write("cpu_usage", Instant.parse("2024-06-02T10:00:00Z"), "67.1");
        store.write("cpu_usage", Instant.parse("2024-06-03T10:00:00Z"), "55.0");
        System.out.println("Query June 1-2: " + store.query("cpu_usage", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 2)).size() + " records");

        System.out.println("\n=== Downsampling ===");
        var values = new ArrayList<Double>();
        for (int i = 0; i < 1440; i++) values.add(Math.random() * 100);
        var downsampled = downsample(values, LocalDateTime.of(2024, 6, 1, 0, 0), 60);
        System.out.println("1440 minutes -> " + downsampled.size() + " hourly buckets");
    }
}