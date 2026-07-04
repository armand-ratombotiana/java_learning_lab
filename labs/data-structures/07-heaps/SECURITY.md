# Security Considerations for Heaps

## Denial of Service

### Unbounded Growth

```java
PriorityQueue<Task> pq = new PriorityQueue<>();
// Attacker floods with tasks — unbounded memory growth
while (true) pq.offer(attackerTask);
```

Mitigation: use bounded heap (wrapping PriorityQueue with capacity limit) or `PriorityBlockingQueue` with capacity.

### Expensive Comparison Functions

```java
// Malicious Comparator — slow on purpose
PriorityQueue<String> pq = new PriorityQueue<>((a, b) -> {
    // Simulate slow comparison
    Thread.sleep(100);
    return a.compareTo(b);
});
```

Custom Comparators should be fast (O(1)). A slow Comparator can be used for timing attacks or DoS.

## Integer Overflow in Index Math

```java
int left = 2 * i + 1;
// If i is large (near Integer.MAX_VALUE/2), left becomes negative
// ArrayIndexOutOfBoundsException or access to wrong element
```

Valid for Java PriorityQueue (capacity limited to MAX_ARRAY_SIZE), but custom implementations should check bounds.

## Data Leakage via Heap

PriorityQueue does not guarantee ordering during iteration. However, the internal array is accessible via reflection, and sensitive data in the queue could be read:
- Use `clear()` or overwrite elements after use
- Never store sensitive data (passwords, tokens) in a heap

## Thread Safety

PriorityQueue is **not thread-safe**. Concurrent access can:
- Produce incorrect ordering
- Overwrite elements
- Cause infinite loops during siftUp/siftDown

Use `PriorityBlockingQueue` for concurrent access.

## Serialization

Deserializing a crafted PriorityQueue can:
- Cause O(n) heap rebuild with malicious comparator
- Exhaust memory with huge arrays
- Create objects that violate heap property (if comparator behavior changes)

## Secure Practices

- Bound queue size for untrusted input
- Validate Comparator consistency (compare(a,b) must be consistent with equals)
- Use immutable objects as heap elements
- Avoid reflection-based access to heap internals
- Clear sensitive data after use
