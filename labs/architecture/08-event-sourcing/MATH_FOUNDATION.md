# Math Foundation for Event Sourcing

## Event Store Size Calculation
```
EventCount = Operations / Aggregate × Aggregates
Storage = EventCount × AvgEventSize

Example: 1000 orders, 10 events each, 500 bytes per event
Storage = 1000 × 10 × 500 = 5MB
```

## Replay Performance
```
ReplayTime = EventCount × ProcessTimePerEvent
RebuildTime = SnapshotLoadTime + RemainingEvents × ProcessTimePerEvent

Without snapshot: 1000 events × 10ms = 10 seconds
With snapshot: 2ms + 100 events × 10ms = 1.002 seconds
```

## Snapshot Strategy
```
OptimalSnapshotFrequency = √(TotalEvents × EventProcessTime / SnapshotCreationTime)

Example: TotalEvents=10000, EventProcessTime=10ms, SnapshotTime=100ms
Optimal = √(10000 × 10 / 100) = √1000 ≈ 32 events
```

## Temporal Query Complexity
```
StateAtTime(T) = ReplayEvents(EventStore, AggregateId, T)
Complexity = O(N) where N = events before time T
Optimization: Binary search on snapshot + events
```

```java
public class EventSourcingMath {
    public long estimateStorage(int aggregates, int avgEvents, int avgEventSize) {
        return (long) aggregates * avgEvents * avgEventSize;
    }

    public int optimalSnapshotFrequency(int totalEvents, 
                                         long eventProcessTime, 
                                         long snapshotTime) {
        return (int) Math.sqrt(totalEvents * eventProcessTime / snapshotTime);
    }
}
```
