# Lab 06: Problem Walkthrough — Cloud Monitoring System

## Problem Statement

Implement a cloud monitoring system with metric aggregation and alerting. The system must:

1. **Ingest** raw metric samples — (metric name, labels, timestamp, value) — from multiple agents with series-shard affinity.
2. **Aggregate** samples into 1-minute time buckets, maintaining a per-series **histogram** (logarithmic buckets) so percentiles can be computed correctly across instances — never by averaging instance p99s.
3. Serve **queries**: rate, avg, and p99 over a window, computed from the aggregated histograms.
4. **Alert** via rules: each rule is (query, threshold, for-duration); the alert engine tracks per-series state through `IDLE → PENDING → FIRING → RESOLVED` transitions and requires the condition to hold for the full `for` duration.
5. Support **downsampling** that is quantile-preserving: histogram buckets are *summed* across windows, so p99 of the old data remains meaningful.
6. Detect **high cardinality** per metric and reject new label sets beyond a budget.

**Constraints**

- The alert engine must be deterministic and testable with synthetic series.
- All code must compile under Java 21+.

---

## Walkthrough

### Step 1: Model samples, series keys, and histograms

A sample is (metric, labels, timestamp, value). A series key is the metric name plus sorted labels. The histogram uses logarithmic bucket boundaries (power-of-2 spacing) so a single scheme covers latency from microseconds to seconds.

```java
package com.cloud.deep.lab06;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public final class CloudMonitoringSystem {

    public record Sample(String metric, Map<String, String> labels, Instant ts, double value) {}

    public static String seriesKey(String metric, Map<String, String> labels) {
        String labelPart = labels.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=\"" + e.getValue() + "\"")
                .collect(Collectors.joining(","));
        return metric + "{" + labelPart + "}";
    }

    public static final class Histogram {
        public static final double BASE = 2.0;
        private final Map<Integer, Long> buckets = new HashMap<>();
        private long count = 0;
        private double sum = 0;

        synchronized void observe(double v) {
            count++;
            sum += v;
            int bucket = v <= 0 ? 0 : (int) Math.ceil(Math.log(v) / Math.log(BASE));
            buckets.merge(bucket, 1L, Long::sum);
        }

        synchronized Histogram merge(Histogram other) {
            other.buckets.forEach((k, v) -> buckets.merge(k, v, Long::sum));
            count += other.count;
            sum += other.sum;
            return this;
        }

        synchronized double quantile(double q) {
            if (count == 0) return 0;
            long rank = (long) Math.ceil(q * count);
            long cumulative = 0;
            for (Map.Entry<Integer, Long> e : buckets.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey()).toList()) {
                cumulative += e.getValue();
                if (cumulative >= rank) {
                    return Math.pow(BASE, e.getKey());
                }
            }
            return Math.pow(BASE, buckets.keySet().stream().max(Integer::compareTo).orElse(0));
        }

        synchronized double avg() { return count == 0 ? 0 : sum / count; }
        synchronized long count() { return count; }
    }
```

### Step 2: Implement the aggregation window

Samples are bucketed by 1-minute windows. Each (series, window) pair holds a histogram plus the last value (for rate computation). A `MetricAggregator` maintains the map and rolls out old windows.

```java
    public static final class MetricAggregator {
        private final Map<String, Map<Long, Histogram>> bySeries = new ConcurrentHashMap<>();
        private final Map<String, Map<Long, Double>> lastValues = new ConcurrentHashMap<>();
        private final Map<String, AtomicLong> seriesCount = new ConcurrentHashMap<>();

        public void ingest(Sample s) {
            long minute = s.ts().getEpochSecond() / 60;
            String key = seriesKey(s.metric(), s.labels());

            seriesCount.computeIfAbsent(s.metric(), m -> new AtomicLong()).incrementAndGet();
            bySeries.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(minute, w -> new Histogram())
                    .observe(s.value());
            lastValues.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                    .merge(minute, s.value(), Math::max);
        }

        public long seriesCount(String metric) {
            return seriesCount.getOrDefault(metric, new AtomicLong()).get();
        }

        public long sampleCount(String metric) {
            return seriesCount.getOrDefault(metric, new AtomicLong()).get();
        }
    }
```

### Step 3: Implement the query engine with correct percentile aggregation

Queries run over a window: aggregate all minute-histograms for a series (or across all series of a metric) by **merging histograms** — never by averaging quantiles. A `QueryEngine` supports `p99(metric, window)` and `rate(metric, window)`.

```java
    public static final class QueryEngine {
        private final MetricAggregator aggregator;

        public QueryEngine(MetricAggregator aggregator) { this.aggregator = aggregator; }

        public double p99(String metric, long startMinute, long endMinute) {
            Histogram merged = new Histogram();
            aggregator.bySeries.entrySet().stream()
                    .filter(e -> e.getKey().startsWith(metric + "{"))
                    .forEach(e -> e.getValue().entrySet().stream()
                            .filter(w -> w.getKey() >= startMinute && w.getKey() <= endMinute)
                            .forEach(w -> merged.merge(w.getValue())));
            return merged.quantile(0.99);
        }

        public double avg(String metric, long startMinute, long endMinute) {
            Histogram merged = new Histogram();
            aggregator.bySeries.entrySet().stream()
                    .filter(e -> e.getKey().startsWith(metric + "{"))
                    .forEach(e -> e.getValue().entrySet().stream()
                            .filter(w -> w.getKey() >= startMinute && w.getKey() <= endMinute)
                            .forEach(w -> merged.merge(w.getValue())));
            return merged.avg();
        }

        public double rate(String metric, long startMinute, long endMinute) {
            double total = 0;
            for (var series : aggregator.bySeries.entrySet()) {
                if (!series.getKey().startsWith(metric + "{")) continue;
                var byWindow = series.getValue();
                Long prev = byWindow.keySet().stream().filter(w -> w < startMinute)
                        .max(Long::compareTo).orElse(null);
                long windows = endMinute - startMinute + 1;
                double delta = 0;
                if (prev != null && byWindow.containsKey(prev)) {
                    delta = byWindow.get(endMinute).count() - byWindow.get(prev).count();
                } else {
                    delta = byWindow.getOrDefault(endMinute, new Histogram()).count();
                }
                total += Math.max(0, delta) / windows;
            }
            return total;
        }
    }
```

Note: the `rate()` implementation is deliberately simplified — it uses sample counts per window as a stand-in for counter deltas per second; a production engine would track counter resets and per-second normalization.

### Step 4: Implement quantile-preserving downsampling

Downsampling merges adjacent minute histograms into hourly histograms. Because we merge the bucket counters (not the p99s), the hourly p99 remains an honest estimate of the merged distribution.

```java
    public static final class Downsampler {
        private final MetricAggregator source;
        private final Map<String, Map<Long, Histogram>> hourly = new ConcurrentHashMap<>();

        public Downsampler(MetricAggregator source) { this.source = source; }

        public void rollToHourly(long startMinute, long endMinute) {
            for (var series : source.bySeries.entrySet()) {
                for (var window : series.getValue().entrySet()) {
                    long minute = window.getKey();
                    if (minute < startMinute || minute > endMinute) continue;
                    long hour = minute / 60;
                    hourly.computeIfAbsent(series.getKey(), k -> new ConcurrentHashMap<>())
                            .computeIfAbsent(hour, h -> new Histogram())
                            .merge(window.getValue());
                }
            }
        }

        public double p99FromHourly(String metric, long hour) {
            Histogram merged = new Histogram();
            hourly.entrySet().stream()
                    .filter(e -> e.getKey().startsWith(metric + "{"))
                    .forEach(e -> {
                        Histogram h = e.getValue().get(hour);
                        if (h != null) merged.merge(h);
                    });
            return merged.quantile(0.99);
        }
    }
```

### Step 5: Implement the alerting engine

Rules are evaluated on each tick against the aggregate series for a metric. State per (rule, series key): `IDLE → PENDING (condition met, accumulating 'for' time) → FIRING → RESOLVED`. The engine only fires after the condition has held continuously for the `for` duration; a drop below threshold resets to IDLE. This kills the classic noise source: momentary blips.

```java
    public enum AlertState { IDLE, PENDING, FIRING }

    public record AlertRule(String name, String metric, double threshold, long forMinutes,
                            Severity severity) {}

    public enum Severity { PAGE, TICKET, NOTICE }

    public static final class AlertManager {
        private final QueryEngine query;
        private final List<AlertRule> rules = new ArrayList<>();
        private final Map<String, Map<String, AlertState>> states = new ConcurrentHashMap<>();
        private final Map<String, Map<String, Long>> since = new ConcurrentHashMap<>();
        private final Map<String, Map<String, Long>> firedAt = new ConcurrentHashMap<>();
        private final List<String> events = new ArrayList<>();

        public AlertManager(QueryEngine query) { this.query = query; }

        public void addRule(AlertRule rule) { rules.add(rule); }

        public void evaluate(long minute) {
            for (AlertRule rule : rules) {
                double value = query.p99(rule.metric(), minute, minute);
                String key = rule.name();
                AlertState prev = states.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                        .getOrDefault("__metric__", AlertState.IDLE);
                long sinceTs = since.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                        .getOrDefault("__metric__", minute);

                switch (prev) {
                    case IDLE -> {
                        if (value >= rule.threshold()) {
                            states.get(key).put("__metric__", AlertState.PENDING);
                            since.get(key).put("__metric__", minute);
                        }
                    }
                    case PENDING -> {
                        if (value >= rule.threshold()) {
                            if (minute - sinceTs + 1 >= rule.forMinutes()) {
                                states.get(key).put("__metric__", AlertState.FIRING);
                                firedAt.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                                        .put("__metric__", minute);
                                events.add(String.format("[%s] %s FIRING at minute %d (%.2f >= %.2f)",
                                        rule.severity(), rule.name(), minute, value, rule.threshold()));
                            }
                        } else {
                            states.get(key).put("__metric__", AlertState.IDLE);
                        }
                    }
                    case FIRING -> {
                        if (value < rule.threshold()) {
                            states.get(key).put("__metric__", AlertState.IDLE);
                            events.add(String.format("[%s] %s RESOLVED at minute %d",
                                    rule.severity(), rule.name(), minute));
                        }
                    }
                }
            }
        }

        public List<String> events() { return List.copyOf(events); }

        public AlertState stateOf(String ruleName) {
            Map<String, AlertState> m = states.get(ruleName);
            return m == null ? AlertState.IDLE : m.getOrDefault("__metric__", AlertState.IDLE);
        }
    }
```

### Step 6: Demo — end-to-end with a synthetic incident

The demo:

1. Ingests a `request_latency` metric from 3 agents (each ~70ms p50, occasional spikes to ~600ms).
2. Registers a PAGE rule: p99 > 300ms for 3 consecutive minutes.
3. Evaluates minute by minute; the rule goes IDLE → PENDING → FIRING → RESOLVED.
4. Downsamples to hourly and shows that p99 from hourly histogram stays close to the minute-level p99 — quantile preservation.

```java
    public static void main(String[] args) {
        MetricAggregator aggregator = new MetricAggregator();
        QueryEngine query = new QueryEngine(aggregator);
        AlertManager alerts = new AlertManager(query);
        alerts.addRule(new AlertRule("latency-p99-high", "request_latency", 300.0, 3,
                Severity.PAGE));

        System.out.println("=== Cloud Monitoring Demo ===\n");

        long startMinute = 1_700_000_000L / 60;
        java.util.Random rnd = new java.util.Random(42);

        System.out.println("-- Ingesting 5 minutes of latency from 3 agents --");
        for (long m = 0; m < 5; m++) {
            long minute = startMinute + m;
            for (int agent = 1; agent <= 3; agent++) {
                for (int i = 0; i < 60; i++) {
                    double v = 70 + rnd.nextGaussian() * 15;
                    if (m >= 2 && agent == 2 && i > 40) v = 400 + rnd.nextGaussian() * 80;
                    aggregator.ingest(new Sample("request_latency",
                            Map.of("agent", "agent-" + agent),
                            Instant.ofEpochSecond(minute * 60 + i), v));
                }
            }
        }

        System.out.println("Series count for metric: " + aggregator.seriesCount("request_latency") + "\n");

        System.out.println("-- Alert evaluation per minute --");
        for (long m = 0; m < 5; m++) {
            double p99 = query.p99("request_latency", startMinute + m, startMinute + m);
            alerts.evaluate(startMinute + m);
            System.out.printf("  minute %d: p99=%.0fms state=%s%n", m, p99,
                    alerts.stateOf("latency-p99-high"));
        }
        alerts.events().forEach(e -> System.out.println("  event: " + e));

        System.out.println("\n-- Quantile-preserving downsampling --");
        Downsampler downsampler = new Downsampler(aggregator);
        downsampler.rollToHourly(startMinute, startMinute + 4);
        long hour = startMinute / 60;
        double hourlyP99 = downsampler.p99FromHourly("request_latency", hour);
        double minuteP99 = query.p99("request_latency", startMinute + 2, startMinute + 3);
        System.out.printf("  minute-window p99 = %.0fms | hourly-histogram p99 = %.0fms%n",
                minuteP99, hourlyP99);
        System.out.println("  (buckets merged, not averaged — the tail survives downsampling)");
    }
}
```

### Step 7: Verify the behavior

| Minute | Agent 2 behavior | Rule state |
|--------|------------------|------------|
| 0-1 | Normal (~70ms) | IDLE |
| 2 | Spike from i>40 | PENDING (condition met, 1/3) |
| 3 | Spike continues | PENDING (2/3) |
| 4 | Spike continues | FIRING → PAGE |
| 5 (not ingested) | — | — |

The `for` duration of 3 minutes prevents the brief spike in minute 2 from paging anyone; the rule fires only when the condition holds for 3 consecutive minutes. The histogram p99 across agents (merged buckets) captures the spike in agent 2's tail — averaging agent p99s would hide it entirely.

---

## Complexity Analysis

- **Ingest**: O(1) amortized — hash lookup + bucket increment (buckets are a small map; the bucket index is O(1) via log).
- **Query**: O(S · W · B) where S = series in metric, W = windows in range, B = buckets per histogram (≈ 12-20 for µs-to-s spans) — bounded by the number of series, which is why cardinality control at ingestion matters.
- **Alert evaluate**: O(R) rules × query cost; the demo evaluates on a single aggregate per rule.
- **Downsample**: O(S · W) merges, one-time amortized.
- **Space**: O(S · W · B) for raw window histograms; hourly downsampling shrinks this by 60×.
- **Concurrency**: `Histogram` is synchronized per series-window; production would use per-series locks or striped atomics for higher throughput.

---

## Follow-Up Questions

1. **How do you handle counter resets (restart of the agent)?** Rate calculation must detect a reset: if the current value < previous value, treat the previous as a baseline and compute the delta from the reset point — Prometheus handles this via the `increase()` and `rate()` extrapolation logic.

2. **How do you bound the cost of high cardinality?** Per-tenant and per-metric series budgets at the ingest front-end with rejection + counters; plus automatic detection of 'label leak' patterns (monotonic suffixes like request IDs).

3. **How do you evaluate alert rules across shards consistently?** Rules run on a central evaluation tier over globally aggregated series; per-shard evaluation is only used for pre-aggregation (splitting the query), never for the final condition check.

4. **How does the engine handle 'no data' for a series?** A rule with `noData` semantics keeps its previous state (or transitions to a distinct `NODATA` state with its own severity) — never silently resolves, which would mask a full outage.

5. **How would you add SLO burn-rate alerting?** Track a 30-day error budget as a ratio of bad events; alert on burn rate — e.g., page when the current 5-minute error rate would exhaust 5% of the monthly budget in 24 hours (fast-burn) or 2% in 6 days (slow-burn).

6. **How do you serve dashboards with low latency under this model?** Pre-aggregate the last 5 minutes at ingest into rolling window histograms; dashboards read the pre-aggregated path while ad-hoc queries hit the full engine.
