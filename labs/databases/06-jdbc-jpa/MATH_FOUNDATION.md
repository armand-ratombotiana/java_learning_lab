# Math Foundation: JDBC & JPA

## Complexity

- **JDBC batch insert**: O(N) network round-trips without batching, O(N/B) with batch size B
- **JPA flush**: O(N) dirty checks per flush (N = managed entities)
- **N+1 problem**: 1 query + N additional queries = O(N) queries instead of O(1)
- **Pessimistic lock**: Blocks other transactions until release (queue-based)

## Connection Pool Sizing

### Formula (based on QPS and latency)
```
connections = (max_requests_per_second × avg_query_time_ms) / 1000 + spare
```

### Common formula (pool ≤ CPU cores × 2 + effective_spindle_count)
```
For SSD: pool = cpu_cores × 2 + 1
For HDD: pool = cpu_cores × 4 + 1
```

### PostgreSQL max_connections
```
max_connections ≈ (RAM - shared_buffers - OS_mem) / work_mem
```

## Transaction Overhead

- **Begin/commit**: 1 round-trip each + WAL flush
- **Long transactions**: Bloat MVCC dead tuples, lock contention
- **Deadlock detection**: Transaction graph cycle detection (O(V+E))

## Hibernate Batch Size

```
optimal_batch_size = (target_flush_time_ms × throughput_per_connection)
                     / avg_statement_time_ms
```

Typical: 20-50 for inserts, 10-30 for updates

## Second-Level Cache Hit Ratio
```
hit_ratio = cache_hits / (cache_hits + cache_misses)
```
- Hit ratio > 90%: cache well-configured
- Hit ratio < 70%: review caching strategy

## JPQL Query Cost
```
query_cost = entity_fetch_cost + association_fetch_cost + filter_cost
```
- `JOIN FETCH` eliminates N additional queries but may return Cartesian products
- `COUNT` queries should avoid fetching all columns
