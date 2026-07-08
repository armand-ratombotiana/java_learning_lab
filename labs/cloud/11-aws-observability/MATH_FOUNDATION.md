# Math Foundation - AWS Observability

### 1. Probability and Statistics
Understanding request distributions and percentile calculations:
- **p50/p95/p99**: Latency percentiles for SLAs
- **Error Budget**: 1 - (error rate target) over time window
- **Exponential Backoff**: wait = min(cap, base * 2^attempt) + random

### 2. Queueing Theory
For capacity planning and load management:
- **Little's Law**: L = lambda * W (avg requests = arrival rate * avg time)
- **M/M/1 Queue**: Average wait time = lambda / (mu * (mu - lambda))
- **Erlang Distribution**: For multi-server systems

### 3. Rate Limiting Math
Token bucket algorithm refill: tokens = min(capacity, tokens + elapsed * refillRate)

### 4. Circuit Breaker State Machine
Closed -> failure threshold -> Open -> timeout -> Half-Open -> success/failure

### 5. Capacity Planning Formula
Instances = (peak_rps * avg_response_time) / (instance_capacity * utilization_target)
