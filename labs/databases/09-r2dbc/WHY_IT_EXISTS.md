# Why R2DBC Exists

R2DBC exists because JDBC is fundamentally blocking. Every JDBC operation (connect, query, fetch) blocks the calling thread until the database responds. In reactive stacks (Spring WebFlux, Vert.x, Akka), blocking operations require dedicated thread pools and negate the benefits of non-blocking I/O.

R2DBC fills the gap for reactive applications that need relational databases. Without it, reactive applications were forced to either:
- Wrap JDBC calls in blocking thread pools (Schedulers.boundedElastic)
- Abandon relational databases for NoSQL (MongoDB reactive driver)
- Use non-relational reactive data stores
