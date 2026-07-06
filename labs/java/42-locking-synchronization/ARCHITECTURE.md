# Architecture of Java Locking

## Lock Type Comparison
```
Lock Type        | Reentrant | Try | Interruptible | Fair | Condition | Optimistic
synchronized     | Yes       | No  | No            | No   | No (wait) | No
ReentrantLock    | Yes       | Yes | Yes           | Yes  | Yes       | No
ReentrantReadWriteLock | Yes | Yes | Yes     | Yes  | Yes       | No
StampedLock      | No (write)| Yes | Yes          | Approx | No     | Yes
```

## AQS Architecture
```
AbstractQueuedSynchronizer
├── state (volatile int)
├── head, tail (Node references)
├── exclusiveOwnerThread
├── tryAcquire(int) — must override
├── tryRelease(int) — must override
├── tryAcquireShared(int) — for shared modes
├── tryReleaseShared(int) — for shared modes
└── acquire/release — public API
```

## CLH Queue Node
```
Node
├── waitStatus (int): SIGNAL, CANCELLED, CONDITION, PROPAGATE
├── prev (Node)
├── next (Node)
├── thread (Thread)
└── nextWaiter (Node — for Condition queue)
```

## LockSupport Architecture
```
LockSupport
├── setBlocker(Thread, Object) — records blocker for monitoring
├── park() — block until permit available
├── parkNanos(long) — timed park
├── parkUntil(long) — absolute deadline park
├── unpark(Thread) — make permit available
└── getBlocker(Thread) — diagnostic access
```

## StampedLock Modes
```
StampedLock
├── tryOptimisticRead() → long (stamp)
├── validate(long) → boolean
├── readLock() → long
├── writeLock() → long
├── tryConvertToWriteLock(long) → long
├── tryConvertToReadLock(long) → long
└── unlock(long)
```
