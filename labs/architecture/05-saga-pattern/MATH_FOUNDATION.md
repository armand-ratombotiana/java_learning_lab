# Math Foundation for Saga Pattern

## Saga Success Probability
```
P(success) = P(step1) × P(step2) × ... × P(stepN)

Example: 4 steps each at 99% success
P = 0.99^4 = 0.9606 (96% overall success)

With compensating transactions:
P(recoverable) = P(success) + P(failure) × P(compensation)

Example: P(success)=0.96, P(compensation)=0.99
P(recoverable) = 0.96 + 0.04 × 0.99 = 0.9996 (99.96%)
```

## Compensation Cost
```
Compensation Cost = Σ(step_i_cost) × compensation_overhead

Example: 4 steps at $1 each, 200% overhead
Total cost = $4 × 2 = $8 per failure
```

## Saga Latency
```
Expected latency = Σ(step_latency) + Σ(overhead)

Choreography: Lower overhead but more complex
Orchestration: More overhead but manageable

Example (4 steps, 100ms each):
Total = 4 × 100ms + 3 × 10ms (network) = 430ms
```

## Recovery Point Objective (RPO)
```
RPO = time between saga start and failure detection
Maximum data loss = operations completed before notification

For compensating sagas:
RPO = 0 (compensation restores original state)
```

```java
public class SagaMath {
    public double calculateSuccessRate(int steps, double stepReliability) {
        return Math.pow(stepReliability, steps);
    }

    public long estimateLatencyMs(int steps, long stepMs, long overheadMs) {
        return steps * stepMs + (steps - 1) * overheadMs;
    }
}
```
