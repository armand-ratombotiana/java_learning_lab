# Math Foundation: Distributed Cache

## Consistent Hashing
- Number_of_keys_moved_on_add = K / N (average, where K = keys, N = nodes)
- Virtual nodes reduce std deviation of load from O(N) to O(sqrt(V)) where V = virtual nodes

## LRU Efficiency
- Hit rate increases with cache size, diminishing returns after ~20% of working set
- Working set size estimation: monitor miss rate over sliding window

## Replication Consistency
- W + R > N for strong consistency (W = write quorum, R = read quorum, N = replicas)
- W = 2, R = 2, N = 3: write to 2 replicas, read from 2 replicas -> strong consistency

## Gossip Convergence
- Convergence time: O(log N) rounds
- Each round: node exchanges with 1-3 random peers
- Suspicion timeout: delta = configurable (default 5s), phi = -log10(1 - P(no heartbeat))
