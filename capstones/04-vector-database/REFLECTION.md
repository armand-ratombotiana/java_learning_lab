# Reflection: Vector Database

## What I Learned
- Implementing HNSW from scratch teaches deep understanding of graph-based ANN search
- Memory layout and cache efficiency matter enormously for performance
- Trade-offs between recall, latency, memory, and throughput
- The complexity of concurrent index structures

## Challenges
- Getting HNSW construction parameters right for high recall (> 95%)
- Debugging graph fragmentation (isolated nodes that never get searched)
- Performance tuning: avoiding boxing, using primitive collections
- Implementing crash-consistent WAL without fsync on every write

## What I'd Do Differently
- Start with brute-force kNN as baseline for correctness testing
- Use JMH for microbenchmarks on hot paths (distance calculation)
- Build the metadata filter engine as a separate concern earlier
- Add property-based testing (jqwik) for graph invariants
