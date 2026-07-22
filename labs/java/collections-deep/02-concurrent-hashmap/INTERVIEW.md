# Interview Questions: ConcurrentHashMap

## Company-Specific Focus

### Google
- ConcurrentHashMap: lock striping (Java 7) vs CAS + synchronized (Java 8+)
- Segment-based locking (Java 7): 16 segments by default, each with its own lock
- Java 8+ redesign: Node array with CAS for inserts, synchronized for bin collisions

### Microsoft
- ConcurrentHashMap vs ConcurrentDictionary in .NET: comparison of lock-free strategies
- Thread-safe iteration: weakly consistent iterator vs fail-fast

### Amazon
- ConcurrentHashMap for high-throughput caching: no global lock, scalable writes
- computeIfAbsent: atomic lazy initialization for cache entries
- Performance: scales linearly with thread count under concurrent writes

### Meta
- CAS operations: tabAt, casTabAt, setTabAt for lock-free updates
- Tree bins: converting linked lists to TreeNodes under collision
- Resize: multi-threaded resize with helpTransfer

### Apple
- ConcurrentHashMap vs Collections.synchronizedMap: why prefer CHM
- Size() method: summing counter cells for contention-aware counting

### Oracle
- java.util.concurrent.ConcurrentHashMap specification
- Lock striping (Java 7) vs CAS-based (Java 8+)
- ForwardingNode, ReservationNode internal node types
- CounterCell for high-contention counting

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 146 LRU Cache | Medium | Google, Amazon, Apple | Thread-safe cache design |
| 380 Insert Delete GetRandom O(1) | Medium | Amazon, Google | Thread-safe insert/delete |
| 895 Maximum Frequency Stack | Hard | Amazon, Google | Thread-safe frequency tracking |

## Real Production Scenarios
- **Netflix**: ConcurrentHashMap with computeIfAbsent in a lambda causing recursive computation issues
- **LinkedIn**: Migrating from synchronizedMap to ConcurrentHashMap reduced lock contention by 90%
- **Uber**: ConcurrentHashMap resize with helpTransfer prevented single-threaded rehashing bottleneck

## Interview Patterns & Tips
- **computeIfAbsent**: Atomic lazy initialization — the function runs at most once
- **Weakly consistent iterator**: reflects state at creation time, may miss or include subsequent modifications
- **Size estimation**: mappingCount() returns long, size() returns int
- **ConcurrentHashMap does not allow null keys or values**

## Deep Dive Questions
- **Java 7 vs 8**: What are the key differences in ConcurrentHashMap implementation?
- **CAS**: How does ConcurrentHashMap use compare-and-swap for updates?
- **Resize**: How does multi-threaded resizing work with helpTransfer?
- **TreeBin**: How does ConcurrentHashMap handle tree bins under concurrent access?
- **CounterCell**: How does ConcurrentHashMap count elements under high contention?