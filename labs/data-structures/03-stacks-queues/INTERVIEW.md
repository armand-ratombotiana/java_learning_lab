# Interview Questions: Stacks & Queues

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 20 Valid Parentheses](https://leetcode.com/problems/valid-parentheses/) | Easy | Amazon, Google, Meta, Microsoft, Apple | Stack matching |
| [LC 155 Min Stack](https://leetcode.com/problems/min-stack/) | Medium | Amazon, Meta, Microsoft, Oracle | Two-stack / pair stack |
| [LC 739 Daily Temperatures](https://leetcode.com/problems/daily-temperatures/) | Medium | Amazon, Meta, Google, Microsoft | Monotonic stack |
| [LC 150 Evaluate Reverse Polish Notation](https://leetcode.com/problems/evaluate-reverse-polish-notation/) | Medium | Amazon, Google, Microsoft, Meta | Stack evaluation |
| [LC 232 Implement Queue Using Stacks](https://leetcode.com/problems/implement-queue-using-stacks/) | Easy | Amazon, Microsoft, Google, Meta | Two-stack queue |
| [LC 239 Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/) | Hard | Amazon, Meta, Google, Microsoft | Monotonic deque |
| [LC 84 Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/) | Hard | Amazon, Meta, Google, Microsoft | Monotonic stack |
| [LC 225 Implement Stack Using Queues](https://leetcode.com/problems/implement-stack-using-queues/) | Easy | Microsoft, Amazon | Two-queue stack |
| [LC 316 Remove Duplicate Letters](https://leetcode.com/problems/remove-duplicate-letters/) | Medium | Google, Microsoft | Monotonic stack |
| [LC 456 132 Pattern](https://leetcode.com/problems/132-pattern/) | Medium | Amazon, Google, Meta, Microsoft | Monotonic stack |
| [LC 394 Decode String](https://leetcode.com/problems/decode-string/) | Medium | Google, Amazon, Meta, Microsoft | Stack with repetition |
| [LC 636 Exclusive Time of Functions](https://leetcode.com/problems/exclusive-time-of-functions/) | Medium | Meta, Amazon, Google, Microsoft | Stack of logs |
| [LC 42 Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water/) | Hard | Amazon, Meta, Google, Microsoft | Monotonic stack / two-pointer |
| [LC 71 Simplify Path](https://leetcode.com/problems/simplify-path/) | Medium | Meta, Amazon, Google, Microsoft | Stack of path components |
| [LC 503 Next Greater Element II](https://leetcode.com/problems/next-greater-element-ii/) | Medium | Amazon, Microsoft | Monotonic stack (circular) |

## NeetCode Reference
NeetCode 150: Stack category — 7 problems. All are important: Valid Parentheses, Min Stack, RPN, Generate Parentheses, Daily Temperatures, Car Fleet, Largest Rectangle in Histogram.

## Company-Specific Questions

### Google
- Implement a stack that supports push, pop, top, and retrieving the minimum element in O(1)
- How would you implement a queue with constant-time maximum?
- Design a parser that validates XML using a stack (opening/closing tags)
- Evaluate a mathematical expression with parentheses, +, -, *, / (shunting-yard algorithm)

### Microsoft
- Implement a stack that also supports increment bottom k elements in O(1)
- Browser forward/back navigation — how would you design it using two stacks?
- Design a priority queue-based stack (pop highest priority, not most recent)

### Meta
- Asteroid collision — simulate asteroids moving left/right, resolve collisions using stack
- Exclusive time of functions — compute how much CPU time each function used
- Design a task scheduler with cooldown period using queue and heap

### Amazon
- Sliding window maximum — how to achieve O(n) using deque (proof that amortized analysis gives O(1) per window)
- Design an order matching engine using priority queue (price-time priority)
- Level-order traversal of a tree using queue (BFS)

### Apple
- Design a circular queue (ring buffer) for audio/video processing
- Stack-based undo/redo in a text editor (infinite vs bounded undo)
- How would you implement an operation queue for asynchronous tasks (like GCD)?

### Oracle
- `ArrayDeque` vs `Stack` vs `LinkedList` — which is the best choice and why?
- How does `BlockingQueue` work? What are the different implementations?
- Design a thread-safe bounded blocking queue using condition variables
- What is the `TransferQueue` interface and how does it differ from `BlockingQueue`?

## Real Production Scenarios

- **Scenario 1: Expression Evaluator** — A financial application evaluates user-defined formulas (e.g., "PRICE * (1 + TAX_RATE)"). Uses a recursive descent parser with operator stack and operand stack. Handles operator precedence, parentheses, and unary operators.

- **Scenario 2: API Rate Limiter** — An API gateway uses a sliding window rate limiter backed by a queue (or deque) of timestamps. Each request adds a timestamp to the queue; expired timestamps are dequeued from the front. The queue size represents the request count in the window.

- **Scenario 3: Thread Pool Work Queue** — A web application uses `LinkedBlockingQueue` between producer threads handling HTTP requests and consumer worker threads. The queue's blocking nature provides backpressure: if the queue is full, producers block until space is available.

## Interview Tips

- Time: O(1) for push/pop/peek, O(1) amortized for two-stack queue, O(n) total for monotonic stack (each element pushed and popped once)
- Space: O(n) for auxiliary stack in MinStack, O(k) for sliding window deque, O(h) for recursion stack
- Common edge cases: empty stack/queue (throw exception vs return sentinel), single element, all equal values, very large input
- Monotonic stack pattern: for "next greater element" problems, process from end to start or start to end; maintain a stack of candidates
- Two-stack queue: amortized analysis shows each element moves at most twice (pushed to s1, pushed to s2)

## Java-Specific Considerations

- `Stack<E>` extends `Vector<E>` — legacy, synchronized, deprecated in favor of `Deque`
- `Deque<E>` — use `ArrayDeque` for single-threaded LIFO/FIFO operations
- `Queue<E>` — `LinkedList` implements Queue but `ArrayDeque` is generally faster
- `PriorityQueue<E>` — min-heap, uses `Comparator.reverseOrder()` for max-heap
- `BlockingQueue` — `ArrayBlockingQueue` (bounded, fair lock), `LinkedBlockingQueue` (optionally bounded), `SynchronousQueue` (direct handoff)
- `ConcurrentLinkedDeque` — lock-free, CAS-based, for high-throughput concurrent use
- `ArrayDeque` capacity always power of 2; grows by doubling; use `ensureCapacity()` to pre-size
