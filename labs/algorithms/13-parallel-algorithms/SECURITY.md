# Security — Parallel Algorithms

- Thread exhaustion: Creating too many threads can DoS the system
- Shared state: Race conditions can lead to incorrect results
- Data races: Unsynchronized mutable state is undefined behavior
- Deadlock: Improper fork/join ordering can cause deadlocks
- Resource leak: Not shutting down ForkJoinPool can prevent GC
- Mitigation: Use common pool, immutable data, proper synchronization
