# Caching - MENTAL_MODELS

## Mental Model 1: The Kitchen Pantry
- **Refrigerator (L1 cache)**: Fast access, small capacity, frequent items
- **Pantry (L2 cache)**: Slower, larger, backup supply
- **Grocery store (Database)**: Slowest, unlimited, must drive there
- **Meal prep**: Pre-compute results (cache warming)

## Mental Model 2: The Library
- **Study desk**: Books you're currently reading (L1)
- **Checked-out books**: At home, faster than going to library (L2)
- **Library shelves**: All books, takes time to find (Database)
- **Book return**: Updates can make your copy stale (invalidation)

## Mental Model 3: The Newsstand
- **Hot items**: Today's newspaper, visible immediately (hot cache)
- **Weekly magazines**: Still relevant, a bit older (warm cache)
- **Archives**: Cold storage, takes time to retrieve (DB)
- **New edition**: Previous issues are now stale (TTL expiration)

## Caching Patterns at a Glance

| Pattern | Read Strategy | Write Strategy | Best For |
|---------|-------------|---------------|----------|
| Cache-Aside | Check cache, miss → query DB, populate cache | Direct DB write, invalidate cache | Read-heavy |
| Read-Through | Cache proxies DB reads | Direct DB write | Simple read-through |
| Write-Through | Cache proxies DB reads | Cache updates first, then DB | Write-heavy, data integrity |
| Write-Behind | Cache proxies DB reads | Cache updates, async DB | High write throughput |
| Refresh-Ahead | Cache refreshes before TTL expires | Direct DB write | Predictable access patterns |

## The Cache Spectrum

```
FAST ◄────────────────────────────────────────────────► SLOW
SMALL ◄──────────────────────────────────────────────► LARGE

L1 CPU Cache   L2 CPU Cache    RAM         SSD         Network      HDD
(32KB, 1ns)    (256KB, 5ns)    (64GB, 100ns) (1TB, 10μs) Storage(50ms) (10ms-100ms)
         │                                       │
         │          ┌────────────────┐            │
         └──────────│ Application     │────────────┘
                    │ Cache (Caffeine)│
                    │ (10GB, 0.1ms)  │
                    ├────────────────┤
                    │ Redis Cache    │
                    │ (50GB, 1ms)   │
                    └────────────────┘
```
