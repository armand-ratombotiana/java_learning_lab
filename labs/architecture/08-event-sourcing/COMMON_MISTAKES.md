# Common Mistakes in Event Sourcing

## 1. Mutable Events
```java
// WRONG: Events should be immutable
public class OrderPlacedEvent {
    private String orderId;
    public void setOrderId(String id) { this.orderId = id; } // NO!
}

// CORRECT: Immutable event
public record OrderPlacedEvent(String orderId, String customerId, Instant timestamp) {}
```

## 2. Events That Depend On Current State
```java
// WRONG: Event contains computed/derived data
public class DiscountAppliedEvent {
    private BigDecimal discountedTotal; // Depends on current total at time of event
}

// CORRECT: Event contains facts only
public class DiscountAppliedEvent {
    private BigDecimal discountPercent; // Fact - 10% off
    // Total can be recalculated from events
}
```

## 3. No Snapshot Strategy
- Without snapshots, replay becomes slower over time
- Add snapshots at regular intervals (every 50-100 events)
- Store snapshot alongside events for disaster recovery

## 4. Event Schema Changes Without Migration
```java
// WRONG: Change event class without handling old versions
public class OrderPlacedEvent {
    private String email; // Added later - breaks deserialization of old events!
}

// CORRECT: Versioned events or upcasting
// Use @Upcaster for event migration
```

## 5. Using Event Store as Query Database
- Event store is for replaying aggregates, not for querying
- Build projections/read models for queries
- Event store should only be written to, not read from for operations
