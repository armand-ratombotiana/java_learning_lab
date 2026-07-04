# Interview: Connection Pooling

## Common Questions

**Q:** How do you determine the optimal connection pool size?
**A:** Start with `CPUCores × 2 + diskSpindles`. Monitor `connections.active` and `connections.pending` at peak load. If pending > 0, increase pool. Lower bound: P99 query latency × target throughput from Little's Law.

**Q:** What happens if the database restarts while connections are in the pool?
**A:** HikariCP validates connections before use (`connection-test-query`). Stale connections fail validation and are evicted. New connections are created to replace them.

**Q:** Explain HikariCP's ConcurrentBag.
**A:** It's a lock-free bag using ThreadLocal cache (fast path) and shared CopyOnWriteArrayList. CAS operations for state transitions. No synchronized blocks in the common path. This makes HikariCP extremely fast for connection borrow/return.

**Q:** How do you handle connection pools in a multi-tenant application?
**A:** Options: 1) Separate pool per tenant (isolation, more resources). 2) Single shared pool with tenant-aware routing (efficient, more complex). 3) Connection proxy that sets tenant context.

**Q:** What metrics do you monitor for connection pools?
**A:** Active connections, idle connections, pending threads (waiting), timeout count, connection acquire time, connection usage time. All available via JMX/HikariCP MXBean.
