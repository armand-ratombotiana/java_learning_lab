# Scalability - QUIZ

## Quiz Instructions
This quiz contains 30 questions covering vertical/horizontal scaling, load balancing, caching, and database scaling. Each question has 4 options with one correct answer.

---

## Questions 1-10: Scaling Fundamentals

**Q1. What type of scaling involves adding more machines to handle increased load?**
- A) Vertical Scaling
- B) Horizontal Scaling
- C) Diagonal Scaling
- D) Inline Scaling

**Answer: B** - Horizontal scaling (scaling out) adds more machines to handle increased load.

---

**Q2. Which is NOT an advantage of horizontal scaling?**
- A) No single point of failure
- B) Essentially unlimited scaling potential
- C) Simple to implement
- D) Cost-effective using commodity hardware

**Answer: C** - Horizontal scaling is more complex to implement than vertical scaling.

---

**Q3. What is the main limitation of vertical scaling?**
- A) Requires code changes
- B) Hardware limits
- C) Network complexity
- D) Data consistency issues

**Answer: B** - Vertical scaling eventually hits hardware limits.

---

**Q4. A stateless application is one that:**
- A) Stores no data in the database
- B) Does not store user session data locally
- C) Has no user interface
- D) Cannot be horizontally scaled

**Answer: B** - Stateless applications don't store session data locally, enabling horizontal scaling.

---

**Q5. Which metric is commonly used to trigger auto-scaling?**
- A) Code coverage
- B) CPU utilization
- C) Test coverage
- D) Code complexity

**Answer: B** - CPU utilization is a common auto-scaling trigger.

---

**Q6. What is the primary purpose of a load balancer?**
- A) Store frequently accessed data
- B) Distribute traffic across multiple servers
- C) Encrypt network traffic
- D) Monitor server health

**Answer: B** - Load balancers distribute incoming traffic across multiple servers.

---

**Q7. Which load balancing algorithm sends requests to the server with fewest active connections?**
- A) Round Robin
- B) Random
- C) Least Connections
- D) IP Hash

**Answer: C** - Least Connections routes to the server with the fewest active connections.

---

**Q8. What type of load balancer operates at Layer 4 (Transport Layer)?**
- A) Application Load Balancer
- B) Network Load Balancer
- C) Gateway Load Balancer
- D) Web Load Balancer

**Answer: B** - Network Load Balancer operates at Layer 4.

---

**Q9. What is a health check in load balancing?**
- A) Security authentication
- B) Mechanism to detect server failures
- C) SSL certificate validation
- D) User authentication

**Answer: B** - Health checks detect server failures and remove unhealthy servers from the pool.

---

**Q10. Which load balancing algorithm ensures the same client always reaches the same server?**
- A) Round Robin
- B) Least Connections
- C) IP Hash
- D) Random

**Answer: C** - IP Hash uses client IP to ensure consistent server assignment.

---

## Questions 11-20: Caching

**Q11. What is the cache-aside pattern?**
- A) Write to cache first, then database
- B) Check cache first, load from database on miss
- C) Write to database, then cache
- D) Never use cache

**Answer: B** - Cache-aside checks cache first and loads from database on cache miss.

---

**Q12. Which cache strategy writes to both cache and database simultaneously?**
- A) Write-Around
- B) Write-Back
- C) Write-Through
- D) Write-Only

**Answer: C** - Write-Through writes to both cache and database at the same time.

---

**Q13. What is cache invalidation?**
- A) Cleaning the browser cache
- B) Removing stale data from cache
- C) Adding data to cache
- D) Measuring cache performance

**Answer: B** - Cache invalidation removes stale data from the cache.

---

**Q14. Redis is an example of:**
- A) SQL Database
- B) NoSQL Database
- C) In-memory cache
- D) Message Queue

**Answer: C** - Redis is primarily an in-memory cache/data store.

---

**Q15. Which is the hardest problem in computer science?**
- A) Sorting algorithms
- B) Cache invalidation
- C) Searching algorithms
- D) Encryption

**Answer: B** - "There are only two hard things in computer science: cache invalidation and naming things" - Phil Karlton

---

**Q16. What does TTL stand for in caching?**
- A) Time To Load
- B) Time To Live
- C) Total Transfer Length
- D) Type Transfer List

**Answer: B** - TTL (Time To Live) defines how long data stays in cache.

---

**Q17. CDN stands for:**
- A) Central Distribution Network
- B) Content Delivery Network
- C) Cache Data Node
- D) Central Data Network

**Answer: B** - CDN (Content Delivery Network) distributes content globally.

---

**Q18. Which is a benefit of CDN?**
- A) Reduces database load
- B) Decreases latency for end users
- C) Increases server costs
- D) Removes need for caching

**Answer: B** - CDN reduces latency by serving content from edge locations.

---

**Q19. What is write-back caching?**
- A) Write to cache, then immediately to database
- B) Write to cache, async write to database later
- C) Write directly to database
- D) Don't write to cache

**Answer: B** - Write-back writes to cache first and async writes to database later.

---

**Q20. A cache hit occurs when:**
- A) Data is not found in cache
- B) Data is found in cache
- C) Cache is cleared
- D) Cache expires

**Answer: B** - A cache hit occurs when requested data is found in the cache.

---

## Questions 21-30: Database Scaling

**Q21. What is a database read replica?**
- A) Primary database for writes
- B) Copy of database for read operations
- C) Backup database
- D) Test database

**Answer: B** - Read replicas are copies of the database optimized for read operations.

**Q22. What is replication lag?**
- A) Time to backup database
- B) Delay between primary and replica
- C) Database query time
- D) Index rebuild time

**Answer: B** - Replication lag is the time delay between primary and replica databases.

---

**Q23. What is database sharding?**
- A) Creating database indexes
- B) Partitioning data across multiple databases
- C) Creating database backups
- D) Optimizing queries

**Answer: B** - Sharding partitions data across multiple database nodes.

---

**Q24. Which is a potential sharding key?**
- A) Timestamp
- B) User ID
- C) Random number
- D) Session ID

**Answer: B** - User ID is a common sharding key for user-centric data.

---

**Q25. What does eventual consistency mean in distributed databases?**
- A) All data is always consistent
- B) Data becomes consistent over time
- C) Data is never consistent
- D) Consistency doesn't matter

**Answer: B** - Eventual consistency means data becomes consistent over time after updates.

---

**Q26. What is CQRS?**
- A) Central Query Request System
- B) Command Query Responsibility Segregation
- C) Concurrent Query Response System
- D) Cache Query Request Service

**Answer: B** - CQRS separates read and write operations for database scaling.

---

**Q27. Which database scaling approach helps with read-heavy workloads?**
- A) Sharding
- B) Write replicas
- C) Vertical scaling
- D) All of the above

**Answer: B** - Read replicas specifically help with read-heavy workloads.

---

**Q28. What is a connection pool?**
- A) Pool of database connections
- B) Pool of HTTP connections
- C) Pool of memory
- D) Pool of threads

**Answer: A** - A connection pool maintains a pool of database connections for reuse.

---

**Q29. What is the purpose of database indexing?**
- A) Store more data
- B) Speed up query execution
- C) Add security
- D) Create backups

**Answer: B** - Indexes speed up data retrieval queries.

---

**Q30. Which scaling approach is best for a write-heavy workload?**
- A) Read replicas
- B) Database sharding
- C) Caching
- D) Load balancing

**Answer: B** - Sharding distributes write load across multiple database nodes.

---

## Answer Key

| Question | Answer | Question | Answer | Question | Answer |
|----------|--------|----------|--------|----------|--------|
| 1 | B | 11 | B | 21 | B |
| 2 | C | 12 | C | 22 | B |
| 3 | B | 13 | B | 23 | B |
| 4 | B | 14 | C | 24 | B |
| 5 | B | 15 | B | 25 | B |
| 6 | B | 16 | B | 26 | B |
| 7 | C | 17 | B | 27 | B |
| 8 | B | 18 | B | 28 | A |
| 9 | B | 19 | B | 29 | B |
| 10 | C | 20 | B | 30 | B |

---

## Score Interpretation

- **27-30**: Expert - Deep understanding of scalability
- **21-26**: Proficient - Good grasp of concepts
- **15-20**: Developing - Need more study
- **Below 15**: Review material and try again