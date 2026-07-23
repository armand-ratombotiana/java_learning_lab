# Mock Interview Transcript: Collections

## Interviewer: Senior SWE, Amazon
## Candidate: Mid-level Java developer
## Time: 45 minutes
## Focus: HashMap internals, concurrent collections, performance

---

**Q1: Walk through HashMap.put() in Java 8+. What happens step by step?**

**Candidate**: Step 1: Compute hash using `hash(key.hashCode())` which XORs high bits into low bits for better distribution. Step 2: Find bucket index: `(n - 1) & hash`. Step 3: If bucket is empty, create Node. Step 4: If collision, check if key equals existing key — replace value. Step 5: If collision and different key, append to linked list (if < 8 elements) or TreeBin (if ≥ 8 and table size ≥ 64). Step 6: If size exceeds threshold (loadFactor * capacity), resize: double capacity and rehash all entries.

**Interviewer**: Why the threshold of 8 for tree conversion?

**Candidate**: It's based on Poisson distribution — with a good hash function, collisions should be rare. A bucket having 8 or more collisions has probability less than 1 in 10 million. If it happens, it indicates either a bad hashCode or a deliberate attack (hash collision DoS). The tree (TreeNode, a Red-Black tree) improves worst-case from O(n) to O(log n).

**Interviewer**: What's the initial capacity and load factor default?

**Candidate**: Default initial capacity is 16. Default load factor is 0.75. The table resizes when `size > capacity * 0.75`. 0.75 is a trade-off between time and space — higher (0.9) gives less space but more collisions, lower (0.5) gives fewer collisions but more space.

**Interviewer**: How does ConcurrentHashMap differ from HashMap for concurrent access?

**Candidate**: In Java 8+, ConcurrentHashMap uses: (1) CAS for table initialization, (2) synchronized on individual bins (not segments like Java 7), (3) `size()` uses `LongAdder`-style striping, (4) iteration reflects current state without locking the entire map, (5) `computeIfAbsent()` uses CAS with retry or bin-level locking.

**Interviewer**: Compare ConcurrentHashMap's computeIfAbsent in Java 8 vs the Java 7 version.

**Candidate**: In Java 7 (Segments), computeIfAbsent locked the entire segment. In Java 8+, it first tries without locking: if the key exists, return the value. Only if creation is needed does it lock the bin. This is more concurrent-friendly — multiple threads can read from the same bin without blocking.

**Interviewer**: Let's implement a custom collection. Design a bounded, thread-safe queue.

**Candidate**: 
```java
public class BoundedQueue<T> {
    private final T[] items;
    private int head, tail, count;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    
    @SuppressWarnings("unchecked")
    public BoundedQueue(int capacity) {
        items = (T[]) new Object[capacity];
    }
    
    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length) notFull.await();
            items[tail] = item;
            if (++tail == items.length) tail = 0;
            count++;
            notEmpty.signal();
        } finally { lock.unlock(); }
    }
    
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) notEmpty.await();
            T item = items[head];
            items[head] = null;
            if (++head == items.length) head = 0;
            count--;
            notFull.signal();
            return item;
        } finally { lock.unlock(); }
    }
}
```

**Interviewer**: How does this compare to `ArrayBlockingQueue`?

**Candidate**: ArrayBlockingQueue uses a single lock for both put and take (like mine), whereas LinkedBlockingQueue uses separate locks for put and take for higher throughput. ArrayBlockingQueue has better cache locality because it uses a contiguous array.

---

## Feedback

**Strengths**:
- Complete HashMap.put() walkthrough with Java 8+ details
- Understands treeification threshold reasoning
- ConcurrentHashMap comparison across versions
- Implements bounded queue with proper signaling

**Areas for Improvement**:
- Could discuss `IdentityHashMap` use cases
- Might mention `WeakHashMap` for cache implementations

**Score**: 4.5/5 — Strong collections knowledge
