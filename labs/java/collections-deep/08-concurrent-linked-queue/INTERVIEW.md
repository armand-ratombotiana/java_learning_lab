# Interview Questions: ConcurrentLinkedQueue

## Company-Specific Focus

### Google
- ConcurrentLinkedQueue: unbounded, lock-free, FIFO queue
- Michael-Scott algorithm: non-blocking linked list queue
- CAS operations: compareAndSet on head and tail nodes

### Microsoft
- ConcurrentLinkedQueue vs ConcurrentQueue in .NET: lock-free queue comparisons
- Weakly consistent iterator: snapshot vs live iteration

### Amazon
- Work queue design: ConcurrentLinkedQueue for producer-consumer patterns
- No blocking operations: poll returns null when empty, not block

### Meta
- Lock-free algorithm benefits: no context switching, no contention
- ABA problem: how ConcurrentLinkedQueue avoids it (optimistic updates)
- Memory management: node garbage collection after removal

### Apple
- Use cases: thread pools, event queues, task scheduling
- Iterator: weakly consistent, safe for concurrent traversal
- Size(): O(n) traversal — not constant time

### Oracle
- ConcurrentLinkedQueue specification: lock-free, thread-safe queue
- Node: item and next reference, both volatile
- tail update: optimistic CAS with slack

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 232 Implement Queue using Stacks | Easy | Apple, Google, Amazon | Queue design principles |
| 622 Design Circular Queue | Medium | Amazon, Google, Apple | Fixed-size queue design |
| 641 Design Circular Deque | Medium | Amazon, Google | Deque design |
| 346 Moving Average from Data Stream | Easy | Google, Amazon | Queue-based sliding window |

## Real Production Scenarios
- **Netflix**: ConcurrentLinkedQueue as a work queue for parallel video transcoding tasks
- **LinkedIn**: Using ConcurrentLinkedQueue for non-blocking message passing between services
- **Twitter**: Event bus implementation using ConcurrentLinkedQueue

## Interview Patterns & Tips
- **poll() returns null**: for non-blocking empty queue handling
- **peek() vs poll()**: peek reads head without removing, poll removes
- **add() vs offer()**: add throws IllegalStateException on capacity bound (queue is unbounded)
- **Weakly consistent iterator**: may or may not reflect elements added after creation

## Deep Dive Questions
- **Michael-Scott algorithm**: How does the lock-free queue work?
- **CAS**: How are CAS operations used to update head and tail?
- **Slack**: Why does ConcurrentLinkedQueue allow tail to lag behind?
- **Node**: How is the node structure designed?
- **GC**: How does the garbage collector interact with concurrent removal?