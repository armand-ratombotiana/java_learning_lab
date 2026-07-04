# Math Foundation for CQRS

## Consistency Calculations
### Eventual Consistency Delay
```
Δt = T_projection - T_event
Where:
T_projection = time read model is updated
T_event = time event was committed

Target: Δt < 100ms for critical reads
```

## Read/Write Distribution
```
Read Ratio = R_requests / (R_requests + W_requests)
Write Ratio = W_requests / (R_requests + W_requests)

Example: 9000 reads + 1000 writes per minute
Read Ratio = 0.9 (90%)
Write Ratio = 0.1 (10%)
```

## Scaling Requirements
```
Write capacity = W_requests × avg_write_latency / target_utilization
Read capacity = R_requests × avg_read_latency / target_utilization

Separate scaling for read vs write infrastructure
```

## Projection Latency Budget
```
Total sync budget = 1000ms
Breakdown:
  - Event store fetch: 50ms
  - Projection computation: 200ms
  - Read model update: 100ms
  - Network: 50ms
  - Buffer: 600ms
```

```java
public class CQRSCapacityPlanner {
    public ScalingPlan calculateCapacity(
        long readsPerSecond, long writesPerSecond,
        double readLatency, double writeLatency,
        double targetUtilization) {

        long readInstances = (long) Math.ceil(
            readsPerSecond * readLatency / targetUtilization);
        long writeInstances = (long) Math.ceil(
            writesPerSecond * writeLatency / targetUtilization);

        return new ScalingPlan(readInstances, writeInstances);
    }
}
```
