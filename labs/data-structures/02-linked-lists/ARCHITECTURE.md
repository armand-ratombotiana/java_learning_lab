# Architecture: Linked Lists in System Design

## Usage Patterns

### Queue/Deque

```java
// FIFO queue — LinkedList as Queue
Queue<Task> queue = new LinkedList<>();
queue.offer(task);    // enqueue
Task t = queue.poll(); // dequeue

// Deque — double-ended
Deque<String> deque = new LinkedList<>();
deque.addFirst("front");
deque.addLast("back");
String front = deque.removeFirst();
String back = deque.removeLast();
```

### Stack (LIFO)

```java
Deque<String> stack = new LinkedList<>();
stack.push("item1");    // addFirst
stack.push("item2");
String top = stack.pop(); // removeFirst → "item2"
```

### LRU Cache (LinkedHashMap + LinkedList)

```java
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);  // access-order
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;  // removes least recently accessed
    }
}
```

`LinkedHashMap` uses a doubly linked list internally to maintain insertion or access order. On access-order mode, every `get()` moves the entry to the end.

## Concurrency Patterns

### ConcurrentLinkedDeque

```java
// Lock-free, non-blocking Deque (Java 7+)
ConcurrentLinkedDeque<Task> deque = new ConcurrentLinkedDeque<>();
deque.addFirst(task);
deque.addLast(task);
Task t = deque.pollFirst();  // thread-safe
```

Uses **CAS (Compare-And-Swap)** operations for lock-free node insertion/removal.

### BlockingQueue Variants

```java
// Thread-safe blocking queue
BlockingQueue<Task> queue = new LinkedBlockingQueue<>();
queue.put(task);         // blocks if full
Task t = queue.take();   // blocks if empty
```

`LinkedBlockingQueue` uses separate locks for head and tail operations, enabling higher throughput than a single-lock approach.

## Integration Patterns

- **Producer-Consumer**: LinkedList as buffer between producers and consumers
- **Work-Stealing Deque**: Each thread has a deque; idle threads steal from others' tails
- **Round-Robin Scheduling**: Circular linked list of processes, each gets a time slice
- **Undo/Redo**: Doubly linked list of states; undo goes backward, redo goes forward
