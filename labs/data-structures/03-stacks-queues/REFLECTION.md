# Reflection: Stacks & Queues

## What I Learned

- Stacks and queues are abstract data types defined by their access patterns (LIFO, FIFO)
- Array-backed implementations are cache-friendly and fast (O(1) amortized)
- ArrayDeque is the recommended Java implementation for both stacks and queues
- Priority queues use heap ordering for O(log n) insertion/removal by priority
- Monotonic stacks and deques solve next-greater-element and sliding-window problems optimally
- Two-stack queue provides FIFO behavior using LIFO structures

## Questions to Consider

1. Why is ArrayDeque preferred over Stack and LinkedList for stack/queue operations?
2. When would you choose a linked-list queue over a circular-array queue?
3. How does the monotonic stack pattern work for next-greater-element problems?
4. What are the trade-offs between bounded and unbounded queues in production?
5. How would you implement a thread-safe priority queue?

## Connections

Stacks and queues connect to:
- **Trees** (DFS uses stack, BFS uses queue)
- **Graphs** (DFS stack, BFS queue, Dijkstra priority queue)
- **Sorting** (heapsort, priority queue sort)
- **OS** (call stack, process queues, IO queues)
- **Networking** (packet queues, message brokers)
- **Parsing** (expression evaluation, syntax analysis)

## Self-Assessment

- [ ] Can implement a stack from scratch (array and linked)
- [ ] Can implement a circular queue from scratch
- [ ] Can implement a queue using two stacks
- [ ] Understand ArrayDeque internals (circular buffer, power-of-2)
- [ ] Can solve monotonic stack problems
- [ ] Understand PriorityQueue heap internals
