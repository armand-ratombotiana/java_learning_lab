# Consistency Models - MATH FOUNDATION

## CAP Theorem Formalization

### Theorem Statement
In an asynchronous network model, a shared data object cannot guarantee all three:
- **Consistency**: All reads return the most recent write
- **Availability**: Every request receives a response (not necessarily latest)
- **Partition Tolerance**: System continues operating despite network partitions

Gilbert & Lynch (2002) proved: During a partition, either consistency or availability must be abandoned.

## Quorum Math

### W + R > N Inequality
For a system with N replicas:
- **W**: Minimum replicas that must confirm a write
- **R**: Minimum replicas that must respond to a read

Strong consistency condition: `W + R > N`
Proof: Any read quorum intersects any write quorum by at least one replica, guaranteeing the read sees the latest write.

### Fault Tolerance
```
Max tolerable failures:
- Write failures: N - W
- Read failures:  N - R
- Any failures:   min(N - W, N - R, W - 1, R - 1)
```

With N=5, W=3, R=3: Tolerates 2 failures.

## Replication Lag

### Synchronous Replication Latency
```
T_write = T_local + max(T_replica1, T_replica2, ..., T_replicaN) + T_commit
```

### Asynchronous Replication Lag
```
Lag = T_arrival_replica - T_commit_master
```

For geo-distributed replicas, lag = propagation delay + queue delay.

### 95th Percentile Lag
For replicas in the same region: `P95_lag < 50ms`
Cross-continent: `P95_lag < 500ms`

## Linearizability Cost

### Coordination Overhead
```
Throughput_linearizable = Throughput_single_node / N
```
For N=3: Approximately 1/3 of single-node throughput (in practice, 40-60% due to batching).

### Spanner TrueTime
```
t_absolute = t_local ± epsilon  (epsilon = 1-7ms for GPS + atomic clocks)
commit_wait = 2 * epsilon  (waits for uncertainty window to pass)
```

## Eventual Consistency Convergence

### Conflict Probability
With N replicas and conflicting writes occurring simultaneously:
```
P(conflict) = 1 - (write_rate × window_size)^(1/N)
```
For high write rates, conflict probability approaches 1 — resolution strategy becomes critical.

### Convergence Time
```
T_converge ≈ latency_to_furthest_replica × log(N)
```
LWW (Last Writer Wins) converges in ~1 round trip. CRDTs converge immediately at read time.
