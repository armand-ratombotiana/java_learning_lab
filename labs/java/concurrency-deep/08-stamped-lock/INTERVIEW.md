# Interview Questions: StampedLock

## Company-Specific Focus

### Google
- StampedLock: ReadWriteLock with optimistic read mode
- Optimistic read: tryOptimisticRead returns a stamp, validate later
- Three modes: reading, writing, optimistic reading

### Microsoft
- StampedLock vs C# ReaderWriterLockSlim: optimistic reads are unique to Java
- Conversion: tryConvertToWriteLock, tryConvertToReadLock

### Amazon
- Optimistic reads for read-heavy workloads with low write contention
- Validation: boolean validate(long stamp) checks if write occurred since stamp
- Performance: optimistic reads avoid blocking entirely

### Meta
- StampedLock not reentrant: can cause deadlock if reentrant usage
- tryOptimisticRead: no blocking, but must validate
- Read vs optimistic: use read lock when data must be consistent

### Apple
- Memory ordering: StampedLock ensures happens-before relationships
- tryConvertToWriteLock: upgrade read to write lock atomically

### Oracle
- java.util.concurrent.locks.StampedLock specification
- Three access modes: read, write, optimistic read
- Reentrancy: StampedLock is NOT reentrant
- Lock conversion: upgrade/downgrade between modes

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (StampedLock patterns apply to concurrent data structure design) |

## Real Production Scenarios
- **Netflix**: StampedLock for a read-heavy configuration cache — optimistic reads eliminated contention entirely
- **LinkedIn**: Point-in-time data snapshot with optimistic reads — validation retry pattern

## Interview Patterns & Tips
- **Optimistic read**: Non-blocking, but must re-read if write occurred during operation
- **validate**: checks that no write happened since the stamp was acquired
- **Conversion**: upgrade from optimistic to read, or read to write
- **Not reentrant**: reentrant usage causes deadlock

## Deep Dive Questions
- **Optimistic read**: How does tryOptimisticRead work without blocking?
- **Validation**: What does validate() check internally?
- **Conversion**: How does tryConvertToWriteLock atomically upgrade?
- **CLH queue**: Does StampedLock use a CLH queue like AQS?
- **Performance**: How much faster is an optimistic read vs a read lock?