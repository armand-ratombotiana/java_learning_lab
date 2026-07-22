# Interview Questions: CopyOnWriteArrayList

## Company-Specific Focus

### Google
- CopyOnWriteArrayList: thread-safe variant where mutative operations create a new copy
- Copy-on-write semantics: all mutative operations (add, set, remove) create a new underlying array
- Thread-safe iteration: snapshot-based, no ConcurrentModificationException

### Microsoft
- CopyOnWriteArrayList vs .NET collections: similar to immutable collections
- Performance characteristics: read O(n), write O(n) due to array copy
- Read-optimized: best for read-heavy, write-rare scenarios

### Amazon
- Use case: listener lists, subscriber registrations where reads dominate
- Memory overhead: each write creates a new array copy
- Snapshot iteration: iterators never throw ConcurrentModificationException

### Meta
- Atomic operations: uses ReentrantLock for serializing writes
- Volatile array reference: ensures visibility of new snapshot
- Snapshot iteration: safe even during concurrent modification

### Apple
- Use CopyOnWriteArrayList for small, frequently read collections
- No blocking reads: reads are lock-free
- Memory: high overhead during writes, good for low-write systems

### Oracle
- java.util.concurrent.CopyOnWriteArrayList specification
- Snapshot iterator: reflects state at creation time
- COW semantics: each mutative operation creates a fresh copy

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 341 Flatten Nested List Iterator | Medium | Apple, Google, Amazon | Snapshot iteration pattern |
| 284 Peeking Iterator | Medium | Google, Facebook | Thread-safe peek design |
| 251 Flatten 2D Vector | Medium | Google, Amazon, Apple | Snapshot iteration pattern |

## Real Production Scenarios
- **Netflix**: CopyOnWriteArrayList for subscriber lists in event bus — frequent reads, rare subscriber changes
- **LinkedIn**: Listener registry for notification service — CopyOnWriteArrayList prevented ConcurrentModificationException in callbacks
- **Uber**: CopyOnWriteArrayList for service registration — reads during hot path, writes during deployment only

## Interview Patterns & Tips
- **Read-optimized**: Best for collections where reads vastly outnumber writes
- **Snapshot iteration**: Safe but does not reflect subsequent modifications
- **Memory overhead**: Each write allocates a new array — avoid in write-heavy scenarios
- **Weakly consistent**: The iterator is a snapshot, not live view

## Deep Dive Questions
- **Array copy**: How does CopyOnWriteArrayList ensure atomic array replacement?
- **Locking**: Why doesn't it use synchronized for writes?
- **Iterator**: How does the iterator capture the snapshot?
- **Memory impact**: What is the memory behavior during concurrent writes?
- **Performance profile**: When does CopyOnWriteArrayList outperform synchronized list?