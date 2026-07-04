# Caching - REFLECTION

## Key Takeaways

1. **Measure before caching**: Profile which queries are actually slow. Don't cache everything.

2. **Caching adds complexity**: Consistency, invalidation, and cache warming are non-trivial. Add caching only when performance data justifies it.

3. **Multi-layer cache**: L1 (local) for hot data, L2 (distributed) for shared warm data. Each layer serves a different purpose.

4. **Invalidation is the hardest problem**: "There are only two hard things in computer science: cache invalidation and naming things."

## Self-Assessment Questions

- Can I explain the difference between cache-aside, read-through, write-through, and write-behind?
- Do I know when to use local cache vs distributed cache?
- Can I design an invalidation strategy?
- Do I understand how to prevent cache stampede?

## Common Misconceptions

- "More cache = better" — False: cache management overhead, memory pressure, stale data risk
- "Cache solves all performance problems" — False: some bottlenecks are IO, CPU, or network
- "Redis is just a cache" — Redis is a data structure store, useful for much more than caching
- "TTL solves invalidation" — TTL controls recency, not correctness; stale data may be served

## Next Steps

- Profile a real application to identify caching opportunities
- Set up Redis Sentinel or Redis Cluster for high availability
- Implement multi-layer caching in a real project
- Read "Redis in Action" by Josiah L. Carlson
