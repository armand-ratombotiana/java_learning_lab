# Caching - MATH FOUNDATION

## Cache Hit Ratio

### Single Object Access
For object `i` accessed `f_i` times in a period:
```
total_accesses = Σ f_i
cache_hits = Σ min(f_i, 1) for objects in cache
hit_ratio = cache_hits / total_accesses
```

### Working Set Size
Given `N` cache slots and request distribution following Zipf's law:
```
P(i) = 1 / (i^α × H_N)  ← Zipf distribution
hit_ratio ≈ access_count(N) / total_access_count
```

For a typical Zipf(α=1.0) distribution: Top 10% of objects receive ~50% of requests.

### Optimal Cache Size
```
Cache size = working_set_size × (1 + headroom%)
For 95% hit rate + 20% headroom:
  Cache = WSS × 1.2
```

## Latency Improvement

### Effective Latency
```
E = H × L_cache + (1 - H) × (L_db + L_cache)
  = L_cache + (1 - H) × L_db

Where H = hit ratio
```

Example: L_cache = 1ms, L_db = 100ms, H = 0.95
```
E = 1 + 0.05 × 100 = 6ms (16x improvement)
```

## Cache Size Calculations

### Object Size Budget
```
max_objects = cache_memory / avg_object_size
```

For 8GB cache, 10KB average objects:
```
max_objects = 8GB / 10KB = 800,000 objects
```

### Memory Overhead
```
Total_memory = Σ object_size + key_size + metadata + overhead
```

Redis overhead per key: ~150 bytes + key name + value

### Serialization Size Comparison
| Format | Product | Size | Notes |
|--------|---------|------|-------|
| JSON | {id:1, name:"P"} | 120 bytes | Readable |
| Protocol Buffers | \x12\x05Product | 45 bytes | Compact |
| Java Serialization | \xAC\xED... | 200+ bytes | Avoid |

## Throughput Improvement

### Cache vs Database Throughput
```
hits_per_second = max_cache_qps × hit_ratio
db_offload = db_qps_without_cache - db_qps_with_cache
```

### Cache Node Vertical Scaling
```
max_ops_per_node = (total_ops / nodes) × cluster_hit_ratio
```

Redis single-threaded: ~100K ops/sec per node
Redis cluster: ~100K × N nodes

## Caching Economics

### Cost-Benefit
```
Cost_benefit = (DB_queries_saved × DB_cost_per_query) - Cache_cost

DB_cost = instance_hours × hourly_rate
Cache_cost = nodes × instance_cost + maintenance
```

Break-even: DB_queries_saved × DB_cost_per_query > Cache_cost

## TTL Optimization

### Optimal TTL
```
For cache with refresh cost C_refresh and miss penalty P_miss:
optimal_ttl ≈ sqrt(2 × C_refresh / P_miss × λ)
Where λ = update rate of objects
```

### Refresh Strategy
```
If time_to_expiry < threshold AND request arrives:
  refresh_asynchronously()  // Serve stale + refresh in background
Else:
  sync_load()  // Block and reload
```
