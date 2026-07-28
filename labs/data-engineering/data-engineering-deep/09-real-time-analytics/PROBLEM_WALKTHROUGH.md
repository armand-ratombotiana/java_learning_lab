# Lab 09: Problem Walkthrough — Real-Time Revenue Dashboard Pipeline

## Problem

Build a `RevenueDashboardPipeline` that ingests order events, computes running revenue totals across multiple window sizes (1min, 5min, 1hr), and maintains a materialized view for a real-time dashboard.

## Walkthrough

### Step 1: Event Ingestion

```java
public record OrderEvent(String orderId, String userId, double amount, String currency, Instant timestamp) {}

public class RevenueCalculator {
    public RevenueEvent toRevenue(OrderEvent order, double conversionRate) {
        return new RevenueEvent(order.orderId(), order.amount() * conversionRate, order.timestamp());
    }

    public record RevenueEvent(String orderId, double amountUSD, Instant timestamp) {}
}
```

### Step 2: Multi-Window Aggregator

```java
public class MultiWindowAggregator {
    private final Map<String, TumblingWindowAggregator> aggregators = Map.of(
        "1min", new TumblingWindowAggregator(Duration.ofMinutes(1)),
        "5min", new TumblingWindowAggregator(Duration.ofMinutes(5)),
        "1hr", new TumblingWindowAggregator(Duration.ofHours(1))
    );

    public Map<String, WindowState> process(RevenueEvent event) {
        Map<String, WindowState> results = new HashMap<>();
        for (var entry : aggregators.entrySet()) {
            results.put(entry.getKey(), entry.getValue().process(event));
        }
        return results;
    }
}
```

### Step 3: Materialized Dashboard View

```java
public class DashboardView {
    private final MaterializedView view = new MaterializedView();

    public void update(Map<String, WindowState> windows) {
        for (var entry : windows.entrySet()) {
            String key = "revenue_" + entry.getKey();
            String value = String.format("%.2f", entry.getValue().sum());
            view.upsert(key, value);
        }
        // Composite metrics
        view.upsert("revenue_total",
            String.format("%.2f", windows.values().stream().mapToDouble(WindowState::sum).sum()));
    }

    public String query(String metric) { return view.get(metric); }
}
```

### Step 4: Complete Pipeline

```java
public class RevenueDashboardPipeline {
    private final RevenueCalculator calculator = new RevenueCalculator();
    private final MultiWindowAggregator aggregator = new MultiWindowAggregator();
    private final DashboardView dashboard = new DashboardView();

    public void processOrder(OrderEvent order, double rate) {
        var revenue = calculator.toRevenue(order, rate);
        var windows = aggregator.process(revenue);
        dashboard.update(windows);
    }

    public String getRevenue(String window) { return dashboard.query("revenue_" + window); }
}
```

## Complexity

- **Time**: O(1) per event per window
- **Space**: O(W) where W = active windows
