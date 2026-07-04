# Caching - WHY IT MATTERS

## Business Impact

### Performance Gains
| Approach | Latency | Throughput |
|----------|---------|-----------|
| Direct DB query | 50ms | 5,000 QPS |
| Local cache (Caffeine) | 0.1ms | 500,000 QPS |
| Distributed cache (Redis) | 1ms | 100,000 QPS |
| CDN cache | 10ms (edge) | Unlimited |

### Cost Savings
- 1 Redis node (6GB) = ~$15/month vs 1 RDS instance (db.r5.large) = ~$175/month
- Cache reduces DB instances by 50-80%
- Reduced compute for serving cached responses

## Key Reasons It Matters

### 1. User Experience
Sub-second page loads require caching. Amazon: 100ms delay = 1% revenue loss. Google: 500ms delay = 20% traffic drop.

### 2. Database Protection
Spikes (Black Friday, product launches) will overload databases without caching.

### 3. Cost Optimization
Caching is 10-100x cheaper per request than database reads.

### 4. Global Performance
CDNs bring content to the edge, reducing latency for international users.

### 5. Predictable Performance
Cached responses have consistent latency (no query optimization variance).

## Real-World Examples
- **Twitter**: Redis caches timelines, reduces DB reads by 90%
- **Stack Overflow**: 90% of page views served from cache
- **GitHub**: Redis cluster handles 1M+ requests/second for session data
- **Netflix**: EVCache (Ephemeral Volatile Cache) serves 100B+ operations/day
