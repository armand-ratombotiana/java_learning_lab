# GUIDE — Load Balancing

## Step 1: Backend Pool
```java
public record Backend(String id, String host, int port, int weight, boolean healthy) {}
```

## Step 2: Load Balancing Algorithms
```java
public interface LbAlgorithm {
    Backend select(List<Backend> backends, String clientKey);
}
```
Implement RoundRobin, LeastConnections, ConsistentHash, WeightedRoundRobin.

## Step 3: Health Checks
```java
HealthChecker checker = new HealthChecker(pool, intervalSec, timeoutSec);
checker.start(); // periodic TCP/HTTP health probes
```

## Step 4: Session Persistence
- Cookie-based: Set-Cookie with backend identifier
- IP hash: deterministic backend selection by client IP
- Consistent hashing: minimal disruption on pool changes

## Step 5: Layer 7 Reverse Proxy
- Parse HTTP request headers
- Forward to selected backend
- Handle X-Forwarded-For, X-Real-IP

## Step 6: Exercises
1. Implement weighted least-connections algorithm
2. Build a consistent hash ring with virtual nodes
3. Create a circuit breaker that removes failing backends
