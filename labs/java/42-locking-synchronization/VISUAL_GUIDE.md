# Visual Guide to Locking

## Lock State Transitions
```
synchronized(obj) {
    Entry:
    ├── Biased: thread matches bias → enter immediately
    ├── Thin: CAS on mark word succeeds → enter
    └── Inflated: OS mutex → block
    Exit:
    ├── Biased: no change (bias remains)
    ├── Thin: CAS to release mark word
    └── Inflated: OS mutex unlock
}
```

## AQS CLH Queue
```
  Head                           Tail
   ↓                              ↓
  Node0 ──next──> Node1 ──next──> Node2
   ↑<──prev─── ↑<──prev───
  [waitStatus=SIGNAL] [SIGNAL] [0]
  [thread=T1]        [T2]     [T3]
```

## CAS Loop (Spinlock)
```
Thread: read value = V
        CAS(V, V+1)? → Yes → done
        No → read again, retry
```

## StampedLock Modes
```
Optimistic Read:          Write Lock:
┌────────────────────┐   ┌────────────────────┐
│ stamp = tryOptimistic│   │ stamp = writeLock() │
│ read cx, cy         │   │ write x, y          │
│ if validate(stamp)  │   │ unlockWrite(stamp)   │
│   return cx+cy      │   └────────────────────┘
│ else → readLock()   │
└────────────────────┘
```

## Memory Ordering with CAS
```
Thread 1:                    Thread 2:
  data = 42 (plain write)     while (!CAS(flag, 0, 1))
  CAS(flag, 0, 1)               // spin
                               r1 = data
```
CAS provides a full memory barrier: Thread 1's write to `data` happens-before Thread 2's read of `data` (via the flag).
