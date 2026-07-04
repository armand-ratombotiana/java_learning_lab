# Scalability - MATH FOUNDATION

## Amdahl's Law (Parallel Speedup)

```
Speedup = 1 / ((1 - P) + P/N)
```
Where `P` = parallelizable portion, `N` = processors.

If 90% of a task is parallelizable, 10 cores gives:
```
Speedup = 1 / (0.1 + 0.9/10) = 1 / 0.19 ≈ 5.26x
```
Not 10x! The serial portion limits gains.

## Little's Law (Queueing)

```
L = λ × W
```

- `L` = average number of requests in system
- `λ` = arrival rate (requests/second)
- `W` = average time per request (seconds)

Example: 1000 req/s, 200ms average processing:
```
L = 1000 × 0.2 = 200 concurrent requests
```

## Universal Scalability Law (USL)

```
C(N) = N / (1 + α(N-1) + βN(N-1))
```

Where:
- `N` = number of nodes
- `C(N)` = relative capacity
- `α` = contention coefficient
- `β` = coherence coefficient

With no contention (α=0, β=0): `C(N) = N` (linear)
With real overhead: scaling flattens after some point.

## Database Scaling Math

### Read Replica Throughput
```
T_total = T_master + N_replicas × T_replica_each
```
Reads scale linearly with replicas. Writes limited to master.

### Sharding Distribution
To achieve even distribution of M rows across N shards:
```
Rows_per_shard ≈ M / N
```
Variance increases when shard key has low cardinality.

## Capacity Planning

### Request Rate Estimation
```
Peak RPS = DAU × Requests_per_session / Peak_seconds
```
- DAU = Daily Active Users
- Peak seconds = usually 3600-7200 (1-2 hour window)

### Resource Calculation
```
Instances_needed = Peak_RPS / RPS_per_instance
```
With 50% headroom: `Instances_needed × 1.5`

## Caching Math

### Cache Hit Ratio
```
Effective_latency = hit_rate × cache_latency + (1 - hit_rate) × db_latency
```

With 95% cache hit, 1ms cache, 50ms DB:
```
Effective = 0.95 × 1 + 0.05 × 50 = 0.95 + 2.5 = 3.45ms
```
10x improvement over 50ms DB-only.
