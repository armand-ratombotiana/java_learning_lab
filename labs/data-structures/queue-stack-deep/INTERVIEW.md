# Interview Questions: Queue & Stack (Deep Dive)

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 20 Valid Parentheses](https://leetcode.com/problems/valid-parentheses/) | Easy | Amazon, Google, Meta, Microsoft, Apple | Stack matching |
| [LC 155 Min Stack](https://leetcode.com/problems/min-stack/) | Medium | Amazon, Meta, Microsoft, Oracle | Two-stack / pair stack |
| [LC 739 Daily Temperatures](https://leetcode.com/problems/daily-temperatures/) | Medium | Amazon, Meta, Google | Monotonic stack |
| [LC 150 Evaluate Reverse Polish Notation](https://leetcode.com/problems/evaluate-reverse-polish-notation/) | Medium | Amazon, Google, Microsoft | Stack evaluation |
| [LC 232 Implement Queue Using Stacks](https://leetcode.com/problems/implement-queue-using-stacks/) | Easy | Amazon, Microsoft, Google | Two-stack queue |
| [LC 239 Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/) | Hard | Amazon, Meta, Google, Microsoft | Monotonic deque |
| [LC 84 Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/) | Hard | Amazon, Meta, Google, Microsoft | Monotonic stack |
| [LC 225 Implement Stack Using Queues](https://leetcode.com/problems/implement-stack-using-queues/) | Easy | Microsoft, Amazon | Two-queue stack |
| [LC 316 Remove Duplicate Letters](https://leetcode.com/problems/remove-duplicate-letters/) | Medium | Google, Microsoft | Monotonic stack |
| [LC 456 132 Pattern](https://leetcode.com/problems/132-pattern/) | Medium | Amazon, Google, Meta | Monotonic stack |
| [LC 503 Next Greater Element II](https://leetcode.com/problems/next-greater-element-ii/) | Medium | Amazon, Microsoft | Monotonic stack (circular) |

## NeetCode Reference
NeetCode 150: Stack category — 7 problems. The core stack problems (Valid Parentheses, Min Stack, RPN, Daily Temperatures) are must-know.

## Company-Specific Questions

### Google
- Design a stack that supports push, pop, top, and retrieving the minimum element in O(1)
- Implement a queue with getMax() in O(1) amortized (deque of max candidates)
- How would you implement a stack using a single queue?
- Browser history — forward and back navigation using two stacks

### Microsoft
- Evaluate reverse Polish notation — handle edge cases (division by zero, negative numbers)
- Design a stack that supports push, pop, and increment bottom k elements in O(1)
- Implement a priority queue-backed stack (pop highest priority, not most recent)
- How does the Windows kernel use stack-based callbacks?

### Meta
- Asteroid collision simulation with a stack
- Exclusive time of functions (stack of (functionId, startTime))
- Design a task scheduler using priority queue (cooldown between same task types)
- How would you implement a blocking queue for a thread pool?

### Amazon
- Sliding window maximum — optimize from O(n·k) to O(n) using deque
- Design an order book with priority queue-based matching engine
- Level-order traversal using a queue (breadth-first search in tree)

### Apple
- Design a ring queue for audio processing (lock-free, fixed capacity)
- Stack-based undo/redo in a text editor
- How does GCD (Grand Central Dispatch) use queues for task scheduling?

### Oracle
- ArrayDeque vs Stack vs LinkedList — which offers best performance for single-threaded use?
- What is the `Deque` interface and why should it replace `Stack` in new code?
- BlockingQueue implementations (ArrayBlockingQueue, LinkedBlockingQueue, SynchronousQueue) — trade-offs for enterprise middleware
- How does the JVM's internal stack work? StackOverflowError — what causes it and how to prevent it?

## Real Production Scenarios

- **Scenario 1: Expression Evaluator** — A calculator service evaluates user-defined formulas with operator precedence. Uses two stacks (operands and operators) with Dijkstra's shunting-yard algorithm. Must handle parentheses, unary minus, and functions like sin(), log().

- **Scenario 2: Rate Limiting with Sliding Window** — An API gateway tracks request counts per user in a sliding time window using a deque of timestamps. When a new request arrives, expired timestamps are popped from the front, and the new timestamp is pushed to the back.

- **Scenario 3: Thread Pool Work Queue** — A web server uses a BlockingQueue (LinkedBlockingQueue or ArrayBlockingQueue) to hold submitted tasks. Worker threads poll from the queue. The queue supports backpressure when full (blocking put) and fair ordering.

## Interview Tips

- Time: O(1) for push/pop/peek on stack or queue, O(n) for searching, O(n) for monotonic stack total (each element pushed/popped once)
- Space: O(n) for auxiliary stack in MinStack, O(k) for sliding window deque, O(h) for recursive stack (h = recursion depth)
- Common edge cases: empty stack/queue (throw or return sentinel?), single element, all equal values in monotonic problems
- Monotonic stack pattern: when you need next greater/smaller element, the stack maintains a sequence of candidates in decreasing/increasing order
- Sliding window max with deque: maintain deque in decreasing order; front is max; remove from front when index leaves window

## Java-Specific Considerations

- `Stack<E>` extends `Vector` — legacy, synchronized overhead, use `ArrayDeque<E>` instead
- `Deque<E>` interface — `ArrayDeque` is the preferred implementation (resizable array, not thread-safe)
- `Queue<E>` interface — `LinkedList` implements Queue, but `ArrayDeque` is faster for single-threaded use
- `PriorityQueue<E>` — min-heap by default, use `Comparator.reverseOrder()` for max-heap, O(log n) offer/poll
- `BlockingQueue` implementations: `ArrayBlockingQueue` (bounded, fair), `LinkedBlockingQueue` (optionally bounded), `SynchronousQueue` (handoff), `DelayQueue`, `PriorityBlockingQueue`
- `ConcurrentLinkedDeque` — lock-free, non-blocking, CAS-based, for high-throughput concurrent scenarios
- `Deque` can be used as LIFO stack (`push`/`pop`) or FIFO queue (`offer`/`poll`)
- `ArrayDeque` capacity is always a power of 2; grows by doubling; initial capacity can be tuned
