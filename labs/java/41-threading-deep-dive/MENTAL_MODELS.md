# Mental Models for Threading

## Thread States as a State Machine
Think of each thread as moving through a finite state machine: NEW → RUNNABLE → {BLOCKED, WAITING, TIMED_WAITING} → RUNNABLE → TERMINATED. The scheduler transitions between RUNNABLE and non-RUNNABLE states. This model helps debug "the thread is stuck" problems by checking which state the thread is in and why.

## ThreadPoolExecutor as a Water Reservoir
Core threads are the base flow. The work queue is a buffer tank. Max threads are emergency pumps. When water (tasks) flows in, the base flow handles it. If the buffer fills, emergency pumps activate. If everything is full, a spillway (rejection handler) triggers. Keep-alive time is how long emergency pumps stay on after the surge passes.

## ForkJoinPool as a Team of Chefs
Each chef (worker) has their own prep station (deque). They work on their own tasks (LIFO — popping the most recent). When a chef runs out of work, they look at another chef's station and take the oldest task (FIFO stealing). This works because the most recent task is likely still in cache (hot data), and the oldest task is the least likely to be worked on by the owner.

## CompletableFuture as a Pipeline
Think of CompletableFuture as an assembly line. thenApply is a worker that transforms parts. thenCompose replaces a part with a sub-assembly line. exceptionally is a repair station for defective parts. allOf merges multiple lines into one. anyOf takes the first finished part from any line.

## StructuredTaskScope as a Try-With-Resources for Threads
Just as `try (InputStream in = ...)` ensures the stream is closed, `try (var scope = new StructuredTaskScope(...))` ensures all subtasks complete before the scope exits. The scope is the parent; subtasks are children with the same lifetime. This parallels how blocks scope local variables in structured programming.
