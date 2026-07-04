# Common Mistakes with Stacks & Queues

## Stack Underflow

```java
Deque<Integer> stack = new ArrayDeque<>();
stack.pop();  // NoSuchElementException on empty stack

// Always check
if (!stack.isEmpty()) {
    stack.pop();
}
```

## Queue Overflow (Fixed Capacity)

```java
// WRONG — overwrites tail
void enqueue(E e) {
    elements[tail] = e;  // no capacity check!
    tail = (tail + 1) % capacity;
}

// CORRECT
boolean enqueue(E e) {
    if (size == capacity) return false;  // or throw
    elements[tail] = e;
    tail = (tail + 1) % capacity;
    size++;
    return true;
}
```

## Confusing Stack and Deque Methods

```java
// Stack method → Deque equivalent
stack.push(e)    → deque.push(e) or deque.addFirst(e)
stack.pop()      → deque.pop() or deque.removeFirst()
stack.peek()     → deque.peek() or deque.peekFirst()

// Queue method → Deque equivalent
queue.offer(e)   → deque.offer(e) or deque.addLast(e)
queue.poll()     → deque.poll() or deque.removeFirst()
queue.peek()     → deque.peek() or deque.peekFirst()
```

## PriorityQueue with Non-Comparable Elements

```java
// WRONG — ClassCastException at runtime
PriorityQueue<Object> pq = new PriorityQueue<>();
pq.add(new Object());  // Object doesn't implement Comparable

// CORRECT — provide Comparator
PriorityQueue<Object> pq = new PriorityQueue<>(
    (a, b) -> a.hashCode() - b.hashCode()
);
```

## Using Stack (Legacy) Instead of ArrayDeque

```java
// AVOID — Stack extends Vector, synchronized, slow
Stack<Integer> stack = new Stack<>();

// PREFER — ArrayDeque, fast, no synchronization overhead
Deque<Integer> stack = new ArrayDeque<>();
```

## Confusing PriorityQueue Ordering

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.add(5); pq.add(3); pq.add(4);

pq.poll(); // returns 3 (smallest) — NOT insertion order!
```

PriorityQueue is a min-heap by default. The first element removed is always the smallest, not the first inserted.
