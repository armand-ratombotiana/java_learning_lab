package com.dataengineering.deep.lab09;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RealTimeAnalytics {

    public record AnalyticsEvent(String id, String eventType, String userId, double value,
                                 Instant eventTime, Map<String, String> properties) {}

    public record WindowState(long count, double sum, double min, double max) {
        public double avg() { return count == 0 ? 0 : sum / count; }
        public static WindowState single(double v) { return new WindowState(1, v, v, v); }
        public WindowState merge(WindowState other) {
            return new WindowState(this.count + other.count, this.sum + other.sum,
                Math.min(this.min, other.min), Math.max(this.max, other.max));
        }
    }

    public static class TumblingWindowAggregator {
        private final Duration windowSize;
        private final Map<Long, WindowState> windows = new ConcurrentHashMap<>();

        public TumblingWindowAggregator(Duration windowSize) { this.windowSize = windowSize; }

        public WindowState process(AnalyticsEvent event) {
            long windowKey = event.eventTime().toEpochMilli() / windowSize.toMillis();
            return windows.merge(windowKey, WindowState.single(event.value()), (old, _new) -> old.merge(_new));
        }

        public Map<Long, WindowState> getWindows() { return new HashMap<>(windows); }

        public void purgeBefore(Instant cutoff) {
            long cutoffKey = cutoff.toEpochMilli() / windowSize.toMillis();
            windows.keySet().removeIf(k -> k < cutoffKey);
        }
    }

    public static class WatermarkTracker {
        private final Duration maxOutOfOrderness;
        private long maxObservedTimestamp = Long.MIN_VALUE;

        public WatermarkTracker(Duration maxOutOfOrderness) { this.maxOutOfOrderness = maxOutOfOrderness; }

        public long currentWatermark() { return maxObservedTimestamp - maxOutOfOrderness.toMillis(); }

        public void observe(AnalyticsEvent event) {
            long et = event.eventTime().toEpochMilli();
            if (et > maxObservedTimestamp) maxObservedTimestamp = et;
        }

        public boolean isLate(AnalyticsEvent event) {
            return event.eventTime().toEpochMilli() < currentWatermark();
        }
    }

    public static class MaterializedView {
        private final Map<String, String> store = new ConcurrentHashMap<>();
        private final List<String> changelog = new ArrayList<>();

        public void upsert(String key, String value) {
            String old = store.put(key, value);
            changelog.add("UPDATE " + key + ": " + old + " -> " + value);
        }

        public String get(String key) { return store.get(key); }
        public List<String> getChangelog() { return List.copyOf(changelog); }
    }

    public record OrderEvent(String orderId, String userId, double amount, String currency, Instant timestamp) {}

    public record RevenueEvent(String orderId, double amountUSD, Instant timestamp) {}

    public static class RevenueCalculator {
        public RevenueEvent toRevenue(OrderEvent order, double conversionRate) {
            return new RevenueEvent(order.orderId(), order.amount() * conversionRate, order.timestamp());
        }
    }

    public static class MultiWindowAggregator {
        private final Map<String, TumblingWindowAggregator> aggregators = Map.of(
            "1min", new TumblingWindowAggregator(Duration.ofMinutes(1)),
            "5min", new TumblingWindowAggregator(Duration.ofMinutes(5)),
            "1hr", new TumblingWindowAggregator(Duration.ofHours(1))
        );

        public Map<String, WindowState> process(RevenueEvent event) {
            var fakeEvent = new AnalyticsEvent(event.orderId(), "revenue", "system", event.amountUSD(),
                event.timestamp(), Map.of());
            Map<String, WindowState> results = new HashMap<>();
            for (var entry : aggregators.entrySet()) {
                results.put(entry.getKey(), entry.getValue().process(fakeEvent));
            }
            return results;
        }
    }

    public static class DashboardView {
        private final MaterializedView view = new MaterializedView();

        public void update(Map<String, WindowState> windows) {
            for (var entry : windows.entrySet()) {
                view.upsert("revenue_" + entry.getKey(), String.format("%.2f", entry.getValue().sum()));
            }
            view.upsert("revenue_total", String.format("%.2f",
                windows.values().stream().mapToDouble(WindowState::sum).sum()));
        }

        public String query(String metric) { return view.get(metric); }
        public MaterializedView getView() { return view; }
    }

    public static void main(String[] args) {
        var pipeline = new DashboardView();
        var aggregator = new MultiWindowAggregator();
        var calculator = new RevenueCalculator();
        var now = Instant.now();

        for (int i = 0; i < 100; i++) {
            var order = new OrderEvent("order-" + i, "user-" + (i % 5), 10.0 + i, "USD", now.minusSeconds(i * 10));
            var revenue = calculator.toRevenue(order, 1.0);
            var windows = aggregator.process(revenue);
            pipeline.update(windows);
        }

        System.out.println("Revenue 1min: " + pipeline.query("revenue_1min"));
        System.out.println("Revenue 5min: " + pipeline.query("revenue_5min"));
        System.out.println("Revenue 1hr: " + pipeline.query("revenue_1hr"));
        System.out.println("Revenue total: " + pipeline.query("revenue_total"));
        System.out.println("Changelog entries: " + pipeline.getView().getChangelog().size());
    }
}
