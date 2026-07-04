# Theory: Stacks & Queues

## Stack (LIFO — Last In, First Out)

A stack is an abstract data type where elements are added and removed from the same end (the top). The last element inserted is the first one removed.

**Operations**:
- `push(element)` — add to top
- `pop()` — remove and return top
- `peek()` — return top without removing
- `isEmpty()` — check if empty

**Applications**: Expression evaluation, undo/redo, back navigation, parenthesis matching, DFS.

## Queue (FIFO — First In, First Out)

A queue is an abstract data type where elements are added at the rear and removed from the front. The first element inserted is the first one removed.

**Operations**:
- `enqueue(element)` / `offer(element)` — add to rear
- `dequeue()` / `poll()` — remove and return front
- `peek()` — return front without removing
- `isEmpty()` — check if empty

**Applications**: BFS, task scheduling, printer spooling, message queues.

## Deque (Double-Ended Queue)

Allows insertion and removal at both ends. Java's `Deque` interface:

- `addFirst(e)` / `addLast(e)` — insert at front/back
- `removeFirst()` / `removeLast()` — remove from front/back
- `getFirst()` / `getLast()` — peek front/back

Deques can serve as both stacks and queues.

## Priority Queue

Elements are ordered by priority, not insertion order. The element with highest priority is removed first.

**Operations**: All O(log n) for heap-based implementations.

**Applications**: Dijkstra's algorithm, Huffman coding, task scheduling with priorities.

## Time Complexity

| Operation | Array Stack | Linked Stack | Array Queue | Linked Queue | Heap PQ |
|-----------|------------|-------------|-------------|-------------|---------|
| push/enqueue | O(1)* | O(1) | O(1)* | O(1) | O(log n) |
| pop/dequeue | O(1) | O(1) | O(1) | O(1) | O(log n) |
| peek | O(1) | O(1) | O(1) | O(1) | O(1) |
| isEmpty | O(1) | O(1) | O(1) | O(1) | O(1) |
| contains | O(n) | O(n) | O(n) | O(n) | O(n) |

\*Amortized for dynamic array
