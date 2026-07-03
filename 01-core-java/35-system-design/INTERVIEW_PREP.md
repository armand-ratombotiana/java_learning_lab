# Module 35: System Design - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the CAP Theorem, and how does it influence database selection?
**Answer**:
The CAP Theorem states that a distributed system can only provide two of the following three guarantees simultaneously:
1. **Consistency (C)**: Every read receives the most recent write or an error.
2. **Availability (A)**: Every request receives a (non-error) response, but without the guarantee that it contains the most recent write.
3. **Partition Tolerance (P)**: The system continues to operate despite an arbitrary number of messages being dropped or delayed by the network between nodes.

Because network partitions (P) are unavoidable in distributed systems, architects must choose between **CP** (Consistency + Partition Tolerance) and **AP** (Availability + Partition Tolerance).
- **CP Systems** (e.g., MongoDB, HBase): Will refuse to answer read/write requests if they cannot guarantee data consistency during a network partition.
- **AP Systems** (e.g., Cassandra, DynamoDB): Will always answer requests, prioritizing uptime, even if the data returned is temporarily stale (Eventual Consistency).

### Q2: Compare Vertical Scaling (Scale Up) and Horizontal Scaling (Scale Out).
**Answer**:
- **Vertical Scaling**: Involves adding more power (CPU, RAM, Storage) to a single existing server. It is extremely simple because no code architecture changes are required. However, it has a hard physical limit (you can only buy so much RAM), is expensive, and represents a Single Point of Failure (SPOF).
- **Horizontal Scaling**: Involves adding more servers to a resource pool. It provides theoretically infinite scale and high availability (redundancy). However, it vastly increases architectural complexity. You must implement Load Balancers, ensure application nodes are perfectly stateless (no local sessions), and handle distributed database architectures (Replication/Sharding).

### Q3: What is Consistent Hashing, and what problem does it solve?
**Answer**:
In a distributed caching system (like Memcached), if you have `N` servers, standard hashing determines which server holds a key using `hash(key) % N`. 
The problem arises when you add or remove a server. `N` changes, which completely recalculates the hash mapping for *every single key*. This causes a massive cache miss storm, overwhelming the database.
**Consistent Hashing** solves this by mapping both servers and data keys to a virtual ring. When a key is hashed, the system walks clockwise around the ring to find the nearest server. If a server is added or removed, only the keys immediately adjacent to that server on the ring are remapped, leaving the vast majority of the cache intact.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Designing a Rate Limiter
**Problem**: An interviewer asks you to design an API Rate Limiter that allows a user to make a maximum of 10 requests per minute. How do you implement this in a distributed microservices environment?

**Solution Strategy**:
Since the system is distributed (multiple API Gateways or load-balanced servers), an in-memory counter on a single server will not work. The state must be shared.
1. **Storage**: Use a fast, distributed, in-memory datastore like **Redis**.
2. **Algorithm**: The most common algorithm is the **Token Bucket** or the **Sliding Window Log**. 
3. **Token Bucket Implementation**:
   - For a given user (key: `user_123_bucket`), store two values: `tokens_available` and `last_refill_timestamp`.
   - When a request arrives, retrieve the bucket from Redis.
   - Calculate how many tokens should have been refilled based on the time elapsed since `last_refill_timestamp` (refill rate is 10 per minute).
   - If `tokens_available > 0`, decrement by 1, update the timestamp, save back to Redis, and allow the request.
   - If `tokens_available == 0`, reject the request with HTTP `429 Too Many Requests`.
4. **Concurrency**: Use Redis Lua scripts to ensure the read-calculate-update cycle happens atomically, preventing race conditions from concurrent requests.