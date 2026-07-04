# Debugging Cache Issues

## Cache Hit Ratio Analysis
```java
public class CacheMonitor {
    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();
    
    public void recordHit() { hits.incrementAndGet(); }
    public void recordMiss() { misses.incrementAndGet(); }
    
    public double hitRate() {
        long total = hits.get() + misses.get();
        return total == 0 ? 0 : (double) hits.get() / total;
    }
}
```

## Common Issues
- Low hit ratio: check key distribution, TTL settings
- High eviction rate: increase capacity or optimize access patterns
- Stale data: verify invalidation mechanism
- Memory pressure: monitor heap usage, adjust eviction
