# Microservices Internals

## Service Discovery Internals
```java
// Eureka Client internals
@Component
public class ServiceDiscoveryManager {
    private final DiscoveryClient discoveryClient;

    public List<ServiceInstance> getInstances(String serviceId) {
        // Returns cached list of healthy instances
        // Updates via heartbeat mechanism (30s interval)
        return discoveryClient.getInstances(serviceId);
    }
}
```

## Load Balancing Internals
```java
// Ribbon/RoundRobin load balancing logic
public class RoundRobinLoadBalancer implements LoadBalancer {
    private final AtomicInteger index = new AtomicInteger(0);

    public ServiceInstance choose(List<ServiceInstance> instances) {
        int idx = index.getAndIncrement() % instances.size();
        return instances.get(idx);
    }
}
```

## Circuit Breaker States
```
CLOSED (normal) -> OPEN (failure threshold exceeded) -> HALF_OPEN (testing)
1. CLOSED: requests pass through normally
2. OPEN: requests fail fast without calling service
3. HALF_OPEN: limited requests allowed to test recovery
4. On success -> CLOSED; On failure -> OPEN again
```

## Resilience4j Internals
```java
// Sliding window for failure rate calculation
public class SlidingWindowCircuitBreaker {
    private final RingBitSet bitSet;  // tracks success/failure
    private final int windowSize;     // e.g., 100 requests
    private final float threshold;    // e.g., 50% failure rate

    public boolean isCallPermitted() {
        if (state == State.OPEN) {
            return checkRecoveryTimeout();
        }
        return true;
    }
}
```
