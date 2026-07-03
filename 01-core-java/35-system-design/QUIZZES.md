# Module 35: System Design - Quizzes

---

## Q1: Scalability
What is the primary drawback of Vertical Scaling (Scaling Up) compared to Horizontal Scaling (Scaling Out)?

A) Vertical scaling requires rewriting the application into microservices.
B) Vertical scaling has a hard hardware limit and creates a single point of failure.
C) Vertical scaling is always more expensive than horizontal scaling.
D) Vertical scaling cannot be used with relational databases.

**Answer**: B
**Explanation**: You can only add so much CPU and RAM to a single machine before you hit physical limits. If that single machine goes down, the entire application goes down.

---

## Q2: CAP Theorem
According to the CAP Theorem, if a network partition occurs (a communication break between nodes), what must a distributed system architect choose between?

A) Consistency and Partition Tolerance
B) Consistency and Availability
C) Availability and Partition Tolerance
D) Latency and Throughput

**Answer**: B
**Explanation**: Network partitions (P) are inevitable in distributed systems. When one occurs, the system must either stop serving requests to avoid serving stale data (choosing Consistency) or keep serving requests even if the data might be stale (choosing Availability).

---

## Q3: Database Architecture
Which database scaling technique involves splitting a single large table into multiple smaller tables distributed across different servers based on a key (e.g., User ID)?

A) Replication
B) Load Balancing
C) Sharding
D) Caching

**Answer**: C
**Explanation**: Sharding (Horizontal Partitioning) distributes data across multiple machines based on a sharding key, allowing the database to store more data than a single physical disk can hold.