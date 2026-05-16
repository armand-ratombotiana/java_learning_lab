# Scalability - FLASHCARDS

## Flashcard Set Overview
This flashcard set contains 35 cards covering scaling fundamentals, load balancing, caching, and database scaling.

---

## Scaling Fundamentals Flashcards (1-10)

### Flashcard 1
**Term**: Vertical Scaling (Scale Up)
**Definition**: Adding more resources (CPU, RAM, storage) to a single machine to increase capacity.

---

### Flashcard 2
**Term**: Horizontal Scaling (Scale Out)
**Definition**: Adding more machines to the system to handle increased load. Enables essentially unlimited scaling.

---

### Flashcard 3
**Term**: Stateless Application
**Definition**: Application that doesn't store user session or state locally - each request contains all information needed for processing.

---

### Flashcard 4
**Term**: Shared-Nothing Architecture
**Definition**: Design where each node is independent and has its own memory and disk storage, with no shared resources.

---

### Flashcard 5
**Term**: Auto-Scaling
**Definition**: Automatically adjusting compute resources based on demand using predefined rules or metrics.

---

### Flashcard 6
**Term**: Throughput
**Definition**: The number of requests or operations a system can process per unit of time (e.g., requests per second).

---

### Flashcard 7
**Term**: Latency
**Definition**: The time it takes for a single request to be processed from start to finish.

---

### Flashcard 8
**Term**: Capacity Planning
**Definition**: Process of determining the resources needed to meet current and future workload demands.

---

### Flashcard 9
**Term**: Elasticity
**Definition**: The ability of a system to automatically scale up and down based on demand.

---

### Flashcard 10
**Term**: Scalability
**Definition**: The ability of a system to handle increased load by adding resources either vertically or horizontally.

---

## Load Balancing Flashcards (11-20)

### Flashcard 11
**Term**: Load Balancer
**Definition**: A device or software that distributes incoming network traffic across multiple servers.

---

### Flashcard 12
**Term**: Round Robin
**Definition**: A load balancing algorithm that distributes requests sequentially across all servers.

---

### Flashcard 13
**Term**: Least Connections
**Definition**: A load balancing algorithm that routes requests to the server with fewest active connections.

---

### Flashcard 14
**Term**: IP Hash
**Definition**: A load balancing algorithm that hashes the client IP to determine which server handles the request.

---

### Flashcard 15
**Term**: Layer 4 Load Balancing
**Definition**: Load balancing at the transport layer (TCP/UDP), making routing decisions based on IP and port.

---

### Flashcard 16
**Term**: Layer 7 Load Balancing
**Definition**: Load balancing at the application layer, making routing decisions based on HTTP content, headers, or URL.

---

### Flashcard 17
**Term**: Health Check
**Definition**: A mechanism that monitors server health by periodically testing if servers are responsive and able to handle requests.

---

### Flashcard 18
**Term**: Session Affinity
**Definition**: A feature that ensures requests from the same client are routed to the same server (also called sticky sessions).

---

### Flashcard 19
**Term**: SSL Termination
**Definition**: Decrypting SSL/TLS traffic at the load balancer so backend servers don't need to handle encryption.

---

### Flashcard 20
**Term**: Gateway Load Balancer
**Definition**: A load balancer that handles routing for third-party virtual appliances like firewalls.

---

## Caching Flashcards (21-30)

### Flashcard 21
**Term**: Cache
**Definition**: A fast storage layer that stores frequently accessed data to reduce latency and database load.

---

### Flashcard 22
**Term**: Cache Hit
**Definition**: When requested data is found in the cache, avoiding slower backend data retrieval.

---

### Flashcard 23
**Term**: Cache Miss
**Definition**: When requested data is not found in the cache, requiring data to be fetched from the source.

---

### Flashcard 24
**Term**: TTL (Time To Live)
**Definition**: The duration that data remains in cache before expiration.

---

### Flashcard 25
**Term**: Cache-Aside (Lazy Loading)
**Definition**: A pattern where the application first checks the cache, and on miss, loads from database and caches the result.

---

### Flashcard 26
**Term**: Write-Through
**Definition**: A caching strategy where data is written to both cache and database simultaneously.

---

### Flashcard 27
**Term**: Write-Behind (Write-Back)
**Definition**: A caching strategy where data is written to cache first, then asynchronously written to the database.

---

### Flashcard 28
**Term**: CDN (Content Delivery Network)
**Definition**: A distributed network of servers that delivers content from edge locations close to users.

---

### Flashcard 29
**Term**: Redis
**Definition**: An in-memory data structure store used as a database, cache, and message broker.

---

### Flashcard 30
**Term**: Cache Invalidation
**Definition**: The process of removing or updating stale data in the cache to ensure data consistency.

---

## Database Scaling Flashcards (31-35)

### Flashcard 31
**Term**: Read Replica
**Definition**: A copy of a database optimized for read operations that replicates data from the primary database.

---

### Flashcard 32
**Term**: Replication Lag
**Definition**: The time delay between when data is written to the primary database and when it appears in replicas.

---

### Flashcard 33
**Term**: Database Sharding
**Definition**: Horizontal partitioning of data across multiple database instances, with each shard containing a subset of data.

---

### Flashcard 34
**Term**: Sharding Key
**Definition**: A specific field (like user ID) used to determine which database shard stores a particular piece of data.

---

### Flashcard 35
**Term**: Eventual Consistency
**Definition**: A consistency model where updates propagate to all replicas over time, allowing temporary inconsistencies.

---

## Quick Reference Card

| Concept | Key Term | Example |
|---------|----------|---------|
| Adding more servers | Horizontal Scaling | Kubernetes pods |
| Adding more CPU | Vertical Scaling | AWS instance resize |
| Distributing requests | Load Balancer | NGINX, HAProxy |
| Fast data storage | Cache | Redis, Memcached |
| Copy database | Read Replica | AWS RDS Read Replica |
| Partition data | Sharding | MongoDB sharding |

---

## Study Tips

1. **Understand the Trade-offs**: Each scaling approach has pros and cons - understand when to use each
2. **Practice Implementation**: Build actual load balancers, caches, and sharding implementations
3. **Know the Math**: Calculate hit rates, replication lag impacts, and scaling requirements
4. **Real-World Examples**: Study how Netflix, Amazon, and Google scale their systems
5. **Combine Concepts**: Modern systems use multiple scaling strategies together