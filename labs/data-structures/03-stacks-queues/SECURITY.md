# Security Considerations for Stacks & Queues

## Stack Overflow (Call Stack)

The most famous security issue related to stacks is **call stack overflow**:

```java
// Recursive method without base case
void neverEnds() {
    neverEnds();  // Eventually StackOverflowError
}
```

Deep recursion can exhaust the call stack. In Java, the default stack size is typically 1MB. Attackers can craft inputs that cause deep recursion (e.g., a deeply nested JSON parser). Mitigation: use iterative solutions or `-Xss` to increase stack size.

## Deserialization Attacks

PriorityQueue implements `Serializable`. Deserializing a crafted heap can cause:
- DoS via huge size (O(n²) heapify)
- Arbitrary object creation via `Comparator` deserialization (gadget chains)

## Denial of Service (Queue Flooding)

```java
// Attacker can flood queue
Queue<Request> queue = new LinkedList<>();
while (true) queue.offer(attackerRequest);  // memory exhaustion
```

Always bound queue sizes. Use `ArrayBlockingQueue` or `LinkedBlockingQueue` with capacity limits in production.

## PriorityQueue Ordering Attacks

If a PriorityQueue stores security-sensitive data:
- The `Comparator` could be swapped to leak priority order information
- Custom objects without proper `equals`/`compareTo` consistency can produce undefined behavior
- The `remove(Object)` method uses linear search (O(n)), enabling timing attacks

## Thread Safety

- `ArrayDeque` and `PriorityQueue` are **not thread-safe**
- `Stack` is synchronized (legacy) but outdated
- Use `LinkedBlockingQueue`, `ArrayBlockingQueue`, or `ConcurrentLinkedDeque` for thread-safe operations
- Without synchronization, concurrent modification can corrupt internal pointers/indices

## Resource Exhaustion Patterns

| Pattern | Risk | Mitigation |
|---------|------|------------|
| Unbounded queue growth | Memory exhaustion | Bounded capacity |
| Deep recursion | StackOverflowError | Iterative algorithms |
| Large heap build | O(n) build time, OOM | Validate input size |
| Deserialized PQ | Arbitrary comparator | Validate serialized data |
