# Debugging Stacks & Queues

## Common Issues

| Symptom | Likely Cause |
|---------|-------------|
| `EmptyStackException` / `NoSuchElementException` | Pop/poll on empty structure |
| Stack grows without bound | Push without matching pop |
| Queue returns wrong element | Head/tail index not updated |
| Circular queue loses elements | Wraparound index bug (off-by-one) |
| PriorityQueue returns unexpected order | Wrong Comparator |

## Debugging Techniques

### Trace Stack Contents

```java
Deque<Integer> stack = new ArrayDeque<>();
int[] testSequence = {1, 2, 3};
for (int x : testSequence) {
    stack.push(x);
    System.out.println("push(" + x + "): " + stack);
}
// Now pop:
while (!stack.isEmpty()) {
    System.out.println("pop(): " + stack.pop() + ", remaining: " + stack);
}
```

### Verify Queue Circular Invariant

```java
void checkInvariant(CircularQueue<?> q) {
    assert q.size <= q.capacity;
    assert q.head >= 0 && q.head < q.capacity;
    assert q.tail >= 0 && q.tail < q.capacity;
    assert q.size == 0 || q.elements[q.head] != null;
    // Verify: tail follows head after (size) steps
    int expectedTail = (q.head + q.size) % q.capacity;
    assert expectedTail == q.tail : "tail invariant violated";
}
```

### Unit Testing

```java
@Test
void testQueueFIFO() {
    CircularQueue<String> q = new CircularQueue<>(5);
    assertTrue(q.enqueue("A"));
    assertTrue(q.enqueue("B"));
    assertTrue(q.enqueue("C"));
    assertEquals("A", q.dequeue());
    assertEquals("B", q.dequeue());
    assertTrue(q.enqueue("D"));
    assertEquals("C", q.dequeue());
    assertEquals("D", q.dequeue());
    assertTrue(q.isEmpty());
}

@Test
void testStackLIFO() {
    ArrayStack<String> stack = new ArrayStack<>();
    stack.push("A");
    stack.push("B");
    stack.push("C");
    assertEquals("C", stack.pop());
    assertEquals("B", stack.pop());
    assertEquals("A", stack.pop());
    assertTrue(stack.isEmpty());
}

@Test
void testPriorityOrder() {
    PriorityQueue<Integer> pq = new PriorityQueue<>();
    pq.add(5); pq.add(3); pq.add(4);
    assertEquals(Integer.valueOf(3), pq.poll());
    assertEquals(Integer.valueOf(4), pq.poll());
    assertEquals(Integer.valueOf(5), pq.poll());
}
```
