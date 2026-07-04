# Architecture Patterns - MATH FOUNDATION

## Layered Architecture Complexity

### Communication Overhead
For `n` layers processing a request, the overhead is `O(n)` for a single request, but `O(n × m)` for m requests due to context switching.

Total latency per request:
```
L_total = L_presentation + L_business + L_data + L_transport
```

### Fan-Out in Microservices

### Request Fan-Out
If service A calls B and C, and each of those calls D, the total number of calls for one request is:
```
f = 1 + s + s × d
```
Where `s` = number of downstream services from root, `d` = average downstream calls per service.

### Failure Probability
Given per-service reliability `R`, system reliability for a chain of `n` services:
```
R_system = R^n
```
With `R = 0.99` and `n = 10`: `R_system = 0.904` — 10% chance of failure per request.

## Event-Driven Throughput

### Queueing Theory (Little's Law)
```
L = λ × W
```
Where `L` = average events in queue, `λ` = arrival rate, `W` = average processing time.

To avoid backpressure: `capacity > λ × W_peak`

### Partition Count Trade-off
For Kafka, throughput scales with partition count `p`:
```
Throughput = min(p × consumer_throughput, broker_throughput)
```
More partitions = higher throughput but more rebalancing overhead.

## CQRS Consistency Window

### Replication Lag
The time between write and read consistency:
```
t_lag = t_event_store + t_projection + t_network
```
For typical setups, `t_lag` ranges from 50ms (synchronous) to several seconds (asynchronous batch).

### Query Optimization
Read model query time for denormalized data:
```
T_query ≈ O(1) or O(log n)  (index lookup)
```
vs normalized join queries:
```
T_query ≈ O(m × log n)  (m = number of joins)
```
