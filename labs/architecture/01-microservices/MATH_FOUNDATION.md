# Math Foundation for Microservices

## Availability Calculation
System availability with N services in series:
```
Availability = A1 × A2 × ... × An
Example: 3 services at 99.9% each = 0.999^3 = 99.7%
```

## CAP Theorem Probability
### Consistency vs Availability Tradeoff
```
P(partition) = probability of network partition
P(available) = 1 - P(partition) (for CP systems)
P(consistent) = 1 - P(partition) (for AP systems)
```

## Little's Law for Queueing
```
L = λ × W
Where:
L = average number of requests in system
λ = average arrival rate
W = average time a request spends in system
```

## Circuit Breaker Threshold Calculation
```
Failure Rate = Failed Calls / Total Calls (in sliding window)
Trip threshold = configurable (typically 50%)
```
```java
public class CircuitBreakerMath {
    public boolean shouldTrip(int failures, int total, double threshold) {
        double rate = (double) failures / total;
        return rate >= threshold;
    }
}
```

## Scaling Calculations
```
Service replicas needed = (RequestRate × ResponseTime) / TargetUtilization
Example: (1000 req/s × 0.2s) / 0.75 = 267 instances
```
