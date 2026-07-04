# Common Mistakes with Distributed Caching

## 1. Cache Stampede
When many concurrent requests miss cache simultaneously and all hit the database. Solution: request coalescing, early recomputation.

## 2. Thundering Herd
Multiple cache nodes all expire the same key at once. Solution: jitter TTL values.

## 3. Stale Cache After Write
Write-through missing or not implemented. Solution: always invalidate cache on write.

## 4. Cache Penetration
Requests for non-existent keys always miss cache and hit DB. Solution: cache null values.

## 5. Over-Caching
Caching everything without consideration of access patterns wastes memory.

## 6. Hotspot Keys
Popular keys overload single nodes. Solution: replicate hot keys, use local caching.
