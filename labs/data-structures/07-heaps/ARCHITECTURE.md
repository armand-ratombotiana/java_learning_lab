# Architecture: Heaps in System Design

## Priority Queue Architecture

```
Producer Threads           Consumer Threads
    │                           ▲
    │   ┌─────────────────┐     │
    └──→│ PriorityQueue   │─────┘
        │ (Binary Heap)   │
        └─────────────────┘
```

Priority queues decouple producers from consumers while ensuring the highest-priority item is processed first.

## Task Scheduling Architecture

```
┌──────────┐    ┌──────────────┐    ┌──────────┐
│ Scheduler│───→│ PriorityQueue│───→│ Executor │
│ (adds    │    │ (sorted by   │    │ (polls   │
│  tasks)  │    │  deadline)   │    │  & runs) │
└──────────┘    └──────────────┘    └──────────┘
```

## Dijkstra's Algorithm Architecture

```
Input        PriorityQueue          Distance Array
Graph +      ┌────────────────┐     ┌────────────────┐
Source ─────→│ (dist, vertex) │────→│ dist[V]        │
             │ min-heap       │     │ prev[V]        │
             └────────────────┘     └────────────────┘
```

## Event Simulation Architecture

```
Future Event List (PriorityQueue sorted by time)
    │
    ├─ Event at t=1: Process arrival
    ├─ Event at t=3: Process departure
    └─ Event at t=5: Process arrival
```

The simulation repeatedly polls the next event, processes it, and may schedule new events.

## Two-Heap Median Architecture

```
Stream of numbers
    │
    ├──→ MaxHeap (lower half)  ← stores smaller numbers
    │        peek() = median candidate
    │
    └──→ MinHeap (upper half)  ← stores larger numbers
             peek() = median candidate
```

## Distributed Priority Queue

```java
// Sharded by priority range
// Each shard has its own PriorityQueue
// Coordinator distributes tasks to shards
// Workers poll from their assigned shard

PriorityQueue[] shards = new PriorityQueue[4];
int shardIndex = priority % shards.length;
shards[shardIndex].offer(task);
```

## Java Ecosystem

- **ScheduledThreadPoolExecutor**: uses `DelayQueue` (heap-based) for delayed/scheduled tasks
- **ForkJoinPool**: work-stealing deque (not heap-based, but similar priority concept)
- **Spring TaskScheduler**: wraps `ScheduledThreadPoolExecutor`
- **Apache Kafka**: uses priority-like mechanics for partition assignment
- **Netty**: event loop uses task queue (not heap, but priority-based in some configurations)
