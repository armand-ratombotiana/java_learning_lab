# Distributed Locking: Mathematical Foundation

## Safety and Liveness

### Mutual Exclusion (Safety)
At most one process holds the lock at any time.

∀ t: |{p | holds_lock(p, t)}| ≤ 1

### Deadlock Freedom (Liveness)
If no process holds the lock, some process eventually acquires it.

∀ t, ∃ t' > t: ¬∃ p holds_lock(p, t') → ∃ q holds_lock(q, t')

## Redlock Analysis (Martin Kleppmann)

### Redlock Safety
P(safety violation) = P(lock expired before holder released)

For N=5, majority=3:
P(violation) = P(3+ nodes have incorrect timing)

If clock drift between nodes can be > lease_duration:
Safety cannot be guaranteed.

## ZooKeeper Lock Analysis

### Safety
ZooKeeper sequential znode provides strict total order.
With linearizable reads (sync), mutual exclusion is guaranteed.

### Performance
Lock acquisition time = 1 RTT (best case, if first)
= N/2 RTTs (average, waiting for predecessors)

### Availability
With ZooKeeper ensemble of 2f+1 nodes:
P(lock available) = P(ZK cluster available) = 1 - P(f+1 nodes fail)
