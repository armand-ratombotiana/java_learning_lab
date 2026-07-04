# Why Stacks & Queues Matter

Stacks and queues matter because they are **universal abstractions** that appear in every layer of computing.

## Practical Impact

- **Every program**: the call stack manages function invocation and return
- **Every OS**: process scheduling queues, IO request queues
- **Every network**: packet queues, message brokers (Kafka, RabbitMQ)
- **Every browser**: back button (stack of URLs), event loop (queue)
- **Every database**: transaction logs, query execution queues
- **Every algorithm**: BFS uses queues, DFS uses stacks, Dijkstra uses priority queues

## Why Learn Stacks & Queues

1. **Foundational ADTs**: They are the simplest abstract data types that teach implementation vs interface separation
2. **Interview goldmine**: Balanced parentheses, min-stack, queue with stacks, sliding window maximum — all stack/queue problems
3. **Algorithm building blocks**: BFS, DFS, topological sort, Dijkstra all depend on stacks/queues
4. **Systems thinking**: Understanding queues is essential for distributed systems and async programming
5. **Performance insight**: ArrayDeque vs LinkedList vs Stack — choosing the right implementation matters

Stacks and queues are often the **first abstract data types** taught because they separate **what** (interface) from **how** (implementation).
