# Interview: R2DBC

## Common Questions

**Q:** Why was R2DBC created instead of making JDBC reactive?
**A:** JDBC is deeply synchronous in its SPI design – every method blocks. Making JDBC reactive would break backward compatibility. R2DBC was created as a new SPI from the ground up for reactive, non-blocking database access.

**Q:** Explain backpressure in R2DBC.
**A:** Backpressure means the consumer controls the data flow. The consumer calls `request(n)` to indicate how many rows it can handle. The driver sends at most n rows before waiting for the next request. This prevents memory overflow when consumer is slower than producer.

**Q:** When would you NOT use R2DBC?
**A:** Use JDBC/JPA when: 1) The application uses Spring MVC (servlet stack), 2) You need complex ORM features (lazy loading, caching, dirty checking), 3) You're already heavily invested in Hibernate/JPA, 4) Your database access is not the bottleneck.

**Q:** How does R2DBC handle connection pooling?
**A:** Via `io.r2dbc.pool.ConnectionPool` which implements `ConnectionFactory`. It wraps a real `ConnectionFactory` and provides `acquire()` returning `Publisher<Connection>`. Configuration includes min/max size, idle timeout, validation.

**Q:** Explain Spring Data R2DBC vs Spring Data JPA.
**A:** Spring Data JPA is a full ORM (entities, lazy loading, caching, dirty checking). Spring Data R2DBC is a lightweight, reactive mapping layer – it maps rows to objects but provides no ORM features, no 1st-level cache, no lazy loading. It's closer to a reactive JDBC template.
