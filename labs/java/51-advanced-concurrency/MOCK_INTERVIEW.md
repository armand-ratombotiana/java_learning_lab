# Mock Interview Transcript: Advanced Concurrency Patterns

## Interviewer: Staff Engineer, Google
## Candidate: Staff Java developer
## Time: 50 minutes
## Focus: Advanced patterns, non-blocking algorithms, performance

---

**Q1: Design a non-blocking hash map for high-concurrency reads and writes.**

**Candidate**: 
```java
class NonBlockingHashMap<K, V> {
    private volatile Node<K, V>[] table;
    private final AtomicInteger size = new AtomicInteger();
    
    record Node<K, V>(int hash, K key, V value, Node<K, V> next) {}
    
    V get(K key) {
        int hash = spread(key.hashCode());
        Node<K, V>[] tab = table;  // Volatile read
        int idx = (tab.length - 1) & hash;
        for (Node<K, V> n = tab[idx]; n != null; n = n.next) {
            if (n.hash == hash && Objects.equals(n.key, key)) return n.value;
        }
        return null;
    }
    
    void put(K key, V value) {
        int hash = spread(key.hashCode());
        while (true) {
            Node<K, V>[] tab = table;
            int idx = (tab.length - 1) & hash;
            Node<K, V> head = tab[idx];
            
            // Update existing
            for (Node<K, V> n = head; n != null; n = n.next) {
                if (n.hash == hash && Objects.equals(n.key, key)) {
                    // Create new node with updated value (copy-on-write)
                    Node<K, V> newNode = replaceInChain(head, n, 
                        new Node<>(hash, key, value, n.next));
                    if (CAS(tab, idx, head, newNode)) return;
                    break;  // CAS failed, retry
                }
            }
            
            // Insert new — Linearization point
            Node<K, V> newNode = new Node<>(hash, key, value, head);
            if (CAS(tab, idx, head, newNode)) {
                size.incrementAndGet();
                checkResize();
                return;
            }
            // CAS failed — retry
        }
    }
}
```
This is a simplified version. Real implementations (like Cliff Click's NonBlockingHashMap) use more complex mechanisms.

**Interviewer**: How does a `Phaser` differ from `CyclicBarrier` and `CountDownLatch`?

**Candidate**: 
| Feature | CountDownLatch | CyclicBarrier | Phaser |
|---------|---------------|---------------|--------|
| Reusable | No | Yes | Yes |
| Parties | Fixed at creation | Fixed at creation | Dynamic (register/deregister) |
| Action | After count reaches 0 | After barrier, optional Runnable | On advance, override onAdvance |
| Phase tracking | No | Has generation counter | Has phase number |
| Termination | Single-shot | Cyclic (reset) | Can be terminated |
| Tree structure | No | No | Yes (Phaser tree for scalability) |

Phaser is the most flexible — supports dynamic party registration and tree-based phasing for large-scale parallelism.

**Interviewer**: Implement a concurrent bounded buffer using `VarHandle`.

**Candidate**: 
```java
class ConcurrentBoundedBuffer<T> {
    private final T[] buffer;
    private volatile int head, tail, count;
    
    private static final VarHandle HEAD, TAIL, COUNT;
    static {
        try {
            var l = MethodHandles.lookup();
            HEAD = l.findVarHandle(ConcurrentBoundedBuffer.class, "head", int.class);
            TAIL = l.findVarHandle(ConcurrentBoundedBuffer.class, "tail", int.class);
            COUNT = l.findVarHandle(ConcurrentBoundedBuffer.class, "count", int.class);
        } catch (Exception e) { throw new ExceptionInInitializerError(e); }
    }
    
    @SuppressWarnings("unchecked")
    ConcurrentBoundedBuffer(int capacity) {
        buffer = (T[]) new Object[capacity];
    }
    
    boolean offer(T item) {
        while (true) {
            int c = (int) COUNT.getAcquire(this);
            if (c == buffer.length) return false;
            // ... CAS logic for multiple producers
        }
    }
}
```

**Interviewer**: How does work-stealing work in ForkJoinPool?

**Candidate**: Each worker thread has a deque of tasks. It processes tasks from the bottom of its own deque (LIFO — better cache locality). When its deque is empty, it steals tasks from the bottom of another thread's deque (FIFO — to minimize contention with that thread's own LIFO pops). Work-stealing balances load: idle threads steal from busy ones. The ForkJoinPool uses a common pool for parallel streams and CompletableFuture's async operations.

**Interviewer**: Implement a circuit breaker pattern using reactive streams.

**Candidate**: 
```java
class CircuitBreaker {
    enum State { CLOSED, OPEN, HALF_OPEN }
    
    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    private final AtomicInteger failureCount = new AtomicInteger();
    private final int threshold;
    private final long timeout;
    private volatile long lastFailureTime;
    
    CircuitBreaker(int threshold, Duration timeout) {
        this.threshold = threshold;
        this.timeout = timeout.toNanos();
    }
    
    <T> T call(Supplier<T> operation, Supplier<T> fallback) {
        if (state.get() == State.OPEN) {
            if (System.nanoTime() - lastFailureTime > timeout) {
                state.compareAndSet(State.OPEN, State.HALF_OPEN);
            } else {
                return fallback.get();
            }
        }
        try {
            T result = operation.get();
            if (state.get() == State.HALF_OPEN) {
                state.set(State.CLOSED);
                failureCount.set(0);
            }
            return result;
        } catch (Exception e) {
            lastFailureTime = System.nanoTime();
            if (failureCount.incrementAndGet() >= threshold) {
                state.set(State.OPEN);
            }
            return fallback.get();
        }
    }
}
```

**Interviewer**: Final: How would you implement a distributed lock in Java for a multi-region system?

**Candidate**: Distributed lock requirements: (1) Mutual exclusion across JVMs. (2) Lease-based (auto-release on failure). (3) Fencing tokens to prevent stale lock holders. (4) Recovery mechanism if lock holder crashes. Implementation options: (1) Redis `RedLock` algorithm (Redisson). (2) ZooKeeper `sequential ephemeral node`. (3) etcd with lease. (4) Database row-level lock with TTL. Key Java considerations: (1) Use `CompletableFuture` for async lock acquisition. (2) Implement `AutoCloseable` for try-with-resources. (3) Handle reconnection and session expiry. (4) Never block virtual threads on distributed lock acquisition.

---

## Feedback

**Strengths**:
- Non-blocking hash map design
- Phaser vs CyclicBarrier vs CountDownLatch comparison
- VarHandle for concurrent fields
- Work-stealing algorithm explanation
- Circuit breaker pattern
- Distributed lock design

**Areas for Improvement**:
- Could discuss `VarHandle` segment-fencing
- Mention `SequencedCollection` concurrency improvements

**Score**: 5/5 — Expert advanced concurrency
