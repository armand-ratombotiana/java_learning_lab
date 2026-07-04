# Scalability - REFLECTION

## Key Takeaways

1. **Start with vertical**: Many applications never need horizontal scaling. A well-optimized monolith handles a lot.

2. **Stateless is the goal**: State is the enemy of horizontal scaling. Push state to dedicated stores (Redis, DB).

3. **Measure first**: Without metrics, you're guessing. Profile CPU, memory, IO, and network before optimizing.

4. **Each scale step adds complexity**: Read replicas require replication lag monitoring. Sharding requires rebalancing logic. Only adopt when metrics justify it.

## Self-Assessment Questions

- Can I identify the bottleneck in a slow system?
- Do I know the difference between auto-scaling and load balancing?
- Can I design a stateless service?
- Do I understand when to use read replicas vs sharding?

## Misconceptions Addressed

- "Horizontal scaling is always better" — False: it adds latency and complexity
- "More servers = linear improvement" — False: Amdahl's law limits gains
- "Caching solves everything" — False: stale data, invalidation complexity

## Next Steps

- Practice profiling with JProfiler/Async Profiler
- Study AWS/GCP/Azure auto-scaling docs
- Implement a sharded data store
- Read "Designing Data-Intensive Applications" by Martin Kleppmann
