# Lab 09: Real-Time Analytics — Implementation Guide

## Step 1: Event Model

```java
public record AnalyticsEvent(String id, String eventType, String userId, double value,
                             Instant eventTime, Map<String, String> properties) {}
```

## Step 2: Tumbling Window Aggregation

```java
public class TumblingWindowAggregator {
    private final Duration windowSize;
    private final Map<Long, WindowState> windows = new ConcurrentHashMap<>();

    public record WindowState(long count, double sum, double min, double max) {
        public double avg() { return count == 0 ? 0 : sum / count; }
    }

    public WindowState process(AnalyticsEvent event) {
        long windowKey = getWindowKey(event.eventTime());
        return windows.merge(windowKey, new WindowState(1, event.value(), event.value(), event.value()),
            (old, _new) -> new WindowState(
                old.count() + 1,
                old.sum() + event.value(),
                Math.min(old.min(), event.value()),
                Math.max(old.max(), event.value())
            ));
    }

    private long getWindowKey(Instant time) {
        return time.toEpochMilli() / windowSize.toMillis();
    }
}
```

## Step 3: Hopping Window

```java
public class HoppingWindowAggregator {
    private final Duration windowSize;
    private final Duration advanceSize; // e.g., 5min window, 1min advance

    public List<WindowState> process(AnalyticsEvent event) {
        List<WindowState> affected = new ArrayList<>();
        long eventMs = event.eventTime().toEpochMilli();
        long start = eventMs - (eventMs % advanceSize.toMillis()) - windowSize.toMillis() + advanceSize.toMillis();
        long end = eventMs - (eventMs % advanceSize.toMillis());
        for (long ws = start; ws <= end; ws += advanceSize.toMillis()) {
            // update each overlapping window
            affected.add(updateWindow(ws, event));
        }
        return affected;
    }
}
```

## Step 4: Late Data Handling with Watermark

```java
public class WatermarkTracker {
    private final Duration maxOutOfOrderness;
    private long maxObservedTimestamp = Long.MIN_VALUE;

    public WatermarkTracker(Duration maxOutOfOrderness) {
        this.maxOutOfOrderness = maxOutOfOrderness;
    }

    public long currentWatermark() {
        return maxObservedTimestamp - maxOutOfOrderness.toMillis();
    }

    public void observe(AnalyticsEvent event) {
        long et = event.eventTime().toEpochMilli();
        if (et > maxObservedTimestamp) maxObservedTimestamp = et;
    }

    public boolean isLate(AnalyticsEvent event) {
        return event.eventTime().toEpochMilli() < currentWatermark();
    }
}
```

## Step 5: Materialized View (KV Store)

```java
public class MaterializedView {
    private final Map<String, String> store = new ConcurrentHashMap<>();

    public void upsert(String key, String value) { store.put(key, value); }
    public String get(String key) { return store.get(key); }

    // Emit changelog to Kafka topic for dashboard consumption
    public void emitChange(String key, String oldValue, String newValue) {
        // producer.send(new ProducerRecord<>("view-updates", key, newValue));
    }
}
```
