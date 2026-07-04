# History: Stacks & Queues

## Stacks

- **1946**: Konrad Zuse's Plankalkül included a stack-like memory for nested operations
- **1957**: FORTRAN introduced the concept of a push-down list for expression evaluation
- **1960s**: Edsger Dijkstra used stacks for the **shunting-yard algorithm** (infix to postfix conversion)
- **1964**: The **call stack** was formalized in ALGOL 60 implementations
- **1995**: `java.util.Stack` — the first Java collection, extended `Vector` (retrospectively considered a design mistake)

## Queues

- **1950s**: Queues appeared in early operating systems for job scheduling
- **1961**: **Breadth-first search** published by Edward F. Moore
- **1965**: Circular queues (ring buffers) used in Dijkstra's THE operating system
- **1998**: `java.util.Queue` interface added to Java Collections Framework
- **2006**: `java.util.Deque` interface added (Java 6)
- **2006**: `java.util.concurrent` added `BlockingQueue`, `LinkedBlockingQueue`, `ArrayBlockingQueue`

## Priority Queues

- **1964**: Williams invented the **binary heap** for heapsort
- **1988**: **Pairing heap** (Fredman, Sedgewick, Sleator, Tarjan) — simpler self-adjusting heap
- **1990s**: Fibonacci heaps for improved Dijkstra performance
- **1998**: `java.util.PriorityQueue` added to Java Collections Framework

## Deque

- **1987**: Deque (double-ended queue) formalized by Knuth in The Art of Computer Programming
- **2006**: `java.util.ArrayDeque` introduced — faster than Stack and LinkedList for stack/queue operations
