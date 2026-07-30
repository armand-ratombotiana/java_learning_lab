package com.systemdesign.deep.lab08;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Lab 08: Real-Time Systems — Stream processing, windowing, watermarks,
 * exactly-once semantics, and low-latency design.
 */
public class RealTimeSystemsLab {

    static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    static final Random RAND = ThreadLocalRandom.current();

    // ──────────────────────────────────────────────
    // Event and Stream types
    // ──────────────────────────────────────────────
    record Event(String id, String key, long eventTimeMs, double value) {}
    record WindowedResult(String windowKey, long windowStart, long windowEnd,
                          double sum, long count, double avg) {}

    // ──────────────────────────────────────────────
    // 1. Stream Processing with Windowing
    // ──────────────────────────────────────────────
    static class StreamProcessor {
        final long windowSizeMs;
        final Consumer<WindowedResult> output;

        StreamProcessor(long windowSizeMs, Consumer<WindowedResult> output) {
            this.windowSizeMs = windowSizeMs;
            this.output = output;
        }

        // Tumbling window aggregation
        void processTumbling(List<Event> events, String key) {
            var windows = new TreeMap<Long, List<Event>>();
            for (var e : events) {
                long windowStart = (e.eventTimeMs() / windowSizeMs) * windowSizeMs;
                windows.computeIfAbsent(windowStart, k -> new ArrayList<>()).add(e);
            }
            for (var entry : windows.entrySet()) {
                long ws = entry.getKey();
                long we = ws + windowSizeMs;
                var evts = entry.getValue();
                double sum = evts.stream().mapToDouble(Event::value).sum();
                output.accept(new WindowedResult(key, ws, we, sum, evts.size(), sum / evts.size()));
            }
        }

        // Sliding window aggregation
        void processSliding(List<Event> events, String key, long slideMs) {
            long minTime = events.stream().mapToLong(Event::eventTimeMs).min().orElse(0);
            long maxTime = events.stream().mapToLong(Event::eventTimeMs).max().orElse(0);

            for (long ws = minTime; ws <= maxTime - windowSizeMs; ws += slideMs) {
                long we = ws + windowSizeMs;
                long fws = ws;
                var inWindow = events.stream()
                        .filter(e -> e.eventTimeMs() >= fws && e.eventTimeMs() < we)
                        .toList();
                if (!inWindow.isEmpty()) {
                    double sum = inWindow.stream().mapToDouble(Event::value).sum();
                    output.accept(new WindowedResult(key, ws, we, sum, inWindow.size(), sum / inWindow.size()));
                }
            }
        }
    }

    // ──────────────────────────────────────────────
    // 2. Watermark-based Event Time Processing
    // ──────────────────────────────────────────────
    static class WatermarkProcessor {
        final long windowSizeMs;
        final long maxLatenessMs;
        long currentWatermark = Long.MIN_VALUE;
        final Map<Long, List<Event>> pendingWindows = new ConcurrentHashMap<>();

        WatermarkProcessor(long windowSizeMs, long maxLatenessMs) {
            this.windowSizeMs = windowSizeMs;
            this.maxLatenessMs = maxLatenessMs;
        }

        // Heuristic watermark: advance to min of observed - lateness
        void observeEvent(Event event) {
            long watermark = event.eventTimeMs() - maxLatenessMs;
            if (watermark > currentWatermark) {
                currentWatermark = watermark;
                triggerCompletedWindows();
            }
        }

        void addEvent(Event event) {
            long ws = (event.eventTimeMs() / windowSizeMs) * windowSizeMs;
            pendingWindows.computeIfAbsent(ws, k -> new ArrayList<>()).add(event);
            observeEvent(event);
        }

        void triggerCompletedWindows() {
            var completed = pendingWindows.entrySet().stream()
                    .filter(e -> e.getKey() + windowSizeMs <= currentWatermark)
                    .toList();
            for (var entry : completed) {
                long ws = entry.getKey();
                var events = entry.getValue();
                double sum = events.stream().mapToDouble(Event::value).sum();
                System.out.println("  [Watermark] Window [" + ws + ".." + (ws + windowSizeMs)
                        + "] triggered — events=" + events.size() + " sum=" + String.format("%.1f", sum)
                        + " watermark=" + currentWatermark);
                pendingWindows.remove(ws);
            }
        }

        void handleLateEvent(Event event) {
            long ws = (event.eventTimeMs() / windowSizeMs) * windowSizeMs;
            if (ws + windowSizeMs <= currentWatermark) {
                System.out.println("  [Late] Event " + event.id() + " (time="
                        + event.eventTimeMs() + ") arrived after watermark " + currentWatermark
                        + " — routing to side output");
            }
        }

        long getCurrentWatermark() { return currentWatermark; }
    }

    // ──────────────────────────────────────────────
    // 3. Exactly-Once Semantics Simulation
    // ──────────────────────────────────────────────
    static class ExactlyOnceProcessor {
        static class OffsetStore {
            final Map<String, Long> committedOffsets = new ConcurrentHashMap<>();

            boolean isCommitted(String source, long offset) {
                return committedOffsets.getOrDefault(source, -1L) >= offset;
            }

            void commit(String source, long offset) {
                committedOffsets.merge(source, offset, Math::max);
            }
        }

        static class IdempotentSink {
            final Set<String> written = ConcurrentHashMap.newKeySet();

            // Idempotent write: same key+value produces same result
            boolean write(String key, String value, long idempotencyKey) {
                String dedupKey = key + ":" + idempotencyKey;
                if (written.add(dedupKey)) {
                    System.out.println("  [Sink] Wrote " + key + "=" + value + " (idempotencyKey=" + idempotencyKey + ")");
                    return true;
                }
                System.out.println("  [Sink] Duplicate write suppressed for " + key + " (idempotencyKey=" + idempotencyKey + ")");
                return false;
            }
        }

        static class TransactionalProcessor {
            final OffsetStore offsets = new OffsetStore();
            final IdempotentSink sink = new IdempotentSink();
            final Map<String, List<String>> txBuffer = new ConcurrentHashMap<>();

            void processInTransaction(String source, long offset, String key, String value) {
                // Simulate atomic processing: buffer writes
                var txId = source + ":" + offset;
                txBuffer.computeIfAbsent(txId, k -> new ArrayList<>()).add(key + "=" + value);
            }

            boolean commit(String source, long offset) {
                var txId = source + ":" + offset;
                var pending = txBuffer.remove(txId);
                if (pending == null) return false;

                // Simulate atomic commit: all writes + offset commit
                var idempotencyKey = offset;
                for (var entry : pending) {
                    var parts = entry.split("=");
                    sink.write(parts[0], parts[1], idempotencyKey);
                }
                offsets.commit(source, offset);
                System.out.println("  [TX] Committed " + txId + " — " + pending.size() + " writes");
                return true;
            }

            // On restart, check which offsets are already committed
            void recover(String source) {
                long lastCommitted = offsets.committedOffsets.getOrDefault(source, -1L);
                System.out.println("  [Recovery] " + source + " last committed offset: " + lastCommitted);
            }
        }

        static void demo() {
            System.out.println("3. Exactly-Once Semantics");
            var processor = new TransactionalProcessor();

            // Process events in transactions
            processor.processInTransaction("kafka:topic1", 1, "user:42", "purchase:99.99");
            processor.processInTransaction("kafka:topic1", 1, "user:43", "purchase:49.99");
            processor.commit("kafka:topic1", 1);

            // Simulate duplicate delivery (consumer rebalance)
            processor.processInTransaction("kafka:topic1", 1, "user:42", "purchase:99.99");
            processor.commit("kafka:topic1", 1); // Idempotent write suppresses duplicate

            processor.processInTransaction("kafka:topic1", 2, "user:44", "purchase:29.99");
            processor.commit("kafka:topic1", 2);

            processor.recover("kafka:topic1");
            System.out.println();
        }
    }

    // ──────────────────────────────────────────────
    // 4. Low-Latency Stateful Processing
    // ──────────────────────────────────────────────
    static class LowLatencyProcessor {
        static class MovingAverage {
            final int windowSize;
            final Deque<Double> values = new ArrayDeque<>();
            double sum = 0;

            MovingAverage(int windowSize) { this.windowSize = windowSize; }

            synchronized double add(double value) {
                values.add(value);
                sum += value;
                if (values.size() > windowSize) {
                    sum -= values.removeFirst();
                }
                return sum / values.size();
            }
        }

        static class FastStateStore {
            final Map<String, MovingAverage> state = new ConcurrentHashMap<>();
            final int windowSize;

            FastStateStore(int windowSize) { this.windowSize = windowSize; }

            double update(String key, double value) {
                var ma = state.computeIfAbsent(key, k -> new MovingAverage(windowSize));
                return ma.add(value);
            }
        }

        static void demo() {
            System.out.println("4. Low-Latency Stateful Processing");
            var stateStore = new FastStateStore(5);
            var events = List.of(
                    new Event("e1", "sensor-A", 1000, 22.5),
                    new Event("e2", "sensor-A", 1001, 23.0),
                    new Event("e3", "sensor-A", 1002, 21.8),
                    new Event("e4", "sensor-B", 1000, 45.0),
                    new Event("e5", "sensor-A", 1003, 24.1),
                    new Event("e6", "sensor-A", 1004, 22.9)
            );

            for (var e : events) {
                double avg = stateStore.update(e.key(), e.value());
                System.out.println("  Event " + e.id() + " -> " + e.key() + " value="
                        + e.value() + " moving_avg=" + String.format("%.2f", avg));
            }
            System.out.println();
        }
    }

    // ──────────────────────────────────────────────
    // 5. End-to-End Pipeline Simulation
    // ──────────────────────────────────────────────
    static class PipelineSimulation {

        static void demo() {
            System.out.println("5. End-to-End Stream Processing Pipeline");

            // Generate events at different timestamps
            long baseTime = System.currentTimeMillis();
            var events = new ArrayList<Event>();
            for (int i = 0; i < 20; i++) {
                events.add(new Event("evt-" + i, "metric-A",
                        baseTime + (i * 500L) + RAND.nextInt(200),
                        10 + RAND.nextDouble() * 90));
            }

            // Tumbling windows
            var processor = new StreamProcessor(2000, r ->
                    System.out.println("  [Result] " + r.windowKey() + " ["
                            + r.windowStart() + ".." + r.windowEnd()
                            + "] avg=" + String.format("%.2f", r.avg())
                            + " count=" + r.count()));
            processor.processTumbling(events, "metric-A");

            // Watermark processing
            System.out.println("\n  --- Late event handling ---");
            var wm = new WatermarkProcessor(2000, 1000);
            for (var e : events) {
                wm.addEvent(e);
            }
            // Simulate late event
            var lateEvent = new Event("late-1", "metric-A", baseTime, 50.0);
            wm.handleLateEvent(lateEvent);

            // Remaining pending windows
            System.out.println("  Remaining pending windows: " + wm.pendingWindows.size());
            System.out.println("  Final watermark: " + wm.getCurrentWatermark());
            System.out.println();
        }
    }

    // ──────────────────────────────────────────────
    // Main
    // ──────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  Lab 08: Real-Time Systems Deep-Dive        ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        // 1. Stream Processing with Tumbling & Sliding Windows
        System.out.println("1. Stream Processing with Windowing");
        long now = System.currentTimeMillis();
        var events = List.of(
                new Event("e1", "m1", now, 10),
                new Event("e2", "m1", now + 500, 20),
                new Event("e3", "m1", now + 1200, 30),
                new Event("e4", "m1", now + 2500, 40),
                new Event("e5", "m1", now + 3100, 50)
        );

        System.out.println("  Tumbling windows (2000ms):");
        var tumbling = new StreamProcessor(2000, System.out::println);
        tumbling.processTumbling(events, "m1");
        System.out.println();

        System.out.println("  Sliding windows (2000ms window, 1000ms slide):");
        var sliding = new StreamProcessor(2000, System.out::println);
        sliding.processSliding(events, "m1", 1000);
        System.out.println();

        // 2. Watermark Processing
        System.out.println("2. Watermark-based Event Time Processing");
        var wm = new WatermarkProcessor(2000, 1000);
        wm.addEvent(new Event("e1", "k", now, 10));
        wm.addEvent(new Event("e2", "k", now + 2200, 20));
        wm.addEvent(new Event("e3", "k", now + 1100, 15)); // out of order
        sleep(100);
        System.out.println("  Current watermark: " + wm.getCurrentWatermark());
        wm.handleLateEvent(new Event("late", "k", now - 500, 5));
        System.out.println();

        // 3. Exactly-Once
        ExactlyOnceProcessor.demo();

        // 4. Low-Latency State
        LowLatencyProcessor.demo();

        // 5. Pipeline Simulation
        PipelineSimulation.demo();

        System.out.println("All real-time systems concepts demonstrated successfully.");
    }
}
