# Architecture of LRU Cache

## Integration Patterns

`
Application
  â†’ CacheService
    â†’ LRUCache<K, V>
      â†’ Node<K, V> (Doubly Linked List)
      â†’ HashMap<K, Node<K, V>>
`

## Design Patterns Used

1. **Strategy**: Pluggable eviction policy (LRU, LFU, FIFO)
2. **Decorator**: Add TTL, statistics, persistence
3. **Factory**: Create caches with different policies
4. **Proxy**: Cache-aside pattern with database backing
