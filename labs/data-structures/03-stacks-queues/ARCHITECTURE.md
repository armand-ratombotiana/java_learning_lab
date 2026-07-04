# Architecture: Stacks & Queues in System Design

## Architectural Patterns

### Producer-Consumer

```
┌──────────┐     ┌──────────────┐     ┌──────────┐
│ Producer │────→│   Queue      │────→│ Consumer │
│ Threads  │     │ (Blocking)   │     │ Threads  │
└──────────┘     └──────────────┘     └──────────┘
```

Decouples producers from consumers. The queue absorbs bursts and allows different processing rates.

### Call Stack Architecture

```
┌──────────────────────────────┐
│ main()                       │ ← base of stack
│   processRequest()           │
│     validateInput()          │
│       checkPermissions()     │ ← current frame
└──────────────────────────────┘
```

Each method call pushes a stack frame (local variables, return address, operand stack).

### Event Loop

```
┌─────────────┐     ┌──────────────┐
│ Event       │────→│ Event Queue  │
│ Producers   │     │ (Deque)      │
└─────────────┘     └──────┬───────┘
                           │
                    ┌──────▼───────┐
                    │ Event Loop   │
                    │ (single      │
                    │  thread)     │
                    └──────────────┘
```

JavaScript's event loop, Java's AWT EventQueue, many UI frameworks.

## Integration Patterns

### Priority-Based Scheduling

```java
// Priority queue for task scheduling
class Task implements Comparable<Task> {
    int priority;
    int deadline;
    Runnable action;

    @Override
    public int compareTo(Task other) {
        if (this.priority != other.priority)
            return Integer.compare(other.priority, this.priority);
        return Integer.compare(this.deadline, other.deadline);
    }
}

PriorityQueue<Task> scheduler = new PriorityQueue<>();
scheduler.offer(new Task(/* high priority */));
scheduler.offer(new Task(/* low priority */));
Task next = scheduler.poll();  // highest priority first
```

### Circuit Breaker with Queue

A queue tracks recent failures for circuit breaker patterns — sliding window queue of timestamps.

### Cache with Deque (LRU)

```java
class LRUCache<K, V> {
    private final int capacity;
    private final Deque<K> order = new ArrayDeque<>();
    private final Map<K, V> cache = new HashMap<>();

    public V get(K key) {
        if (!cache.containsKey(key)) return null;
        order.removeFirstOccurrence(key);  // O(n)
        order.addFirst(key);  // move to front
        return cache.get(key);
    }
    // Better: LinkedHashMap for O(1)
}
```

## Distributed Systems

- **Message queues**: Kafka (partitioned queues), RabbitMQ (AMQP queues)
- **Task queues**: Celery (Redis-backed), SQS (Amazon Simple Queue Service)
- **Work-stealing**: ForkJoinPool uses deque per worker thread
