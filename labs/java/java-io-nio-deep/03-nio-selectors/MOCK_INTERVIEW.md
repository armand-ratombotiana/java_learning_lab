# Mock Interview Transcript: NIO Selectors

## Interviewer: Senior SWE, Google
## Candidate: Senior Java developer
## Time: 30 minutes
## Focus: Non-blocking I/O, Reactor pattern, scalability

---

**Q1: Explain how Java NIO Selectors work.**

**Candidate**: A `Selector` sits on top of `SelectableChannel` objects and uses the OS's multiplexing facility (`select()`, `poll()`, `epoll()`, or `kqueue()`). Channels register with the selector expressing interest in `OP_ACCEPT`, `OP_READ`, `OP_WRITE`, or `OP_CONNECT`. The selector's `select()` method blocks until at least one channel has an event ready. You then iterate the `selectedKeys()` to process events.

**Interviewer**: What are the performance characteristics of select() vs epoll()?

**Candidate**: The traditional `select()` is O(N) — it iterates all file descriptors every time. It also has an FD limit (1024). `epoll()` on Linux is O(1) — it uses a callback-based approach where the kernel notifies userspace only about active FDs. `epoll()` also supports edge-triggered mode, which only notifies on state changes, reducing wake-ups.

**Interviewer**: What's the difference between level-triggered and edge-triggered?

**Candidate**: Level-triggered: notified as long as data is available. If you don't read fully, you get notified again. Edge-triggered: notified only when data arrives. You must read until EAGAIN. Edge-triggered is more efficient (fewer wake-ups) but harder to code correctly — you must handle partial reads and buffer state carefully.

**Interviewer**: What is the C10K problem and how does NIO address it?

**Candidate**: C10K is handling 10,000 concurrent connections. The traditional thread-per-connection model fails because each thread consumes ~1MB of stack space — 10,000 threads = 10GB of memory. NIO Selectors solve this by multiplexing many connections onto a few threads. With epoll, a single thread can handle 100K+ connections.

**Interviewer**: How do you handle partial reads in non-blocking mode?

**Candidate**: You maintain a per-connection buffer. When `read()` returns with partial data, you leave the buffer state and continue. The `SelectionKey` is still registered for `OP_READ`, and when more data arrives, you read the remainder. For framing, you might use a length-prefixed protocol: read the length first, then keep reading until you have that many bytes.
