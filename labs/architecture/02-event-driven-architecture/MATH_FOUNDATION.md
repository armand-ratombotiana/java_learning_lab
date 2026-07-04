# Math Foundation for Event-Driven Architecture

## Throughput Calculation
```
Throughput = N / T
Where:
N = number of events
T = time period

Example: 1,000,000 events / 60 seconds = 16,667 events/sec
```

## Latency Percentiles
```
p50 = median latency
p90 = 90% of requests faster than this
p99 = 99% of requests faster than this
p999 = 99.9% of requests faster than this
```

## Kafka Partition Count Calculation
```
Partitions needed = max(Throughput / PartitionThroughput, Consumers)
PartitionThroughput ~ 10MB/s (typical)

Example: 100MB/s throughput / 10MB/s = 10 partitions
```

## Eventual Consistency Convergence
```
P(t) = 1 - e^(-λt)
Where:
P(t) = probability of consistency after time t
λ = event propagation rate

Convergence time decreases with more replicas
```

## Buffer Sizing
```
Buffer size = ProductionRate × MaxExpectedDelay
B = R × D

Example: 1000 events/sec × 300 seconds = 300,000 events capacity
```

```java
public class EventCapacityPlanner {
    public int calculateRequiredCapacity(
        double eventsPerSecond,
        int maxDelaySeconds,
        double safetyMultiplier) {
        return (int) (eventsPerSecond * maxDelaySeconds * safetyMultiplier);
    }
}
```
