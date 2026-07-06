# Tests for Locking & Synchronization

## Test Categories

### Unit Tests (LockingTest.java)
- `testAqsLockExclusion()` — validates mutual exclusion with 5 threads incrementing a shared counter 1000 times each (expected: 5000)
- `testReentrantLockFairness()` — verifies fair lock hold count and reentrancy
- `testCasCounter()` — validates atomic increment across 5 threads × 500 iterations
- `testLockSupport()` — verifies park/unpark thread lifecycle

### Running Tests
```bash
mvn test -pl labs/java/42-locking-synchronization
```

### Additional Test Ideas
- StampedLock: validate optimistic read consistency under concurrent writes
- ReentrantLock: test lockInterruptibly with thread interruption
- ReentrantLock: test tryLock timeout expiration
- AqsLock: test reentrancy (locking twice from same thread)

### Test Design Principles
- All tests are deterministic (no sleep-based timing)
- Thread.join() with timeouts prevents hung tests
- Counter-based tests verify thread safety statistically
- Each test isolates a single locking mechanism

### Coverage Targets
- AQS: acquire/release, exclusive mode
- ReentrantLock: fair, unfair, tryLock, lockInterruptibly
- StampedLock: optimistic read, conversion
- LockSupport: park, unpark, blocker
- CAS: atomic increment correctness
