# Mini Project: Read-Write Lock from AQS

## Objective
Implement a reusable read-write lock using AbstractQueuedSynchronizer (AQS). Your lock must support multiple concurrent readers, exclusive writers, and downgrading from write to read lock.

## Requirements
1. Implement `ReadWriteAqsLock` with `readLock()` returning a `Lock` and `writeLock()` returning a `Lock`
2. Multiple threads can hold the read lock simultaneously
3. Write lock is exclusive — only one writer, no readers while writing
4. Support `tryLock()` with timeout for both read and write locks
5. Support lock downgrading: writer acquires read lock before releasing write lock

## Internal Design
- Use `AbstractQueuedSynchronizer` state field:
  - Upper 16 bits: read lock count
  - Lower 16 bits: write lock count (0 or 1)
- Override `tryAcquire`, `tryRelease`, `tryAcquireShared`, `tryReleaseShared`

## Test Scenarios
1. 10 reader threads reading concurrently — verify no blocking
2. Writer blocks when reader holds lock
3. Reader blocks when writer holds lock
4. 5 readers + 1 writer + 5 more readers — verify writer gets priority
5. Downgrade from write to read succeeds without deadlock

## Deliverables
- `ReadWriteAqsLock.java` with full implementation
- Comprehensive JUnit 5 test suite
- Benchmark comparing against `ReentrantReadWriteLock`
