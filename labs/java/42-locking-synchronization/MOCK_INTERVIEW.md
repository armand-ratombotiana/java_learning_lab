# Mock Interview Transcript: Locking & Synchronization

## Interviewer: Staff Engineer, Oracle
## Candidate: Senior Java developer
## Time: 45 minutes
## Focus: AQS, lock internals, CAS, biased locking

---

**Q1: Explain the AbstractQueuedSynchronizer (AQS) framework.**

**Candidate**: AQS is the foundation for most `java.util.concurrent` locks (`ReentrantLock`, `Semaphore`, `CountDownLatch`, `ReentrantReadWriteLock`, `ThreadPoolExecutor.Worker`). It manages: (1) A volatile `int state` — represents lock ownership count, permit count, etc. (2) A CLH-based (Craig, Landin, Hagersten) FIFO queue of waiting threads. (3) CAS operations for state management. (4) Template methods: `tryAcquire`, `tryRelease`, `tryAcquireShared`, `tryReleaseShared`.

**Interviewer**: How does `ReentrantLock` use AQS?

**Candidate**: `ReentrantLock.Sync` extends AQS. `state = 0` means unlocked. `state = 1` means locked once. `state = n` means locked n times (reentrant). `tryAcquire`: if state is 0, CAS to 1 and set owner. If state > 0 and current thread is owner, increment state. `tryRelease`: decrement state; if it reaches 0, clear owner. Fair vs non-fair: non-fair allows barging (CAS immediately), fair checks if queue is empty before acquiring.

**Interviewer**: How does `ReentrantReadWriteLock` distinguish read vs write locks?

**Candidate**: It splits the `state` into two 16-bit parts: high 16 bits for read lock count and low 16 bits for write lock count. Write lock: exclusive — waits until both counts are 0. Read lock: shared — increments read count if write count is 0. Read reentrancy uses `ThreadLocal` to track per-thread read count. Upgrade from read to write is not allowed (would cause deadlock).

**Interviewer**: Explain the object header structure and lock state transitions.

**Candidate**: The mark word (8 bytes on 64-bit) contains: (1) Bias pattern (age, thread ID, epoch) — biased locking. (2) Thin lock (pointer to stack-allocated lock record) — uncontended CAS. (3) Inflated lock (pointer to ObjectMonitor) — contended OS mutex. (4) GC mark bits. (5) Identity hash code (once computed). Transition: New object → biasable → biased → thin → inflated (escalation only, never de-escalate except at safepoint).

**Interviewer**: What is biased locking? Why was it deprecated?

**Candidate**: Biased locking gives the first-acquiring thread permanent access (no CAS needed). The thread ID is stored in the mark word. Each subsequent lock/unlock is a single `test` instruction. Benefits: very low overhead for single-thread access patterns. Deprecated in Java 15 (JEP 374) because: (1) High complexity in the JVM. (2) Requires safepoints for revocation. (3) Many modern workloads don't benefit. (4) Contention handling is slow. Disabled by default in Java 15+.

**Interviewer**: Write a lock-free stack using CAS.

**Candidate**: 
```java
class LockFreeStack<T> {
    private final AtomicReference<Node<T>> head = new AtomicReference<>();
    
    void push(T value) {
        Node<T> newNode = new Node<>(value);
        while (true) {
            Node<T> current = head.get();
            newNode.next = current;
            if (head.compareAndSet(current, newNode)) return;
        }
    }
    
    T pop() {
        while (true) {
            Node<T> current = head.get();
            if (current == null) return null;
            if (head.compareAndSet(current, current.next)) {
                return current.value;
            }
        }
    }
    
    record Node<T>(T value, Node<T> next) {}
}
```

**Interviewer**: What's the ABA problem with CAS? How does `AtomicStampedReference` help?

**Candidate**: ABA problem: Thread 1 reads head = A. Thread 2 pops A and B, pushes A. Thread 1's CAS succeeds (head is still A) but the list structure has changed (A.next is now different). `AtomicStampedReference` adds a version stamp that is also CAS'd, preventing the ABA problem. Or use `AtomicMarkableReference` for boolean mark.

**Interviewer**: Final: Compare `StampedLock` with `ReentrantReadWriteLock`.

**Candidate**: `StampedLock` adds optimistic reads: `long stamp = lock.tryOptimisticRead()` — no lock acquired, just a version check. After reading, `lock.validate(stamp)` checks if write occurred. If invalidated, upgrade to read lock. Benefits: (1) Optimistic reads are non-blocking (no CAS, no memory barrier). (2) Higher throughput for read-heavy workloads. Drawbacks: (1) Not reentrant. (2) Can't upgrade from read to write. (3) More complex API. (4) No Condition support.

---

## Feedback

**Strengths**:
- Complete AQS framework explanation
- ReentrantLock internals with state machine
- Object header and lock escalation
- Lock-free stack with CAS
- StampedLock vs ReadWriteLock

**Areas for Improvement**:
- Could discuss `VarHandle`'s `getAndSet` vs `compareAndSet`
- Mention JDK internal `ObjectMonitor` implementation

**Score**: 5/5 — Expert-level locking knowledge
