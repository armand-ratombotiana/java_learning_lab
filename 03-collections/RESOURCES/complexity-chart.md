# Collections Complexity Chart

Time and Space complexity for Java Collections Framework.

## Collection Hierarchy Overview

```
Iterable
    └── Collection
            ├── List (ordered, indexed)
            │       ├── ArrayList
            │       ├── LinkedList
            │       ├── Vector (sync)
            │       └── Stack (LIFO)
            │
            ├── Set (unique elements)
            │       ├── HashSet (hash)
            │       ├── LinkedHashSet (order)
            │       ├── TreeSet (sorted)
            │       └── EnumSet
            │
            └── Queue (FIFO)
                    ├── ArrayDeque
                    ├── LinkedList
                    ├── PriorityQueue
                    └── Deque
                            └── BlockingDeque
```

## Complexity by Operation

### List Implementations

| Operation | ArrayList | LinkedList | Vector |
|-----------|-----------|------------|--------|
| get(i) | O(1) | O(n) | O(1) |
| add(value) | O(1)* | O(1) | O(1)* |
| add(i, value) | O(n) | O(1)** | O(n) |
| remove(i) | O(n) | O(1)** | O(n) |
| contains | O(n) | O(n) | O(n) |
| Space | O(n) | O(n)* | O(n) |

*Amortized - ArrayList doubles capacity
**Adding/removing at head/tail is O(1) for LinkedList

### Set Implementations

| Operation | HashSet | LinkedHashSet | TreeSet |
|-----------|---------|---------------|---------|
| add | O(1) | O(1) | O(log n) |
| remove | O(1) | O(1) | O(log n) |
| contains | O(1) | O(1) | O(log n) |
| get | N/A | N/A | O(log n) |
| sorted | No | No (insertion order) | Yes |
| Space | O(n) | O(n) | O(n) |

### Map Implementations

| Operation | HashMap | LinkedHashMap | TreeMap |
|-----------|---------|---------------|---------|
| get | O(1) | O(1) | O(log n) |
| put | O(1) | O(1) | O(log n) |
| remove | O(1) | O(1) | O(log n) |
| containsKey | O(1) | O(1) | O(log n) |
| firstKey | N/A | N/A | O(log n) |
| Space | O(n) | O(n) | O(n) |

### Queue Implementations

| Operation | ArrayDeque | PriorityQueue | LinkedList |
|-----------|------------|--------------|------------|
| add/remove | O(1) | O(log n) | O(1) |
| peek | O(1) | O(1) | O(1) |
| Poll | O(1) | O(log n) | O(1) |

## When to Use Which

```
┌─────────────────────────────────────────────────────────────┐
│                    DECISION TREE                            │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   Need unique elements?                                      │
│       └── NO ──→ ArrayList (most cases)                      │
│       └── YES ─→ Need ordering?                              │
│                   ├── NO ──→ HashSet                         │
│                   ├── INSERTION ─→ LinkedHashSet             │
│                   └── SORTED ──→ TreeSet                     │
│                                                              │
│   Need FIFO?                                                 │
│       └── NO ──→ ArrayDeque                                  │
│       └── YES ─→ Deque (double-ended)?                       │
│                   ├── NO ──→ Queue/PriorityQueue            │
│                   └── YES ─→ ArrayDeque/LinkedList           │
│                                                              │
│   Frequent insertions/removals at middle?                    │
│       └── YES ──→ LinkedList                                 │
│       └── NO ──→ ArrayList                                   │
└─────────────────────────────────────────────────────────────┘
```

## Performance Comparison Table

| Big-O | n=10 | n=100 | n=1000 | n=100000 |
|-------|------|-------|--------|----------|
| O(1) | 1 | 1 | 1 | 1 |
| O(log n) | 3.3 | 6.6 | 10 | 16.6 |
| O(n) | 10 | 100 | 1000 | 100000 |
| O(n log n) | 33 | 664 | 10000 | 1.6M |
| O(n²) | 100 | 10000 | 1M | 10B |

## Thread-Safe Alternatives

| Interface | Non-Sync | Synchronized | Concurrent |
|-----------|----------|--------------|------------|
| List | ArrayList | Collections.synchronizedList() | CopyOnWriteArrayList |
| Map | HashMap | Collections.synchronizedMap() | ConcurrentHashMap |
| Set | HashSet | Collections.synchronizedSet() | CopyOnWriteArraySet |
| Queue | - | - | ConcurrentLinkedQueue |

## Memory Overhead (approx)

| Collection | Base Overhead |
|------------|---------------|
| ArrayList | ~48 bytes |
| LinkedList | ~48 bytes + 2 refs/node |
| HashSet/HashMap | ~64 bytes |
| TreeSet/TreeMap | ~64 bytes + color/refs/node |
| LinkedHashSet/Map | ~64 bytes + 2 refs/entry |
