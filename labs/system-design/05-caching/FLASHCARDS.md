# Caching Strategies - FLASHCARDS

### Card 1
**Q:** Cache-aside pattern?
**A:** Check cache first, load from DB on miss, populate cache.

### Card 2
**Q:** Write-through?
**A:** Write to DB and cache simultaneously. Cache always current.

### Card 3
**Q:** Write-behind (write-back)?
**A:** Write to cache, async flush to DB. Fast but risky.

### Card 4
**Q:** Read-through?
**A:** Cache automatically loads from DB on miss.

### Card 5
**Q:** TTL?
**A:** Time To Live - cache expiration time.

### Card 6
**Q:** Cache warming?
**A:** Preloading hot data on application startup.

### Card 7
**Q:** Eviction policy LRU?
**A:** Least Recently Used - evict oldest accessed item.

### Card 8
**Q:** Cache stampede?
**A:** Multiple requests hit cache miss simultaneously.

**Total: 8 flashcards**