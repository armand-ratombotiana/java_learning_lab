# Distributed Caching: Flashcards

## Front: What is cache-aside?
**Back**: App reads cache; on miss, loads from DB and populates cache.

## Front: What is write-through?
**Back**: Writes go through cache to DB synchronously.

## Front: What is write-behind?
**Back**: Cache accepts writes, async persists to DB.

## Front: What is cache stampede?
**Back**: Many concurrent cache misses all hit DB simultaneously.

## Front: How does Redis shard?
**Back**: CRC16(key) % 16384 → assigned to specific node.

## Front: What is consistent hashing?
**Back**: Hash ring where node add/remove only affects neighboring keys.

## Front: Common eviction policies?
**Back**: LRU, LFU, TTL, FIFO.

## Front: What is cache penetration?
**Back**: Requests for non-existent keys always miss and hit DB.
