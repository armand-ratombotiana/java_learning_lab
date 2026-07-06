# Performance Considerations for Threading

## Thread Pool Sizing
For CPU-bound tasks, pool size = number of cores. For I/O-bound tasks, pool size = cores × (1 + wait/service). The wait/service ratio measures blocking time. A task that spends 90% waiting (e.g., HTTP call) needs 10× more threads than cores.

## Thread Creation Overhead
Creating a thread requires:
- OS kernel call (system call)
- Stack allocation (default 1 MB on Linux, 512 KB on Windows)
- TLS (Thread Local Storage) initialization
The overhead is ~1 microsecond for the OS call plus memory allocation. Thread pools amortize this cost.

## Context Switching
Each context switch costs ~1-10 microseconds. At 10,000 switches/second/core, the CPU spends significant time switching. Monitor context switches with `vmstat 1` (Linux) or Performance Monitor (Windows).

## Work-Stealing Overhead
Stealing a task requires:
- Random victim selection (O(1))
- CAS on the victim's deque (memory fence)
- Potential cache miss (task data in victim's cache)
Stealing overhead is ~100 nanoseconds. For tasks under 10 microseconds, stealing overhead dominates.

## CompletableFuture Stage Overhead
Each stage adds ~50-100 nanoseconds in the common case. A chain of 10 thenApply stages adds ~1 microsecond of overhead. Consider batching transformations when possible.

## StructuredTaskScope vs CompletableFuture
StructuredTaskScope has higher overhead per subtask (registration, cancellation support) but guarantees structured lifetime. CompletableFuture has lower per-stage overhead but lacks lifetime guarantees. Choose based on whether structured completion matters for correctness.

## False Sharing
When two threads modify fields on the same cache line (64 bytes), the cache coherence protocol forces a cache miss on each write. Pad objects to 64 bytes or use `@Contended` (Java 8+, with -XX:-RestrictContended) to avoid this.
