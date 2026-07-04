# Flashcards: Connection Pooling

**Q:** What is HikariCP?
**A:** High-performance JDBC connection pool, default in Spring Boot 2+.

**Q:** What does `maximum-pool-size` control?
**A:** Maximum number of connections in the pool at any time.

**Q:** What does `minimum-idle` control?
**A:** Minimum number of idle connections the pool tries to maintain.

**Q:** What happens on `connection.getConnection()` when all connections are busy?
**A:** The thread waits up to `connection-timeout` for a connection to free up.

**Q:** What is a connection leak?
**A:** When code acquires a connection but never calls `close()` to return it.

**Q:** How does HikariCP detect leaks?
**A:** `leak-detection-threshold` logs stack trace if a connection is held past the threshold.

**Q:** Why not just open a new connection per request?
**A:** Connection creation is expensive (TCP + SSL + auth, 5-50ms). Pooling reuses connections.

**Q:** What does HikariCP's `ProxyConnection` do?
**A:** Wraps real connections so `close()` returns to pool instead of closing the TCP socket.
