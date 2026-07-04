# Why Stacks & Queues Exist

Stacks and queues exist because **ordering matters** — different problems require different access patterns.

## Stacks: LIFO for Nested Structures

Stacks model **nested and recursive** patterns naturally:
- Function calls (call stack): when a function calls another, the return address is pushed; when it returns, the address is popped
- Expression evaluation: parentheses matching, postfix (RPN) evaluation
- Undo systems: each action is pushed; undo pops the last action
- Depth-first search: the stack models the traversal frontier

## Queues: FIFO for Fairness

Queues model **sequential processing** where order must be preserved:
- Task scheduling: first-come, first-served
- Breadth-first search: process nodes in order of discovery
- Network packet buffering: packets processed in arrival order
- Producer-consumer: one thread produces, another consumes in order

## Deques: Combined Flexibility

Deques exist for problems requiring access at both ends:
- Sliding window maximum (monotonic deque)
- Work-stealing algorithms (threads steal from deque ends)
- Palindrome checking

## Priority Queues: Beyond FIFO

Regular queues process in arrival order; priority queues process by **importance** — the most urgent task goes first regardless of arrival time.

## Java-Specific Context

- `ArrayDeque` is the preferred stack/queue implementation (faster than Stack and LinkedList)
- `Stack` is legacy (extends Vector, synchronized, slower)
- `PriorityQueue` provides heap-based priority ordering
- `Deque` interface is more flexible than separate Stack/Queue
